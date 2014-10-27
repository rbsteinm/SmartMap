package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;

/**
 * @author rbsteinm
 * 
 */
public class FriendsActivity extends ListActivity {
    // Mock stuff
    private List<User> mockUsersList;
    private Friend julien;
    private Friend robin;
    private Friend alain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // Filling up mock stuff
        mockUsersList = new ArrayList<User>();
        julien = new Friend(1, "Julien Perrenoud");
        julien.setOnline(true);
        mockUsersList.add(julien);
        robin = new Friend(2, "Robin Genolet");
        robin.setOnline(false);
        mockUsersList.add(robin);
        alain = new Friend(2, "Alain Milliet");
        alain.setOnline(false);
        mockUsersList.add(alain);

        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(this, mockUsersList);
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
