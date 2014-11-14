package ch.epfl.smartmap.background;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;

/**
 * This class creates different sort of notifications
 * 
 * @author agpmilli
 */
public class Notifications {

	/**
	 * Create a friend invitation notification and notify it
	 * 
	 * @param view
	 *            The current view
	 * @param activity
	 *            The current activity
	 * @param friend
	 *            The inviter
	 */
	public static void newFriendNotification(View view, Activity activity,
			Friend friend) {
		// Prepare intent that redirects the user to FriendActivity
		PendingIntent pFriendIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						FriendsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);

		// Add Big View Specific Configuration
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		String[] events = new String[2];
		events[0] = new String(friend.getName() + " wants to be your friend");
		events[1] = new String("Click here to open your list of friends");

		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle("SmartMap Friend Invitation");
		// Moves events into the big view
		for (int i = 0; i < events.length; i++) {
			inboxStyle.addLine(events[i]);
		}

		// Add all notification's specifications in the builder
		noti.setStyle(inboxStyle);
		noti.setAutoCancel(true);
		noti.setContentIntent(pFriendIntent);
		noti.setSmallIcon(R.drawable.ic_launcher);
		noti.setTicker(friend.getName() + " wants to be your friend");

		noti.setContentTitle("SmartMap Friend Invitation");
		noti.setContentText(friend.getName()
				+ " wants to be your friend \n Click here to open your list of friends");
		// TODO: determine if we need those buttons
		// noti.addAction(0, "Decline", pIntent);
		// noti.addAction(0, "Accept", pIntent).build();

		activity.getBaseContext();
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, noti.build());
	}

	/**
	 * Create an accepted friend invitation notification and notify it
	 * 
	 * @param view
	 *            The current view
	 * @param activity
	 *            The current activity
	 * @param friend
	 *            The invited
	 */
	public static void acceptedNotification(View view, Activity activity,
			Friend friend) {


		// Prepare intent that redirects the user to FriendActivity
		PendingIntent pFriendIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						FriendsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);

		// Add Big View Specific Configuration
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		String[] events = new String[2];
		events[0] = new String(friend.getName() + " accepted your invitation. ");
		events[1] = new String("Click here to open your list of friends");

		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle("SmartMap friend invitation accepted");
		// Moves events into the big view
		for (int i = 0; i < events.length; i++) {
			inboxStyle.addLine(events[i]);
		}

		// Sets all notification's specifications in the builder
		noti.setStyle(inboxStyle);
		noti.setAutoCancel(true);
		noti.setContentTitle("Friend invitation accepted");
		noti.setContentText(friend.getName()
				+ " accepted your invitation. \n Click here to open your list of friends");
		noti.setSmallIcon(R.drawable.ic_launcher);
		noti.setTicker(friend.getName() + " accepted your invitation");
		noti.setContentIntent(pFriendIntent);

		activity.getBaseContext();
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(2, noti.build());
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
	public static void newEventNotification(View view, Activity activity,
			Event event) {
		// Prepare intent that redirect the user to EventActivity
		PendingIntent pEventIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						ShowEventsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);
		// Add Big View Specific Configuration
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

		String[] events = new String[2];
		events[0] = new String(event.getCreatorName() + " invite you to "
				+ event.getName());
		events[1] = new String("Click here to open your list of events");

		// Sets a title for the Inbox style big view
		inboxStyle.setBigContentTitle("SmartMap Event Invitation");
		// Moves events into the big view
		for (int i = 0; i < events.length; i++) {
			inboxStyle.addLine(events[i]);
		}
		
		// Sets all notification's specifications in the builder
		noti.setStyle(inboxStyle);
		noti.setAutoCancel(true);
		noti.setContentTitle("SmartMap Event Invitation");
		noti.setContentText(event.getCreatorName() + " invite you to "
				+ event.getName() + "\n Click here to open your list of events");
		noti.setSmallIcon(R.drawable.ic_launcher);
		noti.setTicker(event.getCreatorName() + " invites you to "
				+ event.getName());
		noti.setContentIntent(pEventIntent);
		
		// Build the notification and issues it with notification manager.
		activity.getBaseContext();
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(3, noti.build());
	}
}
