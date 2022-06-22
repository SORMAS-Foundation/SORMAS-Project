package de.symeda.sormas.backend.user;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class CurrentUserService {

	@Resource
	private SessionContext context;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal userRoleConfigFacade;

	private final UserCache userCache;

	public CurrentUserService() {
		this.userCache = UserCache.getInstance();
	}

	/**
	 * Returns the User entity corresponding to the current user.
	 */
	@RequestScoped
// FIXME @TransactionScoped would be better for performance, but is not support by novatec.bean-test (see their github #4)
	public User getCurrentUser() {
		final String currentUsername = context.getCallerPrincipal().getName();

		User cachedUser = userCache.get(currentUsername);
		if (cachedUser != null) {
			return cachedUser;
		}

		// todo prohibit these names
		if (currentUsername.equals("ANONYMOUS") || currentUsername.equals("SYSTEM")) {
			return null;
		}

		final User currentUser = fetchUser(currentUsername);

		if (currentUser == null) {
			return null;
		} else {
			userCache.put(currentUsername, currentUser);
			return currentUser;
		}
	}

	public boolean hasUserRight(UserRight userRight) {
		// this only works for user rights that are used in RolesAllowed or DeclareRoles annotations.
		// return context.isCallerInRole(userRight.name());
		// We don't want to have to do this for all the user rights, so we check against the user rights of the current user instead
		return userRoleConfigFacade.hasUserRight(getCurrentUser().getUserRoles(), userRight); // would be better to cache this, but will be changed in 1.73 anyway
	}

	// We need a clean transaction as we do not want call potential entity listeners which would lead to recursion
	@Transactional(Transactional.TxType.REQUIRES_NEW)
	User fetchUser(String userName) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
		final CriteriaQuery<User> cq = cb.createQuery(User.class);

		// avoid "Hibernate could not initialize proxy – no Session" Exception
		// do eager loading in this case
		final Root<User> user = cq.from(User.class);
		user.fetch(User.ADDRESS);

		final Predicate equal = cb.equal(cb.lower(user.get(User.USER_NAME)), userNameParam);
		cq.select(user).distinct(true);
		cq.where(equal);

		final TypedQuery<User> q = em.createQuery(cq).setParameter(userNameParam, userName.toLowerCase());

		return q.getResultList().stream().findFirst().orElse(null);
	}
}
