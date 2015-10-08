package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;

/**
 * The class <code>RequestSubmissionInboundPort</code> implements the
 * inbound port offering the interface <code>RequestSubmissionI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	uri != null && owner instanceof RequestSubmissionHandlerI
 * </pre>
 * 
 * <p>Created on : 9 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RequestSubmissionInboundPort
extends		AbstractInboundPort
implements	RequestSubmissionI
{
	private static final long serialVersionUID = 1L;

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	owner instanceof RequestSubmissionHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				RequestSubmissionInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestSubmissionI.class, owner) ;

		assert	owner instanceof RequestSubmissionHandlerI ;
	}

	/**
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	uri != null && owner instanceof RequestSubmissionHandlerI
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param uri			uri of the port.
	 * @param owner			owner component.
	 * @throws Exception
	 */
	public				RequestSubmissionInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestSubmissionI.class, owner) ;

		assert	uri != null && owner instanceof RequestSubmissionHandlerI ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionI#submitRequest(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void			submitRequest(final RequestI r) throws Exception
	{
		final RequestSubmissionHandlerI rh =
										(RequestSubmissionHandlerI) this.owner ;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						rh.acceptRequestSubmission(r) ;
						return null ;
					}					
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestSubmissionI#submitRequestAndNotify(fr.upmc.datacenter.software.interfaces.RequestI)
	 */
	@Override
	public void submitRequestAndNotify(final RequestI r)
	throws Exception
	{
		final RequestSubmissionHandlerI rh =
									(RequestSubmissionHandlerI) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rh.acceptRequestSubmissionAndNotify(r) ;
							return null ;
						}
					}) ;
	}
}
