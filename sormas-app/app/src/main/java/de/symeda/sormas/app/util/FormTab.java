package de.symeda.sormas.app.util;

import android.databinding.BindingAdapter;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;

import de.symeda.sormas.api.sample.ShipmentStatus;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.LabelField;
import de.symeda.sormas.app.component.LocationDialog;
import de.symeda.sormas.app.component.TextField;

public abstract class FormTab extends DialogFragment implements FormFragment {


    /**
     * Create the {@see LocationDialog} for given location.
     */
    protected void addLocationField(final Location location, final int locationFieldId, int locationBtnId, final Consumer positiveCallback ) {
        DatabaseHelper.getLocationDao().initializeLocation(location);

        // set the TextField for the location
        final LabelField locationText = (LabelField) getView().findViewById(locationFieldId);
        locationText.setValue(location.toString());

        if (locationText.getValue() == null || locationText.getValue().isEmpty()) {
            locationText.setValue("Enter location");
            locationText.setTextColor(Color.LTGRAY);
        }

        ImageButton btn = (ImageButton) getView().findViewById(locationBtnId);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final LocationDialog dialogBuilder = new LocationDialog(getActivity(), location, positiveCallback, null);
                AlertDialog locationDialog = dialogBuilder.create();
                locationDialog.show();
            }
        });

        View locationField = getView().findViewById(locationFieldId);
        locationField.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                final LocationDialog dialogBuilder = new LocationDialog(getActivity(), location, positiveCallback, null);
                AlertDialog locationDialog = dialogBuilder.create();
                locationDialog.show();
            }
        });
    }

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

    @BindingAdapter("app:sampleShipped")
    public static void setShipmentStatus(CheckBox checkBox, ShipmentStatus shipmentStatus) {
        checkBox.setChecked(true);
    }

}
