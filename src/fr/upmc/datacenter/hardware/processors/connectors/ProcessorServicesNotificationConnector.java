package fr.upmc.datacenter.hardware.processors.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesNotificationConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ProcessorServicesNotificationI</code>.
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
public class			ProcessorServicesNotificationConnector
extends		AbstractConnector
implements	ProcessorServicesNotificationI
{
	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI#notifyEndOfTask(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI)
	 */
	@Override
	public void			notifyEndOfTask(TaskI t) throws Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
				"ProcessorServicesNotificationConnector>>notifyEndOfTask(" +
				t.getTaskURI() + ")") ;
		}

		((ProcessorServicesNotificationI)this.offering).notifyEndOfTask(t) ;
	}
}
