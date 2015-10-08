package fr.upmc.datacenter.software.interfaces;

/**
 * The interface <code>RequestNotificationHandlerI</code> defines the methods
 * that must be implemented by a component to handle request notifications
 * received through an inboud port <code>RequestNotificationInboundPort</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 4 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RequestNotificationHandlerI
{
	/**
	 * process the termination notification of a request.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r
	 * @throws Exception
	 */
	public void			acceptRequestTerminationNotification(RequestI r)
	throws Exception ;
}
