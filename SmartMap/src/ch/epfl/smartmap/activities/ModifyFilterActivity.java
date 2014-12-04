package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;

/**
 * @author marion-S
 */
public class ModifyFilterActivity extends Activity {

    private LinearLayout mInsideFilterLayout;
    private LinearLayout mOutsideFilterLayout;
    private ListView mListInside;
    private ListView mListOutside;
    private List<User> mFriendsInside;
    private List<User> mFriendsOutside;

    private final MockDB mockDB = new MockDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_modify_filter);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        mInsideFilterLayout = (LinearLayout) this.findViewById(R.id.activity_modify_filter_inside_layout);
        mOutsideFilterLayout = (LinearLayout) this.findViewById(R.id.activity_modify_filter_outside_layout);
        mListInside = (ListView) this.findViewById(R.id.activity_modify_filter_inside_list);
        mListOutside = (ListView) this.findViewById(R.id.activity_modify_filter_outside_list);

    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {

        super.onResume();

        // TODO set the inside and outside list using the intent and the cache
        // For the moment,mock stuff
        mFriendsInside =
            new ArrayList<User>(Arrays.asList(mockDB.JULIEN, mockDB.ALAIN, mockDB.ROBIN, mockDB.MATTHIEU,
                mockDB.NICOLAS, mockDB.MARION, mockDB.RAPHAEL, mockDB.HUGO));
        mFriendsOutside =
            new ArrayList<User>(Arrays.asList(mockDB.GUILLAUME, mockDB.SELINE, mockDB.CYRIL, mockDB.PIETRO,
                mockDB.CHRISTIE, mockDB.MARIE));

        FriendListItemAdapter insideAdapter =
            new FriendListItemAdapter(this.getBaseContext(), mFriendsInside);
        mListInside.setAdapter(insideAdapter);

        FriendListItemAdapter outsideAdapter =
            new FriendListItemAdapter(this.getBaseContext(), mFriendsOutside);
        mListOutside.setAdapter(outsideAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.modify_filter, menu);
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
