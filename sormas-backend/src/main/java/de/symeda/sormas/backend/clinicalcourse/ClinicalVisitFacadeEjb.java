package de.symeda.sormas.backend.clinicalcourse;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitExportDto;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitFacade;
import de.symeda.sormas.api.clinicalcourse.ClinicalVisitIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseJurisdictionChecker;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.clinicalcourse.ClinicalCourseFacadeEjb.ClinicalCourseFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.symptoms.SymptomsService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;
import de.symeda.sormas.utils.CaseJoins;

@Stateless(name = "ClinicalVisitFacade")
public class ClinicalVisitFacadeEjb implements ClinicalVisitFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private ClinicalVisitService service;
	@EJB
	private UserService userService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private ClinicalCourseService clinicalCourseService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private ClinicalCourseFacadeEjbLocal clinicalCourseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private SymptomsService symptomsService;
	@EJB
	private CaseJurisdictionChecker caseJurisdictionChecker;

	//	private String countPositiveSymptomsQuery;

	@Override
	public List<ClinicalVisitIndexDto> getIndexList(ClinicalVisitCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClinicalVisitIndexDto> cq = cb.createQuery(ClinicalVisitIndexDto.class);
		Root<ClinicalVisit> visit = cq.from(ClinicalVisit.class);
		ClinicalVisitJoins joins = new ClinicalVisitJoins(visit);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						visit.get(ClinicalVisit.UUID),
						visit.get(ClinicalVisit.VISIT_DATE_TIME),
						visit.get(ClinicalVisit.VISITING_PERSON),
						visit.get(ClinicalVisit.VISIT_REMARKS),
						joins.getSymptoms().get(Symptoms.TEMPERATURE),
						joins.getSymptoms().get(Symptoms.TEMPERATURE_SOURCE),
						joins.getSymptoms().get(Symptoms.BLOOD_PRESSURE_SYSTOLIC),
						joins.getSymptoms().get(Symptoms.BLOOD_PRESSURE_DIASTOLIC),
						joins.getSymptoms().get(Symptoms.HEART_RATE),
						joins.getSymptoms().get(Symptoms.ID)),
					getJurisdictionSelections(joins))
				.collect(Collectors.toList()));

		if (criteria != null) {
			cq.where(service.buildCriteriaFilter(criteria, cb, visit));
		}

		cq.orderBy(cb.desc(visit.get(ClinicalVisit.VISIT_DATE_TIME)));

		List<ClinicalVisitIndexDto> results = em.createQuery(cq).getResultList();

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		pseudonymizer.pseudonymizeDtoCollection(
			ClinicalVisitIndexDto.class,
			results,
			v -> caseJurisdictionChecker.isInJurisdictionOrOwned(v.getCaseJurisdiction()),
			null);

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
		return convertToDto(service.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid) {
		return saveClinicalVisit(clinicalVisit, caseUuid, true);
	}

	public ClinicalVisitDto saveClinicalVisit(ClinicalVisitDto clinicalVisit, String caseUuid, boolean handleChanges) {
		SymptomsHelper.updateIsSymptomatic(clinicalVisit.getSymptoms());

		ClinicalVisit existingClinicalVisit = service.getByUuid(clinicalVisit.getUuid());

		restorePseudonymizedDto(clinicalVisit, existingClinicalVisit);

		ClinicalVisit entity = fromDto(clinicalVisit, existingClinicalVisit, true);

		service.ensurePersisted(entity);

		if (handleChanges) {
			// Update case symptoms
			CaseDataDto caze = caseFacade.getCaseDataByUuid(caseUuid);
			SymptomsDto caseSymptoms = caze.getSymptoms();
			SymptomsHelper.updateSymptoms(clinicalVisit.getSymptoms(), caseSymptoms);
			caseFacade.saveCase(caze);
		}

		return convertToDto(entity, Pseudonymizer.getDefault(userService::hasRight));
	}

	/**
	 * Should only be used for synchronization purposes since the associated
	 * case symptoms are not updated from this method.
	 */
	@Override
	public ClinicalVisitDto saveClinicalVisit(@Valid ClinicalVisitDto clinicalVisit) {

		ClinicalCourse clinicalCourse = clinicalCourseService.getByReferenceDto(clinicalVisit.getClinicalCourse());
		return saveClinicalVisit(clinicalVisit, clinicalCourse.getCaze().getUuid());
	}

	@Override
	public void deleteClinicalVisit(String clinicalVisitUuid) {

		if (!userService.hasRight(UserRight.VISIT_DELETE)) {
			throw new UnsupportedOperationException("Your user is not allowed to delete clinical visits");
		}

		ClinicalVisit clinicalVisit = service.getByUuid(clinicalVisitUuid);
		service.delete(clinicalVisit);
	}

	@Override
	public List<ClinicalVisitDto> getAllActiveClinicalVisitsAfter(Date date) {

		if (userService.getCurrentUser() == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getAllActiveClinicalVisitsAfter(date).stream().map(t -> convertToDto(t, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<ClinicalVisitDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return service.getByUuids(uuids).stream().map(t -> convertToDto(t, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return service.getAllActiveUuids(user);
	}

	@Override
	public List<ClinicalVisitExportDto> getExportList(CaseCriteria criteria, Collection<String> selectedRows, int first, int max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClinicalVisitExportDto> cq = cb.createQuery(ClinicalVisitExportDto.class);
		Root<ClinicalVisit> clinicalVisit = cq.from(ClinicalVisit.class);
		ClinicalVisitJoins joins = new ClinicalVisitJoins(clinicalVisit);

		cq.multiselect(
			Stream
				.concat(
					Stream.of(
						joins.getCaze().get(Case.UUID),
						joins.getCasePerson().get(Person.FIRST_NAME),
						joins.getCasePerson().get(Person.LAST_NAME),
						clinicalVisit.get(ClinicalVisit.DISEASE),
						clinicalVisit.get(ClinicalVisit.VISIT_DATE_TIME),
						clinicalVisit.get(ClinicalVisit.VISIT_REMARKS),
						clinicalVisit.get(ClinicalVisit.VISITING_PERSON),
						joins.getSymptoms().get(Symptoms.ID)),
					getJurisdictionSelections(joins))
				.collect(Collectors.toList()));

		Predicate filter = service.createUserFilter(cb, cq, clinicalVisit);

		Predicate criteriaFilter = caseService.createCriteriaFilter(criteria, new CaseQueryContext(cb, cq, joins.getCaze()));
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, joins.getCaze().get(Case.UUID));
		cq.where(filter);
		cq.orderBy(cb.desc(joins.getCaze().get(Case.UUID)), cb.desc(clinicalVisit.get(ClinicalVisit.VISIT_DATE_TIME)));

		List<ClinicalVisitExportDto> resultList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight, I18nProperties.getCaption(Captions.inaccessibleValue));
		for (ClinicalVisitExportDto exportDto : resultList) {
			exportDto.setSymptoms(SymptomsFacadeEjb.toDto(symptomsService.getById(exportDto.getSymptomsId())));

			Boolean inJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(exportDto.getCaseJurisdiction());
			pseudonymizer.pseudonymizeDto(ClinicalVisitExportDto.class, exportDto, inJurisdiction, (v) -> {
				pseudonymizer.pseudonymizeDto(SymptomsDto.class, v.getSymptoms(), inJurisdiction, null);
			});
		}

		return resultList;
	}

	private Stream<Selection<?>> getJurisdictionSelections(ClinicalVisitJoins joins) {
		return Stream.of(
			joins.getCaseReportingUser().get(User.UUID),
			joins.getCaseRegion().get(Region.UUID),
			joins.getCaseDistrict().get(District.UUID),
			joins.getCaseCommunity().get(Community.UUID),
			joins.getCaseHealthFacility().get(Facility.UUID),
			joins.getCasePointOfEntry().get(PointOfEntry.UUID));
	}

	public ClinicalVisitDto convertToDto(ClinicalVisit source, Pseudonymizer pseudonymizer) {
		ClinicalVisitDto dto = toDto(source);

		pseudonymizeDto(source, dto, pseudonymizer);

		return dto;
	}

	private void pseudonymizeDto(ClinicalVisit source, ClinicalVisitDto dto, Pseudonymizer pseudonymizer) {
		if (source != null && dto != null) {
			Boolean inJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(source.getClinicalCourse().getCaze());
			pseudonymizer.pseudonymizeDto(ClinicalVisitDto.class, dto, inJurisdiction, v -> {
				pseudonymizer.pseudonymizeDto(SymptomsDto.class, dto.getSymptoms(), inJurisdiction, null);
			});
		}
	}

	private void restorePseudonymizedDto(ClinicalVisitDto clinicalVisit, ClinicalVisit existingClinicalVisit) {
		if (existingClinicalVisit != null) {
			Boolean inJurisdiction = caseJurisdictionChecker.isInJurisdictionOrOwned(existingClinicalVisit.getClinicalCourse().getCaze());
			Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
			ClinicalVisitDto existingDto = toDto(existingClinicalVisit);

			pseudonymizer.restorePseudonymizedValues(ClinicalVisitDto.class, clinicalVisit, existingDto, inJurisdiction);
			pseudonymizer.restorePseudonymizedValues(SymptomsDto.class, clinicalVisit.getSymptoms(), existingDto.getSymptoms(), inJurisdiction);
		}
	}

	public static ClinicalVisitDto toDto(ClinicalVisit source) {

		if (source == null) {
			return null;
		}

		ClinicalVisitDto target = new ClinicalVisitDto();
		DtoHelper.fillDto(target, source);

		target.setClinicalCourse(ClinicalCourseFacadeEjb.toReferenceDto(source.getClinicalCourse()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		target.setDisease(source.getDisease());
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

		return target;
	}

	public ClinicalVisit fromDto(@NotNull ClinicalVisitDto source, ClinicalVisit target, boolean checkChangeDate) {
		target = DtoHelper.fillOrBuildEntity(source, target, ClinicalVisit::new, checkChangeDate);

		target.setClinicalCourse(clinicalCourseService.getByReferenceDto(source.getClinicalCourse()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms(), checkChangeDate));
		target.setDisease(source.getDisease());
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitingPerson(source.getVisitingPerson());

		return target;
	}

	@LocalBean
	@Stateless
	public static class ClinicalVisitFacadeEjbLocal extends ClinicalVisitFacadeEjb {

	}
}
