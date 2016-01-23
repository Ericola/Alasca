package fr.upmc.datacenter.software.coordinators.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.hardware.processors.Processor;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;
import fr.upmc.datacenter.software.coordinators.ProcessorCoordinator;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;

public class ProcessorCoordinatorServicesInboundPort extends AbstractInboundPort
        implements ProcessorCoordinatorServicesI {

    private static final long serialVersionUID = 1L;

    public ProcessorCoordinatorServicesInboundPort(ComponentI owner) throws Exception {
        super(ControllerManagementI.class, owner);
    }

    public ProcessorCoordinatorServicesInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ControllerManagementI.class, owner);
    }

    @Override
    public void changeFrequenciesDemand(final int coreNo, final int f) throws Exception {
        final ProcessorCoordinator c = (ProcessorCoordinator) this.owner;

        this.owner.handleRequestAsync(new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                c.changeFrequenciesDemand(coreNo, f);
                return null;
            }

        });

    }

    @Override
    public void setFrequencies(final int coreNo, final int f) throws Exception {
        final Processor p = (Processor) this.owner;

        this.owner.handleRequestAsync(new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                p.setCoreFrequency(coreNo, f);
                return null;
            }

        });
        
    }
}
