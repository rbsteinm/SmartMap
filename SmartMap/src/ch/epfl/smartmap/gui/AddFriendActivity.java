package ch.epfl.smartmap.gui;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * @author rbsteinm
 *
 */
public class AddFriendActivity extends Activity {
    
    private List<User> mUserList;
    
    protected void updateUserList() {
        //TODO call this method in the Activity's xml
        new RefreshUserList().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_friend, menu);
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
    
    /**
     * Asynchronous task that refreshes the list of users displayed every time a new 
     * character is typed in the searchbar
     * @author rbsteinm
     *
     */
    private class RefreshUserList extends AsyncTask<String, Void, List<User>> {

        @Override
        protected List<User> doInBackground(String... params) {
            // TODO implement Marion's method and return the refreshed User list
            return null;
        }
        @Override
        protected void onPostExecute(List<User> refreshedList) {
            super.onPostExecute(refreshedList);
            //TODO dispaly the new user list (with custom adapter?)
            //something like this.setListAdapter(new custom adapter)
        }
        
    }
}


