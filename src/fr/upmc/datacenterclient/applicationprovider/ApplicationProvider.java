package fr.upmc.datacenterclient.applicationprovider;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class ApplicationProvider extends AbstractComponent {

    /** the URI of the component. */
    protected String apURI;

    /** the output port used to send application to the admission controller. */
    protected ApplicationSubmissionOutboundPort      asop;
    protected RequestGeneratorManagementOutboundPort rgmop;
    protected ApplicationNotificationOutboundPort    anop;

    public ApplicationProvider( String apURI , String applicationSubmissionOutboundPortURI ,
            String applicationNotificationOutboundPortURI ) throws Exception {
        super( false , true );
        this.apURI = apURI;
        this.addRequiredInterface( ApplicationSubmissionI.class );
        this.asop = new ApplicationSubmissionOutboundPort( applicationSubmissionOutboundPortURI , this );
        this.addPort( asop );
        this.asop.localPublishPort();

        this.anop = new ApplicationNotificationOutboundPort( applicationNotificationOutboundPortURI , this );
        this.addPort( anop );
        this.anop.localPublishPort();

    }

    public void sendApplication() throws Exception {
        this.logMessage( this.apURI + " Begin sendApplication" );
        String requestDispatcherURI = this.asop.submitApplication( 1 );
        System.out.println( requestDispatcherURI );
        if ( requestDispatcherURI != null ) {
            // Creation dynamique du request generator
            RequestGenerator rg = new RequestGenerator( "rg" , 500.0 , 6000000000L , "rgmip" , "rsop" , "rnip" );
            AbstractCVM.theCVM.addDeployedComponent( rg );
            RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rg.findPortFromURI( "rsop" );
            rsop.doConnection( requestDispatcherURI , RequestSubmissionConnector.class.getCanonicalName() );

            rg.toggleTracing();
            rg.toggleLogging();

            rgmop = new RequestGeneratorManagementOutboundPort( "rgmop" , this );
            rgmop.publishPort();
            rgmop.doConnection( "rgmip" , RequestGeneratorManagementConnector.class.getCanonicalName() );

            int cpt = Integer.parseInt( requestDispatcherURI.substring( 5 , requestDispatcherURI.length() ) );
            System.out.println( "ApplicationProvider --> notifying requestGenerator Created" );
            anop.notifyRequestGeneratorCreated( "rnip" , cpt );
            rg.startGeneration();
            this.logMessage( this.apURI + " Finish sendApplication" );
        }
        else
            this.logMessage( this.apURI + " Pas de ressources disponibles" );
    }

    public void stopApplication() throws Exception {
        rgmop.stopGeneration();
    }

}
