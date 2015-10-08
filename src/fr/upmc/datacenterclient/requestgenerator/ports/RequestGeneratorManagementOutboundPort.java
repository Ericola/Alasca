package fr.upmc.datacenterclient.requestgenerator.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementOutboundPort</code> implements the
 * outbound port through which one calls the component management methods.
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
public class			RequestGeneratorManagementOutboundPort
extends		AbstractOutboundPort
implements	RequestGeneratorManagementI
{
	public				RequestGeneratorManagementOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestGeneratorManagementI.class, owner) ;

		assert	owner != null ;
	}

	public				RequestGeneratorManagementOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestGeneratorManagementI.class, owner) ;

		assert	uri != null && owner != null ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void			startGeneration() throws Exception
	{
		((RequestGeneratorManagementI)this.connector).startGeneration() ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void			stopGeneration() throws Exception
	{
		((RequestGeneratorManagementI)this.connector).stopGeneration() ;
	}
}
