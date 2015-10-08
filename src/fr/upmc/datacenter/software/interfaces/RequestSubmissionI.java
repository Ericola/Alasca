package fr.upmc.datacenter.software.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

/**
 * The interface <code>ApplicationVMServicesI</code>
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
public interface		RequestSubmissionI
extends		OfferedI,
			RequiredI
{
	/**
	 * submit a request to a request handler.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null && r.getPredictedNumberOfInstructions() >= 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r				request to be submitted.
	 * @throws Exception
	 */
	public void			submitRequest(final RequestI r) throws Exception ;

	/**
	 * submit a request to a request handler and require notifications of
	 * request execution progress.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null && r.getPredictedNumberOfInstructions() >= 0
	 * pre	notificationPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r						request to be submitted.
	 * @throws Exception
	 */
	public void			submitRequestAndNotify( final RequestI r)
	throws Exception ;
}
