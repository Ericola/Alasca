package fr.upmc.datacenter.software.controller;

import java.util.ArrayList;
import java.util.List;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.cvm.AbstractCVM;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.upmc.datacenter.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.software.applicationvm.ApplicationVM;
import fr.upmc.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.upmc.datacenter.software.connectors.RequestNotificationConnector;
import fr.upmc.datacenter.software.connectors.RequestSubmissionConnector;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationNotificationI;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationNotificationInboundPort;
import fr.upmc.datacenterclient.applicationprovider.ports.ApplicationSubmissionInboundPort;

public class AdmissionController extends AbstractComponent {

    /** the URI of the component. */
    protected String apURI;

    protected ApplicationSubmissionInboundPort asip;

    protected ApplicationNotificationInboundPort anip;

    protected ApplicationVMManagementOutboundPort avmop;

    protected ComputerServicesOutboundPort csop;

    private int cpt = 0;

    private RequestDispatcher rd;

    public AdmissionController( String apURI , String applicationSubmissionInboundPortURI ,
            String applicationNotificationInboundPortURI , String computerServiceOutboundPortURI ) throws Exception {
        super( false , true );
        this.apURI = apURI;
        this.addRequiredInterface( ApplicationSubmissionI.class );
        this.asip = new ApplicationSubmissionInboundPort( applicationSubmissionInboundPortURI , this );
        this.addPort( asip );
        this.asip.localPublishPort();

        this.addRequiredInterface( ApplicationNotificationI.class );
        this.anip = new ApplicationNotificationInboundPort( applicationNotificationInboundPortURI , this );
        this.addPort( anip );
        this.anip.localPublishPort();

        this.csop = new ComputerServicesOutboundPort( computerServiceOutboundPortURI , this );
        this.addPort( csop );
        this.csop.localPublishPort();
    }

    public String submitApplication( int nbVm ) throws Exception {
        // Verifier que des resources sont disponible

        // Création d'une VM
        ApplicationVM vm = new ApplicationVM( createURI( "vm" ) , createURI( "avmip" ) , createURI( "rsip" ) ,
                createURI( "rnop" ) );
        AbstractCVM.theCVM.addDeployedComponent( vm );

        avmop = new ApplicationVMManagementOutboundPort( createURI( "avmop" ) , new AbstractComponent() {} );
        avmop.publishPort();
        avmop.doConnection( createURI( "avmip" ) , ApplicationVMManagementConnector.class.getCanonicalName() );

        // AllocateCore des computer aux VMs
        AllocatedCore[] ac = this.csop.allocateCores( 4 );
        this.avmop.allocateCores( ac );

        // Création d'un requestdispatcher
        List<String> rdsop = new ArrayList<>();
        rdsop.add( createURI( "rdsop" ) );
        rd = new RequestDispatcher( createURI( "rd" ) , createURI( "rdsip" ) , rdsop , createURI( "rdnop" ) ,
                createURI( "rdnip" ) );
        AbstractCVM.theCVM.addDeployedComponent( rd );

        // Connect RD with VM
        RequestSubmissionOutboundPort rsop = ( RequestSubmissionOutboundPort ) rd.findPortFromURI( rdsop.get( 0 ) );
        rsop.doConnection( createURI( "rsip" ) , RequestSubmissionConnector.class.getCanonicalName() );
        String res = createURI( "rdsip" );

        cpt++;
        return res;
    }

    public void notifyRequestGeneratorCreated( String requestNotificationInboundPortURI ) throws Exception {
        RequestNotificationOutboundPort rdnop = ( RequestNotificationOutboundPort ) rd.findPortFromURI( createURI( "rdnop" ) );
        rdnop.doConnection( requestNotificationInboundPortURI , RequestNotificationConnector.class.getCanonicalName() );
    }

    private String createURI( String uri ) {
        return uri + cpt;
    }

}
