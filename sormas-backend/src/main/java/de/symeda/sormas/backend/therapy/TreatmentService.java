package de.symeda.sormas.backend.therapy;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class TreatmentService extends AdoServiceWithUserFilter<Treatment> {

	@EJB
	private CaseService caseService;

	public TreatmentService() {
		super(Treatment.class);
	}

	public List<Treatment> findBy(TreatmentCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Treatment> cq = cb.createQuery(getElementClass());
		Root<Treatment> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(criteria, cb, from);

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Treatment.CREATION_DATE)));

		List<Treatment> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Object[]> getTreatmentCountByCases(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Treatment> treatmentRoot = cq.from(getElementClass());
		Join<Treatment, Therapy> therapyJoin = treatmentRoot.join(Treatment.THERAPY, JoinType.LEFT);
		Root<Case> caseRoot = cq.from(Case.class);
		Join<Case, Therapy> caseTherapyJoin = caseRoot.join(Case.THERAPY, JoinType.LEFT);

		cq.multiselect(caseRoot.get(Case.ID), cb.count(treatmentRoot));

		Expression<String> caseIdsExpression = caseRoot.get(Case.ID);
		cq.where(cb.and(caseIdsExpression.in(caseIds), cb.equal(therapyJoin.get(Therapy.ID), caseTherapyJoin.get(Therapy.ID))));
		cq.groupBy(caseRoot.get(Case.ID));

		return em.createQuery(cq).getResultList();
	}

	public List<Treatment> getAllActiveTreatmentsAfter(Date date, User user, Integer batchSize, String lastSynchronizedUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Treatment> cq = cb.createQuery(getElementClass());
		Root<Treatment> from = cq.from(getElementClass());
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date, lastSynchronizedUuid);
			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}

		cq.where(filter);
		cq.distinct(true);

		return getBatchedQueryResults(cb, cq, from, batchSize);
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Treatment> from = cq.from(getElementClass());
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Treatment.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(TreatmentCriteria criteria, CriteriaBuilder cb, Root<Treatment> treatment) {

		Predicate filter = null;
		Join<Treatment, Therapy> therapy = treatment.join(Treatment.THERAPY, JoinType.LEFT);

		if (criteria.getTherapy() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(therapy.get(Therapy.UUID), criteria.getTherapy().getUuid()));
		}
		if (criteria.getTreatmentType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(treatment.get(Treatment.TREATMENT_TYPE), criteria.getTreatmentType()));
		}
		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				// #1389: Disabled the possibility to search in TREATMENT_TYPE and TYPE_OF_DRUG
				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, treatment.get(Treatment.TREATMENT_DETAILS), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, treatment.get(Treatment.EXECUTING_CLINICIAN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Treatment> from) {
		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		return caseService.createUserFilter(cb, cq, therapy.join(Therapy.CASE, JoinType.LEFT));
	}
}
