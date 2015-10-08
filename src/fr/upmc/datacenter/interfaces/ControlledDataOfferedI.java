package fr.upmc.datacenter.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;

/**
 * The interface <code>ControlledDataOfferedI</code> defines the data exchange
 * services offered by a server component where the pushing of data can be
 * explicitly started and stopped by the client component.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface extends the standard <code>DataOfferedI</code> with its methods
 * to pull and push data.  Its pull interface also extends
 * <code>PushModeControllingI</code> to start and stop pushing of data in the
 * server to be used by the client to manage its notification reception periods.
 * 
 * <p>Created on : 1 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ControlledDataOfferedI
extends		DataOfferedI
{
	public interface	ControlledDataI
	extends		DataOfferedI.DataI
	{
		
	}

	public interface	ControlledPullI
	extends		DataOfferedI.PullI,
				PushModeControllingI
	{
		
	}
}
