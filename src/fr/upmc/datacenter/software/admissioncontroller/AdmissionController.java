package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.connectors.AdmissionControllerManagementConnector;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.ports.AdmissionControllerManagementOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.controller.Controller;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.software.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
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
public class AdmissionController extends AbstractComponent implements 
ComputerStateDataConsumerI, AdmissionControllerManagementI{

	public final static int NB_CORE = 2;

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

	/** the outbound port of the ComputerDynamicStateOutboundPort service */
	protected ComputerDynamicStateDataOutboundPort[] cdsop;

	private int cpt = 0;

	private Map<String , RequestNotificationOutboundPort> rnopList;

	/** Map containing the ressources (core) and their state (true reserved, false not reserved) */
	private Map<String, Map<AllocatedCore, Boolean>> tabCore;

	/** Uri of the computer **/
	private String[] computerURI;

	/** Map between RequestDispatcher URIs and the outbound ports to call them.		*/
	protected Map<String, RequestDispatcherManagementOutboundPort> rdmopList;

	/** Map between RequestDispatcher URIs and the inbound ports through which request
	 *  termination notifications are received from each applicationVM.			*/
	protected Map<String, RequestNotificationInboundPort> rdnipList;

	/** Inbound port used by the controlller to manage the AdmissionController */
	protected AdmissionControllerManagementInboundPort acmip;

	private int nbVMCreated = 0;

	/**
	 * Create an admission controller
	 * 
	 * @param applicationSubmissionInboundPortURI URI of the application submission inbound port
	 * @param applicationNotificationInboundPortURI URI of the application notification inbound port
	 * @param computerServiceOutboundPortURI URI of the computer service outbout port
	 * @param ComputerDynamicStateDataOutboundPort URI of the computer dynamic state data outbound port
	 * @param computerURI URI of computer(s) 
	 * @throws Exception
	 */
	public AdmissionController( String acURI , String applicationSubmissionInboundPortURI ,
			String applicationNotificationInboundPortURI, 
			String AdmissionControllerManagementInboundPortURI,
			String computerServiceOutboundPortURI[],
			String ComputerDynamicStateDataOutboundPort[],
			String computerURI[]) throws Exception {
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

		this.addOfferedInterface(AdmissionControllerManagementI.class);
		this.acmip = new AdmissionControllerManagementInboundPort(AdmissionControllerManagementInboundPortURI, this);
		this.addPort(acmip);
		this.acmip.publishPort();

		this.cdsop = new ComputerDynamicStateDataOutboundPort[computerServiceOutboundPortURI.length];
		this.csop = new ComputerServicesOutboundPort[computerServiceOutboundPortURI.length];
		this.computerURI = computerURI;
		for ( int i = 0 ; i < computerServiceOutboundPortURI.length ; i++ ) {
			this.addRequiredInterface( ComputerServicesI.class );
			this.csop[i] = new ComputerServicesOutboundPort( computerServiceOutboundPortURI[i] , this );
			this.addPort( csop[i] );
			this.csop[i].publishPort();
			this.addRequiredInterface( ComputerDynamicStateI.class );
			this.cdsop[i] = new ComputerDynamicStateDataOutboundPort( ComputerDynamicStateDataOutboundPort[i], 
					this, computerURI[i]);
			this.addPort( cdsop[i] );
			this.cdsop[i].publishPort();
		}

		// Allocation Hashmap
		this.tabCore = new HashMap<String, Map<AllocatedCore, Boolean>>();

		rnopList = new HashMap<>();
		avmop = new ArrayList<>();
		rdmopList = new HashMap<>();
		rdnipList = new HashMap<>();
	}

	/**
	 * Fill map tabCore of all cores from computer(s)
	 * @throws Exception
	 */
	public void fillCore() throws Exception{
		for(int i = 0; i < csop.length; i++){
			AllocatedCore[] listCores = csop[i].allocateCores(50);
			HashMap<AllocatedCore, Boolean> listCoresFromComputer = new HashMap<AllocatedCore, Boolean>();
			listCoresFromComputer = new HashMap<AllocatedCore, Boolean>();
			for(int j = 0; j < listCores.length; j++){
				listCoresFromComputer.put(listCores[j], false);
			}
			this.tabCore.put(computerURI[i], listCoresFromComputer);
		}
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
		ArrayList<AllocatedCore> ac = new ArrayList<AllocatedCore>();
		ArrayList<AllocatedCore> tmp;
		for ( int i = 0 ; i < computerURI.length ; i++) {
			tmp = new ArrayList<AllocatedCore>();
			print("Looking for " + NB_CORE + " available core in Computer " + computerURI[i] + "...");
			for(Map.Entry<AllocatedCore, Boolean> res : tabCore.get(computerURI[i]).entrySet()){
				if(!res.getValue()){
					tmp.add(res.getKey());
				}
				if(tmp.size() == NB_CORE)
					break;
			}
			if(tmp.size() == NB_CORE){
				ac.addAll(tmp);
				break;
			}
			else{
				if(tmp.size() > ac.size()){
					ac = new ArrayList<AllocatedCore>();
					ac.addAll(tmp);
				}
			}
		}
		if ( ac.size() != 0 ) {
			print( "Resources found! (" + ac.size() + " available Core(s))" );

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
			AllocatedCore[] coreList = new AllocatedCore[ac.size()];
			for(int i = 0; i < ac.size(); i++)
				coreList[i] = ac.get(i);

			this.avmop.get( cpt ).allocateCores( coreList );
			print( ac.size() + " cores allocated." );

			// Création d'un requestdispatcher
			print( "Creating the requestDispatcher..." );
			List<String> rdsop = new ArrayList<>();
			rdsop.add( createURI( "rdsop" ) );
			RequestDispatcher rd = new RequestDispatcher( createURI( "rd" ) , createURI( "rdsip" ) , createURI( "rdmip" ), rdsop ,
					createURI( "rdnop" ) , createURI( "rdnip" ) , createURI( "rddsdip" ) );
			rd.start();
			String rdnop = createURI( "rdnop" );
			rnopList.put( rdnop , ( RequestNotificationOutboundPort ) ( rd.findPortFromURI( rdnop ) ) );

			// Creation Request Dispatcher Management Outbound Port
			rdmopList.put(createURI("rd"), new RequestDispatcherManagementOutboundPort(createURI("rdmop"), new AbstractComponent() {}));
			rdmopList.get(createURI("rd")).publishPort();
			rdmopList.get(createURI("rd")).doConnection(createURI("rdmip"), RequestDispatcherManagementConnector.class.getCanonicalName());
			AbstractCVM.theCVM.addDeployedComponent( rd );

			// Connect RD with VM
			RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rd.findPortFromURI( rdsop.get( 0 ) );
			rsop.doConnection( createURI( "rsip" ) , RequestSubmissionConnector.class.getCanonicalName() );
			RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
					.findPortFromURI( createURI( "rnop" ) );
			rnop.doConnection( createURI( "rdnip" ) , RequestNotificationConnector.class.getCanonicalName() );

			// Create controller
			print( "Creating the controller..." );
			Controller controller = new Controller( createURI( "c" ) , createURI( "rd" ) , createURI("acmop"), createURI( "rddsdip" ) );
			controller.toggleLogging();
			controller.toggleTracing();

			// Connect Controller with AdmissionController
			AdmissionControllerManagementOutboundPort acmop = (AdmissionControllerManagementOutboundPort) controller.findPortFromURI(createURI("acmop"));
			acmop.doConnection(acmip.getPortURI(), AdmissionControllerManagementConnector.class.getCanonicalName());

			controller.startControlling();

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

	/** Use for allocating new Vm */
	private String createVMURI(String uri){
		return acURI + uri + nbVMCreated;
	}

	private void print( String s ) {
		this.logMessage( "[AdmissionController] " + s );
	}

	@Override
	public void acceptComputerStaticData(String computerURI,
			ComputerStaticStateI staticState) throws Exception {
		return;
	}

	@Override
	public void acceptComputerDynamicData(String computerURI,
			ComputerDynamicStateI currentDynamicState) throws Exception {
		boolean[][] listCore = currentDynamicState.getCurrentCoreReservations();
		ArrayList<Boolean> coreStatus = new ArrayList<>();
		for(int i = 0; i < listCore.length; i++){
			for(int j = 0; j < listCore.length; j++){
				coreStatus.add(listCore[i][j]);
			}
		}
		int i = 0;
		for(Map.Entry<AllocatedCore, Boolean> res : tabCore.get(computerURI).entrySet()){
			tabCore.get(computerURI).put(res.getKey(), coreStatus.get(i));
			i++;
		}
	}

	@Override
	public void allocateVM(String RequestDispatcherURI) throws Exception{
		// Allocation of VM
		ApplicationVM vm = new ApplicationVM( createVMURI( "vm" ) , createVMURI( "avmip" ) , createVMURI( "rsip" ) ,
				createVMURI( "rnop" ) );
		AbstractCVM.theCVM.addDeployedComponent( vm );

		avmop.add( new ApplicationVMManagementOutboundPort( createVMURI( "avmop" ) , new AbstractComponent() {} ) );
		avmop.get( cpt ).publishPort();
		avmop.get( cpt ).doConnection( createVMURI( "avmip" ) ,
				ApplicationVMManagementConnector.class.getCanonicalName() );
		vm.start();
		nbVMCreated++;
		
		String RequestNotificationInboundport = this.rdmopList.get(RequestDispatcherURI).connectVm(createVMURI( "rsip" ));
		// Connected RequestDispatcher -- VM
		RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
				.findPortFromURI( createVMURI( "rnop" ) );
		rnop.doConnection( RequestNotificationInboundport , RequestNotificationConnector.class.getCanonicalName() );

	}

	@Override
	public void removeVM(String RequestDispatcherURI) throws Exception{

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

			for(int i = 0; i < csop.length; i++){
				if ( this.csop[i].connected() ) {
					this.csop[i].doDisconnection();
				}

				if (this.cdsop[i].connected() )
					this.cdsop[i].connected();

			}

			for(RequestDispatcherManagementOutboundPort rdmop : rdmopList.values()){
				if(rdmop.connected())
					rdmop.doDisconnection();
			}
		}
		catch ( Exception e ) {
			throw new ComponentShutdownException( e );
		}
		super.shutdown();
	}

}
