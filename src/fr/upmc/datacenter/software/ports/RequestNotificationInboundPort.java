package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;

/**
 * The class <code>RequestNotificationInboundPort</code> implements the
 * inbound port offering the interface <code>RequestNotificationI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	uri != null && owner instanceof RequestNotificationHandlerI
 * </pre>
 * 
 * <p>Created on : 9 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RequestNotificationInboundPort
extends		AbstractInboundPort
implements	RequestNotificationI
{
	private static final long serialVersionUID = 1L;

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof RequestNotificationHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				RequestNotificationInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestNotificationI.class, owner) ;

		assert	owner instanceof RequestNotificationHandlerI ;
		assert	uri != null ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner instanceof RequestNotificationHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				RequestNotificationInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestNotificationI.class, owner) ;

		assert	uri != null && owner instanceof RequestNotificationHandlerI ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestNotificationI#notifyRequestTermination(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			notifyRequestTermination(final RequestI r)
	throws Exception
	{
		final RequestNotificationHandlerI rnh =
									(RequestNotificationHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						rnh.acceptRequestTerminationNotification(r) ;
						return null;
					}
				}) ;
	}
}
