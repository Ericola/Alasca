package fr.upmc.datacenter.software.admissioncontroller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.admissioncontroller.interfaces.AdmissionControllerManagementI;

public class AdmissionControllerManagementOutboundPort extends AbstractOutboundPort
        implements AdmissionControllerManagementI {

    public AdmissionControllerManagementOutboundPort( ComponentI owner ) throws Exception {
        super( AdmissionControllerManagementI.class , owner );
    }

    public AdmissionControllerManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , AdmissionControllerManagementI.class , owner );
    }

    @Override
    public boolean addCores( String rdURI, int nbCores ) throws Exception {
        return ( ( AdmissionControllerManagementI ) this.connector ).addCores( rdURI, nbCores );
    }
    
}
