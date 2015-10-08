package fr.upmc.datacenter.hardware.computers.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI;

/**
 * The class <code>ComputerServiceConnector</code> implements a connector for
 * ports exchanging through the interface <code>ComputerServicesI</code>.
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
public class			ComputerServicesConnector
extends		AbstractConnector
implements	ComputerServicesI
{
	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCore()
	 */
	@Override
	public AllocatedCore	allocateCore() throws Exception
	{
		return ((ComputerServicesI)this.offering).allocateCore() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerServicesI#allocateCores(int)
	 */
	@Override
	public AllocatedCore[]	allocateCores(int numberRequested) throws Exception
	{
		return ((ComputerServicesI)this.offering).
											allocateCores(numberRequested) ;
	}
}
