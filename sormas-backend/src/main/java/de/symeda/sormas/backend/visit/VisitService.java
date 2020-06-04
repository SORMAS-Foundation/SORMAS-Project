/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
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
import de.symeda.sormas.api.visit.DashboardVisitDto;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
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

	public List<String> getAllActiveUuids(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Visit> visitRoot = visitsQuery.from(Visit.class);
		visitsQuery.select(visitRoot.get(Visit.UUID));

		// get all visits of the user's contact's persons
		Subquery<Long> contactPersonSubquery = visitsQuery.subquery(Long.class);
		Root<Contact> contactRoot = contactPersonSubquery.from(Contact.class);
		Join<Contact, Case> contactCase = contactRoot.join(Contact.CAZE, JoinType.LEFT);
		Predicate userFilter = contactService.createUserFilter(cb, visitsQuery, contactRoot);
		Predicate contactPersonPredicate = createContactPersonPredicate(cb, contactCase, userFilter);
		contactPersonSubquery.where(contactPersonPredicate);
		contactPersonSubquery.select(contactRoot.get(Contact.PERSON).get(Person.ID));

		Predicate filter = cb.in(visitRoot.get(Visit.PERSON).get(Person.ID)).value(contactPersonSubquery);
		visitsQuery.where(filter);
		visitsQuery.distinct(true);

		List<String> resultList = em.createQuery(visitsQuery).getResultList();
		return resultList;	
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	public List<Visit> getAllActiveVisitsAfter(Date date) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> visitRoot = cq.from(Visit.class);

		// get all visits of the user's contact's persons
		Subquery<Integer> contactPersonSubquery = cq.subquery(Integer.class);
		Root<Contact> contactRoot = contactPersonSubquery.from(Contact.class);
		Join<Contact, Case> contactCase = contactRoot.join(Contact.CAZE, JoinType.LEFT);
		Predicate userFilter = contactService.createUserFilter(cb, cq, contactRoot);
		Predicate contactPersonPredicate = createContactPersonPredicate(cb, contactCase, userFilter);
		contactPersonSubquery.where(contactPersonPredicate);
		contactPersonSubquery.select(contactRoot.get(Contact.PERSON).get(Person.ID));
		Predicate filter = cb.in(visitRoot.get(Visit.PERSON).get(Person.ID)).value(contactPersonSubquery);
		// date range
		if (date != null) {
			filter = cb.and(filter, createChangeDateFilter(cb, visitRoot, DateHelper.toTimestampUpper(date)));
		}
		cq.where(filter);
		cq.distinct(true);
		cq.orderBy(cb.asc(visitRoot.get(AbstractDomainObject.ID)));

		List<Visit> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	private Predicate createContactPersonPredicate(CriteriaBuilder cb, Join<Contact, Case> contactCase, Predicate userFilter) {
		Predicate contactCaseNotArchived = cb.or(cb.equal(contactCase.get(Case.ARCHIVED), false),
				cb.isNull(contactCase.get(Case.ARCHIVED)));
		return userFilter != null ? cb.and(userFilter,
				contactCaseNotArchived) : contactCaseNotArchived;
	}

	/**
	 * All visits of the contact person with the same disease and within lastContactDate and followUpUntil
	 */
	public List<Visit> getAllByContact(Contact contact) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		Predicate filter = buildVisitFilter(contact, null, cb, from);

		cq.where(filter);
		cq.orderBy(cb.asc(from.get(Visit.VISIT_DATE_TIME)));

		List<Visit> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public List<DashboardVisitDto> getDashboardVisitsByContact(Contact contact, Date fromDate, Date toDate) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DashboardVisitDto> cq = cb.createQuery(DashboardVisitDto.class);
		Root<Visit> from = cq.from(getElementClass());

		Predicate filter = buildVisitFilter(contact, null, cb, from);
		if (from != null) {
			filter = cb.and(filter, cb.or(cb.greaterThan(from.get(Visit.VISIT_DATE_TIME), fromDate), cb.equal(from.get(Visit.VISIT_DATE_TIME), fromDate)));
		}
		if (toDate != null) {
			filter = cb.and(filter, cb.or(cb.lessThan(from.get(Visit.VISIT_DATE_TIME), toDate), cb.equal(from.get(Visit.VISIT_DATE_TIME), toDate)));
		}

		cq.where(filter);
		cq.multiselect(
				from.get(Visit.VISIT_STATUS));
		List<DashboardVisitDto> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	public int getVisitCount(Contact contact, VisitStatus visitStatus) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> from = cq.from(getElementClass());

		Predicate filter = buildVisitFilter(contact, visitStatus, cb, from);

		cq.select(cb.count(from));
		cq.where(filter);

		return em.createQuery(cq).getSingleResult().intValue();
	}

	public int getVisitCountByContactId(long contactPersonId, Date lastContactDate, Date contactReportDate, Date followUpUntil, Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> from = cq.from(Visit.class);

		Predicate filter = buildVisitFilter(contactPersonId, disease, null, lastContactDate, contactReportDate, followUpUntil, cb, from);

		cq.where(filter);

		cq.select(cb.count(from));
		
		return em.createQuery(cq).getSingleResult().intValue();
	}

	public int getSymptomaticCountByContact(Contact contact) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Visit> from = cq.from(getElementClass());
		Join<Visit, Symptoms> symptoms = from.join(Visit.SYMPTOMS, JoinType.LEFT);

		Predicate filter = buildVisitFilter(contact, null, cb, from);
		filter = cb.and(filter, cb.equal(symptoms.get(Symptoms.SYMPTOMATIC), true));

		cq.select(cb.count(from));
		cq.where(filter);

		return em.createQuery(cq).getSingleResult().intValue();
	}

	public Visit getLastVisitByContact(Contact contact, VisitStatus visitStatus) {
		return getLastVisitByContactId(contact.getPerson().getId(), contact.getLastContactDate(),  contact.getReportDateTime(), contact.getFollowUpUntil(), contact.getDisease(), visitStatus);
	}

	public Visit getLastVisitByContactId(long contactPersonId, Date lastContactDate, Date contactReportDate, Date followUpUntil, Disease disease, VisitStatus visitStatus) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(getElementClass());
		Root<Visit> from = cq.from(getElementClass());

		Predicate filter = buildVisitFilter(contactPersonId, disease, visitStatus, lastContactDate, contactReportDate, followUpUntil, cb, from);

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Visit.VISIT_DATE_TIME)));

		TypedQuery<Visit> query = em.createQuery(cq);
		query.setFirstResult(0);
		query.setMaxResults(1);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
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

		List<Visit> results = em.createQuery(cq).getResultList();
		return results.size()>0? results.get(0): null;
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

	/**
	 * The logic to calculate the listed visits needs to match the ContactService.getAllByVisit method.
	 */
	private Predicate buildVisitFilter(Contact contact, VisitStatus visitStatus, CriteriaBuilder cb, Root<?> from) {
		return buildVisitFilter(contact.getPerson().getId(), contact.getDisease(), visitStatus, contact.getLastContactDate(), contact.getReportDateTime(), contact.getFollowUpUntil(), cb, from);
	}
	
	/**
	 * The logic to calculate the listed visits needs to match the ContactService.getAllByVisit method.
	 */
	private Predicate buildVisitFilter(Long personId, Disease cazeDisease, VisitStatus visitStatus, Date lastContactDate, Date contactReportDate, Date followUpUntil, CriteriaBuilder cb, Root<?> from) {
		// all of the person
		Predicate filter = cb.equal(from.get(Visit.PERSON).get(Person.ID), personId);

		// only disease relevant
		filter = cb.and(filter, cb.equal(from.get(Visit.DISEASE), cazeDisease));

		// only visits with the given visit status, if present
		if (visitStatus != null) {
			filter = cb.and(filter, cb.equal(from.get(Visit.VISIT_STATUS), visitStatus));
		}

		// list all visits between contact date ...
		// IMPORTANT: This is different than the calculation of "follow-up until", where the date of report is used as reference
		// We also want to have visits that took place before.
		Date contactReferenceDate = lastContactDate != null ? lastContactDate : contactReportDate;
		Predicate dateStartFilter = cb.greaterThan(from.get(Visit.VISIT_DATE_TIME), DateHelper.subtractDays(contactReferenceDate, VisitDto.ALLOWED_CONTACT_DATE_OFFSET));
		filter = cb.and(filter, dateStartFilter);

		// .. and follow-up until
		if (followUpUntil != null) {
			Predicate dateFilter = cb.lessThan(from.get(Visit.VISIT_DATE_TIME), DateHelper.addDays(followUpUntil, VisitDto.ALLOWED_CONTACT_DATE_OFFSET));
			filter = cb.and(filter, dateFilter);
		}

		return filter;
	}

//	/**
//	 * The logic to calculate the visits needs to match the buildVisitFilter method; this method returns part of a native
//	 * query for usage in performance-critical situations.
//	 */
//	private String buildVisitFilterQuery(long contactId, long contactPersonId, Date lastContactDate, Date contactReportDate, Date followUpUntil, Disease disease, VisitStatus visitStatus) {
//		StringBuilder builder = new StringBuilder();
//		builder.append(Visit.PERSON + "_id = " + contactPersonId).append(" AND " + Visit.DISEASE + " = '" + disease.getName() + "'");
//		if (visitStatus != null) {
//			builder.append(" AND " + Visit.VISIT_STATUS + " = '" + visitStatus.getName() + "'");
//		}
//		Date contactReferenceDate = lastContactDate != null ? lastContactDate : contactReportDate;
//		Date earliestDate = DateHelper.subtractDays(contactReferenceDate, VisitDto.ALLOWED_CONTACT_DATE_OFFSET);
//		Date latestDate = DateHelper.addDays(followUpUntil, VisitDto.ALLOWED_CONTACT_DATE_OFFSET);
//		builder.append(" AND " + Visit.VISIT_DATE_TIME + " > '" + new Timestamp(earliestDate.getTime()) + "'");
//		if (followUpUntil != null) {
//			builder.append(" AND " + Visit.VISIT_DATE_TIME + " < '" + new Timestamp(latestDate.getTime()) + "'");
//		}
//		return builder.toString();
//	}
	
	public Predicate buildCriteriaFilter(VisitCriteria criteria, CriteriaBuilder cb, Root<Visit> from) {
		Predicate filter = null;
		if (criteria.getContact() != null) {
			Predicate visitFilter = buildVisitFilter(contactService.getByReferenceDto(criteria.getContact()), null, cb, from);
			filter = and(cb, filter, visitFilter);
		}

		return filter;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<Visit, Visit> from) {
		// getAllUuids and getAllAfter have custom implementations
		throw new UnsupportedOperationException();
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<Visit,Visit> visitPath, Timestamp date) {
		Predicate dateFilter = cb.greaterThan(visitPath.get(Visit.CHANGE_DATE), date);

		Join<Visit, Symptoms> symptoms = visitPath.join(Visit.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}
	
}
