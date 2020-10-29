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

import java.util.List;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.Vaccination;
import de.symeda.sormas.api.epidata.AnimalCondition;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.epidata.WaterSource;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.databinding.FragmentEditEpidLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public class EpidemiologicalDataEditFragment extends BaseEditFragment<FragmentEditEpidLayoutBinding, EpiData, PseudonymizableAdo> {

	public static final String TAG = EpidemiologicalDataEditFragment.class.getSimpleName();

	private EpiData record;
	private Disease disease;

	private List<Item> drinkingWaterSourceList;
	private List<Item> animalConditionList;

	// Static methods

	public static EpidemiologicalDataEditFragment newInstance(PseudonymizableAdo activityRootData) {
		return newInstanceWithFieldCheckers(
			EpidemiologicalDataEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	// Instance methods

	private void setUpControlListeners(final FragmentEditEpidLayoutBinding contentBinding) {

	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_epidemiological_data);
	}

	@Override
	public EpiData getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		final AbstractDomainObject abstractDomainObject = getActivityRootData();
		disease = getDiseaseOfCaseOrContact(abstractDomainObject);
		record = getEpiDataOfCaseOrContact(abstractDomainObject);
		drinkingWaterSourceList = DataUtils.getEnumItems(WaterSource.class, true);
		animalConditionList = DataUtils.getEnumItems(AnimalCondition.class, true);
	}

	@Override
	public void onLayoutBinding(final FragmentEditEpidLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setWaterSourceClass(WaterSource.class);
		contentBinding.setVaccinationClass(Vaccination.class);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEditEpidLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EpiDataDto.class, contentBinding.mainContent);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_edit_epid_layout;
	}

	@Override
	public boolean isShowSaveAction() {
		return true;
	}

	@Override
	public boolean isShowNewAction() {
		return false;
	}

	private void setFieldAccesses(Class<?> dtoClass, View view) {
		FieldVisibilityAndAccessHelper
			.setFieldVisibilitiesAndAccesses(dtoClass, (ViewGroup) view, new FieldVisibilityCheckers(), getFieldAccessCheckers());

	}
}
