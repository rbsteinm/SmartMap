package ch.epfl.smartmap.gui;

import android.content.Context;
import ch.epfl.smartmap.cache.Friend;

/**
 * Factory class that provides a function {@code getSearchResultView} returning
 * the right type of SearchResultView
 * 
 * @author jfperren
 */
public abstract class SearchResultViewFactory {
    @SuppressWarnings("unused")
    private static final String TAG = "SEARCH_RESULT_VIEW_FACTORY";

    public static SearchResultView getSearchResultView(Context context, Object item) {
        if (item instanceof Friend) {
            return new FriendSearchResultView(context, (Friend) item);
        } else {
            throw new IllegalArgumentException("Bad type item");
        }
    }
}
