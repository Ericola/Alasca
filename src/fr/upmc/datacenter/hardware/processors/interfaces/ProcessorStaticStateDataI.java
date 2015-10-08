package fr.upmc.datacenter.hardware.processors.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;

/**
 * The interface <code>ProcessorStaticStateDataI</code> defines the static
 * state notification services for <code>Processor</code> components.
 *
 * <p><strong>Description</strong></p>
 * 
 * The interface extends the standard <code>DataOfferedI</code> and
 * <code>DataRequiredI</code> with their methods to pull and push data.
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 7 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		ProcessorStaticStateDataI
extends		DataOfferedI,
			DataRequiredI
{
	// The data interface is defined as an external interface
	// ProcessorStaticStateI
}
