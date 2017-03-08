package de.symeda.sormas.app.component;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.Argumentable;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.Consumer;
import de.symeda.sormas.app.util.FormTab;


public class AddEditDialog extends AlertDialog.Builder implements Argumentable {

    private Bundle bundle;
    private FormTab editTab;

    public AddEditDialog(FragmentActivity activity, final AbstractDomainObject ado, final Consumer positiveCallback, final Callback negativeCallback, FormTab editTab) {
        super(activity);

        this.editTab = editTab;
        this.setTitle(activity.getResources().getString(R.string.headline_location));
        this.setView(this.editTab.getView());

        this.setPositiveButton(activity.getResources().getString(R.string.action_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (positiveCallback != null) {
                    positiveCallback.accept(ado);
                }
            }
        });
        this.setNegativeButton(activity.getResources().getString(R.string.action_dimiss), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (negativeCallback != null) {
                    negativeCallback.call();
                }
            }
        });

    }

    @Override
    public void setArguments(Bundle bundle) {
        if(bundle!=null) {
            this.bundle = bundle;
            this.editTab.setArguments(bundle);
            this.editTab.onResume();
        }
    }

    @Override
    public Bundle getArguments() {
        return bundle;
    }
}


