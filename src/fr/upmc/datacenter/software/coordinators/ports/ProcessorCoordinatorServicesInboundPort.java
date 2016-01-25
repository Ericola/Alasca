package fr.upmc.datacenter.software.coordinators.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.controller.Controller;
import fr.upmc.datacenter.software.coordinators.CoordinatorDecision;
import fr.upmc.datacenter.software.coordinators.ProcessorCoordinator;
import fr.upmc.datacenter.software.coordinators.interfaces.ProcessorCoordinatorServicesI;

public class ProcessorCoordinatorServicesInboundPort extends AbstractInboundPort
        implements ProcessorCoordinatorServicesI {

    private static final long serialVersionUID = 1L;

    public ProcessorCoordinatorServicesInboundPort(ComponentI owner) throws Exception {
        super(ProcessorCoordinatorServicesI.class, owner);
    }

    public ProcessorCoordinatorServicesInboundPort(String uri, ComponentI owner) throws Exception {
        super(uri, ProcessorCoordinatorServicesI.class, owner);
    }

    @Override
    public void setCoordinatorDecision(final CoordinatorDecision flag) throws Exception {
        final Controller c = (Controller) this.owner;

        this.owner.handleRequestSync(new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                c.setCoordinatorDecision(flag);
                return null;
            }

        });

    }

    @Override
    public boolean frequencyDemand(final String uri, final int coreNo, final int f) throws Exception {
        final ProcessorCoordinator pc = (ProcessorCoordinator) this.owner;

        return this.owner.handleRequestSync(new ComponentService<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return pc.frequencyDemand(uri, coreNo, f);
            }
        });
    }

    @Override
    public void attachController(final String controllerInboundPortURI) throws Exception {
        final ProcessorCoordinator pc = (ProcessorCoordinator) this.owner;

         this.owner.handleRequestSync(new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                 pc.attachController(controllerInboundPortURI);
                return null;
            }
        });
        
    }
}
