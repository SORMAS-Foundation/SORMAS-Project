package de.symeda.sormas.backend.disease;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationCriteria;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;

@Stateless
@LocalBean
public class DiseaseConfigurationService extends AdoServiceWithUserFilterAndJurisdiction<DiseaseConfiguration> {

	private List<Disease> defaultActiveDiseases = new ArrayList<>();
	private List<Disease> defaultInactiveDiseases = new ArrayList<>();
	private List<Disease> defaultCaseSurveillanceDiseases = new ArrayList<>();
	private List<Disease> defaultAggregateReportingDiseases = new ArrayList<>();

	public DiseaseConfigurationService() {
		super(DiseaseConfiguration.class);

		for (Disease disease : Disease.values()) {
			if (disease.isDefaultActive()) {
				defaultActiveDiseases.add(disease);
			} else {
				defaultInactiveDiseases.add(disease);
			}

			if (disease.isDefaultCaseSurveillanceEnabled()) {
				defaultCaseSurveillanceDiseases.add(disease);
			}

			if (disease.isDefaultAggregateReportingEnabled()) {
				defaultAggregateReportingDiseases.add(disease);
			}
		}
	}

	public DiseaseConfiguration getDiseaseConfiguration(Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseConfiguration> cq = cb.createQuery(DiseaseConfiguration.class);
		Root<DiseaseConfiguration> root = cq.from(DiseaseConfiguration.class);

		ParameterExpression<Disease> diseaseParam = cb.parameter(Disease.class);
		cq.where(cb.equal(root.get(DiseaseConfiguration.DISEASE), diseaseParam));

		cq.select(root);
		return em.createQuery(cq).setParameter(diseaseParam, disease).getResultList().stream().findFirst().orElse(null);
	}

	public Subquery<Long> existActiveDisease(CriteriaQuery cq, CriteriaBuilder cb, Root<?> root, String diseaseFieldName) {
		Subquery sub = cq.subquery(Long.class);
		Root subRoot = sub.from(DiseaseConfiguration.class);

		sub.select(cb.literal(1));

		sub.where(
			cb.and(
				cb.equal(subRoot.get(DiseaseConfiguration.DISEASE), root.get(diseaseFieldName)),
				cb.or(cb.isNull(subRoot.get(DiseaseConfiguration.PRIMARY_DISEASE)), cb.isTrue(subRoot.get(DiseaseConfiguration.PRIMARY_DISEASE))),
				cb.or(
					cb.isNull(subRoot.get(DiseaseConfiguration.CASE_SURVEILLANCE_ENABLED)),
					cb.isTrue(subRoot.get(DiseaseConfiguration.CASE_SURVEILLANCE_ENABLED)))));

		return sub;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, DiseaseConfiguration> from) {
		return null;
	}

	public Predicate buildCriteriaFilter(DiseaseConfigurationCriteria criteria, CriteriaBuilder cb, Root<DiseaseConfiguration> from) {

		Predicate filter = null;

		if (criteria.getRelevanceStatus() != null) {
			switch (criteria.getRelevanceStatus()) {
			case ALL:
				filter = cb.isNotNull(from.get(DiseaseConfiguration.ID));
				break;
			case ACTIVE:
				filter = cb.or(
					cb.equal(from.get(DiseaseConfiguration.ACTIVE), true),
					cb.and(
						cb.isNull(from.get(DiseaseConfiguration.ACTIVE)),
						cb.in(from.get(DiseaseConfiguration.DISEASE)).value(defaultActiveDiseases)));
				break;
			case INACTIVE:
				filter = cb.or(
					cb.equal(from.get(DiseaseConfiguration.ACTIVE), false),
					cb.and(
						cb.isNull(from.get(DiseaseConfiguration.ACTIVE)),
						cb.in(from.get(DiseaseConfiguration.DISEASE)).value(defaultInactiveDiseases)));
				break;
			}
		}

		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(DiseaseConfiguration.DISEASE), criteria.getDisease()));
		}

		if (criteria.getReportingType() != null) {
			switch (criteria.getReportingType()) {
			case CASE_BASED_SURVEILLANCE:
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.equal(from.get(DiseaseConfiguration.CASE_SURVEILLANCE_ENABLED), true),
						cb.and(
							cb.isNull(from.get(DiseaseConfiguration.CASE_SURVEILLANCE_ENABLED)),
							cb.in(from.get(DiseaseConfiguration.DISEASE)).value(defaultCaseSurveillanceDiseases))));
				break;
			case AGGREGATE_REPORTING:
				filter = CriteriaBuilderHelper.and(
					cb,
					filter,
					cb.or(
						cb.equal(from.get(DiseaseConfiguration.AGGREGATE_REPORTING_ENABLED), true),
						cb.and(
							cb.isNull(from.get(DiseaseConfiguration.AGGREGATE_REPORTING_ENABLED)),
							cb.in(from.get(DiseaseConfiguration.DISEASE)).value(defaultAggregateReportingDiseases))));
				break;
			}
		}

		return filter;
	}

	public EntityManager getEntityManager() {
		return em;
	}
}
