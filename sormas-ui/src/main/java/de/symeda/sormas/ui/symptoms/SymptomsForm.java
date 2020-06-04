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
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateComparisonValidator;
import de.symeda.sormas.ui.utils.FieldHelper;
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
			fluidRowLocs(SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE) +
			fluidRowLocs(SymptomsDto.BLOOD_PRESSURE_SYSTOLIC, SymptomsDto.BLOOD_PRESSURE_DIASTOLIC, SymptomsDto.HEART_RATE, SymptomsDto.RESPIRATORY_RATE) +
			fluidRowLocs(SymptomsDto.GLASGOW_COMA_SCALE, SymptomsDto.WEIGHT, SymptomsDto.HEIGHT, SymptomsDto.MID_UPPER_ARM_CIRCUMFERENCE) +
			h3(I18nProperties.getString(Strings.headingSignsAndSymptoms)) +
			fluidRowCss(VSPACE_3,
					//XXX #1620 fluidColumnLoc?
					fluidColumn(8, 0, loc(SYMPTOMS_HINT_LOC)),
					fluidColumn(4, 0,locCss(CssStyles.ALIGN_RIGHT, BUTTONS_LOC))) +
			fluidRow(
					fluidColumn(6, 0,
							locsCss(VSPACE_3,
									SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.ABNORMAL_LUNG_XRAY_FINDINGS, SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME, SymptomsDto.HEARINGLOSS, SymptomsDto.ANOREXIA_APPETITE_LOSS,
									SymptomsDto.BACKACHE, SymptomsDto.BLACKENING_DEATH_OF_TISSUE, SymptomsDto.BLOOD_IN_STOOL, SymptomsDto.BUBOES_GROIN_ARMPIT_NECK, SymptomsDto.BULGING_FONTANELLE, 
									SymptomsDto.BILATERAL_CATARACTS, SymptomsDto.UNILATERAL_CATARACTS, SymptomsDto.CHEST_PAIN, SymptomsDto.CHILLS_SWEATS, 
									SymptomsDto.CONGENITAL_GLAUCOMA, SymptomsDto.CONGENITAL_HEART_DISEASE, SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE, SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS,
									SymptomsDto.CONJUNCTIVITIS, SymptomsDto.CONJUNCTIVAL_INJECTION, SymptomsDto.COUGH, SymptomsDto.DARK_URINE, SymptomsDto.DEHYDRATION, SymptomsDto.DEVELOPMENTAL_DELAY, SymptomsDto.DIARRHEA, 
									SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.LYMPHADENOPATHY_AXILLARY, SymptomsDto.LYMPHADENOPATHY_CERVICAL, SymptomsDto.LYMPHADENOPATHY_INGUINAL, 
									SymptomsDto.FATIGUE_WEAKNESS, SymptomsDto.FEVER, SymptomsDto.FLUID_IN_LUNG_CAVITY, SymptomsDto.FLUID_IN_LUNG_CAVITY_AUSCULTATION, SymptomsDto.FLUID_IN_LUNG_CAVITY_XRAY,
									SymptomsDto.HEADACHE, SymptomsDto.HICCUPS, SymptomsDto.BEDRIDDEN, 
									SymptomsDto.JAUNDICE, SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, SymptomsDto.JOINT_PAIN, SymptomsDto.KOPLIKS_SPOTS, SymptomsDto.LOSS_SKIN_TURGOR, 
									SymptomsDto.SKIN_RASH, SymptomsDto.MALAISE, SymptomsDto.MENINGOENCEPHALITIS, SymptomsDto.OTITIS_MEDIA, SymptomsDto.MICROCEPHALY, SymptomsDto.MUSCLE_PAIN, 
									SymptomsDto.NAUSEA, SymptomsDto.NECK_STIFFNESS, SymptomsDto.OEDEMA_FACE_NECK, SymptomsDto.OEDEMA_LOWER_EXTREMITY, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, 
									SymptomsDto.PAINFUL_LYMPHADENITIS, SymptomsDto.ANXIETY_STATES, SymptomsDto.DELIRIUM, SymptomsDto.UPROARIOUSNESS, SymptomsDto.PARASTHESIA_AROUND_WOUND, 
									SymptomsDto.EXCESS_SALIVATION, SymptomsDto.INSOMNIA, SymptomsDto.PARALYSIS, SymptomsDto.EXCITATION, SymptomsDto.DYSPHAGIA, SymptomsDto.AEROPHOBIA, SymptomsDto.CONVULSION)),
					fluidColumn(6, 0, 
							locsCss(VSPACE_3,
									SymptomsDto.PALPABLE_LIVER, SymptomsDto.PALPABLE_SPLEEN, SymptomsDto.PHARYNGEAL_ERYTHEMA, SymptomsDto.PHARYNGEAL_EXUDATE, SymptomsDto.PIGMENTARY_RETINOPATHY, SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
									SymptomsDto.PURPURIC_RASH, SymptomsDto.RADIOLUCENT_BONE_DISEASE, SymptomsDto.RAPID_BREATHING, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.RUNNY_NOSE, 
									SymptomsDto.ORAL_ULCERS, SymptomsDto.SIDE_PAIN, SymptomsDto.SORE_THROAT, SymptomsDto.SPLENOMEGALY, SymptomsDto.SUNKEN_EYES_FONTANELLE, SymptomsDto.SWOLLEN_GLANDS, 
									SymptomsDto.THROBOCYTOPENIA, SymptomsDto.TREMOR, SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.EYES_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, 
									SymptomsDto.BLEEDING_VAGINA, SymptomsDto.GUMS_BLEEDING, SymptomsDto.STOMACH_BLEEDING, SymptomsDto.BLOOD_URINE, SymptomsDto.BLOODY_BLACK_STOOL, 
									SymptomsDto.SKIN_BRUISING, SymptomsDto.COUGHING_BLOOD, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.NOSE_BLEEDING, 
									SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, 
									SymptomsDto.LESIONS, SymptomsDto.LESIONS_THAT_ITCH, SymptomsDto.LESIONS_SAME_STATE, SymptomsDto.LESIONS_SAME_SIZE, SymptomsDto.LESIONS_DEEP_PROFOUND, 
									LESIONS_LOCATIONS_LOC, SymptomsDto.LESIONS_FACE, SymptomsDto.LESIONS_LEGS, SymptomsDto.LESIONS_SOLES_FEET, SymptomsDto.LESIONS_PALMS_HANDS, SymptomsDto.LESIONS_THORAX,
									SymptomsDto.LESIONS_ARMS, SymptomsDto.LESIONS_GENITALS, SymptomsDto.LESIONS_ALL_OVER_BODY, SymptomsDto.LESIONS_RESEMBLE_IMG1, MONKEYPOX_LESIONS_IMG1, 
									SymptomsDto.LESIONS_RESEMBLE_IMG2, MONKEYPOX_LESIONS_IMG2, SymptomsDto.LESIONS_RESEMBLE_IMG3, MONKEYPOX_LESIONS_IMG3, SymptomsDto.LESIONS_RESEMBLE_IMG4, MONKEYPOX_LESIONS_IMG4,
									SymptomsDto.LESIONS_ONSET_DATE, SymptomsDto.VOMITING, SymptomsDto.HYDROPHOBIA, SymptomsDto.OPISTHOTONUS, SymptomsDto.HYPERACTIVITY, SymptomsDto.PARESIS, SymptomsDto.AGITATION, 
											SymptomsDto.ASCENDING_FLACCID_PARALYSIS, SymptomsDto.ERRATIC_BEHAVIOUR,
											SymptomsDto.COMA, SymptomsDto.LOSS_OF_TASTE,
											SymptomsDto.LOSS_OF_SMELL,
									SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT) +
							locsCss(VSPACE_3,
									SymptomsDto.PATIENT_ILL_LOCATION, SymptomsDto.SYMPTOMS_COMMENTS)
					)
			) +
			fluidRowLocsCss(VSPACE_3, SymptomsDto.ONSET_SYMPTOM, SymptomsDto.ONSET_DATE) +
			loc(COMPLICATIONS_HEADING) +
			fluidRow(
					fluidColumn(6, 0,
							locsCss(VSPACE_3,
									SymptomsDto.ALTERED_CONSCIOUSNESS, SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.HEMORRHAGIC_SYNDROME, 
									SymptomsDto.HYPERGLYCEMIA, SymptomsDto.HYPOGLYCEMIA)),
					fluidColumn(6, 0, 
							locsCss(VSPACE_3,
									SymptomsDto.MENINGEAL_SIGNS, SymptomsDto.SEIZURES, SymptomsDto.SEPSIS, SymptomsDto.SHOCK))
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

	public SymptomsForm(CaseDataDto caze, Disease disease, PersonDto person, SymptomsContext symptomsContext, UserRight editOrCreateUserRight, ViewMode viewMode) {
		// TODO add user right parameter
		super(SymptomsDto.class, SymptomsDto.I18N_PREFIX, editOrCreateUserRight);
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

		DateField onsetDateField = addField(SymptomsDto.ONSET_DATE, DateField.class);
		ComboBox onsetSymptom = addField(SymptomsDto.ONSET_SYMPTOM, ComboBox.class);
		if (symptomsContext == SymptomsContext.CASE) {
			onsetDateField.addValidator(new DateComparisonValidator(onsetDateField, caze.getHospitalization().getAdmissionDate(), true, false, 
					I18nProperties.getValidationError(Validations.beforeDateSoft, onsetDateField.getCaption(), I18nProperties.getPrefixCaption(HospitalizationDto.I18N_PREFIX, HospitalizationDto.ADMISSION_DATE))));
			onsetDateField.setInvalidCommitted(true);
		}

		ComboBox temperature = addField(SymptomsDto.TEMPERATURE, ComboBox.class);
		for (Float temperatureValue : SymptomsHelper.getTemperatureValues()) {
			temperature.addItem(temperatureValue);
			temperature.setItemCaption(temperatureValue, SymptomsHelper.getTemperatureString(temperatureValue));
		}
		if (symptomsContext == SymptomsContext.CASE) {
			temperature.setCaption(I18nProperties.getCaption(Captions.symptomsMaxTemperature));
		}
		addField(SymptomsDto.TEMPERATURE_SOURCE);

		ComboBox bloodPressureSystolic = addField(SymptomsDto.BLOOD_PRESSURE_SYSTOLIC, ComboBox.class);
		bloodPressureSystolic.addItems(SymptomsHelper.getBloodPressureValues());
		ComboBox bloodPressureDiastolic = addField(SymptomsDto.BLOOD_PRESSURE_DIASTOLIC, ComboBox.class);
		bloodPressureDiastolic.addItems(SymptomsHelper.getBloodPressureValues());
		ComboBox heartRate = addField(SymptomsDto.HEART_RATE, ComboBox.class);
		heartRate.addItems(SymptomsHelper.getHeartRateValues());
		ComboBox respiratoryRate = addField(SymptomsDto.RESPIRATORY_RATE, ComboBox.class);
		respiratoryRate.addItems(SymptomsHelper.getRespiratoryRateValues());
		ComboBox weight = addField(SymptomsDto.WEIGHT, ComboBox.class);
		for (Integer weightValue : SymptomsHelper.getWeightValues()) {
			weight.addItem(weightValue);
			weight.setItemCaption(weightValue, SymptomsHelper.getDecimalString(weightValue));
		}
		ComboBox height = addField(SymptomsDto.HEIGHT, ComboBox.class);
		height.addItems(SymptomsHelper.getHeightValues());
		ComboBox midUpperArmCircumference = addField(SymptomsDto.MID_UPPER_ARM_CIRCUMFERENCE, ComboBox.class);
		for (Integer circumferenceValue : SymptomsHelper.getMidUpperArmCircumferenceValues()) {
			midUpperArmCircumference.addItem(circumferenceValue);
			midUpperArmCircumference.setItemCaption(circumferenceValue, SymptomsHelper.getDecimalString(circumferenceValue));
		}
		ComboBox glasgowComaScale = addField(SymptomsDto.GLASGOW_COMA_SCALE, ComboBox.class);
		glasgowComaScale.addItems(SymptomsHelper.getGlasgowComaScaleValues());

		addFields(SymptomsDto.FEVER, SymptomsDto.VOMITING, SymptomsDto.DIARRHEA, SymptomsDto.BLOOD_IN_STOOL, SymptomsDto.NAUSEA, 
				SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE, SymptomsDto.MUSCLE_PAIN, SymptomsDto.FATIGUE_WEAKNESS, SymptomsDto.SKIN_RASH, 
				SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, SymptomsDto.COUGH, SymptomsDto.RUNNY_NOSE, 
				SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.CHEST_PAIN, SymptomsDto.CONJUNCTIVITIS, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.KOPLIKS_SPOTS,
				SymptomsDto.THROBOCYTOPENIA, SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS, SymptomsDto.DEHYDRATION,
				SymptomsDto.ANOREXIA_APPETITE_LOSS, 
				SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN, SymptomsDto.HICCUPS, SymptomsDto.BACKACHE, SymptomsDto.EYES_BLEEDING,
				SymptomsDto.JAUNDICE, SymptomsDto.DARK_URINE, SymptomsDto.STOMACH_BLEEDING, SymptomsDto.RAPID_BREATHING, SymptomsDto.SWOLLEN_GLANDS, SymptomsDto.SYMPTOMS_COMMENTS,
				SymptomsDto.UNEXPLAINED_BLEEDING, SymptomsDto.GUMS_BLEEDING, SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, 
				SymptomsDto.BLOODY_BLACK_STOOL, SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.COUGHING_BLOOD,
				SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING, SymptomsDto.BLOOD_URINE, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT, 
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT, SymptomsDto.LESIONS, SymptomsDto.LESIONS_THAT_ITCH,
				SymptomsDto.LESIONS_SAME_STATE, SymptomsDto.LESIONS_SAME_SIZE, SymptomsDto.LESIONS_DEEP_PROFOUND, SymptomsDto.LESIONS_FACE, SymptomsDto.LESIONS_LEGS,
				SymptomsDto.LESIONS_SOLES_FEET, SymptomsDto.LESIONS_PALMS_HANDS, SymptomsDto.LESIONS_THORAX, SymptomsDto.LESIONS_ARMS, SymptomsDto.LESIONS_GENITALS, SymptomsDto.LESIONS_ALL_OVER_BODY,  
				SymptomsDto.LYMPHADENOPATHY_AXILLARY, SymptomsDto.LYMPHADENOPATHY_CERVICAL, SymptomsDto.LYMPHADENOPATHY_INGUINAL,  
				SymptomsDto.CHILLS_SWEATS, SymptomsDto.BEDRIDDEN, SymptomsDto.ORAL_ULCERS, SymptomsDto.PAINFUL_LYMPHADENITIS, SymptomsDto.BLACKENING_DEATH_OF_TISSUE, SymptomsDto.BUBOES_GROIN_ARMPIT_NECK, 
				SymptomsDto.BULGING_FONTANELLE, SymptomsDto.PHARYNGEAL_ERYTHEMA, SymptomsDto.PHARYNGEAL_EXUDATE, SymptomsDto.OEDEMA_FACE_NECK, SymptomsDto.OEDEMA_LOWER_EXTREMITY,
				SymptomsDto.LOSS_SKIN_TURGOR, SymptomsDto.PALPABLE_LIVER, SymptomsDto.PALPABLE_SPLEEN, SymptomsDto.MALAISE, SymptomsDto.SUNKEN_EYES_FONTANELLE,
				SymptomsDto.SIDE_PAIN, SymptomsDto.FLUID_IN_LUNG_CAVITY, SymptomsDto.TREMOR, SymptomsDto.BILATERAL_CATARACTS, SymptomsDto.UNILATERAL_CATARACTS,
				SymptomsDto.CONGENITAL_GLAUCOMA, SymptomsDto.CONGENITAL_HEART_DISEASE, SymptomsDto.PIGMENTARY_RETINOPATHY, SymptomsDto.RADIOLUCENT_BONE_DISEASE,
				SymptomsDto.SPLENOMEGALY, SymptomsDto.MICROCEPHALY, SymptomsDto.MENINGOENCEPHALITIS, SymptomsDto.PURPURIC_RASH, SymptomsDto.DEVELOPMENTAL_DELAY, 
				SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE, SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS, SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, SymptomsDto.PATIENT_ILL_LOCATION,
				SymptomsDto.HYDROPHOBIA, SymptomsDto.OPISTHOTONUS, SymptomsDto.ANXIETY_STATES, SymptomsDto.DELIRIUM, SymptomsDto.UPROARIOUSNESS, SymptomsDto.PARASTHESIA_AROUND_WOUND, 
				SymptomsDto.EXCESS_SALIVATION, SymptomsDto.INSOMNIA, SymptomsDto.PARALYSIS, SymptomsDto.EXCITATION, SymptomsDto.DYSPHAGIA, SymptomsDto.AEROPHOBIA, SymptomsDto.HYPERACTIVITY, 
				SymptomsDto.PARESIS, SymptomsDto.AGITATION, SymptomsDto.ASCENDING_FLACCID_PARALYSIS, SymptomsDto.ERRATIC_BEHAVIOUR, SymptomsDto.COMA, SymptomsDto.CONVULSION,
				SymptomsDto.FLUID_IN_LUNG_CAVITY_AUSCULTATION, SymptomsDto.FLUID_IN_LUNG_CAVITY_XRAY, SymptomsDto.ABNORMAL_LUNG_XRAY_FINDINGS, SymptomsDto.CONJUNCTIVAL_INJECTION,
				SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME, SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC,
				SymptomsDto.LOSS_OF_TASTE, SymptomsDto.LOSS_OF_SMELL);
		addField(SymptomsDto.LESIONS_ONSET_DATE, DateField.class);

		// complications
		addFields(SymptomsDto.ALTERED_CONSCIOUSNESS, SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.HEMORRHAGIC_SYNDROME,
				SymptomsDto.HYPERGLYCEMIA, SymptomsDto.HYPOGLYCEMIA, SymptomsDto.MENINGEAL_SIGNS,
				SymptomsDto.SEIZURES, SymptomsDto.SEPSIS, SymptomsDto.SHOCK); 

		monkeypoxImageFieldIds = Arrays.asList(SymptomsDto.LESIONS_RESEMBLE_IMG1, SymptomsDto.LESIONS_RESEMBLE_IMG2, SymptomsDto.LESIONS_RESEMBLE_IMG3, SymptomsDto.LESIONS_RESEMBLE_IMG4);
		for (String propertyId : monkeypoxImageFieldIds) {
			@SuppressWarnings("rawtypes")
			Field monkeypoxImageField = addField(propertyId);
			CssStyles.style(monkeypoxImageField, VSPACE_NONE);
		}

		// Set initial visibilities

		initializeVisibilitiesAndAllowedVisibilities(disease, viewMode);

		if (symptomsContext != SymptomsContext.CLINICAL_VISIT) {
			setVisible(false, SymptomsDto.BLOOD_PRESSURE_SYSTOLIC, SymptomsDto.BLOOD_PRESSURE_DIASTOLIC, SymptomsDto.HEART_RATE,
					SymptomsDto.RESPIRATORY_RATE, SymptomsDto.WEIGHT, SymptomsDto.HEIGHT, SymptomsDto.MID_UPPER_ARM_CIRCUMFERENCE,
					SymptomsDto.GLASGOW_COMA_SCALE);
		} else {
			setVisible(false, SymptomsDto.ONSET_SYMPTOM, SymptomsDto.ONSET_DATE);
		}

		// Initialize lists

		conditionalBleedingSymptomFieldIds = Arrays.asList(SymptomsDto.GUMS_BLEEDING,
				SymptomsDto.INJECTION_SITE_BLEEDING, SymptomsDto.NOSE_BLEEDING, SymptomsDto.BLOODY_BLACK_STOOL,
				SymptomsDto.RED_BLOOD_VOMIT, SymptomsDto.DIGESTED_BLOOD_VOMIT, SymptomsDto.EYES_BLEEDING,
				SymptomsDto.COUGHING_BLOOD, SymptomsDto.BLEEDING_VAGINA, SymptomsDto.SKIN_BRUISING,
				SymptomsDto.STOMACH_BLEEDING, SymptomsDto.BLOOD_URINE, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);
		lesionsFieldIds = Arrays.asList(SymptomsDto.LESIONS_SAME_STATE, SymptomsDto.LESIONS_SAME_SIZE,
				SymptomsDto.LESIONS_DEEP_PROFOUND, SymptomsDto.LESIONS_THAT_ITCH);
		lesionsLocationFieldIds = Arrays.asList(SymptomsDto.LESIONS_FACE, SymptomsDto.LESIONS_LEGS,
				SymptomsDto.LESIONS_SOLES_FEET, SymptomsDto.LESIONS_PALMS_HANDS, SymptomsDto.LESIONS_THORAX,
				SymptomsDto.LESIONS_ARMS, SymptomsDto.LESIONS_GENITALS, SymptomsDto.LESIONS_ALL_OVER_BODY);
		unconditionalSymptomFieldIds = Arrays.asList(SymptomsDto.FEVER, SymptomsDto.ABNORMAL_LUNG_XRAY_FINDINGS,
				SymptomsDto.CONJUNCTIVAL_INJECTION, SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME,
				SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC, SymptomsDto.VOMITING, SymptomsDto.DIARRHEA,
				SymptomsDto.BLOOD_IN_STOOL, SymptomsDto.NAUSEA, SymptomsDto.ABDOMINAL_PAIN, SymptomsDto.HEADACHE,
				SymptomsDto.MUSCLE_PAIN, SymptomsDto.FATIGUE_WEAKNESS, SymptomsDto.SKIN_RASH,
				SymptomsDto.NECK_STIFFNESS, SymptomsDto.SORE_THROAT, SymptomsDto.COUGH, SymptomsDto.RUNNY_NOSE,
				SymptomsDto.DIFFICULTY_BREATHING, SymptomsDto.CHEST_PAIN, SymptomsDto.CONJUNCTIVITIS,
				SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE, SymptomsDto.KOPLIKS_SPOTS, SymptomsDto.THROBOCYTOPENIA,
				SymptomsDto.OTITIS_MEDIA, SymptomsDto.HEARINGLOSS, SymptomsDto.DEHYDRATION,
				SymptomsDto.ANOREXIA_APPETITE_LOSS, SymptomsDto.REFUSAL_FEEDOR_DRINK, SymptomsDto.JOINT_PAIN,
				SymptomsDto.HICCUPS, SymptomsDto.BACKACHE, SymptomsDto.JAUNDICE, SymptomsDto.DARK_URINE,
				SymptomsDto.RAPID_BREATHING, SymptomsDto.SWOLLEN_GLANDS, SymptomsDto.UNEXPLAINED_BLEEDING,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, SymptomsDto.LESIONS, SymptomsDto.LYMPHADENOPATHY_AXILLARY,
				SymptomsDto.LYMPHADENOPATHY_CERVICAL, SymptomsDto.LYMPHADENOPATHY_INGUINAL, SymptomsDto.CHILLS_SWEATS,
				SymptomsDto.BEDRIDDEN, SymptomsDto.ORAL_ULCERS, SymptomsDto.PAINFUL_LYMPHADENITIS,
				SymptomsDto.BLACKENING_DEATH_OF_TISSUE, SymptomsDto.BUBOES_GROIN_ARMPIT_NECK,
				SymptomsDto.BULGING_FONTANELLE, SymptomsDto.PHARYNGEAL_ERYTHEMA, SymptomsDto.PHARYNGEAL_EXUDATE,
				SymptomsDto.OEDEMA_FACE_NECK, SymptomsDto.OEDEMA_LOWER_EXTREMITY, SymptomsDto.LOSS_SKIN_TURGOR,
				SymptomsDto.PALPABLE_LIVER, SymptomsDto.PALPABLE_SPLEEN, SymptomsDto.MALAISE,
				SymptomsDto.SUNKEN_EYES_FONTANELLE, SymptomsDto.SIDE_PAIN, SymptomsDto.FLUID_IN_LUNG_CAVITY,
				SymptomsDto.FLUID_IN_LUNG_CAVITY_AUSCULTATION, SymptomsDto.FLUID_IN_LUNG_CAVITY_XRAY,
				SymptomsDto.TREMOR, SymptomsDto.BILATERAL_CATARACTS, SymptomsDto.UNILATERAL_CATARACTS,
				SymptomsDto.CONGENITAL_GLAUCOMA, SymptomsDto.CONGENITAL_HEART_DISEASE,
				SymptomsDto.RADIOLUCENT_BONE_DISEASE, SymptomsDto.SPLENOMEGALY, SymptomsDto.MICROCEPHALY,
				SymptomsDto.MENINGOENCEPHALITIS, SymptomsDto.DEVELOPMENTAL_DELAY, SymptomsDto.PURPURIC_RASH,
				SymptomsDto.PIGMENTARY_RETINOPATHY, SymptomsDto.CONVULSION, SymptomsDto.AEROPHOBIA,
				SymptomsDto.AGITATION, SymptomsDto.ANXIETY_STATES, SymptomsDto.ASCENDING_FLACCID_PARALYSIS,
				SymptomsDto.COMA, SymptomsDto.DELIRIUM, SymptomsDto.DYSPHAGIA, SymptomsDto.ERRATIC_BEHAVIOUR,
				SymptomsDto.EXCESS_SALIVATION, SymptomsDto.EXCITATION, SymptomsDto.HYDROPHOBIA,
				SymptomsDto.HYPERACTIVITY, SymptomsDto.INSOMNIA, SymptomsDto.OPISTHOTONUS, SymptomsDto.PARALYSIS,
				SymptomsDto.PARASTHESIA_AROUND_WOUND, SymptomsDto.PARESIS, SymptomsDto.UPROARIOUSNESS,
				// complications
				SymptomsDto.ALTERED_CONSCIOUSNESS, SymptomsDto.CONFUSED_DISORIENTED, SymptomsDto.HEMORRHAGIC_SYNDROME,
				SymptomsDto.HYPERGLYCEMIA, SymptomsDto.HYPOGLYCEMIA, SymptomsDto.MENINGEAL_SIGNS,
				SymptomsDto.SEIZURES, SymptomsDto.SEPSIS, SymptomsDto.SHOCK, SymptomsDto.LOSS_OF_TASTE,
				SymptomsDto.LOSS_OF_SMELL);

		// Set visibilities

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				conditionalBleedingSymptomFieldIds,
				SymptomsDto.UNEXPLAINED_BLEEDING,
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT,
				SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT,
				SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				lesionsFieldIds, 
				SymptomsDto.LESIONS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				lesionsLocationFieldIds, 
				SymptomsDto.LESIONS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), 
				SymptomsDto.LESIONS_ONSET_DATE, 
				SymptomsDto.LESIONS, 
				Arrays.asList(SymptomState.YES), true);

		FieldHelper.setVisibleWhen(getFieldGroup(), SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE, SymptomsDto.CONGENITAL_HEART_DISEASE, Arrays.asList(SymptomState.YES), true);
		FieldHelper.setVisibleWhen(getFieldGroup(), SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS, SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE, Arrays.asList(CongenitalHeartDiseaseType.OTHER), true);
		if (isVisibleAllowed(getFieldGroup().getField(SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH))) {
			FieldHelper.setVisibleWhen(getFieldGroup(), SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH, SymptomsDto.JAUNDICE, Arrays.asList(SymptomState.YES), true);
		}

		FieldHelper.addSoftRequiredStyle(getField(SymptomsDto.LESIONS_ONSET_DATE));

		boolean isInfant = person != null && person.getApproximateAge() != null
				&& ((person.getApproximateAge() <= 12 && person.getApproximateAgeType() == ApproximateAgeType.MONTHS)
						|| person.getApproximateAge() <= 1);
		if (!isInfant) {
			getFieldGroup().getField(SymptomsDto.BULGING_FONTANELLE).setVisible(false);
		}

		// Handle visibility of lesions locations caption
		Label lesionsLocationsCaption = new Label(I18nProperties.getCaption(Captions.symptomsLesionsLocations));
		CssStyles.style(lesionsLocationsCaption, VSPACE_3);
		getContent().addComponent(lesionsLocationsCaption, LESIONS_LOCATIONS_LOC);
		getContent().getComponent(LESIONS_LOCATIONS_LOC).setVisible(getFieldGroup().getField(SymptomsDto.LESIONS).getValue() == SymptomState.YES);
		getFieldGroup().getField(SymptomsDto.LESIONS).addValueChangeListener(e -> {
			getContent().getComponent(LESIONS_LOCATIONS_LOC).setVisible(e.getProperty().getValue() == SymptomState.YES);
		});

		// Symptoms hint text
		Label symptomsHint = new Label(I18nProperties.getString(symptomsContext == SymptomsContext.CASE ? Strings.messageSymptomsHint : Strings.messageSymptomsVisitHint), ContentMode.HTML);
		getContent().addComponent(symptomsHint, SYMPTOMS_HINT_LOC);

		if (disease == Disease.MONKEYPOX) {
			setUpMonkeypoxVisibilities();
		}

		if (symptomsContext != SymptomsContext.CASE) {
			getFieldGroup().getField(SymptomsDto.PATIENT_ILL_LOCATION).setVisible(false);
		}

		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS), 
				Arrays.asList(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS), 
				Arrays.asList(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT), Arrays.asList(SymptomState.YES), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.LESIONS), lesionsFieldIds, Arrays.asList(SymptomState.YES), disease);
		FieldHelper.setRequiredWhen(getFieldGroup(), getFieldGroup().getField(SymptomsDto.LESIONS), monkeypoxImageFieldIds, Arrays.asList(SymptomState.YES), disease);

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
		FieldHelper.addSoftRequiredStyleWhen(getFieldGroup(), visitStatus, Arrays.asList(SymptomsDto.TEMPERATURE, SymptomsDto.TEMPERATURE_SOURCE), Arrays.asList(VisitStatus.COOPERATIVE), disease);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_DATE, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), visitStatus);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_SYMPTOM, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), visitStatus);
		getFieldGroup().getField(SymptomsDto.FEVER).addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue() != null) {
					if((Float)(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue()) >= 38.0f) {
						if(value != SymptomState.YES) {
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
		addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_DATE, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), null);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.ONSET_SYMPTOM, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), null);
		addSoftRequiredStyleWhenSymptomaticAndCooperative(getFieldGroup(), SymptomsDto.PATIENT_ILL_LOCATION, unconditionalSymptomFieldIds, Arrays.asList(SymptomState.YES), null);
	}

	private void initializeSymptomRequirementsForClinicalVisit() {
		getFieldGroup().getField(SymptomsDto.FEVER).addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue() != null) {
					if((Float)(getFieldGroup().getField(SymptomsDto.TEMPERATURE).getValue()) >= 38.0f) {
						if(value != SymptomState.YES) {
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
			List<String> sourcePropertyIds, List<Object> sourceValues, OptionGroup visitStatusField) {

		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceField instanceof AbstractField<?>) {
				((AbstractField) sourceField).setImmediate(true);
			}
		}

		// Initialize
		final Field targetField = fieldGroup.getField(targetPropertyId);
		if (!targetField.isVisible()) {
			return;
		}

		if(visitStatusField != null) {
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
		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			sourceField.addValueChangeListener(event -> {
				if(visitStatusField != null) {
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

		if(visitStatusField != null) {
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

		for(Object sourcePropertyId : sourcePropertyIds) {
			Field sourceField = fieldGroup.getField(sourcePropertyId);
			if(sourceValues.contains(sourceField.getValue())) {
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
		allPropertyIds.add(SymptomsDto.LESIONS_THAT_ITCH);

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
				SymptomsDto.LESIONS, 
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
		boolean lesionsSetToYes = getFieldGroup().getField(SymptomsDto.LESIONS).getValue() == SymptomState.YES;
		for (String monkeypoxImage : monkeypoxImages) {
			getContent().getComponent(monkeypoxImage).setVisible(lesionsSetToYes);
		}

		// Set up image visibility listener
		getFieldGroup().getField(SymptomsDto.LESIONS).addValueChangeListener(e -> {
			for (String monkeypoxImage : monkeypoxImages) {
				getContent().getComponent(monkeypoxImage).setVisible(e.getProperty().getValue() == SymptomState.YES);
			}
		});
	}

	public List<String> getUnconditionalSymptomFieldIds() {
		return unconditionalSymptomFieldIds;
	}
}
