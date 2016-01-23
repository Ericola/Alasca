package fr.upmc.datacenter.software.coordinators.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;

public class ProcessorCoordinatorServicesConnector extends AbstractConnector implements ProcessorCoordinatorServicesI {


    @Override
    public void changeFrequenciesDemand(int coreNo, int f) throws Exception {
        ( ( ProcessorCoordinatorServicesI ) this.offering ).changeFrequenciesDemand(coreNo, f );
        
    }

    @Override
    public void setFrequencies(int coreNo, int f) throws Exception {
        ( ( Processor ) this.offering ).setCoreFrequency(coreNo, f );        
    }
}
