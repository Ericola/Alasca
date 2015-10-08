package fr.upmc.datacenter.hardware.processors;

/**
 * The class <code>UnacceptableFrequencyException</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 15 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			UnacceptableFrequencyException
extends		Exception
{
	private static final long	serialVersionUID = 1L ;
	protected int				frequency ;

	public				UnacceptableFrequencyException(
		int frequency
		)
	{
		super() ;
		this.frequency = frequency ;
	}

	public UnacceptableFrequencyException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public UnacceptableFrequencyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UnacceptableFrequencyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UnacceptableFrequencyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
}
