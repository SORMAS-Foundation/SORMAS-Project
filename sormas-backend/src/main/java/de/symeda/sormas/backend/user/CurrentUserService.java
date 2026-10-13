package de.symeda.sormas.backend.user;

import static de.symeda.sormas.backend.user.UserHelper.isRestrictedToAssignEntities;

import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
@AuditIgnore
public class CurrentUserService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private SessionContext context;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final UserCache userCache;

	public CurrentUserService() {
		this.userCache = UserCache.getInstance();
	}

	/**
	 * Returns the User entity corresponding to the current user.
	 *
	 * @TransactionScoped would be better for performance, but is not supported by the CDI based testing framework
	 */
	//@RequestScoped
	@Transactional(Transactional.TxType.REQUIRED)
	public User getCurrentUser() {
		final String currentUsername = context.getCallerPrincipal().getName();

		if (currentUsername == null) {
			return null;
		}

		User cachedUser = userCache.get(currentUsername);
		if (cachedUser != null) {
			unProxyUserAddress(cachedUser);

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
		if (getCurrentUser() == null || getCurrentUser().getUserRoles() == null) {
			return false;
		}

		return getCurrentUser().hasUserRight(userRight); // todo cache this?
	}

	public boolean hasAnyUserRight(Set<UserRight> userRights) {
		// this only works for user rights that are used in RolesAllowed or DeclareRoles annotations.
		// return context.isCallerInRole(userRight.name());
		// We don't want to have to do this for all the user rights, so we check against the user rights of the current user instead
		if (getCurrentUser() == null || getCurrentUser().getUserRoles() == null) {
			return false;
		}

		return getCurrentUser().hasAnyUserRight(userRights);
	}

	public boolean isRestrictedToAssignedEntities() {
		return isRestrictedToAssignEntities(getCurrentUser());
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
		Fetch<Object, Object> fetch = user.fetch(User.USER_ROLES);
		fetch.fetch(UserRole.EMAIL_NOTIFICATIONS, JoinType.LEFT);
		fetch.fetch(UserRole.SMS_NOTIFICATIONS, JoinType.LEFT);

		final Predicate equal = cb.equal(cb.lower(user.get(User.USER_NAME)), userNameParam);
		cq.select(user).distinct(true);
		cq.where(equal);

		final TypedQuery<User> q = em.createQuery(cq).setParameter(userNameParam, userName.toLowerCase());

		User currentUser = q.getResultList().stream().findFirst().orElse(null);
		if (currentUser != null) {
			unproxy(currentUser.getRegion());
			unproxy(currentUser.getDistrict());
			unproxy(currentUser.getCommunity());
			unproxy(currentUser.getHealthFacility());
			unproxy(currentUser.getPointOfEntry());
			unproxy(currentUser.getLaboratory());
			unproxy(currentUser.getAssociatedOfficer());

			unProxyUserAddress(currentUser);

		}

		return currentUser;
	}

	private void unProxyUserAddress(User currentUser) {
		if (currentUser.getAddress() != null) {
			Location deProxiedAddress = unproxy(currentUser.getAddress());
			deProxiedAddress.setContinent(unproxy(deProxiedAddress.getContinent()));
			deProxiedAddress.setSubcontinent(unproxy(deProxiedAddress.getSubcontinent()));
			deProxiedAddress.setCountry(unproxy(deProxiedAddress.getCountry()));
			deProxiedAddress.setRegion(unproxy(deProxiedAddress.getRegion()));
			deProxiedAddress.setDistrict(unproxy(deProxiedAddress.getDistrict()));
			deProxiedAddress.setCommunity(unproxy(deProxiedAddress.getCommunity()));
			deProxiedAddress.setFacility(unproxy(deProxiedAddress.getFacility()));
			currentUser.setAddress(deProxiedAddress);
		}
	}

	public static <T> T unproxy(T proxied) {
		if (proxied instanceof HibernateProxy) {
			Hibernate.initialize(proxied);
			@SuppressWarnings("unchecked")
			T obj = (T) ((HibernateProxy) proxied).getHibernateLazyInitializer().getImplementation();
			return obj;
		} else {
			return proxied;
		}
	}
}
