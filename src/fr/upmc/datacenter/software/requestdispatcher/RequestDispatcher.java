package fr.upmc.datacenter.software.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataInboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;

/**
 * 
 * Class which implements the Request Dispatcher component
 *
 * Request Dispatcher offers the interface RequestSubmissionI and RequestDispatcherI.
 */
public class RequestDispatcher extends AbstractComponent
        implements RequestSubmissionHandlerI, RequestNotificationHandlerI {

    /** URI of this request dispatcher RD */
    protected String rdURI;

    /** RequestSubmissionInboundPort */
    protected RequestSubmissionInboundPort rdsip;

    /** List of OutboundPort to send requests to the connected ApplicationVM */
    protected List<RequestSubmissionOutboundPort> rdsopList;

    protected RequestNotificationInboundPort  rdnip;
    /** Outbound port used by the RD to notify tasks' termination to the generator. */
    protected RequestNotificationOutboundPort rdnop;

    /** Variable to know the less recent ApplicationVM **/
    protected int current = 0;

    /** map associate RequestUri with the startTime in millis */
    protected Map<String , Long> requestStartTimes;

    /** map associate RequestUri with the end Time in millis */
    protected Map<String , Long> requestEndTimes;

    protected RequestDispatcherDynamicStateDataInboundPort requestDispatcherDynamicStateDataInboundPort;

    /**
     * Create a RequestDispatcher
     * 
     * @param rdURI URI of the RequestDispatcher
     * @param rdsip URI of the RequestSubmissionInboundPort
     * @param rdsop URI of the RequestSubmissionOutboundPort
     * @param rdnop URI of the RequestNotificationOutboundPort
     * @param rdnip URI of the RequestNotificationInboundPort
     * @throws Exception
     */
    public RequestDispatcher( String rdURI , String rdsip , List<String> rdsop , String rdnop , String rdnip ,
            String requestDispatcherDynamicStateDataInboundPortURI ) throws Exception {
        super( true , false );

        // Preconditions
        assert rdURI != null;

        assert rdsip != null;
        assert rdsop != null;

        assert rdnip != null;
        assert rdnop != null;

        this.rdURI = rdURI;

        rdsopList = new ArrayList<>();

        // Creates and add ports to the component
        this.addOfferedInterface( RequestSubmissionI.class );
        this.rdsip = new RequestSubmissionInboundPort( rdsip , this );
        this.addPort( this.rdsip );
        this.rdsip.publishPort();

        this.addOfferedInterface( RequestNotificationI.class );
        this.rdnip = new RequestNotificationInboundPort( rdnip , this );
        this.addPort( this.rdnip );
        this.rdnip.publishPort();

        for ( int i = 0 ; i < rdsop.size() ; i++ ) {
            this.addRequiredInterface( RequestSubmissionI.class );
            this.rdsopList.add( new RequestSubmissionOutboundPort( rdsop.get( i ) , this ) );
            this.addPort( this.rdsopList.get( i ) );
            this.rdsopList.get( i ).publishPort();
        }

        this.addRequiredInterface( RequestNotificationI.class );
        this.rdnop = new RequestNotificationOutboundPort( rdnop , this );
        this.addPort( this.rdnop );
        this.rdnop.publishPort();

        this.addOfferedInterface( ControlledDataOfferedI.ControlledPullI.class );
        this.requestDispatcherDynamicStateDataInboundPort = new RequestDispatcherDynamicStateDataInboundPort(
                requestDispatcherDynamicStateDataInboundPortURI , this );
        this.addPort( this.requestDispatcherDynamicStateDataInboundPort );
        this.requestDispatcherDynamicStateDataInboundPort.publishPort();

        requestStartTimes = new LinkedHashMap<>();
        requestEndTimes = new LinkedHashMap<>();
    }

    /**
     * Notify the Requests termination to the RequestGenerator
     */
    @Override
    public void acceptRequestTerminationNotification( RequestI r ) throws Exception {
        assert r != null;
        this.logMessage(
                "Request dispatcher " + this.rdURI + "  notified the request " + r.getRequestURI() + " has ended." );
        this.rdnop.notifyRequestTermination( r );

        requestEndTimes.put( r.getRequestURI() , System.nanoTime() );

    }

    /**
     * Send the Request r to the less recent ApplicationVM
     */
    @Override
    public void acceptRequestSubmission( RequestI r ) throws Exception {
        this.logMessage( this.rdURI + " submits request " + r.getRequestURI() );
        this.rdsopList.get( current ).submitRequest( r );
        current = ( current + 1 ) % rdsopList.size();
        requestStartTimes.put( r.getRequestURI() , System.nanoTime() );
    }

    /**
     * Send the Request r to the less recent ApplicationVM and notify its termination to the
     * RequestGenerator
     */
    @Override
    public void acceptRequestSubmissionAndNotify( RequestI r ) throws Exception {
        this.logMessage( this.rdURI + " submits request " + r.getRequestURI() );
        this.rdsopList.get( current ).submitRequestAndNotify( r );
        current = ( current + 1 ) % rdsopList.size();

        requestStartTimes.put( r.getRequestURI() , System.nanoTime() );
    }

    public RequestDispatcherDynamicStateI getRequestProcessingTimeAvg() throws Exception {
        long total = 0;
        long nbRequest = 0;

        for ( Map.Entry<String , Long> entry : requestEndTimes.entrySet() ) {
            long startTime = requestStartTimes.get( entry.getKey() );
            total += entry.getValue() - startTime;
            nbRequest++;
        }
        long avg = nbRequest == 0 ? 0 : total / nbRequest;
        return new RequestDispatcherDynamicState( this.rdURI , avg / 1000000 );
    }
    
    public RequestDispatcherDynamicStateI getRequestProcessingTimeAvg(int history) throws Exception {
        long total = 0;
        long nbRequest = 0;
       
        for ( Map.Entry<String , Long> entry : requestEndTimes.entrySet() ) {
            long startTime = requestStartTimes.get( entry.getKey() );
            total += entry.getValue() - startTime;
            nbRequest++;
        }
        long avg = nbRequest == 0 ? 0 : total / nbRequest;
        return new RequestDispatcherDynamicState( this.rdURI , avg / 1000000 );
    }

    /**
     * Disconnect all connected ports of the Request Dispatcher
     */
    public void shutdown() throws ComponentShutdownException {
        try {
            if ( this.rdnop.connected() ) {
                this.rdnop.doDisconnection();
            }
            for ( int i = 0 ; i < rdsopList.size() ; i++ )
                if ( this.rdsopList.get( i ).connected() ) {
                    this.rdsopList.get( i ).doDisconnection();
                }
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }

        try {
            rdnip.doDisconnection();
            rdsip.doDisconnection();
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }

        super.shutdown();
    }

    public Boolean isWaitingForTermination() {

        return null;
    }
}
