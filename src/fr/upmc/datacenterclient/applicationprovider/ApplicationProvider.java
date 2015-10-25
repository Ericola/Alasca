package fr.upmc.datacenterclient.applicationprovider;

import fr.upmc.components.AbstractComponent;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;

public class ApplicationProvider extends AbstractComponent {

    public static final String RequestGeneratorManagementInboundPortURI  = "rgmip";
    public static final String RequestGeneratorManagementOutboundPortURI = "rgmop";
    public static final String RequestSubmissionOutboundPortURI          = "rsobp";
    public static final String RequestNotificationInboundPortURI         = "rnibp";

    /** the URI of the component. */
    protected String apURI;

    /** the output port used to send application to the admission controller. */
    protected ApplicationSubmissionOutboundPort asop;

    private int cpt = 0;

    public ApplicationProvider( String apURI , String applicationSubmissionOutboundPortURI ) throws Exception {
        this.apURI = apURI;
        this.addRequiredInterface( ApplicationSubmissionI.class );
        this.asop = new ApplicationSubmissionOutboundPort( applicationSubmissionOutboundPortURI , this );
        this.addPort( asop );
        this.asop.localPublishPort();
    }

    public void sendApplication() throws Exception {
        String requestDispatcherURI = this.asop.submitApplication( 1 );

        if ( requestDispatcherURI != null ) {
            // Creation dynamique du request generator
            RequestGenerator rg = new RequestGenerator( "rg" + cpt , 500.0 , 6000000000L ,
                    RequestGeneratorManagementInboundPortURI + cpt, RequestSubmissionOutboundPortURI + cpt,
                    RequestNotificationInboundPortURI + cpt);

            
            


        }
        else
            System.out.println( "Pas de resources disponible" );
    }

}
