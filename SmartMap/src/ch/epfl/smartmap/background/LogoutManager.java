package ch.epfl.smartmap.background;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.StartActivity;

import com.facebook.Session;

/**
 * Lets the user logout from SmartMap. This is a singleton, use LogoutManager.getInstance().
 *
 * @author SpicyCH
 */
public final class LogoutManager {

    private static LogoutManager mInstance;
    private static Context mContext;

    /**
     * Constructor
     *
     * @param context
     */
    private LogoutManager(Context context) {
        mContext = context;
    }

    /**
     * First shows an alert dialog to the user for confirmation, then log him/she out from SmartMap if he confirms the
     * action.
     *
     * @author SpicyCH
     */
    public void showConfirmationThenLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set title
        alertDialogBuilder.setTitle(mContext.getString(R.string.logout_dialog_title));

        // set dialog message
        alertDialogBuilder.setMessage(mContext.getString(R.string.logout_dialog_message)).setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.logout_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        Toast.makeText(mContext, mContext.getString(R.string.logout_toast_confirm), Toast.LENGTH_SHORT)
                                .show();
                        LogoutManager.this.logout();
                    }
                }).setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Reinitialize all services, disable FB session and return to the StartActivity.<br />
     * Note: we don't need to destroy the PHP session on the server since we can re-auth with another login on top of
     * the previous one.
     *
     * @author SpicyCH
     */
    private void logout() {
        // Clear cache and database. Note: the preferences set in the SettingsActivity will be kept since they are local
        // to the device.

        ServiceContainer.initSmartMapServices(mContext);

        // Close the FB session to avoid re-logging in automatically
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().closeAndClearTokenInformation();
        }

        // Stop the service
        mContext.stopService(new Intent(mContext, InvitationsService.class));
        mContext.stopService(new Intent(mContext, OwnPositionService.class));

        // Go to the SartActivity where the user can log back again with (another) account.
        mContext.startActivity(new Intent(mContext, StartActivity.class));
    }

    /**
     * Gets an instance of <code>LogoutManager</code>.
     *
     * @param context
     * @return the unique instance
     * @author SpicyCH
     */
    public static LogoutManager getInstance() {
        if (mContext == null) {
            throw new UnsupportedOperationException(
                    "You must initialize the LogoutManager before you can get an instance");
        }
        if (mInstance == null) {
            mInstance = new LogoutManager(mContext);
        }
        return mInstance;
    }

    public static void initialize(Context context) {
        mInstance = new LogoutManager(context);
    }
}
