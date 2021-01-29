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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class VisitService extends BaseAdoService<Visit> {

	@EJB
	private ContactService contactService;
	@EJB
	private CaseService caseService;

	public VisitService() {
		super(Visit.class);
	}

	public List<String> getAllActiveUuids(User user) {
		Set<String> resultSet = new HashSet<>();
		resultSet.addAll(getAllActiveInContactsUuids());
		resultSet.addAll(getAllActiveInCasesUuids());
		return new ArrayList<>(resultSet);
	}

	private List<String> getAllActiveInContactsUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Contact> contactRoot = visitsQuery.from(Contact.class);
		Join<Contact, Visit> visitJoin = contactRoot.join(Contact.VISITS, JoinType.LEFT);

		visitsQuery.where(CriteriaBuilderHelper.
			and(
				cb,
				contactService.createUserFilter(cb, visitsQuery, contactRoot),
				contactService.createActiveContactsFilter(cb, contactRoot),
				cb.isNotEmpty(contactRoot.get(Contact.VISITS))));
		visitsQuery.select(visitJoin.get(Visit.UUID));
		visitsQuery.distinct(true);

		return em.createQuery(visitsQuery).getResultList();
	}

	private List<String> getAllActiveInCasesUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Case> caseRoot = visitsQuery.from(Case.class);
		Join<Case, Visit> visitJoin = caseRoot.join(Case.VISITS, JoinType.INNER);

		visitsQuery.where(
			CriteriaBuilderHelper.and(
				cb,
				caseService.createUserFilter(cb, visitsQuery, caseRoot),
				caseService.createActiveCasesFilter(cb, caseRoot),
				cb.isNotEmpty(caseRoot.get(Case.VISITS))));
		visitsQuery.select(visitJoin.get(Visit.UUID));
		visitsQuery.distinct(true);

		return em.createQuery(visitsQuery).getResultList();
	}

	/**
	 * Attention: For now this only returns the visits of contacts, since case visits are not yet implemented in the mobile app
	 */
	public List<Visit> getAllActiveVisitsAfter(Date date) {
		List<Visit> result = new ArrayList<>();
		result.addAll(getAllActiveVisitsInContactsAfter(date));
		// include when case visits are implemented for the mobile app
//		result.addAll(getAllActiveVisitsInCasesAfter(date));

		return result.stream().distinct().sorted(Comparator.comparing(AbstractDomainObject::getId)).collect(Collectors.toList());
	}

	private List<Visit> getAllActiveVisitsInContactsAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> visitsQuery = cb.createQuery(Visit.class);
		Root<Contact> contactRoot = visitsQuery.from(Contact.class);
		Join<Contact, Visit> visitJoin = contactRoot.join(Contact.VISITS, JoinType.LEFT);
		visitJoin.fetch(Visit.SYMPTOMS);
		Fetch<Visit, Person> personFetch = visitJoin.fetch(Visit.PERSON);
		personFetch.fetch(Person.ADDRESS);

		Predicate filter =
				CriteriaBuilderHelper.and(cb, contactService.createUserFilter(cb, visitsQuery, contactRoot), contactService.createActiveContactsFilter(cb, contactRoot));

		if (date != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createChangeDateFilter(cb, visitJoin, DateHelper.toTimestampUpper(date)));
		}

		visitsQuery.select(visitJoin);
		visitsQuery.where(filter);
		visitsQuery.distinct(true);
		visitsQuery.orderBy(cb.asc(visitJoin.get(AbstractDomainObject.ID)));

		return em.createQuery(visitsQuery).getResultList();
	}

	private List<Visit> getAllActiveVisitsInCasesAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> visitsQuery = cb.createQuery(Visit.class);
		Root<Case> caseRoot = visitsQuery.from(Case.class);
		Join<Case, Visit> visitJoin = caseRoot.join(Case.VISITS, JoinType.LEFT);
		visitJoin.fetch(Visit.SYMPTOMS);
		Fetch<Visit, Person> personFetch = visitJoin.fetch(Visit.PERSON);
		personFetch.fetch(Person.ADDRESS);

		Predicate filter = CriteriaBuilderHelper.and(cb, caseService.createUserFilter(cb, visitsQuery, caseRoot), caseService.createActiveCasesFilter(cb, caseRoot));

		if (date != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createChangeDateFilter(cb, visitJoin, DateHelper.toTimestampUpper(date)));
		}

		visitsQuery.select(visitJoin);
		visitsQuery.where(filter);
		visitsQuery.distinct(true);
		visitsQuery.orderBy(cb.asc(visitJoin.get(AbstractDomainObject.ID)));

		return em.createQuery(visitsQuery).getResultList();
	}

	// Used only for testing; directly retrieve the visits from the contact instead
	public List<Visit> getAllByContact(Contact contact) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(Visit.class);
		Root<Contact> contactRoot = cq.from(Contact.class);

		cq.where(cb.equal(contactRoot.get(Contact.ID), contact.getId()));
		cq.select(contactRoot.get(Contact.VISITS));

		return em.createQuery(cq).getResultList();
	}

	public Set<Visit> getAllRelevantVisits(Person person, Disease disease, Date startDate, Date endDate) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Visit> cq = cb.createQuery(Visit.class);
		Root<Visit> visitRoot = cq.from(Visit.class);

		cq.where(buildRelevantVisitsFilter(person, disease, startDate, endDate, cb, visitRoot));

		return new HashSet<>(em.createQuery(cq).getResultList());
	}

	/**
	 * Returns a filter that can be used to retrieve all visits with the specified person and disease
	 * whose visit date is after the start date and before the end date.
	 */
	public Predicate buildRelevantVisitsFilter(Person person, Disease disease, Date startDate, Date endDate, CriteriaBuilder cb, Root<Visit> from) {

		startDate = DateHelper.getStartOfDay(startDate);
		endDate = DateHelper.getEndOfDay(endDate);

		Predicate filter = cb.and(cb.equal(from.get(Visit.PERSON), person), cb.equal(from.get(Visit.DISEASE), disease));

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			cb.greaterThanOrEqualTo(from.get(Visit.VISIT_DATE_TIME), DateHelper.subtractDays(startDate, FollowUpLogic.ALLOWED_DATE_OFFSET)),
			cb.lessThanOrEqualTo(from.get(Visit.VISIT_DATE_TIME), DateHelper.addDays(endDate, FollowUpLogic.ALLOWED_DATE_OFFSET)));

		return filter;
	}

	public Predicate buildCriteriaFilter(VisitCriteria criteria, CriteriaBuilder cb, Root<Visit> from) {

		Predicate filter = null;
		if (criteria.getContact() != null) {
			Contact contact = contactService.getByUuid(criteria.getContact().getUuid());
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				buildRelevantVisitsFilter(
					contact.getPerson(),
					contact.getDisease(),
					ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime()),
					ContactLogic.getEndDate(contact.getLastContactDate(), contact.getReportDateTime(), contact.getFollowUpUntil()),
					cb,
					from));
		}
		if (criteria.getCaze() != null) {
			Case caze = caseService.getByUuid(criteria.getCaze().getUuid());
			filter = CriteriaBuilderHelper.and(
				cb,
				filter,
				buildRelevantVisitsFilter(
					caze.getPerson(),
					caze.getDisease(),
					CaseLogic.getStartDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate()),
					CaseLogic.getEndDate(caze.getSymptoms().getOnsetDate(), caze.getReportDate(), caze.getFollowUpUntil()),
					cb,
					from));
		}

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Visit> visitPath, Timestamp date) {

		Predicate dateFilter = cb.greaterThan(visitPath.get(Visit.CHANGE_DATE), date);

		Join<Visit, Symptoms> symptoms = visitPath.join(Visit.SYMPTOMS, JoinType.LEFT);
		dateFilter = cb.or(dateFilter, cb.greaterThan(symptoms.get(AbstractDomainObject.CHANGE_DATE), date));

		return dateFilter;
	}
}
