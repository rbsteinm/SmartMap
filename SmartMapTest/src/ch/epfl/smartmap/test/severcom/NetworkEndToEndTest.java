package ch.epfl.smartmap.test.severcom;

import org.junit.Test;

import android.location.Location;

import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import junit.framework.TestCase;

/**
 * Tests whether we can interact with the real quiz server.
 * 
 * @author marion-S
 */
public class NetworkEndToEndTest extends TestCase {

	private final long facebookId = 1482245642055847L;
	private final String name = "SmartMap SwEng";
	private final String fbAccessToken = "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";
	private Location location = new Location("SmartMapServers");

	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Tests if a user with valid data can authenticate
	 * @throws SmartMapClientException
	 */
	@Test
	public void testAuthServer() throws SmartMapClientException {

		NetworkSmartMapClient networkClient = NetworkSmartMapClient
				.getInstance();

		networkClient.authServer(name, facebookId, fbAccessToken);

	}

	/**
	 * Tests if a user can update its position with valid data
	 * @throws SmartMapClientException
	 */
	@Test
	public void testUpdatePos() throws SmartMapClientException {
		location.setLatitude(45);
		location.setLongitude(2);
		NetworkSmartMapClient networkPosClient = NetworkSmartMapClient
				.getInstance();

		networkPosClient.updatePos(location);
	}
	
	

}
