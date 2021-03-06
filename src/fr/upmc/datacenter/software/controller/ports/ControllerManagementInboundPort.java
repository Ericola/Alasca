package fr.upmc.datacenter.software.controller.ports;

import java.util.List;
import java.util.Map;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.controller.Controller;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;

public class ControllerManagementInboundPort extends AbstractInboundPort
implements ControllerManagementI {

	private static final long serialVersionUID = 1L;

	public ControllerManagementInboundPort(ComponentI owner) throws Exception {
		super(ControllerManagementI.class, owner);
	}

	public ControllerManagementInboundPort( String uri , ComponentI owner ) throws Exception {
		super( uri , ControllerManagementI.class , owner );
	}

	@Override
	public void notifyVMEndingItsRequests(final String[] VmURis) throws Exception{
		final Controller c = ( Controller ) this.owner;

		this.owner.handleRequestAsync( new ComponentService<Void>() {

			@Override
			public Void call() throws Exception {
				c.notifyVMEndingItsRequests(VmURis);
				return null;
			}

		});


	}

    @Override
    public void attachCoordinator(final Map<String, List<Integer>> processorCores) throws Exception {
        final Controller c = ( Controller ) this.owner;

        this.owner.handleRequestAsync( new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                c.attachCoordinator(processorCores);
                return null;
            }

        });
        
    }
}
