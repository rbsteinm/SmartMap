package ch.epfl.smartmap.activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Filter;
import ch.epfl.smartmap.cache.FilterContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.gui.FriendListItemAdapter;

/**
 * An activity that allows to modify a filter: add or remove people, rename
 * filter, remove filter. The action
 * of adding/removing a friend from the filter is done by drag and drop between
 * two lists.
 * 
 * @author marion-S
 */
@SuppressLint("InflateParams")
public class ModifyFilterActivity extends Activity {

    public static final int MAX_NAME_LENGTH = 25;

    private LinearLayout mInsideFilterLayout;
    private LinearLayout mOutsideFilterLayout;

    private ListView mListViewInside;
    private ListView mListViewOutside;

    private List<User> mFriendsInside;
    private List<User> mFriendsOutside;

    private Filter mFilter;

    private OnItemLongClickListener mOnInsideItemLongClickListener;
    private OnItemLongClickListener mOnOutsideItemLongClickListener;

    private OnDragListener mFromInsideDragListener;
    private OnDragListener mFromOutsideDragListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_modify_filter);
        this.getActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.main_blue));
        // ServiceContainer.initSmartMapServices(this.getBaseContext());

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

        FriendListItemAdapter insideAdapter = new FriendListItemAdapter(this, mFriendsInside);
        mListViewInside.setAdapter(insideAdapter);

        FriendListItemAdapter outsideAdapter = new FriendListItemAdapter(this, mFriendsOutside);
        mListViewOutside.setAdapter(outsideAdapter);

    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.modify_filter, menu);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
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
                this.saveFilter();
                break;
            case R.id.action_rename_filter:
                this.renameFilterDialog(item);
                break;
            case R.id.action_remove_filter:
                this.removeFilterDialog();
                break;
            default:
                // No other menu items!
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A dialog to rename the filter.
     */
    public void renameFilterDialog(MenuItem item) {
        // inflate the alertDialog
        LayoutInflater inflater = this.getLayoutInflater();
        final View alertLayout = inflater.inflate(R.layout.new_filter_alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getResources().getString(R.string.rename_filter));
        builder.setView(alertLayout);

        // Add positive button
        builder.setPositiveButton(this.getResources().getString(R.string.action_rename_filter_button),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    EditText editText =
                        (EditText) alertLayout.findViewById(R.id.show_filters_alert_dialog_edittext);
                    String newName = editText.getText().toString();
                    if (newName.isEmpty() || (newName.length() > MAX_NAME_LENGTH)) {
                        Toast.makeText(
                            ModifyFilterActivity.this.getBaseContext(),
                            ModifyFilterActivity.this.getResources().getString(
                                R.string.create_filter_invalid_name), Toast.LENGTH_LONG).show();
                    } else {
                        ServiceContainer.getCache().putFilter(
                            new FilterContainer(mFilter.getId(), newName, mFilter.getIds(), mFilter
                                .isActive()));

                        Toast.makeText(ModifyFilterActivity.this,
                            ModifyFilterActivity.this.getResources().getString(R.string.new_name_saved),
                            Toast.LENGTH_LONG).show();
                        ModifyFilterActivity.this.setTitle(newName);
                    }

                }
            });

        // Add negative button
        builder.setNegativeButton(
            ModifyFilterActivity.this.getResources().getString(R.string.action_cancel_button),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        // display the AlertDialog
        builder.create().show();
    }

    /**
     * This method saves the updated filter in the {@code Cache}
     */
    public void saveFilter() {
        ServiceContainer.getCache().putFilter(
            new FilterContainer(mFilter.getId(), mFilter.getName(), ModifyFilterActivity.this
                .friendListToIdSet(mFriendsInside), mFilter.isActive()));
        Toast.makeText(ModifyFilterActivity.this,
            ModifyFilterActivity.this.getResources().getString(R.string.changes_saved), Toast.LENGTH_LONG)
            .show();
    }

    /**
     * An utility method to convert a friend list to a set of corresponding ids,
     * because the {@code Cache} methods
     * uses sets of ids.
     * 
     * @param friendList
     * @return a set of id corresponding to the list of friend
     */
    private Set<Long> friendListToIdSet(List<User> friendList) {
        Set<Long> idSet = new HashSet<Long>();
        for (User friend : friendList) {
            idSet.add(friend.getId());
        }
        return idSet;
    }

    /**
     * A dialog to remove a filter.
     */
    private void removeFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getResources().getString(R.string.remove_filter));

        // Add positive button
        builder.setPositiveButton(this.getResources().getString(R.string.yes),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                    ServiceContainer.getCache().removeFilter(mFilter.getId());
                    Toast.makeText(ModifyFilterActivity.this,
                        ModifyFilterActivity.this.getResources().getString(R.string.removed_filter),
                        Toast.LENGTH_LONG).show();

                    ModifyFilterActivity.this.finish();
                }
            });

        // Add negative button
        builder.setNegativeButton(this.getResources().getString(R.string.no),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // nothing
                }
            });

        // display the AlertDialog
        builder.create().show();
    }

    /**
     * Set the filter that the activity displays by retrieving its id from the
     * starting intent and using the {@code Cache}
     */
    private void setFilter() {
        mFilter =
            ServiceContainer.getCache().getFilter(this.getIntent().getLongExtra("FILTER", Filter.NO_ID));

        for (long id : mFilter.getIds()) {
            User user = ServiceContainer.getCache().getUser(id);
            if (!mFriendsInside.contains(user)) {
                mFriendsInside.add(user);
            }

        }
        for (User friend : ServiceContainer.getCache().getAllFriends()) {
            if (!mFriendsInside.contains(friend) && !mFriendsOutside.contains(friend)) {
                mFriendsOutside.add(friend);
            }
        }
    }

    /**
     * Updates the inside and outside filter lists when an item is dragged from
     * the outside list and dropped
     * into the inside list, i.e when a friend is added to the filter
     * 
     * @param droppedItem
     * @return true is the drop action was taken in account, false otherwise
     */
    private boolean updateFriendsListsWhenAddedFriend(User droppedItem) {
        if (mFriendsOutside.contains(droppedItem)) {
            mFriendsInside.add(droppedItem);
            mFriendsOutside.remove(droppedItem);
            mListViewInside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this, mFriendsInside));
            mListViewOutside
                .setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this, mFriendsOutside));

            return true;
        } else {
            return false;
        }

    }

    /**
     * Updates the inside and outside filter lists when an item is dragged from
     * the inside list and dropped
     * into the outside list, i.e when a friend is removed from the filter
     * 
     * @param droppedItem
     * @return true is the drop action was taken in account, false otherwise
     */
    private boolean updateFriendsListsWhenRemovedFriend(User droppedItem) {
        if (mFriendsInside.contains(droppedItem)) {
            mFriendsInside.remove(droppedItem);
            mFriendsOutside.add(droppedItem);
            mListViewInside.setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this, mFriendsInside));
            mListViewOutside
                .setAdapter(new FriendListItemAdapter(ModifyFilterActivity.this, mFriendsOutside));
            return true;
        } else {
            return false;
        }
    }

    /**
     * A drag listener for the outside filter list.
     * 
     * @author marion-S
     */
    protected class ListInsideDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                    return true;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    // If apply only if drop on target layout
                    if (v.equals(mOutsideFilterLayout)) {
                        Long droppedItemId = Long.valueOf(item.getText().toString());
                        User droppedItem = ServiceContainer.getCache().getUser(droppedItemId);
                        return ModifyFilterActivity.this.updateFriendsListsWhenRemovedFriend(droppedItem);
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
                default:

                    return false;

            }
        }
    }

    /**
     * A drag listener for the outside filter list.
     * 
     * @author marion-S
     */
    protected class ListOutsideDragEventListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                case DragEvent.ACTION_DRAG_ENTERED:

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:

                    return true;
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    // If apply only if drop on target layout
                    if (v.equals(mInsideFilterLayout)) {
                        Long droppedItemId = Long.valueOf(item.getText().toString());
                        User droppedItem = ServiceContainer.getCache().getUser(droppedItemId);
                        return ModifyFilterActivity.this.updateFriendsListsWhenAddedFriend(droppedItem);
                    } else {
                        return false;
                    }

                case DragEvent.ACTION_DRAG_ENDED:

                    return true;
                default:

                    return false;

            }
        }
    }

    /**
     * A long click listener for items of the inside filter list.
     * 
     * @author marion-S
     */
    private class OnInsideListItemLongClickListener implements OnItemLongClickListener {

        /*
         * (non-Javadoc)
         * @see
         * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick
         * (android.widget.AdapterView,
         * android.view.View, int, long)
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(Long.toString(mFriendsInside.get(position).getId()));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(null, clipDescription, item);

            view.startDrag(dragData, new View.DragShadowBuilder(view), null, 0);

            return true;
        }

    }

    /**
     * A long click listener for items in the outside filter list.
     * 
     * @author marion-S
     */
    private class OnOutsideListItemLongClickListener implements OnItemLongClickListener {

        /*
         * (non-Javadoc)
         * @see
         * android.widget.AdapterView.OnItemLongClickListener#onItemLongClick
         * (android.widget.AdapterView,
         * android.view.View, int, long)
         */
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // Selected item is passed as item in dragData
            ClipData.Item item = new ClipData.Item(Long.toString(mFriendsOutside.get(position).getId()));

            String[] clipDescription = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(null, clipDescription, item);

            view.startDrag(dragData, new View.DragShadowBuilder(view), null, 0);
            return true;
        }

    }
}