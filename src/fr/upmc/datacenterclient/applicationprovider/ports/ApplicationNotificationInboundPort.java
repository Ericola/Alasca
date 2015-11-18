package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.controller.AdmissionController;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;

public class ApplicationNotificationInboundPort extends AbstractInboundPort implements ApplicationNotificationI {

    private static final long serialVersionUID = 1L;

    public ApplicationNotificationInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationNotificationI.class , owner );

    }

    public ApplicationNotificationInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationNotificationI.class , owner );
    }

    @Override
    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI, String rdnop ) throws Exception {
        final AdmissionController ac = ( AdmissionController ) this.owner;
        ac.notifyRequestGeneratorCreated( requestNotificationInboundPortURI, rdnop );

//        this.owner.handleRequestSync( new ComponentService<String>() {
//
//            @Override
//            public String call() throws Exception {
//                ac.notifyRequestGeneratorCreated( requestNotificationInboundPortURI, i );
//
//                return null;
//
//            }
//        } );
    }

}
