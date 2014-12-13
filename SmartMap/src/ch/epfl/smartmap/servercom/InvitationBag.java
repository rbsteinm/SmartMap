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
     * Get a list of the users that sent an invitation request.
     * 
     * @return a list of the inviting users.
     */
    Set<InvitationContainer> getInvitations();
}
