package de.symeda.sormas.backend.deletionconfiguration;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.BaseAdoService;

@Stateless
@LocalBean
public class DeletionConfigurationService extends BaseAdoService<DeletionConfiguration> {

	public DeletionConfigurationService() {
		super(DeletionConfiguration.class);
	}

	public DeletionConfiguration getForEntityType(CoreEntityType coreEntityType) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DeletionConfiguration> cq = cb.createQuery(getElementClass());
		Root<DeletionConfiguration> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(DeletionConfiguration.ENTITY_TYPE), coreEntityType));

		return em.createQuery(cq).getSingleResult();
	}

}
