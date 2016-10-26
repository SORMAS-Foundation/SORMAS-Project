package de.symeda.sormas.backend.symptoms;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsFacade;

@Stateless(name = "SymptomsFacade")
public class SymptomsFacadeEjb implements SymptomsFacade {
	
	@EJB
	private SymptomsService service;

	public Symptoms fromDto(SymptomsDto dto) {		
		if (dto == null) {
			return null;
		}
		
		Symptoms symptoms = service.getByUuid(dto.getUuid());
		if (symptoms == null) {
			symptoms = new Symptoms();
			symptoms.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				symptoms.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		} 
		
		Symptoms a = symptoms;
		SymptomsDto b = dto;
		
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

		
		return symptoms;
	}
	
	public static SymptomsDto toDto(Symptoms symptoms) {
		
		if (symptoms == null) {
			return null;
		}

		SymptomsDto a = new SymptomsDto();
		Symptoms b = symptoms;
		
		a.setCreationDate(b.getCreationDate());
		a.setChangeDate(b.getChangeDate());
		a.setUuid(b.getUuid());
		
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
		
		return a;
	}

	@Override
	public SymptomsDto saveSymptoms(SymptomsDto dto) {
		Symptoms ado = fromDto(dto);
		service.ensurePersisted(ado);
		return toDto(ado);	}
	
	@LocalBean
	@Stateless
	public static class SymptomsFacadeEjbLocal extends SymptomsFacadeEjb {
	}
}

