/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.adverseeventsfollowingimmunization;

import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiDateType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiExportDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AdverseEvents;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.Aefi;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiJoins;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers.AefiIndexDtoResultTransformer;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers.AefiListEntryDtoResultTransformer;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.country.Country;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonJoins;
import de.symeda.sormas.backend.person.PersonJurisdictionPredicateValidator;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;
import de.symeda.sormas.backend.vaccination.Vaccination;

@Stateless
@LocalBean
public class AefiService extends AbstractCoreAdoService<Aefi, AefiJoins> {

	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public AefiService() {
		super(Aefi.class, DeletableEntityType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION);
	}

	public Long getIdByUuid(@NotNull String uuid) {

		if (uuid == null) {
			return null;
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		ParameterExpression<String> uuidParam = cb.parameter(String.class, AbstractDomainObject.UUID);
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Aefi> from = cq.from(Aefi.class);
		cq.select(from.get(AbstractDomainObject.ID));
		cq.where(cb.equal(from.get(AbstractDomainObject.UUID), uuidParam));

		TypedQuery<Long> q = em.createQuery(cq).setParameter(uuidParam, uuid);

		return q.getResultList().stream().findFirst().orElse(null);
	}

	public List<AefiListEntryDto> getEntriesList(Long immunizationId, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<Aefi> aefi = cq.from(Aefi.class);

		AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefi);
		AefiJoins joins = aefiQueryContext.getJoins();

		Join<Aefi, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();
		Join<Aefi, AdverseEvents> adverseEvents = joins.getAdverseEvents();

		cq.multiselect(
			aefi.get(Aefi.UUID),
			aefi.get(Aefi.SERIOUS),
			primarySuspectVaccine.get(Vaccination.VACCINE_NAME),
			primarySuspectVaccine.get(Vaccination.VACCINE_DOSE),
			primarySuspectVaccine.get(Vaccination.VACCINATION_DATE),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT),
			adverseEvents.get(AdverseEvents.SEIZURES),
			adverseEvents.get(AdverseEvents.SEIZURE_TYPE),
			adverseEvents.get(AdverseEvents.ABSCESS),
			adverseEvents.get(AdverseEvents.SEPSIS),
			adverseEvents.get(AdverseEvents.ENCEPHALOPATHY),
			adverseEvents.get(AdverseEvents.TOXIC_SHOCK_SYNDROME),
			adverseEvents.get(AdverseEvents.THROMBOCYTOPENIA),
			adverseEvents.get(AdverseEvents.ANAPHYLAXIS),
			adverseEvents.get(AdverseEvents.FEVERISH_FEELING),
			adverseEvents.get(AdverseEvents.OTHER_ADVERSE_EVENT_DETAILS),
			aefi.get(Aefi.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, isInJurisdictionOrOwned(aefiQueryContext)));

		final Predicate criteriaFilter = buildCriteriaFilter(immunizationId, aefiQueryContext);
		if (criteriaFilter != null) {
			cq.where(criteriaFilter);
		}

		cq.orderBy(cb.desc(aefi.get(Aefi.CHANGE_DATE)));

		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, new AefiListEntryDtoResultTransformer(), first, max);
	}

	private Predicate buildCriteriaFilter(Long immunizationId, AefiQueryContext aefiQueryContext) {

		final CriteriaBuilder cb = aefiQueryContext.getCriteriaBuilder();
		final From<?, ?> from = aefiQueryContext.getRoot();

		Predicate filter = cb.equal(from.get(Aefi.IMMUNIZATION_ID), immunizationId);

		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Aefi.DELETED)));

		return filter;
	}

	public long count(AefiCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Aefi> aefi = cq.from(Aefi.class);

		AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefi);

		buildWhereCondition(criteria, cb, cq, aefiQueryContext, null);

		cq.select(cb.countDistinct(aefi));
		return em.createQuery(cq).getSingleResult();
	}

	public List<AefiIndexDto> getIndexList(AefiCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);
		List<AefiIndexDto> aefiIndexDtos = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {

			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			final Root<Aefi> aefi = cq.from(Aefi.class);

			AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefi);
			AefiJoins joins = aefiQueryContext.getJoins();

			final Join<Aefi, Immunization> immunization = joins.getImmunization();
			final Join<Immunization, Person> person = joins.getImmunizationJoins().getPerson();

			final Join<Immunization, Region> responsibleRegion = joins.getImmunizationJoins().getResponsibleRegion();
			final Join<Immunization, District> responsibleDistrict = joins.getImmunizationJoins().getResponsibleDistrict();

			final Join<Aefi, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();
			final Join<Aefi, AdverseEvents> adverseEvents = joins.getAdverseEvents();

			cq.multiselect(
				Stream
					.concat(
						Stream.of(
							aefi.get(Aefi.UUID),
							immunization.get(Immunization.UUID),
							immunization.get(Immunization.DISEASE),
							person.get(Person.UUID),
							person.get(Person.FIRST_NAME),
							person.get(Person.LAST_NAME),
							person.get(Person.APPROXIMATE_AGE),
							person.get(Person.APPROXIMATE_AGE_TYPE),
							person.get(Person.BIRTHDATE_DD),
							person.get(Person.BIRTHDATE_MM),
							person.get(Person.BIRTHDATE_YYYY),
							person.get(Person.SEX),
							responsibleRegion.get(Region.NAME),
							responsibleDistrict.get(District.NAME),
							aefi.get(Aefi.SERIOUS),
							primarySuspectVaccine.get(Vaccination.VACCINE_NAME),
							primarySuspectVaccine.get(Vaccination.OTHER_VACCINE_NAME),
							aefi.get(Aefi.OUTCOME),
							primarySuspectVaccine.get(Vaccination.VACCINATION_DATE),
							aefi.get(Aefi.REPORT_DATE),
							aefi.get(Aefi.START_DATE_TIME),
							adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION),
							adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS),
							adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT),
							adverseEvents.get(AdverseEvents.SEIZURES),
							adverseEvents.get(AdverseEvents.SEIZURE_TYPE),
							adverseEvents.get(AdverseEvents.ABSCESS),
							adverseEvents.get(AdverseEvents.SEPSIS),
							adverseEvents.get(AdverseEvents.ENCEPHALOPATHY),
							adverseEvents.get(AdverseEvents.TOXIC_SHOCK_SYNDROME),
							adverseEvents.get(AdverseEvents.THROMBOCYTOPENIA),
							adverseEvents.get(AdverseEvents.ANAPHYLAXIS),
							adverseEvents.get(AdverseEvents.FEVERISH_FEELING),
							adverseEvents.get(AdverseEvents.OTHER_ADVERSE_EVENT_DETAILS),
							aefi.get(Aefi.DELETION_REASON),
							aefi.get(Aefi.OTHER_DELETION_REASON),
							JurisdictionHelper.booleanSelector(cb, isInJurisdictionOrOwned(aefiQueryContext)),
							aefi.get(Aefi.CHANGE_DATE)),
						// add sort properties to select
						sortBy(sortProperties, aefiQueryContext).stream())
					.collect(Collectors.toList()));

			buildWhereCondition(criteria, cb, cq, aefiQueryContext, aefi.get(Aefi.ID).in(batchedIds));
			cq.distinct(true);

			aefiIndexDtos.addAll(QueryHelper.getResultList(em, cq, new AefiIndexDtoResultTransformer(), null, null));
		});

		return aefiIndexDtos;
	}

	private List<Long> getIndexListIds(AefiCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Aefi> aefi = cq.from(Aefi.class);

		AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefi);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(aefi.get(Aefi.ID));
		selections.addAll(sortBy(sortProperties, aefiQueryContext));

		cq.multiselect(selections);

		buildWhereCondition(criteria, cb, cq, aefiQueryContext, null);
		cq.distinct(true);

		List<Tuple> aefiResultList = QueryHelper.getResultList(em, cq, first, max);
		return aefiResultList.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	public List<AefiExportDto> getExportList(AefiCriteria criteria, Collection<String> selectedRows, int first, int max) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<AefiExportDto> cq = cb.createQuery(AefiExportDto.class);
		final Root<Aefi> aefi = cq.from(Aefi.class);

		final AefiQueryContext aefiQueryContext = new AefiQueryContext(cb, cq, aefi);
		AefiJoins joins = aefiQueryContext.getJoins();

		final Join<Aefi, Immunization> immunization = joins.getImmunization();
		final Join<Immunization, Person> person = joins.getImmunizationJoins().getPerson();
		final Join<Immunization, Facility> immunizationFacility = joins.getImmunizationJoins().getHealthFacility();
		final Join<Facility, Region> immunizationFacilityRegion = immunizationFacility.join(Facility.REGION, JoinType.LEFT);
		final Join<Facility, District> immunizationFacilityDistrict = immunizationFacility.join(Facility.DISTRICT, JoinType.LEFT);
		final Join<Facility, Region> immunizationFacilityCommunity = immunizationFacility.join(Facility.COMMUNITY, JoinType.LEFT);

		final Join<Person, Location> personLocation = person.join(Person.ADDRESS, JoinType.LEFT);
		final Join<Location, Region> personLocationRegion = personLocation.join(Location.REGION, JoinType.LEFT);
		final Join<Location, District> personLocationDistrict = personLocation.join(Location.DISTRICT, JoinType.LEFT);
		final Join<Location, Community> personLocationCommunity = personLocation.join(Location.COMMUNITY, JoinType.LEFT);

		final Join<Immunization, Region> responsibleRegion = joins.getImmunizationJoins().getResponsibleRegion();
		final Join<Immunization, District> responsibleDistrict = joins.getImmunizationJoins().getResponsibleDistrict();

		final Join<Aefi, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();
		final Join<Aefi, AdverseEvents> adverseEvents = joins.getAdverseEvents();

		final Join<Aefi, User> reportingUser = joins.getReportingUser();
		final Join<User, Location> reportingUserLocation = reportingUser.join(User.ADDRESS, JoinType.LEFT);
		final Join<Location, Country> reportingUserLocationCountry = reportingUserLocation.join(Location.COUNTRY, JoinType.LEFT);
		final Join<User, Facility> reportingUserFacility = reportingUser.join(User.HEALTH_FACILITY, JoinType.LEFT);
		final Join<Facility, Region> reportingUserFacilityRegion = reportingUserFacility.join(Facility.REGION, JoinType.LEFT);
		final Join<Facility, District> reportingUserFacilityDistrict = reportingUserFacility.join(Facility.DISTRICT, JoinType.LEFT);
		final Join<Facility, Community> reportingUserFacilityCommunity = reportingUserFacility.join(Facility.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			aefi.get(Aefi.UUID),
			aefi.get(Aefi.RECEIVED_AT_NATIONAL_LEVEL_DATE),
			immunizationFacility.get(Facility.NAME),
			immunizationFacilityRegion.get(Region.NAME),
			immunizationFacilityDistrict.get(District.NAME),
			immunizationFacilityCommunity.get(Community.NAME),
			reportingUserLocationCountry.get(Country.DEFAULT_NAME),
			personLocationRegion.get(Region.NAME),
			personLocationDistrict.get(District.NAME),
			personLocationCommunity.get(Community.NAME),
			personLocation.get(Location.STREET),
			personLocation.get(Location.HOUSE_NUMBER),
			personLocation.get(Location.POSTAL_CODE),
			personLocation.get(Location.CITY),
			aefi.get(Aefi.REPORTINGID_NUMBER),
			aefi.get(Aefi.WORLD_WIDE_ID),
			person.get(Person.FIRST_NAME),
			person.get(Person.LAST_NAME),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			aefi.get(Aefi.ONSET_AGE_YEARS),
			aefi.get(Aefi.ONSET_AGE_MONTHS),
			aefi.get(Aefi.ONSET_AGE_DAYS),
			aefi.get(Aefi.AGE_GROUP),
			person.get(Person.SEX),
			aefi.get(Aefi.AEFI_DESCRIPTION),
			primarySuspectVaccine.get(Vaccination.VACCINE_NAME),
			primarySuspectVaccine.get(Vaccination.OTHER_VACCINE_NAME),
			primarySuspectVaccine.get(Vaccination.VACCINE_MANUFACTURER),
			primarySuspectVaccine.get(Vaccination.VACCINE_BATCH_NUMBER),
			primarySuspectVaccine.get(Vaccination.VACCINE_DOSE),
			primarySuspectVaccine.get(Vaccination.VACCINATION_DATE),
			aefi.get(Aefi.START_DATE_TIME),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_MORE_THAN_THREE_DAYS),
			adverseEvents.get(AdverseEvents.SEVERE_LOCAL_REACTION_BEYOND_NEAREST_JOINT),
			adverseEvents.get(AdverseEvents.SEIZURES),
			adverseEvents.get(AdverseEvents.SEIZURE_TYPE),
			adverseEvents.get(AdverseEvents.ABSCESS),
			adverseEvents.get(AdverseEvents.SEPSIS),
			adverseEvents.get(AdverseEvents.ENCEPHALOPATHY),
			adverseEvents.get(AdverseEvents.TOXIC_SHOCK_SYNDROME),
			adverseEvents.get(AdverseEvents.THROMBOCYTOPENIA),
			adverseEvents.get(AdverseEvents.ANAPHYLAXIS),
			adverseEvents.get(AdverseEvents.FEVERISH_FEELING),
			adverseEvents.get(AdverseEvents.OTHER_ADVERSE_EVENT_DETAILS),
			aefi.get(Aefi.OUTCOME),
			aefi.get(Aefi.SERIOUS),
			reportingUser.get(User.FIRST_NAME),
			reportingUser.get(User.LAST_NAME),
			reportingUserFacility.get(Facility.NAME),
			reportingUserFacilityRegion.get(Region.NAME),
			reportingUserFacilityDistrict.get(District.NAME),
			reportingUserFacilityCommunity.get(Community.NAME),
			reportingUser.get(User.USER_EMAIL),
			reportingUser.get(User.PHONE),
			aefi.get((Aefi.REPORT_DATE)),
			aefi.get((Aefi.NATIONAL_LEVEL_COMMENT)),
			JurisdictionHelper.booleanSelector(cb, isInJurisdictionOrOwned(aefiQueryContext)));

		/*
		 * buildWhereCondition(criteria, cb, cq, aefiQueryContext, null);
		 * cq.distinct(true);
		 */

		Predicate filter = buildExportListWhereCondition(criteria, cb, cq, aefiQueryContext);
		if (selectedRows != null && !selectedRows.isEmpty()) {
			filter = CriteriaBuilderHelper.andInValues(selectedRows, filter, cb, aefi.get(Aefi.UUID));
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, first, max);
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, AefiQueryContext aefiQueryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = aefiQueryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = aefiQueryContext.getQuery();

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case AefiIndexDto.UUID:
				case AefiIndexDto.DISEASE:
				case AefiIndexDto.START_DATE_TIME:
					expression = aefiQueryContext.getRoot().get(sortProperty.propertyName);
					break;
				case AefiIndexDto.PERSON_UUID:
					expression = aefiQueryContext.getJoins().getImmunizationJoins().getPerson().get(Person.UUID);
					break;
				case AefiIndexDto.PERSON_FIRST_NAME:
					expression = cb.lower(aefiQueryContext.getJoins().getImmunizationJoins().getPerson().get(Person.FIRST_NAME));
					break;
				case AefiIndexDto.PERSON_LAST_NAME:
					expression = cb.lower(aefiQueryContext.getJoins().getImmunizationJoins().getPerson().get(Person.LAST_NAME));
					break;
				case AefiIndexDto.AGE_AND_BIRTH_DATE:
					expression = aefiQueryContext.getJoins().getImmunizationJoins().getPerson().get(Person.APPROXIMATE_AGE);
					break;
				case AefiIndexDto.SEX:
					expression = aefiQueryContext.getJoins().getImmunizationJoins().getPerson().get(Person.SEX);
					break;
				case AefiIndexDto.REGION:
					expression =
						cb.lower(aefiQueryContext.getJoins().getImmunizationJoins().getPersonJoins().getAddressJoins().getRegion().get(Region.NAME));
					break;
				case AefiIndexDto.DISTRICT:
					expression = cb.lower(
						aefiQueryContext.getJoins().getImmunizationJoins().getPersonJoins().getAddressJoins().getDistrict().get(District.NAME));
					break;
				case AefiIndexDto.PRIMARY_VACCINE_NAME:
					expression = aefiQueryContext.getJoins().getPrimarySuspectVaccination().get(Vaccination.VACCINE_NAME);
					break;
				case AefiIndexDto.VACCINATION_DATE:
					expression = aefiQueryContext.getJoins().getPrimarySuspectVaccination().get(Vaccination.VACCINATION_DATE);
					break;
				case AefiIndexDto.SERIOUS:
					expression = aefiQueryContext.getRoot().get(Aefi.SERIOUS);
					break;
				case AefiIndexDto.OUTCOME:
					expression = aefiQueryContext.getRoot().get(Aefi.OUTCOME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = aefiQueryContext.getRoot().get(Aefi.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	private <T> void buildWhereCondition(
		AefiCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<T> cq,
		AefiQueryContext aefiQueryContext,
		Predicate additionalFilter) {

		Predicate filter = createUserFilter(aefiQueryContext);
		if (additionalFilter != null) {
			filter = CriteriaBuilderHelper.and(cb, additionalFilter, filter);
		}

		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, aefiQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
	}

	private <T> Predicate buildExportListWhereCondition(
		AefiCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<T> cq,
		AefiQueryContext aefiQueryContext) {

		Predicate filter = createUserFilter(aefiQueryContext);

		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, aefiQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		return filter;
	}

	public Predicate buildCriteriaFilter(AefiCriteria criteria, AefiQueryContext aefiQueryContext) {

		final AefiJoins joins = aefiQueryContext.getJoins();
		final CriteriaBuilder cb = aefiQueryContext.getCriteriaBuilder();
		final From<?, ?> from = aefiQueryContext.getRoot();
		final Join<Aefi, Immunization> immunization = joins.getImmunization();
		final Join<Immunization, Person> person = joins.getImmunizationJoins().getPerson();
		final Join<Aefi, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();

		final Join<Person, Location> location = joins.getImmunizationJoins().getPersonJoins().getAddress();

		Predicate filter = null;
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, null, cb.equal(immunization.get(Immunization.DISEASE), criteria.getDisease()));
		}

		if (!DataHelper.isNullOrEmpty(criteria.getPersonLike())) {
			final CriteriaQuery<PersonIndexDto> cq = cb.createQuery(PersonIndexDto.class);
			final PersonQueryContext personQueryContext = new PersonQueryContext(cb, cq, joins.getImmunizationJoins().getPersonJoins());

			String[] textFilters = criteria.getPersonLike().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(Aefi.UUID), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.UUID), textFilter),
					CriteriaBuilderHelper.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_EMAIL_SUBQUERY), textFilter),
					phoneNumberPredicate(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PHONE_SUBQUERY), textFilter),
					CriteriaBuilderHelper
						.ilike(cb, personQueryContext.getSubqueryExpression(PersonQueryContext.PERSON_PRIMARY_OTHER_SUBQUERY), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.STREET), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, location.get(Location.CITY), textFilter),
					CriteriaBuilderHelper.ilike(cb, location.get(Location.POSTAL_CODE), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.EXTERNAL_TOKEN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		if (criteria.getAefiType() != null) {
			if (criteria.getAefiType() == AefiType.SERIOUS) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Aefi.SERIOUS), YesNoUnknown.YES));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.notEqual(from.get(Aefi.SERIOUS), YesNoUnknown.YES));
			}
		} else {
			if (criteria.isGisMapCriteria()) {
				if (criteria.isShowSeriousAefiForMap() && !criteria.isShowNonSeriousAefiForMap()) {
					filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Aefi.SERIOUS), YesNoUnknown.YES));
				} else if (criteria.isShowNonSeriousAefiForMap() && !criteria.isShowSeriousAefiForMap()) {
					filter = CriteriaBuilderHelper.and(cb, filter, cb.notEqual(from.get(Aefi.SERIOUS), YesNoUnknown.YES));
				} else if (criteria.isShowSeriousAefiForMap() && criteria.isShowNonSeriousAefiForMap()) {
					filter = CriteriaBuilderHelper.and(cb, filter, cb.isNotNull(from.get(Aefi.SERIOUS)));
				} else if (!criteria.isShowSeriousAefiForMap() && !criteria.isShowNonSeriousAefiForMap()) {
					filter = CriteriaBuilderHelper.and(cb, filter, cb.isNull(from.get(Aefi.SERIOUS)));
				}
			}
		}
		if (criteria.getOutcome() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Aefi.OUTCOME), criteria.getOutcome()));
		}
		if (criteria.getVaccineName() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(primarySuspectVaccine.get(Vaccination.VACCINE_NAME), criteria.getVaccineName()));
		}
		if (criteria.getVaccineManufacturer() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(primarySuspectVaccine.get(Vaccination.VACCINE_MANUFACTURER), criteria.getVaccineManufacturer()));
		}
		filter = andEquals(cb, () -> joins.getImmunizationJoins().getResponsibleRegion(), filter, criteria.getRegion());
		filter = andEquals(cb, () -> joins.getImmunizationJoins().getResponsibleDistrict(), filter, criteria.getDistrict());
		filter = andEquals(cb, () -> joins.getImmunizationJoins().getResponsibleCommunity(), filter, criteria.getCommunity());
		if (criteria.getFacilityType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(immunization.get(Immunization.FACILITY_TYPE), criteria.getFacilityType()));
		}
		filter = andEquals(cb, () -> joins.getImmunizationJoins().getHealthFacility(), filter, criteria.getHealthFacility());
		if (criteria.getAefiDateType() != null) {
			Path<Object> path = buildPathForDateFilter(criteria.getAefiDateType(), aefiQueryContext);
			if (path != null) {
				filter = CriteriaBuilderHelper.applyDateFilter(cb, filter, path, criteria.getFromDate(), criteria.getToDate());
			}
		}

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.or(cb.equal(from.get(Aefi.ARCHIVED), false), cb.isNull(from.get(Aefi.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Aefi.ARCHIVED), true));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Aefi.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Aefi.DELETED)));
		}

		return filter;
	}

	private Path<Object> buildPathForDateFilter(AefiDateType aefiDateType, AefiQueryContext aefiQueryContext) {
		Path<Object> path = null;
		String dateField = getDateFieldFromDateType(aefiDateType);
		if (dateField != null) {
			if (Vaccination.VACCINATION_DATE.equals(dateField)) {
				final Join<Aefi, Vaccination> primarySuspectVaccination = aefiQueryContext.getJoins().getPrimarySuspectVaccination();
				path = primarySuspectVaccination.get(Vaccination.VACCINATION_DATE);
			} else {
				path = aefiQueryContext.getRoot().get(dateField);
			}
		}
		return path;
	}

	private String getDateFieldFromDateType(AefiDateType aefiDateType) {
		switch (aefiDateType) {
		case REPORT_DATE:
			return Aefi.REPORT_DATE;
		case START_DATE:
			return Aefi.START_DATE_TIME;
		case VACCINATION_DATE:
			return Vaccination.VACCINATION_DATE;
		}
		return null;
	}

	public Predicate createUserFilter(AefiQueryContext qc) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final CriteriaBuilder cb = qc.getCriteriaBuilder();

		Predicate filter = isInJurisdictionOrOwned(qc);

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.limitedDiseasePredicate(cb, currentUser, qc.getRoot().get(Aefi.IMMUNIZATION).get(Immunization.DISEASE)));

		return filter;
	}

	private Predicate isInJurisdictionOrOwned(AefiQueryContext qc) {

		final User currentUser = userService.getCurrentUser();
		CriteriaBuilder cb = qc.getCriteriaBuilder();
		Predicate filter;
		if (!featureConfigurationFacade
			.isPropertyValueTrue(FeatureType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			filter = AefiJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
		} else {
			filter = CriteriaBuilderHelper.or(
				cb,
				cb.equal(qc.getRoot().get(Aefi.REPORTING_USER), currentUser),
				PersonJurisdictionPredicateValidator
					.of(qc.getQuery(), cb, new PersonJoins(qc.getJoins().getPerson()), currentUser, personService.getPermittedAssociations())
					.inJurisdictionOrOwned());
		}
		return filter;
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, Aefi> from) {
		return createUserFilter(new AefiQueryContext(cb, cq, from));
	}

	@Override
	protected AefiJoins toJoins(From<?, Aefi> adoPath) {
		return new AefiJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, Aefi> from) {
		return isInJurisdictionOrOwned(new AefiQueryContext(cb, query, from));
	}
}
