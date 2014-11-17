package ch.epfl.smartmap.background;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.Friend;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.cache.User;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * A background service that updates friends' position periodically
 * @author ritterni
 */
public class UpdateService extends Service {
    public static final String BROADCAST_POS = "ch.epfl.smartmap.background.broadcastPos";
    public static final String UPDATED_ROWS = "UpdatedRows";
    
    private static final int HANDLER_DELAY = 1000;
    private static final int POS_UPDATE_DELAY = 10000;
    private static final int INVITE_UPDATE_DELAY = 30000;
    
    private final Handler mHandler = new Handler();
    private Intent mFriendsPosIntent;
    private Intent mFriendNotifIntent;
    private boolean mFriendsPosEnabled = true;
    private boolean mReady = false;
    private DatabaseHelper mHelper = DatabaseHelper.getInstance();
    private SettingsManager mManager = SettingsManager.getInstance();
    private NetworkSmartMapClient mClient = NetworkSmartMapClient.getInstance();
    
    private Runnable sendFriendsPosUpdate = new Runnable() {
        public void run() {
        	if (mFriendsPosEnabled) {
        	    if (mReady) {
    	        	new AsyncFriendsPos().execute();
    	            sendBroadcast(mFriendsPosIntent);
    	            Log.d("UpdateService", "Broadcast sent");
        	    }
	            mHandler.postDelayed(this, POS_UPDATE_DELAY);
	            Log.d("UpdateService", "Friends pos update");
        	}
        }
    };
    
    private Runnable sendOwnPosUpdate = new Runnable() {
        public void run() {
            new AsyncOwnPos().execute();
            mHandler.postDelayed(this, POS_UPDATE_DELAY);
            Log.d("UpdateService", "Own pos update");
        }
    };
    
    private Runnable showFriendNotif = new Runnable() {
        public void run() {
            new AsyncRequestCheck().execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
            Log.d("UpdateService", "Friend requests check");
        }
    };
    
    private Runnable getReplies = new Runnable() {
        public void run() {
            new AsyncReplyCheck().execute();
            mHandler.postDelayed(this, INVITE_UPDATE_DELAY);
            Log.d("UpdateService", "Friend replies check");
        }
    };  
    
    @Override
    public void onCreate() {
        super.onCreate();
        mFriendsPosIntent = new Intent(BROADCAST_POS);
        mFriendNotifIntent = new Intent(this, ch.epfl.smartmap.activities.FriendsPagerActivity.class);
        new AsyncFriendsInit().execute();
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        mHandler.removeCallbacks(sendFriendsPosUpdate);
        mHandler.postDelayed(sendFriendsPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(sendOwnPosUpdate);
        mHandler.postDelayed(sendOwnPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(showFriendNotif);
        mHandler.postDelayed(showFriendNotif, HANDLER_DELAY);
        mHandler.removeCallbacks(getReplies);
        mHandler.postDelayed(getReplies, HANDLER_DELAY);
        Log.d("UpdateService", "Service started");
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /** Enables/disables friends position updates
     * @param isEnabled True if updates should be enabled
     */
    public void setFriendsPosUpdateEnabled(boolean isEnabled) {
    	mFriendsPosEnabled = isEnabled;
    	if (isEnabled) {
    		mHandler.postDelayed(sendFriendsPosUpdate, HANDLER_DELAY);
    	}
    }
    
    /**
     * AsyncTask to get friends' positions
     * @author ritterni
     */
    private class AsyncFriendsPos extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... args0) {
            int rows = 0;
            rows = mHelper.refreshFriendsPos();
        	return rows;
        }

        @Override
        protected void onPostExecute(Integer result) {
        	mFriendsPosIntent.putExtra(UPDATED_ROWS, result);
        }
    }
    
    /**
     * AsyncTask to send the user's own position to the server
     * @author ritterni
     */
    private class AsyncOwnPos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                mClient.updatePos(mManager.getLocation());
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Position update failed!");
            }
            return null;
        }
    }
    
    /**
     * AsyncTask to send the user's own position to the server
     * @author ritterni
     */
    private class AsyncFriendsInit extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            mHelper.initializeAllFriends();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void result) {
            mReady = true;
        }
    }
    
    /**
     * AsyncTask to check if a friend request was received
     * @author ritterni
     */
    private class AsyncRequestCheck extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... arg0) {
            List<User> list = new ArrayList<User>();
            try {
                //First list, the list of received invitations
                list = mClient.getInvitations().get(0);
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't retrieve invites!");
            }
            return list;
        }
        
        @Override
        protected void onPostExecute(List<User> result) {
            if (!result.isEmpty()) {
                for (User user : result) {
                    //TODO Create a notification
                }
            }
        }
    }
    
    /**
     * AsyncTask to check if a friend request was received
     * @author ritterni
     */
    private class AsyncReplyCheck extends AsyncTask<Void, Void, List<User>> {
        @Override
        protected List<User> doInBackground(Void... arg0) {
            List<User> list = new ArrayList<User>();
            try {
                //Second list, the list of accepted invitations
                list = mClient.getInvitations().get(1);
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't retrieve replies!");
            }
            return list;
        }
        
        @Override
        protected void onPostExecute(List<User> result) {
            if (!result.isEmpty()) {
                for (User user : result) {
                    mHelper.addUser(user);
                }
            }
        }
    }
}
