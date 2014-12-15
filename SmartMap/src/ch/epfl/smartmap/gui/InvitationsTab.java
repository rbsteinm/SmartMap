package ch.epfl.smartmap.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.background.ServiceContainer;
import ch.epfl.smartmap.cache.Invitation;
import ch.epfl.smartmap.callbacks.NetworkRequestCallback;
import ch.epfl.smartmap.listeners.OnCacheListener;

/**
 * Fragment displaying your invitations in FriendsActivity
 * 
 * @author marion-S
 */
public class InvitationsTab extends ListFragment {

    private List<Invitation> mInvitationList;
    private final Context mContext;

    /**
     * Constructor
     * 
     * @param ctx
     */
    public InvitationsTab(Context ctx) {
        mContext = ctx;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment_invitations_tab, container, false);
        mInvitationList =
            new ArrayList<Invitation>(ServiceContainer.getCache().getUnansweredFriendInvitations());

        // Create custom Adapter and pass it to the Activity
        this.setListAdapter(new FriendInvitationListItemAdapter(mContext, mInvitationList));
        Log.d("inv tab", "nb displayed inv " + mInvitationList.size());
        // Initialize the listener to update the diaplayed list when the invitation list is updated in the
        // cache
        ServiceContainer.getCache().addOnCacheListener(new OnCacheListener() {
            @Override
            public void onInvitationListUpdate() {
                mInvitationList =
                    new ArrayList<Invitation>(ServiceContainer.getCache().getUnansweredFriendInvitations());
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InvitationsTab.this.setListAdapter(new FriendInvitationListItemAdapter(mContext,
                            mInvitationList));
                    }
                });

            }
        });
        return view;
    }

    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
     * , android.view.View, int, long) When a list item is clicked, display a
     * dialog to ask whether to accept or decline the invitation
     */
    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        Invitation invitation = mInvitationList.get(position);
        Log.d("inv tab", "item clicked");
        this.displayAcceptFriendDialog(invitation);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();

        mInvitationList =
            new ArrayList<Invitation>(ServiceContainer.getCache().getUnansweredFriendInvitations());
        this.setListAdapter(new FriendInvitationListItemAdapter(mContext, mInvitationList));
    }

    /*
     * (non-Javadoc)
     * @see
     * android.support.v4.app.ListFragment#onCreateView(android.view.LayoutInflater
     * , android.view.ViewGroup, android.os.Bundle)
     */
    /**
     * A dialog to ask whether to accept or decline the invitation of the given
     * user
     * 
     * @param invitation
     */
    private void displayAcceptFriendDialog(final Invitation invitation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.add) + " " + invitation.getUser().getName() + " "
            + mContext.getString(R.string.as_a_friend));

        // Add positive button
        builder.setPositiveButton(mContext.getString(R.string.yes_accept),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Accept invitation in Cache
                    ServiceContainer.getCache().acceptInvitation(invitation,
                        new AcceptInvitationRequestCallback(invitation));
                }
            });

        // Add negative button
        builder.setNegativeButton(R.string.no_decline, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Decline invitation in Cache
                ServiceContainer.getCache().declineInvitation(invitation,
                    new DeclineInvitationRequestCallback(invitation));
            }
        });

        // display the AlertDialog
        builder.create().show();
    }

    /**
     * An implementation of {@linkNetworkRequestCallback} executed with an accepting invitation request to the
     * server
     * 
     * @author marion-S
     */
    private class AcceptInvitationRequestCallback implements NetworkRequestCallback<Void> {

        private final Invitation mInvitation;

        /**
         * Constructor
         * 
         * @param invitation
         */
        public AcceptInvitationRequestCallback(Invitation invitation) {
            mInvitation = invitation;
        }

        /*
         * (non-Javadoc)
         * @see ch.epfl.smartmap.callbacks.NetworkRequestCallback#onFailure(java.lang.Exception)
         */
        @Override
        public void onFailure(Exception e) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                        ((Activity) mContext).getString(R.string.accept_friend_network_error),
                        Toast.LENGTH_SHORT).show();
                }
            });

        }

        /*
         * (non-Javadoc)
         * @see ch.epfl.smartmap.callbacks.NetworkRequestCallback#onSuccess(java.lang.Object)
         */
        @Override
        public void onSuccess(Void result) {
            // We need to get the invitation from the cache as
            // it
            // has been modified
            Invitation updated = ServiceContainer.getCache().getInvitation(mInvitation.getId());
            // We run it's intent
            InvitationsTab.this.startActivity(updated.getIntent());

        }

    }

    /**
     * An implementation of {@linkNetworkRequestCallback} executed with a declining invitation request to the
     * server
     * 
     * @author marion-S
     */
    private class DeclineInvitationRequestCallback implements NetworkRequestCallback<Void> {

        private final Invitation mInvitation;

        /**
         * Constructor
         * 
         * @param invitation
         */
        public DeclineInvitationRequestCallback(Invitation invitation) {
            mInvitation = invitation;
        }

        /*
         * (non-Javadoc)
         * @see ch.epfl.smartmap.callbacks.NetworkRequestCallback#onFailure(java.lang.Exception)
         */
        @Override
        public void onFailure(Exception e) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,
                        ((Activity) mContext).getString(R.string.decline_friend_network_error),
                        Toast.LENGTH_SHORT).show();
                }
            });

        }

        /*
         * (non-Javadoc)
         * @see ch.epfl.smartmap.callbacks.NetworkRequestCallback#onSuccess(java.lang.Object)
         */
        @Override
        public void onSuccess(Void result) {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Remove invitation from list
                    mInvitationList.remove(mInvitation);
                    InvitationsTab.this.setListAdapter(new FriendInvitationListItemAdapter(mContext,
                        mInvitationList));

                    // Confirm decline
                    Toast
                        .makeText(mContext, mContext.getString(R.string.decline_confirm), Toast.LENGTH_SHORT)
                        .show();
                }
            });
        }

    }
}