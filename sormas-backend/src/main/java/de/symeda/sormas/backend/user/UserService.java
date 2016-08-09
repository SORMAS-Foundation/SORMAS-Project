package de.symeda.sormas.backend.user;

import java.util.Arrays;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.util.PasswordHelper;

@Stateless
@LocalBean
public class UserService extends AbstractAdoService<User> {
	
	public UserService() {
		super(User.class);
	}
	
	public User getByUserName(String userName) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(User.USER_NAME), userNameParam));
		
		TypedQuery<User> q = em.createQuery(cq)
			.setParameter(userNameParam, userName);
		
		User entity = q.getResultList().stream()
				.findFirst()
				.orElse(null);
		
		return entity;
	}
	
	public User createUser() {
		User user = new User();
		// dummy password to make sure no one can login with this user
		String password = PasswordHelper.createPass(12);
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));
		
		return user;
	}
	
	public List<User> getListByUserRoles(UserRole... userRole) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		Join<User, UserRole> userRoles = from.join(User.USER_ROLES, JoinType.LEFT);
		cq.where(userRoles.in(Arrays.asList(userRole)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));
		return em.createQuery(cq).getResultList();
	}

	public boolean isLoginUnique(String uuid, String userName) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		CriteriaQuery<User> cq = cb.createQuery(getElementClass());
		Root<User> from = cq.from(getElementClass());
		cq.where(cb.equal(from.get(User.USER_NAME), userNameParam));
		
		TypedQuery<User> q = em.createQuery(cq)
			.setParameter(userNameParam, userName);
		
		User entity = q.getResultList().stream()
				.findFirst()
				.orElse(null);
		
		return entity==null || (entity!=null&&entity.getUuid().equals(uuid));
	}
	
	public String resetPassword(String userUuid) {
		User user = getByUuid(userUuid);

		if (user == null) {
//			logger.warn("resetPassword() for unknown user '{}'", realmUserUuid);
			return null;
		}

		String password = PasswordHelper.createPass(12);
		user.setSeed(PasswordHelper.createPass(16));
		user.setPassword(PasswordHelper.encodePassword(password, user.getSeed()));

		return password;
	}
}
