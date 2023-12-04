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
package de.symeda.sormas.backend.person;

import static de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect.SIMILARITY_OPERATOR;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.and;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEquals;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andInValues;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.RequestContextHolder;
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
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseJoins;
import de.symeda.sormas.backend.caze.CaseQueryContext;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.CaseUserFilterCriteria;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoAttributes;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.ChangeDateUuidComparator;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.FilterProvider;
import de.symeda.sormas.backend.manualmessagelog.ManualMessageLogService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactJoins;
import de.symeda.sormas.backend.contact.ContactQueryContext;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantJoins;
import de.symeda.sormas.backend.event.EventParticipantQueryContext;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventUserFilterCriteria;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.geocoding.GeocodingService;
import de.symeda.sormas.backend.immunization.ImmunizationJoins;
import de.symeda.sormas.backend.immunization.ImmunizationQueryContext;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.travelentry.TravelEntry;
import de.symeda.sormas.backend.travelentry.TravelEntryJoins;
import de.symeda.sormas.backend.travelentry.TravelEntryQueryContext;
import de.symeda.sormas.backend.travelentry.services.TravelEntryService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.ExternalDataUtil;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless
@LocalBean
public class PersonService extends AdoServiceWithUserFilterAndJurisdiction<Person> {

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
	@EJB
	private VisitService visitService;

	public PersonService() {
		super(Person.class);
	}

	public Person createPerson() {
		return new Person();
	}

	public Set<PersonAssociation> getPermittedAssociations() {

		return new LinkedHashSet<>(Arrays.stream(PersonAssociation.values()).filter(e -> isPermittedAssociation(e)).collect(Collectors.toList()));
	}

	public boolean isPermittedAssociation(@NotNull PersonAssociation association) {

		final boolean allowed;
		switch (association) {
		case ALL:
			allowed = isPermitted(FeatureType.PERSON_MANAGEMENT, UserRight.PERSON_VIEW);
			break;
		case CASE:
			allowed = isPermitted(FeatureType.CASE_SURVEILANCE, UserRight.CASE_VIEW);
			break;
		case CONTACT:
			allowed = isPermitted(FeatureType.CONTACT_TRACING, UserRight.CONTACT_VIEW);
			break;
		case EVENT_PARTICIPANT:
			allowed = isPermitted(FeatureType.EVENT_SURVEILLANCE, UserRight.EVENT_VIEW);
			break;
		case IMMUNIZATION:
			allowed = isPermitted(FeatureType.IMMUNIZATION_MANAGEMENT, UserRight.IMMUNIZATION_VIEW)
				&& !featureConfigurationFacade.isPropertyValueTrue(FeatureType.IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED);
			break;
		case TRAVEL_ENTRY:
			allowed = isPermitted(FeatureType.TRAVEL_ENTRIES, UserRight.TRAVEL_ENTRY_MANAGEMENT_ACCESS)
				&& configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY);
			break;
		default:
			throw new IllegalArgumentException("Unexpected association: " + association.name());
		}

		return allowed;
	}

	private boolean isPermitted(FeatureType featureType, UserRight userRight) {

		return getCurrentUser().hasUserRight(userRight) && featureConfigurationFacade.isFeatureEnabled(featureType);
	}

	@Override
	public List<String> getAllUuids() {

		Set<String> personUuids = new LinkedHashSet<>();
		for (PersonAssociation personAssociation : getPermittedAssociations()) {
			switch (personAssociation) {
			case CASE:
				personUuids.addAll(
					getAllUuids(Case.class, Case.PERSON, (b, q, f) -> caseService.createUserFilter(new CaseQueryContext(b, q, new CaseJoins(f)))));
				break;
			case CONTACT:
				personUuids.addAll(
					getAllUuids(
						Contact.class,
						Contact.PERSON,
						(b, q, f) -> contactService.createUserFilter(new ContactQueryContext(b, q, new ContactJoins(f)))));
				break;
			case EVENT_PARTICIPANT:
				personUuids.addAll(
					getAllUuids(
						EventParticipant.class,
						EventParticipant.PERSON,
						(b, q, f) -> eventParticipantService.createUserFilter(new EventParticipantQueryContext(b, q, new EventParticipantJoins(f)))));
				break;
			case IMMUNIZATION:
				personUuids.addAll(
					getAllUuids(
						Immunization.class,
						Immunization.PERSON,
						(b, q, f) -> immunizationService.createUserFilter(new ImmunizationQueryContext(b, q, new ImmunizationJoins(f)))));
				break;
			case TRAVEL_ENTRY:
				// exluded for sync from the mobile app because they relevant for now
				if (!RequestContextHolder.isMobileSync()) {
					personUuids.addAll(
						getAllUuids(
							TravelEntry.class,
							TravelEntry.PERSON,
							(b, q, f) -> travelEntryService.createUserFilter(new TravelEntryQueryContext(b, q, new TravelEntryJoins(f)))));
				}
				break;
			case ALL:
				// NOOP: Persons need to be identified by permitted explicit associations
				break;
			default:
				throw new IllegalArgumentException(personAssociation.toString());
			}
		}

		return new ArrayList<>(personUuids);
	}

	private <R extends AbstractDomainObject> List<String> getAllUuids(
		Class<R> referenceClass,
		String personAttributeName,
		FilterProvider<R> referenceUserFilterProvider) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> query = cb.createQuery(String.class);
		Root<R> referenceRoot = query.from(referenceClass);
		Join<R, Person> personJoin = referenceRoot.join(personAttributeName);
		query.select(personJoin.get(Person.UUID));

		Predicate casePersonsFilter = referenceUserFilterProvider.provide(cb, query, referenceRoot);
		if (casePersonsFilter != null) {
			query.where(casePersonsFilter);
		}
		query.distinct(true);

		return em.createQuery(query).getResultList();
	}

	@Override
	@Deprecated
	@SuppressWarnings("rawtypes")
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Person> from) {
		throw new UnsupportedOperationException("Should not be called -> obsolete!");
	}

	public Predicate createUserFilter(PersonQueryContext queryContext, PersonCriteria personCriteria) {

		/*
		 * Creates a combined filter only if no filtering on a specific association is given.
		 */
		final Predicate userFilter;
		final PersonAssociation personAssociation =
			Optional.ofNullable(personCriteria).map(e -> e.getPersonAssociation()).orElse(PersonCriteria.DEFAULT_ASSOCIATION);
		switch (personAssociation) {
		case ALL:
			// Combine all associations that are permitted
			userFilter = CriteriaBuilderHelper.or(
				queryContext.getCriteriaBuilder(),
				createAssociationPredicate(queryContext, PersonAssociation.CASE),
				createAssociationPredicate(queryContext, PersonAssociation.CONTACT),
				createAssociationPredicate(queryContext, PersonAssociation.EVENT_PARTICIPANT),
				createAssociationPredicate(queryContext, PersonAssociation.IMMUNIZATION),
				createAssociationPredicate(queryContext, PersonAssociation.TRAVEL_ENTRY));
			break;
		case CASE:
		case CONTACT:
		case EVENT_PARTICIPANT:
		case IMMUNIZATION:
		case TRAVEL_ENTRY:
			userFilter = createAssociationPredicate(queryContext, personAssociation);
			break;
		default:
			throw new IllegalArgumentException(personAssociation.toString());
		}

		if (userFilter == null) {
			logger.debug("No userFilter compiled for persons by association {}, fallback to empty collection", personAssociation.name());
			return queryContext.getCriteriaBuilder().disjunction();
		}

		return userFilter;
	}

	/**
	 * @return {@code null}, if the association is not permitted to query,
	 *         otherwise an appropriate {@link Predicate} for the given {@link PersonAssociation}.
	 * @see #isPermittedAssociation(PersonAssociation)
	 */
	private Predicate createAssociationPredicate(@NotNull PersonQueryContext queryContext, @NotNull PersonAssociation personAssociation) {

		if (!isPermittedAssociation(personAssociation)) {
			// association not permitted: cancel rest of logic
			return null;
		}

		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final CriteriaQuery<?> cq = queryContext.getQuery();
		final PersonJoins joins = queryContext.getJoins();
		final Predicate associationPredicate;
		switch (personAssociation) {
		case CASE:
			associationPredicate = CriteriaBuilderHelper.and(
				cb,
				caseService.createUserFilter(new CaseQueryContext(cb, cq, joins.getCaseJoins()), new CaseUserFilterCriteria()),
				caseService.createDefaultFilter(cb, joins.getCaze()));
			break;
		case CONTACT:
			associationPredicate = CriteriaBuilderHelper.and(
				cb,
				contactService.createUserFilter(
					new ContactQueryContext(cb, cq, joins.getContactJoins()),
					new ContactCriteria().includeContactsFromOtherJurisdictions(false)),
				contactService.createDefaultFilter(cb, joins.getContact()));
			break;
		case EVENT_PARTICIPANT:
			associationPredicate = CriteriaBuilderHelper.and(
				cb,
				eventParticipantService.createUserFilter(
					new EventParticipantQueryContext(cb, cq, joins.getEventParticipantJoins()),
					new EventUserFilterCriteria().includeUserCaseAndEventParticipantFilter(false).forceRegionJurisdiction(true)),
				eventParticipantService.createDefaultFilter(cb, joins.getEventParticipant()));
			break;
		case IMMUNIZATION:
			associationPredicate = CriteriaBuilderHelper.and(
				cb,
				immunizationService.createUserFilter(new ImmunizationQueryContext(cb, cq, joins.getImmunizationJoins())),
				immunizationService.createDefaultFilter(cb, joins.getImmunization()));
			break;
		case TRAVEL_ENTRY:
			associationPredicate = CriteriaBuilderHelper.and(
				cb,
				travelEntryService.createUserFilter(new TravelEntryQueryContext(cb, cq, joins.getTravelEntryJoins())),
				travelEntryService.createDefaultFilter(cb, joins.getTravelEntry()));
			break;
		case ALL:
		default:
			throw new IllegalArgumentException(personAssociation.toString());
		}

		return associationPredicate;
	}

	public Predicate buildCriteriaFilter(PersonCriteria personCriteria, PersonQueryContext personQueryContext) {

		// Hint: personCriteria.getPersonAssociation() is interpreted in createUserFilter, but not again here

		final CriteriaBuilder cb = personQueryContext.getCriteriaBuilder();
		final From<?, Person> personFrom = personQueryContext.getRoot();

		final PersonJoins personJoins = personQueryContext.getJoins();
		final Join<Person, Location> location = personJoins.getAddress();

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
					CriteriaBuilderHelper
						.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PRIMARY_OTHER_SUBQUERY), textFilter),
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
		filter = andEquals(cb, () -> personJoins.getAddressJoins().getRegion(), filter, personCriteria.getRegion());
		filter = andEquals(cb, () -> personJoins.getAddressJoins().getDistrict(), filter, personCriteria.getDistrict());
		filter = andEquals(cb, () -> personJoins.getAddressJoins().getCommunity(), filter, personCriteria.getCommunity());
		filter = andInValues(personCriteria.getUuids(), filter, cb, personFrom.get(Person.UUID));

		return filter;
	}

	@Override
	public List<Person> getAllAfter(Date date, Integer batchSize, String lastSynchronizedUuid) {

		long startTime = DateHelper.startTime();
		logger.trace("getAllAfter started...");

		// 1. Get attributes by permitted references
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		Set<AdoAttributes> personAttributes = new LinkedHashSet<>();
		for (PersonAssociation personAssociation : getPermittedAssociations()) {
			switch (personAssociation) {
			case CASE:
				personAttributes.addAll(
					getAllAfter(
						cb,
						Case.class,
						Case.PERSON,
						(b, q, f) -> caseService.createUserFilter(new CaseQueryContext(b, q, new CaseJoins(f))),
						(f, d) -> caseService.createChangeDateFilter(cb, f, d),
						date,
						batchSize,
						lastSynchronizedUuid));
				break;
			case CONTACT:
				personAttributes.addAll(
					getAllAfter(
						cb,
						Contact.class,
						Contact.PERSON,
						(b, q, f) -> contactService.createUserFilter(new ContactQueryContext(b, q, new ContactJoins(f))),
						(f, d) -> contactService.createChangeDateFilter(cb, f, d),
						date,
						batchSize,
						lastSynchronizedUuid));
				break;
			case EVENT_PARTICIPANT:
				personAttributes.addAll(
					getAllAfter(
						cb,
						EventParticipant.class,
						EventParticipant.PERSON,
						(b, q, f) -> eventParticipantService.createUserFilter(new EventParticipantQueryContext(b, q, new EventParticipantJoins(f))),
						(f, d) -> eventParticipantService.createChangeDateFilter(cb, f, DateHelper.toTimestampUpper(d)),
						date,
						batchSize,
						lastSynchronizedUuid));
				break;
			case IMMUNIZATION:
				personAttributes.addAll(
					getAllAfter(
						cb,
						Immunization.class,
						Immunization.PERSON,
						(b, q, f) -> immunizationService.createUserFilter(new ImmunizationQueryContext(b, q, new ImmunizationJoins(f))),
						(f, d) -> immunizationService.createChangeDateFilter(cb, f, DateHelper.toTimestampUpper(d)),
						date,
						batchSize,
						lastSynchronizedUuid));
				break;
			case TRAVEL_ENTRY:
				// exluded for sync from the mobile app because they relevant for now
				if (!RequestContextHolder.isMobileSync()) {
					personAttributes.addAll(
						getAllAfter(
							cb,
							TravelEntry.class,
							TravelEntry.PERSON,
							(b, q, f) -> travelEntryService.createUserFilter(new TravelEntryQueryContext(b, q, new TravelEntryJoins(f))),
							(f, d) -> travelEntryService.createChangeDateFilter(cb, f, DateHelper.toTimestampUpper(d)),
							date,
							batchSize,
							lastSynchronizedUuid));
				}
				break;
			case ALL:
				// NOOP: Persons need to be identified by permitted explicit associations
				break;
			default:
				throw new IllegalArgumentException(personAssociation.toString());
			}
		}

		// 2. Unique personIds sorted and limited by the given batch size
		List<Long> personIds = personAttributes.stream()
			.sorted(new ChangeDateUuidComparator())
			.map(e -> e.getId())
			.limit(batchSize == null ? Long.MAX_VALUE : batchSize)
			.collect(Collectors.toList());
		logger.trace(
			"getAllAfter: Unique personIds identified. batchSize:{}, personAttributes:{}, personIds:{}, {} ms",
			batchSize,
			personAttributes.size(),
			personIds.size(),
			DateHelper.durationMillies(startTime));

		// 3. Fetch Person entities by id
		List<Person> persons = getByIds(personIds);
		logger.trace("getAllAfter finished. Fetched persons:{}, {} ms", persons.size(), DateHelper.durationMillies(startTime));
		return persons;
	}

	/**
	 * Find persons by reference.
	 * 
	 * @param <R>
	 *            Type of {@code referenceClass}.
	 * @param cb
	 *            builder to use for the query.
	 * @param referenceClass
	 *            The entity that is referencing the person.
	 * @param personAttributeName
	 *            Name of person attribute in {@code referenceClass}.
	 * @param referenceUserFilterProvider
	 *            Identify the references that are allowed for the user.
	 * @param referenceChangeDateFilterFunction
	 *            Identify the references that changed since {@code timestamp}.
	 * @param timestamp
	 *            Find persons changed after this point in time.
	 * @param batchSize
	 *            Number of persons to fetch.
	 * @param lastSynchronizedUuid
	 *            This entity identified by this {@code uuid} was the last one from that recent batch.
	 */
	private <R extends AbstractDomainObject> List<AdoAttributes> getAllAfter(
		CriteriaBuilder cb,
		Class<R> referenceClass,
		String personAttributeName,
		FilterProvider<R> referenceUserFilterProvider,
		BiFunction<From<?, R>, Date, Predicate> referenceChangeDateFilterFunction,
		Date timestamp,
		Integer batchSize,
		String lastSynchronizedUuid) {

		long startTime = DateHelper.startTime();
		CriteriaQuery<AdoAttributes> cq = cb.createQuery(AdoAttributes.class);
		Root<R> referenceRoot = cq.from(referenceClass);
		Join<Person, Person> personJoin = referenceRoot.join(personAttributeName);

		// SELECT
		cq.multiselect(personJoin.get(AdoAttributes.ID), personJoin.get(AdoAttributes.UUID), personJoin.get(AdoAttributes.CHANGE_DATE));

		// FILTER
		Predicate filter = referenceUserFilterProvider.provide(cb, cq, referenceRoot);
		if (timestamp != null) {
			Predicate dateFilter = createChangeDateFilter(cb, personJoin, DateHelper.toTimestampUpper(timestamp), lastSynchronizedUuid);
			// Don't fetch persons by reference changes for mobile app. If a person is missing, it lazily fetches that missing person.
			if (!RequestContextHolder.isMobileSync()) {
				dateFilter = cb.or(dateFilter, referenceChangeDateFilterFunction.apply(referenceRoot, DateHelper.toTimestampUpper(timestamp)));
			}
			filter = and(cb, filter, dateFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);

		List<AdoAttributes> result = getBatchedAttributesQueryResults(cb, cq, personJoin, batchSize);
		logger.trace(
			"getAllAfter: Fetched personIds for {} reference. n: {}, {} ms",
			referenceClass.getSimpleName(),
			result.size(),
			DateHelper.durationMillies(startTime));
		return result;
	}

	@Override
	public List<Long> getInJurisdictionIds(List<Person> entities) {
		return getIdList(entities, (cb, cq, from) -> inJurisdictionOrOwned(new PersonQueryContext(cb, cq, from)));
	}

	@Override
	public boolean inJurisdictionOrOwned(Person entity) {
		return fulfillsCondition(entity, (cb, cq, from) -> inJurisdictionOrOwned(new PersonQueryContext(cb, cq, from)));
	}

	public Predicate inJurisdictionOrOwned(PersonQueryContext personQueryContext) {

		final User currentUser = userService.getCurrentUser();
		return PersonJurisdictionPredicateValidator
			.of(
				personQueryContext.getQuery(),
				personQueryContext.getCriteriaBuilder(),
				personQueryContext.getJoins(),
				currentUser,
				getPermittedAssociations())
			.inJurisdictionOrOwned();
	}

	public boolean isPersonSimilar(PersonSimilarityCriteria criteria, String personUuid) {
		if (personUuid == null) {
			return false;
		}

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Person> from = cq.from(Person.class);

		final PersonQueryContext personQueryContext = new PersonQueryContext(cb, cq, from);
		final PersonJoins joins = personQueryContext.getJoins();

		Predicate personSimilarityFilter = buildSimilarityCriteriaFilter(criteria, cb, from);

		cq.select(cb.count(from.get(AbstractDomainObject.ID)));

		Predicate predicate = cb.or(
			cb.isFalse(joins.getCaze().get(Case.DELETED)),
			cb.isFalse(joins.getContact().get(Contact.DELETED)),
			cb.isFalse(joins.getTravelEntry().get(TravelEntry.DELETED)),
			cb.isFalse(joins.getImmunization().get(Immunization.DELETED)),
			cb.isFalse(joins.getEventParticipant().get(EventParticipant.DELETED)));
		predicate = cb.and(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam), predicate, personSimilarityFilter);

		cq.where(predicate);

		TypedQuery<Long> q = em.createQuery(cq).setParameter(uuidParam, personUuid);
		return q.getSingleResult() > 0;
	}

	public List<SimilarPersonDto> getSimilarPersonDtos(Integer limit, PersonSimilarityCriteria criteria) {

		setSimilarityThresholdQuery();
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Person> personQuery = cb.createQuery(Person.class);
		final Root<Person> personRoot = personQuery.from(Person.class);
		final PersonQueryContext queryContext = new PersonQueryContext(cb, personQuery, personRoot);

		// Find similar persons by permitted associations, optionally limited to active entries
		Predicate personSimilarityFilter = buildSimilarityCriteriaFilter(criteria, cb, personRoot);

		Predicate associationFilter = buildAssociationFilter(queryContext, configFacade.isDuplicateChecksExcludePersonsOfArchivedEntries());
		personQuery.where(and(cb, personSimilarityFilter, associationFilter));
		personQuery.distinct(true);

		TypedQuery<Person> query = em.createQuery(personQuery);
		if (limit != null) {
			query.setMaxResults(limit);
		}

		List<Person> persons = query.getResultList();
		List<Long> personsInJurisdiction = getInJurisdictionIds(persons);
		return persons.stream().filter(p -> personsInJurisdiction.contains(p.getId())).map(this::toSimilarPersonDto).collect(Collectors.toList());
	}

	private Predicate buildAssociationFilter(PersonQueryContext queryContext, boolean activeEntriesOnly) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> personQuery = queryContext.getQuery();
		PersonJoins joins = queryContext.getJoins();

		Set<PersonAssociation> permittedAssociations = getPermittedAssociations();
		List<Predicate> associationPredicates = new ArrayList<>();
		for (PersonAssociation personAssociation : permittedAssociations) {
			switch (personAssociation) {
			case CASE:
				Join<Person, Case> personCaseJoin = joins.getCaze();
				Predicate activeCasesFilter =
					activeEntriesOnly ? caseService.createActiveCasesFilter(cb, personCaseJoin) : caseService.createDefaultFilter(cb, personCaseJoin);
				Predicate caseUserFilter = caseService.createUserFilter(new CaseQueryContext(cb, personQuery, joins.getCaseJoins()));
				associationPredicates.add(and(cb, personCaseJoin.get(Case.ID).isNotNull(), activeCasesFilter, caseUserFilter));
				break;
			case CONTACT:
				Join<Person, Contact> personContactJoin = joins.getContact();
				final ContactQueryContext contactQueryContext = new ContactQueryContext(cb, personQuery, joins.getContactJoins());
				Predicate activeContactsFilter = activeEntriesOnly
					? contactService.createActiveContactsFilter(contactQueryContext)
					: contactService.createDefaultFilter(cb, personContactJoin);
				Predicate contactUserFilter = contactService.createUserFilter(contactQueryContext, null);
				associationPredicates.add(and(cb, personContactJoin.get(Contact.ID).isNotNull(), contactUserFilter, activeContactsFilter));
				break;
			case EVENT_PARTICIPANT:
				Join<Person, EventParticipant> personEventParticipantJoin = joins.getEventParticipant();
				final EventParticipantQueryContext eventParticipantQueryContext =
					new EventParticipantQueryContext(cb, personQuery, joins.getEventParticipantJoins());
				Predicate activeEventParticipantsFilter = activeEntriesOnly
					? eventParticipantService.createActiveEventParticipantsInActiveEventsFilter(eventParticipantQueryContext)
					: eventParticipantService.createDefaultInRestoredEventsFilter(eventParticipantQueryContext);
				Predicate eventParticipantUserFilter = eventParticipantService.createUserFilter(eventParticipantQueryContext);
				associationPredicates.add(
					and(
						cb,
						personEventParticipantJoin.get(EventParticipant.ID).isNotNull(),
						activeEventParticipantsFilter,
						eventParticipantUserFilter));
				break;
			case IMMUNIZATION:
				Join<Person, Immunization> personImmunizationJoin = joins.getImmunization();
				Predicate activeImmunizationsFilter = activeEntriesOnly
					? immunizationService.createActiveImmunizationsFilter(cb, personImmunizationJoin)
					: immunizationService.createDefaultFilter(cb, personImmunizationJoin);
				Predicate immunizationUserFilter =
					immunizationService.createUserFilter(new ImmunizationQueryContext(cb, personQuery, joins.getImmunizationJoins()));
				associationPredicates
					.add(and(cb, personImmunizationJoin.get(Immunization.ID).isNotNull(), immunizationUserFilter, activeImmunizationsFilter));
				break;
			case TRAVEL_ENTRY:
				Join<Person, TravelEntry> personTravelEntryJoin = joins.getTravelEntry();
				Predicate activeTravelEntriesFilter = activeEntriesOnly
					? travelEntryService.createActiveTravelEntriesFilter(cb, personTravelEntryJoin)
					: travelEntryService.createDefaultFilter(cb, personTravelEntryJoin);
				Predicate travelEntryUserFilter =
					travelEntryService.createUserFilter(new TravelEntryQueryContext(cb, personQuery, joins.getTravelEntryJoins()));
				associationPredicates
					.add(and(cb, personTravelEntryJoin.get(TravelEntry.ID).isNotNull(), travelEntryUserFilter, activeTravelEntriesFilter));
				break;
			case ALL:
				// NOOP: Persons need to be identified by permitted explicit associations
				break;
			default:
				throw new IllegalArgumentException(personAssociation.toString());
			}
		}

		if (associationPredicates.isEmpty()) {
			throw new IllegalArgumentException("No filter compiled for persons by associations: " + permittedAssociations);
		}

		return CriteriaBuilderHelper.or(cb, associationPredicates.toArray(new Predicate[associationPredicates.size()]));
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

		SimilarPersonDto similarPersonDto = new SimilarPersonDto(entity.getUuid());
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
		Predicate casePersonsFilter = caseService.createUserFilter(new CaseQueryContext(cb, casePersonsQuery, new CaseJoins(casePersonsRoot)));

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
			final Predicate yearEquals = cb.equal(personFrom.get(Person.BIRTHDATE_YYYY), criteria.getBirthdateYYYY());
			filter = and(cb, filter, cb.or(cb.isNull(personFrom.get(Person.BIRTHDATE_YYYY)), yearEquals));
		}
		if (criteria.getBirthdateMM() != null) {
			final Predicate monthEquals = cb.equal(personFrom.get(Person.BIRTHDATE_MM), criteria.getBirthdateMM());
			filter = and(cb, filter, cb.or(cb.isNull(personFrom.get(Person.BIRTHDATE_MM)), monthEquals));
		}
		if (criteria.getBirthdateDD() != null) {
			final Predicate dayEquals = cb.equal(personFrom.get(Person.BIRTHDATE_DD), criteria.getBirthdateDD());
			filter = and(cb, filter, cb.or(cb.isNull(personFrom.get(Person.BIRTHDATE_DD)), dayEquals));
		}
		if (!StringUtils.isBlank(criteria.getNationalHealthId())) {
			final Predicate nationalEqual = cb.equal(personFrom.get(Person.NATIONAL_HEALTH_ID), criteria.getNationalHealthId());
			filter = and(cb, filter, cb.or(cb.isNull(personFrom.get(Person.NATIONAL_HEALTH_ID)), nationalEqual));
		}
		if (!StringUtils.isBlank(criteria.getPassportNumber())) {
			filter = CriteriaBuilderHelper.or(cb, filter, cb.equal(personFrom.get(Person.PASSPORT_NUMBER), criteria.getPassportNumber()));
		}

		String uuidExternalIdExternalTokenLike = criteria.getNameUuidExternalIdExternalTokenLike();
		if (!StringUtils.isBlank(uuidExternalIdExternalTokenLike)) {

			String[] textFilters = uuidExternalIdExternalTokenLike.split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				boolean strictNameComparison = criteria.getStrictNameComparison() != null ? criteria.getStrictNameComparison() : false;
				Predicate likeFilters = CriteriaBuilderHelper.or(
					cb,
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, personFrom.get(Person.EXTERNAL_TOKEN), textFilter),
					!strictNameComparison ? CriteriaBuilderHelper.unaccentedIlike(cb, personFrom.get(Person.FIRST_NAME), textFilter) : null,
					!strictNameComparison ? CriteriaBuilderHelper.unaccentedIlike(cb, personFrom.get(Person.LAST_NAME), textFilter) : null,
					!strictNameComparison
						? cb.isTrue(cb.function(SIMILARITY_OPERATOR, boolean.class, personFrom.get(Person.FIRST_NAME), cb.literal(textFilter)))
						: null,
					!strictNameComparison
						? cb.isTrue(cb.function(SIMILARITY_OPERATOR, boolean.class, personFrom.get(Person.LAST_NAME), cb.literal(textFilter)))
						: null);

				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
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
		IterableHelper.executeBatched(externalIds, ModelConstants.PARAMETER_LIMIT, batchedExternalIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Person> cq = cb.createQuery(getElementClass());
			Root<Person> from = cq.from(getElementClass());

			cq.where(from.get(Person.EXTERNAL_ID).in(batchedExternalIds));

			persons.addAll(em.createQuery(cq).getResultList());
		});
		return persons;
	}

	public List<String> getAllNonReferencedPersonUuids() {

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
		visitService.deletePersonVisits(Collections.singletonList(person.getUuid()));

		super.deletePermanent(person);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deletePermanentByUuids(List<String> uuids) {

		visitService.deletePersonVisits(uuids);
		super.deletePermanentByUuids(uuids);
	}

	public boolean isEditAllowed(String personUuid) {
		if (personUuid == null) {
			return false;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Person> from = cq.from(Person.class);

		cq.select(from.get(Person.ID));

		PersonJoins joins = new PersonJoins(from);

		Subquery<Long> travelEntrySubQuery = cq.subquery(Long.class);
		Root<TravelEntry> travelEntryFrom = travelEntrySubQuery.from(TravelEntry.class);
		travelEntrySubQuery.select(travelEntryFrom.get(TravelEntry.PERSON))
			.where(
				cb.equal(travelEntryFrom.join(TravelEntry.PERSON, JoinType.LEFT).get(Person.ID), from.get(Person.ID)),
				cb.isFalse(travelEntryFrom.get(TravelEntry.DELETED)));

		cq.where(
			cb.equal(from.get(Person.UUID), personUuid),
			cb.or(
				cb.and(
					cb.and(cb.isNotNull(joins.getCaze()), cb.isFalse(joins.getCaze().get(Case.DELETED))),
					caseService.createOwnershipPredicate(true, joins.getCaze(), cb, cq)),
				cb.and(
					cb.and(cb.isNotNull(joins.getContact()), cb.isFalse(joins.getContact().get(Contact.DELETED))),
					contactService.createOwnershipPredicate(true, joins.getContact(), cb, cq)),
				cb.and(
					cb.and(cb.isNotNull(joins.getEventParticipant()), cb.isFalse(joins.getEventParticipant().get(EventParticipant.DELETED))),
					eventParticipantService.createOwnershipPredicate(true, joins.getEventParticipant(), cb, cq)),
				cb.and(
					cb.and(cb.isNotNull(joins.getImmunization()), cb.isFalse(joins.getImmunization().get(Immunization.DELETED))),
					immunizationService.createOwnershipPredicate(true, joins.getImmunization(), cb, cq)),
				cb.exists(travelEntrySubQuery)));

		return !em.createQuery(cq).getResultList().isEmpty();
	}
}
