package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import ch.epfl.smartmap.util.Utils;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author rbsteinm
 */
public class UserInformationActivity extends Activity {

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
                ServiceContainer.getNetworkClient().removeFriend(params[0]);
                confirmString = "You're no longer friend with " + mUser.getName();

                // remove friend from cache and update displayed list
                // TODO should not have to remove the friend from the cache too
                final long userId = params[0];
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ServiceContainer.getCache().removeFriend(userId);
                    }
                });

            } catch (SmartMapClientException e) {
                Log.e(TAG, "Error while removing friend: " + e);
                confirmString = e.getMessage();
            }
            return confirmString;
        }

        @Override
        protected void onPostExecute(String confirmString) {
            Toast.makeText(UserInformationActivity.this, confirmString, Toast.LENGTH_LONG).show();
        }

    }

    private static final String TAG = UserInformationActivity.class.getSimpleName();
    private User mUser;
    private Switch mFollowSwitch;
    private Switch mBlockSwitch;
    private TextView mSubtitlesView;
    private TextView mNameView;
    private ImageView mPictureView;
    private TextView mDistanceView;
    private Context mContext;

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("remove " + mUser.getName() + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new RemoveFriend().execute(mUser.getId());
                ((Activity) mContext).finish();
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
        // TODO need method setVisible
    }

    @Override
    public void onBackPressed() {
        this.onNotificationOpen();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_information);
        mContext = this;
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mSubtitlesView = (TextView) this.findViewById(R.id.user_info_subtitles);
        mFollowSwitch = (Switch) this.findViewById(R.id.user_info_follow_switch);
        mBlockSwitch = (Switch) this.findViewById(R.id.user_info_blocking_switch);
        mDistanceView = (TextView) this.findViewById(R.id.user_info_distance);
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.user_information, menu);
        return true;
    }

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set user informations
        mUser = ServiceContainer.getCache().getUser(this.getIntent().getLongExtra("USER", User.NO_ID));
        mNameView.setText(mUser.getName());
        mSubtitlesView.setText(mUser.getSubtitle());
        mPictureView.setImageBitmap(mUser.getImage());
        mFollowSwitch.setChecked(mUser.isVisible());
        mBlockSwitch.setChecked(mUser.isBlocked());
        mDistanceView.setText(Utils.printDistanceToMe(mUser.getLocation()));
    }
}