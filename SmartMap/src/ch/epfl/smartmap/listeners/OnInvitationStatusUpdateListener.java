package ch.epfl.smartmap.listeners;

/**
 * Listens to invitation status updates in the database
 * 
 * @author ritterni
 */
public interface OnInvitationStatusUpdateListener {
    void onInvitationStatusUpdate(long userID, int newStatus);
}
