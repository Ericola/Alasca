package fr.upmc.datacenter.software.coordinators.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.coordinators.CoordinatorDecision;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;

public class ProcessorCoordinatorServicesOutboundPort extends AbstractOutboundPort
implements ProcessorCoordinatorServicesI {

    public ProcessorCoordinatorServicesOutboundPort( ComponentI owner ) throws Exception {
        super( ProcessorCoordinatorServicesI.class , owner );
    }

    public ProcessorCoordinatorServicesOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ProcessorCoordinatorServicesI.class , owner );
    }

    @Override
    public void setCoordinatorDecision(CoordinatorDecision flag) throws Exception {
        ( ( ProcessorCoordinatorServicesI ) this.connector ).setCoordinatorDecision(flag);
        
    }

    @Override
    public boolean frequencyDemand(String controllerURI, int coreNo, int f) throws Exception {
        return ( ( ProcessorCoordinatorServicesI ) this.connector ).frequencyDemand(controllerURI, coreNo, f);
    }

    @Override
    public void attachController(String controllerInboundPortURI) throws Exception {
        ( ( ProcessorCoordinatorServicesI ) this.connector ).attachController(controllerInboundPortURI);
        
    }
}

