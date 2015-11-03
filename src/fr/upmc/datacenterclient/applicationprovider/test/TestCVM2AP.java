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
import fr.upmc.datacenter.software.controller.AdmissionController;
import fr.upmc.datacenterclient.applicationprovider.ApplicationProvider;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationNotificationConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationProviderManagementConnector;
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationSubmissionConnector;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationProviderManagementOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;

/**
 * This runs on a single JVM :
 * - 2 computers with 2 processors of 2 cores,
 * - 1 admission controller that create a requestdispatcher and an applicationVM each time it receives an application and allocate 4 cores to that applicationVM
 * - 3 applications provider try to send application to the admission controller but only 2 are accepted the 3rd is refused cuz there is not enough resources 
 * applications provider that are accepted create a RequestGenerator that will send request to the corresponding requestDispatcher
 */
public class TestCVM2AP extends AbstractCVM {

    private static final int NB_COMPUTER             = 2;
    private static final int NB_APPLICATION_PROVIDER = 3;

    protected ComputerServicesOutboundPort              csop[];
    protected ComputerStaticStateDataOutboundPort       cssdop[];
    protected ComputerDynamicStateDataOutboundPort      cdsdop[];
    protected ApplicationSubmissionOutboundPort         asop[];
    protected ApplicationNotificationOutboundPort       anop[];
    protected ApplicationProvider                       ap[];
    protected ApplicationProviderManagementOutboundPort apmop[];

    @Override
    public void deploy() throws Exception {
        // --------------------------------------------------------------------
        // Create and deploy a computer component with its 2 processors and
        // each with 2 cores.
        // --------------------------------------------------------------------
        int numberOfProcessors = 2;
        int numberOfCores = 2;
        Set<Integer> admissibleFrequencies = new HashSet<Integer>();
        admissibleFrequencies.add( 1500 ); // Cores can run at 1,5 GHz
        admissibleFrequencies.add( 3000 ); // and at 3 GHz
        Map<Integer , Integer> processingPower = new HashMap<Integer , Integer>();
        processingPower.put( 1500 , 1500000 ); // 1,5 GHz executes 1,5 Mips
        processingPower.put( 3000 , 3000000 ); // 3 GHz executes 3 Mips

        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            Computer c = new Computer( "computer" + i , admissibleFrequencies , processingPower , 1500 , 1500 ,
                    numberOfProcessors , numberOfCores , "csip" + i , "cssdip" + i , "cdsdip" + i );
            this.addDeployedComponent( c );
        }

        // --------------------------------------------------------------------

        // --------------------------------------------------------------------
        // Create the computer monitor component and connect its to ports
        // with the computer component.
        // --------------------------------------------------------------------

        this.cssdop = new ComputerStaticStateDataOutboundPort[NB_COMPUTER];
        this.cdsdop = new ComputerDynamicStateDataOutboundPort[NB_COMPUTER];
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            ComputerMonitor cm = new ComputerMonitor( "computer" + i , true , "cssdop" + i , "cdsdop" + i );
            this.addDeployedComponent( cm );
            this.cssdop[i] = ( ComputerStaticStateDataOutboundPort ) cm.findPortFromURI( "cssdop" + i );
            this.cssdop[i].doConnection( "cssdip" + i , DataConnector.class.getCanonicalName() );

            this.cdsdop[i] = ( ComputerDynamicStateDataOutboundPort ) cm.findPortFromURI( "cdsdop" + i );
            this.cdsdop[i].doConnection( "cdsdip" + i , ControlledDataConnector.class.getCanonicalName() );
        }

        // --------------------------------------------------------------------
        // Create and deploy an AdmissionController component
        // --------------------------------------------------------------------

        String csop[] = new String[NB_COMPUTER];
        for ( int i = 0 ; i < NB_COMPUTER ; ++i )
            csop[i] = "csop" + i;

        AdmissionController ac = new AdmissionController( "ac" , "asip" , "anip" , csop );

        this.csop = new ComputerServicesOutboundPort[NB_COMPUTER];
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            this.csop[i] = ( ComputerServicesOutboundPort ) ac.findPortFromURI( "csop" + i );
            this.csop[i].doConnection( "csip" + i , ComputerServicesConnector.class.getCanonicalName() );
        }
        ac.toggleTracing();
        ac.toggleLogging();
        this.addDeployedComponent( ac );

        // --------------------------------------------------------------------
        // Create and deploy NB_APPLICATION_PROVIDER ApplicationProvider component
        // --------------------------------------------------------------------
        this.ap = new ApplicationProvider[NB_APPLICATION_PROVIDER];
        this.asop = new ApplicationSubmissionOutboundPort[NB_APPLICATION_PROVIDER];
        this.anop = new ApplicationNotificationOutboundPort[NB_APPLICATION_PROVIDER];
        this.apmop = new ApplicationProviderManagementOutboundPort[NB_APPLICATION_PROVIDER];
        for ( int i = 0 ; i < NB_APPLICATION_PROVIDER ; i++ ) {
            ap[i] = new ApplicationProvider( "ap" + i , "asop" + i , "anop" + i , "apmip" + i );
            this.addDeployedComponent( ap[i] );
            ap[i].toggleTracing();
            ap[i].toggleLogging();

            // asop -- asip
            this.asop[i] = ( ApplicationSubmissionOutboundPort ) ap[i].findPortFromURI( "asop" + i );
            this.asop[i].doConnection( "asip" , ApplicationSubmissionConnector.class.getCanonicalName() );

            // anop -- anip
            this.anop[i] = ( ApplicationNotificationOutboundPort ) ap[i].findPortFromURI( "anop" + i );
            this.anop[i].doConnection( "anip" , ApplicationNotificationConnector.class.getCanonicalName() );

            this.apmop[i] = new ApplicationProviderManagementOutboundPort( "apmop" + i , new AbstractComponent() {} );
            this.apmop[i].publishPort();
            this.apmop[i].doConnection( "apmip" + i , ApplicationProviderManagementConnector.class.getCanonicalName() );
        }
        super.deploy();
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    public void test() throws Exception {
        for ( int i = 0 ; i < NB_APPLICATION_PROVIDER  ; i++ )
            apmop[i].sendApplication();
    }

    @Override
    public void shutdown() throws Exception {
        for ( int i = 0 ; i < NB_COMPUTER ; ++i ) {
            csop[i].doDisconnection();
            cssdop[i].doDisconnection();
            cdsdop[i].doDisconnection();
            asop[i].doDisconnection();
            anop[i].doDisconnection();
            apmop[i].doDisconnection();
        }
        super.shutdown();
    }

    public static void main( String[] args ) {
      final  TestCVM2AP test = new TestCVM2AP();
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
