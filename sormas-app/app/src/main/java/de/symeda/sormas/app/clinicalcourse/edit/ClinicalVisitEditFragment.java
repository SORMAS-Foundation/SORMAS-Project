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

import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.clinicalcourse.ClinicalVisit;
import de.symeda.sormas.app.core.FieldHelper;
import de.symeda.sormas.app.databinding.FragmentClinicalVisitEditLayoutBinding;

public class ClinicalVisitEditFragment extends BaseEditFragment<FragmentClinicalVisitEditLayoutBinding, ClinicalVisit, ClinicalVisit> {

	private ClinicalVisit record;

	public static ClinicalVisitEditFragment newInstance(ClinicalVisit activityRootData) {
		return newInstanceWithFieldCheckers(
			ClinicalVisitEditFragment.class,
			null,
			activityRootData,
			null,
			FieldAccessCheckers.withCheckers(FieldHelper.createSensitiveDataFieldAccessChecker(!activityRootData.isPseudonymized())));
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_clinical_visit_information);
	}

	@Override
	public ClinicalVisit getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();
	}

	@Override
	public void onLayoutBinding(FragmentClinicalVisitEditLayoutBinding contentBinding) {
		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentClinicalVisitEditLayoutBinding contentBinding) {
		contentBinding.clinicalVisitVisitDateTime.initializeDateTimeField(getFragmentManager());

		setFieldVisibilitiesAndAccesses(ClinicalVisitDto.class, contentBinding.mainContent);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_clinical_visit_edit_layout;
	}
}
