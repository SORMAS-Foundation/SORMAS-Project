package de.symeda.sormas.backend.therapy;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.therapy.TreatmentCriteria;
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
public class TreatmentService extends AdoServiceWithUserFilterAndJurisdiction<Treatment> {

	@EJB
	private CaseService caseService;
	@EJB
	protected FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

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

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Treatment> from) {

		Join<Treatment, Therapy> therapy = from.join(Treatment.THERAPY, JoinType.LEFT);
		Join<Therapy, Case> caze = therapy.join(Therapy.CASE, JoinType.LEFT);
		Predicate filter = caseService.createActiveCasesFilter(cb, caze);

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		return filter;
	}

	@Override
	protected Predicate limitSynchronizationFilter(CriteriaBuilder cb, From<?, Treatment> from) {
		final Integer maxChangeDatePeriod = featureConfigurationFacade
			.getProperty(FeatureType.LIMITED_MOBILE_SYNCHRONIZATION, null, FeatureTypeProperty.MAX_CHANGEDATE_SYNCHRONIZATION, Integer.class);
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.LIMITED_MOBILE_SYNCHRONIZATION)
			&& maxChangeDatePeriod != null && maxChangeDatePeriod != -1) {
			Timestamp timestamp = Timestamp.from(DateHelper.subtractDays(new Date(), maxChangeDatePeriod).toInstant());
			return CriteriaBuilderHelper.and(cb, cb.greaterThanOrEqualTo(from.get(Treatment.CHANGE_DATE), timestamp));
		}
		return null;
	}

	@Override
	protected Predicate limitSynchronizationFilterObsoleteEntities(CriteriaBuilder cb, From<?, Treatment> from) {
		final Integer maxChangeDatePeriod = featureConfigurationFacade
			.getProperty(FeatureType.LIMITED_MOBILE_SYNCHRONIZATION, null, FeatureTypeProperty.MAX_CHANGEDATE_SYNCHRONIZATION, Integer.class);
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.LIMITED_MOBILE_SYNCHRONIZATION)
			&& maxChangeDatePeriod != null && maxChangeDatePeriod != -1) {
			Timestamp timestamp = Timestamp.from(DateHelper.subtractDays(new Date(), maxChangeDatePeriod).toInstant());
			return CriteriaBuilderHelper.and(cb, cb.lessThan(from.get(Treatment.CHANGE_DATE), timestamp));
		}
		return null;
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

		if (RequestContextHolder.isMobileSync()) {
			Predicate predicate = limitSynchronizationFilter(cb, from);
			if (predicate != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, predicate);
			}
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
		return caseService.createUserFilter(new CaseQueryContext(cb, cq, new CaseJoins(therapy.join(Therapy.CASE, JoinType.LEFT))));
	}

	public void unlinkPrescriptionFromTreatments(List<String> treatmentUuids){
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Treatment> criteriaUpdate = cb.createCriteriaUpdate(getElementClass());
		Root<Treatment> from = criteriaUpdate.from(getElementClass());

		criteriaUpdate.set(Treatment.PRESCRIPTION, null);

		criteriaUpdate.where(from.get(Treatment.UUID).in(treatmentUuids));

		this.em.createQuery(criteriaUpdate).executeUpdate();
	}

	@Override
	public boolean inJurisdictionOrOwned(Treatment entity) {
		return fulfillsCondition(entity, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	@Override
	public List<Long> getInJurisdictionIds(List<Treatment> entities) {
		return getIdList(entities, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Treatment> from) {

		return caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, query, new CaseJoins(from.join(Treatment.THERAPY).join(Therapy.CASE))));
	}
}
