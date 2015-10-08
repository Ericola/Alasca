package fr.upmc.datacenter.hardware.computers.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServiceOutboundPort</code> implements an outbound
 * port requiring the <code>ComputerServicesI</code> interface.
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
public class			ComputerServicesOutboundPort
extends		AbstractOutboundPort
implements	ComputerServicesI
{
	public				ComputerServicesOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ComputerServicesI.class, owner) ;
	}

	public				ComputerServicesOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ComputerServicesI.class, owner);
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore	allocateCore() throws Exception
	{
		return ((ComputerServicesI)this.connector).allocateCore() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[] allocateCores(final int numberRequested)
	throws Exception
	{
		return ((ComputerServicesI)this.connector).
											allocateCores(numberRequested) ;
	}
}
