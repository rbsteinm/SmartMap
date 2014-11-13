package ch.epfl.smartmap.background;

import ch.epfl.smartmap.cache.DatabaseHelper;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

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
    private DatabaseHelper mHelper = new DatabaseHelper(this);
    
    private Runnable sendFriendsPosUpdate = new Runnable() {
        public void run() {
            int rows = mHelper.refreshFriendsPos();
            mIntent.putExtra(UPDATED_ROWS, rows);
            sendBroadcast(mIntent);
            System.out.println(rows);
            mHandler.postDelayed(this, UPDATE_DELAY);
        }
    };  
    
    @Override
    public void onCreate() {
        super.onCreate();
        mIntent = new Intent(BROADCAST_POS);  
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        mHandler.removeCallbacks(sendFriendsPosUpdate);
        mHandler.postDelayed(sendFriendsPosUpdate, HANDLER_DELAY); 
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
}
