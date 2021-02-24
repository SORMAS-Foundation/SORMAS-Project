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
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getDiseaseOfCaseOrContact;
import static de.symeda.sormas.app.epidata.EpiDataFragmentHelper.getEpiDataOfCaseOrContact;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ObservableArrayList;

import de.symeda.sormas.api.activityascase.ActivityAsCaseDto;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.exposure.ExposureDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseReadFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.activityascase.ActivityAsCase;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.backend.contact.Contact;
import de.symeda.sormas.app.backend.epidata.EpiData;
import de.symeda.sormas.app.backend.exposure.Exposure;
import de.symeda.sormas.app.component.dialog.InfoDialog;
import de.symeda.sormas.app.core.IEntryItemOnClickListener;
import de.symeda.sormas.app.databinding.DialogActivityAsCaseReadLayoutBinding;
import de.symeda.sormas.app.databinding.DialogExposureReadLayoutBinding;
import de.symeda.sormas.app.databinding.FragmentReadEpidLayoutBinding;
import de.symeda.sormas.app.util.FieldVisibilityAndAccessHelper;

public class EpidemiologicalDataReadFragment extends BaseReadFragment<FragmentReadEpidLayoutBinding, EpiData, AbstractDomainObject> {

	public static final String TAG = EpidemiologicalDataReadFragment.class.getSimpleName();

	private EpiData record;
	private IEntryItemOnClickListener onExposureItemClickListener;
	private IEntryItemOnClickListener onActivityAsCaseItemClickListener;

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

	private void setUpControlListeners() {
		onExposureItemClickListener = (v, item) -> {
			InfoDialog infoDialog = new InfoDialog(
				getContext(),
				R.layout.dialog_exposure_read_layout,
				item,
				boundView -> FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
					ExposureDto.class,
					(ViewGroup) boundView,
					new FieldVisibilityCheckers(),
					getFieldAccessCheckers()));

			final DialogExposureReadLayoutBinding exposureBinding = (DialogExposureReadLayoutBinding) infoDialog.getBinding();
			if (((Exposure) item).getMeansOfTransport() == MeansOfTransport.PLANE) {
				exposureBinding.exposureConnectionNumber.setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			}

			final FacilityType facilityType = ((Exposure) item).getLocation().getFacilityType();

			exposureBinding.exposureWorkEnvironment.setVisibility(
				facilityType == null || FacilityTypeGroup.WORKING_PLACE != facilityType.getFacilityTypeGroup() ? View.GONE : View.VISIBLE);

			FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
				ExposureDto.class,
				(ViewGroup) infoDialog.getBinding().getRoot(),
				FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(getActivityRootData())),
				UiFieldAccessCheckers.forSensitiveData(((PseudonymizableAdo) getActivityRootData()).isPseudonymized()));

			infoDialog.show();
		};

		onActivityAsCaseItemClickListener = (v, item) -> {
			InfoDialog infoDialog = new InfoDialog(
				getContext(),
				R.layout.dialog_activity_as_case_read_layout,
				item,
				boundView -> FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
					ActivityAsCaseDto.class,
					(ViewGroup) boundView,
					new FieldVisibilityCheckers(),
					getFieldAccessCheckers()));

			final DialogActivityAsCaseReadLayoutBinding activityAsCaseBinding = (DialogActivityAsCaseReadLayoutBinding) infoDialog.getBinding();
			if (((ActivityAsCase) item).getMeansOfTransport() == MeansOfTransport.PLANE) {
				activityAsCaseBinding.activityAsCaseConnectionNumber.setCaption(I18nProperties.getCaption(Captions.activityAsCaseFlightNumber));
			}

			final FacilityType facilityType = ((ActivityAsCase) item).getLocation().getFacilityType();

			activityAsCaseBinding.activityAsCaseWorkEnvironment.setVisibility(
				facilityType == null || FacilityTypeGroup.WORKING_PLACE != facilityType.getFacilityTypeGroup() ? View.GONE : View.VISIBLE);

			FieldVisibilityAndAccessHelper.setFieldVisibilitiesAndAccesses(
				ActivityAsCaseDto.class,
				(ViewGroup) infoDialog.getBinding().getRoot(),
				FieldVisibilityCheckers.withDisease(getDiseaseOfCaseOrContact(getActivityRootData())),
				UiFieldAccessCheckers.forSensitiveData(((PseudonymizableAdo) getActivityRootData()).isPseudonymized()));

			infoDialog.show();
		};
	}

	@Override
	protected void prepareFragmentData(Bundle savedInstanceState) {
		record = getEpiDataOfCaseOrContact(getActivityRootData());
	}

	@Override
	public void onLayoutBinding(FragmentReadEpidLayoutBinding contentBinding) {
		setUpControlListeners();

		contentBinding.setData(record);

		ObservableArrayList<Exposure> exposures = new ObservableArrayList<>();
		exposures.addAll(record.getExposures());

		contentBinding.setExposureList(exposures);
		contentBinding.setExposureItemClickCallback(onExposureItemClickListener);
		contentBinding.setExposureListBindCallback(
			v -> FieldVisibilityAndAccessHelper
				.setFieldVisibilitiesAndAccesses(ExposureDto.class, (ViewGroup) v, new FieldVisibilityCheckers(), getFieldAccessCheckers()));

		ObservableArrayList<ActivityAsCase> activitiesAsCase = new ObservableArrayList<>();
		activitiesAsCase.addAll(record.getActivitiesAsCase());

		contentBinding.setActivityascaseList(activitiesAsCase);
		contentBinding.setActivityascaseItemClickCallback(onActivityAsCaseItemClickListener);
		contentBinding.setActivityascaseListBindCallback(
			v -> FieldVisibilityAndAccessHelper
				.setFieldVisibilitiesAndAccesses(ActivityAsCaseDto.class, (ViewGroup) v, new FieldVisibilityCheckers(), getFieldAccessCheckers()));
	}

	@Override
	public void onAfterLayoutBinding(FragmentReadEpidLayoutBinding contentBinding) {
		setFieldVisibilitiesAndAccesses(EpiDataDto.class, contentBinding.mainContent);
		if (record.getExposureDetailsKnown() != YesNoUnknown.YES) {
			contentBinding.exposuresLayout.setVisibility(View.GONE);
		}

		if (getActivityRootData() instanceof Case) {
			if (record.getActivityAsCaseDetailsKnown() != YesNoUnknown.YES) {
				contentBinding.activitiesascaseLayout.setVisibility(View.GONE);
			}
		}

		if (!(getActivityRootData() instanceof Case)) {
			contentBinding.epiDataContactWithSourceCaseKnown.setVisibility(GONE);
			contentBinding.activitiesascaseLayout.setVisibility(View.GONE);
			contentBinding.epiDataActivityAsCaseDetailsKnown.setVisibility(View.GONE);
		}
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

}
