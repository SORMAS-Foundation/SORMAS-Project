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
	public SymptomState getVomitingNausea() {
		return vomitingNausea;
	}
	public void setVomitingNausea(SymptomState vomitingNausea) {
		this.vomitingNausea = vomitingNausea;
	}
	@Enumerated(EnumType.STRING)
	public SymptomState getDiarrhea() {
		return diarrhea;
	}
	public void setDiarrhea(SymptomState diarrhea) {
		this.diarrhea = diarrhea;
	}
	@Enumerated(EnumType.STRING)
	public SymptomState getIntenseFatigueWeakness() {
		return intenseFatigueWeakness;
	}
	public void setIntenseFatigueWeakness(SymptomState intenseFatigueWeakness) {
		this.intenseFatigueWeakness = intenseFatigueWeakness;
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
	public SymptomState getDifficultySwallowing() {
		return difficultySwallowing;
	}
	public void setDifficultySwallowing(SymptomState difficultySwallowing) {
		this.difficultySwallowing = difficultySwallowing;
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
	@Enumerated(EnumType.STRING)
	public SymptomState getOtherHemorrhagic() {
		return otherHemorrhagic;
	}
	public void setOtherHemorrhagic(SymptomState otherHemorrhagic) {
		this.otherHemorrhagic = otherHemorrhagic;
	}
	@Column(length=255)
	public String getOtherHemorrhagicText() {
		return otherHemorrhagicText;
	}
	public void setOtherHemorrhagicText(String otherHemorrhagicText) {
		this.otherHemorrhagicText = otherHemorrhagicText;
	}
	@Enumerated(EnumType.STRING)
	public SymptomState getOtherNonHemorrhagic() {
		return otherNonHemorrhagic;
	}
	public void setOtherNonHemorrhagic(SymptomState otherNonHemorrhagic) {
		this.otherNonHemorrhagic = otherNonHemorrhagic;
	}
	@Column(length=255)
	public String getOtherNonHemorrhagicSymptoms() {
		return otherNonHemorrhagicSymptoms;
	}
	public void setOtherNonHemorrhagicSymptoms(String otherNonHemorrhagicSymptoms) {
		this.otherNonHemorrhagicSymptoms = otherNonHemorrhagicSymptoms;
	}
	
}
