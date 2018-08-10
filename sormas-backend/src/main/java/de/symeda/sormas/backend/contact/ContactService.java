package de.symeda.sormas.backend.contact;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.ContactClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactStatus;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb.PersonFacadeEjbLocal;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
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

	public List<Contact> findBy(ContactCriteria contactCriteria, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		Predicate filter = buildCriteriaFilter(contactCriteria, cb, from);
		filter = and(cb, filter, createUserFilter(cb, cq, from, user));

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.asc(from.get(Contact.CREATION_DATE)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Contact> getAllByCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		cq.where(cb.equal(from.get(Contact.CAZE), caze));
		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<Contact> getAllByResultingCase(Case caze) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());

		cq.where(cb.equal(from.get(Contact.RESULTING_CASE), caze));
		cq.orderBy(cb.desc(from.get(Contact.REPORT_DATE_TIME)));

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public boolean hadContactWithConfirmedCase(Case resultingCase) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Contact> from = cq.from(getElementClass());
		Join<Object, Object> cazeJoin = from.join(Contact.CAZE);

		cq.where(cb.and(cb.equal(from.get(Contact.RESULTING_CASE), resultingCase),
				cb.equal(cazeJoin.get(Case.CASE_CLASSIFICATION), CaseClassification.CONFIRMED)));

		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	/**
	 * 
	 * @param fromDate inclusive
	 * @param toDate   exclusive
	 * @param disease  optional
	 * @param user     optional
	 * @return
	 */
	public List<Contact> getFollowUpBetween(@NotNull Date fromDate, @NotNull Date toDate, District district,
			Disease disease, User user) {
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

	public List<MapContactDto> getContactsForMap(Region region, District district, Disease disease, Date fromDate,
			Date toDate, User user, List<String> caseUuids) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MapContactDto> cq = cb.createQuery(MapContactDto.class);
		Root<Contact> contact = cq.from(getElementClass());
		Join<Contact, Person> person = contact.join(Contact.PERSON, JoinType.LEFT);
		Join<Person, Location> contactPersonAddress = person.join(Person.ADDRESS, JoinType.LEFT);
		Join<Contact, Case> caze = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Case, Person> casePerson = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Symptoms> symptoms = caze.join(Case.SYMPTOMS, JoinType.LEFT);

		Predicate filter = createUserFilter(cb, cq, contact, user);

		if (caseUuids != null && !caseUuids.isEmpty()) {
			// Only return contacts whose cases are displayed
			Path<Object> contactCaseUuid = contact.get(Contact.CAZE).get(Case.UUID);
			Predicate caseFilter = contactCaseUuid.in(caseUuids);
			if (filter != null) {
				filter = cb.and(filter, caseFilter);
			} else {
				filter = caseFilter;
			}
		} else {
			// If no cases are shown, there should also be no contacts shown
			return Collections.emptyList();
		}

		if (region != null) {
			Predicate regionFilter = cb.equal(caze.get(Case.REGION), region);
			if (filter != null) {
				filter = cb.and(filter, regionFilter);
			} else {
				filter = regionFilter;
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

		List<MapContactDto> result;
		if (filter != null) {
			cq.where(filter);
			cq.multiselect(contact.get(Contact.UUID), contact.get(Contact.CONTACT_CLASSIFICATION),
					contact.get(Contact.REPORT_LAT), contact.get(Contact.REPORT_LON),
					contactPersonAddress.get(Location.LATITUDE), contactPersonAddress.get(Location.LONGITUDE),
					symptoms.get(Symptoms.ONSET_DATE), caze.get(Case.REPORT_DATE), person.get(Person.FIRST_NAME),
					person.get(Person.LAST_NAME), casePerson.get(Person.FIRST_NAME), casePerson.get(Person.LAST_NAME));

			result = em.createQuery(cq).getResultList();
			for (MapContactDto mapContactDto : result) {
				Visit lastVisit = visitService.getLastVisitByContact(getByUuid(mapContactDto.getUuid()),
						VisitStatus.COOPERATIVE);
				if (lastVisit != null) {
					mapContactDto.setLastVisitDateTime(lastVisit.getVisitDateTime());
				}
			}
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * Calculates resultingCase and contact status based on: - existing disease
	 * cases (and classification) of the person - the incubation period and - the
	 * contact classification - the follow-up status
	 */
	public void udpateContactStatusAndResultingCase(Contact contact) {

		ContactClassification contactClassification = contact.getContactClassification();
		if (contactClassification == null) { // fallback
			contactClassification = ContactClassification.UNCONFIRMED;
		}

		switch (contactClassification) {
		case UNCONFIRMED:
			contact.setContactStatus(ContactStatus.ACTIVE);
			contact.setResultingCase(null);
			contact.setResultingCaseUser(null);
			break;
		case NO_CONTACT:
			contact.setContactStatus(ContactStatus.DROPPED);
			contact.setResultingCase(null);
			contact.setResultingCaseUser(null);
			break;
		case CONFIRMED:

			// calculate the incubation period relative to the contact
			// make sure to get the maximum time span based on report date time and last
			// contact date
			Date incubationPeriodStart = contact.getReportDateTime();
			Date incubationPeriodEnd = contact.getReportDateTime();
			if (contact.getLastContactDate() != null) {
				if (contact.getLastContactDate().before(incubationPeriodStart)) { // whatever is earlier
					incubationPeriodStart = contact.getLastContactDate();
				}
				if (contact.getLastContactDate().after(incubationPeriodEnd)) { // whatever is later
					incubationPeriodEnd = contact.getLastContactDate();
				}
			}
			incubationPeriodEnd = DateHelper.addDays(incubationPeriodEnd, DiseaseHelper
					.getIncubationPeriodDays(contact.getCaze().getDisease(), contact.getCaze().getPlagueType()));

			// see if any case was reported or has symptom onset within the period
			Case resultingCase = caseService.getFirstByPersonDiseaseAndOnset(contact.getCaze().getDisease(),
					contact.getPerson(), incubationPeriodStart, incubationPeriodEnd);

			// set
			if (resultingCase != null) {
				contact.setContactStatus(ContactStatus.CONVERTED);
				contact.setResultingCase(resultingCase);
				contact.setResultingCaseUser(null);
			} else {
				FollowUpStatus followUpStatus = contact.getFollowUpStatus();
				if (followUpStatus != null) {
					switch (followUpStatus) {
					case CANCELED:
					case COMPLETED:
					case LOST:
					case NO_FOLLOW_UP:
						contact.setContactStatus(ContactStatus.DROPPED);
						break;
					case FOLLOW_UP:
						contact.setContactStatus(ContactStatus.ACTIVE);
						break;
					default:
						throw new NoSuchElementException(followUpStatus.toString());
					}
				} else {
					contact.setContactStatus(ContactStatus.ACTIVE);
				}
				contact.setResultingCase(null);
				contact.setResultingCaseUser(null);
			}

			break;

		default:
			throw new NoSuchElementException(DataHelper.toStringNullable(contactClassification));
		}

		ensurePersisted(contact);
	}

	/**
	 * Calculates and sets the follow-up until date and status of the contact.
	 * <ul>
	 * <li>Disease with no follow-up: Leave empty and set follow-up status to "No
	 * follow-up"</li>
	 * <li>Others: Use follow-up duration of the disease. Reference for calculation
	 * is the reporting date (since this is always later than the last contact date
	 * and we can't be sure the last contact date is correct) If the last visit was
	 * not cooperative and happened at the last date of contact tracing, we need to
	 * do an additional visit.</li>
	 * </ul>
	 */
	public void updateFollowUpUntilAndStatus(Contact contact) {

		Disease disease = contact.getCaze().getDisease();
		boolean changeStatus = contact.getFollowUpStatus() != FollowUpStatus.CANCELED
				&& contact.getFollowUpStatus() != FollowUpStatus.LOST;

		if (!DiseaseHelper.hasContactFollowUp(disease, contact.getCaze().getPlagueType())) {
			contact.setFollowUpUntil(null);
			if (changeStatus) {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
		} else {

			int followUpDuration = DiseaseHelper.getIncubationPeriodDays(disease, contact.getCaze().getPlagueType());
			LocalDate beginDate = DateHelper8.toLocalDate(contact.getReportDateTime());
			LocalDate untilDate = beginDate.plusDays(followUpDuration);

			Visit lastVisit = null;
			boolean additionalVisitNeeded;
			do {
				additionalVisitNeeded = false;
				lastVisit = visitService.getLastVisitByPerson(contact.getPerson(), disease, untilDate);
				if (lastVisit != null) {
					// if the last visit was not cooperative and happened at the last date of
					// contact tracing ..
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
	 * Should be used whenever a new visit is created or the status or date of a
	 * visit changes. Makes sure the follow-up until date of all related contacts is
	 * updated.
	 */
	public void updateFollowUpUntilAndStatusByVisit(Visit visit) {

		List<Contact> contacts = getByPersonAndDisease(visit.getPerson(), visit.getDisease());
		for (Contact contact : contacts) {
			updateFollowUpUntilAndStatus(contact);
		}
	}

	public List<Contact> getAllByVisit(Visit visit) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Contact> cq = cb.createQuery(getElementClass());
		Root<Contact> from = cq.from(getElementClass());
		Join<Contact, Case> caze = from.join(Contact.CAZE, JoinType.LEFT);

		Predicate filter = cb.equal(from.get(Contact.PERSON), visit.getPerson());
		filter = cb.and(filter, cb.equal(caze.get(Case.DISEASE), visit.getDisease()));

		Predicate dateStartFilter = cb.or(
				cb.and(cb.isNotNull(from.get(Contact.LAST_CONTACT_DATE)),
						cb.lessThan(from.get(Contact.LAST_CONTACT_DATE),
								DateHelper.addDays(visit.getVisitDateTime(), VisitDto.ALLOWED_CONTACT_DATE_OFFSET))),
				cb.lessThan(from.get(Contact.REPORT_DATE_TIME),
						DateHelper.addDays(visit.getVisitDateTime(), VisitDto.ALLOWED_CONTACT_DATE_OFFSET)));

		Predicate dateEndFilter = cb.greaterThan(from.get(Contact.FOLLOW_UP_UNTIL),
				DateHelper.subtractDays(visit.getVisitDateTime(), VisitDto.ALLOWED_CONTACT_DATE_OFFSET));

		filter = cb.and(filter, dateStartFilter);
		filter = cb.and(filter, dateEndFilter);

		cq.where(filter);

		List<Contact> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Contact, Contact> contactPath,
			User user) {

		Predicate userFilter = caseService.createUserFilter(cb, cq, contactPath.join(Contact.CAZE, JoinType.LEFT),
				user);
		Predicate filter;
		if (userFilter != null) {
			filter = cb.or(createUserFilterWithoutCase(cb, cq, contactPath, user), userFilter);
		} else {
			filter = createUserFilterWithoutCase(cb, cq, contactPath, user);
		}
		return filter;
	}

	public Predicate createUserFilterWithoutCase(CriteriaBuilder cb, CriteriaQuery cq,
			From<Contact, Contact> contactPath, User user) {
		// National users can access all contacts in the system
		if (user.getUserRoles().contains(UserRole.NATIONAL_USER)
				|| user.getUserRoles().contains(UserRole.NATIONAL_OBSERVER)) {
			return null;
		}

		// whoever created it or is assigned to it is allowed to access it
		Predicate filter = cb.equal(contactPath.join(Contact.REPORTING_USER, JoinType.LEFT), user);
		filter = cb.or(filter, cb.equal(contactPath.join(Contact.CONTACT_OFFICER, JoinType.LEFT), user));
		return filter;
	}

	public Predicate buildCriteriaFilter(ContactCriteria contactCriteria, CriteriaBuilder cb, Root<Contact> from) {
		Predicate filter = null;
		Join<Contact, Case> caze = from.join(Contact.CAZE, JoinType.LEFT);

		if (contactCriteria.getReportingUserRole() != null) {
			filter = and(cb, filter, cb.isMember(contactCriteria.getReportingUserRole(),
					from.join(Contact.REPORTING_USER, JoinType.LEFT).get(User.USER_ROLES)));
		}
		if (contactCriteria.getCaseDisease() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.DISEASE), contactCriteria.getCaseDisease()));
		}
		if (contactCriteria.getCaze() != null) {
			filter = and(cb, filter, cb.equal(caze.get(Case.UUID), contactCriteria.getCaze().getUuid()));
		}
		if (contactCriteria.getCaseRegion() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.REGION, JoinType.LEFT).get(Region.UUID),
					contactCriteria.getCaseRegion().getUuid()));
		}
		if (contactCriteria.getCaseDistrict() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.DISTRICT, JoinType.LEFT).get(District.UUID),
					contactCriteria.getCaseDistrict().getUuid()));
		}
		if (contactCriteria.getCaseFacility() != null) {
			filter = and(cb, filter, cb.equal(caze.join(Case.HEALTH_FACILITY, JoinType.LEFT).get(Facility.UUID),
					contactCriteria.getCaseFacility().getUuid()));
		}
		if (contactCriteria.getContactOfficer() != null) {
			filter = and(cb, filter, cb.equal(from.join(Contact.CONTACT_OFFICER, JoinType.LEFT).get(User.UUID),
					contactCriteria.getContactOfficer().getUuid()));
		}
		if (contactCriteria.getContactClassification() != null) {
			filter = and(cb, filter,
					cb.equal(from.get(Contact.CONTACT_CLASSIFICATION), contactCriteria.getContactClassification()));
		}
		if (contactCriteria.getContactStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Contact.CONTACT_STATUS), contactCriteria.getContactStatus()));
		}
		if (contactCriteria.getFollowUpStatus() != null) {
			filter = and(cb, filter, cb.equal(from.get(Contact.FOLLOW_UP_STATUS), contactCriteria.getFollowUpStatus()));
		}

		return filter;
	}
}
