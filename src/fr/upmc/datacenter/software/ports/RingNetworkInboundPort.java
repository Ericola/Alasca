package fr.upmc.datacenter.software.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.interfaces.RingNetworkI;

public class RingNetworkInboundPort extends AbstractInboundPort implements RingNetworkI{

	private static final long serialVersionUID = 1L;

	public	RingNetworkInboundPort(ComponentI owner) throws Exception{
		super(RingNetworkI.class, owner);
	}

	public	RingNetworkInboundPort(String uri, ComponentI owner) throws Exception{
		super(uri, RingNetworkI.class, owner);
	}

	@Override
	public void sendVM(final String vmURI, final String requestSubmissionInboundPortURI) throws Exception {
		final RingNetworkI controller = (RingNetworkI) this.owner;
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						controller.sendVM(vmURI, requestSubmissionInboundPortURI);
						return null ;
					}					
				}) ;

	}

}
