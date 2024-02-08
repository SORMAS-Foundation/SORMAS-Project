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

package de.symeda.sormas.app.immunization.read;

import static android.view.View.GONE;

import android.os.Bundle;
import android.view.View;

import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.caze.read.CaseReadActivity;
import de.symeda.sormas.app.databinding.FragmentImmunizationReadLayoutBinding;

public class ImmunizationReadFragment extends BaseReadFragment<FragmentImmunizationReadLayoutBinding, Immunization, Immunization> {

	private Immunization record;

	public static ImmunizationReadFragment newInstance(Immunization activityRootData) {
		ImmunizationReadFragment immunizationReadFragment = newInstanceWithFieldCheckers(
			ImmunizationReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(activityRootData.getDisease())
				.add(new CountryFieldVisibilityChecker(ConfigProvider.getServerLocale())),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		return immunizationReadFragment;
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getActivityRootData();
		DatabaseHelper.getImmunizationDao().initVaccinations(record);
	}

	@Override
	protected void onLayoutBinding(FragmentImmunizationReadLayoutBinding contentBinding) {
		contentBinding.setData(record);
		setUpControlListeners(contentBinding);
	}

	@Override
	public void onAfterLayoutBinding(FragmentImmunizationReadLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(ImmunizationDto.class, contentBinding.mainContent);

		if (record.getHealthFacility() == null || FacilityDto.NONE_FACILITY_UUID.equals(record.getHealthFacility().getUuid())) {
			contentBinding.facilityTypeFieldsLayout.setVisibility(GONE);
		} else {
			final FacilityType facilityType = record.getFacilityType();
			if (facilityType != null) {
				contentBinding.facilityTypeGroup.setValue(facilityType.getFacilityTypeGroup());
			}
		}

		boolean isVaccination = MeansOfImmunization.isVaccination(record.getMeansOfImmunization());
		contentBinding.immunizationVaccinationLayout.setVisibility(isVaccination ? View.VISIBLE : View.GONE);

		boolean isRecovery = MeansOfImmunization.isRecovery(record.getMeansOfImmunization());
		contentBinding.immunizationRecoveryLayout.setVisibility(isRecovery ? View.VISIBLE : View.GONE);

		if (record.getRelatedCase() == null) {
			contentBinding.openLinkedCase.setVisibility(View.GONE);
		}
		contentBinding.immunizationReportingUser.setPseudonymized(record.isPseudonymized());
	}

	private void setUpControlListeners(FragmentImmunizationReadLayoutBinding contentBinding) {
		contentBinding.openLinkedCase.setOnClickListener(v -> CaseReadActivity.startActivity(getActivity(), record.getRelatedCase().getUuid(), true));
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_immunization_read_layout;
	}

	@Override
	public Immunization getPrimaryData() {
		return record;
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_immunization_information);
	}
}
