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
package de.symeda.sormas.api.symptoms;

import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_GERMANY;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_LUXEMBOURG;
import static de.symeda.sormas.api.CountryHelper.COUNTRY_CODE_SWITZERLAND;
import static de.symeda.sormas.api.Disease.ACUTE_VIRAL_HEPATITIS;
import static de.symeda.sormas.api.Disease.ADENOVIRUS;
import static de.symeda.sormas.api.Disease.AFP;
import static de.symeda.sormas.api.Disease.ANTHRAX;
import static de.symeda.sormas.api.Disease.ARI;
import static de.symeda.sormas.api.Disease.BURULI_ULCER;
import static de.symeda.sormas.api.Disease.CHIKUNGUNYA;
import static de.symeda.sormas.api.Disease.CHOLERA;
import static de.symeda.sormas.api.Disease.CONGENITAL_RUBELLA;
import static de.symeda.sormas.api.Disease.CORONAVIRUS;
import static de.symeda.sormas.api.Disease.CRYPTOSPORIDIOSIS;
import static de.symeda.sormas.api.Disease.CSM;
import static de.symeda.sormas.api.Disease.C_PNEUMONIAE;
import static de.symeda.sormas.api.Disease.DENGUE;
import static de.symeda.sormas.api.Disease.DIARRHEA_BLOOD;
import static de.symeda.sormas.api.Disease.DIARRHEA_DEHYDRATION;
import static de.symeda.sormas.api.Disease.DIPHTHERIA;
import static de.symeda.sormas.api.Disease.ENTEROVIRUS;
import static de.symeda.sormas.api.Disease.EVD;
import static de.symeda.sormas.api.Disease.FHA;
import static de.symeda.sormas.api.Disease.GIARDIASIS;
import static de.symeda.sormas.api.Disease.GONOCOCCAL_INFECTION;
import static de.symeda.sormas.api.Disease.GUINEA_WORM;
import static de.symeda.sormas.api.Disease.HIV;
import static de.symeda.sormas.api.Disease.H_METAPNEUMOVIRUS;
import static de.symeda.sormas.api.Disease.INFLUENZA;
import static de.symeda.sormas.api.Disease.INFLUENZA_A;
import static de.symeda.sormas.api.Disease.INFLUENZA_B;
import static de.symeda.sormas.api.Disease.INVASIVE_MENINGOCOCCAL_INFECTION;
import static de.symeda.sormas.api.Disease.INVASIVE_PNEUMOCOCCAL_INFECTION;
import static de.symeda.sormas.api.Disease.LASSA;
import static de.symeda.sormas.api.Disease.LEPROSY;
import static de.symeda.sormas.api.Disease.LYMPHATIC_FILARIASIS;
import static de.symeda.sormas.api.Disease.MALARIA;
import static de.symeda.sormas.api.Disease.MATERNAL_DEATHS;
import static de.symeda.sormas.api.Disease.MEASLES;
import static de.symeda.sormas.api.Disease.MONKEYPOX;
import static de.symeda.sormas.api.Disease.MUMPS;
import static de.symeda.sormas.api.Disease.M_PNEUMONIAE;
import static de.symeda.sormas.api.Disease.NEONATAL_TETANUS;
import static de.symeda.sormas.api.Disease.NEW_INFLUENZA;
import static de.symeda.sormas.api.Disease.NON_NEONATAL_TETANUS;
import static de.symeda.sormas.api.Disease.ONCHOCERCIASIS;
import static de.symeda.sormas.api.Disease.OTHER;
import static de.symeda.sormas.api.Disease.PARAINFLUENZA_1_4;
import static de.symeda.sormas.api.Disease.PERINATAL_DEATHS;
import static de.symeda.sormas.api.Disease.PERTUSSIS;
import static de.symeda.sormas.api.Disease.PLAGUE;
import static de.symeda.sormas.api.Disease.PNEUMONIA;
import static de.symeda.sormas.api.Disease.POLIO;
import static de.symeda.sormas.api.Disease.POST_IMMUNIZATION_ADVERSE_EVENTS_MILD;
import static de.symeda.sormas.api.Disease.POST_IMMUNIZATION_ADVERSE_EVENTS_SEVERE;
import static de.symeda.sormas.api.Disease.RABIES;
import static de.symeda.sormas.api.Disease.RESPIRATORY_SYNCYTIAL_VIRUS;
import static de.symeda.sormas.api.Disease.RHINOVIRUS;
import static de.symeda.sormas.api.Disease.RUBELLA;
import static de.symeda.sormas.api.Disease.SALMONELLOSIS;
import static de.symeda.sormas.api.Disease.SCHISTOSOMIASIS;
import static de.symeda.sormas.api.Disease.SHIGELLOSIS;
import static de.symeda.sormas.api.Disease.SNAKE_BITE;
import static de.symeda.sormas.api.Disease.SOIL_TRANSMITTED_HELMINTHS;
import static de.symeda.sormas.api.Disease.SYPHILIS;
import static de.symeda.sormas.api.Disease.TRACHOMA;
import static de.symeda.sormas.api.Disease.TRYPANOSOMIASIS;
import static de.symeda.sormas.api.Disease.TUBERCULOSIS;
import static de.symeda.sormas.api.Disease.TYPHOID_FEVER;
import static de.symeda.sormas.api.Disease.UNDEFINED;
import static de.symeda.sormas.api.Disease.UNSPECIFIED_VHF;
import static de.symeda.sormas.api.Disease.WEST_NILE_FEVER;
import static de.symeda.sormas.api.Disease.YAWS_ENDEMIC_SYPHILIS;
import static de.symeda.sormas.api.Disease.YELLOW_FEVER;
import static de.symeda.sormas.api.Disease.YERSINIOSIS;

import java.util.Date;

import javax.validation.constraints.Size;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectionSite;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisInfectiousness;
import de.symeda.sormas.api.symptoms.syphilis.SyphilisStage;
import de.symeda.sormas.api.utils.Complication;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.FieldConstraints;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.SymptomGroup;
import de.symeda.sormas.api.utils.SymptomGrouping;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;

@DependingOnFeatureType(featureType = {
	FeatureType.CASE_SURVEILANCE,
	FeatureType.CONTACT_TRACING })
public class SymptomsDto extends PseudonymizableDto {

	private static final long serialVersionUID = 4146526547904182448L;

	public static final String I18N_PREFIX = "Symptoms";

	// Symptoms
	public static final String ABDOMINAL_PAIN = "abdominalPain";
	public static final String ABNORMAL_LUNG_XRAY_FINDINGS = "abnormalLungXrayFindings";
	public static final String ACUTE_RESPIRATORY_DISTRESS_SYNDROME = "acuteRespiratoryDistressSyndrome";
	public static final String AEROPHOBIA = "aerophobia";
	public static final String AGITATION = "agitation";
	public static final String ANOREXIA_APPETITE_LOSS = "anorexiaAppetiteLoss";
	public static final String ANXIETY_STATES = "anxietyStates";
	public static final String APNOEA = "apnoea";
	public static final String ASCENDING_FLACCID_PARALYSIS = "ascendingFlaccidParalysis";
	public static final String BACKACHE = "backache";
	public static final String BEDRIDDEN = "bedridden";
	public static final String BILATERAL_CATARACTS = "bilateralCataracts";
	public static final String BLACKENING_DEATH_OF_TISSUE = "blackeningDeathOfTissue";
	public static final String BLEEDING_VAGINA = "bleedingVagina";
	public static final String BLOODY_BLACK_STOOL = "bloodyBlackStool";
	public static final String BLOOD_IN_STOOL = "bloodInStool";
	public static final String BLOOD_PRESSURE_DIASTOLIC = "bloodPressureDiastolic";
	public static final String BLOOD_PRESSURE_SYSTOLIC = "bloodPressureSystolic";
	public static final String BLOOD_URINE = "bloodUrine";
	public static final String BRONCHITIS = "bronchitis";
	public static final String BUBOES_GROIN_ARMPIT_NECK = "buboesGroinArmpitNeck";
	public static final String BULGING_FONTANELLE = "bulgingFontanelle";
	public static final String CHEST_PAIN = "chestPain";
	public static final String CHILLS_SWEATS = "chillsSweats";
	public static final String COMA = "coma";
	public static final String CONGENITAL_GLAUCOMA = "congenitalGlaucoma";
	public static final String CONGENITAL_HEART_DISEASE = "congenitalHeartDisease";
	public static final String CONGENITAL_HEART_DISEASE_DETAILS = "congenitalHeartDiseaseDetails";
	public static final String CONGENITAL_HEART_DISEASE_TYPE = "congenitalHeartDiseaseType";
	// #13399 - deprecated, replaced by CONJUNCTIVITIS
	@Deprecated
	public static final String CONJUNCTIVAL_INJECTION = "conjunctivalInjection";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String CONSTIPATION = "constipation";
	// #13399 - deprecated, replaced by SEIZURES
	@Deprecated
	public static final String CONVULSION = "convulsion";
	public static final String COUGH = "cough";
	public static final String COUGHING_BLOOD = "coughingBlood";
	public static final String COUGHING_BOUTS = "coughingBouts";
	public static final String COUGHS_PROVOKE_VOMITING = "coughsProvokeVomiting";
	// #13399 - deprecated, replaced by COUGHING_BLOOD
	@Deprecated
	public static final String COUGH_WITH_HEAMOPTYSIS = "coughWithHeamoptysis";
	public static final String COUGH_WITH_SPUTUM = "coughWithSputum";
	public static final String DARK_URINE = "darkUrine";
	public static final String DEHYDRATION = "dehydration";
	public static final String DELIRIUM = "delirium";
	public static final String DEVELOPMENTAL_DELAY = "developmentalDelay";
	public static final String DIARRHEA = "diarrhea";
	public static final String DIFFICULTY_BREATHING = "difficultyBreathing";
	public static final String DIGESTED_BLOOD_VOMIT = "digestedBloodVomit";
	public static final String DYSPHAGIA = "dysphagia";
	public static final String DYSURIA = "dysuria";
	public static final String ERRATIC_BEHAVIOUR = "erraticBehaviour";
	public static final String EXCESS_SALIVATION = "excessSalivation";
	public static final String EXCITATION = "excitation";
	public static final String EYES_BLEEDING = "eyesBleeding";
	public static final String EYE_IRRITATION = "eyeIrritation";
	public static final String EYE_PAIN_LIGHT_SENSITIVE = "eyePainLightSensitive";
	public static final String FATIGUE_WEAKNESS = "fatigueWeakness";
	public static final String FEVER = "fever";
	public static final String FLUID_IN_LUNG_CAVITY = "fluidInLungCavity";
	public static final String FLUID_IN_LUNG_CAVITY_AUSCULTATION = "fluidInLungCavityAuscultation";
	public static final String FLUID_IN_LUNG_CAVITY_XRAY = "fluidInLungCavityXray";
	public static final String GLASGOW_COMA_SCALE = "glasgowComaScale";
	public static final String GREGGS_TRIAD = "greggsTriad";
	public static final String GUMS_BLEEDING = "gumsBleeding";
	public static final String HEADACHE = "headache";
	public static final String HEARINGLOSS = "hearingloss";
	public static final String HEART_RATE = "heartRate";
	public static final String HEIGHT = "height";
	public static final String HEPATITIS = "hepatitis";
	public static final String HICCUPS = "hiccups";
	public static final String HYDROPHOBIA = "hydrophobia";
	public static final String HYPERACTIVITY = "hyperactivity";
	public static final String INABILITY_TO_WALK = "inabilityToWalk";
	public static final String INJECTION_SITE_BLEEDING = "injectionSiteBleeding";
	public static final String INSOMNIA = "insomnia";
	public static final String IN_DRAWING_OF_CHEST_WALL = "inDrawingOfChestWall";
	public static final String JAUNDICE = "jaundice";
	public static final String JAUNDICE_WITHIN_24_HOURS_OF_BIRTH = "jaundiceWithin24HoursOfBirth";
	// #13399 - deprecated, replaced by ARTHRALGIA
	@Deprecated
	public static final String JOINT_PAIN = "jointPain";
	public static final String KOPLIKS_SPOTS = "kopliksSpots";
	public static final String LESIONS = "lesions";
	public static final String LESIONS_ALL_OVER_BODY = "lesionsAllOverBody";
	public static final String LESIONS_ARMS = "lesionsArms";
	public static final String LESIONS_DEEP_PROFOUND = "lesionsDeepProfound";
	public static final String LESIONS_FACE = "lesionsFace";
	public static final String LESIONS_GENITALS = "lesionsGenitals";
	public static final String LESIONS_LEGS = "lesionsLegs";
	public static final String LESIONS_ONSET_DATE = "lesionsOnsetDate";
	public static final String LESIONS_PALMS_HANDS = "lesionsPalmsHands";
	public static final String LESIONS_RESEMBLE_IMG1 = "lesionsResembleImg1";
	public static final String LESIONS_RESEMBLE_IMG2 = "lesionsResembleImg2";
	public static final String LESIONS_RESEMBLE_IMG3 = "lesionsResembleImg3";
	public static final String LESIONS_RESEMBLE_IMG4 = "lesionsResembleImg4";
	public static final String LESIONS_SAME_SIZE = "lesionsSameSize";
	public static final String LESIONS_SAME_STATE = "lesionsSameState";
	public static final String LESIONS_SOLES_FEET = "lesionsSolesFeet";
	public static final String LESIONS_THAT_ITCH = "lesionsThatItch";
	public static final String LESIONS_THORAX = "lesionsThorax";
	public static final String LOSS_OF_SMELL = "lossOfSmell";
	public static final String LOSS_OF_TASTE = "lossOfTaste";
	public static final String LOSS_SKIN_TURGOR = "lossSkinTurgor";
	public static final String LOW_BIRTH_WEIGHT = "lowBirthWeight";
	public static final String LYMPHADENOPATHY = "lymphadenopathy";
	public static final String LYMPHADENOPATHY_AXILLARY = "lymphadenopathyAxillary";
	public static final String LYMPHADENOPATHY_CERVICAL = "lymphadenopathyCervical";
	public static final String LYMPHADENOPATHY_INGUINAL = "lymphadenopathyInguinal";
	public static final String LYMPHADENOPATHY_RETROAURICULAR = "lymphadenopathyRetroauricular";
	public static final String LYMPHADENOPATHY_SUBOCCIPITAL = "lymphadenopathySuboccipital";
	public static final String MALAISE = "malaise";
	public static final String MENINGOENCEPHALITIS = "meningoencephalitis";
	public static final String MICROCEPHALY = "microcephaly";
	public static final String MID_UPPER_ARM_CIRCUMFERENCE = "midUpperArmCircumference";
	public static final String MUSCLE_PAIN = "musclePain";
	public static final String MYOCARDITIS = "myocarditis";
	public static final String NAUSEA = "nausea";
	public static final String NECK_STIFFNESS = "neckStiffness";
	public static final String NEUROLOGICAL_COMPLICATIONS = "neurologicalComplications";
	public static final String NOSE_BLEEDING = "noseBleeding";
	public static final String OEDEMA_FACE_NECK = "oedemaFaceNeck";
	public static final String OEDEMA_LOWER_EXTREMITY = "oedemaLowerExtremity";
	public static final String ONSET_DATE = "onsetDate";
	public static final String ONSET_SYMPTOM = "onsetSymptom";
	public static final String OPISTHOTONUS = "opisthotonus";
	public static final String ORAL_ULCERS = "oralUlcers";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS = "otherHemorrhagicSymptoms";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS_TEXT = "otherHemorrhagicSymptomsText";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS = "otherNonHemorrhagicSymptoms";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT = "otherNonHemorrhagicSymptomsText";
	public static final String OTITIS_MEDIA = "otitisMedia";
	public static final String PAINFUL_LYMPHADENITIS = "painfulLymphadenitis";
	// #13399 - deprecated, replaced by HEPATOMEGALY
	@Deprecated
	public static final String PALPABLE_LIVER = "palpableLiver";
	// #13399 - deprecated, replaced by SPLENOMEGALY
	@Deprecated
	public static final String PALPABLE_SPLEEN = "palpableSpleen";
	public static final String PARALYSIS = "paralysis";
	public static final String PARASTHESIA_AROUND_WOUND = "paresthesiaAroundWound";
	public static final String PARESIS = "paresis";
	public static final String PATIENT_ILL_LOCATION = "patientIllLocation";
	public static final String PERICARDITIS = "pericarditis";
	public static final String PHARYNGEAL_ERYTHEMA = "pharyngealErythema";
	public static final String PHARYNGEAL_EXUDATE = "pharyngealExudate";
	public static final String PIGMENTARY_RETINOPATHY = "pigmentaryRetinopathy";
	public static final String PNEUMONIA_CLINICAL_OR_RADIOLOGIC = "pneumoniaClinicalOrRadiologic";
	public static final String PURPURIC_RASH = "purpuricRash";
	public static final String RADIOLUCENT_BONE_DISEASE = "radiolucentBoneDisease";
	public static final String RAPID_BREATHING = "rapidBreathing";
	public static final String RED_BLOOD_VOMIT = "redBloodVomit";
	public static final String REFUSAL_FEEDOR_DRINK = "refusalFeedorDrink";
	public static final String RESPIRATORY_RATE = "respiratoryRate";
	public static final String RUBELLA_ARTHRITIS = "rubellaArthritis";
	public static final String RUNNY_NOSE = "runnyNose";
	public static final String SIDE_PAIN = "sidePain";
	public static final String SKIN_BRUISING = "skinBruising";
	public static final String SKIN_RASH = "skinRash";
	public static final String SKIN_RASH_ONSET_DATE = "skinRashOnsetDate";
	public static final String SKIN_ULCERS = "skinUlcers";
	public static final String SORE_THROAT = "soreThroat";
	public static final String SPLENOMEGALY = "splenomegaly";
	public static final String STILLBORN_INFANT = "stillbornInfant";
	public static final String STOMACH_BLEEDING = "stomachBleeding";
	public static final String SUNKEN_EYES_FONTANELLE = "sunkenEyesFontanelle";
	public static final String SWOLLEN_GLANDS = "swollenGlands";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String SYMPTOMS_COMMENTS = "symptomsComments";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String THROBOCYTOPENIA = "throbocytopenia";
	public static final String THROMBOCYTOPENIA = "thrombocytopenia";
	public static final String THROMBOCYTOPENIC_PURPURA = "thrombocytopenicPurpura";
	public static final String TREMOR = "tremor";
	public static final String UNEXPLAINED_BLEEDING = "unexplainedBleeding";
	public static final String UNILATERAL_CATARACTS = "unilateralCataracts";
	public static final String UNKNOWN_CLINICAL_PRESENTATION = "unknownClinicalPresentation";
	public static final String UPROARIOUSNESS = "uproariousness";
	public static final String VOMITING = "vomiting";
	public static final String WATERY_DIARRHEA = "wateryDiarrhea";
	public static final String WHEEZING = "wheezing";
	public static final String WHOOP_SOUND = "whoopSound";
	public static final String NOCTURNAL_COUGH = "nocturnalCough";
	public static final String RESPIRATORY_DISEASE_VENTILATION = "respiratoryDiseaseVentilation";
	// #13399 - deprecated, replaced by MALAISE
	@Deprecated
	public static final String FEELING_ILL = "feelingIll";
	public static final String SHIVERING = "shivering";
	public static final String FAST_HEART_RATE = "fastHeartRate";
	public static final String OXYGEN_SATURATION_LOWER_94 = "oxygenSaturationLower94";

	public static final String WEIGHT = "weight";

	public static final String FEVERISHFEELING = "feverishFeeling";
	public static final String WEAKNESS = "weakness";
	public static final String FATIGUE = "fatigue";
	public static final String COUGH_WITHOUT_SPUTUM = "coughWithoutSputum";
	public static final String BREATHLESSNESS = "breathlessness";
	public static final String CHEST_PRESSURE = "chestPressure";
	public static final String BLUE_LIPS = "blueLips";
	public static final String BLOOD_CIRCULATION_PROBLEMS = "bloodCirculationProblems";
	public static final String PALPITATIONS = "palpitations";
	public static final String DIZZINESS_STANDING_UP = "dizzinessStandingUp";
	public static final String HIGH_OR_LOW_BLOOD_PRESSURE = "highOrLowBloodPressure";
	public static final String URINARY_RETENTION = "urinaryRetention";

	// Complications
	public static final String ALTERED_CONSCIOUSNESS = "alteredConsciousness";
	public static final String CONFUSED_DISORIENTED = "confusedDisoriented";
	public static final String HEMORRHAGIC_SYNDROME = "hemorrhagicSyndrome";
	public static final String HYPERGLYCEMIA = "hyperglycemia";
	public static final String HYPOGLYCEMIA = "hypoglycemia";
	public static final String MENINGEAL_SIGNS = "meningealSigns";
	public static final String OTHER_COMPLICATIONS = "otherComplications";
	public static final String OTHER_COMPLICATIONS_TEXT = "otherComplicationsText";
	public static final String SEIZURES = "seizures";
	public static final String SEPSIS = "sepsis";
	public static final String SHOCK = "shock";
	// #13399 - deprecated, replaced by ENCEPHALITIS
	@Deprecated
	public static final String ACUTE_ENCEPHALITIS = "acuteEncephalitis";
	// clinical
	public static final String ASYMPTOMATIC = "asymptomatic";
	public static final String HEMORRHAGIC_RASH = "hemorrhagicRash";
	public static final String ARTHRITIS = "arthritis";
	public static final String MENINGITIS = "meningitis";
	public static final String SEPTICAEMIA = "septicaemia";
	public static final String UNKNOWN_SYMPTOM = "unknownSymptom";
	public static final String DIFFICULTY_BREATHING_DURING_MEALS = "difficultyBreathingDuringMeals";
	public static final String PARADOXICAL_BREATHING = "paradoxicalBreathing";
	public static final String RESPIRATORY_FATIGUE = "respiratoryFatigue";
	public static final String PARENT_TIME_OFF_WORK = "parentTimeOffWork";
	public static final String TIME_OFF_WORK_DAYS = "timeOffWorkDays";
	public static final String OTHER_CLINICAL_PRESENTATION = "otherClinicalPresentation";
	public static final String OTHER_CLINICAL_PRESENTATION_TEXT = "otherClinicalPresentationText";

	public static final String DIAGNOSIS = "diagnosis";
	public static final String MAJOR_SITE = "majorSite";
	public static final String OTHER_MAJOR_SITE_DETAILS = "otherMajorSiteDetails";
	public static final String MINOR_SITE = "minorSite";
	public static final String OTHER_MINOR_SITE_DETAILS = "otherMinorSiteDetails";
	public static final String DATE_OF_ONSET_KNOWN = "dateOfOnsetKnown";
	public static final String CLINICAL_PRESENTATION_STATUS = "clinicalPresentationStatus";

	// Giardiasis & Cryptosporidiosis symptoms
	public static final String EGGY_BURPS = "eggyBurps";
	public static final String WEIGHT_LOSS = "weightLoss";
	public static final String WEIGHT_LOSS_AMOUNT = "weightLossAmount";
	public static final String BLOATING = "bloating";
	public static final String REOCCURRENCE = "reoccurrence";
	public static final String OVERNIGHT_STAY_REQUIRED = "overnightStayRequired";
	public static final String SYMPTOM_CURRENT_STATUS = "symptomCurrentStatus";
	public static final String DURATION_OF_SYMPTOMS = "durationOfSymptoms";

	// Malaria and Dengue specific symptoms
	public static final String CLAMMY_SKIN = "clammySkin";
	public static final String COLD_SKIN = "coldSkin";
	public static final String ENCEPHALITIS = "encephalitis";
	public static final String GUILLAIN_BARRE_SYNDROME = "guillainBarreSyndrome";
	public static final String LETHARGY = "lethargy";
	// #13399 - deprecated, replaced by CONFUSED_DISORIENTED
	@Deprecated
	public static final String CONFUSION = "confusion";
	public static final String PERSISTENT_VOMITING = "persistentVomiting";
	public static final String RESTLESSNESS = "restlessness";
	public static final String ACUTE_BLEEDING = "acuteBleeding";
	public static final String SEVERE_ORGAN_IMPAIRMENT = "severeOrganImpairment";
	public static final String PLASMA_LEAKAGE_SIGN = "plasmaLeakageSign";
	public static final String POLYDIPSIA = "polydipsia";
	public static final String SYNDROMIC_FLU = "syndromicFlu";
	public static final String ANEMIA = "anemia";
	// #13399 - deprecated, replaced by ALTERED_CONSCIOUSNESS
	@Deprecated
	public static final String ALTERED_LEVEL_OF_CONSCIOUSNESS = "alteredLevelOfConsciousness";
	public static final String SEVERE_ANEMIA = "severeAnemia";
	public static final String ACUTE_KIDNEY_FAILURE = "acuteKidneyFailure";
	public static final String METABOLIC_ACIDOSIS = "metabolicAcidosis";
	public static final String DISSEMINATED_INTRA_VASCULAR_COAGULATION = "disseminatedIntraVascularCoagulation";
	public static final String OFFSET_DATE = "offsetDate";
	public static final String CLINICAL_MANIFESTATION = "clinicalManifestation";
	public static final String CLINICAL_MANIFESTATION_TEXT = "clinicalManifestationText";
	public static final String CEREBRAL_MALARIA = "cerebralMalaria";
	public static final String SCANT_HEMORRHAGE = "scantHemorrhage";
	public static final String OTHER_NEUROLOGICAL_SYMPTOMS = "otherNeurologicalSymptoms";
	public static final String OTHER_NEUROLOGICAL_SYMPTOMS_TEXT = "otherNeurologicalSymptomsText";
	public static final String FATAL_RISK = "fatalRisk";
	public static final String TENESMUS = "tenesmus";
	public static final String BLOODY_DIARRHEA = "bloodyDiarrhea";
	public static final String HAEMOLYTIC_UREMIC_SYNDROME = "haemolyticUremicSyndrome";
	// Mumps specific symptoms
	public static final String SALIVARY_SWELLING = "salivarySwelling";
	public static final String NO_COMPLICATIONS = "noComplications";
	public static final String UNKNOWN_COMPLICATIONS = "unknownComplications";
	public static final String ORCHITIS = "orchitis";
	public static final String PANCREATITIS = "pancreatitis";
	public static final String OTHER_GENERAL_SYMPTOMS = "otherGeneralSymptoms";
	public static final String OTHER_GENERAL_SYMPTOMS_TEXT = "otherGeneralSymptomsText";

	// Diphtheria clinical criteria
	public static final String CONJUNCTIVAL_PRESENTATION = "conjunctivalPresentation";
	public static final String CUTANEOUS_PRESENTATION = "cutaneousPresentation";
	public static final String GENITAL_PRESENTATION = "genitalPresentation";
	public static final String NASAL_DISCHARGE_CLEAR_TO_BLOODY = "nasalDischargeClearToBloody";
	public static final String OTHER_CLINICAL_CRITERIA = "otherClinicalCriteria";
	public static final String OTHER_CLINICAL_CRITERIA_TEXT = "otherClinicalCriteriaText";
	public static final String RESPIRATORY_AND_CUTANEOUS_PRESENTATION = "respiratoryAndCutaneousPresentation";
	public static final String RESPIRATORY_WITH_MEMBRANE = "respiratoryWithMembrane";
	public static final String RESPIRATORY_WITHOUT_MEMBRANE = "respiratoryWithoutMembrane";
	public static final String UNKNOWN_CLINICAL_CRITERIA = "unknownClinicalCriteria";

	// Diphtheria complication diagnosis
	public static final String NO_COMPLICATION_DIAGNOSIS = "noComplicationDiagnosis";
	public static final String CARDIAC_DISORDER = "cardiacDisorder";
	public static final String NEUROLOGICAL_COMPLICATION_DIAGNOSIS = "neurologicalComplicationDiagnosis";
	public static final String NEUROLOGICAL_AND_CARDIAC_DISORDER = "neurologicalAndCardiacDisorder";
	public static final String OTHER_COMPLICATION_DIAGNOSIS = "otherComplicationDiagnosis";
	public static final String OTHER_COMPLICATION_DIAGNOSIS_TEXT = "otherComplicationDiagnosisText";
	public static final String UNKNOWN_COMPLICATION_DIAGNOSIS = "unknownComplicationDiagnosis";

	// Syphilis-specific symptoms
	public static final String PATCHY_ALOPECIA = "patchyAlopecia";
	public static final String CHANCRE = "chancre";
	public static final String CONDYLOMA_VENEREUM = "condylomaVenereum";
	public static final String MUCOUS_PATCHES = "mucousPatches";
	public static final String GENERALIZED_LYMPHADENOPATHY = "generalizedLymphadenopathy";
	public static final String OCULAR_MANIFESTATIONS = "ocularManifestations";
	public static final String NEUROLOGICAL_MANIFESTATIONS = "neurologicalManifestations";
	public static final String CONDYLOMA_LATA = "condylomaLata";
	public static final String BONY_ABNORMALITIES = "bonyAbnormalities";
	public static final String PSEUDOPARALYSIS = "pseudoparalysis";
	public static final String REFRACTORY_RHINITIS = "refractoryRhinitis";
	public static final String CENTRAL_NERVOUS_SYSTEM_DAMAGE = "centralNervousSystemDamage";
	public static final String MALNUTRITION = "malnutrition";
	public static final String NEPHROTIC_SYNDROME = "nephroticSyndrome";
	public static final String SYPHILIS_INFECTION_SITE = "syphilisInfectionSite";
	public static final String SYPHILIS_STAGE = "syphilisStage";
	public static final String SYPHILIS_INFECTIOUSNESS = "syphilisInfectiousness";
	public static final String CLINICAL_CRITERIA_MET = "clinicalCriteriaMet";
	public static final String ARTHRALGIA = "arthralgia";
	public static final String HEPATOMEGALY = "hepatomegaly";
	public static final String HEPATOSPLENOMEGALY = "hepatosplenomegaly";
	public static final String LOW_GRADE_FEVER = "lowGradeFever";
	public static final String MUCOCUTANEOUS_LESION = "mucocutaneousLesion";
	// #13399 - deprecated, replaced by SKIN_RASH
	@Deprecated
	public static final String MACULOPAPULAR_RASH = "maculopapularRash";

	public static final String CERVICITIS = "cervicitis";
	public static final String NEWBORN_CONJUNCTIVITIS = "newbornConjunctivitis";
	public static final String EPIDIDYMITIS = "epididymitis";
	public static final String PELVIC_INFLAMMATORY_DISEASE = "pelvicInflammatoryDisease";
	public static final String PROCTITIS = "proctitis";
	public static final String ACUTE_SALPINGITIS = "acuteSalpingitis";
	public static final String URETHRITIS = "urethritis";
	public static final String GONOCOCCAL_INFECTION_SITE_ANORECTAL = "gonococcalInfectionSiteAnorectal";
	public static final String GONOCOCCAL_INFECTION_SITE_BLOOD = "gonococcalInfectionSiteBlood";
	public static final String GONOCOCCAL_INFECTION_SITE_CEREBROSPINAL_FLUID = "gonococcalInfectionSiteCerebrospinalFluid";
	public static final String GONOCOCCAL_INFECTION_SITE_EYE = "gonococcalInfectionSiteEye";
	public static final String GONOCOCCAL_INFECTION_SITE_GENITAL = "gonococcalInfectionSiteGenital";
	public static final String GONOCOCCAL_INFECTION_SITE_JOINT_FLUID = "gonococcalInfectionSiteJointFluid";
	public static final String GONOCOCCAL_INFECTION_SITE_PHARYNGEAL = "gonococcalInfectionSitePharyngeal";
	public static final String GONOCOCCAL_INFECTION_SITE_UNKNOWN = "gonococcalInfectionSiteUnknown";
	public static final String GONOCOCCAL_INFECTION_SITE_OTHER = "gonococcalInfectionSiteOther";
	public static final String GONOCOCCAL_INFECTION_SITE_OTHER_TEXT = "gonococcalInfectionSiteOtherText";
	public static final String NO_CONCURRENT_STI = "noConcurrentSti";
	public static final String CONCURRENT_STI_CHLAMYDIA = "concurrentStiChlamydia";
	public static final String CONCURRENT_STI_GENITAL_HERPES = "concurrentStiGenitalHerpes";
	public static final String CONCURRENT_STI_LGV = "concurrentStiLgv";
	public static final String CONCURRENT_STI_MYCOPLASMA_GENITALIUM = "concurrentStiMycoplasmaGenitalium";
	public static final String CONCURRENT_STI_INFECTIOUS_SYPHILIS = "concurrentStiInfectiousSyphilis";
	public static final String CONCURRENT_STI_TRICHOMONAS_VAGINALIS = "concurrentStiTrichomonasVaginalis";
	public static final String CONCURRENT_STI_GENITAL_WARTS = "concurrentStiGenitalWarts";
	public static final String CONCURRENT_STI_OTHER = "concurrentStiOther";
	public static final String CONCURRENT_STI_OTHER_TEXT = "concurrentStiOtherText";
	public static final String CONCURRENT_STI_UNKNOWN = "concurrentStiUnknown";

	// Yersiniosis symptoms
	public static final String PSEUDO_APPENDICULAR_SYNDROME = "pseudoAppendicularSyndrome";
	public static final String NECROTIZING_ENTEROCOLITIS = "necrotizingEnterocolitis";
	public static final String REACTIVE_ARTHRITIS = "reactiveArthritis";
	public static final String ERYTHEMA_NODOSUM = "erythemaNodosum";
	public static final String SYMPTOM_END_DATE = "symptomEndDate";

	// Fields are declared in the order they should appear in the import template

	public static SymptomsDto build() {
		SymptomsDto symptoms = new SymptomsDto();
		symptoms.setUuid(DataHelper.createUuid());
		return symptoms;
	}

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CHOLERA,
		YELLOW_FEVER,
		DENGUE,
		PLAGUE,
		POLIO,
		ANTHRAX,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		SALMONELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		SHIGELLOSIS,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		DENGUE })
	private SymptomState abdominalPain;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		CSM,
		CHOLERA,
		POLIO,
		YELLOW_FEVER,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		UNSPECIFIED_VHF,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		SALMONELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState anorexiaAppetiteLoss;

	@Diseases({
		AFP,
		GUINEA_WORM,
		POLIO,
		YELLOW_FEVER,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState backache;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState bedridden;

	@Diseases({
		AFP,
		GUINEA_WORM,
		PLAGUE,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState blackeningDeathOfTissue;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState bleedingVagina;

	@Diseases({
		AFP,
		CHOLERA,
		GUINEA_WORM,
		POLIO,
		YELLOW_FEVER,
		ANTHRAX,
		DENGUE,
		UNSPECIFIED_VHF,
		SALMONELLOSIS,
		UNDEFINED,
		OTHER })
	@Complication(value = {
		DENGUE })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState bloodInStool;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer bloodPressureDiastolic;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer bloodPressureSystolic;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.URINARY)
	private SymptomState bloodUrine;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState bloodyBlackStool;

	@Diseases({
		AFP,
		GUINEA_WORM,
		PLAGUE,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState buboesGroinArmpitNeck;

	@Diseases({
		AFP,
		CSM,
		GUINEA_WORM,
		POLIO,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState bulgingFontanelle;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		PLAGUE,
		POLIO,
		ANTHRAX,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState chestPain;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		PLAGUE,
		ANTHRAX,
		POLIO,
		CORONAVIRUS,
		MALARIA,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState chillsSweats;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		MEASLES,
		MONKEYPOX,
		POLIO,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER,
		RUBELLA,
		CORONAVIRUS }) // CORONAVIRUS taken over from conjunctivalInjection
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState conjunctivitis;

	@Diseases({
		SALMONELLOSIS })
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState constipation;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		MEASLES,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		ANTHRAX,
		UNSPECIFIED_VHF,
		CORONAVIRUS,
		PERTUSSIS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState cough;

	@Diseases({
		CORONAVIRUS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState coughWithSputum;

	// #13399 - deprecated, replaced by coughingBlood. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState coughWithHeamoptysis;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER,
		CORONAVIRUS }) // CORONAVIRUS taken over from coughWithHeamoptysis
	@Outbreaks
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState coughingBlood;

	@Diseases({
		AFP,
		GUINEA_WORM,
		POLIO,
		YELLOW_FEVER,
		MALARIA,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.URINARY)
	private SymptomState darkUrine;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		CHOLERA,
		POLIO,
		UNSPECIFIED_VHF,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		SALMONELLOSIS,
		SHIGELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	@Complication({
		SHIGELLOSIS,
		YERSINIOSIS })
	private SymptomState dehydration;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		PLAGUE,
		POLIO,
		ANTHRAX,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		SALMONELLOSIS,
		SHIGELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Complication({
		MEASLES })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState diarrhea;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		MEASLES,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState difficultyBreathing;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState digestedBloodVomit;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		MEASLES,
		DENGUE,
		MONKEYPOX,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState eyePainLightSensitive;

	@Diseases({
		AFP,
		GUINEA_WORM,
		POLIO,
		YELLOW_FEVER,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState eyesBleeding;

	@Diseases({
		SALMONELLOSIS })
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState eyeIrritation;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState fatigueWeakness;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		UNDEFINED,
		PERTUSSIS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		SALMONELLOSIS,
		SHIGELLOSIS,
		SYPHILIS,
		YERSINIOSIS,
		MUMPS,
		OTHER,
		RUBELLA })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState fever;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState fluidInLungCavity;

	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private Integer glasgowComaScale;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		YELLOW_FEVER,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Complication({
		DENGUE })
	private SymptomState gumsBleeding;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		MALARIA,
		SALMONELLOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState headache;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState hearingloss;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer heartRate;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer height;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState hiccups;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState injectionSiteBleeding;

	@Diseases({
		AFP,
		GUINEA_WORM,
		YELLOW_FEVER,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		MALARIA,
		DENGUE,
		SYPHILIS,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		DENGUE,
		SYPHILIS })
	private SymptomState jaundice;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.METABOLIC)
	@DependantOn(JAUNDICE)
	private YesNoUnknown jaundiceWithin24HoursOfBirth;

	// #13399 - deprecated, replaced by arthralgia. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState jointPain;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MEASLES,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState kopliksSpots;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		ANTHRAX,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	/** Vesiculopustular rash */
	private SymptomState lesions;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsAllOverBody;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		ANTHRAX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsArms;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		ANTHRAX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsDeepProfound;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsFace;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsGenitals;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsLegs;

	@Diseases({
		MONKEYPOX,
		ANTHRAX })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Date lesionsOnsetDate;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsPalmsHands;

	@Diseases({
		MONKEYPOX })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsResembleImg1;
	@Diseases({
		MONKEYPOX })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsResembleImg2;

	@Diseases({
		MONKEYPOX })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsResembleImg3;

	@Diseases({
		MONKEYPOX })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsResembleImg4;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsSameSize;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsSameState;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsSolesFeet;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState lesionsThatItch;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@DependantOn(LESIONS)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private Boolean lesionsThorax;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState lossSkinTurgor;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		MEASLES,
		DENGUE,
		SYPHILIS,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathy;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathyAxillary;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER,
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathyCervical;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathyInguinal;

	@Diseases({
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	@Complication({
		RUBELLA })
	private SymptomState bronchitis;

	@Diseases({
		RUBELLA,
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@Complication({
		RUBELLA,
		CONGENITAL_RUBELLA })
	private SymptomState myocarditis;

	@Diseases({
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@Complication({
		RUBELLA })
	private SymptomState pericarditis;

	@Diseases({
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Complication({
		RUBELLA })
	private SymptomState thrombocytopenia;

	@Diseases({
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathySuboccipital;

	@Diseases({
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState lymphadenopathyRetroauricular;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		SYPHILIS,
		OTHER,
		CORONAVIRUS }) // CORONAVIRUS taken over from feelingIll
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState malaise;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer midUpperArmCircumference;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		MALARIA,
		SALMONELLOSIS,
		UNDEFINED,
		SYPHILIS,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState musclePain;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		UNSPECIFIED_VHF,
		POLIO,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		CRYPTOSPORIDIOSIS,
		GIARDIASIS,
		MALARIA,
		SALMONELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState nausea;

	@Diseases({
		AFP,
		CSM,
		GUINEA_WORM,
		POLIO,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState neckStiffness;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		YELLOW_FEVER,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Complication({
		DENGUE })
	private SymptomState noseBleeding;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState oedemaFaceNeck;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState oedemaLowerExtremity;

	@Diseases({
		AFP,
		CHOLERA,
		CONGENITAL_RUBELLA,
		CSM,
		DENGUE,
		EVD,
		GUINEA_WORM,
		LASSA,
		MEASLES,
		MONKEYPOX,
		NEW_INFLUENZA,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		WEST_NILE_FEVER,
		YELLOW_FEVER,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		PNEUMONIA,
		MALARIA,
		TYPHOID_FEVER,
		ACUTE_VIRAL_HEPATITIS,
		NON_NEONATAL_TETANUS,
		HIV,
		SCHISTOSOMIASIS,
		SOIL_TRANSMITTED_HELMINTHS,
		TRYPANOSOMIASIS,
		DIARRHEA_DEHYDRATION,
		DIARRHEA_BLOOD,
		SNAKE_BITE,
		RUBELLA,
		TUBERCULOSIS,
		LEPROSY,
		LYMPHATIC_FILARIASIS,
		BURULI_ULCER,
		NEONATAL_TETANUS,
		ONCHOCERCIASIS,
		OTHER,
		DIPHTHERIA,
		TRACHOMA,
		YAWS_ENDEMIC_SYPHILIS,
		MATERNAL_DEATHS,
		PERINATAL_DEATHS,
		INFLUENZA,
		INFLUENZA_A,
		INFLUENZA_B,
		H_METAPNEUMOVIRUS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		PARAINFLUENZA_1_4,
		ADENOVIRUS,
		RHINOVIRUS,
		ENTEROVIRUS,
		M_PNEUMONIAE,
		C_PNEUMONIAE,
		ARI,
		CHIKUNGUNYA,
		POST_IMMUNIZATION_ADVERSE_EVENTS_MILD,
		POST_IMMUNIZATION_ADVERSE_EVENTS_SEVERE,
		FHA,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		PERTUSSIS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		SALMONELLOSIS,
		SYPHILIS,
		SHIGELLOSIS,
		YERSINIOSIS,
		GONOCOCCAL_INFECTION,
		MUMPS })
	@HideForCountries
	@Outbreaks
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private Date onsetDate;

	@Diseases({
		AFP,
		CHOLERA,
		CONGENITAL_RUBELLA,
		CSM,
		DENGUE,
		EVD,
		GUINEA_WORM,
		LASSA,
		MEASLES,
		MONKEYPOX,
		NEW_INFLUENZA,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		WEST_NILE_FEVER,
		YELLOW_FEVER,
		RABIES,
		ANTHRAX,
		CORONAVIRUS,
		PNEUMONIA,
		MALARIA,
		TYPHOID_FEVER,
		ACUTE_VIRAL_HEPATITIS,
		NON_NEONATAL_TETANUS,
		HIV,
		SCHISTOSOMIASIS,
		SOIL_TRANSMITTED_HELMINTHS,
		TRYPANOSOMIASIS,
		DIARRHEA_DEHYDRATION,
		DIARRHEA_BLOOD,
		SNAKE_BITE,
		RUBELLA,
		TUBERCULOSIS,
		LEPROSY,
		LYMPHATIC_FILARIASIS,
		BURULI_ULCER,
		NEONATAL_TETANUS,
		ONCHOCERCIASIS,
		OTHER,
		DIPHTHERIA,
		TRACHOMA,
		YAWS_ENDEMIC_SYPHILIS,
		MATERNAL_DEATHS,
		PERINATAL_DEATHS,
		INFLUENZA,
		H_METAPNEUMOVIRUS,
		PARAINFLUENZA_1_4,
		ADENOVIRUS,
		RHINOVIRUS,
		ENTEROVIRUS,
		M_PNEUMONIAE,
		C_PNEUMONIAE,
		ARI,
		CHIKUNGUNYA,
		POST_IMMUNIZATION_ADVERSE_EVENTS_MILD,
		POST_IMMUNIZATION_ADVERSE_EVENTS_SEVERE,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		FHA,
		PERTUSSIS,
		SALMONELLOSIS,
		SYPHILIS,
		SHIGELLOSIS,
		YERSINIOSIS,
		MUMPS })
	@HideForCountries
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private String onsetSymptom;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState oralUlcers;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState otherHemorrhagicSymptoms;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(OTHER_HEMORRHAGIC_SYMPTOMS)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SensitiveData
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherHemorrhagicSymptomsText;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		CRYPTOSPORIDIOSIS,
		GIARDIASIS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		POLIO,
		RABIES,
		CORONAVIRUS,
		PERTUSSIS,
		SALMONELLOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState otherNonHemorrhagicSymptoms;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		CORONAVIRUS,
		PERTUSSIS,
		SALMONELLOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@DependantOn(OTHER_NON_HEMORRHAGIC_SYMPTOMS)
	@SensitiveData
	@SymptomGrouping(SymptomGroup.OTHER)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherNonHemorrhagicSymptomsText;

	@Diseases({
		AFP,
		GUINEA_WORM,
		NEW_INFLUENZA,
		MEASLES,
		POLIO,
		UNDEFINED,
		OTHER,
		RUBELLA })
	@Complication({
		MEASLES,
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState otitisMedia;

	@Diseases({
		AFP,
		GUINEA_WORM,
		PLAGUE,
		POLIO,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState painfulLymphadenitis;

	// #13399 - deprecated, replaced by hepatomegaly. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState palpableLiver;

	// #13399 - deprecated, replaced by splenomegaly. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState palpableSpleen;

	@Diseases({
		AFP,
		GUINEA_WORM,
		MONKEYPOX,
		POLIO,
		UNDEFINED,
		OTHER })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String patientIllLocation;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState pharyngealErythema;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState pharyngealExudate;

	@Diseases({
		AFP,
		CORONAVIRUS,
		DENGUE,
		GUINEA_WORM,
		POLIO,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		MALARIA,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	@Complication({
		DENGUE })
	private SymptomState rapidBreathing;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		DENGUE,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		DENGUE })
	private SymptomState redBloodVomit;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		CSM,
		CHOLERA,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState refusalFeedorDrink;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer respiratoryRate;

	@Diseases({
		AFP,
		GUINEA_WORM,
		NEW_INFLUENZA,
		MEASLES,
		POLIO,
		CORONAVIRUS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState runnyNose;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState sidePain;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.SKIN)
	@Complication({
		DENGUE })
	private SymptomState skinBruising;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		MEASLES,
		DENGUE,
		POLIO,
		UNSPECIFIED_VHF,
		SALMONELLOSIS,
		UNDEFINED,
		SYPHILIS,
		OTHER,
		CORONAVIRUS,
		RUBELLA })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState skinRash;

	@Diseases({
		MEASLES })
	@Outbreaks
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SymptomGrouping(SymptomGroup.SKIN)
	@DependantOn(SKIN_RASH)
	private Date skinRashOnsetDate;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		MEASLES,
		MONKEYPOX,
		ANTHRAX,
		POLIO,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		UNDEFINED,
		SYPHILIS,
		GONOCOCCAL_INFECTION,
		OTHER })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState soreThroat;

	@Diseases({
		AFP,
		GUINEA_WORM,
		POLIO,
		YELLOW_FEVER,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState stomachBleeding;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState sunkenEyesFontanelle;

	@Diseases({
		AFP,
		GUINEA_WORM,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState swollenGlands;

	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private Boolean symptomatic;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		CORONAVIRUS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		SALMONELLOSIS,
		SHIGELLOSIS,
		DIPHTHERIA,
		YERSINIOSIS,
		MUMPS,
		UNDEFINED,
		OTHER })
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private String symptomsComments;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		POLIO,
		RABIES,
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Float temperature;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		DENGUE,
		MONKEYPOX,
		PLAGUE,
		UNSPECIFIED_VHF,
		CONGENITAL_RUBELLA,
		POLIO,
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private TemperatureSource temperatureSource;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState throbocytopenia;

	@Diseases({
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState tremor;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState bilateralCataracts;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState unilateralCataracts;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState congenitalGlaucoma;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState pigmentaryRetinopathy;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState purpuricRash;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState microcephaly;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState developmentalDelay;

	@Diseases({
		CONGENITAL_RUBELLA,
		// taken over from palpableSpleen
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		MALARIA,
		UNDEFINED,
		OTHER })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState splenomegaly;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState meningoencephalitis;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	private SymptomState radiolucentBoneDisease;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState congenitalHeartDisease;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@DependantOn(CONGENITAL_HEART_DISEASE)
	private CongenitalHeartDiseaseType congenitalHeartDiseaseType;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SensitiveData
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@DependantOn(CONGENITAL_HEART_DISEASE_TYPE)
	private String congenitalHeartDiseaseDetails;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState stillbornInfant;

	@Diseases({
		CONGENITAL_RUBELLA,
		RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private SymptomState unknownClinicalPresentation;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState neurologicalComplications;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GENERAL)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState lowBirthWeight;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState hepatitis;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.SKIN)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState thrombocytopenicPurpura;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState rubellaArthritis;

	@Diseases({
		CONGENITAL_RUBELLA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@Complication({
		CONGENITAL_RUBELLA })
	private SymptomState greggsTriad;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		YELLOW_FEVER,
		DENGUE,
		PLAGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		POLIO,
		OTHER,
		CORONAVIRUS })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState unexplainedBleeding;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		YELLOW_FEVER,
		MONKEYPOX,
		PLAGUE,
		ANTHRAX,
		UNSPECIFIED_VHF,
		UNDEFINED,
		POLIO,
		CORONAVIRUS,
		CRYPTOSPORIDIOSIS,
		GIARDIASIS,
		MALARIA,
		SALMONELLOSIS,
		DENGUE,
		YERSINIOSIS,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState vomiting;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState hydrophobia;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState opisthotonus;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState anxietyStates;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState delirium;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState uproariousness;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState paresthesiaAroundWound;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState excessSalivation;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState insomnia;

	@Diseases({
		RABIES,
		DENGUE })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE })
	private SymptomState paralysis;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState excitation;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState dysphagia;

	@Diseases({
		SALMONELLOSIS })
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.URINARY)
	private SymptomState dysuria;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState aerophobia;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState hyperactivity;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState paresis;

	@Diseases({
		RABIES,
		MALARIA,
		DENGUE })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	// Irritability and restlessness
	@Complication({
		DENGUE })
	private SymptomState agitation;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState ascendingFlaccidParalysis;

	@Diseases({
		RABIES })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState erraticBehaviour;

	@Diseases({
		RABIES,
		CORONAVIRUS })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState coma;

	// #13399 - deprecated, replaced by seizures. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState convulsion;

	@Diseases({
		PERTUSSIS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState apnoea;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState fluidInLungCavityAuscultation;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState fluidInLungCavityXray;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState abnormalLungXrayFindings;

	// #13399 - deprecated, replaced by conjunctivitis. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState conjunctivalInjection;

	@Diseases({
		PERTUSSIS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState coughingBouts;

	@Diseases({
		PERTUSSIS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState coughsProvokeVomiting;

	@Diseases({
		CORONAVIRUS,
		MALARIA,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	@Complication({
		MALARIA })
	private SymptomState acuteRespiratoryDistressSyndrome;

	@Diseases({
		CORONAVIRUS,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		MEASLES,
		UNDEFINED,
		OTHER })
	@Complication({
		MEASLES })
	@HideForCountries(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState pneumoniaClinicalOrRadiologic;

	@Diseases({
		PERTUSSIS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState whoopSound;

	@Diseases({
		PERTUSSIS })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState nocturnalCough;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState lossOfTaste;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState lossOfSmell;

	@Diseases({
		CORONAVIRUS,
		RESPIRATORY_SYNCYTIAL_VIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState wheezing;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState skinUlcers;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	private SymptomState inabilityToWalk;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountries(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState inDrawingOfChestWall;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState respiratoryDiseaseVentilation;

	// #13399 - deprecated, replaced by malaise. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState feelingIll;

	@Diseases({
		CORONAVIRUS,
		DENGUE,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_GERMANY,
		CountryHelper.COUNTRY_CODE_SWITZERLAND,
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState fastHeartRate;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState oxygenSaturationLower94;

	@SymptomGrouping(SymptomGroup.CLINICAL_MEASUREMENTS)
	private Integer weight;

	// complications

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		UNDEFINED,
		OTHER,
		MALARIA }) // MALARIA taken over from alteredLevelOfConsciousness
	@Outbreaks

	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState alteredConsciousness;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		PLAGUE,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		CORONAVIRUS,
		DENGUE,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE })
	private SymptomState confusedDisoriented;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		YELLOW_FEVER,
		DENGUE,
		PLAGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Outbreaks

	@HideForCountries
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState hemorrhagicSyndrome;

	@Diseases({
		AFP,
		CSM,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@Complication({
		MALARIA })
	@HideForCountries
	@SymptomGrouping(SymptomGroup.METABOLIC)
	private SymptomState hyperglycemia;

	@Diseases({
		AFP,
		CSM,
		GUINEA_WORM,
		LASSA,
		POLIO,
		MALARIA,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.METABOLIC)
	@HideForCountries
	// Hypoglycaemia (low blood sugar)
	@Complication({
		MALARIA })
	private SymptomState hypoglycemia;

	@Diseases({
		AFP,
		CSM,
		GUINEA_WORM,
		LASSA,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		UNDEFINED,
		OTHER })
	@Outbreaks

	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState meningealSigns;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		YELLOW_FEVER,
		DENGUE,
		MALARIA,
		PLAGUE,
		ANTHRAX,
		CORONAVIRUS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MUMPS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER,
		RUBELLA,
		CONGENITAL_RUBELLA })
	@SymptomGrouping(SymptomGroup.OTHER)
	@HideForCountries
	private SymptomState otherComplications;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		YELLOW_FEVER,
		DENGUE,
		MALARIA,
		PLAGUE,
		ANTHRAX,
		CORONAVIRUS,
		CRYPTOSPORIDIOSIS,
		GIARDIASIS,
		MUMPS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER,
		RUBELLA,
		CONGENITAL_RUBELLA })
	@DependantOn(OTHER_COMPLICATIONS)
	@SymptomGrouping(SymptomGroup.OTHER)
	@HideForCountries
	@SensitiveData
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	@Complication({
		DENGUE,
		MALARIA,
		CRYPTOSPORIDIOSIS,
		GIARDIASIS,
		MUMPS,
		YERSINIOSIS,
		RUBELLA,
		CONGENITAL_RUBELLA })
	private String otherComplicationsText;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		POLIO,
		UNSPECIFIED_VHF,
		RABIES,
		CORONAVIRUS,
		MALARIA,
		SHIGELLOSIS,
		UNDEFINED,
		OTHER,
		// taken over from convulsion
		ANTHRAX,
		DENGUE })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		SHIGELLOSIS,
		DENGUE }) // DENGUE taken over from convulsion
	private SymptomState seizures;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		POLIO,
		SHIGELLOSIS,
		UNSPECIFIED_VHF,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@Complication({
		SHIGELLOSIS,
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@HideForCountries
	private SymptomState sepsis;

	@Diseases({
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		NEW_INFLUENZA,
		CSM,
		CHOLERA,
		MEASLES,
		PLAGUE,
		ANTHRAX,
		POLIO,
		UNSPECIFIED_VHF,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		MALARIA,
		DENGUE,
		UNDEFINED,
		OTHER })
	@Outbreaks
	@HideForCountries
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@Complication({
		MALARIA,
		DENGUE })
	private SymptomState shock;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState feverishFeeling;
	@Diseases({
		CORONAVIRUS,
		DENGUE,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_SWITZERLAND,
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState weakness;
	@Diseases({
		CORONAVIRUS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		UNDEFINED,
		OTHER })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState fatigue;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState coughWithoutSputum;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState breathlessness;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState chestPressure;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState blueLips;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState bloodCirculationProblems;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState palpitations;
	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState dizzinessStandingUp;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	private SymptomState highOrLowBloodPressure;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_SWITZERLAND)
	@SymptomGrouping(SymptomGroup.URINARY)
	private SymptomState urinaryRetention;

	@Diseases({
		CORONAVIRUS,
		UNDEFINED,
		OTHER })
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_GERMANY,
		COUNTRY_CODE_SWITZERLAND })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState shivering;

	// Hiding the asymptomatic symptom for all countries of TUBERCULOSIS.
	// For now TB has been implemented for LUX only,
	// When we implement it for other countries, we should consider it, based on symptoms requirements.
	@Diseases(value = {
		TUBERCULOSIS }, hide = true)
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private SymptomState asymptomatic;
	@Diseases({
		INVASIVE_MENINGOCOCCAL_INFECTION })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState hemorrhagicRash;

	@Diseases({
		INVASIVE_MENINGOCOCCAL_INFECTION,
		GONOCOCCAL_INFECTION,
		RUBELLA })
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	@Complication({
		RUBELLA })
	private SymptomState arthritis;

	@Diseases({
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		DENGUE,
		MUMPS })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE,
		MUMPS })
	private SymptomState meningitis;

	@Diseases({
		INVASIVE_PNEUMOCOCCAL_INFECTION })
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState septicaemia;

	@Diseases({
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		MEASLES,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		DENGUE,
		SYPHILIS,
		SHIGELLOSIS,
		GONOCOCCAL_INFECTION,
		CONGENITAL_RUBELLA })
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState otherClinicalPresentation;

	@Diseases({
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		MEASLES,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		DENGUE,
		SYPHILIS,
		SHIGELLOSIS,
		TUBERCULOSIS,
		GONOCOCCAL_INFECTION,
		CONGENITAL_RUBELLA })
	@SymptomGrouping(SymptomGroup.OTHER)
	@DependantOn(OTHER_CLINICAL_PRESENTATION)
	private String otherClinicalPresentationText;

	@Diseases({
		PERTUSSIS,
		INVASIVE_PNEUMOCOCCAL_INFECTION,
		INVASIVE_MENINGOCOCCAL_INFECTION,
		MEASLES,
		DENGUE,
		SYPHILIS,
		MUMPS,
		MALARIA,
		GONOCOCCAL_INFECTION })
	@Complication({
		MEASLES })
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState unknownSymptom;

	// RSV-specific symptoms
	@Diseases(RESPIRATORY_SYNCYTIAL_VIRUS)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState difficultyBreathingDuringMeals;

	@Diseases(RESPIRATORY_SYNCYTIAL_VIRUS)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState paradoxicalBreathing;

	@Diseases(RESPIRATORY_SYNCYTIAL_VIRUS)
	@SymptomGrouping(SymptomGroup.RESPIRATORY)
	private SymptomState respiratoryFatigue;

	@Diseases({
		RESPIRATORY_SYNCYTIAL_VIRUS,
		GIARDIASIS,
		YERSINIOSIS,
		CRYPTOSPORIDIOSIS })
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private YesNoUnknown parentTimeOffWork;

	@Diseases({
		RESPIRATORY_SYNCYTIAL_VIRUS,
		GIARDIASIS,
		YERSINIOSIS,
		CRYPTOSPORIDIOSIS })
	@DependantOn("parentTimeOffWork")
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private Float timeOffWorkDays;

	// #13399 - deprecated, replaced by encephalitis. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState acuteEncephalitis;
	@Diseases({
		GIARDIASIS })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState eggyBurps;

	@Diseases({
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		SYPHILIS,
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState weightLoss;
	@Diseases({
		GIARDIASIS,
		YERSINIOSIS,
		CRYPTOSPORIDIOSIS,
		SYPHILIS })
	@DependantOn(WEIGHT_LOSS)
	@SymptomGrouping(SymptomGroup.GENERAL)
	private Float weightLossAmount;

	@Diseases({
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		DENGUE,
		SHIGELLOSIS,
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private SymptomState symptomCurrentStatus;

	@Diseases({
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		MALARIA,
		YERSINIOSIS,
		SHIGELLOSIS,
		DENGUE })
	@DependantOn(SYMPTOM_CURRENT_STATUS)
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private Integer durationOfSymptoms;

	@Diseases({
		GIARDIASIS,
		CRYPTOSPORIDIOSIS })
	@Complication()
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private SymptomState reoccurrence;
	@Diseases({
		GIARDIASIS,
		YERSINIOSIS,
		CRYPTOSPORIDIOSIS })
	@Complication()
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private SymptomState overnightStayRequired;
	@Diseases({
		YERSINIOSIS,
		GIARDIASIS })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState bloating;
	@SymptomGrouping(SymptomGroup.LOCALISATION)
	private DiagnosisType diagnosis;
	@SymptomGrouping(SymptomGroup.LOCALISATION)
	@DependantOn(DIAGNOSIS)
	private InfectionSite majorSite;
	@SymptomGrouping(SymptomGroup.LOCALISATION)
	@DependantOn(MAJOR_SITE)
	private String otherMajorSiteDetails;
	@SymptomGrouping(SymptomGroup.LOCALISATION)
	private InfectionSite minorSite;
	@SymptomGrouping(SymptomGroup.LOCALISATION)
	@DependantOn(MINOR_SITE)
	private String otherMinorSiteDetails;
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private YesNoUnknown dateOfOnsetKnown;
	@HideForCountriesExcept(countries = CountryHelper.COUNTRY_CODE_LUXEMBOURG)
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@Diseases({
		TUBERCULOSIS })
	private ClinicalPresentationStatus clinicalPresentationStatus;
	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.SKIN)
	@Complication({
		DENGUE })
	private SymptomState clammySkin;
	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.SKIN)
	@Complication({
		DENGUE })
	private SymptomState coldSkin;

	@Diseases({
		DENGUE,
		MUMPS,
		RUBELLA,
		MEASLES,
		CONGENITAL_RUBELLA })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE,
		MUMPS,
		RUBELLA,
		MEASLES,
		CONGENITAL_RUBELLA })
	private SymptomState encephalitis;
	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE })
	private SymptomState guillainBarreSyndrome;

	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE })
	private SymptomState lethargy;

	// #13399 - deprecated, replaced by confusedDisoriented. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState confusion;

	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Complication({
		DENGUE })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState scantHemorrhage;

	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		DENGUE })
	private SymptomState persistentVomiting;

	@Diseases({
		DENGUE })
	@Complication({
		DENGUE })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState restlessness;

	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	@Complication({
		DENGUE })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState acuteBleeding;

	@Diseases({
		DENGUE })

	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@Complication({
		DENGUE })
	private SymptomState severeOrganImpairment;

	@Diseases({
		DENGUE })

	@SymptomGrouping(SymptomGroup.CARDIOVASCULAR)
	@Complication({
		DENGUE })
	private SymptomState plasmaLeakageSign;

	// unusual thirst
	@Diseases({
		DENGUE })
	@Complication({
		DENGUE })
	@SymptomGrouping(SymptomGroup.METABOLIC)
	private SymptomState polydipsia;

	@Diseases({
		MALARIA })
	// Flu-like syndrome
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState syndromicFlu;

	@Diseases({
		MALARIA,
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	private SymptomState anemia;

	// #13399 - deprecated, replaced by alteredConsciousness. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState alteredLevelOfConsciousness;
	@Diseases({
		MALARIA })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	//Severe anaemia, leading to pale skin and weakness
	@Complication({
		MALARIA })
	private SymptomState severeAnemia;

	@Diseases({
		MALARIA })
	@SymptomGrouping(SymptomGroup.METABOLIC)
	@Complication({
		MALARIA })
	private SymptomState metabolicAcidosis;

	@Diseases({
		MALARIA })
	@SymptomGrouping(SymptomGroup.URINARY)
	@Complication({
		MALARIA })
	private SymptomState acuteKidneyFailure;

	@Diseases({
		MALARIA })
	@SymptomGrouping(SymptomGroup.HAEMATOLOGICAL)
	//Disseminated intravascular coagulation (DIC)
	@Complication({
		MALARIA })
	private SymptomState disseminatedIntraVascularCoagulation;

	@Diseases({
		MALARIA,
		DENGUE,
		SHIGELLOSIS })
	@Outbreaks
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private Date offsetDate;

	@Diseases({
		DENGUE,
		MUMPS })
	@Outbreaks
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private ClinicalManifestation clinicalManifestation;

	@Diseases({
		DENGUE })
	@Outbreaks
	@HideForCountriesExcept(countries = {
		CountryHelper.COUNTRY_CODE_LUXEMBOURG })
	@DependantOn(CLINICAL_MANIFESTATION)
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private String clinicalManifestationText;

	@Diseases({
		MALARIA })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		MALARIA })
	private SymptomState cerebralMalaria;

	@Diseases({
		DENGUE,
		RUBELLA })
	@Outbreaks
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_LUXEMBOURG })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE,
		RUBELLA })
	private YesNoUnknown otherNeurologicalSymptoms;

	@Diseases({
		DENGUE,
		RUBELLA })
	@Outbreaks
	@HideForCountriesExcept(countries = {
		COUNTRY_CODE_LUXEMBOURG })
	@DependantOn(OTHER_NEUROLOGICAL_SYMPTOMS)
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	@Complication({
		DENGUE,
		RUBELLA })
	private String otherNeurologicalSymptomsText;

	@Diseases({
		DENGUE })
	@SymptomGrouping(SymptomGroup.GENERAL)
	// mCComplicationsPotentiellementMortelles to map the life threatening complications of dengue
	// but not required to show in the warning signs/complications
	private SymptomState fatalRisk;

	@Diseases({
		SHIGELLOSIS })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState tenesmus;
	@Diseases({
		SHIGELLOSIS })
	@SymptomGrouping(SymptomGroup.URINARY)
	@Complication({
		SHIGELLOSIS })
	private SymptomState haemolyticUremicSyndrome;

	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Diseases(value = {
		SHIGELLOSIS,
		YERSINIOSIS })
	@Complication({
		SHIGELLOSIS })
	private SymptomState bloodyDiarrhea;

	@Diseases({
		CHOLERA,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS,
		SALMONELLOSIS,
		SHIGELLOSIS })
	@Outbreaks
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState wateryDiarrhea;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState patchyAlopecia;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState chancre;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState condylomaVenereum;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState mucousPatches;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.OTHER)
	private SymptomState generalizedLymphadenopathy;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState ocularManifestations;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState neurologicalManifestations;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	private SymptomState bonyAbnormalities;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState pseudoparalysis;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState refractoryRhinitis;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState condylomaLata;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.NERVOUS_SYSTEM)
	private SymptomState centralNervousSystemDamage;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.METABOLIC)
	private SymptomState malnutrition;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.URINARY)
	private SymptomState nephroticSyndrome;

	@Diseases({
		SYPHILIS,
		RUBELLA,
		DENGUE,
		// taken over from jointPain
		AFP,
		EVD,
		GUINEA_WORM,
		LASSA,
		POLIO,
		NEW_INFLUENZA,
		CSM,
		MEASLES,
		CORONAVIRUS,
		UNSPECIFIED_VHF,
		MALARIA,
		SALMONELLOSIS,
		YERSINIOSIS,
		UNDEFINED,
		OTHER })
	@Outbreaks // taken over from jointPain
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	private SymptomState arthralgia;

	@Diseases({
		SYPHILIS,
		CONGENITAL_RUBELLA,
		// taken over from palpableLiver
		AFP,
		GUINEA_WORM,
		LASSA,
		POLIO,
		DENGUE,
		UNSPECIFIED_VHF,
		UNDEFINED,
		OTHER })
	@Complication({
		DENGUE }) // taken over from palpableLiver
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState hepatomegaly;

	@Diseases({
		SYPHILIS,
		CONGENITAL_RUBELLA })
	@Complication({
		CONGENITAL_RUBELLA })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState hepatosplenomegaly;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private SymptomState lowGradeFever;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	private SymptomState mucocutaneousLesion;

	// #13399 - deprecated, replaced by skinRash. Hidden for every disease, but still mapped so that
	// older app versions and REST clients keep working.
	@Deprecated
	@Diseases({})
	private SymptomState maculopapularRash;

	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState cervicitis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.EYE)
	private SymptomState newbornConjunctivitis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState epididymitis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState pelvicInflammatoryDisease;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState proctitis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState acuteSalpingitis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.GENITAL)
	private SymptomState urethritis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteAnorectal;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteBlood;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteCerebrospinalFluid;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteEye;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteGenital;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteJointFluid;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSitePharyngeal;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteUnknown;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private SymptomState gonococcalInfectionSiteOther;
	@Diseases(GONOCOCCAL_INFECTION)
	@DependantOn(GONOCOCCAL_INFECTION_SITE_OTHER)
	@SymptomGrouping(SymptomGroup.SITE_OF_INFECTION)
	private String gonococcalInfectionSiteOtherText;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState noConcurrentSti;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiChlamydia;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiGenitalHerpes;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiLgv;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiMycoplasmaGenitalium;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiInfectiousSyphilis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiTrichomonasVaginalis;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiGenitalWarts;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiOther;
	@Diseases(GONOCOCCAL_INFECTION)
	@DependantOn(CONCURRENT_STI_OTHER)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private String concurrentStiOtherText;
	@Diseases(GONOCOCCAL_INFECTION)
	@SymptomGrouping(SymptomGroup.CONCURRENT_STI)
	private SymptomState concurrentStiUnknown;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	@DependantOn(CHANCRE)
	private SyphilisInfectionSite syphilisInfectionSite;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.STAGE)
	private SyphilisStage syphilisStage;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.STAGE)
	private SyphilisInfectiousness syphilisInfectiousness;

	@Diseases({
		SYPHILIS })
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private YesNoUnknown clinicalCriteriaMet;

	// Yersiniosis-specific symptoms
	@Diseases({
		YERSINIOSIS })
	@Complication()
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	private SymptomState pseudoAppendicularSyndrome;

	@Diseases({
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		YERSINIOSIS })
	private SymptomState necrotizingEnterocolitis;

	@Diseases({
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.MUSCULOSKELETAL)
	@Complication({
		YERSINIOSIS })
	private SymptomState reactiveArthritis;

	@Diseases({
		YERSINIOSIS })
	@SymptomGrouping(SymptomGroup.SKIN)
	@Complication({
		YERSINIOSIS })
	private SymptomState erythemaNodosum;

	@Diseases({
		YERSINIOSIS,
		GIARDIASIS,
		CRYPTOSPORIDIOSIS })
	@HideForCountries
	@Outbreaks
	@SymptomGrouping(SymptomGroup.CLINICAL_ASSESSMENT_AND_COURSE)
	private Date symptomEndDate;

	@Diseases({
		MUMPS })
	@Outbreaks
	@Complication({
		MUMPS })
	@SymptomGrouping(SymptomGroup.ENT)
	private SymptomState salivarySwelling;
	@Diseases({
		MUMPS })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown noComplications;
	@Diseases({
		MUMPS })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@Complication({
		MUMPS })
	private YesNoUnknown unknownComplications;

	@Diseases({
		MUMPS })
	@SymptomGrouping(SymptomGroup.GENITAL)
	@Complication({
		MUMPS })
	private SymptomState orchitis;

	@Diseases({
		MUMPS })
	@SymptomGrouping(SymptomGroup.GASTROINTESTINAL)
	@Complication({
		MUMPS })
	private SymptomState pancreatitis;

	@Diseases({
		MUMPS,
		RUBELLA })
	@SymptomGrouping(SymptomGroup.GENERAL)
	private YesNoUnknown otherGeneralSymptoms;

	@Diseases({
		MUMPS,
		RUBELLA })
	@DependantOn(OTHER_GENERAL_SYMPTOMS)
	@SymptomGrouping(SymptomGroup.GENERAL)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherGeneralSymptomsText;

	// Diphtheria clinical criteria
	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown conjunctivalPresentation;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown cutaneousPresentation;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown genitalPresentation;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown nasalDischargeClearToBloody;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown otherClinicalCriteria;

	@Diseases({
		DIPHTHERIA })
	@DependantOn(OTHER_CLINICAL_CRITERIA)
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherClinicalCriteriaText;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown respiratoryAndCutaneousPresentation;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown respiratoryWithMembrane;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown respiratoryWithoutMembrane;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.CLINICAL_PRESENTATION)
	private YesNoUnknown unknownClinicalCriteria;

	// Diphtheria complication diagnosis
	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown noComplicationDiagnosis;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown cardiacDisorder;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown neurologicalComplicationDiagnosis;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown neurologicalAndCardiacDisorder;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown otherComplicationDiagnosis;

	@Diseases({
		DIPHTHERIA })
	@DependantOn(OTHER_COMPLICATION_DIAGNOSIS)
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	@Size(max = FieldConstraints.CHARACTER_LIMIT_DEFAULT, message = Validations.textTooLong)
	private String otherComplicationDiagnosisText;

	@Diseases({
		DIPHTHERIA })
	@SymptomGrouping(SymptomGroup.SYSTEM_DISORDER)
	private YesNoUnknown unknownComplicationDiagnosis;

	@Order(0)
	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	@Order(1)
	public TemperatureSource getTemperatureSource() {
		return temperatureSource;
	}

	public void setTemperatureSource(TemperatureSource temperatureSource) {
		this.temperatureSource = temperatureSource;
	}

	@Order(20)
	@ImportIgnore
	public Boolean getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	@Order(21)
	public String getSymptomsComments() {
		return symptomsComments;
	}

	public void setSymptomsComments(String symptomsComments) {
		this.symptomsComments = symptomsComments;
	}

	@Order(22)
	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	@Order(23)
	public Date getOffsetDate() {
		return offsetDate;
	}

	public void setOffsetDate(Date offsetDate) {
		this.offsetDate = offsetDate;
	}

	@Order(24)
	public String getOnsetSymptom() {
		return onsetSymptom;
	}

	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
	}

	@Order(25)
	public SymptomState getAsymptomatic() {
		return asymptomatic;
	}

	public void setAsymptomatic(SymptomState asymptomatic) {
		this.asymptomatic = asymptomatic;
	}

	@Order(100)
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}

	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}

	@Order(101)
	public SymptomState getAlteredConsciousness() {
		return alteredConsciousness;
	}

	public void setAlteredConsciousness(SymptomState alteredConsciousness) {
		this.alteredConsciousness = alteredConsciousness;
	}

	@Order(102)
	public SymptomState getAnorexiaAppetiteLoss() {
		return anorexiaAppetiteLoss;
	}

	public void setAnorexiaAppetiteLoss(SymptomState anorexiaAppetiteLoss) {
		this.anorexiaAppetiteLoss = anorexiaAppetiteLoss;
	}

	@Order(103)
	public SymptomState getBackache() {
		return backache;
	}

	public void setBackache(SymptomState backache) {
		this.backache = backache;
	}

	@Order(104)
	public SymptomState getBedridden() {
		return bedridden;
	}

	public void setBedridden(SymptomState bedridden) {
		this.bedridden = bedridden;
	}

	@Order(105)
	public SymptomState getBlackeningDeathOfTissue() {
		return blackeningDeathOfTissue;
	}

	public void setBlackeningDeathOfTissue(SymptomState blackeningDeathOfTissue) {
		this.blackeningDeathOfTissue = blackeningDeathOfTissue;
	}

	@Order(110)
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}

	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}

	@Order(111)
	public SymptomState getBloodInStool() {
		return bloodInStool;
	}

	public void setBloodInStool(SymptomState bloodInStool) {
		this.bloodInStool = bloodInStool;
	}

	@Order(112)
	public SymptomState getBloodUrine() {
		return bloodUrine;
	}

	public void setBloodUrine(SymptomState bloodUrine) {
		this.bloodUrine = bloodUrine;
	}

	@Order(113)
	public SymptomState getBloodyBlackStool() {
		return bloodyBlackStool;
	}

	public void setBloodyBlackStool(SymptomState bloodyBlackStool) {
		this.bloodyBlackStool = bloodyBlackStool;
	}

	@Order(114)
	public SymptomState getBuboesGroinArmpitNeck() {
		return buboesGroinArmpitNeck;
	}

	public void setBuboesGroinArmpitNeck(SymptomState buboesGroinArmpitNeck) {
		this.buboesGroinArmpitNeck = buboesGroinArmpitNeck;
	}

	@Order(115)
	public SymptomState getBulgingFontanelle() {
		return bulgingFontanelle;
	}

	public void setBulgingFontanelle(SymptomState bulgingFontanelle) {
		this.bulgingFontanelle = bulgingFontanelle;
	}

	@Order(116)
	public SymptomState getBilateralCataracts() {
		return bilateralCataracts;
	}

	public void setBilateralCataracts(SymptomState bilateralCataracts) {
		this.bilateralCataracts = bilateralCataracts;
	}

	@Order(117)
	public SymptomState getUnilateralCataracts() {
		return unilateralCataracts;
	}

	public void setUnilateralCataracts(SymptomState unilateralCataracts) {
		this.unilateralCataracts = unilateralCataracts;
	}

	@Order(120)
	public SymptomState getChestPain() {
		return chestPain;
	}

	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}

	@Order(121)
	public SymptomState getChillsSweats() {
		return chillsSweats;
	}

	public void setChillsSweats(SymptomState chillsSweats) {
		this.chillsSweats = chillsSweats;
	}

	@Order(122)
	public SymptomState getConfusedDisoriented() {
		return confusedDisoriented;
	}

	public void setConfusedDisoriented(SymptomState confusedDisoriented) {
		this.confusedDisoriented = confusedDisoriented;
	}

	@Order(123)
	public SymptomState getCongenitalGlaucoma() {
		return congenitalGlaucoma;
	}

	public void setCongenitalGlaucoma(SymptomState congenitalGlaucoma) {
		this.congenitalGlaucoma = congenitalGlaucoma;
	}

	@Order(124)
	public SymptomState getCongenitalHeartDisease() {
		return congenitalHeartDisease;
	}

	public void setCongenitalHeartDisease(SymptomState congenitalHeartDisease) {
		this.congenitalHeartDisease = congenitalHeartDisease;
	}

	@Order(125)
	public CongenitalHeartDiseaseType getCongenitalHeartDiseaseType() {
		return congenitalHeartDiseaseType;
	}

	public void setCongenitalHeartDiseaseType(CongenitalHeartDiseaseType congenitalHeartDiseaseType) {
		this.congenitalHeartDiseaseType = congenitalHeartDiseaseType;
	}

	@Order(126)
	public String getCongenitalHeartDiseaseDetails() {
		return congenitalHeartDiseaseDetails;
	}

	public void setCongenitalHeartDiseaseDetails(String congenitalHeartDiseaseDetails) {
		this.congenitalHeartDiseaseDetails = congenitalHeartDiseaseDetails;
	}

	@Order(127)
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	@Order(128)
	public SymptomState getCough() {
		return cough;
	}

	public void setCough(SymptomState cough) {
		this.cough = cough;
	}

	@Order(129)
	public SymptomState getCoughingBlood() {
		return coughingBlood;
	}

	public void setCoughingBlood(SymptomState coughingBlood) {
		this.coughingBlood = coughingBlood;
	}

	@Order(130)
	public SymptomState getDarkUrine() {
		return darkUrine;
	}

	public void setDarkUrine(SymptomState darkUrine) {
		this.darkUrine = darkUrine;
	}

	@Order(131)
	public SymptomState getDehydration() {
		return dehydration;
	}

	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}

	@Order(132)
	public SymptomState getDevelopmentalDelay() {
		return developmentalDelay;
	}

	public void setDevelopmentalDelay(SymptomState developmentalDelay) {
		this.developmentalDelay = developmentalDelay;
	}

	@Order(133)
	public SymptomState getDiarrhea() {
		return diarrhea;
	}

	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}

	@Order(134)
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}

	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}

	@Order(135)
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}

	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}

	@Order(136)
	public SymptomState getEyePainLightSensitive() {
		return eyePainLightSensitive;
	}

	public void setEyePainLightSensitive(SymptomState eyePainLightSensitive) {
		this.eyePainLightSensitive = eyePainLightSensitive;
	}

	@Order(140)
	public SymptomState getEyesBleeding() {
		return eyesBleeding;
	}

	public void setEyesBleeding(SymptomState eyesBleeding) {
		this.eyesBleeding = eyesBleeding;
	}

	@Order(141)
	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}

	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}

	@Order(142)
	public SymptomState getFever() {
		return fever;
	}

	public void setFever(SymptomState fever) {
		this.fever = fever;
	}

	@Order(143)
	public SymptomState getFluidInLungCavity() {
		return fluidInLungCavity;
	}

	public void setFluidInLungCavity(SymptomState fluidInLungCavity) {
		this.fluidInLungCavity = fluidInLungCavity;
	}

	@Order(144)
	public SymptomState getFluidInLungCavityAuscultation() {
		return fluidInLungCavityAuscultation;
	}

	public void setFluidInLungCavityAuscultation(SymptomState fluidInLungCavityAuscultation) {
		this.fluidInLungCavityAuscultation = fluidInLungCavityAuscultation;
	}

	@Order(145)
	public SymptomState getFluidInLungCavityXray() {
		return fluidInLungCavityXray;
	}

	public void setFluidInLungCavityXray(SymptomState fluidInLungCavityXray) {
		this.fluidInLungCavityXray = fluidInLungCavityXray;
	}

	@Order(146)
	public SymptomState getGumsBleeding() {
		return gumsBleeding;
	}

	public void setGumsBleeding(SymptomState gumsBleeding) {
		this.gumsBleeding = gumsBleeding;
	}

	@Order(147)
	public SymptomState getHeadache() {
		return headache;
	}

	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}

	@Order(150)
	public SymptomState getHearingloss() {
		return hearingloss;
	}

	public void setHearingloss(SymptomState hearingloss) {
		this.hearingloss = hearingloss;
	}

	@Order(151)
	public SymptomState getHemorrhagicSyndrome() {
		return hemorrhagicSyndrome;
	}

	public void setHemorrhagicSyndrome(SymptomState hemorrhagicSyndrome) {
		this.hemorrhagicSyndrome = hemorrhagicSyndrome;
	}

	@Order(152)
	public SymptomState getHiccups() {
		return hiccups;
	}

	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}

	@Order(153)
	public SymptomState getHyperglycemia() {
		return hyperglycemia;
	}

	public void setHyperglycemia(SymptomState hyperglycemia) {
		this.hyperglycemia = hyperglycemia;
	}

	@Order(154)
	public SymptomState getHypoglycemia() {
		return hypoglycemia;
	}

	public void setHypoglycemia(SymptomState hypoglycemia) {
		this.hypoglycemia = hypoglycemia;
	}

	@Order(155)
	public SymptomState getInjectionSiteBleeding() {
		return injectionSiteBleeding;
	}

	public void setInjectionSiteBleeding(SymptomState injectionSiteBleeding) {
		this.injectionSiteBleeding = injectionSiteBleeding;
	}

	@Order(160)
	public SymptomState getJaundice() {
		return jaundice;
	}

	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}

	@Order(161)
	public YesNoUnknown getJaundiceWithin24HoursOfBirth() {
		return jaundiceWithin24HoursOfBirth;
	}

	public void setJaundiceWithin24HoursOfBirth(YesNoUnknown jaundiceWithin24HoursOfBirth) {
		this.jaundiceWithin24HoursOfBirth = jaundiceWithin24HoursOfBirth;
	}

	// #13399 - deprecated, replaced by arthralgia
	@Deprecated
	@Order(162)
	public SymptomState getJointPain() {
		return jointPain;
	}

	@Deprecated
	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}

	@Order(163)
	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}

	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}

	@Order(164)
	public SymptomState getLesions() {
		return lesions;
	}

	public void setLesions(SymptomState lesions) {
		this.lesions = lesions;
	}

	@Order(165)
	public Boolean getLesionsAllOverBody() {
		return lesionsAllOverBody;
	}

	public void setLesionsAllOverBody(Boolean lesionsAllOverBody) {
		this.lesionsAllOverBody = lesionsAllOverBody;
	}

	@Order(166)
	public Boolean getLesionsArms() {
		return lesionsArms;
	}

	public void setLesionsArms(Boolean lesionsArms) {
		this.lesionsArms = lesionsArms;
	}

	@Order(170)
	public SymptomState getLesionsDeepProfound() {
		return lesionsDeepProfound;
	}

	public void setLesionsDeepProfound(SymptomState lesionsDeepProfound) {
		this.lesionsDeepProfound = lesionsDeepProfound;
	}

	@Order(171)
	public Boolean getLesionsFace() {
		return lesionsFace;
	}

	public void setLesionsFace(Boolean lesionsFace) {
		this.lesionsFace = lesionsFace;
	}

	@Order(172)
	public Boolean getLesionsGenitals() {
		return lesionsGenitals;
	}

	public void setLesionsGenitals(Boolean lesionsGenitals) {
		this.lesionsGenitals = lesionsGenitals;
	}

	@Order(173)
	public Boolean getLesionsLegs() {
		return lesionsLegs;
	}

	public void setLesionsLegs(Boolean lesionsLegs) {
		this.lesionsLegs = lesionsLegs;
	}

	@Order(174)
	public Date getLesionsOnsetDate() {
		return lesionsOnsetDate;
	}

	public void setLesionsOnsetDate(Date lesionsOnsetDate) {
		this.lesionsOnsetDate = lesionsOnsetDate;
	}

	@Order(175)
	public Boolean getLesionsPalmsHands() {
		return lesionsPalmsHands;
	}

	public void setLesionsPalmsHands(Boolean lesionsPalmsHands) {
		this.lesionsPalmsHands = lesionsPalmsHands;
	}

	@Order(180)
	public SymptomState getLesionsSameSize() {
		return lesionsSameSize;
	}

	public void setLesionsSameSize(SymptomState lesionsSameSize) {
		this.lesionsSameSize = lesionsSameSize;
	}

	@Order(181)
	public SymptomState getLesionsSameState() {
		return lesionsSameState;
	}

	public void setLesionsSameState(SymptomState lesionsSameState) {
		this.lesionsSameState = lesionsSameState;
	}

	@Order(182)
	public Boolean getLesionsSolesFeet() {
		return lesionsSolesFeet;
	}

	public void setLesionsSolesFeet(Boolean lesionsSolesFeet) {
		this.lesionsSolesFeet = lesionsSolesFeet;
	}

	@Order(183)
	public SymptomState getLesionsThatItch() {
		return lesionsThatItch;
	}

	public void setLesionsThatItch(SymptomState lesionsThatItch) {
		this.lesionsThatItch = lesionsThatItch;
	}

	@Order(184)
	public Boolean getLesionsThorax() {
		return lesionsThorax;
	}

	public void setLesionsThorax(Boolean lesionsThorax) {
		this.lesionsThorax = lesionsThorax;
	}

	@Order(185)
	public SymptomState getLossSkinTurgor() {
		return lossSkinTurgor;
	}

	public void setLossSkinTurgor(SymptomState lossSkinTurgor) {
		this.lossSkinTurgor = lossSkinTurgor;
	}

	@Order(190)
	public SymptomState getLymphadenopathyAxillary() {
		return lymphadenopathyAxillary;
	}

	public void setLymphadenopathyAxillary(SymptomState lymphadenopathyAxillary) {
		this.lymphadenopathyAxillary = lymphadenopathyAxillary;
	}

	@Order(191)
	public SymptomState getLymphadenopathyCervical() {
		return lymphadenopathyCervical;
	}

	public void setLymphadenopathyCervical(SymptomState lymphadenopathyCervical) {
		this.lymphadenopathyCervical = lymphadenopathyCervical;
	}

	@Order(192)
	public SymptomState getLymphadenopathyInguinal() {
		return lymphadenopathyInguinal;
	}

	public void setLymphadenopathyInguinal(SymptomState lymphadenopathyInguinal) {
		this.lymphadenopathyInguinal = lymphadenopathyInguinal;
	}

	@Order(193)
	public SymptomState getMalaise() {
		return malaise;
	}

	public void setMalaise(SymptomState malaise) {
		this.malaise = malaise;
	}

	@Order(194)
	public SymptomState getMeningealSigns() {
		return meningealSigns;
	}

	public void setMeningealSigns(SymptomState meningealSigns) {
		this.meningealSigns = meningealSigns;
	}

	@Order(195)
	public SymptomState getMeningoencephalitis() {
		return meningoencephalitis;
	}

	public void setMeningoencephalitis(SymptomState meningoencephalitis) {
		this.meningoencephalitis = meningoencephalitis;
	}

	@Order(196)
	public SymptomState getMicrocephaly() {
		return microcephaly;
	}

	public void setMicrocephaly(SymptomState microcephaly) {
		this.microcephaly = microcephaly;
	}

	@Order(197)
	public Integer getMidUpperArmCircumference() {
		return midUpperArmCircumference;
	}

	public void setMidUpperArmCircumference(Integer midUpperArmCircumference) {
		this.midUpperArmCircumference = midUpperArmCircumference;
	}

	@Order(200)
	public SymptomState getMusclePain() {
		return musclePain;
	}

	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}

	@Order(201)
	public SymptomState getNausea() {
		return nausea;
	}

	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}

	@Order(202)
	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}

	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}

	@Order(203)
	public SymptomState getNoseBleeding() {
		return noseBleeding;
	}

	public void setNoseBleeding(SymptomState noseBleeding) {
		this.noseBleeding = noseBleeding;
	}

	@Order(204)
	public SymptomState getOedemaFaceNeck() {
		return oedemaFaceNeck;
	}

	public void setOedemaFaceNeck(SymptomState oedemaFaceNeck) {
		this.oedemaFaceNeck = oedemaFaceNeck;
	}

	@Order(205)
	public SymptomState getOedemaLowerExtremity() {
		return oedemaLowerExtremity;
	}

	public void setOedemaLowerExtremity(SymptomState oedemaLowerExtremity) {
		this.oedemaLowerExtremity = oedemaLowerExtremity;
	}

	@Order(210)
	public SymptomState getOralUlcers() {
		return oralUlcers;
	}

	public void setOralUlcers(SymptomState oralUlcers) {
		this.oralUlcers = oralUlcers;
	}

	@Order(211)
	public SymptomState getOtherHemorrhagicSymptoms() {
		return otherHemorrhagicSymptoms;
	}

	public void setOtherHemorrhagicSymptoms(SymptomState otherHemorrhagicSymptoms) {
		this.otherHemorrhagicSymptoms = otherHemorrhagicSymptoms;
	}

	@Order(212)
	public String getOtherHemorrhagicSymptomsText() {
		return otherHemorrhagicSymptomsText;
	}

	public void setOtherHemorrhagicSymptomsText(String otherHemorrhagicSymptomsText) {
		this.otherHemorrhagicSymptomsText = otherHemorrhagicSymptomsText;
	}

	@Order(213)
	public SymptomState getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}

	public void setOtherNonHemorrhagicSymptoms(SymptomState otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}

	@Order(214)
	public String getOtherNonHemorrhagicSymptomsText() {
		return otherNonHemorrhagicSymptomsText;
	}

	public void setOtherNonHemorrhagicSymptomsText(String otherNonHemorrhagicSymptomsText) {
		this.otherNonHemorrhagicSymptomsText = otherNonHemorrhagicSymptomsText;
	}

	@Order(215)
	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}

	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
	}

	@Order(220)
	public SymptomState getPainfulLymphadenitis() {
		return painfulLymphadenitis;
	}

	public void setPainfulLymphadenitis(SymptomState painfulLymphadenitis) {
		this.painfulLymphadenitis = painfulLymphadenitis;
	}

	// #13399 - deprecated, replaced by hepatomegaly
	@Deprecated
	@Order(221)
	public SymptomState getPalpableLiver() {
		return palpableLiver;
	}

	@Deprecated
	public void setPalpableLiver(SymptomState palpableLiver) {
		this.palpableLiver = palpableLiver;
	}

	// #13399 - deprecated, replaced by splenomegaly
	@Deprecated
	@Order(222)
	public SymptomState getPalpableSpleen() {
		return palpableSpleen;
	}

	@Deprecated
	public void setPalpableSpleen(SymptomState palpableSpleen) {
		this.palpableSpleen = palpableSpleen;
	}

	@Order(223)
	public SymptomState getPharyngealErythema() {
		return pharyngealErythema;
	}

	public void setPharyngealErythema(SymptomState pharyngealErythema) {
		this.pharyngealErythema = pharyngealErythema;
	}

	@Order(224)
	public SymptomState getPharyngealExudate() {
		return pharyngealExudate;
	}

	public void setPharyngealExudate(SymptomState pharyngealExudate) {
		this.pharyngealExudate = pharyngealExudate;
	}

	@Order(225)
	public SymptomState getPigmentaryRetinopathy() {
		return pigmentaryRetinopathy;
	}

	public void setPigmentaryRetinopathy(SymptomState pigmentaryRetinopathy) {
		this.pigmentaryRetinopathy = pigmentaryRetinopathy;
	}

	@Order(226)
	public SymptomState getPurpuricRash() {
		return purpuricRash;
	}

	public void setPurpuricRash(SymptomState purpuricRash) {
		this.purpuricRash = purpuricRash;
	}

	@Order(227)
	public SymptomState getRadiolucentBoneDisease() {
		return radiolucentBoneDisease;
	}

	public void setRadiolucentBoneDisease(SymptomState radiolucentBoneDisease) {
		this.radiolucentBoneDisease = radiolucentBoneDisease;
	}

	@Order(228)
	public SymptomState getRapidBreathing() {
		return rapidBreathing;
	}

	public void setRapidBreathing(SymptomState rapidBreathing) {
		this.rapidBreathing = rapidBreathing;
	}

	@Order(230)
	public SymptomState getRedBloodVomit() {
		return redBloodVomit;
	}

	public void setRedBloodVomit(SymptomState redBloodVomit) {
		this.redBloodVomit = redBloodVomit;
	}

	@Order(231)
	public SymptomState getRefusalFeedorDrink() {
		return refusalFeedorDrink;
	}

	public void setRefusalFeedorDrink(SymptomState refusalFeedorDrink) {
		this.refusalFeedorDrink = refusalFeedorDrink;
	}

	@Order(232)
	public SymptomState getRunnyNose() {
		return runnyNose;
	}

	public void setRunnyNose(SymptomState runnyNose) {
		this.runnyNose = runnyNose;
	}

	@Order(233)
	public SymptomState getSeizures() {
		return seizures;
	}

	public void setSeizures(SymptomState seizures) {
		this.seizures = seizures;
	}

	@Order(234)
	public SymptomState getSepsis() {
		return sepsis;
	}

	public void setSepsis(SymptomState sepsis) {
		this.sepsis = sepsis;
	}

	@Order(235)
	public SymptomState getShock() {
		return shock;
	}

	public void setShock(SymptomState shock) {
		this.shock = shock;
	}

	@Order(240)
	public SymptomState getSidePain() {
		return sidePain;
	}

	public void setSidePain(SymptomState sidePain) {
		this.sidePain = sidePain;
	}

	@Order(241)
	public SymptomState getSkinBruising() {
		return skinBruising;
	}

	public void setSkinBruising(SymptomState skinBruising) {
		this.skinBruising = skinBruising;
	}

	@Order(242)
	public SymptomState getSkinRash() {
		return skinRash;
	}

	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}

	@Order(243)
	public SymptomState getSoreThroat() {
		return soreThroat;
	}

	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}

	@Order(244)
	public SymptomState getSplenomegaly() {
		return splenomegaly;
	}

	public void setSplenomegaly(SymptomState splenomegaly) {
		this.splenomegaly = splenomegaly;
	}

	@Order(245)
	public SymptomState getStomachBleeding() {
		return stomachBleeding;
	}

	public void setStomachBleeding(SymptomState stomachBleeding) {
		this.stomachBleeding = stomachBleeding;
	}

	@Order(246)
	public SymptomState getSunkenEyesFontanelle() {
		return sunkenEyesFontanelle;
	}

	public void setSunkenEyesFontanelle(SymptomState sunkenEyesFontanelle) {
		this.sunkenEyesFontanelle = sunkenEyesFontanelle;
	}

	@Order(250)
	public SymptomState getSwollenGlands() {
		return swollenGlands;
	}

	public void setSwollenGlands(SymptomState swollenGlands) {
		this.swollenGlands = swollenGlands;
	}

	@Order(251)
	public SymptomState getThrobocytopenia() {
		return throbocytopenia;
	}

	public void setThrobocytopenia(SymptomState throbocytopenia) {
		this.throbocytopenia = throbocytopenia;
	}

	@Order(252)
	public SymptomState getTremor() {
		return tremor;
	}

	public void setTremor(SymptomState tremor) {
		this.tremor = tremor;
	}

	@Order(253)
	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}

	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}

	@Order(254)
	public SymptomState getVomiting() {
		return vomiting;
	}

	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
	}

	@Order(260)
	public SymptomState getAbnormalLungXrayFindings() {
		return abnormalLungXrayFindings;
	}

	public void setAbnormalLungXrayFindings(SymptomState abnormalLungXrayFindings) {
		this.abnormalLungXrayFindings = abnormalLungXrayFindings;
	}

	// #13399 - deprecated, replaced by conjunctivitis
	@Deprecated
	@Order(261)
	public SymptomState getConjunctivalInjection() {
		return conjunctivalInjection;
	}

	@Deprecated
	public void setConjunctivalInjection(SymptomState conjunctivalInjection) {
		this.conjunctivalInjection = conjunctivalInjection;
	}

	@Order(262)
	public SymptomState getAcuteRespiratoryDistressSyndrome() {
		return acuteRespiratoryDistressSyndrome;
	}

	public void setAcuteRespiratoryDistressSyndrome(SymptomState acuteRespiratoryDistressSyndrome) {
		this.acuteRespiratoryDistressSyndrome = acuteRespiratoryDistressSyndrome;
	}

	@Order(263)
	public SymptomState getPneumoniaClinicalOrRadiologic() {
		return pneumoniaClinicalOrRadiologic;
	}

	public void setPneumoniaClinicalOrRadiologic(SymptomState pneumoniaClinicalOrRadiologic) {
		this.pneumoniaClinicalOrRadiologic = pneumoniaClinicalOrRadiologic;
	}

	@Order(264)
	public SymptomState getLossOfTaste() {
		return lossOfTaste;
	}

	public void setLossOfTaste(SymptomState lossOfTaste) {
		this.lossOfTaste = lossOfTaste;
	}

	@Order(265)
	public SymptomState getLossOfSmell() {
		return lossOfSmell;
	}

	public void setLossOfSmell(SymptomState lossOfSmell) {
		this.lossOfSmell = lossOfSmell;
	}

	@Order(266)
	public SymptomState getCoughWithSputum() {
		return coughWithSputum;
	}

	public void setCoughWithSputum(SymptomState coughWithSputum) {
		this.coughWithSputum = coughWithSputum;
	}

	// #13399 - deprecated, replaced by coughingBlood
	@Deprecated
	@Order(267)
	public SymptomState getCoughWithHeamoptysis() {
		return coughWithHeamoptysis;
	}

	@Deprecated
	public void setCoughWithHeamoptysis(SymptomState coughWithHeamoptysis) {
		this.coughWithHeamoptysis = coughWithHeamoptysis;
	}

	@Order(268)
	public SymptomState getLymphadenopathy() {
		return lymphadenopathy;
	}

	public void setLymphadenopathy(SymptomState lymphadenopathy) {
		this.lymphadenopathy = lymphadenopathy;
	}

	@Order(269)
	public SymptomState getWheezing() {
		return wheezing;
	}

	public void setWheezing(SymptomState wheezing) {
		this.wheezing = wheezing;
	}

	@Order(270)
	public SymptomState getSkinUlcers() {
		return skinUlcers;
	}

	public void setSkinUlcers(SymptomState skinUlcers) {
		this.skinUlcers = skinUlcers;
	}

	@Order(271)
	public SymptomState getInabilityToWalk() {
		return inabilityToWalk;
	}

	public void setInabilityToWalk(SymptomState inabilityToWalk) {
		this.inabilityToWalk = inabilityToWalk;
	}

	@Order(272)
	public SymptomState getInDrawingOfChestWall() {
		return inDrawingOfChestWall;
	}

	public void setInDrawingOfChestWall(SymptomState inDrawingOfChestWall) {
		this.inDrawingOfChestWall = inDrawingOfChestWall;
	}

	@Order(273)
	public SymptomState getOtherComplications() {
		return otherComplications;
	}

	public void setOtherComplications(SymptomState otherComplications) {
		this.otherComplications = otherComplications;
	}

	@Order(274)
	public String getOtherComplicationsText() {
		return otherComplicationsText;
	}

	public void setOtherComplicationsText(String otherComplicationsText) {
		this.otherComplicationsText = otherComplicationsText;
	}

	@Order(300)
	public SymptomState getRespiratoryDiseaseVentilation() {
		return respiratoryDiseaseVentilation;
	}

	public void setRespiratoryDiseaseVentilation(SymptomState respiratoryDiseaseVentilation) {
		this.respiratoryDiseaseVentilation = respiratoryDiseaseVentilation;
	}

	// #13399 - deprecated, replaced by malaise
	@Deprecated
	@Order(301)
	public SymptomState getFeelingIll() {
		return feelingIll;
	}

	@Deprecated
	public void setFeelingIll(SymptomState feelingIll) {
		this.feelingIll = feelingIll;
	}

	@Order(302)
	public SymptomState getShivering() {
		return shivering;
	}

	public void setShivering(SymptomState shivering) {
		this.shivering = shivering;
	}

	@Order(304)
	public SymptomState getFastHeartRate() {
		return fastHeartRate;
	}

	public void setFastHeartRate(SymptomState fastHeartRate) {
		this.fastHeartRate = fastHeartRate;
	}

	@Order(305)
	public SymptomState getOxygenSaturationLower94() {
		return oxygenSaturationLower94;
	}

	public void setOxygenSaturationLower94(SymptomState oxygenSaturationLower94) {
		this.oxygenSaturationLower94 = oxygenSaturationLower94;
	}

	@Order(310)
	public SymptomState getFeverishFeeling() {
		return feverishFeeling;
	}

	public void setFeverishFeeling(SymptomState feverishFeeling) {
		this.feverishFeeling = feverishFeeling;
	}

	@Order(311)
	public SymptomState getWeakness() {
		return weakness;
	}

	public void setWeakness(SymptomState weakness) {
		this.weakness = weakness;
	}

	@Order(312)
	public SymptomState getFatigue() {
		return fatigue;
	}

	public void setFatigue(SymptomState fatigue) {
		this.fatigue = fatigue;
	}

	@Order(313)
	public SymptomState getCoughWithoutSputum() {
		return coughWithoutSputum;
	}

	public void setCoughWithoutSputum(SymptomState coughWithoutSputum) {
		this.coughWithoutSputum = coughWithoutSputum;
	}

	@Order(314)
	public SymptomState getBreathlessness() {
		return breathlessness;
	}

	public void setBreathlessness(SymptomState breathlessness) {
		this.breathlessness = breathlessness;
	}

	@Order(315)
	public SymptomState getChestPressure() {
		return chestPressure;
	}

	public void setChestPressure(SymptomState chestPressure) {
		this.chestPressure = chestPressure;
	}

	@Order(316)
	public SymptomState getBlueLips() {
		return blueLips;
	}

	public void setBlueLips(SymptomState blueLips) {
		this.blueLips = blueLips;
	}

	@Order(317)
	public SymptomState getBloodCirculationProblems() {
		return bloodCirculationProblems;
	}

	public void setBloodCirculationProblems(SymptomState bloodCirculationProblems) {
		this.bloodCirculationProblems = bloodCirculationProblems;
	}

	@Order(318)
	public SymptomState getDizzinessStandingUp() {
		return dizzinessStandingUp;
	}

	public void setDizzinessStandingUp(SymptomState dizzinessStandingUp) {
		this.dizzinessStandingUp = dizzinessStandingUp;
	}

	@Order(319)
	public SymptomState getHighOrLowBloodPressure() {
		return highOrLowBloodPressure;
	}

	public void setHighOrLowBloodPressure(SymptomState highOrLowBloodPressure) {
		this.highOrLowBloodPressure = highOrLowBloodPressure;
	}

	@Order(320)
	public SymptomState getUrinaryRetention() {
		return urinaryRetention;
	}

	public void setUrinaryRetention(SymptomState urinaryRetention) {
		this.urinaryRetention = urinaryRetention;
	}

	@Order(321)
	public SymptomState getOtherClinicalPresentation() {
		return otherClinicalPresentation;
	}

	public void setOtherClinicalPresentation(SymptomState otherClinicalPresentation) {
		this.otherClinicalPresentation = otherClinicalPresentation;
	}

	@Order(322)
	public String getOtherClinicalPresentationText() {
		return otherClinicalPresentationText;
	}

	public void setOtherClinicalPresentationText(String otherClinicalPresentationText) {
		this.otherClinicalPresentationText = otherClinicalPresentationText;
	}

	@Order(330)
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	// #13399 - deprecated, replaced by seizures
	@Deprecated
	@Order(331)
	public SymptomState getConvulsion() {
		return convulsion;
	}

	@Deprecated
	public void setConvulsion(SymptomState convulsion) {
		this.convulsion = convulsion;
	}

	@Order(332)
	public SymptomState getHydrophobia() {
		return hydrophobia;
	}

	public void setHydrophobia(SymptomState hydrophobia) {
		this.hydrophobia = hydrophobia;
	}

	@Order(333)
	public SymptomState getOpisthotonus() {
		return opisthotonus;
	}

	public void setOpisthotonus(SymptomState opisthotonus) {
		this.opisthotonus = opisthotonus;
	}

	@Order(334)
	public SymptomState getAnxietyStates() {
		return anxietyStates;
	}

	public void setAnxietyStates(SymptomState anxietyStates) {
		this.anxietyStates = anxietyStates;
	}

	@Order(335)
	public SymptomState getDelirium() {
		return delirium;
	}

	public void setDelirium(SymptomState delirium) {
		this.delirium = delirium;
	}

	@Order(336)
	public SymptomState getUproariousness() {
		return uproariousness;
	}

	public void setUproariousness(SymptomState uproariousness) {
		this.uproariousness = uproariousness;
	}

	@Order(337)
	public SymptomState getParesthesiaAroundWound() {
		return paresthesiaAroundWound;
	}

	public void setParesthesiaAroundWound(SymptomState paresthesiaAroundWound) {
		this.paresthesiaAroundWound = paresthesiaAroundWound;
	}

	@Order(338)
	public SymptomState getExcessSalivation() {
		return excessSalivation;
	}

	public void setExcessSalivation(SymptomState excessSalivation) {
		this.excessSalivation = excessSalivation;
	}

	@Order(339)
	public SymptomState getInsomnia() {
		return insomnia;
	}

	public void setInsomnia(SymptomState insomnia) {
		this.insomnia = insomnia;
	}

	@Order(340)
	public SymptomState getParalysis() {
		return paralysis;
	}

	public void setParalysis(SymptomState paralysis) {
		this.paralysis = paralysis;
	}

	@Order(341)
	public SymptomState getExcitation() {
		return excitation;
	}

	public void setExcitation(SymptomState excitation) {
		this.excitation = excitation;
	}

	@Order(342)
	public SymptomState getDysphagia() {
		return dysphagia;
	}

	public void setDysphagia(SymptomState dysphagia) {
		this.dysphagia = dysphagia;
	}

	@Order(343)
	public SymptomState getAerophobia() {
		return aerophobia;
	}

	public void setAerophobia(SymptomState aerophobia) {
		this.aerophobia = aerophobia;
	}

	@Order(344)
	public SymptomState getHyperactivity() {
		return hyperactivity;
	}

	public void setHyperactivity(SymptomState hyperactivity) {
		this.hyperactivity = hyperactivity;
	}

	@Order(345)
	public SymptomState getParesis() {
		return paresis;
	}

	public void setParesis(SymptomState paresis) {
		this.paresis = paresis;
	}

	@Order(346)
	public SymptomState getAgitation() {
		return agitation;
	}

	public void setAgitation(SymptomState agitation) {
		this.agitation = agitation;
	}

	@Order(347)
	public SymptomState getAscendingFlaccidParalysis() {
		return ascendingFlaccidParalysis;
	}

	public void setAscendingFlaccidParalysis(SymptomState ascendingFlaccidParalysis) {
		this.ascendingFlaccidParalysis = ascendingFlaccidParalysis;
	}

	@Order(348)
	public SymptomState getErraticBehaviour() {
		return erraticBehaviour;
	}

	public void setErraticBehaviour(SymptomState erraticBehaviour) {
		this.erraticBehaviour = erraticBehaviour;
	}

	@Order(349)
	public SymptomState getComa() {
		return coma;
	}

	public void setComa(SymptomState coma) {
		this.coma = coma;
	}

	@Order(350)
	public SymptomState getPalpitations() {
		return palpitations;
	}

	public void setPalpitations(SymptomState palpitations) {
		this.palpitations = palpitations;
	}

	@Order(360)
	public SymptomState getApnoea() {
		return apnoea;
	}

	public void setApnoea(SymptomState apnoea) {
		this.apnoea = apnoea;
	}

	@Order(361)
	public SymptomState getWhoopSound() {
		return whoopSound;
	}

	public void setWhoopSound(SymptomState whoopSound) {
		this.whoopSound = whoopSound;
	}

	@Order(362)
	public SymptomState getCoughingBouts() {
		return coughingBouts;
	}

	public void setCoughingBouts(SymptomState coughingBouts) {
		this.coughingBouts = coughingBouts;
	}

	@Order(363)
	public SymptomState getCoughsProvokeVomiting() {
		return coughsProvokeVomiting;
	}

	public void setCoughsProvokeVomiting(SymptomState coughsProvokeVomiting) {
		this.coughsProvokeVomiting = coughsProvokeVomiting;
	}

	@Order(364)
	public SymptomState getPseudoAppendicularSyndrome() {
		return pseudoAppendicularSyndrome;
	}

	public void setPseudoAppendicularSyndrome(SymptomState pseudoAppendicularSyndrome) {
		this.pseudoAppendicularSyndrome = pseudoAppendicularSyndrome;
	}

	@Order(365)
	public SymptomState getNecrotizingEnterocolitis() {
		return necrotizingEnterocolitis;
	}

	public void setNecrotizingEnterocolitis(SymptomState necrotizingEnterocolitis) {
		this.necrotizingEnterocolitis = necrotizingEnterocolitis;
	}

	@Order(366)
	public SymptomState getReactiveArthritis() {
		return reactiveArthritis;
	}

	public void setReactiveArthritis(SymptomState reactiveArthritis) {
		this.reactiveArthritis = reactiveArthritis;
	}

	@Order(367)
	public SymptomState getErythemaNodosum() {
		return erythemaNodosum;
	}

	public void setErythemaNodosum(SymptomState erythemaNodosum) {
		this.erythemaNodosum = erythemaNodosum;
	}

	@Order(368)
	public Date getSymptomEndDate() {
		return symptomEndDate;
	}

	public void setSymptomEndDate(Date symptomEndDate) {
		this.symptomEndDate = symptomEndDate;
	}

	@Order(369)
	public SymptomState getSalivarySwelling() {
		return salivarySwelling;
	}

	public void setSalivarySwelling(SymptomState salivarySwelling) {
		this.salivarySwelling = salivarySwelling;
	}

	@Order(370)
	public SymptomState getWateryDiarrhea() {
		return wateryDiarrhea;
	}

	public void setWateryDiarrhea(SymptomState wateryDiarrhea) {
		this.wateryDiarrhea = wateryDiarrhea;
	}

	@Order(371)
	public YesNoUnknown getNoComplications() {
		return noComplications;
	}

	public void setNoComplications(YesNoUnknown noComplications) {
		this.noComplications = noComplications;
	}

	@Order(372)
	public YesNoUnknown getUnknownComplications() {
		return unknownComplications;
	}

	public void setUnknownComplications(YesNoUnknown unknownComplications) {
		this.unknownComplications = unknownComplications;
	}

	@Order(373)
	public SymptomState getOrchitis() {
		return orchitis;
	}

	public void setOrchitis(SymptomState orchitis) {
		this.orchitis = orchitis;
	}

	@Order(374)
	public SymptomState getPancreatitis() {
		return pancreatitis;
	}

	public void setPancreatitis(SymptomState pancreatitis) {
		this.pancreatitis = pancreatitis;
	}

	@Order(375)
	public YesNoUnknown getOtherGeneralSymptoms() {
		return otherGeneralSymptoms;
	}

	public void setOtherGeneralSymptoms(YesNoUnknown otherGeneralSymptoms) {
		this.otherGeneralSymptoms = otherGeneralSymptoms;
	}

	@Order(376)
	public String getOtherGeneralSymptomsText() {
		return otherGeneralSymptomsText;
	}

	public void setOtherGeneralSymptomsText(String otherGeneralSymptomsText) {
		this.otherGeneralSymptomsText = otherGeneralSymptomsText;
	}

	@Order(378)
	public ClinicalManifestation getClinicalManifestation() {
		return clinicalManifestation;
	}

	public void setClinicalManifestation(ClinicalManifestation clinicalManifestation) {
		this.clinicalManifestation = clinicalManifestation;
	}

	@Order(379)
	public String getClinicalManifestationText() {
		return clinicalManifestationText;
	}

	public void setClinicalManifestationText(String clinicalManifestationText) {
		this.clinicalManifestationText = clinicalManifestationText;
	}

	@ImportIgnore
	@Order(380)
	public String getPatientIllLocation() {
		return patientIllLocation;
	}

	public void setPatientIllLocation(String patientIllLocation) {
		this.patientIllLocation = patientIllLocation;
	}

	@Order(381)
	public Integer getBloodPressureDiastolic() {
		return bloodPressureDiastolic;
	}

	public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}

	@Order(382)
	public Integer getBloodPressureSystolic() {
		return bloodPressureSystolic;
	}

	public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}

	@Order(383)
	public Integer getGlasgowComaScale() {
		return glasgowComaScale;
	}

	public void setGlasgowComaScale(Integer glasgowComaScale) {
		this.glasgowComaScale = glasgowComaScale;
	}

	@Order(384)
	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}

	@Order(385)
	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	@Order(386)
	public SymptomState getLesionsResembleImg1() {
		return lesionsResembleImg1;
	}

	public void setLesionsResembleImg1(SymptomState lesionsResembleImg1) {
		this.lesionsResembleImg1 = lesionsResembleImg1;
	}

	@Order(387)
	public SymptomState getLesionsResembleImg2() {
		return lesionsResembleImg2;
	}

	public void setLesionsResembleImg2(SymptomState lesionsResembleImg2) {
		this.lesionsResembleImg2 = lesionsResembleImg2;
	}

	@Order(388)
	public SymptomState getLesionsResembleImg3() {
		return lesionsResembleImg3;
	}

	public void setLesionsResembleImg3(SymptomState lesionsResembleImg3) {
		this.lesionsResembleImg3 = lesionsResembleImg3;
	}

	@Order(389)
	public SymptomState getLesionsResembleImg4() {
		return lesionsResembleImg4;
	}

	public void setLesionsResembleImg4(SymptomState lesionsResembleImg4) {
		this.lesionsResembleImg4 = lesionsResembleImg4;
	}

	@Order(390)
	public Integer getRespiratoryRate() {
		return respiratoryRate;
	}

	public void setRespiratoryRate(Integer respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}

	@Order(392)
	public SymptomState getHemorrhagicRash() {
		return hemorrhagicRash;
	}

	public void setHemorrhagicRash(SymptomState hemorrhagicRash) {
		this.hemorrhagicRash = hemorrhagicRash;
	}

	@Order(393)
	public SymptomState getArthritis() {
		return arthritis;
	}

	public void setArthritis(SymptomState arthritis) {
		this.arthritis = arthritis;
	}

	@Order(394)
	public SymptomState getMeningitis() {
		return meningitis;
	}

	public void setMeningitis(SymptomState meningitis) {
		this.meningitis = meningitis;
	}

	@Order(395)
	public SymptomState getSepticaemia() {
		return septicaemia;
	}

	public void setSepticaemia(SymptomState septicaemia) {
		this.septicaemia = septicaemia;
	}

	@Order(396)
	public DiagnosisType getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(DiagnosisType diagnosis) {
		this.diagnosis = diagnosis;
	}

	@Order(397)
	public InfectionSite getMajorSite() {
		return majorSite;
	}

	public void setMajorSite(InfectionSite majorSite) {
		this.majorSite = majorSite;
	}

	@Order(398)
	public String getOtherMajorSiteDetails() {
		return otherMajorSiteDetails;
	}

	public void setOtherMajorSiteDetails(String otherMajorSiteDetails) {
		this.otherMajorSiteDetails = otherMajorSiteDetails;
	}

	@Order(399)
	public InfectionSite getMinorSite() {
		return minorSite;
	}

	public void setMinorSite(InfectionSite minorSite) {
		this.minorSite = minorSite;
	}

	@Order(400)
	public String getOtherMinorSiteDetails() {
		return otherMinorSiteDetails;
	}

	public void setOtherMinorSiteDetails(String otherMinorSiteDetails) {
		this.otherMinorSiteDetails = otherMinorSiteDetails;
	}

	@Order(401)
	public YesNoUnknown getDateOfOnsetKnown() {
		return dateOfOnsetKnown;
	}

	public void setDateOfOnsetKnown(YesNoUnknown dateOfOnsetKnown) {
		this.dateOfOnsetKnown = dateOfOnsetKnown;
	}

	@Order(402)
	public ClinicalPresentationStatus getClinicalPresentationStatus() {
		return clinicalPresentationStatus;
	}

	public void setClinicalPresentationStatus(ClinicalPresentationStatus clinicalPresentationStatus) {
		this.clinicalPresentationStatus = clinicalPresentationStatus;
	}

	@Order(403)
	public SymptomState getNocturnalCough() {
		return nocturnalCough;
	}

	public void setNocturnalCough(SymptomState nocturnalCough) {
		this.nocturnalCough = nocturnalCough;
	}

	@Order(404)
	public SymptomState getUnknownSymptom() {
		return unknownSymptom;
	}

	public void setUnknownSymptom(SymptomState unknownSymptom) {
		this.unknownSymptom = unknownSymptom;
	}

	@Order(405)
	public SymptomState getDifficultyBreathingDuringMeals() {
		return difficultyBreathingDuringMeals;
	}

	public void setDifficultyBreathingDuringMeals(SymptomState difficultyBreathingDuringMeals) {
		this.difficultyBreathingDuringMeals = difficultyBreathingDuringMeals;
	}

	@Order(406)
	public SymptomState getParadoxicalBreathing() {
		return paradoxicalBreathing;
	}

	public void setParadoxicalBreathing(SymptomState paradoxicalBreathing) {
		this.paradoxicalBreathing = paradoxicalBreathing;
	}

	@Order(407)
	public SymptomState getRespiratoryFatigue() {
		return respiratoryFatigue;
	}

	public void setRespiratoryFatigue(SymptomState respiratoryFatigue) {
		this.respiratoryFatigue = respiratoryFatigue;
	}

	@Order(408)
	public YesNoUnknown getParentTimeOffWork() {
		return parentTimeOffWork;
	}

	public void setParentTimeOffWork(YesNoUnknown parentTimeOffWork) {
		this.parentTimeOffWork = parentTimeOffWork;
	}

	@Order(409)
	public Float getTimeOffWorkDays() {
		return timeOffWorkDays;
	}

	public void setTimeOffWorkDays(Float timeOffWorkDays) {
		this.timeOffWorkDays = timeOffWorkDays;
	}

	@Order(410)
	public Date getSkinRashOnsetDate() {
		return skinRashOnsetDate;
	}

	public void setSkinRashOnsetDate(Date skinRashOnsetDate) {
		this.skinRashOnsetDate = skinRashOnsetDate;
	}

	// #13399 - deprecated, replaced by encephalitis
	@Deprecated
	@Order(411)
	public SymptomState getAcuteEncephalitis() {
		return acuteEncephalitis;
	}

	@Deprecated
	public void setAcuteEncephalitis(SymptomState acuteEncephalitis) {
		this.acuteEncephalitis = acuteEncephalitis;
	}

	@Order(412)
	public SymptomState getEggyBurps() {
		return eggyBurps;
	}

	public void setEggyBurps(SymptomState eggyBurps) {
		this.eggyBurps = eggyBurps;
	}

	@Order(413)
	public SymptomState getWeightLoss() {
		return weightLoss;
	}

	public void setWeightLoss(SymptomState weightLoss) {
		this.weightLoss = weightLoss;
	}

	@Order(414)
	public Float getWeightLossAmount() {
		return weightLossAmount;
	}

	public void setWeightLossAmount(Float weightLossAmount) {
		this.weightLossAmount = weightLossAmount;
	}

	@Order(415)
	public SymptomState getSymptomCurrentStatus() {
		return symptomCurrentStatus;
	}

	public void setSymptomCurrentStatus(SymptomState symptomCurrentStatus) {
		this.symptomCurrentStatus = symptomCurrentStatus;
	}

	@Order(416)
	public Integer getDurationOfSymptoms() {
		return durationOfSymptoms;
	}

	public void setDurationOfSymptoms(Integer durationOfSymptoms) {
		this.durationOfSymptoms = durationOfSymptoms;
	}

	@Order(417)
	public SymptomState getReoccurrence() {
		return reoccurrence;
	}

	public void setReoccurrence(SymptomState reoccurrence) {
		this.reoccurrence = reoccurrence;
	}

	@Order(418)
	public SymptomState getOvernightStayRequired() {
		return overnightStayRequired;
	}

	public void setOvernightStayRequired(SymptomState overnightStayRequired) {
		this.overnightStayRequired = overnightStayRequired;
	}

	@Order(419)
	public SymptomState getBloating() {
		return bloating;
	}

	public void setBloating(SymptomState bloating) {
		this.bloating = bloating;
	}

	@Order(420)
	public SymptomState getClammySkin() {
		return clammySkin;
	}

	public void setClammySkin(SymptomState clammySkin) {
		this.clammySkin = clammySkin;
	}

	@Order(421)
	public SymptomState getColdSkin() {
		return coldSkin;
	}

	public void setColdSkin(SymptomState coldSkin) {
		this.coldSkin = coldSkin;
	}

	@Order(422)
	public SymptomState getEncephalitis() {
		return encephalitis;
	}

	public void setEncephalitis(SymptomState encephalitis) {
		this.encephalitis = encephalitis;
	}

	@Order(423)
	public SymptomState getGuillainBarreSyndrome() {
		return guillainBarreSyndrome;
	}

	public void setGuillainBarreSyndrome(SymptomState guillainBarreSyndrome) {
		this.guillainBarreSyndrome = guillainBarreSyndrome;
	}

	@Order(424)
	public SymptomState getLethargy() {
		return lethargy;
	}

	public void setLethargy(SymptomState lethargy) {
		this.lethargy = lethargy;
	}

	// #13399 - deprecated, replaced by confusedDisoriented
	@Deprecated
	@Order(425)
	public SymptomState getConfusion() {
		return confusion;
	}

	@Deprecated
	public void setConfusion(SymptomState confusion) {
		this.confusion = confusion;
	}

	@Order(426)
	public SymptomState getPersistentVomiting() {
		return persistentVomiting;
	}

	public void setPersistentVomiting(SymptomState persistentVomiting) {
		this.persistentVomiting = persistentVomiting;
	}

	@Order(427)
	public SymptomState getRestlessness() {
		return restlessness;
	}

	public void setRestlessness(SymptomState restlessness) {
		this.restlessness = restlessness;
	}

	@Order(428)
	public SymptomState getAcuteBleeding() {
		return acuteBleeding;
	}

	public void setAcuteBleeding(SymptomState acuteBleeding) {
		this.acuteBleeding = acuteBleeding;
	}

	@Order(429)
	public SymptomState getSevereOrganImpairment() {
		return severeOrganImpairment;
	}

	public void setSevereOrganImpairment(SymptomState severeOrganImpairment) {
		this.severeOrganImpairment = severeOrganImpairment;
	}

	@Order(430)
	public SymptomState getPlasmaLeakageSign() {
		return plasmaLeakageSign;
	}

	public void setPlasmaLeakageSign(SymptomState plasmaLeakageSign) {
		this.plasmaLeakageSign = plasmaLeakageSign;
	}

	@Order(431)
	public SymptomState getPolydipsia() {
		return polydipsia;
	}

	public void setPolydipsia(SymptomState polydipsia) {
		this.polydipsia = polydipsia;
	}

	@Order(432)
	public SymptomState getSyndromicFlu() {
		return syndromicFlu;
	}

	public void setSyndromicFlu(SymptomState syndromicFlu) {
		this.syndromicFlu = syndromicFlu;
	}

	@Order(433)
	public SymptomState getAnemia() {
		return anemia;
	}

	public void setAnemia(SymptomState anemia) {
		this.anemia = anemia;
	}

	// #13399 - deprecated, replaced by alteredConsciousness
	@Deprecated
	@Order(434)
	public SymptomState getAlteredLevelOfConsciousness() {
		return alteredLevelOfConsciousness;
	}

	@Deprecated
	public void setAlteredLevelOfConsciousness(SymptomState alteredLevelOfConsciousness) {
		this.alteredLevelOfConsciousness = alteredLevelOfConsciousness;
	}

	@Order(435)
	public SymptomState getSevereAnemia() {
		return severeAnemia;
	}

	public void setSevereAnemia(SymptomState severeAnemia) {
		this.severeAnemia = severeAnemia;
	}

	@Order(436)
	public SymptomState getMetabolicAcidosis() {
		return metabolicAcidosis;
	}

	public void setMetabolicAcidosis(SymptomState metabolicAcidosis) {
		this.metabolicAcidosis = metabolicAcidosis;
	}

	@Order(437)
	public SymptomState getAcuteKidneyFailure() {
		return acuteKidneyFailure;
	}

	public void setAcuteKidneyFailure(SymptomState acuteKidneyFailure) {
		this.acuteKidneyFailure = acuteKidneyFailure;
	}

	@Order(438)
	public SymptomState getDisseminatedIntraVascularCoagulation() {
		return disseminatedIntraVascularCoagulation;
	}

	public void setDisseminatedIntraVascularCoagulation(SymptomState disseminatedIntraVascularCoagulation) {
		this.disseminatedIntraVascularCoagulation = disseminatedIntraVascularCoagulation;
	}

	@Order(439)
	public SymptomState getCerebralMalaria() {
		return cerebralMalaria;
	}

	public void setCerebralMalaria(SymptomState cerebralMalaria) {
		this.cerebralMalaria = cerebralMalaria;
	}

	@Order(440)
	public SymptomState getScantHemorrhage() {
		return scantHemorrhage;
	}

	public void setScantHemorrhage(SymptomState scantHemorrhage) {
		this.scantHemorrhage = scantHemorrhage;
	}

	@Order(441)
	public YesNoUnknown getOtherNeurologicalSymptoms() {
		return otherNeurologicalSymptoms;
	}

	public void setOtherNeurologicalSymptoms(YesNoUnknown otherNeurologicalSymptoms) {
		this.otherNeurologicalSymptoms = otherNeurologicalSymptoms;
	}

	@Order(442)
	public String getOtherNeurologicalSymptomsText() {
		return otherNeurologicalSymptomsText;
	}

	public void setOtherNeurologicalSymptomsText(String otherNeurologicalSymptomsText) {
		this.otherNeurologicalSymptomsText = otherNeurologicalSymptomsText;
	}

	@Order(443)
	public SymptomState getFatalRisk() {
		return fatalRisk;
	}

	public void setFatalRisk(SymptomState fatalRisk) {
		this.fatalRisk = fatalRisk;
	}

	@Order(444)
	public SymptomState getConstipation() {
		return constipation;
	}

	public void setConstipation(SymptomState constipation) {
		this.constipation = constipation;
	}

	@Order(445)
	public SymptomState getDysuria() {
		return dysuria;
	}

	public void setDysuria(SymptomState dysuria) {
		this.dysuria = dysuria;
	}

	@Order(446)
	public SymptomState getEyeIrritation() {
		return eyeIrritation;
	}

	public void setEyeIrritation(SymptomState eyeIrritation) {
		this.eyeIrritation = eyeIrritation;
	}

	@Order(447)
	public SymptomState getTenesmus() {
		return tenesmus;
	}

	public void setTenesmus(SymptomState tenesmus) {
		this.tenesmus = tenesmus;
	}

	@Order(448)
	public SymptomState getHaemolyticUremicSyndrome() {
		return haemolyticUremicSyndrome;
	}

	public void setHaemolyticUremicSyndrome(SymptomState haemolyticUremicSyndrome) {
		this.haemolyticUremicSyndrome = haemolyticUremicSyndrome;
	}

	@Order(449)
	public SymptomState getBloodyDiarrhea() {
		return bloodyDiarrhea;
	}

	public void setBloodyDiarrhea(SymptomState bloodyDiarrhea) {
		this.bloodyDiarrhea = bloodyDiarrhea;
	}

	@Order(450)
	public SymptomState getChancre() {
		return chancre;
	}

	public void setChancre(SymptomState chancre) {
		this.chancre = chancre;
	}

	@Order(451)
	public SymptomState getPatchyAlopecia() {
		return patchyAlopecia;
	}

	public void setPatchyAlopecia(SymptomState patchyAlopecia) {
		this.patchyAlopecia = patchyAlopecia;
	}

	@Order(452)
	public SymptomState getCondylomaVenereum() {
		return condylomaVenereum;
	}

	public void setCondylomaVenereum(SymptomState condylomaVenereum) {
		this.condylomaVenereum = condylomaVenereum;
	}

	@Order(453)
	public SymptomState getMucousPatches() {
		return mucousPatches;
	}

	public void setMucousPatches(SymptomState mucousPatches) {
		this.mucousPatches = mucousPatches;
	}

	@Order(454)
	public SymptomState getGeneralizedLymphadenopathy() {
		return generalizedLymphadenopathy;
	}

	public void setGeneralizedLymphadenopathy(SymptomState generalizedLymphadenopathy) {
		this.generalizedLymphadenopathy = generalizedLymphadenopathy;
	}

	@Order(455)
	public SymptomState getOcularManifestations() {
		return ocularManifestations;
	}

	public void setOcularManifestations(SymptomState ocularManifestations) {
		this.ocularManifestations = ocularManifestations;
	}

	@Order(456)
	public SymptomState getNeurologicalManifestations() {
		return neurologicalManifestations;
	}

	public void setNeurologicalManifestations(SymptomState neurologicalManifestations) {
		this.neurologicalManifestations = neurologicalManifestations;
	}

	@Order(457)
	public SymptomState getBonyAbnormalities() {
		return bonyAbnormalities;
	}

	public void setBonyAbnormalities(SymptomState bonyAbnormalities) {
		this.bonyAbnormalities = bonyAbnormalities;
	}

	@Order(458)
	public SymptomState getPseudoparalysis() {
		return pseudoparalysis;
	}

	public void setPseudoparalysis(SymptomState pseudoparalysis) {
		this.pseudoparalysis = pseudoparalysis;
	}

	@Order(459)
	public SymptomState getRefractoryRhinitis() {
		return refractoryRhinitis;
	}

	public void setRefractoryRhinitis(SymptomState refractoryRhinitis) {
		this.refractoryRhinitis = refractoryRhinitis;
	}

	@Order(460)
	public SymptomState getCondylomaLata() {
		return condylomaLata;
	}

	public void setCondylomaLata(SymptomState condylomaLata) {
		this.condylomaLata = condylomaLata;
	}

	@Order(461)
	public SymptomState getCentralNervousSystemDamage() {
		return centralNervousSystemDamage;
	}

	public void setCentralNervousSystemDamage(SymptomState centralNervousSystemDamage) {
		this.centralNervousSystemDamage = centralNervousSystemDamage;
	}

	@Order(462)
	public SymptomState getMalnutrition() {
		return malnutrition;
	}

	public void setMalnutrition(SymptomState malnutrition) {
		this.malnutrition = malnutrition;
	}

	@Order(463)
	public SymptomState getNephroticSyndrome() {
		return nephroticSyndrome;
	}

	public void setNephroticSyndrome(SymptomState nephroticSyndrome) {
		this.nephroticSyndrome = nephroticSyndrome;
	}

	@Order(464)
	public SymptomState getArthralgia() {
		return arthralgia;
	}

	public void setArthralgia(SymptomState arthralgia) {
		this.arthralgia = arthralgia;
	}

	@Order(465)
	public SymptomState getHepatomegaly() {
		return hepatomegaly;
	}

	public void setHepatomegaly(SymptomState hepatomegaly) {
		this.hepatomegaly = hepatomegaly;
	}

	@Order(466)
	public SymptomState getHepatosplenomegaly() {
		return hepatosplenomegaly;
	}

	public void setHepatosplenomegaly(SymptomState hepatosplenomegaly) {
		this.hepatosplenomegaly = hepatosplenomegaly;
	}

	@Order(467)
	public SymptomState getLowGradeFever() {
		return lowGradeFever;
	}

	public void setLowGradeFever(SymptomState lowGradeFever) {
		this.lowGradeFever = lowGradeFever;
	}

	@Order(468)
	public SymptomState getMucocutaneousLesion() {
		return mucocutaneousLesion;
	}

	public void setMucocutaneousLesion(SymptomState mucocutaneousLesion) {
		this.mucocutaneousLesion = mucocutaneousLesion;
	}

	// #13399 - deprecated, replaced by skinRash
	@Deprecated
	@Order(469)
	public SymptomState getMaculopapularRash() {
		return maculopapularRash;
	}

	@Deprecated
	public void setMaculopapularRash(SymptomState maculopapularRash) {
		this.maculopapularRash = maculopapularRash;
	}

	@Order(470)
	public SyphilisInfectionSite getSyphilisInfectionSite() {
		return syphilisInfectionSite;
	}

	public void setSyphilisInfectionSite(SyphilisInfectionSite syphilisInfectionSite) {
		this.syphilisInfectionSite = syphilisInfectionSite;
	}

	@Order(471)
	public SyphilisStage getSyphilisStage() {
		return syphilisStage;
	}

	public void setSyphilisStage(SyphilisStage syphilisStage) {
		this.syphilisStage = syphilisStage;
	}

	@Order(472)
	public SyphilisInfectiousness getSyphilisInfectiousness() {
		return syphilisInfectiousness;
	}

	public void setSyphilisInfectiousness(SyphilisInfectiousness syphilisInfectiousness) {
		this.syphilisInfectiousness = syphilisInfectiousness;
	}

	@Order(473)
	public YesNoUnknown getClinicalCriteriaMet() {
		return clinicalCriteriaMet;
	}

	public void setClinicalCriteriaMet(YesNoUnknown clinicalCriteriaMet) {
		this.clinicalCriteriaMet = clinicalCriteriaMet;
	}

	@Order(474)
	public SymptomState getCervicitis() {
		return cervicitis;
	}

	public void setCervicitis(SymptomState cervicitis) {
		this.cervicitis = cervicitis;
	}

	@Order(475)
	public SymptomState getNewbornConjunctivitis() {
		return newbornConjunctivitis;
	}

	public void setNewbornConjunctivitis(SymptomState newbornConjunctivitis) {
		this.newbornConjunctivitis = newbornConjunctivitis;
	}

	@Order(476)
	public SymptomState getEpididymitis() {
		return epididymitis;
	}

	public void setEpididymitis(SymptomState epididymitis) {
		this.epididymitis = epididymitis;
	}

	@Order(477)
	public SymptomState getPelvicInflammatoryDisease() {
		return pelvicInflammatoryDisease;
	}

	public void setPelvicInflammatoryDisease(SymptomState pelvicInflammatoryDisease) {
		this.pelvicInflammatoryDisease = pelvicInflammatoryDisease;
	}

	@Order(478)
	public SymptomState getProctitis() {
		return proctitis;
	}

	public void setProctitis(SymptomState proctitis) {
		this.proctitis = proctitis;
	}

	@Order(479)
	public SymptomState getAcuteSalpingitis() {
		return acuteSalpingitis;
	}

	public void setAcuteSalpingitis(SymptomState acuteSalpingitis) {
		this.acuteSalpingitis = acuteSalpingitis;
	}

	@Order(480)
	public SymptomState getUrethritis() {
		return urethritis;
	}

	public void setUrethritis(SymptomState urethritis) {
		this.urethritis = urethritis;
	}

	@Order(481)
	public SymptomState getGonococcalInfectionSiteAnorectal() {
		return gonococcalInfectionSiteAnorectal;
	}

	public void setGonococcalInfectionSiteAnorectal(SymptomState gonococcalInfectionSiteAnorectal) {
		this.gonococcalInfectionSiteAnorectal = gonococcalInfectionSiteAnorectal;
	}

	@Order(482)
	public SymptomState getGonococcalInfectionSiteBlood() {
		return gonococcalInfectionSiteBlood;
	}

	public void setGonococcalInfectionSiteBlood(SymptomState gonococcalInfectionSiteBlood) {
		this.gonococcalInfectionSiteBlood = gonococcalInfectionSiteBlood;
	}

	@Order(483)
	public SymptomState getGonococcalInfectionSiteCerebrospinalFluid() {
		return gonococcalInfectionSiteCerebrospinalFluid;
	}

	public void setGonococcalInfectionSiteCerebrospinalFluid(SymptomState gonococcalInfectionSiteCerebrospinalFluid) {
		this.gonococcalInfectionSiteCerebrospinalFluid = gonococcalInfectionSiteCerebrospinalFluid;
	}

	@Order(484)
	public SymptomState getGonococcalInfectionSiteEye() {
		return gonococcalInfectionSiteEye;
	}

	public void setGonococcalInfectionSiteEye(SymptomState gonococcalInfectionSiteEye) {
		this.gonococcalInfectionSiteEye = gonococcalInfectionSiteEye;
	}

	@Order(485)
	public SymptomState getGonococcalInfectionSiteGenital() {
		return gonococcalInfectionSiteGenital;
	}

	public void setGonococcalInfectionSiteGenital(SymptomState gonococcalInfectionSiteGenital) {
		this.gonococcalInfectionSiteGenital = gonococcalInfectionSiteGenital;
	}

	@Order(486)
	public SymptomState getGonococcalInfectionSiteJointFluid() {
		return gonococcalInfectionSiteJointFluid;
	}

	public void setGonococcalInfectionSiteJointFluid(SymptomState gonococcalInfectionSiteJointFluid) {
		this.gonococcalInfectionSiteJointFluid = gonococcalInfectionSiteJointFluid;
	}

	@Order(487)
	public SymptomState getGonococcalInfectionSitePharyngeal() {
		return gonococcalInfectionSitePharyngeal;
	}

	public void setGonococcalInfectionSitePharyngeal(SymptomState gonococcalInfectionSitePharyngeal) {
		this.gonococcalInfectionSitePharyngeal = gonococcalInfectionSitePharyngeal;
	}

	@Order(488)
	public SymptomState getGonococcalInfectionSiteUnknown() {
		return gonococcalInfectionSiteUnknown;
	}

	public void setGonococcalInfectionSiteUnknown(SymptomState gonococcalInfectionSiteUnknown) {
		this.gonococcalInfectionSiteUnknown = gonococcalInfectionSiteUnknown;
	}

	@Order(489)
	public SymptomState getGonococcalInfectionSiteOther() {
		return gonococcalInfectionSiteOther;
	}

	public void setGonococcalInfectionSiteOther(SymptomState gonococcalInfectionSiteOther) {
		this.gonococcalInfectionSiteOther = gonococcalInfectionSiteOther;
	}

	@Order(490)
	public String getGonococcalInfectionSiteOtherText() {
		return gonococcalInfectionSiteOtherText;
	}

	public void setGonococcalInfectionSiteOtherText(String gonococcalInfectionSiteOtherText) {
		this.gonococcalInfectionSiteOtherText = gonococcalInfectionSiteOtherText;
	}

	@Order(491)
	public SymptomState getNoConcurrentSti() {
		return noConcurrentSti;
	}

	public void setNoConcurrentSti(SymptomState noConcurrentSti) {
		this.noConcurrentSti = noConcurrentSti;
	}

	@Order(492)
	public SymptomState getConcurrentStiChlamydia() {
		return concurrentStiChlamydia;
	}

	public void setConcurrentStiChlamydia(SymptomState concurrentStiChlamydia) {
		this.concurrentStiChlamydia = concurrentStiChlamydia;
	}

	@Order(493)
	public SymptomState getConcurrentStiGenitalHerpes() {
		return concurrentStiGenitalHerpes;
	}

	public void setConcurrentStiGenitalHerpes(SymptomState concurrentStiGenitalHerpes) {
		this.concurrentStiGenitalHerpes = concurrentStiGenitalHerpes;
	}

	@Order(494)
	public SymptomState getConcurrentStiLgv() {
		return concurrentStiLgv;
	}

	public void setConcurrentStiLgv(SymptomState concurrentStiLgv) {
		this.concurrentStiLgv = concurrentStiLgv;
	}

	@Order(495)
	public SymptomState getConcurrentStiMycoplasmaGenitalium() {
		return concurrentStiMycoplasmaGenitalium;
	}

	public void setConcurrentStiMycoplasmaGenitalium(SymptomState concurrentStiMycoplasmaGenitalium) {
		this.concurrentStiMycoplasmaGenitalium = concurrentStiMycoplasmaGenitalium;
	}

	@Order(496)
	public SymptomState getConcurrentStiInfectiousSyphilis() {
		return concurrentStiInfectiousSyphilis;
	}

	public void setConcurrentStiInfectiousSyphilis(SymptomState concurrentStiInfectiousSyphilis) {
		this.concurrentStiInfectiousSyphilis = concurrentStiInfectiousSyphilis;
	}

	@Order(497)
	public SymptomState getConcurrentStiTrichomonasVaginalis() {
		return concurrentStiTrichomonasVaginalis;
	}

	public void setConcurrentStiTrichomonasVaginalis(SymptomState concurrentStiTrichomonasVaginalis) {
		this.concurrentStiTrichomonasVaginalis = concurrentStiTrichomonasVaginalis;
	}

	@Order(498)
	public SymptomState getConcurrentStiGenitalWarts() {
		return concurrentStiGenitalWarts;
	}

	public void setConcurrentStiGenitalWarts(SymptomState concurrentStiGenitalWarts) {
		this.concurrentStiGenitalWarts = concurrentStiGenitalWarts;
	}

	@Order(499)
	public SymptomState getConcurrentStiOther() {
		return concurrentStiOther;
	}

	public void setConcurrentStiOther(SymptomState concurrentStiOther) {
		this.concurrentStiOther = concurrentStiOther;
	}

	@Order(500)
	public String getConcurrentStiOtherText() {
		return concurrentStiOtherText;
	}

	public void setConcurrentStiOtherText(String concurrentStiOtherText) {
		this.concurrentStiOtherText = concurrentStiOtherText;
	}

	@Order(501)
	public SymptomState getConcurrentStiUnknown() {
		return concurrentStiUnknown;
	}

	public void setConcurrentStiUnknown(SymptomState concurrentStiUnknown) {
		this.concurrentStiUnknown = concurrentStiUnknown;
	}

	@Order(502)
	public SymptomState getBronchitis() {
		return bronchitis;
	}

	public void setBronchitis(SymptomState bronchitis) {
		this.bronchitis = bronchitis;
	}

	@Order(503)
	public SymptomState getMyocarditis() {
		return myocarditis;
	}

	public void setMyocarditis(SymptomState myocarditis) {
		this.myocarditis = myocarditis;
	}

	@Order(504)
	public SymptomState getPericarditis() {
		return pericarditis;
	}

	public void setPericarditis(SymptomState pericarditis) {
		this.pericarditis = pericarditis;
	}

	@Order(505)
	public SymptomState getThrombocytopenia() {
		return thrombocytopenia;
	}

	public void setThrombocytopenia(SymptomState thrombocytopenia) {
		this.thrombocytopenia = thrombocytopenia;
	}

	@Order(506)
	public SymptomState getLymphadenopathySuboccipital() {
		return lymphadenopathySuboccipital;
	}

	public void setLymphadenopathySuboccipital(SymptomState lymphadenopathySuboccipital) {
		this.lymphadenopathySuboccipital = lymphadenopathySuboccipital;
	}

	@Order(507)
	public SymptomState getLymphadenopathyRetroauricular() {
		return lymphadenopathyRetroauricular;
	}

	public void setLymphadenopathyRetroauricular(SymptomState lymphadenopathyRetroauricular) {
		this.lymphadenopathyRetroauricular = lymphadenopathyRetroauricular;
	}

	@Order(508)
	public SymptomState getStillbornInfant() {
		return stillbornInfant;
	}

	public void setStillbornInfant(SymptomState stillbornInfant) {
		this.stillbornInfant = stillbornInfant;
	}

	@Order(509)
	public SymptomState getUnknownClinicalPresentation() {
		return unknownClinicalPresentation;
	}

	public void setUnknownClinicalPresentation(SymptomState unknownClinicalPresentation) {
		this.unknownClinicalPresentation = unknownClinicalPresentation;
	}

	@Order(510)
	public SymptomState getNeurologicalComplications() {
		return neurologicalComplications;
	}

	public void setNeurologicalComplications(SymptomState neurologicalComplications) {
		this.neurologicalComplications = neurologicalComplications;
	}

	@Order(511)
	public SymptomState getLowBirthWeight() {
		return lowBirthWeight;
	}

	public void setLowBirthWeight(SymptomState lowBirthWeight) {
		this.lowBirthWeight = lowBirthWeight;
	}

	@Order(512)
	public SymptomState getHepatitis() {
		return hepatitis;
	}

	public void setHepatitis(SymptomState hepatitis) {
		this.hepatitis = hepatitis;
	}

	@Order(513)
	public SymptomState getThrombocytopenicPurpura() {
		return thrombocytopenicPurpura;
	}

	public void setThrombocytopenicPurpura(SymptomState thrombocytopenicPurpura) {
		this.thrombocytopenicPurpura = thrombocytopenicPurpura;
	}

	@Order(514)
	public SymptomState getRubellaArthritis() {
		return rubellaArthritis;
	}

	public void setRubellaArthritis(SymptomState rubellaArthritis) {
		this.rubellaArthritis = rubellaArthritis;
	}

	@Order(515)
	public SymptomState getGreggsTriad() {
		return greggsTriad;
	}

	public void setGreggsTriad(SymptomState greggsTriad) {
		this.greggsTriad = greggsTriad;
	}

	@Order(516)
	public YesNoUnknown getConjunctivalPresentation() {
		return conjunctivalPresentation;
	}

	public void setConjunctivalPresentation(YesNoUnknown conjunctivalPresentation) {
		this.conjunctivalPresentation = conjunctivalPresentation;
	}

	@Order(517)
	public YesNoUnknown getCutaneousPresentation() {
		return cutaneousPresentation;
	}

	public void setCutaneousPresentation(YesNoUnknown cutaneousPresentation) {
		this.cutaneousPresentation = cutaneousPresentation;
	}

	@Order(518)
	public YesNoUnknown getGenitalPresentation() {
		return genitalPresentation;
	}

	public void setGenitalPresentation(YesNoUnknown genitalPresentation) {
		this.genitalPresentation = genitalPresentation;
	}

	@Order(519)
	public YesNoUnknown getNasalDischargeClearToBloody() {
		return nasalDischargeClearToBloody;
	}

	public void setNasalDischargeClearToBloody(YesNoUnknown nasalDischargeClearToBloody) {
		this.nasalDischargeClearToBloody = nasalDischargeClearToBloody;
	}

	@Order(520)
	public YesNoUnknown getOtherClinicalCriteria() {
		return otherClinicalCriteria;
	}

	public void setOtherClinicalCriteria(YesNoUnknown otherClinicalCriteria) {
		this.otherClinicalCriteria = otherClinicalCriteria;
	}

	@Order(521)
	public YesNoUnknown getUnknownClinicalCriteria() {
		return unknownClinicalCriteria;
	}

	public void setUnknownClinicalCriteria(YesNoUnknown unknownClinicalCriteria) {
		this.unknownClinicalCriteria = unknownClinicalCriteria;
	}

	@Order(522)
	public YesNoUnknown getRespiratoryAndCutaneousPresentation() {
		return respiratoryAndCutaneousPresentation;
	}

	public void setRespiratoryAndCutaneousPresentation(YesNoUnknown respiratoryAndCutaneousPresentation) {
		this.respiratoryAndCutaneousPresentation = respiratoryAndCutaneousPresentation;
	}

	@Order(523)
	public YesNoUnknown getRespiratoryWithMembrane() {
		return respiratoryWithMembrane;
	}

	public void setRespiratoryWithMembrane(YesNoUnknown respiratoryWithMembrane) {
		this.respiratoryWithMembrane = respiratoryWithMembrane;
	}

	@Order(524)
	public YesNoUnknown getRespiratoryWithoutMembrane() {
		return respiratoryWithoutMembrane;
	}

	public void setRespiratoryWithoutMembrane(YesNoUnknown respiratoryWithoutMembrane) {
		this.respiratoryWithoutMembrane = respiratoryWithoutMembrane;
	}

	@Order(525)
	public String getOtherClinicalCriteriaText() {
		return otherClinicalCriteriaText;
	}

	public void setOtherClinicalCriteriaText(String otherClinicalCriteriaText) {
		this.otherClinicalCriteriaText = otherClinicalCriteriaText;
	}

	@Order(526)
	public YesNoUnknown getNoComplicationDiagnosis() {
		return noComplicationDiagnosis;
	}

	public void setNoComplicationDiagnosis(YesNoUnknown noComplicationDiagnosis) {
		this.noComplicationDiagnosis = noComplicationDiagnosis;
	}

	@Order(527)
	public YesNoUnknown getCardiacDisorder() {
		return cardiacDisorder;
	}

	public void setCardiacDisorder(YesNoUnknown cardiacDisorder) {
		this.cardiacDisorder = cardiacDisorder;
	}

	@Order(528)
	public YesNoUnknown getNeurologicalComplicationDiagnosis() {
		return neurologicalComplicationDiagnosis;
	}

	public void setNeurologicalComplicationDiagnosis(YesNoUnknown neurologicalComplicationDiagnosis) {
		this.neurologicalComplicationDiagnosis = neurologicalComplicationDiagnosis;
	}

	@Order(529)
	public YesNoUnknown getNeurologicalAndCardiacDisorder() {
		return neurologicalAndCardiacDisorder;
	}

	public void setNeurologicalAndCardiacDisorder(YesNoUnknown neurologicalAndCardiacDisorder) {
		this.neurologicalAndCardiacDisorder = neurologicalAndCardiacDisorder;
	}

	@Order(530)
	public YesNoUnknown getOtherComplicationDiagnosis() {
		return otherComplicationDiagnosis;
	}

	public void setOtherComplicationDiagnosis(YesNoUnknown otherComplicationDiagnosis) {
		this.otherComplicationDiagnosis = otherComplicationDiagnosis;
	}

	@Order(531)
	public String getOtherComplicationDiagnosisText() {
		return otherComplicationDiagnosisText;
	}

	public void setOtherComplicationDiagnosisText(String otherComplicationDiagnosisText) {
		this.otherComplicationDiagnosisText = otherComplicationDiagnosisText;
	}

	@Order(532)
	public YesNoUnknown getUnknownComplicationDiagnosis() {
		return unknownComplicationDiagnosis;
	}

	public void setUnknownComplicationDiagnosis(YesNoUnknown unknownComplicationDiagnosis) {
		this.unknownComplicationDiagnosis = unknownComplicationDiagnosis;
	}

}
