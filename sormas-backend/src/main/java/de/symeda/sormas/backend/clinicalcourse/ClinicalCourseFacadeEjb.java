package de.symeda.sormas.backend.clinicalcourse;

import java.sql.Timestamp;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalCourseFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "ClinicalCourseFacade")
public class ClinicalCourseFacadeEjb implements ClinicalCourseFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

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

	//	private String countPositiveSymptomsQuery;

	@Override
	public List<ClinicalVisitIndexDto> getClinicalVisitIndexList(ClinicalVisitCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClinicalVisitIndexDto> cq = cb.createQuery(ClinicalVisitIndexDto.class);
		Root<ClinicalVisit> visit = cq.from(ClinicalVisit.class);
		Join<ClinicalVisit, Symptoms> symptoms = visit.join(ClinicalVisit.SYMPTOMS, JoinType.LEFT);

		cq.multiselect(
				visit.get(ClinicalVisit.UUID),
				visit.get(ClinicalVisit.VISIT_DATE_TIME),
				visit.get(ClinicalVisit.VISITING_PERSON),
				visit.get(ClinicalVisit.VISIT_REMARKS),
				symptoms.get(Symptoms.TEMPERATURE),
				symptoms.get(Symptoms.TEMPERATURE_SOURCE),
				symptoms.get(Symptoms.BLOOD_PRESSURE_SYSTOLIC),
				symptoms.get(Symptoms.BLOOD_PRESSURE_DIASTOLIC),
				symptoms.get(Symptoms.HEART_RATE),
				symptoms.get(Symptoms.ID));

		if (criteria != null) {
			cq.where(clinicalVisitService.buildCriteriaFilter(criteria, cb, visit));
		}

		cq.orderBy(cb.desc(visit.get(ClinicalVisit.VISIT_DATE_TIME)));

		List<ClinicalVisitIndexDto> results = em.createQuery(cq).getResultList();

		// Build the query to count positive symptoms
		// TODO: Re-activate when issue #964 (replace EclipseLink with Hibernate) has been done
		//		if (countPositiveSymptomsQuery == null) {
		//			String listColumnsQuery = "SELECT column_name FROM information_schema.columns "
		//					+ "WHERE table_schema = 'public' AND data_type IN ('character varying') "
		//					+ "AND TABLE_NAME = '" + Symptoms.TABLE_NAME + "' "
		//					+ "ORDER BY column_name";
		//			List<String> columns = em.createNativeQuery(listColumnsQuery).getResultList();
		//
		//			StringBuilder queryBuilder = new StringBuilder();
		//			queryBuilder.append("SELECT id, ");
		//			for (int i = 0; i < columns.size(); i++) {
		//				queryBuilder.append("CASE " + columns.get(i) + " WHEN 'YES' THEN 1 ELSE 0 END");
		//				if (i < columns.size() - 1) {
		//					queryBuilder.append(" + ");
		//				}
		//			}
		//			queryBuilder.append(" AS count FROM symptoms WHERE id IN (");
		//			countPositiveSymptomsQuery = queryBuilder.toString();
		//		}
		//
		//		if (!results.isEmpty()) {
		//			// Add number of positive symptoms
		//			HashMap<Long, ClinicalVisitIndexDto> symptomVisits = new HashMap<>();
		//			StringBuilder symptomIdsBuilder = new StringBuilder();
		//			for (int i = 0; i < results.size(); i++) {
		//				ClinicalVisitIndexDto result = results.get(i);
		//				symptomIdsBuilder.append(result.getSymptomsId());
		//				if (i < results.size() - 1) {
		//					symptomIdsBuilder.append(", ");
		//				}
		//				symptomVisits.put(result.getSymptomsId(), result);
		//			}
		//			symptomIdsBuilder.append(");");
		//
		//			List<Object[]> symptomCounts = em.createNativeQuery(countPositiveSymptomsQuery + symptomIdsBuilder.toString()).getResultList();
		//
		//			symptomCounts.stream().forEach(c -> {
		//				symptomVisits.get(c[0]).setSignsAndSymptomsCount((Integer) c[1]);
		//			});
		//		}

		return results;
	}

	@Override
	public ClinicalVisitDto getClinicalVisitByUuid(String uuid) {
		return toClinicalVisitDto(clinicalVisitService.getByUuid(uuid));
	}

	@Override
	public ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid) {
		SymptomsHelper.updateIsSymptomatic(clinicalVisit.getSymptoms());
		ClinicalVisit entity = fromClinicalVisitDto(clinicalVisit);

		clinicalVisitService.ensurePersisted(entity);

		// Update case symptoms
		CaseDataDto caze = caseFacade.getCaseDataByUuid(caseUuid);
		SymptomsDto caseSymptoms = caze.getSymptoms();
		SymptomsHelper.updateSymptoms(clinicalVisit.getSymptoms(), caseSymptoms);
		caseFacade.saveCase(caze);

		return toClinicalVisitDto(entity);
	}

	@Override
	public void deleteClinicalVisit(String clinicalVisitUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		// TODO replace this with a proper right call #944
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities");
		}

		ClinicalVisit clinicalVisit = clinicalVisitService.getByUuid(clinicalVisitUuid);
		clinicalVisitService.delete(clinicalVisit);
	}
	
	@Override
	public ClinicalCourseDto saveClinicalCourse(ClinicalCourseDto clinicalCourse) {
		ClinicalCourse entity = fromDto(clinicalCourse);
		
		service.ensurePersisted(entity);
		
		return toDto(entity);
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

	public static ClinicalVisitDto toClinicalVisitDto(ClinicalVisit source) {
		if (source == null) {
			return null;
		}

		ClinicalVisitDto target = new ClinicalVisitDto();
		DtoHelper.fillDto(target, source);

		target.setClinicalCourse(toDto(source.getClinicalCourse()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

		return target;
	}

	public ClinicalVisit fromClinicalVisitDto(@NotNull ClinicalVisitDto source) {
		ClinicalVisit target = clinicalVisitService.getByUuid(source.getUuid());

		if (target == null) {
			target = new ClinicalVisit();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}

		DtoHelper.validateDto(source, target);

		target.setClinicalCourse(fromDto(source.getClinicalCourse()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));
		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

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
		target.setOtherConditions(source.getOtherConditions());

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
		target.setOtherConditions(source.getOtherConditions());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ClinicalCourseFacadeEjbLocal extends ClinicalCourseFacadeEjb {

	}

}
