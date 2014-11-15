package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.MockDB;
import ch.epfl.smartmap.cache.User;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendsTab extends ListFragment {
	 private List<User> mMockUsersList;
	 Context mContext;
	 
	 public FriendsTab(Context context) {
		// TODO Auto-generated constructor stub
		 mContext = context;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.list_fragment_friends_tab, container, false);
		mMockUsersList = new ArrayList<User>();
        for (User user: MockDB.FRIENDS_LIST) {
            mMockUsersList.add(user);
        }
        mMockUsersList.get(1).setOnline(true);
        mMockUsersList.get(3).setOnline(true);
       // sortByOnline(mMockUsersList);
        
        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(mContext, mMockUsersList);
        setListAdapter(adapter);
    
		
		return view;
	}
	
}
