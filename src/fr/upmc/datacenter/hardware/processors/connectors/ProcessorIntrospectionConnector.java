package fr.upmc.datacenter.hardware.processors.connectors ;

import fr.upmc.components.connectors.AbstractConnector ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI ;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;

/**
 * The class <code>ProcessorIntrospectionConnector</code> implements the
 * connector between outbound and inboud ports implementing the interface
 * <code>ProcessorIntrospectionI</code>.
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
public class			ProcessorIntrospectionConnector
extends		AbstractConnector
implements	ProcessorIntrospectionI
{
	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getNumberOfCores()
	 */
	@Override
	public int			getNumberOfCores() throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).getNumberOfCores() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDefaultFrequency()
	 */
	@Override
	public int			getDefaultFrequency() throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).getDefaultFrequency() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getMaxFrequencyGap()
	 */
	@Override
	public int			getMaxFrequencyGap() throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).getMaxFrequencyGap() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isValidCoreNo(int)
	 */
	@Override
	public boolean		isValidCoreNo(final int coreNo) throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).isValidCoreNo(coreNo) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#isAdmissibleFrequency(int)
	 */
	@Override
	public boolean		isAdmissibleFrequency(final int frequency) throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).
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
		return ((ProcessorIntrospectionI)this.offering).
						isCurrentlyPossibleFrequencyForCore(coreNo, frequency) ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getStaticState()
	 */
	@Override
	public ProcessorStaticStateI getStaticState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).getStaticState() ;
	}

	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorIntrospectionI#getDynamicState()
	 */
	@Override
	public ProcessorDynamicStateI getDynamicState() throws Exception
	{
		return ((ProcessorIntrospectionI)this.offering).getDynamicState() ;
	}
}
