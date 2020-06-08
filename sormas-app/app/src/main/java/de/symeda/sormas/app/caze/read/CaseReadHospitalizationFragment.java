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

package de.symeda.sormas.app.caze.read;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.databinding.ObservableArrayList;

import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.databinding.FragmentCaseReadHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class CaseReadHospitalizationFragment extends BaseReadFragment<FragmentCaseReadHospitalizationLayoutBinding, Hospitalization, Case> {

	public static final String TAG = CaseReadHospitalizationFragment.class.getSimpleName();

	private Case caze;
	private Hospitalization record;

	// Static methods

	public static CaseReadHospitalizationFragment newInstance(Case activityRootData) {
		return newInstance(CaseReadHospitalizationFragment.class, null, activityRootData);
	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		caze = getActivityRootData();
		record = caze.getHospitalization();
	}

	@Override
	public void onLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
		ObservableArrayList<PreviousHospitalization> previousHospitalizations = new ObservableArrayList<>();
		previousHospitalizations.addAll(record.getPreviousHospitalizations());

		contentBinding.setData(record);
		contentBinding.setCaze(caze);
		contentBinding.setPreviousHospitalizationList(previousHospitalizations);
	}

	@Override
	public void onAfterLayoutBinding(FragmentCaseReadHospitalizationLayoutBinding contentBinding) {
		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

		// Previous hospitalizations list
		if (contentBinding.getData().getPreviousHospitalizations().isEmpty()) {
			contentBinding.listPreviousHospitalizationsLayout.setVisibility(GONE);
		}
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_hospitalization);
	}

	@Override
	public Hospitalization getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_case_read_hospitalization_layout;
	}
}
