package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The interface <code>ProcessorServicesNotificationConsumerI</code> is
 * implemented by consumers of the processor notifications.
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
public interface		ProcessorServicesNotificationConsumerI
{
	public void			acceptNotifyEndOfTask(final TaskI t)
	throws Exception ;
}
