package de.symeda.sormas.app.backend.symptoms;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class SymptomsDtoHelper extends AdoDtoHelper<Symptoms, SymptomsDto> {

    @Override
    public Symptoms create() {
        return new Symptoms();
    }

    @Override
    public SymptomsDto createDto() {
        return new SymptomsDto();
    }

    @Override
    public void fillInnerFromDto(Symptoms a, SymptomsDto b) {

        a.setAbdominalPain(b.getAbdominalPain());
        a.setAnorexiaAppetiteLoss(b.getAnorexiaAppetiteLoss());
        a.setBleedingVagina(b.getBleedingVagina());
        a.setChestPain(b.getChestPain());
        a.setChills(b.getChills());
        a.setComaUnconscious(b.getComaUnconscious());
        a.setConfusedDisoriented(b.getConfusedDisoriented());
        a.setConjunctivitis(b.getConjunctivitis());
        a.setCough(b.getCough());
        a.setDehydration(b.getDehydration());
        a.setDiarrhea(b.getDiarrhea());
        a.setDifficultyBreathing(b.getDifficultyBreathing());
        a.setDigestedBloodVomit(b.getDigestedBloodVomit());
        a.setEpistaxis(b.getEpistaxis());
        a.setEyePainLightSensitive(b.getEyePainLightSensitive());
        a.setFatigueWeakness(b.getFatigueWeakness());
        a.setFever(b.getFever());
        a.setGumsBleeding(b.getGumsBleeding());
        a.setHeadache(b.getHeadache());
        a.setHematemesis(b.getHematemesis());
        a.setHematuria(b.getHematuria());
        a.setHemoptysis(b.getHemoptysis());
        a.setHiccups(b.getHiccups());
        a.setHighBloodPressure(b.getHighBloodPressure());
        a.setInjectionSiteBleeding(b.getInjectionSiteBleeding());
        a.setJaundice(b.getJaundice());
        a.setJointPain(b.getJointPain());
        a.setKopliksSpots(b.getKopliksSpots());
        a.setLethargy(b.getLethargy());
        a.setLowBloodPressure(b.getLowBloodPressure());
        a.setMelena(b.getMelena());
        a.setMusclePain(b.getMusclePain());
        a.setNausea(b.getNausea());
        a.setNeckStiffness(b.getNeckStiffness());
        a.setOedema(b.getOedema());
        a.setOnsetDate(b.getOnsetDate());
        a.setOnsetSymptom(b.getOnsetSymptom());
        a.setOtherHemorrhagicSymptoms(b.getOtherHemorrhagicSymptoms());
        a.setOtherHemorrhagicSymptomsText(b.getOtherHemorrhagicSymptomsText());
        a.setOtherNonHemorrhagicSymptoms(b.getOtherNonHemorrhagicSymptoms());
        a.setOtherNonHemorrhagicSymptomsText(b.getOtherNonHemorrhagicSymptomsText());
        a.setOtitisMedia(b.getOtitisMedia());
        a.setPetechiae(b.getPetechiae());
        a.setRefusalFeedorDrink(b.getRefusalFeedorDrink());
        a.setRunnyNose(b.getRunnyNose());
        a.setSeizures(b.getSeizures());
        a.setSepsis(b.getSepsis());
        a.setSkinRash(b.getSkinRash());
        a.setSoreThroat(b.getSoreThroat());
        a.setSwollenLymphNodes(b.getSwollenLymphNodes());
        a.setSymptomatic(b.getSymptomatic());
        a.setTemperature(b.getTemperature());
        a.setTemperatureSource(b.getTemperatureSource());
        a.setUnexplainedBleeding(b.getUnexplainedBleeding());
        a.setVomiting(b.getVomiting());
    }

    @Override
    public void fillInnerFromAdo(SymptomsDto a, Symptoms b) {

        a.setAbdominalPain(b.getAbdominalPain());
        a.setAnorexiaAppetiteLoss(b.getAnorexiaAppetiteLoss());
        a.setBleedingVagina(b.getBleedingVagina());
        a.setChestPain(b.getChestPain());
        a.setChills(b.getChills());
        a.setComaUnconscious(b.getComaUnconscious());
        a.setConfusedDisoriented(b.getConfusedDisoriented());
        a.setConjunctivitis(b.getConjunctivitis());
        a.setCough(b.getCough());
        a.setDehydration(b.getDehydration());
        a.setDiarrhea(b.getDiarrhea());
        a.setDifficultyBreathing(b.getDifficultyBreathing());
        a.setDigestedBloodVomit(b.getDigestedBloodVomit());
        a.setEpistaxis(b.getEpistaxis());
        a.setEyePainLightSensitive(b.getEyePainLightSensitive());
        a.setFatigueWeakness(b.getFatigueWeakness());
        a.setFever(b.getFever());
        a.setGumsBleeding(b.getGumsBleeding());
        a.setHeadache(b.getHeadache());
        a.setHematemesis(b.getHematemesis());
        a.setHematuria(b.getHematuria());
        a.setHemoptysis(b.getHemoptysis());
        a.setHiccups(b.getHiccups());
        a.setHighBloodPressure(b.getHighBloodPressure());
        a.setInjectionSiteBleeding(b.getInjectionSiteBleeding());
        a.setJaundice(b.getJaundice());
        a.setJointPain(b.getJointPain());
        a.setKopliksSpots(b.getKopliksSpots());
        a.setLethargy(b.getLethargy());
        a.setLowBloodPressure(b.getLowBloodPressure());
        a.setMelena(b.getMelena());
        a.setMusclePain(b.getMusclePain());
        a.setNausea(b.getNausea());
        a.setNeckStiffness(b.getNeckStiffness());
        a.setOedema(b.getOedema());
        a.setOnsetDate(b.getOnsetDate());
        a.setOnsetSymptom(b.getOnsetSymptom());
        a.setOtherHemorrhagicSymptoms(b.getOtherHemorrhagicSymptoms());
        a.setOtherHemorrhagicSymptomsText(b.getOtherHemorrhagicSymptomsText());
        a.setOtherNonHemorrhagicSymptoms(b.getOtherNonHemorrhagicSymptoms());
        a.setOtherNonHemorrhagicSymptomsText(b.getOtherNonHemorrhagicSymptomsText());
        a.setOtitisMedia(b.getOtitisMedia());
        a.setPetechiae(b.getPetechiae());
        a.setRefusalFeedorDrink(b.getRefusalFeedorDrink());
        a.setRunnyNose(b.getRunnyNose());
        a.setSeizures(b.getSeizures());
        a.setSepsis(b.getSepsis());
        a.setSkinRash(b.getSkinRash());
        a.setSoreThroat(b.getSoreThroat());
        a.setSwollenLymphNodes(b.getSwollenLymphNodes());
        a.setSymptomatic(b.getSymptomatic());
        a.setTemperature(b.getTemperature());
        a.setTemperatureSource(b.getTemperatureSource());
        a.setUnexplainedBleeding(b.getUnexplainedBleeding());
        a.setVomiting(b.getVomiting());

    }
}
