package ch.epfl.smartmap.test.severcom;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import android.test.AndroidTestCase;

import ch.epfl.smartmap.servercom.DefaultNetworkProvider;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;

import ch.epfl.smartmap.servercom.SmartMapClientException;

public class SmartMapClientTest extends AndroidTestCase {

	 private static String SERVER_URL = "http://www.swissgen.net/sweng/test-server.php";
	    private static String SWISSGEN_COUNTER_URL = "http://www.swissgen.net/sweng/counter.php";

	    public SmartMapClientTest() {
	        super();
	    }

	    @Test
	    public void testRead() throws MalformedURLException, IOException {
	        DefaultNetworkProvider provider = new DefaultNetworkProvider();
	        String correctContent = "Test read works!";
	        String pageContent = provider.read(new URL("http://www.swissgen.net/sweng/test-read"));
	        assertEquals(correctContent, pageContent);
	    }

	    @Test
	    public void testSendViaPost() throws MalformedURLException, IOException {
	        DefaultNetworkProvider provider = new DefaultNetworkProvider();
	        NetworkSmartMapClient networkClient = NetworkSmartMapClient.getInstance();

	        Map<String, String> params = new LinkedHashMap<String, String>();
	        params.put("push", "increment");

	        String previousCount = provider.read(new URL(SWISSGEN_COUNTER_URL));
	        int previousCountInt = Integer.parseInt(previousCount);

	        HttpURLConnection conn=null;
	        String serverAnswer=null;
			try {
				conn = networkClient.getHttpURLConnection("");
				serverAnswer =  networkClient.sendViaPost(params,conn);
			} catch (SmartMapClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	       
	        if (serverAnswer.equals("ERROR")) {
	            fail("sendViaPost returned an ERROR");
	        }

	        String finalCount = provider.read(new URL(SWISSGEN_COUNTER_URL));
	        int finalCountInt = Integer.parseInt(finalCount);

	        assertEquals(previousCountInt +1, finalCountInt);
	    }


}
