package fr.upmc.datacenter.software.coordinators.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ProcessorCoordinatorServicesI extends OfferedI, RequiredI{
    
    public void setFrequencies(int coreNo, int f) throws Exception;
    
    public void changeFrequenciesDemand(int coreNo, int f) throws Exception;

}
