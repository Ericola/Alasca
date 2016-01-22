package fr.upmc.datacenter.software.controller.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenter.software.controller.interfaces.ControllerManagementI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;

public class ControllerManagementConnector extends AbstractConnector implements ControllerManagementI {

	@Override
	public void notifyVMEndingItsRequests(String[] VmURis) throws Exception {
		( ( ControllerManagementI ) this.offering ).notifyVMEndingItsRequests( VmURis );
	}
}
