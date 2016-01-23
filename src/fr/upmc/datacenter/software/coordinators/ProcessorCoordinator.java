package fr.upmc.datacenter.software.coordinators;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorManagementI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.upmc.datacenter.software.coordinators.connectors.ProcessorCoordinatorServicesConnector;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;
import fr.upmc.datacenter.software.coordinators.ports.ProcessorCoordinatorServicesInboundPort;

public class ProcessorCoordinator extends AbstractComponent {

    protected String pcURI;

    // Processor
    protected ProcessorManagementOutboundPort pmop;

    protected ProcessorCoordinatorServicesInboundPort pcsip;

    public ProcessorCoordinator(String uri, String pmipURI) throws Exception {
        super(true, true);
        this.pcURI = uri;

        pmop = new ProcessorManagementOutboundPort(pcURI + "pmop", this);
        this.addRequiredInterface(ProcessorManagementI.class);
        this.addPort(pmop);
        pmop.publishPort();
        pmop.doConnection(pmipURI, ProcessorCoordinatorServicesConnector.class.getCanonicalName());

        this.addOfferedInterface(ProcessorCoordinatorServicesI.class);
        this.pcsip = new ProcessorCoordinatorServicesInboundPort(pcURI + "pcsip", this);
        this.addPort(pcsip);
        this.pcsip.publishPort();
    }

    public void changeFrequenciesDemand(int coreNo, int f) throws Exception {

        pmop.setCoreFrequency(coreNo, f);
    }

}
