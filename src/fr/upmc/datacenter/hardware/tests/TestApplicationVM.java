package fr.upmc.datacenter.hardware.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.connectors.ControlledDataConnector;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * The class <code>TestApplicationVM</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 4 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			TestApplicationVM
extends		AbstractCVM
{
	public static class	Request
	implements	RequestI
	{
		private static final long serialVersionUID = 1L ;
		protected final long	numberOfInstructions ;
		protected final String	requestURI ;

		public			Request(long numberOfInstructions)
		{
			super() ;
			this.numberOfInstructions = numberOfInstructions ;
			this.requestURI = java.util.UUID.randomUUID().toString() ;
		}

		public			Request(
			String uri,
			long numberOfInstructions
			)
		{
			super() ;
			this.numberOfInstructions = numberOfInstructions ;
			this.requestURI = uri ;
		}

		@Override
		public long		getPredictedNumberOfInstructions()
		{
			return this.numberOfInstructions ;
		}

		@Override
		public String	getRequestURI()
		{
			return this.requestURI ;
		}
	}

	public static class	RequestionNotificationConsumer
	extends		AbstractComponent
	implements	RequestNotificationHandlerI
	{
		public static boolean	ACTIVE = true ;

		public			RequestionNotificationConsumer()
		{
			super(true);
		}

		@Override
		public void		acceptRequestTerminationNotification(RequestI r)
		throws Exception
		{
			if (RequestionNotificationConsumer.ACTIVE) {
				this.logMessage(" Request " +
							   				r.getRequestURI() + " has ended.") ;
			}
		}
	}

	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;
	public static final String	ApplicationVMManagementInboundPortURI = "avm-ibp" ;
	public static final String	ApplicationVMManagementOutboundPortURI = "avm-obp" ;
	public static final String	RequestSubmissionInboundPortURI = "rsibp" ;
	public static final String	RequestSubmissionOutboundPortURI = "rsobp" ;
	public static final String	RequestNotificationInboundPortURI = "rnibp" ;
	public static final String	RequestNotificationOutboundPortURI = "rnobp" ;

	protected ComputerServicesOutboundPort			csPort ;
	protected ComputerStaticStateDataOutboundPort	cssPort ;
	protected ComputerDynamicStateDataOutboundPort	cdsPort ;

	@Override
	public void			deploy() throws Exception
	{
		String computerURI = "computer0" ;
		int numberOfProcessors = 2 ;
		int numberOfCores = 2 ;
		Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
		admissibleFrequencies.add(1500) ;
		admissibleFrequencies.add(3000) ;
		Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
		processingPower.put(1500, 1500000) ;
		processingPower.put(3000, 3000000) ;
		Computer c = new Computer(
							computerURI,
							admissibleFrequencies,
							processingPower,  
							1500,		// Test scenario 1
							// 3000,	// Test scenario 2
							1500,
							numberOfProcessors,
							numberOfCores,
							ComputerServicesInboundPortURI,
							ComputerStaticStateDataInboundPortURI,
							ComputerDynamicStateDataInboundPortURI) ;
		c.toggleTracing() ;
		c.toggleLogging() ;
		this.addDeployedComponent(c) ;

		this.csPort = new ComputerServicesOutboundPort(
										ComputerServicesOutboundPortURI,
										new AbstractComponent(){}) ;
		this.csPort.publishPort() ;
		this.csPort.doConnection(
						ComputerServicesInboundPortURI,
						ComputerServicesConnector.class.getCanonicalName()) ;

		ComputerMonitor cm = new ComputerMonitor(
									computerURI,
									true,
									ComputerStaticStateDataOutboundPortURI,
									ComputerDynamicStateDataOutboundPortURI) ;
		cm.toggleLogging() ;
		cm.toggleTracing() ;
		this.addDeployedComponent(cm) ;
		this.cssPort =
			(ComputerStaticStateDataOutboundPort)
				cm.findPortFromURI(ComputerStaticStateDataOutboundPortURI) ;
		this.cssPort.doConnection(ComputerStaticStateDataInboundPortURI,
								  DataConnector.class.getCanonicalName()) ;

		this.cdsPort =
			(ComputerDynamicStateDataOutboundPort)
				cm.findPortFromURI(ComputerDynamicStateDataOutboundPortURI) ;
		this.cdsPort.doConnection(
							ComputerDynamicStateDataInboundPortURI,
							ControlledDataConnector.class.getCanonicalName());

		super.deploy();
	}

	@Override
	public void			start() throws Exception
	{
		super.start() ;
	}

	@Override
	public void			shutdown() throws Exception
	{
		this.csPort.doDisconnection() ;
		this.cssPort.doDisconnection() ;
		this.cdsPort.doDisconnection() ;

		super.shutdown();
	}

	public void			testScenario() throws Exception
	{
		AllocatedCore[] ac = this.csPort.allocateCores(4) ;

		ApplicationVM vm =
			new ApplicationVM("vm0",
							  ApplicationVMManagementInboundPortURI,
							  RequestSubmissionInboundPortURI,
							  RequestNotificationOutboundPortURI) ;
		this.addDeployedComponent(vm) ;
		vm.toggleTracing() ;
		vm.toggleLogging() ;
		vm.start() ;

		ApplicationVMManagementOutboundPort avmPort =
				new ApplicationVMManagementOutboundPort(
						ApplicationVMManagementOutboundPortURI,
						new AbstractComponent() {}) ;
		avmPort.publishPort() ;
		avmPort.doConnection(
					ApplicationVMManagementInboundPortURI,
					ApplicationVMManagementConnector.class.getCanonicalName()) ;
		avmPort.allocateCores(ac) ;

		RequestSubmissionOutboundPort rsobp =
					new RequestSubmissionOutboundPort(
									RequestSubmissionOutboundPortURI,
									new AbstractComponent() {}) ;
		rsobp.publishPort() ;
		rsobp.doConnection(
				RequestSubmissionInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName()) ;

		RequestionNotificationConsumer rnc =
										new RequestionNotificationConsumer() ;
		rnc.toggleLogging() ;
		rnc.toggleTracing() ;
		this.addDeployedComponent(rnc) ;
		rnc.start() ;
		RequestNotificationInboundPort nibp =
					new RequestNotificationInboundPort(
									RequestNotificationInboundPortURI,
									rnc) ;
		nibp.publishPort() ;

		RequestNotificationOutboundPort nobp =
				(RequestNotificationOutboundPort) vm.findPortFromURI(
											RequestNotificationOutboundPortURI) ;
		nobp.doConnection(
				RequestNotificationInboundPortURI,
				RequestNotificationConnector.class.getCanonicalName()) ;

		for(int i = 0 ; i < 10 ; i++) {
			rsobp.submitRequestAndNotify(new Request("r" + i, 6000000000L)) ;
			Thread.sleep(500L) ;
		}
		Thread.sleep(40000L) ;
		rsobp.doDisconnection() ;
		rsobp.unpublishPort() ;
	}

	public static void	main(String[] args)
	{
		//AbstractCVM.toggleDebugMode() ;
		final TestApplicationVM tappvm = new TestApplicationVM() ;
		try {
			tappvm.deploy() ;
			System.out.println("starting...") ;
			tappvm.start() ;
			new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									tappvm.testScenario() ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start() ;
			Thread.sleep(60000L) ;
			System.out.println("shutting down...") ;
			tappvm.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
