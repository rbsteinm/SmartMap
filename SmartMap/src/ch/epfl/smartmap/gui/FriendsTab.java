package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;

/**
 * Fragment displaying your friends in FriendsActivity
 * 
 * @author rbsteinm
 */
public class FriendsTab extends ListFragment {
    /**
     * Asynchronous task that removes a friend from the users friendList
     * both from the server and from the cache
     * 
     * @author rbsteinm
     */
    private class RemoveFriend extends AsyncTask<Long, Void, String> {

        @Override
        protected String doInBackground(Long... params) {
            String confirmString = "";
            try {
                NetworkSmartMapClient.getInstance().removeFriend(params[0]);
                confirmString =
                    "You're no longer friend with "
                        + NetworkSmartMapClient.getInstance().getUserInfo(params[0]).getName();

                // remove friend from cache and update displayed list
                // TODO should be done on the removeFriend method
                mCacheDB.deleteUser(params[0]);
            } catch (SmartMapClientException e) {
                confirmString = "Network error, operation failed";
            }
            return confirmString;
        }

        @Override
        protected void onPostExecute(String confirmString) {
            FriendsTab.this
                .setListAdapter(new FriendListItemAdapter(mContext, asList(mCacheDB.getAllUsers())));
            Toast.makeText(mContext, confirmString, Toast.LENGTH_LONG).show();
        }

    }

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
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    private void displayDeleteConfirmationDialog(String name, final long userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setMessage("remove " + name + " from your friends?");

        // Add positive button
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new RemoveFriend().execute(userId);
                // TODO refresh the userList
            }
        });

        // Add negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // display the AlertDialog
        builder.create().show();
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_friends_tab, container, false);
        mCacheDB = new DatabaseHelper(mContext);
        mFriendList = new ArrayList<User>();
        mFriendList = asList(mCacheDB.getAllUsers());
        this.sortByOnline(mFriendList);

        // Create custom Adapter and pass it to the Activity
        FriendListItemAdapter adapter = new FriendListItemAdapter(mContext, mFriendList);
        this.setListAdapter(adapter);

        return view;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        long userId = (Long) view.getTag();
        RelativeLayout rl = (RelativeLayout) view;
        TextView tv = (TextView) rl.getChildAt(1);
        assert (tv instanceof TextView) && (tv.getId() == R.id.activity_friends_name);
        String name = tv.getText().toString();
        this.displayDeleteConfirmationDialog(name, userId);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setListAdapter(new FriendListItemAdapter(mContext, asList(mCacheDB.getAllUsers())));
    }

    private void sortByOnline(List<User> userList) {
        Collections.sort(userList, new Comparator<User>() {

            @SuppressWarnings("deprecation")
            @Override
            public int compare(User user1, User user2) {
                if (user1.isOnline()) {
                    return -1;
                }
                if (user2.isOnline()) {
                    return 1;
                }
                return 0;
            }
        });
    }

}