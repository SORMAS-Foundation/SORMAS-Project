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
package de.symeda.sormas.api.symptoms;

import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class SymptomsDto extends EntityDto {

	public static final String ABDOMINAL_PAIN = "abdominalPain";

	public static final String ALTERED_CONSCIOUSNESS = "alteredConsciousness";

	public static final String ANOREXIA_APPETITE_LOSS = "anorexiaAppetiteLoss";
	public static final String BACKACHE = "backache";
	public static final String BEDRIDDEN = "bedridden";
	public static final String BILATERAL_CATARACTS = "bilateralCataracts";
	public static final String BLACKENING_DEATH_OF_TISSUE = "blackeningDeathOfTissue";
	public static final String BLEEDING_VAGINA = "bleedingVagina";
	public static final String BLOOD_IN_STOOL = "bloodInStool";
	public static final String BLOOD_PRESSURE_DIASTOLIC = "bloodPressureDiastolic";
	public static final String BLOOD_PRESSURE_SYSTOLIC = "bloodPressureSystolic";
	public static final String BLOOD_URINE = "bloodUrine";
	public static final String BLOODY_BLACK_STOOL = "bloodyBlackStool";
		
	public static final String BUBOES_GROIN_ARMPIT_NECK = "buboesGroinArmpitNeck";
	public static final String BULGING_FONTANELLE = "bulgingFontanelle";
	public static final String CHEST_PAIN = "chestPain";

	public static final String CHILLS_SWEATS = "chillsSweats";
	public static final String CONFUSED_DISORIENTED = "confusedDisoriented";
	public static final String CONGENITAL_GLAUCOMA = "congenitalGlaucoma";
	public static final String CONGENITAL_HEART_DISEASE = "congenitalHeartDisease";
	public static final String CONGENITAL_HEART_DISEASE_TYPE = "congenitalHeartDiseaseType";
	public static final String CONGENITAL_HEART_DISEASE_DETAILS = "congenitalHeartDiseaseDetails";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String COUGH = "cough";
	public static final String COUGHING_BLOOD = "coughingBlood";
	public static final String DARK_URINE = "darkUrine";
	public static final String DEHYDRATION = "dehydration";
	public static final String DEVELOPMENTAL_DELAY = "developmentalDelay";
	public static final String DIARRHEA = "diarrhea";
	public static final String DIFFICULTY_BREATHING = "difficultyBreathing";
	public static final String DIGESTED_BLOOD_VOMIT = "digestedBloodVomit";
	public static final String EYE_PAIN_LIGHT_SENSITIVE = "eyePainLightSensitive";
	public static final String EYES_BLEEDING = "eyesBleeding";
	public static final String FATIGUE_WEAKNESS = "fatigueWeakness";
	public static final String FEVER = "fever";
	public static final String FLUID_IN_LUNG_CAVITY = "fluidInLungCavity";
	public static final String GLASGOW_COMA_SCALE = "glasgowComaScale";
	public static final String GUMS_BLEEDING = "gumsBleeding";
	public static final String HEADACHE = "headache";
	public static final String HEARINGLOSS = "hearingloss";
	public static final String HEART_RATE = "heartRate";
	public static final String HEIGHT = "height";
	public static final String HEMORRHAGIC_SYNDROME = "hemorrhagicSyndrome";
	public static final String HICCUPS = "hiccups";
	public static final String HYPERGLYCEMIA = "hyperglycemia";
	public static final String HYPOGLYCEMIA = "hypoglycemia";
	public static final String I18N_PREFIX = "Symptoms";
	public static final String INJECTION_SITE_BLEEDING = "injectionSiteBleeding";
	public static final String JAUNDICE = "jaundice";
	public static final String JAUNDICE_WITHIN_24_HOURS_OF_BIRTH = "jaundiceWithin24HoursOfBirth";
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
	public static final String LOSS_SKIN_TURGOR = "lossSkinTurgor";	
	public static final String LYMPHADENOPATHY_AXILLARY = "lymphadenopathyAxillary";
	public static final String LYMPHADENOPATHY_CERVICAL = "lymphadenopathyCervical";
	public static final String LYMPHADENOPATHY_INGUINAL = "lymphadenopathyInguinal";
	public static final String MALAISE = "malaise";	
	public static final String MENINGEAL_SIGNS = "meningealSigns";
	public static final String MENINGOENCEPHALITIS = "meningoencephalitis";
	public static final String MICROCEPHALY = "microcephaly";
	public static final String MID_UPPER_ARM_CIRCUMFERENCE = "midUpperArmCircumference";
	public static final String MUSCLE_PAIN = "musclePain";
	public static final String NAUSEA = "nausea";
	public static final String NECK_STIFFNESS = "neckStiffness";
	public static final String NOSE_BLEEDING = "noseBleeding";
	public static final String OEDEMA_FACE_NECK = "oedemaFaceNeck";
	public static final String OEDEMA_LOWER_EXTREMITY = "oedemaLowerExtremity";
	public static final String ONSET_DATE = "onsetDate";
	public static final String ONSET_SYMPTOM = "onsetSymptom";	
	public static final String ORAL_ULCERS = "oralUlcers";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS = "otherHemorrhagicSymptoms";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS_TEXT = "otherHemorrhagicSymptomsText";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS = "otherNonHemorrhagicSymptoms";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT = "otherNonHemorrhagicSymptomsText";
	public static final String OTITIS_MEDIA = "otitisMedia";
	public static final String PAINFUL_LYMPHADENITIS = "painfulLymphadenitis";
	public static final String PALPABLE_LIVER = "palpableLiver";
	public static final String PALPABLE_SPLEEN = "palpableSpleen";
	public static final String PATIENT_ILL_LOCATION = "patientIllLocation";
	public static final String PHARYNGEAL_ERYTHEMA = "pharyngealErythema";
	public static final String PHARYNGEAL_EXUDATE = "pharyngealExudate";
	public static final String PIGMENTARY_RETINOPATHY = "pigmentaryRetinopathy";
	public static final String PURPURIC_RASH = "purpuricRash";
	public static final String RADIOLUCENT_BONE_DISEASE = "radiolucentBoneDisease";
	public static final String RAPID_BREATHING = "rapidBreathing";
	public static final String RED_BLOOD_VOMIT = "redBloodVomit";
	public static final String REFUSAL_FEEDOR_DRINK = "refusalFeedorDrink";
	public static final String RESPIRATORY_RATE = "respiratoryRate";
	public static final String RUNNY_NOSE = "runnyNose";
	public static final String SEIZURES = "seizures";
	public static final String SEPSIS = "sepsis";
	private static final long serialVersionUID = 4146526547904182448L;
	public static final String SHOCK = "shock";
	public static final String SIDE_PAIN = "sidePain";
	public static final String SKIN_BRUISING = "skinBruising";
	public static final String SKIN_RASH = "skinRash";
	public static final String SORE_THROAT = "soreThroat";
	public static final String SPLENOMEGALY = "splenomegaly";
	public static final String STOMACH_BLEEDING = "stomachBleeding";
	public static final String SUNKEN_EYES_FONTANELLE = "sunkenEyesFontanelle";
	public static final String SWOLLEN_GLANDS = "swollenGlands";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String SYMPTOMS_COMMENTS = "symptomsComments";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String THROBOCYTOPENIA = "throbocytopenia";
	public static final String TREMOR = "tremor";
	public static final String UNEXPLAINED_BLEEDING = "unexplainedBleeding";
	public static final String UNILATERAL_CATARACTS = "unilateralCataracts";
	public static final String VOMITING = "vomiting";	
	public static final String HYDROPHOBIA = "hydrophobia";	
	public static final String OPISTHOTONUS = "opisthotonus";
	public static final String ANXIETY_STATES = "anxietyStates";
	public static final String DELIRIUM = "delirium";
	public static final String UPROARIOUSNESS = "uproariousness";
	public static final String PARASTHESIA_AROUND_WOUND = "paresthesiaAroundWound";
	public static final String EXCESS_SALIVATION = "excessSalivation";
	public static final String INSOMNIA = "insomnia";
	public static final String PARALYSIS = "paralysis";
	public static final String EXCITATION = "excitation";
	public static final String DYSPHAGIA = "dysphagia";
	public static final String AEROPHOBIA = "aerophobia";
	public static final String HYPERACTIVITY = "hyperactivity";
	public static final String PARESIS = "paresis";
	public static final String AGITATION = "agitation";
	public static final String ASCENDING_FLACCID_PARALYSIS = "ascendingFlaccidParalysis";
	public static final String ERRATIC_BEHAVIOUR = "erraticBehaviour";
	public static final String COMA = "coma";
	public static final String CONVULSION = "convulsion";
	public static final String FLUID_IN_LUNG_CAVITY_AUSCULTATION = "fluidInLungCavityAuscultation";
	public static final String FLUID_IN_LUNG_CAVITY_XRAY = "fluidInLungCavityXray";
	public static final String ABNORMAL_LUNG_XRAY_FINDINGS = "abnormalLungXrayFindings";
	public static final String CONJUNCTIVAL_INJECTION = "conjunctivalInjection";
	public static final String ACUTE_RESPIRATORY_DISTRESS_SYNDROME = "acuteRespiratoryDistressSyndrome";
	public static final String PNEUMONIA_CLINICAL_OR_RADIOLOGIC = "pneumoniaClinicalOrRadiologic";
	public static final String LOSS_OF_TASTE = "lossOfTaste";
	public static final String LOSS_OF_SMELL = "lossOfSmell";
	
	public static final String WEIGHT = "weight";

	// Fields are declared in the order they should appear in the import template
	
	public static SymptomsDto build() {
		SymptomsDto symptoms = new SymptomsDto();
		symptoms.setUuid(DataHelper.createUuid());
		return symptoms;
	}

	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CHOLERA, Disease.YELLOW_FEVER,
			Disease.DENGUE, Disease.PLAGUE, Disease.POLIO, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED,
			Disease.OTHER })
	private SymptomState abdominalPain;
	// complications
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState alteredConsciousness;
	
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.CSM, Disease.CHOLERA,
			Disease.POLIO, Disease.YELLOW_FEVER, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState anorexiaAppetiteLoss;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.POLIO, Disease.YELLOW_FEVER, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState backache;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState bedridden;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.PLAGUE, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState blackeningDeathOfTissue;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState bleedingVagina;	
	@Diseases({ Disease.AFP, Disease.CHOLERA, Disease.GUINEA_WORM, Disease.POLIO, Disease.YELLOW_FEVER, Disease.ANTHRAX,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState bloodInStool;
	private Integer bloodPressureDiastolic;
	private Integer bloodPressureSystolic;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState bloodUrine;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState bloodyBlackStool;
	
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.PLAGUE, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState buboesGroinArmpitNeck;
	@Diseases({ Disease.AFP, Disease.CSM, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState bulgingFontanelle;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.PLAGUE,
			Disease.POLIO, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState chestPain;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.PLAGUE, Disease.ANTHRAX, Disease.POLIO, Disease.CORONAVIRUS, 
			Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState chillsSweats;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.PLAGUE, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.CORONAVIRUS, Disease.UNDEFINED,
			Disease.OTHER })
	@Outbreaks
	private SymptomState confusedDisoriented;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.MONKEYPOX,
			Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState conjunctivitis;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.MONKEYPOX,
			Disease.PLAGUE, Disease.POLIO, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState cough;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.PLAGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState coughingBlood;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.POLIO, Disease.YELLOW_FEVER, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState darkUrine;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.CHOLERA, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState dehydration;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.PLAGUE, Disease.POLIO, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED,
			Disease.OTHER })
	@Outbreaks
	private SymptomState diarrhea;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.PLAGUE,
			Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState difficultyBreathing;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState digestedBloodVomit;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.MEASLES,
			Disease.DENGUE, Disease.MONKEYPOX, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED,
			Disease.OTHER })
	@Outbreaks
	private SymptomState eyePainLightSensitive;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.POLIO, Disease.YELLOW_FEVER, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState eyesBleeding;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState fatigueWeakness;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState fever;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState fluidInLungCavity;
	private Integer glasgowComaScale;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.YELLOW_FEVER,
			Disease.DENGUE, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState gumsBleeding;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.YELLOW_FEVER,
			Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES,
			Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState headache;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.CONGENITAL_RUBELLA, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState hearingloss;
	private Integer heartRate;
	private Integer height;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.YELLOW_FEVER,
			Disease.DENGUE, Disease.PLAGUE, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState hemorrhagicSyndrome;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState hiccups;
	@Diseases({ Disease.AFP, Disease.CSM, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState hyperglycemia;
	@Diseases({ Disease.AFP, Disease.CSM, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState hypoglycemia;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState injectionSiteBleeding;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.YELLOW_FEVER, Disease.LASSA, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.CONGENITAL_RUBELLA, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState jaundice;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private YesNoUnknown jaundiceWithin24HoursOfBirth;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.NEW_INFLUENZA,
			Disease.CSM, Disease.MEASLES, Disease.DENGUE, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState jointPain;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MEASLES, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState kopliksSpots;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.ANTHRAX, Disease.POLIO, Disease.UNDEFINED,
			Disease.OTHER })
	/** Vesiculopustular rash */
	private SymptomState lesions;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsAllOverBody;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.ANTHRAX, Disease.POLIO, Disease.UNDEFINED,
			Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsArms;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.ANTHRAX, Disease.POLIO, Disease.UNDEFINED,
			Disease.OTHER })
	@DependantOn(LESIONS)
	private SymptomState lesionsDeepProfound;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsFace;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsGenitals;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsLegs;
	@Diseases({Disease.MONKEYPOX,Disease.ANTHRAX})
	@DependantOn(LESIONS)
	private Date lesionsOnsetDate;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsPalmsHands;
	@Diseases({Disease.MONKEYPOX})
	@DependantOn(LESIONS)
	private SymptomState lesionsResembleImg1;
	@Diseases({Disease.MONKEYPOX})
	@DependantOn(LESIONS)
	private SymptomState lesionsResembleImg2;
	@Diseases({Disease.MONKEYPOX})
	@DependantOn(LESIONS)
	private SymptomState lesionsResembleImg3;
	@Diseases({Disease.MONKEYPOX})
	@DependantOn(LESIONS)
	private SymptomState lesionsResembleImg4;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private SymptomState lesionsSameSize;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private SymptomState lesionsSameState;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsSolesFeet;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private SymptomState lesionsThatItch;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(LESIONS)
	private Boolean lesionsThorax;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState lossSkinTurgor;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState lymphadenopathyAxillary;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState lymphadenopathyCervical;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState lymphadenopathyInguinal;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState malaise;
	@Diseases({ Disease.AFP, Disease.CSM, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.RABIES, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState meningealSigns;
	private Integer midUpperArmCircumference;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO, Disease.CORONAVIRUS,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState musclePain;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.UNSPECIFIED_VHF,
			Disease.POLIO, Disease.RABIES, Disease.ANTHRAX, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState nausea;
	@Diseases({ Disease.AFP, Disease.CSM, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState neckStiffness;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.YELLOW_FEVER,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState noseBleeding;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState oedemaFaceNeck;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState oedemaLowerExtremity;
	@Outbreaks
	private Date onsetDate;
	private String onsetSymptom;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState oralUlcers;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.PLAGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState otherHemorrhagicSymptoms;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.PLAGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(OTHER_HEMORRHAGIC_SYMPTOMS)
	private String otherHemorrhagicSymptomsText;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.UNSPECIFIED_VHF,
			Disease.CONGENITAL_RUBELLA, Disease.POLIO, Disease.RABIES, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState otherNonHemorrhagicSymptoms;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.CONGENITAL_RUBELLA, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	@DependantOn(OTHER_NON_HEMORRHAGIC_SYMPTOMS)
	private String otherNonHemorrhagicSymptomsText;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.POLIO,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState otitisMedia;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.PLAGUE, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState painfulLymphadenitis;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState palpableLiver;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState palpableSpleen;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.MONKEYPOX, Disease.POLIO, Disease.UNDEFINED, Disease.OTHER })
	private String patientIllLocation;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState pharyngealErythema;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState pharyngealExudate;
	@Diseases({ Disease.AFP, Disease.DENGUE, Disease.GUINEA_WORM, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState rapidBreathing;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.DENGUE, Disease.POLIO,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState redBloodVomit;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.CSM, Disease.CHOLERA,
			Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState refusalFeedorDrink;
	private Integer respiratoryRate;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.POLIO, Disease.CORONAVIRUS,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState runnyNose;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.RABIES, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState seizures;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private SymptomState sepsis;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.PLAGUE, Disease.ANTHRAX, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED,
			Disease.OTHER })
	@Outbreaks
	private SymptomState shock;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState sidePain;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState skinBruising;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.MEASLES,
			Disease.DENGUE, Disease.POLIO, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	/** Maculopapular rash */
	private SymptomState skinRash;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.MEASLES, Disease.MONKEYPOX,
			Disease.ANTHRAX, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.OTHER })
	private SymptomState soreThroat;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.POLIO, Disease.YELLOW_FEVER, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	@DependantOn(UNEXPLAINED_BLEEDING)
	private SymptomState stomachBleeding;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState sunkenEyesFontanelle;
	@Diseases({ Disease.AFP, Disease.DENGUE, Disease.GUINEA_WORM, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState swollenGlands;
	private Boolean symptomatic;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.CONGENITAL_RUBELLA, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	private String symptomsComments;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE,
			Disease.UNSPECIFIED_VHF, Disease.CONGENITAL_RUBELLA, Disease.POLIO, Disease.RABIES, Disease.CORONAVIRUS, Disease.UNDEFINED,
			Disease.OTHER })
	@Outbreaks
	private Float temperature;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE,
			Disease.UNSPECIFIED_VHF, Disease.CONGENITAL_RUBELLA, Disease.POLIO, Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER })
	@Outbreaks
	private TemperatureSource temperatureSource;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState throbocytopenia;
	@Diseases({ Disease.AFP, Disease.GUINEA_WORM, Disease.LASSA, Disease.POLIO, Disease.UNSPECIFIED_VHF,
			Disease.UNDEFINED, Disease.OTHER })
	private SymptomState tremor;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState bilateralCataracts;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState unilateralCataracts;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState congenitalGlaucoma;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState pigmentaryRetinopathy;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState purpuricRash;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState microcephaly;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState developmentalDelay;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState splenomegaly;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState meningoencephalitis;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState radiolucentBoneDisease;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private SymptomState congenitalHeartDisease;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private CongenitalHeartDiseaseType congenitalHeartDiseaseType;
	@Diseases({Disease.CONGENITAL_RUBELLA})
	private String congenitalHeartDiseaseDetails;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.PLAGUE,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.POLIO, Disease.OTHER })
	private SymptomState unexplainedBleeding;
	@Diseases({ Disease.AFP, Disease.EVD, Disease.GUINEA_WORM, Disease.LASSA, Disease.NEW_INFLUENZA, Disease.CSM, Disease.CHOLERA,
			Disease.MEASLES, Disease.YELLOW_FEVER, Disease.DENGUE, Disease.MONKEYPOX, Disease.PLAGUE, Disease.ANTHRAX,
			Disease.UNSPECIFIED_VHF, Disease.UNDEFINED, Disease.POLIO, Disease.CORONAVIRUS, Disease.OTHER })
	@Outbreaks
	private SymptomState vomiting;
	@Diseases({Disease.RABIES})
	private SymptomState hydrophobia;
	@Diseases({Disease.RABIES})
	private SymptomState opisthotonus;
	@Diseases({Disease.RABIES})
	private SymptomState anxietyStates;
	@Diseases({Disease.RABIES})
	private SymptomState delirium;
	@Diseases({Disease.RABIES})
	private SymptomState uproariousness;
	@Diseases({Disease.RABIES})
	private SymptomState paresthesiaAroundWound;
	@Diseases({Disease.RABIES})
	private SymptomState excessSalivation;
	@Diseases({Disease.RABIES})
	private SymptomState insomnia;
	@Diseases({Disease.RABIES})
	private SymptomState paralysis;
	@Diseases({Disease.RABIES, Disease.CORONAVIRUS})
	private SymptomState excitation;
	@Diseases({Disease.RABIES})
	private SymptomState dysphagia;
	@Diseases({Disease.RABIES})
	private SymptomState aerophobia;
	@Diseases({Disease.RABIES})
	private SymptomState hyperactivity;
	@Diseases({Disease.RABIES})
	private SymptomState paresis;
	@Diseases({Disease.RABIES})
	private SymptomState agitation;
	@Diseases({Disease.RABIES})
	private SymptomState ascendingFlaccidParalysis;
	@Diseases({Disease.RABIES})
	private SymptomState erraticBehaviour;
	@Diseases({Disease.RABIES, Disease.CORONAVIRUS})
	private SymptomState coma;
	@Diseases({Disease.ANTHRAX})
	private SymptomState convulsion;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState fluidInLungCavityAuscultation;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState fluidInLungCavityXray;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState abnormalLungXrayFindings;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState conjunctivalInjection;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState acuteRespiratoryDistressSyndrome;
	@Diseases({Disease.CORONAVIRUS, Disease.UNDEFINED, Disease.OTHER})
	private SymptomState pneumoniaClinicalOrRadiologic;
	@Diseases({ Disease.CORONAVIRUS })
	private SymptomState lossOfTaste;
	@Diseases({ Disease.CORONAVIRUS })
	private SymptomState lossOfSmell;

	private Integer weight;
	
	@Order(0)
	public Float getTemperature() {
		return temperature;
	}
	@Order(1)
	public TemperatureSource getTemperatureSource() {
		return temperatureSource;
	}
	@Order(20)
	@ImportIgnore
	public Boolean getSymptomatic() {
		return symptomatic;
	}
	@Order(21)
	public String getSymptomsComments() {
		return symptomsComments;
	}
	@Order(22)
	public Date getOnsetDate() {
		return onsetDate;
	}
	@Order(23)
	public String getOnsetSymptom() {
		return onsetSymptom;
	}
	@ImportIgnore
	public String getPatientIllLocation() {
		return patientIllLocation;
	}

	@Order(100)
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}
	@Order(101)
	public SymptomState getAlteredConsciousness() {
		return alteredConsciousness;
	}
	@Order(102)
	public SymptomState getAnorexiaAppetiteLoss() {
		return anorexiaAppetiteLoss;
	}
	@Order(103)
	public SymptomState getBackache() {
		return backache;
	}
	@Order(104)
	public SymptomState getBedridden() {
		return bedridden;
	}
	@Order(105)
	public SymptomState getBlackeningDeathOfTissue() {
		return blackeningDeathOfTissue;
	}
	@Order(110)
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}
	@Order(111)
	public SymptomState getBloodInStool() {
		return bloodInStool;
	}
	public Integer getBloodPressureDiastolic() {
		return bloodPressureDiastolic;
	}
	public Integer getBloodPressureSystolic() {
		return bloodPressureSystolic;
	}
	@Order(112)
	public SymptomState getBloodUrine() {
		return bloodUrine;
	}
	@Order(113)
	public SymptomState getBloodyBlackStool() {
		return bloodyBlackStool;
	}
	@Order(114)
	public SymptomState getBuboesGroinArmpitNeck() {
		return buboesGroinArmpitNeck;
	}
	@Order(115)
	public SymptomState getBulgingFontanelle() {
		return bulgingFontanelle;
	}
	@Order(116)
	public SymptomState getBilateralCataracts() {
		return bilateralCataracts;
	}
	@Order(117)
	public SymptomState getUnilateralCataracts() {
		return unilateralCataracts;
	}
	@Order(120)
	public SymptomState getChestPain() {
		return chestPain;
	}
	@Order(121)
	public SymptomState getChillsSweats() {
		return chillsSweats;
	}
	@Order(122)
	public SymptomState getConfusedDisoriented() {
		return confusedDisoriented;
	}
	@Order(123)
	public SymptomState getCongenitalGlaucoma() {
		return congenitalGlaucoma;
	}
	@Order(124)
	public SymptomState getCongenitalHeartDisease() {
		return congenitalHeartDisease;
	}
	@Order(125)
	public CongenitalHeartDiseaseType getCongenitalHeartDiseaseType() {
		return congenitalHeartDiseaseType;
	}
	@Order(126)
	public String getCongenitalHeartDiseaseDetails() {
		return congenitalHeartDiseaseDetails;
	}
	@Order(127)
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}
	@Order(128)
	public SymptomState getCough() {
		return cough;
	}
	@Order(129)
	public SymptomState getCoughingBlood() {
		return coughingBlood;
	}
	@Order(130)
	public SymptomState getDarkUrine() {
		return darkUrine;
	}
	@Order(131)
	public SymptomState getDehydration() {
		return dehydration;
	}
	@Order(132)
	public SymptomState getDevelopmentalDelay() {
		return developmentalDelay;
	}
	@Order(133)
	public SymptomState getDiarrhea() {
		return diarrhea;
	}	
	@Order(134)
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}
	@Order(135)
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}
	@Order(136)
	public SymptomState getEyePainLightSensitive() {
		return eyePainLightSensitive;
	}
	@Order(140)
	public SymptomState getEyesBleeding() {
		return eyesBleeding;
	}
	@Order(141)
	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}
	@Order(142)
	public SymptomState getFever() {
		return fever;
	}
	@Order(143)
	public SymptomState getFluidInLungCavity() {
		return fluidInLungCavity;
	}
	@Order(144)
	public SymptomState getFluidInLungCavityAuscultation() {
		return fluidInLungCavityAuscultation;
	}
	@Order(145)
	public SymptomState getFluidInLungCavityXray() {
		return fluidInLungCavityXray;
	}
	public Integer getGlasgowComaScale() {
		return glasgowComaScale;
	}
	@Order(146)
	public SymptomState getGumsBleeding() {
		return gumsBleeding;
	}
	@Order(147)
	public SymptomState getHeadache() {
		return headache;
	}
	@Order(150)
	public SymptomState getHearingloss() {
		return hearingloss;
	}
	public Integer getHeartRate() {
		return heartRate;
	}
	public Integer getHeight() {
		return height;
	}
	@Order(151)
	public SymptomState getHemorrhagicSyndrome() {
		return hemorrhagicSyndrome;
	}
	@Order(152)
	public SymptomState getHiccups() {
		return hiccups;
	}
	@Order(153)
	public SymptomState getHyperglycemia() {
		return hyperglycemia;
	}
	@Order(154)
	public SymptomState getHypoglycemia() {
		return hypoglycemia;
	}
	@Order(155)
	public SymptomState getInjectionSiteBleeding() {
		return injectionSiteBleeding;
	}
	@Order(160)
	public SymptomState getJaundice() {
		return jaundice;
	}
	@Order(161)
	public YesNoUnknown getJaundiceWithin24HoursOfBirth() {
		return jaundiceWithin24HoursOfBirth;
	}
	@Order(162)
	public SymptomState getJointPain() {
		return jointPain;
	}
	@Order(163)
	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}
	@Order(164)
	public SymptomState getLesions() {
		return lesions;
	}
	@Order(165)
	public Boolean getLesionsAllOverBody() {
		return lesionsAllOverBody;
	}
	@Order(166)
	public Boolean getLesionsArms() {
		return lesionsArms;
	}
	@Order(170)
	public SymptomState getLesionsDeepProfound() {
		return lesionsDeepProfound;
	}
	@Order(171)
	public Boolean getLesionsFace() {
		return lesionsFace;
	}
	@Order(172)
	public Boolean getLesionsGenitals() {
		return lesionsGenitals;
	}
	@Order(173)
	public Boolean getLesionsLegs() {
		return lesionsLegs;
	}
	@Order(174)
	public Date getLesionsOnsetDate() {
		return lesionsOnsetDate;
	}
	@Order(175)
	public Boolean getLesionsPalmsHands() {
		return lesionsPalmsHands;
	}
	public SymptomState getLesionsResembleImg1() {
		return lesionsResembleImg1;
	}
	public SymptomState getLesionsResembleImg2() {
		return lesionsResembleImg2;
	}
	public SymptomState getLesionsResembleImg3() {
		return lesionsResembleImg3;
	}
	public SymptomState getLesionsResembleImg4() {
		return lesionsResembleImg4;
	}
	@Order(180)
	public SymptomState getLesionsSameSize() {
		return lesionsSameSize;
	}
	@Order(181)
	public SymptomState getLesionsSameState() {
		return lesionsSameState;
	}
	@Order(182)
	public Boolean getLesionsSolesFeet() {
		return lesionsSolesFeet;
	}
	@Order(183)
	public SymptomState getLesionsThatItch() {
		return lesionsThatItch;
	}
	@Order(184)
	public Boolean getLesionsThorax() {
		return lesionsThorax;
	}
	@Order(185)
	public SymptomState getLossSkinTurgor() {
		return lossSkinTurgor;
	}
	@Order(190)
	public SymptomState getLymphadenopathyAxillary() {
		return lymphadenopathyAxillary;
	}
	@Order(191)
	public SymptomState getLymphadenopathyCervical() {
		return lymphadenopathyCervical;
	}
	@Order(192)
	public SymptomState getLymphadenopathyInguinal() {
		return lymphadenopathyInguinal;
	}
	@Order(193)
	public SymptomState getMalaise() {
		return malaise;
	}
	@Order(194)
	public SymptomState getMeningealSigns() {
		return meningealSigns;
	}
	@Order(195)
	public SymptomState getMeningoencephalitis() {
		return meningoencephalitis;
	}
	@Order(196)
	public SymptomState getMicrocephaly() {
		return microcephaly;
	}
	@Order(197)
	public Integer getMidUpperArmCircumference() {
		return midUpperArmCircumference;
	}
	@Order(200)
	public SymptomState getMusclePain() {
		return musclePain;
	}
	@Order(201)
	public SymptomState getNausea() {
		return nausea;
	}
	@Order(202)
	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}
	@Order(203)
	public SymptomState getNoseBleeding() {
		return noseBleeding;
	}
	@Order(204)
	public SymptomState getOedemaFaceNeck() {
		return oedemaFaceNeck;
	}
	@Order(205)
	public SymptomState getOedemaLowerExtremity() {
		return oedemaLowerExtremity;
	}
	@Order(210)
	public SymptomState getOralUlcers() {
		return oralUlcers;
	}
	@Order(211)
	public SymptomState getOtherHemorrhagicSymptoms() {
		return otherHemorrhagicSymptoms;
	}	
	@Order(212)
	public String getOtherHemorrhagicSymptomsText() {
		return otherHemorrhagicSymptomsText;
	}
	@Order(213)
	public SymptomState getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}
	@Order(214)
	public String getOtherNonHemorrhagicSymptomsText() {
		return otherNonHemorrhagicSymptomsText;
	}
	@Order(215)
	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}
	@Order(220)
	public SymptomState getPainfulLymphadenitis() {
		return painfulLymphadenitis;
	}
	@Order(221)
	public SymptomState getPalpableLiver() {
		return palpableLiver;
	}
	@Order(222)
	public SymptomState getPalpableSpleen() {
		return palpableSpleen;
	}
	@Order(223)
	public SymptomState getPharyngealErythema() {
		return pharyngealErythema;
	}
	@Order(224)
	public SymptomState getPharyngealExudate() {
		return pharyngealExudate;
	}
	@Order(225)
	public SymptomState getPigmentaryRetinopathy() {
		return pigmentaryRetinopathy;
	}
	@Order(226)
	public SymptomState getPurpuricRash() {
		return purpuricRash;
	}
	@Order(227)
	public SymptomState getRadiolucentBoneDisease() {
		return radiolucentBoneDisease;
	}
	@Order(228)
	public SymptomState getRapidBreathing() {
		return rapidBreathing;
	}
	@Order(230)
	public SymptomState getRedBloodVomit() {
		return redBloodVomit;
	}
	@Order(231)
	public SymptomState getRefusalFeedorDrink() {
		return refusalFeedorDrink;
	}
	public Integer getRespiratoryRate() {
		return respiratoryRate;
	}
	@Order(232)
	public SymptomState getRunnyNose() {
		return runnyNose;
	}
	@Order(233)
	public SymptomState getSeizures() {
		return seizures;
	}
	@Order(234)
	public SymptomState getSepsis() {
		return sepsis;
	}
	@Order(235)
	public SymptomState getShock() {
		return shock;
	}
	@Order(240)
	public SymptomState getSidePain() {
		return sidePain;
	}
	@Order(241)
	public SymptomState getSkinBruising() {
		return skinBruising;
	}
	@Order(242)
	public SymptomState getSkinRash() {
		return skinRash;
	}
	@Order(243)
	public SymptomState getSoreThroat() {
		return soreThroat;
	}
	@Order(244)
	public SymptomState getSplenomegaly() {
		return splenomegaly;
	}
	@Order(245)
	public SymptomState getStomachBleeding() {
		return stomachBleeding;
	}
	@Order(246)
	public SymptomState getSunkenEyesFontanelle() {
		return sunkenEyesFontanelle;
	}
	@Order(250)
	public SymptomState getSwollenGlands() {
		return swollenGlands;
	}
	@Order(251)
	public SymptomState getThrobocytopenia() {
		return throbocytopenia;
	}
	@Order(252)
	public SymptomState getTremor() {
		return tremor;
	}
	@Order(253)
	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}
	@Order(254)
	public SymptomState getVomiting() {
		return vomiting;
	}
	@Order(260)
	public SymptomState getAbnormalLungXrayFindings() {
		return abnormalLungXrayFindings;
	}
	@Order(261)
	public SymptomState getConjunctivalInjection() {
		return conjunctivalInjection;
	}
	@Order(262)
	public SymptomState getAcuteRespiratoryDistressSyndrome() {
		return acuteRespiratoryDistressSyndrome;
	}
	@Order(263)
	public SymptomState getPneumoniaClinicalOrRadiologic() {
		return pneumoniaClinicalOrRadiologic;
	}

	@Order(264)
	public SymptomState getLossOfTaste() {
		return lossOfTaste;
	}

	@Order(265)
	public SymptomState getLossOfSmell() {
		return lossOfSmell;
	}
	
	public Integer getWeight() {
		return weight;
	}
	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}
	public void setAlteredConsciousness(SymptomState alteredConsciousness) {
		this.alteredConsciousness = alteredConsciousness;
	}
	public void setAnorexiaAppetiteLoss(SymptomState anorexiaAppetiteLoss) {
		this.anorexiaAppetiteLoss = anorexiaAppetiteLoss;
	}
	public void setBackache(SymptomState backache) {
		this.backache = backache;
	}	
	public void setBedridden(SymptomState bedridden) {
		this.bedridden = bedridden;
	}
	public void setBlackeningDeathOfTissue(SymptomState blackeningDeathOfTissue) {
		this.blackeningDeathOfTissue = blackeningDeathOfTissue;
	}
	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}
	public void setBloodInStool(SymptomState bloodInStool) {
		this.bloodInStool = bloodInStool;
	}
	public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}
	public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}
	public void setBloodUrine(SymptomState bloodUrine) {
		this.bloodUrine = bloodUrine;
	}
	public void setBloodyBlackStool(SymptomState bloodyBlackStool) {
		this.bloodyBlackStool = bloodyBlackStool;
	}
	public void setBuboesGroinArmpitNeck(SymptomState buboesGroinArmpitNeck) {
		this.buboesGroinArmpitNeck = buboesGroinArmpitNeck;
	}
	public void setBulgingFontanelle(SymptomState bulgingFontanelle) {
		this.bulgingFontanelle = bulgingFontanelle;
	}
	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}
	public void setChillsSweats(SymptomState chillsSweats) {
		this.chillsSweats = chillsSweats;
	}
	public void setConfusedDisoriented(SymptomState confusedDisoriented) {
		this.confusedDisoriented = confusedDisoriented;
	}
	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}
	public void setCough(SymptomState cough) {
		this.cough = cough;
	}
	public void setCoughingBlood(SymptomState coughingBlood) {
		this.coughingBlood = coughingBlood;
	}
	public void setDarkUrine(SymptomState darkUrine) {
		this.darkUrine = darkUrine;
	}
	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}
	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}
	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}
	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}
	public void setEyePainLightSensitive(SymptomState eyePainLightSensitive) {
		this.eyePainLightSensitive = eyePainLightSensitive;
	}
	public void setEyesBleeding(SymptomState eyesBleeding) {
		this.eyesBleeding = eyesBleeding;
	}
	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}
	public void setFever(SymptomState fever) {
		this.fever = fever;
	}
	public void setFluidInLungCavity(SymptomState fluidInLungCavity) {
		this.fluidInLungCavity = fluidInLungCavity;
	}
	public void setGlasgowComaScale(Integer glasgowComaScale) {
		this.glasgowComaScale = glasgowComaScale;
	}
	public void setGumsBleeding(SymptomState gumsBleeding) {
		this.gumsBleeding = gumsBleeding;
	}
	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}
	public void setHearingloss(SymptomState hearingloss) {
		this.hearingloss = hearingloss;
	}
	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public void setHemorrhagicSyndrome(SymptomState hemorrhagicSyndrome) {
		this.hemorrhagicSyndrome = hemorrhagicSyndrome;
	}
	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}
	public void setHyperglycemia(SymptomState hyperglycemia) {
		this.hyperglycemia = hyperglycemia;
	}
	public void setHypoglycemia(SymptomState hypoglycemia) {
		this.hypoglycemia = hypoglycemia;
	}
	public void setInjectionSiteBleeding(SymptomState injectionSiteBleeding) {
		this.injectionSiteBleeding = injectionSiteBleeding;
	}
	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}
	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}
	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}
	public void setLesions(SymptomState lesions) {
		this.lesions = lesions;
	}
	public void setLesionsAllOverBody(Boolean lesionsAllOverBody) {
		this.lesionsAllOverBody = lesionsAllOverBody;
	}
	public void setLesionsArms(Boolean lesionsArms) {
		this.lesionsArms = lesionsArms;
	}
	public void setLesionsDeepProfound(SymptomState lesionsDeepProfound) {
		this.lesionsDeepProfound = lesionsDeepProfound;
	}
	public void setLesionsFace(Boolean lesionsFace) {
		this.lesionsFace = lesionsFace;
	}
	public void setLesionsGenitals(Boolean lesionsGenitals) {
		this.lesionsGenitals = lesionsGenitals;
	}
	public void setLesionsLegs(Boolean lesionsLegs) {
		this.lesionsLegs = lesionsLegs;
	}
	public void setLesionsOnsetDate(Date lesionsOnsetDate) {
		this.lesionsOnsetDate = lesionsOnsetDate;
	}
	public void setLesionsPalmsHands(Boolean lesionsPalmsHands) {
		this.lesionsPalmsHands = lesionsPalmsHands;
	}
	public void setLesionsResembleImg1(SymptomState lesionsResembleImg1) {
		this.lesionsResembleImg1 = lesionsResembleImg1;
	}
	public void setLesionsResembleImg2(SymptomState lesionsResembleImg2) {
		this.lesionsResembleImg2 = lesionsResembleImg2;
	}
	public void setLesionsResembleImg3(SymptomState lesionsResembleImg3) {
		this.lesionsResembleImg3 = lesionsResembleImg3;
	}
	public void setLesionsResembleImg4(SymptomState lesionsResembleImg4) {
		this.lesionsResembleImg4 = lesionsResembleImg4;
	}
	public void setLesionsSameSize(SymptomState lesionsSameSize) {
		this.lesionsSameSize = lesionsSameSize;
	}
	public void setLesionsSameState(SymptomState lesionsSameState) {
		this.lesionsSameState = lesionsSameState;
	}
	public void setLesionsSolesFeet(Boolean lesionsSolesFeet) {
		this.lesionsSolesFeet = lesionsSolesFeet;
	}
	public void setLesionsThatItch(SymptomState lesionsThatItch) {
		this.lesionsThatItch = lesionsThatItch;
	}
	public void setLesionsThorax(Boolean lesionsThorax) {
		this.lesionsThorax = lesionsThorax;
	}
	public void setLossSkinTurgor(SymptomState lossSkinTurgor) {
		this.lossSkinTurgor = lossSkinTurgor;
	}
	public void setLymphadenopathyAxillary(SymptomState lymphadenopathyAxillary) {
		this.lymphadenopathyAxillary = lymphadenopathyAxillary;
	}
	public void setLymphadenopathyCervical(SymptomState lymphadenopathyCervical) {
		this.lymphadenopathyCervical = lymphadenopathyCervical;
	}
	public void setLymphadenopathyInguinal(SymptomState lymphadenopathyInguinal) {
		this.lymphadenopathyInguinal = lymphadenopathyInguinal;
	}
	public void setMalaise(SymptomState malaise) {
		this.malaise = malaise;
	}
	public void setMeningealSigns(SymptomState meningealSigns) {
		this.meningealSigns = meningealSigns;
	}
	public void setMidUpperArmCircumference(Integer midUpperArmCircumference) {
		this.midUpperArmCircumference = midUpperArmCircumference;
	}
	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}
	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}
	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}
	public void setNoseBleeding(SymptomState noseBleeding) {
		this.noseBleeding = noseBleeding;
	}
	public void setOedemaFaceNeck(SymptomState oedemaFaceNeck) {
		this.oedemaFaceNeck = oedemaFaceNeck;
	}
	public void setOedemaLowerExtremity(SymptomState oedemaLowerExtremity) {
		this.oedemaLowerExtremity = oedemaLowerExtremity;
	}
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}
	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
	}
	public void setOralUlcers(SymptomState oralUlcers) {
		this.oralUlcers = oralUlcers;
	}
	public void setOtherHemorrhagicSymptoms(SymptomState otherHemorrhagicSymptoms) {
		this.otherHemorrhagicSymptoms = otherHemorrhagicSymptoms;
	}
	public void setOtherHemorrhagicSymptomsText(String otherHemorrhagicSymptomsText) {
		this.otherHemorrhagicSymptomsText = otherHemorrhagicSymptomsText;
	}
	public void setOtherNonHemorrhagicSymptoms(SymptomState otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}
	public void setOtherNonHemorrhagicSymptomsText(String otherNonHemorrhagicSymptomsText) {
		this.otherNonHemorrhagicSymptomsText = otherNonHemorrhagicSymptomsText;
	}
	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
	}
	public void setPainfulLymphadenitis(SymptomState painfulLymphadenitis) {
		this.painfulLymphadenitis = painfulLymphadenitis;
	}
	public void setPalpableLiver(SymptomState palpableLiver) {
		this.palpableLiver = palpableLiver;
	}
	public void setPalpableSpleen(SymptomState palpableSpleen) {
		this.palpableSpleen = palpableSpleen;
	}
	public void setPatientIllLocation(String patientIllLocation) {
		this.patientIllLocation = patientIllLocation;
	}
	public void setPharyngealErythema(SymptomState pharyngealErythema) {
		this.pharyngealErythema = pharyngealErythema;
	}
	
	public void setPharyngealExudate(SymptomState pharyngealExudate) {
		this.pharyngealExudate = pharyngealExudate;
	}
	public void setRapidBreathing(SymptomState rapidBreathing) {
		this.rapidBreathing = rapidBreathing;
	}
	public void setRedBloodVomit(SymptomState redBloodVomit) {
		this.redBloodVomit = redBloodVomit;
	}
	public void setRefusalFeedorDrink(SymptomState refusalFeedorDrink) {
		this.refusalFeedorDrink = refusalFeedorDrink;
	}
	public void setRespiratoryRate(Integer respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}
	public void setRunnyNose(SymptomState runnyNose) {
		this.runnyNose = runnyNose;
	}

	public void setSeizures(SymptomState seizures) {
		this.seizures = seizures;
	}
	public void setSepsis(SymptomState sepsis) {
		this.sepsis = sepsis;
	}
	public void setShock(SymptomState shock) {
		this.shock = shock;
	}
	public void setSidePain(SymptomState sidePain) {
		this.sidePain = sidePain;
	}	
	public void setSkinBruising(SymptomState skinBruising) {
		this.skinBruising = skinBruising;
	}
	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}
	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}
	public void setStomachBleeding(SymptomState stomachBleeding) {
		this.stomachBleeding = stomachBleeding;
	}
	public void setSunkenEyesFontanelle(SymptomState sunkenEyesFontanelle) {
		this.sunkenEyesFontanelle = sunkenEyesFontanelle;
	}
	public void setSwollenGlands(SymptomState swollenGlands) {
		this.swollenGlands = swollenGlands;
	}	
	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}
	public void setSymptomsComments(String symptomsComments) {
		this.symptomsComments = symptomsComments;
	}
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}
	public void setTemperatureSource(TemperatureSource temperatureSource) {
		this.temperatureSource = temperatureSource;
	}
	public void setThrobocytopenia(SymptomState throbocytopenia) {
		this.throbocytopenia = throbocytopenia;
	}
	public void setTremor(SymptomState tremor) {
		this.tremor = tremor;
	}
	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}
	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
	}	
	public SymptomState getConvulsion() {
		return convulsion;
	}
	public void setConvulsion(SymptomState convulsion) {
		this.convulsion = convulsion;
	}
	public void setJaundiceWithin24HoursOfBirth(YesNoUnknown jaundiceWithin24HoursOfBirth) {
		this.jaundiceWithin24HoursOfBirth = jaundiceWithin24HoursOfBirth;
	}
	public void setBilateralCataracts(SymptomState bilateralCataracts) {
		this.bilateralCataracts = bilateralCataracts;
	}
	public void setUnilateralCataracts(SymptomState unilateralCataracts) {
		this.unilateralCataracts = unilateralCataracts;
	}
	public void setCongenitalGlaucoma(SymptomState congenitalGlaucoma) {
		this.congenitalGlaucoma = congenitalGlaucoma;
	}
	public void setPigmentaryRetinopathy(SymptomState pigmentaryRetinopathy) {
		this.pigmentaryRetinopathy = pigmentaryRetinopathy;
	}
	public void setPurpuricRash(SymptomState purpuricRash) {
		this.purpuricRash = purpuricRash;
	}
	public void setMicrocephaly(SymptomState microcephaly) {
		this.microcephaly = microcephaly;
	}
	public void setDevelopmentalDelay(SymptomState developmentalDelay) {
		this.developmentalDelay = developmentalDelay;
	}
	public void setSplenomegaly(SymptomState splenomegaly) {
		this.splenomegaly = splenomegaly;
	}
	public void setMeningoencephalitis(SymptomState meningoencephalitis) {
		this.meningoencephalitis = meningoencephalitis;
	}
	public void setRadiolucentBoneDisease(SymptomState radiolucentBoneDisease) {
		this.radiolucentBoneDisease = radiolucentBoneDisease;
	}
	public void setCongenitalHeartDisease(SymptomState congenitalHeartDisease) {
		this.congenitalHeartDisease = congenitalHeartDisease;
	}
	public void setCongenitalHeartDiseaseType(CongenitalHeartDiseaseType congenitalHeartDiseaseType) {
		this.congenitalHeartDiseaseType = congenitalHeartDiseaseType;
	}
	public void setCongenitalHeartDiseaseDetails(String congenitalHeartDiseaseDetails) {
		this.congenitalHeartDiseaseDetails = congenitalHeartDiseaseDetails;
	}
	public SymptomState getHydrophobia() {
		return hydrophobia;
	}
	public void setHydrophobia(SymptomState hydrophobia) {
		this.hydrophobia = hydrophobia;
	}
	public SymptomState getOpisthotonus() {
		return opisthotonus;
	}
	public void setOpisthotonus(SymptomState opisthotonus) {
		this.opisthotonus = opisthotonus;
	}
	public SymptomState getAnxietyStates() {
		return anxietyStates;
	}
	public void setAnxietyStates(SymptomState anxietyStates) {
		this.anxietyStates = anxietyStates;
	}
	public SymptomState getDelirium() {
		return delirium;
	}
	public void setDelirium(SymptomState delirium) {
		this.delirium = delirium;
	}
	public SymptomState getUproariousness() {
		return uproariousness;
	}
	public void setUproariousness(SymptomState uproariousness) {
		this.uproariousness = uproariousness;
	}
	public SymptomState getParesthesiaAroundWound() {
		return paresthesiaAroundWound;
	}
	public void setParesthesiaAroundWound(SymptomState paresthesiaAroundWound) {
		this.paresthesiaAroundWound = paresthesiaAroundWound;
	}
	public SymptomState getExcessSalivation() {
		return excessSalivation;
	}
	public void setExcessSalivation(SymptomState excessSalivation) {
		this.excessSalivation = excessSalivation;
	}
	public SymptomState getInsomnia() {
		return insomnia;
	}
	public void setInsomnia(SymptomState insomnia) {
		this.insomnia = insomnia;
	}
	public SymptomState getParalysis() {
		return paralysis;
	}
	public void setParalysis(SymptomState paralysis) {
		this.paralysis = paralysis;
	}
	public SymptomState getExcitation() {
		return excitation;
	}
	public void setExcitation(SymptomState excitation) {
		this.excitation = excitation;
	}
	public SymptomState getDysphagia() {
		return dysphagia;
	}
	public void setDysphagia(SymptomState dysphagia) {
		this.dysphagia = dysphagia;
	}
	public SymptomState getAerophobia() {
		return aerophobia;
	}
	public void setAerophobia(SymptomState aerophobia) {
		this.aerophobia = aerophobia;
	}
	public SymptomState getHyperactivity() {
		return hyperactivity;
	}
	public void setHyperactivity(SymptomState hyperactivity) {
		this.hyperactivity = hyperactivity;
	}
	public SymptomState getParesis() {
		return paresis;
	}
	public void setParesis(SymptomState paresis) {
		this.paresis = paresis;
	}
	public SymptomState getAgitation() {
		return agitation;
	}
	public void setAgitation(SymptomState agitation) {
		this.agitation = agitation;
	}
	public SymptomState getAscendingFlaccidParalysis() {
		return ascendingFlaccidParalysis;
	}
	public void setAscendingFlaccidParalysis(SymptomState ascendingFlaccidParalysis) {
		this.ascendingFlaccidParalysis = ascendingFlaccidParalysis;
	}
	public SymptomState getErraticBehaviour() {
		return erraticBehaviour;
	}
	public void setErraticBehaviour(SymptomState erraticBehaviour) {
		this.erraticBehaviour = erraticBehaviour;
	}
	public SymptomState getComa() {
		return coma;
	}
	public void setComa(SymptomState coma) {
		this.coma = coma;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	public void setFluidInLungCavityAuscultation(SymptomState fluidInLungCavityAuscultation) {
		this.fluidInLungCavityAuscultation = fluidInLungCavityAuscultation;
	}
	public void setFluidInLungCavityXray(SymptomState fluidInLungCavityXray) {
		this.fluidInLungCavityXray = fluidInLungCavityXray;
	}
	public void setAbnormalLungXrayFindings(SymptomState abnormalLungXrayFindings) {
		this.abnormalLungXrayFindings = abnormalLungXrayFindings;
	}
	public void setConjunctivalInjection(SymptomState conjunctivalInjection) {
		this.conjunctivalInjection = conjunctivalInjection;
	}
	public void setAcuteRespiratoryDistressSyndrome(SymptomState acuteRespiratoryDistressSyndrome) {
		this.acuteRespiratoryDistressSyndrome = acuteRespiratoryDistressSyndrome;
	}
	public void setPneumoniaClinicalOrRadiologic(SymptomState pneumoniaClinicalOrRadiologic) {
		this.pneumoniaClinicalOrRadiologic = pneumoniaClinicalOrRadiologic;
	}
	public void setLossOfTaste(SymptomState lossOfTaste) {
		this.lossOfTaste = lossOfTaste;
	}
	public void setLossOfSmell(SymptomState lossOfSmell) {
		this.lossOfSmell = lossOfSmell;
	}
}
