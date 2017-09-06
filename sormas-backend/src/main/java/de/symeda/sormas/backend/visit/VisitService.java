package de.symeda.sormas.backend.visit;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DateHelper8;

@Stateless
@LocalBean
public class VisitService extends AbstractAdoService<Visit> {
	
	@EJB
	ContactService contactService;
	
	public VisitService() {
		super(Visit.class);
	}
	
	@Override
	public List<String> getAllUuids(User user) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Visit> visitRoot = visitsQuery.from(Visit.class);
		visitsQuery.select(visitRoot.get(Visit.UUID));

		// get all visits of the user's contact's persons
		Subquery<Long> contactPersonSubquery = visitsQuery.subquery(Long.class);
		Root<Contact> contactRoot = contactPersonSubquery.from(Contact.class);
		contactPersonSubquery.where(contactService.createUserFilter(cb, visitsQuery, contactRoot, user));
		contactPersonSubquery.select(contactRoot.get(Contact.PERSON).get(Person.ID));
		
		Predicate filter = cb.in(visitRoot.get(Visit.PERSON).get(Person.ID)).value(contactPersonSubquery);
		visitsQuery.where(filter);
		visitsQuery.distinct(true);
		visitsQuery.orderBy(cb.asc(visitRoot.get(AbstractDomainObject.ID)));
		
		List<String> resultList = em.createQuery(visitsQuery).getResultList();
		return resultList;	
	}
	
	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public List<Visit> getAllAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> visitRoot = cq.from(Visit.class);

		// get all visits of the user's contact's persons
		Subquery<Integer> contactPersonSubquery = cq.subquery(Integer.class);
		Root<Contact> contactRoot = contactPersonSubquery.from(Contact.class);
		contactPersonSubquery.where(contactService.createUserFilter(cb, cq, contactRoot, user));
		contactPersonSubquery.select(contactRoot.get(Contact.PERSON).get(Person.ID));
		Predicate filter = cb.in(visitRoot.get(Visit.PERSON).get(Person.ID)).value(contactPersonSubquery);
		// date range
		if (date != null) {
			filter = cb.and(filter, createDateFilter(cb, cq, visitRoot, date));
		}
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(visitRoot.get(AbstractDomainObject.ID)));
		
		List<Visit> resultList = em.createQuery(cq).getResultList();
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
	
	public Visit getLastVisitByContact(Contact contact, VisitStatus visitStatus) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		// all of the person
		Predicate filter = cb.equal(from.get(Visit.PERSON), contact.getPerson());

		// only disease relevant
		filter = cb.and(filter, cb.equal(from.get(Visit.DISEASE), contact.getCaze().getDisease()));
		
		// only visits with the given visit status, if present
		if (visitStatus != null) {
			filter = cb.and(filter, cb.equal(from.get(Visit.VISIT_STATUS), visitStatus));
		}
		
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
		cq.orderBy(cb.desc(from.get(Visit.VISIT_DATE_TIME)));

		List<Visit> result = em.createQuery(cq).getResultList();
		return result.size() > 0 ? result.get(0) : null;
	}
	
	public Visit getLastVisitByPerson(Person person, Disease disease, LocalDate maxDate) {
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		// all of the person
		Predicate filter = cb.equal(from.get(Visit.PERSON), person);

		// only disease relevant
		filter = cb.and(filter, cb.equal(from.get(Visit.DISEASE), disease));
		
		// before or equal date
		Predicate dateFilter = cb.lessThan(from.get(Visit.VISIT_DATE_TIME), DateHelper8.toDate(maxDate.plusDays(1)));
		filter = cb.and(filter, dateFilter);

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Visit.VISIT_DATE_TIME)));

		try {
			Visit result = em.createQuery(cq).getSingleResult();
			return result;
		} 
		catch (NoResultException | NonUniqueResultException ex) {
			return null;
		}
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
	
	@Override
	protected Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Visit, Visit> from, User user) {
		// getAllUuids and getAllAfter have custom implementations
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected Predicate createDateFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Visit,Visit> visitPath, Date date) {
		
		Predicate dateFilter = cb.greaterThan(visitPath.get(Visit.CHANGE_DATE), date);
		
		Join<Visit, Symptoms> symptoms = visitPath.join(Visit.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.join(Symptoms.ILLLOCATION, JoinType.LEFT).get(Location.CHANGE_DATE), date));
		
		return dateFilter;
	}
}
