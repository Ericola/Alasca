package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServicesInboudPort</code> implements an inbound
 * port offering the <code>ComputerServicesI</code> interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 9 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ComputerServicesInboundPort
extends		AbstractInboundPort
implements	ComputerServicesI
{
	private static final long serialVersionUID = 1L;

	public				ComputerServicesInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ComputerServicesI.class, owner) ;

		assert owner instanceof Computer ;
	}

	public ComputerServicesInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ComputerServicesI.class, owner);

		assert owner instanceof Computer ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore	allocateCore() throws Exception
	{
		final Computer c = (Computer) this.owner ;
		return c.handleRequestSync(
					new ComponentI.ComponentService<AllocatedCore>() {
						@Override
						public AllocatedCore call() throws Exception {
							return c.allocateCore() ;
						}
					});
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[]	allocateCores(
		final int numberRequested
		) throws Exception
	{
		final Computer c = (Computer) this.owner ;
		return c.handleRequestSync(
					new ComponentI.ComponentService<AllocatedCore[]>() {
						@Override
						public AllocatedCore[] call() throws Exception {
							return c.allocateCores(numberRequested) ;
						}
					}) ;
	}
}
