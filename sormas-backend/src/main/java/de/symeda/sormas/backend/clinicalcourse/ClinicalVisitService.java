package de.symeda.sormas.backend.clinicalcourse;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.clinicalcourse.ClinicalVisitCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ClinicalVisitService extends AdoServiceWithUserFilterAndJurisdiction<ClinicalVisit> {

	@EJB
	private CaseService caseService;

	public ClinicalVisitService() {
		super(ClinicalVisit.class);
	}

	public List<ClinicalVisit> findBy(ClinicalVisitCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ClinicalVisit> cq = cb.createQuery(getElementClass());
		Root<ClinicalVisit> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(criteria, cb, from);

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(ClinicalVisit.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ClinicalVisit> from) {

		Join<ClinicalVisit, ClinicalCourse> clinicalCourse = from.join(ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT);
		Join<ClinicalCourse, Case> caze = clinicalCourse.join(ClinicalCourse.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		return filter;
	}

	@Override
	protected List<String> referencesToBeFetched() {
		return Arrays.asList(ClinicalVisit.SYMPTOMS);
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ClinicalVisit> from = cq.from(getElementClass());
		Join<ClinicalVisit, ClinicalCourse> clinicalCourse = from.join(ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT);
		Join<ClinicalCourse, Case> caze = clinicalCourse.join(ClinicalCourse.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(ClinicalVisit.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(ClinicalVisitCriteria criteria, CriteriaBuilder cb, Root<ClinicalVisit> visit) {

		Predicate filter = null;
		Join<ClinicalVisit, ClinicalCourse> clinicalCourse = visit.join(ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT);

		if (criteria.getClinicalCourse() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(clinicalCourse.get(ClinicalCourse.UUID), criteria.getClinicalCourse().getUuid()));
		}

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, ClinicalVisit> clinicalVisits, Timestamp date) {
		return createChangeDateFilter(cb, clinicalVisits, date, null);
	}

	private Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, ClinicalVisit> clinicalVisits, Timestamp date, String lastSynchronizedUuid) {

		Join<ClinicalVisit, Symptoms> symptoms = clinicalVisits.join(ClinicalVisit.SYMPTOMS, JoinType.LEFT);

		ChangeDateFilterBuilder changeDateFilterBuilder = lastSynchronizedUuid == null
			? new ChangeDateFilterBuilder(cb, date)
			: new ChangeDateFilterBuilder(cb, date, clinicalVisits, lastSynchronizedUuid);
		return changeDateFilterBuilder.add(clinicalVisits).add(symptoms).build();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ClinicalVisit> from) {
		Join<ClinicalVisit, ClinicalCourse> clinicalCourse = from.join(ClinicalVisit.CLINICAL_COURSE, JoinType.LEFT);
		return caseService.createUserFilter(new CaseQueryContext(cb, cq, new CaseJoins(clinicalCourse.join(ClinicalCourse.CASE, JoinType.LEFT))));
	}

	@Override
	public boolean inJurisdictionOrOwned(ClinicalVisit entity) {
		return fulfillsCondition(entity, this::inJurisdictionOrOwned);
	}

	@Override
	public List<Long> getInJurisdictionIds(List<ClinicalVisit> entities) {
		return getIdList(entities, this::inJurisdictionOrOwned);
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, ClinicalVisit> from) {

		return caseService.inJurisdictionOrOwned(
			new CaseQueryContext(cb, query, new CaseJoins(from.join(ClinicalVisit.CLINICAL_COURSE).join(ClinicalCourse.CASE))));
	}
}
