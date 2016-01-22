package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.connectors.RingNetworkConnector;
import fr.upmc.datacenter.software.controller.Controller;
import fr.upmc.datacenter.software.controller.connectors.ControllerManagementConnector;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;
import fr.upmc.datacenter.software.controller.ports.ControllerManagementOutboundPort;
import fr.upmc.datacenter.software.interfaces.RingNetworkI;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenter.software.ports.RingNetworkInboundPort;
import fr.upmc.datacenter.software.ports.RingNetworkOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.upmc.datacenter.software.requestdispatcher.connectors.RequestDispatcherVMEndingNotificationConnector;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherVMEndingNotificationI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherVMEndingNotificationInboundPort;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherVMEndingNotificationOutboundPort;
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

public class AdmissionController extends AbstractComponent
implements AdmissionControllerManagementI, RequestDispatcherVMEndingNotificationI, RingNetworkI {

	public final static int NB_CORE = 2;
	public final static long THRESHOLD_ADDING_VM_MS = 500000L;
	public final static long THRESHOLD_REMOVING_VM_RING_MS = 5000L;


	/** the URI of the component. */
	protected String acURI;

	/** The inbound port used to receive application */
	protected ApplicationSubmissionInboundPort asip;

	/**
	 * The inbound port used to be notified when the requestgenerator is created (by the AP)
	 */
	protected ApplicationNotificationInboundPort               anip;

	/**
	 * The inbound port used to be notified the end of a VM (by RD)
	 */
	protected RequestDispatcherVMEndingNotificationInboundPort rdvenip;

	/** The outbound port used to allocate core to the vm */
	protected List<ApplicationVMManagementOutboundPort> avmop;

	/** map associate vm submission inbound port uri with its management outbound port */
	protected Map<String , ApplicationVMManagementOutboundPort> avmopMap;

	/** the outbound port of the computer service */
	protected ComputerServicesOutboundPort[] csop;


	/** InboundPort used by the ring network */
	protected RingNetworkInboundPort rnetip;

	/** OutboundPOrt used to pass VM on the network */
	protected RingNetworkOutboundPort rnetop;


	private int cpt = 0;

	private Map<String , RequestNotificationOutboundPort> rnopList;

	/** array associate the index of the computer with the number of available cores */
	private int[] nbAvailablesCores;

	/** Uri of the computer **/
	private String[] computerURI;

	/** Map between RequestDispatcher URIs and the outbound ports to call them. */
	protected Map<String , RequestDispatcherManagementOutboundPort> rdmopList;

	/**
	 * Map between RequestSubmissionInboundPort URIs and the inboundPort
	 */
	protected Map<String , RequestSubmissionInboundPort> rsipList;

	/** Inbound port used by the controlller to manage the AdmissionController */
	protected AdmissionControllerManagementInboundPort acmip;

	public boolean controlVmFrequency = false;

	private int nbVMCreated = 0;

	private int currentAVMOP = 0;

	/** indexes are computers and values are whether it's used or not */
	private boolean computerUsed[];

	private final Integer coresPerComputers;

	/** map associate processor URIs with their outboundport */
	private Map<String , ProcessorManagementOutboundPort> pmops;

	private List<AllocatedCore> allocatedCores;

	/** map associate avmop with csop index **/
	private Map<ApplicationVMManagementOutboundPort , Integer> avmopComp;

	/** map associate VmURIs with avmop **/
	private Map<String, ApplicationVMManagementOutboundPort> vmAvmop;
	protected Integer[] frequencies;

	private long lastVMReceived;

	/**
	 * Create an admission controller
	 * 
	 * @param applicationSubmissionInboundPortURI URI of the application submission inbound port
	 * @param applicationNotificationInboundPortURI URI of the application notification inbound port
	 * @param computerServiceOutboundPortURI URI of the computer service outbout port
	 * @param ComputerDynamicStateDataOutboundPort URI of the computer dynamic state data outbound
	 *            port
	 * @param computerURI URI of computer(s)
	 * @throws Exception
	 */
	public AdmissionController( String acURI , String applicationSubmissionInboundPortURI ,
			String requestDispatcherVMEndingNotificationInboundPortURI , String applicationNotificationInboundPortURI ,
			String AdmissionControllerManagementInboundPortURI , String ringNetworkInboundPortURI, String ringNetworkOutboundPortURI, String computerServiceOutboundPortURI[] ,
			String computerURI[] , int[] nbAvailableCoresPerComputer , Map<String , String> pmipURIs, Integer[] frequencies )
					throws Exception {
		super( 2 , 2 );
		this.acURI = acURI;
		this.addOfferedInterface( ApplicationSubmissionI.class );
		this.asip = new ApplicationSubmissionInboundPort( applicationSubmissionInboundPortURI , this );
		this.addPort( asip );
		this.asip.publishPort();

		this.addOfferedInterface( ApplicationNotificationI.class );
		this.anip = new ApplicationNotificationInboundPort( applicationNotificationInboundPortURI , this );
		this.addPort( anip );
		this.anip.publishPort();

		this.addOfferedInterface( AdmissionControllerManagementI.class );
		this.acmip = new AdmissionControllerManagementInboundPort( AdmissionControllerManagementInboundPortURI , this );
		this.addPort( acmip );
		this.acmip.publishPort();

		this.addOfferedInterface( RequestDispatcherVMEndingNotificationI.class );
		this.rdvenip = new RequestDispatcherVMEndingNotificationInboundPort(
				requestDispatcherVMEndingNotificationInboundPortURI , this );
		this.addPort( rdvenip );
		this.rdvenip.publishPort();

		this.csop = new ComputerServicesOutboundPort[computerServiceOutboundPortURI.length];
		this.computerURI = computerURI;
		for ( int i = 0 ; i < computerServiceOutboundPortURI.length ; i++ ) {
			this.addRequiredInterface( ComputerServicesI.class );
			this.csop[i] = new ComputerServicesOutboundPort( computerServiceOutboundPortURI[i] , this );
			this.addPort( csop[i] );
			this.csop[i].publishPort();
		}

		this.addRequiredInterface(RingNetworkI.class);
		this.addOfferedInterface(RingNetworkI.class);

		this.rnetop = new RingNetworkOutboundPort(ringNetworkOutboundPortURI, this);
		this.addPort(this.rnetop);
		this.rnetop.publishPort();

		this.rnetip = new RingNetworkInboundPort(ringNetworkInboundPortURI, this);
		this.addPort(this.rnetip);
		this.rnetip.publishPort();

		pmops = new HashMap<>();
		int i = 0;
		this.addRequiredInterface( ProcessorManagementI.class );
		for ( Map.Entry<String , String> entry : pmipURIs.entrySet() ) {
			ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort( acURI + "pmop" + i , this );
			pmops.put( entry.getKey() , pmop );
			this.addPort( pmop );
			pmop.publishPort();
			pmop.doConnection( entry.getValue() , ProcessorManagementConnector.class.getCanonicalName() );
			i++;
		}

		// Allocation Hashmap
		nbAvailablesCores = nbAvailableCoresPerComputer;

		coresPerComputers = nbAvailableCoresPerComputer[0];

		rnopList = new HashMap<>();
		avmop = new ArrayList<>();
		rdmopList = new HashMap<>();
		avmopComp = new HashMap<>();
		lastVMReceived = -1;

		rsipList = new HashMap<>();
		avmopMap = new HashMap<>();
		vmAvmop = new HashMap<>();
		allocatedCores = new ArrayList<>();
		this.frequencies = frequencies;

	}

	private boolean isUsed( int computerIndex ) {
		return nbAvailablesCores[computerIndex] < coresPerComputers;

	}

	private Integer getAvailableCores( int nbCores ) {
		int max = 0;
		Integer index = -1;
		for ( int i = 0 ; i < nbAvailablesCores.length ; i++ ) {
			if ( nbAvailablesCores[i] == nbCores ) {
				return i;
			}
			if ( nbAvailablesCores[i] > max ) {
				max = nbAvailablesCores[i];
				index = i;
			}
		}
		return index;
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
		Integer index = getAvailableCores( NB_CORE );

		AllocatedCore[] ac;
		if ( index != null ) {
			ac = this.csop[index].allocateCores( NB_CORE );
			nbAvailablesCores[index] = nbAvailablesCores[index] - ac.length;

			// Add allocated cores to the allocatedCores list
			for ( int i = 0 ; i < ac.length ; i++ ) {
				allocatedCores.add( ac[i] );
			}

			print( "Resources found! (" + ac.length + " available Core(s))" );

			// Creation d'une VM
			print( "Creating an applicationVM..." );
			String vmURI = createURI( "vm" );
			ApplicationVM vm = new ApplicationVM( vmURI , createURI( "avmip" ) , createURI( "rsip" ) ,
					createURI( "rnop" ) );
			AbstractCVM.theCVM.addDeployedComponent( vm );
			ApplicationVMManagementOutboundPort avmopTemp = new ApplicationVMManagementOutboundPort(
					createURI( "avmop" ) , new AbstractComponent() {} );
			avmop.add( avmopTemp );
			avmop.get( cpt ).publishPort();
			avmop.get( cpt ).doConnection( createURI( "avmip" ) ,
					ApplicationVMManagementConnector.class.getCanonicalName() );
			vm.start();
			avmopMap.put( vmURI , avmopTemp );
			rsipList.put( createURI( "rsip" ) ,
					( RequestSubmissionInboundPort ) vm.findPortFromURI( createURI( "rsip" ) ) );

			avmopComp.put( avmopTemp , index );
			vmAvmop.put(vmURI, avmopTemp);
			
			// AllocateCore des computers aux VMs
			this.avmop.get( cpt ).allocateCores( ac );
			print( ac.length + " cores allocated." );

			// Création d'un requestdispatcher
			print( "Creating the requestDispatcher..." );
			List<String> rdsop = new ArrayList<>();
			rdsop.add( createURI( "rdsop" ) );
			List<String> vmURIs = new ArrayList<>();
			vmURIs.add( vmURI );
			String rdURI = createURI( "rd" ) ;
			RequestDispatcher rd = new RequestDispatcher( rdURI, createURI( "rdsip" ) ,
					createURI( "rdmip" ) , rdsop , vmURIs , createURI( "rdvenop" ) , createURI( "rdnop" ) ,
					createURI( "rdnip" ) , createURI("cmop"), createURI( "rddsdip" ) );
			rd.start();
			String rdnop = createURI( "rdnop" );
			rnopList.put( rdnop , ( RequestNotificationOutboundPort ) ( rd.findPortFromURI( rdnop ) ) );

			// Creation Request Dispatcher Management Outbound Port
			rdmopList.put( createURI( "rd" ) ,
					new RequestDispatcherManagementOutboundPort( createURI( "rdmop" ) , new AbstractComponent() {} ) );
			rdmopList.get( createURI( "rd" ) ).publishPort();
			rdmopList.get( createURI( "rd" ) ).doConnection( createURI( "rdmip" ) ,
					RequestDispatcherManagementConnector.class.getCanonicalName() );
			AbstractCVM.theCVM.addDeployedComponent( rd );

			// Connect RD with AC
			RequestDispatcherVMEndingNotificationOutboundPort rdvenop = ( RequestDispatcherVMEndingNotificationOutboundPort ) rd
					.findPortFromURI( createURI( "rdvenop" ) );
			rdvenop.doConnection( rdvenip.getPortURI() ,
					RequestDispatcherVMEndingNotificationConnector.class.getCanonicalName() );

			// Connect RD with VM
			RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rd.findPortFromURI( rdsop.get( 0 ) );
			rsop.doConnection( createURI( "rsip" ) , RequestSubmissionConnector.class.getCanonicalName() );
			RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
					.findPortFromURI( createURI( "rnop" ) );
			rnop.doConnection( createURI( "rdnip" ) , RequestNotificationConnector.class.getCanonicalName() );

			// Allocate remaining VMs
			for ( int i = 0 ; i < nbVm - 1; i++ ) {
				this.allocateVM( rdURI, false );                
			}

			// Create controller
			print( "Creating the controller..." );


			Controller controller = new Controller( createURI( "c" ) , createURI( "rd" ) , createURI("cmip"), createURI( "acmop" ) ,
					createURI("rdmop"), createURI( "rddsdip" ), createURI("rnetip"), createURI("rnetop"), frequencies);
			controller.toggleLogging();
			controller.toggleTracing();
			controller.start();
			// Connect Controller with AdmissionController
			AdmissionControllerManagementOutboundPort acmop = ( AdmissionControllerManagementOutboundPort ) controller
					.findPortFromURI( createURI( "acmop" ) );
			acmop.doConnection( acmip.getPortURI() , AdmissionControllerManagementConnector.class.getCanonicalName() );

			// Connect Controller with RequestDispatcher
			print("Connecting Controller and RequestDispatcher");
			RequestDispatcherManagementOutboundPort rdmop = (RequestDispatcherManagementOutboundPort) controller.findPortFromURI(createURI("rdmop"));
			rdmop.doConnection(createURI( "rdmip" ) , RequestDispatcherManagementConnector.class.getCanonicalName() );

			ControllerManagementOutboundPort cmop = (ControllerManagementOutboundPort) rd.findPortFromURI(createURI("cmop"));
			print(cmop.getPortURI());
			cmop.doConnection(createURI("cmip"), ControllerManagementConnector.class.getCanonicalName());
			//	synchronized(this){
		
			if(!rnetop.connected()){
				print("Connecting to the Ring network");
				// Network Connect AdmissionController -> Controller
				rnetop.doConnection(createURI("rnetip"), RingNetworkConnector.class.getCanonicalName());

				// Network Connect Controller -> AdmissionController
				RingNetworkOutboundPort ringNetworkOutboundPort = (RingNetworkOutboundPort) controller.findPortFromURI(createURI("rnetop"));
				ringNetworkOutboundPort.doConnection(rnetip.getPortURI(), RingNetworkConnector.class.getCanonicalName());
			}

			else{
				//Network Disconnect previous Connection AdmissionController -> Controller
				String ringNetworkInboundPort = rnetop.getServerPortURI();
				rnetop.doDisconnection();

				// Network Connect AdmissionController -> Controller
				rnetop.doConnection(createURI("rnetip"), RingNetworkConnector.class.getCanonicalName());

				// Network Connect Controller -> Controller
				RingNetworkOutboundPort ringNetworkOutboundPort = (RingNetworkOutboundPort) controller.findPortFromURI(createURI("rnetop"));
				ringNetworkOutboundPort.doConnection(ringNetworkInboundPort, RingNetworkConnector.class.getCanonicalName());

			}


			//}
			controller.startControlling();

			//Creating a new VM and sending it in the ring
			String tab[] = allocateVM("", true);
			if(tab != null){
				print("Putting VM " + tab[0] + " in the ring");
			}


			String res[] = new String[2];

			res[0] = createURI( "rdsip" );
			res[1] = rdnop;
			
			this.rnetop.sendVM(tab[0], tab[1]);
			
			if(!controlVmFrequency){
				controlVmFrequency = true;
				controlVmArrivalFrequency();
			}
			
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

	/** Use for allocating new Vm */
	private String createVMURI( String uri ) {
		return acURI + uri + cpt + nbVMCreated;
	}

	private void print( String s ) {
		this.logMessage( "[AdmissionController] " + s );
	}

	public String[] allocateVM( String RequestDispatcherURI, boolean vmToSendToRing ) throws Exception {
		// Verifier que des resources sont disponibles
		print( "Looking for available resources..." );
		Integer index = getAvailableCores( NB_CORE );
		AllocatedCore[] ac;
		if ( index != -1 ) {
			String[] tab = new String[2];
			ac = this.csop[index].allocateCores( NB_CORE );
			nbAvailablesCores[index] = nbAvailablesCores[index] - ac.length;

			// Add allocated cores to the allocatedCores list
			for ( int i = 0 ; i < ac.length ; i++ ) {
				allocatedCores.add( ac[i] );
			}

			// Allocation of VM
			String vmURI = createVMURI( "vm" );
			ApplicationVM vm = new ApplicationVM( vmURI , createVMURI( "avmip" ) , createVMURI( "rsip" ) ,
					createVMURI( "rnop" ) );
			AbstractCVM.theCVM.addDeployedComponent( vm );
			ApplicationVMManagementOutboundPort avmopTemp = new ApplicationVMManagementOutboundPort(
					createVMURI( "avmop" ) , new AbstractComponent() {} );
			avmop.add( avmopTemp );
			avmop.get( avmop.size() - 1 ).publishPort();
			avmop.get( avmop.size() - 1 ).doConnection( createVMURI( "avmip" ) ,
					ApplicationVMManagementConnector.class.getCanonicalName() );
			avmopMap.put( vmURI , avmop.get( avmop.size() - 1 ) );
			avmopComp.put( avmopTemp , index );
			vmAvmop.put(vmURI, avmopTemp);
			
			// AllocatedCore to VM
			avmop.get( avmop.size() - 1 ).allocateCores( ac );
			print( ac.length + " cores allocated." );
			if(!vmToSendToRing)
				this.rdmopList.get( RequestDispatcherURI ).connectVm( vmURI , createVMURI( "rsip" ) );
			rsipList.put( createVMURI( "rsip" ) ,
					( RequestSubmissionInboundPort ) vm.findPortFromURI( createVMURI( "rsip" ) ) );
			
			vm.start();
			vm.toggleLogging();
			vm.toggleTracing();
			print( "VM Allocated!" );

			tab[0] = vmURI;
			tab[1] = createVMURI( "rsip" );
			nbVMCreated++;
			return tab;
		}
		else {
			print( "Could not allocate VM (no Core Available)" );
			return null;
		}
	}

	//		@Override
	//		public void removeVM( String RequestDispatcherURI ) throws Exception {
	//			rdmopList.get( RequestDispatcherURI ).disconnectVm();
	//
	//
	//		}

	@Override
	public void notifyAdmissionControllerVMFinishRequest( String RequestSubmissionInboundPortURI ) throws Exception {
		print( "Shutting down VM..." );
		rsipList.get( RequestSubmissionInboundPortURI ).getOwner().shutdown();

		// update available cores

	}

	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			for ( int i = 0 ; i < cpt ; i++ ) {
				if ( this.avmop.get( i ).connected() ) {
					this.avmop.get( i ).doDisconnection();
				}

				if ( this.rnopList.get( acURI + "rdnop" + i ).connected() ) {
					this.rnopList.get( acURI + "rdnop" + i ).doDisconnection();
				}
			}

			for ( int i = 0 ; i < csop.length ; i++ ) {
				if ( this.csop[i].connected() ) {
					this.csop[i].doDisconnection();
				}
			}

			for ( RequestDispatcherManagementOutboundPort rdmop : rdmopList.values() ) {
				if ( rdmop.connected() )
					rdmop.doDisconnection();
			}
		}
		catch ( Exception e ) {
			throw new ComponentShutdownException( e );
		}
		super.shutdown();
	}

	@Override
	public boolean addCores( String rdURI , int nbCores ) throws Exception {
		// parcourir les computers utilisés
		print( "Looking for available resources (addCores)..." );
		boolean ok = false;
		int nbAllocated = 0;
		String s = rdmopList.get( rdURI ).getMostBusyVMURI();

		int compIndex = avmopComp.get( avmopMap.get( s ) );
		AllocatedCore[] ac = csop[compIndex].allocateCores( nbCores );
		ok = ( ( nbAllocated = ac.length ) > 0 );

		if ( ok ) {
			avmopMap.get( s ).allocateCores( ac );
			print( nbAllocated + " cores allocated on the computer " + currentAVMOP );
		}
		else
			print( "No core available on the computer  " + compIndex );
		nbAvailablesCores[compIndex] = nbAvailablesCores[compIndex] - nbAllocated;

		return ok;
	}

	@Override
	public void setFrequency( Integer f ) throws Exception {
		// parcourir les processeurs utilisés
		for ( AllocatedCore a : allocatedCores ) {
			pmops.get( a.processorURI ).setCoreFrequency( a.coreNo , 3000 );
		}
	}

	@Override
	public void sendVM(String vmURI, String requestSubmissionInboundPortURI)
			throws Exception {
		Date testDate;
		//if we did not already receive a VM from the ring, we just have to set the lastVMreceived date.
		if(lastVMReceived == -1){
			testDate = new Date();
			lastVMReceived = testDate.getTime();
			rnetop.sendVM(vmURI, requestSubmissionInboundPortURI);
		}
		else{
			// Test if the difference betweencurrent time and last time receivedVM 
			//is out of the threshold we have set. 

			testDate = new Date();
			long timeSinceLastVMReceivedInMs = testDate.getTime() - lastVMReceived;
			if(timeSinceLastVMReceivedInMs < THRESHOLD_REMOVING_VM_RING_MS){
				// remove the vm from the ring
				AllocatedCore[] ac = vmAvmop.get(vmURI).getCoresInVM();
				ComputerServicesOutboundPort csoptmp = csop[avmopComp.get(vmAvmop.get(vmURI))];
				csoptmp.releaseCores(ac);
				rsipList.get(requestSubmissionInboundPortURI).getOwner().shutdown();
				
			}
			//else{
				rnetop.sendVM(vmURI, requestSubmissionInboundPortURI);
			//}

		}


	}
	
	/**
	 *ScheduleTask to control the frequency of arrival of VM. If the frequency is too low, we create a new vm 
	 * and we put it in the ring
	 */
	public void controlVmArrivalFrequency(){
		this.scheduleTaskAtFixedRate( new ComponentI.ComponentTask() {
			
			@Override
			public void run() {
				Date testDate = new Date();
				if(lastVMReceived != -1)
					if(testDate.getTime() - lastVMReceived > THRESHOLD_ADDING_VM_MS){
						//Create new Vm
						try {
							String tab[] = allocateVM("", true);
							rnetop.sendVM(tab[0], tab[1]);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						
				
			}
			
			
		}, 1l , 1l , TimeUnit.SECONDS );
	}
	
	

}

