package fr.upmc.datacenter.hardware.computers.interfaces;

import java.util.Map;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.interfaces.TimeStampingI;

/**
 * The class <code>ComputerStaticStateI</code> implements objects
 * representing the static state information of computers transmitted
 * through the <code>ComputerStaticStateDataI</code> interface of
 * <code>Computer</code> components.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface is used to type objects pulled from or pushed by a computer
 * using a data interface in pull or push mode.  It gives access to static
 * information, that is information *not* subject to changes during the
 * existence of the computer.
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
public interface		ComputerStaticStateI
extends		DataOfferedI.DataI,
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
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the computer URI.
	 */
	public String			getComputerURI() ;

	/**
	 * return the number of processors in the computer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the number of processors in the computer.
	 */
	public int				getNumberOfProcessors() ;

	/**
	 * return the number of cores per processor on this computer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the number of cores per processor on this computer.
	 */
	public int				getNumberOfCoresPerProcessor() ;

	/**
	 * return an array of the processors URI on this computer.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	array of the processors URI on this computer.
	 */
	public Map<Integer,String>	getProcessorURIs() ;

	/**
	 * return a map from processors URI to a map from processor's port types
	 * to processors' ports URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	map from processors URI to a map from processor's port types to processors' ports URI.
	 */
	public Map<String,Map<Processor.ProcessorPortTypes,String>>		
							getProcessorPortMap() ;
}
