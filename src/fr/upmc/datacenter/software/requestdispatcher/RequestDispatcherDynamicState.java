package fr.upmc.datacenter.software.requestdispatcher;

import java.net.InetAddress;

import fr.upmc.datacenter.software.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;

public class RequestDispatcherDynamicState implements RequestDispatcherDynamicStateI {

    private static final long serialVersionUID = 1L;
    /** timestamp in Unix time format, local time of the timestamper. */
    protected final long      timestamp;
    /** IP of the node that did the timestamping. */
    protected final String    timestamperIP;
    /** URI of the request dispatcher to which this dynamic state relates. */
    protected final String    requestDispatcherURI;
    /** the average request processing time */
    protected final long    requestProcessingAvg;

    public RequestDispatcherDynamicState( String rdUri , long requestProcessingAvg ) throws Exception {
        super();
        this.timestamp = System.currentTimeMillis();
        this.timestamperIP = InetAddress.getLocalHost().getHostAddress();
        this.requestDispatcherURI = rdUri;
        this.requestProcessingAvg = requestProcessingAvg;

    }

    @Override
    public long getTimeStamp() {
        return this.timestamp;
    }

    @Override
    public String getTimeStamperId() {

        return this.timestamperIP;
    }

    @Override
    public String getRequestDispatcherURI() {
        return this.requestDispatcherURI;
    }

    @Override
    public double getRequestProcessingAvg() {
        return this.requestProcessingAvg;
    }

}
