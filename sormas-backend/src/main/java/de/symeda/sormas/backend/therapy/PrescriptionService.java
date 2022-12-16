package de.symeda.sormas.backend.therapy;

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

import de.symeda.sormas.api.RequestContextHolder;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class PrescriptionService extends AdoServiceWithUserFilterAndJurisdiction<Prescription> {

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

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Prescription> from) {

		Join<Prescription, Therapy> therapy = from.join(Prescription.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);

		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		return filter;
	}

	@Override
	protected Predicate createLimitedChangeDateFilter(CriteriaBuilder cb, From<?, Prescription> from) {
		return null;
	}

	@Override
	protected Predicate createLimitedChangeDateFilterForObsoleteEntities(CriteriaBuilder cb, From<?, Prescription> from) {
		return null;

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

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = createLimitedChangeDateFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
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

	@Override
	public Predicate createUserFilterForObsoleteSync(CriteriaBuilder cb, CriteriaQuery cq, From<?, Prescription> from) {

		Join<Prescription, Therapy> therapy = from.join(Prescription.THERAPY, JoinType.LEFT);
		return caseService.createUserFilterForObsoleteSync(new CaseQueryContext(cb, cq, new CaseJoins(therapy.join(Therapy.CASE, JoinType.LEFT))));
	}

	@Override
	public boolean inJurisdictionOrOwned(Prescription entity) {
		return fulfillsCondition(entity, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	@Override
	public List<Long> getInJurisdictionIds(List<Prescription> entities) {
		return getIdList(entities, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Prescription> from) {

		return caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, query, new CaseJoins(from.join(Prescription.THERAPY).join(Therapy.CASE))));
	}
}
