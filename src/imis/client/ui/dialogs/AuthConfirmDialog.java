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
 * Date: 2.6.13
 * Time: 19:11
 */
public class AuthConfirmDialog extends DialogFragment {
    private String title;
    private String message;

    public interface AuthConfirmDialogListener {
        public void onConfirmClickPositiveClick();
    }

    public AuthConfirmDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AuthConfirmDialogListener activity = (AuthConfirmDialogListener) getActivity();
                        activity.onConfirmClickPositiveClick();
                    }
                })
                .setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        return alertDialog;
    }
}
