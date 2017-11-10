package de.symeda.sormas.backend.region;

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
public class CommunityService extends AbstractAdoService<Community> {
	
	public CommunityService() {
		super(Community.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Community, Community> from, User user) {
		// no fitler by user needed
		return null;
	}
}
