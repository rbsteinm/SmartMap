package ch.epfl.smartmap.gui;

/*<<<<<<< HEAD
 import java.security.MessageDigest;
 import java.security.NoSuchAlgorithmException;;

 import android.content.pm.PackageInfo;
 import android.content.pm.PackageManager;
 import android.content.pm.PackageManager.NameNotFoundException;
 import android.content.pm.Signature;
 import android.os.Bundle;
 import android.support.v4.app.FragmentActivity;
 import android.util.Base64;
 import android.util.Log;

 /**
 *
 * @author SpicyCH
 *
 */
/*
 public class StartActivity extends FragmentActivity {

 private MainFragment mainFragment;

 @Override
 public void onCreate(Bundle savedInstanceState) {

 try {
 PackageInfo info = getPackageManager().getPackageInfo("ch.epfl.smartmap", PackageManager.GET_SIGNATURES);
 for (Signature signature : info.signatures) {
 MessageDigest md = MessageDigest.getInstance("SHA");
 md.update(signature.toByteArray());
 Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
 }
 } catch (NameNotFoundException e) {

 } catch (NoSuchAlgorithmException e) {

 }

 super.onCreate(savedInstanceState);

 if (savedInstanceState == null) {
 // Add the fragment on initial activity setup
 mainFragment = new MainFragment();

 getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
 } else {
 // Or set the fragment from restored state info
 mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
 }
 }
 }
 =======*/
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import ch.epfl.smartmap.R;

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
    private com.facebook.widget.LoginButton mFacebookButton;
    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Displays the facebook app hash in LOG.d
        try {
            Log.d(TAG, "Retrieving sha1 app hash...");
            PackageInfo info = getPackageManager().getPackageInfo("ch.epfl.smartmap", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "SHA1 hash of the app : " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Cannot retrieve the sha1 hash for this app (used by fb)");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Cannot retrieve the sha1 hash for this app (used by fb)");
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mLogoImage = (ImageView) findViewById(R.id.logo);
        mWelcomeText = (TextView) findViewById(R.id.welcome);
        mFacebookButton = (com.facebook.widget.LoginButton) findViewById(R.id.authButton);

        mLogoImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_anim));
        mWelcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_anim));
        
        // Start animation on facebook Button ( IL APPARAITERA DE TOUTE FACON.. PROBLEME ? )
        mFacebookButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.face_anim));

        // We set a time out to use postDelayed method
        int timeOut = this.getResources().getInteger(R.integer.offset_runnable);

        // Wait for the end of facebook animation before testing if already logged in or not
        mFacebookButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mFacebookFragment == null) { //TODO savedInstanceState ? -> (Alain) pour moi on peut l'enlever le TODO

                    // Login button
                    mFacebookFragment = new FacebookFragment();
                    getSupportFragmentManager().beginTransaction().add(android.R.id.content, mFacebookFragment).commit();
                } else {
                    // Or set the fragment from restored state info
                    mFacebookFragment = (FacebookFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
                }
            }
        }, timeOut);
        // TODO http://stackoverflow.com/questions/5321344/android-animation-wait-until-finished
    }
    
    public Context getContext() {
    	return this;
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
