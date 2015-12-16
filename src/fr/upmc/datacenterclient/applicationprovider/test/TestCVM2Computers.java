package fr.upmc.datacenterclient.applicationprovider.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenterclient.applicationprovider.ApplicationProvider;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationNotificationConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationSubmissionConnector;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * This runs on a single JVM :
 * - NB_COMPUTER computers with 2 processors of 2 cores (can be modified by changing the variable NB_COMPUTER
 * in the class TestCVM2Computers)
 * - 1 admission controller that create a requestdispatcher and an applicationVM 
 * - 1 applications provider which send application to the admission controller and after getting the permission
 * creates a requestGenerator and connects it to the requestdispatcher of the admission controller
 */
public class TestCVM2Computers extends AbstractCVM {

    private static final int                            NB_COMPUTER    = 2;
    protected ComputerServicesOutboundPort              csop[];
    protected ComputerDynamicStateDataOutboundPort      cdsdop[];
    protected ApplicationSubmissionOutboundPort         asop;
    protected ApplicationNotificationOutboundPort       anop;
    protected ApplicationProvider                       ap;
    protected ApplicationProviderManagementOutboundPort apmop;

    @Override
    public void deploy() throws Exception {
        // --------------------------------------------------------------------
        // Create and deploy NB_COMPUTER computer component with its 2 processors and
        // each with 2 cores.
        // --------------------------------------------------------------------
        int numberOfProcessors = 2;
        int numberOfCores = 4;
        Set<Integer> admissibleFrequencies = new HashSet<Integer>();
        admissibleFrequencies.add( 1500 ); // Cores can run at 1,5 GHz
        admissibleFrequencies.add( 3000 ); // and at 3 GHz
        Map<Integer , Integer> processingPower = new HashMap<Integer , Integer>();
        processingPower.put( 1500 , 1500000 ); // 1,5 GHz executes 1,5 Mips
        processingPower.put( 3000 , 3000000 ); // 3 GHz executes 3 Mips

     // map associate processor uri with uri of inbound port
        Map<String , String> pmipURIs = new HashMap<>();   
    
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            Computer c = new Computer( "computer" + i , admissibleFrequencies , processingPower , 1500 , 1500 ,
                    numberOfProcessors , numberOfCores , "csip" + i , "cssdip" + i , "cdsdip" + i );
            this.addDeployedComponent( c );      
           
            Map<Integer , String> processorURIs = c.getStaticState().getProcessorURIs();
            for ( Map.Entry<Integer , String> entry : processorURIs.entrySet() ) {
                Map<ProcessorPortTypes , String> pPortsList = c.getStaticState().getProcessorPortMap()
                        .get( entry.getValue() );
                pmipURIs.put( entry.getValue() , pPortsList.get( Processor.ProcessorPortTypes.MANAGEMENT ) );    
            }

        }

        // --------------------------------------------------------------------

    
     
        
        
        // --------------------------------------------------------------------
        // Create and deploy an AdmissionController component
        // --------------------------------------------------------------------

        String csop[] = new String[NB_COMPUTER];
        String computer[] = new String[NB_COMPUTER];
        
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ){
            csop[i] = "csop" + i;
            computer[i] = "computer" + i;
            
        }
        final int[] nbAvailableCoresPerComputer = new int[NB_COMPUTER];
        for ( int i = 0 ; i < NB_COMPUTER ; ++i )     
            nbAvailableCoresPerComputer[i] = numberOfProcessors * numberOfCores; 
        
        Integer[] frequencies = {1500, 3000};
        AdmissionController ac = new AdmissionController( "ac" , "asip" , "rdvenip", "anip" , "acmip", csop, computer, nbAvailableCoresPerComputer, pmipURIs, frequencies );

        this.csop = new ComputerServicesOutboundPort[NB_COMPUTER];
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            this.csop[i] = ( ComputerServicesOutboundPort ) ac.findPortFromURI( "csop" + i );
            this.csop[i].doConnection( "csip" + i , ComputerServicesConnector.class.getCanonicalName() );
        }
        
        ac.toggleTracing();
        ac.toggleLogging();
        this.addDeployedComponent( ac );

        ap = new ApplicationProvider( "ap" , "asop" , "anop" , "apmip" );
        this.addDeployedComponent( ap );
        ap.toggleTracing();
        ap.toggleLogging();

        // asop -- asip
        this.asop = ( ApplicationSubmissionOutboundPort ) ap.findPortFromURI( "asop" );
        this.asop.doConnection( "asip" , ApplicationSubmissionConnector.class.getCanonicalName() );

        // anop -- anip
        this.anop = ( ApplicationNotificationOutboundPort ) ap.findPortFromURI( "anop" );
        this.anop.doConnection( "anip" , ApplicationNotificationConnector.class.getCanonicalName() );

        this.apmop = new ApplicationProviderManagementOutboundPort( "apmop" , new AbstractComponent() {} );
        this.apmop.publishPort();
        this.apmop.doConnection( "apmip" , ApplicationProviderManagementConnector.class.getCanonicalName() );
        super.deploy();
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    public void test() throws Exception {
        apmop.sendApplication();
    }

    @Override
    public void shutdown() throws Exception {
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            csop[i].doDisconnection();
            cdsdop[i].doDisconnection();
        }
        asop.doDisconnection();
        anop.doDisconnection();
        super.shutdown();
    }

    public static void main( String[] args ) {
       final TestCVM2Computers test = new TestCVM2Computers();
        try {
            test.deploy();
            test.start();
            new Thread( new Runnable() {

                @Override
                public void run() {
                    try {
                        test.test();
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            } ).start();
            Thread.sleep(  500000L );
            System.out.println( "shutting down..." );
            test.shutdown();
            System.out.println( "ending..." );
            System.exit( 0 );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
