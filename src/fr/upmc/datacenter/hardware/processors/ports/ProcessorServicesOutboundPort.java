package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesOutboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 19 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorServicesOutboundPort
extends		AbstractOutboundPort
implements	ProcessorServicesI
{
	public				ProcessorServicesOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorServicesI.class, owner) ;

		assert	uri != null ;
	}

	public				ProcessorServicesOutboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ProcessorServicesI.class, owner) ;

		assert	uri != null ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCore(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI, int)
	 */
	@Override
	public void			executeTaskOnCore(final TaskI t, final int coreNo)
	throws Exception
	{
		assert	t != null && coreNo >= 0 ;

		((ProcessorServicesI)this.connector).executeTaskOnCore(t, coreNo) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI#executeTaskOnCoreAndNotify(fr.upmc.datacenter.software.applicationvm.interfaces.TaskI, int, java.lang.String)
	 */
	@Override
	public void executeTaskOnCoreAndNotify(
		final TaskI t,
		final int coreNo,
		final String notificationPortURI
		) throws Exception
	{
		((ProcessorServicesI)this.connector).
				executeTaskOnCoreAndNotify(t, coreNo, notificationPortURI) ;
	}
}
