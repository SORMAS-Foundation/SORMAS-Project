package de.symeda.sormas.app.backend.symptoms;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.app.backend.common.AbstractDomainObject;
import de.symeda.sormas.app.component.SymptomStateField;

@Entity(name= Symptoms.TABLE_NAME)
@DatabaseTable(tableName = Symptoms.TABLE_NAME)
public class Symptoms extends AbstractDomainObject {
	
	private static final long serialVersionUID = 392886645668778670L;

	public static final String TABLE_NAME = "symptoms";

	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date onsetDate;
	@Column(length = 255)
	private String onsetSymptom;
	@Column(length = 255)
	private String symptomsComments;
	private Boolean symptomatic;

	@Column(columnDefinition = "float8")
	private Float temperature;
	@Enumerated(EnumType.STRING)
	private TemperatureSource temperatureSource;
	@Enumerated(EnumType.STRING)
	private SymptomState fever;
	@Enumerated(EnumType.STRING)
	private SymptomState vomiting;
	@Enumerated(EnumType.STRING)
	private SymptomState diarrhea;
	@Enumerated(EnumType.STRING)
	private SymptomState bloodInStool;
	@Enumerated(EnumType.STRING)
	private SymptomState nausea;
	@Enumerated(EnumType.STRING)
	private SymptomState abdominalPain;
	@Enumerated(EnumType.STRING)
	private SymptomState headache;
	@Enumerated(EnumType.STRING)
	private SymptomState musclePain;
	@Enumerated(EnumType.STRING)
	private SymptomState fatigueWeakness;
	@Enumerated(EnumType.STRING)
	private SymptomState unexplainedBleeding;
	@Enumerated(EnumType.STRING)
	private SymptomState gumsBleeding;
	@Enumerated(EnumType.STRING)
	private SymptomState injectionSiteBleeding;
	@Enumerated(EnumType.STRING)
	private SymptomState noseBleeding;
	@Enumerated(EnumType.STRING)
	private SymptomState bloodyBlackStool;
	@Enumerated(EnumType.STRING)
	private SymptomState redBloodVomit;
	@Enumerated(EnumType.STRING)
	private SymptomState digestedBloodVomit;
	@Enumerated(EnumType.STRING)
	private SymptomState coughingBlood;
	@Enumerated(EnumType.STRING)
	private SymptomState bleedingVagina;
	@Enumerated(EnumType.STRING)
	private SymptomState skinBruising;
	@Enumerated(EnumType.STRING)
	private SymptomState bloodUrine;
	@Enumerated(EnumType.STRING)
	private SymptomState skinRash;
	@Enumerated(EnumType.STRING)
	private SymptomState neckStiffness;
	@Enumerated(EnumType.STRING)
	private SymptomState soreThroat;
	@Enumerated(EnumType.STRING)
	private SymptomState cough;
	@Enumerated(EnumType.STRING)
	private SymptomState runnyNose;
	@Enumerated(EnumType.STRING)
	private SymptomState difficultyBreathing;
	@Enumerated(EnumType.STRING)
	private SymptomState chestPain;
	@Enumerated(EnumType.STRING)
	private SymptomState confusedDisoriented;
	@Enumerated(EnumType.STRING)
	private SymptomState seizures;
	@Enumerated(EnumType.STRING)
	private SymptomState alteredConsciousness;
	@Enumerated(EnumType.STRING)
	private SymptomState conjunctivitis;
	@Enumerated(EnumType.STRING)
	private SymptomState eyePainLightSensitive;
	@Enumerated(EnumType.STRING)
	private SymptomState kopliksSpots;
	@Enumerated(EnumType.STRING)
	private SymptomState throbocytopenia;
	@Enumerated(EnumType.STRING)
	private SymptomState otitisMedia;
	@Enumerated(EnumType.STRING)
	private SymptomState hearingLoss;
	@Enumerated(EnumType.STRING)
	private SymptomState dehydration;
	@Enumerated(EnumType.STRING)
	private SymptomState anorexiaAppetiteLoss;
	@Enumerated(EnumType.STRING)
	private SymptomState refusalFeedorDrink;
	@Enumerated(EnumType.STRING)
	private SymptomState jointPain;
	@Enumerated(EnumType.STRING)
	private SymptomState shock;
	@Enumerated(EnumType.STRING)
	private SymptomState hiccups;

	@Enumerated(EnumType.STRING)
	private SymptomState otherHemorrhagicSymptoms;
	@Column(length = 255)
	private String otherHemorrhagicSymptomsText;
	@Enumerated(EnumType.STRING)
	private SymptomState otherNonHemorrhagicSymptoms;
	@Column(length = 255)
	private String otherNonHemorrhagicSymptomsText;

	public Date getOnsetDate() {
		return onsetDate;
	}

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
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}
	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}
	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
	}

	public SymptomState getDehydration() {
		return dehydration;
	}

	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}

	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}

	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
	}

	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}

	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}

	public SymptomState getNausea() {
		return nausea;
	}

	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}

	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}

	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}

	public String getOnsetSymptom() {
		return onsetSymptom;
	}

	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
	}

	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}

	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
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

	public Boolean getSymptomatic() {
		return symptomatic;
	}

	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}

	public SymptomState getVomiting() {
		return vomiting;
	}

	public void setVomiting(SymptomState vomiting) {
		this.vomiting = vomiting;
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

	public String getSymptomsComments() {
		return symptomsComments;
	}

	public void setSymptomsComments(String symptomsComments) {
		this.symptomsComments = symptomsComments;
	}

	public SymptomState getBloodInStool() {
		return bloodInStool;
	}

	public void setBloodInStool(SymptomState bloodInStool) {
		this.bloodInStool = bloodInStool;
	}

	public SymptomState getNoseBleeding() {
		return noseBleeding;
	}

	public void setNoseBleeding(SymptomState noseBleeding) {
		this.noseBleeding = noseBleeding;
	}

	public SymptomState getBloodyBlackStool() {
		return bloodyBlackStool;
	}

	public void setBloodyBlackStool(SymptomState bloodyBlackStool) {
		this.bloodyBlackStool = bloodyBlackStool;
	}

	public SymptomState getRedBloodVomit() {
		return redBloodVomit;
	}

	public void setRedBloodVomit(SymptomState redBloodVomit) {
		this.redBloodVomit = redBloodVomit;
	}

	public SymptomState getCoughingBlood() {
		return coughingBlood;
	}

	public void setCoughingBlood(SymptomState coughingBlood) {
		this.coughingBlood = coughingBlood;
	}

	public SymptomState getSkinBruising() {
		return skinBruising;
	}

	public void setSkinBruising(SymptomState skinBruising) {
		this.skinBruising = skinBruising;
	}

	public SymptomState getBloodUrine() {
		return bloodUrine;
	}

	public void setBloodUrine(SymptomState bloodUrine) {
		this.bloodUrine = bloodUrine;
	}

	public SymptomState getThrobocytopenia() {
		return throbocytopenia;
	}

	public void setThrobocytopenia(SymptomState throbocytopenia) {
		this.throbocytopenia = throbocytopenia;
	}

	public SymptomState getHearingloss() {
		return hearingLoss;
	}

	public void setHearingloss(SymptomState hearingLoss) {
		this.hearingLoss = hearingLoss;
	}

	public SymptomState getShock() {
		return shock;
	}

	public void setShock(SymptomState shock) {
		this.shock = shock;
	}

	public SymptomState getSeizures() {
		return seizures;
	}

	public void setSeizures(SymptomState seizures) {
		this.seizures = seizures;
	}

	public SymptomState getAlteredConsciousness() {
		return alteredConsciousness;
	}

	public void setAlteredConsciousness(SymptomState alteredConsciousness) {
		this.alteredConsciousness = alteredConsciousness;
	}
}
