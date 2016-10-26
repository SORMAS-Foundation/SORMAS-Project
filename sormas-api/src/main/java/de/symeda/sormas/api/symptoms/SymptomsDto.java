package de.symeda.sormas.api.symptoms;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.symeda.sormas.api.DataTransferObject;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.PreciseDateAdapter;

public class SymptomsDto extends DataTransferObject {

	public static final String I18N_PREFIX = "Symptoms";

	private static final long serialVersionUID = 4146526547904182448L;

	public static final String ABDOMINAL_PAIN = "abdominalPain";
	public static final String ANOREXIA_APPETITE_LOSS = "anorexiaAppetiteLoss";
	public static final String BLEEDING_VAGINA = "bleedingVagina";
	public static final String CHEST_PAIN = "chestPain";
	public static final String CHILLS = "chills";
	public static final String COMA_UNCONSCIOUS = "comaUnconscious";
	public static final String CONFUSED_DISORIENTED = "confusedDisoriented";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String COUGH = "cough";
	public static final String DEHYDRATION = "dehydration";
	public static final String DIARRHEA = "diarrhea";
	public static final String DIFFICULTY_BREATHING = "difficultyBreathing";
	public static final String DIGESTED_BLOOD_VOMIT = "digestedBloodVomit";
	public static final String EPISTAXIS = "epistaxis";
	public static final String EYE_PAIN_LIGHT_SENSITIVE = "eyePainLightSensitive";
	public static final String FATIGUE_WEAKNESS = "fatigueWeakness";
	public static final String FEVER = "fever";
	public static final String GUMS_BLEEDING = "gumsBleeding";
	public static final String HEADACHE = "headache";
	public static final String HEMATEMESIS = "hematemesis";
	public static final String HEMATURIA = "hematuria";
	public static final String HEMOPTYSIS = "hemoptysis";
	public static final String HICCUPS = "hiccups";
	public static final String HIGH_BLOOD_PRESSURE = "highBloodPressure";
	public static final String INJECTION_SITE_BLEEDING = "injectionSiteBleeding";
	public static final String JAUNDICE = "jaundice";
	public static final String JOINT_PAIN = "jointPain";
	public static final String KOPLIKS_SPOTS = "kopliksSpots";
	public static final String LETHARGY = "lethargy";
	public static final String LOW_BLOOD_PRESSURE = "lowBloodPressure";
	public static final String MELENA = "melena";
	public static final String MUSCLE_PAIN = "musclePain";
	public static final String NAUSEA = "nausea";
	public static final String NECK_STIFFNESS = "neckStiffness";
	public static final String OEDEMA = "oedema";
	public static final String ONSET_DATE = "onsetDate";
	public static final String ONSET_SYMPTOM = "onsetSymptom";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS = "otherHemorrhagicSymptoms";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS_TEXT = "otherHemorrhagicSymptomsText";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS = "otherNonHemorrhagicSymptoms";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT = "otherNonHemorrhagicSymptomsText";
	public static final String OTITIS_MEDIA = "otitisMedia";
	public static final String PETECHIAE = "petechiae";
	public static final String REFUSAL_FEEDOR_DRINK = "refusalFeedorDrink";
	public static final String RUNNY_NOSE = "runnyNose";
	public static final String SEIZURES = "seizures";
	public static final String SEPSIS = "sepsis";
	public static final String SKIN_RASH = "skinRash";
	public static final String SORE_THROAT = "soreThroat";
	public static final String SWOLLEN_LYMPH_NODES = "swollenLymphNodes";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String UNEXPLAINED_BLEEDING = "unexplainedBleeding";
	public static final String VOMITING = "vomiting";

	private Boolean symptomatic;
	private Date onsetDate;
	private String onsetSymptom;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM})
	private Float temperature;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM})
	private TemperatureSource temperatureSource;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState fever;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState chills;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState fatigueWeakness;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState seizures;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM})
	private SymptomState headache;
	@Diseases({Disease.CSM})
	private SymptomState neckStiffness;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState musclePain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.MEASLES})
	private SymptomState jointPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA})
	private SymptomState nausea;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState vomiting;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CHOLERA})
	private SymptomState abdominalPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState diarrhea;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM,Disease.CHOLERA})
	private SymptomState anorexiaAppetiteLoss;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM,Disease.CHOLERA})
	private SymptomState refusalFeedorDrink;
	@Diseases({Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState runnyNose;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState cough;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState soreThroat;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState chestPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState difficultyBreathing;
	@Diseases({Disease.EVD})
	private SymptomState hiccups;
	@Diseases({Disease.MEASLES})
	private SymptomState kopliksSpots;
	@Diseases({Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState otitisMedia;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState conjunctivitis;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.MEASLES})
	private SymptomState eyePainLightSensitive;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState jaundice;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM,Disease.MEASLES})
	private SymptomState skinRash;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private SymptomState dehydration;
	@Diseases({Disease.MEASLES})
	private SymptomState swollenLymphNodes;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState oedema;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState lethargy;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState confusedDisoriented;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState comaUnconscious;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM})
	private SymptomState sepsis;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState highBloodPressure;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private SymptomState lowBloodPressure;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState unexplainedBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState gumsBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState injectionSiteBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState epistaxis;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private SymptomState melena;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState hematemesis;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState digestedBloodVomit;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState hemoptysis;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState bleedingVagina;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState petechiae;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState hematuria;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState otherHemorrhagicSymptoms;
	@Diseases({Disease.EVD,Disease.LASSA})
	private String otherHemorrhagicSymptomsText;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState otherNonHemorrhagicSymptoms;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private String otherNonHemorrhagicSymptomsText;


	public SymptomState getChills() {
		return chills;
	}

	public void setChills(SymptomState chills) {
		this.chills = chills;
	}

	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}

	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}

	public SymptomState getSeizures() {
		return seizures;
	}

	public void setSeizures(SymptomState seizures) {
		this.seizures = seizures;
	}

	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}

	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}

	public SymptomState getNausea() {
		return nausea;
	}

	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}

	public SymptomState getRefusalFeedorDrink() {
		return refusalFeedorDrink;
	}

	public void setRefusalFeedorDrink(SymptomState refusalFeedorDrink) {
		this.refusalFeedorDrink = refusalFeedorDrink;
	}

	public SymptomState getRunnyNose() {
		return runnyNose;
	}

	public void setRunnyNose(SymptomState runnyNose) {
		this.runnyNose = runnyNose;
	}

	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}

	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}

	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}

	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
	}

	public SymptomState getDehydration() {
		return dehydration;
	}

	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}

	public SymptomState getSwollenLymphNodes() {
		return swollenLymphNodes;
	}

	public void setSwollenLymphNodes(SymptomState swollenLymphNodes) {
		this.swollenLymphNodes = swollenLymphNodes;
	}

	public SymptomState getOedema() {
		return oedema;
	}

	public void setOedema(SymptomState oedema) {
		this.oedema = oedema;
	}

	public SymptomState getLethargy() {
		return lethargy;
	}

	public void setLethargy(SymptomState lethargy) {
		this.lethargy = lethargy;
	}

	public SymptomState getSepsis() {
		return sepsis;
	}

	public void setSepsis(SymptomState sepsis) {
		this.sepsis = sepsis;
	}

	public SymptomState getHighBloodPressure() {
		return highBloodPressure;
	}

	public void setHighBloodPressure(SymptomState highBloodPressure) {
		this.highBloodPressure = highBloodPressure;
	}

	public SymptomState getLowBloodPressure() {
		return lowBloodPressure;
	}

	public void setLowBloodPressure(SymptomState lowBloodPressure) {
		this.lowBloodPressure = lowBloodPressure;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getOnsetDate() {
		return onsetDate;
	}

	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public TemperatureSource getTemperatureSource() {
		return temperatureSource;
	}

	public void setTemperatureSource(TemperatureSource temperatureSource) {
		this.temperatureSource = temperatureSource;
	}

	public SymptomState getFever() {
		return fever;
	}

	public void setFever(SymptomState fever) {
		this.fever = fever;
	}

	public SymptomState getVomiting() {
		return vomiting;
	}

	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
	}

	public SymptomState getDiarrhea() {
		return diarrhea;
	}

	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}

	public SymptomState getAnorexiaAppetiteLoss() {
		return anorexiaAppetiteLoss;
	}

	public void setAnorexiaAppetiteLoss(SymptomState anorexiaAppetiteLoss) {
		this.anorexiaAppetiteLoss = anorexiaAppetiteLoss;
	}

	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}

	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}

	public SymptomState getChestPain() {
		return chestPain;
	}

	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}

	public SymptomState getMusclePain() {
		return musclePain;
	}

	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}

	public SymptomState getJointPain() {
		return jointPain;
	}

	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}

	public SymptomState getHeadache() {
		return headache;
	}

	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}

	public SymptomState getCough() {
		return cough;
	}

	public void setCough(SymptomState cough) {
		this.cough = cough;
	}

	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}

	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}

	public SymptomState getSoreThroat() {
		return soreThroat;
	}

	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}

	public SymptomState getJaundice() {
		return jaundice;
	}

	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}

	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	public SymptomState getSkinRash() {
		return skinRash;
	}

	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}

	public SymptomState getHiccups() {
		return hiccups;
	}

	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}

	public SymptomState getEyePainLightSensitive() {
		return eyePainLightSensitive;
	}

	public void setEyePainLightSensitive(SymptomState eyePainLightSensitive) {
		this.eyePainLightSensitive = eyePainLightSensitive;
	}

	public SymptomState getComaUnconscious() {
		return comaUnconscious;
	}

	public void setComaUnconscious(SymptomState comaUnconscious) {
		this.comaUnconscious = comaUnconscious;
	}

	public SymptomState getConfusedDisoriented() {
		return confusedDisoriented;
	}

	public void setConfusedDisoriented(SymptomState confusedDisoriented) {
		this.confusedDisoriented = confusedDisoriented;
	}

	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}

	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}

	public SymptomState getGumsBleeding() {
		return gumsBleeding;
	}

	public void setGumsBleeding(SymptomState gumsBleeding) {
		this.gumsBleeding = gumsBleeding;
	}

	public SymptomState getInjectionSiteBleeding() {
		return injectionSiteBleeding;
	}

	public void setInjectionSiteBleeding(SymptomState injectionSiteBleeding) {
		this.injectionSiteBleeding = injectionSiteBleeding;
	}

	public SymptomState getEpistaxis() {
		return epistaxis;
	}

	public void setEpistaxis(SymptomState epistaxis) {
		this.epistaxis = epistaxis;
	}

	public SymptomState getMelena() {
		return melena;
	}

	public void setMelena(SymptomState melena) {
		this.melena = melena;
	}

	public SymptomState getHematemesis() {
		return hematemesis;
	}

	public void setHematemesis(SymptomState hematemesis) {
		this.hematemesis = hematemesis;
	}

	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}

	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}

	public SymptomState getHemoptysis() {
		return hemoptysis;
	}

	public void setHemoptysis(SymptomState hemoptysis) {
		this.hemoptysis = hemoptysis;
	}

	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}

	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}

	public SymptomState getPetechiae() {
		return petechiae;
	}

	public void setPetechiae(SymptomState petechiae) {
		this.petechiae = petechiae;
	}

	public SymptomState getHematuria() {
		return hematuria;
	}

	public void setHematuria(SymptomState hematuria) {
		this.hematuria = hematuria;
	}

	public Boolean getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public String getOnsetSymptom() {
		return onsetSymptom;
	}

	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
	}

	public SymptomState getOtherHemorrhagicSymptoms() {
		return otherHemorrhagicSymptoms;
	}

	public void setOtherHemorrhagicSymptoms(SymptomState otherHemorrhagicSymptoms) {
		this.otherHemorrhagicSymptoms = otherHemorrhagicSymptoms;
	}

	public String getOtherHemorrhagicSymptomsText() {
		return otherHemorrhagicSymptomsText;
	}

	public void setOtherHemorrhagicSymptomsText(String otherHemorrhagicSymptomsText) {
		this.otherHemorrhagicSymptomsText = otherHemorrhagicSymptomsText;
	}

	public SymptomState getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}

	public void setOtherNonHemorrhagicSymptoms(SymptomState otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}

	public String getOtherNonHemorrhagicSymptomsText() {
		return otherNonHemorrhagicSymptomsText;
	}

	public void setOtherNonHemorrhagicSymptomsText(String otherNonHemorrhagicSymptomsText) {
		this.otherNonHemorrhagicSymptomsText = otherNonHemorrhagicSymptomsText;
	}

}
