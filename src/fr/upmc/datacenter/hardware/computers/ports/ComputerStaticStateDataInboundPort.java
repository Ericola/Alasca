package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerStaticStateDataI;

/**
 * The class <code>ComputerStaticStateDataInboundPort</code> implements a data
 * inbound port offering the <code>ComputerStaticStateDataI</code> interface.
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
public class			ComputerStaticStateDataInboundPort
extends		AbstractDataInboundPort
implements	ComputerStaticStateDataI
{
	private static final long serialVersionUID = 1L;

	public				ComputerStaticStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner) ;

		assert	owner instanceof Computer ;
	}

	public ComputerStaticStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);

		assert	owner instanceof Computer ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		return ((Computer)this.owner).getStaticState() ;
	}
}
