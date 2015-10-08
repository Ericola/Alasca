package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStateDataConsumerI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ComputerDynamicDataOutboundPort</code> implements a data
 * outbound port requiring the <code>ComputerDynamicStateDataI</code> interface.
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
public class			ComputerDynamicStateDataOutboundPort
extends		AbstractControlledDataOutboundPort
{
	private static final long	serialVersionUID = 1L ;
	protected String			computerURI ;

	public				ComputerDynamicStateDataOutboundPort(
		ComponentI owner,
		String computerURI
		) throws Exception
	{
		super(owner) ;
		this.computerURI = computerURI ;

		assert	owner instanceof ComputerStateDataConsumerI ;
	}

	public				ComputerDynamicStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String computerURI
		) throws Exception
	{
		super(uri, owner);
		this.computerURI = computerURI ;

		assert	owner instanceof ComputerStateDataConsumerI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d)
	throws Exception
	{
		((ComputerStateDataConsumerI)this.owner).
						acceptComputerDynamicData(this.computerURI,
												  (ComputerDynamicStateI) d) ;
	}
}
