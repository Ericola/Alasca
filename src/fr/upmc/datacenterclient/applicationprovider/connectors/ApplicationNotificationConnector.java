package fr.upmc.datacenterclient.applicationprovider.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;

public class ApplicationNotificationConnector extends AbstractConnector implements ApplicationNotificationI {

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , int i ) throws Exception {
        ( ( ApplicationNotificationI ) this.offering ).notifyRequestGeneratorCreated( requestNotificationInboundPortURI , i );
    }



}
