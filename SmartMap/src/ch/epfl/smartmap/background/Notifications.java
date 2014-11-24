package ch.epfl.smartmap.background;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
    /**
     * Listener use to listen when notifications arrive
     * 
     * @author agpmilli
     */
    public static class NotificationListener {
        public void onNewNotification() {

        }
    }

    private static final int VIBRATE_NOTIFICATION_TIME = 500;
    private static final int SILENT_NOTIFICATION_TIME = 100;
    private final static long[] PATTERN = {0, VIBRATE_NOTIFICATION_TIME, SILENT_NOTIFICATION_TIME,
        VIBRATE_NOTIFICATION_TIME};
    private static long notificationID = 0;
    private static int numberOfEventNotification = 0;
    private static int numberOfFriendNotification = 0;

    private static List<NotificationListener> listeners = new ArrayList<NotificationListener>();

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
    public static void acceptedFriendNotification(Context context, User user) {

        // Get ID of notifications
        notificationID++;

        // Prepare intent that redirects the user to FriendActivity
        PendingIntent pFriendIntent =
            PendingIntent.getActivity(context, 0, new Intent(context, FriendsPagerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] =
            new String(user.getName() + " " + context.getString(R.string.notification_invitation_accepted));
        events[1] = context.getString(R.string.notification_open_friend_list);

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_acceptedfriend_title));
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti =
            new NotificationCompat.Builder(context)
                // Sets all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_acceptedfriend_title))
                .setContentText(
                    user.getName() + " " + context.getString(R.string.notification_invitation_accepted)
                        + "\n" + context.getString(R.string.notification_open_friend_list))
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(
                    user.getName() + " " + context.getString(R.string.notification_invitation_accepted))
                .setVibrate(PATTERN).setContentIntent(pFriendIntent);

        displayNotification(context, noti.build(), notificationID);

        notifyListeners();
    }

    public static void addNotificationListener(NotificationListener listener) {
        listeners.add(listener);
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
    private static void displayNotification(Context context, Notification notification, long notificationId) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) notificationId, notification);
    }

    public static int getNumberOfEventNotification() {
        return numberOfEventNotification;
    }

    public static int getNumberOfFriendNotification() {
        return numberOfFriendNotification;
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

        // Get ID and the number of ongoing Event notifications
        notificationID++;
        setNumberOfUnreadEventNotification(getNumberOfEventNotification() + 1);

        // Prepare intent that redirect the user to EventActivity
        PendingIntent pEventIntent =
            PendingIntent.getActivity(context, 0, new Intent(context, ShowEventsActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] =
            new String(event.getCreatorName() + " "
                + context.getString(R.string.notification_event_invitation) + " " + event.getName());
        events[1] = context.getString(R.string.notification_open_event_list);

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_inviteevent_title));
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti =
            new NotificationCompat.Builder(context)
                // Sets all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.notification_inviteevent_title))
                .setContentText(
                    event.getCreatorName() + " " + context.getString(R.string.notification_event_invitation)
                        + event.getName() + "\n" + context.getString(R.string.notification_open_event_list))
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(
                    event.getCreatorName() + " " + context.getString(R.string.notification_event_invitation)
                        + event.getName()).setVibrate(PATTERN).setContentIntent(pEventIntent);

        displayNotification(context, noti.build(), notificationID);

        notifyListeners();
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

        // Get ID and the number of ongoing Friend notifications
        notificationID++;
        setNumberOfUnreadFriendNotification(getNumberOfFriendNotification() + 1);
        Log.d("BABOUIN", "" + getNumberOfFriendNotification());

        // Prepare intent that redirects the user to FriendActivity
        PendingIntent pFriendIntent =
            PendingIntent.getActivity(context, 0, new Intent(context, FriendsPagerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] = user.getName() + " " + context.getString(R.string.notification_friend_invitation);
        events[1] = context.getString(R.string.notification_open_friend_list);

        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getString(R.string.notification_invitefriend_title));
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }

        // Build notification
        NotificationCompat.Builder noti =
            new NotificationCompat.Builder(context)
                // Add all notification's specifications in the builder
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setContentIntent(pFriendIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(user.getName() + " " + context.getString(R.string.notification_friend_invitation))
                .setContentTitle(context.getString(R.string.notification_invitefriend_title))
                .setVibrate(PATTERN)
                .setContentText(
                    user.getName() + " " + context.getString(R.string.notification_friend_invitation) + "\n"
                        + context.getString(R.string.notification_open_friend_list));
        displayNotification(context, noti.build(), notificationID);

        notifyListeners();
    }

    private static void notifyListeners() {
        for (NotificationListener l : listeners) {
            l.onNewNotification();
        }
    }

    public static void setNumberOfUnreadEventNotification(int numberOfEvent) {
        Notifications.numberOfEventNotification = numberOfEvent;
    }

    public static void setNumberOfUnreadFriendNotification(int numberOfFriend) {
        Notifications.numberOfFriendNotification = numberOfFriend;
    }
}