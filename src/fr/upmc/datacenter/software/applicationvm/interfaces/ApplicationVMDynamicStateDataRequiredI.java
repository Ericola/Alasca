package fr.upmc.datacenter.software.applicationvm.interfaces;

import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;

/**
 * The interface <code>ApplicationVMDynamicStateDataRequiredI</code> defines the
 * dynamic state notification services required from <code>ApplicationVM</code>
 * components.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface extends the standard <code>DataRequiredI</code> with its
 * methods to pull and push data.  Its pull interface also extends
 * <code>PushModeControllingI</code> to start and stop pushing of data in the
 * server to be used by the client to manage its notification reception periods.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 1 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ApplicationVMDynamicStateDataRequiredI
extends		DataRequiredI
{
	// The data interface is defined as an external interface
	// ApplicationVMDynamicStateI

	public interface	PullI
	extends		DataRequiredI.PullI,
				PushModeControllingI
	{
		
	}
}
