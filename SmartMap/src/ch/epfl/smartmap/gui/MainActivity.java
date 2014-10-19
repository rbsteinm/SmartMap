package ch.epfl.smartmap.gui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.epfl.smartmap.R;

/**
 * @author rbsteinm
 * 
 */
public class MainActivity extends Activity {

    private DrawerLayout mSideDrawerLayout;
    private String[] mSideLayoutElements;
    private ListView mDrawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
        initializeDrawerLayout();
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

    public void initializeDrawerLayout() {
        // Get ressources
        mSideLayoutElements = getResources().getStringArray(R.array.sideMenuElements);
        mSideDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        // Set the adapter for the listView
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                mSideLayoutElements));
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    public Context getContext() {
        return this;
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

    public class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
            case 0:
                // profile
                break;
            case 1:
                // friends
                Intent displayActivityIntent = new Intent(getContext(), FriendsActivity.class);
                startActivity(displayActivityIntent);
                break;
            case 2:
                // Events
                break;
            case 3:
                // Filters
                break;
            case 4:
                // Settings
                break;
            default:
                break;
            }

        }

    }
}
