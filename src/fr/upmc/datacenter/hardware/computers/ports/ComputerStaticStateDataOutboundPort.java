package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.ports.AbstractDataOutboundPort;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateI;

/**
 * The class <code>ComputerStaticStateDataOutboundPort</code> implements a data
 * outbound port requiring the <code>ComputerStaticStateDataI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 14 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ComputerStaticStateDataOutboundPort
extends		AbstractDataOutboundPort
implements	ComputerStaticStateDataI
{
	private static final long	serialVersionUID = 1L;
	protected final String		computerURI ;

	public				ComputerStaticStateDataOutboundPort(
		ComponentI owner,
		String computerURI
		) throws Exception
	{
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.computerURI = computerURI ;

		assert	owner instanceof ComputerStateDataConsumerI ;
	}

	public				ComputerStaticStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String computerURI
		) throws Exception
	{
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner);
		this.computerURI = computerURI ;

		assert	owner instanceof ComputerStateDataConsumerI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d) throws Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println("ComputerStaticStateDataOutboundPort>>receive " +
									computerURI + " " + d) ;
		}

		((ComputerStateDataConsumerI)this.owner).
			acceptComputerStaticData(computerURI,  (ComputerStaticStateI) d) ;
	}
}
