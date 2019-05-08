package de.symeda.sormas.backend.disease;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class DiseaseConfigurationService extends AbstractAdoService<DiseaseConfiguration> {

	public DiseaseConfigurationService() {
		super(DiseaseConfiguration.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq,
			From<DiseaseConfiguration, DiseaseConfiguration> from, User user) {
		return null;
	}
	
}
