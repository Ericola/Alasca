package fr.upmc.datacenter.software.applicationvm.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.ports.AbstractDataInboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateDataI;

/**
 * The class <code>ApplicationVMDynamicStateDataInboundPort</code>
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
public class			ApplicationVMDynamicStateDataInboundPort
extends		AbstractDataInboundPort
implements	ApplicationVMDynamicStateDataI
{
	private static final long serialVersionUID = 1L;

	public ApplicationVMDynamicStateDataInboundPort(
			Class<?> implementedPullInterface,
			Class<?> implementedPushInterface, ComponentI owner)
			throws Exception {
		super(DataOfferedI.PullI.class, DataOfferedI.PushI.class, owner);
		// TODO Auto-generated constructor stub
	}

	public ApplicationVMDynamicStateDataInboundPort(String uri,
			Class<?> implementedPullInterface,
			Class<?> implementedPushInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedPullInterface, implementedPushInterface, owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startUnlimitedPushing(int)
	 */
	@Override
	public void startUnlimitedPushing(int interval) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#startLimitedPushing(int, int)
	 */
	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.datacenter.interfaces.PushModeControllingI#stopPushing()
	 */
	@Override
	public void stopPushing() throws Exception {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
	 */
	@Override
	public DataOfferedI.DataI	get() throws Exception
	{
		return null;
	}
}
