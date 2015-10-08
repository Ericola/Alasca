package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionInboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 28 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorIntrospectionInboundPort
extends		AbstractInboundPort
implements	ProcessorIntrospectionI
{
	private static final long serialVersionUID = 1L;

	public				ProcessorIntrospectionInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorIntrospectionI.class, owner) ;
	}

	public				ProcessorIntrospectionInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, ProcessorIntrospectionI.class, owner);
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getNumberOfCores()
	 */
	@Override
	public int			getNumberOfCores() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return p.getNumberOfCores() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int			getDefaultFrequency() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return p.getDefaultFrequency() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int			getMaxFrequencyGap() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return p.getMaxFrequencyGap() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean		isValidCoreNo(final int coreNo) throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return p.isValidCoreNo(coreNo) ;
						}			
					}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean		isAdmissibleFrequency(final int frequency)
	throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
					new ComponentI.ComponentService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return p.isAdmissibleFrequency(frequency) ;
						}
					});
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isCurrentlyPossibleFrequencyForCore(int, int)
	 */
	@Override
	public boolean		isCurrentlyPossibleFrequencyForCore(
		final int coreNo,
		final int frequency
		) throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return p.isCurrentlyPossibleFrequencyForCore(
														coreNo, frequency) ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI	getStaticState() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<ProcessorStaticStateI>() {
					@Override
					public ProcessorStaticStateI call() throws Exception {
						return p.getStaticState() ;
					}
				}) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception
	{
		final Processor p = (Processor) this.owner ;
		return p.handleRequestSync(
				new ComponentI.ComponentService<ProcessorDynamicStateI>() {
					@Override
					public ProcessorDynamicStateI call() throws Exception {
						return p.getDynamicState() ;
					}
				}) ;
	}
}
