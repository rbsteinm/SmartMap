package ch.epfl.smartmap.test.severcom;

import org.junit.Test;

import android.test.AndroidTestCase;
import ch.epfl.smartmap.servercom.DefaultNetworkProvider;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

public class NetworkAuthenticationClientTest extends AndroidTestCase {
    @SuppressWarnings("unused")
    private static String SERVER_URL = "http://smartmap.ddns.net";
    private final long facebookId = 1482245642055847L;
    private final String name = "SmartMap SwEng";
    private final String fbAccessToken = "CAAEWMqbRPIkBAJjvxMI0zNXLgzxYJURV5frWkDu8T60EfWup92GNEE7xDIVohfpa43Qm7FNbZCvZB7bXVTd0ZC0qLHZCju2zZBR3mc8mQH0OskEe7X5mZAWOlLZCIzsAWnfEy1ZAzz2JgYPKjaIwhIpI9OvJkQNWkJnX3rIwv4v9lL7hr9yx8LKuOegEHfZCcCNp491jewilZCz69ZA2ohryEYy";

    public NetworkAuthenticationClientTest() {
        super();
    }

    @Test
    public void test() throws SmartMapClientException {
        @SuppressWarnings("unused")
        DefaultNetworkProvider provider = new DefaultNetworkProvider();
        NetworkSmartMapClient networkClient = NetworkSmartMapClient
            .getInstance();

        networkClient.authServer(name, facebookId, fbAccessToken);

    }

}
