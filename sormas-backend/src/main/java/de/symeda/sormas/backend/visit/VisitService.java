package de.symeda.sormas.backend.visit;

import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class VisitService extends AbstractAdoService<Visit> {
	
	public VisitService() {
		super(Visit.class);
	}
	
	public List<Visit> getAllAfter(Date date, User user) {

		// TODO get user from session?

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

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
		
		if (contact.getLastContactDate() != null) {
			Predicate dateFilter = cb.greaterThan(from.get(Visit.VISIT_DATE_TIME), contact.getLastContactDate());
			filter = cb.and(filter, dateFilter);
		}

		if (contact.getFollowUpUntil() != null) {
			Predicate dateFilter = cb.lessThan(from.get(Visit.VISIT_DATE_TIME), contact.getFollowUpUntil());
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
