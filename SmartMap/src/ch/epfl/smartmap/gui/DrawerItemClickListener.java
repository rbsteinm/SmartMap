package ch.epfl.smartmap.gui;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * @author rbsteinm
 * 
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private static final int INDEX_ZERO = 0;
    private static final int INDEX_ONE = 1;
    private static final int INDEX_TWO = 2;
    private static final int INDEX_THREE = 3;
    private static final int INDEX_FOUR = 4;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case INDEX_ZERO:
                // profile
                break;
            case INDEX_ONE:
                // friends
                Intent displayActivityIntent = new Intent(view.getContext(), FriendsActivity.class);
                view.getContext().startActivity(displayActivityIntent);
                break;
            case INDEX_TWO:
                // Events
                break;
            case INDEX_THREE:
                // Filters
                break;
            case INDEX_FOUR:
                // Settings
                break;
            default:
                break;
        }
    }
}
