package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.cache.Friend;
import android.content.Context;

/**
 * Factory class that provides a function {@code getSearchResultView} returning the right type of SearchResultView
 * 
 * @author jfperren
 */
public abstract class SearchResultViewFactory {
    public static SearchResultView getSearchResultView(Context context, Friend friend) {
        // TODO : Update this method when more searchResultView types are written.
        return new FriendSearchResultView(context, friend);
    }
}