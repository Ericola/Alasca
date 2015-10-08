package fr.upmc.datacenter.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import fr.upmc.datacenter.interfaces.ControlledDataOfferedI;
import fr.upmc.datacenter.interfaces.PushModeControllerI;

/**
 * The class <code>ControlledDataInboundPort</code>
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
public abstract class	AbstractControlledDataInboundPort
extends		AbstractDataInboundPort
implements	ControlledDataOfferedI.ControlledPullI
{
	private static final long serialVersionUID = 1L;

	public				AbstractControlledDataInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ControlledDataOfferedI.ControlledPullI.class,
											DataOfferedI.PushI.class, owner) ;

		assert	owner instanceof PushModeControllerI ;
	}

	public				AbstractControlledDataInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ControlledDataOfferedI.ControlledPullI.class,
											DataOfferedI.PushI.class, owner) ;

		assert	owner instanceof PushModeControllerI ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void			startUnlimitedPushing(final int interval) 
	throws Exception
	{
		final PushModeControllerI pmc = (PushModeControllerI) this.owner ;
		this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							pmc.startUnlimitedPushing(interval) ;
							return null;
						}
					}) ;
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void			startLimitedPushing(final int interval, final int n)
	throws Exception
	{
		final PushModeControllerI pmc = (PushModeControllerI) this.owner ;
		this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							pmc.startLimitedPushing(interval, n) ;
							return null;
						}
					}) ;	
	}

	/**
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void			stopPushing() throws Exception
	{
		final PushModeControllerI pmc = (PushModeControllerI) this.owner ;
		this.owner.handleRequestSync(
					new ComponentI.ComponentService<Void>() {
						@Override
						public Void call() throws Exception {
							pmc.stopPushing() ;
							return null;
						}
					}) ;
	}
}
