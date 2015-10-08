package fr.upmc.datacenterclient.requestgenerator.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

/**
 * The class <code>RequestGeneratorManagementConnector</code> implements a
 * standard client/server connector for the management request generator
 * management interface.
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
public class			RequestGeneratorManagementConnector
extends		AbstractConnector
implements	RequestGeneratorManagementI
{
	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#startGeneration()
	 */
	@Override
	public void			startGeneration() throws Exception
	{
		((RequestGeneratorManagementI)this.offering).startGeneration() ;
	}

	/**
	 * @see fr.upmc.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI#stopGeneration()
	 */
	@Override
	public void			stopGeneration() throws Exception
	{
		((RequestGeneratorManagementI)this.offering).stopGeneration() ;
	}
}
