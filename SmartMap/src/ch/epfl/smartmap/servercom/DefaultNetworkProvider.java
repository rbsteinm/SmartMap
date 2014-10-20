package ch.epfl.smartmap.servercom;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A default implementation of the {@link NetworkProvider} interface that uses
 * the mechanism available in the {@link URL} object to create
 * {@link HttpURLConnection} objects.
 *
 * @author SpicyCH
 */
public class DefaultNetworkProvider implements NetworkProvider {

    /**
     * The default constructor.
     */
    public DefaultNetworkProvider() {
    }

    /*
     * (non-Javadoc)
     *
     * @see ch.epfl.sweng.quizapp.NetworkProvider#getConnection(java.net.URL)
     */
    @Override
    public HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /* (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.NetworkProvider#read(java.net.URL)
     */
    @Override
    public String read(URL url) throws IOException {



        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        StringBuilder sb = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();

        return sb.toString();
    }
}