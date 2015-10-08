package fr.upmc.datacenter.hardware.tests;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.exceptions.ComponentStartException;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
import fr.upmc.datacenter.hardware.computers.ports.ComputerDynamicStateDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.ports.ComputerStaticStateDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;

/**
 * The class <code>ComputerMonitor</code> is a component used in the test to
 * act as a receiver for state data notifications coming from a computer.
 *
 * <p><strong>Description</strong></p>
 * 
 * The component class simply implements the necessary methods to process the
 * notifications without paying attention to do that in a really safe component
 * programming way. More or less quick and dirty...
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 24 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ComputerMonitor
extends		AbstractComponent
implements	ComputerStateDataConsumerI
{
	// -------------------------------------------------------------------------
	// Constructors and instance variables
	// -------------------------------------------------------------------------

	protected boolean									active ;
	protected ComputerStaticStateDataOutboundPort		cssPort ;
	protected ComputerDynamicStateDataOutboundPort		cdsPort ;

	public				ComputerMonitor(
		String computerURI,
		boolean active,
		String computerStaticStateDataOutboundPortURI,
		String computerDynamicStateDataOutboundPortURI
		) throws Exception
	{
		super(true) ;
		this.active = active ;

		this.addOfferedInterface(DataRequiredI.PushI.class) ;
		this.addRequiredInterface(DataRequiredI.PullI.class) ;
		this.cssPort = new ComputerStaticStateDataOutboundPort(
								computerStaticStateDataOutboundPortURI,
								this,
								computerURI) ;
		this.addPort(cssPort) ;
		this.cssPort.publishPort() ;

		this.addRequiredInterface(ControlledDataRequiredI.ControlledPullI.class) ;
		this.cdsPort = new ComputerDynamicStateDataOutboundPort(
								computerDynamicStateDataOutboundPortURI,
								this,
								computerURI) ;
		this.addPort(cdsPort) ;
		this.cdsPort.publishPort() ;
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	@Override
	public void			start() throws ComponentStartException
	{
		super.start() ;

		// start the pushing of dynamic state information from the computer;
		// here only one push of information is planned after one second.
		try {
			this.cdsPort.startLimitedPushing(1000, 25) ;
		} catch (Exception e) {
			throw new ComponentStartException(
							"Unable to start the pushing of dynamic data from"
							+ " the comoter component.", e) ;
		}
	}

	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			if (this.cssPort.connected()) {
				this.cssPort.doDisconnection() ;
			}
			if (this.cdsPort.connected()) {
				this.cdsPort.doDisconnection() ;
			}
		} catch (Exception e) {
			throw new ComponentShutdownException("port disconnection error", e) ;
		}

		super.shutdown();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	@Override
	public void		acceptComputerStaticData(
		String computerURI,
		ComputerStaticStateI ss
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("\nAccepting static data from " + computerURI + "\n") ;
			sb.append("  timestamp                      : " +
			   							ss.getTimeStamp() + "\n") ;
			sb.append("  timestamper id                 : " +
										ss.getTimeStamperId() + "\n") ;
			sb.append("  number of processors           : " +
										ss.getNumberOfProcessors() + "\n") ;
			sb.append("  number of cores per processors : " +
										ss.getNumberOfCoresPerProcessor() + "\n") ;
			for (int p = 0 ; p < ss.getNumberOfProcessors() ; p++) {
				if (p == 0) {
					sb.append("  processor URIs                 : ") ;
				} else {
					sb.append("                                 : ") ;
				}
				sb.append(p + "  " + ss.getProcessorURIs().get(p) + "\n") ;
			}
			sb.append("  processor port URIs            : " + "\n") ;
			sb.append(Computer.printProcessorsInboundPortURI(
						10, ss.getNumberOfProcessors(),
						ss.getProcessorURIs(), ss.getProcessorPortMap())) ;
			this.logMessage(sb.toString()) ;
		}
	}

	@Override
	public void		acceptComputerDynamicData(
		String computerURI,
		ComputerDynamicStateI cds
		) throws Exception
	{
		if (this.active) {
			StringBuffer sb = new StringBuffer() ;
			sb.append("\nAccepting dynamic data from " + computerURI + "\n") ;
			sb.append("  timestamp                : " +
											cds.getTimeStamp() + "\n") ;
			sb.append("  timestamper id           : " +
											cds.getTimeStamperId() + "\n") ;

			boolean[][] reservedCores = cds.getCurrentCoreReservations() ;
			for (int p = 0 ; p < reservedCores.length ; p++) {
				if (p == 0) {
					sb.append("  reserved cores           : ") ;
				} else {
					sb.append("                             ") ;
				}
				for (int c = 0 ; c < reservedCores[p].length ; c++) {
					if (reservedCores[p][c]) {
						sb.append("t ") ;
					} else {
						sb.append("f ") ;
					}
				}
				sb.append("\n") ;
			}
			this.logMessage(sb.toString()) ;
		}
	}
}
