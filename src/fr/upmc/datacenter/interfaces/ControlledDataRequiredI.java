package fr.upmc.datacenter.interfaces;

import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The interface <code>ControlledDataRequiredI</code> defines the data exchange
 * services required by a client component which can explicitly start and
 * stop the pushing of data.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface extends the standard <code>DataRequiredI</code> with its
 * methods to pull and push data.  Its pull interface also extends
 * <code>PushModeControllingI</code> to start and stop pushing of data in the
 * server to be used by the client to manage its notification reception periods.
 * 
 * <p>Created on : 1 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ControlledDataRequiredI
extends		DataRequiredI
{
	public interface	ControlledDataI
	extends		DataRequiredI.DataI
	{
		
	}

	public interface	ControlledPullI
	extends		DataRequiredI.PullI,
				PushModeControllingI
	{
		
	}
}
