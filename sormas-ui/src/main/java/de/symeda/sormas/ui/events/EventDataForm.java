/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.events;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.validator.EmailValidator;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.DiseaseTransmissionMode;
import de.symeda.sormas.api.event.EpidemiologicalEvidenceDetail;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventInvestigationStatus;
import de.symeda.sormas.api.event.EventSourceType;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.event.HumanTransmissionMode;
import de.symeda.sormas.api.event.InstitutionalPartnerType;
import de.symeda.sormas.api.event.LaboratoryDiagnosticEvidenceDetail;
import de.symeda.sormas.api.event.MeansOfTransport;
import de.symeda.sormas.api.event.ParenteralTransmissionMode;
import de.symeda.sormas.api.event.SpecificRisk;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.location.LocationEditForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CheckBoxTree;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.PhoneNumberValidator;
import de.symeda.sormas.ui.utils.ResizableTextAreaWrapper;
import de.symeda.sormas.ui.utils.UserField;
import de.symeda.sormas.ui.utils.ValidationUtils;

@SuppressWarnings("deprecation")
public class EventDataForm extends AbstractEditForm<EventDto> {

	private static final long serialVersionUID = 1L;

	private static final String EVENT_DATA_HEADING_LOC = "contactDataHeadingLoc";
	private static final String MULTI_DAY_EVENT_LOC = "eventMultiDay";
	private static final String INFORMATION_SOURCE_HEADING_LOC = "informationSourceHeadingLoc";
	private static final String LOCATION_HEADING_LOC = "locationHeadingLoc";
	private static final String EXTERNAL_TOKEN_WARNING_LOC = "externalTokenWarningLoc";

	private static final String STATUS_CHANGE = "statusChange";

	private static final String EVENT_ENTITY = "Event";
	private static final String EVOLUTION_DATE_WITH_STATUS = "eventEvolutionDateWithStatus";
	private static final String EVOLUTION_COMMENT_WITH_STATUS = "eventEvolutionCommentWithStatus";

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(EVENT_DATA_HEADING_LOC) +
					fluidRowLocs(4, EventDto.UUID, 3, EventDto.REPORT_DATE_TIME, 3, EventDto.REPORTING_USER, 2, "") +
					fluidRowLocs(EventDto.EVENT_STATUS, EventDto.EVENT_MANAGEMENT_STATUS) +
					fluidRowLocs(EventDto.EVENT_IDENTIFICATION_SOURCE) +
					fluidRowLocs(EventDto.RISK_LEVEL, EventDto.SPECIFIC_RISK) +
					fluidRowLocs(EventDto.MULTI_DAY_EVENT) +
					fluidRowLocs(4, EventDto.START_DATE, 4, EventDto.END_DATE) +
					fluidRowLocs(EventDto.EVOLUTION_DATE, EventDto.EVOLUTION_COMMENT) +
					fluidRowLocs(EventDto.EVENT_INVESTIGATION_STATUS) +
					fluidRowLocs(4,EventDto.EVENT_INVESTIGATION_START_DATE, 4, EventDto.EVENT_INVESTIGATION_END_DATE) +
					fluidRow(
							fluidColumnLoc(6, 0, EventDto.DISEASE),
							fluidColumnLoc(6, 0, EventDto.DISEASE_DETAILS)) +
					fluidRowLocs(EventDto.DISEASE_VARIANT, EventDto.DISEASE_VARIANT_DETAILS) +
					fluidRowLocs(EventDto.EXTERNAL_ID, EventDto.EXTERNAL_TOKEN) +
					fluidRowLocs(EventDto.INTERNAL_TOKEN, EXTERNAL_TOKEN_WARNING_LOC) +
					fluidRowLocs(EventDto.EVENT_TITLE) +
					fluidRowLocs(EventDto.EVENT_DESC) +
					fluidRowLocs(EventDto.DISEASE_TRANSMISSION_MODE, EventDto.NOSOCOMIAL) +
					fluidRowLocs(EventDto.HUMAN_TRANSMISSION_MODE, EventDto.INFECTION_PATH_CERTAINTY) +
					fluidRowLocs(6, EventDto.PARENTERAL_TRANSMISSION_MODE, 6, EventDto.MEDICALLY_ASSOCIATED_TRANSMISSION_MODE) +
					fluidRowLocs(EventDto.EPIDEMIOLOGICAL_EVIDENCE, EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE) +
					fluidRowLocs(6, EventDto.EPIDEMIOLOGICAL_EVIDENCE_DETAILS, 6, EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE_DETAILS) +

					loc(INFORMATION_SOURCE_HEADING_LOC) +
					fluidRowLocs(EventDto.SRC_TYPE, "") +
					fluidRowLocs(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE, EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS) +
					fluidRowLocs(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME) +
					fluidRowLocs(EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL) +

					fluidRowLocs(EventDto.SRC_MEDIA_WEBSITE, EventDto.SRC_MEDIA_NAME) +
					fluidRowLocs(EventDto.SRC_MEDIA_DETAILS) +

					loc(LOCATION_HEADING_LOC) +
					fluidRowLocs(EventDto.TRANSREGIONAL_OUTBREAK, "") +
					fluidRow(
							fluidColumn(6,0,locs(EventDto.TYPE_OF_PLACE)),
							fluidColumn(6,0, locs(
									EventDto.TYPE_OF_PLACE_TEXT,
									EventDto.MEANS_OF_TRANSPORT,
									EventDto.WORK_ENVIRONMENT))) +
					loc(EventDto.MEANS_OF_TRANSPORT_DETAILS) +
					fluidRowLocs(4, EventDto.CONNECTION_NUMBER, 4, EventDto.TRAVEL_DATE) +
					fluidRowLocs(EventDto.EVENT_LOCATION) +
					fluidRowLocs("", EventDto.RESPONSIBLE_USER) +
					fluidRowLocs(CaseDataDto.DELETION_REASON) +
					fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);
	//@formatter:on

	private final Boolean isCreateForm;
	private final boolean isPseudonymized;
	private final boolean inJurisdiction;
	private List<UserReferenceDto> regionEventResponsibles = new ArrayList<>();
	private List<UserReferenceDto> districtEventResponsibles = new ArrayList<>();
	private LocationEditForm locationForm;

	public EventDataForm(boolean create, boolean isPseudonymized, boolean inJurisdiction) {
		super(
			EventDto.class,
			EventDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			createFieldAccessCheckers(isPseudonymized, inJurisdiction, true));

		isCreateForm = create;
		this.isPseudonymized = isPseudonymized;
		this.inJurisdiction = inJurisdiction;

		if (create) {
			hideValidationUntilNextCommit();
		}
		VerticalLayout statusChangeLayout = new VerticalLayout();
		statusChangeLayout.setSpacing(false);
		statusChangeLayout.setMargin(false);
		getContent().addComponent(statusChangeLayout, STATUS_CHANGE);

		addFields();
	}

	private static UiFieldAccessCheckers createFieldAccessCheckers(
		boolean isPseudonymized,
		boolean inJurisdiction,
		boolean withPersonalAndSensitive) {

		if (withPersonalAndSensitive) {
			return UiFieldAccessCheckers
				.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized);
		}

		return UiFieldAccessCheckers.getNoop();
	}

	@Override
	protected void addFields() {
		if (isCreateForm == null) {
			return;
		}

		Label eventDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingEventData));
		eventDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(eventDataHeadingLabel, EVENT_DATA_HEADING_LOC);

		Label informationSourceHeadingLabel = new Label(I18nProperties.getString(Strings.headingInformationSource));
		informationSourceHeadingLabel.addStyleName(H3);
		getContent().addComponent(informationSourceHeadingLabel, INFORMATION_SOURCE_HEADING_LOC);

		Label locationHeadingLabel = new Label(I18nProperties.getString(Strings.headingLocation));
		locationHeadingLabel.addStyleName(H3);
		getContent().addComponent(locationHeadingLabel, LOCATION_HEADING_LOC);

		addField(EventDto.UUID, TextField.class);
		ComboBox diseaseField = addDiseaseField(EventDto.DISEASE, false, isCreateForm);
		addField(EventDto.DISEASE_DETAILS, TextField.class);
		ComboBox diseaseVariantField = addCustomizableEnumField(EventDto.DISEASE_VARIANT);
		diseaseVariantField.setNullSelectionAllowed(true);
		addFields(EventDto.EXTERNAL_ID);
		TextField diseaseVariantDetailsField = addField(EventDto.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);

		TextField externalTokenField = addField(EventDto.EXTERNAL_TOKEN);
		Label externalTokenWarningLabel = new Label(I18nProperties.getString(Strings.messageEventExternalTokenWarning));
		externalTokenWarningLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		getContent().addComponent(externalTokenWarningLabel, EXTERNAL_TOKEN_WARNING_LOC);

		addField(EventDto.INTERNAL_TOKEN);

		DateTimeField startDate = addField(EventDto.START_DATE, DateTimeField.class);
		CheckBox multiDayCheckbox = addField(EventDto.MULTI_DAY_EVENT, CheckBox.class);
		DateTimeField endDate = addField(EventDto.END_DATE, DateTimeField.class);
		initEventDateValidation(startDate, endDate, multiDayCheckbox);

		addField(EventDto.EVENT_STATUS, NullableOptionGroup.class);
		addField(EventDto.RISK_LEVEL);
		ComboBox specificRiskField = addCustomizableEnumField(EventDto.SPECIFIC_RISK);
		specificRiskField.setNullSelectionAllowed(true);

		addField(EventDto.EVENT_MANAGEMENT_STATUS, NullableOptionGroup.class);
		addField(EventDto.EVENT_IDENTIFICATION_SOURCE, NullableOptionGroup.class);

		addField(EventDto.EVENT_INVESTIGATION_STATUS, NullableOptionGroup.class);
		addField(EventDto.EVENT_INVESTIGATION_START_DATE, DateField.class);
		addField(EventDto.EVENT_INVESTIGATION_END_DATE, DateField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.EVENT_INVESTIGATION_START_DATE, EventDto.EVENT_INVESTIGATION_END_DATE),
			EventDto.EVENT_INVESTIGATION_STATUS,
			Arrays.asList(EventInvestigationStatus.ONGOING, EventInvestigationStatus.DONE, EventInvestigationStatus.DISCARDED),
			true);
		TextField title = addField(EventDto.EVENT_TITLE, TextField.class);
		title.addStyleName(CssStyles.SOFT_REQUIRED);

		TextArea descriptionField = addField(EventDto.EVENT_DESC, TextArea.class, new ResizableTextAreaWrapper<>());
		descriptionField.setRows(2);
		descriptionField.setDescription(
			I18nProperties.getPrefixDescription(EventDto.I18N_PREFIX, EventDto.EVENT_DESC, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		addField(EventDto.DISEASE_TRANSMISSION_MODE, ComboBox.class);
		addField(EventDto.NOSOCOMIAL, NullableOptionGroup.class);

		addFields(EventDto.HUMAN_TRANSMISSION_MODE, EventDto.INFECTION_PATH_CERTAINTY);
		addFields(EventDto.PARENTERAL_TRANSMISSION_MODE, EventDto.MEDICALLY_ASSOCIATED_TRANSMISSION_MODE);

		final NullableOptionGroup epidemiologicalEvidence = addField(EventDto.EPIDEMIOLOGICAL_EVIDENCE, NullableOptionGroup.class);
		final NullableOptionGroup laboratoryDiagnosticEvidence = addField(EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE, NullableOptionGroup.class);

		CheckBoxTree<EpidemiologicalEvidenceDetail> epidemiologicalEvidenceCheckBoxTree =
			addField(EventDto.EPIDEMIOLOGICAL_EVIDENCE_DETAILS, CheckBoxTree.class);
		epidemiologicalEvidenceCheckBoxTree.setEnumType(EpidemiologicalEvidenceDetail.class, EpidemiologicalEvidenceDetail::getParent);

		CheckBoxTree<LaboratoryDiagnosticEvidenceDetail> laboratoryDiagnosticEvidenceDetailCheckBoxTree =
			addField(EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE_DETAILS, CheckBoxTree.class);
		laboratoryDiagnosticEvidenceDetailCheckBoxTree
			.setEnumType(LaboratoryDiagnosticEvidenceDetail.class, LaboratoryDiagnosticEvidenceDetail::getParent);

		DateField evolutionDateField = addField(EventDto.EVOLUTION_DATE, DateField.class);
		TextField evolutionCommentField = addField(EventDto.EVOLUTION_COMMENT, TextField.class);

		Field<?> statusField = getField(EventDto.EVENT_STATUS);
		statusField.addValueChangeListener(e -> {
			if (statusField.getValue() == null) {
				return;
			}

			EventStatus eventStatus = (EventStatus) statusField.getValue();
			// The status will be used to modify the caption of the field
			// However we don't want to have somthing like "Dropped evolution date"
			// So let's ignore the DROPPED status and use the Event entity caption instead
			String statusCaption;
			if (eventStatus == EventStatus.DROPPED) {
				statusCaption = I18nProperties.getCaption(EVENT_ENTITY);
			} else {
				statusCaption = I18nProperties.getEnumCaption(eventStatus);
			}

			evolutionDateField.setCaption(String.format(I18nProperties.getCaption(EVOLUTION_DATE_WITH_STATUS), statusCaption));
			evolutionCommentField.setCaption(String.format(I18nProperties.getCaption(EVOLUTION_COMMENT_WITH_STATUS), statusCaption));
		});

		FieldHelper
			.setVisibleWhenSourceNotNull(getFieldGroup(), Collections.singletonList(EventDto.EVOLUTION_COMMENT), EventDto.EVOLUTION_DATE, true);

		ComboBox typeOfPlace = addField(EventDto.TYPE_OF_PLACE, ComboBox.class);
		typeOfPlace.setNullSelectionAllowed(true);
		addField(EventDto.TYPE_OF_PLACE_TEXT, TextField.class);

		addField(EventDto.WORK_ENVIRONMENT);
		ComboBox meansOfTransport = addField(EventDto.MEANS_OF_TRANSPORT);
		TextField connectionNumber = addField(EventDto.CONNECTION_NUMBER);
		DateField travelDate = addField(EventDto.TRAVEL_DATE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.MEANS_OF_TRANSPORT),
			EventDto.TYPE_OF_PLACE,
			Collections.singletonList(TypeOfPlace.MEANS_OF_TRANSPORT),
			true);

		TextField meansOfTransportDetails = addField(EventDto.MEANS_OF_TRANSPORT_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.MEANS_OF_TRANSPORT_DETAILS),
			EventDto.MEANS_OF_TRANSPORT,
			Collections.singletonList(MeansOfTransport.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.CONNECTION_NUMBER, EventDto.TRAVEL_DATE),
			EventDto.TYPE_OF_PLACE,
			Collections.singletonList(TypeOfPlace.MEANS_OF_TRANSPORT),
			true);

		getField(EventDto.MEANS_OF_TRANSPORT).addValueChangeListener(e -> {
			if (e.getProperty().getValue() == MeansOfTransport.PLANE) {
				getField(EventDto.CONNECTION_NUMBER).setCaption(I18nProperties.getCaption(Captions.exposureFlightNumber));
			} else {
				getField(EventDto.CONNECTION_NUMBER).setCaption(I18nProperties.getPrefixCaption(EventDto.I18N_PREFIX, EventDto.CONNECTION_NUMBER));
			}
		});

		DateField reportDate = addField(EventDto.REPORT_DATE_TIME, DateField.class);

		UserField reportingUser = addField(EventDto.REPORTING_USER, UserField.class);
		reportingUser.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());

		addField(EventDto.TRANSREGIONAL_OUTBREAK, NullableOptionGroup.class);

		ComboBox srcType = addField(EventDto.SRC_TYPE);

		TextField srcFirstName = addField(EventDto.SRC_FIRST_NAME, TextField.class);
		TextField srcLastName = addField(EventDto.SRC_LAST_NAME, TextField.class);
		TextField srcTelNo = addField(EventDto.SRC_TEL_NO, TextField.class);
		TextField srcEmail = addField(EventDto.SRC_EMAIL, TextField.class);
		srcTelNo.addValidator(new PhoneNumberValidator(I18nProperties.getValidationError(Validations.validPhoneNumber, srcTelNo.getCaption())));
		srcEmail.addValidator(new EmailValidator(I18nProperties.getValidationError(Validations.validEmailAddress, srcEmail.getCaption())));

		TextField srcMediaWebsite = addField(EventDto.SRC_MEDIA_WEBSITE, TextField.class);
		TextField srcMediaName = addField(EventDto.SRC_MEDIA_NAME, TextField.class);
		TextArea srcMediaDetails = addField(EventDto.SRC_MEDIA_DETAILS, TextArea.class);
		srcMediaDetails.setRows(4);

		ComboBox srcInstitutionalPartnerType = addField(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE),
			EventDto.SRC_TYPE,
			Collections.singletonList(EventSourceType.INSTITUTIONAL_PARTNER),
			true);

		TextField srcInstitutionalPartnerTypeDetails = addField(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS),
			EventDto.SRC_INSTITUTIONAL_PARTNER_TYPE,
			Collections.singletonList(InstitutionalPartnerType.OTHER),
			true);

		addField(
			EventDto.EVENT_LOCATION,
			new LocationEditForm(fieldVisibilityCheckers, createFieldAccessCheckers(isPseudonymized, inJurisdiction, false))).setCaption(null);

		locationForm = (LocationEditForm) getFieldGroup().getField(EventDto.EVENT_LOCATION);
		locationForm.setDistrictRequiredOnDefaultCountry(true);
		ComboBox regionField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.REGION);
		ComboBox districtField = (ComboBox) locationForm.getFieldGroup().getField(LocationDto.DISTRICT);

		UserField responsibleUserField = addField(EventDto.RESPONSIBLE_USER, UserField.class);
		responsibleUserField.setParentPseudonymizedSupplier(() -> getValue().isPseudonymized());
		responsibleUserField.setEnabled(true);

		addField(EventDto.DELETION_REASON);
		addField(EventDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, EventDto.DELETION_REASON, EventDto.OTHER_DELETION_REASON);

		if (isCreateForm) {
			locationForm.hideValidationUntilNextCommit();
		}

		setReadOnly(true, EventDto.UUID, EventDto.REPORTING_USER);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EventDto.WORK_ENVIRONMENT,
			locationForm.getFacilityTypeGroup(),
			Collections.singletonList(FacilityTypeGroup.WORKING_PLACE),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Collections.singletonList(EventDto.DISEASE_DETAILS),
			EventDto.DISEASE,
			Collections.singletonList(Disease.OTHER),
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			EventDto.DISEASE,
			Collections.singletonList(EventDto.DISEASE_DETAILS),
			Collections.singletonList(Disease.OTHER));

		// Customizable enum fields visibilities
		diseaseVariantField.setVisible(false);
		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			// Disease variants
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField.setVisible(disease != null && CollectionUtils.isNotEmpty(diseaseVariants));
			// Specific event risks
			List<SpecificRisk> specificRiskValues =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.SPECIFIC_EVENT_RISK, disease);
			FieldHelper.updateItems(specificRiskField, specificRiskValues);
			specificRiskField.setVisible(isVisibleAllowed(EventDto.SPECIFIC_RISK) && CollectionUtils.isNotEmpty(specificRiskValues));
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});

		setRequired(true, EventDto.EVENT_STATUS, EventDto.UUID, EventDto.EVENT_TITLE, EventDto.REPORT_DATE_TIME, EventDto.REPORTING_USER);

		reportDate.addValidator(
			new DateComparisonValidator(
				reportDate,
				startDate,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, reportDate.getCaption(), startDate.getCaption())));

		startDate.addValidator(
			new DateComparisonValidator(
				startDate,
				reportDate,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), reportDate.getCaption())));

		DateComparisonValidator.dateFieldDependencyValidationVisibility(startDate, reportDate);

		FieldHelper.setVisibleWhen(getFieldGroup(), EventDto.END_DATE, EventDto.MULTI_DAY_EVENT, Collections.singletonList(true), true);
		FieldHelper.setCaptionWhen(
			multiDayCheckbox,
			startDate,
			false,
			I18nProperties.getCaption(Captions.singleDayEventDate),
			I18nProperties.getCaption(Captions.Event_startDate));
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.NOSOCOMIAL, EventDto.TRANSREGIONAL_OUTBREAK, EventDto.DISEASE_TRANSMISSION_MODE),
			EventDto.EVENT_STATUS,
			Collections.singletonList(EventStatus.CLUSTER),
			true);
		if (isVisibleAllowed(EventDto.INFECTION_PATH_CERTAINTY)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.INFECTION_PATH_CERTAINTY,
				EventDto.NOSOCOMIAL,
				Collections.singletonList(YesNoUnknown.YES),
				true);
		}
		if (isVisibleAllowed(EventDto.HUMAN_TRANSMISSION_MODE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.HUMAN_TRANSMISSION_MODE,
				EventDto.DISEASE_TRANSMISSION_MODE,
				Collections.singletonList(DiseaseTransmissionMode.HUMAN_TO_HUMAN),
				true);
		}
		if (isVisibleAllowed(EventDto.PARENTERAL_TRANSMISSION_MODE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.PARENTERAL_TRANSMISSION_MODE,
				EventDto.HUMAN_TRANSMISSION_MODE,
				Collections.singletonList(HumanTransmissionMode.PARENTERAL),
				true);
		}
		if (isVisibleAllowed(EventDto.MEDICALLY_ASSOCIATED_TRANSMISSION_MODE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.MEDICALLY_ASSOCIATED_TRANSMISSION_MODE,
				EventDto.PARENTERAL_TRANSMISSION_MODE,
				Collections.singletonList(ParenteralTransmissionMode.MEDICALLY_ASSOCIATED),
				true);
		}
		if (isVisibleAllowed(EventDto.EPIDEMIOLOGICAL_EVIDENCE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.EPIDEMIOLOGICAL_EVIDENCE,
				EventDto.DISEASE_TRANSMISSION_MODE,
				Collections.singletonList(DiseaseTransmissionMode.HUMAN_TO_HUMAN),
				true);

			epidemiologicalEvidence.addValueChangeListener(valueChangeEvent -> {
				if (((NullableOptionGroup) valueChangeEvent.getProperty()).getNullableValue() == YesNoUnknown.YES) {
					epidemiologicalEvidenceCheckBoxTree.setVisible(true);
				} else {
					epidemiologicalEvidenceCheckBoxTree.clear();
					epidemiologicalEvidenceCheckBoxTree.setVisible(false);
				}
			});
		}
		if (isVisibleAllowed(EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				EventDto.LABORATORY_DIAGNOSTIC_EVIDENCE,
				EventDto.DISEASE_TRANSMISSION_MODE,
				Collections.singletonList(DiseaseTransmissionMode.HUMAN_TO_HUMAN),
				true);

			laboratoryDiagnosticEvidence.addValueChangeListener(valueChangeEvent -> {
				if (((NullableOptionGroup) valueChangeEvent.getProperty()).getNullableValue() == YesNoUnknown.YES) {
					laboratoryDiagnosticEvidenceDetailCheckBoxTree.setVisible(true);
				} else {
					laboratoryDiagnosticEvidenceDetailCheckBoxTree.clear();
					laboratoryDiagnosticEvidenceDetailCheckBoxTree.setVisible(false);
				}
			});
		}
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.SRC_FIRST_NAME, EventDto.SRC_LAST_NAME, EventDto.SRC_TEL_NO, EventDto.SRC_EMAIL),
			EventDto.SRC_TYPE,
			Arrays.asList(EventSourceType.HOTLINE_PERSON, EventSourceType.INSTITUTIONAL_PARTNER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(EventDto.SRC_MEDIA_WEBSITE, EventDto.SRC_MEDIA_NAME, EventDto.SRC_MEDIA_DETAILS),
			EventDto.SRC_TYPE,
			Collections.singletonList(EventSourceType.MEDIA_NEWS),
			true);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), EventDto.TYPE_OF_PLACE_TEXT, EventDto.TYPE_OF_PLACE, Collections.singletonList(TypeOfPlace.OTHER), true);
		setTypeOfPlaceTextRequirement();
		locationForm.setFacilityFieldsVisible(getField(EventDto.TYPE_OF_PLACE).getValue() == TypeOfPlace.FACILITY, true);
		typeOfPlace.addValueChangeListener(e -> locationForm.setFacilityFieldsVisible(e.getProperty().getValue() == TypeOfPlace.FACILITY, true));

		regionField.addValueChangeListener(e -> {
			RegionReferenceDto region = (RegionReferenceDto) regionField.getValue();
			if (region != null) {
				regionEventResponsibles =
					FacadeProvider.getUserFacade().getUsersByRegionAndRights(region, getValue().getDisease(), UserRight.EVENT_RESPONSIBLE);
			} else {
				regionEventResponsibles.clear();
			}
			addRegionAndDistrict(responsibleUserField);
		});

		districtField.addValueChangeListener(e -> {
			DistrictReferenceDto district = (DistrictReferenceDto) districtField.getValue();
			if (district != null) {
				districtEventResponsibles =
					FacadeProvider.getUserFacade().getUserRefsByDistrict(district, getValue().getDisease(), UserRight.EVENT_RESPONSIBLE);
			} else {
				districtEventResponsibles.clear();
			}
			addRegionAndDistrict(responsibleUserField);
		});

		FieldHelper.addSoftRequiredStyle(
			startDate,
			endDate,
			typeOfPlace,
			meansOfTransport,
			meansOfTransportDetails,
			connectionNumber,
			travelDate,
			responsibleUserField,
			srcType,
			srcInstitutionalPartnerType,
			srcInstitutionalPartnerTypeDetails,
			srcFirstName,
			srcLastName,
			srcTelNo,
			srcMediaWebsite,
			srcMediaName);

		// Make external ID field read-only when SORMAS is connected to a SurvNet instance
		if (StringUtils.isNotEmpty(FacadeProvider.getConfigFacade().getExternalSurveillanceToolGatewayUrl())) {
			setEnabled(false, EventDto.EXTERNAL_ID);
			((TextField) getField(EventDto.EXTERNAL_ID)).setInputPrompt(I18nProperties.getString(Strings.promptExternalIdExternalSurveillanceTool));
		}

		addValueChangeListener((e) -> {
			ValidationUtils.initComponentErrorValidator(
				externalTokenField,
				getValue().getExternalToken(),
				Validations.duplicateExternalToken,
				externalTokenWarningLabel,
				(externalToken) -> FacadeProvider.getEventFacade().doesExternalTokenExist(externalToken, getValue().getUuid()));

			// Initialize specific risk field if disease is null
			if (getValue().getDisease() == null) {
				List<SpecificRisk> specificRiskValues =
					FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.SPECIFIC_EVENT_RISK, null);
				FieldHelper.updateItems(specificRiskField, specificRiskValues);
				specificRiskField.setVisible(isVisibleAllowed(EventDto.SPECIFIC_RISK) && CollectionUtils.isNotEmpty(specificRiskValues));
			}
		});
	}

	private void addRegionAndDistrict(UserField responsibleUserField) {
		List<UserReferenceDto> responsibleUsers = new ArrayList<>();
		responsibleUsers.addAll(regionEventResponsibles);
		responsibleUsers.addAll(districtEventResponsibles);

		FieldHelper.updateItems(responsibleUserField, responsibleUsers);
	}

	private void initEventDateValidation(DateTimeField startDate, DateTimeField endDate, CheckBox multiDayCheckbox) {
		DateComparisonValidator startDateValidator = new DateComparisonValidator(
			startDate,
			endDate,
			true,
			true,
			I18nProperties.getValidationError(Validations.beforeDate, startDate.getCaption(), endDate.getCaption()));

		DateComparisonValidator endDateValidator = new DateComparisonValidator(
			endDate,
			startDate,
			false,
			true,
			I18nProperties.getValidationError(Validations.afterDate, endDate.getCaption(), startDate.getCaption()));

		endDate.removeAllValidators(); // make sure the end date does not come with a future date validator

		multiDayCheckbox.addValueChangeListener(e -> {
			if ((Boolean) e.getProperty().getValue()) {
				startDate.addValidator(startDateValidator);
				endDate.addValidator(endDateValidator);
				DateComparisonValidator.dateFieldDependencyValidationVisibility(startDate, endDate);
			} else {
				startDate.removeValidator(startDateValidator);
				startDate.setValidationVisible(true);
				endDate.removeValidator(endDateValidator);
				endDate.setValidationVisible(true);
			}
		});

	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	public void setTypeOfPlaceTextRequirement() {

		FieldGroup fieldGroup = getFieldGroup();
		ComboBox typeOfPlaceField = (ComboBox) fieldGroup.getField(EventDto.TYPE_OF_PLACE);
		typeOfPlaceField.setImmediate(true);

		TextField typeOfPlaceTextField = (TextField) fieldGroup.getField(EventDto.TYPE_OF_PLACE_TEXT);
		typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER);
		typeOfPlaceField.addValueChangeListener(event -> typeOfPlaceTextField.setRequired(typeOfPlaceField.getValue() == TypeOfPlace.OTHER));
	}

	@Override
	public void setValue(EventDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		if (!isCreateForm && FacadeProvider.getEventFacade().hasAnyEventParticipantWithoutJurisdiction(newFieldValue.getUuid())) {
			locationForm.setHasEventParticipantsWithoutJurisdiction(true);
			locationForm.setFieldsRequirement(true, LocationDto.REGION, LocationDto.DISTRICT);
			locationForm.setCountryDisabledWithHint(I18nProperties.getString(Strings.infoCountryNotEditableEventParticipantsWithoutJurisdiction));
		}

		super.setValue(newFieldValue);

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		locationForm.discard();
	}
}
