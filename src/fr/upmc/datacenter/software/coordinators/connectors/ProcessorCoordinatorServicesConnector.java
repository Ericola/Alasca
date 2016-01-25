package fr.upmc.datacenter.software.coordinators.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.coordinators.CoordinatorDecision;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;

public class ProcessorCoordinatorServicesConnector extends AbstractConnector implements ProcessorCoordinatorServicesI {

    @Override
    public void setCoordinatorDecision(CoordinatorDecision flag) throws Exception {
         ( ( ProcessorCoordinatorServicesI ) this.offering ).setCoordinatorDecision(flag);
    }

    @Override
    public boolean frequencyDemand(String controllerURI, int coreNo, int f) throws Exception {
        return ( ( ProcessorCoordinatorServicesI ) this.offering ).frequencyDemand(controllerURI, coreNo, f );

    }

    @Override
    public void attachController(String controllerInboundPortURI) throws Exception {
         ( ( ProcessorCoordinatorServicesI ) this.offering ).attachController(controllerInboundPortURI);
        
    }
}
