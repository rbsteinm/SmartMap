package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
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
import ch.epfl.smartmap.cache.UserContainer;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.listeners.OnCacheListener;
import ch.epfl.smartmap.util.Utils;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author rbsteinm
 */
public class UserInformationActivity extends Activity {

    /**
     * Callback that describes connection with network
     * 
     * @author agpmilli
     */
    class AddFriendCallback implements NetworkRequestCallback<Void> {
        @Override
        public void onFailure(Exception e) {
            UserInformationActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserInformationActivity.this,
                        UserInformationActivity.this.getString(R.string.invite_friend_failure), Toast.LENGTH_SHORT)
                        .show();
                }
            });
        }

        @Override
        public void onSuccess(Void result) {
            UserInformationActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserInformationActivity.this,
                        UserInformationActivity.this.getString(R.string.invite_friend_success), Toast.LENGTH_SHORT)
                        .show();
                    UserInformationActivity.this.finish();
                }
            });
        }
    }

    private static final String TAG = UserInformationActivity.class.getSimpleName();
    private User mUser;
    private long mUserId;
    private boolean mIsVisible;
    private Switch mShowOnMapSwitch;
    private Switch mBlockSwitch;
    private TextView mSubtitlesView;
    private TextView mNameView;
    private ImageView mPictureView;
    private TextView mDistanceView;
    private static final boolean SHOW_ON_MAP_ENABLED = false;
    private static final boolean BLOCK_ENABLED = false;

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

        //add cache listener
        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onFilterListUpdate() {
                User user = ServiceContainer.getCache().getUser(mUserId);
                UserInformationActivity.this.updateInformations(user);
            }

            @Override
            public void onUserListUpdate() {
                User user = ServiceContainer.getCache().getUser(mUserId);
                UserInformationActivity.this.updateInformations(user);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set user informations
        mUserId = this.getIntent().getLongExtra("USER", User.NO_ID);
        mUser = ServiceContainer.getCache().getUser(mUserId);
        this.updateInformations(mUser);

        //Note: these two functionalities caused several problems, so we decided we
        //wouldn't implement them yet. Will be fixed before google play release
        if (!SHOW_ON_MAP_ENABLED) {
            mShowOnMapSwitch.setVisibility(View.GONE);
        }
        if (!BLOCK_ENABLED) {
            mBlockSwitch.setVisibility(View.GONE);
        }

    }


    /**
     * Display a confirmation dialog to send a friend request to a non-friend
     * user
     * 
     * @param name
     * @param userId
     */
    public void displayConfirmationDialog(View v, final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.add) + " " + mUser.getName() + " "
            + this.getResources().getString(R.string.as_a_friend));

        // Add positive button
        builder.setPositiveButton(this.getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ServiceContainer.getCache().inviteUser(userId, new AddFriendCallback());
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

    /**
     * displays a confirmation dialog when the user tries to
     * remove a friend from his friendlist
     * 
     * @param view
     */
    public void displayRemoveFriendConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.remove) + " " + mUser.getName() + " "
            + this.getString(R.string.from_your_friends));

        // Add positive button
        builder.setPositiveButton(this.getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ServiceContainer.getCache().removeFriend(mUser.getId(), new NetworkRequestCallback<Void>() {
                    @Override
                    public void onFailure(Exception e) {
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
                    public void onSuccess(Void result) {
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

    /**
     * When this tab is open by a notification
     */
    private void onNotificationOpen() {
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false)) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
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


    /**
     * called when switching the "block" switch
     * blocks the concerned friend and disables the "show on map" button
     * 
     * @param view
     */
    public void setBlockedStatus(View view) {
        UserContainer modified = mUser.getContainerCopy();
        if (mBlockSwitch.isChecked()) {
            Log.d(TAG, "blocking user");
            modified.setBlocked(User.BlockStatus.BLOCKED);
        } else {
            modified.setBlocked(User.BlockStatus.UNBLOCKED);
            Log.d(TAG, "unblocking user");
        }

        ServiceContainer.getCache().setBlockedStatus(modified, new NetworkRequestCallback<Void>() {

            @Override
            public void onFailure(Exception e) {
                UserInformationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBlockSwitch.setChecked(UserInformationActivity.this.statusToBool(mUser.getBlockStatus()));
                        Toast.makeText(UserInformationActivity.this, UserInformationActivity.
                            this.getString(R.string.blocking_failure), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(Void result) {
                UserInformationActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mShowOnMapSwitch.setEnabled(!UserInformationActivity.this.statusToBool(mUser.getBlockStatus()));
                        if (UserInformationActivity.this.statusToBool(mUser.getBlockStatus())) {
                            Toast.makeText(UserInformationActivity.this, UserInformationActivity.
                                this.getString(R.string.friend_blocked),
                                Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UserInformationActivity.this, UserInformationActivity.
                                this.getString(R.string.friend_unblocked),
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

    }

    /**
     * called when switching the "show on map" switch
     * when ON, the concerned user does not appear on the map anymore
     * no matter in which filters he is
     * pay attention: the default filter works in a reverse-fashion
     * compared to the other classical filters, which means the users contained
     * in it are NOT displayed on the map
     * 
     * @param view
     */
    public void showOnMap(View view) {
        String toastString = "";
        if (!ServiceContainer.getCache().getDefaultFilter().getVisibleFriends().contains(mUser)) {
            ServiceContainer.getCache().putFilter(
                ServiceContainer.getCache().getDefaultFilter().getContainerCopy().addId(mUserId));
            toastString = UserInformationActivity.this.getString(R.string.user_not_shown_on_map);
        } else {
            ServiceContainer.getCache().putFilter(
                ServiceContainer.getCache().getDefaultFilter().getContainerCopy().removeId(mUserId));
            toastString = UserInformationActivity.this.getString(R.string.user_shown_on_map);
        }
        Toast.makeText(UserInformationActivity.this, toastString, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param status
     *            blocked status
     * @return true if the user is blocked,false if unblocked or unset
     *         unset should never happen
     */
    private boolean statusToBool(User.BlockStatus status) {
        return status == User.BlockStatus.BLOCKED;
    }

    /**
     * Updates the user's informations when we resume the activity
     * called in the onResume() method
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
                    mNameView.setText(UserInformationActivity.this.getString(R.string.unknown_user));

                    mShowOnMapSwitch.setVisibility(View.INVISIBLE);
                    mBlockSwitch.setVisibility(View.INVISIBLE);
                    mDistanceView.setVisibility(View.INVISIBLE);

                    UserInformationActivity.this.findViewById(R.id.user_info_remove_button).setVisibility(
                        View.INVISIBLE);
                } else {
                    if (user.getFriendship() == User.FRIEND) {
                        Friend friend = (Friend) user;

                        mNameView.setText(friend.getName());
                        mSubtitlesView.setText(friend.getSubtitle());
                        mPictureView.setImageBitmap(friend.getActionImage());
                        mIsVisible = !ServiceContainer.getCache().
                            getDefaultFilter().getVisibleFriends().contains(mUser);
                        mShowOnMapSwitch.setChecked(mIsVisible);
                        mBlockSwitch.setChecked(UserInformationActivity.this.statusToBool(friend.getBlockStatus()));
                        mDistanceView.setText(Utils.printDistanceToMe(friend.getLocation()));

                        Button button =
                            (Button) UserInformationActivity.this.findViewById(R.id.user_info_remove_button);
                        button.setVisibility(View.VISIBLE);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserInformationActivity.this.displayRemoveFriendConfirmationDialog(v);
                            }
                        });
                        button.setText(UserInformationActivity.this.getResources().getString(
                            R.string.remove_friend_button_text));
                        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_discard_white, 0, 0, 0);
                    } else {
                        mNameView.setText(user.getName());
                        mSubtitlesView.setText(user.getSubtitle());
                        mPictureView.setImageBitmap(user.getActionImage());
                        mShowOnMapSwitch.setVisibility(View.INVISIBLE);
                        mBlockSwitch.setVisibility(View.INVISIBLE);
                        mDistanceView.setVisibility(View.INVISIBLE);
                        // We do not display the remove button as we are not
                        // friends yet
                        Button button =
                            (Button) UserInformationActivity.this.findViewById(R.id.user_info_remove_button);
                        button.setVisibility(View.VISIBLE);
                        button.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserInformationActivity.this.displayConfirmationDialog(v, user.getId());
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
}