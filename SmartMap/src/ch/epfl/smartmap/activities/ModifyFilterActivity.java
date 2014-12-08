package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.ImmutableFilter;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;

/**
 * @author marion-S
 */
public class ModifyFilterActivity extends Activity {

    private LinearLayout mInsideFilterLayout;
    private LinearLayout mOutsideFilterLayout;
    private ListView mListViewInside;
    private ListView mListViewOutside;
    private List<User> mFriendsInside;
    private List<User> mFriendsOutside;

    private Filter mFilter;
    private Cache mCache;

    // private final MockDB mockDB = new MockDB();

    private OnItemLongClickListener mOnInsideItemLongClickListener;
    private OnItemLongClickListener mOnOutsideItemLongClickListener;

    private OnDragListener mFromInsideDragListener;
    private OnDragListener mFromOutsideDragListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_modify_filter);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));

        mCache = ServiceContainer.getCache();

        mInsideFilterLayout = (LinearLayout) this.findViewById(R.id.activity_modify_filter_inside_layout);
        mOutsideFilterLayout = (LinearLayout) this.findViewById(R.id.activity_modify_filter_outside_layout);
        mListViewInside = (ListView) this.findViewById(R.id.activity_modify_filter_inside_list);
        mListViewOutside = (ListView) this.findViewById(R.id.activity_modify_filter_outside_list);

        mFriendsInside = new ArrayList<User>();
        mFriendsOutside = new ArrayList<User>();

        mOnInsideItemLongClickListener = new OnInsideListItemLongClickListener();
        mOnOutsideItemLongClickListener = new OnOutsideListItemLongClickListener();

        mListViewInside.setOnItemLongClickListener(mOnInsideItemLongClickListener);
        mListViewOutside.setOnItemLongClickListener(mOnOutsideItemLongClickListener);

        mFromInsideDragListener = new ListInsideDragEventListener();
        mFromOutsideDragListener = new ListOutsideDragEventListener();

        mListViewInside.setOnDragListener(mFromInsideDragListener);
        mOutsideFilterLayout.setOnDragListener(mFromInsideDragListener);

        mListViewOutside.setOnDragListener(mFromOutsideDragListener);
        mInsideFilterLayout.setOnDragListener(mFromOutsideDragListener);

        // this.setFilter();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {

        super.onResume();
        this.setFilter();

        this.setTitle(mFilter.getName());
        // For the moment,mock stuff
        // this.setTitle("Sweng Team");
        //
        // mFriendsInside =
        // new ArrayList<User>(Arrays.asList(mockDB.JULIEN, mockDB.ALAIN, mockDB.ROBIN, mockDB.MATTHIEU,
        // mockDB.NICOLAS, mockDB.MARION, mockDB.RAPHAEL, mockDB.HUGO));
        // mFriendsOutside =
        // new ArrayList<User>(Arrays.asList(mockDB.GUILLAUME, mockDB.SELINE, mockDB.CYRIL, mockDB.PIETRO,
        // mockDB.CHRISTIE, mockDB.MARIE));

        FriendListItemAdapter insideAdapter =
            new FriendListItemAdapter(this.getBaseContext(), mFriendsInside);
        mListViewInside.setAdapter(insideAdapter);

        FriendListItemAdapter outsideAdapter =
            new FriendListItemAdapter(this.getBaseContext(), mFriendsOutside);
        mListViewOutside.setAdapter(outsideAdapter);

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
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_save_filter:
                this.saveFilterDialog();
                break;
            case R.id.action_rename_filter:
                // TODO : dialog asking for the name, rename filter, update cache?
                break;
            default:
                // No other menu items!
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private Set<Long> friendListToIdSet(List<User> friendList) {
        Set<Long> idSet = new HashSet<Long>();
        for (User friend : friendList) {
            idSet.add(friend.getId());
        }
        return idSet;
    }

    private void saveFilter() {
        mCache.updateFilter(new ImmutableFilter(mFilter.getId(), mFilter.getName(), this
            .friendListToIdSet(mFriendsInside), mFilter.isActive()));

    }

    private void saveFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save changes?");

        // Add positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ModifyFilterActivity.this.saveFilter();
                Toast
                    .makeText(ModifyFilterActivity.this.getBaseContext(), "Changes saved", Toast.LENGTH_LONG)
                    .show();

            }
        });

        // Add negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        // display the AlertDialog
        builder.create().show();
    }

    private void setFilter() {
        mFilter = mCache.getFilter(this.getIntent().getLongExtra("FILTER", Filter.NO_ID));
        for (long id : mFilter.getFriendIds()) {
            mFriendsInside.add(mCache.getFriend(id));
        }
        for (User friend : mCache.getAllFriends()) {
            if (!mFriendsInside.contains(friend)) {
                mFriendsOutside.add(friend);
            }
        }
    }

    protected class ListInsideDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // All involved view accept ACTION_DRAG_STARTED for MIMETYPE_TEXT_PLAIN
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return true;
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENTERED:

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                    return true;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // If apply only if drop on buttonTarget
                    if (v.equals(mOutsideFilterLayout)) {
                        Long droppedItemId = Long.valueOf(item.getText().toString());
                        User droppedItem = mCache.getFriend(droppedItemId);

                        mFriendsInside.remove(droppedItem);
                        mFriendsOutside.add(droppedItem);
                        mListViewInside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this
                            .getBaseContext(), mFriendsInside));
                        mListViewOutside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this
                            .getBaseContext(), mFriendsOutside));

                        return true;
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
                default: // unknown case

                    return false;

            }
        }
    }

    protected class ListOutsideDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // All involved view accept ACTION_DRAG_STARTED for MIMETYPE_TEXT_PLAIN
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        return true;
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENTERED:

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                    return true;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // If apply only if drop on buttonTarget
                    if (v.equals(mInsideFilterLayout)) {
                        Long droppedItemId = Long.valueOf(item.getText().toString());
                        User droppedItem = mCache.getFriend(droppedItemId);

                        mFriendsInside.add(droppedItem);
                        mFriendsOutside.remove(droppedItem);
                        mListViewInside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this
                            .getBaseContext(), mFriendsInside));
                        mListViewOutside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this
                            .getBaseContext(), mFriendsOutside));

                        return true;
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
                default: // unknown case

                    return false;

            }
        }
    }

    private class OnInsideListItemLongClickListener implements OnItemLongClickListener {

        /*
         * (non-Javadoc)
         * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView,
         * android.view.View, int, long)
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(Long.toString(mFriendsInside.get(position).getId()));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(null, clipDescription, item);

            view.startDrag(dragData, // ClipData
                new View.DragShadowBuilder(view), // View.DragShadowBuilder
                null, // Object myLocalState
                0); // flags

            return true;
        }

    }

    private class OnOutsideListItemLongClickListener implements OnItemLongClickListener {

        /*
         * (non-Javadoc)
         * @see android.widget.AdapterView.OnItemLongClickListener#onItemLongClick(android.widget.AdapterView,
         * android.view.View, int, long)
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(Long.toString(mFriendsOutside.get(position).getId()));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(null, clipDescription, item);

            view.startDrag(dragData, // ClipData
                new View.DragShadowBuilder(view), // View.DragShadowBuilder
                null, // Object myLocalState
                0); // flags

            return true;
        }

    }
}
