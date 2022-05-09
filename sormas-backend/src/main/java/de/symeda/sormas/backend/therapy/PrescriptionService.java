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

import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PrescriptionService extends AdoServiceWithUserFilter<Prescription> {

	@EJB
	private CaseService caseService;

	public PrescriptionService() {
		super(Prescription.class);
	}

	public List<Prescription> findBy(PrescriptionCriteria prescriptionCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Prescription> cq = cb.createQuery(getElementClass());
		Root<Prescription> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(prescriptionCriteria, cb, from);

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Prescription.CREATION_DATE)));

		List<Prescription> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Prescription> getAllActivePrescriptionsAfter(Date date, User user, Integer batchSize, String lastSynchronizedUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Prescription> cq = cb.createQuery(getElementClass());
		Root<Prescription> from = cq.from(getElementClass());
		Join<Prescription, Therapy> therapy = from.join(Prescription.THERAPY, JoinType.LEFT);
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

	public List<Object[]> getPrescriptionCountByCases(List<Long> caseIds) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Prescription> prescriptionRoot = cq.from(getElementClass());
		Join<Prescription, Therapy> therapyJoin = prescriptionRoot.join(Prescription.THERAPY, JoinType.LEFT);
		Root<Case> caseRoot = cq.from(Case.class);
		Join<Case, Therapy> caseTherapyJoin = caseRoot.join(Case.THERAPY, JoinType.LEFT);

		cq.multiselect(caseRoot.get(Case.ID), cb.count(prescriptionRoot));

		Expression<String> caseIdsExpression = caseRoot.get(Case.ID);
		cq.where(cb.and(caseIdsExpression.in(caseIds), cb.equal(therapyJoin.get(Therapy.ID), caseTherapyJoin.get(Therapy.ID))));
		cq.groupBy(caseRoot.get(Case.ID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllActiveUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Prescription> from = cq.from(getElementClass());
		Join<Prescription, Therapy> therapy = from.join(Prescription.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}

		cq.where(filter);
		cq.select(from.get(Prescription.UUID));

		return em.createQuery(cq).getResultList();
	}

	public Predicate buildCriteriaFilter(PrescriptionCriteria criteria, CriteriaBuilder cb, Root<Prescription> prescription) {

		Predicate filter = null;
		Join<Prescription, Therapy> therapy = prescription.join(Prescription.THERAPY, JoinType.LEFT);

		if (criteria.getTherapy() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(therapy.get(Therapy.UUID), criteria.getTherapy().getUuid()));
		}
		if (criteria.getPrescriptionType() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(prescription.get(Prescription.PRESCRIPTION_TYPE), criteria.getPrescriptionType()));
		}
		if (!StringUtils.isEmpty(criteria.getTextFilter())) {
			String[] textFilters = criteria.getTextFilter().split("\\s+");
			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				// #1389: Disabled the possibility to search in PRESCRIPTION_TYPE and TYPE_OF_DRUG
				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, prescription.get(Prescription.PRESCRIPTION_DETAILS), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, prescription.get(Prescription.PRESCRIBING_CLINICIAN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Prescription> from) {

		Join<Prescription, Therapy> therapy = from.join(Prescription.THERAPY, JoinType.LEFT);
		return caseService.createUserFilter(new CaseQueryContext(cb, cq, new CaseJoins(therapy.join(Therapy.CASE, JoinType.LEFT))));
	}
}
