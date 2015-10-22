package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationRequestI;

public class ApplicationRequestInboundPort extends AbstractInboundPort implements ApplicationRequestI {

    private static final long serialVersionUID = 1L;

    public ApplicationRequestInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationRequestI.class , owner );

    }

    public ApplicationRequestInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationRequestI.class , owner );

    }

    @Override
    public void submitApplication( int nbVM ) {
        // TODO Auto-generated method stub

    }

}
