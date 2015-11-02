package fr.upmc.datacenter.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.ComponentI.ComponentService;
import fr.upmc.components.ports.AbstractInboundPort;
import fr.upmc.datacenter.requestdispatcher.RequestDispatcher;
import fr.upmc.datacenter.requestdispatcher.interfaces.RequestDispatcherManagementI;

public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
        implements RequestDispatcherManagementI {

    private static final long serialVersionUID = 1L;

    public RequestDispatcherManagementInboundPort( ComponentI owner ) throws Exception {
        super( RequestDispatcherManagementI.class , owner );

        assert owner instanceof RequestDispatcherManagementI;
    }

    public RequestDispatcherManagementInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , RequestDispatcherManagementI.class , owner );

        assert uri != null && owner instanceof RequestDispatcherManagementI;
    }

    @Override
    public boolean isWaitingForTermination() throws Exception {
        final RequestDispatcher rd = ( RequestDispatcher ) this.owner;

        return this.owner.handleRequestSync( new ComponentService<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                return rd.isWaitingForTermination();
            }
        } );
    }

}
