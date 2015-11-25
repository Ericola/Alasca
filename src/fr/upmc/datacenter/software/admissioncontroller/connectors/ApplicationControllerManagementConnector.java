package fr.upmc.datacenter.software.admissioncontroller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

public class ApplicationControllerManagementConnector extends AbstractConnector
        implements AdmissionControllerManagementI {

    @Override
    public void freeUpVM() throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).freeUpVM();

    }
}
