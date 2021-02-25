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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getEpiDataOfCaseOrContact;

import android.content.res.Resources;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.caze.edit.CaseEditActivity;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.FragmentEditEpidLayoutBinding;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public class EpidemiologicalDataEditFragment extends BaseEditFragment<FragmentEditEpidLayoutBinding, EpiData, PseudonymizableAdo> {

	public static final String TAG = EpidemiologicalDataEditFragment.class.getSimpleName();

	private EpiData record;
	private IEntryItemOnClickListener onExposureItemClickListener;
	private IEntryItemOnClickListener onActivityAsCaseItemClickListener;

	// Static methods

	public static EpidemiologicalDataEditFragment newInstance(PseudonymizableAdo activityRootData) {
		return newInstanceWithFieldCheckers(
			EpidemiologicalDataEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(activityRootData)),
			UiFieldAccessCheckers.forSensitiveData(activityRootData.isPseudonymized()));
	}

	private void setUpControlListeners(final FragmentEditEpidLayoutBinding contentBinding) {
		onExposureItemClickListener = (v, item) -> {
			final Exposure exposure = (Exposure) item;
			final Exposure exposureClone = (Exposure) exposure.clone();
			final ExposureDialog dialog = new ExposureDialog(CaseEditActivity.getActiveActivity(), exposureClone, getActivityRootData(), false);

			dialog.setPositiveCallback(() -> {
				record.getExposures().set(record.getExposures().indexOf(exposure), exposureClone);
				updateExposures();
			});

			dialog.setDeleteCallback(() -> {
				removeExposure(exposure);
				dialog.dismiss();
			});

			dialog.show();
		};

		contentBinding.btnAddExposure.setOnClickListener(v -> {
			final Exposure exposure = DatabaseHelper.getExposureDao().build();
			final ExposureDialog dialog = new ExposureDialog(CaseEditActivity.getActiveActivity(), exposure, getActivityRootData(), true);

			dialog.setPositiveCallback(() -> addExposure(exposure));
			dialog.show();
		});

		contentBinding.epiDataExposureDetailsKnown.addValueChangedListener(field -> {
			YesNoUnknown value = (YesNoUnknown) field.getValue();
			contentBinding.exposuresLayout.setVisibility(value == YesNoUnknown.YES ? VISIBLE : GONE);
			if (value != YesNoUnknown.YES) {
				clearExposures();
			}

			getContentBinding().epiDataExposureDetailsKnown.setEnabled(getExposureList().isEmpty());
		});

		onActivityAsCaseItemClickListener = (v, item) -> {
			final ActivityAsCase activityAsCase = (ActivityAsCase) item;
			final ActivityAsCase activityAsCaseClone = (ActivityAsCase) activityAsCase.clone();
			final ActivityAsCaseDialog dialog =
				new ActivityAsCaseDialog(CaseEditActivity.getActiveActivity(), activityAsCaseClone, getActivityRootData(), false);

			dialog.setPositiveCallback(() -> {
				record.getActivitiesAsCase().set(record.getActivitiesAsCase().indexOf(activityAsCase), activityAsCaseClone);
				updateActivitiesAsCase();
			});

			dialog.setDeleteCallback(() -> {
				removeActivityAsCase(activityAsCase);
				dialog.dismiss();
			});

			dialog.show();
		};

		contentBinding.btnAddActivityascase.setOnClickListener(v -> {
			final ActivityAsCase activityAsCase = DatabaseHelper.getActivityAsCaseDao().build();
			final ActivityAsCaseDialog dialog =
				new ActivityAsCaseDialog(CaseEditActivity.getActiveActivity(), activityAsCase, getActivityRootData(), true);

			dialog.setPositiveCallback(() -> addActivityAsCase(activityAsCase));
			dialog.show();
		});

		contentBinding.epiDataActivityAsCaseDetailsKnown.addValueChangedListener(field -> {
			YesNoUnknown value = (YesNoUnknown) field.getValue();
			contentBinding.activityascaseLayout.setVisibility(value == YesNoUnknown.YES ? VISIBLE : GONE);
			if (value != YesNoUnknown.YES) {
				clearActivitiesAsCase();
			}

			getContentBinding().epiDataActivityAsCaseDetailsKnown.setEnabled(getActivityAsCaseList().isEmpty());
		});

	}

	private ObservableArrayList<Exposure> getExposureList() {
		ObservableArrayList<Exposure> exposures = new ObservableArrayList<>();
		exposures.addAll(record.getExposures());
		return exposures;
	}

	private void clearExposures() {
		record.getExposures().clear();
		updateExposures();
	}

	private void removeExposure(Exposure exposure) {
		record.getExposures().remove(exposure);
		updateExposures();
	}

	private void updateExposures() {
		getContentBinding().setExposureList(getExposureList());
		getContentBinding().epiDataExposureDetailsKnown.setEnabled(getExposureList().isEmpty());
		updateAddExposuresButtonVisibility();
	}

	private void addExposure(Exposure exposure) {
		record.getExposures().add(0, exposure);
		updateExposures();
	}

	private ObservableArrayList<ActivityAsCase> getActivityAsCaseList() {
		ObservableArrayList<ActivityAsCase> activitiesAsCase = new ObservableArrayList<>();
		activitiesAsCase.addAll(record.getActivitiesAsCase());
		return activitiesAsCase;
	}

	private void clearActivitiesAsCase() {
		record.getActivitiesAsCase().clear();
		updateActivitiesAsCase();
	}

	private void removeActivityAsCase(ActivityAsCase activityAsCase) {
		record.getActivitiesAsCase().remove(activityAsCase);
		updateActivitiesAsCase();
	}

	private void updateActivitiesAsCase() {
		getContentBinding().setActivityAsCaseList(getActivityAsCaseList());
		getContentBinding().epiDataActivityAsCaseDetailsKnown.setEnabled(getActivityAsCaseList().isEmpty());
		updateAddActivitiesAsCaseButtonVisibility();
	}

	private void addActivityAsCase(ActivityAsCase activityAsCase) {
		record.getActivitiesAsCase().add(0, activityAsCase);
		updateActivitiesAsCase();
	}

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
		record = getEpiDataOfCaseOrContact(getActivityRootData());
	}

	@Override
	public void onLayoutBinding(final FragmentEditEpidLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setExposureList(getExposureList());
		contentBinding.setExposureItemClickCallback(onExposureItemClickListener);
		contentBinding.setExposureListBindCallback(
			v -> FieldVisibilityAndAccessHelper
				.setFieldVisibilitiesAndAccesses(ExposureDto.class, (ViewGroup) v, new FieldVisibilityCheckers(), getFieldAccessCheckers()));

		contentBinding.setActivityAsCaseList(getActivityAsCaseList());
		contentBinding.setActivityAsCaseItemClickCallback(onActivityAsCaseItemClickListener);
		contentBinding.setActivityAsCaseListBindCallback(
			v -> FieldVisibilityAndAccessHelper
				.setFieldVisibilitiesAndAccesses(ActivityAsCaseDto.class, (ViewGroup) v, new FieldVisibilityCheckers(), getFieldAccessCheckers()));
	}

	@Override
	public void onAfterLayoutBinding(FragmentEditEpidLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EpiDataDto.class, contentBinding.mainContent);
		contentBinding.epiDataExposureDetailsKnown.setEnabled(getExposureList().isEmpty());
		contentBinding.epiDataActivityAsCaseDetailsKnown.setEnabled(getActivityAsCaseList().isEmpty());

		if (!(getActivityRootData() instanceof Case)) {
			contentBinding.epiDataContactWithSourceCaseKnown.setVisibility(GONE);
			contentBinding.sourceContactsHeading.setVisibility(GONE);
			contentBinding.exposureInvestigationInfo.setText(Html.fromHtml(I18nProperties.getString(Strings.infoExposureInvestigationContacts)));
			contentBinding.activityascaseInvestigationInfo.setText(Html.fromHtml(I18nProperties.getString(Strings.infoActivityAsCaseInvestigation)));
			contentBinding.activityascaseLayout.setVisibility(GONE);
			contentBinding.epiDataActivityAsCaseDetailsKnown.setVisibility(GONE);
		}
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

	private void updateAddExposuresButtonVisibility() {
		if (getActivityRootData() instanceof Contact && !getExposureList().isEmpty()) {
			getContentBinding().btnAddExposure.setVisibility(GONE);
		} else {
			getContentBinding().btnAddExposure.setVisibility(VISIBLE);
		}
	}

	private void updateAddActivitiesAsCaseButtonVisibility() {
		if (getActivityRootData() instanceof Contact) {
			getContentBinding().btnAddActivityascase.setVisibility(View.GONE);
		} else if (getActivityRootData() instanceof Case && !getActivityAsCaseList().isEmpty()) {
			getContentBinding().btnAddActivityascase.setVisibility(View.VISIBLE);
		}
	}
}
