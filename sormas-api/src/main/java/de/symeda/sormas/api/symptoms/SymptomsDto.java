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
	public static final String ALTERED_CONSCIOUSNESS = "alteredConsciousness";
	public static final String ANOREXIA_APPETITE_LOSS = "anorexiaAppetiteLoss";
	public static final String BLEEDING_VAGINA = "bleedingVagina";
	public static final String BLOOD_IN_STOOL = "bloodInStool";
	public static final String BLOOD_URINE = "bloodUrine";
	public static final String BLOODY_BLACK_STOOL = "bloodyBlackStool";
	public static final String CHEST_PAIN = "chestPain";
	public static final String CONFUSED_DISORIENTED = "confusedDisoriented";
	public static final String CONJUNCTIVITIS = "conjunctivitis";
	public static final String COUGH = "cough";
	public static final String COUGHING_BLOOD = "coughingBlood";
	public static final String DEHYDRATION = "dehydration";
	public static final String DIARRHEA = "diarrhea";
	public static final String DIFFICULTY_BREATHING = "difficultyBreathing";
	public static final String DIGESTED_BLOOD_VOMIT = "digestedBloodVomit";
	public static final String EYE_PAIN_LIGHT_SENSITIVE = "eyePainLightSensitive";
	public static final String FATIGUE_WEAKNESS = "fatigueWeakness";
	public static final String FEVER = "fever";
	public static final String GUMS_BLEEDING = "gumsBleeding";
	public static final String HEADACHE = "headache";
	public static final String HEARINGLOSS = "hearingloss";
	public static final String HICCUPS = "hiccups";
	public static final String INJECTION_SITE_BLEEDING = "injectionSiteBleeding";
	public static final String JOINT_PAIN = "jointPain";
	public static final String KOPLIKS_SPOTS = "kopliksSpots";
	public static final String MUSCLE_PAIN = "musclePain";
	public static final String NAUSEA = "nausea";
	public static final String NECK_STIFFNESS = "neckStiffness";
	public static final String NOSE_BLEEDING = "noseBleeding";
	public static final String ONSET_DATE = "onsetDate";
	public static final String ONSET_SYMPTOM = "onsetSymptom";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS = "otherHemorrhagicSymptoms";
	public static final String OTHER_HEMORRHAGIC_SYMPTOMS_TEXT = "otherHemorrhagicSymptomsText";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS = "otherNonHemorrhagicSymptoms";
	public static final String OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT = "otherNonHemorrhagicSymptomsText";
	public static final String OTITIS_MEDIA = "otitisMedia";
	public static final String RED_BLOOD_VOMIT = "redBloodVomit";
	public static final String REFUSAL_FEEDOR_DRINK = "refusalFeedorDrink";
	public static final String RUNNY_NOSE = "runnyNose";
	public static final String SEIZURES = "seizures";
	public static final String SHOCK = "shock";
	public static final String SKIN_BRUISING = "skinBruising";
	public static final String SKIN_RASH = "skinRash";
	public static final String SORE_THROAT = "soreThroat";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String SYMPTOMS_COMMENTS = "symptomsComments";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String THROBOCYTOPENIA = "throbocytopenia";
	public static final String UNEXPLAINED_BLEEDING = "unexplainedBleeding";
	public static final String VOMITING = "vomiting";

	private Boolean symptomatic;
	private Date onsetDate;
	private String onsetSymptom;
	
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private Float temperature;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private TemperatureSource temperatureSource;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState fever;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState vomiting;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState diarrhea;
	@Diseases({Disease.CHOLERA})
	private SymptomState bloodInStool;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA})
	private SymptomState nausea;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CHOLERA})
	private SymptomState abdominalPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM})
	private SymptomState headache;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState musclePain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState fatigueWeakness;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState unexplainedBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState gumsBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState injectionSiteBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState noseBleeding;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState bloodyBlackStool;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState redBloodVomit;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState digestedBloodVomit;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState coughingBlood;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState bleedingVagina;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState skinBruising;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState bloodUrine;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState otherHemorrhagicSymptoms;
	@Diseases({Disease.EVD,Disease.LASSA})
	private String otherHemorrhagicSymptomsText;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.MEASLES})
	private SymptomState skinRash;
	@Diseases({Disease.CSM})
	private SymptomState neckStiffness;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState soreThroat;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState cough;
	@Diseases({Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState runnyNose;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState difficultyBreathing;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA})
	private SymptomState chestPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState confusedDisoriented;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState seizures;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState alteredConsciousness;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState conjunctivitis;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.MEASLES})
	private SymptomState eyePainLightSensitive;
	@Diseases({Disease.MEASLES})
	private SymptomState kopliksSpots;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState throbocytopenia;
	@Diseases({Disease.AVIAN_INFLUENCA,Disease.MEASLES})
	private SymptomState otitisMedia;
	@Diseases({Disease.EVD,Disease.LASSA})
	private SymptomState hearingloss;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CHOLERA})
	private SymptomState dehydration;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM,Disease.CHOLERA})
	private SymptomState anorexiaAppetiteLoss;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.CSM,Disease.CHOLERA})
	private SymptomState refusalFeedorDrink;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.MEASLES})
	private SymptomState jointPain;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState shock;
	@Diseases({Disease.EVD})
	private SymptomState hiccups;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private SymptomState otherNonHemorrhagicSymptoms;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private String otherNonHemorrhagicSymptomsText;
	@Diseases({Disease.EVD,Disease.LASSA,Disease.AVIAN_INFLUENCA,Disease.CSM,Disease.CHOLERA,Disease.MEASLES})
	private String symptomsComments;


	public Boolean getSymptomatic() {
		return symptomatic;
	}
	public void setSymptomatic(Boolean symptomatic) {
		this.symptomatic = symptomatic;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public Date getOnsetDate() {
		return onsetDate;
	}
	@XmlJavaTypeAdapter(PreciseDateAdapter.class)
	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}
	public String getOnsetSymptom() {
		return onsetSymptom;
	}
	public void setOnsetSymptom(String onsetSymptom) {
		this.onsetSymptom = onsetSymptom;
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
	public SymptomState getBloodInStool() {
		return bloodInStool;
	}
	public void setBloodInStool(SymptomState bloodInStool) {
		this.bloodInStool = bloodInStool;
	}
	public SymptomState getNausea() {
		return nausea;
	}
	public void setNausea(SymptomState nausea) {
		this.nausea = nausea;
	}
	public SymptomState getAbdominalPain() {
		return abdominalPain;
	}
	public void setAbdominalPain(SymptomState abdominalPain) {
		this.abdominalPain = abdominalPain;
	}
	public SymptomState getHeadache() {
		return headache;
	}
	public void setHeadache(SymptomState headache) {
		this.headache = headache;
	}
	public SymptomState getMusclePain() {
		return musclePain;
	}
	public void setMusclePain(SymptomState musclePain) {
		this.musclePain = musclePain;
	}
	public SymptomState getFatigueWeakness() {
		return fatigueWeakness;
	}
	public void setFatigueWeakness(SymptomState fatigueWeakness) {
		this.fatigueWeakness = fatigueWeakness;
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
	public SymptomState getDigestedBloodVomit() {
		return digestedBloodVomit;
	}
	public void setDigestedBloodVomit(SymptomState digestedBloodVomit) {
		this.digestedBloodVomit = digestedBloodVomit;
	}
	public SymptomState getCoughingBlood() {
		return coughingBlood;
	}
	public void setCoughingBlood(SymptomState coughingBlood) {
		this.coughingBlood = coughingBlood;
	}
	public SymptomState getBleedingVagina() {
		return bleedingVagina;
	}
	public void setBleedingVagina(SymptomState bleedingVagina) {
		this.bleedingVagina = bleedingVagina;
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
	public SymptomState getSkinRash() {
		return skinRash;
	}
	public void setSkinRash(SymptomState skinRash) {
		this.skinRash = skinRash;
	}
	public SymptomState getNeckStiffness() {
		return neckStiffness;
	}
	public void setNeckStiffness(SymptomState neckStiffness) {
		this.neckStiffness = neckStiffness;
	}
	public SymptomState getSoreThroat() {
		return soreThroat;
	}
	public void setSoreThroat(SymptomState soreThroat) {
		this.soreThroat = soreThroat;
	}
	public SymptomState getCough() {
		return cough;
	}
	public void setCough(SymptomState cough) {
		this.cough = cough;
	}
	public SymptomState getRunnyNose() {
		return runnyNose;
	}
	public void setRunnyNose(SymptomState runnyNose) {
		this.runnyNose = runnyNose;
	}
	public SymptomState getDifficultyBreathing() {
		return difficultyBreathing;
	}
	public void setDifficultyBreathing(SymptomState difficultyBreathing) {
		this.difficultyBreathing = difficultyBreathing;
	}
	public SymptomState getChestPain() {
		return chestPain;
	}
	public void setChestPain(SymptomState chestPain) {
		this.chestPain = chestPain;
	}
	public SymptomState getConfusedDisoriented() {
		return confusedDisoriented;
	}
	public void setConfusedDisoriented(SymptomState confusedDisoriented) {
		this.confusedDisoriented = confusedDisoriented;
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
	public SymptomState getConjunctivitis() {
		return conjunctivitis;
	}
	public void setConjunctivitis(SymptomState conjunctivitis) {
		this.conjunctivitis = conjunctivitis;
	}
	public SymptomState getEyePainLightSensitive() {
		return eyePainLightSensitive;
	}
	public void setEyePainLightSensitive(SymptomState eyePainLightSensitive) {
		this.eyePainLightSensitive = eyePainLightSensitive;
	}
	public SymptomState getKopliksSpots() {
		return kopliksSpots;
	}
	public void setKopliksSpots(SymptomState kopliksSpots) {
		this.kopliksSpots = kopliksSpots;
	}
	public SymptomState getThrobocytopenia() {
		return throbocytopenia;
	}
	public void setThrobocytopenia(SymptomState throbocytopenia) {
		this.throbocytopenia = throbocytopenia;
	}
	public SymptomState getOtitisMedia() {
		return otitisMedia;
	}
	public void setOtitisMedia(SymptomState otitisMedia) {
		this.otitisMedia = otitisMedia;
	}
	public SymptomState getHearingloss() {
		return hearingloss;
	}
	public void setHearingloss(SymptomState hearingloss) {
		this.hearingloss = hearingloss;
	}
	public SymptomState getDehydration() {
		return dehydration;
	}
	public void setDehydration(SymptomState dehydration) {
		this.dehydration = dehydration;
	}
	public SymptomState getAnorexiaAppetiteLoss() {
		return anorexiaAppetiteLoss;
	}
	public void setAnorexiaAppetiteLoss(SymptomState anorexiaAppetiteLoss) {
		this.anorexiaAppetiteLoss = anorexiaAppetiteLoss;
	}
	public SymptomState getRefusalFeedorDrink() {
		return refusalFeedorDrink;
	}
	public void setRefusalFeedorDrink(SymptomState refusalFeedorDrink) {
		this.refusalFeedorDrink = refusalFeedorDrink;
	}
	public SymptomState getJointPain() {
		return jointPain;
	}
	public void setJointPain(SymptomState jointPain) {
		this.jointPain = jointPain;
	}
	public SymptomState getShock() {
		return shock;
	}
	public void setShock(SymptomState shock) {
		this.shock = shock;
	}
	public SymptomState getHiccups() {
		return hiccups;
	}
	public void setHiccups(SymptomState hiccups) {
		this.hiccups = hiccups;
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SymptomsDto other = (SymptomsDto) obj;
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
