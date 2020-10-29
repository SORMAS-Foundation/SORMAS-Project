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

package de.symeda.sormas.app.epidata;

import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getEpiDataOfCaseOrContact;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.databinding.FragmentReadEpidLayoutBinding;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public class EpidemiologicalDataReadFragment extends BaseReadFragment<FragmentReadEpidLayoutBinding, EpiData, AbstractDomainObject> {

	public static final String TAG = EpidemiologicalDataReadFragment.class.getSimpleName();

	private EpiData record;

	// Static methods

	public static EpidemiologicalDataReadFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			EpidemiologicalDataReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	public static EpidemiologicalDataReadFragment newInstance(Contact activityRootData) {
		return newInstanceWithFieldCheckers(
			EpidemiologicalDataReadFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	// Instance methods

	private void setUpControlListeners() {

	}

	// Overrides

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getEpiDataOfCaseOrContact(getActivityRootData());
	}

	@Override
	public void onLayoutBinding(FragmentReadEpidLayoutBinding contentBinding) {
		setUpControlListeners();

		contentBinding.setData(record);
	}

	@Override
	public void onAfterLayoutBinding(FragmentReadEpidLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EpiDataDto.class, contentBinding.mainContent);
	}

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_case_epidemiological_data);
	}

	@Override
	public EpiData getPrimaryData() {
		return record;
	}

	@Override
	public int getReadLayout() {
		return R.layout.fragment_read_epid_layout;
	}

	private void setFieldAccesses(Class<?> dtoClass, View view) {
		FieldVisibilityAndAccessHelper
			.setFieldVisibilitiesAndAccesses(dtoClass, (ViewGroup) view, new FieldVisibilityCheckers(), getFieldAccessCheckers());
	}
}
