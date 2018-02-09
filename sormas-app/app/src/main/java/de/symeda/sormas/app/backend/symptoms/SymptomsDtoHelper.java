package de.symeda.sormas.app.backend.symptoms;

import java.util.List;

import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.location.Location;
import de.symeda.sormas.app.backend.location.LocationDtoHelper;
import retrofit2.Call;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class SymptomsDtoHelper extends AdoDtoHelper<Symptoms, SymptomsDto> {

    @Override
    protected Class<Symptoms> getAdoClass() {
        return Symptoms.class;
    }

    @Override
    protected Class<SymptomsDto> getDtoClass() {
        return SymptomsDto.class;
    }

    @Override
    protected Call<List<SymptomsDto>> pullAllSince(long since) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<List<SymptomsDto>> pullByUuids(List<String> uuids) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    protected Call<Integer> pushAll(List<SymptomsDto> symptomsDtos) {
        throw new UnsupportedOperationException("Entity is embedded");
    }

    @Override
    public void fillInnerFromDto(Symptoms a, SymptomsDto b) {
        a.setAbdominalPain(b.getAbdominalPain());
        a.setAlteredConsciousness(b.getAlteredConsciousness());
        a.setAnorexiaAppetiteLoss(b.getAnorexiaAppetiteLoss());
        a.setBleedingVagina(b.getBleedingVagina());
        a.setBloodInStool(b.getBloodInStool());
        a.setBloodUrine(b.getBloodUrine());
        a.setBloodyBlackStool(b.getBloodyBlackStool());
        a.setChestPain(b.getChestPain());
        a.setConfusedDisoriented(b.getConfusedDisoriented());
        a.setConjunctivitis(b.getConjunctivitis());
        a.setCough(b.getCough());
        a.setCoughingBlood(b.getCoughingBlood());
        a.setDehydration(b.getDehydration());
        a.setDiarrhea(b.getDiarrhea());
        a.setDifficultyBreathing(b.getDifficultyBreathing());
        a.setDigestedBloodVomit(b.getDigestedBloodVomit());
        a.setEyePainLightSensitive(b.getEyePainLightSensitive());
        a.setFatigueWeakness(b.getFatigueWeakness());
        a.setFever(b.getFever());
        a.setGumsBleeding(b.getGumsBleeding());
        a.setHeadache(b.getHeadache());
        a.setHearingloss(b.getHearingloss());
        a.setHiccups(b.getHiccups());
        a.setInjectionSiteBleeding(b.getInjectionSiteBleeding());
        a.setJointPain(b.getJointPain());
        a.setKopliksSpots(b.getKopliksSpots());
        a.setMusclePain(b.getMusclePain());
        a.setNausea(b.getNausea());
        a.setNeckStiffness(b.getNeckStiffness());
        a.setNoseBleeding(b.getNoseBleeding());
        a.setOnsetDate(b.getOnsetDate());
        a.setOnsetSymptom(b.getOnsetSymptom());
        a.setOtherHemorrhagicSymptoms(b.getOtherHemorrhagicSymptoms());
        a.setOtherHemorrhagicSymptomsText(b.getOtherHemorrhagicSymptomsText());
        a.setOtherNonHemorrhagicSymptoms(b.getOtherNonHemorrhagicSymptoms());
        a.setOtherNonHemorrhagicSymptomsText(b.getOtherNonHemorrhagicSymptomsText());
        a.setOtitisMedia(b.getOtitisMedia());
        a.setRedBloodVomit(b.getRedBloodVomit());
        a.setRefusalFeedorDrink(b.getRefusalFeedorDrink());
        a.setRunnyNose(b.getRunnyNose());
        a.setSeizures(b.getSeizures());
        a.setShock(b.getShock());
        a.setSkinBruising(b.getSkinBruising());
        a.setSkinRash(b.getSkinRash());
        a.setSoreThroat(b.getSoreThroat());
        a.setSymptomatic(b.getSymptomatic());
        a.setSymptomsComments(b.getSymptomsComments());
        a.setTemperature(b.getTemperature());
        a.setTemperatureSource(b.getTemperatureSource());
        a.setThrobocytopenia(b.getThrobocytopenia());
        a.setUnexplainedBleeding(b.getUnexplainedBleeding());
        a.setVomiting(b.getVomiting());
        a.setBackache(b.getBackache());
        a.setEyesBleeding(b.getEyesBleeding());
        a.setJaundice(b.getJaundice());
        a.setDarkUrine(b.getDarkUrine());
        a.setStomachBleeding(b.getStomachBleeding());
        a.setRapidBreathing(b.getRapidBreathing());
        a.setSwollenGlands(b.getSwollenGlands());
        a.setLesions(b.getLesions());
        a.setLesionsSameState(b.getLesionsSameState());
        a.setLesionsSameSize(b.getLesionsSameSize());
        a.setLesionsDeepProfound(b.getLesionsDeepProfound());
        a.setLesionsThatItch(b.getLesionsThatItch());
        a.setLesionsFace(b.getLesionsFace());
        a.setLesionsLegs(b.getLesionsLegs());
        a.setLesionsSolesFeet(b.getLesionsSolesFeet());
        a.setLesionsPalmsHands(b.getLesionsPalmsHands());
        a.setLesionsThorax(b.getLesionsThorax());
        a.setLesionsArms(b.getLesionsArms());
        a.setLesionsGenitals(b.getLesionsGenitals());
        a.setLesionsAllOverBody(b.getLesionsAllOverBody());
        a.setLesionsResembleImg1(b.getLesionsResembleImg1());
        a.setLesionsResembleImg2(b.getLesionsResembleImg2());
        a.setLesionsResembleImg3(b.getLesionsResembleImg3());
        a.setLesionsResembleImg4(b.getLesionsResembleImg4());
        a.setLesionsOnsetDate(b.getLesionsOnsetDate());
        a.setLymphadenopathyAxillary(b.getLymphadenopathyAxillary());
        a.setLymphadenopathyCervical(b.getLymphadenopathyCervical());
        a.setLymphadenopathyInguinal(b.getLymphadenopathyInguinal());
        a.setChillsSweats(b.getChillsSweats());
        a.setBedridden(b.getBedridden());
        a.setOralUlcers(b.getOralUlcers());
        a.setPatientIllLocation(b.getPatientIllLocation());
        a.setBlackeningDeathOfTissue(b.getBlackeningDeathOfTissue());
        a.setBuboesGroinArmpitNeck(b.getBuboesGroinArmpitNeck());
        a.setPainfulLymphadenitis(b.getPainfulLymphadenitis());
        a.setBulgingFontanelle(b.getBulgingFontanelle());
    }

    @Override
    public void fillInnerFromAdo(SymptomsDto a, Symptoms b) {
        a.setAbdominalPain(b.getAbdominalPain());
        a.setAlteredConsciousness(b.getAlteredConsciousness());
        a.setAnorexiaAppetiteLoss(b.getAnorexiaAppetiteLoss());
        a.setBleedingVagina(b.getBleedingVagina());
        a.setBloodInStool(b.getBloodInStool());
        a.setBloodUrine(b.getBloodUrine());
        a.setBloodyBlackStool(b.getBloodyBlackStool());
        a.setChestPain(b.getChestPain());
        a.setConfusedDisoriented(b.getConfusedDisoriented());
        a.setConjunctivitis(b.getConjunctivitis());
        a.setCough(b.getCough());
        a.setCoughingBlood(b.getCoughingBlood());
        a.setDehydration(b.getDehydration());
        a.setDiarrhea(b.getDiarrhea());
        a.setDifficultyBreathing(b.getDifficultyBreathing());
        a.setDigestedBloodVomit(b.getDigestedBloodVomit());
        a.setEyePainLightSensitive(b.getEyePainLightSensitive());
        a.setFatigueWeakness(b.getFatigueWeakness());
        a.setFever(b.getFever());
        a.setGumsBleeding(b.getGumsBleeding());
        a.setHeadache(b.getHeadache());
        a.setHearingloss(b.getHearingloss());
        a.setHiccups(b.getHiccups());
        a.setInjectionSiteBleeding(b.getInjectionSiteBleeding());
        a.setJointPain(b.getJointPain());
        a.setKopliksSpots(b.getKopliksSpots());
        a.setMusclePain(b.getMusclePain());
        a.setNausea(b.getNausea());
        a.setNeckStiffness(b.getNeckStiffness());
        a.setNoseBleeding(b.getNoseBleeding());
        a.setOnsetDate(b.getOnsetDate());
        a.setOnsetSymptom(b.getOnsetSymptom());
        a.setOtherHemorrhagicSymptoms(b.getOtherHemorrhagicSymptoms());
        a.setOtherHemorrhagicSymptomsText(b.getOtherHemorrhagicSymptomsText());
        a.setOtherNonHemorrhagicSymptoms(b.getOtherNonHemorrhagicSymptoms());
        a.setOtherNonHemorrhagicSymptomsText(b.getOtherNonHemorrhagicSymptomsText());
        a.setOtitisMedia(b.getOtitisMedia());
        a.setRedBloodVomit(b.getRedBloodVomit());
        a.setRefusalFeedorDrink(b.getRefusalFeedorDrink());
        a.setRunnyNose(b.getRunnyNose());
        a.setSeizures(b.getSeizures());
        a.setShock(b.getShock());
        a.setSkinBruising(b.getSkinBruising());
        a.setSkinRash(b.getSkinRash());
        a.setSoreThroat(b.getSoreThroat());
        a.setSymptomatic(b.getSymptomatic());
        a.setSymptomsComments(b.getSymptomsComments());
        a.setTemperature(b.getTemperature());
        a.setTemperatureSource(b.getTemperatureSource());
        a.setThrobocytopenia(b.getThrobocytopenia());
        a.setUnexplainedBleeding(b.getUnexplainedBleeding());
        a.setVomiting(b.getVomiting());
        a.setBackache(b.getBackache());
        a.setEyesBleeding(b.getEyesBleeding());
        a.setJaundice(b.getJaundice());
        a.setDarkUrine(b.getDarkUrine());
        a.setStomachBleeding(b.getStomachBleeding());
        a.setRapidBreathing(b.getRapidBreathing());
        a.setSwollenGlands(b.getSwollenGlands());
        a.setLesions(b.getLesions());
        a.setLesionsSameState(b.getLesionsSameState());
        a.setLesionsSameSize(b.getLesionsSameSize());
        a.setLesionsDeepProfound(b.getLesionsDeepProfound());
        a.setLesionsThatItch(b.getLesionsThatItch());
        a.setLesionsFace(b.getLesionsFace());
        a.setLesionsLegs(b.getLesionsLegs());
        a.setLesionsSolesFeet(b.getLesionsSolesFeet());
        a.setLesionsPalmsHands(b.getLesionsPalmsHands());
        a.setLesionsThorax(b.getLesionsThorax());
        a.setLesionsArms(b.getLesionsArms());
        a.setLesionsGenitals(b.getLesionsGenitals());
        a.setLesionsAllOverBody(b.getLesionsAllOverBody());
        a.setLesionsResembleImg1(b.getLesionsResembleImg1());
        a.setLesionsResembleImg2(b.getLesionsResembleImg2());
        a.setLesionsResembleImg3(b.getLesionsResembleImg3());
        a.setLesionsResembleImg4(b.getLesionsResembleImg4());
        a.setLesionsOnsetDate(b.getLesionsOnsetDate());
        a.setLymphadenopathyAxillary(b.getLymphadenopathyAxillary());
        a.setLymphadenopathyCervical(b.getLymphadenopathyCervical());
        a.setLymphadenopathyInguinal(b.getLymphadenopathyInguinal());
        a.setChillsSweats(b.getChillsSweats());
        a.setBedridden(b.getBedridden());
        a.setOralUlcers(b.getOralUlcers());
        a.setPatientIllLocation(b.getPatientIllLocation());
        a.setBlackeningDeathOfTissue(b.getBlackeningDeathOfTissue());
        a.setBuboesGroinArmpitNeck(b.getBuboesGroinArmpitNeck());
        a.setPainfulLymphadenitis(b.getPainfulLymphadenitis());
        a.setBulgingFontanelle(b.getBulgingFontanelle());
    }
}
