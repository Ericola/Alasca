package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.admissioncontroller.AdmissionController;
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
    public void notifyRequestGeneratorCreated(final String requestNotificationInboundPortURI,final String rdnop ) throws Exception {
        final AdmissionController ac = ( AdmissionController ) this.owner;

        this.owner.handleRequestSync( new ComponentService<Void>() {

            @Override
            public Void call() throws Exception {
                ac.notifyRequestGeneratorCreated(requestNotificationInboundPortURI, rdnop );
                return null;

            }
        } );
    }

}
