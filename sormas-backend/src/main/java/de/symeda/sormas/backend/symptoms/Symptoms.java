package de.symeda.sormas.backend.symptoms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
public class Symptoms extends AbstractDomainObject {

	private static final long serialVersionUID = 1467852910743225822L;

	private Date onsetDate;

	private Float temperature;
	private TemperatureSource temperatureSource;

	private SymptomState fever;
	private SymptomState diarrhea;
	private SymptomState anorexiaAppetiteLoss;
	private SymptomState abdominalPain;
	private SymptomState chestPain;
	private SymptomState musclePain;
	private SymptomState jointPain;
	private SymptomState headache;
	private SymptomState cough;
	private SymptomState difficultyBreathing;
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

	private SymptomState chills;

	private SymptomState dehydration;

	private SymptomState fatigueWeakness;

	private SymptomState highBloodPressure;

	private SymptomState kopliksSpots;

	private SymptomState lethargy;

	private SymptomState lowBloodPressure;

	private SymptomState nausea;

	private SymptomState neckStiffness;

	private SymptomState oedema;

	private String onsetSymptom;

	private SymptomState otitisMedia;

	private SymptomState refusalFeedorDrink;

	private SymptomState runnyNose;

	private SymptomState seizures;

	private SymptomState sepsis;

	private SymptomState swollenLymphNodes;

	private Boolean symptomatic;

	private SymptomState vomiting;

	private SymptomState otherHemorrhagicSymptoms;

	private String otherHemorrhagicSymptomsText;

	private SymptomState otherNonHemorrhagicSymptoms;

	private String otherNonHemorrhagicSymptomsText;

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	@Column(columnDefinition = "float8")
	public Float getTemperature() {
		return temperature;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	@Enumerated(EnumType.STRING)
	public TemperatureSource getTemperatureSource() {
		return temperatureSource;
	}

	public void setTemperatureSource(TemperatureSource temperatureSource) {
		this.temperatureSource = temperatureSource;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getFever() {
		return fever;
	}

	public void setFever(SymptomState fever) {
		this.fever = fever;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDiarrhea() {
		return diarrhea;
	}

	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAnorexiaAppetiteLoss() {
		return anorexiaAppetiteLoss;
	}

	public void setAnorexiaAppetiteLoss(SymptomState anorexiaAppetiteLoss) {
		this.anorexiaAppetiteLoss = anorexiaAppetiteLoss;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}

	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getChestPain() {
		return chestPain;
	}

	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMusclePain() {
		return musclePain;
	}

	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getJointPain() {
		return jointPain;
	}

	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHeadache() {
		return headache;
	}

	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getCough() {
		return cough;
	}

	public void setCough(SymptomState cough) {
		this.cough = cough;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}

	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSoreThroat() {
		return soreThroat;
	}

	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getJaundice() {
		return jaundice;
	}

	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}

	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSkinRash() {
		return skinRash;
	}

	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHiccups() {
		return hiccups;
	}

	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getEyePainLightSensitive() {
		return eyePainLightSensitive;
	}

	public void setEyePainLightSensitive(SymptomState eyePainLightSensitive) {
		this.eyePainLightSensitive = eyePainLightSensitive;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getComaUnconscious() {
		return comaUnconscious;
	}

	public void setComaUnconscious(SymptomState comaUnconscious) {
		this.comaUnconscious = comaUnconscious;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getConfusedDisoriented() {
		return confusedDisoriented;
	}

	public void setConfusedDisoriented(SymptomState confusedDisoriented) {
		this.confusedDisoriented = confusedDisoriented;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getUnexplainedBleeding() {
		return unexplainedBleeding;
	}

	public void setUnexplainedBleeding(SymptomState unexplainedBleeding) {
		this.unexplainedBleeding = unexplainedBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getGumsBleeding() {
		return gumsBleeding;
	}

	public void setGumsBleeding(SymptomState gumsBleeding) {
		this.gumsBleeding = gumsBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getInjectionSiteBleeding() {
		return injectionSiteBleeding;
	}

	public void setInjectionSiteBleeding(SymptomState injectionSiteBleeding) {
		this.injectionSiteBleeding = injectionSiteBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getEpistaxis() {
		return epistaxis;
	}

	public void setEpistaxis(SymptomState epistaxis) {
		this.epistaxis = epistaxis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMelena() {
		return melena;
	}

	public void setMelena(SymptomState melena) {
		this.melena = melena;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHematemesis() {
		return hematemesis;
	}

	public void setHematemesis(SymptomState hematemesis) {
		this.hematemesis = hematemesis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}

	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHemoptysis() {
		return hemoptysis;
	}

	public void setHemoptysis(SymptomState hemoptysis) {
		this.hemoptysis = hemoptysis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}

	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPetechiae() {
		return petechiae;
	}

	public void setPetechiae(SymptomState petechiae) {
		this.petechiae = petechiae;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHematuria() {
		return hematuria;
	}

	public void setHematuria(SymptomState hematuria) {
		this.hematuria = hematuria;
	}

	public void setChills(SymptomState chills) {
		this.chills = chills;
	}

	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}

	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}

	public void setHighBloodPressure(SymptomState highBloodPressure) {
		this.highBloodPressure = highBloodPressure;
	}

	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}

	public void setLethargy(SymptomState lethargy) {
		this.lethargy = lethargy;
	}

	public void setLowBloodPressure(SymptomState lowBloodPressure) {
		this.lowBloodPressure = lowBloodPressure;
	}

	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}

	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}

	public void setOedema(SymptomState oedema) {
		this.oedema = oedema;
	}

	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
	}

	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
	}

	public void setRefusalFeedorDrink(SymptomState refusalFeedorDrink) {
		this.refusalFeedorDrink = refusalFeedorDrink;
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

	public void setSwollenLymphNodes(SymptomState swollenLymphNodes) {
		this.swollenLymphNodes = swollenLymphNodes;
	}

	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
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

	@Enumerated(EnumType.STRING)
	public SymptomState getChills() {
		return chills;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDehydration() {
		return dehydration;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHighBloodPressure() {
		return highBloodPressure;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLethargy() {
		return lethargy;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLowBloodPressure() {
		return lowBloodPressure;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getNausea() {
		return nausea;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOedema() {
		return oedema;
	}

	@Column(length = 255)
	public String getOnsetSymptom() {
		return onsetSymptom;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getRefusalFeedorDrink() {
		return refusalFeedorDrink;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getRunnyNose() {
		return runnyNose;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSeizures() {
		return seizures;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSepsis() {
		return sepsis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSwollenLymphNodes() {
		return swollenLymphNodes;
	}

	public Boolean getSymptomatic() {
		return symptomatic;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getVomiting() {
		return vomiting;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOtherHemorrhagicSymptoms() {
		return otherHemorrhagicSymptoms;
	}

	@Column(length = 255)
	public String getOtherHemorrhagicSymptomsText() {
		return otherHemorrhagicSymptomsText;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}

	@Column(length = 255)
	public String getOtherNonHemorrhagicSymptomsText() {
		return otherNonHemorrhagicSymptomsText;
	}

}
