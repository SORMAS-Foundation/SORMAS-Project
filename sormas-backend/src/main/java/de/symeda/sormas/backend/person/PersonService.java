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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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
import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonNameDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.GeoLatLon;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.geocoding.GeocodingService;
import de.symeda.sormas.backend.immunization.Immunization;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ExternalDataUtil;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class PersonService extends AdoServiceWithUserFilter<Person> {

	@EJB
	private UserService userService;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private TravelEntryService travelEntryService;
	@EJB
	private GeocodingService geocodingService;
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

		// persons by immunization
		CriteriaQuery<String> immunizationPersonsQuery = cb.createQuery(String.class);
		Root<Immunization> immunizationPersonsRoot = immunizationPersonsQuery.from(Immunization.class);
		Join<Immunization, Person> immunizationPersonsSelect = immunizationPersonsRoot.join(Immunization.PERSON);
		immunizationPersonsQuery.select(immunizationPersonsSelect.get(Person.UUID));
		Predicate immunizationPersonsFilter = immunizationService.createUserFilter(cb, immunizationPersonsQuery, immunizationPersonsRoot);
		if (immunizationPersonsFilter != null) {
			immunizationPersonsQuery.where(immunizationPersonsFilter);
		}
		immunizationPersonsQuery.distinct(true);
		List<String> immunizationPersonsResultList = em.createQuery(immunizationPersonsQuery).getResultList();

		// persons by travel entry
		CriteriaQuery<String> travelEntryPersonsQuery = cb.createQuery(String.class);
		Root<TravelEntry> travelEntryPersonsRoot = travelEntryPersonsQuery.from(TravelEntry.class);
		Join<TravelEntry, Person> travelEntryPersonsSelect = travelEntryPersonsRoot.join(TravelEntry.PERSON);
		travelEntryPersonsQuery.select(travelEntryPersonsSelect.get(Person.UUID));
		Predicate travelEntryPersonsFilter = travelEntryService.createUserFilter(cb, travelEntryPersonsQuery, travelEntryPersonsRoot);
		if (travelEntryPersonsFilter != null) {
			travelEntryPersonsQuery.where(travelEntryPersonsFilter);
		}
		travelEntryPersonsQuery.distinct(true);
		List<String> travelEntryPersonsResultList = em.createQuery(travelEntryPersonsQuery).getResultList();

		return Stream
			.of(
				lgaResultList,
				casePersonsResultList,
				contactPersonsResultList,
				eventPersonsResultList,
				immunizationPersonsResultList,
				travelEntryPersonsResultList)
			.flatMap(List<String>::stream)
			.distinct()
			.collect(Collectors.toList());
	}

	@Override
	@Deprecated
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Person> from) {
		throw new UnsupportedOperationException("Should not be called -> obsolete!");
	}

	@SuppressWarnings({
		"rawtypes",
		"unchecked" })
	public Predicate createUserFilter(PersonQueryContext personQueryContext, PersonCriteria personCriteria) {

		final CriteriaBuilder cb = personQueryContext.getCriteriaBuilder();
		final CriteriaQuery cq = personQueryContext.getQuery();
		final PersonJoins joins = (PersonJoins) personQueryContext.getJoins();

		// 1. Define filters per association lazy to avoid superfluous joins
		final Supplier<Predicate> caseFilter = () -> CriteriaBuilderHelper
			.and(cb, caseService.createUserFilter(cb, cq, joins.getCaze()), caseService.createDefaultFilter(cb, joins.getCaze()));
		final Supplier<Predicate> contactFilter = () -> CriteriaBuilderHelper
			.and(cb, contactService.createUserFilter(cb, cq, joins.getContact()), contactService.createDefaultFilter(cb, joins.getContact()));
		final Supplier<Predicate> eventParticipantFilter = () -> CriteriaBuilderHelper.and(
			cb,
			eventParticipantService.createUserFilter(cb, cq, joins.getEventParticipant()),
			eventParticipantService.createDefaultFilter(cb, joins.getEventParticipant()));
		final Supplier<Predicate> immunizationFilter = () -> CriteriaBuilderHelper.and(
			cb,
			immunizationService.createUserFilter(cb, cq, joins.getImmunization()),
			immunizationService.createDefaultFilter(cb, joins.getImmunization()));
		final Supplier<Predicate> travelEntryFilter = () -> CriteriaBuilderHelper.and(
			cb,
			travelEntryService.createUserFilter(cb, cq, joins.getTravelEntry()),
			travelEntryService.createDefaultFilter(cb, joins.getTravelEntry()));

		// 2. Define the Joins on associations where needed
		PersonAssociation personAssociation =
			Optional.ofNullable(personCriteria).map(e -> e.getPersonAssociation()).orElse(PersonCriteria.DEFAULT_ASSOCIATION);
		switch (personAssociation) {
		case ALL:
			return CriteriaBuilderHelper
				.or(cb, caseFilter.get(), contactFilter.get(), eventParticipantFilter.get(), immunizationFilter.get(), travelEntryFilter.get());
		case CASE:
			return caseFilter.get();
		case CONTACT:
			return contactFilter.get();
		case EVENT_PARTICIPANT:
			return eventParticipantFilter.get();
		case IMMUNIZATION:
			return immunizationFilter.get();
		case TRAVEL_ENTRY:
			return travelEntryFilter.get();
		default:
			throw new IllegalArgumentException(personAssociation.toString());
		}
	}

	public Predicate buildCriteriaFilter(PersonCriteria personCriteria, PersonQueryContext personQueryContext) {

		// Hint: personCriteria.getPersonAssociation() is interpreted in createUserFilter, but not again here

		final CriteriaBuilder cb = personQueryContext.getCriteriaBuilder();
		final From<?, Person> personFrom = personQueryContext.getRoot();

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

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, personFrom.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, personFrom.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.UUID), textFilter),
					CriteriaBuilderHelper.ilike(
						cb,
						(Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY),
						textFilter),
					phoneNumberPredicate(
						cb,
						(Expression<String>) personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY),
						textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.STREET), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.CITY), textFilter),
					CriteriaBuilderHelper.ilike(cb, location.get(Location.POSTAL_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_TOKEN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		filter = andEquals(cb, personFrom, filter, personCriteria.getPresentCondition(), Person.PRESENT_CONDITION);
		filter = andEqualsReferenceDto(cb, region, filter, personCriteria.getRegion());
		filter = andEqualsReferenceDto(cb, district, filter, personCriteria.getDistrict());
		filter = andEqualsReferenceDto(cb, community, filter, personCriteria.getCommunity());

		return filter;
	}

	@Override
	// todo refactor this to use the create user filter form persons
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

		// persons by immunization
		CriteriaQuery<Person> immunizationPersonsQuery = cb.createQuery(Person.class);
		Root<Immunization> immunizationPersonsRoot = immunizationPersonsQuery.from(Immunization.class);
		Join<Immunization, Person> immunizationPersonsSelect = immunizationPersonsRoot.join(Immunization.PERSON);
		immunizationPersonsSelect.fetch(Person.ADDRESS);
		immunizationPersonsQuery.select(immunizationPersonsSelect);
		Predicate immunizationPersonsFilter = immunizationService.createUserFilter(cb, immunizationPersonsQuery, immunizationPersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, immunizationPersonsSelect, DateHelper.toTimestampUpper(date));
			Predicate immunizationDateFilter =
				immunizationService.createChangeDateFilter(cb, immunizationPersonsRoot, DateHelper.toTimestampUpper(date));
			immunizationPersonsFilter = and(cb, immunizationPersonsFilter, cb.or(dateFilter, immunizationDateFilter));
		}
		if (immunizationPersonsFilter != null) {
			immunizationPersonsQuery.where(immunizationPersonsFilter);
		}
		immunizationPersonsQuery.distinct(true);
		List<Person> immunizationPersonsResultList = em.createQuery(immunizationPersonsQuery).getResultList();

		// persons by travel entries
		CriteriaQuery<Person> tepQuery = cb.createQuery(Person.class);
		Root<TravelEntry> tepRoot = tepQuery.from(TravelEntry.class);
		Join<TravelEntry, Person> tepSelect = tepRoot.join(TravelEntry.PERSON);
		tepSelect.fetch(Person.ADDRESS);
		tepQuery.select(tepSelect);
		Predicate tepFilter = travelEntryService.createUserFilter(cb, tepQuery, tepRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, tepSelect, DateHelper.toTimestampUpper(date));
			Predicate travelEntryDateFilter = travelEntryService.createChangeDateFilter(cb, tepRoot, DateHelper.toTimestampUpper(date));
			tepFilter = and(cb, tepFilter, cb.or(dateFilter, travelEntryDateFilter));
		}
		if (tepFilter != null) {
			tepQuery.where(tepFilter);
		}
		tepQuery.distinct(true);
		List<Person> travelEntryPersonsResultList = em.createQuery(tepQuery).getResultList();

		return Stream
			.of(
				lgaResultList,
				casePersonsResultList,
				contactPersonsResultList,
				eventPersonsResultList,
				immunizationPersonsResultList,
				travelEntryPersonsResultList)
			.flatMap(List<Person>::stream)
			.distinct()
			.sorted(Comparator.comparing(Person::getChangeDate))
			.collect(Collectors.toList());
	}

	public List<Long> getInJurisdictionIDs(final List<Person> selectedEntities) {
		if (selectedEntities.size() == 0) {
			return Collections.emptyList();
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> inJurisdictionQuery = cb.createQuery(Long.class);
		final Root<Person> personRoot = inJurisdictionQuery.from(Person.class);

		inJurisdictionQuery.select(personRoot.get(Person.ID));

		final Predicate isFromSelectedPersons =
			cb.in(personRoot.get(Person.ID)).value(selectedEntities.stream().map(Person::getId).collect(Collectors.toList()));
		inJurisdictionQuery.where(cb.and(isFromSelectedPersons, inJurisdictionOrOwned(new PersonQueryContext(cb, inJurisdictionQuery, personRoot))));

		return em.createQuery(inJurisdictionQuery).getResultList();
	}

	public boolean inJurisdictionOrOwned(Person person) {
		return !getInJurisdictionIDs(Arrays.asList(person)).isEmpty();
	}

	public Predicate inJurisdictionOrOwned(PersonQueryContext personQueryContext) {
		final User currentUser = userService.getCurrentUser();
		return PersonJurisdictionPredicateValidator
			.of(personQueryContext.getQuery(), personQueryContext.getCriteriaBuilder(), (PersonJoins) personQueryContext.getJoins(), currentUser)
			.inJurisdictionOrOwned();
	}

	public List<PersonNameDto> getMatchingNameDtos(PersonSimilarityCriteria criteria, Integer limit) {

		setSimilarityThresholdQuery();
		boolean activeEntriesOnly = configFacade.isDuplicateChecksExcludePersonsOfArchivedEntries();

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<PersonNameDto> personQuery = cb.createQuery(PersonNameDto.class);
		Root<Person> personRoot = personQuery.from(Person.class);
		Join<Person, Case> personCaseJoin = personRoot.join(Person.CASES, JoinType.LEFT);
		Join<Person, Contact> personContactJoin = personRoot.join(Person.CONTACTS, JoinType.LEFT);
		Join<Person, EventParticipant> personEventParticipantJoin = personRoot.join(Person.EVENT_PARTICIPANTS, JoinType.LEFT);
		Join<Person, Immunization> personImmunizationJoin = personRoot.join(Person.IMMUNIZATIONS, JoinType.LEFT);
		Join<Person, TravelEntry> personTravelEntryJoin = personRoot.join(Person.TRAVEL_ENTRIES, JoinType.LEFT);

		personQuery.multiselect(personRoot.get(Person.FIRST_NAME), personRoot.get(Person.LAST_NAME), personRoot.get(Person.UUID));

		// Persons of active cases
		Predicate personSimilarityFilter = buildSimilarityCriteriaFilter(criteria, cb, personRoot);
		Predicate activeCasesFilter =
			activeEntriesOnly ? caseService.createActiveCasesFilter(cb, personCaseJoin) : caseService.createDefaultFilter(cb, personCaseJoin);
		Predicate caseUserFilter = caseService.createUserFilter(cb, personQuery, personCaseJoin);
		Predicate personCasePredicate = and(cb, personCaseJoin.get(Case.ID).isNotNull(), activeCasesFilter, caseUserFilter);

		// Persons of active contacts
		Predicate activeContactsFilter = activeEntriesOnly
			? contactService.createActiveContactsFilter(cb, personContactJoin)
			: contactService.createDefaultFilter(cb, personContactJoin);
		Predicate contactUserFilter = contactService.createUserFilter(cb, personQuery, personContactJoin);
		Predicate personContactPredicate = and(cb, personContactJoin.get(Contact.ID).isNotNull(), contactUserFilter, activeContactsFilter);

		// Persons of event participants in active events
		Predicate activeEventParticipantsFilter = activeEntriesOnly
			? eventParticipantService.createActiveEventParticipantsInActiveEventsFilter(cb, personEventParticipantJoin)
			: eventParticipantService.createDefaultInUndeletedEventsFilter(cb, personEventParticipantJoin);
		Predicate eventParticipantUserFilter = eventParticipantService.createUserFilter(cb, personQuery, personEventParticipantJoin);
		Predicate personEventParticipantPredicate =
			and(cb, personEventParticipantJoin.get(EventParticipant.ID).isNotNull(), activeEventParticipantsFilter, eventParticipantUserFilter);

		// Persons of active immunizations
		Predicate activeImmunizationsFilter = activeEntriesOnly
			? immunizationService.createActiveImmunizationsFilter(cb, personImmunizationJoin)
			: immunizationService.createDefaultFilter(cb, personImmunizationJoin);
		Predicate immunizationUserFilter = immunizationService.createUserFilter(cb, personQuery, personImmunizationJoin);
		Predicate personImmunizationPredicate =
			and(cb, personImmunizationJoin.get(Immunization.ID).isNotNull(), immunizationUserFilter, activeImmunizationsFilter);

		// Persons of active travel entries
		Predicate activeTravelEntriesFilter = activeEntriesOnly
			? travelEntryService.createActiveTravelEntriesFilter(cb, personTravelEntryJoin)
			: travelEntryService.createDefaultFilter(cb, personTravelEntryJoin);
		Predicate travelEntryUserFilter = travelEntryService.createUserFilter(cb, personQuery, personTravelEntryJoin);
		Predicate personTravelEntryPredicate =
			and(cb, personTravelEntryJoin.get(TravelEntry.ID).isNotNull(), travelEntryUserFilter, activeTravelEntriesFilter);

		Predicate finalPredicate = CriteriaBuilderHelper.or(
			cb,
			personCasePredicate,
			personContactPredicate,
			personEventParticipantPredicate,
			personImmunizationPredicate,
			personTravelEntryPredicate);

		personQuery.where(and(cb, personSimilarityFilter, finalPredicate));
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
			Expression<Sex> sexExpr = cb.literal(criteria.getSex());

			Predicate sexFilter = cb.or(
				cb.or(cb.isNull(personFrom.get(Person.SEX)), cb.isNull(sexExpr)),
				cb.or(cb.equal(personFrom.get(Person.SEX), Sex.UNKNOWN), cb.equal(sexExpr, Sex.UNKNOWN)),
				cb.equal(personFrom.get(Person.SEX), sexExpr));

			filter = and(cb, filter, sexFilter);
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

		String uuidExternalIdExternalTokenLike = criteria.getUuidExternalIdExternalTokenLike();
		if (!StringUtils.isBlank(uuidExternalIdExternalTokenLike)) {
			Predicate uuidExternalIdExternalTokenFilter = CriteriaBuilderHelper.buildFreeTextSearchPredicate(
				cb,
				uuidExternalIdExternalTokenLike,
				(searchTerm) -> cb.or(
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.UUID), searchTerm),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_ID), searchTerm),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_TOKEN), searchTerm)));
			filter = CriteriaBuilderHelper.or(cb, filter, uuidExternalIdExternalTokenFilter);
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

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public long updateGeoLocation(List<String> personUuids, boolean overwriteExistingCoordinates) {

		long updatedCount = 0;
		List<Person> persons = getByUuids(personUuids);
		for (Person person : persons) {
			if (updateGeoLocation(person, overwriteExistingCoordinates)) {
				updatedCount++;
			}
		}
		return updatedCount;
	}

	public boolean updateGeoLocation(Person person, boolean overwriteExistingCoordinates) {

		boolean geoLocationUpdated = false;
		if (person.getAddress() != null
			&& (overwriteExistingCoordinates || (person.getAddress().getLatitude() == null || person.getAddress().getLongitude() == null))) {
			GeoLatLon latLon = geocodingService.getLatLon(person.getAddress());
			if (latLon != null) {
				person.getAddress().setLatitude(latLon.getLat());
				person.getAddress().setLongitude(latLon.getLon());
				ensurePersisted(person);
				geoLocationUpdated = true;
			}
		}
		return geoLocationUpdated;
	}

	@Transactional(rollbackOn = Exception.class)
	public void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException {
		ExternalDataUtil.updateExternalData(externalData, this::getByUuids, this::ensurePersisted);
	}

	public List<Person> getByExternalIdsBatched(List<String> externalIds) {
		if (CollectionUtils.isEmpty(externalIds)) {
			// Avoid empty IN clause
			return Collections.emptyList();
		} else if (externalIds.size() > ModelConstants.PARAMETER_LIMIT) {
			List<Person> persons = new LinkedList<>();
			IterableHelper
				.executeBatched(externalIds, ModelConstants.PARAMETER_LIMIT, batchedPersonUuids -> persons.addAll(getByExternalIds(externalIds)));
			return persons;
		} else {
			return getByExternalIds(externalIds);
		}
	}

	private List<Person> getByExternalIds(List<String> externalIds) {
		if (externalIds == null || externalIds.isEmpty()) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(getElementClass());
		Root<Person> from = cq.from(getElementClass());
		cq.where(from.get(Person.EXTERNAL_ID).in(externalIds));

		return em.createQuery(cq).getResultList();
	}
}
