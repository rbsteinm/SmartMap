package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendInformationActivity;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.FriendsListener;
import ch.epfl.smartmap.cache.User;

/**
 * Fragment displaying your friends in FriendsActivity
 * 
 * @author rbsteinm
 */
public class FriendsTab extends ListFragment implements FriendsListener {
    public static <C> List<C> asList(LongSparseArray<C> sparseArray) {
        if (sparseArray == null) {
            return null;
        }
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

    private List<User> mFriendList;
    private final Context mContext;

    private DatabaseHelper mCacheDB;

    public FriendsTab(Context context) {
        mContext = context;
    }

    @Override
    public void onChange() {
        this.setListAdapter(new FriendListItemAdapter(mContext, asList(mCacheDB.getAllUsers())));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_friends_tab, container, false);
        mCacheDB = DatabaseHelper.getInstance();
        mFriendList = new ArrayList<User>();
        mFriendList = asList(mCacheDB.getAllUsers());

        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(mContext, mFriendList);
        this.setListAdapter(adapter);

        // Initialize the listener
        mCacheDB.addFriendsListener(this);

        return view;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        User user = mFriendList.get(position);
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.getChildAt(1);
        assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
        Intent intent = new Intent(mContext, FriendInformationActivity.class);
        intent.putExtra("CURRENT_DISPLAYABLE", (Parcelable) user);
        this.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setListAdapter(new FriendListItemAdapter(mContext, asList(mCacheDB.getAllUsers())));
    }
}
