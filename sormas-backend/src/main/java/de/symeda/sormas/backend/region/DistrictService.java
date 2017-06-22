package de.symeda.sormas.backend.region;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.user.User;



@Stateless
@LocalBean
public class DistrictService extends AbstractAdoService<District> {
	
	public DistrictService() {
		super(District.class);
	}

	@Override
	protected Predicate createUserFilter(CriteriaBuilder cb, From<District, District> from, User user) {
		// A user should not directly query for this
		throw new UnsupportedOperationException();
	}
}
