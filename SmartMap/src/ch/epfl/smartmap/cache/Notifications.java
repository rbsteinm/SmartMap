package ch.epfl.smartmap.cache;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.EventInformationActivity;
import ch.epfl.smartmap.activities.FriendsPagerActivity;
import ch.epfl.smartmap.activities.UserInformationActivity;

/**
 * This class creates different sort of notifications
 * 
 * @author agpmilli
 */
public class Notifications {

    private static final String TAG = Notifications.class.getSimpleName();

    private static final int VIBRATE_NOTIFICATION_TIME = 500;

    private static final int SILENT_NOTIFICATION_TIME = 100;
    private final static long[] PATTERN = {0, VIBRATE_NOTIFICATION_TIME, SILENT_NOTIFICATION_TIME,
        VIBRATE_NOTIFICATION_TIME};
    private static long notificationID = 0;

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
    public static void acceptedFriendNotification(Context context, ImmutableUser user) {

        // Get ID of notifications
        notificationID++;

        // Prepare intent that redirects the user to FriendActivity
        Intent showFriendInfoIntent = new Intent(context, UserInformationActivity.class);
        showFriendInfoIntent.putExtra("USER", user.getId());
        PendingIntent pFriendIntent =
            PendingIntent.getActivity(context, 0, showFriendInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] = new String(user.getName() + " " + context.getString(R.string.notification_invitation_accepted));
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
                    user.getName() + " " + context.getString(R.string.notification_invitation_accepted) + "\n"
                        + context.getString(R.string.notification_open_friend_list))
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(user.getName() + " " + context.getString(R.string.notification_invitation_accepted))
                .setVibrate(PATTERN).setContentIntent(pFriendIntent);

        displayNotification(context, noti.build(), notificationID);

    }

    /**
     * Cancel (destroy) all notifications from the status bar
     * 
     * @param context
     *            the current context
     */
    public static void cancelNotification(Context context) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
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
    public static void newEventNotification(final Context context, GenericInvitation invitation) {

        // Get ID and the number of ongoing Event notifications
        notificationID++;

        // Prepare intent that redirect the user to EventActivity
        Intent showEventInfoIntent = new Intent(context, EventInformationActivity.class);
        showEventInfoIntent.putExtra("EVENT", invitation.getEvent().getId());
        PendingIntent pEventIntent =
            PendingIntent.getActivity(context, 0, showEventInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Add Big View Specific Configuration
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        String[] events = new String[2];
        events[0] =
            invitation.getEvent().getName() + " " + context.getString(R.string.notification_event_invitation) + " "
                + invitation.getEvent().getName();
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
                    /*
                     * CachedSearchEngine.getInstance().findFriendById(event.
                     * getCreatorId()) +
                     */" " + context.getString(R.string.notification_event_invitation)
                        + invitation.getEvent().getName() + "\n"
                        + context.getString(R.string.notification_open_event_list))
                .setSmallIcon(R.drawable.ic_launcher).setTicker(
                /*
                 * CachedSearchEngine.getInstance().findFriendById(event.
                 * getCreatorId()) +
                 */" " + context.getString(R.string.notification_event_invitation) + invitation.getEvent().getName())
                .setVibrate(PATTERN).setContentIntent(pEventIntent);

        displayNotification(context, noti.build(), notificationID);
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
    public static void newFriendNotification(Context context, ImmutableUser user) {

        // Get ID and the number of ongoing Friend notifications
        notificationID++;

        // Prepare intent that redirects the user to FriendActivity
        Intent showFriendIntent = new Intent(context, FriendsPagerActivity.class);
        showFriendIntent.putExtra("INVITATION", true);
        PendingIntent pFriendIntent =
            PendingIntent.getActivity(context, 0, showFriendIntent, PendingIntent.FLAG_UPDATE_CURRENT);

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
    }

    /**
     * Display notification in status bar using notification manager
     * 
     * @param context
     *            the current context
     * @param notification
     *            th notification to display
     * @param notificationId
     *            the notification id
     */
    private static void displayNotification(Context context, Notification notification, long notificationId) {
        NotificationManager notificationManager =
            (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) notificationId, notification);
    }
}