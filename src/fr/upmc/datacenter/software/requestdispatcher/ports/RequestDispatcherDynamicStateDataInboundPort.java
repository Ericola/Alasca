package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.datacenter.ports.AbstractControlledDataInboundPort;
import fr.upmc.datacenter.software.requestdispatcher.RequestDispatcher;

public class RequestDispatcherDynamicStateDataInboundPort extends AbstractControlledDataInboundPort {

    private static final long serialVersionUID = 1L;

    public RequestDispatcherDynamicStateDataInboundPort( ComponentI owner ) throws Exception {
        super( owner );
        assert owner instanceof RequestDispatcher;
    }

    public RequestDispatcherDynamicStateDataInboundPort( String uri , ComponentI owner ) throws Exception {
        super( uri , owner );
        assert owner instanceof RequestDispatcher;
    }

    /**
     * @see fr.upmc.components.interfaces.DataOfferedI.PullI#get()
     */
    @Override
    public DataOfferedI.DataI get() throws Exception {
        final RequestDispatcher rd = ( RequestDispatcher ) this.owner;
        return rd.handleRequestSync( new ComponentI.ComponentService<DataOfferedI.DataI>() {

            @Override
            public DataOfferedI.DataI call() throws Exception {
                return rd.getRequestProcessingTimeAvg();
            }
        } );
    }
}
