/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityGraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.RequestContextHolder;
import de.symeda.sormas.api.caze.CaseLogic;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.followup.FollowUpLogic;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitCriteria;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class VisitService extends AdoServiceWithUserFilterAndJurisdiction<Visit> {

	@EJB
	private ContactService contactService;
	@EJB
	private CaseService caseService;

	public VisitService() {
		super(Visit.class);
	}

	@Override
	public List<Long> getInJurisdictionIds(List<Visit> entities) {
		return getIdList(entities, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	@Override
	public boolean inJurisdictionOrOwned(Visit entity) {
		return fulfillsCondition(entity, (cb, cq, from) -> inJurisdictionOrOwned(cb, cq, from));
	}

	@SuppressWarnings("rawtypes")
	private Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery cq, From<?, Visit> from) {
		return inJurisdictionOrOwned(new VisitQueryContext(cb, cq, from));
	}

	public Predicate inJurisdictionOrOwned(VisitQueryContext queryContext) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();
		VisitJoins visitJoins = queryContext.getJoins();
		return cb.or(
			caseService.inJurisdictionOrOwned(new CaseQueryContext(cb, cq, visitJoins.getCaseJoins())),
			contactService.inJurisdictionOrOwned(new ContactQueryContext(cb, cq, visitJoins.getContactJoins())));
	}

	public List<String> getAllActiveUuids(User user) {

		if (RequestContextHolder.isMobileSync() && !user.hasUserRight(UserRight.CONTACT_VIEW)) {
			return Collections.emptyList();
		}

		Set<String> resultSet = new HashSet<>();
		resultSet.addAll(getAllActiveInContactsUuids());
		resultSet.addAll(getAllActiveInCasesUuids());
		return new ArrayList<>(resultSet);
	}

	public List<Visit> getByPersonUuids(List<String> personUuids) {

		List<Visit> visits = new LinkedList<>();
		IterableHelper.executeBatched(personUuids, ModelConstants.PARAMETER_LIMIT, batchedPersonUuids -> {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Visit> cq = cb.createQuery(Visit.class);
			Root<Visit> root = cq.from(Visit.class);
			Join<Visit, Person> personJoin = root.join(Visit.PERSON, JoinType.INNER);

			cq.where(cb.and(personJoin.get(AbstractDomainObject.UUID).in(batchedPersonUuids)));

			visits.addAll(em.createQuery(cq).getResultList());
		});
		return visits;
	}

	private List<String> getAllActiveInContactsUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> visitsQuery = cb.createQuery(String.class);
		Root<Contact> contactRoot = visitsQuery.from(Contact.class);
		Join<Contact, Visit> visitJoin = contactRoot.join(Contact.VISITS, JoinType.LEFT);

		visitsQuery.where(
			CriteriaBuilderHelper.and(
				cb,
				contactService.createUserFilter(new ContactQueryContext(cb, visitsQuery, new ContactJoins(contactRoot))),
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
				caseService.createUserFilter(new CaseQueryContext(cb, visitsQuery, new CaseJoins(caseRoot))),
				caseService.createActiveCasesFilter(cb, caseRoot),
				cb.isNotEmpty(caseRoot.get(Case.VISITS))));
		visitsQuery.select(visitJoin.get(Visit.UUID));
		visitsQuery.distinct(true);

		return em.createQuery(visitsQuery).getResultList();
	}

	/**
	 * Attention: For now this only returns the visits of contacts, since case visits are not yet implemented in the mobile app
	 */
	public List<Visit> getAllAfter(Date since, Integer batchSize, String lastSynchronizedUuid) {

		if (!getCurrentUser().hasUserRight(UserRight.CONTACT_VIEW)) {
			return Collections.emptyList();
		}

		return getList((cb, cq, from) -> {
			Predicate filter = createRelevantDataFilter(cb, cq, from);
			if (since != null) {
				filter = CriteriaBuilderHelper.and(cb, filter, createChangeDateFilter(cb, from, since, lastSynchronizedUuid));
			}
			return filter;
		}, batchSize);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Visit> from) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Visit> from) {

		Join<Visit, Contact> contacts = from.join(Visit.CONTACTS);
		Predicate filter = CriteriaBuilderHelper.and(
			cb,
			contactService.createUserFilter(new ContactQueryContext(cb, cq, new ContactJoins(contacts))),
			contactService.createActiveContactsFilter(cb, contacts));

		// XXX Include when case visits are implemented for the mobile app (commented out with #1886)
//		Join<Visit, Case> cases = from.join(Visit.CASES);
//		filter = CriteriaBuilderHelper.or(
//			cb,
//			filter,
//			CriteriaBuilderHelper.and(
//				cb,
//				caseService.createUserFilter(new CaseQueryContext(cb, cq, new CaseJoins(cases))),
//				caseService.createActiveCasesFilter(cb, cases)));

		return filter;
	}

	@Override
	protected void fetchReferences(From<?, Visit> from) {
		from.fetch(Visit.SYMPTOMS);
		Fetch<Visit, Person> personFetch = from.fetch(Visit.PERSON);
		personFetch.fetch(Person.ADDRESS);
	}

	@Override
	protected EntityGraph<Visit> getEntityFetchGraph() {
		final EntityGraph<Visit> entityFetchGraph = super.getEntityFetchGraph();
		entityFetchGraph.addAttributeNodes(Visit.SYMPTOMS);
		entityFetchGraph.addAttributeNodes(Visit.PERSON);
		entityFetchGraph.addSubgraph(Visit.PERSON).addAttributeNodes(Person.ADDRESS);
		return entityFetchGraph;
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
	 * Should only be used with the UUIDs of persons that are not associated to any contact or case anymore
	 * in order to avoid foreign key constraint errors.
	 */
	public void deletePersonVisits(List<String> personUuids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<Visit> cd = cb.createCriteriaDelete(Visit.class);
		Root<Visit> visitRoot = cd.from(Visit.class);

		Subquery<Long> personSubquery = cd.subquery(Long.class);
		Root<Visit> subqueryRoot = personSubquery.from(Visit.class);
		Join<Visit, Person> visitPersonJoin = subqueryRoot.join(Visit.PERSON, JoinType.INNER);
		personSubquery.where(visitPersonJoin.get(AbstractDomainObject.UUID).in(personUuids));
		personSubquery.select(subqueryRoot.get(Visit.ID));

		cd.where(cb.equal(visitRoot.get(Visit.ID), personSubquery));

		em.createQuery(cd).executeUpdate();
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
		return createChangeDateFilter(cb, visitPath, date, null);
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Visit> from, Date date, String lastSynchronizedUuid) {

		Join<Visit, Symptoms> symptoms = from.join(Visit.SYMPTOMS, JoinType.LEFT);

		//@formatter:off
		ChangeDateFilterBuilder changeDateFilterBuilder = lastSynchronizedUuid == null
			? new ChangeDateFilterBuilder(cb, date)
			: new ChangeDateFilterBuilder(cb, date, from, lastSynchronizedUuid);
		//@formatter:on
		return changeDateFilterBuilder.add(from).add(symptoms).build();
	}
}
