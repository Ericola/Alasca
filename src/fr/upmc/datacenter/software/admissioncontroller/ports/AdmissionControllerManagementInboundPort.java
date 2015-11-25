package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;

public class AdmissionControllerManagementInboundPort extends AbstractInboundPort
        implements AdmissionControllerManagementI {

    private static final long serialVersionUID = 1L;

    public AdmissionControllerManagementInboundPort( ComponentI owner ) throws Exception {
        super( AdmissionControllerManagementI.class , owner );

        assert owner instanceof AdmissionControllerManagementI;
    }

    public AdmissionControllerManagementInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , AdmissionControllerManagementI.class , owner );

        assert uri != null && owner instanceof AdmissionControllerManagementI;
    }

    @Override
    public void freeUpVM() throws Exception {
        final AdmissionController ac = ( AdmissionController ) this.owner;
        this.owner.handleRequestAsync( new ComponentI.ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                ac.freeUpVM();
                return null;
            }
        } );
        
    }

}
