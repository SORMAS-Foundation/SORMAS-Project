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
package de.symeda.sormas.backend.symptoms;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Entity
@Audited
public class Symptoms extends AbstractDomainObject {

	private static final long serialVersionUID = 1467852910743225822L;

	public static final String TABLE_NAME = "symptoms";

	public static final String ONSET_DATE = "onsetDate";
	public static final String SYMPTOMATIC = "symptomatic";
	public static final String TEMPERATURE = "temperature";
	public static final String TEMPERATURE_SOURCE = "temperatureSource";
	public static final String BLOOD_PRESSURE_SYSTOLIC = "bloodPressureSystolic";
	public static final String BLOOD_PRESSURE_DIASTOLIC = "bloodPressureDiastolic";
	public static final String HEART_RATE = "heartRate";

	private Date onsetDate;
	private String onsetSymptom;
	private Boolean symptomatic;
	private String patientIllLocation;

	private Float temperature;
	private TemperatureSource temperatureSource;
	private Integer bloodPressureSystolic;
	private Integer bloodPressureDiastolic;
	private Integer heartRate;
	private Integer respiratoryRate;
	private Integer weight;
	private Integer height;
	private Integer midUpperArmCircumference;
	private Integer glasgowComaScale;

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
	private SymptomState hiccups;
	private SymptomState otherNonHemorrhagicSymptoms;
	private SymptomState backache;
	private SymptomState eyesBleeding;
	private SymptomState jaundice;
	private YesNoUnknown jaundiceWithin24HoursOfBirth;
	private SymptomState darkUrine;
	private SymptomState stomachBleeding;
	private SymptomState rapidBreathing;
	private SymptomState swollenGlands;
	private SymptomState lesions;
	private SymptomState lesionsSameState;
	private SymptomState lesionsSameSize;
	private SymptomState lesionsDeepProfound;
	private SymptomState lesionsThatItch;
	private Boolean lesionsFace;
	private Boolean lesionsLegs;
	private Boolean lesionsSolesFeet;
	private Boolean lesionsPalmsHands;
	private Boolean lesionsThorax;
	private Boolean lesionsArms;
	private Boolean lesionsGenitals;
	private Boolean lesionsAllOverBody;
	private SymptomState lesionsResembleImg1;
	private SymptomState lesionsResembleImg2;
	private SymptomState lesionsResembleImg3;
	private SymptomState lesionsResembleImg4;
	private Date lesionsOnsetDate;
	private SymptomState lymphadenopathyInguinal;
	private SymptomState lymphadenopathyAxillary;
	private SymptomState lymphadenopathyCervical;
	private SymptomState chillsSweats;
	private SymptomState bedridden;
	private SymptomState oralUlcers;
	private SymptomState painfulLymphadenitis;
	private SymptomState blackeningDeathOfTissue;
	private SymptomState buboesGroinArmpitNeck;
	private SymptomState bulgingFontanelle;
	private SymptomState pharyngealErythema;
	private SymptomState pharyngealExudate;
	private SymptomState oedemaFaceNeck;
	private SymptomState oedemaLowerExtremity;
	private SymptomState lossSkinTurgor;
	private SymptomState palpableLiver;
	private SymptomState palpableSpleen;
	private SymptomState malaise;
	private SymptomState sunkenEyesFontanelle;
	private SymptomState sidePain;
	private SymptomState fluidInLungCavity;
	private SymptomState tremor;
	private SymptomState bilateralCataracts;
	private SymptomState unilateralCataracts;
	private SymptomState congenitalGlaucoma;
	private SymptomState pigmentaryRetinopathy;
	private SymptomState purpuricRash;
	private SymptomState microcephaly;
	private SymptomState developmentalDelay;
	private SymptomState splenomegaly;
	private SymptomState meningoencephalitis;
	private SymptomState radiolucentBoneDisease;
	private SymptomState congenitalHeartDisease;
	private CongenitalHeartDiseaseType congenitalHeartDiseaseType;
	private String congenitalHeartDiseaseDetails;
	private SymptomState hydrophobia;
	private SymptomState opisthotonus;
	private SymptomState anxietyStates;
	private SymptomState delirium;
	private SymptomState uproariousness;
	private SymptomState paresthesiaAroundWound;
	private SymptomState excessSalivation;
	private SymptomState insomnia;
	private SymptomState paralysis;
	private SymptomState excitation;
	private SymptomState dysphagia;
	private SymptomState aerophobia;
	private SymptomState hyperactivity;
	private SymptomState paresis;
	private SymptomState agitation;
	private SymptomState ascendingFlaccidParalysis;
	private SymptomState erraticBehaviour;
	private SymptomState coma;
	private String otherNonHemorrhagicSymptomsText;
	private String symptomsComments;
	private SymptomState convulsion;

	// complications
	private SymptomState alteredConsciousness;
	private SymptomState confusedDisoriented;
	private SymptomState hemorrhagicSyndrome;
	private SymptomState hyperglycemia;
	private SymptomState hypoglycemia;
	private SymptomState meningealSigns;
	private SymptomState seizures;
	private SymptomState sepsis;
	private SymptomState shock;
	private SymptomState fluidInLungCavityAuscultation;
	private SymptomState fluidInLungCavityXray;
	private SymptomState abnormalLungXrayFindings;
	private SymptomState conjunctivalInjection;
	private SymptomState acuteRespiratoryDistressSyndrome;
	private SymptomState pneumoniaClinicalOrRadiologic;

	// when adding new fields make sure to extend toHumanString

	@Temporal(TemporalType.TIMESTAMP)
	public Date getOnsetDate() {
		return onsetDate;
	}

	public void setOnsetDate(Date onsetDate) {
		this.onsetDate = onsetDate;
	}

	@Column(length = 255)
	public String getPatientIllLocation() {
		return patientIllLocation;
	}

	public void setPatientIllLocation(String patientIllLocation) {
		this.patientIllLocation = patientIllLocation;
	}

	@Column(columnDefinition = "float4")
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

	public Integer getBloodPressureSystolic() {
		return bloodPressureSystolic;
	}

	public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
		this.bloodPressureSystolic = bloodPressureSystolic;
	}

	public Integer getBloodPressureDiastolic() {
		return bloodPressureDiastolic;
	}

	public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
		this.bloodPressureDiastolic = bloodPressureDiastolic;
	}

	public Integer getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(Integer heartRate) {
		this.heartRate = heartRate;
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

	@Enumerated(EnumType.STRING)
	public SymptomState getBackache() {
		return backache;
	}

	public void setBackache(SymptomState backache) {
		this.backache = backache;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getEyesBleeding() {
		return eyesBleeding;
	}

	public void setEyesBleeding(SymptomState eyesBleeding) {
		this.eyesBleeding = eyesBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getJaundice() {
		return jaundice;
	}

	public void setJaundice(SymptomState jaundice) {
		this.jaundice = jaundice;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDarkUrine() {
		return darkUrine;
	}

	public void setDarkUrine(SymptomState darkUrine) {
		this.darkUrine = darkUrine;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getStomachBleeding() {
		return stomachBleeding;
	}

	public void setStomachBleeding(SymptomState stomachBleeding) {
		this.stomachBleeding = stomachBleeding;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getRapidBreathing() {
		return rapidBreathing;
	}

	public void setRapidBreathing(SymptomState rapidBreathing) {
		this.rapidBreathing = rapidBreathing;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSwollenGlands() {
		return swollenGlands;
	}

	public void setSwollenGlands(SymptomState swollenGlands) {
		this.swollenGlands = swollenGlands;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesions() {
		return lesions;
	}

	public void setLesions(SymptomState lesions) {
		this.lesions = lesions;
	}

	public Boolean getLesionsFace() {
		return lesionsFace;
	}

	public Boolean getLesionsLegs() {
		return lesionsLegs;
	}

	public Boolean getLesionsSolesFeet() {
		return lesionsSolesFeet;
	}

	public Boolean getLesionsPalmsHands() {
		return lesionsPalmsHands;
	}

	public Boolean getLesionsThorax() {
		return lesionsThorax;
	}

	public Boolean getLesionsArms() {
		return lesionsArms;
	}

	public Boolean getLesionsGenitals() {
		return lesionsGenitals;
	}

	public Boolean getLesionsAllOverBody() {
		return lesionsAllOverBody;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsSameState() {
		return lesionsSameState;
	}

	public void setLesionsSameState(SymptomState lesionsSameState) {
		this.lesionsSameState = lesionsSameState;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsSameSize() {
		return lesionsSameSize;
	}

	public void setLesionsSameSize(SymptomState lesionsSameSize) {
		this.lesionsSameSize = lesionsSameSize;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsDeepProfound() {
		return lesionsDeepProfound;
	}

	public void setLesionsDeepProfound(SymptomState lesionsDeepProfound) {
		this.lesionsDeepProfound = lesionsDeepProfound;
	}

	public void setLesionsFace(Boolean lesionsFace) {
		this.lesionsFace = lesionsFace;
	}

	public void setLesionsLegs(Boolean lesionsLegs) {
		this.lesionsLegs = lesionsLegs;
	}

	public void setLesionsSolesFeet(Boolean lesionsSolesFeet) {
		this.lesionsSolesFeet = lesionsSolesFeet;
	}

	public void setLesionsPalmsHands(Boolean lesionsPalmsHands) {
		this.lesionsPalmsHands = lesionsPalmsHands;
	}

	public void setLesionsThorax(Boolean lesionsThorax) {
		this.lesionsThorax = lesionsThorax;
	}

	public void setLesionsArms(Boolean lesionsArms) {
		this.lesionsArms = lesionsArms;
	}

	public void setLesionsGenitals(Boolean lesionsGenitals) {
		this.lesionsGenitals = lesionsGenitals;
	}

	public void setLesionsAllOverBody(Boolean lesionsAllOverBody) {
		this.lesionsAllOverBody = lesionsAllOverBody;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsResembleImg1() {
		return lesionsResembleImg1;
	}

	public void setLesionsResembleImg1(SymptomState lesionsResembleImg1) {
		this.lesionsResembleImg1 = lesionsResembleImg1;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsResembleImg2() {
		return lesionsResembleImg2;
	}

	public void setLesionsResembleImg2(SymptomState lesionsResembleImg2) {
		this.lesionsResembleImg2 = lesionsResembleImg2;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsResembleImg3() {
		return lesionsResembleImg3;
	}

	public void setLesionsResembleImg3(SymptomState lesionsResembleImg3) {
		this.lesionsResembleImg3 = lesionsResembleImg3;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsResembleImg4() {
		return lesionsResembleImg4;
	}

	public void setLesionsResembleImg4(SymptomState lesionsResembleImg4) {
		this.lesionsResembleImg4 = lesionsResembleImg4;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getLesionsOnsetDate() {
		return lesionsOnsetDate;
	}

	public void setLesionsOnsetDate(Date lesionsOnsetDate) {
		this.lesionsOnsetDate = lesionsOnsetDate;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLymphadenopathyInguinal() {
		return lymphadenopathyInguinal;
	}

	public void setLymphadenopathyInguinal(SymptomState lymphadenopathyInguinal) {
		this.lymphadenopathyInguinal = lymphadenopathyInguinal;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLymphadenopathyAxillary() {
		return lymphadenopathyAxillary;
	}

	public void setLymphadenopathyAxillary(SymptomState lymphadenopathyAxillary) {
		this.lymphadenopathyAxillary = lymphadenopathyAxillary;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLymphadenopathyCervical() {
		return lymphadenopathyCervical;
	}

	public void setLymphadenopathyCervical(SymptomState lymphadenopathyCervical) {
		this.lymphadenopathyCervical = lymphadenopathyCervical;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMeningealSigns() {
		return meningealSigns;
	}

	public void setMeningealSigns(SymptomState meningealSigns) {
		this.meningealSigns = meningealSigns;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getChillsSweats() {
		return chillsSweats;
	}

	public void setChillsSweats(SymptomState chillsSweats) {
		this.chillsSweats = chillsSweats;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLesionsThatItch() {
		return lesionsThatItch;
	}

	public void setLesionsThatItch(SymptomState lesionsThatItch) {
		this.lesionsThatItch = lesionsThatItch;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBedridden() {
		return bedridden;
	}

	public void setBedridden(SymptomState bedridden) {
		this.bedridden = bedridden;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOralUlcers() {
		return oralUlcers;
	}

	public void setOralUlcers(SymptomState oralUlcers) {
		this.oralUlcers = oralUlcers;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPainfulLymphadenitis() {
		return painfulLymphadenitis;
	}

	public void setPainfulLymphadenitis(SymptomState painfulLymphadenitis) {
		this.painfulLymphadenitis = painfulLymphadenitis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBlackeningDeathOfTissue() {
		return blackeningDeathOfTissue;
	}

	public void setBlackeningDeathOfTissue(SymptomState blackeningDeathOfTissue) {
		this.blackeningDeathOfTissue = blackeningDeathOfTissue;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBuboesGroinArmpitNeck() {
		return buboesGroinArmpitNeck;
	}

	public void setBuboesGroinArmpitNeck(SymptomState buboesGroinArmpitNeck) {
		this.buboesGroinArmpitNeck = buboesGroinArmpitNeck;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBulgingFontanelle() {
		return bulgingFontanelle;
	}

	public void setBulgingFontanelle(SymptomState bulgingFontanelle) {
		this.bulgingFontanelle = bulgingFontanelle;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPharyngealErythema() {
		return pharyngealErythema;
	}

	public void setPharyngealErythema(SymptomState pharyngealErythema) {
		this.pharyngealErythema = pharyngealErythema;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPharyngealExudate() {
		return pharyngealExudate;
	}

	public void setPharyngealExudate(SymptomState pharyngealExudate) {
		this.pharyngealExudate = pharyngealExudate;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOedemaFaceNeck() {
		return oedemaFaceNeck;
	}

	public void setOedemaFaceNeck(SymptomState oedemaFaceNeck) {
		this.oedemaFaceNeck = oedemaFaceNeck;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOedemaLowerExtremity() {
		return oedemaLowerExtremity;
	}

	public void setOedemaLowerExtremity(SymptomState oedemaLowerExtremity) {
		this.oedemaLowerExtremity = oedemaLowerExtremity;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getLossSkinTurgor() {
		return lossSkinTurgor;
	}

	public void setLossSkinTurgor(SymptomState lossSkinTurgor) {
		this.lossSkinTurgor = lossSkinTurgor;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPalpableLiver() {
		return palpableLiver;
	}

	public void setPalpableLiver(SymptomState palpableLiver) {
		this.palpableLiver = palpableLiver;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPalpableSpleen() {
		return palpableSpleen;
	}

	public void setPalpableSpleen(SymptomState palpableSpleen) {
		this.palpableSpleen = palpableSpleen;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMalaise() {
		return malaise;
	}

	public void setMalaise(SymptomState malaise) {
		this.malaise = malaise;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSunkenEyesFontanelle() {
		return sunkenEyesFontanelle;
	}

	public void setSunkenEyesFontanelle(SymptomState sunkenEyesFontanelle) {
		this.sunkenEyesFontanelle = sunkenEyesFontanelle;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSidePain() {
		return sidePain;
	}

	public void setSidePain(SymptomState sidePain) {
		this.sidePain = sidePain;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getFluidInLungCavity() {
		return fluidInLungCavity;
	}

	public void setFluidInLungCavity(SymptomState fluidInLungCavity) {
		this.fluidInLungCavity = fluidInLungCavity;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getTremor() {
		return tremor;
	}

	public void setTremor(SymptomState tremor) {
		this.tremor = tremor;
	}

	public Integer getMidUpperArmCircumference() {
		return midUpperArmCircumference;
	}

	public void setMidUpperArmCircumference(Integer midUpperArmCircumference) {
		this.midUpperArmCircumference = midUpperArmCircumference;
	}

	public SymptomState getConvulsion() {
		return convulsion;
	}

	public void setConvulsion(SymptomState convulsion) {
		this.convulsion = convulsion;
	}

	public Integer getRespiratoryRate() {
		return respiratoryRate;
	}

	public void setRespiratoryRate(Integer respiratoryRate) {
		this.respiratoryRate = respiratoryRate;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getGlasgowComaScale() {
		return glasgowComaScale;
	}

	public void setGlasgowComaScale(Integer glasgowComaScale) {
		this.glasgowComaScale = glasgowComaScale;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHemorrhagicSyndrome() {
		return hemorrhagicSyndrome;
	}

	public void setHemorrhagicSyndrome(SymptomState hemorrhagicSyndrome) {
		this.hemorrhagicSyndrome = hemorrhagicSyndrome;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHyperglycemia() {
		return hyperglycemia;
	}

	public void setHyperglycemia(SymptomState hyperglycemia) {
		this.hyperglycemia = hyperglycemia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHypoglycemia() {
		return hypoglycemia;
	}

	public void setHypoglycemia(SymptomState hypoglycemia) {
		this.hypoglycemia = hypoglycemia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSepsis() {
		return sepsis;
	}

	public void setSepsis(SymptomState sepsis) {
		this.sepsis = sepsis;
	}

	@Enumerated(EnumType.STRING)
	public YesNoUnknown getJaundiceWithin24HoursOfBirth() {
		return jaundiceWithin24HoursOfBirth;
	}

	public void setJaundiceWithin24HoursOfBirth(YesNoUnknown jaundiceWithin24HoursOfBirth) {
		this.jaundiceWithin24HoursOfBirth = jaundiceWithin24HoursOfBirth;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getBilateralCataracts() {
		return bilateralCataracts;
	}

	public void setBilateralCataracts(SymptomState bilateralCataracts) {
		this.bilateralCataracts = bilateralCataracts;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getUnilateralCataracts() {
		return unilateralCataracts;
	}

	public void setUnilateralCataracts(SymptomState unilateralCataracts) {
		this.unilateralCataracts = unilateralCataracts;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getCongenitalGlaucoma() {
		return congenitalGlaucoma;
	}

	public void setCongenitalGlaucoma(SymptomState congenitalGlaucoma) {
		this.congenitalGlaucoma = congenitalGlaucoma;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPigmentaryRetinopathy() {
		return pigmentaryRetinopathy;
	}

	public void setPigmentaryRetinopathy(SymptomState pigmentaryRetinopathy) {
		this.pigmentaryRetinopathy = pigmentaryRetinopathy;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPurpuricRash() {
		return purpuricRash;
	}

	public void setPurpuricRash(SymptomState purpuricRash) {
		this.purpuricRash = purpuricRash;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMicrocephaly() {
		return microcephaly;
	}

	public void setMicrocephaly(SymptomState microcephaly) {
		this.microcephaly = microcephaly;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDevelopmentalDelay() {
		return developmentalDelay;
	}

	public void setDevelopmentalDelay(SymptomState developmentalDelay) {
		this.developmentalDelay = developmentalDelay;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getSplenomegaly() {
		return splenomegaly;
	}

	public void setSplenomegaly(SymptomState splenomegaly) {
		this.splenomegaly = splenomegaly;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getMeningoencephalitis() {
		return meningoencephalitis;
	}

	public void setMeningoencephalitis(SymptomState meningoencephalitis) {
		this.meningoencephalitis = meningoencephalitis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getRadiolucentBoneDisease() {
		return radiolucentBoneDisease;
	}

	public void setRadiolucentBoneDisease(SymptomState radiolucentBoneDisease) {
		this.radiolucentBoneDisease = radiolucentBoneDisease;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getCongenitalHeartDisease() {
		return congenitalHeartDisease;
	}

	public void setCongenitalHeartDisease(SymptomState congenitalHeartDisease) {
		this.congenitalHeartDisease = congenitalHeartDisease;
	}

	@Enumerated(EnumType.STRING)
	public CongenitalHeartDiseaseType getCongenitalHeartDiseaseType() {
		return congenitalHeartDiseaseType;
	}

	public void setCongenitalHeartDiseaseType(CongenitalHeartDiseaseType congenitalHeartDiseaseType) {
		this.congenitalHeartDiseaseType = congenitalHeartDiseaseType;
	}

	@Column(length = 512)
	public String getCongenitalHeartDiseaseDetails() {
		return congenitalHeartDiseaseDetails;
	}

	public void setCongenitalHeartDiseaseDetails(String congenitalHeartDiseaseDetails) {
		this.congenitalHeartDiseaseDetails = congenitalHeartDiseaseDetails;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHydrophobia() {
		return hydrophobia;
	}

	public void setHydrophobia(SymptomState hydrophobia) {
		this.hydrophobia = hydrophobia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getOpisthotonus() {
		return opisthotonus;
	}

	public void setOpisthotonus(SymptomState opisthotonus) {
		this.opisthotonus = opisthotonus;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAnxietyStates() {
		return anxietyStates;
	}

	public void setAnxietyStates(SymptomState anxietyStates) {
		this.anxietyStates = anxietyStates;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDelirium() {
		return delirium;
	}

	public void setDelirium(SymptomState delirium) {
		this.delirium = delirium;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getUproariousness() {
		return uproariousness;
	}

	public void setUproariousness(SymptomState uproariousness) {
		this.uproariousness = uproariousness;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getParesthesiaAroundWound() {
		return paresthesiaAroundWound;
	}

	public void setParesthesiaAroundWound(SymptomState paresthesiaAroundWound) {
		this.paresthesiaAroundWound = paresthesiaAroundWound;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getExcessSalivation() {
		return excessSalivation;
	}

	public void setExcessSalivation(SymptomState excessSalivation) {
		this.excessSalivation = excessSalivation;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getInsomnia() {
		return insomnia;
	}

	public void setInsomnia(SymptomState insomnia) {
		this.insomnia = insomnia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getParalysis() {
		return paralysis;
	}

	public void setParalysis(SymptomState paralysis) {
		this.paralysis = paralysis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getExcitation() {
		return excitation;
	}

	public void setExcitation(SymptomState excitation) {
		this.excitation = excitation;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getDysphagia() {
		return dysphagia;
	}

	public void setDysphagia(SymptomState dysphagia) {
		this.dysphagia = dysphagia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAerophobia() {
		return aerophobia;
	}

	public void setAerophobia(SymptomState aerophobia) {
		this.aerophobia = aerophobia;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getHyperactivity() {
		return hyperactivity;
	}

	public void setHyperactivity(SymptomState hyperactivity) {
		this.hyperactivity = hyperactivity;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getParesis() {
		return paresis;
	}

	public void setParesis(SymptomState paresis) {
		this.paresis = paresis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAgitation() {
		return agitation;
	}

	public void setAgitation(SymptomState agitation) {
		this.agitation = agitation;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAscendingFlaccidParalysis() {
		return ascendingFlaccidParalysis;
	}

	public void setAscendingFlaccidParalysis(SymptomState ascendingFlaccidParalysis) {
		this.ascendingFlaccidParalysis = ascendingFlaccidParalysis;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getErraticBehaviour() {
		return erraticBehaviour;
	}

	public void setErraticBehaviour(SymptomState erraticBehaviour) {
		this.erraticBehaviour = erraticBehaviour;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getComa() {
		return coma;
	}

	public void setComa(SymptomState coma) {
		this.coma = coma;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getFluidInLungCavityAuscultation() {
		return fluidInLungCavityAuscultation;
	}

	public void setFluidInLungCavityAuscultation(SymptomState fluidInLungCavityAuscultation) {
		this.fluidInLungCavityAuscultation = fluidInLungCavityAuscultation;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getFluidInLungCavityXray() {
		return fluidInLungCavityXray;
	}

	public void setFluidInLungCavityXray(SymptomState fluidInLungCavityXray) {
		this.fluidInLungCavityXray = fluidInLungCavityXray;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAbnormalLungXrayFindings() {
		return abnormalLungXrayFindings;
	}

	public void setAbnormalLungXrayFindings(SymptomState abnormalLungXrayFindings) {
		this.abnormalLungXrayFindings = abnormalLungXrayFindings;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getConjunctivalInjection() {
		return conjunctivalInjection;
	}

	public void setConjunctivalInjection(SymptomState conjunctivalInjection) {
		this.conjunctivalInjection = conjunctivalInjection;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getAcuteRespiratoryDistressSyndrome() {
		return acuteRespiratoryDistressSyndrome;
	}

	public void setAcuteRespiratoryDistressSyndrome(SymptomState acuteRespiratoryDistressSyndrome) {
		this.acuteRespiratoryDistressSyndrome = acuteRespiratoryDistressSyndrome;
	}

	@Enumerated(EnumType.STRING)
	public SymptomState getPneumoniaClinicalOrRadiologic() {
		return pneumoniaClinicalOrRadiologic;
	}

	public void setPneumoniaClinicalOrRadiologic(SymptomState pneumoniaClinicalOrRadiologic) {
		this.pneumoniaClinicalOrRadiologic = pneumoniaClinicalOrRadiologic;
	}

	@Column(length = 255)
	public String getSymptomsComments() {
		return symptomsComments;
	}

	public String toHumanString(boolean includeOnset, Language language) {
		StringBuilder string = new StringBuilder();

		// would be much nicer to have some automatism for this
		if (includeOnset) {
			appendNotNullDateValue(string, onsetDate, SymptomsDto.ONSET_DATE, language);
		}
		// onsetSymptom;
		// symptomatic;
		// patientIllLocation;
		appendNotNullValue(string, temperature, SymptomsDto.TEMPERATURE);
		appendNotNullValue(string, temperatureSource, SymptomsDto.TEMPERATURE_SOURCE);
		appendNotNullValue(string, bloodPressureSystolic, SymptomsDto.BLOOD_PRESSURE_SYSTOLIC);
		appendNotNullValue(string, bloodPressureDiastolic, SymptomsDto.BLOOD_PRESSURE_DIASTOLIC);
		appendNotNullValue(string, heartRate, SymptomsDto.HEART_RATE);
		appendNotNullValue(string, midUpperArmCircumference, SymptomsDto.MID_UPPER_ARM_CIRCUMFERENCE);
		appendNotNullValue(string, respiratoryRate, SymptomsDto.RESPIRATORY_RATE);
		appendNotNullValue(string, weight, SymptomsDto.WEIGHT);
		appendNotNullValue(string, height, SymptomsDto.HEIGHT);
		appendNotNullValue(string, glasgowComaScale, SymptomsDto.GLASGOW_COMA_SCALE);

		appendYesSymptom(string, alteredConsciousness, SymptomsDto.ALTERED_CONSCIOUSNESS);
		appendYesSymptom(string, confusedDisoriented, SymptomsDto.CONFUSED_DISORIENTED);
		appendYesSymptom(string, hemorrhagicSyndrome, SymptomsDto.HEMORRHAGIC_SYNDROME);
		appendYesSymptom(string, hyperglycemia, SymptomsDto.HYPERGLYCEMIA);
		appendYesSymptom(string, hypoglycemia, SymptomsDto.HYPOGLYCEMIA);
		appendYesSymptom(string, meningealSigns, SymptomsDto.MENINGEAL_SIGNS);
		appendYesSymptom(string, seizures, SymptomsDto.SEIZURES);
		appendYesSymptom(string, sepsis, SymptomsDto.SEPSIS);
		appendYesSymptom(string, shock, SymptomsDto.SHOCK);

		appendYesSymptom(string, fever, SymptomsDto.FEVER);
		appendYesSymptom(string, vomiting, SymptomsDto.VOMITING);
		appendYesSymptom(string, diarrhea, SymptomsDto.DIARRHEA);
		appendYesSymptom(string, bloodInStool, SymptomsDto.BLOOD_IN_STOOL);
		appendYesSymptom(string, nausea, SymptomsDto.NAUSEA);
		appendYesSymptom(string, abdominalPain, SymptomsDto.ABDOMINAL_PAIN);
		appendYesSymptom(string, headache, SymptomsDto.HEADACHE);
		appendYesSymptom(string, musclePain, SymptomsDto.MUSCLE_PAIN);
		appendYesSymptom(string, fatigueWeakness, SymptomsDto.FATIGUE_WEAKNESS);
		appendYesSymptom(string, unexplainedBleeding, SymptomsDto.UNEXPLAINED_BLEEDING);
		appendYesSymptom(string, gumsBleeding, SymptomsDto.GUMS_BLEEDING);
		appendYesSymptom(string, injectionSiteBleeding, SymptomsDto.INJECTION_SITE_BLEEDING);
		appendYesSymptom(string, noseBleeding, SymptomsDto.NOSE_BLEEDING);
		appendYesSymptom(string, bloodyBlackStool, SymptomsDto.BLOODY_BLACK_STOOL);
		appendYesSymptom(string, redBloodVomit, SymptomsDto.RED_BLOOD_VOMIT);
		appendYesSymptom(string, digestedBloodVomit, SymptomsDto.DIGESTED_BLOOD_VOMIT);
		appendYesSymptom(string, coughingBlood, SymptomsDto.COUGHING_BLOOD);
		appendYesSymptom(string, bleedingVagina, SymptomsDto.BLEEDING_VAGINA);
		appendYesSymptom(string, skinBruising, SymptomsDto.SKIN_BRUISING);
		appendYesSymptom(string, bloodUrine, SymptomsDto.BLOOD_URINE);
		//otherHemorrhagicSymptoms
		appendNotNullValue(string, otherHemorrhagicSymptomsText, SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT);
		appendYesSymptom(string, skinRash, SymptomsDto.SKIN_RASH);
		appendYesSymptom(string, neckStiffness, SymptomsDto.NECK_STIFFNESS);
		appendYesSymptom(string, soreThroat, SymptomsDto.SORE_THROAT);
		appendYesSymptom(string, cough, SymptomsDto.COUGH);
		appendYesSymptom(string, runnyNose, SymptomsDto.RUNNY_NOSE);
		appendYesSymptom(string, difficultyBreathing, SymptomsDto.DIFFICULTY_BREATHING);
		appendYesSymptom(string, chestPain, SymptomsDto.CHEST_PAIN);
		appendYesSymptom(string, conjunctivitis, SymptomsDto.CONJUNCTIVITIS);
		appendYesSymptom(string, eyePainLightSensitive, SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE);
		appendYesSymptom(string, kopliksSpots, SymptomsDto.KOPLIKS_SPOTS);
		appendYesSymptom(string, throbocytopenia, SymptomsDto.THROBOCYTOPENIA);
		appendYesSymptom(string, otitisMedia, SymptomsDto.OTITIS_MEDIA);
		appendYesSymptom(string, hearingloss, SymptomsDto.HEARINGLOSS);
		appendYesSymptom(string, dehydration, SymptomsDto.DEHYDRATION);
		appendYesSymptom(string, anorexiaAppetiteLoss, SymptomsDto.ANOREXIA_APPETITE_LOSS);
		appendYesSymptom(string, refusalFeedorDrink, SymptomsDto.REFUSAL_FEEDOR_DRINK);
		appendYesSymptom(string, jointPain, SymptomsDto.JOINT_PAIN);
		appendYesSymptom(string, hiccups, SymptomsDto.HICCUPS);
		// otherNonHemorrhagicSymptoms
		appendNotNullValue(string, otherNonHemorrhagicSymptomsText, SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT);
		appendYesSymptom(string, backache, SymptomsDto.BACKACHE);
		appendYesSymptom(string, eyesBleeding, SymptomsDto.EYES_BLEEDING);
		appendYesSymptom(string, jaundice, SymptomsDto.JAUNDICE);
		appendNotNullValue(string, jaundiceWithin24HoursOfBirth, SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);
		appendYesSymptom(string, darkUrine, SymptomsDto.DARK_URINE);
		appendYesSymptom(string, stomachBleeding, SymptomsDto.STOMACH_BLEEDING);
		appendYesSymptom(string, rapidBreathing, SymptomsDto.RAPID_BREATHING);
		appendYesSymptom(string, swollenGlands, SymptomsDto.SWOLLEN_GLANDS);
		appendYesSymptom(string, lesions, SymptomsDto.LESIONS);
		appendYesSymptom(string, lesionsSameState, SymptomsDto.LESIONS_SAME_STATE);
		appendYesSymptom(string, lesionsSameSize, SymptomsDto.LESIONS_SAME_SIZE);
		appendYesSymptom(string, lesionsDeepProfound, SymptomsDto.LESIONS_DEEP_PROFOUND);
		appendYesSymptom(string, lesionsThatItch, SymptomsDto.LESIONS_THAT_ITCH);
		appendTrue(string, lesionsFace, SymptomsDto.LESIONS_FACE);
		appendTrue(string, lesionsLegs, SymptomsDto.LESIONS_LEGS);
		appendTrue(string, lesionsSolesFeet, SymptomsDto.LESIONS_SOLES_FEET);
		appendTrue(string, lesionsPalmsHands, SymptomsDto.LESIONS_PALMS_HANDS);
		appendTrue(string, lesionsThorax, SymptomsDto.LESIONS_THORAX);
		appendTrue(string, lesionsArms, SymptomsDto.LESIONS_ARMS);
		appendTrue(string, lesionsGenitals, SymptomsDto.LESIONS_GENITALS);
		appendTrue(string, lesionsAllOverBody, SymptomsDto.LESIONS_ALL_OVER_BODY);
		// TODO images should have more specific caption to be included here
		//		appendYesSymptom(string, lesionsResembleImg1, SymptomsDto.LESIONS_RESEMBLE_IMG1);
		//		appendYesSymptom(string, lesionsResembleImg2, SymptomsDto.LESIONS_RESEMBLE_IMG2);
		//		appendYesSymptom(string, lesionsResembleImg3, SymptomsDto.LESIONS_RESEMBLE_IMG3);
		//		appendYesSymptom(string, lesionsResembleImg4, SymptomsDto.LESIONS_RESEMBLE_IMG4);
		appendNotNullDateValue(string, lesionsOnsetDate, SymptomsDto.LESIONS_ONSET_DATE, language);
		appendYesSymptom(string, lymphadenopathyInguinal, SymptomsDto.LYMPHADENOPATHY_INGUINAL);
		appendYesSymptom(string, lymphadenopathyAxillary, SymptomsDto.LYMPHADENOPATHY_AXILLARY);
		appendYesSymptom(string, lymphadenopathyCervical, SymptomsDto.LYMPHADENOPATHY_CERVICAL);
		appendYesSymptom(string, meningealSigns, SymptomsDto.MENINGEAL_SIGNS);
		appendYesSymptom(string, chillsSweats, SymptomsDto.CHILLS_SWEATS);
		appendYesSymptom(string, bedridden, SymptomsDto.BEDRIDDEN);
		appendYesSymptom(string, oralUlcers, SymptomsDto.ORAL_ULCERS);
		appendYesSymptom(string, painfulLymphadenitis, SymptomsDto.PAINFUL_LYMPHADENITIS);
		appendYesSymptom(string, blackeningDeathOfTissue, SymptomsDto.BLACKENING_DEATH_OF_TISSUE);
		appendYesSymptom(string, buboesGroinArmpitNeck, SymptomsDto.BUBOES_GROIN_ARMPIT_NECK);
		appendYesSymptom(string, bulgingFontanelle, SymptomsDto.BULGING_FONTANELLE);
		appendYesSymptom(string, pharyngealErythema, SymptomsDto.PHARYNGEAL_ERYTHEMA);
		appendYesSymptom(string, pharyngealExudate, SymptomsDto.PHARYNGEAL_EXUDATE);
		appendYesSymptom(string, oedemaFaceNeck, SymptomsDto.OEDEMA_FACE_NECK);
		appendYesSymptom(string, oedemaLowerExtremity, SymptomsDto.OEDEMA_LOWER_EXTREMITY);
		appendYesSymptom(string, lossSkinTurgor, SymptomsDto.LOSS_SKIN_TURGOR);
		appendYesSymptom(string, palpableLiver, SymptomsDto.PALPABLE_LIVER);
		appendYesSymptom(string, palpableSpleen, SymptomsDto.PALPABLE_SPLEEN);
		appendYesSymptom(string, malaise, SymptomsDto.MALAISE);
		appendYesSymptom(string, sunkenEyesFontanelle, SymptomsDto.SUNKEN_EYES_FONTANELLE);
		appendYesSymptom(string, sidePain, SymptomsDto.SIDE_PAIN);
		appendYesSymptom(string, fluidInLungCavity, SymptomsDto.FLUID_IN_LUNG_CAVITY);
		appendYesSymptom(string, tremor, SymptomsDto.TREMOR);
		appendYesSymptom(string, bilateralCataracts, SymptomsDto.BILATERAL_CATARACTS);
		appendYesSymptom(string, unilateralCataracts, SymptomsDto.UNILATERAL_CATARACTS);
		appendYesSymptom(string, congenitalGlaucoma, SymptomsDto.CONGENITAL_GLAUCOMA);
		appendYesSymptom(string, pigmentaryRetinopathy, SymptomsDto.PIGMENTARY_RETINOPATHY);
		appendYesSymptom(string, purpuricRash, SymptomsDto.PURPURIC_RASH);
		appendYesSymptom(string, microcephaly, SymptomsDto.MICROCEPHALY);
		appendYesSymptom(string, developmentalDelay, SymptomsDto.DEVELOPMENTAL_DELAY);
		appendYesSymptom(string, splenomegaly, SymptomsDto.SPLENOMEGALY);
		appendYesSymptom(string, meningoencephalitis, SymptomsDto.MENINGOENCEPHALITIS);
		appendYesSymptom(string, radiolucentBoneDisease, SymptomsDto.RADIOLUCENT_BONE_DISEASE);
		appendYesSymptom(string, congenitalHeartDisease, SymptomsDto.CONGENITAL_HEART_DISEASE);
		appendYesSymptom(string, fluidInLungCavityAuscultation, SymptomsDto.FLUID_IN_LUNG_CAVITY_AUSCULTATION);
		appendYesSymptom(string, fluidInLungCavityXray, SymptomsDto.FLUID_IN_LUNG_CAVITY_XRAY);
		appendYesSymptom(string, abnormalLungXrayFindings, SymptomsDto.ABNORMAL_LUNG_XRAY_FINDINGS);
		appendYesSymptom(string, conjunctivalInjection, SymptomsDto.CONJUNCTIVAL_INJECTION);
		appendYesSymptom(string, acuteRespiratoryDistressSyndrome, SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME);
		appendYesSymptom(string, pneumoniaClinicalOrRadiologic, SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC);
		appendNotNullValue(string, congenitalHeartDiseaseType, SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE);
		appendNotNullValue(string, congenitalHeartDiseaseDetails, SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS);
		// symptomsComments;

		return string.toString();
	}

	private static void appendNotNullValue(StringBuilder stringBuilder, Object value, String dtoPropertyId) {
		if (value != null) {
			if (value instanceof String && ((String) value).isEmpty()) {
				return; // ignore empty strings
			}
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null))
					.append(": ");

			stringBuilder.append(value);
		}
	}

	private static void appendNotNullDateValue(StringBuilder stringBuilder, Date value, String dtoPropertyId, Language language) {
		appendNotNullValue(stringBuilder, DateHelper.formatLocalDate(value, language), dtoPropertyId);
	}

	private static void appendYesSymptom(StringBuilder stringBuilder, SymptomState symptom, String dtoPropertyId) {
		if (symptom == SymptomState.YES) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null));
		}
	}

	private static void appendTrue(StringBuilder stringBuilder, Boolean value, String dtoPropertyId) {
		if (value != null && Boolean.TRUE.equals(value)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null));
		}
	}
}
