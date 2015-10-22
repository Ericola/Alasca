package fr.upmc.datacenterclient.applicationprovider.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationRequestI extends OfferedI, RequiredI {
    
    public void submitApplication(int nbVM);

}
