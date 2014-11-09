package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;

/**
 * @author rbsteinm
 * 
 */
public class FriendsActivity extends ListActivity {
    // Mock stuff
    private List<User> mockUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mockUsersList = new ArrayList<User>();
        //mockUsersList = MockDB.FRIENDS_LIST;
        for (User user: MockDB.FRIENDS_LIST) {
            mockUsersList.add(user);
        }
        mockUsersList.get(1).setOnline(true);
        mockUsersList.get(3).setOnline(true);
        //We sort the users to first display the online ones
        Collections.sort(mockUsersList, new Comparator<User>(){

            @Override
            public int compare(User user1, User user2) {
                return Boolean.compare(user2.isOnline(), user1.isOnline());
            }
        });
        
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
