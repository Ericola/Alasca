package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestNotificationOutboundPort</code> implements the
 * inbound port requiring the interface <code>RequestNotificationI</code>.
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
public class			RequestNotificationOutboundPort
extends		AbstractOutboundPort
implements	RequestNotificationI
{
	public				RequestNotificationOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestNotificationI.class, owner);
	}

	public				RequestNotificationOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestNotificationI.class, owner) ;

		assert	uri != null ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			notifyRequestTermination(RequestI r) throws Exception
	{
		((RequestNotificationI)this.connector).notifyRequestTermination(r) ;
	}
}
