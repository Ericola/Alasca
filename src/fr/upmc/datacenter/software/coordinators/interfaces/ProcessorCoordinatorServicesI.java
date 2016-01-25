package fr.upmc.datacenter.software.coordinators.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;
import fr.upmc.datacenter.software.coordinators.CoordinatorDecision;

public interface ProcessorCoordinatorServicesI extends OfferedI, RequiredI{
    
    public void setCoordinatorDecision(CoordinatorDecision flag) throws Exception;
    
    public boolean frequencyDemand(String controllerURI, int coreNo, int f) throws Exception;
    
    public void attachController(String controllerInboundPortURI)throws Exception;

}
