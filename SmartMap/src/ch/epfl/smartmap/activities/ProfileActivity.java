package ch.epfl.smartmap.activities;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;

/**
 * this Activity represents user's own profile
 * 
 * @author rbsteinm
 */
public class ProfileActivity extends Activity {

    private TextView mNameView;
    private TextView mSubtitlesView;
    private ImageView mPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_profile);

        this.getActionBar().setBackgroundDrawable(new ColorDrawable(this.getResources().getColor(R.color.main_blue)));

        mNameView = (TextView) this.findViewById(R.id.profile_name);
        mSubtitlesView = (TextView) this.findViewById(R.id.profile_subtitles);
        mPicture = (ImageView) this.findViewById(R.id.profile_picture);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.profile, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        mNameView.setText(ServiceContainer.getSettingsManager().getUserName());
        mSubtitlesView.setText(ServiceContainer.getSettingsManager().getSubtitle());
        mPicture.setImageBitmap(ServiceContainer.getCache().getUser(ServiceContainer.getSettingsManager().getUserId())
            .getImage());

    }
}
