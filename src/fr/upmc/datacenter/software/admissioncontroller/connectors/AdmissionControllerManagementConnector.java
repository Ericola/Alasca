package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

public class AdmissionControllerManagementConnector extends AbstractConnector
        implements AdmissionControllerManagementI {

    @Override
    public boolean addCores( String rdURI , int nbCores ) throws Exception {
        return ( ( AdmissionControllerManagementI ) this.offering ).addCores( rdURI , nbCores );
    }

    @Override
    public void setFrequency( Integer f) throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).setFrequency( f );

    }

}
