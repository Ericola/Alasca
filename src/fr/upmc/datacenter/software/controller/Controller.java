package fr.upmc.datacenter.software.controller;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import fr.upmc.components.AbstractComponent;
import fr.upmc.components.ComponentI;
import fr.upmc.components.connectors.DataConnector;
import fr.upmc.components.exceptions.ComponentShutdownException;
import fr.upmc.components.interfaces.DataOfferedI;
import fr.upmc.components.interfaces.DataRequiredI;
import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.upmc.datacenter.software.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;

public class Controller extends AbstractComponent {

    /** the URI of the component. */
    protected String                                        cURI;
    protected ScheduledFuture<?>                            pullingFuture;
    protected RequestDispatcherDynamicStateDataOutboundPort rdDynamicStateDataOP;

    protected RequestDispatcherDynamicStateDataOutboundPort requestDispatcherDynamicStateDataOutboundPort;

    public Controller( String cURI , String requestDispatcherURI , String rddsdip ) throws Exception {
        this.cURI = cURI;
        this.requestDispatcherDynamicStateDataOutboundPort = new RequestDispatcherDynamicStateDataOutboundPort( this ,
                requestDispatcherURI );
        this.addRequiredInterface(DataRequiredI.PullI.class) ;
        this.addOfferedInterface(DataRequiredI.PushI.class);
        this.addPort( this.requestDispatcherDynamicStateDataOutboundPort );
        this.requestDispatcherDynamicStateDataOutboundPort.publishPort();
        this.requestDispatcherDynamicStateDataOutboundPort.doConnection( rddsdip ,
                DataConnector.class.getCanonicalName() );
    }

    public void startControlling() {
        this.pullingFuture = this.scheduleTaskAtFixedRate( new ComponentI.ComponentTask() {

            @Override
            public void run() {
                try {
                    RequestDispatcherDynamicStateI rdds = getDynamicState();
                    print( "timestamp      : " + rdds.getTimeStamp() );
                    print( "timestamper id : " + rdds.getTimeStamperId() );
                    print( "request time average : " + rdds.getRequestProcessingAvg() );

                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } , 0 , 10 , TimeUnit.MILLISECONDS );
    }

    public RequestDispatcherDynamicStateI getDynamicState() throws Exception {
        return ( RequestDispatcherDynamicStateI ) rdDynamicStateDataOP.request();
    }

    private void print( String s ) {
        this.logMessage( "[Controller] " + s );
    }

    @Override
    public void shutdown() throws ComponentShutdownException {

    }
}
