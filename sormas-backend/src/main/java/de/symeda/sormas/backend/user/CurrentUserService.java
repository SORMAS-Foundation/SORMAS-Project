package de.symeda.sormas.backend.user;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import de.symeda.sormas.backend.util.ModelConstants;

/**
 * The class CurrentUserService.
 */
@Stateless
@LocalBean
public class CurrentUserService {

	@Resource
	private SessionContext context;

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

    /**
     * Returns the User entity corresponding to the current user.
     *
     * @return
     */
    @Produces
    @CurrentUserQualifier
    @RequestScoped
    @Transactional
    public CurrentUser getCurrentUser() {
        final String userName = context.getCallerPrincipal().getName();
        if (userName.equalsIgnoreCase("ANONYMOUS")) {
            return new CurrentUser(null);
        }

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final ParameterExpression<String> userNameParam = cb.parameter(String.class, User.USER_NAME);
        final CriteriaQuery<User> cq = cb.createQuery(User.class);
        final Root<User> from = cq.from(User.class);
        cq.where(cb.equal(from.get(User.USER_NAME), userNameParam));

        final TypedQuery<User> q = em.createQuery(cq)
                .setParameter(userNameParam, userName);

        final User user = q.getResultList().stream()
                .findFirst()
                .orElse(null);

        if (user != null) {
            user.getUserRoles().size();
            return new CurrentUser(user);
        } else {
            return new CurrentUser(null);
        }
    }
}
