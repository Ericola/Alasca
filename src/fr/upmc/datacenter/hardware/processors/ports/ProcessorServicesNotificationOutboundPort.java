package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesNotificationOutboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 24 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorServicesNotificationOutboundPort
extends		AbstractOutboundPort
implements	ProcessorServicesNotificationI
{

	public				ProcessorServicesNotificationOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorServicesNotificationI.class, owner);
	}

	public				ProcessorServicesNotificationOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ProcessorServicesNotificationI.class, owner);
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI#notifyEndOfTask(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void			notifyEndOfTask(TaskI t) throws Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
				"ProcessorServicesNotificationOutboundPort>>notifyEndOfTask(" +
				t.getTaskURI() + ")") ;
		}

		((ProcessorServicesNotificationI)this.connector).notifyEndOfTask(t) ;
	}
}
