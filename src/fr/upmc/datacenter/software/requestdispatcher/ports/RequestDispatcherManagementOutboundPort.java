package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort
implements RequestDispatcherManagementI {

	public RequestDispatcherManagementOutboundPort( ComponentI owner ) throws Exception {
		super( RequestDispatcherManagementI.class , owner );
	}

	public RequestDispatcherManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
		super( uri , RequestDispatcherManagementI.class , owner );
	}

	@Override
	public boolean isWaitingForTermination() throws Exception {
		return ( ( RequestDispatcherManagementI ) this.connector ).isWaitingForTermination();

	}

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception {
		( ( RequestDispatcherManagementI ) this.connector ).connectVm(vmURI, RequestSubmissionInboundPortURI);

	}

	@Override
	public void disconnectVm() throws Exception {
		( ( RequestDispatcherManagementI ) this.connector ).disconnectVm();

	}

	@Override
	public String getMostBusyVMURI() throws Exception {
		return ( ( RequestDispatcherManagementI ) this.connector ).getMostBusyVMURI();
	}
}
