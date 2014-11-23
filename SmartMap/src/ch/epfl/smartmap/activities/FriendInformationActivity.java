package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author jfperren
 */
public class FriendInformationActivity extends Activity {

    private static final String TAG = "INFORMATION_ACTIVITY";
    private static User mUser;
    private static long mUserId;
    private DatabaseHelper mCacheDB;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_information);
        Log.d(TAG, "onCreate");

        mUser = this.getIntent().getParcelableExtra("CURRENT_DISPLAYABLE");
        TextView name = (TextView) this.findViewById(R.id.info_name);
        name.setText(mUser.getName());

        mUserId = mUser.getID();
        mCacheDB = DatabaseHelper.getInstance();
        mContext = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.information, menu);
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

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("remove " + mUser.getName() + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new RemoveFriend().execute(mUserId);
                // TODO refresh the userList
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

    /*private void setUpRemoveFriendButton(){
        Button button = (Button) this.findViewById(R.id.remove_displayable_button);
        button.setText("Remove from friendList");
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FriendInformationActivity.this.displayDeleteConfirmationDialog(mUser.getName(), mUserId);
            }
        });
    }*/



    /**
     * Asynchronous task that removes a friend from the users friendList both
     * from the server and from the cache
     * 
     * @author rbsteinm
     */
    private class RemoveFriend extends AsyncTask<Long, Void, String> {

        private final Handler mHandler = new Handler();
        @Override
        protected String doInBackground(Long... params) {
            String confirmString = "";
            try {
                NetworkSmartMapClient.getInstance().removeFriend(params[0]);
                confirmString = "You're no longer friend with " + mUser.getName();

                // remove friend from cache and update displayed list
                // TODO should it be done in the removeFriend method ?
                final long userId = params[0];
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mCacheDB.deleteUser(userId);
                    }
                });


            } catch (SmartMapClientException e) {
                confirmString = "Network error, operation failed";
            }
            return confirmString;
        }

        @Override
        protected void onPostExecute(String confirmString) {
            Toast.makeText(mContext, confirmString, Toast.LENGTH_LONG).show();
        }

    }
}
