package ch.epfl.smartmap.cache;

/**
 * Describe methods that need to be implemented for any {@code User} subclass of the Application
 * 
 * @author jfperren
 */
public interface UserInterface extends Displayable {

    /**
     * @return the Block status of the {@code User}, can be {@code BLOCKED}, {@code UNBLOCKED} or
     *         {@code NOT_SET}
     */
    User.BlockStatus getBlockStatus();

    /**
     * @return a {@code UserContainer} instance containing all informations about this {@code User}
     */
    UserContainer getContainerCopy();

    /**
     * @return an {@code int} corresponding to the friendship link with current User of the App. Recognized
     *         values are {@code DONT_KNOW}(-1), {@code STRANGER}(0), {@code FRIEND}(1) or {@code SELF}(2).
     */
    int getFriendship();

    /**
     * @return the name of the {@code User}
     */
    String getName();

    /**
     * Updates all values of the {@code User} with the values from the {@code UserContainer}. If you don't
     * want to update a field, you can either set the "NO_VALUE" (i. e {@code NO_ID, NO_NAME, NO_IMAGE, ...})
     * or {@code null}.
     * 
     * @param newValues
     *            Container with new values
     * @return {@code True} if a change was actually made.
     */
    boolean update(UserContainer newValues);
}