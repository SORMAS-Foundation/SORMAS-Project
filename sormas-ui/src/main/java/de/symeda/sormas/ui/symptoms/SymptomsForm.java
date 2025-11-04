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
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locsCss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
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
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Descriptions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.ClinicalPresentationStatus;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.DiagnosisType;
import de.symeda.sormas.api.symptoms.InfectionSite;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.utils.DateComparator;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.SymptomGrouping;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.hospitalization.HospitalizationView;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class SymptomsForm extends AbstractEditForm<SymptomsDto> {

	private static final long serialVersionUID = 1L;

	private static final String LOCALISATION_HEADING_LOC = "localisationHeadingLoc";
	private static final String CLINICAL_MEASUREMENTS_HEADING_LOC = "clinicalMeasurementsHeadingLoc";
	private static final String SIGNS_AND_SYMPTOMS_HEADING_LOC = "signsAndSymptomsHeadingLoc";
	private static final String GENERAL_SIGNS_AND_SYMPTOMS_HEADING_LOC = "generalSignsAndSymptomsHeadingLoc";
	private static final String RESPIRATORY_SIGNS_AND_SYMPTOMS_HEADING_LOC = "respiratorySignsAndSymptomsHeadingLoc";
	private static final String CARDIOVASCULAR_SIGNS_AND_SYMPTOMS_HEADING_LOC = "cardiovascularSignsAndSymptomsHeadingLoc";
	private static final String GASTROINTESTINAL_SIGNS_AND_SYMPTOMS_HEADING_LOC = "gastrointestinalSignsAndSymptomsHeadingLoc";
	private static final String URINARY_SIGNS_AND_SYMPTOMS_HEADING_LOC = "urinarySignsAndSymptomsHeadingLoc";
	private static final String NERVOUS_SYSTEM_SIGNS_AND_SYMPTOMS_HEADING_LOC = "nervousSystemSignsAndSymptomsHeadingLoc";
	private static final String SKIN_SIGNS_AND_SYMPTOMS_HEADING_LOC = "skinSignsAndSymptomsHeadingLoc";
	private static final String OTHER_SIGNS_AND_SYMPTOMS_HEADING_LOC = "otherSignsAndSymptomsHeadingLoc";
	private static final String BUTTONS_LOC = "buttonsLoc";
	private static final String LESIONS_LOCATIONS_LOC = "lesionsLocationsLoc";
	private static final String MONKEYPOX_LESIONS_IMG1 = "monkeypoxLesionsImg1";
	private static final String MONKEYPOX_LESIONS_IMG2 = "monkeypoxLesionsImg2";
	private static final String MONKEYPOX_LESIONS_IMG3 = "monkeypoxLesionsImg3";
	private static final String MONKEYPOX_LESIONS_IMG4 = "monkeypoxLesionsImg4";
	private static final String SYMPTOMS_HINT_LOC = "symptomsHintLoc";
	private static final String COMPLICATIONS_HEADING = "complicationsHeading";
	private static final String CLINICAL_PRESENTATION_HEADING = "clinicalPresentationHeading";
	private static final String TUBERCULOSIS_ONSET_DATE_LOC = "tuberculosisOnsetDateLoc";
	private static final String TUBERCULOSIS_CLINICAL_PRESENTATION_DETAILS_LOC = "tuberculosisClinicalPresentationDetailsLoc";

	private static Map<String, List<String>> symptomGroupMap = new HashMap<>();
	public static final String SKIN_RASH_ONSET_DATE_LAYOUT = fluidRowLocs(6, "LBL_SKIN_RASH_ONSET_DATE", 1, "", 5, SKIN_RASH_ONSET_DATE);

	//@formatter:off
	private static final String HTML_LAYOUT =
			loc(LOCALISATION_HEADING_LOC) +
					fluidRowLocs(DIAGNOSIS, MAJOR_SITE, MINOR_SITE) +
					fluidRowLocs("", OTHER_MAJOR_SITE_DETAILS, OTHER_MINOR_SITE_DETAILS) +
					loc(CLINICAL_MEASUREMENTS_HEADING_LOC) +
					fluidRowLocs(TEMPERATURE, TEMPERATURE_SOURCE) +
					fluidRowLocs(BLOOD_PRESSURE_SYSTOLIC, BLOOD_PRESSURE_DIASTOLIC, HEART_RATE, RESPIRATORY_RATE) +
					fluidRowLocs(GLASGOW_COMA_SCALE, WEIGHT, HEIGHT, MID_UPPER_ARM_CIRCUMFERENCE) +
					loc(SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					fluidRowCss(VSPACE_3,
							//XXX #1620 fluidColumnLoc?
							fluidColumn(8, 0, loc(SYMPTOMS_HINT_LOC))) +
					fluidRow(fluidColumn(8,4, locCss(CssStyles.ALIGN_RIGHT,BUTTONS_LOC)))+
                    loc(CLINICAL_PRESENTATION_HEADING)+
					fluidRow(fluidColumn(6, 0, locsCss(VSPACE_3, ASYMPTOMATIC)))+
                    fluidRowLocs(DATE_OF_ONSET_KNOWN, TUBERCULOSIS_ONSET_DATE_LOC, "") +
                    fluidRowLocs(CLINICAL_PRESENTATION_STATUS, TUBERCULOSIS_CLINICAL_PRESENTATION_DETAILS_LOC) +
                    fluidRow(
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3,
                                            HEMORRHAGIC_RASH, ARTHRITIS, MENINGITIS, MENINGEAL_SIGNS, SEPTICAEMIA, ACUTE_ENCEPHALITIS, UNKNOWN_SYMPTOM)),
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3, SHOCK, PNEUMONIA_CLINICAL_OR_RADIOLOGIC)))+
					createSymptomGroupLayout(SymptomGroup.GENERAL, GENERAL_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.RESPIRATORY, RESPIRATORY_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.CARDIOVASCULAR, CARDIOVASCULAR_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.GASTROINTESTINAL, GASTROINTESTINAL_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.URINARY, URINARY_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.NERVOUS_SYSTEM, NERVOUS_SYSTEM_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.SKIN, SKIN_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					createSymptomGroupLayout(SymptomGroup.OTHER, OTHER_SIGNS_AND_SYMPTOMS_HEADING_LOC) +
					fluidRowLocsCss(VSPACE_3, SYMPTOM_CURRENT_STATUS, DURATION_OF_SYMPTOMS)+
					fluidRow(fluidColumn(6, 0, loc("LAYOUT_SKIN_RASH_ONSET_DATE"))) +
					
					loc(COMPLICATIONS_HEADING) +
					fluidRow(
							fluidColumn(6, 0,
									locsCss(VSPACE_3, ALTERED_CONSCIOUSNESS, CONFUSED_DISORIENTED, HEMORRHAGIC_SYNDROME, HYPERGLYCEMIA, HYPOGLYCEMIA, OVERNIGHT_STAY_REQUIRED)),
							fluidColumn(6, 0,
									locsCss(VSPACE_3, MENINGEAL_SIGNS, SEIZURES, SEPSIS, SHOCK,REOCCURRENCE, OTHER_COMPLICATIONS, OTHER_COMPLICATIONS_TEXT)))+
					fluidRowLocs(PARENT_TIME_OFF_WORK, TIME_OFF_WORK_DAYS) +
					locsCss(VSPACE_3, PATIENT_ILL_LOCATION, SYMPTOMS_COMMENTS) +
					fluidRowLocsCss(VSPACE_3, ONSET_SYMPTOM, ONSET_DATE);
	//@formatter:on

	private static String createSymptomGroupLayout(SymptomGroup symptomGroup, String loc) {

		final Predicate<java.lang.reflect.Field> groupSymptoms =
			field -> field.isAnnotationPresent(SymptomGrouping.class) && field.getAnnotation(SymptomGrouping.class).value() == symptomGroup;
		final List<String> symptomLocations = Arrays.stream(SymptomsDto.class.getDeclaredFields())
			.filter(groupSymptoms)
			.map(field -> field.getName())
			.sorted(Comparator.comparing(fieldName -> I18nProperties.getPrefixCaption(I18N_PREFIX, fieldName)))
			.collect(Collectors.toList());

		if (symptomGroup == SymptomGroup.SKIN) {
			symptomLocations.add(symptomLocations.indexOf(LESIONS_RESEMBLE_IMG1) + 1, MONKEYPOX_LESIONS_IMG1);
			symptomLocations.add(symptomLocations.indexOf(LESIONS_RESEMBLE_IMG2) + 1, MONKEYPOX_LESIONS_IMG2);
			symptomLocations.add(symptomLocations.indexOf(LESIONS_RESEMBLE_IMG3) + 1, MONKEYPOX_LESIONS_IMG3);
			symptomLocations.add(symptomLocations.indexOf(LESIONS_RESEMBLE_IMG4) + 1, MONKEYPOX_LESIONS_IMG4);
		}

		symptomGroupMap.put(loc, symptomLocations);

		return loc(loc)
			+ fluidRow(
				fluidColumn(6, -1, locsCss(VSPACE_3, new ArrayList<>(symptomLocations.subList(0, symptomLocations.size() / 2)))),
				fluidColumn(
					6,
					0,
					locsCss(VSPACE_3, new ArrayList<>(symptomLocations.subList(symptomLocations.size() / 2, symptomLocations.size())))));
	}

	private final CaseDataDto caze;
	private final Disease disease;
	private final PersonDto person;
	private final SymptomsContext symptomsContext;
	private final ViewMode viewMode;
	private transient List<String> unconditionalSymptomFieldIds;
	private List<String> conditionalBleedingSymptomFieldIds;
	private List<String> lesionsFieldIds;
	private List<String> lesionsLocationFieldIds;
	private List<String> monkeypoxImageFieldIds;
	private boolean isListenerAction = false;

	public SymptomsForm(
		CaseDataDto caze,
		Disease disease,
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
		this.person = person;
		this.symptomsContext = symptomsContext;
		this.viewMode = viewMode;
		if (disease == null || symptomsContext == null) {
			throw new IllegalArgumentException("disease and symptoms context cannot be null");
		}
		if (symptomsContext == SymptomsContext.CASE && caze == null) {
			throw new IllegalArgumentException("case cannot be null when symptoms context is case");
		}
		addFields();
		hideValidationUntilNextCommit();
	}

	@Override
	protected void addFields() {
		if (disease == null || symptomsContext == null) {
			// workaround to stop initialization until disease is set
			return;
		}

		// Add fields
		Label clinicalMeasurementsHeadingLabel =
			createLabel(I18nProperties.getString(Strings.headingClinicalMeasurements), H3, CLINICAL_MEASUREMENTS_HEADING_LOC);

		Label signsAndSymptomsHeadingLabel =
			createLabel(I18nProperties.getString(Strings.headingSignsAndSymptoms), H3, SIGNS_AND_SYMPTOMS_HEADING_LOC);

		final Label generalSymptomsHeadingLabel = createLabel(SymptomGroup.GENERAL.toString(), H3, GENERAL_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label respiratorySymptomsHeadingLabel =
			createLabel(SymptomGroup.RESPIRATORY.toString(), H3, RESPIRATORY_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label cardiovascularSymptomsHeadingLabel =
			createLabel(SymptomGroup.CARDIOVASCULAR.toString(), H3, CARDIOVASCULAR_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label gastrointestinalSymptomsHeadingLabel =
			createLabel(SymptomGroup.GASTROINTESTINAL.toString(), H3, GASTROINTESTINAL_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label urinarySymptomsHeadingLabel = createLabel(SymptomGroup.URINARY.toString(), H3, URINARY_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label nervousSystemSymptomsHeadingLabel =
			createLabel(SymptomGroup.NERVOUS_SYSTEM.toString(), H3, NERVOUS_SYSTEM_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label skinSymptomsHeadingLabel = createLabel(SymptomGroup.SKIN.toString(), H3, SKIN_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		final Label otherSymptomsHeadingLabel = createLabel(SymptomGroup.OTHER.toString(), H3, OTHER_SIGNS_AND_SYMPTOMS_HEADING_LOC);
		Label clinicalPresentationHeadingLabel =
			createLabel(I18nProperties.getString(Strings.headingClinicalPresentation), H3, CLINICAL_PRESENTATION_HEADING);

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

		addFields(
			VOMITING,
			DIARRHEA,
			BLOOD_IN_STOOL,
			NAUSEA,
			ABDOMINAL_PAIN,
			HEADACHE,
			MUSCLE_PAIN,
			FATIGUE_WEAKNESS,
			SKIN_RASH,
			NECK_STIFFNESS,
			SORE_THROAT,
			COUGH,
			COUGH_WITH_SPUTUM,
			COUGH_WITH_HEAMOPTYSIS,
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
			JOINT_PAIN,
			HICCUPS,
			BACKACHE,
			EYES_BLEEDING,
			JAUNDICE,
			DARK_URINE,
			STOMACH_BLEEDING,
			RAPID_BREATHING,
			SWOLLEN_GLANDS,
			UNEXPLAINED_BLEEDING,
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
			PALPABLE_LIVER,
			PALPABLE_SPLEEN,
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
			CONVULSION,
			FLUID_IN_LUNG_CAVITY_AUSCULTATION,
			FLUID_IN_LUNG_CAVITY_XRAY,
			ABNORMAL_LUNG_XRAY_FINDINGS,
			CONJUNCTIVAL_INJECTION,
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
			FEELING_ILL,
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
			RESPIRATORY_FATIGUE);

		addField(SYMPTOMS_COMMENTS, TextField.class).setDescription(
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
			ACUTE_ENCEPHALITIS,
			OTHER_CLINICAL_PRESENTATION,
			OTHER_CLINICAL_PRESENTATION_TEXT };
		addFields(clinicalPresentationFieldIds);

		NullableOptionGroup asymptomaticNOG = addField(ASYMPTOMATIC);

		asymptomaticNOG.addValueChangeListener(e -> {
			boolean isSymptamatic = !SymptomState.YES.equals(asymptomaticNOG.getNullableValue());
			editableAllowedFields().stream().filter(field -> !field.getId().equals(ASYMPTOMATIC)).forEach(field -> {
				if (!isSymptamatic) {
					field.clear();
				}
				field.setEnabled(isSymptamatic);
				onsetSymptom.setEnabled(isSymptamatic);
				onsetDateField.setEnabled(isSymptamatic);
			});
		});

		monkeypoxImageFieldIds = Arrays.asList(LESIONS_RESEMBLE_IMG1, LESIONS_RESEMBLE_IMG2, LESIONS_RESEMBLE_IMG3, LESIONS_RESEMBLE_IMG4);
		for (String propertyId : monkeypoxImageFieldIds) {
			@SuppressWarnings("rawtypes")
			Field monkeypoxImageField = addField(propertyId);
			CssStyles.style(monkeypoxImageField, VSPACE_NONE);
		}

		CustomLayout skinRashDateLayout = new CustomLayout();
		skinRashDateLayout.setTemplateContents(SKIN_RASH_ONSET_DATE_LAYOUT);
		skinRashDateLayout.setStyleName("compliance-padding");
		skinRashDateLayout.setVisible(true);

		Label skinRashDateLabel = new Label(I18nProperties.getCaption(Captions.Symptoms_skinRashOnsetDate));
		skinRashDateLabel.setVisible(false);
		skinRashDateLayout.addComponent(skinRashDateLabel, "LBL_SKIN_RASH_ONSET_DATE");

		skinRashDateLabel.setVisible(
			isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG)
				&& FieldHelper.getNullableSourceFieldValue(getField(SKIN_RASH)) == YesNoUnknown.YES);
		DateField skinRashOnsetDate = addField(skinRashDateLayout, SKIN_RASH_ONSET_DATE, DateField.class);
		skinRashOnsetDate.setId(SKIN_RASH_ONSET_DATE);
		skinRashOnsetDate.addStyleNames(ValoTheme.DATEFIELD_BORDERLESS, CssStyles.VIEW_SECTION_WIDTH_AUTO, VSPACE_3);
		skinRashOnsetDate.setCaption(null);
		skinRashDateLayout.addComponent(skinRashOnsetDate, SKIN_RASH_ONSET_DATE);
		getContent().addComponent(skinRashDateLayout, "LAYOUT_SKIN_RASH_ONSET_DATE");

		getField(SKIN_RASH).addValueChangeListener(e -> {
			// Show skin rash onset date field only if skin rash is set to YES
			if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
				Object v = FieldHelper.getNullableSourceFieldValue((Field) e.getProperty());
				boolean isVisible = v == SymptomState.YES || (v instanceof java.util.Set && ((java.util.Set<?>) v).contains(SymptomState.YES));
				skinRashDateLabel.setVisible(isVisible);
			}
		});

		Field<?> parentTimeOffWorkField = addField(PARENT_TIME_OFF_WORK);
		TextField timeOffWorkDaysField = addField(TIME_OFF_WORK_DAYS, TextField.class);
		timeOffWorkDaysField.setConverter(new StringToFloatConverter());
		timeOffWorkDaysField
			.setConversionError(I18nProperties.getValidationError(Validations.onlyDecimalNumbersAllowed, timeOffWorkDaysField.getCaption()));
		Field<?> overNightStayRequiredField = addField(OVERNIGHT_STAY_REQUIRED);
		addField(DURATION_OF_SYMPTOMS, TextField.class);

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

		// Hide clinical measurements heading if no clinical measurements are visible
		clinicalMeasurementsHeadingLabel.setVisible(
			Set.of(TEMPERATURE_SOURCE, BLOOD_PRESSURE_SYSTOLIC, BLOOD_PRESSURE_DIASTOLIC, HEART_RATE, RESPIRATORY_RATE, WEIGHT, GLASGOW_COMA_SCALE)
				.stream()
				.anyMatch(e -> getFieldGroup().getField(e).isVisible()));

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
			CONJUNCTIVAL_INJECTION,
			ACUTE_RESPIRATORY_DISTRESS_SYNDROME,
			PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
			VOMITING,
			DIARRHEA,
			BLOOD_IN_STOOL,
			NAUSEA,
			ABDOMINAL_PAIN,
			HEADACHE,
			MUSCLE_PAIN,
			FATIGUE_WEAKNESS,
			SKIN_RASH,
			NECK_STIFFNESS,
			SORE_THROAT,
			COUGH,
			COUGH_WITH_SPUTUM,
			COUGH_WITH_HEAMOPTYSIS,
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
			JOINT_PAIN,
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
			PALPABLE_LIVER,
			PALPABLE_SPLEEN,
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
			PURPURIC_RASH,
			PIGMENTARY_RETINOPATHY,
			CONVULSION,
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
			FEELING_ILL,
			SHIVERING,
			RESPIRATORY_DISEASE_VENTILATION,
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
			ACUTE_ENCEPHALITIS,
			OTHER_CLINICAL_PRESENTATION);

		// Set visibilities

		NullableOptionGroup feverField = (NullableOptionGroup) getFieldGroup().getField(FEVER);
		feverField.setImmediate(true);

		FieldHelper.setVisibleWhen(getFieldGroup(), conditionalBleedingSymptomFieldIds, UNEXPLAINED_BLEEDING, Arrays.asList(SymptomState.YES), true);

		FieldHelper
			.setVisibleWhen(getFieldGroup(), OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, OTHER_HEMORRHAGIC_SYMPTOMS, Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT,
			OTHER_NON_HEMORRHAGIC_SYMPTOMS,
			Arrays.asList(SymptomState.YES),
			true);

		FieldHelper.setVisibleWhen(getFieldGroup(), OTHER_COMPLICATIONS_TEXT, OTHER_COMPLICATIONS, Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), lesionsFieldIds, LESIONS, Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), lesionsLocationFieldIds, LESIONS, Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), LESIONS_ONSET_DATE, LESIONS, Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), CONGENITAL_HEART_DISEASE_TYPE, CONGENITAL_HEART_DISEASE, Arrays.asList(SymptomState.YES), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			CONGENITAL_HEART_DISEASE_DETAILS,
			CONGENITAL_HEART_DISEASE_TYPE,
			Arrays.asList(CongenitalHeartDiseaseType.OTHER),
			true);

		if (disease != Disease.TUBERCULOSIS) {
			FieldHelper.setVisibleWhen(
				getFieldGroup(),
				OTHER_CLINICAL_PRESENTATION_TEXT,
				OTHER_CLINICAL_PRESENTATION,
				Arrays.asList(SymptomState.YES),
				true);
		}

		if (isVisibleAllowed(getFieldGroup().getField(JAUNDICE_WITHIN_24_HOURS_OF_BIRTH))) {
			FieldHelper.setVisibleWhen(getFieldGroup(), JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, JAUNDICE, Arrays.asList(SymptomState.YES), true);
		}

		FieldHelper.addSoftRequiredStyle(getField(LESIONS_ONSET_DATE));

		boolean isInfant = person != null
			&& person.getApproximateAge() != null
			&& ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS) || person.getApproximateAge() <= 1);
		if (!isInfant) {
			getFieldGroup().getField(BULGING_FONTANELLE).setVisible(false);
		}

		// Handle visibility of lesions locations caption
		Label lesionsLocationsCaption = new Label(I18nProperties.getCaption(Captions.symptomsLesionsLocations));
		CssStyles.style(lesionsLocationsCaption, VSPACE_3);
		getContent().addComponent(lesionsLocationsCaption, LESIONS_LOCATIONS_LOC);
		getContent().getComponent(LESIONS_LOCATIONS_LOC)
			.setVisible(FieldHelper.getNullableSourceFieldValue(getFieldGroup().getField(LESIONS)) == SymptomState.YES);
		getFieldGroup().getField(LESIONS).addValueChangeListener(e -> {
			getContent().getComponent(LESIONS_LOCATIONS_LOC)
				.setVisible(FieldHelper.getNullableSourceFieldValue((Field) e.getProperty()) == SymptomState.YES);
		});

		// Symptoms hint text
		Label symptomsHint = new Label(
			I18nProperties.getString(symptomsContext == SymptomsContext.CASE ? Strings.messageSymptomsHint : Strings.messageSymptomsVisitHint),
			ContentMode.HTML);
		symptomsHint.setWidth(100, Unit.PERCENTAGE);
		getContent().addComponent(symptomsHint, SYMPTOMS_HINT_LOC);

		if (disease == Disease.MONKEYPOX) {
			setUpMonkeypoxVisibilities();
		}

		if (symptomsContext != SymptomsContext.CASE) {
			getFieldGroup().getField(PATIENT_ILL_LOCATION).setVisible(false);
		}

		symptomGroupMap.forEach((location, strings) -> {
			final Component groupLabel = getContent().getComponent(location);
			final Optional<String> groupHasVisibleSymptom =
				strings.stream().filter(s -> getFieldGroup().getField(s) != null && getFieldGroup().getField(s).isVisible()).findAny();
			if (!groupHasVisibleSymptom.isPresent()) {
				groupLabel.setVisible(false);
			}
		});

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

		Button clearAllButton = ButtonHelper.createButton(Captions.actionClearAll, event -> {
			for (Object symptomId : unconditionalSymptomFieldIds) {
				getFieldGroup().getField(symptomId).setValue(null);
			}
			for (Object symptomId : conditionalBleedingSymptomFieldIds) {
				getFieldGroup().getField(symptomId).setValue(null);
			}
			for (Object symptomId : lesionsFieldIds) {
				getFieldGroup().getField(symptomId).setValue(null);
			}
			for (Object symptomId : lesionsLocationFieldIds) {
				getFieldGroup().getField(symptomId).setValue(null);
			}
			for (Object symptomId : monkeypoxImageFieldIds) {
				getFieldGroup().getField(symptomId).setValue(null);
			}
		}, ValoTheme.BUTTON_LINK);

		Button setEmptyToNoButton = createButtonSetClearedToSymptomState(Captions.symptomsSetClearedToNo, SymptomState.NO);

		Button setEmptyToUnknownButton = createButtonSetClearedToSymptomState(Captions.symptomsSetClearedToUnknown, SymptomState.UNKNOWN);

		Label complicationsHeading = new Label(I18nProperties.getString(Strings.headingComplications));
		CssStyles.style(complicationsHeading, CssStyles.H3);
		getContent().addComponent(complicationsHeading, COMPLICATIONS_HEADING);

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.addComponent(clearAllButton);
		buttonsLayout.addComponent(setEmptyToNoButton);
		buttonsLayout.addComponent(setEmptyToUnknownButton);
		buttonsLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		buttonsLayout.setMargin(new MarginInfo(true, false, true, true));

		getContent().addComponent(buttonsLayout, BUTTONS_LOC);

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

		// checking the disease is invasive bacterial disease(IMI/IPI)
		boolean lablesVisible = false;

		if (caze != null) {
			lablesVisible = DiseaseHelper.checkDiseaseIsInvasiveBacterialDiseases(caze.getDisease()) || disease == Disease.PERTUSSIS;
		}

		clinicalMeasurementsHeadingLabel.setVisible(!lablesVisible);
		signsAndSymptomsHeadingLabel.setVisible(!lablesVisible);
		complicationsHeading.setVisible(!lablesVisible && isComplicationsHeadingVisible);

		if (Disease.INVASIVE_MENINGOCOCCAL_INFECTION == disease) {
			getField(SHOCK).setCaption(I18nProperties.getCaption(Captions.Symptoms_imi_shock));
			getField(PNEUMONIA_CLINICAL_OR_RADIOLOGIC).setCaption(I18nProperties.getCaption(Captions.Symptoms_imi_pneumoniaClinicalOrRadiologic));
		} else if (Disease.INVASIVE_PNEUMOCOCCAL_INFECTION == disease) {
			getField(PNEUMONIA_CLINICAL_OR_RADIOLOGIC).setCaption(I18nProperties.getCaption(Captions.Symptoms_ipi_pneumoniaClinicalOrRadiologic));
		}

		if (symptomsContext == SymptomsContext.CASE && caze != null && caze.getDisease() == Disease.PLAGUE) {
			getField(CHEST_PAIN).setVisible(PlagueType.PNEUMONIC == caze.getPlagueType());
		}

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG)) {
			FieldHelper.setVisibleWhen(getFieldGroup(), SKIN_RASH_ONSET_DATE, SKIN_RASH, Arrays.asList(SymptomState.YES), true);
		}
		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG) && disease == Disease.TUBERCULOSIS) {
			Label localisationHeadingLabel = createLabel(I18nProperties.getString(Strings.headingLocalisation), H3, LOCALISATION_HEADING_LOC);

			clinicalPresentationHeadingLabel.setVisible(true);
			clinicalMeasurementsHeadingLabel.setVisible(false);
			signsAndSymptomsHeadingLabel.setVisible(false);
			respiratorySymptomsHeadingLabel.setVisible(false);
			symptomsHint.setVisible(false);
			clearAllButton.setVisible(false);
			setEmptyToNoButton.setVisible(false);
			setEmptyToUnknownButton.setVisible(false);
			onsetSymptom.setVisible(false);

			ComboBox diagnosisField = addField(DIAGNOSIS, ComboBox.class);

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

			FieldHelper.setVisibleWhen(getFieldGroup(), OTHER_MAJOR_SITE_DETAILS, MAJOR_SITE, Arrays.asList(InfectionSite.OTHER), true);
			FieldHelper.setVisibleWhen(getFieldGroup(), OTHER_MINOR_SITE_DETAILS, MINOR_SITE, Arrays.asList(InfectionSite.OTHER), true);

			FieldHelper.setVisibleWhen(getFieldGroup(), MAJOR_SITE, DIAGNOSIS, Arrays.asList(DiagnosisType.EXTRAPULMONARY), true);

			addField(DATE_OF_ONSET_KNOWN, OptionGroup.class);

			FieldHelper.setEnabledWhen(getFieldGroup(), DATE_OF_ONSET_KNOWN, YesNoUnknown.YES, ONSET_DATE, true);

			ComboBox clinicalPresentationStatusField = addField(CLINICAL_PRESENTATION_STATUS, ComboBox.class);
			clinicalPresentationStatusField.setItemCaptionMode(AbstractSelect.ItemCaptionMode.EXPLICIT_DEFAULTS_ID);
			clinicalPresentationStatusField
				.setItemCaption(ClinicalPresentationStatus.COMPATIBLE, ClinicalPresentationStatus.COMPATIBLE.buildCaption(disease.toShortString()));
			getFieldGroup().getField(OTHER_CLINICAL_PRESENTATION_TEXT).setVisible(true);
		}

		parentTimeOffWorkField.addValueChangeListener(e -> {
			if (!parentTimeOffWorkField.isVisible()) {
				return;
			}
			final boolean isParentTimeOffWorkYes = YesNoUnknown.YES.equals(FieldHelper.getNullableSourceFieldValue(parentTimeOffWorkField));
			timeOffWorkDaysField.setVisible(isParentTimeOffWorkYes);
			timeOffWorkDaysField.setValue(isParentTimeOffWorkYes ? timeOffWorkDaysField.getValue() : null);
		});

		// Change captions for giardiasis and Cryptosporidiosis
		if (ImmutableList.of(Disease.GIARDIASIS, Disease.CRYPTOSPORIDIOSIS).contains(disease)) {
			parentTimeOffWorkField.setCaption(I18nProperties.getCaption(Captions.Symptoms_timeOffWorkOrSchool));
			timeOffWorkDaysField.setCaption(I18nProperties.getCaption(Captions.Symptoms_timeOffWorkDays_giardiasis));
			getField(OTHER_COMPLICATIONS).setCaption(I18nProperties.getCaption(Captions.Symptoms_otherComplications_CryptoGiardia));
			getField(OTHER_COMPLICATIONS_TEXT).setCaption(I18nProperties.getCaption(Captions.Symptoms_otherComplicationsText_CryptoGiardia));
			getContent().getComponent(CLINICAL_MEASUREMENTS_HEADING_LOC).setVisible(false);
		}

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

		FieldHelper.setVisibleWhen(getFieldGroup(), WEIGHT_LOSS_AMOUNT, WEIGHT_LOSS, Arrays.asList(SymptomState.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), DURATION_OF_SYMPTOMS, SYMPTOM_CURRENT_STATUS, Arrays.asList(SymptomState.YES), true);
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
		String emptyLoc = "location=''";
		String onsetDateLoc = "location='" + ONSET_DATE + "'";
		String tbOnsetDateLoc = "location='" + TUBERCULOSIS_ONSET_DATE_LOC + "'";
		String clinicalPresentationDetailsLoc = "location='" + OTHER_CLINICAL_PRESENTATION_TEXT + "'";
		String tbClinicalPresentationDetailsLoc = "location='" + TUBERCULOSIS_CLINICAL_PRESENTATION_DETAILS_LOC + "'";
		String vspace3Class = "class='vspace-3'";

		String FINAL_HTML_LAYOUT = HTML_LAYOUT;

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_LUXEMBOURG) && disease == Disease.TUBERCULOSIS) {
			FINAL_HTML_LAYOUT = FINAL_HTML_LAYOUT.replace(onsetDateLoc, emptyLoc)
				.replace(tbOnsetDateLoc, onsetDateLoc)
				.replace(clinicalPresentationDetailsLoc, emptyLoc)
				.replace(tbClinicalPresentationDetailsLoc, clinicalPresentationDetailsLoc)
				.replace(vspace3Class, "");
		}

		return FINAL_HTML_LAYOUT;
	}

	public void initializeSymptomRequirementsForVisit(NullableOptionGroup visitStatus) {
		FieldHelper.addSoftRequiredStyleWhen(
			getFieldGroup(),
			visitStatus,
			Arrays.asList(TEMPERATURE, TEMPERATURE_SOURCE),
			Arrays.asList(VisitStatus.COOPERATIVE),
			disease);
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

		if (visitStatusField != null) {
			if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) && visitStatusField.getNullableValue() == VisitStatus.COOPERATIVE) {
				FieldHelper.addSoftRequiredStyle(targetField);
			} else {
				FieldHelper.removeSoftRequiredStyle(targetField);
			}
		} else {
			if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues)) {
				FieldHelper.addSoftRequiredStyle(targetField);
			} else {
				FieldHelper.removeSoftRequiredStyle(targetField);
			}
		}

		// Add listeners
		for (Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			sourceField.addValueChangeListener(event -> {
				if (visitStatusField != null) {
					if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) && visitStatusField.getValue() == VisitStatus.COOPERATIVE) {
						FieldHelper.addSoftRequiredStyle(targetField);
					} else {
						FieldHelper.removeSoftRequiredStyle(targetField);
					}
				} else {
					if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues)) {
						FieldHelper.addSoftRequiredStyle(targetField);
					} else {
						FieldHelper.removeSoftRequiredStyle(targetField);
					}
				}
			});
		}

		if (visitStatusField != null) {
			visitStatusField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChange(com.vaadin.v7.data.Property.ValueChangeEvent event) {
					if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) && visitStatusField.getValue() == VisitStatus.COOPERATIVE) {
						FieldHelper.addSoftRequiredStyle(targetField);
					} else {
						FieldHelper.removeSoftRequiredStyle(targetField);
					}
				}
			});
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
			if (sourceValues.contains(FieldHelper.getNullableSourceFieldValue(sourceField))) {
				return true;
			}
		}

		return false;
	}

	public boolean isAnySymptomVisible(FieldGroup fieldGroup, List<String> sourcePropertyIds, List<Object> sourceValues) {
		return true;
	}

	@SuppressWarnings("rawtypes")
	private void addListenerForOnsetFields(ComboBox onsetSymptom, DateField onsetDateField) {
		List<String> allPropertyIds =
			Stream.concat(unconditionalSymptomFieldIds.stream(), conditionalBleedingSymptomFieldIds.stream()).collect(Collectors.toList());
		allPropertyIds.add(LESIONS_THAT_ITCH);

		for (Object sourcePropertyId : allPropertyIds) {
			Field sourceField = getFieldGroup().getField(sourcePropertyId);
			sourceField.addValueChangeListener(event -> {
				if (FieldHelper.getNullableSourceFieldValue(sourceField) == SymptomState.YES) {
					onsetSymptom.addItem(sourceField.getCaption());
					onsetDateField.setEnabled(true);
				} else {
					onsetSymptom.removeItem(sourceField.getCaption());
					final Date onsetDate = getValue().getOnsetDate();
					boolean isOnsetDateFieldEnabled = isAnySymptomSetToYes(getFieldGroup(), allPropertyIds, Arrays.asList(SymptomState.YES));
					onsetDateField.setEnabled(isOnsetDateFieldEnabled || !onsetSymptom.isVisible());
					if (onsetDate != null) {
						onsetDateField.setValue(onsetDate);
					} else if (!isOnsetDateFieldEnabled) {
						onsetDateField.setValue(null);
					}
				}
				onsetSymptom.setEnabled(!onsetSymptom.getItemIds().isEmpty());
			});
		}
		onsetSymptom.setEnabled(false); // will be updated by listener if needed

		// make onsetDate editable for diseases that have no symptoms (a.k. no first symptom)
		onsetDateField.setEnabled(!onsetSymptom.isVisible());
	}

	private void setUpMonkeypoxVisibilities() {
		// Monkeypox picture resemblance fields
		FieldHelper.setVisibleWhen(getFieldGroup(), monkeypoxImageFieldIds, LESIONS, Arrays.asList(SymptomState.YES), true);

		// Set up images
		Image lesionsImg1 = new Image(null, new ThemeResource("img/monkeypox-lesions-1.png"));
		CssStyles.style(lesionsImg1, VSPACE_3);
		Image lesionsImg2 = new Image(null, new ThemeResource("img/monkeypox-lesions-2.png"));
		CssStyles.style(lesionsImg2, VSPACE_3);
		Image lesionsImg3 = new Image(null, new ThemeResource("img/monkeypox-lesions-3.png"));
		CssStyles.style(lesionsImg3, VSPACE_3);
		Image lesionsImg4 = new Image(null, new ThemeResource("img/monkeypox-lesions-4.png"));
		CssStyles.style(lesionsImg4, VSPACE_3);
		getContent().addComponent(lesionsImg1, MONKEYPOX_LESIONS_IMG1);
		getContent().addComponent(lesionsImg2, MONKEYPOX_LESIONS_IMG2);
		getContent().addComponent(lesionsImg3, MONKEYPOX_LESIONS_IMG3);
		getContent().addComponent(lesionsImg4, MONKEYPOX_LESIONS_IMG4);

		List<String> monkeypoxImages = Arrays.asList(MONKEYPOX_LESIONS_IMG1, MONKEYPOX_LESIONS_IMG2, MONKEYPOX_LESIONS_IMG3, MONKEYPOX_LESIONS_IMG4);

		// Set up initial visibility
		boolean lesionsSetToYes = FieldHelper.getNullableSourceFieldValue(getFieldGroup().getField(LESIONS)) == SymptomState.YES;
		for (String monkeypoxImage : monkeypoxImages) {
			getContent().getComponent(monkeypoxImage).setVisible(lesionsSetToYes);
		}

		// Set up image visibility listener
		getFieldGroup().getField(LESIONS).addValueChangeListener(e -> {
			for (String monkeypoxImage : monkeypoxImages) {
				getContent().getComponent(monkeypoxImage)
					.setVisible(FieldHelper.getNullableSourceFieldValue((Field) e.getProperty()) == SymptomState.YES);
			}
		});
	}

	public List<String> getUnconditionalSymptomFieldIds() {
		return unconditionalSymptomFieldIds;
	}

	public Button createButtonSetClearedToSymptomState(String caption, SymptomState symptomState) {

		Button button = ButtonHelper.createButton(caption, event -> {
			for (Object symptomId : unconditionalSymptomFieldIds) {
				Field<Object> symptom = (Field<Object>) getFieldGroup().getField(symptomId);
				if (symptom.isVisible() && (Set.class.isAssignableFrom(symptom.getValue().getClass()) && ((Set) symptom.getValue()).size() == 0)) {
					Set<SymptomState> value = (Set<SymptomState>) symptom.getValue();
					value.add(symptomState);
					symptom.setValue(value);
				}
			}
			for (Object symptomId : conditionalBleedingSymptomFieldIds) {
				Field<Object> symptom = (Field<Object>) getFieldGroup().getField(symptomId);
				if (symptom.isVisible() && (Set.class.isAssignableFrom(symptom.getValue().getClass()) && ((Set) symptom.getValue()).size() == 0)) {
					Set<SymptomState> value = (Set<SymptomState>) symptom.getValue();
					value.add(symptomState);
					symptom.setValue(value);
				}
			}
			for (Object symptomId : lesionsFieldIds) {
				Field<Object> symptom = (Field<Object>) getFieldGroup().getField(symptomId);
				if (symptom.isVisible()) {
					if (symptom.isRequired() && symptom.getValue() == null) {
						symptom.setValue(symptomState);
					} else if (Set.class.isAssignableFrom(symptom.getValue().getClass()) && ((Set) symptom.getValue()).size() == 0) {
						Set<SymptomState> value = (Set<SymptomState>) symptom.getValue();
						value.add(symptomState);
						symptom.setValue(value);
					}
				}
			}
			for (Object symptomId : monkeypoxImageFieldIds) {
				Field<Object> symptom = (Field<Object>) getFieldGroup().getField(symptomId);
				if (symptom.isVisible()) {
					if (symptom.isRequired() && symptom.getValue() == null) {
						symptom.setValue(symptomState);
					} else if (Set.class.isAssignableFrom(symptom.getValue().getClass()) && ((Set) symptom.getValue()).size() == 0) {
						Set<SymptomState> value = (Set<SymptomState>) symptom.getValue();
						value.add(symptomState);
						symptom.setValue(value);
					}
				}
			}
		}, ValoTheme.BUTTON_LINK);

		return button;
	}

}
