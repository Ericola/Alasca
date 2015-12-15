	package fr.upmc.datacenterclient.applicationprovider.test.test1appprovidermultijvm;

import java.io.File;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.cvm.pre.dcc.DynamicComponentCreator;
import fr.upmc.components.examples.basic_cs.DistributedCVM;
import fr.upmc.components.ports.PortI;
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
import fr.upmc.datacenterclient.applicationprovider.test.TestCVM;

/**
 * A distributed CVM that runs 2 JVMs</br>
 * 1st JVM contains
 * <ul>
 * <li>a computer</li>
 * <li>a computer monitor</li>
 * <li>an admission controller</li>
 * </ul>
 * 2nd JVM contains
 * <ul>
 * <li>an application provider</li>
 * </ul>
 */
public class TestAppProviderMultiJVM extends AbstractDistributedCVM {


	// URI of the CVM instances as defined in the config.xml file

	// Provider JVM contains 1 Computer, 1 Computer Monitor and 1 ApplicationController
	protected static String PROVIDER_JVM_URI = "provider";
	// Consumer JVM contains 1 Application Provider
	protected static String CONSUMER_JVM_URI = "consumer";

	/** PORT **/
	protected ComputerServicesOutboundPort              csop;
	protected ComputerStaticStateDataOutboundPort       cssdop;
	protected ComputerDynamicStateDataOutboundPort      cdsdop;
	protected ApplicationSubmissionOutboundPort         asop;
	protected ApplicationNotificationOutboundPort       anop;
	protected ApplicationProviderManagementOutboundPort apmop;

	/** PORT URI **/
	protected static final String AP    = "ap";
	protected static final String ASOP  = "asop";
	protected static final String ANOP  = "anop";
	protected static final String APMIP = "apmip";

	/** Components **/
	protected ApplicationProvider ap;
	protected Computer            c;
	protected ComputerMonitor     cm;
	protected AdmissionController ac;

	public TestAppProviderMultiJVM( String[] args ) throws Exception {
		super( args );
	}

	@Override
	public void initialise() throws Exception {
		// AbstractCVM.toggleDebugMode();
		super.initialise();
		AbstractComponent.configureLogging( "." + File.separator + "bcm-tmp" , "log" , 4000 , '|' );

	}

	@Override
	public void instantiateAndPublish() throws Exception {
		if ( thisJVMURI.equals( CONSUMER_JVM_URI ) ) {
			// On instancie l'ApplicationProvider
			ap = new ApplicationProvider( AP , ASOP , ANOP , APMIP );
			this.addDeployedComponent( ap );

			ap.toggleTracing();
			ap.toggleLogging();

		}
		else if ( thisJVMURI.equals( PROVIDER_JVM_URI ) ) {
			// On instancie le computer, computer monitor + applicationController

			// Computer
			String computerURI = "computer0";
			int numberOfProcessors = 2;
			int numberOfCores = 2;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>();
			admissibleFrequencies.add( 1500 ); // Cores can run at 1,5 GHz
			admissibleFrequencies.add( 3000 ); // and at 3 GHz
			Map<Integer , Integer> processingPower = new HashMap<Integer , Integer>();
			processingPower.put( 1500 , 1500000 ); // 1,5 GHz executes 1,5 Mips
			processingPower.put( 3000 , 3000000 ); // 3 GHz executes 3 Mips
			c = new Computer( computerURI , admissibleFrequencies , processingPower , 1500 , 1500 , numberOfProcessors ,
					numberOfCores , "csip" , "cssdip" , "cdsdip" );
			this.addDeployedComponent( c );

			// --------------------------------------------------------------------

			Map<Integer , String> processorURIs = c.getStaticState().getProcessorURIs();
			Map<String , String> pmipURIs = new HashMap<>(); // map associate processor uri with uri of
			// inbound port
			for ( Map.Entry<Integer , String> entry : processorURIs.entrySet() ) {
				Map<ProcessorPortTypes , String> pPortsList = c.getStaticState().getProcessorPortMap()
						.get( entry.getValue() );
				pmipURIs.put( entry.getValue() , pPortsList.get( Processor.ProcessorPortTypes.MANAGEMENT ) );
			}

			// AdmissionController
			String csop[] = new String[1];
			csop[0] = "csop";
			String computer[] = new String[1];
			computer[0] = computerURI;

			final int[] nbAvailableCoresPerComputer = new int[1];  
			nbAvailableCoresPerComputer[0] = numberOfProcessors * numberOfCores; 

			ac = new AdmissionController( "ac" , "asip" , "rdvenip" , "anip" , "acmip" , csop, computer, nbAvailableCoresPerComputer, pmipURIs);
			this.addDeployedComponent( ac );

			ac.toggleTracing();
			ac.toggleLogging();
		}
		else {

			System.out.println( "Unknown JVM URI... " + thisJVMURI );

		}

		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {

		if ( thisJVMURI.equals( PROVIDER_JVM_URI ) ) {


			// Connexion Computer - AdmissionController
			this.csop = ( ComputerServicesOutboundPort ) ac.findPortFromURI( "csop" );
			this.csop.doConnection( "csip" , ComputerServicesConnector.class.getCanonicalName() );

		}
		else if ( thisJVMURI.equals( CONSUMER_JVM_URI ) ) {
			// Connexion ApplicationProvider - AdmissionController

			this.asop = ( ApplicationSubmissionOutboundPort ) ap.findPortFromURI( "asop" );
			asop.doConnection( "asip" , ApplicationSubmissionConnector.class.getCanonicalName() );

			this.anop = ( ApplicationNotificationOutboundPort ) ap.findPortFromURI( "anop" );
			anop.doConnection( "anip" , ApplicationNotificationConnector.class.getCanonicalName() );

			this.apmop = new ApplicationProviderManagementOutboundPort( "apmop" , new AbstractComponent() {} );
			this.apmop.publishPort();
			this.apmop.doConnection( "apmip" , ApplicationProviderManagementConnector.class.getCanonicalName() );

		}
		else {

			System.out.println( "Unknown JVM URI... " + thisJVMURI );

		}

		super.interconnect();
	}

	@Override
	public void shutdown() throws Exception {
		if ( thisJVMURI.equals( PROVIDER_JVM_URI ) ) {
			csop.doDisconnection();
			cssdop.doDisconnection();

		}
		else if ( thisJVMURI.equals( CONSUMER_JVM_URI ) ) {
			asop.doDisconnection();
			anop.doDisconnection();

		}
		else {

			System.out.println( "Unknown JVM URI... " + thisJVMURI );

		}

		super.shutdown();
	}

	public void test() throws Exception {
		apmop.sendApplication();

	}

	public static void main( String[] args ) {
		System.out.println( "Beginning" );
		try {

			TestAppProviderMultiJVM test = new TestAppProviderMultiJVM( args );

			test.deploy();
			test.start();

			// connecter
			if ( thisJVMURI.equals( CONSUMER_JVM_URI ) ) {
				Thread.sleep(100);
				test.test();
			}
			Thread.sleep( 25000L ); // Attente de 25 secondes le temps pour le consumer de se

			test.shutdown();
		}
		catch ( Exception e ) {
			System.out.println( e );
			e.printStackTrace();
		}
		System.out.println( "Main thread ending" );
		System.exit( 0 );
	}

}
