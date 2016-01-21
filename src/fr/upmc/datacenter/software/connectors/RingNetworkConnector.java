package fr.upmc.datacenter.software.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.interfaces.RingNetworkI;

public class RingNetworkConnector extends AbstractConnector implements RingNetworkI{

	@Override
	public void sendVM(String vmURI, String requestSubmissionInboundPortURI) throws Exception {
		((RingNetworkI)(this.offering)).sendVM(vmURI, requestSubmissionInboundPortURI);
		
	}

}
