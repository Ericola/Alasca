package fr.upmc.datacenter.software.applicationvm.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ApplicationVMManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : August 25, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ApplicationVMManagementConnector
extends		AbstractConnector
implements	ApplicationVMManagementI
{
	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void			allocateCores(
		final AllocatedCore[] allocatedCores
		) throws Exception
	{
		((ApplicationVMManagementI)this.offering).allocateCores(allocatedCores) ; ;
	}
}
