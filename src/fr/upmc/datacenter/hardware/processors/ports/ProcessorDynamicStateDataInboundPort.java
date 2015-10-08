package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;

/**
 * The class <code>ProcessorDynamicStateDataInboundPort</code> is the
 * server-side port to exchange dynamic state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Inbound port used to pull or push dynamic state data from a processor or to
 * a processor client.  These ports must be connected by a
 * <code>ProcessorDynamicStateDataConnector</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 7 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorDynamicStateDataInboundPort
extends		AbstractControlledDataInboundPort
{
	private static final long serialVersionUID = 1L;

	public				ProcessorDynamicStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(owner) ;

		assert owner instanceof Processor ;
	}

	public				ProcessorDynamicStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, owner);

		assert owner instanceof Processor ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
					new ComponentI.ComponentService<DataOfferedI.DataI>() {
						@Override
						public DataOfferedI.DataI call() throws Exception {
							return p.getDynamicState() ;
						}
					}) ;
	}
}
