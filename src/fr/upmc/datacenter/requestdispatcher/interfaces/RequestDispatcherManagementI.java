package fr.upmc.datacenter.requestdispatcher.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface RequestDispatcherManagementI extends OfferedI, RequiredI {

    public boolean isWaitingForTermination() throws Exception;
}
