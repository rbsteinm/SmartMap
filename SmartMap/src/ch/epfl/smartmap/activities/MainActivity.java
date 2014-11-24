package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.Notifications;
import ch.epfl.smartmap.background.Notifications.NotificationListener;
import ch.epfl.smartmap.background.UpdateService;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.Displayable;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.SearchLayout;
import ch.epfl.smartmap.gui.SideMenu;
import ch.epfl.smartmap.gui.SlidingPanel;
import ch.epfl.smartmap.gui.Utils;
import ch.epfl.smartmap.listeners.AddEventOnMapLongClickListener;
import ch.epfl.smartmap.listeners.OnDisplayableInformationsChangeListener;
import ch.epfl.smartmap.listeners.OnFriendsLocationUpdateListener;
import ch.epfl.smartmap.map.DefaultMarkerManager;
import ch.epfl.smartmap.map.DefaultZoomManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * This Activity displays the core features of the App. It displays the map and
 * the whole menu.
 * It is a FriendsLocationListener to update the markers on the map when friends positions
 * change
 * 
 * @author jfperren
 * @author hugo-S
 * @author SpicyCH
 * @author agpmilli
 */

public class MainActivity extends FragmentActivity implements OnFriendsLocationUpdateListener {

    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GOOGLE_PLAY_REQUEST_CODE = 10;

    private static final int MENU_ITEM_SEARCHBAR_INDEX = 0;
    private static final int MENU_ITEM_NOTIFICATION_INDEX = 1;
    private static final int MENU_ITEM_CLOSE_SEARCH_INDEX = 2;
    private static final int MENU_ITEM_OPEN_INFO_INDEX = 3;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private DatabaseHelper mDbHelper;
    private SideMenu mSideMenu;
    private GoogleMap mGoogleMap;
    private DefaultMarkerManager<User> mFriendMarkerManager;
    private DefaultMarkerManager<Event> mEventMarkerManager;
    private DefaultZoomManager mMapZoomer;
    private SupportMapFragment mFragmentMap;

    private Menu mMenu;
    private MenuTheme mMenuTheme;
    private Displayable mCurrentItem;

    private LayerDrawable mIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // starting the background service
        this.startService(new Intent(this, UpdateService.class));

        // TODO resolve main activity test ?
        mDbHelper = DatabaseHelper.initialize(this.getApplicationContext());
        mDbHelper.addOnFriendsLocationUpdateListener(this);

        // Set actionbar color
        this.getActionBar().setBackgroundDrawable(
            new ColorDrawable(this.getResources().getColor(R.color.main_blue)));
        this.getActionBar().setHomeButtonEnabled(true);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        mMenuTheme = MenuTheme.MAP;

        // Get needed Views
        mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) this.findViewById(R.id.left_drawer_listView);

        mSideMenu = new SideMenu(this.getContext());
        mSideMenu.initializeDrawerLayout();

        mDbHelper = DatabaseHelper.getInstance();
        if (mDbHelper == null) {
            DatabaseHelper.initialize(this);
        }

        if (savedInstanceState == null) {
            this.displayMap();
        }

        if (mGoogleMap != null) {
            // Set different tools for the GoogleMap
            mEventMarkerManager = new DefaultMarkerManager<Event>(mGoogleMap);
            mFriendMarkerManager = new DefaultMarkerManager<User>(mGoogleMap);
            mMapZoomer = new DefaultZoomManager(mFragmentMap);

            this.initializeMarkers();

            // Add listeners to the GoogleMap
            mGoogleMap.setOnMapLongClickListener(new AddEventOnMapLongClickListener(this));
            mGoogleMap.setOnMarkerClickListener(new ShowInfoOnMarkerClick());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO A method to unregister to the service when the map is not opened?
        // this.unregisterReceiver(mBroadcastReceiver);
        // stopService(mUpdateServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // startService(mUpdateServiceIntent);
        // this.registerReceiver(mBroadcastReceiver, new IntentFilter(UpdateService.BROADCAST_POS));
        mGoogleMap.setOnMapLongClickListener(new AddEventOnMapLongClickListener(this));
        // get Intent that started this Activity
        Intent startingIntent = this.getIntent();
        // get the value of the user string
        Location eventLocation = startingIntent.getParcelableExtra("location");
        if (eventLocation != null) {
            mMapZoomer.zoomOnLocation(new LatLng(eventLocation.getLatitude(), eventLocation.getLongitude()));
            eventLocation = null;
        }

        // Set menu Style
        switch (mMenuTheme) {
            case SEARCH:
                this.setSearchMenu();
                break;
            case ITEM:
                this.setItemMenu(mCurrentItem);
                break;
            case MAP:
                break;
            default:
                assert false;
        }

        if (mGoogleMap != null) {
            mGoogleMap.setOnMapLongClickListener(new AddEventOnMapLongClickListener(this));

        }

    }

    /**
     * Create a notification that appear in the notifications tab
     */
    public void createNotification(View view) {
        // Notifications.createAddNotification(view, this);
    }

    /**
     * Display the map with the current location
     */
    public void displayMap() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are
            // not available
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, GOOGLE_PLAY_REQUEST_CODE);
            dialog.show();
        } else {
            // Google Play Services are available.
            // Getting reference to the SupportMapFragment of activity_main.xml
            mFragmentMap = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
            // Getting GoogleMap object from the fragment
            mGoogleMap = mFragmentMap.getMap();
            // Enabling MyLocation Layer of Google Map
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    public Context getContext() {
        return this;
    }

    @Override
    public void onBackPressed() {
        switch (mMenuTheme) {
            case MAP:
                super.onBackPressed();
                break;
            case SEARCH:
            case ITEM:
                this.setMainMenu();
                break;
            default:
                assert false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu);

        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_notifications);
        mIcon = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
        Utils.setBadgeCount(this, mIcon,
            Notifications.getNumberOfEventNotification() + Notifications.getNumberOfFriendNotification());

        Notifications.addNotificationListener(new NotificationListener() {
            @Override
            public void onNewNotification() {
                // Update LayerDrawable's BadgeDrawable
                Utils.setBadgeCount(MainActivity.this, mIcon, Notifications.getNumberOfEventNotification()
                    + Notifications.getNumberOfFriendNotification());
            }
        });

        mMenu = menu;
        // Get Views
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) searchItem.getActionView();
        final SearchLayout mSearchLayout = (SearchLayout) this.findViewById(R.id.search_layout);
        final MainActivity thisActivity = this;

        searchItem.setOnActionExpandListener(new OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                thisActivity.setMainMenu();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Open Sliding Panel and Displays the main search view
                thisActivity.setSearchMenu();
                mSearchLayout.resetView("");
                return true;
            }
        });

        mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                // Give the query results to searchLayout
                mSearchLayout.setSearchQuery(mSearchView.getQuery().toString());
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchView.clearFocus();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     * @see ch.epfl.smartmap.cache.FriendsLocationListener#onChange()
     */
    @Override
    public void onFriendsLocationChange() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFriendMarkerManager.updateMarkers(MainActivity.this.getContext(), mDbHelper.getAllFriends());
            }
        });
    }

    public void onLocationChanged(Location location) {
        SettingsManager.getInstance().setLocation(location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get ID of MenuItem
        int id = item.getItemId();
        // Handle Click according to ID
        switch (id) {
            case android.R.id.home:
                switch (mMenuTheme) {
                    case MAP:
                        if (mDrawerList.isShown()) {
                            mDrawerLayout.closeDrawer(mDrawerList);
                        } else {
                            mDrawerLayout.openDrawer(mDrawerList);
                        }
                        break;
                    case SEARCH:
                        this.setMainMenu();
                        break;
                    case ITEM:
                        mMapZoomer.zoomOnLocation(mCurrentItem.getLatLng());
                        break;
                    default:
                        assert false;
                }
                break;
            case R.id.action_notifications:
                Notifications.setNumberOfUnreadEventNotification(0);
                Notifications.setNumberOfUnreadFriendNotification(0);
                Intent pNotifIntent = new Intent(this, NotificationsActivity.class);
                this.startActivity(pNotifIntent);
                return true;
            case R.id.action_hide_search:
                this.setMainMenu();
                return true;
            case R.id.action_item_more:
                this.openInformationActivity();
                return true;
            default:
                assert false;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens Information Activity (called from MenuItem on Item view)
     * 
     * @author jfperren
     */
    public void openInformationActivity() {
        if (mCurrentItem instanceof User) {
            Intent intent = new Intent(this, UserInformationActivity.class);
            intent.putExtra("USER", (User) mCurrentItem);
            this.startActivity(intent);
        }
    }

    /**
     * Computes the changes needed when a query is sent.
     * 
     * @param friend
     */
    public void performQuery(Displayable item) {
        // Focus on Friend
        mMapZoomer.zoomOnLocation(item.getLatLng());
        this.setItemMenu(item);
    }

    /**
     * Sets the view for Item Focus, this means - Write name / Display photo on
     * ActionBar - Sets ActionMenu for Item Focus
     * 
     * @param item
     *            Item to be displayed
     */
    public void setItemMenu(final Displayable item) {
        final SlidingPanel mSearchPanel = (SlidingPanel) this.findViewById(R.id.search_panel);
        // Closes panel and change only if panel could close
        if (mSearchPanel.close() || mSearchPanel.isClosed()) {
            // Set visibility of MenuItems
            mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).getActionView().clearFocus();
            mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).collapseActionView();
            mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).setVisible(false);
            mMenu.getItem(MENU_ITEM_NOTIFICATION_INDEX).setVisible(false);
            mMenu.getItem(MENU_ITEM_CLOSE_SEARCH_INDEX).setVisible(false);
            mMenu.getItem(MENU_ITEM_OPEN_INFO_INDEX).setVisible(true);
            // Change ActionBar title and icon
            ActionBar actionBar = this.getActionBar();
            actionBar.setTitle(item.getName());
            actionBar.setSubtitle(item.getShortInfos());
            actionBar.setIcon(new BitmapDrawable(this.getResources(), item.getPicture(this)));
            // ActionBar HomeIndicator
            actionBar.setHomeAsUpIndicator(null);

            mCurrentItem = item;
            mMenuTheme = MenuTheme.ITEM;

            mDbHelper.addOnDisplayableInformationsChangeListener(item,
                new OnDisplayableInformationsChangeListener() {

                    @Override
                    public void onDisplayableInformationsChange() {
                        if (item instanceof Friend) {
                            MainActivity.this.mCurrentItem = mDbHelper.getUser(item.getID());
                            ActionBar actionBar = MainActivity.this.getActionBar();
                            actionBar.setTitle(item.getName());
                            actionBar.setSubtitle(item.getShortInfos());
                            actionBar.setIcon(new BitmapDrawable(MainActivity.this.getResources(), item
                                .getPicture(MainActivity.this)));
                        }
                    }
                });
        }
    }

    /**
     * Sets the main Menu of the Activity
     */
    public void setMainMenu() {
        final SlidingPanel mSearchPanel = (SlidingPanel) this.findViewById(R.id.search_panel);
        // Closes panel and change only if panel could close
        if (mSearchPanel.close() || mSearchPanel.isClosed()) {
            // Collapse searchBar if needed
            if ((mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).getActionView() != null)
                && mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).isActionViewExpanded()) {
                mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).collapseActionView();
            }
            // Set visibility of MenuItems
            mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).setVisible(true);
            mMenu.getItem(MENU_ITEM_NOTIFICATION_INDEX).setVisible(true);
            mMenu.getItem(MENU_ITEM_CLOSE_SEARCH_INDEX).setVisible(false);
            mMenu.getItem(MENU_ITEM_OPEN_INFO_INDEX).setVisible(false);
            // Change ActionBar title and icon
            ActionBar actionBar = this.getActionBar();
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(null);
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setHomeAsUpIndicator(this.getResources().getDrawable(R.drawable.ic_drawer));
            mMenuTheme = MenuTheme.MAP;
        }
    }

    public void setSearchMenu() {
        final SlidingPanel mSearchPanel = (SlidingPanel) this.findViewById(R.id.search_panel);
        // Closes panel and change only if panel could close
        if (mSearchPanel.open()) {
            // Set visibility of MenuItems
            mMenu.getItem(MENU_ITEM_SEARCHBAR_INDEX).setVisible(true);
            mMenu.getItem(MENU_ITEM_NOTIFICATION_INDEX).setVisible(false);
            mMenu.getItem(MENU_ITEM_CLOSE_SEARCH_INDEX).setVisible(true);
            mMenu.getItem(MENU_ITEM_OPEN_INFO_INDEX).setVisible(false);
            // Change ActionBar title and icon
            ActionBar actionBar = this.getActionBar();
            actionBar.setTitle(R.string.app_name);
            actionBar.setSubtitle(null);
            actionBar.setIcon(R.drawable.ic_launcher);
            mMenuTheme = MenuTheme.SEARCH;
        }
    }

    private void initializeMarkers() {
        mEventMarkerManager.updateMarkers(this, mDbHelper.getAllEvents());
        mFriendMarkerManager.updateMarkers(this, mDbHelper.getAllFriends());

        List<Marker> allMarkers = new ArrayList<Marker>(mFriendMarkerManager.getDisplayedMarkers());
        allMarkers.addAll(mEventMarkerManager.getDisplayedMarkers());
        Intent startingIntent = this.getIntent();
        if (startingIntent.getParcelableExtra("location") == null) {
            mMapZoomer.zoomAccordingToMarkers(allMarkers);
        }
    }

    /**
     * Types of Menu that can be displayed on this activity
     * 
     * @author jfperren
     */
    private enum MenuTheme {
        MAP,
        SEARCH,
        ITEM;
    }

    /**
     * A listener that shows info in action bar when a marker is clicked on
     * 
     * @author hugo-S
     */
    private class ShowInfoOnMarkerClick implements OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker arg0) {

            if (mFriendMarkerManager.isDisplayedMarker(arg0)) {
                Displayable friendClicked = mFriendMarkerManager.getItemForMarker(arg0);
                mMapZoomer.zoomOnLocation(arg0.getPosition());
                MainActivity.this.setItemMenu(friendClicked);
                return true;
            } else if (mEventMarkerManager.isDisplayedMarker(arg0)) {
                Displayable eventClicked = mEventMarkerManager.getItemForMarker(arg0);
                mMapZoomer.zoomOnLocation(arg0.getPosition());
                MainActivity.this.setItemMenu(eventClicked);
                return true;
            }
            return false;
        }

    }
}