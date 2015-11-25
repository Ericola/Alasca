package fr.upmc.datacenter.software.requestdispatcher.interfaces;

public interface RequestDispatcherStateDataConsumerI {

    public void acceptRequestDispatcherDynamicData( String requestDispatcherURI ,
            RequestDispatcherDynamicStateI currentDynamicState ) throws Exception;
}
