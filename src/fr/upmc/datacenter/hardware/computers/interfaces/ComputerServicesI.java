package fr.upmc.datacenter.hardware.computers.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * The interface <code>ComputerServicesI</code> defines the services offered by
 * <code>Computer>/code> components (allocating cores).
 *
 * <p><strong>Description</strong></p>
 * 
 * TODO: add the deallocation of cores.
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
public interface		ComputerServicesI
extends		OfferedI,
			RequiredI
{
	/**
	 * allocate one core on this computer and return an instance of
	 * <code>AllocatedCore</code> containing the processor number,
	 * the core number and a map giving the URI of the processor
	 * inbound ports; return null if no core is available.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	an instance of <code>AllocatedCore</code> with the data about the allocated core.
	 * @throws Exception
	 */
	public AllocatedCore	allocateCore() throws Exception ;

	/**
	 * allocate up to <code>numberRequested</code> cores on this computer and
	 * return and array of <code>AllocatedCore</code> containing the data for
	 * each requested core; return an empty array if no core is available.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	numberRequested > 0
	 * post	return.length >= 0 && return.length <= numberRequested
	 * </pre>
	 *
	 * @param numberRequested	number of cores to be allocated.
	 * @return					an array of instances of <code>AllocatedCore</code> with the data about the allocated cores.
	 * @throws Exception
	 */
	public AllocatedCore[]	allocateCores(final int numberRequested)
	throws Exception ;
}
