package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI ;
import fr.upmc.components.interfaces.DataRequiredI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI ;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;

/**
 * The class <code>ProcessorDynamicStateDataOutboundPort</code> is the
 * client-side port to exchange dynamic state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Outbound port used to pull or push dynamic state data from a processor or to
 * a processor client.  These ports must be connected by a
 * <code>ProcessorDynamicStateDataConnector</code>.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 8 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorDynamicStateDataOutboundPort
extends		AbstractControlledDataOutboundPort
{
	private static final long	serialVersionUID = 1L;
	protected String			processorURI ;

	public				ProcessorDynamicStateDataOutboundPort(
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	public				ProcessorDynamicStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(uri, owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	/**
	 * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
	 */
	@Override
	public void			receive(DataRequiredI.DataI d) throws Exception
	{
		((ProcessorStateDataConsumerI)this.owner).
					acceptProcessorDynamicData(this.processorURI,
											   ((ProcessorDynamicStateI)d)) ;
	}
}
