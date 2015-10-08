package fr.upmc.datacenter.software.interfaces;

/**
 * The class <code>RequestSubmissionHandlerI</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 4 mai 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public interface		RequestSubmissionHandlerI
{
	public void			acceptRequestSubmission(final RequestI r)
	throws Exception ;

	public void			acceptRequestSubmissionAndNotify(
		final RequestI r
		) throws Exception ;
}
