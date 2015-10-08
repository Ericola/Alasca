package fr.upmc.datacenter.software.applicationvm.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>ApplicationVMDynamicStateDataI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 sept. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ApplicationVMDynamicStateDataI
extends		DataOfferedI,
			DataRequiredI,
			PushModeControllingI
{

}
