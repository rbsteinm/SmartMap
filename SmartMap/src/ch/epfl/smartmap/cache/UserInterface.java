package ch.epfl.smartmap.cache;

/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */

public interface UserInterface extends Displayable {

    int getFriendship();

    UserContainer getContainerCopy();

    String getName();

    boolean update(UserContainer user);

    User.BlockStatus getBlockStatus();
}