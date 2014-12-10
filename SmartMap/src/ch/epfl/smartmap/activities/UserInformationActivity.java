package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.util.Utils;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author rbsteinm
 */
public class UserInformationActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = UserInformationActivity.class.getSimpleName();
    private User mUser;
    private Switch mFollowSwitch;
    private Switch mBlockSwitch;
    private TextView mSubtitlesView;
    private TextView mNameView;
    private ImageView mPictureView;
    private TextView mDistanceView;

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("remove " + mUser.getName() + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ServiceContainer.getCache().removeFriend(mUser.getId(), new NetworkRequestCallback() {
                    @Override
                    public void onFailure() {
                        UserInformationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserInformationActivity.this,
                                    UserInformationActivity.this.getString(R.string.remove_friend_failure),
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess() {
                        UserInformationActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserInformationActivity.this,
                                    UserInformationActivity.this.getString(R.string.remove_friend_success),
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                UserInformationActivity.this.finish();
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

    @Override
    public void onBackPressed() {
        this.onNotificationOpen();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_information);
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mSubtitlesView = (TextView) this.findViewById(R.id.user_info_subtitles);
        mFollowSwitch = (Switch) this.findViewById(R.id.user_info_follow_switch);
        mBlockSwitch = (Switch) this.findViewById(R.id.user_info_blocking_switch);
        mDistanceView = (TextView) this.findViewById(R.id.user_info_distance);
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
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
        // Set user informations
        mUser = ServiceContainer.getCache().getUser(this.getIntent().getLongExtra("USER", User.NO_ID));
        mNameView.setText(mUser.getName());
        mSubtitlesView.setText(mUser.getSubtitle());
        mPictureView.setImageBitmap(mUser.getImage());
        mFollowSwitch.setChecked(mUser.isVisible());
        mBlockSwitch.setChecked(mUser.isBlocked());
        mDistanceView.setText(Utils.printDistanceToMe(mUser.getLocation()));
    }

    public void showOnMap(View view) {
        // TODO
    }
}