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

package de.symeda.sormas.app.caze.edit;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import java.util.List;

import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.hospitalization.HospitalizationReasonType;
import de.symeda.sormas.api.hospitalization.PreviousHospitalizationDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.hospitalization.Hospitalization;
import de.symeda.sormas.app.backend.hospitalization.PreviousHospitalization;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentCaseEditHospitalizationLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;
import de.symeda.sormas.app.util.InfrastructureHelper;

public class CaseEditHospitalizationFragment extends BaseEditFragment<FragmentCaseEditHospitalizationLayoutBinding, Hospitalization, Case> {

	private Hospitalization record;
	private Case caze;

	private IEntryItemOnClickListener onPrevHosItemClickListener;

	// Static methods

	public static CaseEditHospitalizationFragment newInstance(Case activityRootData) {
		return newInstanceWithFieldCheckers(
			CaseEditHospitalizationFragment.class,
			null,
			activityRootData,
			new FieldVisibilityCheckers(),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	// Instance methods

	private void setUpControlListeners() {
		onPrevHosItemClickListener = (v, item) -> {
			final PreviousHospitalization previousHospitalization = (PreviousHospitalization) item;
			final PreviousHospitalization previousHospitalizationClone = (PreviousHospitalization) previousHospitalization.clone();
			final PreviousHospitalizationDialog dialog =
				new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), previousHospitalizationClone, false);

			dialog.setPositiveCallback(() -> {
				record.getPreviousHospitalizations()
					.set(record.getPreviousHospitalizations().indexOf(previousHospitalization), previousHospitalizationClone);
				updatePreviousHospitalizations();
			});

			dialog.setDeleteCallback(() -> removePreviousHospitalization(previousHospitalization));

			dialog.show();
		};

		getContentBinding().btnAddPrevHosp.setOnClickListener(v -> {
			final PreviousHospitalization previousHospitalization = DatabaseHelper.getPreviousHospitalizationDao().build();
			final PreviousHospitalizationDialog dialog =
				new PreviousHospitalizationDialog(CaseEditActivity.getActiveActivity(), previousHospitalization, true);

			dialog.setPositiveCallback(() -> addPreviousHospitalization(previousHospitalization));

			dialog.setDeleteCallback(() -> removePreviousHospitalization(previousHospitalization));

			dialog.show();
		});
	}

	private ObservableArrayList<PreviousHospitalization> getPreviousHospitalizations() {
		ObservableArrayList<PreviousHospitalization> newPreHospitalizations = new ObservableArrayList<>();
		newPreHospitalizations.addAll(record.getPreviousHospitalizations());
		return newPreHospitalizations;
	}

	private void clearPreviousHospitalizations() {
		record.getPreviousHospitalizations().clear();
		updatePreviousHospitalizations();
	}

	private void removePreviousHospitalization(PreviousHospitalization item) {
		record.getPreviousHospitalizations().remove(item);
		updatePreviousHospitalizations();
	}

	private void updatePreviousHospitalizations() {
		getContentBinding().setPreviousHospitalizationList(getPreviousHospitalizations());

		verifyPrevHospitalizationStatus();
	}

	private void addPreviousHospitalization(PreviousHospitalization item) {
		record.getPreviousHospitalizations().add(0, item);
		updatePreviousHospitalizations();
	}

	private void verifyPrevHospitalizationStatus() {
		YesNoUnknown hospitalizedPreviously = record.getHospitalizedPreviously();
		if (hospitalizedPreviously == YesNoUnknown.YES && getPreviousHospitalizations().size() <= 0) {
			getContentBinding().caseHospitalizationHospitalizedPreviously.enableWarningState(R.string.validation_soft_add_list_entry);
		} else {
			getContentBinding().caseHospitalizationHospitalizedPreviously.disableWarningState();
		}

		getContentBinding().caseHospitalizationHospitalizedPreviously.setEnabled(getPreviousHospitalizations().size() == 0);
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		Resources r = getResources();
		return r.getString(R.string.caption_case_hospitalization);
	}

	@Override
	public Hospitalization getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		caze = getActivityRootData();
		record = caze.getHospitalization();
	}

	@Override
	public void onLayoutBinding(final FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		setUpControlListeners();

		CaseValidator.initializeHospitalizationValidation(contentBinding, caze);

		List<Item> hospitalizationReasons = DataUtils.getEnumItems(HospitalizationReasonType.class, true);

		contentBinding.setData(record);
		contentBinding.setCaze(caze);
		contentBinding.setPreviousHospitalizationList(getPreviousHospitalizations());
		contentBinding.setPrevHosItemClickCallback(onPrevHosItemClickListener);
		getContentBinding().setPreviousHospitalizationBindCallback(this::setFieldVisibilitiesAndAccesses);
		contentBinding.caseHospitalizationHospitalizationReason.initializeSpinner(hospitalizationReasons);

		contentBinding.caseHospitalizationHospitalizedPreviously.addValueChangedListener(field -> {
			YesNoUnknown value = (YesNoUnknown) field.getValue();
			contentBinding.prevHospitalizationsLayout.setVisibility(value == YesNoUnknown.YES ? View.VISIBLE : View.GONE);
			if (value != YesNoUnknown.YES) {
				clearPreviousHospitalizations();
			}

			verifyPrevHospitalizationStatus();
		});
	}

	@Override
	protected void onAfterLayoutBinding(FragmentCaseEditHospitalizationLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(HospitalizationDto.class, contentBinding.mainContent);

		InfrastructureHelper
			.initializeHealthFacilityDetailsFieldVisibility(contentBinding.caseDataHealthFacility, contentBinding.caseDataHealthFacilityDetails);

		// Initialize ControlDateFields
		contentBinding.caseHospitalizationAdmissionDate.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationDischargeDate.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIntensiveCareUnitStart.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIntensiveCareUnitEnd.initializeDateField(getFragmentManager());
		contentBinding.caseHospitalizationIsolationDate.initializeDateField(getFragmentManager());

		verifyPrevHospitalizationStatus();
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_case_edit_hospitalization_layout;
	}

	private void setFieldVisibilitiesAndAccesses(View view) {
		FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
			PreviousHospitalizationDto.class,
			(ViewGroup) view,
			new FieldVisibilityCheckers(),
			getFieldAccessCheckers());

	}
}
