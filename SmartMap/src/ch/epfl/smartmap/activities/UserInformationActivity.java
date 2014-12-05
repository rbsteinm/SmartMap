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
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author jfperren
 * @author rbsteinm
 */
public class UserInformationActivity extends Activity {

    @SuppressWarnings("unused")
    private static final String TAG = UserInformationActivity.class.getSimpleName();

    private Switch mFollowSwitch;
    private TextView mInfosView;
    private TextView mNameView;

    // Children Views
    private ImageView mPictureView;
    private User mUser;
    // TODO replace this by mUser.isFollowing() when implemented
    private final boolean isFollowing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_information);
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mInfosView = (TextView) this.findViewById(R.id.user_info_infos);
        mFollowSwitch = (Switch) this.findViewById(R.id.user_info_follow_switch);
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get User & Database
        mUser = ServiceContainer.getCache().getFriend(this.getIntent().getLongExtra("USER", User.NO_ID));

        // Set Informations
        mNameView.setText(mUser.getName());
        mInfosView.setText(mUser.getSubtitle());
        mPictureView.setImageBitmap(mUser.getImage());
        mFollowSwitch.setChecked(isFollowing);
    }

    public void displayDeleteConfirmationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("remove " + mUser.getName() + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // TODO use cache instead
                // new RemoveFriend().execute(mUser.getId());
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
        if (this.getIntent().getBooleanExtra("NOTIFICATION", false) == true) {
            this.startActivity(new Intent(this, MainActivity.class));
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (this.getIntent().getBooleanExtra("NOTIFICATION", false) == true) {
                    this.startActivity(new Intent(this, MainActivity.class));
                }
                this.finish();
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Asynchronous task that removes a friend from the users friendList both
     * from the server and from the cache
     * 
     * @author rbsteinm
     */
    // private class RemoveFriend extends AsyncTask<Long, Void, String> {
    //
    // private final Handler mHandler = new Handler();
    //
    // @Override
    // protected String doInBackground(Long... params) {
    // String confirmString = "";
    // try {
    // NetworkSmartMapClient.getInstance().removeFriend(params[0]);
    // confirmString = "You're no longer friend with " + mUser.getName();
    //
    // // remove friend from cache and update displayed list
    // // TODO should it be done in the removeFriend method ?
    // final long userId = params[0];
    // mHandler.post(new Runnable() {
    // @Override
    // public void run() {
    // Cache.getInstance().removeFriend(userId);
    // }
    // });
    //
    // } catch (SmartMapClientException e) {
    // confirmString = "Network error, operation failed";
    // }
    // return confirmString;
    // }
    //
    // @Override
    // protected void onPostExecute(String confirmString) {
    // Toast.makeText(UserInformationActivity.this, confirmString,
    // Toast.LENGTH_LONG).show();
    // }
    // }
}
