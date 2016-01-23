package fr.upmc.datacenter.software.coordinators.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
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
    public void changeFrequenciesDemand (int coreNo, int f) throws Exception {
        ( ( ProcessorCoordinatorServicesI ) this.connector ).changeFrequenciesDemand(coreNo, f);

    }

    @Override
    public void setFrequencies(int coreNo, int f) throws Exception {
        ( ( Processor ) this.connector ).setCoreFrequency(coreNo, f);
    }
}

