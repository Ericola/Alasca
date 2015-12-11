package fr.upmc.datacenter.software.admissioncontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.ports.OutboundPortI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementInboundPort;
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
public class AdmissionController extends AbstractComponent implements AdmissionControllerManagementI {

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

    /** array associate the index of the computer with the number of available cores */
    private int[] nbAvailablesCores;

    /** Uri of the computer **/
    private String[] computerURI;

    /** Map between RequestDispatcher URIs and the outbound ports to call them. */
    protected Map<String , RequestDispatcherManagementOutboundPort> rdmopList;

    /**
     * Map between RequestDispatcher URIs and the inbound ports through which request termination
     * notifications are received from each applicationVM.
     */
    protected Map<String , RequestNotificationInboundPort> rdnipList;

    /** Inbound port used by the controlller to manage the AdmissionController */
    protected AdmissionControllerManagementInboundPort acmip;

    private int nbVMCreated = 0;

    private int currentCSOP = 0;

    /** indexes are computers and values are whether it's used or not */
    private boolean computerUsed[];

    private final Integer coresPerComputers;

    /** map associate processor URIs with their outboundport */
    private Map<String , ProcessorManagementOutboundPort> pmops;

    private List<AllocatedCore> allocatedCores;

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
            String applicationNotificationInboundPortURI , String AdmissionControllerManagementInboundPortURI ,
            String computerServiceOutboundPortURI[] , String ComputerDynamicStateDataOutboundPort[] ,
            String computerURI[] , int[] nbAvailableCoresPerComputer , Map<String , String> pmipURIs )
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

        this.cdsop = new ComputerDynamicStateDataOutboundPort[computerServiceOutboundPortURI.length];
        this.csop = new ComputerServicesOutboundPort[computerServiceOutboundPortURI.length];
        this.computerURI = computerURI;
        for ( int i = 0 ; i < computerServiceOutboundPortURI.length ; i++ ) {
            this.addRequiredInterface( ComputerServicesI.class );
            this.csop[i] = new ComputerServicesOutboundPort( computerServiceOutboundPortURI[i] , this );
            this.addPort( csop[i] );
            this.csop[i].publishPort();
            this.addRequiredInterface( ComputerDynamicStateI.class );
            this.cdsop[i] = new ComputerDynamicStateDataOutboundPort( ComputerDynamicStateDataOutboundPort[i] , this ,
                    computerURI[i] );
            this.addPort( cdsop[i] );
            this.cdsop[i].publishPort();
        }

        pmops = new HashMap<>();
        int i = 0;
        for ( Map.Entry<String , String> entry : pmipURIs.entrySet() ) {
            this.addRequiredInterface( ProcessorManagementI.class );
            ProcessorManagementOutboundPort pmop = new ProcessorManagementOutboundPort( acURI + "pmop" + i , this );
            pmops.put( entry.getKey() , pmop );
            this.addPort( pmop );
            pmop.publishPort();
            pmop.doConnection( entry.getValue() , ProcessorManagementConnector.class.getCanonicalName() );
        }

        // Allocation Hashmap
        nbAvailablesCores = nbAvailableCoresPerComputer;

        coresPerComputers = nbAvailableCoresPerComputer[0];

        rnopList = new HashMap<>();
        avmop = new ArrayList<>();
        rdmopList = new HashMap<>();
        rdnipList = new HashMap<>();
        allocatedCores = new ArrayList<>();

    }

    private boolean isUsed( int computerIndex ) {
        return nbAvailablesCores[computerIndex] < coresPerComputers;
    }

    private Integer getAvailableCores( int nbCores ) {
        int max = 0;
        Integer index = null;
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
            print( ac.length + " cores allocated." );

            // Création d'un requestdispatcher
            print( "Creating the requestDispatcher..." );
            List<String> rdsop = new ArrayList<>();
            rdsop.add( createURI( "rdsop" ) );
            RequestDispatcher rd = new RequestDispatcher( createURI( "rd" ) , createURI( "rdsip" ) ,
                    createURI( "rdmip" ) , rdsop , createURI( "rdnop" ) , createURI( "rdnip" ) ,
                    createURI( "rddsdip" ) );
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

            // Connect RD with VM
            RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rd.findPortFromURI( rdsop.get( 0 ) );
            rsop.doConnection( createURI( "rsip" ) , RequestSubmissionConnector.class.getCanonicalName() );
            RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
                    .findPortFromURI( createURI( "rnop" ) );
            rnop.doConnection( createURI( "rdnip" ) , RequestNotificationConnector.class.getCanonicalName() );

            // Create controller
            print( "Creating the controller..." );
            Controller controller = new Controller( createURI( "c" ) , createURI( "rd" ) , createURI( "acmop" ) ,
                    createURI( "rddsdip" ) );
            controller.toggleLogging();
            controller.toggleTracing();

            // Connect Controller with AdmissionController
            AdmissionControllerManagementOutboundPort acmop = ( AdmissionControllerManagementOutboundPort ) controller
                    .findPortFromURI( createURI( "acmop" ) );
            acmop.doConnection( acmip.getPortURI() , AdmissionControllerManagementConnector.class.getCanonicalName() );

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
    private String createVMURI( String uri ) {
        return acURI + uri + cpt + nbVMCreated;
    }

    private void print( String s ) {
        this.logMessage( "[AdmissionController] " + s );
    }

    @Override
    public void allocateVM( String RequestDispatcherURI ) throws Exception {
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

            // Allocation of VM
            ApplicationVM vm = new ApplicationVM( createVMURI( "vm" ) , createVMURI( "avmip" ) , createVMURI( "rsip" ) ,
                    createVMURI( "rnop" ) );
            AbstractCVM.theCVM.addDeployedComponent( vm );

            avmop.add( new ApplicationVMManagementOutboundPort( createVMURI( "avmop" ) , new AbstractComponent() {} ) );
            avmop.get( avmop.size() - 1 ).publishPort();
            avmop.get( avmop.size() - 1 ).doConnection( createVMURI( "avmip" ) ,
                    ApplicationVMManagementConnector.class.getCanonicalName() );

            // AllocatedCore to VM
            avmop.get( avmop.size() - 1 ).allocateCores( ac );
            print( ac.length + " cores allocated." );
            String RequestNotificationInboundport = this.rdmopList.get( RequestDispatcherURI )
                    .connectVm( createVMURI( "rsip" ) );
            // Connected RequestDispatcher -- VM
            RequestNotificationOutboundPort rnop = ( RequestNotificationOutboundPort ) vm
                    .findPortFromURI( createVMURI( "rnop" ) );
            rnop.doConnection( RequestNotificationInboundport , RequestNotificationConnector.class.getCanonicalName() );
            nbVMCreated++;
            vm.start();
            vm.toggleLogging();
            vm.toggleTracing();
            print( "VM Allocated!" );
        }
        else {
            print( "Can not allocate VM (no Core Available)" );
        }
    }

    @Override
    public void removeVM( String RequestDispatcherURI ) throws Exception {
        rdmopList.get( RequestDispatcherURI ).disconnectVm();
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

                if ( this.cdsop[i].connected() )
                    this.cdsop[i].connected();

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
    public boolean addCores( int nbCores ) throws Exception {
        // parcourir les computers utilisés
        print( "Looking for available resources..." );
        boolean ok = false;
        int nbAllocated = 0;

        while ( !isUsed( currentCSOP ) )
            currentCSOP = ( currentCSOP + 1 ) % csop.length;

        ok = ( ( nbAllocated = csop[currentCSOP].allocateCores( nbCores ).length ) > 0 );
        currentCSOP = ( currentCSOP + 1 ) % csop.length;
        print( nbAllocated + " cores allocated on the computer " + currentCSOP );

        return ok;
    }

    @Override
    public void increaseFrequency() throws  Exception {
        // parcourir les processeurs utilisés
        for ( AllocatedCore a : allocatedCores ) {
            pmops.get( a.processorURI ).setCoreFrequency( a.coreNo , 3000 );
        }
    }

}
