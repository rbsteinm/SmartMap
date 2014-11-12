package ch.epfl.smartmap.activities;

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
import ch.epfl.smartmap.gui.FriendListItemAdapter;

/**
 * @author rbsteinm
 * 
 */
public class FriendsActivity extends ListActivity {
    // Mock stuff
    private List<User> mMockUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mMockUsersList = new ArrayList<User>();
        for (User user: MockDB.FRIENDS_LIST) {
            mMockUsersList.add(user);
        }
        mMockUsersList.get(1).setOnline(true);
        mMockUsersList.get(3).setOnline(true);
        sortByOnline(mMockUsersList);
        
        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(this, mMockUsersList);
        setListAdapter(adapter);
    }
    
    private void sortByOnline(List<User> userList) {
        Collections.sort(userList, new Comparator<User>(){

            @Override
            public int compare(User user1, User user2) {
                return Boolean.compare(user2.isOnline(), user1.isOnline());
            }
        });
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
