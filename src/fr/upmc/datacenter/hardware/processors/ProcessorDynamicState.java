package fr.upmc.datacenter.hardware.processors;

import java.net.InetAddress;

import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;

/**
 * The class <code>ProcessorDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a processor component to be pulled or
 * pushed through the dynamic state data interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	timestamp >= 0 && timestamperIP != null
 * invariant	coresIdleStatus != null && currentCoreFrequencies != null
 * </pre>
 * 
 * <p>Created on : 7 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorDynamicState
implements	ProcessorDynamicStateI
{
	// ------------------------------------------------------------------------
	// Instance variables and constants
	// ------------------------------------------------------------------------

	private static final long	serialVersionUID = 1L ;
	/** timestamp in Unix time format, local time of the timestamper.		*/
	protected final long		timestamp ;
	/** IP of the node that did the timestamping.							*/
	protected final String		timestamperIP ;
	/** execution status of the processor cores.							*/
	protected final boolean[]	coresIdleStatus ;
	/** current frequencies of the processor cores.							*/
	protected final int[]		currentCoreFrequencies ;

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------

	/**
	 * create a snapshot of the dynamic state of a processor component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	coresIdleStatus != null && currentCoreFrequencies != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param coresIdleStatus			execution status of the processor cores.
	 * @param currentCoreFrequencies	current frequencies of the processor cores.
	 * @throws Exception
	 */
	public				ProcessorDynamicState(
		boolean[] coresIdleStatus,
		int[] currentCoreFrequencies
		) throws Exception
	{
		super() ;

		assert	coresIdleStatus != null && currentCoreFrequencies != null ;

		this.timestamp = System.currentTimeMillis() ;
		this.timestamperIP = InetAddress.getLocalHost().getHostAddress() ;
		this.coresIdleStatus = new boolean[currentCoreFrequencies.length] ;
		this.currentCoreFrequencies = new int[currentCoreFrequencies.length] ;
		for (int i = 0 ; i < currentCoreFrequencies.length ; i++) {
			this.coresIdleStatus[i] = coresIdleStatus[i] ;
			this.currentCoreFrequencies[i] = currentCoreFrequencies[i] ;
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getTimeStamp()
	 */
	@Override
	public long			getTimeStamp()
	{
		return this.timestamp ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getTimeStamperId()
	 */
	@Override
	public String		getTimeStamperId()
	{
		return this.timestamperIP ;
	}

	@Override
	public boolean[]	getCoresIdleStatus()
	{
		return this.coresIdleStatus ;
	}

	@Override
	public boolean	getCoreIdleStatus(int coreNo)
	{
		return this.coresIdleStatus[coreNo] ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequencies()
	 */
	@Override
	public int[]		getCurrentCoreFrequencies() {
		return this.currentCoreFrequencies ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI#getCurrentCoreFrequency(int)
	 */
	@Override
	public int			getCurrentCoreFrequency(int coreNo)
	{
		return this.currentCoreFrequencies[coreNo] ;
	}
}
