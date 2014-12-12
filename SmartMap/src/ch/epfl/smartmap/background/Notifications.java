package ch.epfl.smartmap.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.cache.Invitation;

/**
 * This class creates different sort of notifications
 * 
 * @author agpmilli
 */
public class Notifications {

    @SuppressWarnings("unused")
    private static final String TAG = Notifications.class.getSimpleName();

    private static final int VIBRATE_NOTIFICATION_TIME = 500;

    private static final int SILENT_NOTIFICATION_TIME = 100;
    private final static long[] PATTERN = {0, VIBRATE_NOTIFICATION_TIME, SILENT_NOTIFICATION_TIME,
        VIBRATE_NOTIFICATION_TIME};
    private static long notificationID = 0;

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
     * Create a notification in status bar from an invitation
     * 
     * @param invitation
     *            invitation on which notification is based on
     * @param context
     *            current context
     */
    public static void createNotification(Invitation invitation, Context context) {

        boolean notificationAllowed =
            ServiceContainer.getSettingsManager().notificationsEnabled()
                && (invitation.getStatus() == Invitation.UNREAD);

        switch (invitation.getType()) {
            case Invitation.EVENT_INVITATION:
                notificationAllowed =
                    notificationAllowed
                        && ServiceContainer.getSettingsManager().notificationsForEventInvitations();
                break;
            case Invitation.FRIEND_INVITATION:
                notificationAllowed =
                    notificationAllowed
                        && ServiceContainer.getSettingsManager().notificationsForFriendRequests();
                break;
            case Invitation.ACCEPTED_FRIEND_INVITATION:
                notificationAllowed =
                    notificationAllowed
                        && ServiceContainer.getSettingsManager().notificationsForFriendshipConfirmations();
                break;
            default:
                assert false;
                break;
        }

        if (notificationAllowed) {

            // Get ID and the number of ongoing Event notifications
            notificationID++;

            // Prepare intent that redirect the user to EventActivity
            Intent intent = invitation.getIntent();

            Log.d(TAG, "intent : " + intent);

            if (invitation.getType() == Invitation.EVENT_INVITATION) {
                intent.putExtra("EVENT", invitation.getEvent().getId());
            } else if (invitation.getType() == Invitation.FRIEND_INVITATION) {
                intent.putExtra("INVITATION", true);
            } else if (invitation.getType() == Invitation.ACCEPTED_FRIEND_INVITATION) {
                intent.putExtra("USER", invitation.getUser().getId());
            }

            intent.putExtra("NOTIFICATION", true);

            PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Add Big View Specific Configuration
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            String[] strings = new String[1];
            strings[0] = invitation.getSubtitle();

            // Sets a title for the Inbox style big view
            inboxStyle.setBigContentTitle(invitation.getTitle());
            // Moves events into the big view
            for (int i = 0; i < strings.length; i++) {
                inboxStyle.addLine(strings[i]);
            }

            // Build notification
            NotificationCompat.Builder noti =
                new NotificationCompat.Builder(context)
                    // Sets all notification's specifications in the builder
                    .setStyle(inboxStyle).setAutoCancel(true).setContentTitle(invitation.getTitle())
                    .setContentText(invitation.getSubtitle()).setSmallIcon(R.drawable.ic_launcher)
                    .setTicker(invitation.getSubtitle()).setContentIntent(pendingIntent);
            if (ServiceContainer.getSettingsManager().notificationsVibrate()) {
                noti.setVibrate(PATTERN);
            }

            displayNotification(context, noti.build(), notificationID);

        }
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