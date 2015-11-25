package fr.upmc.datacenter.software.requestdispatcher.ports;

import fr.upmc.components.ComponentI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;

public class RequestDispatcherDynamicStateDataOutboundPort extends AbstractControlledDataOutboundPort {

    private static final long serialVersionUID = 1L;
    protected String          requestDispatcherURI;

    public RequestDispatcherDynamicStateDataOutboundPort( ComponentI owner , String requestDispatcherURI )
            throws Exception {
        super( owner );
        this.requestDispatcherURI = requestDispatcherURI;

        assert owner instanceof RequestDispatcherStateDataConsumerI;
    }

    public RequestDispatcherDynamicStateDataOutboundPort( String uri , ComponentI owner , String requestDispatcherURI )
            throws Exception {
        super( uri , owner );
        this.requestDispatcherURI = requestDispatcherURI;

        assert owner instanceof RequestDispatcherStateDataConsumerI;
    }

    /**
     * @see fr.upmc.components.interfaces.DataRequiredI.PushI#receive(fr.upmc.components.interfaces.DataRequiredI.DataI)
     */
    @Override
    public void receive( DataRequiredI.DataI d ) throws Exception {
        ( ( RequestDispatcherStateDataConsumerI ) this.owner )
                .acceptRequestDispatcherDynamicData( this.requestDispatcherURI , ( RequestDispatcherDynamicStateI ) d );
    }

}
