package fr.upmc.datacenter.hardware.processors.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.upmc.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;

/**
 * The class <code>ProcessorManagementConnector</code> implements a
 * connector for ports exchanging through the interface
 * <code>ProcessorManagementI</code>.
 *
 * <p><strong>Description</strong></p>
 * 
 * <p><strong>Invariant</strong></p>
 * 
 * <pre>
 * invariant	true
 * </pre>
 * 
 * <p>Created on : 30 janv. 2015</p>
 * 
 * @author	<a href="mailto:Jacques.Malenfant@lip6.fr">Jacques Malenfant</a>
 * @version	$Name$ -- $Revision$ -- $Date$
 */
public class			ProcessorManagementConnector
extends		AbstractConnector
implements	ProcessorManagementI
{
	/**
	 * @see fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI#setCoreFrequency(int, int)
	 */
	@Override
	public void			setCoreFrequency(final int coreNo, final int frequency)
	throws	UnavailableFrequencyException,
			UnacceptableFrequencyException,
			Exception
	{
		if (AbstractCVM.DEBUG) {
			System.out.println(
					"ProcessorManagementConnector>>setCoreFrequency(" +
					coreNo + ", " + frequency + ")") ;
		}

		((ProcessorManagementI)this.offering).
										setCoreFrequency(coreNo, frequency) ;
	}
}
