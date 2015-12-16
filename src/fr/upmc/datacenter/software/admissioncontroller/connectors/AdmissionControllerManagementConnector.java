package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

public class AdmissionControllerManagementConnector extends AbstractConnector
        implements AdmissionControllerManagementI {

    @Override
    public void freeUpVM() throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).freeUpVM();

    }

    @Override
    public void allocateVM( String RequestDispatcherURI ) throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).allocateVM( RequestDispatcherURI );

    }

    @Override
    public void removeVM( String RequestDispatcherURI ) throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).removeVM( RequestDispatcherURI );

    }

    @Override
    public boolean addCores( String rdURI , int nbCores ) throws Exception {
        return ( ( AdmissionControllerManagementI ) this.offering ).addCores( rdURI , nbCores );
    }

    @Override
    public void setFrequency( Integer f) throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).setFrequency( f );

    }

}
