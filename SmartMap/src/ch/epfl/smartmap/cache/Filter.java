package ch.epfl.smartmap.cache;

import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * A list of user IDs
 * 
 * @author ritterni
 */
public interface Filter extends Displayable {

    // Id of the Default filter
    long DEFAULT_FILTER_ID = 0;

    Bitmap DEFAULT_IMAGE = BitmapFactory.decodeResource(ServiceContainer.getSettingsManager().getContext()
        .getResources(), R.drawable.ic_hashtag);

    /**
     * Adds a user to the list
     * 
     * @param id
     *            The user's ID
     */
    void addFriend(long newFriend);

    /**
     * @return The whole list of IDs
     */
    Set<Long> getFriendIds();

    ImmutableFilter getImmutableCopy();

    /**
     * @return The name of the list
     */
    String getName();

    boolean isActive();

    boolean update(ImmutableFilter filterInfo);
}