package fr.upmc.datacenter.software.requestdispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherVMEndingNotificationI;

public class RequestDispatcherVMEndingNotificationConnector extends AbstractConnector 
	implements RequestDispatcherVMEndingNotificationI {

	@Override
	public void notifyAdmissionControllerVMEnd(
			String RequestSubmissionInboundPortURI) throws Exception {
		( (RequestDispatcherVMEndingNotificationI) this.offering).notifyAdmissionControllerVMEnd(RequestSubmissionInboundPortURI);
	}
	
	
}
