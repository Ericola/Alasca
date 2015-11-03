package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.applicationprovider.ApplicationProvider;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;

public class ApplicationProviderManagementInboundPort extends AbstractInboundPort
        implements ApplicationProviderManagementI {

    private static final long serialVersionUID = 1L;

    public ApplicationProviderManagementInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationProviderManagementI.class , owner );

    }

    public ApplicationProviderManagementInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationProviderManagementI.class , owner );
    }

    @Override
    public void sendApplication() throws Exception {
        final ApplicationProvider ap = ( ApplicationProvider ) this.owner;

        this.owner.handleRequestAsync( new ComponentService<String>() {

            @Override
            public String call() throws Exception {
                ap.sendApplication();
                return null;

            }
        } );

    }

    @Override
    public void stopApplication() throws Exception {
        final ApplicationProvider ap = ( ApplicationProvider ) this.owner;

        this.owner.handleRequestAsync( new ComponentService<String>() {

            @Override
            public String call() throws Exception {
                ap.stopApplication();
                return null;

            }
        } );

    }

}
