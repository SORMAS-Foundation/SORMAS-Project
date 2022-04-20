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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.geo.GeoLatLon;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonAssociation;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.messaging.ManualMessageLogService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventUserFilterCriteria;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.geocoding.GeocodingService;
import de.symeda.sormas.backend.immunization.ImmunizationQueryContext;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ChangeDateUuidComparator;
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
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private ManualMessageLogService manualMessageLogService;

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
		Predicate eventPersonsFilter = eventParticipantService.createUserFilter(new EventParticipantQueryContext(cb, eventPersonsQuery, eventPersonsRoot));
		if (eventPersonsFilter != null) {
			eventPersonsQuery.where(eventPersonsFilter);
		}
		eventPersonsQuery.distinct(true);
		List<String> eventPersonsResultList = em.createQuery(eventPersonsQuery).getResultList();

		// persons by immunization
		List<String> immunizationPersonsResultList = new ArrayList<>();
		if (!featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			CriteriaQuery<String> immunizationPersonsQuery = cb.createQuery(String.class);
			Root<Immunization> immunizationPersonsRoot = immunizationPersonsQuery.from(Immunization.class);
			Join<Immunization, Person> immunizationPersonsSelect = immunizationPersonsRoot.join(Immunization.PERSON);
			immunizationPersonsQuery.select(immunizationPersonsSelect.get(Person.UUID));
			Predicate immunizationPersonsFilter = immunizationService.createUserFilter(cb, immunizationPersonsQuery, immunizationPersonsRoot);
			if (immunizationPersonsFilter != null) {
				immunizationPersonsQuery.where(immunizationPersonsFilter);
			}
			immunizationPersonsQuery.distinct(true);
			immunizationPersonsResultList = em.createQuery(immunizationPersonsQuery).getResultList();
		}

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
		final PersonJoins joins = personQueryContext.getJoins();

		final boolean fullImmunizationModuleUsed =
			!featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED);

		// 1. Define filters per association lazy to avoid superfluous joins
		final Supplier<Predicate> caseFilter = () -> CriteriaBuilderHelper.and(
			cb,
			caseService.createUserFilter(cb, cq, joins.getCaze(), new CaseUserFilterCriteria()),
			caseService.createDefaultFilter(cb, joins.getCaze()));
		final Supplier<Predicate> contactFilter = () -> {
			final Predicate contactUserFilter = contactService.createUserFilter(
				new ContactQueryContext(cb, cq, joins.getContactJoins()),
				new ContactCriteria().includeContactsFromOtherJurisdictions(false));
			return CriteriaBuilderHelper.and(cb, contactUserFilter, contactService.createDefaultFilter(cb, joins.getContact()));
		};
		final Supplier<Predicate> eventParticipantFilter = () -> CriteriaBuilderHelper.and(
			cb,
			eventParticipantService.createUserFilter( new EventParticipantQueryContext(
				cb,
				cq,
				joins.getEventParticipantJoins()),
				new EventUserFilterCriteria().includeUserCaseAndEventParticipantFilter(false).forceRegionJurisdiction(true)),
			eventParticipantService.createDefaultFilter(cb, joins.getEventParticipant()));
		final Supplier<Predicate> immunizationFilter = fullImmunizationModuleUsed
			? () -> CriteriaBuilderHelper.and(
				cb,
				immunizationService.createUserFilter(cb, cq, joins.getImmunization()),
				immunizationService.createDefaultFilter(cb, joins.getImmunization()))
			: () -> null;
		final Supplier<Predicate> travelEntryFilter = () -> CriteriaBuilderHelper.and(
			cb,
			travelEntryService.createUserFilter(cb, cq, joins.getTravelEntry()),
			travelEntryService.createDefaultFilter(cb, joins.getTravelEntry()));

		// 2. Define the Joins on associations where needed
		PersonAssociation personAssociation =
			Optional.ofNullable(personCriteria).map(e -> e.getPersonAssociation()).orElse(PersonCriteria.DEFAULT_ASSOCIATION);
		switch (personAssociation) {
		case ALL:
			return CriteriaBuilderHelper.or(
				cb,
				caseFilter.get(),
				contactFilter.get(),
				eventParticipantFilter.get(),
				fullImmunizationModuleUsed ? immunizationFilter.get() : null,
				travelEntryFilter.get());
		case CASE:
			return caseFilter.get();
		case CONTACT:
			return contactFilter.get();
		case EVENT_PARTICIPANT:
			return eventParticipantFilter.get();
		case IMMUNIZATION:
			if (!fullImmunizationModuleUsed) {
				throw new UnsupportedOperationException(
					"Filtering persons by immunizations is not supported when the reduced immunization module is used.");
			}
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

		final PersonJoins personJoins = (PersonJoins) personQueryContext.getJoins();
		final Join<Person, Location> location = personJoins.getAddress();
		final Join<Location, Region> region = personJoins.getAddressJoins().getRegion();
		final Join<Location, District> district = personJoins.getAddressJoins().getDistrict();
		final Join<Location, Community> community = personJoins.getAddressJoins().getCommunity();

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
					CriteriaBuilderHelper.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter),
					phoneNumberPredicate(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
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
	public List<Person> getAllAfter(Date date) {
		return getAllAfter(date, null, null);
	}

	@Override
	// todo refactor this to use the create user filter form persons
	public List<Person> getAllAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {

		User user = getCurrentUser();

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Person> personsQuery = cb.createQuery(Person.class);
		final Root<Person> personsRoot = personsQuery.from(Person.class);
		final PersonQueryContext personQueryContext = new PersonQueryContext(cb, personsQuery, personsRoot);
		final PersonJoins joins = personQueryContext.getJoins();

		// persons by district

		Join<Person, Location> address = joins.getAddress();
		Predicate districtFilter = cb.equal(address.get(Location.DISTRICT), user.getDistrict());
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(personQueryContext, DateHelper.toTimestampUpper(date), lastSynchronizedUuid);
			districtFilter = cb.and(districtFilter, dateFilter);
		}
		personsQuery.where(districtFilter);
		List<Person> districtResultList = getBatchedQueryResults(cb, personsQuery, personsRoot, batchSize);

		// persons by case
		CriteriaQuery<Person> casePersonsQuery = cb.createQuery(Person.class);
		Root<Case> casePersonsRoot = casePersonsQuery.from(Case.class);
		Join<Person, Person> casePersonsSelect = casePersonsRoot.join(Case.PERSON);
		casePersonsSelect.fetch(Person.ADDRESS);
		casePersonsQuery.select(casePersonsSelect);
		Predicate casePersonsFilter = caseService.createUserFilter(cb, casePersonsQuery, casePersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, casePersonsSelect, DateHelper.toTimestampUpper(date), lastSynchronizedUuid);
			if (batchSize == null) {
				// include case change dates: When a case is relocated it may become available to another user and this will have to include the person as-well
				Predicate caseDateFilter = caseService.createChangeDateFilter(cb, casePersonsRoot, DateHelper.toTimestampUpper(date));
				dateFilter = cb.or(dateFilter, caseDateFilter);
			}
			if (casePersonsFilter != null) {
				casePersonsFilter = cb.and(casePersonsFilter, dateFilter);
			} else {
				casePersonsFilter = dateFilter;
			}
		}
		if (casePersonsFilter != null) {
			casePersonsQuery.where(casePersonsFilter);
		}
		casePersonsQuery.distinct(true);
		List<Person> casePersonsResultList = getBatchedQueryResults(cb, casePersonsQuery, casePersonsSelect, batchSize);

		// persons by contact
		CriteriaQuery<Person> contactPersonsQuery = cb.createQuery(Person.class);
		Root<Contact> contactPersonsRoot = contactPersonsQuery.from(Contact.class);
		Join<Person, Person> contactPersonsSelect = contactPersonsRoot.join(Contact.PERSON);
		contactPersonsSelect.fetch(Person.ADDRESS);
		contactPersonsQuery.select(contactPersonsSelect);
		Predicate contactPersonsFilter = contactService.createUserFilter(cb, contactPersonsQuery, contactPersonsRoot);
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, contactPersonsSelect, DateHelper.toTimestampUpper(date), lastSynchronizedUuid);
			if (batchSize == null) {
				Predicate contactDateFilter = contactService.createChangeDateFilter(cb, contactPersonsRoot, date);
				dateFilter = cb.or(dateFilter, contactDateFilter);
			}
			contactPersonsFilter = and(cb, contactPersonsFilter, dateFilter);
		}
		if (contactPersonsFilter != null) {
			contactPersonsQuery.where(contactPersonsFilter);
		}
		contactPersonsQuery.distinct(true);
		List<Person> contactPersonsResultList = getBatchedQueryResults(cb, contactPersonsQuery, contactPersonsSelect, batchSize);

		// persons by event participant
		CriteriaQuery<Person> eventPersonsQuery = cb.createQuery(Person.class);
		Root<EventParticipant> eventPersonsRoot = eventPersonsQuery.from(EventParticipant.class);
		Join<Person, Person> eventPersonsSelect = eventPersonsRoot.join(EventParticipant.PERSON);
		eventPersonsSelect.fetch(Person.ADDRESS);
		eventPersonsQuery.select(eventPersonsSelect);
		Predicate eventPersonsFilter = eventParticipantService.createUserFilter(new EventParticipantQueryContext(cb, eventPersonsQuery, eventPersonsRoot));
		// date range
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, eventPersonsSelect, DateHelper.toTimestampUpper(date), lastSynchronizedUuid);
			if (batchSize == null) {
				Predicate eventParticipantDateFilter =
					eventParticipantService.createChangeDateFilter(cb, eventPersonsRoot, DateHelper.toTimestampUpper(date));
				dateFilter = cb.or(dateFilter, eventParticipantDateFilter);
			}
			eventPersonsFilter = and(cb, eventPersonsFilter, dateFilter);
		}
		if (eventPersonsFilter != null) {
			eventPersonsQuery.where(eventPersonsFilter);
		}
		eventPersonsQuery.distinct(true);
		List<Person> eventPersonsResultList = getBatchedQueryResults(cb, eventPersonsQuery, eventPersonsSelect, batchSize);

		// persons by immunization
		List<Person> immunizationPersonsResultList = new ArrayList<>();
		if (!featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			CriteriaQuery<Person> immunizationPersonsQuery = cb.createQuery(Person.class);
			Root<Immunization> immunizationPersonsRoot = immunizationPersonsQuery.from(Immunization.class);
			Join<Immunization, Person> immunizationPersonsSelect = immunizationPersonsRoot.join(Immunization.PERSON);
			immunizationPersonsSelect.fetch(Person.ADDRESS);
			immunizationPersonsQuery.select(immunizationPersonsSelect);
			Predicate immunizationPersonsFilter = immunizationService.createUserFilter(new ImmunizationQueryContext(cb, immunizationPersonsQuery, immunizationPersonsRoot));
			// date range
			if (date != null) {
				Predicate dateFilter = createChangeDateFilter(cb, immunizationPersonsSelect, DateHelper.toTimestampUpper(date), lastSynchronizedUuid);
				if (batchSize == null) {
					Predicate immunizationDateFilter =
						immunizationService.createChangeDateFilter(cb, immunizationPersonsRoot, DateHelper.toTimestampUpper(date));
					dateFilter = cb.or(dateFilter, immunizationDateFilter);
				}
				immunizationPersonsFilter = and(cb, immunizationPersonsFilter, dateFilter);
			}
			if (immunizationPersonsFilter != null) {
				immunizationPersonsQuery.where(immunizationPersonsFilter);
			}
			immunizationPersonsQuery.distinct(true);
			immunizationPersonsResultList = getBatchedQueryResults(cb, immunizationPersonsQuery, immunizationPersonsSelect, batchSize);
		}

		List<Person> travelEntryPersonsResultList = new ArrayList<>();
		// if a batch size is given, this is a sync from the mobile app where travel entries are not relevant for now
		if (batchSize == null) {
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
			travelEntryPersonsResultList = em.createQuery(tepQuery).getResultList();
		}

		return Stream
			.of(
				districtResultList,
				casePersonsResultList,
				contactPersonsResultList,
				eventPersonsResultList,
				immunizationPersonsResultList,
				travelEntryPersonsResultList)
			.flatMap(List<Person>::stream)
			.distinct()
			.sorted(new ChangeDateUuidComparator<>())
			.limit(batchSize == null ? Long.MAX_VALUE : batchSize)
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
			.of(
				personQueryContext.getQuery(),
				personQueryContext.getCriteriaBuilder(),
				(PersonJoins) personQueryContext.getJoins(),
				currentUser,
				!featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED))
			.inJurisdictionOrOwned();
	}

	public List<SimilarPersonDto> getSimilarPersonDtos(PersonSimilarityCriteria criteria, Integer limit) {

		setSimilarityThresholdQuery();
		boolean activeEntriesOnly = configFacade.isDuplicateChecksExcludePersonsOfArchivedEntries();

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Person> personQuery = cb.createQuery(Person.class);
		final Root<Person> personRoot = personQuery.from(Person.class);
		final PersonQueryContext personQueryContext = new PersonQueryContext(cb, personQuery, personRoot);
		final PersonJoins joins = personQueryContext.getJoins();

		Join<Person, Case> personCaseJoin = joins.getCaze();
		Join<Person, Contact> personContactJoin = joins.getContact();
		Join<Person, EventParticipant> personEventParticipantJoin = joins.getEventParticipant();
		Join<Person, Immunization> personImmunizationJoin = joins.getImmunization();
		Join<Person, TravelEntry> personTravelEntryJoin = joins.getTravelEntry();

		// Persons of active cases
		Predicate personSimilarityFilter = buildSimilarityCriteriaFilter(criteria, cb, personRoot);
		Predicate activeCasesFilter =
			activeEntriesOnly ? caseService.createActiveCasesFilter(cb, personCaseJoin) : caseService.createDefaultFilter(cb, personCaseJoin);
		Predicate caseUserFilter = caseService.createUserFilter(cb, personQuery, personCaseJoin);
		Predicate personCasePredicate = and(cb, personCaseJoin.get(Case.ID).isNotNull(), activeCasesFilter, caseUserFilter);

		// Persons of active contacts
		final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, personQuery, joins.getContactJoins());
		Predicate activeContactsFilter = activeEntriesOnly
			? contactService.createActiveContactsFilter(contactQueryContext)
			: contactService.createDefaultFilter(cb, personContactJoin);
		Predicate contactUserFilter = contactService.createUserFilter(contactQueryContext, null);
		Predicate personContactPredicate = and(cb, personContactJoin.get(Contact.ID).isNotNull(), contactUserFilter, activeContactsFilter);

		// Persons of event participants in active events
		final EventParticipantQueryContext eventParticipantQueryContext = new EventParticipantQueryContext(cb, personQuery, joins.getEventParticipantJoins());
		Predicate activeEventParticipantsFilter = activeEntriesOnly
			? eventParticipantService.createActiveEventParticipantsInActiveEventsFilter(eventParticipantQueryContext)
			: eventParticipantService.createDefaultInUndeletedEventsFilter(eventParticipantQueryContext);
		Predicate eventParticipantUserFilter =
			eventParticipantService.createUserFilter(eventParticipantQueryContext);
		Predicate personEventParticipantPredicate =
			and(cb, personEventParticipantJoin.get(EventParticipant.ID).isNotNull(), activeEventParticipantsFilter, eventParticipantUserFilter);

		// Persons of active immunizations
		Predicate personImmunizationPredicate = null;
		if (!featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			Predicate activeImmunizationsFilter = activeEntriesOnly
				? immunizationService.createActiveImmunizationsFilter(cb, personImmunizationJoin)
				: immunizationService.createDefaultFilter(cb, personImmunizationJoin);
			Predicate immunizationUserFilter = immunizationService.createUserFilter(new ImmunizationQueryContext(cb, personQuery, personImmunizationJoin));
			personImmunizationPredicate =
				and(cb, personImmunizationJoin.get(Immunization.ID).isNotNull(), immunizationUserFilter, activeImmunizationsFilter);
		}

		// Persons of active travel entries
		Predicate activeTravelEntriesFilter = activeEntriesOnly
			? travelEntryService.createActiveTravelEntriesFilter(cb, personTravelEntryJoin)
			: travelEntryService.createDefaultFilter(cb, personTravelEntryJoin);
		Predicate travelEntryUserFilter = travelEntryService.createUserFilter(new TravelEntryQueryContext(cb, personQuery, personTravelEntryJoin));
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

		TypedQuery<Person> query = em.createQuery(personQuery);
		if (limit != null) {
			query.setMaxResults(limit);
		}

		List<Person> persons = query.getResultList();
		List<Long> personsInJurisdiction = getInJurisdictionIDs(persons);
		return persons.stream().filter(p -> personsInJurisdiction.contains(p.getId())).map(this::toSimilarPersonDto).collect(Collectors.toList());
	}

	private SimilarPersonDto toSimilarPersonDto(Person entity) {

		Integer approximateAge = entity.getApproximateAge();
		ApproximateAgeType approximateAgeType = entity.getApproximateAgeType();
		if (entity.getBirthdateYYYY() != null) {
			DataHelper.Pair<Integer, ApproximateAgeType> pair = ApproximateAgeType.ApproximateAgeHelper
				.getApproximateAge(entity.getBirthdateYYYY(), entity.getBirthdateMM(), entity.getBirthdateDD(), entity.getDeathDate());
			approximateAge = pair.getElement0();
			approximateAgeType = pair.getElement1();
		}

		SimilarPersonDto similarPersonDto = new SimilarPersonDto();
		similarPersonDto.setUuid(entity.getUuid());
		similarPersonDto.setFirstName(entity.getFirstName());
		similarPersonDto.setLastName(entity.getLastName());
		similarPersonDto.setNickname(entity.getNickname());
		similarPersonDto.setAgeAndBirthDate(
			PersonHelper.getAgeAndBirthdateString(
				approximateAge,
				approximateAgeType,
				entity.getBirthdateDD(),
				entity.getBirthdateMM(),
				entity.getBirthdateYYYY()));
		similarPersonDto.setSex(entity.getSex());
		similarPersonDto.setPresentCondition(entity.getPresentCondition());
		similarPersonDto.setPhone(entity.getPhone());
		similarPersonDto.setDistrictName(entity.getAddress().getDistrict() != null ? entity.getAddress().getDistrict().getName() : null);
		similarPersonDto.setCommunityName(entity.getAddress().getCommunity() != null ? entity.getAddress().getCommunity().getName() : null);
		similarPersonDto.setPostalCode(entity.getAddress().getPostalCode());
		similarPersonDto.setCity(entity.getAddress().getCity());
		similarPersonDto.setStreet(entity.getAddress().getStreet());
		similarPersonDto.setHouseNumber(entity.getAddress().getHouseNumber());
		similarPersonDto.setNationalHealthId(entity.getNationalHealthId());
		similarPersonDto.setPassportNumber(entity.getPassportNumber());

		return similarPersonDto;
	}

	private void setSimilarityThresholdQuery() {
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
		} else if (!StringUtils.isBlank(criteria.getFirstName())) {
			filter = and(
				cb,
				filter,
				cb.isTrue(cb.function(SIMILARITY_OPERATOR, boolean.class, personFrom.get(Person.FIRST_NAME), cb.literal(criteria.getFirstName()))));
		} else if (!StringUtils.isBlank(criteria.getLastName())) {
			filter = and(
				cb,
				filter,
				cb.isTrue(cb.function(SIMILARITY_OPERATOR, boolean.class, personFrom.get(Person.LAST_NAME), cb.literal(criteria.getLastName()))));
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
		return createChangeDateFilter(cb, from, date, null);
	}

	private Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Person> persons, Timestamp date, String lastSynchronizedUuid) {
		Join<Person, Location> address = persons.join(Person.ADDRESS);

		// TODO #7303: Include change date of addresses, personContactDetails?
		ChangeDateFilterBuilder changeDateFilterBuilder = lastSynchronizedUuid == null
			? new ChangeDateFilterBuilder(cb, date)
			: new ChangeDateFilterBuilder(cb, date, persons, lastSynchronizedUuid);
		return changeDateFilterBuilder.add(persons).add(address).build();
	}

	private Predicate createChangeDateFilter(PersonQueryContext personQueryContext, Timestamp date, String lastSynchronizedUuid) {
		final From<?, Person> persons = personQueryContext.getRoot();
		final CriteriaBuilder cb = personQueryContext.getCriteriaBuilder();
		final PersonJoins joins = personQueryContext.getJoins();
		final Join<Person, Location> address = joins.getAddress();

		ChangeDateFilterBuilder changeDateFilterBuilder = lastSynchronizedUuid == null
			? new ChangeDateFilterBuilder(cb, date)
			: new ChangeDateFilterBuilder(cb, date, persons, lastSynchronizedUuid);
		return changeDateFilterBuilder.add(persons).add(address).build();
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

	public Long getIdByUuid(@NotNull String uuid) {

		if (uuid == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> from = cq.from(Person.class);
		cq.select(from.get(AbstractDomainObject.ID));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));

		TypedQuery<Long> q = em.createQuery(cq).setParameter(uuidParam, uuid);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public List<Person> getByExternalIds(List<String> externalIds) {

		List<Person> persons = new LinkedList<>();
		IterableHelper.executeBatched(externalIds, ModelConstants.PARAMETER_LIMIT, batchedPersonUuids -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Person> cq = cb.createQuery(getElementClass());
			Root<Person> from = cq.from(getElementClass());

			cq.where(from.get(Person.EXTERNAL_ID).in(externalIds));

			persons.addAll(em.createQuery(cq).getResultList());
		});
		return persons;
	}

	public void deleteUnreferencedPersons(int batchSize) {
		IterableHelper.executeBatched(getAllNonReferencedPersonUuids(), batchSize, batchedUuids -> deletePermanent(batchedUuids));
	}

	private List<String> getAllNonReferencedPersonUuids() {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<String> cq = cb.createQuery(String.class);
		final Root<Person> personRoot = cq.from(getElementClass());

		final Subquery<String> caseSubquery = createSubquery(cb, cq, personRoot, Case.class, Case.PERSON);
		final Subquery<String> contactSubquery = createSubquery(cb, cq, personRoot, Contact.class, Contact.PERSON);
		final Subquery<String> eventParticipantSubquery = createSubquery(cb, cq, personRoot, EventParticipant.class, EventParticipant.PERSON);
		final Subquery<String> immunizationSubquery = createSubquery(cb, cq, personRoot, Immunization.class, Immunization.PERSON);
		final Subquery<String> travelEntrySubquery = createSubquery(cb, cq, personRoot, TravelEntry.class, TravelEntry.PERSON);

		cq.where(
			cb.and(
				cb.not(cb.exists(caseSubquery)),
				cb.not(cb.exists(contactSubquery)),
				cb.not(cb.exists(eventParticipantSubquery)),
				cb.not(cb.exists(immunizationSubquery)),
				cb.not(cb.exists(travelEntrySubquery))));

		cq.select(personRoot.get(Person.UUID));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	private Subquery<String> createSubquery(
		CriteriaBuilder cb,
		CriteriaQuery<String> cq,
		Root<Person> personRoot,
		Class<? extends CoreAdo> subqueryClass,
		String personField) {
		final Subquery<String> subquery = cq.subquery(String.class);
		final Root<? extends CoreAdo> from = subquery.from(subqueryClass);
		subquery.where(cb.equal(from.get(personField), personRoot));
		subquery.select(from.get(AbstractDomainObject.UUID));
		return subquery;
	}

	@Override
	public void deletePermanent(Person person) {
		manualMessageLogService.getByPersonUuid(person.getUuid())
			.forEach(manualMessageLog -> manualMessageLogService.deletePermanent(manualMessageLog));

		super.deletePermanent(person);
	}
}
