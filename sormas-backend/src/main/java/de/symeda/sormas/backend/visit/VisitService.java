package de.symeda.sormas.backend.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class VisitService extends AbstractAdoService<Visit> {
	
	@EJB
	ContactService contactService;
	
	public VisitService() {
		super(Visit.class);
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public List<Visit> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> visitsQuery = cb.createQuery(getElementClass());
		Root<Visit> visitRoot = visitsQuery.from(Visit.class);

		// get all visits of the user's contact's persons
		Subquery<Integer> contactPersonSubquery = visitsQuery.subquery(Integer.class);
		Root<Contact> contactRoot = contactPersonSubquery.from(Contact.class);
		contactPersonSubquery.where(contactService.createUserFilter(cb, contactRoot, user));
		contactPersonSubquery.select(contactRoot.get(Contact.PERSON).get(Person.ID));
		
		Predicate filter = cb.in(visitRoot.get(Visit.PERSON).get(Person.ID)).value(contactPersonSubquery);
		// date range
		if (date != null) {
			Predicate dateFilter = cb.greaterThan(visitRoot.get(AbstractDomainObject.CHANGE_DATE), date);
			filter = cb.and(filter, dateFilter);
		}
		visitsQuery.where(filter);
		visitsQuery.distinct(true);
		visitsQuery.orderBy(cb.asc(visitRoot.get(AbstractDomainObject.ID)));
		
		List<Visit> resultList = em.createQuery(visitsQuery).getResultList();
		return resultList;
	}

	/**
	 * All visits of the contact person with the same disease and within lastContactDate and followUpUntil
	 */
	public List<Visit> getAllByContact(Contact contact) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		// all of the person
		Predicate filter = cb.equal(from.get(Visit.PERSON), contact.getPerson());

		// only disease relevant
		filter = cb.and(filter, cb.equal(from.get(Visit.DISEASE), contact.getCaze().getDisease()));
		
		// list all visits between contact date ...
		// IMPORTANT: This is different than the calculation of "follow-up until", where the date of report is used as reference
		// We also want to have visits that took place before.
		if (contact.getLastContactDate() != null) {
			Predicate dateStartFilter = cb.greaterThan(from.get(Visit.VISIT_DATE_TIME), DateHelper.subtractDays(contact.getLastContactDate(), 10));
			filter = cb.and(filter, dateStartFilter);
		} else {
			// use date of report as fallback
			Predicate dateStartFilter = cb.greaterThan(from.get(Visit.VISIT_DATE_TIME), contact.getReportDateTime());
			filter = cb.and(filter, dateStartFilter);
		}

		// .. and follow-up until
		if (contact.getFollowUpUntil() != null) {
			Predicate dateFilter = cb.lessThan(from.get(Visit.VISIT_DATE_TIME), DateHelper.addDays(contact.getFollowUpUntil(), 10));
			filter = cb.and(filter, dateFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Visit.VISIT_DATE_TIME)));

		List<Visit> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public List<Visit> getAllByPerson(Person person) {
		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		// all of the person
		Predicate filter = cb.equal(from.get(Visit.PERSON), person);
		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Visit.VISIT_DATE_TIME)));

		List<Visit> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
}
