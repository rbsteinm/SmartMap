package ch.epfl.smartmap.cache;


/**
 * Describes a generic user of the app
 * 
 * @author ritterni
 */

public interface UserInterface extends Displayable {

    int getFriendship();

    ImmutableUser getImmutableCopy();

    String getName();

    void update(ImmutableUser user);
}