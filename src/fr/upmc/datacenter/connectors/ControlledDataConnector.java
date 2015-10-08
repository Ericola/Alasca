package fr.upmc.datacenter.connectors;

import fr.upmc.components.connectors.DataConnector;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>ControlledDataConnector</code>
 *
 * <p><strong>Description</strong></p>
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
public class			ControlledDataConnector
extends		DataConnector
implements	ControlledDataRequiredI.ControlledPullI
{

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void			startUnlimitedPushing(int interval)
	throws Exception
	{
		((PushModeControllingI)this.offering).startUnlimitedPushing(interval) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void			startLimitedPushing(int interval, int n)
	throws Exception
	{
		((PushModeControllingI)this.offering).startLimitedPushing(interval, n) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void			stopPushing() throws Exception
	{
		((PushModeControllingI)this.offering).stopPushing() ;
	}
}
