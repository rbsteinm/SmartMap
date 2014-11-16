package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.Notifications;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;
import ch.epfl.smartmap.gui.InformationPanel;
import ch.epfl.smartmap.gui.SearchLayout;
import ch.epfl.smartmap.gui.SearchPanel;
import ch.epfl.smartmap.gui.SideMenu;
import ch.epfl.smartmap.map.DefaultEventMarkerDisplayer;
import ch.epfl.smartmap.map.DefaultZoomManager;
import ch.epfl.smartmap.map.EventMarkerDisplayer;
import ch.epfl.smartmap.map.FriendMarkerDisplayer;
import ch.epfl.smartmap.map.ProfilePictureFriendMarkerDisplayer;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * This Activity displays the core features of the App. It displays the map and
 * the whole menu.
 * 
 * @author jfperren
 */
public class MainActivity extends FragmentActivity implements LocationListener, OnTouchListener {

    private static final String TAG = "MAIN_ACTIVITY";
    private static final int LOCATION_UPDATE_TIMEOUT = 10000;
    private static final int GOOGLE_PLAY_REQUEST_CODE = 10;
    private static final int LOCATION_UPDATE_DISTANCE = 10;

    private SideMenu mSideMenu;
    private GoogleMap mGoogleMap;
    private FriendMarkerDisplayer mFriendMarkerDisplayer;
    private EventMarkerDisplayer mEventMarkerDisplayer;
    private DefaultZoomManager mMapZoomer;
    private SupportMapFragment mFragmentMap;
    private SearchEngine mSearchEngine;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get needed Views
        final SearchLayout mSearchLayout = (SearchLayout) findViewById(R.id.search_layout);
        final InformationPanel mInformationPanel = (InformationPanel) findViewById(R.id.information_panel);
        mInformationPanel.displayItem(MockDB.JULIEN);
//        mInformationPanel.extend();
        
        mSideMenu = new SideMenu(this);
        mSideMenu.initializeDrawerLayout();

        mSearchEngine = new MockSearchEngine();
        mSearchLayout.setSearchEngine(mSearchEngine);

        if (savedInstanceState == null) {
            displayMap();
        }
        
        if (mGoogleMap != null) {
            initializeMarkers();
        }
        
        final FrameLayout mMainFrameLayout = (FrameLayout) findViewById(R.id.frame_layout_main);
        final RelativeLayout mMapLayout = (RelativeLayout) findViewById(R.id.layout_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;

        // Get Views
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) searchItem.getActionView();
        final SearchLayout mSearchLayout = (SearchLayout) findViewById(R.id.search_layout);
        final SearchPanel mSearchPanel = (SearchPanel) findViewById(R.id.search_panel);
        final InformationPanel mInformationPanel = (InformationPanel) findViewById(R.id.information_panel);
        
        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Give the query results to searchLayout
                mSearchLayout.updateSearchResults(mSearchView.getQuery()
                    .toString());
                return false;
            }
        });

        mSearchView.setOnSearchClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Sliding Panel and Displays the main search view
                String query = mSearchView.getQuery().toString();
                mSearchPanel.open();
                mSearchLayout.showMainPanel(query);
            }
        });
        // Configure the search info and add any event listeners
        return super.onCreateOptionsMenu(menu);
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
    public void onLocationChanged(Location location) {
        mMapZoomer.zoomOnLocation(location, mGoogleMap);
        // updatePos
    }

    @Override
    public void onBackPressed() {
//        final SearchPanel mSearchPanel = (SearchPanel) findViewById(R.id.search_panel);
//
//        if (mSearchPanel.isShown()) {
//            mSearchPanel.close();
//        } else {
//            super.onBackPressed();
//        }
        final InformationPanel mPanel = (InformationPanel) findViewById(R.id.information_panel);
        mPanel.collapse();
    }

    public Context getContext() {
        return this;
    }

    /**
     * Display the map with the current location
     */
    public void displayMap() {
        int status = GooglePlayServicesUtil
            .isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this,
                GOOGLE_PLAY_REQUEST_CODE);
            dialog.show();
        } else {
            // Google Play Services are available.
            // Getting reference to the SupportMapFragment of activity_main.xml
            mFragmentMap = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
            // Getting GoogleMap object from the fragment
            mGoogleMap = mFragmentMap.getMap();
            // Enabling MyLocation Layer of Google Map
            mGoogleMap.setMyLocationEnabled(true);
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            Log.d(TAG, "provider : " + provider);
            // Getting Current Location
            // Location location =
            // locationManager.getLastKnownLocation(provider);
            boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Log.d(TAG, "gps enabled");
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_UPDATE_TIMEOUT,
                    LOCATION_UPDATE_DISTANCE, this);
            } else if (null != locationManager
                .getProvider(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_TIMEOUT,
                    LOCATION_UPDATE_DISTANCE, this);
            }
        }
    }

    public void initializeMarkers() {
        mEventMarkerDisplayer = new DefaultEventMarkerDisplayer();
        mEventMarkerDisplayer.setMarkersToMaps(this, mGoogleMap,
            MockDB.getEventsList());
        mFriendMarkerDisplayer = new ProfilePictureFriendMarkerDisplayer();
        mFriendMarkerDisplayer.setMarkersToMaps(this, mGoogleMap,
            MockDB.FRIENDS_LIST);
        mMapZoomer = new DefaultZoomManager(mFragmentMap);
        Log.i(TAG, "before enter to zoom according");
        List<Marker> allMarkers = new ArrayList<Marker>(
            mFriendMarkerDisplayer.getDisplayedMarkers());
        allMarkers.addAll(mEventMarkerDisplayer.getDisplayedMarkers());
        Intent startingIntent = getIntent();
        if (startingIntent.getParcelableExtra("location") == null) {
            mMapZoomer.zoomAccordingToMarkers(mGoogleMap, allMarkers);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        // get Intent that started this Activity
        Intent startingIntent = getIntent();
        // get the value of the user string
        Location eventLocation = startingIntent.getParcelableExtra("location");
        if (eventLocation != null) {
            mMapZoomer.zoomOnLocation(eventLocation, mGoogleMap);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String provider) {
        // nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String provider) {
        // nothing
    }

    /**
     * Create a notification that appear in the notifications tab
     */
    public void createNotification(View view) {
        Notifications.createAddNotification(view, this);
    }

    public void performQuery(Friend friend) {
        // Get Views
        final MenuItem mSearchView = (MenuItem) mMenu
            .findItem(R.id.action_search);
        final SearchPanel mSearchPanel = (SearchPanel) findViewById(R.id.search_panel);
        final InformationPanel mInformationPanel = (InformationPanel) findViewById(R.id.information_panel);
        // Close search interface
        mSearchPanel.close();
        mSearchView.collapseActionView();
        // Open information panel
        mInformationPanel.displayItem(friend);
        mInformationPanel.collapse();
        

        mMapZoomer.zoomOnLocation(friend.getLocation(), mGoogleMap);
        // Add query to the searchEngine
        mSearchEngine.getHistory().addEntry(friend, new Date());
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent e) {
        final RelativeLayout mMapLayout = (RelativeLayout) findViewById(R.id.layout_map);
        
        return mMapLayout.onTouchEvent(e);
    }
}
