package fr.upmc.datacenterclient.applicationprovider.interfaces;

import fr.upmc.components.interfaces.OfferedI;
import fr.upmc.components.interfaces.RequiredI;

public interface ApplicationSubmissionI extends OfferedI, RequiredI {

    public String submitApplication( int nbVM ) throws Exception;

}
