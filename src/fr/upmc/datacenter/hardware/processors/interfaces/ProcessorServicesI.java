package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The interface <code>ProcessorServicesI</code> defines the functional
 * services of a processor, such as running a task on a given core.
 *
 * <p><strong>Description</strong></p>
 * 
 * The method <code>executeTaskOnCore</code> runs a given task on a given core.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 19 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ProcessorServicesI
extends		OfferedI,
			RequiredI
{
	/**
	 * execute a task on a given core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null && isValidCoreNo(coreNo)
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t			task to be executed.
	 * @param coreNo	number of the core on which t must be executed.
	 * @throws Exception
	 */
	public void			executeTaskOnCore(final TaskI t, final int coreNo)
	throws Exception ;

	/**
	 * execute a task on a given core and notify the end of the task through
	 * a port which URI is given.
	 * 
	 * The port which URI is given must implement the interface
	 * <code>ProcessorServicesNotificationI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	t != null && isValidCoreNo(coreNo) && notificationPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param t						task to be executed.
	 * @param coreNo				number of the core on which t must be executed.
	 * @param notificationPortURI	URI of the port to which the end of the task will be notified.
	 * @throws Exception
	 */
	public void			executeTaskOnCoreAndNotify(
		final TaskI t,
		final int coreNo,
		final String notificationPortURI
		) throws Exception ;
}
