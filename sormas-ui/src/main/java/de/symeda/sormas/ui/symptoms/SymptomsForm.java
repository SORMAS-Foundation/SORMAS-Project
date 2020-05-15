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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.symptoms;

import static de.symeda.sormas.api.symptoms.SymptomsDto.*;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_NONE;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidColumn;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRow;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.locsCss;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.DateField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.hospitalization.HospitalizationDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsContext;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.DiseaseFieldVisibilityChecker;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.OutbreakFieldVisibilityChecker;
import de.symeda.sormas.ui.utils.ViewMode;

public class SymptomsForm extends AbstractEditForm<SymptomsDto> {

    private static final long serialVersionUID = 1L;


    private static final String BUTTONS_LOC = "buttonsLoc";
    private static final String LESIONS_LOCATIONS_LOC = "lesionsLocationsLoc";
    private static final String MONKEYPOX_LESIONS_IMG1 = "monkeypoxLesionsImg1";
    private static final String MONKEYPOX_LESIONS_IMG2 = "monkeypoxLesionsImg2";
    private static final String MONKEYPOX_LESIONS_IMG3 = "monkeypoxLesionsImg3";
    private static final String MONKEYPOX_LESIONS_IMG4 = "monkeypoxLesionsImg4";
    private static final String SYMPTOMS_HINT_LOC = "symptomsHintLoc";
    private static final String COMPLICATIONS_HEADING = "complicationsHeading";

    private static final String HTML_LAYOUT =
            h3(I18nProperties.getString(Strings.headingClinicalMeasurements)) +
                    fluidRowLocs(TEMPERATURE, TEMPERATURE_SOURCE) +
                    fluidRowLocs(BLOOD_PRESSURE_SYSTOLIC, BLOOD_PRESSURE_DIASTOLIC, HEART_RATE, RESPIRATORY_RATE) +
                    fluidRowLocs(GLASGOW_COMA_SCALE, WEIGHT, HEIGHT, MID_UPPER_ARM_CIRCUMFERENCE) +
                    h3(I18nProperties.getString(Strings.headingSignsAndSymptoms)) +
                    fluidRowCss(VSPACE_3,
                            //XXX #1620 fluidColumnLoc?
                            fluidColumn(8, 0, loc(SYMPTOMS_HINT_LOC)),
                            fluidColumn(4, 0, locCss(CssStyles.ALIGN_RIGHT, BUTTONS_LOC))) +
                    fluidRow(
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3,
                                            ABDOMINAL_PAIN, ABNORMAL_LUNG_XRAY_FINDINGS,
                                            ACUTE_RESPIRATORY_DISTRESS_SYNDROME, HEARINGLOSS, ANOREXIA_APPETITE_LOSS,
                                            BACKACHE, BLACKENING_DEATH_OF_TISSUE, BLOOD_IN_STOOL,
                                            BUBOES_GROIN_ARMPIT_NECK, BULGING_FONTANELLE,
                                            BILATERAL_CATARACTS, UNILATERAL_CATARACTS, CHEST_PAIN, CHILLS_SWEATS,
                                            CONGENITAL_GLAUCOMA, CONGENITAL_HEART_DISEASE,
                                            CONGENITAL_HEART_DISEASE_TYPE, CONGENITAL_HEART_DISEASE_DETAILS,
                                            CONJUNCTIVITIS, CONJUNCTIVAL_INJECTION, COUGH, COUGH_WITH_SPUTUM,
                                            COUGH_WITH_HEAMOPTYSIS,
                                            DARK_URINE, DEHYDRATION, DEVELOPMENTAL_DELAY, DIARRHEA,
                                            DIFFICULTY_BREATHING, LYMPHADENOPATHY, LYMPHADENOPATHY_AXILLARY,
                                            LYMPHADENOPATHY_CERVICAL, LYMPHADENOPATHY_INGUINAL,
                                            FATIGUE_WEAKNESS, FEVER, FLUID_IN_LUNG_CAVITY,
                                            FLUID_IN_LUNG_CAVITY_AUSCULTATION, FLUID_IN_LUNG_CAVITY_XRAY,
                                            HEADACHE, HICCUPS, BEDRIDDEN,
                                            JAUNDICE, JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, JOINT_PAIN, KOPLIKS_SPOTS,
                                            LOSS_SKIN_TURGOR,
                                            SKIN_RASH, MALAISE, MENINGOENCEPHALITIS, OTITIS_MEDIA, MICROCEPHALY,
                                            MUSCLE_PAIN,
                                            NAUSEA, NECK_STIFFNESS, OEDEMA_FACE_NECK, OEDEMA_LOWER_EXTREMITY,
                                            EYE_PAIN_LIGHT_SENSITIVE,
                                            PAINFUL_LYMPHADENITIS, ANXIETY_STATES, DELIRIUM, UPROARIOUSNESS,
                                            PARASTHESIA_AROUND_WOUND,
                                            EXCESS_SALIVATION, INSOMNIA, PARALYSIS, EXCITATION, DYSPHAGIA, AEROPHOBIA
                                            , CONVULSION)),
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3,
                                            PALPABLE_LIVER, PALPABLE_SPLEEN, PHARYNGEAL_ERYTHEMA, PHARYNGEAL_EXUDATE,
                                            PIGMENTARY_RETINOPATHY, PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
                                            PURPURIC_RASH, RADIOLUCENT_BONE_DISEASE, RAPID_BREATHING,
                                            REFUSAL_FEEDOR_DRINK, RUNNY_NOSE,
                                            ORAL_ULCERS, SIDE_PAIN, SORE_THROAT, SPLENOMEGALY, SUNKEN_EYES_FONTANELLE
                                            , SWOLLEN_GLANDS,
                                            THROBOCYTOPENIA, TREMOR, UNEXPLAINED_BLEEDING, EYES_BLEEDING,
                                            INJECTION_SITE_BLEEDING,
                                            BLEEDING_VAGINA, GUMS_BLEEDING, STOMACH_BLEEDING, BLOOD_URINE,
                                            BLOODY_BLACK_STOOL,
                                            SKIN_BRUISING, COUGHING_BLOOD, DIGESTED_BLOOD_VOMIT, RED_BLOOD_VOMIT,
                                            NOSE_BLEEDING,
                                            OTHER_HEMORRHAGIC_SYMPTOMS, OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
                                            LESIONS, LESIONS_THAT_ITCH, LESIONS_SAME_STATE, LESIONS_SAME_SIZE,
                                            LESIONS_DEEP_PROFOUND,
                                            LESIONS_LOCATIONS_LOC, LESIONS_FACE, LESIONS_LEGS, LESIONS_SOLES_FEET,
                                            LESIONS_PALMS_HANDS, LESIONS_THORAX,
                                            LESIONS_ARMS, LESIONS_GENITALS, LESIONS_ALL_OVER_BODY,
                                            LESIONS_RESEMBLE_IMG1, MONKEYPOX_LESIONS_IMG1,
                                            LESIONS_RESEMBLE_IMG2, MONKEYPOX_LESIONS_IMG2, LESIONS_RESEMBLE_IMG3,
                                            MONKEYPOX_LESIONS_IMG3, LESIONS_RESEMBLE_IMG4, MONKEYPOX_LESIONS_IMG4,
                                            LESIONS_ONSET_DATE, VOMITING, HYDROPHOBIA, OPISTHOTONUS, HYPERACTIVITY,
                                            PARESIS, AGITATION,
                                            ASCENDING_FLACCID_PARALYSIS, ERRATIC_BEHAVIOUR, COMA, LOSS_OF_TASTE,
                                            LOSS_OF_SMELL, WHEEZING, SKIN_ULCERS, INABILITY_TO_WALK,
                                            IN_DRAWING_OF_CHEST_WALL,
                                            OTHER_NON_HEMORRHAGIC_SYMPTOMS, OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT) +
                                            locsCss(VSPACE_3, PATIENT_ILL_LOCATION, SYMPTOMS_COMMENTS)
                            )
                    ) +
                    fluidRowLocsCss(VSPACE_3, ONSET_SYMPTOM, ONSET_DATE) +
                    loc(COMPLICATIONS_HEADING) +
                    fluidRow(
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3,
                                            ALTERED_CONSCIOUSNESS, CONFUSED_DISORIENTED, HEMORRHAGIC_SYNDROME,
                                            HYPERGLYCEMIA, HYPOGLYCEMIA, OTHER_COMPLICATIONS,
                                            OTHER_COMPLICATIONS_TEXT)),
                            fluidColumn(6, 0,
                                    locsCss(VSPACE_3,
                                            MENINGEAL_SIGNS, SEIZURES, SEPSIS, SHOCK))
                    );

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

    public SymptomsForm(CaseDataDto caze, Disease disease, PersonDto person, SymptomsContext symptomsContext,
						UserRight editOrCreateUserRight, ViewMode viewMode) {

        super(SymptomsDto.class, I18N_PREFIX, editOrCreateUserRight,
                new FieldVisibilityCheckers()
                        .add(new DiseaseFieldVisibilityChecker(disease))
                        .add(new OutbreakFieldVisibilityChecker(viewMode))
                        .add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())));

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

        DateField onsetDateField = addField(ONSET_DATE, DateField.class);
        ComboBox onsetSymptom = addField(ONSET_SYMPTOM, ComboBox.class);
        if (symptomsContext == SymptomsContext.CASE) {
            onsetDateField.addValidator(new DateComparisonValidator(onsetDateField,
					caze.getHospitalization().getAdmissionDate(), true, false,
                    I18nProperties.getValidationError(Validations.beforeDateSoft, onsetDateField.getCaption(),
							I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX,
									HospitalizationDto.ADMISSION_DATE))));
            onsetDateField.setInvalidCommitted(true);
        }

        ComboBox temperature = addField(TEMPERATURE, ComboBox.class);
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
            midUpperArmCircumference.setItemCaption(circumferenceValue,
					SymptomsHelper.getDecimalString(circumferenceValue));
        }
        ComboBox glasgowComaScale = addField(GLASGOW_COMA_SCALE, ComboBox.class);
        glasgowComaScale.addItems(SymptomsHelper.getGlasgowComaScaleValues());

        addFields(FEVER, VOMITING, DIARRHEA, BLOOD_IN_STOOL, NAUSEA,
                ABDOMINAL_PAIN, HEADACHE, MUSCLE_PAIN, FATIGUE_WEAKNESS, SKIN_RASH,
                NECK_STIFFNESS, SORE_THROAT, COUGH, COUGH_WITH_SPUTUM, COUGH_WITH_HEAMOPTYSIS, RUNNY_NOSE,
                DIFFICULTY_BREATHING, CHEST_PAIN, CONJUNCTIVITIS, EYE_PAIN_LIGHT_SENSITIVE, KOPLIKS_SPOTS,
                THROBOCYTOPENIA, OTITIS_MEDIA, HEARINGLOSS, DEHYDRATION,
                ANOREXIA_APPETITE_LOSS, REFUSAL_FEEDOR_DRINK, JOINT_PAIN, HICCUPS, BACKACHE, EYES_BLEEDING,
                JAUNDICE, DARK_URINE, STOMACH_BLEEDING, RAPID_BREATHING, SWOLLEN_GLANDS, SYMPTOMS_COMMENTS,
                UNEXPLAINED_BLEEDING, GUMS_BLEEDING, INJECTION_SITE_BLEEDING, NOSE_BLEEDING,
                BLOODY_BLACK_STOOL, RED_BLOOD_VOMIT, DIGESTED_BLOOD_VOMIT, COUGHING_BLOOD,
                BLEEDING_VAGINA, SKIN_BRUISING, BLOOD_URINE, OTHER_HEMORRHAGIC_SYMPTOMS,
				OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
                OTHER_NON_HEMORRHAGIC_SYMPTOMS, OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT, LESIONS, LESIONS_THAT_ITCH,
                LESIONS_SAME_STATE, LESIONS_SAME_SIZE, LESIONS_DEEP_PROFOUND, LESIONS_FACE, LESIONS_LEGS,
                LESIONS_SOLES_FEET, LESIONS_PALMS_HANDS, LESIONS_THORAX, LESIONS_ARMS, LESIONS_GENITALS,
				LESIONS_ALL_OVER_BODY,
                LYMPHADENOPATHY, LYMPHADENOPATHY_AXILLARY, LYMPHADENOPATHY_CERVICAL, LYMPHADENOPATHY_INGUINAL,
                CHILLS_SWEATS, BEDRIDDEN, ORAL_ULCERS, PAINFUL_LYMPHADENITIS, BLACKENING_DEATH_OF_TISSUE,
				BUBOES_GROIN_ARMPIT_NECK,
                BULGING_FONTANELLE, PHARYNGEAL_ERYTHEMA, PHARYNGEAL_EXUDATE, OEDEMA_FACE_NECK, OEDEMA_LOWER_EXTREMITY,
                LOSS_SKIN_TURGOR, PALPABLE_LIVER, PALPABLE_SPLEEN, MALAISE, SUNKEN_EYES_FONTANELLE,
                SIDE_PAIN, FLUID_IN_LUNG_CAVITY, TREMOR, BILATERAL_CATARACTS, UNILATERAL_CATARACTS,
                CONGENITAL_GLAUCOMA, CONGENITAL_HEART_DISEASE, PIGMENTARY_RETINOPATHY, RADIOLUCENT_BONE_DISEASE,
                SPLENOMEGALY, MICROCEPHALY, MENINGOENCEPHALITIS, PURPURIC_RASH, DEVELOPMENTAL_DELAY,
                CONGENITAL_HEART_DISEASE_TYPE, CONGENITAL_HEART_DISEASE_DETAILS, JAUNDICE_WITHIN_24_HOURS_OF_BIRTH,
				PATIENT_ILL_LOCATION,
                HYDROPHOBIA, OPISTHOTONUS, ANXIETY_STATES, DELIRIUM, UPROARIOUSNESS, PARASTHESIA_AROUND_WOUND,
                EXCESS_SALIVATION, INSOMNIA, PARALYSIS, EXCITATION, DYSPHAGIA, AEROPHOBIA, HYPERACTIVITY,
                PARESIS, AGITATION, ASCENDING_FLACCID_PARALYSIS, ERRATIC_BEHAVIOUR, COMA, CONVULSION,
                FLUID_IN_LUNG_CAVITY_AUSCULTATION, FLUID_IN_LUNG_CAVITY_XRAY, ABNORMAL_LUNG_XRAY_FINDINGS,
				CONJUNCTIVAL_INJECTION,
                ACUTE_RESPIRATORY_DISTRESS_SYNDROME, PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
                LOSS_OF_TASTE, LOSS_OF_SMELL, WHEEZING, SKIN_ULCERS, INABILITY_TO_WALK, IN_DRAWING_OF_CHEST_WALL);
        addField(LESIONS_ONSET_DATE, DateField.class);

        // complications
        addFields(ALTERED_CONSCIOUSNESS, CONFUSED_DISORIENTED,
                OTHER_COMPLICATIONS, OTHER_COMPLICATIONS_TEXT,
                HEMORRHAGIC_SYNDROME, HYPERGLYCEMIA, HYPOGLYCEMIA,
                MENINGEAL_SIGNS, SEIZURES, SEPSIS, SHOCK);

        monkeypoxImageFieldIds = Arrays.asList(LESIONS_RESEMBLE_IMG1, LESIONS_RESEMBLE_IMG2, LESIONS_RESEMBLE_IMG3,
				LESIONS_RESEMBLE_IMG4);
        for (String propertyId : monkeypoxImageFieldIds) {
            @SuppressWarnings("rawtypes")
            Field monkeypoxImageField = addField(propertyId);
            CssStyles.style(monkeypoxImageField, VSPACE_NONE);
        }

        // Set initial visibilities

        initializeVisibilitiesAndAllowedVisibilities();

        if (symptomsContext != SymptomsContext.CLINICAL_VISIT) {
            setVisible(false, BLOOD_PRESSURE_SYSTOLIC, BLOOD_PRESSURE_DIASTOLIC, HEART_RATE,
                    RESPIRATORY_RATE, WEIGHT, HEIGHT, MID_UPPER_ARM_CIRCUMFERENCE,
                    GLASGOW_COMA_SCALE);
        } else {
            setVisible(false, ONSET_SYMPTOM, ONSET_DATE);
        }

        // Initialize lists

        conditionalBleedingSymptomFieldIds = Arrays.asList(GUMS_BLEEDING,
                INJECTION_SITE_BLEEDING, NOSE_BLEEDING, BLOODY_BLACK_STOOL,
                RED_BLOOD_VOMIT, DIGESTED_BLOOD_VOMIT, EYES_BLEEDING,
                COUGHING_BLOOD, BLEEDING_VAGINA, SKIN_BRUISING,
                STOMACH_BLEEDING, BLOOD_URINE, OTHER_HEMORRHAGIC_SYMPTOMS);

        lesionsFieldIds = Arrays.asList(LESIONS_SAME_STATE, LESIONS_SAME_SIZE,
                LESIONS_DEEP_PROFOUND, LESIONS_THAT_ITCH);

        lesionsLocationFieldIds = Arrays.asList(LESIONS_FACE, LESIONS_LEGS,
                LESIONS_SOLES_FEET, LESIONS_PALMS_HANDS, LESIONS_THORAX,
                LESIONS_ARMS, LESIONS_GENITALS, LESIONS_ALL_OVER_BODY);

        unconditionalSymptomFieldIds = Arrays.asList(FEVER, ABNORMAL_LUNG_XRAY_FINDINGS,
                CONJUNCTIVAL_INJECTION, ACUTE_RESPIRATORY_DISTRESS_SYNDROME,
                PNEUMONIA_CLINICAL_OR_RADIOLOGIC, VOMITING, DIARRHEA,
                BLOOD_IN_STOOL, NAUSEA, ABDOMINAL_PAIN, HEADACHE,
                MUSCLE_PAIN, FATIGUE_WEAKNESS, SKIN_RASH,
                NECK_STIFFNESS, SORE_THROAT, COUGH, COUGH_WITH_SPUTUM,
                COUGH_WITH_HEAMOPTYSIS, RUNNY_NOSE,
                DIFFICULTY_BREATHING, CHEST_PAIN, CONJUNCTIVITIS,
                EYE_PAIN_LIGHT_SENSITIVE, KOPLIKS_SPOTS, THROBOCYTOPENIA,
                OTITIS_MEDIA, HEARINGLOSS, DEHYDRATION,
                ANOREXIA_APPETITE_LOSS, REFUSAL_FEEDOR_DRINK, JOINT_PAIN,
                HICCUPS, BACKACHE, JAUNDICE, DARK_URINE,
                RAPID_BREATHING, SWOLLEN_GLANDS, UNEXPLAINED_BLEEDING,
                OTHER_NON_HEMORRHAGIC_SYMPTOMS, LESIONS,
                LYMPHADENOPATHY, LYMPHADENOPATHY_AXILLARY,
                LYMPHADENOPATHY_CERVICAL, LYMPHADENOPATHY_INGUINAL, CHILLS_SWEATS,
                BEDRIDDEN, ORAL_ULCERS, PAINFUL_LYMPHADENITIS,
                BLACKENING_DEATH_OF_TISSUE, BUBOES_GROIN_ARMPIT_NECK,
                BULGING_FONTANELLE, PHARYNGEAL_ERYTHEMA, PHARYNGEAL_EXUDATE,
                OEDEMA_FACE_NECK, OEDEMA_LOWER_EXTREMITY, LOSS_SKIN_TURGOR,
                PALPABLE_LIVER, PALPABLE_SPLEEN, MALAISE,
                SUNKEN_EYES_FONTANELLE, SIDE_PAIN, FLUID_IN_LUNG_CAVITY,
                FLUID_IN_LUNG_CAVITY_AUSCULTATION, FLUID_IN_LUNG_CAVITY_XRAY,
                TREMOR, BILATERAL_CATARACTS, UNILATERAL_CATARACTS,
                CONGENITAL_GLAUCOMA, CONGENITAL_HEART_DISEASE,
                RADIOLUCENT_BONE_DISEASE, SPLENOMEGALY, MICROCEPHALY,
                MENINGOENCEPHALITIS, DEVELOPMENTAL_DELAY, PURPURIC_RASH,
                PIGMENTARY_RETINOPATHY, CONVULSION, AEROPHOBIA,
                AGITATION, ANXIETY_STATES, ASCENDING_FLACCID_PARALYSIS,
                COMA, DELIRIUM, DYSPHAGIA, ERRATIC_BEHAVIOUR,
                EXCESS_SALIVATION, EXCITATION, HYDROPHOBIA,
                HYPERACTIVITY, INSOMNIA, OPISTHOTONUS, PARALYSIS,
                PARASTHESIA_AROUND_WOUND, PARESIS, UPROARIOUSNESS,
                // complications
                ALTERED_CONSCIOUSNESS, CONFUSED_DISORIENTED, HEMORRHAGIC_SYNDROME,
                HYPERGLYCEMIA, HYPOGLYCEMIA, MENINGEAL_SIGNS,
                SEIZURES, SEPSIS, SHOCK, LOSS_OF_TASTE,
                LOSS_OF_SMELL, WHEEZING, SKIN_ULCERS, INABILITY_TO_WALK,
                IN_DRAWING_OF_CHEST_WALL, OTHER_COMPLICATIONS);

        // Set visibilities

        FieldHelper.setVisibleWhen(getFieldGroup(),
                conditionalBleedingSymptomFieldIds,
                UNEXPLAINED_BLEEDING,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
                OTHER_HEMORRHAGIC_SYMPTOMS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT,
                OTHER_NON_HEMORRHAGIC_SYMPTOMS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                OTHER_COMPLICATIONS_TEXT,
                OTHER_COMPLICATIONS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                lesionsFieldIds,
                LESIONS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                lesionsLocationFieldIds,
                LESIONS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(),
                LESIONS_ONSET_DATE,
                LESIONS,
                Arrays.asList(SymptomState.YES), true);

        FieldHelper.setVisibleWhen(getFieldGroup(), CONGENITAL_HEART_DISEASE_TYPE, CONGENITAL_HEART_DISEASE,
				Arrays.asList(SymptomState.YES), true);
        FieldHelper.setVisibleWhen(getFieldGroup(), CONGENITAL_HEART_DISEASE_DETAILS, CONGENITAL_HEART_DISEASE_TYPE,
				Arrays.asList(CongenitalHeartDiseaseType.OTHER), true);
        if (isVisibleAllowed(getFieldGroup().getField(JAUNDICE_WITHIN_24_HOURS_OF_BIRTH))) {
            FieldHelper.setVisibleWhen(getFieldGroup(), JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, JAUNDICE,
					Arrays.asList(SymptomState.YES), true);
        }

        FieldHelper.addSoftRequiredStyle(getField(LESIONS_ONSET_DATE));

        boolean isInfant = person != null && person.getApproximateAge() != null
                && ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS)
                || person.getApproximateAge() <= 1);
        if (!isInfant) {
            getFieldGroup().getField(BULGING_FONTANELLE).setVisible(false);
        }

        // Handle visibility of lesions locations caption
        Label lesionsLocationsCaption = new Label(I18nProperties.getCaption(Captions.symptomsLesionsLocations));
        CssStyles.style(lesionsLocationsCaption, VSPACE_3);
        getContent().addComponent(lesionsLocationsCaption, LESIONS_LOCATIONS_LOC);
        getContent().getComponent(LESIONS_LOCATIONS_LOC).setVisible(getFieldGroup().getField(LESIONS).getValue() == SymptomState.YES);
        getFieldGroup().getField(LESIONS).addValueChangeListener(e -> {
            getContent().getComponent(LESIONS_LOCATIONS_LOC).setVisible(e.getProperty().getValue() == SymptomState.YES);
        });

        // Symptoms hint text
        Label symptomsHint = new Label(I18nProperties.getString(symptomsContext == SymptomsContext.CASE ?
				Strings.messageSymptomsHint : Strings.messageSymptomsVisitHint), ContentMode.HTML);
        getContent().addComponent(symptomsHint, SYMPTOMS_HINT_LOC);

        if (disease == Disease.MONKEYPOX) {
            setUpMonkeypoxVisibilities();
        }

        if (symptomsContext != SymptomsContext.CASE) {
            getFieldGroup().getField(PATIENT_ILL_LOCATION).setVisible(false);
        }

        FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(OTHER_HEMORRHAGIC_SYMPTOMS),
                Arrays.asList(OTHER_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
        FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(OTHER_NON_HEMORRHAGIC_SYMPTOMS),
                Arrays.asList(OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
        FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(OTHER_COMPLICATIONS),
                Arrays.asList(OTHER_COMPLICATIONS_TEXT), Arrays.asList(SymptomState.YES), disease);
        FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(LESIONS), lesionsFieldIds,
				Arrays.asList(SymptomState.YES), disease);
        FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(LESIONS), monkeypoxImageFieldIds,
				Arrays.asList(SymptomState.YES), disease);

        addListenerForOnsetFields(onsetSymptom, onsetDateField);

        Button clearAllButton = new Button(I18nProperties.getCaption(Captions.actionClearAll));
        clearAllButton.addStyleName(ValoTheme.BUTTON_LINK);

        clearAllButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
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
            }
        });

        Button setEmptyToNoButton = new Button(I18nProperties.getCaption(Captions.symptomsSetClearedToNo));
        setEmptyToNoButton.addStyleName(ValoTheme.BUTTON_LINK);

        setEmptyToNoButton.addClickListener(new ClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void buttonClick(ClickEvent event) {
                for (Object symptomId : unconditionalSymptomFieldIds) {
                    Field<SymptomState> symptom = (Field<SymptomState>) getFieldGroup().getField(symptomId);
                    if (symptom.isVisible() && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }
                for (Object symptomId : conditionalBleedingSymptomFieldIds) {
                    Field<SymptomState> symptom = (Field<SymptomState>) getFieldGroup().getField(symptomId);
                    if (symptom.isVisible() && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }
                for (Object symptomId : lesionsFieldIds) {
                    Field<SymptomState> symptom = (Field<SymptomState>) getFieldGroup().getField(symptomId);
                    if (symptom.isVisible() && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }
                for (Object symptomId : monkeypoxImageFieldIds) {
                    Field<SymptomState> symptom = (Field<SymptomState>) getFieldGroup().getField(symptomId);
                    if (symptom.isVisible() && symptom.getValue() == null) {
                        symptom.setValue(SymptomState.NO);
                    }
                }
            }
        });

        // Complications heading - not displayed for Rubella (dirty, should be made generic)
        Label complicationsHeading = new Label(I18nProperties.getString(Strings.headingComplications));
        CssStyles.style(complicationsHeading, CssStyles.H3);
        if (disease != Disease.CONGENITAL_RUBELLA) {
            getContent().addComponent(complicationsHeading, COMPLICATIONS_HEADING);
        }

        HorizontalLayout buttonsLayout = new HorizontalLayout();
        buttonsLayout.addComponent(clearAllButton);
        buttonsLayout.addComponent(setEmptyToNoButton);
        buttonsLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        getContent().addComponent(buttonsLayout, BUTTONS_LOC);
    }

    @Override
    protected String createHtmlLayout() {
        return HTML_LAYOUT;
    }

    public void initializeSymptomRequirementsForVisit(OptionGroup visitStatus) {
        FieldHelper.addSoftRequiredStyleWhen(getFieldGroup(), visitStatus, Arrays.asList(TEMPERATURE,
				TEMPERATURE_SOURCE), Arrays.asList(VisitStatus.COOPERATIVE), disease);
        addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), ONSET_DATE, unconditionalSymptomFieldIds,
				Arrays.asList(SymptomState.YES), visitStatus);
        addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), ONSET_SYMPTOM,
				unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), visitStatus);
        getFieldGroup().getField(FEVER).addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (getFieldGroup().getField(TEMPERATURE).getValue() != null) {
                    if ((Float) (getFieldGroup().getField(TEMPERATURE).getValue()) >= 38.0f) {
                        if (value != SymptomState.YES) {
                            throw new InvalidValueException(I18nProperties.getString(Strings.errorSetFeverRequired));
                        }
                    }
                }
            }
        });
    }

    @Override
    public void setValue(SymptomsDto newFieldValue) throws ReadOnlyException, ConversionException {
        super.setValue(newFieldValue);

        initializeSymptomRequirementsForCase();

        if (symptomsContext == SymptomsContext.CLINICAL_VISIT) {
            initializeSymptomRequirementsForClinicalVisit();
        }
    }

    private void initializeSymptomRequirementsForCase() {
        addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), ONSET_DATE, unconditionalSymptomFieldIds,
				Arrays.asList(SymptomState.YES), null);
        addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), ONSET_SYMPTOM,
				unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), null);
        addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), PATIENT_ILL_LOCATION,
				unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), null);
    }

    private void initializeSymptomRequirementsForClinicalVisit() {
        getFieldGroup().getField(FEVER).addValidator(new Validator() {
            @Override
            public void validate(Object value) throws InvalidValueException {
                if (getFieldGroup().getField(TEMPERATURE).getValue() != null) {
                    if ((Float) (getFieldGroup().getField(TEMPERATURE).getValue()) >= 38.0f) {
                        if (value != SymptomState.YES) {
                            throw new InvalidValueException(I18nProperties.getString(Strings.errorSetFeverRequired));
                        }
                    }
                }
            }
        });
    }

    /**
     * Sets the fields defined by the ids contained in sourceValues to required when the person is symptomatic
     * and - if a visit is processed - cooperative. When this method is called from within a case, it needs to
     * be called with visitStatusField set to null in order to ignore the visit status requirement.
     */
    @SuppressWarnings("rawtypes")
    private void addSoftRequiredStyleWhenSymptomaticAndCooperative(FieldGroup fieldGroup, Object targetPropertyId,
                                                                   List<String> sourcePropertyIds,
																   List<Object> sourceValues,
																   OptionGroup visitStatusField) {

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
            if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) &&
                    visitStatusField.getValue() == VisitStatus.COOPERATIVE) {
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
                    if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) &&
                            visitStatusField.getValue() == VisitStatus.COOPERATIVE) {
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
                    if (isAnySymptomSetToYes(fieldGroup, sourcePropertyIds, sourceValues) &&
                            visitStatusField.getValue() == VisitStatus.COOPERATIVE) {
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
    public boolean isAnySymptomSetToYes(FieldGroup fieldGroup, List<String> sourcePropertyIds,
                                        List<Object> sourceValues) {

        for (Object sourcePropertyId : sourcePropertyIds) {
            Field sourceField = fieldGroup.getField(sourcePropertyId);
            if (sourceValues.contains(sourceField.getValue())) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("rawtypes")
    private void addListenerForOnsetFields(ComboBox onsetSymptom, DateField onsetDateField) {
        List<String> allPropertyIds =
                Stream.concat(unconditionalSymptomFieldIds.stream(), conditionalBleedingSymptomFieldIds.stream())
                        .collect(Collectors.toList());
        allPropertyIds.add(LESIONS_THAT_ITCH);

        for (Object sourcePropertyId : allPropertyIds) {
            Field sourceField = getFieldGroup().getField(sourcePropertyId);
            sourceField.addValueChangeListener(event -> {
                if (sourceField.getValue() == SymptomState.YES) {
                    onsetSymptom.addItem(sourceField.getCaption());
                    onsetDateField.setEnabled(true);
                } else {
                    onsetSymptom.removeItem(sourceField.getCaption());
                    onsetDateField.setEnabled(isAnySymptomSetToYes(getFieldGroup(), allPropertyIds, Arrays.asList(SymptomState.YES)));
                }
                onsetSymptom.setEnabled(!onsetSymptom.getItemIds().isEmpty());
            });
        }
        onsetSymptom.setEnabled(false); // will be updated by listener if needed
        onsetDateField.setEnabled(false); // will be updated by listener if needed
    }

    private void setUpMonkeypoxVisibilities() {
        // Monkeypox picture resemblance fields
        FieldHelper.setVisibleWhen(getFieldGroup(),
                monkeypoxImageFieldIds,
                LESIONS,
                Arrays.asList(SymptomState.YES), true);

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
        boolean lesionsSetToYes = getFieldGroup().getField(LESIONS).getValue() == SymptomState.YES;
        for (String monkeypoxImage : monkeypoxImages) {
            getContent().getComponent(monkeypoxImage).setVisible(lesionsSetToYes);
        }

        // Set up image visibility listener
        getFieldGroup().getField(LESIONS).addValueChangeListener(e -> {
            for (String monkeypoxImage : monkeypoxImages) {
                getContent().getComponent(monkeypoxImage).setVisible(e.getProperty().getValue() == SymptomState.YES);
            }
        });
    }

    public List<String> getUnconditionalSymptomFieldIds() {
        return unconditionalSymptomFieldIds;
    }
}
