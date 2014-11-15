package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
        mMockUsersList.get(2).setOnline(true);
        sortByOnline(mMockUsersList);
        
        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(this, mMockUsersList);
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
    
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        long userId = (Long) view.getTag();
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.getChildAt(1);
        assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
        String name = tv.getText().toString();
        displayDeleteConfirmationDialog(name, userId);
    }
    
    public void startAddFriendActivity(MenuItem menu) {
        Intent displayActivityIntent = new Intent(this, AddFriendActivity.class);
        startActivity(displayActivityIntent);
    }
    
    private void sortByOnline(List<User> userList) {
        Collections.sort(userList, new Comparator<User>(){

            @Override
            public int compare(User user1, User user2) {
                if (user1.isOnline()) {
                    return -1;
                }
                if (user2.isOnline()) {
                    return 1;
                }
                return 0;
            }
        });
    }
    
    private void displayDeleteConfirmationDialog(String name, long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("remove " + name + " from your friends?");
        
        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                /*try {
                    //TODO move this request in an asynch task, => interface? ask Marion
                    NetworkSmartMapClient.getInstance().inviteFriend(id);
                } catch (SmartMapClientException e) {
                    e.printStackTrace();
                }*/
            }
        });
        
        //Add negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        
        // display the AlertDialog
        builder.create().show();
    }
}
