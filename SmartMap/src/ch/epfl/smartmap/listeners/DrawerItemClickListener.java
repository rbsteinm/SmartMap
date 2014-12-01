package ch.epfl.smartmap.listeners;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.SettingsActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.background.LogoutManager;

/**
 * Listener handling the clics on each element in the left drawer menu
 * 
 * @author rbsteinm
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private static final int INDEX_PROFILE = 0;
    private static final int INDEX_FRIENDS = 1;
    private static final int INDEX_EVENTS = 2;
    private static final int INDEX_FILTERS = 3;
    private static final int INDEX_SETTINGS = 4;
    private static final int INDEX_LOGOUT = 5;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case INDEX_PROFILE:
                break;
            case INDEX_FRIENDS:

                Intent displayActivityIntent = new Intent(view.getContext(), FriendsPagerActivity.class);
                view.getContext().startActivity(displayActivityIntent);

                break;
            case INDEX_EVENTS:
                view.getContext().startActivity(new Intent(view.getContext(), ShowEventsActivity.class));
                break;
            case INDEX_FILTERS:
                break;
            case INDEX_SETTINGS:
                view.getContext().startActivity(new Intent(view.getContext(), SettingsActivity.class));
                break;
            case INDEX_LOGOUT:
                LogoutManager.initialize(parent.getContext());
                LogoutManager.getInstance().showConfirmationThenLogout();
                break;
            default:
                break;
        }
    }
}
