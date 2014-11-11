package ch.epfl.smartmap.gui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;
import ch.epfl.smartmap.R;

/**
 * represents the side menu drawer
 * @author rbsteinm
 */
public class SideMenu extends DrawerLayout {

    private final String[] mListItemsNames;
    private ListView mDrawerListView;
    private final Context mContext;
    
    
    /**
     * @param context context of the activity where we want to display the menu
     * @param drawerListView listView to populate with the View items of the menu
     */
    public SideMenu(Context context) {
        super(context);
        mListItemsNames = getResources().getStringArray(R.array.sideMenuElements);
        mDrawerListView = (ListView) ((Activity) context).findViewById(R.id.left_drawer);
        mContext = context;
    }
    
    /**
     * Called to set up the left side menu. Supposedly called only once.
     */
    public void initializeDrawerLayout() {
        // Set the adapter for the listView
        mDrawerListView.setAdapter(new SideMenuAdapter(mContext, mListItemsNames));
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }
}