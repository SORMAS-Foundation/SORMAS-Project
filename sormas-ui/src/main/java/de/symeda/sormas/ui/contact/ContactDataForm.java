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

import com.google.common.collect.Sets;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter;
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
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCategory;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIdentificationSource;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.ContactProximity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.ContactRelation;
import de.symeda.sormas.api.contact.EndOfQuarantineReason;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.contact.TracingApp;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.Diseases.DiseasesConfiguration;
import de.symeda.sormas.api.utils.ExtendedReduced;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.LayoutUtil;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ValidationUtils;
import de.symeda.sormas.ui.utils.ViewMode;

public class ContactDataForm extends AbstractEditForm<ContactDto> {

	private static final long serialVersionUID = 1L;

	private static final String CONTACT_DATA_HEADING_LOC = "contactDataHeadingLoc";
	private static final String FOLLOW_UP_STATUS_HEADING_LOC = "followUpStatusHeadingLoc";
	protected static final String TO_CASE_BTN_LOC = "toCaseBtnLoc";
	private static final String CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC = "cancelOrResumeFollowUpBtnLoc";
	private static final String LOST_FOLLOW_UP_BTN_LOC = "lostFollowUpBtnLoc";
	private static final String GENERAL_COMMENT_LOC = "generalCommentLoc";
	private static final String EXTERNAL_TOKEN_WARNING_LOC = "externalTokenWarningLoc";
	private static final String EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC = "expectedFollowUpUntilDateLoc";

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
					fluidRowLocs(ContactDto.INTERNAL_TOKEN, EXTERNAL_TOKEN_WARNING_LOC) +
					fluidRowLocs(3, ContactDto.REPORTING_USER, 4, ContactDto.REPORT_DATE_TIME, 4,ContactDto.REPORTING_DISTRICT, 1, "") +
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
					fluidRowLocs(6, ContactDto.PROHIBITION_TO_WORK, 3, ContactDto.PROHIBITION_TO_WORK_FROM, 3, ContactDto.PROHIBITION_TO_WORK_UNTIL) +
                    fluidRowLocs(4, ContactDto.QUARANTINE_HOME_POSSIBLE, 8, ContactDto.QUARANTINE_HOME_POSSIBLE_COMMENT) +
                    fluidRowLocs(4, ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED, 8, ContactDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT) +
                    fluidRowLocs(6, ContactDto.QUARANTINE, 3, ContactDto.QUARANTINE_FROM, 3, ContactDto.QUARANTINE_TO) +
					fluidRowLocs(9, ContactDto.QUARANTINE_CHANGE_COMMENT, 3, ContactDto.PREVIOUS_QUARANTINE_TO) +
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
					fluidRowLocs(ContactDto.VACCINATION_STATUS, "") +
					fluidRowLocs(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS) +
                    loc(ContactDto.CARE_FOR_PEOPLE_OVER_60) +
					loc(FOLLOW_UP_STATUS_HEADING_LOC) +
                    fluidRowLocs(ContactDto.FOLLOW_UP_STATUS, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC, LOST_FOLLOW_UP_BTN_LOC) +
					fluidRowLocs(ContactDto.FOLLOW_UP_STATUS_CHANGE_DATE, ContactDto.FOLLOW_UP_STATUS_CHANGE_USER) +
                    fluidRowLocs(ContactDto.FOLLOW_UP_UNTIL, EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC, ContactDto.OVERWRITE_FOLLOW_UP_UNTIL) +
                    fluidRowLocs(ContactDto.FOLLOW_UP_COMMENT) +
                    fluidRowLocs(ContactDto.CONTACT_OFFICER, "") + loc(GENERAL_COMMENT_LOC)
                    + fluidRowLocs(ContactDto.ADDITIONAL_DETAILS) +
					fluidRowLocs(CaseDataDto.DELETION_REASON) +
					fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);;
    //@formatter:on

	private final ViewMode viewMode;
	private final Disease disease;
	private NullableOptionGroup contactProximity;
	private ComboBox region;
	private ComboBox district;
	private ComboBox community;
	private UserField contactOfficerField;
	private Field<?> quarantine;
	private DateField quarantineFrom;
	private DateField dfQuarantineTo;
	private TextField quarantineChangeComment;
	private DateField dfPreviousQuarantineTo;
	private CheckBox cbQuarantineExtended;
	private CheckBox cbQuarantineReduced;
	private CheckBox quarantineOrderedVerbally;
	private CheckBox quarantineOrderedOfficialDocument;
	private ComboBox cbDisease;
	private NullableOptionGroup contactCategory;
	private boolean quarantineChangedByFollowUpUntilChange = false;
	private TextField tfExpectedFollowUpUntilDate;
	private CheckBox cbOverwriteFollowUpUntil;
	private DateField dfFollowUpUntil;
	private FollowUpPeriodDto expectedFollowUpPeriodDto;
	private CheckBox multiDayContact;
	private DateField firstContactDate;
	private DateField lastContactDate;
	private DateField reportDate;
	private Button toCaseButton;

	public ContactDataForm(Disease disease, ViewMode viewMode, boolean isPseudonymized, boolean inJurisdiction) {
		super(
			ContactDto.class,
			ContactDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized));

		this.viewMode = viewMode;
		this.disease = disease;
		addFields();
	}

	public Button getToCaseButton() {
		return toCaseButton;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		if (viewMode == null) {
			return;
		}

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

		addField(ContactDto.INTERNAL_TOKEN, TextField.class);
		addField(ContactDto.REPORTING_USER, UserField.class);
		multiDayContact = addField(ContactDto.MULTI_DAY_CONTACT, CheckBox.class);
		firstContactDate = addDateField(ContactDto.FIRST_CONTACT_DATE, DateField.class, 0);
		lastContactDate = addField(ContactDto.LAST_CONTACT_DATE, DateField.class);
		reportDate = addField(ContactDto.REPORT_DATE_TIME, DateField.class);

		DateComparisonValidator.dateFieldDependencyValidationVisibility(firstContactDate, lastContactDate, reportDate);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), ContactDto.FIRST_CONTACT_DATE, ContactDto.MULTI_DAY_CONTACT, Collections.singletonList(true), true);
		initContactDateValidation();

		addInfrastructureField(ContactDto.REPORTING_DISTRICT).addItems(FacadeProvider.getDistrictFacade().getAllActiveAsReference());
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
		contactProximity.setCaption(I18nProperties.getCaption(Captions.Contact_contactProximityLongForm));
		contactProximity.removeStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		addField(ContactDto.CONTACT_PROXIMITY_DETAILS, TextField.class);
		contactCategory = addField(ContactDto.CONTACT_CATEGORY, NullableOptionGroup.class);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
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
		dfQuarantineTo = addDateField(ContactDto.QUARANTINE_TO, DateField.class, -1);

		quarantineFrom.addValidator(
			new DateComparisonValidator(
				quarantineFrom,
				dfQuarantineTo,
				true,
				false,
				I18nProperties.getValidationError(Validations.beforeDate, quarantineFrom.getCaption(), dfQuarantineTo.getCaption())));
		dfQuarantineTo.addValidator(
			new DateComparisonValidator(
				dfQuarantineTo,
				quarantineFrom,
				false,
				false,
				I18nProperties.getValidationError(Validations.afterDate, dfQuarantineTo.getCaption(), quarantineFrom.getCaption())));

		quarantineChangeComment = addField(ContactDto.QUARANTINE_CHANGE_COMMENT);
		dfPreviousQuarantineTo = addDateField(ContactDto.PREVIOUS_QUARANTINE_TO, DateField.class, -1);
		setReadOnly(true, ContactDto.PREVIOUS_QUARANTINE_TO);
		setVisible(false, ContactDto.QUARANTINE_CHANGE_COMMENT, ContactDto.PREVIOUS_QUARANTINE_TO);

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

		cbQuarantineExtended = addField(ContactDto.QUARANTINE_EXTENDED, CheckBox.class);
		cbQuarantineExtended.setEnabled(false);
		cbQuarantineExtended.setVisible(false);
		CssStyles.style(cbQuarantineExtended, CssStyles.FORCE_CAPTION);

		cbQuarantineReduced = addField(ContactDto.QUARANTINE_REDUCED, CheckBox.class);
		cbQuarantineReduced.setEnabled(false);
		cbQuarantineReduced.setVisible(false);
		CssStyles.style(cbQuarantineReduced, CssStyles.FORCE_CAPTION);

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
		addField(ContactDto.VACCINATION_STATUS);
		addField(ContactDto.RETURNING_TRAVELER, NullableOptionGroup.class);
		addField(ContactDto.CASE_ID_EXTERNAL_SYSTEM, TextField.class);
		addField(ContactDto.CASE_OR_EVENT_INFORMATION, TextArea.class).setRows(4);

		addField(ContactDto.FOLLOW_UP_STATUS, ComboBox.class);
		addField(ContactDto.FOLLOW_UP_STATUS_CHANGE_DATE);
		addField(ContactDto.FOLLOW_UP_STATUS_CHANGE_USER);
		addField(ContactDto.FOLLOW_UP_COMMENT, TextArea.class).setRows(3);
		dfFollowUpUntil = addDateField(ContactDto.FOLLOW_UP_UNTIL, DateField.class, -1);
		dfFollowUpUntil.addValueChangeListener(v -> onFollowUpUntilChanged(v, dfQuarantineTo, cbQuarantineExtended, cbQuarantineReduced));
		cbOverwriteFollowUpUntil = addField(ContactDto.OVERWRITE_FOLLOW_UP_UNTIL, CheckBox.class);
		cbOverwriteFollowUpUntil.addValueChangeListener(e -> {
			updateOverwriteFollowUpUntil();
		});
		dfQuarantineTo.addValueChangeListener(e -> onQuarantineEndChange());
		addValueChangeListener(e -> {
			ValidationUtils.initComponentErrorValidator(
				externalTokenField,
				getValue().getExternalToken(),
				Validations.duplicateExternalToken,
				externalTokenWarningLabel,
				(externalToken) -> FacadeProvider.getContactFacade().doesExternalTokenExist(externalToken, getValue().getUuid()));

			onQuarantineValueChange();
		});

		contactOfficerField = addField(ContactDto.CONTACT_OFFICER, UserField.class);
		contactOfficerField.setEnabled(true);

		region = addInfrastructureField(ContactDto.REGION);
		region.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.REGION));
		district = addInfrastructureField(ContactDto.DISTRICT);
		district.setDescription(I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.DISTRICT));
		community = addInfrastructureField(ContactDto.COMMUNITY);
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

			updateContactOfficers();
		});
		region.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		CheckBox cbHighPriority = addField(ContactDto.HIGH_PRIORITY, CheckBox.class);
		tfExpectedFollowUpUntilDate = new TextField();
		tfExpectedFollowUpUntilDate.setCaption(I18nProperties.getCaption(Captions.Contact_expectedFollowUpUntil));
		getContent().addComponent(tfExpectedFollowUpUntilDate, EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC);

		NullableOptionGroup ogImmunosuppressiveTherapyBasicDisease =
			addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE, NullableOptionGroup.class);
		addField(ContactDto.IMMUNOSUPPRESSIVE_THERAPY_BASIC_DISEASE_DETAILS, TextField.class);
		NullableOptionGroup ogCareForPeopleOver60 = addField(ContactDto.CARE_FOR_PEOPLE_OVER_60, NullableOptionGroup.class);

		cbDisease.addValueChangeListener(e -> updateDiseaseConfiguration((Disease) e.getProperty().getValue()));

		HealthConditionsForm clinicalCourseForm = addField(ContactDto.HEALTH_CONDITIONS, HealthConditionsForm.class);
		clinicalCourseForm.setCaption(null);

		Label generalCommentLabel = new Label(I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.ADDITIONAL_DETAILS));
		generalCommentLabel.addStyleName(H3);
		getContent().addComponent(generalCommentLabel, GENERAL_COMMENT_LOC);

		TextArea additionalDetails = addField(ContactDto.ADDITIONAL_DETAILS, TextArea.class);
		additionalDetails.setRows(6);
		additionalDetails.setDescription(
			I18nProperties.getPrefixDescription(ContactDto.I18N_PREFIX, ContactDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		CssStyles.style(additionalDetails, CssStyles.CAPTION_HIDDEN);

		addField(ContactDto.DELETION_REASON);
		addField(ContactDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, ContactDto.DELETION_REASON, ContactDto.OTHER_DELETION_REASON);

		addFields(ContactDto.END_OF_QUARANTINE_REASON, ContactDto.END_OF_QUARANTINE_REASON_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			ContactDto.END_OF_QUARANTINE_REASON_DETAILS,
			ContactDto.END_OF_QUARANTINE_REASON,
			Collections.singletonList(EndOfQuarantineReason.OTHER),
			true);

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		setReadOnly(
			true,
			ContactDto.UUID,
			ContactDto.REPORTING_USER,
			ContactDto.CONTACT_STATUS,
			ContactDto.FOLLOW_UP_STATUS,
			ContactDto.FOLLOW_UP_STATUS_CHANGE_DATE,
			ContactDto.FOLLOW_UP_STATUS_CHANGE_USER);

		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ContactDto.FOLLOW_UP_STATUS,
			Arrays.asList(ContactDto.FOLLOW_UP_COMMENT),
			Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.LOST));
		FieldHelper.setVisibleWhenSourceNotNull(
			getFieldGroup(),
			Arrays.asList(ContactDto.FOLLOW_UP_STATUS_CHANGE_DATE, ContactDto.FOLLOW_UP_STATUS_CHANGE_USER),
			ContactDto.FOLLOW_UP_STATUS_CHANGE_DATE,
			true);
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
			ContactDto.OVERWRITE_FOLLOW_UP_UNTIL,
			Arrays.asList(Boolean.FALSE),
			false,
			true);
		FieldHelper.setRequiredWhen(
			getFieldGroup(),
			ContactDto.OVERWRITE_FOLLOW_UP_UNTIL,
			Arrays.asList(ContactDto.FOLLOW_UP_UNTIL),
			Arrays.asList(Boolean.TRUE));
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(ContactDto.FOLLOW_UP_UNTIL, ContactDto.OVERWRITE_FOLLOW_UP_UNTIL),
			ContactDto.FOLLOW_UP_STATUS,
			Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.COMPLETED, FollowUpStatus.FOLLOW_UP, FollowUpStatus.LOST),
			true);

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

				updateDateComparison();
				updateDiseaseConfiguration(getValue().getDisease());
				updateFollowUpStatusComponents();

				DistrictReferenceDto referenceDistrict =
					getValue().getDistrict() != null ? getValue().getDistrict() : caseDto != null ? caseDto.getDistrict() : null;
				if (referenceDistrict != null) {
					contactOfficerField.addItems(
						FacadeProvider.getUserFacade().getUserRefsByDistrict(referenceDistrict, getSelectedDisease(), UserRight.CONTACT_RESPONSIBLE));
				}

				getContent().removeComponent(TO_CASE_BTN_LOC);
				if (getValue().getResultingCase() != null && UiUtil.permitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_VIEW)) {
					// link to case
					Link linkToData = ControllerProvider.getCaseController()
						.createLinkToData(getValue().getResultingCase().getUuid(), I18nProperties.getCaption(Captions.contactOpenContactCase));
					getContent().addComponent(linkToData, TO_CASE_BTN_LOC);
				} else if (!ContactClassification.NO_CONTACT.equals(getValue().getContactClassification())) {
					if (UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_CONVERT)
						&& FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_SURVEILANCE)) {
						toCaseButton = ButtonHelper.createButton(Captions.contactCreateContactCase);
						toCaseButton.addStyleName(ValoTheme.BUTTON_LINK);
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
				FollowUpPeriodDto followUpPeriod = ContactLogic.getFollowUpStartDate(
					lastContactDate.getValue(),
					reportDate.getValue(),
					FacadeProvider.getSampleFacade().getEarliestPositiveSampleDate(getValue().getUuid()));
				Date minimumFollowUpUntilDate = FollowUpLogic
					.calculateFollowUpUntilDate(
						followUpPeriod,
						null,
						FacadeProvider.getVisitFacade().getVisitsByContact(new ContactReferenceDto(getValue().getUuid())),
						FacadeProvider.getDiseaseConfigurationFacade().getFollowUpDuration(getSelectedDisease()),
						FacadeProvider.getFeatureConfigurationFacade()
							.isPropertyValueTrue(FeatureType.CONTACT_TRACING, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE))
					.getFollowUpEndDate();

				if (FacadeProvider.getFeatureConfigurationFacade()
					.isPropertyValueTrue(FeatureType.CONTACT_TRACING, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE)) {
					dfFollowUpUntil.addValueChangeListener(valueChangeEvent -> {

						if (DateHelper.getEndOfDay(dfFollowUpUntil.getValue()).before(minimumFollowUpUntilDate)) {
							dfFollowUpUntil.setComponentError(new ErrorMessage() {

								@Override
								public ErrorLevel getErrorLevel() {
									return ErrorLevel.INFO;
								}

								@Override
								public String getFormattedHtmlMessage() {
									return I18nProperties.getValidationError(
										Validations.contactFollowUpUntilDateSoftValidation,
										I18nProperties.getPrefixCaption(ContactDto.I18N_PREFIX, ContactDto.FOLLOW_UP_UNTIL));
								}
							});
						}
					});
				} else {
					dfFollowUpUntil.addValidator(
						new DateRangeValidator(
							I18nProperties.getValidationError(Validations.contactFollowUpUntilDate),
							minimumFollowUpUntilDate,
							null,
							Resolution.DAY));
				}
			}

			// Overwrite visibility for quarantine fields
			if (!isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY) && !isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
				setVisible(
					false,
					ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
					ContactDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
					ContactDto.QUARANTINE_ORDERED_VERBALLY,
					ContactDto.QUARANTINE_ORDERED_VERBALLY_DATE,
					ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT,
					ContactDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE);
			}
		});

		setRequired(true, ContactDto.CONTACT_CLASSIFICATION, ContactDto.CONTACT_STATUS, ContactDto.REPORT_DATE_TIME);
		FieldHelper.addSoftRequiredStyle(firstContactDate, lastContactDate, contactProximity, relationToCase);
	}

	private void updateContactOfficers() {
		DistrictReferenceDto districtDto = (DistrictReferenceDto) district.getValue();
		if (districtDto == null && getValue().getCaze() != null) {
			CaseDataDto caseDto = FacadeProvider.getCaseFacade().getCaseDataByUuid(getValue().getCaze().getUuid());

			FieldHelper.updateOfficersField(contactOfficerField, caseDto, UserRight.CONTACT_RESPONSIBLE);
		} else {
			FieldHelper.updateItems(
				contactOfficerField,
				districtDto != null
					? FacadeProvider.getUserFacade().getUserRefsByDistrict(districtDto, getSelectedDisease(), UserRight.CONTACT_RESPONSIBLE)
					: null);
		}
	}

	private void updateOverwriteFollowUpUntil() {
		if (!Boolean.TRUE.equals(cbOverwriteFollowUpUntil.getValue())) {
			boolean readOnly = dfFollowUpUntil.isReadOnly();
			dfFollowUpUntil.setReadOnly(false);
			dfFollowUpUntil.discard();
			if (expectedFollowUpPeriodDto != null && expectedFollowUpPeriodDto.getFollowUpEndDate() != null) {
				dfFollowUpUntil.setValue(expectedFollowUpPeriodDto.getFollowUpEndDate());
			}
			dfFollowUpUntil.setReadOnly(readOnly);
		}
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
	private void updateFollowUpStatusComponents() {

		getContent().removeComponent(CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
		getContent().removeComponent(LOST_FOLLOW_UP_BTN_LOC);

		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
		boolean followUpVisible = getValue() != null && statusField.isVisible();
		if (followUpVisible && UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_EDIT)) {
			FollowUpStatus followUpStatus = statusField.getValue();
			tfExpectedFollowUpUntilDate.setVisible(followUpStatus != FollowUpStatus.NO_FOLLOW_UP);
			boolean followUpCanceledOrLost = followUpStatus == FollowUpStatus.CANCELED || followUpStatus == FollowUpStatus.LOST;
			cbOverwriteFollowUpUntil.setReadOnly(followUpCanceledOrLost);
			dfFollowUpUntil.setReadOnly(followUpCanceledOrLost || !Boolean.TRUE.equals(cbOverwriteFollowUpUntil.getValue()));
			if (followUpStatus == FollowUpStatus.FOLLOW_UP) {

				Button cancelButton = ButtonHelper.createButton(Captions.contactCancelFollowUp, event -> {
					setFollowUpStatus(FollowUpStatus.CANCELED);
				});
				cancelButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(cancelButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);

				Button lostButton = ButtonHelper.createButton(Captions.contactLostToFollowUp, event -> {
					setFollowUpStatus(FollowUpStatus.LOST);
				});
				lostButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(lostButton, LOST_FOLLOW_UP_BTN_LOC);

			} else if (followUpStatus == FollowUpStatus.CANCELED || followUpStatus == FollowUpStatus.LOST) {

				Button resumeButton = ButtonHelper.createButton(Captions.contactResumeFollowUp, event -> {
					setFollowUpStatus(FollowUpStatus.FOLLOW_UP);
				}, CssStyles.FORCE_CAPTION);
				resumeButton.setWidth(100, Unit.PERCENTAGE);

				getContent().addComponent(resumeButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
			}
		}
	}

	private void setFollowUpStatus(FollowUpStatus followUpStatus) {
		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(ContactDto.FOLLOW_UP_STATUS);
		statusField.setReadOnly(false);
		statusField.setValue(followUpStatus);
		statusField.setReadOnly(true);

		updateFollowUpStatusComponents();
	}

	private void updateDiseaseConfiguration(Disease disease) {
		for (Object propertyId : getFieldGroup().getBoundPropertyIds()) {
			boolean visible = DiseasesConfiguration.isDefinedOrMissing(ContactDto.class, (String) propertyId, disease);
			getFieldGroup().getField(propertyId)
				.setVisible(
					visible
						&& (getFieldGroup().getField(propertyId).isVisible()
							|| (propertyId.equals(ContactDto.CONTACT_CATEGORY) && isVisibleAllowed((String) propertyId))));
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

	public void setSourceCase(CaseReferenceDto caze) {
		getValue().setCaze(caze);
		updateFieldVisibilitiesByCase(caze != null);
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

	// This logic should be consistent with ContactFacadeEjb.onQuarantineEndChange
	private void onQuarantineEndChange() {
		if (quarantineChangedByFollowUpUntilChange) {
			quarantineChangedByFollowUpUntilChange = false;
		} else {
			Date newQuarantineEnd = dfQuarantineTo.getValue();
			ContactDto originalContact = getInternalValue();
			Date oldQuarantineEnd = originalContact.getQuarantineTo();

			ExtendedReduced changeType = null;
			if (oldQuarantineEnd != null && newQuarantineEnd != null) {
				changeType = newQuarantineEnd.after(oldQuarantineEnd)
					? ExtendedReduced.EXTENDED
					: (newQuarantineEnd.before(oldQuarantineEnd) ? ExtendedReduced.REDUCED : null);
			}
			if (changeType != null) {
				confirmQuarantineEndChanged(changeType, originalContact);
			} else {
				resetPreviousQuarantineTo(originalContact);
			}
		}
	}

	private void confirmQuarantineEndChanged(ExtendedReduced changeType, ContactDto originalContact) {
		String headingString = null;
		String confirmationString = null;
		boolean isExtended = changeType == ExtendedReduced.EXTENDED;
		boolean isReduced = changeType == ExtendedReduced.REDUCED;
		if (isExtended) {
			headingString = Strings.headingExtendQuarantine;
			confirmationString = Strings.confirmationExtendQuarantine;
		}
		if (isReduced) {
			headingString = Strings.headingReduceQuarantine;
			confirmationString = Strings.confirmationReduceQuarantine;
		}
		VaadinUiUtil.showConfirmationPopup(
			I18nProperties.getString(headingString),
			new Label(I18nProperties.getString(confirmationString)),
			I18nProperties.getString(Strings.yes),
			I18nProperties.getString(Strings.no),
			640,
			confirmed -> {
				Date quarantineTo = originalContact.getQuarantineTo();
				if (confirmed) {
					dfPreviousQuarantineTo.setReadOnly(false);
					dfPreviousQuarantineTo.setValue(quarantineTo);
					dfPreviousQuarantineTo.setReadOnly(true);
					setVisible(true, CaseDataDto.QUARANTINE_CHANGE_COMMENT, ContactDto.PREVIOUS_QUARANTINE_TO);
					cbQuarantineExtended.setValue(isExtended);
					cbQuarantineReduced.setValue(isReduced);
					setVisible(isExtended, CaseDataDto.QUARANTINE_EXTENDED);
					setVisible(isReduced, CaseDataDto.QUARANTINE_REDUCED);
					if (isExtended && originalContact.getFollowUpUntil() != null) {
						confirmExtendFollowUpPeriod(originalContact);
					}
				} else {
					dfQuarantineTo.setValue(quarantineTo);
					resetPreviousQuarantineTo(originalContact);
				}
			});
	}

	private void resetPreviousQuarantineTo(ContactDto originalContact) {
		Date previousQuarantineTo = originalContact.getPreviousQuarantineTo();
		dfPreviousQuarantineTo.setReadOnly(false);
		dfPreviousQuarantineTo.setValue(previousQuarantineTo);
		dfPreviousQuarantineTo.setReadOnly(true);
		if (previousQuarantineTo == null) {
			quarantineChangeComment.setValue(null);
			setVisible(false, ContactDto.QUARANTINE_CHANGE_COMMENT, ContactDto.PREVIOUS_QUARANTINE_TO);
		}
		cbQuarantineExtended.setValue(originalContact.isQuarantineExtended());
		cbQuarantineExtended.setVisible(originalContact.isQuarantineExtended());
		cbQuarantineReduced.setValue(originalContact.isQuarantineReduced());
		cbQuarantineReduced.setVisible(originalContact.isQuarantineReduced());
	}

	private void confirmExtendFollowUpPeriod(ContactDto originalContact) {
		Date quarantineEnd = dfQuarantineTo.getValue();
		if (quarantineEnd.after(originalContact.getFollowUpUntil())) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingExtendFollowUp),
				new Label(I18nProperties.getString(Strings.confirmationExtendFollowUp)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						cbOverwriteFollowUpUntil.setValue(true);
						dfFollowUpUntil.setValue(quarantineEnd);
					}
				});
		}
	}

	private void onQuarantineValueChange() {
		QuarantineType quarantineType = (QuarantineType) quarantine.getValue();
		if (QuarantineType.isQuarantineInEffect(quarantineType)) {
			setVisible(dfPreviousQuarantineTo.getValue() != null, ContactDto.PREVIOUS_QUARANTINE_TO, ContactDto.QUARANTINE_CHANGE_COMMENT);
			ContactDto contact = this.getInternalValue();
			if (contact != null) {
				quarantineFrom.setValue(contact.getQuarantineFrom());
				if (contact.getQuarantineTo() == null) {
					dfQuarantineTo.setValue(contact.getFollowUpUntil());
				} else {
					dfQuarantineTo.setValue(contact.getQuarantineTo());
				}
				if (contact.isQuarantineExtended()) {
					cbQuarantineExtended.setValue(true);
					setVisible(true, ContactDto.QUARANTINE_EXTENDED);
				}
				if (contact.isQuarantineReduced()) {
					cbQuarantineReduced.setValue(true);
					setVisible(true, ContactDto.QUARANTINE_REDUCED);
				}
			} else {
				quarantineChangeComment.clear();
				setVisible(false, ContactDto.PREVIOUS_QUARANTINE_TO, ContactDto.QUARANTINE_CHANGE_COMMENT);
			}
		} else {
			quarantineFrom.clear();
			dfQuarantineTo.clear();
			quarantineChangeComment.clear();
			cbQuarantineExtended.setValue(false);
			cbQuarantineReduced.setValue(false);
			setVisible(
				false,
				ContactDto.QUARANTINE_REDUCED,
				ContactDto.QUARANTINE_EXTENDED,
				ContactDto.PREVIOUS_QUARANTINE_TO,
				ContactDto.QUARANTINE_CHANGE_COMMENT);
		}
	}

	private void initContactDateValidation() {
		multiDayContact.addValueChangeListener(e -> updateDateComparison());
	}

	private void updateDateComparison() {
		DateComparisonValidator.removeDateComparisonValidators(firstContactDate);
		DateComparisonValidator.removeDateComparisonValidators(lastContactDate);
		DateComparisonValidator.removeDateComparisonValidators(reportDate);

		DateComparisonValidator.addStartEndValidators(lastContactDate, reportDate);

		if (firstContactDate.isVisible() || Boolean.TRUE.equals(multiDayContact.getValue())) {
			DateComparisonValidator.addStartEndValidators(firstContactDate, lastContactDate);
			DateComparisonValidator.addStartEndValidators(firstContactDate, reportDate);
		}
	}

	private void hideAndFillJurisdictionFields() {

		region.setVisible(false);
		region.setValue(FacadeProvider.getRegionFacade().getDefaultInfrastructureReference());
		district.setVisible(false);
		district.setValue(FacadeProvider.getDistrictFacade().getDefaultInfrastructureReference());
		community.setVisible(false);
		community.setValue(FacadeProvider.getCommunityFacade().getDefaultInfrastructureReference());
	}

	@Override
	public void setValue(ContactDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		expectedFollowUpPeriodDto = FacadeProvider.getContactFacade().getCalculatedFollowUpUntilDate(newFieldValue, true);
		tfExpectedFollowUpUntilDate
			.setValue(DateHelper.formatLocalDate(expectedFollowUpPeriodDto.getFollowUpEndDate(), I18nProperties.getUserLanguage()));
		tfExpectedFollowUpUntilDate.setReadOnly(true);
		tfExpectedFollowUpUntilDate.setDescription(
			String.format(
				I18nProperties.getString(Strings.infoExpectedFollowUpUntilDateContact),
				expectedFollowUpPeriodDto.getFollowUpStartDateType(),
				DateHelper.formatLocalDate(expectedFollowUpPeriodDto.getFollowUpStartDate(), I18nProperties.getUserLanguage())));
		updateContactOfficers();
		updateOverwriteFollowUpUntil();
		updateFollowUpStatusComponents();

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideAndFillJurisdictionFields();
		}

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		discard();
	}
}
