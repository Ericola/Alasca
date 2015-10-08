package fr.upmc.datacenter.interfaces;

/**
 * The interface <code>PushModeControllerI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 1 oct. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		PushModeControllerI
{
	/**
	 * start the pushing of data and force the pushing to be done each
	 * <code>interval</code> period of time. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	interval > 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval		delay between pushes (in milliseconds).
	 * @throws Exception
	 */
	public void			startUnlimitedPushing(final int interval)
	throws Exception ;

	/**
	 * start <code>n>/code> pushing of data and force the pushing to be done
	 * each <code>interval</code> period of time. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	interval > 0 && n > 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param interval		delay between pushes (in milliseconds).
	 * @param n				total number of pushes to be done, unless stopped.
	 * @throws Exception
	 */
	public void			startLimitedPushing(final int interval, final int n)
	throws Exception ;

	/**
	 * stop the pushing of data.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception
	 */
	public void			stopPushing() throws Exception ;

}
