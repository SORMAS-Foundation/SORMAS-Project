package de.symeda.sormas.backend.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ContactService extends AbstractAdoService<Contact> {
	
	@EJB
	CaseService caseService;
	
	public ContactService() {
		super(Contact.class);
	}
	
	public List<Contact> getAllByCase(Case caze) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		if (caze != null) {
			cq.where(cb.equal(from.get(Contact.CAZE), caze));
		}
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Contact> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, from, user);
				
		if (date != null) {
			Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(AbstractDomainObject.ID)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public Predicate createUserFilter(CriteriaBuilder cb, From<Contact, Contact> contactPath, User user) {
		// whoever created it or is assigned to it is allowed to access it
		Predicate filter = cb.equal(contactPath.get(Contact.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(contactPath.get(Contact.CONTACT_OFFICER), user));
		
		Predicate userFilter = caseService.createUserFilter(cb, contactPath.join(Contact.CAZE, JoinType.LEFT), user);
		filter = cb.or(filter, userFilter);
		return filter;
	}
}
