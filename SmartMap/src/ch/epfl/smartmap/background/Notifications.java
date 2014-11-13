package ch.epfl.smartmap.background;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.FriendsActivity;
import ch.epfl.smartmap.activities.ShowEventsActivity;
import ch.epfl.smartmap.cache.Event;
import ch.epfl.smartmap.cache.Friend;

/**
 * Class that creates different sort of notifications
 * 
 * @author agpmilli
 * 
 */
public class Notifications {

	public static void newFriendNotification(View view, Activity activity,
			Friend friend) {
		// Prepare intent which is triggered if the
		// notification is selected
		PendingIntent pFriendIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						FriendsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);
		noti.setContentTitle(friend.getName() + " wants to be your friend");
		noti.setContentText("Click here to open your list of friends")
				.setSmallIcon(R.drawable.ic_launcher);
		noti.setContentIntent(pFriendIntent);
		// TODO : determine if we need those buttons
		// noti.addAction(0, "Decline", pIntent);
		// noti.addAction(0, "Accept", pIntent).build();
		@SuppressWarnings("static-access")
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(activity.getBaseContext().NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti.build());
	}
	
	public static void acceptedNotification(View view, Activity activity,
			Friend friend) {
		// Prepare intent which is triggered if the
		// notification is selected
		PendingIntent pFriendIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						FriendsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);
		noti.setContentTitle(friend.getName() + " accepted your invitation");
		noti.setContentText("Click here to open your list of friends")
				.setSmallIcon(R.drawable.ic_launcher);
		noti.setContentIntent(pFriendIntent);
		@SuppressWarnings("static-access")
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(activity.getBaseContext().NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti.build());
	}

	public static void newEventNotification(View view, Activity activity,
			Event event) {
		// Prepare intent which is triggered if the
		// notification is selected
		PendingIntent pEventIntent = PendingIntent.getActivity(activity
				.getBaseContext(), 0, new Intent(activity,
						ShowEventsActivity.class), 0);

		// Build notification
		NotificationCompat.Builder noti = new NotificationCompat.Builder(
				activity);
		noti.setContentTitle(event.getCreatorName() + " invite you to "
				+ event.getName());
		noti.setContentText(event.getDescription()).setSmallIcon(
				R.drawable.ic_launcher);
		noti.setContentIntent(pEventIntent);
		noti.addAction(0, "Open SmartMap", pEventIntent).build();
		@SuppressWarnings("static-access")
		NotificationManager notificationManager = (NotificationManager) activity
				.getSystemService(activity.getBaseContext().NOTIFICATION_SERVICE);
		notificationManager.notify(0, noti.build());
	}
}
