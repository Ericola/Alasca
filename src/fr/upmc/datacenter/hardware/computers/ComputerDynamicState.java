package fr.upmc.datacenter.hardware.computers;

import java.net.InetAddress;
import fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;

/**
 * The class <code>ComputerDynamicState</code> implements objects representing
 * a snapshot of the dynamic state of a computer component to be pulled or
 * pushed through the dynamic state data interface.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * TODO: complete!
 * 
 * <pre>
 * invariant	timestamp >= 0 && timestamperIP != null
 * invariant	computerURI != null && reservedCores != null
 * </pre>
 * 
 * <p>Created on : 23 avr. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ComputerDynamicState
implements	ComputerDynamicStateI
{
	private static final long	serialVersionUID = 1L;
	/** timestamp in Unix time format, local time of the timestamper.		*/
	protected final long		timestamp ;
	/** IP of the node that did the timestamping.							*/
	protected final String		timestamperIP ;
	/** URI of the computer to which this dynamic state relates.			*/
	protected final String		computerURI ;
	/** reservation status of the cores of all computer's processors.		*/
	protected final boolean[][]	reservedCores ;

	/**
	 * create a dynamic state object.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * TODO: complete!
	 * 
	 * <pre>
	 * pre	computerURI != null
	 * pre	reservedCores != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param computerURI	URI of the computer to which this dynamic state relates.
	 * @param reservedCores	reservation status of the cores of all computer's processors.
	 * @throws Exception
	 */
	public				ComputerDynamicState(
		String computerURI,
		boolean[][] reservedCores
		) throws Exception
	{
		super() ;
		this.timestamp = System.currentTimeMillis() ;
		this.timestamperIP = InetAddress.getLocalHost().getHostAddress() ;
		this.computerURI = computerURI ;
		this.reservedCores =
					new boolean[reservedCores.length][reservedCores[0].length] ;
		for (int p = 0 ; p < reservedCores.length ; p++) {
			for (int c = 0 ; c < reservedCores[0].length ; c++) {
				this.reservedCores[p][c] = reservedCores[p][c] ;
			}
		}
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getTimeStamp()
	 */
	@Override
	public long			getTimeStamp()
	{
		return this.timestamp ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getTimeStamperId()
	 */
	@Override
	public String		getTimeStamperId()
	{
		return new String(this.timestamperIP) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getComputerURI()
	 */
	@Override
	public String		getComputerURI()
	{
		return new String(this.computerURI) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.computers.interfaces.ComputerDynamicStateI#getCurrentCoreReservations()
	 */
	@Override
	public boolean[][]	getCurrentCoreReservations()
	{
		// copy not to provide direct access to internal data structures.
		boolean[][] ret =
				new boolean[this.reservedCores.length][
				                                this.reservedCores[0].length] ;
		for (int i = 0 ; i < this.reservedCores.length ; i++) {
			for (int j = 0 ; j < this.reservedCores[i].length ; j++) {
				ret[i][j] = this.reservedCores[i][j] ;
			}
		}
		return ret ;
	}
}
