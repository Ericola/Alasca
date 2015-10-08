package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The interface <code>ProcessorServicesNotificationI</code> defines the
 * notification services, namely the end of task notification.
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
public interface		ProcessorServicesNotificationI
extends		OfferedI,
			RequiredI
{
	/**
	 * notify the end of a task.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t				task which execution has ended.
	 * @throws Exception
	 */
	public void			notifyEndOfTask(final TaskI t)
	throws Exception ;
}
