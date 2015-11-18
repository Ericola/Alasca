package fr.upmc.datacenterclient.applicationprovider.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.software.controller.AdmissionController;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionInboundPort extends AbstractInboundPort implements ApplicationSubmissionI {

    private static final long serialVersionUID = 1L;

    public ApplicationSubmissionInboundPort( ComponentI owner ) throws Exception {
        super( ApplicationSubmissionI.class , owner );

    }

    public ApplicationSubmissionInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , ApplicationSubmissionI.class , owner );

    }

    @Override
    public String[] submitApplication( final int nbVM ) throws Exception {
        final AdmissionController arh = ( AdmissionController ) this.owner;

        return this.owner.handleRequestSync( new ComponentService<String[]>() {

            @Override
            public String[] call() throws Exception {
                return arh.submitApplication( nbVM );
            }
        } );
    }

}
