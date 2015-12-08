package fr.upmc.datacenter.software.requestdispatcher.interfaces;

import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.interfaces.TimeStampingI;

public interface RequestDispatcherDynamicStateI extends DataOfferedI.DataI, DataRequiredI.DataI, TimeStampingI {

    public String getRequestDispatcherURI();

    public double getRequestProcessingAvg();
    
}
