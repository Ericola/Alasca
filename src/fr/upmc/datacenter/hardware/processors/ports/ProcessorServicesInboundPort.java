package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;

/**
 * The class <code>ProcessorServicesInboundPort</code>
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
public class			ProcessorServicesInboundPort
extends		AbstractInboundPort
implements	ProcessorServicesI
{
	private static final long serialVersionUID = 1L;

	public				ProcessorServicesInboundPort(
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
		final Processor p = (Processor) this.owner ;
		p.handleRequest(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						p.executeTaskOnCore(t, coreNo) ;
						return null;
					}
				}) ;
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
		final Processor p = (Processor) this.owner ;
		p.handleRequest(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						p.executeTaskOnCoreAndNotify(
											t, coreNo, notificationPortURI) ;
						return null;
					}
				}) ;
	}
}
