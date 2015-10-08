package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>ProcessorIntrospectionI</code> gives access to the
 * state information of processors through offered ports of processors.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 28 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ProcessorIntrospectionI
extends		OfferedI,
			RequiredI
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
	 * @throws Exception
	 */
	public int			getNumberOfCores() throws Exception ;

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
	 * @throws Exception
	 */
	public int			getDefaultFrequency() throws Exception ;

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
	 * @return	the maximum frequency gap tolerated on this processor.
	 * @throws Exception
	 */
	public int			getMaxFrequencyGap() throws Exception ;

	/**
	 * return true if <code>coreNo</code> is a valid core number on this
	 * processor and false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	core number to be tested.
	 * @return			true if <code>coreNo</code> is a valid core number.
	 * @throws Exception
	 */
	public boolean		isValidCoreNo(final int coreNo) throws Exception ;

	/**
	 * return true if <code>frequency>/code> is an admissible frequency for a
	 * core on this processor, and false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param frequency	frequency to be tested.
	 * @return			true if <code>frequency>/code> is admissible.
	 * @throws Exception
	 */
	public boolean		isAdmissibleFrequency(final int frequency)
	throws Exception ;

	/**
	 * return true if <code>frequency>/code> is a currently possible frequency
	 * for a given core on this processor, and false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coreNo	number of the core to be tested.
	 * @param frequency	frequency to be tested.
	 * @return			true if <code>frequency>/code> is currently possible.
	 * @throws Exception
	 */
	public boolean		isCurrentlyPossibleFrequencyForCore(
		int coreNo,
		int frequency
		) throws Exception ;

	/**
	 * return the static state of the processor as an instance of
	 * <code>ProcessorStaticStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the static state of the processor.
	 * @throws Exception
	 */
	public ProcessorStaticStateI	getStaticState() throws Exception ;

	/**
	 * return the dynamic state of the processor as an instance of
	 * <code>ProcessorDynamicStateI</code>.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	return != null
	 * </pre>
	 *
	 * @return	the dynamic state of the processor.
	 * @throws Exception
	 */
	public ProcessorDynamicStateI	getDynamicState() throws Exception ;
}
