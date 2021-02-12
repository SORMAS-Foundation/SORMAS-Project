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
package de.symeda.sormas.backend.person;

import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.SIMILARITY_OPERATOR;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.and;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEquals;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEqualsReferenceDto;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.backend.common.CoreAdo;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.utils.CaseJoins;

@Stateless
@LocalBean
public class PersonService extends AdoServiceWithUserFilter<Person> {

	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private ConfigFacadeEjbLocal configFacade;

	public PersonService() {
		super(Person.class);
	}

	public Person createPerson() {
		return new Person();
	}

	@Override
	public List<String> getAllUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		// persons by LGA
		CriteriaQuery<String> lgaQuery = cb.createQuery(String.class);
		Root<Person> lgaRoot = lgaQuery.from(Person.class);
		Join<Person, Location> address = lgaRoot.join(Person.ADDRESS);
		lgaQuery.select(lgaRoot.get(Person.UUID));
		Predicate lgaFilter = cb.equal(address.get(Location.DISTRICT), getCurrentUser().getDistrict());
		lgaQuery.where(lgaFilter);
		List<String> lgaResultList = em.createQuery(lgaQuery).getResultList();

		// persons by case
		CriteriaQuery<String> casePersonsQuery = cb.createQuery(String.class);
		Root<Case> casePersonsRoot = casePersonsQuery.from(Case.class);
		Join<Person, Person> casePersonsSelect = casePersonsRoot.join(Case.PERSON);
		casePersonsQuery.select(casePersonsSelect.get(Person.UUID));
		Predicate casePersonsFilter = caseService.createUserFilter(cb, casePersonsQuery, casePersonsRoot);
		if (casePersonsFilter != null) {
			casePersonsQuery.where(casePersonsFilter);
		}
		casePersonsQuery.distinct(true);
		List<String> casePersonsResultList = em.createQuery(casePersonsQuery).getResultList();

		// persons by contact
		CriteriaQuery<String> contactPersonsQuery = cb.createQuery(String.class);
		Root<Contact> contactPersonsRoot = contactPersonsQuery.from(Contact.class);
		Join<Person, Person> contactPersonsSelect = contactPersonsRoot.join(Contact.PERSON);
		contactPersonsQuery.select(contactPersonsSelect.get(Person.UUID));
		Predicate contactPersonsFilter = contactService.createUserFilter(cb, contactPersonsQuery, contactPersonsRoot);
		if (contactPersonsFilter != null) {
			contactPersonsQuery.where(contactPersonsFilter);
		}
		contactPersonsQuery.distinct(true);
		List<String> contactPersonsResultList = em.createQuery(contactPersonsQuery).getResultList();

		// persons by event participant
		CriteriaQuery<String> eventPersonsQuery = cb.createQuery(String.class);
		Root<EventParticipant> eventPersonsRoot = eventPersonsQuery.from(EventParticipant.class);
		Join<Person, Person> eventPersonsSelect = eventPersonsRoot.join(EventParticipant.PERSON);
		eventPersonsQuery.select(eventPersonsSelect.get(Person.UUID));
		Predicate eventPersonsFilter = eventParticipantService.createUserFilter(cb, eventPersonsQuery, eventPersonsRoot);
		if (eventPersonsFilter != null) {
			eventPersonsQuery.where(eventPersonsFilter);
		}
		eventPersonsQuery.distinct(true);
		List<String> eventPersonsResultList = em.createQuery(eventPersonsQuery).getResultList();

		return Stream.of(lgaResultList, casePersonsResultList, contactPersonsResultList, eventPersonsResultList)
			.flatMap(List<String>::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Person> personFrom) {
		final Join<Object, Case> caseJoin = personFrom.join(Person.CASES, JoinType.LEFT);
		final Join<Object, Contact> contactJoin = personFrom.join(Person.CONTACTS, JoinType.LEFT);
		final Join<Object, EventParticipant> eventParticipantJoin = personFrom.join(Person.EVENT_PARTICIPANTS, JoinType.LEFT);
		
		final Predicate caseUserFilter = caseService.createUserFilter(cb, cq, caseJoin);
		final Predicate contactUserFilter = contactService.createUserFilter(cb, cq, contactJoin);
		final Predicate eventParticipantUserFilter = eventParticipantService.createUserFilter(cb, cq, eventParticipantJoin);

		final Predicate caseNotDeleted = caseService.createDefaultFilter(cb, caseJoin);
		final Predicate contactNotDeleted = contactService.createDefaultFilter(cb, contactJoin);
		final Predicate eventParticipantNotDeleted = eventParticipantService.createDefaultFilter(cb, eventParticipantJoin);

		final Predicate caseFilter = CriteriaBuilderHelper.and(cb, caseUserFilter, caseNotDeleted);
		final Predicate contactFilter = CriteriaBuilderHelper.and(cb, contactUserFilter, contactNotDeleted);
		final Predicate eventParticipantFilter = CriteriaBuilderHelper.and(cb, eventParticipantUserFilter, eventParticipantNotDeleted);

		return CriteriaBuilderHelper.or(cb, caseFilter, contactFilter, eventParticipantFilter, cb.and(cb.isNull(caseJoin), cb.isNull(contactJoin), cb.isNull(eventParticipantJoin)));
	}

	public Predicate buildCriteriaFilter(PersonCriteria personCriteria, CriteriaQuery<?> cq, CriteriaBuilder cb, From<?, Person> personFrom) {

		final Join<Person, Location> location = personFrom.join(Person.ADDRESS, JoinType.LEFT);
		final Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
		final Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
		final Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);

		Predicate filter = null;
		filter = andEquals(cb, personFrom, filter, personCriteria.getBirthdateYYYY(), Person.BIRTHDATE_YYYY);
		filter = andEquals(cb, personFrom, filter, personCriteria.getBirthdateMM(), Person.BIRTHDATE_MM);
		filter = andEquals(cb, personFrom, filter, personCriteria.getBirthdateDD(), Person.BIRTHDATE_DD);
		if (personCriteria.getNameAddressPhoneEmailLike() != null) {
			String[] textFilters = personCriteria.getNameAddressPhoneEmailLike().split("\\s+");
			for (int i = 0; i < textFilters.length; i++) {
				String textFilter = formatForLike(textFilters[i]);
				if (!DataHelper.isNullOrEmpty(textFilter)) {
					Predicate likeFilters = cb.or(
						cb.like(cb.lower(personFrom.get(Person.FIRST_NAME)), textFilter),
						cb.like(cb.lower(personFrom.get(Person.LAST_NAME)), textFilter),
						cb.like(cb.lower(personFrom.get(Person.UUID)), textFilter),
						cb.like(cb.lower(personFrom.get(Person.EMAIL_ADDRESS)), textFilter),
						phoneNumberPredicate(cb, personFrom.get(Person.PHONE), textFilter),
						cb.like(cb.lower(location.get(Location.STREET)), textFilter),
						cb.like(cb.lower(location.get(Location.CITY)), textFilter),
						cb.like(cb.lower(location.get(Location.POSTAL_CODE)), textFilter));
					filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
				}
			}
		}
		filter = andEquals(cb, personFrom, filter, personCriteria.getPresentCondition(), Person.PRESENT_CONDITION);
		filter = andEqualsReferenceDto(cb, region, filter, personCriteria.getRegion());
		filter = andEqualsReferenceDto(cb, district, filter, personCriteria.getDistrict());
		filter = andEqualsReferenceDto(cb, community, filter, personCriteria.getCommunity());

		if (personCriteria.getPersonAssociation() != null) {
			switch (personCriteria.getPersonAssociation()) {

			case CASE:
				final Subquery<Case> caseSubquery = cq.subquery(Case.class);
				final Root<Case> caseRoot = caseSubquery.from(Case.class);
				caseSubquery.select(caseRoot.get(Case.ID)).where(cb.equal(caseRoot.get(Case.PERSON), personFrom));
				filter = CriteriaBuilderHelper.and(cb, filter, cb.exists(caseSubquery));
				break;
			case CONTACT:
				final Subquery<Contact> contactSubquery = cq.subquery(Contact.class);
				final Root<Contact> contactRoot = contactSubquery.from(Contact.class);
				contactSubquery.select(contactRoot.get(Contact.ID)).where(cb.equal(contactRoot.get(Contact.PERSON), personFrom));
				filter = CriteriaBuilderHelper.and(cb, filter, cb.exists(contactSubquery));
				break;
			case EVENT_PARTICIPANT:
				final Subquery<EventParticipant> eventParticipantSubquery = cq.subquery(EventParticipant.class);
				final Root<EventParticipant> eventParticipantRoot = eventParticipantSubquery.from(EventParticipant.class);
				eventParticipantSubquery.select(eventParticipantRoot.get(EventParticipant.ID))
					.where(cb.equal(eventParticipantRoot.get(EventParticipant.PERSON), personFrom));
				filter = CriteriaBuilderHelper.and(cb, filter, cb.exists(eventParticipantSubquery));
				break;
			}
		}

		return filter;
	}

	@Override
	public List<Person> getAllAfter(Date date, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		// persons by LGA
		CriteriaQuery<Person> personsQuery = cb.createQuery(Person.class);
		Root<Person> personsRoot = personsQuery.from(Person.class);
		Join<Person, Location> address = personsRoot.join(Person.ADDRESS);
		Predicate lgaFilter = cb.equal(address.get(Location.DISTRICT), user.getDistrict());
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, personsRoot, DateHelper.toTimestampUpper(date));
			lgaFilter = cb.and(lgaFilter, dateFilter);
		}
		personsQuery.where(lgaFilter);
		List<Person> lgaResultList = em.createQuery(personsQuery).getResultList();

		// persons by case
		CriteriaQuery<Person> casePersonsQuery = cb.createQuery(Person.class);
		Root<Case> casePersonsRoot = casePersonsQuery.from(Case.class);
		Join<Person, Person> casePersonsSelect = casePersonsRoot.join(Case.PERSON);
		casePersonsSelect.fetch(Person.ADDRESS);
		casePersonsQuery.select(casePersonsSelect);
		Predicate casePersonsFilter = caseService.createUserFilter(cb, casePersonsQuery, casePersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, casePersonsSelect, DateHelper.toTimestampUpper(date));
			// include case change dates: When a case is relocated it may become available to another user and this will have to include the person as-well
			Predicate caseDateFilter = caseService.createChangeDateFilter(cb, casePersonsRoot, DateHelper.toTimestampUpper(date));
			if (casePersonsFilter != null) {
				casePersonsFilter = cb.and(casePersonsFilter, cb.or(dateFilter, caseDateFilter));
			} else {
				casePersonsFilter = cb.or(dateFilter, caseDateFilter);
			}
		}
		if (casePersonsFilter != null) {
			casePersonsQuery.where(casePersonsFilter);
		}
		casePersonsQuery.distinct(true);
		List<Person> casePersonsResultList = em.createQuery(casePersonsQuery).getResultList();

		// persons by contact
		CriteriaQuery<Person> contactPersonsQuery = cb.createQuery(Person.class);
		Root<Contact> contactPersonsRoot = contactPersonsQuery.from(Contact.class);
		Join<Person, Person> contactPersonsSelect = contactPersonsRoot.join(Contact.PERSON);
		contactPersonsSelect.fetch(Person.ADDRESS);
		contactPersonsQuery.select(contactPersonsSelect);
		Predicate contactPersonsFilter = contactService.createUserFilter(cb, contactPersonsQuery, contactPersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, contactPersonsSelect, DateHelper.toTimestampUpper(date));
			Predicate contactDateFilter = contactService.createChangeDateFilter(cb, contactPersonsRoot, date);
			contactPersonsFilter = and(cb, contactPersonsFilter, cb.or(dateFilter, contactDateFilter));
		}
		if (contactPersonsFilter != null) {
			contactPersonsQuery.where(contactPersonsFilter);
		}
		contactPersonsQuery.distinct(true);
		List<Person> contactPersonsResultList = em.createQuery(contactPersonsQuery).getResultList();

		// persons by event participant
		CriteriaQuery<Person> eventPersonsQuery = cb.createQuery(Person.class);
		Root<EventParticipant> eventPersonsRoot = eventPersonsQuery.from(EventParticipant.class);
		Join<Person, Person> eventPersonsSelect = eventPersonsRoot.join(EventParticipant.PERSON);
		eventPersonsSelect.fetch(Person.ADDRESS);
		eventPersonsQuery.select(eventPersonsSelect);
		Predicate eventPersonsFilter = eventParticipantService.createUserFilter(cb, eventPersonsQuery, eventPersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, eventPersonsSelect, DateHelper.toTimestampUpper(date));
			Predicate eventParticipantDateFilter =
				eventParticipantService.createChangeDateFilter(cb, eventPersonsRoot, DateHelper.toTimestampUpper(date));
			eventPersonsFilter = and(cb, eventPersonsFilter, cb.or(dateFilter, eventParticipantDateFilter));
		}
		if (eventPersonsFilter != null) {
			eventPersonsQuery.where(eventPersonsFilter);
		}
		eventPersonsQuery.distinct(true);
		List<Person> eventPersonsResultList = em.createQuery(eventPersonsQuery).getResultList();

		return Stream.of(lgaResultList, casePersonsResultList, contactPersonsResultList, eventPersonsResultList)
			.flatMap(List<Person>::stream)
			.distinct()
			.sorted(Comparator.comparing(Person::getChangeDate))
			.collect(Collectors.toList());
	}

	public List<Long> getInJurisdictionIDs(final List<Person> selectedPersons) {
		if (selectedPersons.size() == 0) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> inJurisdictionQuery = cb.createQuery(Long.class);
		final Root<Person> personRoot = inJurisdictionQuery.from(Person.class);

		inJurisdictionQuery.select(personRoot.get(Person.ID));

		final Predicate isFromSelectedPersons =
			cb.in(personRoot.get(Person.ID)).value(selectedPersons.stream().map(Person::getId).collect(Collectors.toList()));
		inJurisdictionQuery.where(cb.and(isFromSelectedPersons, getJurisdictionPredicate(cb, inJurisdictionQuery, personRoot)));

		return em.createQuery(inJurisdictionQuery).getResultList();
	}

	public Predicate getJurisdictionPredicate(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<Person> personRoot) {

		final Path<Object> personId = personRoot.get(Person.ID);

		final Subquery<Long> caseJurisdictionSubQuery = cq.subquery(Long.class);
		final Root<Case> caseRoot = caseJurisdictionSubQuery.from(Case.class);
		caseJurisdictionSubQuery.select(caseRoot.get(Case.ID));
		caseJurisdictionSubQuery.where(
			cb.and(cb.equal(caseRoot.get(Case.PERSON).get(Person.ID), personId), caseService.isInJurisdictionOrOwned(cb, new CaseJoins<>(caseRoot))));
		final Predicate isCaseInJurisdiction = cb.exists(caseJurisdictionSubQuery);

		final Subquery<Long> contactJurisdictionSubQuery = cq.subquery(Long.class);
		final Root<Contact> contactRoot = contactJurisdictionSubQuery.from(Contact.class);
		contactJurisdictionSubQuery.select(contactRoot.get(Contact.ID));
		contactJurisdictionSubQuery.where(
			cb.and(
				cb.equal(contactRoot.get(Contact.PERSON).get(Person.ID), personId),
				contactService.isInJurisdictionOrOwned(cb, cq, contactRoot, new ContactJoins(contactRoot))));
		final Predicate isContactInJurisdiction = cb.exists(contactJurisdictionSubQuery);

		final Subquery<Long> eventParticipantJurisdictionSubQuery = cq.subquery(Long.class);
		final Root<EventParticipant> eventParticipantRoot = eventParticipantJurisdictionSubQuery.from(EventParticipant.class);
		eventParticipantJurisdictionSubQuery.select(eventParticipantRoot.get(EventParticipant.ID));

//		final Predicate reportedByCurrentUser = cb.and(
//			cb.isNotNull(eventParticipantRoot.get(EventParticipant.REPORTING_USER)),
//			cb.equal(eventParticipantRoot.get(EventParticipant.REPORTING_USER), getCurrentUser()));
//		eventParticipantJurisdictionSubQuery
//			.where(cb.and(cb.equal(eventParticipantRoot.get(EventParticipant.PERSON).get(Person.ID), personId), reportedByCurrentUser));
		eventParticipantJurisdictionSubQuery.where(cb.equal(eventParticipantRoot.get(EventParticipant.PERSON).get(Person.ID), personId));

		final Predicate isEventParticipantInJurisdiction = cb.exists(eventParticipantJurisdictionSubQuery);

		return cb.or(isCaseInJurisdiction, isContactInJurisdiction, isEventParticipantInJurisdiction);
	}

	public List<PersonNameDto> getMatchingNameDtos(PersonSimilarityCriteria criteria, Integer limit) {

		setSimilarityThresholdQuery();

		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate caseContactEventParticipantLinkPredicate;

		CriteriaQuery<PersonNameDto> personQuery = cb.createQuery(PersonNameDto.class);
		Root<Person> personRoot = personQuery.from(Person.class);
		Join<Person, Case> personCaseJoin = personRoot.join(Person.CASES, JoinType.LEFT);
		Join<Person, Contact> personContactJoin = personRoot.join(Person.CONTACTS, JoinType.LEFT);
		Join<Person, EventParticipant> personEventParticipantJoin = personRoot.join(Person.EVENT_PARTICIPANTS, JoinType.LEFT);

		personQuery.multiselect(personRoot.get(Person.FIRST_NAME), personRoot.get(Person.LAST_NAME), personRoot.get(Person.UUID));

		// Persons of active cases
		Predicate personSimilarityFilter = buildSimilarityCriteriaFilter(criteria, cb, personRoot);
		Predicate activeCasesFilter = caseService.createActiveCasesFilter(cb, personCaseJoin);
		Predicate caseUserFilter = caseService.createUserFilter(cb, personQuery, personCaseJoin);
		Predicate personCasePredicate = and(cb, personCaseJoin.get(Case.ID).isNotNull(), activeCasesFilter, caseUserFilter);

		// Persons of active contacts
		Predicate activeContactsFilter = contactService.createActiveContactsFilter(cb, personContactJoin);
		Predicate contactUserFilter = contactService.createUserFilter(cb, personQuery, personContactJoin);
		Predicate personContactPredicate = and(cb, personContactJoin.get(Contact.ID).isNotNull(), contactUserFilter, activeContactsFilter);

		// Persons of event participants in active events
		Predicate activeEventParticipantsFilter = eventParticipantService.createActiveEventParticipantsFilter(cb, personEventParticipantJoin);
		Predicate eventParticipantUserFilter = eventParticipantService.createUserFilter(cb, personQuery, personEventParticipantJoin);
		Predicate personEventParticipantPredicate =
			and(cb, personEventParticipantJoin.get(EventParticipant.ID).isNotNull(), activeEventParticipantsFilter, eventParticipantUserFilter);

		caseContactEventParticipantLinkPredicate =
			CriteriaBuilderHelper.or(cb, personCasePredicate, personContactPredicate, personEventParticipantPredicate);

		personQuery.where(and(cb, personSimilarityFilter, caseContactEventParticipantLinkPredicate));
		personQuery.distinct(true);

		TypedQuery<PersonNameDto> query = em.createQuery(personQuery);
		if (limit != null) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	public void setSimilarityThresholdQuery() {
		double nameSimilarityThreshold = configFacade.getNameSimilarityThreshold();
		Query q = em.createNativeQuery("select set_limit(" + nameSimilarityThreshold + ")");
		q.getSingleResult();
	}

	public List<Person> getDeathsBetween(Date fromDate, Date toDate, District district, Disease disease, User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Person> casePersonsQuery = cb.createQuery(Person.class);
		Root<Case> casePersonsRoot = casePersonsQuery.from(Case.class);
		Path<Person> casePersonsSelect = casePersonsRoot.get(Case.PERSON);
		casePersonsQuery.select(casePersonsSelect);
		Predicate casePersonsFilter = caseService.createUserFilter(cb, casePersonsQuery, casePersonsRoot);

		// only probable and confirmed cases are of interest
		Predicate classificationFilter = cb.equal(casePersonsRoot.get(Case.CASE_CLASSIFICATION), CaseClassification.CONFIRMED);
		classificationFilter = cb.or(classificationFilter, cb.equal(casePersonsRoot.get(Case.CASE_CLASSIFICATION), CaseClassification.PROBABLE));

		if (casePersonsFilter != null) {
			casePersonsFilter = cb.and(casePersonsFilter, classificationFilter);
		} else {
			casePersonsFilter = classificationFilter;
		}

		// death date range
		Predicate dateFilter = cb.isNotNull(casePersonsSelect.get(Person.DEATH_DATE));
		dateFilter = cb.and(dateFilter, cb.greaterThanOrEqualTo(casePersonsSelect.get(Person.DEATH_DATE), fromDate));
		dateFilter = cb.and(dateFilter, cb.lessThanOrEqualTo(casePersonsSelect.get(Person.DEATH_DATE), toDate));

		if (casePersonsFilter != null) {
			casePersonsFilter = cb.and(casePersonsFilter, dateFilter);
		} else {
			casePersonsFilter = dateFilter;
		}

		if (casePersonsFilter != null && district != null) {
			casePersonsFilter = cb.and(casePersonsFilter, cb.equal(casePersonsRoot.get(Case.DISTRICT), district));
		}

		if (casePersonsFilter != null && disease != null) {
			casePersonsFilter = cb.and(casePersonsFilter, cb.equal(casePersonsRoot.get(Case.DISEASE), disease));
		}

		if (casePersonsFilter != null) {
			casePersonsQuery.where(casePersonsFilter);
		}
		casePersonsQuery.distinct(true);
		return em.createQuery(casePersonsQuery).getResultList();
	}

	public Location getAddressByPersonId(long personId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Location> cq = cb.createQuery(Location.class);
		Root<Person> root = cq.from(getElementClass());
		cq.where(cb.equal(root.get(Person.ID), personId));
		cq.select(root.get(Person.ADDRESS));
		return em.createQuery(cq).getSingleResult();
	}

	public Predicate buildSimilarityCriteriaFilter(PersonSimilarityCriteria criteria, CriteriaBuilder cb, From<?, Person> personFrom) {

		Predicate filter = null;

		if (!StringUtils.isBlank(criteria.getFirstName()) && !StringUtils.isBlank(criteria.getLastName())) {
			Expression<String> nameExpr = cb.concat(personFrom.get(Person.FIRST_NAME), " ");
			nameExpr = cb.concat(nameExpr, personFrom.get(Person.LAST_NAME));

			String name = criteria.getFirstName() + " " + criteria.getLastName();

			filter = and(cb, filter, cb.isTrue(cb.function(SIMILARITY_OPERATOR, boolean.class, nameExpr, cb.literal(name))));
		}

		if (criteria.getSex() != null) {
			filter = and(cb, filter, cb.or(cb.isNull(personFrom.get(Person.SEX)), cb.equal(personFrom.get(Person.SEX), criteria.getSex())));
		}
		if (criteria.getBirthdateYYYY() != null) {
			filter = and(
				cb,
				filter,
				cb.or(
					cb.isNull(personFrom.get(Person.BIRTHDATE_YYYY)),
					cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), criteria.getBirthdateYYYY())));
		}
		if (criteria.getBirthdateMM() != null) {
			filter = and(
				cb,
				filter,
				cb.or(cb.isNull(personFrom.get(Person.BIRTHDATE_MM)), cb.equal(personFrom.get(Person.BIRTHDATE_MM), criteria.getBirthdateMM())));
		}
		if (criteria.getBirthdateDD() != null) {
			filter = and(
				cb,
				filter,
				cb.or(cb.isNull(personFrom.get(Person.BIRTHDATE_DD)), cb.equal(personFrom.get(Person.BIRTHDATE_DD), criteria.getBirthdateDD())));
		}
		if (!StringUtils.isBlank(criteria.getNationalHealthId())) {
			filter = and(
				cb,
				filter,
				cb.or(
					cb.isNull(personFrom.get(Person.NATIONAL_HEALTH_ID)),
					cb.equal(personFrom.get(Person.NATIONAL_HEALTH_ID), criteria.getNationalHealthId())));
		}
		if (!StringUtils.isBlank(criteria.getPassportNumber())) {
			filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(personFrom.get(Person.PASSPORT_NUMBER), criteria.getPassportNumber()));
		}

		return filter;
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Person> from, Timestamp date) {

		Predicate dateFilter = cb.greaterThan(from.get(AbstractDomainObject.CHANGE_DATE), date);
		Join<Person, Location> address = from.join(Person.ADDRESS);
		return cb.or(dateFilter, cb.greaterThan(address.get(AbstractDomainObject.CHANGE_DATE), date));
	}

	public void notifyExternalJournalPersonUpdate(PersonDto existingPerson, PersonDto updatedPerson) {

	}
}
