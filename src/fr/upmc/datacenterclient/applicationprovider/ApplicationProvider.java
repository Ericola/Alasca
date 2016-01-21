package fr.upmc.datacenterclient.applicationprovider;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationProviderManagementI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationProviderManagementInboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionOutboundPort;
import fr.upmc.datacenterclient.requestgenerator.RequestGenerator;
import fr.upmc.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.upmc.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

/**
 * The class <code>ApplicationProvider</code> implements the component representing an application
 * provider in the data center.
 * 
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * An application provider (AP) submits an application to an <code>AdmissionController</code>. If
 * that application is accepted then the AP receives the URI of a <code>RequestDispatcher</code> and
 * creates a <code>RequestGenerator</code> that send request to the <code>RequestDispatcher</code>.
 * 
 * Once the <code>RequestGenerator</code> is created, it notifies the admission controller with the
 * RequestNotificationInboundPort of the request generator. Thus the Admission controller can
 * complete the connection.
 * 
 * The application provider offers the interface <code>ApplicationProviderManagerI</code> through
 * the inbound port <code>ApplicationProviderManagementInboundPort</code> that allows to send and
 * stop applications.
 * 
 */
public class ApplicationProvider extends AbstractComponent {

    /** the URI of the component. */
    protected String apURI;

    // ------------------------------------------------------------------
    // PORTS
    // ------------------------------------------------------------------
    /** the outbound port used to send application to the admission controller. */
    protected ApplicationSubmissionOutboundPort asop;

    /** the outbound port used to start or stop the requestgenerator dynamically created */
    protected RequestGeneratorManagementOutboundPort rgmop;

    /** the outbound port to notify that the requestgenerator has been created */
    protected ApplicationNotificationOutboundPort anop;

    /** the inbound port used to send/stop application **/
    protected ApplicationProviderManagementInboundPort apmip;

    // ------------------------------------------------------------------
    // REQUEST GENERATOR URIs
    // ------------------------------------------------------------------
    /** RequestGenerator URI */
    protected String rgUri;

    /** Request generator management inbound port */
    protected String rgmipUri;

    /** Request submission outbound port */
    protected String rsopUri;

    /** Request notification inbound port */
    protected String rnipUri;

    /** Request generator management outbound port */
    protected String rgmopUri;

    // protected static int i = 0;

    /**
     * Create an application provider
     * 
     * @param applicationSubmissionOutboundPortURI URI of the application submission outbound port
     * @param applicationNotificationOutboundPortURI URI of the application notification outbound
     *            port
     * @param managementInboundPortURI URI of the application provider management inbound port
     * @throws Exception
     */
    public ApplicationProvider( String apURI , String applicationSubmissionOutboundPortURI ,
            String applicationNotificationOutboundPortURI , String managementInboundPortURI ) throws Exception {
        super( false , true );
        this.apURI = apURI;
        this.addRequiredInterface( ApplicationSubmissionI.class );
        this.asop = new ApplicationSubmissionOutboundPort( applicationSubmissionOutboundPortURI , this );
        this.addPort( asop );
        this.asop.localPublishPort();

        this.addRequiredInterface( ApplicationNotificationI.class );
        this.anop = new ApplicationNotificationOutboundPort( applicationNotificationOutboundPortURI , this );
        this.addPort( anop );
        this.anop.localPublishPort();

        this.addOfferedInterface( ApplicationProviderManagementI.class );
        this.apmip = new ApplicationProviderManagementInboundPort( managementInboundPortURI , this );
        this.addPort( this.apmip );
        this.apmip.publishPort();

        // Ports of the request generator
        rgUri = apURI + "-rg";
        rgmipUri = apURI + "-rgmip";
        rsopUri = apURI + "-rsop";
        rnipUri = apURI + "-rnip";
        rgmopUri = apURI + "-rgmop";

    }

    /**
     * Submit an application to the admission controller
     * 
     * @throws Exception
     */
    public void sendApplication() throws Exception {
        print( "Submit an application" );
        print( "Waiting for URI" );
        String res[] = this.asop.submitApplication( 2 );
        String requestDispatcherURI = res[0];

        print( "URI received" );
        if ( requestDispatcherURI != null ) {

            // Creation dynamique du request generator
            print( "creating RequestGenerator" );
            RequestGenerator rg = new RequestGenerator( rgUri , 500.0 , 6000000000L , rgmipUri , rsopUri , rnipUri );
            AbstractCVM.theCVM.addDeployedComponent( rg );
            RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rg.findPortFromURI( rsopUri );
            rsop.doConnection( requestDispatcherURI , RequestSubmissionConnector.class.getCanonicalName() );

            rg.toggleTracing();
            rg.toggleLogging();

            rgmop = new RequestGeneratorManagementOutboundPort( rgmopUri , this );
            rgmop.publishPort();
            rgmop.doConnection( rgmipUri , RequestGeneratorManagementConnector.class.getCanonicalName() );

            String rdnopUri = res[1];
            print( "Notify requestGenerator created" );
            anop.notifyRequestGeneratorCreated( rnipUri , rdnopUri );

            rg.start();

            rg.startGeneration();

        }
        else
            print( "Pas de resources disponibles" );
    }

    /**
     * Stop the application, it means it stops the requestgenerator
     * 
     * @throws Exception
     */
    public void stopApplication() throws Exception {
        rgmop.stopGeneration();

    }

    private void print( String s ) {
        this.logMessage( "[ApplicationProvider " + apURI + "] " + s );
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
            if ( this.asop.connected() ) {
                this.asop.doDisconnection();
            }
            if ( this.rgmop.connected() ) {
                this.rgmop.doDisconnection();
            }
            if ( this.anop.connected() ) {
                this.anop.doDisconnection();
            }
      
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }
        super.shutdown();
    }
}
