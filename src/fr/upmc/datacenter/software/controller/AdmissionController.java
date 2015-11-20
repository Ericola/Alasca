package fr.upmc.datacenter.software.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationInboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionInboundPort;

/**
 * The class <code>AdmissionController</code> implements the component representing an admission
 * controller in the data center.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * An admission controller (AC) receives applications from <code>Application provider</code>. If
 * there enough resources are available, the application is accepted, an <code>ApplicationVM</code>
 * and a <code>RequestDispatcher</code> are created.
 * 
 * The admission controller offers the interface <code>ApplicationSubmissionI</code> to submit
 * applications. It also offeres the interface <code>ApplicationNotificationI</code> to notify the
 * end of the requestgenerator creation
 */
public class AdmissionController extends AbstractComponent {

    /** the URI of the component. */
    protected String acURI;

    /** The inbound port used to receive application */
    protected ApplicationSubmissionInboundPort asip;

    /**
     * The inbound port used to be notified when the requestgenerator is created (by the AP)
     */
    protected ApplicationNotificationInboundPort anip;

    /** The outbound port used to allocate core to the vm */
    protected List<ApplicationVMManagementOutboundPort> avmop;

    /** the outbound port of the computer service */
    protected ComputerServicesOutboundPort[] csop;

    private int cpt = 0;

    private Map<String , RequestNotificationOutboundPort> rnopList;

    /**
     * Create an admission controller
     * 
     * @param applicationSubmissionInboundPortURI URI of the application submission inbound port
     * @param applicationNotificationInboundPortURI URI of the application notification inbound port
     * @param computerServiceOutboundPortURI URI of the comuter service outbout port
     * @throws Exception
     */
    public AdmissionController( String acURI , String applicationSubmissionInboundPortURI ,
            String applicationNotificationInboundPortURI , String computerServiceOutboundPortURI[] ) throws Exception {
        super( false , true );
        this.acURI = acURI;
        this.addOfferedInterface( ApplicationSubmissionI.class );
        this.asip = new ApplicationSubmissionInboundPort( applicationSubmissionInboundPortURI , this );
        this.addPort( asip );
        this.asip.publishPort();

        this.addOfferedInterface( ApplicationNotificationI.class );
        this.anip = new ApplicationNotificationInboundPort( applicationNotificationInboundPortURI , this );
        this.addPort( anip );
        this.anip.publishPort();

        this.addRequiredInterface( ComputerServicesI.class );
        this.csop = new ComputerServicesOutboundPort[computerServiceOutboundPortURI.length];
        for ( int i = 0 ; i < computerServiceOutboundPortURI.length ; i++ ) {
            this.csop[i] = new ComputerServicesOutboundPort( computerServiceOutboundPortURI[i] , this );
            this.addPort( csop[i] );
            this.csop[i].publishPort();
        }

        rnopList = new HashMap<>();
        avmop = new ArrayList<>();
    }

    /**
     * Receive an application
     * 
     * @param nbVm the number of VM we want to allocate to the applicaiton
     * @return [0] The URI of the reqestDispatcher. [1] The requestDispatcher identifier
     * @throws Exception
     */
    public String[] submitApplication( int nbVm ) throws Exception {
        print( "Application received" );

        // Verifier que des resources sont disponibles
        print( "Looking for available resources..." );

        AllocatedCore[] ac = null;
        for ( int i = 0 ; i < csop.length ; ++i ) {
            ac = this.csop[i].allocateCores( 4 );
            if ( ac.length != 0 )
                break;
        }
        if ( ac.length != 0 ) {
            print( "Resources found!" );

            // Creation d'une VM
            print( "Creating an applicationVM..." );
            ApplicationVM vm = new ApplicationVM( createURI( "vm" ) , createURI( "avmip" ) , createURI( "rsip" ) ,
                    createURI( "rnop" ) );
            AbstractCVM.theCVM.addDeployedComponent( vm );

            avmop.add( new ApplicationVMManagementOutboundPort( createURI( "avmop" ) , new AbstractComponent() {} ) );
            avmop.get( cpt ).publishPort();
            avmop.get( cpt ).doConnection( createURI( "avmip" ) ,
                    ApplicationVMManagementConnector.class.getCanonicalName() );
            vm.start();
            // AllocateCore des computers aux VMs
            this.avmop.get( cpt ).allocateCores( ac );
            print( ac.length + " cores allocated" );

            // Création d'un requestdispatcher
            print( "Creating the requestDispatcher..." );
            List<String> rdsop = new ArrayList<>();
            rdsop.add( createURI( "rdsop" ) );
            RequestDispatcher rd = new RequestDispatcher( createURI( "rd" ) , createURI( "rdsip" ) , rdsop ,
                    createURI( "rdnop" ) , createURI( "rdnip" ) );
            rd.start();
            String rdnop = createURI( "rdnop" );
            rnopList.put( rdnop , ( RequestNotificationOutboundPort ) ( rd.findPortFromURI( rdnop ) ) );
            AbstractCVM.theCVM.addDeployedComponent( rd );

            // Connect RD with VM
            RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rd.findPortFromURI( rdsop.get( 0 ) );
            rsop.doConnection( createURI( "rsip" ) , RequestSubmissionConnector.class.getCanonicalName() );
            RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
                    .findPortFromURI( createURI( "rnop" ) );
            rnop.doConnection( createURI( "rdnip" ) , RequestNotificationConnector.class.getCanonicalName() );

            String res[] = new String[2];

            res[0] = createURI( "rdsip" );
            res[1] = rdnop;
            vm.toggleTracing();
            vm.toggleLogging();
            rd.toggleTracing();
            rd.toggleLogging();
            print( "RequestDispatcher created" );
            cpt++;
            return res;
        }
        else {
            return null;
        }
    }

    /**
     * Notify that the request generator has been created. We can now complete the request
     * notification connection
     * 
     * @param requestNotificationInboundPortURI URI of the RG notification inbound port
     * @param i index of the corresponding requestdispatcher
     * @throws Exception
     */
    public void notifyRequestGeneratorCreated( String rnipUri , String rdnopUri ) throws Exception {
        rnopList.get( rdnopUri ).doConnection( rnipUri , RequestNotificationConnector.class.getCanonicalName() );
        print( "RequestGenerator and requestDispatcher are connected" );
    }

    public void freeUpVM() {

    }

    private String createURI( String uri ) {
        return acURI + uri + cpt;
    }

    private void print( String s ) {
        this.logMessage( "[AdmissionController] " + s );
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            // if ( this.asip.connected() ) {
            // this.asip.doDisconnection();
            // }
            // if ( this.anip.connected() ) {
            // this.anip.doDisconnection();
            // }
            for ( int i = 0 ; i < cpt ; i++ ) {
                if ( this.avmop.get( i ).connected() ) {
                    this.avmop.get( i ).doDisconnection();
                }
                if ( this.csop[i].connected() ) {
                    this.csop[i].doDisconnection();
                }
                if ( this.rnopList.get( i ).connected() ) {
                    this.rnopList.get( i ).doDisconnection();
                }
            }
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }
        super.shutdown();
    }

}
