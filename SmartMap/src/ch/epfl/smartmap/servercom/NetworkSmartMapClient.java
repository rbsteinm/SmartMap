/**
 *
 */
package ch.epfl.smartmap.servercom;

/**
 * @author SpicyCH
 *
 */
public class NetworkSmartMapClient implements SmartMapClient {

    /* (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#authRequest(java.lang.String, java.lang.String)
     */
    @Override
    public void authRequest(String hash, String phoneNumber) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#getNewHash(java.lang.String)
     */
    @Override
    public String getNewHash(String phoneNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see ch.epfl.smartmap.servercom.SmartMapClient#verifySMSCode(java.lang.String, java.lang.String)
     */
    @Override
    public ConnectionStatus verifySMSCode(String hash, String codeSMS) {
        // TODO Auto-generated method stub
        return null;
    }

}
