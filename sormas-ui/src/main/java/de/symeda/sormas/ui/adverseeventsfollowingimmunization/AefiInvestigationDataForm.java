/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.adverseeventsfollowingimmunization;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumnLocCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.function.Consumer;

import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;
import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.PasswordField;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.DeliveryProcedure;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PatientStatusAtAefiInvestigation;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.PlaceOfVaccination;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SeriousAefiInfoSource;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.SyringeType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationActivity;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.VaccinationSite;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.fields.vaccines.AefiVaccinationsField;
import de.symeda.sormas.ui.adverseeventsfollowingimmunization.components.form.FormSectionAccordion;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateTimeField;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.UserField;

@SuppressWarnings("deprecation")
public class AefiInvestigationDataForm extends AbstractEditForm<AefiInvestigationDto> {

	public static final String FORM_HEADING_LOC = "formHeadingLoc";
	public static final String MAIN_ACCORDION_LOC = "mainAccordionLoc";
	public static final String FOR_INFANTS_HEADING_LOC = "forInfantsHeadingLoc";
	public static final String SOURCE_OF_INFORMATION_HEADING_LOC = "sourceOfInformationHeadingLoc";
	public static final String CLINICAL_DETAILS_OFFICER_HEADING_LOC = "clinicalDetailsOfficerHeadingLoc";
	public static final String SYRINGES_AND_NEEDLES_HEADING_LOC = "syringesAndNeedlesHeadingLoc";
	public static final String RECONSTITUTION_HEADING_LOC = "reconstitutionHeadingLoc";
	public static final String INJECTION_TECHNIQUE_HEADING_LOC = "injectionTechniqueHeadingLoc";
	public static final String VACCINE_STORAGE_POINT_HEADING_LOC = "vaccineStoragePointHeadingLoc";
	public static final String VACCINE_TRANSPORTATION_HEADING_LOC = "vaccineTransportationHeadingLoc";
	public static final String THOSE_AFFECTED_HEADING_LOC = "thoseAffectedHeadingLoc";

	//@formatter:off
	public static final String HTML_LAYOUT =
			locCss(CssStyles.VSPACE_3, FORM_HEADING_LOC)
			+ fluidRowLocs(MAIN_ACCORDION_LOC);
	//@formatter:on

	//@formatter:off
	public static final String BASIC_DETAILS_HTML_LAYOUT =
			fluidRowLocs(4, AefiInvestigationDto.UUID, 4, AefiInvestigationDto.REPORT_DATE, 3, AefiInvestigationDto.REPORTING_USER)
			+ fluidRowLocs(4, AefiInvestigationDto.RESPONSIBLE_REGION, 4, AefiInvestigationDto.RESPONSIBLE_DISTRICT, 3, AefiInvestigationDto.RESPONSIBLE_COMMUNITY)
			+ fluidRowLocs(4, AefiInvestigationDto.INVESTIGATION_CASE_ID)
			+ fluidRowLocs(8, AefiInvestigationDto.PLACE_OF_VACCINATION, 4, AefiInvestigationDto.PLACE_OF_VACCINATION_DETAILS)
			+ fluidRowLocs(4, AefiInvestigationDto.VACCINATION_ACTIVITY, 4, AefiInvestigationDto.VACCINATION_ACTIVITY_DETAILS)
			+ fluidRowLocs(AefiInvestigationDto.INVESTIGATION_DATE, AefiInvestigationDto.FORM_COMPLETION_DATE, AefiInvestigationDto.INVESTIGATION_STAGE)
			+ fluidRowLocs(AefiInvestigationDto.VACCINATIONS)
			+ fluidRowLocs(8, AefiInvestigationDto.TYPE_OF_SITE, 4, AefiInvestigationDto.TYPE_OF_SITE_DETAILS)
			+ fluidRowLocs(4, AefiInvestigationDto.KEY_SYMPTOM_DATE_TIME, 3, AefiInvestigationDto.HOSPITALIZATION_DATE, 5, AefiInvestigationDto.REPORTED_TO_HEALTH_AUTHORITY_DATE)
			+ fluidRowLocs(AefiInvestigationDto.STATUS_ON_DATE_OF_INVESTIGATION)
			+ fluidRowLocs(4, AefiInvestigationDto.DEATH_DATE_TIME, 3, AefiInvestigationDto.AUTOPSY_DONE, 5, AefiInvestigationDto.AUTOPSY_DATE)
			+ fluidRowLocs(4, "", 5, AefiInvestigationDto.AUTOPSY_PLANNED_DATE_TIME);
	//@formatter:on

	//@formatter:off
	public static final String RELEVANT_PATIENT_INFO_HTML_LAYOUT =
			fluidRowLocs(8, AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT, 4, AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS, 4, AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD, 4, AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER, 4, AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE, 4, AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION, 4, AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY, 4, AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY_DETAILS)
			+ loc(FOR_INFANTS_HEADING_LOC)
			+ fluidRowLocs(8, AefiInvestigationDto.BIRTH_TERM, 4, AefiInvestigationDto.BIRTH_WEIGHT)
			+ fluidRowLocs(8, AefiInvestigationDto.DELIVERY_PROCEDURE, 4, AefiInvestigationDto.DELIVERY_PROCEDURE_DETAILS);
	//@formatter:on

	//@formatter:off
	public static final String FIRST_EXAMINATION_HTML_LAYOUT =
			loc(SOURCE_OF_INFORMATION_HEADING_LOC)
			+ fluidRowLocs(AefiInvestigationDto.SERIOUS_AEFI_INFO_SOURCE)
			+ fluidRowLocs(AefiInvestigationDto.SERIOUS_AEFI_INFO_SOURCE_DETAILS, AefiInvestigationDto.SERIOUS_AEFI_VERBAL_AUTOPSY_INFO_SOURCE_DETAILS)
			+ fluidRowLocs(AefiInvestigationDto.FIRST_CAREGIVERS_NAME, AefiInvestigationDto.OTHER_CAREGIVERS_NAMES)
			+ fluidRowLocs(6, AefiInvestigationDto.OTHER_SOURCES_WHO_PROVIDED_INFO)
			+ fluidRowLocs(AefiInvestigationDto.SIGNS_AND_SYMPTOMS_FROM_TIME_OF_VACCINATION)
			+ loc(CLINICAL_DETAILS_OFFICER_HEADING_LOC)
			+ fluidRowLocs(5, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_NAME, 4, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_PHONE_NUMBER, 3, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_EMAIL)
			+ fluidRowLocs(5, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_DESIGNATION, 4, AefiInvestigationDto.CLINICAL_DETAILS_DATE_TIME)
			+ fluidRowLocs(4, AefiInvestigationDto.PATIENT_RECEIVED_MEDICAL_CARE)
			+ fluidRowLocs(AefiInvestigationDto.PATIENT_RECEIVED_MEDICAL_CARE_DETAILS)
			+ fluidRowLocs(AefiInvestigationDto.PROVISIONAL_OR_FINAL_DIAGNOSIS);
	//@formatter:on

	//@formatter:off
	public static final String VACCINES_DETAILS_HTML_LAYOUT =
			fluidRowLocs(AefiInvestigationDto.PATIENT_IMMUNIZED_PERIOD)
			+ fluidRowLocs(AefiInvestigationDto.VACCINE_GIVEN_PERIOD)
			+ fluidRowCss(CssStyles.VSPACE_TOP_4,
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.ERROR_PRESCRIBING_VACCINE),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.ERROR_PRESCRIBING_VACCINE_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_COULD_HAVE_BEEN_UNSTERILE),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.VACCINE_COULD_HAVE_BEEN_UNSTERILE_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_PHYSICAL_CONDITION_ABNORMAL),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.VACCINE_PHYSICAL_CONDITION_ABNORMAL_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.ERROR_IN_VACCINE_RECONSTITUTION),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.ERROR_IN_VACCINE_RECONSTITUTION_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.ERROR_IN_VACCINE_HANDLING),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.ERROR_IN_VACCINE_HANDLING_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_ADMINISTERED_INCORRECTLY),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.VACCINE_ADMINISTERED_INCORRECTLY_DETAILS)
			)
			+ fluidRowLocs(8, AefiInvestigationDto.NUMBER_IMMUNIZED_FROM_CONCERNED_VACCINE_VIAL)
			+ fluidRowLocs(8, AefiInvestigationDto.NUMBER_IMMUNIZED_WITH_CONCERNED_VACCINE_IN_SAME_SESSION)
			+ fluidRowLocs(12, AefiInvestigationDto.NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_OTHER_LOCATIONS)
			+ fluidRowLocs(12, AefiInvestigationDto.NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_LOCATION_DETAILS)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_HAS_QUALITY_DEFECT),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.VACCINE_HAS_QUALITY_DEFECT_DETAILS)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION),
				fluidColumnLocCss("", 4, 0, AefiInvestigationDto.EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION_DETAILS)
			)
			+ fluidRowLocs(4, AefiInvestigationDto.CASE_IS_PART_OF_A_CLUSTER, 8, AefiInvestigationDto.CASE_IS_PART_OF_A_CLUSTER_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.NUMBER_OF_CASES_DETECTED_IN_CLUSTER)
			+ fluidRowLocs(8, AefiInvestigationDto.ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL, 4, AefiInvestigationDto.ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL_DETAILS)
			+ fluidRowLocs(8, AefiInvestigationDto.NUMBER_OF_VIALS_USED_IN_CLUSTER);
	//@formatter:on

	//@formatter:off
	public static final String IMMUNIZATION_PRACTICES_HTML_LAYOUT =
			loc(SYRINGES_AND_NEEDLES_HEADING_LOC)
			+ fluidRowLocs(6, AefiInvestigationDto.AD_SYRINGES_USED_FOR_IMMUNIZATION)
			+ fluidRowLocs(8, AefiInvestigationDto.TYPE_OF_SYRINGES_USED, 4, AefiInvestigationDto.TYPE_OF_SYRINGES_USED_DETAILS)
			+ fluidRowLocs(AefiInvestigationDto.SYRINGES_USED_ADDITIONAL_DETAILS)
			+ loc(RECONSTITUTION_HEADING_LOC)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_USED_FOR_MULTIPLE_VIALS_OF_SAME_VACCINE)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_USED_FOR_RECONSTITUTING_DIFFERENT_VACCINES)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINE_VIAL)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINATION)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINES_AND_DILUENTS_USED_RECOMMENDED_BY_MANUFACTURER)
			)
			+ fluidRowLocs(AefiInvestigationDto.RECONSTITUTION_ADDITIONAL_DETAILS)
			+ loc(INJECTION_TECHNIQUE_HEADING_LOC)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.CORRECT_DOSE_OR_ROUTE)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.TIME_OF_RECONSTITUTION_MENTIONED_ON_THE_VIAL)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.NON_TOUCH_TECHNIQUE_FOLLOWED)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.CONTRAINDICATION_SCREENED_PRIOR_TO_VACCINATION)
			)
			+ fluidRowLocs(AefiInvestigationDto.NUMBER_OF_AEFI_REPORTED_FROM_VACCINE_DISTRIBUTION_CENTER_LAST_THIRTY_DAYS)
			+ fluidRowLocs(4, AefiInvestigationDto.TRAINING_RECEIVED_BY_VACCINATOR, 4, AefiInvestigationDto.LAST_TRAINING_RECEIVED_BY_VACCINATOR_DATE)
			+ fluidRowLocs(AefiInvestigationDto.INJECTION_TECHNIQUE_ADDITIONAL_DETAILS);
	//@formatter:on

	//@formatter:off
	public static final String COLD_CHAIN_AND_TRANSPORT_HTML_LAYOUT =
			loc(VACCINE_STORAGE_POINT_HEADING_LOC)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_STORAGE_REFRIGERATOR_TEMPERATURE_MONITORED)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.ANY_STORAGE_TEMPERATURE_DEVIATION_OUTSIDE_TWO_TO_EIGHT_DEGREES)
			)
			+ fluidRowLocs(AefiInvestigationDto.STORAGE_TEMPERATURE_MONITORING_ADDITIONAL_DETAILS)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.CORRECT_PROCEDURE_FOR_STORAGE_FOLLOWED)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.ANY_OTHER_ITEM_IN_REFRIGERATOR)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.PARTIALLY_USED_RECONSTITUTED_VACCINES_IN_REFRIGERATOR)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.UNUSABLE_VACCINES_IN_REFRIGERATOR)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.UNUSABLE_DILUENTS_IN_STORE)
			)
			+ fluidRowLocs(AefiInvestigationDto.VACCINE_STORAGE_POINT_ADDITIONAL_DETAILS)
			+ loc(VACCINE_TRANSPORTATION_HEADING_LOC)
			+ fluidRowLocs(4, AefiInvestigationDto.VACCINE_CARRIER_TYPE)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_CARRIER_SENT_TO_SITE_ON_SAME_DATE_AS_VACCINATION)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.VACCINE_CARRIER_RETURNED_FROM_SITE_ON_SAME_DATE_AS_VACCINATION)
			)
			+ fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.CONDITIONED_ICE_PACK_USED)
			)
			+ fluidRowLocs(AefiInvestigationDto.VACCINE_TRANSPORTATION_ADDITIONAL_DETAILS);
	//@formatter:on

	//@formatter:off
	public static final String COMMUNITY_INVESTIGATION_HTML_LAYOUT =
			fluidRow(
				fluidColumnLocCss(CssStyles.OPTIONGROUP_CAPTION_FLEX, 8, 0, AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY)
			)
			+ fluidRowLocs(AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY_DETAILS, AefiInvestigationDto.NUMBER_OF_SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY)
			+ loc(THOSE_AFFECTED_HEADING_LOC)
			+ fluidRowLocs(AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_VACCINATED, AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_NOT_VACCINATED, AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_VACCINATED_UNKNOWN)
			+ fluidRowLocs(AefiInvestigationDto.COMMUNITY_INVESTIGATION_ADDITIONAL_DETAILS);
	//@formatter:on

	//@formatter:off
	public static final String OTHER_FINDINGS_HTML_LAYOUT =
			fluidRowLocs(AefiInvestigationDto.OTHER_INVESTIGATION_FINDINGS);
	//@formatter:on

	//@formatter:off
	public static final String INVESTIGATION_STATUS_HTML_LAYOUT =
			fluidRowLocs(4, AefiInvestigationDto.INVESTIGATION_STATUS, 8, AefiInvestigationDto.INVESTIGATION_STATUS_DETAILS)
			+ fluidRowLocs(AefiInvestigationDto.AEFI_CLASSIFICATION)
			+ fluidRowLocs(AefiInvestigationDto.AEFI_CLASSIFICATION_DETAILS);
	//@formatter:on

	private boolean isCreateAction;
	private final Consumer<Runnable> actionCallback;
	private TextField responsibleRegion;
	private TextField responsibleDistrict;
	private TextField responsibleCommunity;
	private AefiVaccinationsField vaccinationsField;

	public AefiInvestigationDataForm(boolean isCreateAction, boolean isPseudonymized, boolean inJurisdiction, Consumer<Runnable> actionCallback) {
		super(
			AefiInvestigationDto.class,
			AefiInvestigationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized));

		this.isCreateAction = isCreateAction;
		this.actionCallback = actionCallback;

		if (isCreateAction) {
			hideValidationUntilNextCommit();
		}

		addFields();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {

		Label formHeadingLabel = new Label(I18nProperties.getString(Strings.headingAefiInvestigationFormSubHeading));
		formHeadingLabel.addStyleNames(H3, CssStyles.LABEL_CRITICAL, CssStyles.VSPACE_NONE, CssStyles.VSPACE_TOP_NONE);
		getContent().addComponent(formHeadingLabel, FORM_HEADING_LOC);

		FormSectionAccordion accordion = new FormSectionAccordion();

		//Basic details
		CustomLayout basicDetailsLayout = new CustomLayout();
		basicDetailsLayout.setTemplateContents(BASIC_DETAILS_HTML_LAYOUT);

		if (isCreateAction) {
			addField(basicDetailsLayout, AefiInvestigationDto.UUID, PasswordField.class);
		} else {
			addField(basicDetailsLayout, AefiInvestigationDto.UUID);
		}
		addField(basicDetailsLayout, AefiInvestigationDto.REPORT_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.REPORTING_USER, UserField.class);

		responsibleRegion = new TextField(I18nProperties.getPrefixCaption(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.RESPONSIBLE_REGION));
		basicDetailsLayout.addComponent(responsibleRegion, AefiInvestigationDto.RESPONSIBLE_REGION);
		responsibleDistrict =
			new TextField(I18nProperties.getPrefixCaption(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.RESPONSIBLE_DISTRICT));
		basicDetailsLayout.addComponent(responsibleDistrict, AefiInvestigationDto.RESPONSIBLE_DISTRICT);
		responsibleCommunity =
			new TextField(I18nProperties.getPrefixCaption(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.RESPONSIBLE_COMMUNITY));
		basicDetailsLayout.addComponent(responsibleCommunity, AefiInvestigationDto.RESPONSIBLE_COMMUNITY);

		addField(basicDetailsLayout, AefiInvestigationDto.INVESTIGATION_CASE_ID);
		addField(basicDetailsLayout, AefiInvestigationDto.PLACE_OF_VACCINATION, NullableOptionGroup.class);
		addField(basicDetailsLayout, AefiInvestigationDto.PLACE_OF_VACCINATION_DETAILS);
		addField(basicDetailsLayout, AefiInvestigationDto.VACCINATION_ACTIVITY, NullableOptionGroup.class);
		addField(basicDetailsLayout, AefiInvestigationDto.VACCINATION_ACTIVITY_DETAILS);
		addField(basicDetailsLayout, AefiInvestigationDto.INVESTIGATION_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.FORM_COMPLETION_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.INVESTIGATION_STAGE, NullableOptionGroup.class);
		vaccinationsField = addField(basicDetailsLayout, AefiInvestigationDto.VACCINATIONS, AefiVaccinationsField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.TYPE_OF_SITE, NullableOptionGroup.class);
		addField(basicDetailsLayout, AefiInvestigationDto.TYPE_OF_SITE_DETAILS);
		addField(basicDetailsLayout, AefiInvestigationDto.KEY_SYMPTOM_DATE_TIME, DateTimeField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.HOSPITALIZATION_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.REPORTED_TO_HEALTH_AUTHORITY_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.STATUS_ON_DATE_OF_INVESTIGATION, NullableOptionGroup.class);
		addField(basicDetailsLayout, AefiInvestigationDto.DEATH_DATE_TIME, DateTimeField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.AUTOPSY_DONE, NullableOptionGroup.class);
		addField(basicDetailsLayout, AefiInvestigationDto.AUTOPSY_DATE, DateField.class);
		addField(basicDetailsLayout, AefiInvestigationDto.AUTOPSY_PLANNED_DATE_TIME, DateTimeField.class);

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationBasicDetails, true, basicDetailsLayout);

		CustomLayout relevantPatientInformationLayout = new CustomLayout();
		relevantPatientInformationLayout.setTemplateContents(RELEVANT_PATIENT_INFO_HTML_LAYOUT);

		addField(relevantPatientInformationLayout, AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT);
		TextField pastHistoryOfSimilarEventDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT_DETAILS);
		pastHistoryOfSimilarEventDetails.setCaption(null);
		pastHistoryOfSimilarEventDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS);
		TextField adverseEventAfterPreviousVaccinationsDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS_DETAILS);
		adverseEventAfterPreviousVaccinationsDetails.setCaption(null);
		adverseEventAfterPreviousVaccinationsDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD);
		TextField historyOfAllergyDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD_DETAILS);
		historyOfAllergyDetails.setCaption(null);
		historyOfAllergyDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER);
		TextField preExistingIllnessDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER_DETAILS);
		preExistingIllnessDetails.setCaption(null);
		preExistingIllnessDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE);
		TextField historyOfHospitalizationDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE_DETAILS);
		historyOfHospitalizationDetails.setCaption(null);
		historyOfHospitalizationDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION);
		TextField currentlyOnConcomitantMedicationDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION_DETAILS);
		currentlyOnConcomitantMedicationDetails.setCaption(null);
		currentlyOnConcomitantMedicationDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(relevantPatientInformationLayout, AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY);
		TextField familyHistoryOfDiseaseDetails =
			addField(relevantPatientInformationLayout, AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY_DETAILS);
		familyHistoryOfDiseaseDetails.setCaption(null);
		familyHistoryOfDiseaseDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		Label forInfantsLabel = new Label(I18nProperties.getCaption(Captions.aefiInvestigationForInfants));
		forInfantsLabel.addStyleName(CssStyles.H4);
		relevantPatientInformationLayout.addComponent(forInfantsLabel, FOR_INFANTS_HEADING_LOC);

		addField(relevantPatientInformationLayout, AefiInvestigationDto.BIRTH_TERM, NullableOptionGroup.class);
		addField(relevantPatientInformationLayout, AefiInvestigationDto.BIRTH_WEIGHT);
		addField(relevantPatientInformationLayout, AefiInvestigationDto.DELIVERY_PROCEDURE, NullableOptionGroup.class);
		addField(relevantPatientInformationLayout, AefiInvestigationDto.DELIVERY_PROCEDURE_DETAILS);

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationRelevantPatientInformation, false, relevantPatientInformationLayout);

		//First examination
		CustomLayout firstExaminationDetailsLayout = new CustomLayout();
		firstExaminationDetailsLayout.setTemplateContents(FIRST_EXAMINATION_HTML_LAYOUT);

		Label sourceOfInformationLabel = new Label(I18nProperties.getCaption(Captions.aefiInvestigationSourceOfInformation));
		sourceOfInformationLabel.addStyleName(CssStyles.H4);
		firstExaminationDetailsLayout.addComponent(sourceOfInformationLabel, SOURCE_OF_INFORMATION_HEADING_LOC);

		OptionGroup seriousAefiSourcesOfInformation =
			addField(firstExaminationDetailsLayout, AefiInvestigationDto.SERIOUS_AEFI_INFO_SOURCE, OptionGroup.class);
		CssStyles.style(seriousAefiSourcesOfInformation, CssStyles.OPTIONGROUP_CHECKBOXES_HORIZONTAL);
		seriousAefiSourcesOfInformation.setMultiSelect(true);
		seriousAefiSourcesOfInformation.addItems((Object[]) SeriousAefiInfoSource.values());
		seriousAefiSourcesOfInformation.setCaption(null);

		addField(firstExaminationDetailsLayout, AefiInvestigationDto.SERIOUS_AEFI_INFO_SOURCE_DETAILS);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.SERIOUS_AEFI_VERBAL_AUTOPSY_INFO_SOURCE_DETAILS);

		addField(firstExaminationDetailsLayout, AefiInvestigationDto.FIRST_CAREGIVERS_NAME);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.OTHER_CAREGIVERS_NAMES);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.OTHER_SOURCES_WHO_PROVIDED_INFO);

		TextArea signsAndSymptomsFromTimeOfVaccination =
			addField(firstExaminationDetailsLayout, AefiInvestigationDto.SIGNS_AND_SYMPTOMS_FROM_TIME_OF_VACCINATION, TextArea.class);
		signsAndSymptomsFromTimeOfVaccination.setRows(6);
		signsAndSymptomsFromTimeOfVaccination.setDescription(
			I18nProperties
				.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.SIGNS_AND_SYMPTOMS_FROM_TIME_OF_VACCINATION, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		Label clinicalDetailsOfficerLabel = new Label(I18nProperties.getCaption(Captions.aefiInvestigationClinicalDetailsOfficer));
		clinicalDetailsOfficerLabel.addStyleName(CssStyles.H4);
		firstExaminationDetailsLayout.addComponent(clinicalDetailsOfficerLabel, CLINICAL_DETAILS_OFFICER_HEADING_LOC);

		addField(firstExaminationDetailsLayout, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_NAME);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_PHONE_NUMBER);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_EMAIL);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.CLINICAL_DETAILS_OFFICER_DESIGNATION);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.CLINICAL_DETAILS_DATE_TIME, DateTimeField.class);
		addField(firstExaminationDetailsLayout, AefiInvestigationDto.PATIENT_RECEIVED_MEDICAL_CARE, NullableOptionGroup.class);

		TextArea patientReceivedMedicalCareDetails =
			addField(firstExaminationDetailsLayout, AefiInvestigationDto.PATIENT_RECEIVED_MEDICAL_CARE_DETAILS, TextArea.class);
		patientReceivedMedicalCareDetails.setRows(6);
		patientReceivedMedicalCareDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.PATIENT_RECEIVED_MEDICAL_CARE_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		TextArea provisionalOrFinalDiagnosis =
			addField(firstExaminationDetailsLayout, AefiInvestigationDto.PROVISIONAL_OR_FINAL_DIAGNOSIS, TextArea.class);
		provisionalOrFinalDiagnosis.setRows(6);
		provisionalOrFinalDiagnosis.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.PROVISIONAL_OR_FINAL_DIAGNOSIS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationFirstExaminationDetails, false, firstExaminationDetailsLayout);

		//Vaccines details
		CustomLayout vaccinesDetailsLayout = new CustomLayout();
		vaccinesDetailsLayout.setTemplateContents(VACCINES_DETAILS_HTML_LAYOUT);

		addField(vaccinesDetailsLayout, AefiInvestigationDto.PATIENT_IMMUNIZED_PERIOD, NullableOptionGroup.class);
		/*
		 * TextField patientImmunizedPeriodDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.PATIENT_IMMUNIZED_PERIOD_DETAILS);
		 * patientImmunizedPeriodDetails.setCaption(null);
		 * patientImmunizedPeriodDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));
		 */

		addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_GIVEN_PERIOD, NullableOptionGroup.class);
		/*
		 * TextField vaccineGivenPeriodDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_GIVEN_PERIOD_DETAILS);
		 * vaccineGivenPeriodDetails.setCaption(null);
		 * vaccineGivenPeriodDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));
		 */

		addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_PRESCRIBING_VACCINE);
		TextField errorPrescribingVaccineDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_PRESCRIBING_VACCINE_DETAILS);
		errorPrescribingVaccineDetails.setCaption(null);
		errorPrescribingVaccineDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_COULD_HAVE_BEEN_UNSTERILE);
		TextField vaccineCouldBeUnsterileDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_COULD_HAVE_BEEN_UNSTERILE_DETAILS);
		vaccineCouldBeUnsterileDetails.setCaption(null);
		vaccineCouldBeUnsterileDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_PHYSICAL_CONDITION_ABNORMAL);
		TextField vaccinePhysicalConditionAbnormalDetails =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_PHYSICAL_CONDITION_ABNORMAL_DETAILS);
		vaccinePhysicalConditionAbnormalDetails.setCaption(null);
		vaccinePhysicalConditionAbnormalDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_IN_VACCINE_RECONSTITUTION);
		TextField errorInVaccineReconstitutionAbnormalDetails =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_IN_VACCINE_RECONSTITUTION_DETAILS);
		errorInVaccineReconstitutionAbnormalDetails.setCaption(null);
		errorInVaccineReconstitutionAbnormalDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_IN_VACCINE_HANDLING);
		TextField errorInVaccineHandlingDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.ERROR_IN_VACCINE_HANDLING_DETAILS);
		errorInVaccineHandlingDetails.setCaption(null);
		errorInVaccineHandlingDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_ADMINISTERED_INCORRECTLY);
		TextField vaccineAdministeredIncorrectlyDetails =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_ADMINISTERED_INCORRECTLY_DETAILS);
		vaccineAdministeredIncorrectlyDetails.setCaption(null);
		vaccineAdministeredIncorrectlyDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		TextField numberImmunizedFromConcernedVaccineVial =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.NUMBER_IMMUNIZED_FROM_CONCERNED_VACCINE_VIAL, TextField.class);
		numberImmunizedFromConcernedVaccineVial.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberImmunizedFromConcernedVaccineVial.getCaption()));

		TextField numberImmunizedFromConcernedVaccineInSameSession =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.NUMBER_IMMUNIZED_WITH_CONCERNED_VACCINE_IN_SAME_SESSION, TextField.class);
		numberImmunizedFromConcernedVaccineInSameSession.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberImmunizedFromConcernedVaccineInSameSession.getCaption()));

		TextField numberImmunizedFromConcernedVaccineSameBatchNumberOtherLocationsSession = addField(
			vaccinesDetailsLayout,
			AefiInvestigationDto.NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_OTHER_LOCATIONS,
			TextField.class);
		numberImmunizedFromConcernedVaccineSameBatchNumberOtherLocationsSession.setConversionError(
			I18nProperties.getValidationError(
				Validations.onlyIntegerNumbersAllowed,
				numberImmunizedFromConcernedVaccineSameBatchNumberOtherLocationsSession.getCaption()));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.NUMBER_IMMUNIZED_CONCERNED_VACCINE_SAME_BATCH_NUMBER_LOCATION_DETAILS);

		addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_HAS_QUALITY_DEFECT);
		TextField vaccineHasQualityDefectDetails = addField(vaccinesDetailsLayout, AefiInvestigationDto.VACCINE_HAS_QUALITY_DEFECT_DETAILS);
		vaccineHasQualityDefectDetails.setCaption(null);
		vaccineHasQualityDefectDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION);
		TextField eventIsStressResponseRelatedToImmunizationDetails =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.EVENT_IS_A_STRESS_RESPONSE_RELATED_TO_IMMUNIZATION_DETAILS);
		eventIsStressResponseRelatedToImmunizationDetails.setCaption(null);
		eventIsStressResponseRelatedToImmunizationDetails.setInputPrompt(I18nProperties.getString(Strings.promptRemarks));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.CASE_IS_PART_OF_A_CLUSTER, NullableOptionGroup.class);
		addField(vaccinesDetailsLayout, AefiInvestigationDto.CASE_IS_PART_OF_A_CLUSTER_DETAILS);

		TextField numberOfCasesDetectedInCluster =
			addField(vaccinesDetailsLayout, AefiInvestigationDto.NUMBER_OF_CASES_DETECTED_IN_CLUSTER, TextField.class);
		numberOfCasesDetectedInCluster.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfCasesDetectedInCluster.getCaption()));

		addField(vaccinesDetailsLayout, AefiInvestigationDto.ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL, NullableOptionGroup.class);
		addField(vaccinesDetailsLayout, AefiInvestigationDto.ALL_CASES_IN_CLUSTER_RECEIVED_VACCINE_FROM_SAME_VIAL_DETAILS);

		TextField numberOfVialsUsedInCluster = addField(vaccinesDetailsLayout, AefiInvestigationDto.NUMBER_OF_VIALS_USED_IN_CLUSTER, TextField.class);
		numberOfVialsUsedInCluster
			.setConversionError(I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfVialsUsedInCluster.getCaption()));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationVaccinesDetails, false, vaccinesDetailsLayout);

		//Immunization practices
		CustomLayout immunizationPracticesLayout = new CustomLayout();
		immunizationPracticesLayout.setTemplateContents(IMMUNIZATION_PRACTICES_HTML_LAYOUT);

		Label syringesAndNeedlesUsedLabel =
			new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationImmunizationPracticesSyringesAndNeedlesUsed));
		syringesAndNeedlesUsedLabel.addStyleName(CssStyles.H4);
		immunizationPracticesLayout.addComponent(syringesAndNeedlesUsedLabel, SYRINGES_AND_NEEDLES_HEADING_LOC);

		addField(immunizationPracticesLayout, AefiInvestigationDto.AD_SYRINGES_USED_FOR_IMMUNIZATION, NullableOptionGroup.class);
		addField(immunizationPracticesLayout, AefiInvestigationDto.TYPE_OF_SYRINGES_USED, NullableOptionGroup.class);
		addField(immunizationPracticesLayout, AefiInvestigationDto.TYPE_OF_SYRINGES_USED_DETAILS);

		TextArea syringesUsedAdditionalDetails =
			addField(immunizationPracticesLayout, AefiInvestigationDto.SYRINGES_USED_ADDITIONAL_DETAILS, TextArea.class);
		syringesUsedAdditionalDetails.setRows(6);
		syringesUsedAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.SYRINGES_USED_ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		Label reconstitutionLabel = new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationImmunizationPracticesReconstitution));
		reconstitutionLabel.addStyleName(CssStyles.H4);
		immunizationPracticesLayout.addComponent(reconstitutionLabel, RECONSTITUTION_HEADING_LOC);

		addField(immunizationPracticesLayout, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_USED_FOR_MULTIPLE_VIALS_OF_SAME_VACCINE);
		addField(immunizationPracticesLayout, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_USED_FOR_RECONSTITUTING_DIFFERENT_VACCINES);
		addField(immunizationPracticesLayout, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINE_VIAL);
		addField(immunizationPracticesLayout, AefiInvestigationDto.SAME_RECONSTITUTION_SYRINGE_FOR_EACH_VACCINATION);
		addField(immunizationPracticesLayout, AefiInvestigationDto.VACCINES_AND_DILUENTS_USED_RECOMMENDED_BY_MANUFACTURER);

		TextArea reconstitutionAdditionalDetails =
			addField(immunizationPracticesLayout, AefiInvestigationDto.RECONSTITUTION_ADDITIONAL_DETAILS, TextArea.class);
		reconstitutionAdditionalDetails.setRows(6);
		reconstitutionAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.RECONSTITUTION_ADDITIONAL_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		Label injectionTechniqueLabel = new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationImmunizationPracticesInjectionTechnique));
		injectionTechniqueLabel.addStyleName(CssStyles.H4);
		immunizationPracticesLayout.addComponent(injectionTechniqueLabel, INJECTION_TECHNIQUE_HEADING_LOC);

		addField(immunizationPracticesLayout, AefiInvestigationDto.CORRECT_DOSE_OR_ROUTE);
		addField(immunizationPracticesLayout, AefiInvestigationDto.TIME_OF_RECONSTITUTION_MENTIONED_ON_THE_VIAL);
		addField(immunizationPracticesLayout, AefiInvestigationDto.NON_TOUCH_TECHNIQUE_FOLLOWED);
		addField(immunizationPracticesLayout, AefiInvestigationDto.CONTRAINDICATION_SCREENED_PRIOR_TO_VACCINATION);

		TextField numberOfAefiFromDistributionCenter = addField(
			immunizationPracticesLayout,
			AefiInvestigationDto.NUMBER_OF_AEFI_REPORTED_FROM_VACCINE_DISTRIBUTION_CENTER_LAST_THIRTY_DAYS,
			TextField.class);
		numberOfAefiFromDistributionCenter.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfAefiFromDistributionCenter.getCaption()));

		addField(immunizationPracticesLayout, AefiInvestigationDto.TRAINING_RECEIVED_BY_VACCINATOR, NullableOptionGroup.class);
		addField(immunizationPracticesLayout, AefiInvestigationDto.LAST_TRAINING_RECEIVED_BY_VACCINATOR_DATE, DateField.class);

		TextArea injectionTechniqueAdditionalDetails =
			addField(immunizationPracticesLayout, AefiInvestigationDto.INJECTION_TECHNIQUE_ADDITIONAL_DETAILS, TextArea.class);
		injectionTechniqueAdditionalDetails.setRows(6);
		injectionTechniqueAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.INJECTION_TECHNIQUE_ADDITIONAL_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationImmunizationPractices, false, immunizationPracticesLayout);

		//Cold chain and transport
		CustomLayout coldChainAndTransportLayout = new CustomLayout();
		coldChainAndTransportLayout.setTemplateContents(COLD_CHAIN_AND_TRANSPORT_HTML_LAYOUT);

		Label lastVaccineStoragePointLabel =
			new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationColdChainAndTransportLastVaccineStoragePoint));
		lastVaccineStoragePointLabel.addStyleName(CssStyles.H4);
		coldChainAndTransportLayout.addComponent(lastVaccineStoragePointLabel, VACCINE_STORAGE_POINT_HEADING_LOC);

		addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_STORAGE_REFRIGERATOR_TEMPERATURE_MONITORED);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.ANY_STORAGE_TEMPERATURE_DEVIATION_OUTSIDE_TWO_TO_EIGHT_DEGREES);

		TextArea storageTemperatureMonitoringDetails =
			addField(coldChainAndTransportLayout, AefiInvestigationDto.STORAGE_TEMPERATURE_MONITORING_ADDITIONAL_DETAILS, TextArea.class);
		storageTemperatureMonitoringDetails.setRows(6);
		storageTemperatureMonitoringDetails.setDescription(
			I18nProperties
				.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.STORAGE_TEMPERATURE_MONITORING_ADDITIONAL_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		addField(coldChainAndTransportLayout, AefiInvestigationDto.CORRECT_PROCEDURE_FOR_STORAGE_FOLLOWED);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.ANY_OTHER_ITEM_IN_REFRIGERATOR);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.PARTIALLY_USED_RECONSTITUTED_VACCINES_IN_REFRIGERATOR);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.UNUSABLE_VACCINES_IN_REFRIGERATOR);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.UNUSABLE_DILUENTS_IN_STORE);

		TextArea vaccineStoragePointAdditionalDetails =
			addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_STORAGE_POINT_ADDITIONAL_DETAILS, TextArea.class);
		vaccineStoragePointAdditionalDetails.setRows(6);
		vaccineStoragePointAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.VACCINE_STORAGE_POINT_ADDITIONAL_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		Label vaccineTransportationLabel =
			new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationColdChainAndTransportVaccineTransportation));
		vaccineTransportationLabel.addStyleName(CssStyles.H4);
		coldChainAndTransportLayout.addComponent(vaccineTransportationLabel, VACCINE_TRANSPORTATION_HEADING_LOC);

		addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_CARRIER_TYPE, NullableOptionGroup.class);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_CARRIER_SENT_TO_SITE_ON_SAME_DATE_AS_VACCINATION);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_CARRIER_RETURNED_FROM_SITE_ON_SAME_DATE_AS_VACCINATION);
		addField(coldChainAndTransportLayout, AefiInvestigationDto.CONDITIONED_ICE_PACK_USED);

		TextArea vaccineTransportationAdditionalDetails =
			addField(coldChainAndTransportLayout, AefiInvestigationDto.VACCINE_TRANSPORTATION_ADDITIONAL_DETAILS, TextArea.class);
		vaccineTransportationAdditionalDetails.setRows(6);
		vaccineTransportationAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.VACCINE_TRANSPORTATION_ADDITIONAL_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationColdChainAndTransport, false, coldChainAndTransportLayout);

		//Community investigation
		CustomLayout communityInvestigationLayout = new CustomLayout();
		communityInvestigationLayout.setTemplateContents(COMMUNITY_INVESTIGATION_HTML_LAYOUT);

		addField(communityInvestigationLayout, AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY);

		TextArea similarEventsReportedSamePeriodDetails =
			addField(communityInvestigationLayout, AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY_DETAILS, TextArea.class);
		similarEventsReportedSamePeriodDetails.setRows(6);
		similarEventsReportedSamePeriodDetails.setDescription(
			I18nProperties.getPrefixDescription(
				AefiInvestigationDto.I18N_PREFIX,
				AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY_DETAILS,
				"") + "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		TextField numberOfSimilarEventsReportedInSamePeriod =
			addField(communityInvestigationLayout, AefiInvestigationDto.NUMBER_OF_SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY, TextField.class);
		numberOfSimilarEventsReportedInSamePeriod.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfSimilarEventsReportedInSamePeriod.getCaption()));

		Label thoseAffectedLabelLabel = new Label(I18nProperties.getCaption(Captions.titleAefiInvestigationCommunityInvestigationThoseAffected));
		thoseAffectedLabelLabel.addStyleName(CssStyles.H4);
		communityInvestigationLayout.addComponent(thoseAffectedLabelLabel, THOSE_AFFECTED_HEADING_LOC);

		TextField numberOfThoseAffectedVaccinated =
			addField(communityInvestigationLayout, AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_VACCINATED, TextField.class);
		numberOfThoseAffectedVaccinated.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfThoseAffectedVaccinated.getCaption()));

		TextField numberOfThoseAffectedNotVaccinated =
			addField(communityInvestigationLayout, AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_NOT_VACCINATED, TextField.class);
		numberOfThoseAffectedNotVaccinated.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfThoseAffectedNotVaccinated.getCaption()));

		TextField numberOfThoseAffectedVaccinatedUnknown =
			addField(communityInvestigationLayout, AefiInvestigationDto.NUMBER_OF_THOSE_AFFECTED_VACCINATED_UNKNOWN, TextField.class);
		numberOfThoseAffectedVaccinatedUnknown.setConversionError(
			I18nProperties.getValidationError(Validations.onlyIntegerNumbersAllowed, numberOfThoseAffectedVaccinatedUnknown.getCaption()));

		TextArea communityInvestigationAdditionalDetails =
			addField(communityInvestigationLayout, AefiInvestigationDto.COMMUNITY_INVESTIGATION_ADDITIONAL_DETAILS, TextArea.class);
		communityInvestigationAdditionalDetails.setRows(6);
		communityInvestigationAdditionalDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.COMMUNITY_INVESTIGATION_ADDITIONAL_DETAILS, "")
				+ "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationCommunityInvestigation, false, communityInvestigationLayout);

		//Other findings
		CustomLayout otherFindingsLayout = new CustomLayout();
		otherFindingsLayout.setTemplateContents(OTHER_FINDINGS_HTML_LAYOUT);

		TextArea otherInvestigationFindingsDetails = addField(otherFindingsLayout, AefiInvestigationDto.OTHER_INVESTIGATION_FINDINGS, TextArea.class);
		otherInvestigationFindingsDetails.setRows(6);
		otherInvestigationFindingsDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.OTHER_INVESTIGATION_FINDINGS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationOtherFindings, true, otherFindingsLayout);

		//Investigation status
		CustomLayout investigationStatusLayout = new CustomLayout();
		investigationStatusLayout.setTemplateContents(INVESTIGATION_STATUS_HTML_LAYOUT);

		addField(investigationStatusLayout, AefiInvestigationDto.INVESTIGATION_STATUS, NullableOptionGroup.class);
		addField(investigationStatusLayout, AefiInvestigationDto.INVESTIGATION_STATUS_DETAILS);
		addField(investigationStatusLayout, AefiInvestigationDto.AEFI_CLASSIFICATION, NullableOptionGroup.class);
		TextArea aefiClassificationDetails = addField(investigationStatusLayout, AefiInvestigationDto.AEFI_CLASSIFICATION_DETAILS, TextArea.class);
		aefiClassificationDetails.setRows(6);
		aefiClassificationDetails.setDescription(
			I18nProperties.getPrefixDescription(AefiInvestigationDto.I18N_PREFIX, AefiInvestigationDto.AEFI_CLASSIFICATION_DETAILS, "") + "\n"
				+ I18nProperties.getDescription(Descriptions.descGdpr));

		accordion.addFormSectionPanel(Captions.titleAefiInvestigationInvestigationStatus, true, investigationStatusLayout);

		getContent().addComponent(accordion, MAIN_ACCORDION_LOC);

		//set visibility, read only and required status
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.PLACE_OF_VACCINATION_DETAILS,
			AefiInvestigationDto.PLACE_OF_VACCINATION,
			Arrays.asList(PlaceOfVaccination.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.VACCINATION_ACTIVITY_DETAILS,
			AefiInvestigationDto.VACCINATION_ACTIVITY,
			Arrays.asList(VaccinationActivity.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.TYPE_OF_SITE_DETAILS,
			AefiInvestigationDto.TYPE_OF_SITE,
			Arrays.asList(VaccinationSite.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(AefiInvestigationDto.DEATH_DATE_TIME, AefiInvestigationDto.AUTOPSY_DONE),
			AefiInvestigationDto.STATUS_ON_DATE_OF_INVESTIGATION,
			Arrays.asList(PatientStatusAtAefiInvestigation.DIED),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.AUTOPSY_DATE,
			AefiInvestigationDto.AUTOPSY_DONE,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.AUTOPSY_PLANNED_DATE_TIME,
			AefiInvestigationDto.AUTOPSY_DONE,
			Arrays.asList(YesNoUnknown.NO),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT_DETAILS,
			AefiInvestigationDto.PAST_HISTORY_OF_SIMILAR_EVENT,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS_DETAILS,
			AefiInvestigationDto.ADVERSE_EVENT_AFTER_PREVIOUS_VACCINATIONS,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD_DETAILS,
			AefiInvestigationDto.HISTORY_OF_ALLERGY_TO_VACCINE_DRUG_OR_FOOD,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER_DETAILS,
			AefiInvestigationDto.PRE_EXISTING_ILLNESS_THIRTY_DAYS_OR_CONGENITAL_DISORDER,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE_DETAILS,
			AefiInvestigationDto.HISTORY_OF_HOSPITALIZATION_IN_LAST_THIRTY_DAYS_WITH_CAUSE,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION_DETAILS,
			AefiInvestigationDto.CURRENTLY_ON_CONCOMITANT_MEDICATION,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY_DETAILS,
			AefiInvestigationDto.FAMILY_HISTORY_OF_DISEASE_OR_ALLERGY,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.DELIVERY_PROCEDURE_DETAILS,
			AefiInvestigationDto.DELIVERY_PROCEDURE,
			Arrays.asList(DeliveryProcedure.WITH_COMPLICATION),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.TYPE_OF_SYRINGES_USED_DETAILS,
			AefiInvestigationDto.TYPE_OF_SYRINGES_USED,
			Arrays.asList(SyringeType.OTHER),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			AefiInvestigationDto.LAST_TRAINING_RECEIVED_BY_VACCINATOR_DATE,
			AefiInvestigationDto.TRAINING_RECEIVED_BY_VACCINATOR,
			Arrays.asList(YesNoUnknown.YES),
			true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			Arrays.asList(
				AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY_DETAILS,
				AefiInvestigationDto.NUMBER_OF_SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY),
			AefiInvestigationDto.SIMILAR_EVENTS_REPORTED_SAME_PERIOD_AND_LOCALITY,
			Arrays.asList(YesNoUnknown.YES),
			true);

		setReadOnly(true, AefiDto.UUID, AefiDto.REPORTING_USER);

		setRequired(true, AefiInvestigationDto.REPORT_DATE, AefiInvestigationDto.INVESTIGATION_CASE_ID);
		setRequired(true, AefiInvestigationDto.PLACE_OF_VACCINATION, AefiInvestigationDto.VACCINATION_ACTIVITY);
		setRequired(
			true,
			AefiInvestigationDto.INVESTIGATION_DATE,
			AefiInvestigationDto.FORM_COMPLETION_DATE,
			AefiInvestigationDto.INVESTIGATION_STAGE);
		setRequired(true, AefiInvestigationDto.TYPE_OF_SITE);
		setRequired(true, AefiInvestigationDto.KEY_SYMPTOM_DATE_TIME);
		setRequired(true, AefiInvestigationDto.STATUS_ON_DATE_OF_INVESTIGATION);
		setRequired(true, AefiInvestigationDto.INVESTIGATION_STATUS, AefiInvestigationDto.AEFI_CLASSIFICATION);
	}

	@Override
	public void attach() {
		super.attach();

		AefiInvestigationDto dataFormValue = getValue();

		AefiDto aefiDto = FacadeProvider.getAefiFacade().getByUuid(dataFormValue.getAefiReport().getUuid());
		ImmunizationDto immunizationDto = FacadeProvider.getImmunizationFacade().getByUuid(aefiDto.getImmunization().getUuid());

		responsibleRegion.setValue(immunizationDto.getResponsibleRegion().getCaption());
		responsibleDistrict.setValue(immunizationDto.getResponsibleDistrict().getCaption());
		if (immunizationDto.getResponsibleCommunity() != null) {
			responsibleCommunity.setValue(immunizationDto.getResponsibleCommunity().getCaption());
		}

		responsibleRegion.setReadOnly(true);
		responsibleDistrict.setReadOnly(true);
		responsibleCommunity.setReadOnly(true);

		vaccinationsField.applyAefiInvestigationContext(dataFormValue);
		if (dataFormValue.getPrimarySuspectVaccine() != null) {
			vaccinationsField.selectPrimarySuspectVaccination(dataFormValue.getPrimarySuspectVaccine());
		}
	}

	@Override
	public void setValue(AefiInvestigationDto newFieldValue) throws ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);

		getValue();

		// HACK: Binding to the fields will call field listeners that may clear/modify the values of other fields.
		// this hopefully resets everything to its correct value
		discard();
	}

	@Override
	public void discard() throws SourceException {
		super.discard();
	}
}
