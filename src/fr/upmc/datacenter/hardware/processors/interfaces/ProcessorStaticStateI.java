package fr.upmc.datacenter.hardware.processors.interfaces;

import java.util.Map;
import java.util.Set;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

/**
 * The interface <code>ProcessorStaticStateI</code> gives access to the static
 * state information of processors transmitted by data interfaces of
 * processors and computers.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface is used to type objects pulled from or pushed by a processor
 * using a data interface in pull or push mode.  It gives access to static
 * information, that is information that will not change during the whole
 * existence of the processor.
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
public interface		ProcessorStaticStateI
extends 	DataOfferedI.DataI,
			DataRequiredI.DataI,
			TimeStampingI
{
	/**
	 * return the number of cores on the processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return > 0
	 * </pre>
	 *
	 * @return	the number of cores on the processor.
	 */
	public int				getNumberOfCores() ;

	/**
	 * return the default frequency of the processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return > 0
	 * </pre>
	 *
	 * @return	the default frequency of the processor.
	 */
	public int				getDefaultFrequency() ;

	/**
	 * return the maximum frequency gap tolerated on this processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return >= 0
	 * </pre>
	 *
	 * @return	maximum frequency gap tolerated on this processor.
	 */
	public int				getMaxFrequencyGap() ;

	/**
	 * return the set of admissible frequencies for core on this processor.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	set of admissible frequencies for core on this processor.
	 */
	public Set<Integer>		getAdmissibleFrequencies() ;

	/**
	 * return the map giving the processing power of cores for their different
	 * admissible frequencies.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	map giving the processing power of cores for their different admissible frequencies.
	 */
	Map<Integer, Integer>	getProcessingPower() ;
}
