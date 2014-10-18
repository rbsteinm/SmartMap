package ch.epfl.smartmap.gui;

import ch.epfl.smartmap.R;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author Alain
 * 
 */
public class MainActivity extends Activity {
	private static final float TRANSLATEY = -0.4f;
	private static final int TRANS_DURATION = 2000;
	private static final int FADE_DURATION = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageView logoImage = (ImageView) findViewById(R.id.logo);
		TextView welcomeText = (TextView) findViewById(R.id.welcome);

		// --- LOGO ANIMATION ---

		// Creation of translate animation
		TranslateAnimation translationY = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
				TranslateAnimation.RELATIVE_TO_PARENT, TRANSLATEY);

		// Edit of translate animation's behavior
		translationY.setInterpolator(new LinearInterpolator());
		translationY.setDuration(TRANS_DURATION);
		translationY.setStartOffset(FADE_DURATION);

		// Creation and edit of fade-in animation
		Animation fadeInLogo = new AlphaAnimation(0.0f, 1.0f);
		fadeInLogo.setDuration(FADE_DURATION);

		// Creation of the image's animation set
		AnimationSet logoAnimation = new AnimationSet(true);
		logoAnimation.addAnimation(translationY);
		logoAnimation.addAnimation(fadeInLogo);
		logoAnimation.setFillAfter(true);
		logoImage.setAnimation(logoAnimation);

		// --- WELCOME ANIMATION ---

		// Creation and edit of fade-in animation
		Animation fadeInText = new AlphaAnimation(0.0f, 1.0f);
		fadeInText.setDuration(FADE_DURATION);

		// Creation and edit of fade-out animation
		Animation fadeOutText = new AlphaAnimation(1.0f, 0.0f);
		fadeOutText.setStartOffset(FADE_DURATION);
		fadeOutText.setDuration(FADE_DURATION);

		// Creation of the image's animation set
		AnimationSet welcomeAnimation = new AnimationSet(true);
		welcomeAnimation.addAnimation(fadeInText);
		welcomeAnimation.addAnimation(fadeOutText);
		welcomeAnimation.setFillAfter(true);
		welcomeAnimation.setStartOffset(logoAnimation.computeDurationHint());
		welcomeText.setAnimation(welcomeAnimation);

		/**
		// --- FACEBOOK ANIMATION ---

		// Creation and edit of fade-in animation
		Animation fadeInFB = new AlphaAnimation(0.0f, 1.0f);
		fadeInFB.setDuration(FADE_DURATION);

		// Creation of the image's animation set
		AnimationSet animationFB = new AnimationSet(true);
		welcomeAnimation.addAnimation(fadeInFB);
		welcomeAnimation.setFillAfter(true);
		welcomeAnimation.setStartOffset(logoAnimation.computeDurationHint()
				+ welcomeAnimation.computeDurationHint());
		//TO ADD
		//facebookFragment.setAnimation(animationFB);

		*/
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * Methods that test if it's the first visit on the app
	 * 
	 * @param view
	 */
	public void authentication(View view) {
		// TODO
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
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_map, container,
					false);
			return rootView;
		}
	}
}