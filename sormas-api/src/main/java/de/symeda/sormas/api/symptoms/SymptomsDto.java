package de.symeda.sormas.api.symptoms;

import java.util.Date;

import de.symeda.sormas.api.DataTransferObject;

public class SymptomsDto extends DataTransferObject {

	public static final String I18N_PREFIX = "Symptoms";

	private static final long serialVersionUID = 4146526547904182448L;
	
	public static final String ABDOMINAL_PAIN = "abdominalPain";
	public static final String ANOREXIA_APPETITE_LOSS = "anorexiaAppetiteLoss";
	public static final String BLEEDING_VAGINA = "bleedingVagina";
	public static final String CHEST_PAIN = "chestPain";
	public static final String COMA_UNCONSCIOUS = "comaUnconscious";
	public static final String CONFUSED_DISORIENTED = "confusedDisoriented";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String COUGH = "cough";
	public static final String DIARRHEA = "diarrhea";
	public static final String DIFFICULTY_BREATHING = "difficultyBreathing";
	public static final String DIFFICULTY_SWALLOWING = "difficultySwallowing";
	public static final String DIGESTED_BLOOD_VOMIT = "digestedBloodVomit";
	public static final String EPISTAXIS = "epistaxis";
	public static final String EYE_PAIN_LIGHT_SENSITIVE = "eyePainLightSensitive";
	public static final String FEVER = "fever";
	public static final String GUMS_BLEEDING = "gumsBleeding";
	public static final String HEADACHE = "headache";
	public static final String HEMATEMESIS = "hematemesis";
	public static final String HEMATURIA = "hematuria";
	public static final String HEMOPTYSIS = "hemoptysis";
	public static final String HICCUPS = "hiccups";
	public static final String INJECTION_SITE_BLEEDING = "injectionSiteBleeding";
	public static final String INTENSE_FATIGUE_WEAKNESS = "intenseFatigueWeakness";
	public static final String JAUNDICE = "jaundice";
	public static final String JOINT_PAIN = "jointPain";
	public static final String MELENA = "melena";
	public static final String MUSCLE_PAIN = "musclePain";
	public static final String ONSET_DATE = "onsetDate";
	public static final String OTHER_HEMORRHAGIC = "otherHemorrhagic";
	public static final String OTHER_HEMORRHAGIC_TEXT = "otherHemorrhagicText";
	public static final String OTHER_NON_HEMORRHAGIC = "otherNonHemorrhagic";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS = "otherNonHemorrhagicSymptoms";
	public static final String PETECHIAE = "petechiae";
	public static final String SKIN_RASH = "skinRash";
	public static final String SORE_THROAT = "soreThroat";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String UNEXPLAINED_BLEEDING = "unexplainedBleeding";
	public static final String VOMITING_NAUSEA = "vomitingNausea";

	private Date onsetDate;
	
	private float temperature;
	private TemperatureSource temperatureSource;
	
	private SymptomState fever;
	private SymptomState vomitingNausea;
	private SymptomState diarrhea;
	private SymptomState intenseFatigueWeakness;
	private SymptomState anorexiaAppetiteLoss;
	private SymptomState abdominalPain;
	private SymptomState chestPain;
	private SymptomState musclePain;
	private SymptomState jointPain;
	private SymptomState headache;
	private SymptomState cough;
	private SymptomState difficultyBreathing;
	private SymptomState difficultySwallowing;
	private SymptomState soreThroat;
	private SymptomState jaundice;
	private SymptomState conjunctivitis;
	private SymptomState skinRash;
	private SymptomState hiccups;
	private SymptomState eyePainLightSensitive;
	private SymptomState comaUnconscious;
	private SymptomState confusedDisoriented;
	private SymptomState unexplainedBleeding;
	private SymptomState gumsBleeding;
	private SymptomState injectionSiteBleeding;
	private SymptomState epistaxis;
	private SymptomState melena;
	private SymptomState hematemesis;
	private SymptomState digestedBloodVomit;
	private SymptomState hemoptysis;
	private SymptomState bleedingVagina;
	private SymptomState petechiae;
	private SymptomState hematuria;
	
	private SymptomState otherHemorrhagic;
	private String otherHemorrhagicText;
	
	private SymptomState otherNonHemorrhagic;
	private String otherNonHemorrhagicSymptoms;
	public Date getOnsetDate() {
		return onsetDate;
	}
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}
	public float getTemperature() {
		return temperature;
	}
	public void setTemperature(float temperature) {
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
	public SymptomState getVomitingNausea() {
		return vomitingNausea;
	}
	public void setVomitingNausea(SymptomState vomitingNausea) {
		this.vomitingNausea = vomitingNausea;
	}
	public SymptomState getDiarrhea() {
		return diarrhea;
	}
	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}
	public SymptomState getIntenseFatigueWeakness() {
		return intenseFatigueWeakness;
	}
	public void setIntenseFatigueWeakness(SymptomState intenseFatigueWeakness) {
		this.intenseFatigueWeakness = intenseFatigueWeakness;
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
	public SymptomState getDifficultySwallowing() {
		return difficultySwallowing;
	}
	public void setDifficultySwallowing(SymptomState difficultySwallowing) {
		this.difficultySwallowing = difficultySwallowing;
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
	public SymptomState getOtherHemorrhagic() {
		return otherHemorrhagic;
	}
	public void setOtherHemorrhagic(SymptomState otherHemorrhagic) {
		this.otherHemorrhagic = otherHemorrhagic;
	}
	public String getOtherHemorrhagicText() {
		return otherHemorrhagicText;
	}
	public void setOtherHemorrhagicText(String otherHemorrhagicText) {
		this.otherHemorrhagicText = otherHemorrhagicText;
	}
	public SymptomState getOtherNonHemorrhagic() {
		return otherNonHemorrhagic;
	}
	public void setOtherNonHemorrhagic(SymptomState otherNonHemorrhagic) {
		this.otherNonHemorrhagic = otherNonHemorrhagic;
	}
	public String getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}
	public void setOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}
	
}
