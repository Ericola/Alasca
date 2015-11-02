package fr.upmc.datacenterclient.applicationprovider.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementConnector extends AbstractConnector
        implements ApplicationProviderManagementI {

    @Override
    public void sendApplication() throws Exception {
        ( ( ApplicationProviderManagementI ) this.offering ).sendApplication();
    }

}
