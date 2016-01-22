package fr.upmc.datacenter.software.applicationvm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;

/**
 * The class <code>ApplicationVM</code> implements the component representing
 * an application VM in the data center.
 *
 * <p><strong>Description</strong></p>
 * 
 * The Application VM (AVM) component simulates the execution of web
 * applications by receiving requests, executing them and notifying the
 * emitter of the end of execution of its request (as a way to simulate
 * the return of the result).
 * 
 * The AVM is allocated cores on processors of a single computer and uses them
 * to execute the submitted requests.  It maintain a queue for requests waiting
 * a core to become idle before beginning their execution.
 * 
 * As a component, the AVM offers a request submission service through the
 * interface <code>RequestSubmissionI</code> implemented by
 * <code>RequestSubmissionInboundPort</code> inbound port. To notify the end
 * of the execution of requests, the AVM requires the interface
 * <code>RequestNotificationI</code> through the
 * <code>RequestNotificationOutboundPort</code> outbound port.
 * 
 * The AVM can be managed (essentially allocated cores) and it offers the
 * interface <code>ApplicationVMManagementI</code> through the inbound port
 * <code>ApplicationVMManagementInboundPort</code> for this.
 * 
 * AVM uses cores on processors to execute requests. To pass the request to
 * the cores, it requires the interface <code>ProcessorServicesI</code>
 * through <code>ProcessorServicesOutboundPort</code>. It receives the
 * notifications of the end of execution of the requests by offering the
 * interface <code>ProcessorServicesNotificationI</code> through the
 * inbound port <code>ProcessorServicesNotificationInboundPort</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant	vmURI != null
 * invariant	applicationVMManagementInboundPortURI != null
 * invariant	requestSubmissionInboundPortURI != null
 * invariant	requestNotificationOutboundPortURI != null
 * </pre>
 * 
 * <p>Created on : 9 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ApplicationVM
extends		AbstractComponent
implements	ProcessorServicesNotificationConsumerI,
			RequestSubmissionHandlerI,
			ApplicationVMManagementI
{
	// ------------------------------------------------------------------------
	// Component internal state
	// ------------------------------------------------------------------------

	/** URI of this application VM.											*/
	protected String						vmURI ;
	/** Status, idle or in use, of each core allocated to this VM.			*/
	protected Map<AllocatedCore,Boolean>	allocatedCoresIdleStatus ;
	/** Map between processor URIs and the outbound ports to call them.		*/
	protected Map<String,ProcessorServicesOutboundPort>
											processorServicesPorts ;
	/** Map between processor URIs and the inbound ports through which task
	 *  termination notifications are received from each processor.			*/
	protected Map<String,ProcessorServicesNotificationInboundPort>
											processorNotificationInboundPorts ;
	/** Map between running task URIs and the processor cores running them.	*/
	protected Map<String,AllocatedCore>		runningTasks ;
	/** Queue of tasks waiting to be started.								*/
	protected Queue<TaskI>					taskQueue ;
	/* Set of task URIs	which termination will need to be notified.			*/
	protected HashSet<String>				tasksToNotify ;
	/** Inbound port offering the management interface.						*/
	protected ApplicationVMManagementInboundPort applicationVMManagementInboundPort ;
	/** Inbound port offering the request submission service of the VM.		*/
	protected RequestSubmissionInboundPort	requestSubmissionInboundPort ;
	/** Outbound port used by the VM to notify tasks' termination.			*/
	protected RequestNotificationOutboundPort
											requestNotificationOutboundPort ;

	// ------------------------------------------------------------------------
	// Component constructor
	// ------------------------------------------------------------------------

	/**
	 * create a new application VM with the given URI, the given processor
	 * cores, and the URIs to be used to create and publish its inbound and
	 * outbound ports.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete!
	 * 
	 * <pre>
	 * pre	vmURI != null
	 * pre	applicationVMManagementInboundPortURI != null
	 * pre	requestSubmissionInboundPortURI != null
	 * pre	requestNotificationOutboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param vmURI									URI of the newly created VM.
	 * @param applicationVMManagementInboundPortURI	URI of the VM management inbound port.
	 * @param requestSubmissionInboundPortURI		URI of the request submission inbound port.
	 * @param requestNotificationOutboundPortURI	URI of the request notification outbound port.
	 * @throws Exception
	 */
	public				ApplicationVM(
		String vmURI,
		String applicationVMManagementInboundPortURI,
		String requestSubmissionInboundPortURI,
		String requestNotificationOutboundPortURI
		) throws Exception
	{
		super(true, false) ;

		// Preconditions
		assert	vmURI != null ;
		assert	applicationVMManagementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationOutboundPortURI != null ;

		this.vmURI = vmURI ;
		// hash map keeping track of the idle status of cores
		this.allocatedCoresIdleStatus = new HashMap<AllocatedCore,Boolean>() ;
		// queue of awaiting tasks
		this.taskQueue = new LinkedList<TaskI>() ;
		// tasks needing a end of execution notification
		this.tasksToNotify = new HashSet<String>() ;
		// tasks currently running on the cores
		this.runningTasks = new HashMap<String,AllocatedCore>() ;

		// Interfaces and ports
		this.addOfferedInterface(ApplicationVMManagementI.class) ;
		this.applicationVMManagementInboundPort =
				new ApplicationVMManagementInboundPort(
						applicationVMManagementInboundPortURI,
						this) ;
		this.addPort(this.applicationVMManagementInboundPort) ;
		this.applicationVMManagementInboundPort.publishPort() ;
		
		this.addRequiredInterface(ProcessorServicesI.class) ;
		this.addOfferedInterface(ProcessorServicesNotificationI.class) ;
		this.processorServicesPorts =
				new HashMap<String,ProcessorServicesOutboundPort>() ;
		this.processorNotificationInboundPorts =
				new HashMap<String,ProcessorServicesNotificationInboundPort>() ;

		this.addOfferedInterface(RequestSubmissionI.class) ;
		this.requestSubmissionInboundPort =
						new RequestSubmissionInboundPort(
										requestSubmissionInboundPortURI, this) ;
		this.addPort(this.requestSubmissionInboundPort) ;
		this.requestSubmissionInboundPort.publishPort() ;

		this.addRequiredInterface(RequestNotificationI.class) ;
		this.requestNotificationOutboundPort =
			new RequestNotificationOutboundPort(
									requestNotificationOutboundPortURI,
									this) ;
		this.addPort(this.requestNotificationOutboundPort) ;
		this.requestNotificationOutboundPort.publishPort() ;
	}

	// ------------------------------------------------------------------------
	// Component life-cycle
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		// Disconnect ports to the request emitter and to the processors owning
		// the allocated cores.
		try {
			if (this.requestNotificationOutboundPort.connected()) {
				this.requestNotificationOutboundPort.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		for (ProcessorServicesOutboundPort p : this.processorServicesPorts.values()) {
			try {
				p.doDisconnection() ;
			} catch (Exception e) {
				throw new ComponentShutdownException(
							"processor services outbound port disconnection"
							+ " error", e) ;
			}
		}

		super.shutdown();
	}

	// ------------------------------------------------------------------------
	// Component services
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void			allocateCores(AllocatedCore[] allocatedCores)
	throws Exception
	{
		assert	allocatedCores != null && allocatedCores.length != 0 ;

		for(int i = 0 ; i < allocatedCores.length ; i++) {
			this.allocatedCoresIdleStatus.put(allocatedCores[i], true)  ;
		}

		// Link the VM with the newly allocated cores' processors if they are
		// not yet.
		for (int i = 0 ; i < allocatedCores.length ; i++) {
			if (!this.processorServicesPorts.
								containsKey(allocatedCores[i].processorURI)) {
				ProcessorServicesOutboundPort p =
									new ProcessorServicesOutboundPort(this) ;
				this.addPort(p) ;
				p.publishPort() ;
				p.doConnection(
					allocatedCores[i].processorInboundPortURI.
											get(ProcessorPortTypes.SERVICES),
					ProcessorServicesConnector.class.getCanonicalName()) ;
				this.processorServicesPorts.
										put(allocatedCores[i].processorURI, p) ;

				ProcessorServicesNotificationInboundPort np =
						new ProcessorServicesNotificationInboundPort(this) ;
				this.addPort(np) ;
				np.publishPort() ;
				this.processorNotificationInboundPorts.
									put(allocatedCores[i].processorURI, np) ;
			}
		}
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmission(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			acceptRequestSubmission(final RequestI r)
	throws Exception
	{
		this.taskQueue.add(new Task(r)) ;
		this.startTask() ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI#acceptRequestSubmissionAndNotify(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			acceptRequestSubmissionAndNotify(
		final RequestI r
		) throws Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
							"ApplicationVM>>acceptRequestSubmissionAndNotify") ;
		}
		this.logMessage(this.vmURI + " queues request " + r.getRequestURI());
		Task t = new Task(r) ;
		this.taskQueue.add(t) ;
		this.tasksToNotify.add(t.taskURI) ;
		this.startTask() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI#acceptNotifyEndOfTask(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void			acceptNotifyEndOfTask(TaskI t) throws Exception
	{
		this.endTask(t) ;
	}

	// ------------------------------------------------------------------------
	// Component internal services
	// ------------------------------------------------------------------------

	/**
	 * start a task if there is at least one in the queue, and an idle core on
	 * which it can be executed.
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
	public void			startTask() throws Exception
	{
		assert	!this.taskQueue.isEmpty() ;

		AllocatedCore ac = this.findIdleCore() ;
		if (ac != null) {
			this.allocatedCoresIdleStatus.remove(ac) ;
			this.allocatedCoresIdleStatus.put(ac, false) ;
			TaskI t = this.taskQueue.remove() ;
			this.logMessage(this.vmURI + " starts request " +
											t.getRequest().getRequestURI()) ;
			this.runningTasks.put(t.getTaskURI(), ac) ;
			ProcessorServicesOutboundPort p =
				this.processorServicesPorts.get(ac.processorURI) ;
			ProcessorServicesNotificationInboundPort np =
				this.processorNotificationInboundPorts.get(ac.processorURI) ;
			p.executeTaskOnCoreAndNotify(t, ac.coreNo, np.getPortURI()) ;
		}
	}

	/**
	 * update the data structures after the end of a task <code>t</code>, and
	 * if the task queue is not empty, start another one.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null && this.isRunningTask(t)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t
	 * @throws Exception
	 */
	public void			endTask(TaskI t) throws Exception
	{
		assert	t != null && this.isRunningTask(t) ;

		this.logMessage(this.vmURI + " terminates request " +
											t.getRequest().getRequestURI()) ;
		AllocatedCore ac = this.runningTasks.remove(t.getTaskURI()) ;
		this.allocatedCoresIdleStatus.remove(ac) ;
		this.allocatedCoresIdleStatus.put(ac, true) ;
		if (this.tasksToNotify.contains(t.getTaskURI())) {
			this.tasksToNotify.remove(t.getTaskURI()) ;
			this.requestNotificationOutboundPort.
									notifyRequestTermination(t.getRequest()) ;
		}
		if (!this.taskQueue.isEmpty()) {
			this.startTask() ;
		}
	}

	/**
	 * return true if the internal data structures reflects the fact that the
	 * task <code>t>/code> is currently in running state.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t	the task to be tested.
	 * @return	true if the t is currently considered as running on a core.
	 */
	public boolean			isRunningTask(TaskI t)
	{
		assert	t != null ;

		return this.runningTasks.containsKey(t.getTaskURI()) &&
						!this.allocatedCoresIdleStatus.
									get(this.runningTasks.get(t.getTaskURI())) ;
	}

	/**
	 * find an idle core and return its reference or null if none is.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	an idle core reference or null if none is.
	 */
	public AllocatedCore	findIdleCore()
	{
		AllocatedCore ret = null ;
		for (AllocatedCore ac : this.allocatedCoresIdleStatus.keySet()) {
			if (this.allocatedCoresIdleStatus.get(ac)) {
				ret = ac ;
				break ;
			}
		}
		return ret ;
	}

	protected void			printIdleStatus()
	{
		System.out.println("----------") ;
		for (AllocatedCore ac : this.allocatedCoresIdleStatus.keySet()) {
			System.out.println("*** " + ac.processorURI + " " + ac.coreNo + " " + this.allocatedCoresIdleStatus.get(ac)) ;
		}
		System.out.println("----------") ;
	}
	
	public void acceptNotificationPortURI(final String requestNotificationInboundPortURI) throws Exception{
		requestNotificationOutboundPort.doConnection(requestNotificationInboundPortURI, RequestSubmissionI.class.getCanonicalName());
	}
	
	public String[] acceptRequestNotificationPortDisconnection()
			throws Exception {
		
		this.requestNotificationOutboundPort.doDisconnection();
		String[] tab = new String[2];
		tab[0] = vmURI;
		tab[1] = requestSubmissionInboundPort.getPortURI();
		
		return tab;
	}

	@Override
	public AllocatedCore[] getCoresInVM() throws Exception {
		AllocatedCore[] tab = new AllocatedCore[allocatedCoresIdleStatus.entrySet().size()];
		int i = 0;
		for(Map.Entry<AllocatedCore, Boolean> tmp : allocatedCoresIdleStatus.entrySet()){
			tab[i] = tmp.getKey();
		}
		
		return tab;
	}
	
}
