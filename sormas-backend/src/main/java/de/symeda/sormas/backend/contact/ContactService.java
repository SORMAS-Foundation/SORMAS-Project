package de.symeda.sormas.backend.contact;

import java.time.LocalDate;
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
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless
@LocalBean
public class ContactService extends AbstractAdoService<Contact> {
	
	@EJB
	CaseService caseService;

	@EJB
	VisitService visitService;

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

		Predicate filter = createUserFilter(cb, cq, from, user);
				
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
		
		Predicate filter = createUserFilter(cb, cq, from, user);
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
	
	public List<Contact> getByPersonAndDisease(Person person, Disease disease) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		
		Predicate filter = cb.equal(from.get(Contact.PERSON), person);
		Join<Contact, Case> caze = from.join(Contact.CAZE);
		filter = cb.and(filter, cb.equal(caze.get(Case.DISEASE), disease));
		cq.where(filter);
		
		List<Contact> result = em.createQuery(cq).getResultList();
		return result;
	}	

	public int getFollowUpDuration(Disease disease) {
		switch (disease) {
		case EVD:
		case CHOLERA:
			return 21;
		case AVIAN_INFLUENCA:
			return 17;
		case LASSA:
			return 6;
		default:
			return 0;
		}
	}
	
	/**
	 * Calculates and sets the follow-up until date and status of the contact.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation is the reporting date 
	 *   (since this is always later than the last contact date and we can't be sure the last contact date is correct)
	 *   If the last visit was not cooperative and happened at the last date of contact tracing, we need to do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpUntilAndStatus(Contact contact) {
		
		Disease disease = contact.getCaze().getDisease();
		int followUpDuration = getFollowUpDuration(disease);

		if (followUpDuration == 0) {
			contact.setFollowUpUntil(null);
			contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
		} else {
			LocalDate beginDate = DateHelper8.toLocalDate(contact.getReportDateTime());
			LocalDate untilDate = beginDate.plusDays(followUpDuration);
			
			Visit lastVisit = null;
			boolean additionalVisitNeeded;
			do {
				additionalVisitNeeded = false;
				lastVisit = visitService.getLastVisitByPerson(contact.getPerson(), disease, untilDate);
				if (lastVisit != null) {
					// if the last visit was not cooperative and happened at the last date of contact tracing ..
					if (lastVisit.getVisitStatus() != VisitStatus.COOPERATIVE
					 && DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(untilDate)) {
						// .. we need to do an additional visit
						additionalVisitNeeded = true;
						untilDate = untilDate.plusDays(1);
					}
				}
			} while (additionalVisitNeeded);

			contact.setFollowUpUntil(DateHelper8.toDate(untilDate));
			// completed or still follow up?
			if (lastVisit != null && DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(untilDate)) {
				contact.setFollowUpStatus(FollowUpStatus.COMPLETED);
			} else {
				contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);	
			}
		}
		
		ensurePersisted(contact);
	}

	/**
	 * Should be used whenever a new visit is created or the status or date of a visit changes.
	 * Makes sure the follow-up until date of all related contacts is updated.
	 */
	public void updateFollowUpUntilAndStatusByVisit(Visit visit) {
		
		List<Contact> contacts = getByPersonAndDisease(visit.getPerson(), visit.getDisease());
		for (Contact contact : contacts) {
			updateFollowUpUntilAndStatus(contact);
		}
	}	


	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Contact, Contact> contactPath, User user) {
		
		Predicate userFilter = caseService.createUserFilter(cb, cq, contactPath.join(Contact.CAZE, JoinType.LEFT), user);
		Predicate filter = cb.or(createUserFilterWithoutCase(cb, cq, contactPath, user), userFilter);
		return filter;
	}
	
	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<Contact, Contact> contactPath, User user) {
		// whoever created it or is assigned to it is allowed to access it
		Predicate filter = cb.equal(contactPath.get(Contact.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(contactPath.get(Contact.CONTACT_OFFICER), user));
		return filter;
	}
}
