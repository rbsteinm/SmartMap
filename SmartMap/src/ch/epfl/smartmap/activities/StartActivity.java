package ch.epfl.smartmap.activities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
import ch.epfl.smartmap.gui.FacebookFragment;

import com.facebook.Session;

/**
 * 
 * @author Alain
 * @author SpicyCH
 * 
 */
public class StartActivity extends FragmentActivity {

	private static final String TAG = StartActivity.class.getSimpleName();

	private FacebookFragment mFacebookFragment;
	private ImageView mLogoImage;
	private TextView mWelcomeText;
	private ProgressBar mProgressBar;
	private TextView mProgressText;
	private com.facebook.widget.LoginButton mLoginButton;

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

		// Set background color of activity
		setActivityBackgroundColor(getResources().getColor(
				R.color.mainBlueColor));
		
		

		// Get all views
		mLogoImage = (ImageView) findViewById(R.id.logo);
		mWelcomeText = (TextView) findViewById(R.id.welcome);
		mLoginButton = (com.facebook.widget.LoginButton) findViewById(R.id.loginButton);
		mProgressBar = (ProgressBar) findViewById(R.id.loadingBar);
		mProgressText = (TextView) findViewById(R.id.loadingTextView);

		// Not logged in Facebook or permission to use Facebook in SmartMap not
		// given
		if (Session.getActiveSession() == null
				|| Session.getActiveSession().getPermissions().isEmpty()) {

			// Start logo and text animation
			mLogoImage.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.logo_anim));
			mWelcomeText.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.welcome_anim));

			// Set facebook button's, progress bar's and progress text's
			// visibility to invisible
			mLoginButton.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.INVISIBLE);
			mProgressText.setVisibility(View.INVISIBLE);

			// We set a time out to use postDelayed method
			int timeOut = this.getResources().getInteger(
					R.integer.offset_runnable);

			// Wait for the end of welcome animation before instantiate the
			// facebook fragment and use it
			mWelcomeText.postDelayed(new Runnable() {
				@Override
				public void run() {
					mFacebookFragment = new FacebookFragment();
					getSupportFragmentManager().beginTransaction()
							.add(android.R.id.content, mFacebookFragment)
							.commit();
					Log.d(TAG, "facebook session is open");
				}
			}, timeOut);
		} else {
			// Hide all views except progress bar and text
			mLoginButton.setVisibility(View.INVISIBLE);
			mWelcomeText.setVisibility(View.INVISIBLE);
			mLogoImage.setVisibility(View.INVISIBLE);

			mFacebookFragment = new FacebookFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mFacebookFragment).commit();
		}
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

	/**
	 * Set background color of activity
	 * 
	 * @param color
	 *            the color
	 */
	public void setActivityBackgroundColor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	/**
	 * Checks that the Representation Invariant is not violated.
	 * 
	 * @param depth
	 *            represents how deep the audit check is done (use 1 to check
	 *            this object only)
	 * @return The number of audit errors in this object
	 */
	public int auditErrors(int depth) {
		// TODO : Decomment when auditErrors coded for other classes
		if (depth == 0) {
			return 0;
		}

		int auditErrors = 0;
		// auditErrors += mSearchEngine.auditErrors(depth - 1);
		// What are the rep invariants?

		return auditErrors;
	}
}
