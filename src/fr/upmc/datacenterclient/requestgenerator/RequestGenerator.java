package fr.upmc.datacenterclient.requestgenerator;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.RandomDataGenerator;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.tests.TestApplicationVM.Request;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import fr.upmc.datacenterclient.utils.TimeProcessing;

/**
 * The class <code>RequestGenerator</code> implements a component that generates
 * requests for an application and submit them to an Application VM component.
 *
 * <p><strong>Description</strong></p>
 * 
 * A request has a processing time and an arrival process that both follow an
 * exponential probability distribution.  The generation process is started by
 * executing the method <code>generateNextRequest</code> as a component task.
 * It generates an instance of the class <code>Request</code>, with a processing
 * time generated from its exponential distribution, and then schedule its next
 * run after the inter-arrival time also generated from its exponential
 * distribution.  To stop the generation process, the method
 * <code>shutdown</code> uses the future returned when scheduling the next
 * request generation to cancel its execution.
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 5 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RequestGenerator
extends		AbstractComponent
implements	RequestNotificationHandlerI
{
	// -------------------------------------------------------------------------
	// Constructors and instance variables
	// -------------------------------------------------------------------------

	/** the URI of the component.											*/
	protected final String						rgURI ;
	/** a random number generator used to generate processing times.		*/
	protected RandomDataGenerator				rng ;
	/** a counter used to generate request URI.								*/
	protected int								counter ;
	/** the mean inter-arrival time of requests in ms.						*/
	protected double							meanInterArrivalTime ;
	/** the mean processing time of requests in ms.							*/
	protected long								meanNumberOfInstructions ;

	/** the inbound port provided to manage the component.					*/
	protected RequestGeneratorManagementInboundPort rgmip ;
	/** the output port used to send requests to the service provider.		*/
	protected RequestSubmissionOutboundPort		rsop ;
	/** the inbound port receiving end of execution notifications.			*/
	protected RequestNotificationInboundPort	rnip ;
	/** a future pointing to the next request generation task.				*/
	protected Future<?>							nextRequestTaskFuture ;

	/**
	 * create a request generator component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	meanInterArrivalTime > 0.0 && meanNumberOfInstructions > 0
	 * pre	outboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param meanInterArrivalTime		mean interarrival time of the requests in ms.
	 * @param meanNumberOfInstructions	mean number of instructions of the requests in ms.
	 * @param requestSubmissionOutboundPortURI	URI of the outbound port to connect to the request processor.
	 * @param requestNotificationInboundPortURI URI of the inbound port to receive notifications of the request execution progress.
	 * @throws Exception
	 */
	public				RequestGenerator(
		String rgURI,
		double meanInterArrivalTime,
		long meanNumberOfInstructions,
		String managementInboundPortURI,
		String requestSubmissionOutboundPortURI,
		String requestNotificationInboundPortURI
		) throws Exception
	{
		super(true, true) ;

		assert	meanInterArrivalTime > 0.0 && meanNumberOfInstructions > 0 ;
		assert	requestSubmissionOutboundPortURI != null ;

		this.rgURI = rgURI ;
		this.counter = 0 ;
		this.meanInterArrivalTime = meanInterArrivalTime ;
		this.meanNumberOfInstructions = meanNumberOfInstructions ;
		this.rng = new RandomDataGenerator() ;
		this.rng.reSeed() ;
		this.nextRequestTaskFuture = null ;

		this.addOfferedInterface(RequestGeneratorManagementI.class) ;
		this.rgmip = new RequestGeneratorManagementInboundPort(
												managementInboundPortURI, this) ;
		this.addPort(this.rgmip) ;
		this.rgmip.publishPort() ;

		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.rsop = new RequestSubmissionOutboundPort(requestSubmissionOutboundPortURI, this) ;
		this.addPort(this.rsop) ;
		this.rsop.publishPort() ;

		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip =
			new RequestNotificationInboundPort(requestNotificationInboundPortURI, this) ;
		this.addPort(this.rnip) ;
		this.rnip.publishPort() ;

		assert	this.rng != null && this.counter >= 0 ;
		assert	this.meanInterArrivalTime > 0.0 ;
		assert	this.meanNumberOfInstructions > 0 ;
		assert	this.rsop != null && this.rsop instanceof RequestSubmissionI ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * shut down the component, first canceling any future request generation
	 * already scheduled.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		if (this.nextRequestTaskFuture != null &&
							!(this.nextRequestTaskFuture.isCancelled() ||
							  this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true) ;
		}

		try {
			if (this.rsop.connected()) {
				this.rsop.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * start the generation and submission of requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			startGeneration() throws Exception
	{
		this.generateNextRequest() ;
	}

	/**
	 * stop the generation and submission of requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			stopGeneration() throws Exception
	{
		if (this.nextRequestTaskFuture != null &&
						!(this.nextRequestTaskFuture.isCancelled() ||
										this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true) ;
		}
	}

	/**
	 * generate a new request with some processing time following an exponential
	 * distribution and then schedule the next request generation in a delay
	 * also following an exponential distribution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			generateNextRequest() throws Exception
	{
		// generate a random number of instructions for the request.
		long noi =
			(long) this.rng.nextExponential(this.meanNumberOfInstructions) ;
		Request r = new Request(this.rgURI + "-" + this.counter++, noi) ;
		final RequestGenerator cg = this ;
		// generate a random delay until the next request generation.
		long interArrivalDelay =
				(long) this.rng.nextExponential(this.meanInterArrivalTime) ;
		this.logMessage(
			"Request generator " + this.rgURI + 
			" submitting request " + r.getRequestURI() + " at " +
			TimeProcessing.toString(System.currentTimeMillis() +
														interArrivalDelay) +
			" with number of instructions " + noi) ;
		// submit the current request.
		this.rsop.submitRequestAndNotify(r) ;
		// schedule the next request generation.
		this.nextRequestTaskFuture =
			this.scheduleTask(
				new ComponentTask() {
					@Override
					public void run() {
						try {
							cg.generateNextRequest() ;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				},
				interArrivalDelay, TimeUnit.MILLISECONDS) ;
	}

	/**
	 * process an end of execution notification for a request r previously
	 * submitted. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r	request that just terminated.
	 * @throws Exception
	 */
	@Override
	public void			acceptRequestTerminationNotification(RequestI r)
	throws Exception
	{
		assert	r != null ;

		this.logMessage("Request generator " + this.rgURI + " is notified that request "+ r.getRequestURI() + " has ended.") ;
	}
}
