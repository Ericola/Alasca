package fr.upmc.datacenter.software.controller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.controller.interfaces.AdmissionControllerManagementI;

public class ApplicationControllerManagementConnector extends AbstractConnector
        implements AdmissionControllerManagementI {

    @Override
    public void freeUpVM() throws Exception {
        ( ( AdmissionControllerManagementI ) this.offering ).freeUpVM();

    }
}
