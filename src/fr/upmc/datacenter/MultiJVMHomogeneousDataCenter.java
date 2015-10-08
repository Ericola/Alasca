package fr.upmc.datacenter;

import fr.upmc.components.cvm.AbstractDistributedCVM;

/**
 * The class <code>MultiJVMDataCenter</code> .
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : August 26, 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			MultiJVMHomogeneousDataCenter
extends		AbstractDistributedCVM
{

	public				MultiJVMHomogeneousDataCenter(
		String[] args
		) throws Exception
	{
		super(args);
	}

}
