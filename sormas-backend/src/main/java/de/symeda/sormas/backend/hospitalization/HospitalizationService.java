package de.symeda.sormas.backend.hospitalization;

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
public class HospitalizationService extends AbstractAdoService<Hospitalization> {
	
	public HospitalizationService() {
		super(Hospitalization.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Hospitalization, Hospitalization> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
