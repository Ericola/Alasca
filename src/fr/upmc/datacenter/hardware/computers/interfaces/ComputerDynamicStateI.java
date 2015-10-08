package fr.upmc.datacenter.hardware.computers.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ComputerDynamicStateI</code> implements objects
 * representing the dynamic state information of computers transmitted
 * through the <code>ComputerDynamicStateDataI</code> interface of
 * <code>Computer</code> components.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface is used to type objects pulled from or pushed by a computer
 * using a data interface in pull or push mode.  It gives access to dynamic
 * information, that is information subject to changes during the existence of
 * the computer.
 * 
 * Data objects are timestamped in standard Unix local time format, with the
 * IP of the computer doing this timestamp.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 14 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ComputerDynamicStateI
extends 	DataOfferedI.DataI,
			DataRequiredI.DataI,
			TimeStampingI
{
	/**
	 * return the computer URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the computer URI.
	 */
	public String			getComputerURI() ;

	/**
	 * return a boolean 2D array containing <code>true</code> in each cell
	 * <code>(p,c)</code> if the core <code>c</code> of processor
	 * <code>p</code> is currently reserved and false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	a boolean 2D array where true cells indicate reserved cores.
	 */
	public boolean[][]		getCurrentCoreReservations() ;
}
