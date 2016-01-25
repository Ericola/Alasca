package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AdmissionControllerManagementI extends OfferedI, RequiredI {

    public boolean addCores(String rdURI, int nbCores ) throws Exception; 
    
}
