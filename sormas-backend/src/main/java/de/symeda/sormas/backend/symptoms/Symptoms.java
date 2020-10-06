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
package de.symeda.sormas.backend.symptoms;

import de.symeda.auditlog.api.Audited;
import de.symeda.sormas.api.symptoms.CongenitalHeartDiseaseType;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.TemperatureSource;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractDomainObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static de.symeda.sormas.api.EntityDto.COLUMN_LENGTH_DEFAULT;

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
    private YesNoUnknown symptomatic;
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
    private SymptomState coughWithSputum;
    private SymptomState coughWithHeamoptysis;
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
    private SymptomState lymphadenopathy;
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
    private SymptomState lossOfTaste;
    private SymptomState lossOfSmell;
    private SymptomState wheezing;
    private SymptomState skinUlcers;
    private SymptomState inabilityToWalk;
    private SymptomState inDrawingOfChestWall;
    private SymptomState respiratoryDiseaseVentilation;
    private SymptomState feelingIll;
    private SymptomState shivering;
    private SymptomState fastHeartRate;
    private SymptomState oxygenSaturationLower94;

    private SymptomState feverishFeeling;
    private SymptomState weakness;
    private SymptomState fatigue;
    private SymptomState coughWithoutSputum;
    private SymptomState breathlessness;
    private SymptomState chestPressure;
    private SymptomState blueLips;
    private SymptomState bloodCirculationProblems;
    private SymptomState palpitations;
    private SymptomState dizzinessStandingUp;
    private SymptomState highOrLowBloodPressure;
    private SymptomState urinaryRetention;

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

    private SymptomState otherComplications;
    private String otherComplicationsText;

    // when adding new fields make sure to extend toHumanString

    @Temporal(TemporalType.TIMESTAMP)
    public Date getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Date onsetDate) {
        this.onsetDate = onsetDate;
    }

    @Column(length = COLUMN_LENGTH_DEFAULT)
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

    public void setSymptomatic(YesNoUnknown symptomatic) {
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

    @Column(length = COLUMN_LENGTH_DEFAULT)
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

    public YesNoUnknown getSymptomatic() {
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

    @Column(length = COLUMN_LENGTH_DEFAULT)
    public String getOtherHemorrhagicSymptomsText() {
        return otherHemorrhagicSymptomsText;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getOtherNonHemorrhagicSymptoms() {
        return otherNonHemorrhagicSymptoms;
    }

    @Column(length = COLUMN_LENGTH_DEFAULT)
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

    @Column(length = COLUMN_LENGTH_DEFAULT)
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

    @Column(length = COLUMN_LENGTH_DEFAULT)
    public String getSymptomsComments() {
        return symptomsComments;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getLossOfTaste() {
        return lossOfTaste;
    }

    public void setLossOfTaste(SymptomState lossOfTaste) {
        this.lossOfTaste = lossOfTaste;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getLossOfSmell() {
        return lossOfSmell;
    }

    public void setLossOfSmell(SymptomState lossOfSmell) {
        this.lossOfSmell = lossOfSmell;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getLymphadenopathy() {
        return lymphadenopathy;
    }

    public void setLymphadenopathy(SymptomState lymphadenopathy) {
        this.lymphadenopathy = lymphadenopathy;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getWheezing() {
        return wheezing;
    }

    public void setWheezing(SymptomState wheezing) {
        this.wheezing = wheezing;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getSkinUlcers() {
        return skinUlcers;
    }

    public void setSkinUlcers(SymptomState skinUlcers) {
        this.skinUlcers = skinUlcers;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getInabilityToWalk() {
        return inabilityToWalk;
    }

    public void setInabilityToWalk(SymptomState inabilityToWalk) {
        this.inabilityToWalk = inabilityToWalk;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getInDrawingOfChestWall() {
        return inDrawingOfChestWall;
    }

    public void setInDrawingOfChestWall(SymptomState inDrawingOfChestWall) {
        this.inDrawingOfChestWall = inDrawingOfChestWall;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getCoughWithSputum() {
        return coughWithSputum;
    }

    public void setCoughWithSputum(SymptomState coughWithSputum) {
        this.coughWithSputum = coughWithSputum;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getCoughWithHeamoptysis() {
        return coughWithHeamoptysis;
    }

    public void setCoughWithHeamoptysis(SymptomState coughWithHeamoptysis) {
        this.coughWithHeamoptysis = coughWithHeamoptysis;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getRespiratoryDiseaseVentilation() {
        return respiratoryDiseaseVentilation;
    }

    public void setRespiratoryDiseaseVentilation(SymptomState respiratoryDiseaseVentilation) {
        this.respiratoryDiseaseVentilation = respiratoryDiseaseVentilation;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getFeelingIll() {
        return feelingIll;
    }

    public void setFeelingIll(SymptomState feelingIll) {
        this.feelingIll = feelingIll;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getShivering() {
        return shivering;
    }

    public void setShivering(SymptomState shivering) {
        this.shivering = shivering;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getFastHeartRate() {
        return fastHeartRate;
    }

    public void setFastHeartRate(SymptomState fastHeartRate) {
        this.fastHeartRate = fastHeartRate;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getOxygenSaturationLower94() {
        return oxygenSaturationLower94;
    }

    public void setOxygenSaturationLower94(SymptomState oxygenSaturationLower94) {
        this.oxygenSaturationLower94 = oxygenSaturationLower94;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getFeverishFeeling() {
        return feverishFeeling;
    }

    public void setFeverishFeeling(SymptomState feverishFeeling) {
        this.feverishFeeling = feverishFeeling;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getWeakness() {
        return weakness;
    }

    public void setWeakness(SymptomState weakness) {
        this.weakness = weakness;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getFatigue() {
        return fatigue;
    }

    public void setFatigue(SymptomState fatigue) {
        this.fatigue = fatigue;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getCoughWithoutSputum() {
        return coughWithoutSputum;
    }

    public void setCoughWithoutSputum(SymptomState coughWithoutSputum) {
        this.coughWithoutSputum = coughWithoutSputum;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getBreathlessness() {
        return breathlessness;
    }

    public void setBreathlessness(SymptomState breathlessness) {
        this.breathlessness = breathlessness;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getChestPressure() {
        return chestPressure;
    }

    public void setChestPressure(SymptomState chestPressure) {
        this.chestPressure = chestPressure;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getBlueLips() {
        return blueLips;
    }

    public void setBlueLips(SymptomState blueLips) {
        this.blueLips = blueLips;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getBloodCirculationProblems() {
        return bloodCirculationProblems;
    }

    public void setBloodCirculationProblems(SymptomState bloodCirculationProblems) {
        this.bloodCirculationProblems = bloodCirculationProblems;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getPalpitations() {
        return palpitations;
    }

    public void setPalpitations(SymptomState palpitations) {
        this.palpitations = palpitations;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getDizzinessStandingUp() {
        return dizzinessStandingUp;
    }

    public void setDizzinessStandingUp(SymptomState dizzinessStandingUp) {
        this.dizzinessStandingUp = dizzinessStandingUp;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getHighOrLowBloodPressure() {
        return highOrLowBloodPressure;
    }

    public void setHighOrLowBloodPressure(SymptomState highOrLowBloodPressure) {
        this.highOrLowBloodPressure = highOrLowBloodPressure;
    }

    @Enumerated(EnumType.STRING)
    public SymptomState getUrinaryRetention() {
        return urinaryRetention;
    }

    public void setUrinaryRetention(SymptomState urinaryRetention) {
        this.urinaryRetention = urinaryRetention;
    }

    @Enumerated
    public SymptomState getOtherComplications() {
        return otherComplications;
    }

    public void setOtherComplications(SymptomState otherComplications) {
        this.otherComplications = otherComplications;
    }

    @Column(length = COLUMN_LENGTH_DEFAULT)
    public String getOtherComplicationsText() {
        return otherComplicationsText;
    }

    public void setOtherComplicationsText(String otherComplicationsText) {
        this.otherComplicationsText = otherComplicationsText;
    }
}
