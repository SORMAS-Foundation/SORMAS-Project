package de.symeda.sormas.app.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import de.symeda.sormas.app.R;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormFragment;

/**
 * ATTENTION: This class is not using DialogFragment in the appropriate way
 * (see https://developer.android.com/reference/android/app/DialogFragment).
 *
 * It keeps state variables and so doesn't support resuming based on savedInstanceState.
 * Instead initializeSpinner needs to be called.
 *
 * DialogFragment is deprecated starting in Android P anyway.
 * Long-term we should replace this using another approach.
 */
abstract public class AbstractFormDialogFragment<FormClass> extends DialogFragment implements FormFragment {

    private FormClass formItem;

    private Consumer positiveCallback;
    private Consumer deleteCallback;
    private String title;

    abstract public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (formItem == null) {
            // when the fragement is restored it's ok to have formItem == null. Otherwise we have to throw an exception
            // sadly we can't find out whether it was restored
            // throw new IllegalStateException("Make sure to call initializeSpinner before creating (showing) the dialog. May also be called by a dialog that was send to the background");
            setShowsDialog(false);
            dismiss();
            return null;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = onCreateDialogView(getActivity().getLayoutInflater(), null, null);
        onViewCreated(view, null);
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.action_done), null);
        alertDialogBuilder.setNegativeButton(getActivity().getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton(
                getActivity().getResources().getString(R.string.action_delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deleteCallback != null) {
                            deleteCallback.accept(formItem);
                        }
                    }
                });

        return alertDialogBuilder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positiveCallback != null) {
                    positiveCallback.accept(formItem);
                }
            }
        });
    }

    // gives all needed config params
    public void initialize(FormClass formItem, final Consumer positiveCallback, final Consumer deleteCallback, String title) {
        this.formItem = formItem;
        this.positiveCallback = positiveCallback;
        this.deleteCallback = deleteCallback;
        this.title = title;
    }

    protected FormClass getFormItem() {
        return this.formItem;
    }
}
