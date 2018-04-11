package de.symeda.sormas.app.component;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;

import de.symeda.sormas.app.util.Callback;

/**
 * Created by Mate Strysewske on 16.02.2018.
 */

public class ConfirmationDialog {

    private final Dialog confirmationDialog;

    /**
     * Builds a dialog with the given parameters. If positiveButtonText or negativeButtonText are null, the respective buttons are not displayed.
     */
    public ConfirmationDialog(final Activity activity, String title, String message, final String positiveButtonText, final String negativeButtonText,
                              final Callback positiveCallback, final Callback negativeCallback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false);

        if (positiveButtonText != null) {
            builder.setPositiveButton(positiveButtonText, null);
        }
        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText, null);
        }

        confirmationDialog = builder.create();

        confirmationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (positiveButtonText != null) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfirmationDialog.this.dismiss();
                            if (positiveCallback != null) {
                                positiveCallback.call();
                            }
                        }
                    });
                }
                if (negativeButtonText != null) {
                    Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfirmationDialog.this.dismiss();
                            if (negativeCallback != null) {
                                negativeCallback.call();
                            }
                        }
                    });
                }
            }
        });
    }

    public void show() {
        confirmationDialog.show();
    }

    public void dismiss() {
        confirmationDialog.dismiss();
    }

    public boolean isShowing() {
        return confirmationDialog.isShowing();
    }

}
