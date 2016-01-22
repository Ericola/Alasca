package fr.upmc.datacenter.software.controller.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class ControllerManagementOutboundPort extends AbstractOutboundPort
implements ControllerManagementI {

	public ControllerManagementOutboundPort( ComponentI owner ) throws Exception {
		super( ControllerManagementI.class , owner );
	}

	public ControllerManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
		super( uri , ControllerManagementI.class , owner );
	}

	@Override
	public void notifyVMEndingItsRequests(String[] VmURis) throws Exception {
		( ( ControllerManagementI ) this.connector ).notifyVMEndingItsRequests(VmURis);

	}
}
