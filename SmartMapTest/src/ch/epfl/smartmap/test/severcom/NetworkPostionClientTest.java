package ch.epfl.smartmap.test.severcom;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.smartmap.cache.Point;
import ch.epfl.smartmap.servercom.DefaultNetworkProvider;


import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

import android.test.AndroidTestCase;
import android.util.Log;

public class NetworkPostionClientTest extends AndroidTestCase{

	private static String SERVER_URL = "http://smartmap.ddns.net";
	private Point position=new Point (45,2);
	
	public NetworkPostionClientTest() {
		super();
	}
	

	@Test
	public void testUpdatePos() throws SmartMapClientException {
		Log.d("test", "start");
		DefaultNetworkProvider provider = new DefaultNetworkProvider();
		NetworkSmartMapClient networkPosClient = NetworkSmartMapClient.getInstance();

		networkPosClient.updatePos(position);
	}

}
