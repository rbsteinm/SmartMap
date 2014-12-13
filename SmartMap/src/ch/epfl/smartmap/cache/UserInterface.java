package ch.epfl.smartmap.cache;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */

public interface UserInterface extends Displayable {

    int getFriendship();

    UserContainer getImmutableCopy();

    String getName();

    boolean update(UserContainer user);

    User.blockStatus isBlocked();
}