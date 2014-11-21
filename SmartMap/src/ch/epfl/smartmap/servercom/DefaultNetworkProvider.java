package ch.epfl.smartmap.servercom;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A default implementation of the {@link NetworkProvider} interface that uses
 * the mechanism available in the {@link URL} object to create
 * {@link HttpURLConnection} objects.
 * 
 * @author SpicyCH
 * @author Pamoi (code reviewed : 9.11.2014)
 */
public class DefaultNetworkProvider implements NetworkProvider {

    /**
     * The default constructor.
     */
    public DefaultNetworkProvider() {
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.sweng.quizapp.NetworkProvider#getConnection(java.net.URL)
     */
    @Override
    public HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

}