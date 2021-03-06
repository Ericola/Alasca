package fr.upmc.datacenter.software.requestdispatcher.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface RequestDispatcherManagementI extends OfferedI, RequiredI {

    public boolean isWaitingForTermination() throws Exception;
    
    public void connectVm(String vmURI, String RequestSubmissionInboundPortURI) throws Exception;
    
    public void disconnectVm() throws Exception;
    
    public String getMostBusyVMURI() throws Exception;
}
