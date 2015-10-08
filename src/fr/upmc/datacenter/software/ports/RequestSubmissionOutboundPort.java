package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>RequestSubmissionOutboundPort</code> implements the
 * inbound port requiring the interface <code>RequestSubmissionI</code>.
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
public class			RequestSubmissionOutboundPort
extends		AbstractOutboundPort
implements	RequestSubmissionI
{
	public				RequestSubmissionOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestSubmissionI.class, owner);
	}

	public				RequestSubmissionOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestSubmissionI.class, owner) ;

		assert	uri != null ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			submitRequest(final RequestI r) throws Exception
	{
		((RequestSubmissionI)this.connector).submitRequest(r) ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionI#submitRequestAndNotify(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			submitRequestAndNotify(RequestI r)
	throws Exception
	{
		((RequestSubmissionI)this.connector).submitRequestAndNotify(r) ;
	}
}
