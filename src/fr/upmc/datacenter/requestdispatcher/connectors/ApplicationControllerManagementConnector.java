package fr.upmc.datacenter.requestdispatcher.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class ApplicationControllerManagementConnector extends AbstractConnector
        implements RequestDispatcherManagementI {

    @Override
    public boolean isWaitingForTermination() throws Exception {
        return ( ( RequestDispatcherManagementI ) this.offering ).isWaitingForTermination();

    }
}
