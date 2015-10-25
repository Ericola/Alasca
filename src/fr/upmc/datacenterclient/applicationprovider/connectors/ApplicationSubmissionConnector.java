package fr.upmc.datacenterclient.applicationprovider.connectors;

import fr.upmc.components.connectors.AbstractConnector;
import fr.upmc.datacenterclient.applicationprovider.interfaces.ApplicationSubmissionI;

public class ApplicationSubmissionConnector extends AbstractConnector implements ApplicationSubmissionI {

    @Override
    public String submitApplication( int nbVM ) throws Exception {
        return ( ( ApplicationSubmissionI ) this.offering ).submitApplication( nbVM );
    }


}
