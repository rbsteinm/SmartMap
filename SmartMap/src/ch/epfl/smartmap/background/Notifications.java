package ch.epfl.smartmap.background;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.MainActivity;

/**
 * Class that creates different sort of notifications
 * 
 * @author agpmilli
 * 
 */
public class Notifications {

    public static void createAddNotification(View view, Activity activity) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(activity, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(
            activity.getBaseContext(), 0, intent, 0);

        // Build notification
        // Actions are just fake
        NotificationCompat.Builder noti = new NotificationCompat.Builder(
            activity);
        noti.setContentTitle("TITLE OF ADD NOTIFICATION");
        noti.setContentText("SUBJECT OF ADD NOTIFICATION").setSmallIcon(
            R.drawable.ic_launcher);
        noti.setContentIntent(pIntent);
        noti.addAction(0, "accept", pIntent);
        noti.addAction(0, "decline", pIntent).build();
        @SuppressWarnings("static-access")
        NotificationManager notificationManager = (NotificationManager) activity
            .getSystemService(activity.getBaseContext().NOTIFICATION_SERVICE);
        notificationManager.notify(0, noti.build());
    }
}
