package de.symeda.sormas.app.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.LocationDialog;
import de.symeda.sormas.app.component.TextField;

public abstract class FormTab extends DialogFragment implements FormFragment {


    /**
     * Create the {@see LocationDialog} for given location.
     * Triggers the updateLocation method.
     */
    protected void addLocationField(final Location location, final int locationFieldId, int locationBtnId) {
        DatabaseHelper.getLocationDao().initializeLocation(location);

        // set the TextField for the location
        final TextField locationText = (TextField) getView().findViewById(locationFieldId);
        locationText.setEnabled(false);
        locationText.setValue(location.toString());

        ImageButton btn = (ImageButton) getView().findViewById(locationBtnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final LocationDialog dialogBuilder = new LocationDialog(getActivity(), location);
                dialogBuilder.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        updateLocation(dialogBuilder.getLocation());
                    }
                });

                AlertDialog newPersonDialog = dialogBuilder.create();
                newPersonDialog.show();
            }
        });
    }

    /**
     * Called from positiveButton inside the LocationDialog. Have to be overwritten for manual reverse databinding.
     * @param location
     */
    protected void updateLocation(Location location) {};

    protected void deactivateField(View v) {
        v.setEnabled(false);
        v.clearFocus();
    }

    protected void setFieldVisible(View v, boolean visible) {
        if (visible) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.INVISIBLE);
            v.clearFocus();
        }
    }

    protected void setFieldGone(View v) {
        v.setVisibility(View.GONE);
        v.clearFocus();
    }

    protected void activateField(View v) {
        v.setEnabled(true);
    }

    protected void reloadFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}
