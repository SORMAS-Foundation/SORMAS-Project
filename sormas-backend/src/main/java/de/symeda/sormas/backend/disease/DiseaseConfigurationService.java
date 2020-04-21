package de.symeda.sormas.backend.disease;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.backend.common.AbstractAdoService;

@Stateless
@LocalBean
public class DiseaseConfigurationService extends AbstractAdoService<DiseaseConfiguration> {

	public DiseaseConfigurationService() {
		super(DiseaseConfiguration.class);
	}

	public DiseaseConfiguration getDiseaseConfiguration(Disease disease) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DiseaseConfiguration> cq = cb.createQuery(DiseaseConfiguration.class);
		Root<DiseaseConfiguration> root = cq.from(DiseaseConfiguration.class);

		Predicate filter = cb.equal(root.get(DiseaseConfiguration.DISEASE), disease);
		if (filter == null) {
			return null;
		} else {
			cq.where(filter);
		}

		cq.select(root);
		try {
			return em.createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq,
			From<DiseaseConfiguration, DiseaseConfiguration> from) {
		return null;
	}

}
