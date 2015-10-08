package fr.upmc.datacenter.software.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>TaskHandlerNotificationConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>RequestNotificationI</code>.
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
public class			RequestNotificationConnector
extends		AbstractConnector
implements	RequestNotificationI
{
	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			notifyRequestTermination(RequestI r) throws Exception
	{
		((RequestNotificationI)this.offering).notifyRequestTermination(r) ;
	}
}
