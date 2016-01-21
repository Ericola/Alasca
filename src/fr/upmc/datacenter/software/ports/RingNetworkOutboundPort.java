package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.interfaces.RingNetworkI;

public class RingNetworkOutboundPort extends AbstractOutboundPort implements RingNetworkI{

	public RingNetworkOutboundPort(ComponentI owner) throws Exception {
		super(RingNetworkI.class, owner);
	}

	public	RingNetworkOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RingNetworkI.class, owner) ;
		assert	uri != null ;
	}

	@Override
	public void sendVM(String vmURI, String requestSubmissionInboundPortURI) throws Exception {
		((RingNetworkI)this.connector).sendVM(vmURI, requestSubmissionInboundPortURI);
		
	}
}
