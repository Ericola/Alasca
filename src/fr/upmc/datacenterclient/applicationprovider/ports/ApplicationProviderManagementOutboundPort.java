package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementOutboundPort extends AbstractOutboundPort
        implements ApplicationProviderManagementI {

    public ApplicationProviderManagementOutboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationProviderManagementI.class , owner );
    }

    @Override
    public void sendApplication() throws Exception {
        ( ( ApplicationProviderManagementI ) this.connector ).sendApplication();;

    }

    @Override
    public void stopApplication() throws Exception {
        ( ( ApplicationProviderManagementI ) this.connector ).stopApplication();;
    }

}
