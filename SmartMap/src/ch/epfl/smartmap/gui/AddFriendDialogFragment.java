package ch.epfl.smartmap.gui;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;



/**
 * Fragment that pops up when the user wants to add someone as a Friend via AddFriendActivity
 * @author rbsteinm
 */
public class AddFriendDialogFragment extends DialogFragment {
    private String mFriendName;
    
    public AddFriendDialogFragment(String friendName){
        mFriendName = friendName;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setMessage("Add " + mFriendName + " as a friend?");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO handle friend add in the database
                //TODO handle friend add in cache
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
               AddFriendDialogFragment.this.getDialog().cancel();
            }
        });
        return builder.create();
    }
    
}
