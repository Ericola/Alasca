package fr.upmc.datacenter.software.controller.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ControllerManagementI extends OfferedI, RequiredI{
	
	public void notifyVMEndingItsRequests(String[] VmURis) throws Exception;

}
