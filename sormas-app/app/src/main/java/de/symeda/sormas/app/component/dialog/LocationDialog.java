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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.location.AreaType;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.InfrastructureHelper;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends FormDialog {

	public static final String TAG = LocationDialog.class.getSimpleName();

	private Location data;
	private DialogLocationLayoutBinding contentBinding;

	// Constructor

	public LocationDialog(final FragmentActivity activity, Location location, UiFieldAccessCheckers fieldAccessCheckers) {
		this(activity, location, true, fieldAccessCheckers);
	}

	public LocationDialog(
		final FragmentActivity activity,
		Location location,
		boolean closeOnPositiveButtonClick,
		UiFieldAccessCheckers fieldAccessCheckers) {
		super(
			activity,
			R.layout.dialog_root_layout,
			R.layout.dialog_location_layout,
			R.layout.dialog_root_three_button_panel_layout,
			R.string.heading_location,
			-1,
			closeOnPositiveButtonClick,
			fieldAccessCheckers);

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
		List<Item> initialCountries = InfrastructureHelper.loadCountries();
		List<Item> initialRegions = InfrastructureHelper.loadRegions();
		List<Item> initialDistricts = InfrastructureHelper.loadDistricts(data.getRegion());
		List<Item> initialCommunities = InfrastructureHelper.loadCommunities(data.getDistrict());
		List<Item> initialFacilities = InfrastructureHelper.loadFacilities(data.getDistrict(), data.getCommunity(), data.getFacilityType());
		List<Item> facilityTypeGroupList = DataUtils.toItems(Arrays.asList(FacilityTypeGroup.values()), true);
		List<Item> facilityTypeList =
			data.getFacilityType() != null ? DataUtils.toItems(FacilityType.getTypes(data.getFacilityType().getFacilityTypeGroup())) : null;

		InfrastructureHelper.initializeHealthFacilityDetailsFieldVisibility(contentBinding.locationFacility, contentBinding.locationFacilityDetails);

		if (data.getCountry() == null) {
			for (Item countryItem : initialCountries) {
				Country country = (Country) countryItem.getValue();
				if (country != null && ConfigProvider.isConfiguredServer(country.getIsoCode())) {
					data.setCountry(country);
					break;
				}
			}
		}

		InfrastructureHelper.initializeFacilityFields(
			data,
			this.contentBinding.locationCountry,
			initialCountries,
			data.getCountry(),
			this.contentBinding.locationRegion,
			initialRegions,
			data.getRegion(),
			this.contentBinding.locationDistrict,
			initialDistricts,
			data.getDistrict(),
			this.contentBinding.locationCommunity,
			initialCommunities,
			data.getCommunity(),
			null,
			null,
			this.contentBinding.facilityTypeGroup,
			facilityTypeGroupList,
			this.contentBinding.locationFacilityType,
			facilityTypeList,
			this.contentBinding.locationFacility,
			initialFacilities,
			data.getFacility(),
			this.contentBinding.locationFacilityDetails,
			true);

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

		if (data.getId() == null) {
			setLiveValidationDisabled(true);
		}

		if (data.getFacility() == null) {
			contentBinding.locationFacilityDetails.setVisibility(GONE);
		} else {
			contentBinding.facilityTypeGroup.setValue(data.getFacilityType().getFacilityTypeGroup());
		}
	}

	public void setRequiredFieldsBasedOnCountry() {
		contentBinding.locationCountry.addValueChangedListener(e -> {
			Country country = (Country) e.getValue();
			if (country != null && ConfigProvider.isConfiguredServer(country.getIsoCode())) {
				this.setRegionAndDistrictRequired(true);
			} else {
				this.setRegionAndDistrictRequired(false);
				contentBinding.locationRegion.setValue(null);
				contentBinding.locationDistrict.setValue(null);
			}
		});
	}

	public void setRegionAndDistrictRequired(boolean required) {
		contentBinding.locationRegion.setRequired(required);
		contentBinding.locationDistrict.setRequired(required);
	}

	public void setFacilityFieldsVisible(boolean visible, boolean clearOnHidden) {
		final int visibility = visible ? VISIBLE : GONE;
		contentBinding.facilityTypeGroup.setVisibility(visibility);
		contentBinding.locationFacility.setVisibility(visibility);
		contentBinding.locationFacilityDetails.setVisibility(visibility);
		contentBinding.locationFacilityType.setVisibility(visibility);

		if (!visible && clearOnHidden) {
			contentBinding.facilityTypeGroup.setValue(null);
			contentBinding.locationFacility.setValue(null);
			contentBinding.locationFacilityDetails.setValue(null);
			contentBinding.locationFacilityType.setValue(null);
		}
	}

	public DialogLocationLayoutBinding getContentBinding() {
		return contentBinding;
	}

	@Override
	protected void onPositiveClick() {
		setLiveValidationDisabled(false);

		try {
			FragmentValidator.validate(getContext(), contentBinding);
		} catch (ValidationException e) {
			NotificationHelper.showDialogNotification(LocationDialog.this, ERROR, e.getMessage());
			return;
		}

		super.onPositiveClick();
	}

	@Override
	public boolean isDeleteButtonVisible() {
		return false;
	}

	@Override
	public boolean isRounded() {
		return true;
	}

	@Override
	public ControlButtonType getNegativeButtonType() {
		return ControlButtonType.LINE_SECONDARY;
	}

	@Override
	public ControlButtonType getPositiveButtonType() {
		return ControlButtonType.LINE_PRIMARY;
	}

	@Override
	public ControlButtonType getDeleteButtonType() {
		return ControlButtonType.LINE_DANGER;
	}

	public void configureAsPersonAddressDialog(boolean showDeleteButton) {
		if (showDeleteButton) {
			getDeleteButton().setVisibility(View.VISIBLE);
		}

		contentBinding.locationAddressType.setVisibility(View.VISIBLE);
		if (!ConfigProvider.isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			contentBinding.locationAddressType
				.initializeSpinner(DataUtils.toItems(Arrays.asList(PersonAddressType.getValues(ConfigProvider.getServerCountryCode()))));
		} else {
			contentBinding.locationAddressType.initializeSpinner(DataUtils.getEnumItems(PersonAddressType.class));
		}
		contentBinding.locationAddressType.setValidationCallback(() -> contentBinding.locationAddressType.getValue() != null);

		contentBinding.locationAddressType.addValueChangedListener(e -> {
			Object locationAddressTypeValue = contentBinding.locationAddressType.getValue();
			if (locationAddressTypeValue == null || PersonAddressType.HOME.equals(locationAddressTypeValue)) {
				contentBinding.locationAddressTypeDetails.setVisibility(GONE);
			} else {
				contentBinding.locationAddressTypeDetails.setVisibility(View.VISIBLE);
			}
			FacilityTypeGroup oldGroup = (FacilityTypeGroup) contentBinding.facilityTypeGroup.getValue();
			FacilityType oldType = (FacilityType) contentBinding.locationFacilityType.getValue();
			Facility oldFacility = (Facility) contentBinding.locationFacility.getValue();
			String oldDetails = contentBinding.locationFacilityDetails.getValue();
			contentBinding.facilityTypeGroup.setSpinnerData(null);
			if (PersonAddressType.HOME.equals(locationAddressTypeValue)) {
				contentBinding.facilityTypeGroup.setSpinnerData(DataUtils.toItems(FacilityTypeGroup.getAccomodationGroups()));
			} else {
				contentBinding.facilityTypeGroup.setSpinnerData(DataUtils.getEnumItems(FacilityTypeGroup.class));
			}
			contentBinding.facilityTypeGroup.setValue(oldGroup);
			contentBinding.locationFacilityType.setValue(oldType);
			contentBinding.locationFacility.setValue(oldFacility);
			contentBinding.locationFacilityDetails.setValue(oldDetails);
		});
	}
}
