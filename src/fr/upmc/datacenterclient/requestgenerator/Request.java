package fr.upmc.datacenterclient.requestgenerator;

import fr.upmc.datacenter.software.interfaces.RequestI;

/**
 * The class <code>Request</code> implements the interface <code>RequestI</code>
 * to provide request objects for the test.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 5 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			Request
implements	RequestI
{
	private static final long serialVersionUID = 1L ;
	protected final long	numberOfInstructions ;
	protected final String	requestURI ;

	public			Request(long numberOfInstructions)
	{
		super() ;
		this.numberOfInstructions = numberOfInstructions ;
		this.requestURI = java.util.UUID.randomUUID().toString() ;
	}

	public			Request(
		String uri,
		long numberOfInstructions
		)
	{
		super() ;
		this.numberOfInstructions = numberOfInstructions ;
		this.requestURI = uri ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestI#getRequestURI()
	 */
	@Override
	public String	getRequestURI()
	{
		return this.requestURI ;
	}

	/**
	 * @see fr.upmc.datacenter.software.interfaces.RequestI#getPredictedNumberOfInstructions()
	 */
	@Override
	public long		getPredictedNumberOfInstructions()
	{
		return this.numberOfInstructions ;
	}
}
