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
	private String onsetSymptom;
	private Boolean symptomatic;

	private Float temperature;
	private TemperatureSource temperatureSource;
	private SymptomState fever;
	private SymptomState vomiting;
	private SymptomState diarrhea;
	private SymptomState bloodInStool;
	private SymptomState nausea;
	private SymptomState abdominalPain;
	private SymptomState headache;
	private SymptomState musclePain;
	private SymptomState fatigueWeakness;
	private SymptomState unexplainedBleeding;
	private SymptomState gumsBleeding;
	private SymptomState injectionSiteBleeding;
	private SymptomState noseBleeding;
	private SymptomState bloodyBlackStool;
	private SymptomState redBloodVomit;
	private SymptomState digestedBloodVomit;
	private SymptomState coughingBlood;
	private SymptomState bleedingVagina;
	private SymptomState skinBruising;
	private SymptomState bloodUrine;
	private SymptomState otherHemorrhagicSymptoms;
	private String otherHemorrhagicSymptomsText;
	private SymptomState skinRash;
	private SymptomState neckStiffness;
	private SymptomState soreThroat;
	private SymptomState cough;
	private SymptomState runnyNose;
	private SymptomState difficultyBreathing;
	private SymptomState chestPain;
	private SymptomState confusedDisoriented;
	private SymptomState seizures;
	private SymptomState alteredConsciousness;
	private SymptomState conjunctivitis;
	private SymptomState eyePainLightSensitive;
	private SymptomState kopliksSpots;
	private SymptomState throbocytopenia;
	private SymptomState otitisMedia;
	private SymptomState hearingloss;
	private SymptomState dehydration;
	private SymptomState anorexiaAppetiteLoss;
	private SymptomState refusalFeedorDrink;
	private SymptomState jointPain;
	private SymptomState shock;
	private SymptomState hiccups;
	private SymptomState otherNonHemorrhagicSymptoms;
	private String otherNonHemorrhagicSymptomsText;
	private String symptomsComments;
	
	@Temporal(TemporalType.DATE)
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
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}

	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}

	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}

	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}

	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}

	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}
	
	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}

	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
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

	public void setBloodInStool(SymptomState bloodInStool) {
		this.bloodInStool = bloodInStool;
	}

	public void setNoseBleeding(SymptomState noseBleeding) {
		this.noseBleeding = noseBleeding;
	}

	public void setBloodyBlackStool(SymptomState bloodyBlackStool) {
		this.bloodyBlackStool = bloodyBlackStool;
	}

	public void setRedBloodVomit(SymptomState redBloodVomit) {
		this.redBloodVomit = redBloodVomit;
	}

	public void setCoughingBlood(SymptomState coughingBlood) {
		this.coughingBlood = coughingBlood;
	}

	public void setSkinBruising(SymptomState skinBruising) {
		this.skinBruising = skinBruising;
	}

	public void setBloodUrine(SymptomState bloodUrine) {
		this.bloodUrine = bloodUrine;
	}

	public void setAlteredConsciousness(SymptomState alteredConsciousness) {
		this.alteredConsciousness = alteredConsciousness;
	}

	public void setThrobocytopenia(SymptomState throbocytopenia) {
		this.throbocytopenia = throbocytopenia;
	}

	public void setHearingloss(SymptomState hearingloss) {
		this.hearingloss = hearingloss;
	}

	public void setShock(SymptomState shock) {
		this.shock = shock;
	}

	public void setSymptomsComments(String symptomsComments) {
		this.symptomsComments = symptomsComments;
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
	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getNausea() {
		return nausea;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getNeckStiffness() {
		return neckStiffness;
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

	@Enumerated(EnumType.STRING)
	public SymptomState getBloodInStool() {
		return bloodInStool;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getNoseBleeding() {
		return noseBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBloodyBlackStool() {
		return bloodyBlackStool;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getRedBloodVomit() {
		return redBloodVomit;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getCoughingBlood() {
		return coughingBlood;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSkinBruising() {
		return skinBruising;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBloodUrine() {
		return bloodUrine;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAlteredConsciousness() {
		return alteredConsciousness;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getThrobocytopenia() {
		return throbocytopenia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHearingloss() {
		return hearingloss;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getShock() {
		return shock;
	}

	@Column(length = 255)
	public String getSymptomsComments() {
		return symptomsComments;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Symptoms other = (Symptoms) obj;
		if (abdominalPain != other.abdominalPain)
			return false;
		if (alteredConsciousness != other.alteredConsciousness)
			return false;
		if (anorexiaAppetiteLoss != other.anorexiaAppetiteLoss)
			return false;
		if (bleedingVagina != other.bleedingVagina)
			return false;
		if (bloodInStool != other.bloodInStool)
			return false;
		if (bloodUrine != other.bloodUrine)
			return false;
		if (bloodyBlackStool != other.bloodyBlackStool)
			return false;
		if (chestPain != other.chestPain)
			return false;
		if (confusedDisoriented != other.confusedDisoriented)
			return false;
		if (conjunctivitis != other.conjunctivitis)
			return false;
		if (cough != other.cough)
			return false;
		if (coughingBlood != other.coughingBlood)
			return false;
		if (dehydration != other.dehydration)
			return false;
		if (diarrhea != other.diarrhea)
			return false;
		if (difficultyBreathing != other.difficultyBreathing)
			return false;
		if (digestedBloodVomit != other.digestedBloodVomit)
			return false;
		if (eyePainLightSensitive != other.eyePainLightSensitive)
			return false;
		if (fatigueWeakness != other.fatigueWeakness)
			return false;
		if (fever != other.fever)
			return false;
		if (gumsBleeding != other.gumsBleeding)
			return false;
		if (headache != other.headache)
			return false;
		if (hearingloss != other.hearingloss)
			return false;
		if (hiccups != other.hiccups)
			return false;
		if (injectionSiteBleeding != other.injectionSiteBleeding)
			return false;
		if (jointPain != other.jointPain)
			return false;
		if (kopliksSpots != other.kopliksSpots)
			return false;
		if (musclePain != other.musclePain)
			return false;
		if (nausea != other.nausea)
			return false;
		if (neckStiffness != other.neckStiffness)
			return false;
		if (noseBleeding != other.noseBleeding)
			return false;
		if (onsetDate == null) {
			if (other.onsetDate != null)
				return false;
		} else if (!onsetDate.equals(other.onsetDate))
			return false;
		if (onsetSymptom == null) {
			if (other.onsetSymptom != null)
				return false;
		} else if (!onsetSymptom.equals(other.onsetSymptom))
			return false;
		if (otherHemorrhagicSymptoms != other.otherHemorrhagicSymptoms)
			return false;
		if (otherHemorrhagicSymptomsText == null) {
			if (other.otherHemorrhagicSymptomsText != null)
				return false;
		} else if (!otherHemorrhagicSymptomsText.equals(other.otherHemorrhagicSymptomsText))
			return false;
		if (otherNonHemorrhagicSymptoms != other.otherNonHemorrhagicSymptoms)
			return false;
		if (otherNonHemorrhagicSymptomsText == null) {
			if (other.otherNonHemorrhagicSymptomsText != null)
				return false;
		} else if (!otherNonHemorrhagicSymptomsText.equals(other.otherNonHemorrhagicSymptomsText))
			return false;
		if (otitisMedia != other.otitisMedia)
			return false;
		if (redBloodVomit != other.redBloodVomit)
			return false;
		if (refusalFeedorDrink != other.refusalFeedorDrink)
			return false;
		if (runnyNose != other.runnyNose)
			return false;
		if (seizures != other.seizures)
			return false;
		if (shock != other.shock)
			return false;
		if (skinBruising != other.skinBruising)
			return false;
		if (skinRash != other.skinRash)
			return false;
		if (soreThroat != other.soreThroat)
			return false;
		if (symptomatic == null) {
			if (other.symptomatic != null)
				return false;
		} else if (!symptomatic.equals(other.symptomatic))
			return false;
		if (symptomsComments == null) {
			if (other.symptomsComments != null)
				return false;
		} else if (!symptomsComments.equals(other.symptomsComments))
			return false;
		if (temperature == null) {
			if (other.temperature != null)
				return false;
		} else if (!temperature.equals(other.temperature))
			return false;
		if (temperatureSource != other.temperatureSource)
			return false;
		if (throbocytopenia != other.throbocytopenia)
			return false;
		if (unexplainedBleeding != other.unexplainedBleeding)
			return false;
		if (vomiting != other.vomiting)
			return false;
		return true;
	}

}
