package fr.upmc.datacenter.software.applicationvm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

/**
 * The class <code>ApplicationVMManagementInboundPort</code> implements the
 * inbound port offering the interface <code>ApplicationVMManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	owner instanceof ApplicationVMManagementI
 * </pre>
 * 
 * <p>Created on : August 25, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ApplicationVMManagementInboundPort
extends		AbstractInboundPort
implements	ApplicationVMManagementI
{
	private static final long serialVersionUID = 1L;

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof ApplicationVMManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				ApplicationVMManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ApplicationVMManagementI.class, owner) ;

		assert	owner instanceof ApplicationVMManagementI ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner instanceof ApplicationVMManagementI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				ApplicationVMManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ApplicationVMManagementI.class, owner);

		assert	uri != null && owner instanceof ApplicationVMManagementI ;
	}

	/**
	 * @see fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI#allocateCores(fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore[])
	 */
	@Override
	public void			allocateCores(
		final AllocatedCore[] allocatedCores
		) throws Exception
	{
		final ApplicationVMManagementI avm =
									(ApplicationVMManagementI) this.owner ;

		this.owner.handleRequestSync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						avm.allocateCores(allocatedCores) ;
						return null;
					}
				}) ;
	}

	@Override
	public AllocatedCore[] getCoresInVM() throws Exception {
		final ApplicationVMManagementI avm =
				(ApplicationVMManagementI) this.owner ;
		
		return this.owner.handleRequestSync(
				new ComponentI.ComponentService<AllocatedCore[]>() {
					@Override
					public AllocatedCore[] call() throws Exception {
						return avm.getCoresInVM();
					}
				}) ;
	}
}
