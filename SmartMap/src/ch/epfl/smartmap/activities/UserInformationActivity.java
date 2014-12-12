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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.listeners.OnCacheListener;
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
    private long mUserId;
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

        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onFilterListUpdate() {
                User user = ServiceContainer.getCache().getUser(mUserId);
                UserInformationActivity.this.updateInformations(user);
            }

            @Override
            public void onUserListUpdate() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set user informations
        mUserId = this.getIntent().getLongExtra("USER", User.NO_ID);
        User user = ServiceContainer.getCache().getUser(mUserId);

        this.updateInformations(user);
    }

    /**
     * Display a confirmation dialog
     * 
     * @param name
     * @param userId
     */
    public void displayConfirmationDialog(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.add) + " " + mUser.getName() + " "
            + this.getResources().getString(R.string.as_a_friend));

        // Add positive button
        builder.setPositiveButton(this.getResources().getString(R.string.add),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // invite friend
                    UserInformationActivity.this.inviteUser(mUserId);
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
     * Invites a user to be your friend. Displays a toast describing if the
     * invitation was sent or not.
     * 
     * @author agpmilli
     */
    private void inviteUser(long userId) {
        // Send friend request to user
        ServiceContainer.getCache().inviteUser(userId, new AddFriendCallback());

    }

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
    }

    /**
     * Updates the information displayed with a new user
     * 
     * @param user
     */
    private void updateInformations(final User user) {

        UserInformationActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mUser = user;

                // Defensive case, should never happen
                if (user == null) {
                    mNameView.setText("Unknown user");

                    mShowOnMapSwitch.setVisibility(View.INVISIBLE);
                    mBlockSwitch.setVisibility(View.INVISIBLE);
                    mDistanceView.setVisibility(View.INVISIBLE);

                    UserInformationActivity.this.findViewById(R.id.user_info_remove_button).setVisibility(
                        View.INVISIBLE);
                } else {
                    // Ugly instanceof, case classes would be helpful
                    if (user instanceof Friend) {
                        Friend friend = (Friend) user;

                        mNameView.setText(friend.getName());
                        mSubtitlesView.setText(friend.getSubtitle());
                        mPictureView.setImageBitmap(friend.getImage());
                        mShowOnMapSwitch.setChecked(ServiceContainer.getCache().getDefaultFilter()
                            .getIds().contains(user.getId()));
                        mBlockSwitch.setChecked(friend.isBlocked());
                        mDistanceView.setText(Utils.printDistanceToMe(friend.getLocation()));

                        Button button =
                            (Button) UserInformationActivity.this.findViewById(R.id.user_info_remove_button);
                        button.setVisibility(View.VISIBLE);
                        button.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                UserInformationActivity.this.displayDeleteConfirmationDialog(v);
                            }
                        });
                        button.setText(UserInformationActivity.this.getResources().getString(
                            R.string.remove_friend_button_text));
                        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_discard_white, 0,
                            0, 0);
                    } else {
                        mNameView.setText(user.getName());
                        mSubtitlesView.setText(user.getSubtitle());
                        mPictureView.setImageBitmap(user.getImage());
                        mShowOnMapSwitch.setVisibility(View.INVISIBLE);
                        mBlockSwitch.setVisibility(View.INVISIBLE);
                        mDistanceView.setVisibility(View.INVISIBLE);
                        // We do not display the remove button as we are not friends yet
                        Button button =
                            (Button) UserInformationActivity.this.findViewById(R.id.user_info_remove_button);
                        button.setVisibility(View.VISIBLE);
                        button.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                UserInformationActivity.this.displayConfirmationDialog(v);
                            }
                        });
                        button.setText(UserInformationActivity.this.getResources().getString(
                            R.string.add_friend_button_text));
                        button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    }
                }
            }
        });
    }

    /**
     * Callback that describes connection with network
     * 
     * @author agpmilli
     */
    class AddFriendCallback implements NetworkRequestCallback {
        @Override
        public void onFailure() {
            UserInformationActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserInformationActivity.this,
                        UserInformationActivity.this.getString(R.string.invite_friend_failure),
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
                        UserInformationActivity.this.getString(R.string.invite_friend_success),
                        Toast.LENGTH_SHORT).show();
                    UserInformationActivity.this.finish();
                }
            });
        }
    }

}