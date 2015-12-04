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
	public String connectVm() throws Exception {
		return ( ( RequestDispatcherManagementI ) this.offering ).connectVm();
	}
}
