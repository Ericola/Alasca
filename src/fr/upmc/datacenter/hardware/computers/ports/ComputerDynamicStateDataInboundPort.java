package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

/**
 * The class <code>ComputerDynamicStateDataInboundPort</code> implements a data
 * inbound port offering the <code>ComputerDynamicStateDataI</code> interface.
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
public class			ComputerDynamicStateDataInboundPort
extends		AbstractControlledDataInboundPort
{
	private static final long serialVersionUID = 1L;

	public				ComputerDynamicStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(owner) ;

		assert owner instanceof Computer ;
	}

	public				ComputerDynamicStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, owner);

		assert owner instanceof Computer ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		final Computer c = (Computer) this.owner ;
		return c.handleRequestSync(
					new ComponentI.ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception {
							return c.getDynamicState() ;
						}
					});
	}
}
