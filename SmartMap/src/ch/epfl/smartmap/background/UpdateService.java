package ch.epfl.smartmap.background;

import ch.epfl.smartmap.cache.DatabaseHelper;
import ch.epfl.smartmap.cache.SettingsManager;
import ch.epfl.smartmap.servercom.NetworkSmartMapClient;
import ch.epfl.smartmap.servercom.SmartMapClientException;
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
    private Intent mIntent;
    private DatabaseHelper mHelper = DatabaseHelper.getInstance();
    private SettingsManager mManager = SettingsManager.getInstance();
    private NetworkSmartMapClient mClient = NetworkSmartMapClient.getInstance();
    
    private Runnable sendFriendsPosUpdate = new Runnable() {
        public void run() {
        	AsyncFriendsPos task = new AsyncFriendsPos();
            task.execute();
            sendBroadcast(mIntent);
            mHandler.postDelayed(this, UPDATE_DELAY);
            Log.d("UpdateService", "FriendsPosUpdate");
        }
    };
    
    private Runnable sendOwnPosUpdate = new Runnable() {
        public void run() {
            /*try {
				mClient.updatePos(mManager.getLocation());
			} catch (SmartMapClientException e) {
				e.printStackTrace();
			}*/
            mHandler.postDelayed(this, UPDATE_DELAY);
            Log.d("UpdateService", "OwnPosUpdate");
        }
    };  
    
    @Override
    public void onCreate() {
        super.onCreate();
        mIntent = new Intent(BROADCAST_POS);  
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
    	mHelper.initializeAllFriends();
        mHandler.removeCallbacks(sendFriendsPosUpdate);
        mHandler.postDelayed(sendFriendsPosUpdate, HANDLER_DELAY);
        mHandler.removeCallbacks(sendOwnPosUpdate);
        mHandler.postDelayed(sendOwnPosUpdate, HANDLER_DELAY);
        Log.d("UpdateService", "Service started");
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    private class AsyncFriendsPos extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... args0) {
        	return mHelper.refreshFriendsPos();
        }

        @Override
        protected void onPostExecute(Integer result) {
        	mIntent.putExtra(UPDATED_ROWS, result);
        }
    }
}
