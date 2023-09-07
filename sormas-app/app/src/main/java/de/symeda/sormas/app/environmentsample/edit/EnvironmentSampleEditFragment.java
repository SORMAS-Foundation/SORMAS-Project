/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.environmentsample.edit;

import static android.view.View.GONE;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.view.View;

import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleDto;
import de.symeda.sormas.api.environment.environmentsample.EnvironmentSampleMaterial;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.environment.environmentsample.WeatherCondition;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.environment.environmentsample.EnvironmentSample;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.databinding.FragmentEnvironmentSampleEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class EnvironmentSampleEditFragment
	extends BaseEditFragment<FragmentEnvironmentSampleEditLayoutBinding, EnvironmentSample, EnvironmentSample> {

	private EnvironmentSample record;

	// Enum lists

	private List<Item> sampleMaterialList;
	private List<Facility> laboratoryList;
	private List<String> requestedPathogenTests = new ArrayList<>();

	public static EnvironmentSampleEditFragment newInstance(EnvironmentSample activityRootData) {
		return newInstanceWithFieldCheckers(
			EnvironmentSampleEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()),
			UserRight.ENVIRONMENT_SAMPLE_EDIT);
	}

	private void setUpControlListeners(FragmentEnvironmentSampleEditLayoutBinding contentBinding) {
		getContentBinding().environmentSampleLocation.setOnClickListener(v -> openAddressPopup(contentBinding));
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_environment_sample_information);
	}

	@Override
	public EnvironmentSample getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {

		record = getActivityRootData();

		sampleMaterialList = DataUtils.getEnumItems(EnvironmentSampleMaterial.class, true, getFieldVisibilityCheckers());
		laboratoryList = DatabaseHelper.getFacilityDao().getActiveLaboratories(true);

		requestedPathogenTests.clear();
		for (Pathogen pathogen : record.getRequestedPathogenTests()) {
			requestedPathogenTests.add(pathogen.getCaption());
		}
	}

	@Override
	public void onLayoutBinding(FragmentEnvironmentSampleEditLayoutBinding contentBinding) {

		setUpControlListeners(contentBinding);
		contentBinding.setData(record);
		EnvironmentSampleValidator.initializeEnvironmentSampleValidation(contentBinding);
		contentBinding.setWeatherConditionClass(WeatherCondition.class);
		List<CustomizableEnum> pathogens = DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.PATHOGEN, null);
		// Remove default "OTHER" pathogen because it's covered by a separate field
		pathogens.remove(DatabaseHelper.getCustomizableEnumValueDao().getEnumValue(CustomizableEnumType.PATHOGEN, "OTHER"));
		contentBinding.environmentSampleRequestedPathogenTests.setItems(pathogens);
	}

	@Override
	public void onAfterLayoutBinding(final FragmentEnvironmentSampleEditLayoutBinding contentBinding) {

		super.onAfterLayoutBinding(contentBinding);
		setFieldVisibilitiesAndAccesses(EnvironmentSampleDto.class, contentBinding.mainContent);

		// Initialize ControlSpinnerFields
		contentBinding.environmentSampleSampleMaterial.initializeSpinner(sampleMaterialList);
		contentBinding.environmentSampleLaboratory.initializeSpinner(DataUtils.toItems(laboratoryList), field -> {
			Facility laboratory = (Facility) field.getValue();
			if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
				contentBinding.environmentSampleLaboratoryDetails.setVisibility(View.VISIBLE);
			} else {
				contentBinding.environmentSampleLaboratoryDetails.hideField(true);
			}
		});

		// Initialize ControlDateFields and ControlDateTimeFields
		contentBinding.environmentSampleSampleDateTime.initializeDateTimeField(getFragmentManager());
		contentBinding.environmentSampleDispatchDate.initializeDateField(getFragmentManager());

		// Disable fields the user doesn't have access to - this involves almost all fields when
		// the user is not the one that originally reported the sample
		if (!ConfigProvider.getUser().equals(record.getReportingUser()) || !ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT_DISPATCH)) {
			contentBinding.environmentSampleSampleDateTime.setEnabled(false);
			contentBinding.environmentSampleFieldSampleId.setEnabled(false);
			contentBinding.environmentSampleSampleMaterial.setEnabled(false);
			contentBinding.environmentSampleOtherSampleMaterial.setEnabled(false);
			contentBinding.environmentSampleSampleVolume.setEnabled(false);
			contentBinding.environmentSampleTurbidity.setEnabled(false);
			contentBinding.environmentSampleSampleTemperature.setEnabled(false);
			contentBinding.environmentSampleChlorineResiduals.setEnabled(false);
			contentBinding.environmentSamplePhValue.setEnabled(false);
			contentBinding.environmentSampleWeatherConditions.setEnabled(false);
			contentBinding.environmentSampleHeavyRain.setEnabled(false);
			contentBinding.environmentSampleLocation.setEnabled(false);
			contentBinding.environmentSampleLaboratory.setEnabled(false);
			contentBinding.environmentSampleLaboratoryDetails.setEnabled(false);
			contentBinding.environmentSampleRequestedPathogenTests.setVisibility(GONE);
			contentBinding.environmentSampleDispatched.setEnabled(false);
			contentBinding.environmentSampleDispatchDate.setEnabled(false);
			contentBinding.environmentSampleDispatchDetails.setEnabled(false);

			if (!requestedPathogenTests.isEmpty()) {
				contentBinding.environmentSampleRequestedPathogenTestsTags.setTags(requestedPathogenTests);
				if (StringUtils.isEmpty(record.getOtherRequestedPathogenTests())) {
					contentBinding.environmentSampleOtherRequestedPathogenTests.setVisibility(GONE);
				}
			} else {
				contentBinding.environmentSampleRequestedPathogenTestsTags.setVisibility(GONE);
				contentBinding.environmentSampleOtherRequestedPathogenTests.setVisibility(GONE);
			}
		} else {
			contentBinding.environmentSampleRequestedPathogenTestsTags.setVisibility(GONE);
		}
	}

	private void openAddressPopup(FragmentEnvironmentSampleEditLayoutBinding contentBinding) {
		final Location location = record.getLocation();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, getFieldAccessCheckers());
		locationDialog.show();

		locationDialog.setPositiveCallback(() -> {
			contentBinding.environmentSampleLocation.setValue(locationClone);
			record.setLocation(locationClone);
		});
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_environment_sample_edit_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return ConfigProvider.hasUserRight(UserRight.ENVIRONMENT_SAMPLE_EDIT);
	}
}
