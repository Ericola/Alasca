package fr.upmc.datacenter.requestdispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesI;
import fr.upmc.datacenter.hardware.processors.interfaces.ProcessorServicesNotificationI;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesNotificationInboundPort;
import fr.upmc.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.upmc.datacenter.software.applicationvm.interfaces.ApplicationVMManagementI;
import fr.upmc.datacenter.software.applicationvm.interfaces.TaskI;
import fr.upmc.datacenter.software.applicationvm.ports.ApplicationVMManagementInboundPort;
import fr.upmc.datacenter.software.interfaces.RequestI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestNotificationI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.upmc.datacenter.software.interfaces.RequestSubmissionI;
import fr.upmc.datacenter.software.ports.RequestNotificationInboundPort;
import fr.upmc.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.upmc.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDispatcher extends AbstractComponent
        implements RequestSubmissionHandlerI, RequestNotificationHandlerI {

    /** URI of this request dispatcher RD */
    protected String rdURI;

    /** Map between RD URIs and the VM outbound ports to call them. */
    protected RequestSubmissionInboundPort        rdsip;
    protected List<RequestSubmissionOutboundPort> rdsopList;

    protected RequestNotificationInboundPort  rdnip;
    /** Outbound port used by the RD to notify tasks' termination to the generator. */
    protected RequestNotificationOutboundPort rdnop;

    protected int current = 0;

    public RequestDispatcher( String rdURI , String rdsip , List<String> rdsop , String rdnop , String rdnip )
            throws Exception {
        super( true , false );

        // Preconditions
        assert rdURI != null;

        assert rdsip != null;
        assert rdsop != null;

        assert rdnip != null;
        assert rdnop != null;

        this.rdURI = rdURI;

        rdsopList = new ArrayList<>();

        // Interfaces
        this.addOfferedInterface( RequestSubmissionI.class );
        this.rdsip = new RequestSubmissionInboundPort( rdsip , this );
        this.addPort( this.rdsip );
        this.rdsip.publishPort();

        this.addRequiredInterface( RequestNotificationI.class );
        this.rdnip = new RequestNotificationInboundPort( rdnip , this );
        this.addPort( this.rdnip );
        this.rdnip.publishPort();

        for ( int i = 0 ; i < rdsop.size() ; i++ ) {
            this.addOfferedInterface( RequestSubmissionI.class );
            this.rdsopList.add( new RequestSubmissionOutboundPort( rdsop.get( i ) , this ) );
            this.addPort( this.rdsopList.get( i ) );
            this.rdsopList.get( i ).publishPort();
        }

        this.addRequiredInterface( RequestNotificationI.class );
        this.rdnop = new RequestNotificationOutboundPort( rdnop , this );
        this.addPort( this.rdnop );
        this.rdnop.publishPort();
    }

    @Override
    public void acceptRequestTerminationNotification( RequestI r ) throws Exception {
        assert r != null;
        this.logMessage(
                "Request dispatcher " + this.rdURI + "  notified the request " + r.getRequestURI() + " has ended." );
        this.rdnop.notifyRequestTermination( r );
    }

    @Override
    public void acceptRequestSubmission( RequestI r ) throws Exception {
        this.logMessage( this.rdURI + " submits request " + r.getRequestURI() );
        this.rdsopList.get( current ).submitRequest( r );
        current = ( current + 1 ) % rdsopList.size();

    }

    @Override
    public void acceptRequestSubmissionAndNotify( RequestI r ) throws Exception {
        this.logMessage( this.rdURI + " submits request " + r.getRequestURI() );
        this.rdsopList.get( current ).submitRequestAndNotify( r );
        current = ( current + 1 ) % rdsopList.size();
    }

    public void shutdown() throws ComponentShutdownException {
        // Disconnect ports to the request emitter and to the processors owning
        // the allocated cores.
        try {
            if ( this.rdnop.connected() ) {
                this.rdnop.doDisconnection();
            }
            for ( int i = 0 ; i < rdsopList.size() ; i++ )
                if ( this.rdsopList.get( i ).connected() ) {
                    this.rdsopList.get( i ).doDisconnection();
                }
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }

        try {
            rdnip.doDisconnection();
            rdsip.doDisconnection();
        }
        catch ( Exception e ) {
            throw new ComponentShutdownException( e );
        }

        super.shutdown();
    }
}
