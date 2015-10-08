package fr.upmc.datacenter.software.applicationvm.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;

/**
 * The interface <code>ApplicationVMDynamicStateDataOfferedI</code> defines the
 * dynamic state notification services offered by <code>ApplicationVM</code>
 * components.
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
public interface		ApplicationVMDynamicStateDataOfferedI
extends		DataOfferedI
{
	// The data interface is defined as an external interface
	// ApplicationVMDynamicStateI

	public interface	PullI
	extends		DataOfferedI.PullI,
				PushModeControllingI
	{
		
	}
}
