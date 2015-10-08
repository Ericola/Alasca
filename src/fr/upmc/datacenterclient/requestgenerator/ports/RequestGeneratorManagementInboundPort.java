package fr.upmc.datacenterclient.requestgenerator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementInboundPort</code> implements the
 * inbound port through which the component management methods are called.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 5 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			RequestGeneratorManagementInboundPort
extends		AbstractInboundPort
implements	RequestGeneratorManagementI
{
	private static final long serialVersionUID = 1L ;

	public				RequestGeneratorManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestGeneratorManagementI.class, owner) ;

		assert	owner != null && owner instanceof RequestGenerator ;
	}

	public				RequestGeneratorManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestGeneratorManagementI.class, owner);

		assert	owner != null && owner instanceof RequestGenerator ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void			startGeneration() throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rg.startGeneration() ;
							return null;
						}
					}) ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void			stopGeneration() throws Exception
	{
		final RequestGenerator rg = (RequestGenerator) this.owner ;
		this.owner.handleRequestAsync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							rg.stopGeneration() ;
							return null;
						}
					}) ;
	}
}
