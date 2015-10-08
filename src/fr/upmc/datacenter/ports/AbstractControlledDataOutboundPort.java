package fr.upmc.datacenter.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.components.ports.AbstractDataOutboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataRequiredI;
import fr.upmc.datacenter.interfaces.PushModeControllingI;

/**
 * The class <code>AbstractControlledDataOutboundPort</code>
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
public abstract class	AbstractControlledDataOutboundPort
extends		AbstractDataOutboundPort
implements	ControlledDataRequiredI.ControlledPullI
{
	private static final long serialVersionUID = 1L;

	public				AbstractControlledDataOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ControlledDataRequiredI.ControlledPullI.class,
											DataRequiredI.PushI.class, owner) ;
	}

	public				AbstractControlledDataOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ControlledDataRequiredI.ControlledPullI.class,
											DataRequiredI.PushI.class, owner) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void			startUnlimitedPushing(int interval)
	throws Exception
	{
		((PushModeControllingI)this.connector).startUnlimitedPushing(interval) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void			startLimitedPushing(int interval, int n)
	throws Exception
	{
		((PushModeControllingI)this.connector).startLimitedPushing(interval, n) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void			stopPushing() throws Exception
	{
		((PushModeControllingI)this.connector).stopPushing() ;
	}
}
