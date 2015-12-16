package fr.upmc.datacenter.software.admissioncontroller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface AdmissionControllerManagementI extends OfferedI, RequiredI {

    public void freeUpVM() throws Exception;

    public boolean addCores(String rdURI, int nbCores ) throws Exception;

    public void allocateVM( String RequestDispatcherURI ) throws Exception;

    public void removeVM( String RequestDispatcherURI ) throws Exception;

    public void setFrequency( Integer f ) throws Exception;
    

    
    
}
