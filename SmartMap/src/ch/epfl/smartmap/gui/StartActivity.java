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

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Displays the facebook app hash in LOG.d
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
        setContentView(R.layout.activity_start);

        ImageView logoImage = (ImageView) findViewById(R.id.logo);
        TextView welcomeText = (TextView) findViewById(R.id.welcome);

        logoImage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.logo_anim));
        welcomeText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_anim));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();

            // Login button (Robin)
            mainFragment = new MainFragment();
            // TODO exécuter cette ligne après l'animation de bienvenue. OU on choisit d'ouvrir une nouvelle Activity
            // dès que l'anim est terminée
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit(); // Alain
            // tu peux commenter cette ligne pour voir le login button en-haut à gauche avec ton animation
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }
    }
}
