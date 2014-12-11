package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Friend;
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
    private Switch mShowOnMapSwitch;
    private Switch mBlockSwitch;
    private TextView mSubtitlesView;
    private TextView mNameView;
    private ImageView mPictureView;
    private TextView mDistanceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_information);
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mSubtitlesView = (TextView) this.findViewById(R.id.user_info_subtitles);
        mShowOnMapSwitch = (Switch) this.findViewById(R.id.user_info_show_on_map_switch);
        mBlockSwitch = (Switch) this.findViewById(R.id.user_info_blocking_switch);
        mDistanceView = (TextView) this.findViewById(R.id.user_info_distance);
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set user informations
        mUser = ServiceContainer.getCache().getUser(this.getIntent().getLongExtra("USER", User.NO_ID));

        // Defensive case, should never happen
        if (mUser == null) {
            mNameView.setText("Unknown user");
        } else {
            // Ugly instanceof, case classes would be helpful
            if (mUser instanceof Friend) {
                Friend friend = (Friend) mUser;

                mNameView.setText(friend.getName());
                mSubtitlesView.setText(friend.getSubtitle());
                mPictureView.setImageBitmap(friend.getImage());
                mShowOnMapSwitch.setChecked(friend.isVisible());
                mBlockSwitch.setChecked(friend.isBlocked());
                mDistanceView.setText(Utils.printDistanceToMe(friend.getLocation()));

            } else {
                mNameView.setText(mUser.getName());
                mSubtitlesView.setText(mUser.getSubtitle());
                mPictureView.setImageBitmap(mUser.getImage());
                mShowOnMapSwitch.setVisibility(View.INVISIBLE);
                mBlockSwitch.setVisibility(View.INVISIBLE);
                mDistanceView.setVisibility(View.INVISIBLE);
                // We do not display the remove button as we are not friends yet
                this.findViewById(R.id.user_info_remove_button).setVisibility(View.INVISIBLE);
            }
        }
    }

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.remove) + " " + mUser.getName() + " "
            + this.getString(R.string.from_your_friends));

        // Add positive button
        builder.setPositiveButton(this.getString(R.string.remove), new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(this.getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.user_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.onNotificationOpen();
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showOnMap(View view) {
        // TODO need superfiltre. Don't forget to check that the user is a
        // friend !
        // ServiceContainer.getCache().updateFilter(ServiceContainer.getCache().getFilter(SUPERFILTRE_ID));
    }

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
    }
}