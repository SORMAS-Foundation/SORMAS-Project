package de.symeda.sormas.backend.contact;

import java.time.LocalDate;
import java.util.Collections;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContact;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.symptoms.Symptoms;
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
	@EJB
	PersonFacadeEjbLocal personFacade;

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
	
	/**
	 * 
	 * @param fromDate inclusive 
	 * @param toDate exclusive
	 * @param disease optional
	 * @param user optional
	 * @return
	 */
	public List<Contact> getFollowUpBetween(@NotNull Date fromDate, @NotNull Date toDate, District district, Disease disease, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		
		Predicate filter = cb.isNotNull(from.get(Contact.FOLLOW_UP_UNTIL));
		filter = cb.and(filter, cb.greaterThanOrEqualTo(from.get(Contact.FOLLOW_UP_UNTIL), fromDate));
		filter = cb.and(filter, cb.lessThan(from.get(Contact.LAST_CONTACT_DATE), toDate));

		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from, user);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		} 	
		
		if (district != null) {
			Join<Contact, Case> caze = from.join(Contact.CAZE);
			filter = cb.and(filter, cb.equal(caze.get(Case.DISTRICT), district));
		}
		
		if (disease != null) {
			Join<Contact, Case> caze = from.join(Contact.CAZE);
			filter = cb.and(filter, cb.equal(caze.get(Case.DISEASE), disease));
		}
		
		cq.where(filter);
		
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
	
	public List<MapContact> getContactsForMap(District district, Disease disease, Date fromDate, Date toDate, User user, List<Case> cases) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapContact> cq = cb.createQuery(MapContact.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Person> person = contact.join(Contact.PERSON, JoinType.LEFT);
		Join<Person, Location> contactPersonAddress = person.join(Person.ADDRESS, JoinType.LEFT);
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);
		
		Predicate filter = createUserFilter(cb, cq, contact, user);
		
		if (cases != null && !cases.isEmpty()) {
			// Only return contacts whose cases are displayed
			Path<Object> contactCase = contact.get(Contact.CAZE);
			Predicate caseFilter = contactCase.in(cases);
			if (filter != null) {
				filter = cb.and(filter, caseFilter);
			} else {
				filter = caseFilter;
			}
		}
		
		if (district != null) {
			Predicate districtFilter = cb.equal(caze.get(Case.DISTRICT), district);
			if (filter != null) {
				filter = cb.and(filter, districtFilter);
			} else {
				filter = districtFilter;
			}
		}
		
		if (disease != null) {
			Predicate diseaseFilter = cb.equal(caze.get(Case.DISEASE), disease);
			if (filter != null) {
				filter = cb.and(filter, diseaseFilter);
			} else {
				filter = diseaseFilter;
			}
		}	
		
		// Only retrieve contacts that are currently under follow-up
		Predicate followUpFilter = cb.equal(contact.get(Contact.FOLLOW_UP_STATUS), FollowUpStatus.FOLLOW_UP);
		if (filter != null) {
			filter = cb.and(filter, followUpFilter);
		} else {
			filter = followUpFilter;
		}
		
		List<MapContact> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(
					contact.get(Contact.UUID),
					contact.get(Contact.CONTACT_CLASSIFICATION),
					contact.get(Contact.REPORT_LAT),
					contact.get(Contact.REPORT_LON),
					contactPersonAddress.get(Location.LATITUDE),
					contactPersonAddress.get(Location.LONGITUDE),
					symptoms.get(Symptoms.ONSET_DATE),
					caze.get(Case.REPORT_DATE),
					person.get(Person.UUID),
					casePerson.get(Person.UUID)
			);
			
			result = em.createQuery(cq).getResultList();
			for (MapContact mapContact : result) {
				Visit lastVisit = visitService.getLastVisitByContact(getByUuid(mapContact.getUuid()), VisitStatus.COOPERATIVE);
				if (lastVisit != null) {
					mapContact.setLastVisitDateTime(lastVisit.getVisitDateTime());
				}
				
				mapContact.setPerson(personFacade.getReferenceByUuid(mapContact.getPersonUuid()));
				mapContact.setCasePerson(personFacade.getReferenceByUuid(mapContact.getCasePersonUuid()));
			}
		} else {
			result = Collections.emptyList();
		}
		
		return result;
	}

	public int getFollowUpDuration(Case caze) {
		Disease disease = caze.getDisease();
		if (disease == Disease.PLAGUE) {
			if (caze.getPlagueType() == PlagueType.PNEUMONIC) {
				return 7;
			} else {
				return 0;
			}
		} else {
			switch (disease) {
			case EVD:
			case MONKEYPOX:
			case OTHER:
				return 21;
			case AVIAN_INFLUENCA:
				return 17;
			case LASSA:
				return 6;
			default:
				return 0;
			}
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
		int followUpDuration = getFollowUpDuration(contact.getCaze());
		boolean changeStatus = contact.getFollowUpStatus() != FollowUpStatus.CANCELED
				&& contact.getFollowUpStatus() != FollowUpStatus.LOST;

		if (followUpDuration == 0) {
			contact.setFollowUpUntil(null);
			if (changeStatus) {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
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
			if (changeStatus) {
				// completed or still follow up?
				if (lastVisit != null && DateHelper8.toLocalDate(lastVisit.getVisitDateTime()).isEqual(untilDate)) {
					contact.setFollowUpStatus(FollowUpStatus.COMPLETED);
				} else {
					contact.setFollowUpStatus(FollowUpStatus.FOLLOW_UP);	
				}
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
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Contact, Contact> contactPath, User user) {
				
		Predicate userFilter = caseService.createUserFilter(cb, cq, contactPath.join(Contact.CAZE, JoinType.LEFT), user);
		Predicate filter;
		if (userFilter != null) {
			filter = cb.or(createUserFilterWithoutCase(cb, cq, contactPath, user), userFilter);
		} else {
			filter = createUserFilterWithoutCase(cb, cq, contactPath, user);
		}
		return filter;
	}
	
	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq, From<Contact, Contact> contactPath, User user) {
		// National users can access all contacts in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)) {
			return null;
		}
		
		// whoever created it or is assigned to it is allowed to access it
		Predicate filter = cb.equal(contactPath.get(Contact.REPORTING_USER), user);
		filter = cb.or(filter, cb.equal(contactPath.get(Contact.CONTACT_OFFICER), user));
		return filter;
	}
}
