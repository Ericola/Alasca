package fr.upmc.datacenter.software.applicationvm.interfaces;

import java.io.Serializable;

import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The interface <code>TaskI</code> is used to define tasks exchanged between
 * Application VM components and Processor components to execute requests.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 19 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		TaskI
extends		Serializable
{
	/**
	 * return the request to be executed as a task.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the request to be executed as a task.
	 */
	public RequestI		getRequest() ;

	/**
	 * return the URI of the task.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the URI of the task.
	 */
	public String		getTaskURI() ;
}
