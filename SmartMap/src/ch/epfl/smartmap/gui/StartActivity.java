package ch.epfl.smartmap.gui;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ch.epfl.smartmap.R;

import com.facebook.Session;

/**
 * 
 * @author Alain
 * @author SpicyCH
 * 
 */
public class StartActivity extends FragmentActivity {

	private FacebookFragment mFacebookFragment;
	private ImageView mLogoImage;
	private TextView mWelcomeText;
	private ProgressBar mProgressBar;
	private FragmentActivity mActivity;
	private com.facebook.widget.LoginButton mFacebookButton;
	private static final String TAG = StartActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// Displays the facebook app hash in LOG.d
		try {
			Log.d(TAG, "Retrieving sha1 app hash...");
			PackageInfo info = getPackageManager().getPackageInfo(
					"ch.epfl.smartmap", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d(TAG,
						"SHA1 hash of the app : "
								+ Base64.encodeToString(md.digest(),
										Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG,
					"Cannot retrieve the sha1 hash for this app (used by fb)");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG,
					"Cannot retrieve the sha1 hash for this app (used by fb)");
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		mLogoImage = (ImageView) findViewById(R.id.logo);
		mWelcomeText = (TextView) findViewById(R.id.welcome);
		mFacebookButton = (com.facebook.widget.LoginButton) findViewById(R.id.authButton);
		mProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
		mActivity = this;
		
		
		// Start logo and text animation
		mLogoImage.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.logo_anim));
		mWelcomeText.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.welcome_anim));

		// Set facebook button's and progress bar's visibility to invisible
		mFacebookButton.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);

		// We set a time out to use postDelayed method
		int timeOut = this.getResources().getInteger(R.integer.offset_runnable);

		// Wait for the end of facebook animation before testing if already
		// logged in or not
		mFacebookButton.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (Session.getActiveSession() == null) {
					Log.d(TAG, "facebook session is open");
					// Start animation on facebook Button (button always
					// appear.. ISSUE #9)
					// Login button
					mFacebookFragment = new FacebookFragment();
					getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, mFacebookFragment)
							.commit();
				} else {
					Log.d(TAG, "facebook session is closed");
					mProgressBar.setVisibility(View.VISIBLE);
					
					// Or set the fragment from restored state info
					mFacebookFragment = (FacebookFragment) getSupportFragmentManager()
							.findFragmentById(android.R.id.content);
					
					// start the next activity (MainActivity)
					Intent intent = new Intent(mActivity,
							MainActivity.class);
					startActivity(intent);
				}
			}
		}, timeOut);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
