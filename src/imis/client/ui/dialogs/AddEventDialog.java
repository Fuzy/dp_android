package imis.client.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import imis.client.R;

/**
 * Created with IntelliJ IDEA.
 * User: Martin Kadlec
 * Date: 28.5.13
 * Time: 20:08
 */
public class AddEventDialog extends DialogFragment {

    public interface AddEventDialogListener {
        public void onAddEventDialogPositiveClick();
        public void onAddEventDialogNegativeClick();
        public void onAddEventDialogNeutralClick();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.menu_add)
                .setMessage("messdage")
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AddEventDialogListener activity = (AddEventDialogListener) getActivity();
                        activity.onAddEventDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AddEventDialogListener activity = (AddEventDialogListener) getActivity();
                        activity.onAddEventDialogNegativeClick();
                    }
                })
                .setNeutralButton(R.string.other_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddEventDialogListener activity = (AddEventDialogListener) getActivity();
                        activity.onAddEventDialogNeutralClick();
                    }
                });

        return builder.create();
    }
}
