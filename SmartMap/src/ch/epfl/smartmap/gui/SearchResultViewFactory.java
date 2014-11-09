package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.cache.Friend;
import android.content.Context;
import android.util.Log;

/**
 * Factory class that provides a function {@code getSearchResultView} returning the right type of SearchResultView
 * 
 * @author jfperren
 */
public abstract class SearchResultViewFactory {
    private static final String TAG = "SEARCH_RESULT_VIEW_FACTORY";
    public static SearchResultView getSearchResultView(Context context, Object item) {
        if (item instanceof Friend) {
            Log.d(TAG, "return FriendSearchResultView");
            return new FriendSearchResultView(context, (Friend) item);
        } else {
            throw new IllegalArgumentException("Bad type item");
        }
    }
}
