package ch.epfl.smartmap.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AboutActivity;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.ProfileActivity;
import ch.epfl.smartmap.activities.SettingsActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.activities.ShowFiltersActivity;
import ch.epfl.smartmap.background.LogoutManager;

/**
 * Listener handling the clicks on each element in the left drawer menu.
 * 
 * @author rbsteinm
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    @SuppressWarnings("unused")
    private final static String TAG = DrawerItemClickListener.class.getSimpleName();
    private final Context mContext;
    private final DrawerLayout mDrawer;

    private static final int INDEX_PROFILE = 0;
    private static final int INDEX_FRIENDS = 1;
    private static final int INDEX_EVENTS = 2;
    private static final int INDEX_FILTERS = 3;
    private static final int INDEX_SETTINGS = 4;
    private static final int INDEX_ABOUT = 5;
    private static final int INDEX_LOGOUT = 6;

    public DrawerItemClickListener(Context context) {
        mContext = context;
        mDrawer = (DrawerLayout) ((Activity) mContext).findViewById(R.id.drawer_layout);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case INDEX_PROFILE:
                view.getContext().startActivity(new Intent(view.getContext(), ProfileActivity.class));
                break;
            case INDEX_FRIENDS:
                view.getContext().startActivity(new Intent(view.getContext(), FriendsPagerActivity.class));
                break;
            case INDEX_EVENTS:
                view.getContext().startActivity(new Intent(view.getContext(), ShowEventsActivity.class));
                break;
            case INDEX_FILTERS:
                view.getContext().startActivity(new Intent(view.getContext(), ShowFiltersActivity.class));
                break;
            case INDEX_SETTINGS:
                view.getContext().startActivity(new Intent(view.getContext(), SettingsActivity.class));
                break;
            case INDEX_ABOUT:
                view.getContext().startActivity(new Intent(view.getContext(), AboutActivity.class));
                break;
            case INDEX_LOGOUT:
                LogoutManager.initialize(parent.getContext());
                LogoutManager.getInstance().showConfirmationThenLogout();
                break;
            default:
                break;
        }
        // TODO here the view loses the focus
        parent.clearChildFocus(view);
        mDrawer.closeDrawers();
    }
}
