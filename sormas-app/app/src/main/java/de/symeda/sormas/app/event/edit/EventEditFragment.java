/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.core.notification.NotificationType.ERROR;

import java.util.List;
import java.util.Optional;

import android.view.View;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.DiseaseTransmissionMode;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIdentificationSource;
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
import de.symeda.sormas.api.event.SpecificRisk;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.exposure.WorkEnvironment;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.utils.ValidationException;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.app.BaseActivity;
import de.symeda.sormas.app.BaseEditFragment;
import de.symeda.sormas.app.R;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
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

	private List<Item> eventIdentificationSourceList;
	private List<Item> diseaseList;
	private List<Item> diseaseVariantList;
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
	private List<Item> specificRiskList;

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

		contentBinding.eventDisease.addValueChangedListener(f -> updateCustomizableEnumFields(contentBinding));
	}

	private void openAddressPopup(final FragmentEventEditLayoutBinding contentBinding) {
		final Location location = record.getEventLocation();
		final Location locationClone = (Location) location.clone();
		final LocationDialog locationDialog = new LocationDialog(BaseActivity.getActiveActivity(), locationClone, false, null);
		locationDialog.show();
		if (DatabaseHelper.getEventDao().hasAnyEventParticipantWithoutJurisdiction(record.getUuid())) {
			locationDialog.getContentBinding().locationRegion.setRequired(true);
			locationDialog.getContentBinding().locationDistrict.setRequired(true);
			locationDialog.getContentBinding().locationCountry.setEnabled(false);
		} else {
			locationDialog.setRequiredFieldsBasedOnCountry();
		}
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
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.DISEASE_VARIANT,
				Optional.ofNullable(record.getDiseaseVariant()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		diseaseVariantList = DataUtils.toItems(diseaseVariants);
		if (record.getDiseaseVariant() != null && !diseaseVariants.contains(record.getDiseaseVariant())) {
			diseaseVariantList.add(DataUtils.toItem(record.getDiseaseVariant()));
		}
		List<SpecificRisk> specificRisks = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.SPECIFIC_EVENT_RISK,
				Optional.ofNullable(record.getSpecificRisk()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		specificRiskList = DataUtils.toItems(specificRisks);
		if (record.getSpecificRisk() != null && !specificRisks.contains(record.getSpecificRisk())) {
			specificRiskList.add(DataUtils.toItem(record.getSpecificRisk()));
		}

		eventIdentificationSourceList = DataUtils.getEnumItems(EventIdentificationSource.class, true);
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

		ValidationHelper.initEmailValidator(contentBinding.eventSrcEmail);
		ValidationHelper.initPhoneNumberValidator(contentBinding.eventSrcTelNo);
		ValidationHelper.initDateIntervalValidator(contentBinding.eventStartDate, contentBinding.eventEndDate);
		ValidationHelper.initDateIntervalValidator(contentBinding.eventStartDate, contentBinding.eventReportDateTime);
	}

	@Override
	public void onAfterLayoutBinding(FragmentEventEditLayoutBinding contentBinding) {
		// Initialize ControlSpinnerFields
		contentBinding.eventEventIdentificationSource.initializeSpinner(eventIdentificationSourceList);
		contentBinding.eventDisease.initializeSpinner(diseaseList);
		contentBinding.eventDiseaseVariant.initializeSpinner(diseaseVariantList);
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
		contentBinding.eventSpecificRisk.initializeSpinner(specificRiskList);

		// Initialize ControlDateFields
		contentBinding.eventReportDateTime.initializeDateField(getFragmentManager());

		contentBinding.eventStartDate.initializeDateTimeField(getFragmentManager());
		String startDateCaption = Boolean.TRUE.equals(contentBinding.eventMultiDayEvent.getValue())
			? I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.START_DATE)
			: I18nProperties.getCaption(Captions.singleDayEventDate);
		contentBinding.eventStartDate.setCaption(startDateCaption);

		contentBinding.eventEndDate.initializeDateTimeField(getFragmentManager());

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
		contentBinding.eventResponsibleUser.setPseudonymized(record.isPseudonymized());
	}

	private void updateCustomizableEnumFields(FragmentEventEditLayoutBinding contentBinding) {
		// Disease variant
		DiseaseVariant selectedVariant = (DiseaseVariant) contentBinding.eventDiseaseVariant.getValue();
		List<DiseaseVariant> diseaseVariants = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.DISEASE_VARIANT,
				Optional.ofNullable(record.getDiseaseVariant()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		diseaseVariantList.clear();
		diseaseVariantList.addAll(DataUtils.toItems(diseaseVariants));
		contentBinding.eventDiseaseVariant.setSpinnerData(diseaseVariantList);
		if (diseaseVariants.contains(selectedVariant)) {
			contentBinding.eventDiseaseVariant.setValue(selectedVariant);
		} else {
			contentBinding.eventDiseaseVariant.setValue(null);
		}
		contentBinding.eventDiseaseVariant.setVisibility(DataUtils.emptyOrWithOneNullItem(diseaseVariantList) ? GONE : VISIBLE);

		// Specific risk
		SpecificRisk selectedRisk = (SpecificRisk) contentBinding.eventSpecificRisk.getValue();
		List<SpecificRisk> specificRisks = DatabaseHelper.getCustomizableEnumValueDao()
			.getEnumValues(
				CustomizableEnumType.SPECIFIC_EVENT_RISK,
				Optional.ofNullable(record.getSpecificRisk()).map(CustomizableEnum::getValue).orElse(null),
				record.getDisease());
		specificRiskList.clear();
		specificRiskList.addAll(DataUtils.toItems(specificRisks));
		contentBinding.eventSpecificRisk.setSpinnerData(specificRiskList);
		if (specificRisks.contains(selectedRisk)) {
			contentBinding.eventSpecificRisk.setValue(selectedRisk);
		} else {
			contentBinding.eventSpecificRisk.setValue(null);
		}
		contentBinding.eventSpecificRisk.setVisibility(specificRisks.isEmpty() ? GONE : VISIBLE);
	}

	@Override
	public int getEditLayout() {
		return R.layout.fragment_event_edit_layout;
	}
}
