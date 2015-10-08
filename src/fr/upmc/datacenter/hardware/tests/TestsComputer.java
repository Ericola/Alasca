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
import fr.upmc.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.upmc.datacenter.hardware.processors.connectors.ProcessorServicesConnector;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The class <code>TestsComputer</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 15 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			TestsComputer
extends AbstractCVM
{
	public static final String	ComputerServicesInboundPortURI = "cs-ibp" ;
	public static final String	ComputerServicesOutboundPortURI = "cs-obp" ;
	public static final String	ComputerStaticStateDataInboundPortURI = "css-dip" ;
	public static final String	ComputerStaticStateDataOutboundPortURI = "css-dop" ;
	public static final String	ComputerDynamicStateDataInboundPortURI = "cds-dip" ;
	public static final String	ComputerDynamicStateDataOutboundPortURI = "cds-dop" ;

	protected ComputerServicesOutboundPort			csPort ;
	protected ComputerStaticStateDataOutboundPort	cssPort ;
	protected ComputerDynamicStateDataOutboundPort	cdsPort ;

	@Override
	public void			deploy() throws Exception
	{
		String computerURI = "computer0" ;
		int numberOfProcessors = 1 ;
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
		cm.toggleTracing() ;
		cm.toggleLogging() ;
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

		super.deploy() ;
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
		AllocatedCore[] ac = this.csPort.allocateCores(2) ;

		final String processorServicesInboundPortURI =
			ac[0].processorInboundPortURI.get(ProcessorPortTypes.SERVICES) ;
		final String processorManagementInboundPortURI =
			ac[0].processorInboundPortURI.get(ProcessorPortTypes.MANAGEMENT) ;

		ProcessorServicesOutboundPort psPort =
			new ProcessorServicesOutboundPort(new AbstractComponent() {}) ;
		psPort.publishPort() ;
		psPort.doConnection(
					processorServicesInboundPortURI,
					ProcessorServicesConnector.class.getCanonicalName()) ;

		ProcessorManagementOutboundPort pmPort =
			new ProcessorManagementOutboundPort(new AbstractComponent() {}) ;
		pmPort.publishPort() ;
		pmPort.doConnection(
					processorManagementInboundPortURI,
					ProcessorManagementConnector.class.getCanonicalName()) ;

		System.out.println("starting mytask-001 on core 0") ;
		psPort.executeTaskOnCore(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 15000000000L;
							}

							@Override
							public String getRequestURI() {
								return "r0" ;
							}
						};
					}
					@Override
					public String getTaskURI() {
						return "mytask-001";
					}
				},
				ac[0].coreNo) ;

		System.out.println("starting mytask-002 on core 1") ;
		psPort.executeTaskOnCore(
				new TaskI() {
					private static final long serialVersionUID = 1L;
					@Override
					public RequestI getRequest() {
						return new RequestI() {
							private static final long serialVersionUID = 1L;

							@Override
							public long getPredictedNumberOfInstructions() {
								return 30000000000L ;
							}

							@Override
							public String getRequestURI() {
								return "r1" ;
							}
						};
					}
					@Override
					public String getTaskURI() {
						return "mytask-002";
					}
				},
				ac[1].coreNo) ;

		// Test scenario 1
		Thread.sleep(5000L) ;
		pmPort.setCoreFrequency(0, 3000) ;
		// Test scenario 2
		// Thread.sleep(3000L) ;
		// pmPort.setCoreFrequency(0, 1500) ;

		psPort.doDisconnection() ;
		pmPort.doDisconnection() ;
		psPort.unpublishPort() ;
		pmPort.unpublishPort() ;
	}

	public static void	main(String[] args)
	{
		// AbstractCVM.toggleDebugMode() ;
		final TestsComputer c = new TestsComputer() ;
		try {
			c.deploy() ;
			System.out.println("starting...") ;
			c.start() ;
			new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									c.testScenario() ;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start() ;
			Thread.sleep(25000L) ;
			System.out.println("shutting down...") ;
			c.shutdown() ;
			System.out.println("ending...") ;
			System.exit(0) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
