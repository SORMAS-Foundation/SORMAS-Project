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

package de.symeda.sormas.app.clinicalcourse.edit;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.symptoms.Symptoms;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentClinicalMeasurementsEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class ClinicalMeasurementsEditFragment extends BaseEditFragment<FragmentClinicalMeasurementsEditLayoutBinding, Symptoms, Symptoms> {

	private Symptoms record;

	private List<Item> temperatureList;
	private List<Item> temperatureSourceList;
	private List<Item> bloodPressureList;
	private List<Item> heartRateList;
	private List<Item> respiratoryRateList;
	private List<Item> glasgowComaScaleList;
	private List<Item> weightList;
	private List<Item> heightList;
	private List<Item> midUpperArmCircumferenceList;

	public static ClinicalMeasurementsEditFragment newInstance(Symptoms activityRootData) {
		return newInstance(ClinicalMeasurementsEditFragment.class, null, activityRootData);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_clinical_measurements);
	}

	@Override
	public Symptoms getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		temperatureList = buildTemperatureList();
		temperatureSourceList = DataUtils.getEnumItems(TemperatureSource.class, true);
		bloodPressureList = DataUtils.toItems(SymptomsHelper.getBloodPressureValues());
		heartRateList = DataUtils.toItems(SymptomsHelper.getHeartRateValues());
		respiratoryRateList = DataUtils.toItems(SymptomsHelper.getRespiratoryRateValues());
		glasgowComaScaleList = DataUtils.toItems(SymptomsHelper.getGlasgowComaScaleValues());
		weightList = buildWeightList();
		heightList = DataUtils.toItems(SymptomsHelper.getHeightValues());
		midUpperArmCircumferenceList = buildMidUpperArmCircumferenceList();
	}

	@Override
	public void onLayoutBinding(FragmentClinicalMeasurementsEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentClinicalMeasurementsEditLayoutBinding contentBinding) {
		contentBinding.symptomsTemperature.initializeSpinner(DataUtils.addEmptyItem(temperatureList));
		contentBinding.symptomsTemperature.setSelectionOnOpen(37.0f);
		contentBinding.symptomsTemperatureSource.initializeSpinner(DataUtils.addEmptyItem(temperatureSourceList));
		contentBinding.symptomsBloodPressureSystolic.initializeSpinner(DataUtils.addEmptyItem(bloodPressureList));
		contentBinding.symptomsBloodPressureSystolic.setSelectionOnOpen(120);
		contentBinding.symptomsBloodPressureDiastolic.initializeSpinner(DataUtils.addEmptyItem(bloodPressureList));
		contentBinding.symptomsBloodPressureDiastolic.setSelectionOnOpen(80);
		contentBinding.symptomsHeartRate.initializeSpinner(DataUtils.addEmptyItem(heartRateList));
		contentBinding.symptomsHeartRate.setSelectionOnOpen(80);
		contentBinding.symptomsRespiratoryRate.initializeSpinner(DataUtils.addEmptyItem(respiratoryRateList));
		contentBinding.symptomsRespiratoryRate.setSelectionOnOpen(18);
		contentBinding.symptomsGlasgowComaScale.initializeSpinner(DataUtils.addEmptyItem(glasgowComaScaleList));
		contentBinding.symptomsGlasgowComaScale.setSelectionOnOpen(10);
		contentBinding.symptomsWeight.initializeSpinner(DataUtils.addEmptyItem(weightList));
		contentBinding.symptomsWeight.setSelectionOnOpen(8000);
		contentBinding.symptomsHeight.initializeSpinner(DataUtils.addEmptyItem(heightList));
		contentBinding.symptomsHeight.setSelectionOnOpen(180);
		contentBinding.symptomsMidUpperArmCircumference.initializeSpinner(DataUtils.addEmptyItem(midUpperArmCircumferenceList));
		contentBinding.symptomsMidUpperArmCircumference.setSelectionOnOpen(25);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_clinical_measurements_edit_layout;
	}

	private List<Item> buildTemperatureList() {
		List<Item> temperatures = new ArrayList<>();

		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperatures.add(new Item<>(SymptomsHelper.getTemperatureString(temperatureValue), temperatureValue));
		}

		return temperatures;
	}

	private List<Item> buildWeightList() {
		List<Item> weightValues = new ArrayList<>();

		for (Integer weightValue : SymptomsHelper.getWeightValues()) {
			weightValues.add(new Item<>(SymptomsHelper.getDecimalString(weightValue), weightValue));
		}

		return weightValues;
	}

	private List<Item> buildMidUpperArmCircumferenceList() {
		List<Item> circumferenceValues = new ArrayList<>();

		for (Integer circumferenceValue : SymptomsHelper.getMidUpperArmCircumferenceValues()) {
			circumferenceValues.add(new Item<>(SymptomsHelper.getDecimalString(circumferenceValue), circumferenceValue));
		}

		return circumferenceValues;
	}
}
