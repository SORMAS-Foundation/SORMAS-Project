package de.symeda.sormas.app.component;

import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.annotations.Nullable;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.databinding.PreviousHospitalizationEditFragmentLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormFragment;


abstract public class AbstractFormDialogFragment<FormClass> extends DialogFragment implements FormFragment {

    private FormClass formItem;

    private Consumer positiveCallback;
    private Consumer deleteCallback;
    private String title;

    abstract public View onCreateDialogView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = onCreateDialogView(getActivity().getLayoutInflater(), null, null);
        onViewCreated(view, null);
        alertDialogBuilder.setView(view);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (positiveCallback != null) {
                    positiveCallback.accept(formItem);
                }
            }
        });
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
