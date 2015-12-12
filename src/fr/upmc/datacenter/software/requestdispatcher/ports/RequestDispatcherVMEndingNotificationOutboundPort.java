package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherVMEndingNotificationI;

public class RequestDispatcherVMEndingNotificationOutboundPort extends AbstractOutboundPort
	implements RequestDispatcherVMEndingNotificationI{

	public RequestDispatcherVMEndingNotificationOutboundPort( ComponentI owner ) throws Exception {
        super( RequestDispatcherVMEndingNotificationI.class , owner );
    }

    public RequestDispatcherVMEndingNotificationOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , RequestDispatcherVMEndingNotificationI.class , owner );
    }

	@Override
	public void notifyAdmissionControllerVMEnd(
			String RequestSubmissionInboundPortURI) throws Exception {
		( ( RequestDispatcherVMEndingNotificationI ) this.connector ).notifyAdmissionControllerVMEnd(RequestSubmissionInboundPortURI);
		
	}
}
