package de.symeda.sormas.backend.user;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;

@Stateless
@LocalBean
public class UserService extends AbstractAdoService<User> {
	
	public UserService() {
		super(User.class);
	}
	
	public List<User> getListByUserRole(UserRole userRole) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		Join<User, UserRole> userRoles = from.join(User.USER_ROLES);
		cq.where(cb.equal(userRoles, userRole));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		return em.createQuery(cq).getResultList();
	}
}
