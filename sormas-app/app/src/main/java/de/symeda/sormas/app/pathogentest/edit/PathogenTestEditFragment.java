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

package de.symeda.sormas.app.pathogentest.edit;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SamplePurpose;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.sample.PathogenTest;
import de.symeda.sormas.app.backend.sample.Sample;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentPathogenTestEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class PathogenTestEditFragment extends BaseEditFragment<FragmentPathogenTestEditLayoutBinding, PathogenTest, PathogenTest> {

	private PathogenTest record;
	private Sample sample;

	// Enum lists

	private List<Facility> labList;
	private List<Item> testTypeList;
	private List<Item> pcrTestSpecificationList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
	private List<Item> testResultList;

	// Instance methods

	public static PathogenTestEditFragment newInstance(PathogenTest activityRootData) {
		return newInstanceWithFieldCheckers(
			PathogenTestEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.heading_pathogen_test_edit);
	}

	@Override
	public PathogenTest getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
		sample = record.getSample();
		testTypeList = DataUtils.getEnumItems(PathogenTestType.class, true);
		pcrTestSpecificationList = DataUtils.getEnumItems(PCRTestSpecification.class, true);

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getTestedDisease() != null && !diseases.contains(record.getTestedDisease())) {
			diseaseList.add(DataUtils.toItem(record.getTestedDisease()));
		}
		List<DiseaseVariant> diseaseVariants = record.getTestedDisease() != null
			? DatabaseHelper.getCustomizableEnumValueDao().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, record.getTestedDisease())
			: new ArrayList<>();
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		if (record.getTestedDiseaseVariant() != null && !diseaseVariants.contains(record.getTestedDiseaseVariant())) {
			diseaseVariantList.add(DataUtils.toItem(record.getTestedDiseaseVariant()));
		}

		testResultList = DataUtils.toItems(
			Arrays.stream(PathogenTestResultType.values()).filter(type -> type != PathogenTestResultType.NOT_DONE).collect(Collectors.toList()),
			true);
		labList = DatabaseHelper.getFacilityDao().getActiveLaboratories(true);
	}

	@Override
	public void onLayoutBinding(FragmentPathogenTestEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentPathogenTestEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(PathogenTestDto.class, contentBinding.mainContent);

		// Initialize ControlSpinnerFields
		contentBinding.pathogenTestTestType.initializeSpinner(testTypeList, new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				PathogenTestType currentTestType = (PathogenTestType) field.getValue();
				if ((PathogenTestType.PCR_RT_PCR == currentTestType
					&& PathogenTestResultType.POSITIVE == contentBinding.pathogenTestTestResult.getValue())
					|| PathogenTestType.CQ_VALUE_DETECTION == currentTestType) {
					contentBinding.pathogenTestCqValue.setVisibility(View.VISIBLE);
				} else {
					contentBinding.pathogenTestCqValue.hideField(true);
				}

				if (PathogenTestType.PCR_RT_PCR == currentTestType && Disease.CORONAVIRUS == record.getTestedDisease()) {
					getContentBinding().pathogenTestPcrTestSpecification.setVisibility(View.VISIBLE);
				} else {
					getContentBinding().pathogenTestPcrTestSpecification.hideField(false);
				}
			}
		});

		contentBinding.pathogenTestPcrTestSpecification.initializeSpinner(pcrTestSpecificationList);

		contentBinding.pathogenTestTestedDiseaseVariant.initializeSpinner(diseaseVariantList);

		contentBinding.pathogenTestTestedDisease.initializeSpinner(diseaseList, new ValueChangeListener() {

			final Disease currentDisease = record.getTestedDisease();

			@Override
			public void onChange(ControlPropertyField field) {

				if (PathogenTestType.PCR_RT_PCR == record.getTestType() && Disease.CORONAVIRUS == field.getValue()) {
					getContentBinding().pathogenTestPcrTestSpecification.setVisibility(View.VISIBLE);
				} else {
					getContentBinding().pathogenTestPcrTestSpecification.hideField(false);
				}

				if (this.currentDisease == null || contentBinding.pathogenTestTestedDisease.getValue() != currentDisease) {
					updateDiseaseVariantsField(contentBinding);
				}
			}
		});

		contentBinding.pathogenTestTestResult.initializeSpinner(testResultList, new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				PathogenTestResultType currentPathogenTestResult = (PathogenTestResultType) field.getValue();
				if ((PathogenTestType.PCR_RT_PCR == contentBinding.pathogenTestTestType.getValue()
					&& PathogenTestResultType.POSITIVE == currentPathogenTestResult)
					|| PathogenTestType.CQ_VALUE_DETECTION == contentBinding.pathogenTestTestType.getValue()) {
					contentBinding.pathogenTestCqValue.setVisibility(View.VISIBLE);
				} else {
					contentBinding.pathogenTestCqValue.hideField(true);
				}
			}
		});

		contentBinding.pathogenTestLab.initializeSpinner(DataUtils.toItems(labList), new ValueChangeListener() {

			@Override
			public void onChange(ControlPropertyField field) {
				Facility laboratory = (Facility) field.getValue();
				if (laboratory != null && laboratory.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID)) {
					contentBinding.pathogenTestLabDetails.setVisibility(View.VISIBLE);
				} else {
					contentBinding.pathogenTestLabDetails.hideField(true);
				}
			}
		});

//        // Initialize ControlDateFields
		contentBinding.pathogenTestReportDate.initializeDateField(getFragmentManager());
		contentBinding.pathogenTestTestDateTime.initializeDateTimeField(getFragmentManager());

		if (sample.getSamplePurpose() == SamplePurpose.INTERNAL) {
			contentBinding.pathogenTestLab.setRequired(false);
		}
	}

	private void updateDiseaseVariantsField(FragmentPathogenTestEditLayoutBinding contentBinding) {
		List<DiseaseVariant> diseaseVariants = contentBinding.pathogenTestTestedDisease.getValue() != null
			? DatabaseHelper.getCustomizableEnumValueDao()
				.getEnumValues(CustomizableEnumType.DISEASE_VARIANT, (Disease) contentBinding.pathogenTestTestedDisease.getValue())
			: new ArrayList<>();
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.pathogenTestTestedDiseaseVariant.setSpinnerData(diseaseVariantList);
		contentBinding.pathogenTestTestedDiseaseVariant.setValue(null);
		contentBinding.pathogenTestTestedDiseaseVariant.setVisibility(diseaseVariants.isEmpty() ? GONE : VISIBLE);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_pathogen_test_edit_layout;
	}
}
