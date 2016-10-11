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
    public void fillInnerFromDto(Symptoms ado, SymptomsDto dto) {

        ado.setAbdominalPain(dto.getAbdominalPain());
        ado.setAnorexiaAppetiteLoss(dto.getAnorexiaAppetiteLoss());
        ado.setBleedingVagina(dto.getBleedingVagina());
        ado.setChestPain(dto.getChestPain());
        ado.setComaUnconscious(dto.getComaUnconscious());
        ado.setConfusedDisoriented(dto.getConfusedDisoriented());
        ado.setConjunctivitis(dto.getConjunctivitis());
        ado.setCough(dto.getCough());
        ado.setDiarrhea(dto.getDiarrhea());
        ado.setDifficultyBreathing(dto.getDifficultyBreathing());
        ado.setDifficultySwallowing(dto.getDifficultySwallowing());
        ado.setDigestedBloodVomit(dto.getDigestedBloodVomit());
        ado.setEpistaxis(dto.getEpistaxis());
        ado.setEyePainLightSensitive(dto.getEyePainLightSensitive());
        ado.setFever(dto.getFever());
        ado.setGumsBleeding(dto.getGumsBleeding());
        ado.setHeadache(dto.getHeadache());
        ado.setHematemesis(dto.getHematemesis());
        ado.setHematuria(dto.getHematuria());
        ado.setHemoptysis(dto.getHemoptysis());
        ado.setHiccups(dto.getHiccups());
        ado.setInjectionSiteBleeding(dto.getInjectionSiteBleeding());
        ado.setIntenseFatigueWeakness(dto.getIntenseFatigueWeakness());
        ado.setJaundice(dto.getJaundice());
        ado.setJointPain(dto.getJointPain());
        ado.setMelena(dto.getMelena());
        ado.setMusclePain(dto.getMusclePain());
        ado.setOnsetDate(dto.getOnsetDate());
        ado.setOtherHemorrhagic(dto.getOtherHemorrhagic());
        ado.setOtherHemorrhagicText(dto.getOtherHemorrhagicText());
        ado.setOtherNonHemorrhagic(dto.getOtherNonHemorrhagic());
        ado.setOtherNonHemorrhagicSymptoms(dto.getOtherNonHemorrhagicSymptoms());
        ado.setPetechiae(dto.getPetechiae());
        ado.setSkinRash(dto.getSkinRash());
        ado.setSoreThroat(dto.getSoreThroat());
        ado.setTemperature(dto.getTemperature());
        ado.setTemperatureSource(dto.getTemperatureSource());
        ado.setUnexplainedBleeding(dto.getUnexplainedBleeding());
        ado.setVomitingNausea(dto.getVomitingNausea());
    }

    @Override
    public void fillInnerFromAdo(SymptomsDto dto, Symptoms ado) {

        dto.setCreationDate(ado.getCreationDate());
        dto.setChangeDate(ado.getChangeDate());
        dto.setUuid(ado.getUuid());

        dto.setAbdominalPain(ado.getAbdominalPain());
        dto.setAnorexiaAppetiteLoss(ado.getAnorexiaAppetiteLoss());
        dto.setBleedingVagina(ado.getBleedingVagina());
        dto.setChestPain(ado.getChestPain());
        dto.setComaUnconscious(ado.getComaUnconscious());
        dto.setConfusedDisoriented(ado.getConfusedDisoriented());
        dto.setConjunctivitis(ado.getConjunctivitis());
        dto.setCough(ado.getCough());
        dto.setDiarrhea(ado.getDiarrhea());
        dto.setDifficultyBreathing(ado.getDifficultyBreathing());
        dto.setDifficultySwallowing(ado.getDifficultySwallowing());
        dto.setDigestedBloodVomit(ado.getDigestedBloodVomit());
        dto.setEpistaxis(ado.getEpistaxis());
        dto.setEyePainLightSensitive(ado.getEyePainLightSensitive());
        dto.setFever(ado.getFever());
        dto.setGumsBleeding(ado.getGumsBleeding());
        dto.setHeadache(ado.getHeadache());
        dto.setHematemesis(ado.getHematemesis());
        dto.setHematuria(ado.getHematuria());
        dto.setHemoptysis(ado.getHemoptysis());
        dto.setHiccups(ado.getHiccups());
        dto.setInjectionSiteBleeding(ado.getInjectionSiteBleeding());
        dto.setIntenseFatigueWeakness(ado.getIntenseFatigueWeakness());
        dto.setJaundice(ado.getJaundice());
        dto.setJointPain(ado.getJointPain());
        dto.setMelena(ado.getMelena());
        dto.setMusclePain(ado.getMusclePain());
        dto.setOnsetDate(ado.getOnsetDate());
        dto.setOtherHemorrhagic(ado.getOtherHemorrhagic());
        dto.setOtherHemorrhagicText(ado.getOtherHemorrhagicText());
        dto.setOtherNonHemorrhagic(ado.getOtherNonHemorrhagic());
        dto.setOtherNonHemorrhagicSymptoms(ado.getOtherNonHemorrhagicSymptoms());
        dto.setPetechiae(ado.getPetechiae());
        dto.setSkinRash(ado.getSkinRash());
        dto.setSoreThroat(ado.getSoreThroat());
        dto.setTemperature(ado.getTemperature());
        dto.setTemperatureSource(ado.getTemperatureSource());
        dto.setUnexplainedBleeding(ado.getUnexplainedBleeding());
        dto.setVomitingNausea(ado.getVomitingNausea());

    }
}
