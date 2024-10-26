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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.EntityRelevanceStatus;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationCriteria;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationDateType;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationIndexDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiInvestigationListEntryDto;
import de.symeda.sormas.api.adverseeventsfollowingimmunization.AefiType;
import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.Aefi;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiInvestigation;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.entity.AefiInvestigationJoins;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers.AefiInvestigationIndexDtoResultTransformer;
import de.symeda.sormas.backend.adverseeventsfollowingimmunization.transformers.AefiInvestigationListEntryDtoResultTransformer;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.infrastructure.district.District;
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
public class AefiInvestigationService extends AbstractCoreAdoService<AefiInvestigation, AefiInvestigationJoins> {

	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;

	public AefiInvestigationService() {
		super(AefiInvestigation.class, DeletableEntityType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION);
	}

	public List<AefiInvestigationListEntryDto> getEntriesList(Long aefiReportId, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		final Root<AefiInvestigation> aefiInvestigation = cq.from(AefiInvestigation.class);

		AefiInvestigationQueryContext queryContext = new AefiInvestigationQueryContext(cb, cq, aefiInvestigation);
		AefiInvestigationJoins joins = queryContext.getJoins();

		Join<AefiInvestigation, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();

		cq.multiselect(
			aefiInvestigation.get(AefiInvestigation.UUID),
			aefiInvestigation.get(AefiInvestigation.INVESTIGATION_CASE_ID),
			aefiInvestigation.get(AefiInvestigation.INVESTIGATION_DATE),
			aefiInvestigation.get(AefiInvestigation.INVESTIGATION_STAGE),
			aefiInvestigation.get(AefiInvestigation.STATUS_ON_DATE_OF_INVESTIGATION),
			primarySuspectVaccine.get(Vaccination.VACCINE_NAME),
			primarySuspectVaccine.get(Vaccination.OTHER_VACCINE_NAME),
			primarySuspectVaccine.get(Vaccination.VACCINE_DOSE),
			primarySuspectVaccine.get(Vaccination.VACCINATION_DATE),
			aefiInvestigation.get(AefiInvestigation.INVESTIGATION_STATUS),
			aefiInvestigation.get(AefiInvestigation.AEFI_CLASSIFICATION),
			aefiInvestigation.get(Aefi.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, isInJurisdictionOrOwned(queryContext)));

		final Predicate criteriaFilter = buildCriteriaFilter(aefiReportId, queryContext);
		if (criteriaFilter != null) {
			cq.where(criteriaFilter);
		}

		cq.orderBy(cb.desc(aefiInvestigation.get(AefiInvestigation.CHANGE_DATE)));

		cq.distinct(true);

		return QueryHelper.getResultList(em, cq, new AefiInvestigationListEntryDtoResultTransformer(), first, max);
	}

	private Predicate buildCriteriaFilter(Long aefiReportId, AefiInvestigationQueryContext queryContext) {

		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final From<?, ?> from = queryContext.getRoot();

		Predicate filter = cb.equal(from.get(AefiInvestigation.AEFI_REPORT_ID), aefiReportId);

		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(AefiInvestigation.DELETED)));

		return filter;
	}

	public long count(AefiInvestigationCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<AefiInvestigation> aefiInvestigation = cq.from(AefiInvestigation.class);

		AefiInvestigationQueryContext queryContext = new AefiInvestigationQueryContext(cb, cq, aefiInvestigation);

		buildWhereCondition(criteria, cb, cq, queryContext, null);

		cq.select(cb.countDistinct(aefiInvestigation));
		return em.createQuery(cq).getSingleResult();
	}

	public List<AefiInvestigationIndexDto> getIndexList(
		AefiInvestigationCriteria criteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);
		List<AefiInvestigationIndexDto> aefiInvestigationIndexDtos = new ArrayList<>();

		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {

			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			final Root<AefiInvestigation> aefiInvestigation = cq.from(AefiInvestigation.class);

			AefiInvestigationQueryContext queryContext = new AefiInvestigationQueryContext(cb, cq, aefiInvestigation);
			AefiInvestigationJoins joins = queryContext.getJoins();

			final Join<AefiInvestigation, Aefi> aefi = joins.getAefi();
			final Join<Aefi, Immunization> immunization = joins.getAefiJoins().getImmunization();
			final Join<Immunization, Person> person = joins.getAefiJoins().getImmunizationJoins().getPerson();

			final Join<Immunization, Region> responsibleRegion = joins.getAefiJoins().getImmunizationJoins().getResponsibleRegion();
			final Join<Immunization, District> responsibleDistrict = joins.getAefiJoins().getImmunizationJoins().getResponsibleDistrict();

			final Join<AefiInvestigation, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();

			cq.multiselect(
				Stream
					.concat(
						Stream.of(
							aefiInvestigation.get(AefiInvestigation.UUID),
							aefi.get(Aefi.UUID),
							aefiInvestigation.get(AefiInvestigation.INVESTIGATION_CASE_ID),
							immunization.get(Immunization.DISEASE),
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
							aefiInvestigation.get(AefiInvestigation.PLACE_OF_VACCINATION),
							aefiInvestigation.get(AefiInvestigation.VACCINATION_ACTIVITY),
							aefi.get(Aefi.REPORT_DATE),
							aefiInvestigation.get(Aefi.REPORT_DATE),
							aefiInvestigation.get(AefiInvestigation.INVESTIGATION_DATE),
							aefiInvestigation.get(AefiInvestigation.INVESTIGATION_STAGE),
							aefiInvestigation.get(AefiInvestigation.TYPE_OF_SITE),
							aefiInvestigation.get(AefiInvestigation.KEY_SYMPTOM_DATE_TIME),
							aefiInvestigation.get(AefiInvestigation.HOSPITALIZATION_DATE),
							aefiInvestigation.get(AefiInvestigation.REPORTED_TO_HEALTH_AUTHORITY_DATE),
							aefiInvestigation.get(AefiInvestigation.STATUS_ON_DATE_OF_INVESTIGATION),
							primarySuspectVaccine.get(Vaccination.VACCINE_NAME),
							primarySuspectVaccine.get(Vaccination.OTHER_VACCINE_NAME),
							aefiInvestigation.get(AefiInvestigation.INVESTIGATION_STATUS),
							aefiInvestigation.get(AefiInvestigation.AEFI_CLASSIFICATION),
							aefiInvestigation.get(AefiInvestigation.DELETION_REASON),
							aefiInvestigation.get(AefiInvestigation.OTHER_DELETION_REASON),
							JurisdictionHelper.booleanSelector(cb, isInJurisdictionOrOwned(queryContext)),
							aefiInvestigation.get(AefiInvestigation.CHANGE_DATE)),
						// add sort properties to select
						sortBy(sortProperties, queryContext).stream())
					.collect(Collectors.toList()));

			buildWhereCondition(criteria, cb, cq, queryContext, aefiInvestigation.get(AefiInvestigation.ID).in(batchedIds));
			cq.distinct(true);

			aefiInvestigationIndexDtos.addAll(QueryHelper.getResultList(em, cq, new AefiInvestigationIndexDtoResultTransformer(), null, null));
		});

		return aefiInvestigationIndexDtos;
	}

	private List<Long> getIndexListIds(AefiInvestigationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<AefiInvestigation> aefiInvestigation = cq.from(AefiInvestigation.class);

		AefiInvestigationQueryContext queryContext = new AefiInvestigationQueryContext(cb, cq, aefiInvestigation);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(aefiInvestigation.get(AefiInvestigation.ID));
		selections.addAll(sortBy(sortProperties, queryContext));

		cq.multiselect(selections);

		buildWhereCondition(criteria, cb, cq, queryContext, null);
		cq.distinct(true);

		List<Tuple> aefiInvestigationsResultList = QueryHelper.getResultList(em, cq, first, max);
		return aefiInvestigationsResultList.stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Selection<?>> sortBy(List<SortProperty> sortProperties, AefiInvestigationQueryContext queryContext) {

		List<Selection<?>> selections = new ArrayList<>();
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		CriteriaQuery<?> cq = queryContext.getQuery();

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case AefiInvestigationIndexDto.UUID:
				case AefiInvestigationIndexDto.DISEASE:
				case AefiInvestigationIndexDto.INVESTIGATION_DATE:
				case AefiInvestigationIndexDto.STATUS_ON_DATE_OF_INVESTIGATION:
				case AefiInvestigationIndexDto.AEFI_CLASSIFICATION:
					expression = queryContext.getRoot().get(sortProperty.propertyName);
					break;
				case AefiInvestigationIndexDto.AEFI_REPORT_UUID:
					expression = queryContext.getJoins().getAefi().get(Aefi.UUID);
					break;
				case AefiInvestigationIndexDto.PERSON_FIRST_NAME:
					expression = cb.lower(queryContext.getJoins().getAefiJoins().getImmunizationJoins().getPerson().get(Person.FIRST_NAME));
					break;
				case AefiInvestigationIndexDto.PERSON_LAST_NAME:
					expression = cb.lower(queryContext.getJoins().getAefiJoins().getImmunizationJoins().getPerson().get(Person.LAST_NAME));
					break;
				case AefiInvestigationIndexDto.AGE_AND_BIRTH_DATE:
					expression = queryContext.getJoins().getAefiJoins().getImmunizationJoins().getPerson().get(Person.APPROXIMATE_AGE);
					break;
				case AefiInvestigationIndexDto.SEX:
					expression = queryContext.getJoins().getAefiJoins().getImmunizationJoins().getPerson().get(Person.SEX);
					break;
				case AefiInvestigationIndexDto.REGION:
					expression = cb.lower(
						queryContext.getJoins()
							.getAefiJoins()
							.getImmunizationJoins()
							.getPersonJoins()
							.getAddressJoins()
							.getRegion()
							.get(Region.NAME));
					break;
				case AefiInvestigationIndexDto.DISTRICT:
					expression = cb.lower(
						queryContext.getJoins()
							.getAefiJoins()
							.getImmunizationJoins()
							.getPersonJoins()
							.getAddressJoins()
							.getDistrict()
							.get(District.NAME));
					break;
				case AefiInvestigationIndexDto.PRIMARY_VACCINE_NAME:
					expression = queryContext.getJoins().getPrimarySuspectVaccination().get(Vaccination.VACCINE_NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
				selections.add(expression);
			}
			cq.orderBy(order);
		} else {
			Path<Object> changeDate = queryContext.getRoot().get(AefiInvestigation.CHANGE_DATE);
			cq.orderBy(cb.desc(changeDate));
			selections.add(changeDate);
		}

		return selections;
	}

	private <T> void buildWhereCondition(
		AefiInvestigationCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<T> cq,
		AefiInvestigationQueryContext queryContext,
		Predicate additionalFilter) {

		Predicate filter = createUserFilter(queryContext);
		if (additionalFilter != null) {
			filter = CriteriaBuilderHelper.and(cb, additionalFilter, filter);
		}

		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
	}

	public Predicate buildCriteriaFilter(AefiInvestigationCriteria criteria, AefiInvestigationQueryContext queryContext) {

		final AefiInvestigationJoins joins = queryContext.getJoins();
		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final From<?, ?> from = queryContext.getRoot();
		final Join<AefiInvestigation, Aefi> aefi = joins.getAefi();
		final Join<Aefi, Immunization> immunization = joins.getAefiJoins().getImmunization();
		final Join<Immunization, Person> person = joins.getAefiJoins().getImmunizationJoins().getPerson();
		final Join<AefiInvestigation, Vaccination> primarySuspectVaccine = joins.getPrimarySuspectVaccination();

		final Join<Person, Location> location = joins.getAefiJoins().getImmunizationJoins().getPersonJoins().getAddress();

		Predicate filter = null;
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, null, cb.equal(immunization.get(Immunization.DISEASE), criteria.getDisease()));
		}

		if (!DataHelper.isNullOrEmpty(criteria.getAefiReportLike())) {

			String[] textFilters = criteria.getAefiReportLike().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(CriteriaBuilderHelper.ilike(cb, aefi.get(Aefi.UUID), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}

		if (!DataHelper.isNullOrEmpty(criteria.getPersonLike())) {
			final CriteriaQuery<PersonIndexDto> cq = cb.createQuery(PersonIndexDto.class);
			final PersonQueryContext personQueryContext =
				new PersonQueryContext(cb, cq, joins.getAefiJoins().getImmunizationJoins().getPersonJoins());

			String[] textFilters = criteria.getPersonLike().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.ilike(cb, from.get(AefiInvestigation.UUID), textFilter),
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
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(aefi.get(Aefi.SERIOUS), YesNoUnknown.YES));
			} else {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.notEqual(aefi.get(Aefi.SERIOUS), YesNoUnknown.YES));
			}
		}
		if (criteria.getStatusAtAefiInvestigation() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(AefiInvestigation.STATUS_ON_DATE_OF_INVESTIGATION), criteria.getStatusAtAefiInvestigation()));
		}
		if (criteria.getAefiClassification() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(AefiInvestigation.AEFI_CLASSIFICATION), criteria.getAefiClassification()));
		}
		if (criteria.getVaccineName() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(primarySuspectVaccine.get(Vaccination.VACCINE_NAME), criteria.getVaccineName()));
		}
		if (criteria.getVaccineManufacturer() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(primarySuspectVaccine.get(Vaccination.VACCINE_MANUFACTURER), criteria.getVaccineManufacturer()));
		}
		filter = andEquals(cb, () -> joins.getAefiJoins().getImmunizationJoins().getResponsibleRegion(), filter, criteria.getRegion());
		filter = andEquals(cb, () -> joins.getAefiJoins().getImmunizationJoins().getResponsibleDistrict(), filter, criteria.getDistrict());
		filter = andEquals(cb, () -> joins.getAefiJoins().getImmunizationJoins().getResponsibleCommunity(), filter, criteria.getCommunity());
		if (criteria.getFacilityType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(immunization.get(Immunization.FACILITY_TYPE), criteria.getFacilityType()));
		}
		filter = andEquals(cb, () -> joins.getAefiJoins().getImmunizationJoins().getHealthFacility(), filter, criteria.getHealthFacility());
		if (criteria.getAefiInvestigationDateType() != null) {
			Path<Object> path = buildPathForDateFilter(criteria.getAefiInvestigationDateType(), queryContext);
			if (path != null) {
				filter = CriteriaBuilderHelper.applyDateFilter(cb, filter, path, criteria.getFromDate(), criteria.getToDate());
			}
		}

		if (criteria.getRelevanceStatus() != null) {
			if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ACTIVE) {
				filter = CriteriaBuilderHelper
					.and(cb, filter, cb.or(cb.equal(from.get(AefiInvestigation.ARCHIVED), false), cb.isNull(from.get(AefiInvestigation.ARCHIVED))));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.ARCHIVED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(AefiInvestigation.ARCHIVED), true));
			} else if (criteria.getRelevanceStatus() == EntityRelevanceStatus.DELETED) {
				filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(AefiInvestigation.DELETED), true));
			}
		}
		if (criteria.getRelevanceStatus() != EntityRelevanceStatus.DELETED) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(AefiInvestigation.DELETED)));
		}

		return filter;
	}

	private Path<Object> buildPathForDateFilter(AefiInvestigationDateType aefiInvestigationDateType, AefiInvestigationQueryContext queryContext) {
		Path<Object> path = null;
		String dateField = getDateFieldFromDateType(aefiInvestigationDateType);
		if (dateField != null) {
			if (Vaccination.VACCINATION_DATE.equals(dateField)) {
				final Join<AefiInvestigation, Vaccination> primarySuspectVaccination = queryContext.getJoins().getPrimarySuspectVaccination();
				path = primarySuspectVaccination.get(Vaccination.VACCINATION_DATE);
			} else {
				path = queryContext.getRoot().get(dateField);
			}
		}
		return path;
	}

	private String getDateFieldFromDateType(AefiInvestigationDateType aefiInvestigationDateType) {
		switch (aefiInvestigationDateType) {
		case REPORT_DATE:
			return AefiInvestigation.REPORT_DATE;
		case INVESTIGATION_DATE:
			return AefiInvestigation.INVESTIGATION_DATE;
		case VACCINATION_DATE:
			return Vaccination.VACCINATION_DATE;
		}
		return null;
	}

	public Predicate createUserFilter(AefiInvestigationQueryContext qc) {

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}
		final CriteriaBuilder cb = qc.getCriteriaBuilder();

		Predicate filter = isInJurisdictionOrOwned(qc);

		filter = CriteriaBuilderHelper.and(
			cb,
			filter,
			CriteriaBuilderHelper.limitedDiseasePredicate(
				cb,
				currentUser,
				qc.getRoot().get(AefiInvestigation.AEFI_REPORT).get(Aefi.IMMUNIZATION).get(Immunization.DISEASE)));

		return filter;
	}

	private Predicate isInJurisdictionOrOwned(AefiInvestigationQueryContext qc) {

		final User currentUser = userService.getCurrentUser();
		CriteriaBuilder cb = qc.getCriteriaBuilder();
		Predicate filter;
		if (!featureConfigurationFacade
			.isPropertyValueTrue(FeatureType.ADVERSE_EVENTS_FOLLOWING_IMMUNIZATION_MANAGEMENT, FeatureTypeProperty.REDUCED)) {
			filter = AefiInvestigationJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
		} else {
			filter = CriteriaBuilderHelper.or(
				cb,
				cb.equal(qc.getRoot().get(AefiInvestigation.REPORTING_USER), currentUser),
				PersonJurisdictionPredicateValidator
					.of(
						qc.getQuery(),
						cb,
						new PersonJoins(qc.getJoins().getAefiJoins().getImmunizationJoins().getPerson()),
						currentUser,
						personService.getPermittedAssociations())
					.inJurisdictionOrOwned());
		}
		return filter;
	}

	@Override
	protected Predicate createUserFilterInternal(CriteriaBuilder cb, CriteriaQuery cq, From<?, AefiInvestigation> from) {
		return createUserFilter(new AefiInvestigationQueryContext(cb, cq, from));
	}

	@Override
	protected AefiInvestigationJoins toJoins(From<?, AefiInvestigation> adoPath) {
		return new AefiInvestigationJoins(adoPath);
	}

	@Override
	public Predicate inJurisdictionOrOwned(CriteriaBuilder cb, CriteriaQuery<?> query, From<?, AefiInvestigation> from) {
		return isInJurisdictionOrOwned(new AefiInvestigationQueryContext(cb, query, from));
	}
}
