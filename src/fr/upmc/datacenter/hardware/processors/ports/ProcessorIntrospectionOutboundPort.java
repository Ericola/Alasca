package fr.upmc.datacenter.hardware.processors.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionOutboundPort</code>
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 29 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorIntrospectionOutboundPort
extends		AbstractOutboundPort
implements	ProcessorIntrospectionI
{
	public				ProcessorIntrospectionOutboundPort(
		ComponentI owner
		) throws Exception
	{
		super(ProcessorIntrospectionI.class, owner);
	}

	public				ProcessorIntrospectionOutboundPort(
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
		return ((ProcessorIntrospectionI)this.connector).getNumberOfCores() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int			getDefaultFrequency() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getDefaultFrequency() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int			getMaxFrequencyGap() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getMaxFrequencyGap() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean		isValidCoreNo(final int coreNo) throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).isValidCoreNo(coreNo) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean		isAdmissibleFrequency(final int frequency) throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).
											isAdmissibleFrequency(frequency) ;
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
		return ((ProcessorIntrospectionI)this.connector).
						isCurrentlyPossibleFrequencyForCore(coreNo, frequency) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI getStaticState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getStaticState() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.connector).getDynamicState() ;
	}
}
