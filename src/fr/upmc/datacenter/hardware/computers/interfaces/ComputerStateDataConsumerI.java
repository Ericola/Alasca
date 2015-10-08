package fr.upmc.datacenter.hardware.computers.interfaces;

/**
 * The interface <code>ComputerStateDataConsumerI</code> defines the consumer
 * side methods used to receive state data pushed by a computer, both static
 * and dynamic.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface must be implemented by all classes representing components
 * that will consume as clients state data pushed by a computer.  They are
 * used by <code>ComputerStaticStateOutboundPort</code> and
 * <code>ComputerDynamicStateOutboundPort</code> to pass these data
 * upon reception from the processor component.
 * 
 * As a client component may receive data from several different computers,
 * it can assign URI to each at the creation of outbound ports, so that these
 * can pass these URI when receiving data.  Hence, the methods defined in this
 * interface will be unique in one client component but receive the data pushed
 * by all of the different computers.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 15 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ComputerStateDataConsumerI
{
	/**
	 * accept the static data pushed by a computer with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	computerURI != null && staticState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI	URI of the computer sending the data.
	 * @param staticState	static state of this computer.
	 * @throws Exception
	 */
	public void			acceptComputerStaticData(
		String					computerURI,
		ComputerStaticStateI	staticState
		) throws Exception ;

	/**
	 * accept the dynamic data pushed by a computer with the given URI.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	computerURI != null && currentDynamicState != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI			URI of the computer sending the data.
	 * @param currentDynamicState	current dynamic state of this computer.
	 * @throws Exception
	 */
	public void			acceptComputerDynamicData(
		String					computerURI,
		ComputerDynamicStateI	currentDynamicState
		) throws Exception ;
}
