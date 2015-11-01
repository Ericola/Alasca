package fr.upmc.datacenterclient.applicationprovider.test.test1appprovidermultijvm;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractDistributedCVM;
import fr.upmc.components.examples.basic_cs.DistributedCVM;
import fr.upmc.components.ports.PortI;
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
import fr.upmc.datacenterclient.applicationprovider.connectors.ApplicationSubmissionConnector;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.test.TestCVM;

public class TestAppProviderMultiJVM extends AbstractDistributedCVM{

	// URI of the CVM instances as defined in the config.xml file

	// Provider JVM contains 1 Computer, 1 Computer Monitor and 1 ApplicationController
	protected static String		PROVIDER_JVM_URI = "provider" ;
	// Consumer JVM contains 1 Application Provider
	protected static String		CONSUMER_JVM_URI = "consumer" ;

	protected ComputerServicesOutboundPort         csop;
	protected ComputerStaticStateDataOutboundPort  cssdop;
	protected ComputerDynamicStateDataOutboundPort cdsdop;
	protected ApplicationSubmissionOutboundPort    asop;
	protected ApplicationNotificationOutboundPort  anop;

	protected ApplicationProvider ap;
	protected Computer c;
	protected ComputerMonitor cm;
	protected AdmissionController ac;


	public TestAppProviderMultiJVM(String[] args) throws Exception {
		super(args);
	}

	@Override
	public void initialise() throws Exception {
		super.initialise() ;

		AbstractComponent.configureLogging("." + File.separator + "bcm-tmp",
				"log", 4000, '|') ;
	}

	@Override
	public void			instantiateAndPublish() throws Exception
	{
		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {
			// On instancie l'ApplicationProvider
			ap = new ApplicationProvider( "ap" , "asop" , "anop" );
			this.addDeployedComponent( ap );

			ap.toggleTracing();
			ap.toggleLogging();

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
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
			c = new Computer( computerURI , admissibleFrequencies , processingPower , 1500 , 1500 ,
					numberOfProcessors , numberOfCores , "csip" , "cssdip" , "cdsdip" );
			this.addDeployedComponent( c );

			//Computer Monitor
			cm = new ComputerMonitor( computerURI , true , "cssdop" , "cdsdop" );
			this.addDeployedComponent( cm );

			//AdmissionController
			ac = new AdmissionController( "ac" , "asip" , "anip" , "csop" );
			this.addDeployedComponent( ac );

			ac.toggleTracing();
			ac.toggleLogging();
		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.instantiateAndPublish();	
	}

	@Override
	public void			interconnect() throws Exception
	{

		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {
			//Connexion Computer - Computer Monitor
			this.cssdop = ( ComputerStaticStateDataOutboundPort ) cm.findPortFromURI( "cssdop" );
			cssdop.doConnection( "cssdip" , DataConnector.class.getCanonicalName() );

			this.cdsdop = ( ComputerDynamicStateDataOutboundPort ) cm.findPortFromURI( "cdsdop" );
			cdsdop.doConnection( "cdsdip" , ControlledDataConnector.class.getCanonicalName() );

			//Connexion Computer - AdmissionController
			this.csop = ( ComputerServicesOutboundPort ) ac.findPortFromURI( "csop" );
	        this.csop.doConnection( "csip" , ComputerServicesConnector.class.getCanonicalName() );

		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
			// Connexion ApplicationProvider - AdmissionController

			this.asop = ( ApplicationSubmissionOutboundPort ) ap.findPortFromURI( "asop" );
			asop.doConnection( "asip" , ApplicationSubmissionConnector.class.getCanonicalName() );

			this.anop = ( ApplicationNotificationOutboundPort ) ap.findPortFromURI( "anop" );
			anop.doConnection( "anip" , ApplicationNotificationConnector.class.getCanonicalName() );



		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.interconnect();
	}
	
	@Override
	public void			shutdown() throws Exception
	{
		if (thisJVMURI.equals(PROVIDER_JVM_URI)) {
			csop.doDisconnection();
	        cssdop.doDisconnection();
	        cdsdop.doDisconnection();
			
		} else if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
			asop.doDisconnection();
	        anop.doDisconnection();
			
		} else {

			System.out.println("Unknown JVM URI... " + thisJVMURI) ;

		}

		super.shutdown();
	}
	
	 public void test() throws Exception {
	        ap.sendApplication();
	    }
	
	public static void	main(String[] args)
	{
		System.out.println("Beginning") ;
		try {
			TestAppProviderMultiJVM test = new TestAppProviderMultiJVM(args) ;
			test.deploy() ;
			test.start() ;
			Thread.sleep(5000L) ; // Attente de 5 secondes le temps pour le consumer de se connecter
			
			if (thisJVMURI.equals(CONSUMER_JVM_URI)) {
				test.test();
			}
			
			test.shutdown() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Main thread ending") ;
		System.exit(0);
	}


}
