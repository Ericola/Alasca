package fr.upmc.datacenter.software.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface RingNetworkI extends OfferedI,
RequiredI {

	public void sendVM(String vmURI, String requestSubmissionInboundPortURI) throws Exception;
	
}
