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
    public void freeUpVM() throws Exception {
        ( ( AdmissionControllerManagementI ) this.connector ).freeUpVM();;

    }

    @Override
    public void allocateVM( String RequestDispatcherURI ) throws Exception {
        ( ( AdmissionControllerManagementI ) this.connector ).allocateVM( RequestDispatcherURI );

    }

    @Override
    public void removeVM( String RequestDispatcherURI ) throws Exception {
        ( ( AdmissionControllerManagementI ) this.connector ).removeVM( RequestDispatcherURI );

    }

    @Override
    public boolean addCores( int nbCores ) throws Exception {
        return ( ( AdmissionControllerManagementI ) this.connector ).addCores( nbCores );
    }

    @Override
    public void increaseFrequency() throws Exception {
        ( ( AdmissionControllerManagementI ) this.connector ).increaseFrequency();

    }
}
