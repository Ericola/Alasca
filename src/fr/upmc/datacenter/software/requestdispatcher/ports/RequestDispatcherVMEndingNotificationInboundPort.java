package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherVMEndingNotificationI;

public class RequestDispatcherVMEndingNotificationInboundPort extends AbstractInboundPort
implements RequestDispatcherVMEndingNotificationI{

	private static final long serialVersionUID = 1L;

    public RequestDispatcherVMEndingNotificationInboundPort( ComponentI owner ) throws Exception {
        super( RequestDispatcherVMEndingNotificationI.class , owner );

        assert owner instanceof RequestDispatcherVMEndingNotificationI;
    }

    public RequestDispatcherVMEndingNotificationInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , RequestDispatcherVMEndingNotificationI.class , owner );

        assert uri != null && owner instanceof RequestDispatcherVMEndingNotificationI;
    }

	@Override
	public void notifyAdmissionControllerVMFinishRequest(
		 final String RequestSubmissionInboundPortURI) throws Exception {
		
		final AdmissionController arh = ( AdmissionController ) this.owner;
		
		this.owner.handleRequestAsync(
				new ComponentI.ComponentService<Void>() {
					@Override
					public Void call() throws Exception {
						arh.notifyAdmissionControllerVMFinishRequest(RequestSubmissionInboundPortURI);
						return null ;
					}
				}) ;
		
	}
}
