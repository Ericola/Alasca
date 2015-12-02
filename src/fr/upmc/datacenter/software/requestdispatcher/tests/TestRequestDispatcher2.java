//package fr.upmc.datacenter.software.requestdispatcher.tests;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import fr.upmc.components.AbstractComponent;
//import fr.upmc.components.connectors.DataConnector;
//import fr.upmc.components.cvm.AbstractCVM;
//import fr.upmc.datacenter.connectors.ControlledDataConnector;
//import fr.upmc.datacenter.hardware.computers.Computer;
//import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
//import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
//import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
//import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
//import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
//import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
//import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
//import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
//import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
//import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
//import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
//import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
//import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
//import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;
//import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
//import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
//import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
//
//public class TestRequestDispatcher2 extends AbstractCVM {
//
//    /**
//     * The class <code>TestRequestDispatcher</code> deploys a test application for request
//     * generation in a single JVM (no remote execution provided) for a data center simulation.
//     *
//     * <p>
//     * <strong>Description</strong>
//     * </p>
//     * 
//     * A data center has a set of computers, each with several multi-core processors. Application
//     * virtual machines (AVM) are created to run requests of an application. Each AVM is allocated
//     * cores of different processors of a computer. AVM then receive requests for their application.
//     * See the data center simulator documentation for more details about the implementation of this
//     * simulation.
//     * 
//     * This test creates one computer component with two processors, each having two cores. It then
//     * creates an AVM and allocates it all four cores of the two processors of this unique computer.
//     * A request generator component is then created and linked to the application virtual machine.
//     * The test scenario starts the request generation, wait for a specified time and then stops the
//     * generation. The overall test allots sufficient time to the execution of the application so
//     * that it completes the execution of all the generated requests.
//     * 
//     * The waiting time in the scenario and in the main method must be manually set by the tester.
//     * 
//     * <p>
//     * <strong>Invariant</strong>
//     * </p>
//     * 
//     * <pre>
//     * invariant	true
//     * </pre>
//     * 
//     * <p>
//     * Created on : 5 mai 2015
//     * </p>
//     * 
//     * @author <a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
//     * @version $Name$ -- $Revision$ -- $Date$
//     */
//    // ------------------------------------------------------------------------
//    // Constants and instance variables
//    // ------------------------------------------------------------------------
//    public static final int NBVM = 4;
//
//    // Predefined URI of the different ports visible at the component assembly
//    // level.
//    public String ComputerServicesInboundPortURI[];          // "cs-ibp" ;
//    public String ComputerServicesOutboundPortURI[];         // "cs-obp" ;
//    public String ComputerStaticStateDataInboundPortURI[];   // "css-dip" ;
//    public String ComputerStaticStateDataOutboundPortURI[];  // "css-dop" ;
//    public String ComputerDynamicStateDataInboundPortURI[];  // "cds-dip" ;
//    public String ComputerDynamicStateDataOutboundPortURI[]; // "cds-dop" ;
//
//    public String ApplicationVMManagementInboundPortURI[]; // {"avm-ibp", "avm-ibp2"} ;
//    public String ApplicationVMManagementOutboundPortURI[];// {"avm-obp", "avm-obp2"} ;
//
//    public String              VMRequestSubmissionInboundPortURI[];                // {"rsibp","rsibp2"}
//                                                                                   // ;
//    public static final String RGRequestSubmissionOutboundPortURI        = "rsobp";
//    public static final String RGRequestNotificationInboundPortURI       = "rnibp";
//    public String              VMRequestNotificationOutboundPortURI[];
//    public static final String RequestGeneratorManagementInboundPortURI  = "rgmip";
//    public static final String RequestGeneratorManagementOutboundPortURI = "rgmop";
//
//    public static final String RequestDispatcherRequestSubmissionInboundPortURI    = "rdrsibp";
//    public String              RequestDispatcherRequestSubmissionOutboundPortURI[];            // =
//                                                                                               // {"rdrsobp",
//                                                                                               // "rdrsobp2"};
//    public static final String RequestDispatcherRequestNotificationInboundPortURI  = "rdrnibp";
//    public static final String RequestDispatcherRequestNotificationOutboundPortURI = "rdrnobp";
//
//    /** Port connected to the computer component to access its services. */
//    protected ComputerServicesOutboundPort           csPort[];
//    /**
//     * Port connected to the computer component to receive the static state data.
//     */
//    protected ComputerStaticStateDataOutboundPort    cssPort[];
//    /**
//     * Port connected to the computer component to receive the dynamic state data.
//     */
//    protected ComputerDynamicStateDataOutboundPort   cdsPort[];
//    /** Port connected to the AVM component to allocate it cores. */
//    protected ApplicationVMManagementOutboundPort    avmPort[];
//    /**
//     * Port of the request generator component sending requests to the AVM component.
//     */
//    protected RequestSubmissionOutboundPort          rsobp;
//    /**
//     * Port of the request generator component used to receive end of execution notifications from
//     * the AVM component.
//     */
//    protected RequestNotificationOutboundPort        nobp;
//    /**
//     * Port connected to the request generator component to manage its execution (starting and
//     * stopping the request generation).
//     */
//    protected RequestGeneratorManagementOutboundPort rgmop;
//
//    protected RequestSubmissionOutboundPort rdrsobp[];
//
//    protected RequestNotificationOutboundPort rdnobp;
//    // ------------------------------------------------------------------------
//    // Component virtual machine methods
//    // ------------------------------------------------------------------------
//
//    public TestRequestDispatcher2() {
//        ComputerServicesInboundPortURI = new String[NBVM];
//        ComputerServicesOutboundPortURI = new String[NBVM];
//        ComputerStaticStateDataInboundPortURI = new String[NBVM];
//        ComputerStaticStateDataOutboundPortURI = new String[NBVM];
//        ComputerDynamicStateDataInboundPortURI = new String[NBVM];
//        ComputerDynamicStateDataOutboundPortURI = new String[NBVM];
//        ApplicationVMManagementInboundPortURI = new String[NBVM];
//        ApplicationVMManagementOutboundPortURI = new String[NBVM];
//        VMRequestSubmissionInboundPortURI = new String[NBVM];
//        RequestDispatcherRequestSubmissionOutboundPortURI = new String[NBVM];
//        VMRequestNotificationOutboundPortURI = new String[NBVM];
//
//        csPort = new ComputerServicesOutboundPort[NBVM];
//        cssPort = new ComputerStaticStateDataOutboundPort[NBVM];
//        cdsPort = new ComputerDynamicStateDataOutboundPort[NBVM];
//        avmPort = new ApplicationVMManagementOutboundPort[NBVM];
//        rdrsobp = new RequestSubmissionOutboundPort[NBVM];
//
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            ComputerServicesInboundPortURI[i] = "cs-ibp" + i;
//            ComputerServicesOutboundPortURI[i] = "cs-obp" + i;
//            ComputerStaticStateDataInboundPortURI[i] = "css-dip" + i;
//            ComputerStaticStateDataOutboundPortURI[i] = "css-dop" + i;
//            ComputerDynamicStateDataInboundPortURI[i] = "cds-dip" + i;
//            ComputerDynamicStateDataOutboundPortURI[i] = "cds-dop" + i;
//            ApplicationVMManagementInboundPortURI[i] = "avm-ibp" + i;
//            ApplicationVMManagementOutboundPortURI[i] = "avm-obp" + i;
//            VMRequestSubmissionInboundPortURI[i] = "rsibp" + i;
//            RequestDispatcherRequestSubmissionOutboundPortURI[i] = "rdrsobp" + i;
//            VMRequestNotificationOutboundPortURI[i] = "rnobp" + i;
//        }
//    }
//
//    @Override
//    public void deploy() throws Exception {
//        // --------------------------------------------------------------------
//        // Create and deploy a computer component with its 2 processors and
//        // each with 2 cores.
//        // --------------------------------------------------------------------
//        int numberOfProcessors = 2;
//        int numberOfCores = 2;
//        Set<Integer> admissibleFrequencies = new HashSet<Integer>();
//        admissibleFrequencies.add( 1500 ); // Cores can run at 1,5 GHz
//        admissibleFrequencies.add( 3000 ); // and at 3 GHz
//        Map<Integer , Integer> processingPower = new HashMap<Integer , Integer>();
//        processingPower.put( 1500 , 1500000 ); // 1,5 GHz executes 1,5 Mips
//        processingPower.put( 3000 , 3000000 ); // 3 GHz executes 3 Mips
//
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            String computerURI = "computer" + i;
//            Computer c = new Computer( computerURI , admissibleFrequencies , processingPower , 1500 , 1500 ,
//                    numberOfProcessors , numberOfCores , ComputerServicesInboundPortURI[i] ,
//                    ComputerStaticStateDataInboundPortURI[i] , ComputerDynamicStateDataInboundPortURI[i] );
//            this.addDeployedComponent( c );
//
//            // Create a mock-up computer services port to later allocate its cores
//            // to the application virtual machine.
//            this.csPort[i] = new ComputerServicesOutboundPort( ComputerServicesOutboundPortURI[i] ,
//                    new AbstractComponent() {} );
//            this.csPort[i].publishPort();
//            this.csPort[i].doConnection( ComputerServicesInboundPortURI[i] ,
//                    ComputerServicesConnector.class.getCanonicalName() );
//                    // --------------------------------------------------------------------
//
//            // --------------------------------------------------------------------
//            // Create the computer monitor component and connect its to ports
//            // with the computer component.
//            // --------------------------------------------------------------------
//
//            ComputerMonitor cm = new ComputerMonitor( computerURI , true , ComputerStaticStateDataOutboundPortURI[i] ,
//                    ComputerDynamicStateDataOutboundPortURI[i] );
//            this.addDeployedComponent( cm );
//            this.cssPort[i] = ( ComputerStaticStateDataOutboundPort ) cm
//                    .findPortFromURI( ComputerStaticStateDataOutboundPortURI[i] );
//            this.cssPort[i].doConnection( ComputerStaticStateDataInboundPortURI[i] ,
//                    DataConnector.class.getCanonicalName() );
//
//            this.cdsPort[i] = ( ComputerDynamicStateDataOutboundPort ) cm
//                    .findPortFromURI( ComputerDynamicStateDataOutboundPortURI[i] );
//            this.cdsPort[i].doConnection( ComputerDynamicStateDataInboundPortURI[i] ,
//                    ControlledDataConnector.class.getCanonicalName() );
//            // --------------------------------------------------------------------
//        }
//        List<String> rdsopList = new ArrayList<>();
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            rdsopList.add( RequestDispatcherRequestSubmissionOutboundPortURI[i] );
//        }
//        RequestDispatcher rd = new RequestDispatcher( "rd" , RequestDispatcherRequestSubmissionInboundPortURI ,
//                rdsopList , RequestDispatcherRequestNotificationOutboundPortURI ,
//                RequestDispatcherRequestNotificationInboundPortURI );
//
//        rd.toggleTracing();
//        rd.toggleLogging();
//
//        // --------------------------------------------------------------------
//        // Creating the request generator component.
//        // --------------------------------------------------------------------
//        RequestGenerator rg = new RequestGenerator( "rg" , // generator component URI
//                500.0 , // mean time between two requests
//                6000000000L , // mean number of instructions in requests
//                RequestGeneratorManagementInboundPortURI , RGRequestSubmissionOutboundPortURI ,
//                RGRequestNotificationInboundPortURI );
//        this.addDeployedComponent( rg );
//        // Connecting the request generator to the application virtual machine.
//        // Request generators have three different interfaces:
//        // - one for submitting requests to application virtual machines,
//        // - one for receiving end of execution notifications from application
//        // virtual machines, and
//        // - one for request generation management i.e., starting and stopping
//        // the generation process.
//
//        // Toggle on tracing and logging in the request generator to
//        // follow the submission and end of execution notification of
//        // individual requests.
//        rg.toggleTracing();
//        rg.toggleLogging();
//
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            // --------------------------------------------------------------------
//            // Create an Application VM component
//            // --------------------------------------------------------------------
//            ApplicationVM vm = new ApplicationVM( "vm" + i , // application vm component URI
//                    ApplicationVMManagementInboundPortURI[i] , VMRequestSubmissionInboundPortURI[i] ,
//                    VMRequestNotificationOutboundPortURI[i] );
//            this.addDeployedComponent( vm );
//
//            // Create a mock up port to manage the AVM component (allocate cores).
//            this.avmPort[i] = new ApplicationVMManagementOutboundPort( ApplicationVMManagementOutboundPortURI[i] ,
//                    new AbstractComponent() {} );
//            this.avmPort[i].publishPort();
//            this.avmPort[i].doConnection( ApplicationVMManagementInboundPortURI[i] ,
//                    ApplicationVMManagementConnector.class.getCanonicalName() );
//
//            // Toggle on tracing and logging in the application virtual machine to
//            // follow the execution of individual requests.
//            vm.toggleTracing();
//            vm.toggleLogging();
//            // --------------------------------------------------------------------
//
//
//            // --------------------------------------------------------------------
//            // Creating the request dispatcher component.
//            // --------------------------------------------------------------------
//
//            this.rsobp = ( RequestSubmissionOutboundPort ) rg.findPortFromURI( RGRequestSubmissionOutboundPortURI );
//            rsobp.doConnection( RequestDispatcherRequestSubmissionInboundPortURI ,
//                    RequestSubmissionConnector.class.getCanonicalName() );
//
//            this.nobp = ( RequestNotificationOutboundPort ) vm
//                    .findPortFromURI( VMRequestNotificationOutboundPortURI[i] );
//            nobp.doConnection( RequestDispatcherRequestNotificationInboundPortURI ,
//                    RequestNotificationConnector.class.getCanonicalName() );
//
//            this.rdnobp = ( RequestNotificationOutboundPort ) rd
//                    .findPortFromURI( RequestDispatcherRequestNotificationOutboundPortURI );
//            rdnobp.doConnection( RGRequestNotificationInboundPortURI ,
//                    RequestNotificationConnector.class.getCanonicalName() );
//
//            this.rdrsobp[i] = ( RequestSubmissionOutboundPort ) rd
//                    .findPortFromURI( RequestDispatcherRequestSubmissionOutboundPortURI[i] );
//            rdrsobp[i].doConnection( VMRequestSubmissionInboundPortURI[i] ,
//                    RequestSubmissionConnector.class.getCanonicalName() );
//
//            // Create a mock up port to manage to request generator component
//            // (starting and stopping the generation).
//            this.rgmop = new RequestGeneratorManagementOutboundPort( RequestGeneratorManagementOutboundPortURI ,
//                    new AbstractComponent() {} );
//            this.rgmop.publishPort();
//            this.rgmop.doConnection( RequestGeneratorManagementInboundPortURI ,
//                    RequestGeneratorManagementConnector.class.getCanonicalName() );
//            // --------------------------------------------------------------------
//            this.addDeployedComponent( rd );
//            // complete the deployment at the component virtual machine level.
//        }
//        super.deploy();
//
//    }
//
//    /**
//     * @see fr.upmc.components.cvm.AbstractCVM#start()
//     */
//    @Override
//    public void start() throws Exception {
//        super.start();
//
//        // Allocate the 4 cores of the computer to the application virtual
//        // machine.
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            AllocatedCore[] ac = this.csPort[i].allocateCores( 4 );
//            this.avmPort[i].allocateCores( ac );
//        }
//    }
//
//    /**
//     * @see fr.upmc.components.cvm.AbstractCVM#shutdown()
//     */
//    @Override
//    public void shutdown() throws Exception {
//        // disconnect all ports explicitly connected in the deploy phase.$
//        this.rsobp.doDisconnection();
//        this.nobp.doDisconnection();
//        this.rgmop.doDisconnection();
//        this.rdnobp.doDisconnection();
//        for ( int i = 0 ; i < NBVM ; i++ ) {
//            this.csPort[i].doDisconnection();
//            this.avmPort[i].doDisconnection();
//            this.rdrsobp[i].doDisconnection();
//        }
//        super.shutdown();
//    }
//
//    /**
//     * generate requests for 20 seconds and then stop generating.
//     *
//     * @throws Exception
//     */
//    public void testScenario() throws Exception {
//        // start the request generation in the request generator.
//        this.rgmop.startGeneration();
//        // wait 20 seconds
//        Thread.sleep( 20000L );
//        // then stop the generation.
//        this.rgmop.stopGeneration();
//    }
//
//    /**
//     * execute the test application.
//     * 
//     * @param args command line arguments, disregarded here.
//     */
//    public static void main( String[] args ) {
//        // Uncomment next line to execute components in debug mode.
//        // AbstractCVM.toggleDebugMode() ;
//        final TestRequestDispatcher2 trd = new TestRequestDispatcher2();
//        try {
//            // Deploy the components
//            trd.deploy();
//            System.out.println( "starting..." );
//            // Start them.
//            trd.start();
//            // Execute the chosen request generation test scenario in a
//            // separate thread.
//            new Thread( new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//                        trd.testScenario();
//                    }
//                    catch ( Exception e ) {
//                        e.printStackTrace();
//                    }
//                }
//            } ).start();
//            // Sleep to let the test scenario execute to completion.
//            Thread.sleep( 90000L );
//            // Shut down the application.
//            System.out.println( "shutting down..." );
//            trd.shutdown();
//            System.out.println( "ending..." );
//            // Exit from Java.
//            System.exit( 0 );
//        }
//        catch ( Exception e ) {
//            e.printStackTrace();
//        }
//    }
//}
