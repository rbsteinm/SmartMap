package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.User;

/**
 * Activity that shows full informations about a Displayable Object.
 * 
 * @author jfperren
 */
public class UserInformationActivity extends Activity {

    private static final String TAG = "INFORMATION_ACTIVITY";
    // Children Views
    private ImageView mPictureView;
    private TextView mNameView;
    private TextView mInfosView;
    private TextView mLoremIpsum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_user_information);
        // Get views
        mPictureView = (ImageView) this.findViewById(R.id.user_info_picture);
        mNameView = (TextView) this.findViewById(R.id.user_info_name);
        mInfosView = (TextView) this.findViewById(R.id.user_info_infos);
        // Get user
        User user = this.getIntent().getParcelableExtra("USER");
        mNameView.setText(user.getName());
        mInfosView.setText(user.getShortInfos());
        mPictureView.setImageBitmap(user.getPicture(this));
        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
        this.getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
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
}
