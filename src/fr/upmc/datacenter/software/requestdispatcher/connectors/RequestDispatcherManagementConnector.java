package fr.upmc.datacenter.software.requestdispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementConnector extends AbstractConnector
        implements RequestDispatcherManagementI {

    @Override
    public boolean isWaitingForTermination() throws Exception {
        return ( ( RequestDispatcherManagementI ) this.offering ).isWaitingForTermination();

    }

	@Override
	public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception {
		( ( RequestDispatcherManagementI ) this.offering ).connectVm(vmURI, RequestSubmissionInboundPortURI);
	}

	@Override
	public void disconnectVm() throws Exception {
		( ( RequestDispatcherManagementI ) this.offering ).disconnectVm();
		
	}

    @Override
    public String getMostBusyVMURI() throws Exception {
        return ( ( RequestDispatcherManagementI ) this.offering ).getMostBusyVMURI();
    }
}
