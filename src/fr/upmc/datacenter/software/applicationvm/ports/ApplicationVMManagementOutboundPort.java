package fr.upmc.datacenter.software.applicationvm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementOutboundPort</code> implements the
 * inbound port requiring the interface <code>ApplicationVMManagementI</code>.
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
public class			ApplicationVMManagementOutboundPort 
extends		AbstractOutboundPort
implements	ApplicationVMManagementI
{
	public				ApplicationVMManagementOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ApplicationVMManagementI.class, owner) ;
	}

	public				ApplicationVMManagementOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ApplicationVMManagementI.class, owner);
	}

	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void			allocateCores(final AllocatedCore[] allocatedCores)
	throws Exception
	{
		((ApplicationVMManagementI)this.connector).
												allocateCores(allocatedCores) ;
	}
}
