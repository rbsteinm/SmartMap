package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.callbacks.SearchRequestCallback;
import ch.epfl.smartmap.gui.FriendListItemAdapter;
import ch.epfl.smartmap.gui.FriendListItemAdapter.FriendViewHolder;

/**
 * This Activity displays a list of users from the DB and lets you send them friend requests
 * 
 * @author rbsteinm
 */
public class AddFriendActivity extends ListActivity {

    private SearchView mSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_friend);
        // Set action bar color to main color
        this.getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        long userId = ((FriendViewHolder) view.getTag()).getUserId();
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.getChildAt(1);
        assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
        String name = tv.getText().toString();
        this.displayConfirmationDialog(name, userId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.add_friend, menu);
        mSearchBar = (SearchView) menu.findItem(R.id.add_friend_activity_searchBar).getActionView();
        this.setSearchBarListener();
        MenuItem searchMenuItem = menu.findItem(R.id.add_friend_activity_searchBar);
        searchMenuItem.expandActionView();
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

    private void displayConfirmationDialog(String name, final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Add " + name + " as a friend?");

        // Add positive button
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new SendFriendRequest().execute(userId);
            }
        });

        // Add negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // display the AlertDialog
        builder.create().show();
    }

    private void setSearchBarListener() {
        mSearchBar.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ServiceContainer.getSearchEngine().findStrangerByQuery(newText, new FindFriendsCallback());
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String newText) {
                ServiceContainer.getSearchEngine().findStrangerByQuery(newText, new FindFriendsCallback());
                return true;
            }
        });
    }

    private class FindFriendsCallback implements SearchRequestCallback<Set<User>> {

        @Override
        public void onNetworkError() {
            AddFriendActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddFriendActivity.this.getBaseContext(),
                            "Couldn't find friends due to a network error. Please try again later.", Toast.LENGTH_LONG)
                            .show();
                }
            });
        }

        @Override
        public void onNotFound() {
            AddFriendActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AddFriendActivity.this.getBaseContext(), "No user found for this query.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onResult(final Set<User> result) {
            AddFriendActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AddFriendActivity.this.setListAdapter(new FriendListItemAdapter(AddFriendActivity.this,
                            new ArrayList<User>(result)));
                }
            });
        }
    }

    /**
     * Asynchronous task that sends a friend request to the friend whose id is given in parameter
     * 
     * @author rbsteinm
     */
    private class SendFriendRequest extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            ServiceContainer.getCache().inviteUser(params[0], new NetworkRequestCallback() {

                @Override
                public void onFailure() {
                    AddFriendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFriendActivity.this.getBaseContext(),
                                    "Friend request sent couldn't be sent.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onSuccess() {
                    AddFriendActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AddFriendActivity.this.getBaseContext(), "Friend request sent !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            return null;
        }
    }
}