package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
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
public class UserInformationActivity extends Activity {

    private static final String TAG = UserInformationActivity.class.getSimpleName();

    private DatabaseHelper mCacheDB;

    private CheckBox mFollowCheckBox;
    private TextView mInfosView;
    private TextView mNameView;

    // Children Views
    private ImageView mPictureView;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.setContentView(R.layout.activity_user_information);
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mInfosView = (TextView) this.findViewById(R.id.user_info_infos);
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get User & Database
        mUser = this.getIntent().getParcelableExtra("USER");
        mCacheDB = DatabaseHelper.getInstance();
        // Set Informations
        mNameView.setText(mUser.getName());
        mInfosView.setText(mUser.getShortInfos());
        mPictureView.setImageBitmap(mUser.getPicture(this));
    }

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("remove " + mUser.getName() + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new RemoveFriend().execute(mUser.getID());
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

    public void followUnfollow(View view) {
        new FollowFriend().execute(mUser.getID());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.user_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /**
     * @author rbsteinm
     *         AsyncTask that follows/unfollows a friend when the user checks/unchecks
     *         the checkBox
     */
    private class FollowFriend extends AsyncTask<Long, Void, String> {

        @Override
        protected String doInBackground(Long... params) {
            String confirmString = "";
            try {
                if (mFollowCheckBox.isChecked()) {
                    NetworkSmartMapClient.getInstance().followFriend(mUser.getID());
                    confirmString = "You're now following " + mUser.getName();
                } else {
                    NetworkSmartMapClient.getInstance().unfollowFriend(mUser.getID());
                    confirmString = "You're not following " + mUser.getName() + " anymore";
                }
            } catch (SmartMapClientException e) {
                confirmString = e.getMessage();
            }
            return confirmString;
        }

        @Override
        protected void onPostExecute(String confirmString) {
            Toast.makeText(UserInformationActivity.this, confirmString, Toast.LENGTH_LONG).show();
        }

    }

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
            Toast.makeText(UserInformationActivity.this, confirmString, Toast.LENGTH_LONG).show();
        }

    }
}
