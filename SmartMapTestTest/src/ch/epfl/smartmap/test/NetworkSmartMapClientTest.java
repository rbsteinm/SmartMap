package ch.epfl.smartmap.test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.servercom.DefaultNetworkProvider;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;

/**
 * Cannot use @Before with android??? :(
 *
 * @author SpicyCH
 *
 */
public class NetworkSmartMapClientTest extends AndroidTestCase {

    private static String SERVER_URL = "http://www.swissgen.net/sweng/test-server.php";
    private static String SWISSGEN_COUNTER_URL = "http://www.swissgen.net/sweng/counter.php";

    public NetworkSmartMapClientTest() {
        super();
    }

    public void testRead() throws MalformedURLException, IOException {
        DefaultNetworkProvider provider = new DefaultNetworkProvider();
        String correctContent = "Test read works!";
        String pageContent = provider.read(new URL("http://www.swissgen.net/sweng/test-read"));
        assertEquals(correctContent, pageContent);
    }

    public void testSendViaPost() throws MalformedURLException, IOException {
        DefaultNetworkProvider provider = new DefaultNetworkProvider();
        NetworkSmartMapClient networkClient = new NetworkSmartMapClient(SWISSGEN_COUNTER_URL, provider);

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("push", "increment");

        String previousCount = provider.read(new URL(SWISSGEN_COUNTER_URL));
        int previousCountInt = Integer.parseInt(previousCount);


        String serverAnswer =  networkClient.sendViaPost(params);
        if (serverAnswer.equals("ERROR")) {
            fail("sendViaPost returned an ERROR");
        }

        String finalCount = provider.read(new URL(SWISSGEN_COUNTER_URL));
        int finalCountInt = Integer.parseInt(finalCount);

        assertEquals(previousCountInt + 1, finalCountInt);
    }

}
