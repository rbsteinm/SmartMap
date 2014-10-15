package ch.epfl.smartmap.gui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.epfl.smartmap.R;

/**
 * @author rbsteinm
 * 
 */
public class MainActivity extends Activity {

    private DrawerLayout sideDrawerLayout;
    private String[] sideLayoutElements;
    private ListView drawerListView;

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

    private void initializeDrawerLayout() {
        sideLayoutElements = getResources().getStringArray(R.array.sideMenuElements);
        sideDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the listView
        drawerListView.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item,
                sideLayoutElements));
        // sideDrawerLayout.setOnItemClickListener(new OnItemClickListener);
        // TODO implement class OnItemClickListener
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
