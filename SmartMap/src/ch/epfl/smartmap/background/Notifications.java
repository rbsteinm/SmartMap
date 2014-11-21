package ch.epfl.smartmap.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.User;

/**
 * This class creates different sort of notifications
 * 
 * @author agpmilli
 */
public class Notifications {

    private static final int EVENT_NOTIFICATION_ID = 3;

    /**
     * Create an accepted friend invitation notification and notify it
     * 
     * @param view
     *            The current view
     * @param context
     *            The current activity
     * @param user
     *            The invited
     */
    public static void acceptedNotification(Context context, User user) {

        // Prepare intent that redirects the user to FriendActivity
        PendingIntent pFriendIntent = PendingIntent.getActivity(context, 0, new Intent(context,
                FriendsPagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] = new String(user.getName() + " accepted your invitation. ");
        events[1] = new String("Click here to open your list of friends");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("SmartMap friend invitation accepted");
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                // Sets all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentTitle("Friend invitation accepted")
                .setContentText(
                        user.getName()
                                + " accepted your invitation. \n Click here to open your list of friends")
                .setSmallIcon(R.drawable.ic_launcher).setTicker(user.getName() + " accepted your invitation")
                .setContentIntent(pFriendIntent);

        displayNotification(context, noti.build(), 2);
    }

    /**
     * Build the notification and notify it with notification manager.
     * 
     * @param activity
     *            current activity
     * @param notification
     *            notification to notify
     * @param notificationId
     *            id of current notification
     */
    private static void displayNotification(Context context, Notification notification, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    /**
     * Create an event invitation notification and notify it
     * 
     * @param view
     *            The current View
     * @param activity
     *            The current activity
     * @param event
     *            the Event
     */
    public static void newEventNotification(Context context, Event event) {

        // Prepare intent that redirect the user to EventActivity
        PendingIntent pEventIntent = PendingIntent.getActivity(context, 0, new Intent(context,
                ShowEventsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] = new String(event.getCreatorName() + " invite you to " + event.getName());
        events[1] = new String("Click here to open your list of events");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("SmartMap Event Invitation");
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                // Sets all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentTitle("SmartMap Event Invitation")
                .setContentText(
                        event.getCreatorName() + " invite you to " + event.getName()
                                + "\n Click here to open your list of events")
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(event.getCreatorName() + " invites you to " + event.getName())
                .setContentIntent(pEventIntent);

        displayNotification(context, noti.build(), EVENT_NOTIFICATION_ID);
    }

    /**
     * Create a friend invitation notification and notify it
     * 
     * @param view
     *            The current view
     * @param context
     *            The current activity
     * @param user
     *            The inviter
     */
    public static void newFriendNotification(Context context, User user) {
        // Prepare intent that redirects the user to FriendActivity
        PendingIntent pFriendIntent = PendingIntent.getActivity(context, 0, new Intent(context,
                FriendsPagerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] = new String(user.getName() + " wants to be your friend");
        events[1] = new String("Click here to open your list of friends");

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle("SmartMap Friend Invitation");
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context)
                // Add all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(pFriendIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(user.getName() + " wants to be your friend")

                .setContentTitle("SmartMap Friend Invitation")
                .setContentText(
                        user.getName()
                                + " wants to be your friend \n Click here to open your list of friends");

        // TODO : determine if we need those buttons
        // noti.addAction(0, "Decline", pIntent);
        // noti.addAction(0, "Accept", pIntent).build();
        displayNotification(context, noti.build(), 1);
    }
}
