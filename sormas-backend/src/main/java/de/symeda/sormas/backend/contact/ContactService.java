package de.symeda.sormas.backend.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ContactService extends AbstractAdoService<Contact> {
	
	public ContactService() {
		super(Contact.class);
	}
	
	public List<Contact> getAllByCase(Case caze, User user) {

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

		// TODO add Filter for User
		Predicate filter = null;
		
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
}
