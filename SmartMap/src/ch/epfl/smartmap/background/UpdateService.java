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
    private static final int UPDATE_DELAY = 10000;
    
    private final Handler mHandler = new Handler();
    private Intent mFriendsPosIntent;
    private Intent mFriendNotifIntent;
    private boolean mFriendsPosEnabled = true;
    private DatabaseHelper mHelper = DatabaseHelper.getInstance();
    private SettingsManager mManager = SettingsManager.getInstance();
    private NetworkSmartMapClient mClient = NetworkSmartMapClient.getInstance();
    
    private Runnable sendFriendsPosUpdate = new Runnable() {
        public void run() {
        	if (mFriendsPosEnabled) {
	        	new AsyncFriendsPos().execute();
	            sendBroadcast(mFriendsPosIntent);
	            mHandler.postDelayed(this, UPDATE_DELAY);
	            Log.d("UpdateService", "Friends pos update");
        	}
        }
    };
    
    private Runnable sendOwnPosUpdate = new Runnable() {
        public void run() {
            new AsyncOwnPos().execute();
            mHandler.postDelayed(this, UPDATE_DELAY);
            Log.d("UpdateService", "Own pos update");
        }
    };
    
    private Runnable showFriendNotif = new Runnable() {
        public void run() {
            new AsyncRequestCheck().execute();
            mHandler.postDelayed(this, UPDATE_DELAY);
            Log.d("UpdateService", "Friend request check");
        }
    };  
    
    @Override
    public void onCreate() {
        super.onCreate();
        mFriendsPosIntent = new Intent(BROADCAST_POS);
        mFriendNotifIntent = new Intent(this, ch.epfl.smartmap.activities.FriendsActivity.class);
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
        	return mHelper.refreshFriendsPos();
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
                list = mClient.getInvitations();
            } catch (SmartMapClientException e) {
                Log.e("UpdateService", "Couldn't retrieve invites!");
            }
            return list;
        }
        
        @Override
        protected void onPostExecute(List<User> result) {
            if (!result.isEmpty()) {
                for (User user : result) {
                    //TODO
                }
            }
        }
    }
}
