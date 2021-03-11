/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package de.symeda.sormas.ui.contact;

import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.LAYOUT_COL_HIDE_INVSIBLE;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import de.symeda.sormas.ui.SormasUI;
import org.joda.time.LocalDate;

import com.google.common.collect.Sets;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.DateRangeValidator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIdentificationSource;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.EndOfQuarantineReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.contact.TracingApp;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ValidationUtils;
import de.symeda.sormas.ui.utils.ViewMode;
import de.symeda.sormas.ui.vaccination.VaccinationInfoForm;

import javax.validation.constraints.NotNull;

public class ContactDataForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String CONTACT_DATA_HEADING_LOC = "contactDataHeadingLoc";
	private static final String FOLLOW_UP_STATUS_HEADING_LOC = "followUpStatusHeadingLoc";
	private static final String TO_CASE_BTN_LOC = "toCaseBtnLoc";
	private static final String CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC = "cancelOrResumeFollowUpBtnLoc";
	private static final String LOST_FOLLOW_UP_BTN_LOC = "lostFollowUpBtnLoc";
	private static final String GENERAL_COMMENT_LOC = "generalCommentLoc";
	private static final String MEDICAL_INFORMATION_LOC = "medicalInformationLoc";
	private static final String EXTERNAL_TOKEN_WARNING_LOC = "externalTokenWarningLoc";

	//@formatter:off
    private static final String HTML_LAYOUT =
            loc(CONTACT_DATA_HEADING_LOC) +
                    fluidRowLocs(ContactDto.CONTACT_CLASSIFICATION, ContactDto.CONTACT_STATUS) +
                    locCss(VSPACE_3, TO_CASE_BTN_LOC) +
					fluidRowLocs(ContactDto.MULTI_DAY_CONTACT) +
					LayoutUtil.fluidRow(
						LayoutUtil.fluidColumnLocCss(LAYOUT_COL_HIDE_INVSIBLE,4,0, ContactDto.FIRST_CONTACT_DATE),
						LayoutUtil.fluidColumnLoc(4, 0, ContactDto.LAST_CONTACT_DATE),
						LayoutUtil.fluidColumnLoc(4, 0, ContactDto.DISEASE)) +
                    fluidRowLocs(ContactDto.DISEASE_DETAILS) +
					fluidRowLocs(ContactDto.UUID) +
					fluidRowLocs(ContactDto.EXTERNAL_ID, ContactDto.EXTERNAL_TOKEN) +
					fluidRowLocs("", EXTERNAL_TOKEN_WARNING_LOC) +
					fluidRowLocs(ContactDto.REPORTING_USER, ContactDto.REPORT_DATE_TIME, ContactDto.REPORTING_DISTRICT) +
                    fluidRowLocs(ContactDto.REGION, ContactDto.DISTRICT, ContactDto.COMMUNITY) +
					fluidRowLocs(ContactDto.RETURNING_TRAVELER, ContactDto.CASE_ID_EXTERNAL_SYSTEM) +
                    loc(ContactDto.CASE_OR_EVENT_INFORMATION) +
					fluidRowLocs(6, ContactDto.CONTACT_IDENTIFICATION_SOURCE, 6, ContactDto.TRACING_APP) +
					fluidRowLocs(6, ContactDto.CONTACT_IDENTIFICATION_SOURCE_DETAILS, 6, ContactDto.TRACING_APP_DETAILS) +
					fluidRowLocs(ContactDto.CONTACT_PROXIMITY) +
                    fluidRowLocs(ContactDto.CONTACT_PROXIMITY_DETAILS) +
                    fluidRowLocs(ContactDto.CONTACT_CATEGORY) +
                    fluidRowLocs(ContactDto.RELATION_TO_CASE) +
                    fluidRowLocs(ContactDto.RELATION_DESCRIPTION) +
                    fluidRowLocs(ContactDto.DESCRIPTION) +
					fluidRowLocs(6, CaseDataDto.PROHIBITION_TO_WORK, 3, CaseDataDto.PROHIBITION_TO_WORK_FROM, 3, CaseDataDto.PROHIBITION_TO_WORK_UNTIL) +
                    fluidRowLocs(4, ContactDto.QUARANTINE_HOME_POSSIBLE, 8, ContactDto.QUARANTINE_HOME_POSSIBLE_COMMENT) +
                    fluidRowLocs(4, ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED, 8, ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT) +
                    fluidRowLocs(6, ContactDto.QUARANTINE, 3, ContactDto.QUARANTINE_FROM, 3, ContactDto.QUARANTINE_TO) +
					fluidRowLocs(ContactDto.QUARANTINE_EXTENDED) +
					fluidRowLocs(ContactDto.QUARANTINE_REDUCED) +
					fluidRowLocs(ContactDto.QUARANTINE_TYPE_DETAILS) +
					fluidRowLocs(ContactDto.QUARANTINE_ORDERED_VERBALLY, ContactDto.QUARANTINE_ORDERED_VERBALLY_DATE) +
					fluidRowLocs(ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE) +
					fluidRowLocs(ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT, ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE) +
                    fluidRowLocs(ContactDto.QUARANTINE_HELP_NEEDED) +
					fluidRowLocs(ContactDto.END_OF_QUARANTINE_REASON, ContactDto.END_OF_QUARANTINE_REASON_DETAILS) +
					locCss(VSPACE_3, ContactDto.HIGH_PRIORITY) +
					fluidRowLocs(ContactDto.HEALTH_CONDITIONS) +
					loc(MEDICAL_INFORMATION_LOC) +
					loc(ContactDto.VACCINATION_INFO) +
					fluidRowLocs(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS) +
                    loc(ContactDto.CARE_FOR_PEOPLE_OVER_60) +
					loc(FOLLOW_UP_STATUS_HEADING_LOC) +
                    fluidRowLocs(ContactDto.FOLLOW_UP_STATUS, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC, LOST_FOLLOW_UP_BTN_LOC) +
                    fluidRowLocs(4, ContactDto.FOLLOW_UP_UNTIL, 8, ContactDto.OVERWRITE_FOLLOW_UP_UTIL) +
                    fluidRowLocs(ContactDto.FOLLOW_UP_COMMENT) +
                    fluidRowLocs(ContactDto.CONTACT_OFFICER, "") + loc(GENERAL_COMMENT_LOC)
                    + fluidRowLocs(ContactDto.ADDITIONAL_DETAILS);
    //@formatter:on

	private final ViewMode viewMode;
	private final Disease disease;
	private NullableOptionGroup contactProximity;
	private Field<?> quarantine;
	private DateField quarantineFrom;
	private DateField quarantineTo;
	private CheckBox quarantineExtended;
	private CheckBox quarantineReduced;
	private CheckBox quarantineOrderedVerbally;
	private CheckBox quarantineOrderedOfficialDocument;
	private ComboBox cbDisease;
	private NullableOptionGroup contactCategory;
	private boolean quarantineChangedByFollowUpUntilChange = false;

	public ContactDataForm(Disease disease, ViewMode viewMode, boolean isPseudonymized) {
		super(
			ContactDto.class,
			ContactDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()).add(new DiseaseFieldVisibilityChecker(disease)),
			UiFieldAccessCheckers.forSensitiveData(isPseudonymized));

		this.viewMode = viewMode;
		this.disease = disease;
		addFields();
	}

	@Override
	protected void addFields() {

		if (viewMode == null) {
			return;
		}

		SormasUI ui = ((SormasUI)getUI());

		Label contactDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingContactData));
		contactDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(contactDataHeadingLabel, CONTACT_DATA_HEADING_LOC);

		Label followUpStausHeadingLabel = new Label(I18nProperties.getString(Strings.headingFollowUpStatus));
		followUpStausHeadingLabel.addStyleName(H3);
		getContent().addComponent(followUpStausHeadingLabel, FOLLOW_UP_STATUS_HEADING_LOC);

		addField(ContactDto.CONTACT_CLASSIFICATION, NullableOptionGroup.class);
		addField(ContactDto.CONTACT_STATUS, NullableOptionGroup.class);
		addField(ContactDto.UUID, TextField.class);
		addField(ContactDto.EXTERNAL_ID, TextField.class);

		TextField externalTokenField = addField(ContactDto.EXTERNAL_TOKEN, TextField.class);
		Label externalTokenWarningLabel = new Label(I18nProperties.getString(Strings.messageContactExternalTokenWarning));
		externalTokenWarningLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		getContent().addComponent(externalTokenWarningLabel, EXTERNAL_TOKEN_WARNING_LOC);

		addField(ContactDto.REPORTING_USER, ComboBox.class);
		CheckBox multiDayContact = addField(ContactDto.MULTI_DAY_CONTACT, CheckBox.class);
		DateField firstContactDate = addDateField(ContactDto.FIRST_CONTACT_DATE, DateField.class, 0);
		DateField lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), ContactDto.FIRST_CONTACT_DATE, ContactDto.MULTI_DAY_CONTACT, Collections.singletonList(true), true);
		initContactDateValidation(firstContactDate, lastContactDate, multiDayContact);

		DateField reportDate = addField(ContactDto.REPORT_DATE_TIME, DateField.class);
		((ComboBox) addField(ContactDto.REPORTING_DISTRICT)).addItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
		addField(ContactDto.CONTACT_IDENTIFICATION_SOURCE, ComboBox.class);
		TextField contactIdentificationSourceDetails = addField(ContactDto.CONTACT_IDENTIFICATION_SOURCE_DETAILS, TextField.class);
		contactIdentificationSourceDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
//		contactIdentificationSourceDetails.setVisible(false);
		ComboBox tracingApp = addField(ContactDto.TRACING_APP, ComboBox.class);
		TextField tracingAppDetails = addField(ContactDto.TRACING_APP_DETAILS, TextField.class);
		tracingAppDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
//		tracingApp.setVisible(false);
//		tracingAppDetails.setVisible(false);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				ContactDto.CONTACT_IDENTIFICATION_SOURCE_DETAILS,
				ContactDto.CONTACT_IDENTIFICATION_SOURCE,
				Arrays.asList(ContactIdentificationSource.OTHER),
				true);
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				ContactDto.TRACING_APP,
				ContactDto.CONTACT_IDENTIFICATION_SOURCE,
				Arrays.asList(ContactIdentificationSource.TRACING_APP),
				true);
			FieldHelper
				.setVisibleWhen(getFieldGroup(), ContactDto.TRACING_APP_DETAILS, ContactDto.TRACING_APP, Arrays.asList(TracingApp.OTHER), true);
		}
		contactProximity = addField(ContactDto.CONTACT_PROXIMITY, NullableOptionGroup.class);
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			addField(ContactDto.CONTACT_PROXIMITY_DETAILS, TextField.class);
			contactCategory = addField(ContactDto.CONTACT_CATEGORY, NullableOptionGroup.class);

			contactProximity.addValueChangeListener(e -> {
				if (getInternalValue().getContactProximity() != e.getProperty().getValue() || contactCategory.isModified()) {
					updateContactCategory((ContactProximity) contactProximity.getNullableValue());
				}
			});
		}
		ComboBox relationToCase = addField(ContactDto.RELATION_TO_CASE, ComboBox.class);
		addField(ContactDto.RELATION_DESCRIPTION, TextField.class);
		cbDisease = addDiseaseField(ContactDto.DISEASE, false);
		cbDisease.setNullSelectionAllowed(false);
		addField(ContactDto.DISEASE_DETAILS, TextField.class);

		addField(ContactDto.PROHIBITION_TO_WORK, NullableOptionGroup.class).addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		DateField prohibitionToWorkFrom = addField(ContactDto.PROHIBITION_TO_WORK_FROM);
		DateField prohibitionToWorkUntil = addDateField(ContactDto.PROHIBITION_TO_WORK_UNTIL, DateField.class, -1);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ContactDto.PROHIBITION_TO_WORK_FROM, ContactDto.PROHIBITION_TO_WORK_UNTIL),
			ContactDto.PROHIBITION_TO_WORK,
			YesNoUnknown.YES,
			true);
		prohibitionToWorkFrom.addValidator(
			new DateComparisonValidator(
				prohibitionToWorkFrom,
				prohibitionToWorkUntil,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, prohibitionToWorkFrom.getCaption(), prohibitionToWorkUntil.getCaption())));
		prohibitionToWorkUntil.addValidator(
			new DateComparisonValidator(
				prohibitionToWorkUntil,
				prohibitionToWorkFrom,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, prohibitionToWorkUntil.getCaption(), prohibitionToWorkFrom.getCaption())));

		quarantine = addField(ContactDto.QUARANTINE);
		quarantine.addValueChangeListener(e -> onQuarantineValueChange());
		quarantineFrom = addField(ContactDto.QUARANTINE_FROM, DateField.class);
		quarantineTo = addDateField(ContactDto.QUARANTINE_TO, DateField.class, -1);

		quarantineFrom.addValidator(
			new DateComparisonValidator(
				quarantineFrom,
				quarantineTo,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, quarantineFrom.getCaption(), quarantineTo.getCaption())));
		quarantineTo.addValidator(
			new DateComparisonValidator(
				quarantineTo,
				quarantineFrom,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, quarantineTo.getCaption(), quarantineFrom.getCaption())));

		quarantineOrderedVerbally = addField(ContactDto.QUARANTINE_ORDERED_VERBALLY, CheckBox.class);
		CssStyles.style(quarantineOrderedVerbally, CssStyles.FORCE_CAPTION);
		addField(ContactDto.QUARANTINE_ORDERED_VERBALLY_DATE, DateField.class);
		quarantineOrderedOfficialDocument = addField(ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, CheckBox.class);
		CssStyles.style(quarantineOrderedOfficialDocument, CssStyles.FORCE_CAPTION);
		addField(ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE, DateField.class);

		CheckBox quarantineOfficialOrderSent = addField(ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT, CheckBox.class);
		CssStyles.style(quarantineOfficialOrderSent, FORCE_CAPTION);
		addField(ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE, DateField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		quarantineExtended = addField(ContactDto.QUARANTINE_EXTENDED, CheckBox.class);
		quarantineExtended.setEnabled(false);
		quarantineExtended.setVisible(false);
		CssStyles.style(quarantineExtended, CssStyles.FORCE_CAPTION);

		quarantineReduced = addField(ContactDto.QUARANTINE_REDUCED, CheckBox.class);
		quarantineReduced.setEnabled(false);
		quarantineReduced.setVisible(false);
		CssStyles.style(quarantineReduced, CssStyles.FORCE_CAPTION);

		TextField quarantineHelpNeeded = addField(ContactDto.QUARANTINE_HELP_NEEDED, TextField.class);
		quarantineHelpNeeded.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
		TextField quarantineTypeDetails = addField(ContactDto.QUARANTINE_TYPE_DETAILS, TextField.class);
		quarantineTypeDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		addField(ContactDto.QUARANTINE_HOME_POSSIBLE, NullableOptionGroup.class);
		addField(ContactDto.QUARANTINE_HOME_POSSIBLE_COMMENT, TextField.class);
		addField(ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED, NullableOptionGroup.class);
		addField(ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT, TextField.class);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ContactDto.QUARANTINE_FROM, ContactDto.QUARANTINE_TO, ContactDto.QUARANTINE_HELP_NEEDED),
			ContactDto.QUARANTINE,
			QuarantineType.QUARANTINE_IN_EFFECT,
			true);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY) || isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(ContactDto.QUARANTINE_ORDERED_VERBALLY, ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
				ContactDto.QUARANTINE,
				QuarantineType.QUARANTINE_IN_EFFECT,
				true);
		}
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_HOME_POSSIBLE_COMMENT,
			ContactDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			ContactDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT,
			ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), ContactDto.QUARANTINE_TYPE_DETAILS, ContactDto.QUARANTINE, Arrays.asList(QuarantineType.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_ORDERED_VERBALLY_DATE,
			ContactDto.QUARANTINE_ORDERED_VERBALLY,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
			ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE,
			ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		addField(ContactDto.DESCRIPTION, TextArea.class).setRows(6);

		addField(ContactDto.RETURNING_TRAVELER, NullableOptionGroup.class);
		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(4);

		addField(ContactDto.FOLLOW_UP_STATUS, ComboBox.class);
		addField(ContactDto.FOLLOW_UP_COMMENT, TextArea.class).setRows(3);
		DateField dfFollowUpUntil = addDateField(ContactDto.FOLLOW_UP_UNTIL, DateField.class, -1);
		dfFollowUpUntil.addValueChangeListener(v -> onFollowUpUntilChanged(v, quarantineTo, quarantineExtended, quarantineReduced));
		quarantineTo.addValueChangeListener(e -> onQuarantineEndChange(e, quarantineExtended, quarantineReduced, dfFollowUpUntil));
		addValueChangeListener(e -> {
			ValidationUtils.initComponentErrorValidator(
				externalTokenField,
				getValue().getExternalToken(),
				Validations.duplicateExternalToken,
				externalTokenWarningLabel,
				(externalToken) -> FacadeProvider.getContactFacade().doesExternalTokenExist(externalToken, getValue().getUuid()));

			onQuarantineValueChange();
		});

		ComboBox contactOfficerField = addField(ContactDto.CONTACT_OFFICER, ComboBox.class);
		contactOfficerField.setNullSelectionAllowed(true);

		ComboBox region = addInfrastructureField(ContactDto.REGION);
		region.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.REGION));
		ComboBox district = addInfrastructureField(ContactDto.DISTRICT);
		district.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.DISTRICT));
		ComboBox community = addInfrastructureField(ContactDto.COMMUNITY);
		community.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.COMMUNITY));
		region.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(district, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		district.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				community,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			if (districtDto == null && getValue().getCaze() != null) {
				CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
				districtDto = caseDto.getDistrict();
			}

			FieldHelper.updateItems(
				contactOfficerField,
				districtDto != null ? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, false, UserRole.CONTACT_OFFICER) : null);
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveAsReference());

		CheckBox cbHighPriority = addField(ContactDto.HIGH_PRIORITY, CheckBox.class);
		addField(ContactDto.OVERWRITE_FOLLOW_UP_UTIL, CheckBox.class);
		NullableOptionGroup ogImmunosuppressiveTherapyBasicDisease =
			addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, NullableOptionGroup.class);
		addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS, TextField.class);
		NullableOptionGroup ogCareForPeopleOver60 = addField(ContactDto.CARE_FOR_PEOPLE_OVER_60, NullableOptionGroup.class);

		cbDisease.addValueChangeListener(e -> updateDiseaseConfiguration((Disease) e.getProperty().getValue()));

		HealthConditionsForm clinicalCourseForm = addField(ContactDto.HEALTH_CONDITIONS, HealthConditionsForm.class);
		clinicalCourseForm.setCaption(null);

		VaccinationInfoForm vaccinationForm = addField(ContactDto.VACCINATION_INFO, VaccinationInfoForm.class);
		if (vaccinationForm.isVisibleAllowed()) {
			Label medicalInformationCaptionLabel = new Label(I18nProperties.getString(Strings.headingMedicalInformation));
			medicalInformationCaptionLabel.addStyleName(H3);
			getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
		}

		Label generalCommentLabel = new Label(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.ADDITIONAL_DETAILS));
		generalCommentLabel.addStyleName(H3);
		getContent().addComponent(generalCommentLabel, GENERAL_COMMENT_LOC);

		TextArea additionalDetails = addField(ContactDto.ADDITIONAL_DETAILS, TextArea.class);
		additionalDetails.setRows(6);
		additionalDetails.setDescription(
			I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		CssStyles.style(additionalDetails, CssStyles.CAPTION_HIDDEN);

		addFields(ContactDto.END_OF_QUARANTINE_REASON, ContactDto.END_OF_QUARANTINE_REASON_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.END_OF_QUARANTINE_REASON_DETAILS,
			ContactDto.END_OF_QUARANTINE_REASON,
			Collections.singletonList(EndOfQuarantineReason.OTHER),
			true);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setReadOnly(true, ContactDto.UUID, ContactDto.REPORTING_USER, ContactDto.CONTACT_STATUS, ContactDto.FOLLOW_UP_STATUS);

		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ContactDto.FOLLOW_UP_STATUS,
			Arrays.asList(ContactDto.FOLLOW_UP_COMMENT),
			Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.LOST));
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.RELATION_DESCRIPTION,
			ContactDto.RELATION_TO_CASE,
			Arrays.asList(ContactRelation.OTHER),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS,
			ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(getFieldGroup(), ContactDto.DISEASE_DETAILS, ContactDto.DISEASE, Arrays.asList(Disease.OTHER), true);
		FieldHelper.setRequiredWhen(getFieldGroup(), ContactDto.DISEASE, Arrays.asList(ContactDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		FieldHelper.setReadOnlyWhen(
			getFieldGroup(),
			Arrays.asList(ContactDto.FOLLOW_UP_UNTIL),
			ContactDto.OVERWRITE_FOLLOW_UP_UTIL,
			Arrays.asList(Boolean.FALSE),
			false,
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ContactDto.OVERWRITE_FOLLOW_UP_UTIL,
			Arrays.asList(ContactDto.FOLLOW_UP_UNTIL),
			Arrays.asList(Boolean.TRUE));

		initializeVisibilitiesAndAllowedVisibilities();

		addValueChangeListener(e -> {
			if (getValue() != null) {
				CaseDataDto caseDto = null;

				if (getValue().getCaze() != null) {
					setVisible(false, ContactDto.DISEASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION);
					caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());
				} else {
					setRequired(true, ContactDto.DISEASE, ContactDto.REGION, ContactDto.DISTRICT);
				}

				updateLastContactDateValidator();
				updateDiseaseConfiguration(getValue().getDisease());
				updateFollowUpStatusComponents(ui);

				DistrictReferenceDto referenceDistrict =
					getValue().getDistrict() != null ? getValue().getDistrict() : caseDto != null ? caseDto.getDistrict() : null;
				if (referenceDistrict != null) {
					contactOfficerField
						.addItems(FacadeProvider.getUserFacade().getUserRefsByDistrict(referenceDistrict, false, UserRole.CONTACT_OFFICER));
				}

				getContent().removeComponent(TO_CASE_BTN_LOC);
				if (getValue().getResultingCase() != null) {
					// link to case
					Link linkToData = ControllerProvider.getCaseController()
						.createLinkToData(getValue().getResultingCase().getUuid(), I18nProperties.getCaption(Captions.contactOpenContactCase));
					getContent().addComponent(linkToData, TO_CASE_BTN_LOC);
				} else if (!ContactClassification.NO_CONTACT.equals(getValue().getContactClassification())) {
					if (ui.getUserProvider().hasUserRight(UserRight.CONTACT_CONVERT)) {
						Button toCaseButton = ButtonHelper.createButton(Captions.contactCreateContactCase, event -> {
							if (!ContactClassification.CONFIRMED.equals(getValue().getContactClassification())) {
								VaadinUiUtil.showSimplePopupWindow(
									I18nProperties.getString(Strings.headingContactConfirmationRequired),
									I18nProperties.getString(Strings.messageContactToCaseConfirmationRequired));
							} else {
								ControllerProvider.getCaseController().createFromContact(ui, getValue());
							}
						}, ValoTheme.BUTTON_LINK);

						getContent().addComponent(toCaseButton, TO_CASE_BTN_LOC);
					}
				}

				if (!isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
					setVisible(
						false,
						ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE,
						ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS,
						ContactDto.CARE_FOR_PEOPLE_OVER_60);
				} else {
					ogImmunosuppressiveTherapyBasicDisease.addValueChangeListener(getHighPriorityValueChangeListener(cbHighPriority));
					ogCareForPeopleOver60.addValueChangeListener(getHighPriorityValueChangeListener(cbHighPriority));
				}

				// Add follow-up until validator
				Date minimumFollowUpUntilDate = DateHelper.addDays(
					ContactLogic.getStartDate(lastContactDate.getValue(), reportDate.getValue()),
					FacadeProvider.getDiseaseConfigurationFacade().getFollowUpDuration((Disease) cbDisease.getValue()));
				dfFollowUpUntil.addValidator(
					new DateRangeValidator(
						I18nProperties.getValidationError(Validations.contactFollowUpUntilDate),
						minimumFollowUpUntilDate,
						null,
						Resolution.DAY));
			}

			// Overwrite visibility for quarantine fields
			if (!isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY) && !isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
				setVisible(
					false,
					CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
					CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
					CaseDataDto.QUARANTINE_ORDERED_VERBALLY,
					CaseDataDto.QUARANTINE_ORDERED_VERBALLY_DATE,
					CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT,
					CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE);
			}
		});

		setRequired(true, ContactDto.CONTACT_CLASSIFICATION, ContactDto.CONTACT_STATUS, ContactDto.REPORT_DATE_TIME);
		FieldHelper.addSoftRequiredStyle(firstContactDate, lastContactDate, contactProximity, relationToCase);
	}

	/*
	 * Only used for Systems in Germany. Follows specific rules for german systems.
	 */
	private void updateContactCategory(ContactProximity proximity) {
		if (proximity != null) {
			switch (proximity) {
			case FACE_TO_FACE_LONG:
			case TOUCHED_FLUID:
			case AEROSOL:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.HIGH_RISK));
				break;
			case MEDICAL_UNSAFE:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.HIGH_RISK_MED));
				break;
			case MEDICAL_LIMITED:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.MEDIUM_RISK_MED));
				break;
			case SAME_ROOM:
			case FACE_TO_FACE_SHORT:
			case MEDICAL_SAME_ROOM:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.LOW_RISK));
				break;
			case MEDICAL_DISTANT:
			case MEDICAL_SAFE:
				contactCategory.setValue(Sets.newHashSet(ContactCategory.NO_RISK));
				break;
			default:
			}
		}
	}

	private ValueChangeListener getHighPriorityValueChangeListener(CheckBox cbHighPriority) {
		return e -> {
			if (YesNoUnknown.YES.equals(FieldHelper.getNullableSourceFieldValue((Field) e.getProperty()))) {
				cbHighPriority.setValue(true);
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void updateFollowUpStatusComponents(@NotNull final SormasUI ui) {

		getContent().removeComponent(CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
		getContent().removeComponent(LOST_FOLLOW_UP_BTN_LOC);

		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
		boolean followUpVisible = getValue() != null && statusField.isVisible();
		if (followUpVisible && ui.getUserProvider().hasUserRight(UserRight.CONTACT_EDIT)) {
			FollowUpStatus followUpStatus = statusField.getValue();
			if (followUpStatus == FollowUpStatus.FOLLOW_UP) {

				Button cancelButton = ButtonHelper.createButton(Captions.contactCancelFollowUp, event -> {
					Field<FollowUpStatus> statusField1 = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
					statusField1.setReadOnly(false);
					statusField1.setValue(FollowUpStatus.CANCELED);
					statusField1.setReadOnly(true);
					updateFollowUpStatusComponents(ui);
				});
				cancelButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(cancelButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);

				Button lostButton = ButtonHelper.createButton(Captions.contactLostToFollowUp, event -> {
					Field<FollowUpStatus> statusField12 = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
					statusField12.setReadOnly(false);
					statusField12.setValue(FollowUpStatus.LOST);
					statusField12.setReadOnly(true);
					updateFollowUpStatusComponents(ui);
				});
				lostButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(lostButton, LOST_FOLLOW_UP_BTN_LOC);

			} else if (followUpStatus == FollowUpStatus.CANCELED || followUpStatus == FollowUpStatus.LOST) {

				Button resumeButton = ButtonHelper.createButton(Captions.contactResumeFollowUp, event -> {
					Field<FollowUpStatus> statusField13 = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
					statusField13.setReadOnly(false);
					statusField13.setValue(FollowUpStatus.FOLLOW_UP);
					statusField13.setReadOnly(true);
					updateFollowUpStatusComponents(ui);
				}, CssStyles.FORCE_CAPTION);
				resumeButton.setWidth(100, Unit.PERCENTAGE);

				getContent().addComponent(resumeButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
			}
		}
	}

	protected void updateLastContactDateValidator() {

		Field<?> dateField = getField(ContactDto.LAST_CONTACT_DATE);
		for (Validator validator : dateField.getValidators()) {
			if (validator instanceof DateRangeValidator) {
				dateField.removeValidator(validator);
			}
		}
		if (getValue() != null) {
			dateField.addValidator(
				new DateRangeValidator(
					I18nProperties
						.getValidationError(Validations.beforeDate, dateField.getCaption(), getField(ContactDto.REPORT_DATE_TIME).getCaption()),
					null,
					new LocalDate(getValue().getReportDateTime()).plusDays(1).toDate(),
					Resolution.SECOND));
		}
	}

	private void updateDiseaseConfiguration(Disease disease) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(ContactDto.class, (String) propertyId, disease);
			getFieldGroup().getField(propertyId).setVisible(visible && getFieldGroup().getField(propertyId).isVisible());
		}

		FieldHelper.updateEnumData(
			contactProximity,
			Arrays.asList(ContactProximity.getValues(disease, FacadeProvider.getConfigFacade().getCountryLocale())));
	}

	public Disease getSelectedDisease() {
		if (getValue().getCaze() != null) {
			return getValue().getDisease();
		} else {
			return (Disease) cbDisease.getValue();
		}
	}

	public void setSourceCase(CaseIndexDto caze) {
		if (caze != null) {
			getValue().setCaze(caze.toReference());
			updateFieldVisibilitiesByCase(true);
		} else {
			getValue().setCaze(null);
			updateFieldVisibilitiesByCase(false);
		}
	}

	private void updateFieldVisibilitiesByCase(boolean caseSelected) {
		setVisible(!caseSelected, ContactDto.DISEASE, ContactDto.CASE_ID_EXTERNAL_SYSTEM, ContactDto.CASE_OR_EVENT_INFORMATION);
		setRequired(!caseSelected, ContactDto.DISEASE, ContactDto.REGION, ContactDto.DISTRICT);
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	private void onFollowUpUntilChanged(
		Property.ValueChangeEvent valueChangeEvent,
		DateField quarantineTo,
		CheckBox quarantineExtendedCheckBox,
		CheckBox quarantineReducedCheckBox) {
		Property<Date> followUpUntilField = valueChangeEvent.getProperty();
		Date newFollowUpUntil = followUpUntilField.getValue();
		ContactDto originalContact = getInternalValue();
		Date oldFollowUpUntil = originalContact.getFollowUpUntil();
		Date oldQuarantineEnd = originalContact.getQuarantineTo();
		if (shouldAdjustQuarantine(quarantineTo, newFollowUpUntil, oldFollowUpUntil)) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingAdjustQuarantine),
				new Label(I18nProperties.getString(Strings.confirmationAlsoAdjustQuarantine)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						quarantineChangedByFollowUpUntilChange = true;
						quarantineTo.setValue(newFollowUpUntil);
						if (oldQuarantineEnd != null) {
							boolean quarantineExtended = quarantineTo.getValue().after(oldQuarantineEnd);
							quarantineExtendedCheckBox.setValue(quarantineExtended);
							quarantineReducedCheckBox.setValue(!quarantineExtended);
							setVisible(quarantineExtended, ContactDto.QUARANTINE_EXTENDED);
							setVisible(!quarantineExtended, ContactDto.QUARANTINE_REDUCED);
						}
					}
				});
		}
	}

	private boolean shouldAdjustQuarantine(DateField quarantineTo, Date newFollowUpUntil, Date oldFollowUpUntil) {
		return newFollowUpUntil != null
			&& (oldFollowUpUntil == null || newFollowUpUntil.after(oldFollowUpUntil))
			&& quarantineTo.getValue() != null
			&& newFollowUpUntil.compareTo(quarantineTo.getValue()) != 0;
	}

	private void onQuarantineEndChange(
		Property.ValueChangeEvent valueChangeEvent,
		CheckBox quarantineExtendedCheckBox,
		CheckBox quarantineReducedCheckBox,
		DateField followUpUntilField) {
		if (quarantineChangedByFollowUpUntilChange) {
			quarantineChangedByFollowUpUntilChange = false;
		} else {
			Property<Date> quarantineEndField = valueChangeEvent.getProperty();
			Date newQuarantineEnd = quarantineEndField.getValue();
			ContactDto originalContact = getInternalValue();
			Date oldQuarantineEnd = originalContact.getQuarantineTo();
			if (newQuarantineEnd != null) {
				if (oldQuarantineEnd != null) {
					if (newQuarantineEnd.after(oldQuarantineEnd)) {
						confirmQuarantineEndExtended(
							quarantineExtendedCheckBox,
							quarantineReducedCheckBox,
							quarantineEndField,
							originalContact,
							oldQuarantineEnd,
							followUpUntilField);
					} else if (newQuarantineEnd.before(oldQuarantineEnd)) {
						confirmQuarantineEndReduced(quarantineExtendedCheckBox, quarantineReducedCheckBox, quarantineEndField, oldQuarantineEnd);
					}
				}
			} else if (!originalContact.isQuarantineExtended() && !originalContact.isQuarantineReduced()) {
				setVisible(false, quarantineExtendedCheckBox.getId(), quarantineReducedCheckBox.getId());
				quarantineExtendedCheckBox.setValue(false);
				quarantineReducedCheckBox.setValue(false);
			}
		}
	}

	private void confirmQuarantineEndExtended(
		CheckBox quarantineExtendedCheckbox,
		CheckBox quarantineReducedCheckbox,
		Property<Date> quarantineEndField,
		ContactDto originalContact,
		Date oldQuarantineEnd,
		DateField followUpUntil) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingExtendQuarantine),
			new Label(I18nProperties.getString(Strings.confirmationExtendQuarantine)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					quarantineExtendedCheckbox.setValue(true);
					quarantineReducedCheckbox.setValue(false);
					setVisible(true, ContactDto.QUARANTINE_EXTENDED);
					setVisible(false, ContactDto.QUARANTINE_REDUCED);
					if (originalContact.getFollowUpUntil() != null) {
						confirmExtendFollowUpPeriod(originalContact, quarantineEndField.getValue(), followUpUntil);
					}
				} else {
					quarantineEndField.setValue(oldQuarantineEnd);
				}
			});
	}

	private void confirmExtendFollowUpPeriod(ContactDto originalContact, Date quarantineEnd, DateField followUpUntil) {
		if (quarantineEnd.after(originalContact.getFollowUpUntil())) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingExtendFollowUp),
				new Label(I18nProperties.getString(Strings.confirmationExtendFollowUp)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						if (followUpUntil.isReadOnly()) {
							followUpUntil.setReadOnly(false);
							followUpUntil.setValue(quarantineEnd);
							followUpUntil.setReadOnly(true);
						} else {
							followUpUntil.setValue(quarantineEnd);
						}
					}
				});
		}
	}

	private void confirmQuarantineEndReduced(
		CheckBox quarantineExtendedCheckbox,
		CheckBox quarantineReducedCheckbox,
		Property<Date> quarantineEndField,
		Date oldQuarantineEnd) {
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(Strings.headingReduceQuarantine),
			new Label(I18nProperties.getString(Strings.confirmationReduceQuarantine)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				if (confirmed) {
					quarantineExtendedCheckbox.setValue(false);
					quarantineReducedCheckbox.setValue(true);
					setVisible(false, ContactDto.QUARANTINE_EXTENDED);
					setVisible(true, ContactDto.QUARANTINE_REDUCED);
				} else {
					quarantineEndField.setValue(oldQuarantineEnd);
				}
			});
	}

	private void onQuarantineValueChange() {
		QuarantineType quarantineType = (QuarantineType) quarantine.getValue();
		if (QuarantineType.isQuarantineInEffect(quarantineType)) {
			ContactDto contact = this.getInternalValue();
			if (contact != null) {
				quarantineFrom.setValue(contact.getQuarantineFrom());
				if (contact.getQuarantineTo() == null) {
					quarantineTo.setValue(contact.getFollowUpUntil());
				} else {
					quarantineTo.setValue(contact.getQuarantineTo());
				}
				if (contact.isQuarantineExtended()) {
					quarantineExtended.setValue(true);
					setVisible(true, CaseDataDto.QUARANTINE_EXTENDED);
				}
				if (contact.isQuarantineReduced()) {
					quarantineReduced.setValue(true);
					setVisible(true, CaseDataDto.QUARANTINE_REDUCED);
				}
			}
		} else {
			quarantineFrom.clear();
			quarantineTo.clear();
			quarantineExtended.setValue(false);
			quarantineReduced.setValue(false);
			setVisible(false, CaseDataDto.QUARANTINE_REDUCED, CaseDataDto.QUARANTINE_EXTENDED);
		}
	}

	private void initContactDateValidation(DateField startDate, DateField endDate, CheckBox multiDayCheckbox) {
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

		startDate.addValueChangeListener(event -> endDate.setRequired(event.getProperty().getValue() != null));

		multiDayCheckbox.addValueChangeListener(e -> {
			if ((Boolean) e.getProperty().getValue()) {
				startDate.addValidator(startDateValidator);
				endDate.addValidator(endDateValidator);
			} else {
				startDate.removeValidator(startDateValidator);
				endDate.removeValidator(endDateValidator);
			}
		});
	}
}
