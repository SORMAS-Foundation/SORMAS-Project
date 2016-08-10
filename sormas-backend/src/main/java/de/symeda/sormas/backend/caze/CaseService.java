package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class CaseService extends AbstractAdoService<Case> {
	
	public CaseService() {
		super(Case.class);
	}

	public Case createCase(Person person) {
		
		Case caze = new Case();
		caze.setPerson(person);
		return caze;
	}
	
	public List<Case> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Case> cq = cb.createQuery(getElementClass());
		Root<Case> from = cq.from(getElementClass());

		Predicate userFilter = cb.equal(from.get(Case.REPORTING_USER), user);
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_OFFICER)) {
			userFilter = cb.or(userFilter, cb.equal(from.get(Case.SURVEILLANCE_OFFICER), user));
		}
		if (user.getUserRoles().contains(UserRole.SURVEILLANCE_SUPERVISOR)) {
			userFilter = cb.or(userFilter, cb.equal(from.get(Case.SURVEILLANCE_SUPERVISOR), user));
		}
		
		cq.where(cb.and(userFilter, cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date)));
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		List<Case> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}


}
