package fr.upmc.datacenter.software.controller.interfaces;

import java.util.List;
import java.util.Map;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ControllerManagementI extends OfferedI, RequiredI{
	
	public void notifyVMEndingItsRequests(String[] VmURis) throws Exception;

	public void attachCoordinator(Map<String, List<Integer>> processorCores) throws Exception;
}
