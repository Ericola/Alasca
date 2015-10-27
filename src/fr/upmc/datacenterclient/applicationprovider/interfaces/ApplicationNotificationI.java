package fr.upmc.datacenterclient.applicationprovider.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationNotificationI extends OfferedI, RequiredI {

    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI ) throws Exception;

}
