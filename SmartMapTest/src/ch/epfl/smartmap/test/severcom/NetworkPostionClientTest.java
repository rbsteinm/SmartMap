package ch.epfl.smartmap.test.severcom;

import org.junit.Test;

import android.test.AndroidTestCase;
import android.util.Log;
import ch.epfl.smartmap.cache.Point;
import ch.epfl.smartmap.servercom.DefaultNetworkProvider;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

public class NetworkPostionClientTest extends AndroidTestCase {

    @SuppressWarnings("unused")
    private static String SERVER_URL = "http://smartmap.ddns.net";
    private Point position = new Point(45, 2);

    public NetworkPostionClientTest() {
        super();
    }

    @Test
    public void testUpdatePos() throws SmartMapClientException {
        Log.d("test", "start");
        @SuppressWarnings("unused")
        DefaultNetworkProvider provider = new DefaultNetworkProvider();
        NetworkSmartMapClient networkPosClient = NetworkSmartMapClient
            .getInstance();

        networkPosClient.updatePos(position);
    }

}