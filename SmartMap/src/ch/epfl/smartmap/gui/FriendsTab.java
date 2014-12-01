package ch.epfl.smartmap.gui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.cache.Cache;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.listeners.OnCacheListener;

/**
 * Fragment displaying your friends in FriendsActivity
 * 
 * @author rbsteinm
 */
public class FriendsTab extends ListFragment {
    private List<User> mFriendList;

    private final Context mContext;

    public FriendsTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_friends_tab, container, false);
        mFriendList = Cache.getInstance().getAllFriends();

        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(mContext, mFriendList);
        this.setListAdapter(adapter);

        // Initialize the listener
        Cache.getInstance().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onFriendListUpdate() {
                mFriendList = Cache.getInstance().getAllFriends();
                ((Activity) FriendsTab.this.mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FriendsTab.this.setListAdapter(new FriendListItemAdapter(mContext, mFriendList));
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        User user = mFriendList.get(position);
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.getChildAt(1);
        assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
        Intent intent = new Intent(mContext, UserInformationActivity.class);
        intent.putExtra("USER", user.getId());
        this.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setListAdapter(new FriendListItemAdapter(mContext, Cache.getInstance().getAllFriends()));
    }
}