package de.symeda.sormas.backend.dashboard;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.utils.CaseJoins;

@Stateless
@LocalBean
public class DashboardService {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private CaseService caseService;

	public List<DashboardCaseDto> getCases(CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardCaseDto> cq = cb.createQuery(DashboardCaseDto.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins<Case> joins = (CaseJoins<Case>) caseQueryContext.getJoins();
		Join<Case, Symptoms> symptoms = joins.getSymptoms();
		Join<Case, Person> person = joins.getPerson();

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		List<DashboardCaseDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
				caze.get(Case.ID),
				caze.get(Case.UUID),
				caze.get(Case.REPORT_DATE),
				symptoms.get(Symptoms.ONSET_DATE),
				caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.DISEASE),
				caze.get(Case.INVESTIGATION_STATUS),
				person.get(Person.PRESENT_CONDITION),
				person.get(Person.CAUSE_OF_DEATH_DISEASE));

			result = em.createQuery(cq).getResultList();
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	public Map<CaseClassification, Integer> getCasesCountByClassification(CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(true));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);

		if (filter != null) {
			cq.where(filter);
		}

		Map<CaseClassification, Integer> result;
		if (filter != null) {
			cq.multiselect(caze.get(Case.CASE_CLASSIFICATION), cb.count(caze.get(Case.CASE_CLASSIFICATION)));
			cq.where(filter);
			cq.groupBy(caze.get(Case.CASE_CLASSIFICATION));

			result = em.createQuery(cq)
				.getResultStream()
				.collect(Collectors.toMap(tuple -> (CaseClassification) tuple[0], tuple -> ((Number) tuple[1]).intValue()));
		} else {
			result = Collections.emptyMap();
		}

		return result;
	}

	public List<DashboardQuarantineDataDto> getQuarantineData(CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardQuarantineDataDto> cq = cb.createQuery(DashboardQuarantineDataDto.class);
		Root<Case> caze = cq.from(Case.class);

		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);

		Predicate filter = caseService.createUserFilter(cb, cq, caze, new CaseUserFilterCriteria().excludeCasesFromContacts(false));
		Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, caseQueryContext);
		filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		filter =
			CriteriaBuilderHelper.and(cb, filter, cb.notEqual(caseQueryContext.getRoot().get(Case.CASE_CLASSIFICATION), CaseClassification.NO_CASE));

		Predicate dateFilter = buildQuarantineDateFilter(cb, caze, caseCriteria.getNewCaseDateFrom(), caseCriteria.getNewCaseDateTo());
		if (filter != null) {
			filter = cb.and(filter, dateFilter);
		} else {
			filter = dateFilter;
		}

		if (filter != null) {
			cq.where(filter);
			cq.multiselect(caze.get(AbstractDomainObject.ID), caze.get(Case.QUARANTINE_FROM), caze.get(Case.QUARANTINE_TO));

			return em.createQuery(cq).getResultList();
		}

		return Collections.emptyList();
	}

	private Predicate buildQuarantineDateFilter(CriteriaBuilder cb, Root<Case> caze, Date fromDate, Date toDate) {
		Predicate filter = null;
		if (fromDate != null && toDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.between(caze.get(Case.QUARANTINE_FROM), fromDate, toDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.between(caze.get(Case.QUARANTINE_TO), fromDate, toDate)),
				cb.and(
					cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_TO), fromDate),
					cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), toDate)));
		} else if (fromDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), fromDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.greaterThanOrEqualTo(caze.get(Case.QUARANTINE_TO), fromDate)));
		} else if (toDate != null) {
			filter = cb.or(
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_FROM)), cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_TO), toDate)),
				cb.and(cb.isNull(caze.get(Case.QUARANTINE_TO)), cb.lessThanOrEqualTo(caze.get(Case.QUARANTINE_FROM), toDate)));
		}

		return filter;
	}
}
