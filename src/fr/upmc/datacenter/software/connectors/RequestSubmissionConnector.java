package fr.upmc.datacenter.software.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>TaskHandlerServicesConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>RequestSubmissionI</code>.
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
public class			RequestSubmissionConnector
extends		AbstractConnector
implements	RequestSubmissionI
{
	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			submitRequest(RequestI r) throws Exception
	{
		((RequestSubmissionI)this.offering).submitRequest(r);
	}

	@Override
	public void			submitRequestAndNotify(RequestI r)
	throws Exception
	{
		((RequestSubmissionI)this.offering).submitRequestAndNotify(r) ;
	}
}
