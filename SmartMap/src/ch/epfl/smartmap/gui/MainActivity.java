package ch.epfl.smartmap.gui;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.MockSearchEngine;
import ch.epfl.smartmap.cache.SearchEngine;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * This Activity displays the core features of the App. It displays the map and the whole menu.
 * 
 * @author jfperren
 */
public class MainActivity extends FragmentActivity implements LocationListener {

    private static final String TAG = "GoogleMap";
    private static final int LOCATION_UPDATE_TIMEOUT = 20000;
    private static final int GOOGLE_PLAY_REQUEST_CODE = 10;
    private static final int GMAP_ZOOM_LEVEL = 15;
    private static final double CENTER_LATTITUDE = 47.20380010;
    private static final double CENTER_LONGITUDE = 2.00168440;
    private static final int LATLNG_BOUNDS = 50;
    private static final int MARKER_ZOOM = 5;

    @SuppressWarnings("unused")
    private DrawerLayout mSideDrawerLayout;
    private String[] mSideLayoutElements;
    private ListView mDrawerListView;
    private GoogleMap mGoogleMap;
    private ProfilePictureFriendMarkerDisplayer mFriendMarkerDisplayer;
    private SearchEngine mSearchEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get needed Views
        final SearchLayout mSearchLayout = (SearchLayout) findViewById(R.id.searchLayout);

        // FIXME : Should be done in DrawerLayout class
        initializeDrawerLayout();
        // Create SearchEngine and give it to SearchPanel
        mSearchEngine = new MockSearchEngine();
        mSearchLayout.initSearchLayout(mSearchEngine);

        if (savedInstanceState == null) {
            displayMap();
        }

        if (mGoogleMap != null) {
            mFriendMarkerDisplayer = new ProfilePictureFriendMarkerDisplayer();
            mFriendMarkerDisplayer.setMarkersToMaps(this, mGoogleMap,
                MockDB.FRIENDS_LIST);
            zoomAccordingToMarkers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        final SearchLayout searchLayout = (SearchLayout) findViewById(R.id.searchLayout);
        
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "querysubmit");
                // Do something when user his enter on keyboard
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "querychange");
                
                searchLayout.updateSearchResults(searchView.getQuery().toString());
                return false;
            }
        });
        
        searchView.setOnSearchClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                searchLayout.open();
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
        zoomMap(location);
        // updatePos
        // mListOfFriends = = listFriendPos();
        // mFriendMarker.setMarkersToMaps(this, mGoogleMap, mListOfFriends);
        // zoomAccordingToMarkers();
    }

    @Override
    public void onBackPressed() {
        final SearchLayout mSearchLayout = (SearchLayout) findViewById(R.id.searchLayout);

        if (mSearchLayout.isShown()) {
            mSearchLayout.close();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called to set up the left side menu. Supposedly called only once.
     */
    private void initializeDrawerLayout() {
        // Get needed Ressources & Views
        mSideLayoutElements = getResources().getStringArray(
            R.array.sideMenuElements);
        mSideDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the listView
        mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
            R.layout.drawer_list_item, mSideLayoutElements));
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }

    public Context getContext() {
        return this;
    }

    public void performQuery(Friend friend) {
        closeSearchPanel();
        zoomMap(friend.getLocation());
        // Add query to the searchEngine
        mSearchEngine.getHistory().addEntry(friend, new Date());
    }

    /**
     * Zoom on the location "location"
     * 
     * @param location
     */
    public void zoomMap(Location location) {
        Log.d(TAG, "zoomMap called");
        LatLng latLng1 = new LatLng(location.getLatitude(),
            location.getLongitude());
        Log.d(TAG, "1");
        // Zoom in the Google Map
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(GMAP_ZOOM_LEVEL));
    }

    public void closeSearchPanel() {
        final SearchLayout mSearchLayout = (SearchLayout) findViewById(R.id.searchLayout);
        mSearchLayout.close();
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
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
            // Getting GoogleMap object from the fragment
            mGoogleMap = fm.getMap();
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
            Location location = locationManager.getLastKnownLocation(provider);
            boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled) {
                Log.d(TAG, "gps enabled");
            }

            if (location != null) {
                onLocationChanged(location);
                zoomMap(location);
                locationManager.requestLocationUpdates(provider,
                    LOCATION_UPDATE_TIMEOUT, 0, this);
            } else {
                Log.d(TAG, "Error retrieving location from GoogleMap Servers.");
            }
        }
    }

    /**
     * Set bound and zoom with regards to all markers positions on the map
     */
    void zoomAccordingToMarkers() {
        final View mapView = getSupportFragmentManager().findFragmentById(
            R.id.map).getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    // We check which build version we are using.
                        @Override
                        public
                        void onGlobalLayout() {
                        LatLng centre = new LatLng(CENTER_LATTITUDE,
                            CENTER_LONGITUDE);
                        LatLngBounds bounds = new LatLngBounds.Builder()
                            .include(centre).build();
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            mapView.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        } else {
                            mapView.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                        }
                        mGoogleMap.moveCamera(CameraUpdateFactory
                            .newLatLngBounds(bounds, LATLNG_BOUNDS));
                        mGoogleMap.animateCamera(CameraUpdateFactory
                            .zoomTo(MARKER_ZOOM));

                    }
                });
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
}
