package fr.upmc.datacenter.software.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.naming.ldap.StartTlsRequest;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorDynamicStateDataInboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
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
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherVMEndingNotificationI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherVMEndingNotificationOutboundPort;

/**
 * 
 * Class which implements the Request Dispatcher component
 *
 * Request Dispatcher offers the interface RequestSubmissionI and RequestDispatcherI.
 */
public class RequestDispatcher extends AbstractComponent

implements RequestSubmissionHandlerI, RequestNotificationHandlerI, RequestDispatcherManagementI {

	public static final int NB_REQUEST = 50;
	/** URI of this request dispatcher RD */
	protected String        rdURI;

	/** RequestSubmissionInboundPort */
	protected RequestSubmissionInboundPort rdsip;

	/** InboundPort uses to get the notification task end (by VM) */
	protected RequestNotificationInboundPort  rnip;
	
	/** List of OutboundPort to send requests to the connected ApplicationVM */
	protected List<RequestSubmissionOutboundPort> rdsopList;

	/** Outbound port used by the RD to notify tasks' end to the generator. */
	protected RequestNotificationOutboundPort rnop;

	/** Outbound port uses to notify that a VM has finished all his task and is waiting to be shutdown */
	protected RequestDispatcherVMEndingNotificationOutboundPort rdvenop;
	
	/** Variable to know the less recent ApplicationVM **/
	protected int current = 0;

	/**
	 * Variable to know the number of ApplicationVM which has been connected to this
	 * requestDispatcher
	 */
	protected int nbVmConnected = 0;

	/** map associate RequestUri with the startTime in millis */
	protected Map<String , Long> requestStartTimes;

	/** map associate RequestUri with the end Time in millis */
	protected List<RequestTime> requestEndTimes;

	protected RequestDispatcherDynamicStateDataInboundPort requestDispatcherDynamicStateDataInboundPort;

	/** Inbound port offering the management interface. */
	protected RequestDispatcherManagementInboundPort rdmip;

	/**
	 * array associate the index of the applicationvm request submission outbound port with the
	 * number of request in queue
	 */
	protected Map<RequestSubmissionOutboundPort , Integer> nbRequestInQueueOrInProgress;

	/** map associate request URI with the index of the applicationVM */
	protected Map<String , RequestSubmissionOutboundPort> requestApplicationVM;

	/** List of RequestSubmissionOubboundPort waiting for termination */
	protected List<RequestSubmissionOutboundPort> rsopTerminations;
	/**
	 * Create a RequestDispatcher
	 * 
	 * @param rdURI URI of the RequestDispatcher
	 * @param rdsip URI of the RequestSubmissionInboundPort
	 * @param rdsop URI of the RequestSubmissionOutboundPort
	 * @param rnop URI of the RequestNotificationOutboundPort
	 * @param rnip URI of the RequestNotificationInboundPort
	 * @throws Exception
	 */
	public RequestDispatcher( String rdURI , String rdsip , String rdmip , List<String> rdsop ,
			String rdvenop , String rnop , String rnip , String requestDispatcherDynamicStateDataInboundPortURI ) throws Exception {
		super( 3 , 3 );

		// Preconditions
		assert rdURI != null;

		assert rdsip != null;
		assert rdsop != null;

		assert rnip != null;
		assert rnop != null;

		this.rdURI = rdURI;

		rdsopList = new ArrayList<>();

		// Creates and add ports to the component
		this.addOfferedInterface( RequestSubmissionI.class );
		this.rdsip = new RequestSubmissionInboundPort( rdsip , this );
		this.addPort( this.rdsip );
		this.rdsip.publishPort();

		this.addOfferedInterface( RequestNotificationI.class );
		this.rnip = new RequestNotificationInboundPort( rnip , this );
		this.addPort( this.rnip );
		this.rnip.publishPort();

		this.addOfferedInterface( RequestDispatcherManagementI.class );
		this.rdmip = new RequestDispatcherManagementInboundPort( rdmip , this );
		this.addPort( this.rdmip );
		this.rdmip.publishPort();

		for ( int i = 0 ; i < rdsop.size() ; i++ ) {
			this.addRequiredInterface( RequestSubmissionI.class );
			this.rdsopList.add( new RequestSubmissionOutboundPort( rdsop.get( i ) , this ) );
			this.addPort( this.rdsopList.get( i ) );
			this.rdsopList.get( i ).publishPort();
		}

		this.addRequiredInterface( RequestNotificationI.class );
		this.rnop = new RequestNotificationOutboundPort( rnop , this );
		this.addPort( this.rnop );
		this.rnop.publishPort();
		
		this.addRequiredInterface(RequestDispatcherVMEndingNotificationI.class);
		this.rdvenop = new RequestDispatcherVMEndingNotificationOutboundPort(rdvenop, this);
		this.addPort(this.rdvenop);
		this.rdvenop.publishPort();

		this.addOfferedInterface( ControlledDataOfferedI.ControlledPullI.class );
		this.requestDispatcherDynamicStateDataInboundPort = new RequestDispatcherDynamicStateDataInboundPort(
				requestDispatcherDynamicStateDataInboundPortURI , this );
		this.addPort( this.requestDispatcherDynamicStateDataInboundPort );
		this.requestDispatcherDynamicStateDataInboundPort.publishPort();

		requestStartTimes = new HashMap<>();
		requestEndTimes = new ArrayList<>();
		nbRequestInQueueOrInProgress = new HashMap<>();
		requestApplicationVM = new HashMap<>();
		rsopTerminations = new ArrayList<>();
		nbVmConnected = rdsop.size();

		// initialize nbRequestInQueueOrInProgress
		for ( RequestSubmissionOutboundPort r : rdsopList ) {
			nbRequestInQueueOrInProgress.put( r , 0 );
		}
	}

	/**
	 * Send the Request r to the less recent ApplicationVM
	 */
	@Override
	public void acceptRequestSubmission( RequestI r ) throws Exception {
		RequestSubmissionOutboundPort rdsop = this.rdsopList.get( current );
		acceptRequest( r , rdsop );
		rdsop.submitRequest( r );
	}

	/**
	 * Send the Request r to the less recent ApplicationVM and notify its termination to the
	 * RequestGenerator
	 */
	@Override
	public void acceptRequestSubmissionAndNotify( RequestI r ) throws Exception {
		RequestSubmissionOutboundPort rdsop = this.rdsopList.get( current );
		acceptRequest( r , rdsop );
		rdsop.submitRequestAndNotify( r );
	
	}

	private void acceptRequest( RequestI r , RequestSubmissionOutboundPort rdsop ) throws Exception {
		print( this.rdURI + " submits request " + r.getRequestURI() );

		requestApplicationVM.put( r.getRequestURI() , rdsop );
		nbRequestInQueueOrInProgress.put( rdsop , nbRequestInQueueOrInProgress.get( rdsop ) + 1 );

		current = ( current + 1 ) % rdsopList.size();
		requestStartTimes.put( r.getRequestURI() , System.nanoTime() );
	}

	/**
	 * Notify the Requests termination to the RequestGenerator
	 */
	@Override
	public void acceptRequestTerminationNotification( RequestI r ) throws Exception {
		assert r != null;
		print( "Request dispatcher " + this.rdURI + "  notified the request " + r.getRequestURI() + " has ended." );
		this.rnop.notifyRequestTermination( r );

		RequestSubmissionOutboundPort rdsop = requestApplicationVM.get( r.getRequestURI() );
		nbRequestInQueueOrInProgress.put( rdsop , nbRequestInQueueOrInProgress.get( rdsop ) - 1 );

		requestEndTimes.add( new RequestTime( r.getRequestURI() , System.nanoTime() ) );

		updateRequestStates();
	}

	// public RequestDispatcherDynamicStateI getRequestProcessingTimeAvg() throws Exception {
	// long total = 0;
	// long nbRequest = 0;
	// ListIterator<RequestTime> it = requestEndTimes.listIterator( requestEndTimes.size());
	// while ( it.hasPrevious() ) {
	// RequestTime endRequest = it.previous();
	// long startTime = requestStartTimes.get( endRequest.requestURI );
	// total += endRequest.time - startTime;
	// nbRequest++;
	// }
	//
	// long avg = nbRequest == 0 ? 0 : total / nbRequest;
	// return new RequestDispatcherDynamicState( this.rdURI , avg / 1000000 );
	//
	// }

	private void updateRequestStates() {
		if ( requestEndTimes.size() > NB_REQUEST ) {
			String uri = requestEndTimes.remove( 0 ).requestURI;
			requestStartTimes.remove( uri );
			requestApplicationVM.remove( uri );
		}

	}

	public RequestDispatcherDynamicStateI getRequestProcessingTimeAvg() throws Exception {
		long total = 0;
		long nbRequest = 0;
		int i = 0;
		ListIterator<RequestTime> it = requestEndTimes.listIterator( requestEndTimes.size() );
		while ( i < NB_REQUEST && it.hasPrevious() ) {
			RequestTime endRequest = it.previous();
			long startTime = requestStartTimes.get( endRequest.requestURI );
			total += endRequest.time - startTime;
			nbRequest++; // = history
		}

		long avg = nbRequest == 0 ? 0 : total / nbRequest;
		print("nbRequest : " + nbRequest);
		return new RequestDispatcherDynamicState( this.rdURI , avg / 1000000 );

	}

	/**
	 * Disconnect all connected ports of the Request Dispatcher
	 */
	public void shutdown() throws ComponentShutdownException {
		try {
			if ( this.rnop.connected() ) {
				this.rnop.doDisconnection();
			}
			for ( int i = 0 ; i < rdsopList.size() ; i++ )
				if ( this.rdsopList.get( i ).connected() ) {
					this.rdsopList.get( i ).doDisconnection();
				}
		}
		catch ( Exception e ) {
			throw new ComponentShutdownException( e );
		}

		super.shutdown();
	}

	@Override
	public boolean isWaitingForTermination() throws Exception {
		return false;
	}

	/**
	 * Create a new RequestDispatcherSubmissionOutboundPort and return the URI of
	 * requestNotificationInboundPort (Use for AllocateVm to connect the new VM and this
	 * requestDispatcher)
	 */
	@Override
	public String connectVm( String RequestSubmissionInboundPortURI ) throws Exception {

		// Creation du Port
		String rdsopURI = rdURI + "rdsop" + nbVmConnected;
		nbVmConnected++;
		this.addRequiredInterface( RequestSubmissionI.class );
		RequestSubmissionOutboundPort r = new RequestSubmissionOutboundPort( rdsopURI , this );
		this.rdsopList.add( r );
		this.addPort( rdsopList.get(rdsopList.size() - 1) );
		rdsopList.get(rdsopList.size() - 1).localPublishPort();

		print("Connecting VM and RequestDispatcher...");
		// Connect RD with VM
		RequestSubmissionOutboundPort rsop = (RequestSubmissionOutboundPort) this.findPortFromURI(rdsopURI);
		rsop.doConnection( RequestSubmissionInboundPortURI , RequestSubmissionConnector.class.getCanonicalName() );
		print("Vm And Request Dispatcher connected");
		// update
		nbRequestInQueueOrInProgress.put( r , 0 );

		return rnip.getPortURI();
	}

	private void print( String s ) {
		this.logMessage( "[RequestDispatcher" + rdURI + "] " + s );
	}

	/**
	 * Disconnect an ApplicationVM from the RequestDispatcher
	 */
	@Override
	public void disconnectVm() throws Exception {

	}

	private class RequestTime {

		public String requestURI;
		public Long   time;

		public RequestTime( String requestURI , Long time ) {
			this.requestURI = requestURI;
			this.time = time;
		}

	}

}
