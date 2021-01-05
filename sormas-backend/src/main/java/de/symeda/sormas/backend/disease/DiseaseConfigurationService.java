package de.symeda.sormas.backend.disease;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;

@Stateless
@LocalBean
public class DiseaseConfigurationService extends AdoServiceWithUserFilter<DiseaseConfiguration> {

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

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, DiseaseConfiguration> from) {
		return null;
	}
}
