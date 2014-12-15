//package ch.epfl.smartmap.test.database;
//
//import static ch.epfl.smartmap.test.database.MockContainers.ALAIN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.DEFAULT_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.FAMILY_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.FOOTBALL_TOURNAMENT_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.JULIEN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.POLYLAN_EVENT_INVITATION_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.ROBIN_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.ROBIN_FRIEND_ACCEPTED_INVITATION_CONTAINER;
//import static ch.epfl.smartmap.test.database.MockContainers.ROBIN_FRIEND_INVITATION_CONTAINER;
//import ch.epfl.smartmap.cache.Cache;
//import ch.epfl.smartmap.cache.Event;
//import ch.epfl.smartmap.cache.Filter;
//import ch.epfl.smartmap.cache.Invitation;
//import ch.epfl.smartmap.cache.User;
//
///**
// * @author jfperren
// */
//public class MockInstances {
//
//    public static final Cache CREATOR;
//
//    static {
//        CREATOR = new Cache();
//        // Add mock users
//        CREATOR.putUser(JULIEN_CONTAINER);
//        CREATOR.putUser(ALAIN_CONTAINER);
//        CREATOR.putUser(ROBIN_CONTAINER);
//        // add mock events
//        CREATOR.putEvent(FOOTBALL_TOURNAMENT_CONTAINER);
//        CREATOR.putEvent(POLYLAN_CONTAINER);
//        // add mock invitations
//        CREATOR.putInvitation(ROBIN_FRIEND_INVITATION_CONTAINER);
//        CREATOR.putInvitation(ROBIN_FRIEND_ACCEPTED_INVITATION_CONTAINER);
//        CREATOR.putInvitation(POLYLAN_EVENT_INVITATION_CONTAINER);
//        // add mock filter
//        CREATOR.putFilter(FAMILY_CONTAINER);
//        CREATOR.putFilter(DEFAULT_CONTAINER);
//    }
//
//    User JULIEN = CREATOR.getUser(JULIEN_CONTAINER.getId());
//    User ROBIN = CREATOR.getUser(ROBIN_CONTAINER.getId());
//    User ALAIN = CREATOR.getUser(ALAIN_CONTAINER.getId());
//
//    Event POLYLAN = CREATOR.getEvent(POLYLAN_CONTAINER.getId());
//    Event FOOTBALL_TOURNAMENT = CREATOR.getEvent(FOOTBALL_TOURNAMENT_CONTAINER.getId());
//
//    Invitation ROBIN_FRIEND_INVITATION = CREATOR.getInvitation(ROBIN_FRIEND_INVITATION_CONTAINER.getId());
//    Invitation ROBIN_FRIEND_ACCEPTED_INVITATION = CREATOR
//        .getInvitation(ROBIN_FRIEND_ACCEPTED_INVITATION_CONTAINER.getId());
//    Invitation POLYLAN_EVENT_INVITATION = CREATOR.getInvitation(POLYLAN_EVENT_INVITATION_CONTAINER.getId());
//
//    Filter FAMILY = CREATOR.getFilter(FAMILY_CONTAINER.getId());
//    Filter DEFAULT = CREATOR.getDefaultFilter();
//
//}
