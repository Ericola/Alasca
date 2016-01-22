package fr.upmc.datacenter.software.applicationvm.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>ApplicationVMManagementI</code> defines the methods
 * to manage an application virtual machine component.
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
public interface		ApplicationVMManagementI
extends		OfferedI,
			RequiredI
{
	/**
	 * allocate cores to the application virtual machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	allocatedCores != null && allocatedCores.length != 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param allocatedCores	array of cores already reserved provided to the VM.
	 * @throws Exception
	 */
	public void			allocateCores(AllocatedCore[] allocatedCores)
	throws Exception ;
	
	public AllocatedCore[] getCoresInVM() throws Exception;
}
