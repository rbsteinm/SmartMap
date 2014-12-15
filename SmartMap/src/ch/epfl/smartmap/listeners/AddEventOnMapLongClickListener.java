package ch.epfl.smartmap.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import ch.epfl.smartmap.R;
import ch.epfl.smartmap.activities.AddEventActivity;

import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;

/**
 * Listener that loads AddEventActivity with the location when the map is long clicked (if the user confirms his action
 * with pressing the positivit button of an <code>AlertDialog</code>.
 *
 * @author SpicyCH
 */
public class AddEventOnMapLongClickListener implements OnMapLongClickListener {

    private static final String TAG = AddEventOnMapLongClickListener.class.getSimpleName();

    private final Activity mActivity;

    public AddEventOnMapLongClickListener(Activity activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

        // The user was in MainActivity and long clicked to create an event (?)
        Log.d(TAG, "Map longclicked at latLng : " + latLng);

        // Ask user if he really wants to create an event.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);

        // Set title.
        alertDialogBuilder.setTitle(mActivity.getString(R.string.map_long_clicked_dialog_title));

        // Set dialog message.
        alertDialogBuilder
                .setMessage(mActivity.getString(R.string.map_long_clicked_dialog_message))
                .setCancelable(false)
                .setPositiveButton(mActivity.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                Intent result = new Intent(mActivity, AddEventActivity.class);

                                result.putExtra(AddEventActivity.LOCATION_EXTRA, latLng);
                                mActivity.startActivity(result);

                            }
                        })
                .setNegativeButton(mActivity.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Nothing
                            }
                        });

        // show alert dialog.
        alertDialogBuilder.create().show();

    }
}
