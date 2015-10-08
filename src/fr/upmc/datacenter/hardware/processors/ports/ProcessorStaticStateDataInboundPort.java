package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateDataI;

/**
 * The class <code>ProcessorStaticStateDataInboundPort</code> is the
 * server-side port to exchange static state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Inbound port used to pull or push static state data from a processor or to
 * a processor client.  These ports can be connected by a simple
 * <code>DataConnector</code>.
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
public class			ProcessorStaticStateDataInboundPort
extends		AbstractDataInboundPort
implements	ProcessorStaticStateDataI
{
	private static final long serialVersionUID = 8176548432187715058L;

	public				ProcessorStaticStateDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
	}

	public				ProcessorStaticStateDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner) ;
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
							return p.getStaticState() ;
						}
					}) ;
	}
}
