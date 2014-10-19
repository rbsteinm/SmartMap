package ch.epfl.smartmap.gui;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.epfl.smartmap.R;

/**
 * @author rbsteinm
 * 
 */
public class SideMenu extends DrawerLayout {

    // private final DrawerLayout mSideDrawerLayout;
    private final String[] mSideLayoutElements;
    private ListView mDrawerListView;
    private final Context mContext;

    public SideMenu(Context context, ListView drawerListView) {
        super(context);
        // mSideDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mSideLayoutElements = getResources().getStringArray(R.array.sideMenuElements);
        mDrawerListView = drawerListView;
        mContext = context;
    }

    public void initializeDrawerLayout() {
        // Set the adapter for the listView
        mDrawerListView.setAdapter(new ArrayAdapter<String>(mContext, R.layout.drawer_list_item,
                mSideLayoutElements));
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
    }
}