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

package de.symeda.sormas.ui.symptoms;

import static de.symeda.sormas.api.symptoms.SymptomsDto.*;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.CssStyles.H4;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.data.util.converter.StringToFloatConverter;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.AbstractSelect;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.SyphilisPresentation;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.ClinicalManifestation;
import de.symeda.sormas.api.symptoms.ClinicalPresentationStatus;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;
import de.symeda.sormas.api.utils.DateComparator;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.caze.CaseSymptomSideViewComponent;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class SymptomsForm extends AbstractEditForm<SymptomsDto> {

	private static final long serialVersionUID = 1L;

	private static final String SIGNS_AND_SYMPTOMS_HEADING_LOC = "signsAndSymptomsHeadingLoc";
	private static final String COMPLICATIONS_HEADING = "complicationsHeading";
	private static final String CLINICAL_MANIFESTATION_HEADING_LOC = "clinicalManifestationHeadingLoc";

	// Fields only relevant for acquired syphilis, hidden when the case presentation is congenital syphilis
	private static final List<String> SYPHILIS_ACQUIRED_ONLY_FIELD_IDS = Collections.unmodifiableList(
		Arrays.asList(
			PATCHY_ALOPECIA,
			CHANCRE,
			CONDYLOMA_VENEREUM,
			MUCOUS_PATCHES,
			LYMPHADENOPATHY,
			GENERALIZED_LYMPHADENOPATHY,
			OCULAR_MANIFESTATIONS,
			NEUROLOGICAL_MANIFESTATIONS,
			SKIN_RASH,
			// MACULOPAPULAR_RASH, // #13399 - deprecated, replaced by SKIN_RASH
			ARTHRALGIA,
			HEPATOMEGALY,
			LOW_GRADE_FEVER,
			SORE_THROAT,
			WEIGHT_LOSS,
			FEVER,
			MALAISE,
			CLINICAL_CRITERIA_MET,
			MUSCLE_PAIN));
	// Fields only relevant for congenital syphilis, hidden when the case presentation is acquired syphilis
	private static final List<String> SYPHILIS_CONGENITAL_ONLY_FIELD_IDS = Collections.unmodifiableList(
		Arrays.asList(
			ANEMIA,
			BONY_ABNORMALITIES,
			PSEUDOPARALYSIS,
			JAUNDICE,
			REFRACTORY_RHINITIS,
			CONDYLOMA_LATA,
			CENTRAL_NERVOUS_SYSTEM_DAMAGE,
			MALNUTRITION,
			NEPHROTIC_SYNDROME,
			HEPATOSPLENOMEGALY,
			MUCOCUTANEOUS_LESION));

	private static final Map<SyphilisStage, List<String>> SYPHILIS_STAGE_SYMPTOMS_DICTIONARY = Map.of(
		SyphilisStage.PRIMARY_SYPHILIS,
		List.of(CHANCRE),
		SyphilisStage.SECONDARY_SYPHILIS,
		// #13399 - MACULOPAPULAR_RASH is deprecated, replaced by SKIN_RASH
		List.of(SKIN_RASH, CONDYLOMA_VENEREUM, MUCOUS_PATCHES, GENERALIZED_LYMPHADENOPATHY, PATCHY_ALOPECIA),
		SyphilisStage.NEUROLOGICAL_SYPHILIS,
		List.of(NEUROLOGICAL_MANIFESTATIONS));

	private static final List<String> YES_NO_UNKNOWN_SYMPTOM_FIELD_IDS = Collections.unmodifiableList(
		Arrays.asList(
			PARENT_TIME_OFF_WORK,
			JAUNDICE_WITHIN_24_HOURS_OF_BIRTH,
			DATE_OF_ONSET_KNOWN,
			OTHER_NEUROLOGICAL_SYMPTOMS,
			OTHER_GENERAL_SYMPTOMS,
			UNKNOWN_COMPLICATIONS,
			NO_COMPLICATIONS,
			// Diphtheria clinical criteria
			CONJUNCTIVAL_PRESENTATION,
			CUTANEOUS_PRESENTATION,
			GENITAL_PRESENTATION,
			NASAL_DISCHARGE_CLEAR_TO_BLOODY,
			OTHER_CLINICAL_CRITERIA,
			RESPIRATORY_AND_CUTANEOUS_PRESENTATION,
			RESPIRATORY_WITH_MEMBRANE,
			RESPIRATORY_WITHOUT_MEMBRANE,
			UNKNOWN_CLINICAL_CRITERIA,
			// Diphtheria complication diagnosis
			NO_COMPLICATION_DIAGNOSIS,
			CARDIAC_DISORDER,
			NEUROLOGICAL_COMPLICATION_DIAGNOSIS,
			NEUROLOGICAL_AND_CARDIAC_DISORDER,
			OTHER_COMPLICATION_DIAGNOSIS,
			UNKNOWN_COMPLICATION_DIAGNOSIS));
	private static final List<String> COMBO_BOX_FIELDS = Collections.unmodifiableList(Arrays.asList(CLINICAL_MANIFESTATION));

	//	public static final String SKIN_RASH_ONSET_DATE_LAYOUT = fluidRowLocs(6, "LBL_SKIN_RASH_ONSET_DATE", 1, "", 5, SKIN_RASH_ONSET_DATE);
	private static final List<Disease> CLINICAL_SIGNS_AND_SYMPTOMS_HIDE_DISEASES = Arrays.asList(
		Disease.MALARIA,
		Disease.INVASIVE_MENINGOCOCCAL_INFECTION,
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION,
		Disease.PERTUSSIS,
		Disease.SHIGELLOSIS,
		Disease.GONOCOCCAL_INFECTION,
		Disease.YERSINIOSIS);
	// other complicated symptom for onset field listener action
	private static final List<String> OTHER_COMPLICATED_SYMPTOMS = Collections
		.unmodifiableList(Arrays.asList(LESIONS_THAT_ITCH, OTHER_COMPLICATIONS_TEXT, UNKNOWN_COMPLICATIONS, OTHER_NEUROLOGICAL_SYMPTOMS_TEXT));

	// for Mumps few symptoms should display as labels at clinical manifestation of the disease according to case definition section.
	private static final List<String> MUMPS_REPORTING_SYMPTOMS =
		Collections.unmodifiableList(Arrays.asList(OTHER_COMPLICATIONS_TEXT, MENINGITIS, SALIVARY_SWELLING, ORCHITIS));

	final boolean isLuxDengue;
	final boolean isParasiticInfectiousDiseases;
	final boolean isFoodborneGastrointestinal;
	final boolean isSyphilis;
	final boolean isYersiniosis;
	final boolean isLux;

	private final CaseDataDto caze;
	private final Disease disease;
	private final SyphilisPresentation syphilisPresentation;
	private final PersonDto person;
	private final SymptomsContext symptomsContext;
	private final ViewMode viewMode;

	private transient List<String> unconditionalSymptomFieldIds;
	private List<String> conditionalBleedingSymptomFieldIds;
	private List<String> lesionsFieldIds;
	private List<String> lesionsLocationFieldIds;
	private List<String> monkeypoxImageFieldIds;
	private boolean isListenerAction = false;
	private CaseSymptomSideViewComponent caseSymptomSideViewComponent;
	private VerticalLayout clinicalManifestationLayout;
	private Map<Field<?>, Label> symptomLabelMap = new HashMap<>();
	private VerticalLayout mainLayout;
	// components rendered in the same row as a dependent field, e.g. the monkeypox pictures
	private final Map<String, Component> dependentCompanions = new HashMap<>();

	public SymptomsForm(
		CaseDataDto caze,
		Disease disease,
		SyphilisPresentation syphilisPresentation,
		PersonDto person,
		SymptomsContext symptomsContext,
		ViewMode viewMode,
		UiFieldAccessCheckers fieldAccessCheckers) {

		// TODO add user right parameter
		super(
			SymptomsDto.class,
			I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease)
				.andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale())
				.add(new OutbreakFieldVisibilityChecker(viewMode)),
			fieldAccessCheckers);

		this.caze = caze;
		this.disease = disease;
		this.syphilisPresentation = syphilisPresentation;
		this.person = person;
		this.symptomsContext = symptomsContext;
		this.viewMode = viewMode;
		if (disease == null || symptomsContext == null) {
			throw new IllegalArgumentException("disease and symptoms context cannot be null");
		}
		if (symptomsContext == SymptomsContext.CASE && caze == null) {
			throw new IllegalArgumentException("case cannot be null when symptoms context is case");
		}
		isLux = FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG);
		isLuxDengue = isLux && disease == Disease.DENGUE;
		isParasiticInfectiousDiseases = ImmutableList.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(disease);
		isYersiniosis = disease == Disease.YERSINIOSIS;
		isFoodborneGastrointestinal = disease == Disease.SALMONELLOSIS || isYersiniosis;
		isSyphilis = disease == Disease.SYPHILIS;

		addFields();
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {
		if (disease == null || symptomsContext == null) {
			// workaround to stop initialization until disease is set
			return;
		}

		// Clinical measurements are not displayed for Syphilis and Diphtheria
		final boolean hideClinicalMeasurements = isSyphilis || disease == Disease.DIPHTHERIA;

		Label signsAndSymptomsHeadingLabel =
			createLabel(I18nProperties.getString(Strings.headingSignsAndSymptoms), H3, SIGNS_AND_SYMPTOMS_HEADING_LOC);

		Label clinicalManifestationLabel =
			createLabel(I18nProperties.getCaption(Captions.Symptoms_clinicalManifestation), H4, CLINICAL_MANIFESTATION_HEADING_LOC);
		clinicalManifestationLabel.setVisible(Disease.MUMPS == disease);

		DateField onsetDateField = addField(ONSET_DATE, DateField.class);
		ComboBox onsetSymptom = addField(ONSET_SYMPTOM, ComboBox.class);
		if (symptomsContext == SymptomsContext.CASE) {
			// If the symptom onset date is after the hospital admission date, show a warning but don't prevent the user from saving
			onsetDateField.addValueChangeListener(event -> {
				if (caze.getHospitalization().getAdmissionDate() != null
					&& DateComparator.getDateInstance().compare(caze.getHospitalization().getAdmissionDate(), onsetDateField.getValue()) < 0) {
					onsetDateField.setComponentError(new ErrorMessage() {

						@Override
						public ErrorLevel getErrorLevel() {
							return ErrorLevel.INFO;
						}

						@Override
						public String getFormattedHtmlMessage() {
							return I18nProperties.getValidationError(
								Validations.beforeDateSoft,
								onsetDateField.getCaption(),
								I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE));
						}
					});
				} else if (onsetDateField.isValid()) {
					onsetDateField.setComponentError(null);
				}
			});
		}

		ComboBox temperature = addField(TEMPERATURE, ComboBox.class);
		temperature.setImmediate(true);
		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperature.addItem(temperatureValue);
			temperature.setItemCaption(temperatureValue, SymptomsHelper.getTemperatureString(temperatureValue));
		}
		if (symptomsContext == SymptomsContext.CASE) {
			temperature.setCaption(I18nProperties.getCaption(Captions.symptomsMaxTemperature));
		}
		addField(TEMPERATURE_SOURCE);

		ComboBox bloodPressureSystolic = addField(BLOOD_PRESSURE_SYSTOLIC, ComboBox.class);
		bloodPressureSystolic.addItems(SymptomsHelper.getBloodPressureValues());
		ComboBox bloodPressureDiastolic = addField(BLOOD_PRESSURE_DIASTOLIC, ComboBox.class);
		bloodPressureDiastolic.addItems(SymptomsHelper.getBloodPressureValues());
		ComboBox heartRate = addField(HEART_RATE, ComboBox.class);
		heartRate.addItems(SymptomsHelper.getHeartRateValues());
		ComboBox respiratoryRate = addField(RESPIRATORY_RATE, ComboBox.class);
		respiratoryRate.addItems(SymptomsHelper.getRespiratoryRateValues());
		ComboBox weight = addField(WEIGHT, ComboBox.class);
		for (Integer weightValue : SymptomsHelper.getWeightValues()) {
			weight.addItem(weightValue);
			weight.setItemCaption(weightValue, SymptomsHelper.getDecimalString(weightValue));
		}
		ComboBox height = addField(HEIGHT, ComboBox.class);
		height.addItems(SymptomsHelper.getHeightValues());
		ComboBox midUpperArmCircumference = addField(MID_UPPER_ARM_CIRCUMFERENCE, ComboBox.class);
		for (Integer circumferenceValue : SymptomsHelper.getMidUpperArmCircumferenceValues()) {
			midUpperArmCircumference.addItem(circumferenceValue);
			midUpperArmCircumference.setItemCaption(circumferenceValue, SymptomsHelper.getDecimalString(circumferenceValue));
		}
		ComboBox glasgowComaScale = addField(GLASGOW_COMA_SCALE, ComboBox.class);
		glasgowComaScale.addItems(SymptomsHelper.getGlasgowComaScaleValues());

		NullableOptionGroup unexpectedBleeding = addField(UNEXPLAINED_BLEEDING);

		addFields(
			VOMITING,
			DIARRHEA,
			WATERY_DIARRHEA,
			BLOOD_IN_STOOL,
			NAUSEA,
			ABDOMINAL_PAIN,
			CONSTIPATION,
			DYSURIA,
			EYE_IRRITATION,
			HEADACHE,
			MUSCLE_PAIN,
			FATIGUE_WEAKNESS,
			SKIN_RASH,
			NECK_STIFFNESS,
			SORE_THROAT,
			COUGH,
			COUGH_WITH_SPUTUM,
			// COUGH_WITH_HEAMOPTYSIS, // #13399 - deprecated, replaced by COUGHING_BLOOD
			RUNNY_NOSE,
			DIFFICULTY_BREATHING,
			CHEST_PAIN,
			CONJUNCTIVITIS,
			EYE_PAIN_LIGHT_SENSITIVE,
			KOPLIKS_SPOTS,
			THROBOCYTOPENIA,
			OTITIS_MEDIA,
			HEARINGLOSS,
			DEHYDRATION,
			ANOREXIA_APPETITE_LOSS,
			REFUSAL_FEEDOR_DRINK,
			// JOINT_PAIN, // #13399 - deprecated, replaced by ARTHRALGIA
			HICCUPS,
			BACKACHE,
			EYES_BLEEDING,
			JAUNDICE,
			DARK_URINE,
			STOMACH_BLEEDING,
			RAPID_BREATHING,
			SWOLLEN_GLANDS,

			GUMS_BLEEDING,
			INJECTION_SITE_BLEEDING,
			NOSE_BLEEDING,
			BLOODY_BLACK_STOOL,
			RED_BLOOD_VOMIT,
			DIGESTED_BLOOD_VOMIT,
			COUGHING_BLOOD,
			COUGHING_BOUTS,
			COUGHS_PROVOKE_VOMITING,
			BLEEDING_VAGINA,
			SKIN_BRUISING,
			BLOOD_URINE,
			OTHER_HEMORRHAGIC_SYMPTOMS,
			OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
			OTHER_NON_HEMORRHAGIC_SYMPTOMS,
			OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT,
			LESIONS,
			LESIONS_THAT_ITCH,
			LESIONS_SAME_STATE,
			LESIONS_SAME_SIZE,
			LESIONS_DEEP_PROFOUND,
			LESIONS_FACE,
			LESIONS_LEGS,
			LESIONS_SOLES_FEET,
			LESIONS_PALMS_HANDS,
			LESIONS_THORAX,
			LESIONS_ARMS,
			LESIONS_GENITALS,
			LESIONS_ALL_OVER_BODY,
			LYMPHADENOPATHY,
			LYMPHADENOPATHY_AXILLARY,
			LYMPHADENOPATHY_CERVICAL,
			LYMPHADENOPATHY_INGUINAL,
			BRONCHITIS,
			MYOCARDITIS,
			PERICARDITIS,
			THROMBOCYTOPENIA,
			LYMPHADENOPATHY_SUBOCCIPITAL,
			LYMPHADENOPATHY_RETROAURICULAR,
			CHILLS_SWEATS,
			BEDRIDDEN,
			ORAL_ULCERS,
			PAINFUL_LYMPHADENITIS,
			BLACKENING_DEATH_OF_TISSUE,
			BUBOES_GROIN_ARMPIT_NECK,
			BULGING_FONTANELLE,
			PHARYNGEAL_ERYTHEMA,
			PHARYNGEAL_EXUDATE,
			OEDEMA_FACE_NECK,
			OEDEMA_LOWER_EXTREMITY,
			LOSS_SKIN_TURGOR,
			// PALPABLE_LIVER, // #13399 - deprecated, replaced by HEPATOMEGALY
			// PALPABLE_SPLEEN, // #13399 - deprecated, replaced by SPLENOMEGALY
			MALAISE,
			SUNKEN_EYES_FONTANELLE,
			SIDE_PAIN,
			FLUID_IN_LUNG_CAVITY,
			TREMOR,
			BILATERAL_CATARACTS,
			UNILATERAL_CATARACTS,
			CONGENITAL_GLAUCOMA,
			CONGENITAL_HEART_DISEASE,
			PIGMENTARY_RETINOPATHY,
			RADIOLUCENT_BONE_DISEASE,
			SPLENOMEGALY,
			MICROCEPHALY,
			MENINGOENCEPHALITIS,
			PURPURIC_RASH,
			DEVELOPMENTAL_DELAY,
			CONGENITAL_HEART_DISEASE_TYPE,
			CONGENITAL_HEART_DISEASE_DETAILS,
			STILLBORN_INFANT,
			UNKNOWN_CLINICAL_PRESENTATION,
			NEUROLOGICAL_COMPLICATIONS,
			LOW_BIRTH_WEIGHT,
			HEPATITIS,
			THROMBOCYTOPENIC_PURPURA,
			RUBELLA_ARTHRITIS,
			GREGGS_TRIAD,
			JAUNDICE_WITHIN_24_HOURS_OF_BIRTH,
			PATIENT_ILL_LOCATION,
			HYDROPHOBIA,
			OPISTHOTONUS,
			ANXIETY_STATES,
			APNOEA,
			DELIRIUM,
			UPROARIOUSNESS,
			PARASTHESIA_AROUND_WOUND,
			EXCESS_SALIVATION,
			INSOMNIA,
			PARALYSIS,
			EXCITATION,
			DYSPHAGIA,
			AEROPHOBIA,
			HYPERACTIVITY,
			PARESIS,
			AGITATION,
			ASCENDING_FLACCID_PARALYSIS,
			ERRATIC_BEHAVIOUR,
			COMA,
			// CONVULSION, // #13399 - deprecated, replaced by SEIZURES, which also took over its Dengue complication
			FLUID_IN_LUNG_CAVITY_AUSCULTATION,
			FLUID_IN_LUNG_CAVITY_XRAY,
			ABNORMAL_LUNG_XRAY_FINDINGS,
			// CONJUNCTIVAL_INJECTION, // #13399 - deprecated, replaced by CONJUNCTIVITIS
			ACUTE_RESPIRATORY_DISTRESS_SYNDROME,
			PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
			LOSS_OF_TASTE,
			LOSS_OF_SMELL,
			WHEEZING,
			WHOOP_SOUND,
			NOCTURNAL_COUGH,
			SKIN_ULCERS,
			INABILITY_TO_WALK,
			IN_DRAWING_OF_CHEST_WALL,
			// FEELING_ILL, // #13399 - deprecated, replaced by MALAISE
			SHIVERING,
			RESPIRATORY_DISEASE_VENTILATION,
			FAST_HEART_RATE,
			OXYGEN_SATURATION_LOWER_94,
			FEVERISHFEELING,
			WEAKNESS,
			FATIGUE,
			COUGH_WITHOUT_SPUTUM,
			BREATHLESSNESS,
			CHEST_PRESSURE,
			BLUE_LIPS,
			BLOOD_CIRCULATION_PROBLEMS,
			PALPITATIONS,
			DIZZINESS_STANDING_UP,
			HIGH_OR_LOW_BLOOD_PRESSURE,
			URINARY_RETENTION,
			FEVER,
			BLOATING,
			REOCCURRENCE,
			WEIGHT_LOSS,
			WEIGHT_LOSS_AMOUNT,
			EGGY_BURPS,
			SYMPTOM_CURRENT_STATUS,
			DIFFICULTY_BREATHING_DURING_MEALS,
			PARADOXICAL_BREATHING,
			RESPIRATORY_FATIGUE,
			CLAMMY_SKIN,
			COLD_SKIN,
			ENCEPHALITIS,
			GUILLAIN_BARRE_SYNDROME,
			LETHARGY,
			// CONFUSION, // #13399 - deprecated, replaced by CONFUSED_DISORIENTED
			PERSISTENT_VOMITING,
			RESTLESSNESS,
			ACUTE_BLEEDING,
			SEVERE_ORGAN_IMPAIRMENT,
			PLASMA_LEAKAGE_SIGN,
			POLYDIPSIA,
			SYNDROMIC_FLU,
			ANEMIA,
			// ALTERED_LEVEL_OF_CONSCIOUSNESS, // #13399 - deprecated, replaced by ALTERED_CONSCIOUSNESS
			SEVERE_ANEMIA,
			ACUTE_KIDNEY_FAILURE,
			METABOLIC_ACIDOSIS,
			DISSEMINATED_INTRA_VASCULAR_COAGULATION,
			OFFSET_DATE,
			SYMPTOM_END_DATE,
			CLINICAL_MANIFESTATION_TEXT,
			CEREBRAL_MALARIA,
			SCANT_HEMORRHAGE,
			OTHER_NEUROLOGICAL_SYMPTOMS,
			OTHER_NEUROLOGICAL_SYMPTOMS_TEXT,
			FATAL_RISK,
			TENESMUS,
			BLOODY_DIARRHEA,
			HAEMOLYTIC_UREMIC_SYNDROME,
			PATCHY_ALOPECIA,
			CHANCRE,
			CONDYLOMA_VENEREUM,
			MUCOUS_PATCHES,
			GENERALIZED_LYMPHADENOPATHY,
			OCULAR_MANIFESTATIONS,
			NEUROLOGICAL_MANIFESTATIONS,
			CONDYLOMA_LATA,
			BONY_ABNORMALITIES,
			PSEUDOPARALYSIS,
			REFRACTORY_RHINITIS,
			CENTRAL_NERVOUS_SYSTEM_DAMAGE,
			MALNUTRITION,
			NEPHROTIC_SYNDROME,
			SYPHILIS_INFECTION_SITE,
			SYPHILIS_STAGE,
			SYPHILIS_INFECTIOUSNESS,
			CLINICAL_CRITERIA_MET,
			ARTHRALGIA,
			HEPATOMEGALY,
			HEPATOSPLENOMEGALY,
			LOW_GRADE_FEVER,
			MUCOCUTANEOUS_LESION,
			// MACULOPAPULAR_RASH, // #13399 - deprecated, replaced by SKIN_RASH
			PSEUDO_APPENDICULAR_SYNDROME,
			NECROTIZING_ENTEROCOLITIS,
			REACTIVE_ARTHRITIS,
			ERYTHEMA_NODOSUM,
			SALIVARY_SWELLING,
			NO_COMPLICATIONS,
			UNKNOWN_COMPLICATIONS,
			ORCHITIS,
			PANCREATITIS,
			OTHER_GENERAL_SYMPTOMS,
			OTHER_GENERAL_SYMPTOMS_TEXT,
			CERVICITIS,
			NEWBORN_CONJUNCTIVITIS,
			EPIDIDYMITIS,
			PELVIC_INFLAMMATORY_DISEASE,
			PROCTITIS,
			ACUTE_SALPINGITIS,
			URETHRITIS,
			GONOCOCCAL_INFECTION_SITE_ANORECTAL,
			GONOCOCCAL_INFECTION_SITE_BLOOD,
			GONOCOCCAL_INFECTION_SITE_CEREBROSPINAL_FLUID,
			GONOCOCCAL_INFECTION_SITE_EYE,
			GONOCOCCAL_INFECTION_SITE_GENITAL,
			GONOCOCCAL_INFECTION_SITE_JOINT_FLUID,
			GONOCOCCAL_INFECTION_SITE_PHARYNGEAL,
			GONOCOCCAL_INFECTION_SITE_UNKNOWN,
			GONOCOCCAL_INFECTION_SITE_OTHER,
			GONOCOCCAL_INFECTION_SITE_OTHER_TEXT,
			NO_CONCURRENT_STI,
			CONCURRENT_STI_CHLAMYDIA,
			CONCURRENT_STI_GENITAL_HERPES,
			CONCURRENT_STI_LGV,
			CONCURRENT_STI_MYCOPLASMA_GENITALIUM,
			CONCURRENT_STI_INFECTIOUS_SYPHILIS,
			CONCURRENT_STI_TRICHOMONAS_VAGINALIS,
			CONCURRENT_STI_GENITAL_WARTS,
			CONCURRENT_STI_OTHER,
			CONCURRENT_STI_OTHER_TEXT,
			CONCURRENT_STI_UNKNOWN,
			CONJUNCTIVAL_PRESENTATION,
			CUTANEOUS_PRESENTATION,
			GENITAL_PRESENTATION,
			NASAL_DISCHARGE_CLEAR_TO_BLOODY,
			OTHER_CLINICAL_CRITERIA,
			OTHER_CLINICAL_CRITERIA_TEXT,
			RESPIRATORY_AND_CUTANEOUS_PRESENTATION,
			RESPIRATORY_WITH_MEMBRANE,
			RESPIRATORY_WITHOUT_MEMBRANE,
			UNKNOWN_CLINICAL_CRITERIA,
			NO_COMPLICATION_DIAGNOSIS,
			CARDIAC_DISORDER,
			NEUROLOGICAL_COMPLICATION_DIAGNOSIS,
			NEUROLOGICAL_AND_CARDIAC_DISORDER,
			OTHER_COMPLICATION_DIAGNOSIS,
			OTHER_COMPLICATION_DIAGNOSIS_TEXT,
			UNKNOWN_COMPLICATION_DIAGNOSIS);

		// Comments can be long, so they are entered in a multi-line text area that gets a row of its own (see populateGroupLayout)
		TextArea symptomsComments = addField(SYMPTOMS_COMMENTS, TextArea.class);
		symptomsComments.setRows(3);
		symptomsComments.setDescription(
			I18nProperties.getPrefixDescription(I18N_PREFIX, SYMPTOMS_COMMENTS, "") + "\n" + I18nProperties.getDescription(Descriptions.descGdpr));

		addField(LESIONS_ONSET_DATE, DateField.class);

		// complications
		String[] complicationsFieldIds = {
			ALTERED_CONSCIOUSNESS,
			CONFUSED_DISORIENTED,
			OTHER_COMPLICATIONS,
			OTHER_COMPLICATIONS_TEXT,
			HEMORRHAGIC_SYNDROME,
			HYPERGLYCEMIA,
			HYPOGLYCEMIA,
			MENINGEAL_SIGNS,
			SEIZURES,
			SEPSIS,
			SHOCK };

		addFields(complicationsFieldIds);

		// Clinical presentation
		String[] clinicalPresentationFieldIds = {
			HEMORRHAGIC_RASH,
			ARTHRITIS,
			MENINGITIS,
			SEPTICAEMIA,
			UNKNOWN_SYMPTOM,
			// ACUTE_ENCEPHALITIS, // #13399 - deprecated, replaced by ENCEPHALITIS
			OTHER_CLINICAL_PRESENTATION,
			OTHER_CLINICAL_PRESENTATION_TEXT };
		addFields(clinicalPresentationFieldIds);

		NullableOptionGroup asymptomaticNOG = addField(ASYMPTOMATIC);
		addField(CLINICAL_MANIFESTATION);

		// Asymptomatic excluded elements
		// For Mumps, its possible to have a case with an asymptomatic symptoms along with no complicated symptoms
		List<String> nonAsymptomaticFields = Arrays.asList(ASYMPTOMATIC, NO_COMPLICATIONS);

		// toggling the onset Symptom and date based on asymptomatic
		asymptomaticNOG.addValueChangeListener(e -> {
			boolean isSymptomatic = !SymptomState.YES.equals(asymptomaticNOG.getNullableValue());

			editableAllowedFields().stream().filter(field -> !nonAsymptomaticFields.contains(field.getId())).forEach(field -> {
				if (!isSymptomatic) {
					field.clear();
				}
				field.setEnabled(isSymptomatic);
				onsetSymptom.setEnabled(isSymptomatic);
				setOnsetDateEnabled(onsetDateField, isSymptomatic);
			});
		});

		if (disease == Disease.GONOCOCCAL_INFECTION) {
			NullableOptionGroup noConcurrentStiField = getField(NO_CONCURRENT_STI);
			List<String> concurrentStiFields = Arrays.asList(
				CONCURRENT_STI_CHLAMYDIA,
				CONCURRENT_STI_GENITAL_HERPES,
				CONCURRENT_STI_LGV,
				CONCURRENT_STI_MYCOPLASMA_GENITALIUM,
				CONCURRENT_STI_INFECTIOUS_SYPHILIS,
				CONCURRENT_STI_TRICHOMONAS_VAGINALIS,
				CONCURRENT_STI_GENITAL_WARTS,
				CONCURRENT_STI_OTHER,
				CONCURRENT_STI_OTHER_TEXT,
				CONCURRENT_STI_UNKNOWN);
			noConcurrentStiField.addValueChangeListener(e -> {
				boolean noConcurrentSti = SymptomState.YES.equals(noConcurrentStiField.getNullableValue());
				concurrentStiFields.stream().map(fieldId -> (Field<?>) getField(fieldId)).filter(Objects::nonNull).forEach(field -> {
					if (noConcurrentSti) {
						field.clear();
					}
					field.setEnabled(!noConcurrentSti);
				});
			});
		}

		monkeypoxImageFieldIds = Arrays.asList(LESIONS_RESEMBLE_IMG1, LESIONS_RESEMBLE_IMG2, LESIONS_RESEMBLE_IMG3, LESIONS_RESEMBLE_IMG4);
		for (String propertyId : monkeypoxImageFieldIds) {
			@SuppressWarnings("rawtypes")
			Field monkeypoxImageField = addField(propertyId);
			CssStyles.style(monkeypoxImageField, VSPACE_NONE);
		}

		addField(SKIN_RASH_ONSET_DATE, DateField.class);

		Field<?> parentTimeOffWorkField = addField(PARENT_TIME_OFF_WORK);
		TextField timeOffWorkDaysField = addField(TIME_OFF_WORK_DAYS, TextField.class);
		timeOffWorkDaysField.setConverter(new StringToFloatConverter());
		timeOffWorkDaysField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, timeOffWorkDaysField.getCaption()));
		Field<?> overNightStayRequiredField = addField(OVERNIGHT_STAY_REQUIRED);
		addField(DURATION_OF_SYMPTOMS, TextField.class);

		if (isLux && disease == Disease.TUBERCULOSIS) {
			// #14029: no first symptom for Luxembourg Tuberculosis; hidden before the layout is built so it is never placed
			onsetSymptom.setVisible(false);
			addField(DIAGNOSIS, ComboBox.class);

			ComboBox majorSiteField = new ComboBox();
			majorSiteField.addItems(InfectionSite.filter(disease, true, false));
			majorSiteField.setCaption(I18nProperties.getCaption(Captions.Symptoms_majorSite));
			addField(MAJOR_SITE, majorSiteField);

			ComboBox minorSiteField = new ComboBox();
			minorSiteField.addItems(InfectionSite.filter(disease, false, true));
			minorSiteField.setCaption(I18nProperties.getCaption(Captions.Symptoms_minorSite));
			addField(MINOR_SITE, minorSiteField);

			addField(OTHER_MAJOR_SITE_DETAILS, TextField.class);
			addField(OTHER_MINOR_SITE_DETAILS, TextField.class);

			addField(DATE_OF_ONSET_KNOWN, OptionGroup.class);

			ComboBox clinicalPresentationStatusField = addField(CLINICAL_PRESENTATION_STATUS, ComboBox.class);
			clinicalPresentationStatusField.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
			clinicalPresentationStatusField
				.setItemCaption(ClinicalPresentationStatus.COMPATIBLE, ClinicalPresentationStatus.COMPATIBLE.buildCaption(disease.toShortString()));
			getFieldGroup().getField(OTHER_CLINICAL_PRESENTATION_TEXT).setVisible(true);

		}
		// Set initial visibilities

		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		if (symptomsContext != SymptomsContext.CLINICAL_VISIT) {
			setVisible(
				false,
				BLOOD_PRESSURE_SYSTOLIC,
				BLOOD_PRESSURE_DIASTOLIC,
				HEART_RATE,
				RESPIRATORY_RATE,
				WEIGHT,
				HEIGHT,
				MID_UPPER_ARM_CIRCUMFERENCE,
				GLASGOW_COMA_SCALE);
		} else {
			setVisible(false, ONSET_SYMPTOM, ONSET_DATE);
		}

		if (hideClinicalMeasurements) {
			// Clinical measurements are not displayed for Syphilis and Diphtheria regardless of context
			setVisible(
				false,
				TEMPERATURE,
				TEMPERATURE_SOURCE,
				BLOOD_PRESSURE_SYSTOLIC,
				BLOOD_PRESSURE_DIASTOLIC,
				HEART_RATE,
				RESPIRATORY_RATE,
				WEIGHT,
				HEIGHT,
				MID_UPPER_ARM_CIRCUMFERENCE,
				GLASGOW_COMA_SCALE);
		}

		// Initialize lists

		conditionalBleedingSymptomFieldIds = Arrays.asList(
			GUMS_BLEEDING,
			INJECTION_SITE_BLEEDING,
			NOSE_BLEEDING,
			BLOODY_BLACK_STOOL,
			RED_BLOOD_VOMIT,
			DIGESTED_BLOOD_VOMIT,
			EYES_BLEEDING,
			COUGHING_BLOOD,
			BLEEDING_VAGINA,
			SKIN_BRUISING,
			STOMACH_BLEEDING,
			BLOOD_URINE,
			ACUTE_BLEEDING,
			OTHER_HEMORRHAGIC_SYMPTOMS);

		lesionsFieldIds = Arrays.asList(LESIONS_SAME_STATE, LESIONS_SAME_SIZE, LESIONS_DEEP_PROFOUND, LESIONS_THAT_ITCH);

		lesionsLocationFieldIds = Arrays.asList(
			LESIONS_FACE,
			LESIONS_LEGS,
			LESIONS_SOLES_FEET,
			LESIONS_PALMS_HANDS,
			LESIONS_THORAX,
			LESIONS_ARMS,
			LESIONS_GENITALS,
			LESIONS_ALL_OVER_BODY);

		unconditionalSymptomFieldIds = Arrays.asList(
			FEVER,
			ABNORMAL_LUNG_XRAY_FINDINGS,
			// CONJUNCTIVAL_INJECTION, // #13399 - deprecated, replaced by CONJUNCTIVITIS
			ACUTE_RESPIRATORY_DISTRESS_SYNDROME,
			PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
			VOMITING,
			DIARRHEA,
			WATERY_DIARRHEA,
			BLOOD_IN_STOOL,
			NAUSEA,
			ABDOMINAL_PAIN,
			CONSTIPATION,
			DYSURIA,
			EYE_IRRITATION,
			HEADACHE,
			MUSCLE_PAIN,
			FATIGUE_WEAKNESS,
			SKIN_RASH,
			NECK_STIFFNESS,
			SORE_THROAT,
			COUGH,
			COUGH_WITH_SPUTUM,
			// COUGH_WITH_HEAMOPTYSIS, // #13399 - deprecated, replaced by COUGHING_BLOOD
			RUNNY_NOSE,
			DIFFICULTY_BREATHING,
			DIFFICULTY_BREATHING_DURING_MEALS,
			CHEST_PAIN,
			CONJUNCTIVITIS,
			EYE_PAIN_LIGHT_SENSITIVE,
			KOPLIKS_SPOTS,
			THROBOCYTOPENIA,
			OTITIS_MEDIA,
			HEARINGLOSS,
			DEHYDRATION,
			ANOREXIA_APPETITE_LOSS,
			REFUSAL_FEEDOR_DRINK,
			// JOINT_PAIN, // #13399 - deprecated, replaced by ARTHRALGIA
			HICCUPS,
			BACKACHE,
			JAUNDICE,
			DARK_URINE,
			RAPID_BREATHING,
			SWOLLEN_GLANDS,
			UNEXPLAINED_BLEEDING,
			OTHER_NON_HEMORRHAGIC_SYMPTOMS,
			LESIONS,
			LYMPHADENOPATHY,
			LYMPHADENOPATHY_AXILLARY,
			LYMPHADENOPATHY_CERVICAL,
			LYMPHADENOPATHY_INGUINAL,
			BRONCHITIS,
			MYOCARDITIS,
			PERICARDITIS,
			THROMBOCYTOPENIA,
			LYMPHADENOPATHY_SUBOCCIPITAL,
			LYMPHADENOPATHY_RETROAURICULAR,
			CHILLS_SWEATS,
			BEDRIDDEN,
			ORAL_ULCERS,
			PAINFUL_LYMPHADENITIS,
			BLACKENING_DEATH_OF_TISSUE,
			BUBOES_GROIN_ARMPIT_NECK,
			BULGING_FONTANELLE,
			PHARYNGEAL_ERYTHEMA,
			PHARYNGEAL_EXUDATE,
			OEDEMA_FACE_NECK,
			OEDEMA_LOWER_EXTREMITY,
			LOSS_SKIN_TURGOR,
			// PALPABLE_LIVER, // #13399 - deprecated, replaced by HEPATOMEGALY
			// PALPABLE_SPLEEN, // #13399 - deprecated, replaced by SPLENOMEGALY
			MALAISE,
			SUNKEN_EYES_FONTANELLE,
			SIDE_PAIN,
			FLUID_IN_LUNG_CAVITY,
			FLUID_IN_LUNG_CAVITY_AUSCULTATION,
			FLUID_IN_LUNG_CAVITY_XRAY,
			TREMOR,
			BILATERAL_CATARACTS,
			UNILATERAL_CATARACTS,
			CONGENITAL_GLAUCOMA,
			CONGENITAL_HEART_DISEASE,
			RADIOLUCENT_BONE_DISEASE,
			SPLENOMEGALY,
			MICROCEPHALY,
			MENINGOENCEPHALITIS,
			DEVELOPMENTAL_DELAY,
			STILLBORN_INFANT,
			UNKNOWN_CLINICAL_PRESENTATION,
			NEUROLOGICAL_COMPLICATIONS,
			LOW_BIRTH_WEIGHT,
			HEPATITIS,
			THROMBOCYTOPENIC_PURPURA,
			RUBELLA_ARTHRITIS,
			GREGGS_TRIAD,
			PURPURIC_RASH,
			PIGMENTARY_RETINOPATHY,
			// CONVULSION, // #13399 - deprecated, replaced by SEIZURES, which also took over its Dengue complication
			AEROPHOBIA,
			AGITATION,
			ANXIETY_STATES,
			APNOEA,
			ASCENDING_FLACCID_PARALYSIS,
			COMA,
			DELIRIUM,
			DYSPHAGIA,
			ERRATIC_BEHAVIOUR,
			EXCESS_SALIVATION,
			EXCITATION,
			HYDROPHOBIA,
			HYPERACTIVITY,
			INSOMNIA,
			OPISTHOTONUS,
			PARADOXICAL_BREATHING,
			PARALYSIS,
			PARASTHESIA_AROUND_WOUND,
			PARESIS,
			UPROARIOUSNESS,
			LOSS_OF_TASTE,
			LOSS_OF_SMELL,
			WHEEZING,
			WHOOP_SOUND,
			NOCTURNAL_COUGH,
			SKIN_ULCERS,
			INABILITY_TO_WALK,
			IN_DRAWING_OF_CHEST_WALL,
			OTHER_COMPLICATIONS,
			// FEELING_ILL, // #13399 - deprecated, replaced by MALAISE
			SHIVERING,
			RESPIRATORY_DISEASE_VENTILATION,
			RESPIRATORY_FATIGUE,
			FAST_HEART_RATE,
			OXYGEN_SATURATION_LOWER_94,
			FEVERISHFEELING,
			WEAKNESS,
			FATIGUE,
			COUGHING_BOUTS,
			COUGHS_PROVOKE_VOMITING,
			COUGH_WITHOUT_SPUTUM,
			BREATHLESSNESS,
			CHEST_PRESSURE,
			BLUE_LIPS,
			BLOOD_CIRCULATION_PROBLEMS,
			PALPITATIONS,
			DIZZINESS_STANDING_UP,
			HIGH_OR_LOW_BLOOD_PRESSURE,
			URINARY_RETENTION,
			// complications
			ALTERED_CONSCIOUSNESS,
			CONFUSED_DISORIENTED,
			HEMORRHAGIC_SYNDROME,
			HYPERGLYCEMIA,
			HYPOGLYCEMIA,
			MENINGEAL_SIGNS,
			SEIZURES,
			SEPSIS,
			SHOCK,
			ASYMPTOMATIC,
			HEMORRHAGIC_RASH,
			ARTHRITIS,
			MENINGITIS,
			SEPTICAEMIA,
			UNKNOWN_SYMPTOM,
			BLOATING,
			REOCCURRENCE,
			OVERNIGHT_STAY_REQUIRED,
			WEIGHT_LOSS,
			WEIGHT_LOSS_AMOUNT,
			EGGY_BURPS,
			SYMPTOM_CURRENT_STATUS,
			DURATION_OF_SYMPTOMS,
			// ACUTE_ENCEPHALITIS, // #13399 - deprecated, replaced by ENCEPHALITIS
			OTHER_CLINICAL_PRESENTATION,
			CLAMMY_SKIN,
			COLD_SKIN,
			ENCEPHALITIS,
			GUILLAIN_BARRE_SYNDROME,
			LETHARGY,
			// CONFUSION, // #13399 - deprecated, replaced by CONFUSED_DISORIENTED
			PERSISTENT_VOMITING,
			RESTLESSNESS,
			SEVERE_ORGAN_IMPAIRMENT,
			PLASMA_LEAKAGE_SIGN,
			POLYDIPSIA,
			SYNDROMIC_FLU,
			ANEMIA,
			// ALTERED_LEVEL_OF_CONSCIOUSNESS, // #13399 - deprecated, replaced by ALTERED_CONSCIOUSNESS
			SEVERE_ANEMIA,
			ACUTE_KIDNEY_FAILURE,
			METABOLIC_ACIDOSIS,
			DISSEMINATED_INTRA_VASCULAR_COAGULATION,
			CEREBRAL_MALARIA,
			SCANT_HEMORRHAGE,
			FATAL_RISK,
			TENESMUS,
			BLOODY_DIARRHEA,
			HAEMOLYTIC_UREMIC_SYNDROME,
			PATCHY_ALOPECIA,
			CHANCRE,
			CONDYLOMA_VENEREUM,
			MUCOUS_PATCHES,
			GENERALIZED_LYMPHADENOPATHY,
			OCULAR_MANIFESTATIONS,
			NEUROLOGICAL_MANIFESTATIONS,
			CONDYLOMA_LATA,
			BONY_ABNORMALITIES,
			PSEUDOPARALYSIS,
			REFRACTORY_RHINITIS,
			CENTRAL_NERVOUS_SYSTEM_DAMAGE,
			MALNUTRITION,
			NEPHROTIC_SYNDROME,
			ARTHRALGIA,
			HEPATOMEGALY,
			HEPATOSPLENOMEGALY,
			LOW_GRADE_FEVER,
			MUCOCUTANEOUS_LESION,
			PSEUDO_APPENDICULAR_SYNDROME,
			NECROTIZING_ENTEROCOLITIS,
			REACTIVE_ARTHRITIS,
			ERYTHEMA_NODOSUM,
			// MACULOPAPULAR_RASH, // #13399 - deprecated, replaced by SKIN_RASH
			SALIVARY_SWELLING,
			ORCHITIS,
			PANCREATITIS);

		// Set visibilities

		NullableOptionGroup feverField = (NullableOptionGroup) getFieldGroup().getField(FEVER);
		feverField.setImmediate(true);

		boolean isInfant = person != null
			&& person.getApproximateAge() != null
			&& ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS) || person.getApproximateAge() <= 1);
		if (!isInfant) {
			getFieldGroup().getField(BULGING_FONTANELLE).setVisible(false);
		}

		// Symptoms hint text
		Label symptomsHint = new Label(
			I18nProperties.getString(symptomsContext == SymptomsContext.CASE ? Strings.messageSymptomsHint : Strings.messageSymptomsVisitHint),
			ContentMode.HTML);
		symptomsHint.setWidth(100, Unit.PERCENTAGE);
		// getContent().addComponent(symptomsHint, SYMPTOMS_HINT_LOC);

		if (disease == Disease.MONKEYPOX) {
			setUpMonkeypoxVisibilities();
		}

		if (symptomsContext != SymptomsContext.CASE) {
			getFieldGroup().getField(PATIENT_ILL_LOCATION).setVisible(false);
		}

		//		symptomGroupVisibility();

		if (isSyphilis) {
			setUpSyphilisVisibilities();
		}

		if (isEditableAllowed(OTHER_HEMORRHAGIC_SYMPTOMS_TEXT)) {
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				getFieldGroup().getField(OTHER_HEMORRHAGIC_SYMPTOMS),
				Arrays.asList(OTHER_HEMORRHAGIC_SYMPTOMS_TEXT),
				Arrays.asList(SymptomState.YES),
				disease);
		}
		if (isEditableAllowed(OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT)) {
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				getFieldGroup().getField(OTHER_NON_HEMORRHAGIC_SYMPTOMS),
				Arrays.asList(OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT),
				Arrays.asList(SymptomState.YES),
				disease);
		}
		if (isEditableAllowed(OTHER_COMPLICATIONS_TEXT)) {
			FieldHelper.setRequiredWhen(
				getFieldGroup(),
				getFieldGroup().getField(OTHER_COMPLICATIONS),
				Arrays.asList(OTHER_COMPLICATIONS_TEXT),
				Arrays.asList(SymptomState.YES),
				disease);
		}

		if (disease != Disease.TUBERCULOSIS) {
			if (isEditableAllowed(OTHER_CLINICAL_PRESENTATION)) {
				FieldHelper.setRequiredWhen(
					getFieldGroup(),
					getFieldGroup().getField(OTHER_CLINICAL_PRESENTATION),
					Arrays.asList(OTHER_CLINICAL_PRESENTATION_TEXT),
					Arrays.asList(SymptomState.YES),
					disease);
			}
		}

		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(LESIONS), lesionsFieldIds, Arrays.asList(SymptomState.YES), disease);
		FieldHelper
			.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(LESIONS), monkeypoxImageFieldIds, Arrays.asList(SymptomState.YES), disease);

		addListenerForOnsetFields(onsetSymptom, onsetDateField);
		if (isLux && disease == Disease.TUBERCULOSIS) {
			// #14029: enables the onset date when "Date of onset known" changes to Yes and clears it otherwise;
			// the symptom listeners use setOnsetDateEnabled, so they don't enable it while it isn't Yes
			FieldHelper.setEnabledWhen(getFieldGroup(), DATE_OF_ONSET_KNOWN, YesNoUnknown.YES, ONSET_DATE, true);
		}

		Button clearAllButton = ButtonHelper.createButton(Captions.actionClearAll, event -> {
			unconditionalSymptomFieldIds.forEach(this::safeClearField);
			conditionalBleedingSymptomFieldIds.forEach(this::safeClearField);
			lesionsFieldIds.forEach(this::safeClearField);
			lesionsLocationFieldIds.forEach(this::safeClearField);
			monkeypoxImageFieldIds.forEach(this::safeClearField);
			YES_NO_UNKNOWN_SYMPTOM_FIELD_IDS.forEach(this::safeClearField);
			COMBO_BOX_FIELDS.forEach(this::safeClearField);
		}, ValoTheme.BUTTON_LINK);

		Button setEmptyToNoButton = createButtonSetClearedToSymptomState(Captions.symptomsSetClearedToNo, SymptomState.NO);

		Button setEmptyToUnknownButton = createButtonSetClearedToSymptomState(Captions.symptomsSetClearedToUnknown, SymptomState.UNKNOWN);

		Label complicationsHeading = new Label(I18nProperties.getString(Strings.headingComplications));
		CssStyles.style(complicationsHeading, CssStyles.H3);
		getContent().addComponent(complicationsHeading, COMPLICATIONS_HEADING);

		if (feverField.isVisible()) {
			temperature.addValueChangeListener(e -> {
				toggleFeverComponentError(feverField, temperature);
			});
			feverField.addValueChangeListener(e -> {
				toggleFeverComponentError(feverField, temperature);
			});
		}

		boolean isComplicationsHeadingVisible = false;
		for (String complicationField : complicationsFieldIds) {
			if (getFieldGroup().getField(complicationField).isVisible()) {
				isComplicationsHeadingVisible = true;
			}
		}
		complicationsHeading.setVisible(isComplicationsHeadingVisible);

		// Set signs and symptoms heading visibility based on disease
		signsAndSymptomsHeadingLabel.setVisible(!CLINICAL_SIGNS_AND_SYMPTOMS_HIDE_DISEASES.contains(disease));

		if (Disease.INVASIVE_MENINGOCOCCAL_INFECTION == disease) {
			getField(SHOCK).setCaption(I18nProperties.getCaption(Captions.Symptoms_imi_shock));
			getField(PNEUMONIA_CLINICAL_OR_RADIOLOGIC).setCaption(I18nProperties.getCaption(Captions.Symptoms_imi_pneumoniaClinicalOrRadiologic));
		} else if (Disease.INVASIVE_PNEUMOCOCCAL_INFECTION == disease) {
			getField(PNEUMONIA_CLINICAL_OR_RADIOLOGIC).setCaption(I18nProperties.getCaption(Captions.Symptoms_ipi_pneumoniaClinicalOrRadiologic));
		}

		if (symptomsContext == SymptomsContext.CASE && caze != null && caze.getDisease() == Disease.PLAGUE) {
			getField(CHEST_PAIN).setVisible(PlagueType.PNEUMONIC == caze.getPlagueType());
		}

		// Change captions for giardiasis and Cryptosporidiosis
		if (isParasiticInfectiousDiseases) {
			getField(OTHER_COMPLICATIONS).setCaption(I18nProperties.getCaption(Captions.Symptoms_otherComplications_CryptoGiardia));
			getField(OTHER_COMPLICATIONS_TEXT).setCaption(I18nProperties.getCaption(Captions.Symptoms_otherComplicationsText_CryptoGiardia));
		}

		if (isParasiticInfectiousDiseases || isYersiniosis) {
			parentTimeOffWorkField.setCaption(I18nProperties.getCaption(Captions.Symptoms_timeOffWorkOrSchool));
			timeOffWorkDaysField.setCaption(I18nProperties.getCaption(Captions.Symptoms_timeOffWorkDaysDuration));
		}

		// temperature and its source hide for LUX's Dengue, Tuberculosis (#14029), Syphilis and Diphtheria
		boolean hideTemperature = isLuxDengue || disease == Disease.TUBERCULOSIS || hideClinicalMeasurements;
		temperature.setVisible(!hideTemperature);
		getField(TEMPERATURE_SOURCE).setVisible(!hideTemperature);

		DateComparisonValidator.addStartEndValidators(onsetDateField, getField(OFFSET_DATE));
		DateComparisonValidator.addStartEndValidators(onsetDateField, getField(SYMPTOM_END_DATE));

		// Navigate to hospitalization view when overnight stay required is set to yes
		overNightStayRequiredField.addValueChangeListener(e -> {
			if (isListenerAction) {
				final boolean isOvernightStayRequiredYes =
					SymptomState.YES.equals(FieldHelper.getNullableSourceFieldValue(overNightStayRequiredField));
				if (isOvernightStayRequiredYes) {
					ControllerProvider.getCaseController().navigateToView(HospitalizationView.VIEW_NAME, caze.getUuid(), null);
				}
			}
			isListenerAction = true;
		});

		// The symptoms hint and the clear/set buttons sit above all groups, so every disease shows them,
		// even when Clinical measurements has no visible fields (e.g. Syphilis). If it has visible fields,
		// SymptomGroupLayout moves them below the Clinical measurements title.
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponent(clearAllButton);
		buttonLayout.addComponent(setEmptyToNoButton);
		buttonLayout.addComponent(setEmptyToUnknownButton);
		buttonLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
		// #14029: Luxembourg Tuberculosis only records the clinical presentation - no symptoms hint or clear/set buttons
		boolean isLuxTuberculosis = isLux && disease == Disease.TUBERCULOSIS;
		symptomsHint.setVisible(!isLuxTuberculosis);
		buttonLayout.setVisible(!isLuxTuberculosis);
		mainLayout.addComponents(symptomsHint, buttonLayout);
		// the clear/set links sit at the right edge, above the symptom groups
		mainLayout.setComponentAlignment(buttonLayout, Alignment.TOP_RIGHT);

		// The bleeding symptoms follow "Unexplained bleeding", not their own @Diseases: as before the regrouping
		// (FieldHelper.setVisibleWhen(conditionalBleedingSymptomFieldIds, UNEXPLAINED_BLEEDING, YES)), all of them are
		// offered for every disease that shows "Unexplained bleeding", e.g. Coronavirus or Plague. They must be visible
		// here so that SymptomGroupLayout lays them out as dependents; it hides them until "Unexplained bleeding" is Yes.
		if (unexpectedBleeding != null && unexpectedBleeding.isVisible()) {
			for (String bleedingSymptomFieldId : conditionalBleedingSymptomFieldIds) {
				Field<?> bleedingSymptomField = getFieldGroup().getField(bleedingSymptomFieldId);
				// defensive - the field may not be part of the form
				if (bleedingSymptomField != null) {
					bleedingSymptomField.setVisible(true);
				}
			}
		}

		new SymptomGroupLayout(mainLayout, getContent(), dependentCompanions).build(getFieldGroup().getFields(), symptomsHint, buttonLayout);
		// Mumps: reported complications are displayed as labels in the clinical manifestation section
		clinicalManifestationLayout = new VerticalLayout();
		clinicalManifestationLayout.setSpacing(false);
		clinicalManifestationLayout.setMargin(false);
		if (Disease.MUMPS == disease) {
			Label clinicalManifestationHeading = new Label(I18nProperties.getCaption(Captions.Symptoms_clinicalManifestation));
			clinicalManifestationHeading.addStyleName(H4);
			mainLayout.addComponents(clinicalManifestationHeading, clinicalManifestationLayout);
		}
	}

	@Override
	public CustomLayout initContent() {

		String templateHtml = "<div location=\"content\"></div>";

		CustomLayout customLayout;
		try {
			customLayout = new CustomLayout(new ByteArrayInputStream(templateHtml.getBytes(StandardCharsets.UTF_8)));
		} catch (IOException e) {
			throw new RuntimeException("Failed to create CustomLayout", e);
		}

		// Everything below is plain Java - no HTML needed for this part
		mainLayout = new VerticalLayout();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
		mainLayout.setSpacing(false);
		mainLayout.setMargin(false);

		customLayout.addComponent(mainLayout, "content");
		return customLayout;
	}

	private void toggleFeverComponentError(NullableOptionGroup feverField, ComboBox temperatureField) {
		Float temperatureValue = (Float) temperatureField.getValue();
		SymptomState feverValue = (SymptomState) feverField.getNullableValue();

		if (temperatureValue != null && temperatureValue >= 38.0f && feverValue != SymptomState.YES) {
			setFeverComponentError(feverField, true);
		} else if (temperatureValue != null && temperatureValue < 38.0f && feverValue != SymptomState.NO) {
			setFeverComponentError(feverField, false);
		} else {
			feverField.setComponentError(null);
		}
	}

	private void setFeverComponentError(NullableOptionGroup feverField, boolean feverSuggested) {
		feverField.setComponentError(new ErrorMessage() {

			@Override
			public ErrorLevel getErrorLevel() {
				return ErrorLevel.INFO;
			}

			@Override
			public String getFormattedHtmlMessage() {
				return I18nProperties.getValidationError(
					feverSuggested ? Validations.feverTemperatureAboveThreshold : Validations.feverTemperatureBelowThreshold,
					I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, FEVER));
			}
		});
	}

	private Label createLabel(String text, String h4, String location) {
		final Label label = new Label(text);
		label.setId(text);
		label.addStyleName(h4);
		getContent().addComponent(label, location);
		return label;
	}

	@Override
	protected String createHtmlLayout() {
		return "";
	}

	public void initializeSymptomRequirementsForVisit(NullableOptionGroup visitStatus) {
		addSoftRequiredStyleWhenSymptomaticAndCooperative(
			getFieldGroup(),
			ONSET_DATE,
			unconditionalSymptomFieldIds,
			Arrays.asList(SymptomState.YES),
			visitStatus);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(
			getFieldGroup(),
			ONSET_SYMPTOM,
			unconditionalSymptomFieldIds,
			Arrays.asList(SymptomState.YES),
			visitStatus);
	}

	@Override
	public void setValue(SymptomsDto newFieldValue) throws ReadOnlyException, ConversionException {
		super.setValue(newFieldValue);

		initializeSymptomRequirementsForCase();
	}

	private void initializeSymptomRequirementsForCase() {
		addSoftRequiredStyleWhenSymptomaticAndCooperative(
			getFieldGroup(),
			ONSET_DATE,
			unconditionalSymptomFieldIds,
			Arrays.asList(SymptomState.YES),
			null);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(
			getFieldGroup(),
			ONSET_SYMPTOM,
			unconditionalSymptomFieldIds,
			Arrays.asList(SymptomState.YES),
			null);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(
			getFieldGroup(),
			PATIENT_ILL_LOCATION,
			unconditionalSymptomFieldIds,
			Arrays.asList(SymptomState.YES),
			null);
	}

	/**
	 * Sets the fields defined by the ids contained in sourceValues to required when the person is symptomatic
	 * and - if a visit is processed - cooperative. When this method is called from within a case, it needs to
	 * be called with visitStatusField set to null in order to ignore the visit status requirement.
	 */
	@SuppressWarnings("rawtypes")
	private void addSoftRequiredStyleWhenSymptomaticAndCooperative(
		FieldGroup fieldGroup,
		Object targetPropertyId,
		List<String> sourcePropertyIds,
		List<Object> sourceValues,
		NullableOptionGroup visitStatusField) {

		for (Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if (sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}
		}

		// Initialize
		final Field targetField = fieldGroup.getField(targetPropertyId);
		if (!targetField.isVisible()) {
			return;
		}
	}

	/**
	 * Returns true if if the value of any field associated with the sourcePropertyIds
	 * is set to one of the values contained in sourceValues.
	 *
	 * @param fieldGroup
	 * @param sourcePropertyIds
	 * @param sourceValues
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean isAnySymptomSetToYes(FieldGroup fieldGroup, List<String> sourcePropertyIds, List<Object> sourceValues) {

		for (Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if (sourceField == null) {
				// defensive - the field is not part of the form (e.g. a commented out REVIEW field)
				continue;
			}
			if (sourceValues.contains(FieldHelper.getNullableSourceFieldValue(sourceField))) {
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	private void addListenerForOnsetFields(ComboBox onsetSymptom, DateField onsetDateField) {
		List<String> allPropertyIds =
			Stream.concat(unconditionalSymptomFieldIds.stream(), conditionalBleedingSymptomFieldIds.stream()).collect(Collectors.toList());
		allPropertyIds.addAll(OTHER_COMPLICATED_SYMPTOMS);
		allPropertyIds.addAll(SYPHILIS_ACQUIRED_ONLY_FIELD_IDS);
		allPropertyIds.addAll(SYPHILIS_CONGENITAL_ONLY_FIELD_IDS);
		// Asymptomatic is not a symptom, so it must never be offered as the first symptom
		allPropertyIds.remove(ASYMPTOMATIC);
		for (Object sourcePropertyId : allPropertyIds) {
			Field sourceField = getFieldGroup().getField(sourcePropertyId);
			if (sourceField == null) {
				// defensive - the field is not part of the form (e.g. a commented out REVIEW field)
				continue;
			}
			sourceField.addValueChangeListener(event -> {
				if (FieldHelper.getNullableSourceFieldValue(sourceField) == SymptomState.YES) {
					onsetSymptom.addItem(sourceField.getCaption());
					setOnsetDateEnabled(onsetDateField, true);
				} else {
					onsetSymptom.removeItem(sourceField.getCaption());
					final Date onsetDate = getValue().getOnsetDate();
					boolean isOnsetDateFieldEnabled = isAnySymptomSetToYes(getFieldGroup(), allPropertyIds, Arrays.asList(SymptomState.YES));
					setOnsetDateEnabled(onsetDateField, isOnsetDateFieldEnabled || !onsetSymptom.isVisible());
					if (onsetDate != null) {
						onsetDateField.setValue(onsetDate);
					} else if (!isOnsetDateFieldEnabled) {
						onsetDateField.setValue(null);
					}
				}
				onsetSymptom.setEnabled(!onsetSymptom.getItemIds().isEmpty());
				onsetSymptom.setEnabled(!onsetSymptom.getItemIds().isEmpty());
				// If the symptom status set to Yes, it should be updated in the complications section,
				// otherwise delete it from the complications section.
				if (caseSymptomSideViewComponent != null) {
					caseSymptomSideViewComponent.toggleComplicationSymptom(sourceField);
					caseSymptomSideViewComponent.refreshLayout();
				}
				// If any one of the symptoms is set to yes, then the ClinicalManifestation field should be defaulting to "Disease with sign of severity (WHO definition)"
				// and offset date should be enabled based on onset symptom visibility.
				ComboBox cmCb = getField(CLINICAL_MANIFESTATION);
				DateField offsetDate = getField(OFFSET_DATE);

				// If the disease is Dengue and any of the symptoms is set to yes,
				// then the ClinicalManifestation field should be defaulting to "Disease with sign of severity (WHO definition)" and offset date should be enabled based on onset symptom visibility.
				boolean hasOnsetSymptoms = onsetSymptom.isEnabled() && Disease.DENGUE == disease;
				offsetDate.setEnabled(hasOnsetSymptoms);
				if (!hasOnsetSymptoms) {
					offsetDate.clear();
					cmCb.clear();
				} else if (cmCb.getValue() == null) {
					cmCb.setValue(ClinicalManifestation.SIGN_OF_SEVERITY);
				}

				// if the disease is Mumps and  the symptom is in the listed reporting one, then it should display as label.
				boolean isMumpsReportedSymptom = Disease.MUMPS == disease && MUMPS_REPORTING_SYMPTOMS.contains(sourceField.getId());
				if (isMumpsReportedSymptom) {
					cmCb.setVisible(false);
					toggleSymptomsInClinicalManifestationLayout(sourceField);
				} else {
					cmCb.setVisible(hasOnsetSymptoms);
				}
			});
		}
		onsetSymptom.setEnabled(false); // will be updated by listener if needed

		// make onsetDate editable for diseases that have no symptoms (a.k. no first symptom)
		setOnsetDateEnabled(onsetDateField, !onsetSymptom.isVisible());
	}

	/**
	 * Enables or disables the onset date. For Luxembourg Tuberculosis the onset date can only be edited while "Date of onset
	 * known" is Yes, so it stays disabled there whatever the symptom listeners decide.
	 * <p>
	 * Example: Luxembourg TB has no first symptom, so a symptom listener asks to enable the onset date when the form is loaded;
	 * while "Date of onset known" is empty or No, it stays disabled.
	 */
	private void setOnsetDateEnabled(DateField onsetDateField, boolean enabled) {
		if (isLux && disease == Disease.TUBERCULOSIS) {
			Field<?> dateOfOnsetKnown = getField(DATE_OF_ONSET_KNOWN);
			enabled = enabled && dateOfOnsetKnown != null && YesNoUnknown.YES.equals(dateOfOnsetKnown.getValue());
		}
		onsetDateField.setEnabled(enabled);
	}

	/**
	 * toggling the symptoms in clinical manifestation section.
	 * 
	 * @param symptomField
	 */
	private void toggleSymptomsInClinicalManifestationLayout(Field<?> symptomField) {

		Object sourceFieldObj = FieldHelper.getNullableSourceFieldValue(symptomField);

		if (FieldHelper.getNullableSourceFieldValue(symptomField) == SymptomState.YES) {
			// Only add if not already present
			if (!symptomLabelMap.containsKey(symptomField)) {
				Label label = new Label(symptomField.getCaption());
				label.addStyleNames(CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
				symptomLabelMap.put(symptomField, label);
				clinicalManifestationLayout.addComponent(label);
			}
		} else if (sourceFieldObj instanceof String && StringUtils.isNotBlank((String) sourceFieldObj)) {
			Label label = symptomLabelMap.get(symptomField);
			if (label == null) {
				label = new Label((String) sourceFieldObj);
				label.addStyleNames(CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
				symptomLabelMap.put(symptomField, label);
			} else {
				label.setValue((String) sourceFieldObj);
			}
			clinicalManifestationLayout.addComponent(label);
		} else {
			Label label = symptomLabelMap.get(symptomField);
			if (label != null) {
				clinicalManifestationLayout.removeComponent(label);
				symptomLabelMap.remove(symptomField);
			} else {
				// nothing needs to be done, the symptom is not in the clinical manifestation layout.
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void setUpSyphilisVisibilities() {

		boolean isCongenital = isCongenitalSyphilis();

		List<String> fieldIdsToHide = isCongenital ? SYPHILIS_ACQUIRED_ONLY_FIELD_IDS : SYPHILIS_CONGENITAL_ONLY_FIELD_IDS;
		fieldIdsToHide.forEach(fieldId -> {
			if (getFieldGroup().getField(fieldId) == null) {
				// defensive - the field is not part of the form (e.g. a commented out REVIEW field)
				return;
			}
			setVisible(false, fieldId);
			// hide the dependents too, otherwise they are laid out as standalone fields because their parent is hidden (e.g. weightLossAmount)
			List<String> dependentIds = DependantOn.DependencyConfiguration.getChildren(SymptomsDto.class, fieldId);
			if (dependentIds != null) {
				dependentIds.stream()
					.filter(dependentId -> getFieldGroup().getField(dependentId) != null)
					.forEach(dependentId -> setVisible(false, dependentId));
			}
		});

		ComboBox stageField = (ComboBox) getFieldGroup().getField(SYPHILIS_STAGE);
		Field<Object> infectiousnessField = (Field<Object>) getFieldGroup().getField(SYPHILIS_INFECTIOUSNESS);
		Field<?> infectionSiteField = getFieldGroup().getField(SYPHILIS_INFECTION_SITE);

		if (isCongenital) {
			// stageHeadingLabel.setVisible(false);
			stageField.setVisible(false);
			infectiousnessField.setVisible(false);
			infectionSiteField.setVisible(false);
		} else {

			stageField.addValueChangeListener(e -> {
				if (infectiousnessField.getValue() == null) {
					SyphilisInfectiousness infectiousness = mapSyphilisStageToInfectiousness((SyphilisStage) stageField.getValue());
					if (infectiousness != null) {
						infectiousnessField.setValue(infectiousness);
					}
				}
			});

			SYPHILIS_STAGE_SYMPTOMS_DICTIONARY.forEach((syphilisStage, fields) -> fields.forEach(fieldId -> {
				Field<?> stageSymptomField = getFieldGroup().getField(fieldId);
				if (stageSymptomField == null) {
					// defensive - the field is not part of the form (e.g. a commented out REVIEW field)
					return;
				}
				stageSymptomField.addValueChangeListener(e -> {
					if (stageField.getValue() == null && FieldHelper.getNullableSourceFieldValue((Field) e.getProperty()) == SymptomState.YES) {
						stageField.setValue(syphilisStage);
					}
				});
			}));

		}

		// symptomGroupVisibility();
	}

	private SyphilisInfectiousness mapSyphilisStageToInfectiousness(SyphilisStage stage) {
		if (stage == null) {
			return null;
		}
		switch (stage) {
		case PRIMARY_SYPHILIS:
		case SECONDARY_SYPHILIS:
		case EARLY_LATENT_SYPHILIS:
			return SyphilisInfectiousness.INFECTIOUS;
		case LATE_LATENT_SYPHILIS:
		case TERTIARY_SYPHILIS:
		case NEUROLOGICAL_SYPHILIS:
			return SyphilisInfectiousness.NOT_INFECTIOUS;
		case UNKNOWN:
			return SyphilisInfectiousness.UNKNOWN;
		default:
			return null;
		}
	}

	private boolean isCongenitalSyphilis() {
		return disease == Disease.SYPHILIS && syphilisPresentation == SyphilisPresentation.CONGENITAL;
	}

	private void setUpMonkeypoxVisibilities() {

		// The picture resemblance fields are @DependantOn(LESIONS); each picture renders in the same row as its field
		for (int i = 0; i < monkeypoxImageFieldIds.size(); i++) {
			Image lesionsImage = new Image(null, new ThemeResource("img/monkeypox-lesions-" + (i + 1) + ".png"));
			CssStyles.style(lesionsImage, VSPACE_3);
			dependentCompanions.put(monkeypoxImageFieldIds.get(i), lesionsImage);
		}
	}

	public Button createButtonSetClearedToSymptomState(String caption, SymptomState symptomState) {

		Button button = ButtonHelper.createButton(caption, event -> {
			Stream.of(unconditionalSymptomFieldIds, conditionalBleedingSymptomFieldIds, lesionsFieldIds, monkeypoxImageFieldIds)
				.flatMap(List::stream)
				.forEach(symptomId -> setClearedSymptomToState(symptomId, symptomState));
			// Clear YesNoUnknown fields - map SymptomState to YesNoUnknown
			YesNoUnknown yesNoValue;
			switch (symptomState) {
			case YES:
				yesNoValue = YesNoUnknown.YES;
				break;
			case NO:
				yesNoValue = YesNoUnknown.NO;
				break;
			case UNKNOWN:
			default:
				yesNoValue = YesNoUnknown.UNKNOWN;
				break;
			}
			YES_NO_UNKNOWN_SYMPTOM_FIELD_IDS.forEach(symptomId -> {
				final Field<Object> field = (Field<Object>) getFieldGroup().getField(symptomId);
				// quit early if the field is not supposed to be set
				if (field == null) {
					return;
				}
				if (!field.isVisible()) {
					return;
				}
				if (field.isReadOnly()) {
					return;
				}

				final Object value = field.getValue();
				final Set<?> valueSet = value instanceof Set ? (Set<?>) value : null;

				// non empty value within a set
				if (valueSet != null && !valueSet.isEmpty()) {
					return;
				}

				// non empty raw value
				if (valueSet == null && value != null) {
					return;
				}

				// if we have a set as value, add the new value
				if (valueSet != null) {
					safeSetFieldValue(field, Collections.singleton(yesNoValue));
					return;
				}

				// we have a raw value, set it
				safeSetFieldValue(field, yesNoValue);

			});
		}, ValoTheme.BUTTON_LINK);

		return button;
	}

	/**
	 * Sets the given state on a symptom state field that is visible, editable, offers the state and has no value yet.
	 *
	 * @param symptomId
	 *            the property id of the symptom field
	 * @param symptomState
	 *            the state to set
	 */
	@SuppressWarnings("unchecked")
	private void setClearedSymptomToState(String symptomId, SymptomState symptomState) {
		final Field<Object> field = (Field<Object>) getFieldGroup().getField(symptomId);
		// the field is not part of the form for this disease, or must not be changed
		if (field == null || !field.isVisible() || field.isReadOnly()) {
			return;
		}
		// only symptom state selections can take the state; text, date and other fields in the lists are skipped
		if (!(field instanceof AbstractSelect) || !((AbstractSelect) field).containsId(symptomState)) {
			return;
		}

		final Object value = field.getValue();
		if (value == null) {
			safeSetFieldValue(field, symptomState);
		} else if (value instanceof Set && ((Set<?>) value).isEmpty()) {
			safeSetFieldValue(field, new HashSet<>(Collections.singleton(symptomState)));
		}
	}

	public void setCaseSymptomSideViewComponent(CaseSymptomSideViewComponent caseSymptomSideViewComponent) {
		this.caseSymptomSideViewComponent = caseSymptomSideViewComponent;
	}

}
