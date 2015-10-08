package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementOutboundPort</code>
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
public class			ProcessorManagementOutboundPort
extends		AbstractOutboundPort
implements	ProcessorManagementI
{
	public 				ProcessorManagementOutboundPort(ComponentI owner)
	throws Exception
	{
		super(ProcessorManagementI.class, owner);
	}

	public				ProcessorManagementOutboundPort(
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
					"ProcessorManagementOutboundPort>>setCoreFrequency(" +
					coreNo + ", " + frequency + ")") ;
		}

		((ProcessorManagementI)this.connector).
										setCoreFrequency(coreNo, frequency) ;
	}
}
