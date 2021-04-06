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

package de.symeda.sormas.app.event.edit;

import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.List;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DiseaseTransmissionMode;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventManagementStatus;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.HumanTransmissionMode;
import de.symeda.sormas.api.event.InfectionPathCertainty;
import de.symeda.sormas.api.event.InstitutionalPartnerType;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.MedicallyAssociatedTransmissionMode;
import de.symeda.sormas.api.event.ParenteralTransmissionMode;
import de.symeda.sormas.api.event.RiskLevel;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.facility.FacilityTypeGroup;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.event.Event;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.dialog.LocationDialog;
import de.symeda.sormas.app.component.validation.FragmentValidator;
import de.symeda.sormas.app.component.validation.ValidationHelper;
import de.symeda.sormas.app.core.notification.NotificationHelper;
import de.symeda.sormas.app.databinding.FragmentEventEditLayoutBinding;
import de.symeda.sormas.app.util.DataUtils;
import de.symeda.sormas.app.util.DiseaseConfigurationCache;

public class EventEditFragment extends BaseEditFragment<FragmentEventEditLayoutBinding, Event, Event> {

	private static final String EVENT_ENTITY = "Event";
	private static final String EVOLUTION_DATE_WITH_STATUS = "eventEvolutionDateWithStatus";
	private static final String EVOLUTION_COMMENT_WITH_STATUS = "eventEvolutionCommentWithStatus";

	private Event record;

	// Enum lists

	private List<Item> diseaseList;
	private List<Item> typeOfPlaceList;
	private List<Item> srcTypeList;
	private List<Item> srcInstitutionalPartnerTypeList;
	private List<Item> meansOfTransportList;
	private List<Item> diseaseTransmissionModeList;
	private boolean isMultiDayEvent;
	private List<Item> workEnvironmentList;
	private List<Item> humanTransmissionModeList;
	private List<Item> parenteralTransmissionModeList;
	private List<Item> medicallyAssociatedTransmissionModeList;
	private List<Item> infectionPathCertaintyList;

	public static EventEditFragment newInstance(Event activityRootData) {
		EventEditFragment fragment = newInstanceWithFieldCheckers(
			EventEditFragment.class,
			null,
			activityRootData,
			FieldVisibilityCheckers.withCountry(ConfigProvider.getServerCountryCode()),
			UiFieldAccessCheckers.getDefault(activityRootData.isPseudonymized()));

		fragment.isMultiDayEvent = activityRootData.getEndDate() != null;

		return fragment;
	}

	private void setUpControlListeners(final FragmentEventEditLayoutBinding contentBinding) {
		contentBinding.eventEventLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				openAddressPopup(contentBinding);
			}
		});

		contentBinding.eventEventStatus.addValueChangedListener(e -> {
			EventStatus eventStatus = (EventStatus) e.getValue();
			// The status will be used to modify the caption of the field
			// However we don't want to have somthing like "Dropped evolution date"
			// So let's ignore the DROPPED status and use the EVENT status instead
			String statusCaption;
			if (eventStatus == EventStatus.DROPPED) {
				statusCaption = I18nProperties.getCaption(EVENT_ENTITY);
			} else {
				statusCaption = I18nProperties.getEnumCaption(eventStatus);
			}

			contentBinding.eventEvolutionDate.setCaption(String.format(I18nProperties.getCaption(EVOLUTION_DATE_WITH_STATUS), statusCaption));
			contentBinding.eventEvolutionComment.setCaption(String.format(I18nProperties.getCaption(EVOLUTION_COMMENT_WITH_STATUS), statusCaption));
		});
	}

	private void openAddressPopup(final FragmentEventEditLayoutBinding contentBinding) {
		final Location location = record.getEventLocation();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, false, null);
		locationDialog.show();
		locationDialog.setRequiredFieldsBasedOnCountry();
		locationDialog.setFacilityFieldsVisible(record.getTypeOfPlace() == TypeOfPlace.FACILITY, true);

		locationDialog.setPositiveCallback(() -> {
			try {
				FragmentValidator.validate(getContext(), locationDialog.getContentBinding());
				contentBinding.eventEventLocation.setValue(locationClone);
				record.setEventLocation(locationClone);

				if (FacilityTypeGroup.WORKING_PLACE != locationDialog.getContentBinding().facilityTypeGroup.getValue()) {
					contentBinding.eventWorkEnvironment.setValue(null);
					contentBinding.eventWorkEnvironment.setVisibility(View.GONE);
				} else {
					contentBinding.eventWorkEnvironment.setVisibility(View.VISIBLE);
				}

				locationDialog.dismiss();
			} catch (ValidationException e) {
				NotificationHelper.showDialogNotification(locationDialog, ERROR, e.getMessage());
			}
		});
	}

	// Overrides

	@Override
	protected String getSubHeadingTitle() {
		return getResources().getString(R.string.caption_event_information);
	}

	@Override
	public Event getPrimaryData() {
		return record;
	}

	@Override
	protected void prepareFragmentData() {
		record = getActivityRootData();

		List<Disease> diseases = DiseaseConfigurationCache.getInstance().getAllDiseases(true, true, true);
		diseaseList = DataUtils.toItems(diseases);
		if (record.getDisease() != null && !diseases.contains(record.getDisease())) {
			diseaseList.add(DataUtils.toItem(record.getDisease()));
		}
		typeOfPlaceList = DataUtils.getEnumItems(TypeOfPlace.class, true, getFieldVisibilityCheckers());
		srcTypeList = DataUtils.getEnumItems(EventSourceType.class, true);
		srcInstitutionalPartnerTypeList = DataUtils.getEnumItems(InstitutionalPartnerType.class, true);
		meansOfTransportList = DataUtils.getEnumItems(MeansOfTransport.class, true);
		diseaseTransmissionModeList = DataUtils.getEnumItems(DiseaseTransmissionMode.class, true);
		workEnvironmentList = DataUtils.getEnumItems(WorkEnvironment.class, true);
		humanTransmissionModeList = DataUtils.getEnumItems(HumanTransmissionMode.class, true);
		parenteralTransmissionModeList = DataUtils.getEnumItems(ParenteralTransmissionMode.class, true);
		medicallyAssociatedTransmissionModeList = DataUtils.getEnumItems(MedicallyAssociatedTransmissionMode.class, true);
		infectionPathCertaintyList = DataUtils.getEnumItems(InfectionPathCertainty.class, true);
	}

	@Override
	public void onLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
		setUpControlListeners(contentBinding);

		contentBinding.setData(record);
		contentBinding.setEventStatusClass(EventStatus.class);
		contentBinding.setEventInvestigationStatusClass(EventInvestigationStatus.class);
		contentBinding.setRiskLevelClass(RiskLevel.class);
		contentBinding.setEventManagementStatusClass(EventManagementStatus.class);
		contentBinding.setIsMultiDayEvent(isMultiDayEvent);

		ValidationHelper.initDateIntervalValidator(contentBinding.eventStartDate, contentBinding.eventEndDate);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
		// Initialize ControlSpinnerFields
		contentBinding.eventDisease.initializeSpinner(diseaseList);
		contentBinding.eventTypeOfPlace.initializeSpinner(typeOfPlaceList);
		contentBinding.eventSrcType.initializeSpinner(srcTypeList);
		contentBinding.eventSrcInstitutionalPartnerType.initializeSpinner(srcInstitutionalPartnerTypeList);
		contentBinding.eventMeansOfTransport.initializeSpinner(meansOfTransportList);
		contentBinding.eventDiseaseTransmissionMode.initializeSpinner(diseaseTransmissionModeList);
		contentBinding.eventWorkEnvironment.initializeSpinner(workEnvironmentList);
		contentBinding.eventHumanTransmissionMode.initializeSpinner(humanTransmissionModeList);
		contentBinding.eventParenteralTransmissionMode.initializeSpinner(parenteralTransmissionModeList);
		contentBinding.eventMedicallyAssociatedTransmissionMode.initializeSpinner(medicallyAssociatedTransmissionModeList);
		contentBinding.eventInfectionPathCertainty.initializeSpinner(infectionPathCertaintyList);

		// Initialize ControlDateFields
		contentBinding.eventStartDate.initializeDateField(getFragmentManager());
		String startDateCaption = Boolean.TRUE.equals(contentBinding.eventMultiDayEvent.getValue())
			? I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.START_DATE)
			: I18nProperties.getCaption(Captions.singleDayEventDate);
		contentBinding.eventStartDate.setCaption(startDateCaption);

		contentBinding.eventEndDate.initializeDateField(getFragmentManager());

		contentBinding.eventEventInvestigationStartDate.initializeDateField(getFragmentManager());
		contentBinding.eventEventInvestigationEndDate.initializeDateField(getFragmentManager());
		contentBinding.eventTravelDate.initializeDateField(getFragmentManager());
		contentBinding.eventEvolutionDate.initializeDateField(getFragmentManager());

		setFieldVisibilitiesAndAccesses(EventDto.class, contentBinding.mainContent);

		contentBinding.eventTypeOfPlace.addValueChangedListener(e -> {
			if (e.getValue() != TypeOfPlace.FACILITY) {
				contentBinding.eventWorkEnvironment.setValue(null);
				contentBinding.eventWorkEnvironment.setVisibility(View.GONE);
			} else {
				FacilityType facilityType = record.getEventLocation().getFacilityType();
				contentBinding.eventWorkEnvironment.setVisibility(
					facilityType == null || FacilityTypeGroup.WORKING_PLACE != facilityType.getFacilityTypeGroup() ? View.GONE : View.VISIBLE);
			}
		});

		if (isVisibleAllowed(EventDto.class, contentBinding.eventInfectionPathCertainty)) {
			setVisibleWhen(contentBinding.eventInfectionPathCertainty, contentBinding.eventNosocomial, YesNoUnknown.YES);
		}
		if (isVisibleAllowed(EventDto.class, contentBinding.eventHumanTransmissionMode)) {
			setVisibleWhen(
				contentBinding.eventHumanTransmissionMode,
				contentBinding.eventDiseaseTransmissionMode,
				DiseaseTransmissionMode.HUMAN_TO_HUMAN);
		}
		if (isVisibleAllowed(EventDto.class, contentBinding.eventParenteralTransmissionMode)) {
			setVisibleWhen(
				contentBinding.eventParenteralTransmissionMode,
				contentBinding.eventHumanTransmissionMode,
				HumanTransmissionMode.PARENTERAL);
		}
		if (isVisibleAllowed(EventDto.class, contentBinding.eventMedicallyAssociatedTransmissionMode)) {
			setVisibleWhen(
				contentBinding.eventMedicallyAssociatedTransmissionMode,
				contentBinding.eventParenteralTransmissionMode,
				ParenteralTransmissionMode.MEDICALLY_ASSOCIATED);
		}
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_event_edit_layout;
	}
}
