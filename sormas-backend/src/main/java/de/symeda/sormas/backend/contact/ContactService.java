package de.symeda.sormas.backend.contact;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
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

		cq.where(cb.equal(from.get(Contact.CAZE), caze));
		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

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
		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Contact> getFollowUpBetween(Date fromDate, Date toDate, Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		
		Predicate filter = createUserFilter(cb, from, user);
		Predicate followUpFilter = cb.isNotNull(from.get(Contact.FOLLOW_UP_UNTIL));
		followUpFilter = cb.and(followUpFilter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), fromDate));
		followUpFilter = cb.and(followUpFilter, cb.lessThanOrEqualTo(from.get(Contact.LAST_CONTACT_DATE), toDate));
		
		if (filter != null) {
			filter = cb.and(filter, followUpFilter);
		} else {
			filter = followUpFilter;
		}
		
		if (filter != null && disease != null) {
			Join<Contact, Case> caze = from.join(Contact.CAZE);
			filter = cb.and(filter, cb.equal(caze.get(Case.DISEASE), disease));
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		
		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public Predicate createUserFilter(CriteriaBuilder cb, From<Contact, Contact> contactPath, User user) {
		// whoever created it or is assigned to it is allowed to access it
		Predicate filter = cb.equal(contactPath.get(Contact.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(contactPath.get(Contact.CONTACT_OFFICER), user));
		
		Predicate userFilter = caseService.createUserFilter(cb, contactPath.join(Contact.CAZE, JoinType.LEFT), user);
		filter = cb.or(filter, userFilter);
		return filter;
	}
}
