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
import fr.upmc.datacenter.hardware.tests.ComputerMonitor;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenterclient.applicationprovider.ApplicationProvider;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationNotificationConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationSubmissionConnector;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;

/**
 * This runs on a single JVM :
 * - 1 computers with 2 processors of 2 cores,
 * - 1 admission controller that create a requestdispatcher and an applicationVM 
 * - 1 applications provider which send application to the admission controller and after getting the permission
 * creates a requestGenerator and connects it to the requestdispatcher of the admission controller
 */
public class TestCVM extends AbstractCVM {

    protected ComputerServicesOutboundPort              csop;
    protected ComputerStaticStateDataOutboundPort       cssdop;
    protected ComputerDynamicStateDataOutboundPort      cdsdop;
    protected ApplicationSubmissionOutboundPort         asop;
    protected ApplicationNotificationOutboundPort       anop;
    protected ApplicationProvider                       ap;
    protected ApplicationProviderManagementOutboundPort apmop;

    @Override
    public void deploy() throws Exception {
        // --------------------------------------------------------------------
        // Create and deploy a computer component with its 2 processors and
        // each with 2 cores.
        // --------------------------------------------------------------------
        String computerURI = "computer0";
        int numberOfProcessors = 2;
        int numberOfCores = 2;
        Set<Integer> admissibleFrequencies = new HashSet<Integer>();
        admissibleFrequencies.add( 1500 ); // Cores can run at 1,5 GHz
        admissibleFrequencies.add( 3000 ); // and at 3 GHz
        Map<Integer , Integer> processingPower = new HashMap<Integer , Integer>();
        processingPower.put( 1500 , 1500000 ); // 1,5 GHz executes 1,5 Mips
        processingPower.put( 3000 , 3000000 ); // 3 GHz executes 3 Mips
        Computer c = new Computer( computerURI , admissibleFrequencies , processingPower , 1500 , 1500 ,
                numberOfProcessors , numberOfCores , "csip" , "cssdip" , "cdsdip" );
        this.addDeployedComponent( c );

        // --------------------------------------------------------------------

        // --------------------------------------------------------------------
        // Create the computer monitor component and connect its to ports
        // with the computer component.
        // --------------------------------------------------------------------
        ComputerMonitor cm = new ComputerMonitor( computerURI , true , "cssdop" , "cdsdop" );
        this.addDeployedComponent( cm );
        this.cssdop = ( ComputerStaticStateDataOutboundPort ) cm.findPortFromURI( "cssdop" );
        this.cssdop.doConnection( "cssdip" , DataConnector.class.getCanonicalName() );

        this.cdsdop = ( ComputerDynamicStateDataOutboundPort ) cm.findPortFromURI( "cdsdop" );
        this.cdsdop.doConnection( "cdsdip" , ControlledDataConnector.class.getCanonicalName() );

        // --------------------------------------------------------------------
        // Create and deploy an AdmissionController component
        // --------------------------------------------------------------------

        String csop[] = new String[1];
        csop[0] = "csop";
        AdmissionController ac = new AdmissionController( "ac" , "asip" , "anip" , csop );
        this.addDeployedComponent( ac );
        this.csop = ( ComputerServicesOutboundPort ) ac.findPortFromURI( "csop" );
        this.csop.doConnection( "csip" , ComputerServicesConnector.class.getCanonicalName() );
        ac.toggleTracing();
        ac.toggleLogging();

        ap = new ApplicationProvider( "ap" , "asop" , "anop", "apmip" );
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
        csop.doDisconnection();
        cssdop.doDisconnection();
        cdsdop.doDisconnection();
        asop.doDisconnection();
        anop.doDisconnection();
        super.shutdown();
    }

    public static void main( String[] args ) {
      final  TestCVM test = new TestCVM();
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
            Thread.sleep( 10000L );
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
