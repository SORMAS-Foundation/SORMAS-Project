package de.symeda.sormas.backend.clinicalcourse;

import java.sql.Timestamp;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseReferenceDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ClinicalCourseFacade")
public class ClinicalCourseFacadeEjb implements ClinicalCourseFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	ClinicalCourseService service;
	@EJB
	ClinicalVisitService clinicalVisitService;
	@EJB
	SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	UserService userService;
	@EJB
	CaseFacadeEjbLocal caseFacade;
	@EJB
	CaseService caseService;
	@EJB
	PersonService personService;
	@EJB
	HealthConditionsService healthConditionsService;
	
	public static ClinicalCourseReferenceDto toReferenceDto(ClinicalCourse entity) {
		if (entity == null) {
			return null;
		}
		
		ClinicalCourseReferenceDto dto = new ClinicalCourseReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static ClinicalCourseDto toDto(ClinicalCourse source) {
		if (source == null) {
			return null;
		}

		ClinicalCourseDto target = new ClinicalCourseDto();
		DtoHelper.fillDto(target, source);

		if (source.getHealthConditions() != null) {
			target.setHealthConditions(toHealthConditionsDto(source.getHealthConditions()));
		}

		return target;
	}

	public ClinicalCourse fromDto(@NotNull ClinicalCourseDto source) {
		ClinicalCourse target = service.getByUuid(source.getUuid());

		if (target == null) {
			target = new ClinicalCourse();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		if (source.getHealthConditions() != null) {
			target.setHealthConditions(fromHealthConditionsDto(source.getHealthConditions()));
		}

		return target;
	}

	public static HealthConditionsDto toHealthConditionsDto(HealthConditions source) {
		if (source == null) {
			return null;
		}

		HealthConditionsDto target = new HealthConditionsDto();
		DtoHelper.fillDto(target, source);

		target.setAsplenia(source.getAsplenia());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setDiabetes(source.getDiabetes());
		target.setHepatitis(source.getHepatitis());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setTuberculosis(source.getTuberculosis());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setOtherConditions(source.getOtherConditions());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());

		return target;
	}

	public HealthConditions fromHealthConditionsDto(@NotNull HealthConditionsDto source) {
		HealthConditions target = healthConditionsService.getByUuid(source.getUuid());

		if (target == null) {
			target = new HealthConditions();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		target.setAsplenia(source.getAsplenia());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setDiabetes(source.getDiabetes());
		target.setHepatitis(source.getHepatitis());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setTuberculosis(source.getTuberculosis());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setOtherConditions(source.getOtherConditions());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ClinicalCourseFacadeEjbLocal extends ClinicalCourseFacadeEjb {

	}

}
