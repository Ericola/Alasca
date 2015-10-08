package fr.upmc.datacenter.hardware.processors.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ProcessorServicesI</code>.
 *
 * <p><strong>Description</strong></p>
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
public class			ProcessorServicesConnector
extends		AbstractConnector
implements	ProcessorServicesI
{
	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCore(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI, int)
	 */
	@Override
	public void			executeTaskOnCore(final TaskI t, final int coreNo)
	throws Exception
	{
		((ProcessorServicesI)this.offering).executeTaskOnCore(t, coreNo) ;
	}

	@Override
	public void			executeTaskOnCoreAndNotify(
		TaskI t,
		int coreNo,
		String notificationPortURI
		) throws Exception
	{
		((ProcessorServicesI)this.offering).
					executeTaskOnCoreAndNotify(t, coreNo, notificationPortURI) ;
	}
}
