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
package de.symeda.sormas.ui.caze;

import static de.symeda.sormas.ui.utils.CssStyles.ERROR_COLOR_PRIMARY;
import static de.symeda.sormas.ui.utils.CssStyles.FORCE_CAPTION;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.LABEL_WHITE_SPACE_NORMAL;
import static de.symeda.sormas.ui.utils.CssStyles.LAYOUT_COL_HIDE_INVSIBLE;
import static de.symeda.sormas.ui.utils.CssStyles.SOFT_REQUIRED;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.style;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLoc;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.inlineLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.validator.DateRangeValidator;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.VerticalLayout;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseConfirmationBasis;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIdentificationSource;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.EndOfIsolationReason;
import de.symeda.sormas.api.caze.HospitalWardType;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.PreviousCaseDto;
import de.symeda.sormas.api.caze.QuarantineReason;
import de.symeda.sormas.api.caze.ReinfectionDetail;
import de.symeda.sormas.api.caze.ReinfectionDetailGroup;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.TypeOfPlace;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.ExtendedReduced;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.UserRightFieldVisibilityChecker;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.caze.surveillancereport.CaseReinfectionCheckBoxTree;
import de.symeda.sormas.ui.clinicalcourse.HealthConditionsForm;
import de.symeda.sormas.ui.location.AccessibleTextField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CheckBoxTree;
import de.symeda.sormas.ui.utils.ComboBoxHelper;
import de.symeda.sormas.ui.utils.ComboBoxWithPlaceholder;
import de.symeda.sormas.ui.utils.ConfirmationComponent;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.InfrastructureFieldsHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.StringToAngularLocationConverter;
import de.symeda.sormas.ui.utils.UserField;
import de.symeda.sormas.ui.utils.VaadinUiUtil;
import de.symeda.sormas.ui.utils.ValidationUtils;
import de.symeda.sormas.ui.utils.ViewMode;

public class CaseDataForm extends AbstractEditForm<CaseDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String CASE_DATA_HEADING_LOC = "caseDataHeadingLoc";
	private static final String MEDICAL_INFORMATION_LOC = "medicalInformationLoc";
	private static final String PAPER_FORM_DATES_LOC = "paperFormDatesLoc";
	private static final String SMALLPOX_VACCINATION_SCAR_IMG = "smallpoxVaccinationScarImg";
	private static final String CLASSIFICATION_RULES_LOC = "classificationRulesLoc";
	private static final String CLASSIFIED_BY_SYSTEM_LOC = "classifiedBySystemLoc";
	private static final String ASSIGN_NEW_EPID_NUMBER_LOC = "assignNewEpidNumberLoc";
	private static final String EPID_NUMBER_WARNING_LOC = "epidNumberWarningLoc";
	private static final String EXTERNAL_TOKEN_WARNING_LOC = "externalTokenWarningLoc";
	private static final String GENERAL_COMMENT_LOC = "generalCommentLoc";
	private static final String FOLLOW_UP_STATUS_HEADING_LOC = "followUpStatusHeadingLoc";
	private static final String CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC = "cancelOrResumeFollowUpBtnLoc";
	private static final String LOST_FOLLOW_UP_BTN_LOC = "lostFollowUpBtnLoc";
	private static final String PLACE_OF_STAY_HEADING_LOC = "placeOfStayHeadingLoc";
	private static final String FACILITY_OR_HOME_LOC = "facilityOrHomeLoc";
	private static final String TYPE_GROUP_LOC = "typeGroupLoc";
	private static final String CONTACT_TRACING_FIRST_CONTACT_HEADER_LOC = "contactTracingFirstContact";
	private static final String EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC = "expectedFollowUpUntilDateLoc";
	private static final String CASE_CONFIRMATION_BASIS = "caseConfirmationBasis";
	private static final String RESPONSIBLE_JURISDICTION_HEADING_LOC = "responsibleJurisdictionHeadingLoc";
	private static final String DIFFERENT_PLACE_OF_STAY_JURISDICTION = "differentPlaceOfStayJurisdiction";
	private static final String DONT_SHARE_WARNING_LOC = "dontShareWarning";
	private static final String CASE_CLASSIFICATION_CALCULATE_BTN_LOC = "caseClassificationCalculateBtnLoc";
	private static final String REINFECTION_INFO_LOC = "reinfectionInfoLoc";
	private static final String REINFECTION_DETAILS_COL_1_LOC = "reinfectionDetailsCol1Loc";
	private static final String REINFECTION_DETAILS_COL_2_LOC = "reinfectionDetailsCol2Loc";
	public static final String CASE_REFER_POINT_OF_ENTRY_BTN_LOC = "caseReferFromPointOfEntryBtnLoc";

	//@formatter:off
	private static final String MAIN_HTML_LAYOUT =
			loc(CASE_DATA_HEADING_LOC) +
					fluidRowLocs(4, CaseDataDto.UUID, 3, CaseDataDto.REPORT_DATE, 3, CaseDataDto.REPORTING_USER, 2, "") +
					inlineLocs(CaseDataDto.CASE_CLASSIFICATION, CLASSIFICATION_RULES_LOC, CASE_CONFIRMATION_BASIS, CASE_CLASSIFICATION_CALCULATE_BTN_LOC) +
					fluidRow(fluidColumnLoc(3, 0, CaseDataDto.CASE_REFERENCE_DEFINITION)) +
					fluidRowLocs(4, CaseDataDto.CLINICAL_CONFIRMATION, 4, CaseDataDto.EPIDEMIOLOGICAL_CONFIRMATION, 4, CaseDataDto.LABORATORY_DIAGNOSTIC_CONFIRMATION) +
					fluidRowLocsCss(VSPACE_3, CaseDataDto.NOT_A_CASE_REASON_NEGATIVE_TEST, CaseDataDto.NOT_A_CASE_REASON_PHYSICIAN_INFORMATION,
							CaseDataDto.NOT_A_CASE_REASON_DIFFERENT_PATHOGEN, CaseDataDto.NOT_A_CASE_REASON_OTHER) +
					fluidRowLocs(CaseDataDto.NOT_A_CASE_REASON_DETAILS) +
					fluidRow(
							fluidColumnLoc(3, 0, CaseDataDto.CLASSIFICATION_DATE),
							fluidColumnLocCss(LAYOUT_COL_HIDE_INVSIBLE, 5, 0, CaseDataDto.CLASSIFICATION_USER),
							fluidColumnLocCss(LAYOUT_COL_HIDE_INVSIBLE, 4, 0, CLASSIFIED_BY_SYSTEM_LOC)) +
					fluidRowLocs(9, CaseDataDto.INVESTIGATION_STATUS, 3, CaseDataDto.INVESTIGATED_DATE) +
					fluidRowLocs(6, CaseDataDto.EPID_NUMBER, 3, ASSIGN_NEW_EPID_NUMBER_LOC) +
					loc(EPID_NUMBER_WARNING_LOC) +
					fluidRowLocs(CaseDataDto.EXTERNAL_ID, CaseDataDto.EXTERNAL_TOKEN) +
					fluidRowLocs("", EXTERNAL_TOKEN_WARNING_LOC) +
					fluidRowLocs(6, CaseDataDto.CASE_ID_ISM, 6, CaseDataDto.INTERNAL_TOKEN) +
					fluidRow(
							fluidColumnLoc(6, 0, CaseDataDto.DISEASE),
							fluidColumn(6, 0, locs(
									CaseDataDto.DISEASE_DETAILS,
									CaseDataDto.PLAGUE_TYPE,
									CaseDataDto.DENGUE_FEVER_TYPE,
									CaseDataDto.RABIES_TYPE))) +
					fluidRowLocs(CaseDataDto.DISEASE_VARIANT, CaseDataDto.DISEASE_VARIANT_DETAILS) +
					fluidRow(
							fluidColumnLoc(4, 0, CaseDataDto.RE_INFECTION),
							fluidColumnLoc(1, 0, REINFECTION_INFO_LOC),
							fluidColumnLoc(3, 0, CaseDataDto.REINFECTION_STATUS),
							fluidColumnLoc(4, 0, CaseDataDto.PREVIOUS_INFECTION_DATE)
					) +
					fluidRow(
							fluidColumnLoc(6, 0, REINFECTION_DETAILS_COL_1_LOC),
							fluidColumnLoc(6, 0, REINFECTION_DETAILS_COL_2_LOC)
					) +
					fluidRowLocs(9, CaseDataDto.OUTCOME, 3, CaseDataDto.OUTCOME_DATE) +
					fluidRowLocs(3, CaseDataDto.SEQUELAE, 9, CaseDataDto.SEQUELAE_DETAILS) +
					fluidRowLocs(CaseDataDto.CASE_IDENTIFICATION_SOURCE, CaseDataDto.SCREENING_TYPE) +
					fluidRowLocs(CaseDataDto.CASE_ORIGIN, "") +
					fluidRowLocs(RESPONSIBLE_JURISDICTION_HEADING_LOC) +
					fluidRowLocs(CaseDataDto.RESPONSIBLE_REGION, CaseDataDto.RESPONSIBLE_DISTRICT, CaseDataDto.RESPONSIBLE_COMMUNITY) +
					fluidRowLocs(CaseDataDto.DONT_SHARE_WITH_REPORTING_TOOL) +
					fluidRowLocs(DONT_SHARE_WARNING_LOC) +
					fluidRowLocs(DIFFERENT_PLACE_OF_STAY_JURISDICTION) +
					fluidRowLocs(PLACE_OF_STAY_HEADING_LOC) +
					fluidRowLocs(FACILITY_OR_HOME_LOC) +
					fluidRowLocs(CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY) +
					fluidRowLocs(TYPE_GROUP_LOC, CaseDataDto.FACILITY_TYPE) +
					fluidRowLocs(CaseDataDto.HEALTH_FACILITY, CaseDataDto.HEALTH_FACILITY_DETAILS) +
					inlineLocs(CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS, CASE_REFER_POINT_OF_ENTRY_BTN_LOC) +
					fluidRowLocs(CaseDataDto.NOSOCOMIAL_OUTBREAK, CaseDataDto.INFECTION_SETTING) +
					locCss(VSPACE_3, CaseDataDto.SHARED_TO_COUNTRY) +
					fluidRowLocs(4, CaseDataDto.PROHIBITION_TO_WORK, 4, CaseDataDto.PROHIBITION_TO_WORK_FROM, 4, CaseDataDto.PROHIBITION_TO_WORK_UNTIL) +
					fluidRowLocs(4, CaseDataDto.QUARANTINE_HOME_POSSIBLE, 8, CaseDataDto.QUARANTINE_HOME_POSSIBLE_COMMENT) +
					fluidRowLocs(4, CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED, 8, CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT) +
					fluidRowLocs(6, CaseDataDto.QUARANTINE, 3, CaseDataDto.QUARANTINE_FROM, 3, CaseDataDto.QUARANTINE_TO) +
					fluidRowLocs(9, CaseDataDto.QUARANTINE_CHANGE_COMMENT, 3, CaseDataDto.PREVIOUS_QUARANTINE_TO) +
					fluidRowLocs(CaseDataDto.QUARANTINE_EXTENDED) +
					fluidRowLocs(CaseDataDto.QUARANTINE_REDUCED) +
					fluidRowLocs(CaseDataDto.QUARANTINE_TYPE_DETAILS) +
					fluidRowLocs(CaseDataDto.QUARANTINE_ORDERED_VERBALLY, CaseDataDto.QUARANTINE_ORDERED_VERBALLY_DATE) +
					fluidRowLocs(CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE) +
					fluidRowLocs(CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT, CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE) +
					fluidRowLocs(CaseDataDto.QUARANTINE_HELP_NEEDED) +
					fluidRowLocs(CaseDataDto.WAS_IN_QUARANTINE_BEFORE_ISOLATION) +
					fluidRowLocs(CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION, CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION_DETAILS) +
					fluidRowLocs(CaseDataDto.END_OF_ISOLATION_REASON, CaseDataDto.END_OF_ISOLATION_REASON_DETAILS) +
					fluidRowLocs(CaseDataDto.REPORT_LAT, CaseDataDto.REPORT_LON, CaseDataDto.REPORT_LAT_LON_ACCURACY) +
					fluidRowLocs(CaseDataDto.HEALTH_CONDITIONS) +
					loc(MEDICAL_INFORMATION_LOC) +
					fluidRowLocs(CaseDataDto.BLOOD_ORGAN_OR_TISSUE_DONATED) +
					fluidRowLocs(CaseDataDto.PREGNANT, CaseDataDto.POSTPARTUM) + fluidRowLocs(CaseDataDto.TRIMESTER, "") +
					fluidRowLocs(CaseDataDto.VACCINATION_STATUS, "") +
					fluidRowLocs(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED, CaseDataDto.SMALLPOX_VACCINATION_SCAR) +
					fluidRowLocs(CaseDataDto.SMALLPOX_LAST_VACCINATION_DATE, "") +
					fluidRowLocs(SMALLPOX_VACCINATION_SCAR_IMG) +
					fluidRowLocs(6, CaseDataDto.CLINICIAN_NAME) +
					fluidRowLocs(CaseDataDto.NOTIFYING_CLINIC, CaseDataDto.NOTIFYING_CLINIC_DETAILS) +
					fluidRowLocs(CaseDataDto.CLINICIAN_PHONE, CaseDataDto.CLINICIAN_EMAIL) +
					loc(CONTACT_TRACING_FIRST_CONTACT_HEADER_LOC) +
					fluidRowLocs(CaseDataDto.CONTACT_TRACING_FIRST_CONTACT_TYPE, CaseDataDto.CONTACT_TRACING_FIRST_CONTACT_DATE);

	private static final String FOLLOWUP_LAYOUT =
			loc(FOLLOW_UP_STATUS_HEADING_LOC) +
					fluidRowLocs(CaseDataDto.FOLLOW_UP_STATUS, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC, LOST_FOLLOW_UP_BTN_LOC) +
					fluidRowLocs(CaseDataDto.FOLLOW_UP_STATUS_CHANGE_DATE, CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER) +
					fluidRowLocs(CaseDataDto.FOLLOW_UP_UNTIL, EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC, CaseDataDto.OVERWRITE_FOLLOW_UP_UNTIL) +
					fluidRowLocs(CaseDataDto.FOLLOW_UP_COMMENT);
	
	private static final String PAPER_FORM_DATES_AND_HEALTH_CONDITIONS_HTML_LAYOUT =
			fluidRowLocs(6, CaseDataDto.SURVEILLANCE_OFFICER) +
					loc(PAPER_FORM_DATES_LOC) +
					fluidRowLocs(CaseDataDto.DISTRICT_LEVEL_DATE, CaseDataDto.REGION_LEVEL_DATE, CaseDataDto.NATIONAL_LEVEL_DATE) +
					loc(GENERAL_COMMENT_LOC) + fluidRowLocs(CaseDataDto.ADDITIONAL_DETAILS) +
					fluidRowLocs(CaseDataDto.DELETION_REASON) +
					fluidRowLocs(CaseDataDto.OTHER_DELETION_REASON);
	//@formatter:on

	private final String caseUuid;
	private final PersonDto person;
	private final Disease disease;
	private final SymptomsDto symptoms;
	private final boolean caseFollowUpEnabled;
	private DateField dfFollowUpUntil;
	private CheckBox cbOverwriteFollowUpUntil;
	private Field<?> quarantine;
	private DateField quarantineFrom;
	private DateField dfQuarantineTo;
	private TextField quarantineChangeComment;
	private DateField dfPreviousQuarantineTo;
	private CheckBox cbQuarantineExtended;
	private CheckBox cbQuarantineReduced;
	private CheckBox quarantineOrderedVerbally;
	private CheckBox quarantineOrderedOfficialDocument;
	private CheckBox differentPlaceOfStayJurisdiction;
	private ComboBox responsibleRegion;
	private ComboBox responsibleDistrict;
	private ComboBox responsibleCommunity;
	private ComboBox regionCombo;
	private ComboBox districtCombo;
	private ComboBox communityCombo;
	private OptionGroup facilityOrHome;
	private ComboBoxWithPlaceholder facilityTypeGroup;
	private ComboBoxWithPlaceholder facilityTypeCombo;
	private ComboBox facilityCombo;
	private TextField facilityDetails;
	private boolean quarantineChangedByFollowUpUntilChange = false;
	private TextField tfExpectedFollowUpUntilDate;
	private FollowUpPeriodDto expectedFollowUpPeriodDto;
	private boolean ignoreDifferentPlaceOfStayJurisdiction = false;

	private final Map<ReinfectionDetailGroup, CaseReinfectionCheckBoxTree> reinfectionTrees = new EnumMap<>(ReinfectionDetailGroup.class);

	public CaseDataForm(
		String caseUuid,
		PersonDto person,
		Disease disease,
		SymptomsDto symptoms,
		ViewMode viewMode,
		boolean isPseudonymized,
		boolean inJurisdiction) {

		super(
			CaseDataDto.class,
			CaseDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease)
				.add(new OutbreakFieldVisibilityChecker(viewMode))
				.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale()))
				.add(new UserRightFieldVisibilityChecker(UserProvider.getCurrent()::hasUserRight)),
			UiFieldAccessCheckers.forDataAccessLevel(UserProvider.getCurrent().getPseudonymizableDataAccessLevel(inJurisdiction), isPseudonymized));

		this.caseUuid = caseUuid;
		this.person = person;
		this.disease = disease;
		this.symptoms = symptoms;
		this.caseFollowUpEnabled = FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.CASE_FOLLOWUP);

		addFields();
	}

	public static void updateFacilityDetails(ComboBox cbFacility, TextField tfFacilityDetails) {
		if (cbFacility.getValue() != null) {
			boolean otherHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
			boolean noneHealthFacility = ((FacilityReferenceDto) cbFacility.getValue()).getUuid().equals(FacilityDto.NONE_FACILITY_UUID);
			boolean visible = otherHealthFacility || noneHealthFacility;

			tfFacilityDetails.setVisible(visible);

			if (otherHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS));
			}
			if (noneHealthFacility) {
				tfFacilityDetails.setCaption(I18nProperties.getCaption(Captions.CaseData_noneHealthFacilityDetails));
			}
			if (!visible && !tfFacilityDetails.isReadOnly()) {
				tfFacilityDetails.clear();
			}
		} else {
			tfFacilityDetails.setVisible(false);
			if (!tfFacilityDetails.isReadOnly()) {
				tfFacilityDetails.clear();
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void addFields() {

		if (person == null || disease == null) {
			return;
		}

		Label caseDataHeadingLabel = new Label(I18nProperties.getString(Strings.headingCaseData));
		caseDataHeadingLabel.addStyleName(H3);
		getContent().addComponent(caseDataHeadingLabel, CASE_DATA_HEADING_LOC);

		if (caseFollowUpEnabled) {
			Label followUpStatusHeadingLabel = new Label(I18nProperties.getString(Strings.headingFollowUpStatus));
			followUpStatusHeadingLabel.addStyleName(H3);
			getContent().addComponent(followUpStatusHeadingLabel, FOLLOW_UP_STATUS_HEADING_LOC);
		}

		// Add fields
		DateField reportDate = addField(CaseDataDto.REPORT_DATE, DateField.class);
		addFields(
			CaseDataDto.UUID,
			CaseDataDto.DISTRICT_LEVEL_DATE,
			CaseDataDto.REGION_LEVEL_DATE,
			CaseDataDto.NATIONAL_LEVEL_DATE,
			CaseDataDto.CLASSIFICATION_DATE,
			CaseDataDto.CLASSIFICATION_USER,
			CaseDataDto.CLASSIFICATION_COMMENT,
			CaseDataDto.NOTIFYING_CLINIC,
			CaseDataDto.NOTIFYING_CLINIC_DETAILS,
			CaseDataDto.CLINICIAN_NAME,
			CaseDataDto.CLINICIAN_PHONE,
			CaseDataDto.CLINICIAN_EMAIL);

		addField(CaseDataDto.REPORTING_USER, UserField.class);

		TextField epidField = addField(CaseDataDto.EPID_NUMBER, TextField.class);
		epidField.setInvalidCommitted(true);
		epidField.setMaxLength(24);
		style(epidField, ERROR_COLOR_PRIMARY);

		// Button to automatically assign a new epid number
		Button assignNewEpidNumberButton = ButtonHelper.createButton(
			Captions.actionAssignNewEpidNumber,
			e -> epidField.setValue(FacadeProvider.getCaseFacade().getGenerateEpidNumber(getValue())),
			ValoTheme.BUTTON_DANGER,
			FORCE_CAPTION);

		getContent().addComponent(assignNewEpidNumberButton, ASSIGN_NEW_EPID_NUMBER_LOC);
		assignNewEpidNumberButton.setVisible(false);

		Label epidNumberWarningLabel = new Label(I18nProperties.getString(Strings.messageEpidNumberWarning));
		epidNumberWarningLabel.addStyleName(VSPACE_3);
		addField(CaseDataDto.EXTERNAL_ID, TextField.class);

		if (FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled()) {
			CheckBox dontShareCheckbox = addField(CaseDataDto.DONT_SHARE_WITH_REPORTING_TOOL, CheckBox.class);
			CaseFormHelper.addDontShareWithReportingTool(getContent(), () -> dontShareCheckbox, DONT_SHARE_WARNING_LOC);
			if (FacadeProvider.getExternalShareInfoFacade().isSharedCase(this.caseUuid)) {
				dontShareCheckbox.setEnabled(false);
				dontShareCheckbox.setDescription(I18nProperties.getString(Strings.infoDontShareCheckboxAlreadyShared));
			}
		}

		TextField externalTokenField = addField(CaseDataDto.EXTERNAL_TOKEN, TextField.class);
		Label externalTokenWarningLabel = new Label(I18nProperties.getString(Strings.messageCaseExternalTokenWarning));
		externalTokenWarningLabel.addStyleNames(VSPACE_3, LABEL_WHITE_SPACE_NORMAL);
		getContent().addComponent(externalTokenWarningLabel, EXTERNAL_TOKEN_WARNING_LOC);

		addField(CaseDataDto.INTERNAL_TOKEN, TextField.class);

		addField(CaseDataDto.INVESTIGATION_STATUS, NullableOptionGroup.class);
		addField(CaseDataDto.OUTCOME, NullableOptionGroup.class);
		addField(CaseDataDto.BLOOD_ORGAN_OR_TISSUE_DONATED, NullableOptionGroup.class);
		addField(CaseDataDto.SEQUELAE, NullableOptionGroup.class);

		addFields(CaseDataDto.INVESTIGATED_DATE, CaseDataDto.OUTCOME_DATE, CaseDataDto.SEQUELAE_DETAILS);

		addField(CaseDataDto.CASE_IDENTIFICATION_SOURCE);
		addField(CaseDataDto.SCREENING_TYPE);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.SCREENING_TYPE,
			CaseDataDto.CASE_IDENTIFICATION_SOURCE,
			Collections.singletonList(CaseIdentificationSource.SCREENING),
			true);

		ComboBox diseaseField = addDiseaseField(CaseDataDto.DISEASE, false);
		ComboBox diseaseVariantField = addCustomizableEnumField(CaseDataDto.DISEASE_VARIANT);
		TextField diseaseVariantDetailsField = addField(CaseDataDto.DISEASE_VARIANT_DETAILS, TextField.class);
		diseaseVariantDetailsField.setVisible(false);
		diseaseVariantField.setNullSelectionAllowed(true);
		addField(CaseDataDto.DISEASE_DETAILS, TextField.class);
		addField(CaseDataDto.PLAGUE_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.DENGUE_FEVER_TYPE, NullableOptionGroup.class);
		addField(CaseDataDto.RABIES_TYPE, NullableOptionGroup.class);

		addField(CaseDataDto.CASE_ORIGIN, TextField.class);

		quarantine = addField(CaseDataDto.QUARANTINE);
		quarantine.addValueChangeListener(e -> onValueChange());
		quarantineFrom = addField(CaseDataDto.QUARANTINE_FROM, DateField.class);
		dfQuarantineTo = addDateField(CaseDataDto.QUARANTINE_TO, DateField.class, -1);

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

		quarantineChangeComment = addField(CaseDataDto.QUARANTINE_CHANGE_COMMENT);
		dfPreviousQuarantineTo = addDateField(CaseDataDto.PREVIOUS_QUARANTINE_TO, DateField.class, -1);
		setReadOnly(true, CaseDataDto.PREVIOUS_QUARANTINE_TO);
		setVisible(false, CaseDataDto.QUARANTINE_CHANGE_COMMENT, CaseDataDto.PREVIOUS_QUARANTINE_TO);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)) {
			final ComboBox cbCaseClassification = addField(CaseDataDto.CASE_CLASSIFICATION, ComboBox.class);
			cbCaseClassification.addValidator(
				new GermanCaseClassificationValidator(caseUuid, I18nProperties.getValidationError(Validations.caseClassificationInvalid)));

			ComboBox caseReferenceDefinition = addField(CaseDataDto.CASE_REFERENCE_DEFINITION, ComboBox.class);
			caseReferenceDefinition.setReadOnly(true);

			if (diseaseClassificationExists()) {
				Button caseClassificationCalculationButton = ButtonHelper.createButton(Captions.caseClassificationCalculationButton, e -> {
					CaseClassification classification = FacadeProvider.getCaseClassificationFacade().getClassification(getValue());
					((Field<CaseClassification>) getField(CaseDataDto.CASE_CLASSIFICATION)).setValue(classification);
				}, ValoTheme.BUTTON_PRIMARY, FORCE_CAPTION);

				getContent().addComponent(caseClassificationCalculationButton, CASE_CLASSIFICATION_CALCULATE_BTN_LOC);

				if (!UserProvider.getCurrent().hasUserRight(UserRight.CASE_CLASSIFY)) {
					caseClassificationCalculationButton.setEnabled(false);
				}
			}

			//if(cbCaseClassification.getCaption())
			addField(CaseDataDto.NOT_A_CASE_REASON_NEGATIVE_TEST, CheckBox.class);
			addField(CaseDataDto.NOT_A_CASE_REASON_PHYSICIAN_INFORMATION, CheckBox.class);
			addField(CaseDataDto.NOT_A_CASE_REASON_DIFFERENT_PATHOGEN, CheckBox.class);
			addField(CaseDataDto.NOT_A_CASE_REASON_OTHER, CheckBox.class);
			addField(CaseDataDto.NOT_A_CASE_REASON_DETAILS, TextField.class);

			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(
					CaseDataDto.NOT_A_CASE_REASON_NEGATIVE_TEST,
					CaseDataDto.NOT_A_CASE_REASON_PHYSICIAN_INFORMATION,
					CaseDataDto.NOT_A_CASE_REASON_DIFFERENT_PATHOGEN,
					CaseDataDto.NOT_A_CASE_REASON_OTHER),
				CaseDataDto.CASE_CLASSIFICATION,
				CaseClassification.NO_CASE,
				true);

			FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.NOT_A_CASE_REASON_DETAILS, CaseDataDto.NOT_A_CASE_REASON_OTHER, true, true);
		} else {
			final NullableOptionGroup caseClassificationGroup = addField(CaseDataDto.CASE_CLASSIFICATION, NullableOptionGroup.class);
			caseClassificationGroup.removeItem(CaseClassification.CONFIRMED_NO_SYMPTOMS);
			caseClassificationGroup.removeItem(CaseClassification.CONFIRMED_UNKNOWN_SYMPTOMS);
		}

		boolean extendedClassification = FacadeProvider.getDiseaseConfigurationFacade().usesExtendedClassification(disease);

		if (extendedClassification) {
			ComboBox clinicalConfirmationCombo = addField(CaseDataDto.CLINICAL_CONFIRMATION, ComboBox.class);
			ComboBox epidemiologicalConfirmationCombo = addField(CaseDataDto.EPIDEMIOLOGICAL_CONFIRMATION, ComboBox.class);
			ComboBox laboratoryConfirmationCombo = addField(CaseDataDto.LABORATORY_DIAGNOSTIC_CONFIRMATION, ComboBox.class);
			ComboBox caseConfirmationBasisCombo = addCustomField(CASE_CONFIRMATION_BASIS, CaseConfirmationBasis.class, ComboBox.class);

			boolean extendedClassificationMulti = FacadeProvider.getDiseaseConfigurationFacade().usesExtendedClassificationMulti(disease);

			if (extendedClassificationMulti) {
				caseConfirmationBasisCombo.setVisible(false);
			} else {
				caseConfirmationBasisCombo.addValueChangeListener(field -> {
					clinicalConfirmationCombo.setValue(null);
					epidemiologicalConfirmationCombo.setValue(null);
					laboratoryConfirmationCombo.setValue(null);

					if (caseConfirmationBasisCombo.getValue() != null) {
						switch ((CaseConfirmationBasis) caseConfirmationBasisCombo.getValue()) {
						case CLINICAL_CONFIRMATION:
							clinicalConfirmationCombo.setValue(YesNoUnknown.YES);
							break;
						case EPIDEMIOLOGICAL_CONFIRMATION:
							epidemiologicalConfirmationCombo.setValue(YesNoUnknown.YES);
							break;
						case LABORATORY_DIAGNOSTIC_CONFIRMATION:
							laboratoryConfirmationCombo.setValue(YesNoUnknown.YES);
							break;
						}
					}
				});

				FieldHelper.setVisibleWhen(
					getField(CaseDataDto.CASE_CLASSIFICATION),
					Collections.singletonList(caseConfirmationBasisCombo),
					Collections.singletonList(CaseClassification.CONFIRMED),
					true);
				clinicalConfirmationCombo.setVisible(false);
				epidemiologicalConfirmationCombo.setVisible(false);
				laboratoryConfirmationCombo.setVisible(false);
			}

			setReadOnly(
				!UserProvider.getCurrent().hasUserRight(UserRight.CASE_CLASSIFY),
				CaseDataDto.CLINICAL_CONFIRMATION,
				CaseDataDto.EPIDEMIOLOGICAL_CONFIRMATION,
				CaseDataDto.LABORATORY_DIAGNOSTIC_CONFIRMATION);
		}

		quarantineOrderedVerbally = addField(CaseDataDto.QUARANTINE_ORDERED_VERBALLY, CheckBox.class);
		CssStyles.style(quarantineOrderedVerbally, CssStyles.FORCE_CAPTION);
		addField(CaseDataDto.QUARANTINE_ORDERED_VERBALLY_DATE, DateField.class);
		quarantineOrderedOfficialDocument = addField(CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT, CheckBox.class);
		CssStyles.style(quarantineOrderedOfficialDocument, CssStyles.FORCE_CAPTION);
		addField(CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE, DateField.class);

		CheckBox quarantineOfficialOrderSent = addField(CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT, CheckBox.class);
		CssStyles.style(quarantineOfficialOrderSent, FORCE_CAPTION);
		addField(CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE, DateField.class);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		cbQuarantineExtended = addField(CaseDataDto.QUARANTINE_EXTENDED, CheckBox.class);
		cbQuarantineExtended.setEnabled(false);
		cbQuarantineExtended.setVisible(false);
		CssStyles.style(cbQuarantineExtended, CssStyles.FORCE_CAPTION);

		cbQuarantineReduced = addField(CaseDataDto.QUARANTINE_REDUCED, CheckBox.class);
		cbQuarantineReduced.setEnabled(false);
		cbQuarantineReduced.setVisible(false);
		CssStyles.style(cbQuarantineReduced, CssStyles.FORCE_CAPTION);

		TextField quarantineHelpNeeded = addField(CaseDataDto.QUARANTINE_HELP_NEEDED, TextField.class);
		quarantineHelpNeeded.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));
		TextField quarantineTypeDetails = addField(CaseDataDto.QUARANTINE_TYPE_DETAILS, TextField.class);
		quarantineTypeDetails.setInputPrompt(I18nProperties.getString(Strings.pleaseSpecify));

		addField(CaseDataDto.NOSOCOMIAL_OUTBREAK).addStyleNames(CssStyles.FORCE_CAPTION_CHECKBOX);
		addField(CaseDataDto.INFECTION_SETTING);
		FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.INFECTION_SETTING, CaseDataDto.NOSOCOMIAL_OUTBREAK, true, true);

		// Reinfection
		{
			NullableOptionGroup ogReinfection = addField(CaseDataDto.RE_INFECTION, NullableOptionGroup.class);

			addField(CaseDataDto.PREVIOUS_INFECTION_DATE);
			ComboBox tfReinfectionStatus = addField(CaseDataDto.REINFECTION_STATUS, ComboBox.class);
			tfReinfectionStatus.setReadOnly(true);
			FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.PREVIOUS_INFECTION_DATE, CaseDataDto.RE_INFECTION, YesNoUnknown.YES, false);
			FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.REINFECTION_STATUS, CaseDataDto.RE_INFECTION, YesNoUnknown.YES, false);

			final Label reinfectionInfoLabel = new Label(VaadinIcons.EYE.getHtml(), ContentMode.HTML);
			CssStyles.style(reinfectionInfoLabel, CssStyles.LABEL_XLARGE, CssStyles.VSPACE_TOP_3);
			getContent().addComponent(reinfectionInfoLabel, REINFECTION_INFO_LOC);
			reinfectionInfoLabel.setVisible(false);

			final VerticalLayout reinfectionDetailsLeftLayout = new VerticalLayout();
			CssStyles.style(reinfectionDetailsLeftLayout, CssStyles.VSPACE_3);
			final VerticalLayout reinfectionDetailsRightLayout = new VerticalLayout();
			CssStyles.style(reinfectionDetailsRightLayout, CssStyles.VSPACE_3);
			for (ReinfectionDetailGroup group : ReinfectionDetailGroup.values()) {
				CaseReinfectionCheckBoxTree reinfectionTree = new CaseReinfectionCheckBoxTree(
					ReinfectionDetail.values(group).stream().map(e -> new CheckBoxTree.CheckBoxElement<>(null, e)).collect(Collectors.toList()),
					() -> {
						tfReinfectionStatus.setReadOnly(false);
						tfReinfectionStatus.setValue(CaseLogic.calculateReinfectionStatus(mergeReinfectionTrees()));
						tfReinfectionStatus.setReadOnly(true);
					});
				reinfectionTrees.put(group, reinfectionTree);

				if (StringUtils.isNotBlank(group.toString())) {
					Label heading = new Label(group.toString());
					CssStyles.style(heading, CssStyles.H4);
					if (group.ordinal() < 2) {
						reinfectionDetailsLeftLayout.addComponent(heading);
					} else {
						reinfectionDetailsRightLayout.addComponent(heading);
					}
				}

				if (group.ordinal() < 2) {
					reinfectionDetailsLeftLayout.addComponent(reinfectionTree);
				} else {
					reinfectionDetailsRightLayout.addComponent(reinfectionTree);
				}
			}
			getContent().addComponent(reinfectionDetailsLeftLayout, REINFECTION_DETAILS_COL_1_LOC);
			getContent().addComponent(reinfectionDetailsRightLayout, REINFECTION_DETAILS_COL_2_LOC);
			reinfectionDetailsLeftLayout.setVisible(false);
			reinfectionDetailsRightLayout.setVisible(false);

			ogReinfection.addValueChangeListener(e -> {
				if (((NullableOptionGroup) e.getProperty()).getNullableValue() == YesNoUnknown.YES) {
					PreviousCaseDto previousCase = FacadeProvider.getCaseFacade()
						.getMostRecentPreviousCase(getValue().getPerson(), getValue().getDisease(), CaseLogic.getStartDate(getValue()));

					if (previousCase != null) {
						String reinfectionInfoTemplate = "<b>Previous case:</b><br/><br/>%s: %s<br/>%s: %s<br/>%s: %s<br/>%s: %s<br/>%s: %s";
						String reinfectionInfo = String.format(
							reinfectionInfoTemplate,
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, EntityDto.UUID),
							DataHelper.getShortUuid(previousCase.getUuid()),
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE),
							DateHelper.formatLocalDate(previousCase.getReportDate(), I18nProperties.getUserLanguage()),
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.EXTERNAL_TOKEN),
							DataHelper.toStringNullable(previousCase.getExternalToken()),
							I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE_VARIANT),
							DataHelper.toStringNullable(previousCase.getDiseaseVariant()),
							I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, SymptomsDto.ONSET_DATE),
							previousCase.getOnsetDate() != null
								? DateHelper.formatLocalDate(previousCase.getOnsetDate(), I18nProperties.getUserLanguage())
								: "");
						reinfectionInfoLabel.setDescription(reinfectionInfo, ContentMode.HTML);
						reinfectionInfoLabel.setVisible(isVisibleAllowed(CaseDataDto.RE_INFECTION));
					} else {
						reinfectionInfoLabel.setDescription(null);
						reinfectionInfoLabel.setVisible(false);
					}

					reinfectionDetailsLeftLayout.setVisible(isVisibleAllowed(CaseDataDto.RE_INFECTION));
					reinfectionDetailsRightLayout.setVisible(isVisibleAllowed(CaseDataDto.RE_INFECTION));
				} else {
					reinfectionInfoLabel.setDescription(null);
					reinfectionInfoLabel.setVisible(false);
					reinfectionDetailsLeftLayout.setVisible(false);
					reinfectionDetailsRightLayout.setVisible(false);
				}
			});
		}

		addField(CaseDataDto.QUARANTINE_HOME_POSSIBLE, NullableOptionGroup.class);
		addField(CaseDataDto.QUARANTINE_HOME_POSSIBLE_COMMENT, TextField.class);
		addField(CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED, NullableOptionGroup.class);
		addField(CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT, TextField.class);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(CaseDataDto.QUARANTINE_FROM, CaseDataDto.QUARANTINE_TO, CaseDataDto.QUARANTINE_HELP_NEEDED),
			CaseDataDto.QUARANTINE,
			QuarantineType.QUARANTINE_IN_EFFECT,
			true);
		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY) || isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.QUARANTINE_ORDERED_VERBALLY, CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT),
				CaseDataDto.QUARANTINE,
				QuarantineType.QUARANTINE_IN_EFFECT,
				true);
		}
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_HOME_POSSIBLE_COMMENT,
			CaseDataDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			CaseDataDto.QUARANTINE_HOME_POSSIBLE,
			Arrays.asList(YesNoUnknown.YES),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED_COMMENT,
			CaseDataDto.QUARANTINE_HOME_SUPPLY_ENSURED,
			Arrays.asList(YesNoUnknown.NO),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), CaseDataDto.QUARANTINE_TYPE_DETAILS, CaseDataDto.QUARANTINE, Arrays.asList(QuarantineType.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_ORDERED_VERBALLY_DATE,
			CaseDataDto.QUARANTINE_ORDERED_VERBALLY,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
			CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
			Arrays.asList(Boolean.TRUE),
			true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE,
			CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT,
			Collections.singletonList(Boolean.TRUE),
			true);

		final UserField surveillanceOfficerField = addField(CaseDataDto.SURVEILLANCE_OFFICER, UserField.class);
		surveillanceOfficerField.setEnabled(true);

		differentPlaceOfStayJurisdiction = addCustomField(DIFFERENT_PLACE_OF_STAY_JURISDICTION, Boolean.class, CheckBox.class);
		differentPlaceOfStayJurisdiction.addStyleName(VSPACE_3);

		regionCombo = addInfrastructureField(CaseDataDto.REGION);
		districtCombo = addInfrastructureField(CaseDataDto.DISTRICT);
		communityCombo = addInfrastructureField(CaseDataDto.COMMUNITY);
		communityCombo.setNullSelectionAllowed(true);
		communityCombo.addStyleName(SOFT_REQUIRED);

		FieldHelper.setVisibleWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo, communityCombo),
			Collections.singletonList(Boolean.TRUE),
			true);

		FieldHelper.setRequiredWhen(
			differentPlaceOfStayJurisdiction,
			Arrays.asList(regionCombo, districtCombo),
			Collections.singletonList(Boolean.TRUE),
			false,
			null);

		Label placeOfStayHeadingLabel = new Label(I18nProperties.getCaption(Captions.casePlaceOfStay));
		placeOfStayHeadingLabel.addStyleName(H3);
		getContent().addComponent(placeOfStayHeadingLabel, PLACE_OF_STAY_HEADING_LOC);

		facilityOrHome = new OptionGroup(I18nProperties.getCaption(Captions.casePlaceOfStay), TypeOfPlace.FOR_CASES);
		facilityOrHome.setId("facilityOrHome");
		facilityOrHome.setWidth(100, Unit.PERCENTAGE);
		CssStyles.style(facilityOrHome, ValoTheme.OPTIONGROUP_HORIZONTAL);
		getContent().addComponent(facilityOrHome, FACILITY_OR_HOME_LOC);

		facilityTypeGroup = ComboBoxHelper.createComboBoxV7();
		facilityTypeGroup.setId("typeGroup");
		facilityTypeGroup.setCaption(I18nProperties.getCaption(Captions.Facility_typeGroup));
		facilityTypeGroup.setWidth(100, Unit.PERCENTAGE);
		facilityTypeGroup.addItems(FacilityTypeGroup.getAccomodationGroups());
		facilityTypeGroup.setVisible(false);
		getContent().addComponent(facilityTypeGroup, TYPE_GROUP_LOC);
		facilityTypeCombo = addField(CaseDataDto.FACILITY_TYPE, ComboBoxWithPlaceholder.class);
		facilityCombo = addInfrastructureField(CaseDataDto.HEALTH_FACILITY);
		facilityCombo.setImmediate(true);
		facilityDetails = addField(CaseDataDto.HEALTH_FACILITY_DETAILS, TextField.class);
		facilityDetails.setVisible(false);

		regionCombo.addValueChangeListener(e -> {
			RegionReferenceDto regionDto = (RegionReferenceDto) e.getProperty().getValue();
			FieldHelper
				.updateItems(districtCombo, regionDto != null ? FacadeProvider.getDistrictFacade().getAllActiveByRegion(regionDto.getUuid()) : null);
		});
		districtCombo.addValueChangeListener(e -> {
			DistrictReferenceDto districtDto = (DistrictReferenceDto) e.getProperty().getValue();
			FieldHelper.updateItems(
				communityCombo,
				districtDto != null ? FacadeProvider.getCommunityFacade().getAllActiveByDistrict(districtDto.getUuid()) : null);
			updateFacility();
		});
		communityCombo.addValueChangeListener(e -> updateFacility());

		facilityOrHome.addValueChangeListener(e -> {
			FieldHelper.removeItems(facilityCombo);
			if (TypeOfPlace.FACILITY.equals(facilityOrHome.getValue())) {
				// switched from home to facility
				// default values
				if (facilityTypeGroup.getValue() == null && !facilityTypeGroup.isReadOnly()) {
					facilityTypeGroup.setValue(FacilityTypeGroup.MEDICAL_FACILITY);
				}
				if (facilityTypeCombo.getValue() == null
					&& FacilityTypeGroup.MEDICAL_FACILITY.equals(facilityTypeGroup.getValue())
					&& !facilityTypeCombo.isReadOnly()) {
					facilityTypeCombo.setValue(FacilityType.HOSPITAL);
				}
				if (facilityTypeCombo.getValue() != null) {
					updateFacility();
				}

				if (CaseOrigin.IN_COUNTRY.equals(getField(CaseDataDto.CASE_ORIGIN).getValue())) {
					facilityCombo.setRequired(true);
				}
				updateFacilityDetails(facilityCombo, facilityDetails);
			} else {
				// switched from facility to home
				if (!facilityCombo.isReadOnly()) {
					FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
					facilityCombo.addItem(noFacilityRef);
					facilityCombo.setValue(noFacilityRef);
				}
				facilityTypeGroup.clear();
				facilityTypeCombo.clear();
			}
		});
		facilityTypeGroup.addValueChangeListener(
			e -> FieldHelper.updateEnumData(facilityTypeCombo, FacilityType.getAccommodationTypes((FacilityTypeGroup) facilityTypeGroup.getValue())));
		facilityTypeCombo.addValueChangeListener(e -> updateFacility());
		facilityCombo.addValueChangeListener(e -> updateFacilityDetails(facilityCombo, facilityDetails));
		regionCombo.addItems(FacadeProvider.getRegionFacade().getAllActiveByServerCountry());

		if (!FacadeProvider.getFeatureConfigurationFacade().isFeatureDisabled(FeatureType.NATIONAL_CASE_SHARING)) {
			addField(CaseDataDto.SHARED_TO_COUNTRY, CheckBox.class);
			setReadOnly(!UserProvider.getCurrent().hasUserRight(UserRight.CASE_SHARE), CaseDataDto.SHARED_TO_COUNTRY);
		}

		ComboBox pointOfEntry = addInfrastructureField(CaseDataDto.POINT_OF_ENTRY, false);
		addField(CaseDataDto.POINT_OF_ENTRY_DETAILS, TextField.class);

		Button btnReferFromPointOfEntry = ButtonHelper.createButton(Captions.caseReferFromPointOfEntry);
		getContent().addComponent(btnReferFromPointOfEntry, CASE_REFER_POINT_OF_ENTRY_BTN_LOC);

		addField(CaseDataDto.PROHIBITION_TO_WORK, NullableOptionGroup.class).addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		DateField prohibitionToWorkFrom = addField(CaseDataDto.PROHIBITION_TO_WORK_FROM, DateField.class);
		DateField prohibitionToWorkUntil = addDateField(CaseDataDto.PROHIBITION_TO_WORK_UNTIL, DateField.class, -1);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(CaseDataDto.PROHIBITION_TO_WORK_FROM, CaseDataDto.PROHIBITION_TO_WORK_UNTIL),
			CaseDataDto.PROHIBITION_TO_WORK,
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

		AccessibleTextField tfReportLat = addField(CaseDataDto.REPORT_LAT, AccessibleTextField.class);
		tfReportLat.setConverter(new StringToAngularLocationConverter());
		AccessibleTextField tfReportLon = addField(CaseDataDto.REPORT_LON, AccessibleTextField.class);
		tfReportLon.setConverter(new StringToAngularLocationConverter());
		addField(CaseDataDto.REPORT_LAT_LON_ACCURACY, TextField.class);

		dfFollowUpUntil = null;
		cbOverwriteFollowUpUntil = null;
		if (caseFollowUpEnabled) {
			addField(CaseDataDto.FOLLOW_UP_STATUS, ComboBox.class);
			addField(CaseDataDto.FOLLOW_UP_STATUS_CHANGE_DATE);
			addField(CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER);
			addField(CaseDataDto.FOLLOW_UP_COMMENT, TextArea.class).setRows(3);
			dfFollowUpUntil = addDateField(CaseDataDto.FOLLOW_UP_UNTIL, DateField.class, -1);
			dfFollowUpUntil.addValueChangeListener(v -> onFollowUpUntilChanged());
			tfExpectedFollowUpUntilDate = new TextField();
			tfExpectedFollowUpUntilDate.setCaption(I18nProperties.getCaption(Captions.CaseData_expectedFollowUpUntil));
			getContent().addComponent(tfExpectedFollowUpUntilDate, EXPECTED_FOLLOW_UP_UNTIL_DATE_LOC);
			cbOverwriteFollowUpUntil = addField(CaseDataDto.OVERWRITE_FOLLOW_UP_UNTIL, CheckBox.class);

			setReadOnly(true, CaseDataDto.FOLLOW_UP_STATUS, CaseDataDto.FOLLOW_UP_STATUS_CHANGE_DATE, CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER);

			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				CaseDataDto.FOLLOW_UP_STATUS,
				Arrays.asList(CaseDataDto.FOLLOW_UP_COMMENT),
				Arrays.asList(FollowUpStatus.CANCELED, FollowUpStatus.LOST));
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				CaseDataDto.OVERWRITE_FOLLOW_UP_UNTIL,
				Arrays.asList(CaseDataDto.FOLLOW_UP_UNTIL),
				Arrays.asList(Boolean.TRUE));
			FieldHelper.setVisibleWhenSourceNotNull(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.FOLLOW_UP_STATUS_CHANGE_DATE, CaseDataDto.FOLLOW_UP_STATUS_CHANGE_USER),
				CaseDataDto.FOLLOW_UP_STATUS_CHANGE_DATE,
				true);
		}
		if (cbOverwriteFollowUpUntil != null) {
			cbOverwriteFollowUpUntil.addValueChangeListener(e -> {
				if (!Boolean.TRUE.equals(e.getProperty().getValue())) {
					dfFollowUpUntil.discard();
					if (expectedFollowUpPeriodDto != null && expectedFollowUpPeriodDto.getFollowUpEndDate() != null) {
						dfFollowUpUntil.setValue(expectedFollowUpPeriodDto.getFollowUpEndDate());
					}
				}
			});
			FieldHelper.setReadOnlyWhen(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.FOLLOW_UP_UNTIL),
				CaseDataDto.OVERWRITE_FOLLOW_UP_UNTIL,
				Arrays.asList(Boolean.FALSE),
				false,
				true);
		}
		dfQuarantineTo.addValueChangeListener(e -> onQuarantineEndChange());
		this.addValueChangeListener(e -> onValueChange());
		Label generalCommentLabel = new Label(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.ADDITIONAL_DETAILS));
		generalCommentLabel.addStyleName(H3);
		getContent().addComponent(generalCommentLabel, GENERAL_COMMENT_LOC);

		TextArea additionalDetails = addField(CaseDataDto.ADDITIONAL_DETAILS, TextArea.class);
		additionalDetails.setRows(6);
		additionalDetails.setDescription(
			I18nProperties.getPrefixDescription(CaseDataDto.I18N_PREFIX, CaseDataDto.ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));
		CssStyles.style(additionalDetails, CssStyles.CAPTION_HIDDEN);

		addField(CaseDataDto.PREGNANT, NullableOptionGroup.class);
		addField(CaseDataDto.POSTPARTUM, NullableOptionGroup.class);
		addField(CaseDataDto.TRIMESTER, NullableOptionGroup.class);

		addField(CaseDataDto.VACCINATION_STATUS);
		addFields(CaseDataDto.SMALLPOX_VACCINATION_SCAR, CaseDataDto.SMALLPOX_VACCINATION_RECEIVED);
		addDateField(CaseDataDto.SMALLPOX_LAST_VACCINATION_DATE, DateField.class, 0);

		// Swiss fields
		AccessibleTextField caseIdIsmField = addField(CaseDataDto.CASE_ID_ISM, AccessibleTextField.class);
		caseIdIsmField.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, caseIdIsmField.getCaption()));

		if (fieldVisibilityCheckers.isVisible(CaseDataDto.class, CaseDataDto.CONTACT_TRACING_FIRST_CONTACT_TYPE)) {
			Label contactTracingFirstContactHeadingLabel = new Label(I18nProperties.getString(Strings.headingContactTracingFirstContact));
			contactTracingFirstContactHeadingLabel.addStyleName(H3);
			getContent().addComponent(contactTracingFirstContactHeadingLabel, CONTACT_TRACING_FIRST_CONTACT_HEADER_LOC);

			addFields(CaseDataDto.CONTACT_TRACING_FIRST_CONTACT_TYPE, CaseDataDto.CONTACT_TRACING_FIRST_CONTACT_DATE);
		}

		addField(CaseDataDto.WAS_IN_QUARANTINE_BEFORE_ISOLATION).setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
		addFields(CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION, CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION_DETAILS,
			CaseDataDto.QUARANTINE_REASON_BEFORE_ISOLATION,
			Arrays.asList(QuarantineReason.OTHER_REASON),
			true);

		addFields(CaseDataDto.END_OF_ISOLATION_REASON, CaseDataDto.END_OF_ISOLATION_REASON_DETAILS);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CaseDataDto.END_OF_ISOLATION_REASON_DETAILS,
			CaseDataDto.END_OF_ISOLATION_REASON,
			Arrays.asList(EndOfIsolationReason.OTHER),
			true);

		// jurisdiction fields
		Label jurisdictionHeadingLabel = new Label(I18nProperties.getString(Strings.headingCaseResponsibleJurisidction));
		jurisdictionHeadingLabel.addStyleName(H3);
		getContent().addComponent(jurisdictionHeadingLabel, RESPONSIBLE_JURISDICTION_HEADING_LOC);

		responsibleRegion = addInfrastructureField(CaseDataDto.RESPONSIBLE_REGION);
		responsibleRegion.setRequired(true);
		responsibleDistrict = addInfrastructureField(CaseDataDto.RESPONSIBLE_DISTRICT);
		responsibleDistrict.setRequired(true);
		responsibleCommunity = addInfrastructureField(CaseDataDto.RESPONSIBLE_COMMUNITY);
		responsibleCommunity.setNullSelectionAllowed(true);
		responsibleCommunity.addStyleName(SOFT_REQUIRED);

		InfrastructureFieldsHelper.initInfrastructureFields(responsibleRegion, responsibleDistrict, responsibleCommunity);
		InfrastructureFieldsHelper.initPointOfEntry(responsibleDistrict, pointOfEntry);

		responsibleDistrict.addValueChangeListener(e -> {
			Boolean differentPlaceOfStay = differentPlaceOfStayJurisdiction.getValue();
			if (differentPlaceOfStay == null || Boolean.FALSE.equals(differentPlaceOfStay)) {
				updateFacility();
			}
		});
		responsibleCommunity.addValueChangeListener((e) -> {
			Boolean differentPlaceOfStay = differentPlaceOfStayJurisdiction.getValue();
			if (differentPlaceOfStay == null || Boolean.FALSE.equals(differentPlaceOfStay)) {
				updateFacility();
			}
		});

		differentPlaceOfStayJurisdiction.addValueChangeListener(e -> {
			if (!ignoreDifferentPlaceOfStayJurisdiction) {
				updateFacility();
			}
		});

		// Set initial visibilities & accesses
		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		// Set requirements that don't need visibility changes and read only status
		setRequired(
			true,
			CaseDataDto.REPORT_DATE,
			CaseDataDto.CASE_CLASSIFICATION,
			CaseDataDto.INVESTIGATION_STATUS,
			CaseDataDto.OUTCOME,
			CaseDataDto.DISEASE);
		setSoftRequired(true, CaseDataDto.INVESTIGATED_DATE, CaseDataDto.OUTCOME_DATE, CaseDataDto.PLAGUE_TYPE, CaseDataDto.SURVEILLANCE_OFFICER);
		if (isEditableAllowed(CaseDataDto.INVESTIGATED_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.INVESTIGATED_DATE,
				CaseDataDto.INVESTIGATION_STATUS,
				Arrays.asList(InvestigationStatus.DONE, InvestigationStatus.DISCARDED),
				true);
		}
		setReadOnly(
			true,
			CaseDataDto.UUID,
			CaseDataDto.REPORTING_USER,
			CaseDataDto.CLASSIFICATION_USER,
			CaseDataDto.CLASSIFICATION_DATE,
			CaseDataDto.POINT_OF_ENTRY,
			CaseDataDto.POINT_OF_ENTRY_DETAILS,
			CaseDataDto.CASE_ORIGIN);

		setReadOnly(!UserProvider.getCurrent().hasUserRight(UserRight.CASE_CHANGE_DISEASE), CaseDataDto.DISEASE);
		setReadOnly(
			!UserProvider.getCurrent().hasUserRight(UserRight.CASE_INVESTIGATE),
			CaseDataDto.INVESTIGATION_STATUS,
			CaseDataDto.INVESTIGATED_DATE);
		setReadOnly(
			!UserProvider.getCurrent().hasUserRight(UserRight.CASE_CLASSIFY),
			CaseDataDto.CASE_CLASSIFICATION,
			CaseDataDto.OUTCOME,
			CaseDataDto.OUTCOME_DATE);
		setReadOnly(
			!UserProvider.getCurrent().hasUserRight(UserRight.CASE_TRANSFER),
			CaseDataDto.RESPONSIBLE_REGION,
			CaseDataDto.RESPONSIBLE_DISTRICT,
			CaseDataDto.RESPONSIBLE_COMMUNITY,
			DIFFERENT_PLACE_OF_STAY_JURISDICTION,
			CaseDataDto.REGION,
			CaseDataDto.DISTRICT,
			CaseDataDto.COMMUNITY,
			FACILITY_OR_HOME_LOC,
			TYPE_GROUP_LOC,
			CaseDataDto.FACILITY_TYPE,
			CaseDataDto.HEALTH_FACILITY,
			CaseDataDto.HEALTH_FACILITY_DETAILS);

		if (!isEditableAllowed(CaseDataDto.COMMUNITY)) {
			setEnabled(false, CaseDataDto.REGION, CaseDataDto.DISTRICT);
		}

		if (!isEditableAllowed(CaseDataDto.RESPONSIBLE_COMMUNITY)) {
			setEnabled(false, CaseDataDto.RESPONSIBLE_REGION, CaseDataDto.RESPONSIBLE_DISTRICT);
		}

		if (UserProvider.getCurrent().getJurisdictionLevel() == JurisdictionLevel.HEALTH_FACILITY || !isEditableAllowed(CaseDataDto.COMMUNITY)) {
			differentPlaceOfStayJurisdiction.setEnabled(false);
			differentPlaceOfStayJurisdiction.setVisible(false);
		}

		FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.TRIMESTER, CaseDataDto.PREGNANT, Arrays.asList(YesNoUnknown.YES), true);

		diseaseField.addValueChangeListener((ValueChangeListener) valueChangeEvent -> {
			Disease disease = (Disease) valueChangeEvent.getProperty().getValue();
			List<DiseaseVariant> diseaseVariants =
				FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
			FieldHelper.updateItems(diseaseVariantField, diseaseVariants);
			diseaseVariantField
				.setVisible(disease != null && isVisibleAllowed(CaseDataDto.DISEASE_VARIANT) && CollectionUtils.isNotEmpty(diseaseVariants));
		});
		diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant diseaseVariant = (DiseaseVariant) e.getProperty().getValue();
			diseaseVariantDetailsField.setVisible(diseaseVariant != null && diseaseVariant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true));
		});
		if (isVisibleAllowed(CaseDataDto.DISEASE_DETAILS)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.DISEASE_DETAILS), CaseDataDto.DISEASE, Arrays.asList(Disease.OTHER), true);
			FieldHelper
				.setRequiredWhen(getFieldGroup(), CaseDataDto.DISEASE, Arrays.asList(CaseDataDto.DISEASE_DETAILS), Arrays.asList(Disease.OTHER));
		}
		if (isVisibleAllowed(CaseDataDto.PLAGUE_TYPE)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.PLAGUE_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.PLAGUE), true);
		}
		if (isVisibleAllowed(CaseDataDto.DENGUE_FEVER_TYPE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.DENGUE_FEVER_TYPE),
				CaseDataDto.DISEASE,
				Arrays.asList(Disease.DENGUE),
				true);
		}
		if (isVisibleAllowed(CaseDataDto.RABIES_TYPE)) {
			FieldHelper
				.setVisibleWhen(getFieldGroup(), Arrays.asList(CaseDataDto.RABIES_TYPE), CaseDataDto.DISEASE, Arrays.asList(Disease.RABIES), true);
		}
		if (isVisibleAllowed(CaseDataDto.SMALLPOX_VACCINATION_SCAR)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.SMALLPOX_VACCINATION_SCAR,
				CaseDataDto.SMALLPOX_VACCINATION_RECEIVED,
				Arrays.asList(YesNoUnknown.YES),
				true);
		}

		if (isVisibleAllowed(CaseDataDto.SMALLPOX_LAST_VACCINATION_DATE)) {
			if (isVisibleAllowed(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED)) {
				FieldHelper.setVisibleWhen(
					getFieldGroup(),
					CaseDataDto.SMALLPOX_LAST_VACCINATION_DATE,
					CaseDataDto.SMALLPOX_VACCINATION_RECEIVED,
					Collections.singletonList(YesNoUnknown.YES),
					true);
			}
		}

		if (isVisibleAllowed(CaseDataDto.OUTCOME_DATE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.OUTCOME_DATE,
				CaseDataDto.OUTCOME,
				Arrays.asList(CaseOutcome.DECEASED, CaseOutcome.RECOVERED, CaseOutcome.UNKNOWN),
				true);
		}
		if (isVisibleAllowed(CaseDataDto.SEQUELAE)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.SEQUELAE,
				CaseDataDto.OUTCOME,
				Arrays.asList(CaseOutcome.RECOVERED, CaseOutcome.UNKNOWN),
				true);
		}
		if (isVisibleAllowed(CaseDataDto.SEQUELAE_DETAILS)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), CaseDataDto.SEQUELAE_DETAILS, CaseDataDto.SEQUELAE, Arrays.asList(YesNoUnknown.YES), true);
		}
		if (isVisibleAllowed(CaseDataDto.NOTIFYING_CLINIC_DETAILS)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				CaseDataDto.NOTIFYING_CLINIC_DETAILS,
				CaseDataDto.NOTIFYING_CLINIC,
				Arrays.asList(HospitalWardType.OTHER),
				true);
		}
		FieldHelper.setVisibleWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityTypeCombo, facilityCombo),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false);
		FieldHelper.setRequiredWhen(
			facilityOrHome,
			Arrays.asList(facilityTypeGroup, facilityTypeCombo, facilityCombo),
			Collections.singletonList(TypeOfPlace.FACILITY),
			false,
			null);

		/// CLINICIAN FIELDS
		if (isVisibleAllowed(CaseDataDto.CLINICIAN_NAME)) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				Arrays.asList(CaseDataDto.CLINICIAN_NAME, CaseDataDto.CLINICIAN_PHONE, CaseDataDto.CLINICIAN_EMAIL),
				CaseDataDto.FACILITY_TYPE,
				Arrays.asList(FacilityType.HOSPITAL, FacilityType.OTHER_MEDICAL_FACILITY),
				true);
		}

		// Other initializations
		if (disease == Disease.MONKEYPOX) {
			Image smallpoxVaccinationScarImg = new Image(null, new ThemeResource("img/smallpox-vaccination-scar.jpg"));
			style(smallpoxVaccinationScarImg, VSPACE_3);
			getContent().addComponent(smallpoxVaccinationScarImg, SMALLPOX_VACCINATION_SCAR_IMG);

			// Set up initial image visibility
			getContent().getComponent(SMALLPOX_VACCINATION_SCAR_IMG)
				.setVisible(getFieldGroup().getField(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED).getValue() == YesNoUnknown.YES);

			// Set up image visibility listener
			getFieldGroup().getField(CaseDataDto.SMALLPOX_VACCINATION_RECEIVED)
				.addValueChangeListener(
					e -> getContent().getComponent(SMALLPOX_VACCINATION_SCAR_IMG).setVisible(e.getProperty().getValue() == YesNoUnknown.YES));
		}

		List<String> medicalInformationFields =
			Arrays.asList(CaseDataDto.PREGNANT, CaseDataDto.VACCINATION_STATUS, CaseDataDto.SMALLPOX_VACCINATION_RECEIVED);

		addField(CaseDataDto.HEALTH_CONDITIONS, HealthConditionsForm.class).setCaption(null);

		for (String medicalInformationField : medicalInformationFields) {
			if (getFieldGroup().getField(medicalInformationField).isVisible()) {
				Label medicalInformationCaptionLabel = new Label(I18nProperties.getString(Strings.headingMedicalInformation));
				medicalInformationCaptionLabel.addStyleName(H3);
				getContent().addComponent(medicalInformationCaptionLabel, MEDICAL_INFORMATION_LOC);
				break;
			}
		}

		if (!shouldHidePaperFormDates()) {
			Label paperFormDatesLabel = new Label(I18nProperties.getString(Strings.headingPaperFormDates));
			paperFormDatesLabel.addStyleName(H3);
			getContent().addComponent(paperFormDatesLabel, PAPER_FORM_DATES_LOC);
		}

		// Automatic case classification rules button - invisible for other diseases
		DiseaseClassificationCriteriaDto diseaseClassificationCriteria = FacadeProvider.getCaseClassificationFacade().getByDisease(disease);
		if (diseaseClassificationExists()) {
			Button classificationRulesButton = ButtonHelper.createIconButton(
				Captions.info,
				VaadinIcons.INFO_CIRCLE,
				e -> ControllerProvider.getCaseController().openClassificationRulesPopup(diseaseClassificationCriteria),
				ValoTheme.BUTTON_PRIMARY,
				FORCE_CAPTION);

			getContent().addComponent(classificationRulesButton, CLASSIFICATION_RULES_LOC);
		}

		addField(CaseDataDto.DELETION_REASON);
		addField(CaseDataDto.OTHER_DELETION_REASON, TextArea.class).setRows(3);
		setVisible(false, CaseDataDto.DELETION_REASON, CaseDataDto.OTHER_DELETION_REASON);

		addValueChangeListener(e -> {
			diseaseField.addValueChangeListener(new DiseaseChangeListener(diseaseField, getValue().getDisease()));

			FieldHelper.updateOfficersField(surveillanceOfficerField, getValue(), UserRight.CASE_RESPONSIBLE);

			// Replace classification user if case has been automatically classified
			if (getValue().getClassificationDate() != null && getValue().getClassificationUser() == null) {
				getField(CaseDataDto.CLASSIFICATION_USER).setVisible(false);
				Label classifiedBySystemLabel = new Label(I18nProperties.getCaption(Captions.system));
				classifiedBySystemLabel.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CLASSIFIED_BY));
				// ensure correct formatting
				GridLayout tempLayout = new GridLayout();
				tempLayout.addComponent(classifiedBySystemLabel);
				getContent().addComponent(tempLayout, CLASSIFIED_BY_SYSTEM_LOC);
			}

			updateFollowUpStatusComponents();

			setEpidNumberError(epidField, assignNewEpidNumberButton, epidNumberWarningLabel, getValue().getEpidNumber());

			epidField.addValueChangeListener(f -> {
				setEpidNumberError(epidField, assignNewEpidNumberButton, epidNumberWarningLabel, (String) f.getProperty().getValue());
			});

			ValidationUtils.initComponentErrorValidator(
				externalTokenField,
				getValue().getExternalToken(),
				Validations.duplicateExternalToken,
				externalTokenWarningLabel,
				(externalToken) -> FacadeProvider.getCaseFacade().doesExternalTokenExist(externalToken, getValue().getUuid()));

			updateFacilityOrHome();

			// Set health facility/point of entry visibility based on case origin
			if (getValue().getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY) {
				setVisible(true, CaseDataDto.POINT_OF_ENTRY);
				if (getValue().getPointOfEntry() != null) {
					setVisible(getValue().getPointOfEntry().isOtherPointOfEntry(), CaseDataDto.POINT_OF_ENTRY_DETAILS);
					btnReferFromPointOfEntry
						.setVisible(UserProvider.getCurrent().hasUserRight(UserRight.CASE_REFER_FROM_POE) && getValue().getHealthFacility() == null);
				} else if (!isEditableAllowed(CaseDataDto.POINT_OF_ENTRY)) {
					setVisible(false, CaseDataDto.POINT_OF_ENTRY_DETAILS);
					btnReferFromPointOfEntry.setVisible(false);
				}

				if (getValue().getHealthFacility() == null) {
					setVisible(
						false,
						DIFFERENT_PLACE_OF_STAY_JURISDICTION,
						CaseDataDto.COMMUNITY,
						FACILITY_OR_HOME_LOC,
						TYPE_GROUP_LOC,
						CaseDataDto.FACILITY_TYPE,
						CaseDataDto.HEALTH_FACILITY,
						CaseDataDto.HEALTH_FACILITY_DETAILS);
					setReadOnly(true, CaseDataDto.REGION, CaseDataDto.DISTRICT, CaseDataDto.COMMUNITY);
				}
			} else {
				facilityOrHome.setRequired(true);
				setVisible(false, CaseDataDto.POINT_OF_ENTRY, CaseDataDto.POINT_OF_ENTRY_DETAILS);
				btnReferFromPointOfEntry.setVisible(false);
			}

			// Hide case origin from port health users
			if (UserProvider.getCurrent().isPortHealthUser()) {
				setVisible(false, CaseDataDto.CASE_ORIGIN);
			}

			if (caseFollowUpEnabled) {
				// Add follow-up until validator
				List<SampleDto> samples = Collections.emptyList();
				if (UserProvider.getCurrent().hasAllUserRights(UserRight.SAMPLE_VIEW)) {
					samples = FacadeProvider.getSampleFacade().getByCaseUuids(Collections.singletonList(caseUuid));
				}
				FollowUpPeriodDto followUpPeriod = CaseLogic.getFollowUpStartDate(symptoms.getOnsetDate(), reportDate.getValue(), samples);
				Date minimumFollowUpUntilDate =
					FollowUpLogic
						.calculateFollowUpUntilDate(
							followUpPeriod,
							null,
							FacadeProvider.getVisitFacade().getVisitsByCase(new CaseReferenceDto(caseUuid)),
							FacadeProvider.getDiseaseConfigurationFacade().getCaseFollowUpDuration((Disease) diseaseField.getValue()),
							FacadeProvider.getFeatureConfigurationFacade()
								.isPropertyValueTrue(FeatureType.CASE_FOLLOWUP, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE))
						.getFollowUpEndDate();

				if (FacadeProvider.getFeatureConfigurationFacade()
					.isPropertyValueTrue(FeatureType.CASE_FOLLOWUP, FeatureTypeProperty.ALLOW_FREE_FOLLOW_UP_OVERWRITE)) {
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
										I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.FOLLOW_UP_UNTIL));
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
					CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT,
					CaseDataDto.QUARANTINE_ORDERED_OFFICIAL_DOCUMENT_DATE,
					CaseDataDto.QUARANTINE_ORDERED_VERBALLY,
					CaseDataDto.QUARANTINE_ORDERED_VERBALLY_DATE,
					CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT,
					CaseDataDto.QUARANTINE_OFFICIAL_ORDER_SENT_DATE);
			}

			// Make external ID field read-only when SORMAS is connected to a SurvNet instance
			if (StringUtils.isNotEmpty(FacadeProvider.getConfigFacade().getExternalSurveillanceToolGatewayUrl())) {
				setEnabled(false, CaseDataDto.EXTERNAL_ID);
				((TextField) getField(CaseDataDto.EXTERNAL_ID))
					.setInputPrompt(I18nProperties.getString(Strings.promptExternalIdExternalSurveillanceTool));
			}

			for (CaseReinfectionCheckBoxTree reinfectionTree : reinfectionTrees.values()) {
				reinfectionTree.initCheckboxes();
			}
		});
	}

	private void hideJurisdictionFields() {
		getField(CaseDataDto.CASE_ORIGIN).setVisible(false);
		getContent().getComponent(RESPONSIBLE_JURISDICTION_HEADING_LOC).setVisible(false);
		getContent().getComponent(PLACE_OF_STAY_HEADING_LOC).setVisible(false);
		differentPlaceOfStayJurisdiction.setVisible(false);

		responsibleRegion.setVisible(false);
		responsibleDistrict.setVisible(false);
		responsibleCommunity.setVisible(false);

		regionCombo.setVisible(false);
		districtCombo.setVisible(false);
		communityCombo.setVisible(false);
	}

	private void updateFacilityOrHome() {
		if (getValue().getHealthFacility() != null) {
			boolean facilityOrHomeReadOnly = facilityOrHome.isReadOnly();
			boolean facilityTypeGroupReadOnly = facilityTypeGroup.isReadOnly();
			facilityOrHome.setReadOnly(false);
			facilityTypeGroup.setReadOnly(false);
			boolean noneHealthFacility = getValue().getHealthFacility().getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

			FacilityType caseFacilityType = getValue().getFacilityType();
			if (noneHealthFacility || caseFacilityType == null) {
				facilityOrHome.setValue(TypeOfPlace.HOME);
			} else {
				facilityOrHome.setValue(TypeOfPlace.FACILITY);
				facilityTypeGroup.setValue(caseFacilityType.getFacilityTypeGroup());
				if (!facilityTypeCombo.isReadOnly()) {
					facilityTypeCombo.setValue(caseFacilityType);
				}
			}

			facilityOrHome.setReadOnly(facilityOrHomeReadOnly);
			facilityTypeGroup.setReadOnly(facilityTypeGroupReadOnly);
		} else if (getValue().isPseudonymized()) {
			facilityOrHome.setValue(null);
			facilityOrHome.setReadOnly(true);

			facilityTypeGroup.setVisible(true);
			FieldHelper.setComboInaccessible(facilityTypeGroup);

			setVisible(true, facilityTypeCombo, facilityCombo);
			FieldHelper.setComboInaccessible(facilityTypeCombo);
		} else {
			facilityOrHome.setVisible(false);
		}
	}

	private boolean diseaseClassificationExists() {
		DiseaseClassificationCriteriaDto diseaseClassificationCriteria = FacadeProvider.getCaseClassificationFacade().getByDisease(disease);
		return disease != Disease.OTHER && diseaseClassificationCriteria != null;
	}

	private void onFollowUpUntilChanged() {
		Date newFollowUpUntil = dfFollowUpUntil.getValue();
		CaseDataDto originalCase = getInternalValue();
		Date oldFollowUpUntil = originalCase.getFollowUpUntil();
		Date oldQuarantineEnd = originalCase.getQuarantineTo();
		if (shouldAdjustQuarantine(dfQuarantineTo, newFollowUpUntil, oldFollowUpUntil)) {
			VaadinUiUtil.showConfirmationPopup(
				I18nProperties.getString(Strings.headingAdjustQuarantine),
				new Label(I18nProperties.getString(Strings.confirmationAlsoAdjustQuarantine)),
				I18nProperties.getString(Strings.yes),
				I18nProperties.getString(Strings.no),
				640,
				confirmed -> {
					if (confirmed) {
						quarantineChangedByFollowUpUntilChange = true;
						dfQuarantineTo.setValue(newFollowUpUntil);
						if (oldQuarantineEnd != null) {
							boolean isQuarantineExtended = dfQuarantineTo.getValue().after(oldQuarantineEnd);
							cbQuarantineExtended.setValue(isQuarantineExtended);
							cbQuarantineReduced.setValue(!isQuarantineExtended);
							setVisible(isQuarantineExtended, CaseDataDto.QUARANTINE_EXTENDED);
							setVisible(!isQuarantineExtended, CaseDataDto.QUARANTINE_REDUCED);
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

	// This logic should be consistent with CaseFacadeEjb.onCaseChanged
	private void onQuarantineEndChange() {
		if (quarantineChangedByFollowUpUntilChange) {
			quarantineChangedByFollowUpUntilChange = false;
		} else {
			Date newQuarantineEnd = dfQuarantineTo.getValue();
			CaseDataDto originalCase = getInternalValue();
			Date oldQuarantineEnd = originalCase.getQuarantineTo();
			ExtendedReduced changeType = null;
			if (oldQuarantineEnd != null && newQuarantineEnd != null) {
				changeType = newQuarantineEnd.after(oldQuarantineEnd)
					? ExtendedReduced.EXTENDED
					: (newQuarantineEnd.before(oldQuarantineEnd) ? ExtendedReduced.REDUCED : null);
			}
			if (changeType != null) {
				confirmQuarantineEndChanged(changeType, originalCase);
			} else {
				resetPreviousQuarantineTo(originalCase);
			}
		}
	}

	private void confirmQuarantineEndChanged(ExtendedReduced changeType, CaseDataDto originalCase) {
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
				Date quarantineTo = originalCase.getQuarantineTo();
				if (confirmed) {
					dfPreviousQuarantineTo.setReadOnly(false);
					dfPreviousQuarantineTo.setValue(quarantineTo);
					dfPreviousQuarantineTo.setReadOnly(true);
					setVisible(true, CaseDataDto.QUARANTINE_CHANGE_COMMENT, ContactDto.PREVIOUS_QUARANTINE_TO);
					cbQuarantineExtended.setValue(isExtended);
					cbQuarantineReduced.setValue(isReduced);
					setVisible(isExtended, CaseDataDto.QUARANTINE_EXTENDED);
					setVisible(isReduced, CaseDataDto.QUARANTINE_REDUCED);
					if (caseFollowUpEnabled && isExtended && originalCase.getFollowUpUntil() != null) {
						confirmExtendFollowUpPeriod(originalCase);
					}
				} else {
					dfQuarantineTo.setValue(quarantineTo);
					resetPreviousQuarantineTo(originalCase);
				}
			});
	}

	private void resetPreviousQuarantineTo(CaseDataDto originalCase) {
		Date previousQuarantineTo = originalCase.getPreviousQuarantineTo();
		dfPreviousQuarantineTo.setReadOnly(false);
		dfPreviousQuarantineTo.setValue(previousQuarantineTo);
		dfPreviousQuarantineTo.setReadOnly(true);
		if (previousQuarantineTo == null) {
			quarantineChangeComment.setValue(null);
			setVisible(false, ContactDto.QUARANTINE_CHANGE_COMMENT, ContactDto.PREVIOUS_QUARANTINE_TO);
		}
		cbQuarantineExtended.setValue(originalCase.isQuarantineExtended());
		cbQuarantineExtended.setVisible(originalCase.isQuarantineExtended());
		cbQuarantineReduced.setValue(originalCase.isQuarantineReduced());
		cbQuarantineReduced.setVisible(originalCase.isQuarantineReduced());
	}

	private void confirmExtendFollowUpPeriod(CaseDataDto originalCase) {
		Date quarantineEnd = dfQuarantineTo.getValue();
		if (quarantineEnd.after(originalCase.getFollowUpUntil())) {
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

	@SuppressWarnings("unchecked")
	private void updateFollowUpStatusComponents() {
		if (!caseFollowUpEnabled) {
			return;
		}

		getContent().removeComponent(CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
		getContent().removeComponent(LOST_FOLLOW_UP_BTN_LOC);

		Field<FollowUpStatus> statusField = (Field<FollowUpStatus>) getField(CaseDataDto.FOLLOW_UP_STATUS);
		boolean followUpVisible = getValue() != null && statusField.isVisible();
		if (followUpVisible && UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
			FollowUpStatus followUpStatus = statusField.getValue();
			if (followUpStatus == FollowUpStatus.FOLLOW_UP) {

				Button cancelButton = ButtonHelper.createButton(Captions.contactCancelFollowUp, event -> {
					Field<FollowUpStatus> statusField1 = (Field<FollowUpStatus>) getField(CaseDataDto.FOLLOW_UP_STATUS);
					statusField1.setReadOnly(false);
					statusField1.setValue(FollowUpStatus.CANCELED);
					statusField1.setReadOnly(true);
					updateFollowUpStatusComponents();
				});
				cancelButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(cancelButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);

				Button lostButton = ButtonHelper.createButton(Captions.contactLostToFollowUp, event -> {
					Field<FollowUpStatus> statusField12 = (Field<FollowUpStatus>) getField(CaseDataDto.FOLLOW_UP_STATUS);
					statusField12.setReadOnly(false);
					statusField12.setValue(FollowUpStatus.LOST);
					statusField12.setReadOnly(true);
					updateFollowUpStatusComponents();
				});
				lostButton.setWidth(100, Unit.PERCENTAGE);
				getContent().addComponent(lostButton, LOST_FOLLOW_UP_BTN_LOC);

			} else if (followUpStatus == FollowUpStatus.CANCELED || followUpStatus == FollowUpStatus.LOST) {

				Button resumeButton = ButtonHelper.createButton(Captions.contactResumeFollowUp, event -> {
					Field<FollowUpStatus> statusField13 = (Field<FollowUpStatus>) getField(CaseDataDto.FOLLOW_UP_STATUS);
					statusField13.setReadOnly(false);
					statusField13.setValue(FollowUpStatus.FOLLOW_UP);
					statusField13.setReadOnly(true);
					updateFollowUpStatusComponents();
				}, CssStyles.FORCE_CAPTION);
				resumeButton.setWidth(100, Unit.PERCENTAGE);

				getContent().addComponent(resumeButton, CANCEL_OR_RESUME_FOLLOW_UP_BTN_LOC);
			}
		}
	}

	private Map<ReinfectionDetail, Boolean> mergeReinfectionTrees() {
		Map<ReinfectionDetail, Boolean> reinfectionDetails = new EnumMap<>(ReinfectionDetail.class);
		reinfectionTrees.values().forEach(t -> {
			if (t.getValues() != null) {
				reinfectionDetails.putAll(t.getValues());
			}
		});
		return reinfectionDetails;
	}

	@Override
	public CaseDataDto getValue() {
		final CaseDataDto caze = super.getValue();
		caze.setReinfectionDetails(mergeReinfectionTrees());
		return caze;
	}

	@Override
	public void setValue(CaseDataDto newFieldValue) throws ReadOnlyException, ConversionException {

		for (ReinfectionDetailGroup group : reinfectionTrees.keySet()) {
			if (newFieldValue.getReinfectionDetails() != null) {
				reinfectionTrees.get(group)
					.setValues(
						newFieldValue.getReinfectionDetails()
							.entrySet()
							.stream()
							.filter(e -> e.getKey().getGroup() == group)
							.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
			} else {
				reinfectionTrees.get(group).setValues(null);
			}
		}

		super.setValue(newFieldValue);

		ComboBox caseConfirmationBasisCombo = getField(CASE_CONFIRMATION_BASIS);

		if (caseConfirmationBasisCombo != null) {
			if (newFieldValue.getClinicalConfirmation() == YesNoUnknown.YES) {
				caseConfirmationBasisCombo.setValue(CaseConfirmationBasis.CLINICAL_CONFIRMATION);
			} else if (newFieldValue.getEpidemiologicalConfirmation() == YesNoUnknown.YES) {
				caseConfirmationBasisCombo.setValue(CaseConfirmationBasis.EPIDEMIOLOGICAL_CONFIRMATION);
			} else if (newFieldValue.getLaboratoryDiagnosticConfirmation() == YesNoUnknown.YES) {
				caseConfirmationBasisCombo.setValue(CaseConfirmationBasis.LABORATORY_DIAGNOSTIC_CONFIRMATION);
			}
		}

		if (caseFollowUpEnabled && UserProvider.getCurrent().hasUserRight(UserRight.CASE_EDIT)) {
			expectedFollowUpPeriodDto = FacadeProvider.getCaseFacade().calculateFollowUpUntilDate(newFieldValue, true);
			tfExpectedFollowUpUntilDate
				.setValue(DateHelper.formatLocalDate(expectedFollowUpPeriodDto.getFollowUpEndDate(), I18nProperties.getUserLanguage()));
			tfExpectedFollowUpUntilDate.setReadOnly(true);
			tfExpectedFollowUpUntilDate.setDescription(
				String.format(
					I18nProperties.getString(Strings.infoExpectedFollowUpUntilDateCase),
					expectedFollowUpPeriodDto.getFollowUpStartDateType(),
					DateHelper.formatLocalDate(expectedFollowUpPeriodDto.getFollowUpStartDate(), I18nProperties.getUserLanguage())));
		}

		updateVisibilityDifferentPlaceOfStayJurisdiction(newFieldValue);

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			hideJurisdictionFields();
		}

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		discard();
	}

	public void onDiscard() {
		ignoreDifferentPlaceOfStayJurisdiction = true;
		updateVisibilityDifferentPlaceOfStayJurisdiction(getValue());
		ignoreDifferentPlaceOfStayJurisdiction = false;
		FacilityReferenceDto healthFacility = getValue().getHealthFacility();
		String healthFacilityDetails = getValue().getHealthFacilityDetails();
		updateFacilityOrHome();
		boolean readOnlyFacility = facilityCombo.isReadOnly();
		boolean readOnlyFacilityDetails = facilityDetails.isReadOnly();
		facilityCombo.setReadOnly(false);
		facilityDetails.setReadOnly(false);
		facilityCombo.setValue(healthFacility);
		facilityDetails.setValue(healthFacilityDetails);
		facilityCombo.setReadOnly(readOnlyFacility);
		facilityDetails.setReadOnly(readOnlyFacilityDetails);
	}

	private void updateVisibilityDifferentPlaceOfStayJurisdiction(CaseDataDto newFieldValue) {
		boolean isDifferentPlaceOfStayJurisdiction =
			newFieldValue.getRegion() != null || newFieldValue.getDistrict() != null || newFieldValue.getCommunity() != null;
		boolean readOnly = differentPlaceOfStayJurisdiction.isReadOnly();
		differentPlaceOfStayJurisdiction.setReadOnly(false);
		differentPlaceOfStayJurisdiction.setValue(isDifferentPlaceOfStayJurisdiction);
		differentPlaceOfStayJurisdiction.setReadOnly(readOnly);
	}

	private void updateFacility() {
		final DistrictReferenceDto district;
		final CommunityReferenceDto community;

		if (Boolean.TRUE.equals(differentPlaceOfStayJurisdiction.getValue())) {
			district = (DistrictReferenceDto) districtCombo.getValue();
			community = (CommunityReferenceDto) communityCombo.getValue();
		} else {
			district = (DistrictReferenceDto) responsibleDistrict.getValue();
			community = (CommunityReferenceDto) responsibleCommunity.getValue();
		}

		FacilityType facilityType = (FacilityType) facilityTypeCombo.getValue();

		if (facilityType != null) {
			if (community != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade().getActiveFacilitiesByCommunityAndType(community, facilityType, true, false));
			} else if (district != null) {
				FieldHelper.updateItems(
					facilityCombo,
					FacadeProvider.getFacilityFacade().getActiveFacilitiesByDistrictAndType(district, facilityType, true, false));
			} else {
				FieldHelper.removeItems(facilityCombo);
			}
		} else {
			if (TypeOfPlace.HOME.equals(facilityOrHome.getValue())) {
				FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
				facilityCombo.addItem(noFacilityRef);
				boolean readOnly = facilityCombo.isReadOnly();
				facilityCombo.setReadOnly(false);
				facilityCombo.setValue(noFacilityRef);
				facilityCombo.setReadOnly(readOnly);
			} else {
				FieldHelper.removeItems(facilityCombo);
			}
		}
	}

	@Override
	protected String createHtmlLayout() {
		return MAIN_HTML_LAYOUT + (caseFollowUpEnabled ? FOLLOWUP_LAYOUT : "") + PAPER_FORM_DATES_AND_HEALTH_CONDITIONS_HTML_LAYOUT;
	}

	public void addButtonListener(String componentId, Button.ClickListener listener) {
		Button button = (Button) getContent().getComponent(componentId);
		button.addClickListener(listener);
	}

	private void setEpidNumberError(TextField epidField, Button assignNewEpidNumberButton, Label epidNumberWarningLabel, String fieldValue) {
		if (epidField == null) {
			return;
		}

		if (epidField.isVisible()
			&& StringUtils.isNotEmpty(fieldValue)
			&& FacadeProvider.getCaseFacade().doesEpidNumberExist(fieldValue, getValue().getUuid(), getValue().getDisease())) {
			epidField.setComponentError(new UserError(I18nProperties.getValidationError(Validations.duplicateEpidNumber)));
			assignNewEpidNumberButton.setVisible(true);
			getContent().addComponent(epidNumberWarningLabel, EPID_NUMBER_WARNING_LOC);
		} else {
			epidField.setComponentError(null);
			getContent().removeComponent(epidNumberWarningLabel);
			assignNewEpidNumberButton.setVisible(
				!isConfiguredServer(CountryHelper.COUNTRY_CODE_GERMANY)
					&& !isConfiguredServer(CountryHelper.COUNTRY_CODE_SWITZERLAND)
					&& !CaseLogic.isEpidNumberPrefix(fieldValue)
					&& !CaseLogic.isCompleteEpidNumber(fieldValue));
		}
	}

	private boolean shouldHidePaperFormDates() {
		return FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_FRANCE)
			|| FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			|| FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND);
	}

	private static class DiseaseChangeListener implements ValueChangeListener {

		private static final long serialVersionUID = -5339850320902885768L;

		private final AbstractSelect diseaseField;

		private final Disease currentDisease;

		DiseaseChangeListener(AbstractSelect diseaseField, Disease currentDisease) {
			this.diseaseField = diseaseField;
			this.currentDisease = currentDisease;
		}

		@Override
		public void valueChange(Property.ValueChangeEvent e) {

			if (diseaseField.getValue() != currentDisease) {
				ConfirmationComponent confirmDiseaseChangeComponent = new ConfirmationComponent(false) {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onConfirm() {
						diseaseField.removeValueChangeListener(DiseaseChangeListener.this);
					}

					@Override
					protected void onCancel() {
						diseaseField.setValue(currentDisease);
					}
				};
				confirmDiseaseChangeComponent.getConfirmButton().setCaption(I18nProperties.getString(Strings.confirmationChangeCaseDisease));
				confirmDiseaseChangeComponent.getCancelButton().setCaption(I18nProperties.getCaption(Captions.actionCancel));
				confirmDiseaseChangeComponent.setMargin(true);

				Window popupWindow = VaadinUiUtil.showPopupWindow(confirmDiseaseChangeComponent);
				CloseListener closeListener = ce -> diseaseField.setValue(currentDisease);
				popupWindow.addCloseListener(closeListener);
				confirmDiseaseChangeComponent.addDoneListener(() -> {
					popupWindow.removeCloseListener(closeListener);
					popupWindow.close();
				});
				popupWindow.setCaption(I18nProperties.getString(Strings.headingChangeCaseDisease));
			}
		}

	}

	private void onValueChange() {
		QuarantineType quarantineType = (QuarantineType) quarantine.getValue();
		if (QuarantineType.isQuarantineInEffect(quarantineType)) {
			setVisible(dfPreviousQuarantineTo.getValue() != null, CaseDataDto.PREVIOUS_QUARANTINE_TO, CaseDataDto.QUARANTINE_CHANGE_COMMENT);
			CaseDataDto caze = this.getInternalValue();
			if (caze != null) {
				quarantineFrom.setValue(caze.getQuarantineFrom());
				if (caze.getQuarantineTo() == null) {
					if (caseFollowUpEnabled) {
						dfQuarantineTo.setValue(caze.getFollowUpUntil());
					}
				} else {
					dfQuarantineTo.setValue(caze.getQuarantineTo());
				}
				if (caze.isQuarantineExtended()) {
					cbQuarantineExtended.setValue(true);
					setVisible(true, CaseDataDto.QUARANTINE_EXTENDED);
				}
				if (caze.isQuarantineReduced()) {
					cbQuarantineReduced.setValue(true);
					setVisible(true, CaseDataDto.QUARANTINE_REDUCED);
				}
			} else {
				quarantineFrom.clear();
				dfQuarantineTo.clear();
				cbQuarantineExtended.setValue(false);
				cbQuarantineReduced.setValue(false);
				setVisible(
					false,
					CaseDataDto.QUARANTINE_REDUCED,
					CaseDataDto.QUARANTINE_EXTENDED,
					CaseDataDto.PREVIOUS_QUARANTINE_TO,
					CaseDataDto.QUARANTINE_CHANGE_COMMENT);
			}
		} else {
			quarantineChangeComment.clear();
			setVisible(false, CaseDataDto.PREVIOUS_QUARANTINE_TO, CaseDataDto.QUARANTINE_CHANGE_COMMENT);
		}
	}
}
