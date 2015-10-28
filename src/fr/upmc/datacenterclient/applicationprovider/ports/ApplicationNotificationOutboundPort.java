package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;

public class ApplicationNotificationOutboundPort extends AbstractOutboundPort implements ApplicationNotificationI {

    public ApplicationNotificationOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI , int i ) throws Exception {
        ( ( ApplicationNotificationI ) this.connector )
                .notifyRequestGeneratorCreated( requestNotificationInboundPortURI , i );
    }

}
