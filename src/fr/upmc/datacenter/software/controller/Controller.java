package fr.upmc.datacenter.software.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

public class Controller extends AbstractComponent implements RequestDispatcherStateDataConsumerI {

    protected static final long THRESHOLD_AVG_ADJUSTMENT_MS     = 5000;
    protected static final long MIN_THRESHOLD_AVG_ADJUSTMENT_MS = 1000;
    protected static final long DURATION_BETWEEN_ADJUSTMENT     = 5000000000L; // 3s
    public static final boolean TRACE_GRAPH                     = true;
    public static final boolean TURN_ON_ADAPTATION              = true;

    /** the URI of the component. */
    protected String             cURI;
    protected ScheduledFuture<?> pullingFuture;

    protected RequestDispatcherDynamicStateDataOutboundPort requestDispatcherDynamicStateDataOutboundPort;

    /** OutboundPort uses to communicate with the AdmissionController */
    protected AdmissionControllerManagementOutboundPort acmop;
    protected final static String                       Filename  = "Courbe.txt";
    public static int                                   nbMoyRecu = 0;

    protected Long lastAdaptation = 0l;

    boolean x = false;

    protected Integer[] frequencies;

    public Controller( String cURI , String requestDispatcherURI , String admissionControllerManagementOutboundPortURI ,
            String rddsdip , Integer[] frequencies ) throws Exception {
        super( true , true );
        this.cURI = cURI;
        this.requestDispatcherDynamicStateDataOutboundPort = new RequestDispatcherDynamicStateDataOutboundPort( this ,
                requestDispatcherURI );
        this.addRequiredInterface( DataRequiredI.PullI.class );
        this.addOfferedInterface( DataRequiredI.PushI.class );
        this.addRequiredInterface( ControlledDataRequiredI.ControlledPullI.class );
        this.addPort( this.requestDispatcherDynamicStateDataOutboundPort );
        this.requestDispatcherDynamicStateDataOutboundPort.publishPort();
        this.requestDispatcherDynamicStateDataOutboundPort.doConnection( rddsdip ,
                DataConnector.class.getCanonicalName() );

        this.addRequiredInterface( AdmissionControllerManagementI.class );
        this.acmop = new AdmissionControllerManagementOutboundPort( admissionControllerManagementOutboundPortURI ,
                this );
        this.addPort( this.acmop );
        this.acmop.publishPort();
        if ( TRACE_GRAPH ) {
            FileWriter f = new FileWriter( Filename , false );
            f.close();
        }
        this.frequencies = frequencies;

    }

    public void startControlling() throws Exception {
        lastAdaptation = System.nanoTime();

        this.pullingFuture = this.scheduleTaskAtFixedRate( new ComponentI.ComponentTask() {

            @Override
            public void run() {
                try {

                    RequestDispatcherDynamicStateI rdds = getDynamicState();
                    print( "timestamp      : " + rdds.getTimeStamp() );
                    print( "timestamper id : " + rdds.getTimeStamperId() );
                    print( "request time average : " + rdds.getRequestProcessingAvg() + " ms" );
                    boolean adaptation = false;

                    if ( TURN_ON_ADAPTATION ) {

                        if ( System.nanoTime() - lastAdaptation > DURATION_BETWEEN_ADJUSTMENT ) {

                            // WE ARE ABOVE THE THRESHOLD ------------------------------------
                            if ( rdds.getRequestProcessingAvg() > THRESHOLD_AVG_ADJUSTMENT_MS ) {

                                acmop.setFrequency( frequencies[frequencies.length - 1] );

                                // Trying to add cores.. if no more cores available we add a new VM
                                System.out.println( "Trying to add cores..." );
                                if ( !acmop.addCores( rdds.getRequestDispatcherURI() , 2 ) ) {
                                    System.out.println( "Trying to add a new VM..." );
                                    acmop.allocateVM( rdds.getRequestDispatcherURI() );
                                }
                               
                                adaptation = true;
                                lastAdaptation = System.nanoTime();
                            }

                            // WE ARE BELOW THE MINIMUM THRESHOLD ------------------------------
                            if ( rdds.getRequestProcessingAvg() < MIN_THRESHOLD_AVG_ADJUSTMENT_MS ) {
                                acmop.removeVM( rdds.getRequestDispatcherURI() );
                                acmop.setFrequency( frequencies[0] );
                               
                                adaptation = true;
                                lastAdaptation = System.nanoTime();
                            }
                  
                        }
                    }

                    if ( TRACE_GRAPH ) { // Trace the graph if required
                        if ( rdds.getRequestProcessingAvg() != 0 ) {
                            try {
                                FileWriter fw = new FileWriter( Filename , true );
                                if ( !adaptation )
                                    fw.write( nbMoyRecu + " " + rdds.getRequestProcessingAvg() + "\n" );
                                else
                                    fw.write( nbMoyRecu + " " + rdds.getRequestProcessingAvg() + " "
                                            + rdds.getRequestProcessingAvg() + "\n" );
                                nbMoyRecu++;
                                fw.close();
                            }
                            catch ( IOException exception ) {
                                System.out.println( "Erreur lors de l'ecriture : " + exception.getMessage() );
                            }
                        }
                    }

                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } , 1l , 1l , TimeUnit.SECONDS );
    }

    public RequestDispatcherDynamicStateI getDynamicState() throws Exception {
        return ( RequestDispatcherDynamicStateI ) requestDispatcherDynamicStateDataOutboundPort.request();
    }

    private void print( String s ) {
        this.logMessage( "[Controller] " + s );
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            if ( this.acmop.connected() )
                this.acmop.doDisconnection();
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }
        super.shutdown();
    }

    @Override
    public void acceptRequestDispatcherDynamicData( String requestDispatcherURI ,
            RequestDispatcherDynamicStateI currentDynamicState ) throws Exception {
        // TODO Auto-generated method stub
    }
}
