/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.component.dialog;

import java.util.List;

import android.content.Context;
import android.util.Log;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.AppFieldAccessCheckers;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends FormDialog {

	public static final String TAG = LocationDialog.class.getSimpleName();

	private Location data;
	private DialogLocationLayoutBinding contentBinding;

	// Constructor

	public LocationDialog(final FragmentActivity activity, Location location, AppFieldAccessCheckers fieldAccessCheckers) {
		this(activity, location, true, fieldAccessCheckers);
	}

	public LocationDialog(final FragmentActivity activity, Location location, boolean closeOnPositiveButtonClick, AppFieldAccessCheckers fieldAccessCheckers) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_location_layout,
			R.layout.dialog_root_two_button_panel_layout,
			R.string.heading_location,
			-1, closeOnPositiveButtonClick);

		this.data = location;
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
		List<Item> initialRegions = InfrastructureHelper.loadRegions();
		List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
		List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
		InfrastructureHelper.initializeRegionFields(
			this.contentBinding.locationRegion,
			initialRegions,
			data.getRegion(),
			this.contentBinding.locationDistrict,
			initialDistricts,
			data.getDistrict(),
			this.contentBinding.locationCommunity,
			initialCommunities,
			data.getCommunity());

		setFieldVisibilitiesAndAccesses(LocationDto.class, contentBinding.mainContent);
		if (!isFieldAccessible(LocationDto.class, LocationDto.COMMUNITY)) {
			this.contentBinding.locationRegion.setEnabled(false);
			this.contentBinding.locationDistrict.setEnabled(false);
		}

		contentBinding.locationAreaType.initializeSpinner(DataUtils.getEnumItems(AreaType.class));

		// "Pick GPS Coordinates" confirmation dialog
		this.contentBinding.pickGpsCoordinates.setOnClickListener(v -> {
			final ConfirmationDialog confirmationDialog = new ConfirmationDialog(
				getActivity(),
				R.string.heading_confirmation_dialog,
				R.string.confirmation_pick_gps,
				R.string.yes,
				R.string.no);

			confirmationDialog.setPositiveCallback(() -> {
				android.location.Location phoneLocation = LocationService.instance().getLocation(getActivity());
				if (phoneLocation != null) {
					contentBinding.locationLatitude.setDoubleValue(phoneLocation.getLatitude());
					contentBinding.locationLongitude.setDoubleValue(phoneLocation.getLongitude());
					contentBinding.locationLatLonAccuracy.setFloatValue(phoneLocation.getAccuracy());
				} else {
					NotificationHelper.showDialogNotification(LocationDialog.this, NotificationType.WARNING, R.string.message_gps_problem);
				}
			});
			confirmationDialog.show();
		});
	}

	public void setRegionAndDistrictRequired(boolean required) {
		contentBinding.locationRegion.setRequired(required);
		contentBinding.locationDistrict.setRequired(required);
	}

	public DialogLocationLayoutBinding getContentBinding() {
		return contentBinding;
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
