package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI ;
import fr.upmc.components.interfaces.DataRequiredI ;
import fr.upmc.components.ports.AbstractDataOutboundPort ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateDataI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorStaticStateDataOutboundPort</code> is the
 * client-side port to exchange static state data with a processor component.
 *
 * <p><strong>Description</strong></p>
 * 
 * Outbound port used to pull or push static state data from a processor or to
 * a processor client.  These ports can be connected by a simple
 * <code>DataConnector</code>.
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
public class			ProcessorStaticStateDataOutboundPort
extends		AbstractDataOutboundPort
implements	ProcessorStaticStateDataI
{
	private static final long	serialVersionUID = 1L ;
	protected final String		processorURI ;

	public				ProcessorStaticStateDataOutboundPort(
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.processorURI = processorURI ;

		assert owner instanceof ProcessorStateDataConsumerI ;
	}

	public				ProcessorStaticStateDataOutboundPort(
		String uri,
		ComponentI owner,
		String processorURI
		) throws Exception
	{
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
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
					acceptProcessorStaticData(this.processorURI,
											  ((ProcessorStaticStateI)d)) ;
	}
}
