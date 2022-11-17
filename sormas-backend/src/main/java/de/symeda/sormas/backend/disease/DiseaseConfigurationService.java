package de.symeda.sormas.backend.disease;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;

@Stateless
@LocalBean
public class DiseaseConfigurationService extends AdoServiceWithUserFilterAndJurisdiction<DiseaseConfiguration> {

	public DiseaseConfigurationService() {
		super(DiseaseConfiguration.class);
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
}
