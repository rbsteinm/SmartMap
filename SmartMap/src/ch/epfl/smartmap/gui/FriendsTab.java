package ch.epfl.smartmap.gui;

import java.util.ArrayList;
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
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.UserInformationActivity;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.listeners.OnCacheListener;

/**
 * Fragment (Tab) displaying your friends in FriendsActivity
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
        mFriendList = new ArrayList<User>(ServiceContainer.getCache().getAllFriends());

        // Create custom Adapter and pass it to the Activity
        this.setListAdapter(new FriendListItemAdapter(mContext, mFriendList));

        // Initialize the listener
        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onUserListUpdate() {
                mFriendList = new ArrayList<User>(ServiceContainer.getCache().getAllFriends());
                ((Activity) mContext).runOnUiThread(new Runnable() {
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
        Intent intent = new Intent(mContext, UserInformationActivity.class);
        intent.putExtra("USER", user.getId());
        this.startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFriendList = new ArrayList<User>(ServiceContainer.getCache().getAllFriends());
        this.setListAdapter(new FriendListItemAdapter(mContext, mFriendList));
    }
}