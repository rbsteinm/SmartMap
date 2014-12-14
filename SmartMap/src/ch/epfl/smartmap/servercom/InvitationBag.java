/**
 * 
 */
package ch.epfl.smartmap.servercom;

import java.util.Set;

import ch.epfl.smartmap.cache.InvitationContainer;

/**
 * An interface to encapsulate the informations given by the request
 * getInvitations of {@link SmartMapClient}.
 * 
 * @author Pamoi
 */
public interface InvitationBag {

    /**
     * Get a set of {@link InvitationContainer} objects
     * 
     * @return a set of InvitationContainer objects
     */
    Set<InvitationContainer> getInvitations();
}
