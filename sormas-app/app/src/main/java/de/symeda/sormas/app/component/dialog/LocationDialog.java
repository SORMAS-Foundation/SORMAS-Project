/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.dialog;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.Callback;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends AbstractDialog {

    public static final String TAG = LocationDialog.class.getSimpleName();

    private Location data;
    private DialogLocationLayoutBinding contentBinding;

    // Constructor

    public LocationDialog(final FragmentActivity activity, Location location) {
        super(activity, R.layout.dialog_root_layout, R.layout.dialog_location_layout,
                R.layout.dialog_root_two_button_panel_layout, R.string.heading_location, -1);

        this.data = location;
    }

    // Instance methods

    private void updateGpsTextView() {
        if (contentBinding == null) {
            return;
        }

        contentBinding.locationLatLon.setValue(data.getGpsLocation());
    }

    // Overrides

    @Override
    protected void setContentBinding(Context context, ViewDataBinding binding, String layoutName) {
        this.contentBinding = (DialogLocationLayoutBinding) binding;

        if (!binding.setVariable(BR.data, data)) {
            Log.e(TAG, "There is no variable 'data' in layout " + layoutName);
        }
    }

    @Override
    protected void initializeContentView(ViewDataBinding rootBinding, ViewDataBinding buttonPanelBinding) {
        updateGpsTextView();

        List<Item> initialRegions = InfrastructureHelper.loadRegions();
        List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
        List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
        InfrastructureHelper.initializeRegionFields(this.contentBinding.locationRegion, initialRegions,
                this.contentBinding.locationDistrict, initialDistricts,
                this.contentBinding.locationCommunity, initialCommunities);

        contentBinding.locationAreaType.initializeSpinner(DataUtils.getEnumItems(AreaType.class));

        // "Pick GPS Coordinates" confirmation dialog
        this.contentBinding.pickGpsCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmationDialog confirmationDialog = new ConfirmationDialog(getActivity(),
                        R.string.heading_confirmation_dialog,
                        R.string.confirmation_pick_gps,
                        R.string.yes,
                        R.string.no);

                confirmationDialog.setPositiveCallback(new Callback() {
                    @Override
                    public void call() {
                        confirmationDialog.dismiss();

                        android.location.Location phoneLocation = LocationService.instance().getLocation(getActivity());
                        if (phoneLocation != null) {
                            data.setLatitude(phoneLocation.getLatitude());
                            data.setLongitude(phoneLocation.getLongitude());
                            data.setLatLonAccuracy(phoneLocation.getAccuracy());
                        } else {
                            data.setLatitude(null);
                            data.setLongitude(null);
                            data.setLatLonAccuracy(null);
                        }

                        updateGpsTextView();
                    }
                });

                confirmationDialog.show();
            }
        });
    }

    @Override
    public boolean isRounded() {
        return true;
    }

    @Override
    public ControlButtonType getNegativeButtonType() {
        return ControlButtonType.LINE_DANGER;
    }

    @Override
    public ControlButtonType getPositiveButtonType() {
        return ControlButtonType.LINE_PRIMARY;
    }

}
