/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.immunization.vaccination;

import java.util.List;

import android.view.View;

import de.symeda.sormas.api.caze.Trimester;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.caze.VaccineManufacturer;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.vaccination.Vaccination;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.databinding.FragmentVaccinationEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;

public class VaccinationEditFragment extends BaseEditFragment<FragmentVaccinationEditLayoutBinding, Vaccination, Vaccination> {

	public static final String TAG = VaccinationEditFragment.class.getSimpleName();

	private Vaccination record;

	// Enum lists

	private List<Item> vaccineManufacturerList;
	private List<Item> vaccineList;
	private List<Item> vaccineInfoSourceList;

	public static VaccinationEditFragment newInstance(Vaccination activityRootData) {
		return newInstanceWithFieldCheckers(
			VaccinationEditFragment.class,
			null,
			activityRootData,
			null,
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	@Override
	public Vaccination getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		vaccineManufacturerList = DataUtils.getEnumItems(VaccineManufacturer.class, true, getFieldVisibilityCheckers());
		vaccineList = DataUtils.getEnumItems(Vaccine.class, true, getFieldVisibilityCheckers());
		vaccineInfoSourceList = DataUtils.getEnumItems(VaccinationInfoSource.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentVaccinationEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
		contentBinding.setTrimesterClass(Trimester.class);
		if (ConfigProvider.hasUserRight(UserRight.IMMUNIZATION_VIEW)
			&& !DatabaseHelper.getFeatureConfigurationDao().isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			contentBinding.vaccinationPregnant.setVisibility(View.VISIBLE);
		} else {
			contentBinding.vaccinationPregnant.setVisibility(View.GONE);
		}

		contentBinding.vaccinationVaccineName.addValueChangedListener(new ValueChangeListener() {

			private Vaccine currentVaccine = record.getVaccineName();

			@Override
			public void onChange(ControlPropertyField e) {
				Vaccine vaccine = (Vaccine) e.getValue();

				if (currentVaccine != vaccine) {
					contentBinding.vaccinationVaccineManufacturer.setValue(vaccine != null ? vaccine.getManufacturer() : null);
					currentVaccine = vaccine;
				}
			}
		});
	}

	@Override
	public void onAfterLayoutBinding(FragmentVaccinationEditLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(VaccinationDto.class, contentBinding.mainContent);

		// Initialize fields
		contentBinding.vaccinationVaccineName.initializeSpinner(vaccineList);
		contentBinding.vaccinationVaccineManufacturer.initializeSpinner(vaccineManufacturerList);
		contentBinding.vaccinationVaccinationInfoSource.initializeSpinner(vaccineInfoSourceList);
		contentBinding.immunizationReportDate.initializeDateField(getFragmentManager());
		contentBinding.vaccinationVaccinationDate.initializeDateField(getFragmentManager());
		contentBinding.immunizationReportingUser.setPseudonymized(record.isPseudonymized());
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_vaccination_edit_layout;
	}

	@Override
	protected String getSubHeadingTitle() {
		return record.getId() != null
			? getResources().getString(R.string.heading_vaccination_edit)
			: getResources().getString(R.string.heading_vaccination_new);
	}
}
