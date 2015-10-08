package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementInboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorManagementInboundPort
extends		AbstractInboundPort
implements	ProcessorManagementI
{
	private static final long serialVersionUID = 1L;

	public				ProcessorManagementInboundPort(ComponentI owner)
	throws Exception
	{
		super(ProcessorManagementI.class, owner);
	}

	public				ProcessorManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ProcessorManagementI.class, owner);
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI#setCoreFrequency(int, int)
	 */
	@Override
	public void			setCoreFrequency(
		final int coreNo,
		final int frequency
		) throws	UnavailableFrequencyException,
					UnacceptableFrequencyException,
					Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
					"ProcessorManagementInboundPort>>setCoreFrequency(" +
					coreNo + ", " + frequency + ")") ;
		}

		final Processor p = (Processor) this.owner ;
		p.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						p.setCoreFrequency(coreNo, frequency) ;
						return null;
					}
				});
	}
}
