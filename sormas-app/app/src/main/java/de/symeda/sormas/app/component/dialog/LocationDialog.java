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

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentActivity;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.area.AreaType;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonAddressType;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.region.Country;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlButtonType;
import de.symeda.sormas.app.component.controls.ControlTextEditField;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.core.notification.NotificationType;
import de.symeda.sormas.app.databinding.DialogLocationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureDaoHelper;
import de.symeda.sormas.app.util.InfrastructureFieldsDependencyHandler;
import de.symeda.sormas.app.util.LocationService;

public class LocationDialog extends FormDialog {

	public static final String TAG = LocationDialog.class.getSimpleName();

	private Location data;
	private Facility previousFacility;
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
		if (location != null) {
			this.previousFacility = location.getFacility();
		}
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
		List<Item> initialContinents = InfrastructureDaoHelper.loadContinents();
		List<Item> initialSubcontinents = InfrastructureDaoHelper.loadSubcontinents();
		List<Item> initialCountries = InfrastructureDaoHelper.loadCountries();
		List<Item> initialRegions = InfrastructureDaoHelper.loadRegionsByServerCountry();
		List<Item> initialDistricts = InfrastructureDaoHelper.loadDistricts(data.getRegion());
		List<Item> initialCommunities = InfrastructureDaoHelper.loadCommunities(data.getDistrict());
		List<Item> initialFacilities = InfrastructureDaoHelper.loadFacilities(data.getDistrict(), data.getCommunity(), data.getFacilityType());
		List<Item> facilityTypeGroupList = DataUtils.toItems(Arrays.asList(FacilityTypeGroup.values()), true);
		List<Item> facilityTypeList =
			data.getFacilityType() != null ? DataUtils.toItems(FacilityType.getTypes(data.getFacilityType().getFacilityTypeGroup())) : null;

		InfrastructureDaoHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.locationFacility, contentBinding.locationFacilityDetails);

		if (data.getCountry() == null) {
			String serverCountryName = ConfigProvider.getServerCountryName();
			for (Item countryItem : initialCountries) {
				Country country = (Country) countryItem.getValue();
				if (country != null && serverCountryName != null && serverCountryName.equalsIgnoreCase(country.getName())) {
					data.setCountry(country);
					break;
				}
			}
		}

		InfrastructureFieldsDependencyHandler.instance.initializeFacilityFields(
			data,
			this.contentBinding.locationContinent,
			initialContinents,
			data.getContinent(),
			this.contentBinding.locationSubcontinent,
			initialSubcontinents,
			data.getSubcontinent(),
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
		if(!isFieldAccessible(LocationDto.class, LocationDto.FACILITY)){
			FieldVisibilityAndAccessHelper.setFieldInaccessibleValue(contentBinding.facilityTypeGroup);
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

        contentBinding.locationLatitude.setValidationCallback(() -> {
            Double latitude = ControlTextEditField.getDoubleValue(contentBinding.locationLatitude);
            return ValidationHelper.validateLatitude(latitude, contentBinding.locationLatitude);
        });

        contentBinding.locationLongitude.setValidationCallback(() -> {
            Double longitude = ControlTextEditField.getDoubleValue(contentBinding.locationLongitude);
            return ValidationHelper.validateLongitude(longitude, contentBinding.locationLongitude);
        });

		if (data.getId() == null) {
			setLiveValidationDisabled(true);
		}

		if (data.getFacility() == null) {
			contentBinding.locationFacilityDetails.setVisibility(GONE);
		} else {
			contentBinding.facilityTypeGroup.setValue(data.getFacilityType().getFacilityTypeGroup());
		}

		if (data.getFacilityType() != null) {
			contentBinding.locationFacilityType.setValue(data.getFacilityType());
			contentBinding.facilityTypeGroup.setValue(data.getFacilityType().getFacilityTypeGroup());
		} else {
			setFacilityContactPersonFieldsVisible(false, true);
		}

		contentBinding.locationFacilityType.addValueChangedListener(field -> {
			if (field.getValue() == null) {
				setFacilityContactPersonFieldsVisible(false, true);
			} else {
				setFacilityContactPersonFieldsVisible(true, true);
			}
		});

		contentBinding.locationFacility.addValueChangedListener(field -> {
			final Facility facility = (Facility) field.getValue();

			// Need to check the old value as this listener is executed when initializing the field
			// field.isDirty() always return true making it not usable in our case
			if (this.previousFacility != null && this.previousFacility.equals(facility)) {
				// The field didn't changed
				return;
			}
			this.previousFacility = facility;

			if (facility != null
				&& (StringUtils.isNotEmpty(facility.getCity())
					|| StringUtils.isNotEmpty(facility.getPostalCode())
					|| StringUtils.isNotEmpty(facility.getStreet())
					|| StringUtils.isNotEmpty(facility.getHouseNumber())
					|| StringUtils.isNotEmpty(facility.getAdditionalInformation())
					|| facility.getAreaType() != null
					|| facility.getLatitude() != null
					|| facility.getLongitude() != null
					|| (StringUtils.isNotEmpty(facility.getContactPersonFirstName())
						&& StringUtils.isNotEmpty(facility.getContactPersonLastName())))) {
				if ((StringUtils.isNotEmpty(contentBinding.locationCity.getValue())
					&& !contentBinding.locationCity.getValue().equals(facility.getCity()))
					|| (StringUtils.isNotEmpty(contentBinding.locationPostalCode.getValue())
						&& !contentBinding.locationPostalCode.getValue().equals(facility.getPostalCode()))
					|| (StringUtils.isNotEmpty(contentBinding.locationStreet.getValue())
						&& !contentBinding.locationStreet.getValue().equals(facility.getStreet()))
					|| (StringUtils.isNotEmpty(contentBinding.locationHouseNumber.getValue())
						&& !contentBinding.locationHouseNumber.getValue().equals(facility.getHouseNumber()))
					|| (StringUtils.isNotEmpty(contentBinding.locationAdditionalInformation.getValue())
						&& !contentBinding.locationAdditionalInformation.getValue().equals(facility.getAdditionalInformation()))
					|| (contentBinding.locationAreaType.getValue() != null && contentBinding.locationAreaType.getValue() != facility.getAreaType())
					|| (StringUtils.isNotEmpty(contentBinding.locationContactPersonFirstName.getValue())
						&& StringUtils.isNotEmpty(contentBinding.locationContactPersonLastName.getValue()))
					|| (StringUtils.isNotEmpty(contentBinding.locationLatitude.getValue())
						&& !Double.valueOf(contentBinding.locationLatitude.getValue()).equals(facility.getLatitude()))
					|| (StringUtils.isNotEmpty(contentBinding.locationLongitude.getValue())
						&& !Double.valueOf(contentBinding.locationLongitude.getValue()).equals(facility.getLongitude()))) {
					ConfirmationDialog confirmationDialog =
						new ConfirmationDialog(getActivity(), R.string.heading_location, -1, R.string.yes, R.string.no);
					confirmationDialog.getConfig().setSubHeading(I18nProperties.getString(Strings.confirmationLocationFacilityAddressOverride));
					confirmationDialog.setPositiveCallback(() -> overrideLocationDetailsWithFacilityOnes(facility));
					confirmationDialog.show();
				} else {
					overrideLocationDetailsWithFacilityOnes(facility);
				}
			}
		});

		ValidationHelper.initEmailValidator(contentBinding.locationContactPersonEmail);
		ValidationHelper.initPhoneNumberValidator(contentBinding.locationContactPersonPhone);
	}

	private void overrideLocationDetailsWithFacilityOnes(Facility facility) {
		contentBinding.locationCity.setValue(facility.getCity());
		contentBinding.locationPostalCode.setValue(facility.getPostalCode());
		contentBinding.locationStreet.setValue(facility.getStreet());
		contentBinding.locationHouseNumber.setValue(facility.getHouseNumber());
		contentBinding.locationAdditionalInformation.setValue(facility.getAdditionalInformation());
		contentBinding.locationAreaType.setValue(facility.getAreaType());
		contentBinding.locationContactPersonFirstName.setValue(facility.getContactPersonFirstName());
		contentBinding.locationContactPersonLastName.setValue(facility.getContactPersonLastName());
		contentBinding.locationContactPersonPhone.setValue(facility.getContactPersonPhone());
		contentBinding.locationContactPersonEmail.setValue(facility.getContactPersonEmail());
		contentBinding.locationLatitude.setDoubleValue(facility.getLatitude());
		contentBinding.locationLongitude.setDoubleValue(facility.getLongitude());
	}

	public void setRequiredFieldsBasedOnCountry() {
		contentBinding.locationCountry.addValueChangedListener(e -> {
			Country country = (Country) e.getValue();
			String serverCountryName = ConfigProvider.getServerCountryName();
			if (serverCountryName == null) {
				setRegionAndDistrictRequired(country == null);
			} else {
				setRegionAndDistrictRequired(country == null || serverCountryName.equalsIgnoreCase(country.getName()));
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

		setFacilityContactPersonFieldsVisible(visible && (contentBinding.locationFacilityType.getValue() != null), clearOnHidden);

		if (!visible && clearOnHidden) {
			contentBinding.facilityTypeGroup.setValue(null);
			contentBinding.locationFacility.setValue(null);
			contentBinding.locationFacilityDetails.setValue(null);
			contentBinding.locationFacilityType.setValue(null);
		}
	}

	private void setFacilityContactPersonFieldsVisible(boolean visible, boolean clearOnHidden) {
		final int visibility = visible ? VISIBLE : GONE;
		contentBinding.locationContactPersonFirstName.setVisibility(visibility);
		contentBinding.locationContactPersonLastName.setVisibility(visibility);
		contentBinding.locationContactPersonPhone.setVisibility(visibility);
		contentBinding.locationContactPersonEmail.setVisibility(visibility);

		if (!visible && clearOnHidden) {
			contentBinding.locationContactPersonFirstName.setValue(null);
			contentBinding.locationContactPersonLastName.setValue(null);
			contentBinding.locationContactPersonPhone.setValue(null);
			contentBinding.locationContactPersonEmail.setValue(null);
		}
	}

	public void updateContinentFieldsVisibility() {
		if (DatabaseHelper.getContinentDao().countOfActive() == 0) {
			contentBinding.locationContinent.setVisibility(GONE);
			contentBinding.locationContinent.setValue(null);
		} else {
			contentBinding.locationContinent.setVisibility(VISIBLE);
		}
		if (DatabaseHelper.getSubcontinentDao().countOfActive() == 0) {
			contentBinding.locationSubcontinent.setVisibility(GONE);
			contentBinding.locationSubcontinent.setValue(null);
		} else {
			contentBinding.locationSubcontinent.setVisibility(VISIBLE);
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
