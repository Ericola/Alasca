package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ProcessorDynamicStateI</code> gives access to the
 * dynamic state information of processors transmitted by data interfaces of
 * processors.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface is used to type objects pulled from or pushed by a processor
 * using a data interface in pull or push mode.  It gives access to dynamic
 * information, that is information subject to changes during the existence of
 * the processor.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 7 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ProcessorDynamicStateI
extends 	DataOfferedI.DataI,
			DataRequiredI.DataI,
			TimeStampingI
{
	/**
	 * return a boolean array of cores' idle status i.e., for each core
	 * <code>i</code> the result contains at index <code>i</code>
	 * <code>true</code> if the core <code>i</code> is idle and
	 * <code>false</code> otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	array of cores' idle status.
	 */
	public	boolean[]	getCoresIdleStatus() ;

	/**
	 * return <code>true</code> if the core <code>codeNo</code> is idle and
	 * <code>false</code> otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	number of the core tested.
	 * @return			true if the core is idle, false otherwise.
	 */
	public	boolean		getCoreIdleStatus(int coreNo) ;

	/**
	 * return an array of the current frequencies of the cores where the
	 * frequency of core i is at position i.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	array of the current frequencies of the cores.
	 */
	public int[]	getCurrentCoreFrequencies() ;

	/**
	 * return the current frequency of the given core.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	number of the core to be inquired.
	 * @return			current frequency of the given core.
	 */
	public int		getCurrentCoreFrequency(int coreNo) ;
}
