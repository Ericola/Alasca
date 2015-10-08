package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationConsumerI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesNotificationInboudPort</code>
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
public class			ProcessorServicesNotificationInboundPort
extends		AbstractInboundPort
implements	ProcessorServicesNotificationI
{
	private static final long serialVersionUID = 1L;

	public				ProcessorServicesNotificationInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorServicesNotificationI.class, owner) ;
		assert	owner instanceof ProcessorServicesNotificationConsumerI ;
	}

	public ProcessorServicesNotificationInboundPort(
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
	public void			notifyEndOfTask(final TaskI t)
	throws Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
				"ProcessorServicesNotificationInboundPort>>notifyEndOfTask(" +
				t.getTaskURI() + ")") ;
		}

		final ProcessorServicesNotificationConsumerI psnc =
						(ProcessorServicesNotificationConsumerI) this.owner ;
		this.owner.handleRequestAsync(
						new ComponentI.ComponentService<Void>() {
							@Override
							public Void call() throws Exception {
								psnc.acceptNotifyEndOfTask(t) ;
								return null;
							}
						}) ;
	}
}
