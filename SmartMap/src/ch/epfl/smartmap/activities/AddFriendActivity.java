package ch.epfl.smartmap.activities;

import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * @author rbsteinm
 *
 */
public class AddFriendActivity extends ListActivity {
    
    private SearchView mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_friend, menu);
        mSearchBar = (SearchView) menu.findItem(R.id.add_friend_activity_searchBar).getActionView();
        setSearchBarListener();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_friend_activity_searchBar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void setSearchBarListener() {
        mSearchBar.setOnQueryTextListener(new OnQueryTextListener() {
            
            @Override
            public boolean onQueryTextSubmit(String newText) {
                new RefreshUserList().execute(newText);
                return true;
            }
            
            @Override
            public boolean onQueryTextChange(String newText) {
                new RefreshUserList().execute(newText);
                return true;
            }
        });
    }
    
    /**
     * Asynchronous task that refreshes the list of users displayed every time a new 
     * character is typed in the searchbar
     * @author rbsteinm
     *
     */
    private class RefreshUserList extends AsyncTask<String, Void, List<User>> {

        @Override
        protected List<User> doInBackground(String... params) {
            try {
                if (params[0].equals("")) {
                    return Collections.emptyList();
                } else {
                    return NetworkSmartMapClient.getInstance().findUsers(params[0]);
                }
            } catch (SmartMapClientException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<User> refreshedList) {
            super.onPostExecute(refreshedList);
            System.out.println("refreshedList length:" + refreshedList.size());
            setListAdapter(new FriendListItemAdapter(AddFriendActivity.this, refreshedList));
        }
        
    }
}


