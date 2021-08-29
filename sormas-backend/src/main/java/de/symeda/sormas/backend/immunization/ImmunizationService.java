/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.immunization;

import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEquals;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEqualsReferenceDto;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDateType;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.immunization.ImmunizationListEntryDto;
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.immunization.ImmunizationSimilarityCriteria;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.ChangeDateFilterBuilder;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.immunization.tramsformes.ImmunizationIndexDtoResultTransformer;
import de.symeda.sormas.backend.immunization.tramsformes.ImmunizationListEntryDtoTransformer;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonQueryContext;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.vaccination.FirstVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccinationDate;
import de.symeda.sormas.backend.vaccination.LastVaccineType;
import de.symeda.sormas.backend.vaccination.VaccinationEntity;

@Stateless
@LocalBean
public class ImmunizationService extends AbstractCoreAdoService<Immunization> {

	@EJB
	private UserService userService;

	public ImmunizationService() {
		super(Immunization.class);
	}

	public List<ImmunizationIndexDto> getIndexList(ImmunizationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Immunization> immunization = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, immunization);
		ImmunizationJoins<Immunization> joins = (ImmunizationJoins<Immunization>) immunizationQueryContext.getJoins();

		final Join<Immunization, Person> person = joins.getPerson();

		final Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);
		final Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);

		final Join<Immunization, LastVaccineType> lastVaccineType = immunization.join(Immunization.LAST_VACCINE_TYPE, JoinType.LEFT);

		cq.multiselect(
			immunization.get(Immunization.UUID),
			person.get(Person.UUID),
			person.get(Person.FIRST_NAME),
			person.get(Person.LAST_NAME),
			person.get(Person.APPROXIMATE_AGE),
			person.get(Person.APPROXIMATE_AGE_TYPE),
			person.get(Person.BIRTHDATE_DD),
			person.get(Person.BIRTHDATE_MM),
			person.get(Person.BIRTHDATE_YYYY),
			person.get(Person.SEX),
			district.get(District.NAME),
			immunization.get(Immunization.MEANS_OF_IMMUNIZATION),
			immunization.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS),
			immunization.get(Immunization.IMMUNIZATION_STATUS),
			immunization.get(Immunization.START_DATE),
			immunization.get(Immunization.END_DATE),
			lastVaccineType.get(LastVaccineType.VACCINE_TYPE),
			immunization.get(Immunization.RECOVERY_DATE),
			immunization.get(Immunization.DISEASE),
			immunization.get(Immunization.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationQueryContext)));

		buildWhereCondition(criteria, cb, cq, immunizationQueryContext);

		if (CollectionUtils.isNotEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case ImmunizationIndexDto.UUID:
				case ImmunizationIndexDto.MEANS_OF_IMMUNIZATION:
				case ImmunizationIndexDto.MANAGEMENT_STATUS:
				case ImmunizationIndexDto.IMMUNIZATION_STATUS:
				case ImmunizationIndexDto.START_DATE:
				case ImmunizationIndexDto.END_DATE:
				case ImmunizationIndexDto.RECOVERY_DATE:
					expression = immunization.get(sortProperty.propertyName);
					break;
				case ImmunizationIndexDto.PERSON_UUID:
					expression = person.get(Person.UUID);
					break;
				case ImmunizationIndexDto.PERSON_FIRST_NAME:
					expression = person.get(Person.FIRST_NAME);
					break;
				case ImmunizationIndexDto.PERSON_LAST_NAME:
					expression = person.get(Person.LAST_NAME);
					break;
				case ImmunizationIndexDto.DISTRICT:
					expression = district.get(District.NAME);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(immunization.get(Immunization.CHANGE_DATE)));
		}

		cq.distinct(true);

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new ImmunizationIndexDtoResultTransformer())
			.getResultList();
	}

	public long count(ImmunizationCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		final Root<Immunization> immunization = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, immunization);

		buildWhereCondition(criteria, cb, cq, immunizationQueryContext);

		cq.select(cb.countDistinct(immunization));
		return em.createQuery(cq).getSingleResult();
	}

	public List<ImmunizationListEntryDto> getEntriesList(PersonReferenceDto personReferenceDto, Integer first, Integer max) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Immunization> immunization = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, immunization);

		cq.multiselect(
			immunization.get(Immunization.UUID),
			immunization.get(Immunization.DISEASE),
			immunization.get(Immunization.MEANS_OF_IMMUNIZATION),
			immunization.get(Immunization.IMMUNIZATION_STATUS),
			immunization.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS),
			immunization.get(Immunization.START_DATE),
			immunization.get(Immunization.END_DATE),
			immunization.get(Immunization.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationQueryContext)));

		ImmunizationCriteria criteria = new ImmunizationCriteria();
		if (personReferenceDto != null) {
			criteria.person(personReferenceDto);
		}
		buildWhereCondition(criteria, cb, cq, immunizationQueryContext);

		cq.orderBy(cb.desc(immunization.get(Immunization.CHANGE_DATE)));

		cq.distinct(true);

		return createQuery(cq, first, max).unwrap(org.hibernate.query.Query.class)
			.setResultTransformer(new ImmunizationListEntryDtoTransformer())
			.getResultList();
	}

	public boolean inJurisdictionOrOwned(Immunization immunization) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
		Root<Immunization> root = cq.from(Immunization.class);
		cq.multiselect(JurisdictionHelper.booleanSelector(cb, createUserFilter(new ImmunizationQueryContext<>(cb, cq, root))));
		cq.where(cb.equal(root.get(Immunization.UUID), immunization.getUuid()));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Immunization> immunizationPath) {
		return createUserFilter(new ImmunizationQueryContext(cb, cq, immunizationPath));
	}

	public Predicate createActiveImmunizationsFilter(CriteriaBuilder cb, From<?, Immunization> root) {
		return cb.and(cb.isFalse(root.get(Immunization.ARCHIVED)), cb.isFalse(root.get(Immunization.DELETED)));
	}

	public Predicate createDefaultFilter(CriteriaBuilder cb, From<?, Immunization> root) {
		return cb.isFalse(root.get(Immunization.DELETED));
	}

	@Override
	public Predicate createChangeDateFilter(CriteriaBuilder cb, From<?, Immunization> immunization, Timestamp date) {
		Join<Immunization, VaccinationEntity> vaccinations = immunization.join(Immunization.VACCINATIONS, JoinType.LEFT);

		return new ChangeDateFilterBuilder(cb, date).add(immunization).add(vaccinations).build();
	}

	public List<Immunization> getAllActiveAfter(Date date) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Immunization> cq = cb.createQuery(getElementClass());
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createDefaultFilter(cb, from);

		if (getCurrentUser() != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			if (userFilter != null) {
				filter = cb.and(filter, userFilter);
			}
		}

		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, DateHelper.toTimestampUpper(date));
			if (dateFilter != null) {
				filter = cb.and(filter, dateFilter);
			}
		}

		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Immunization.CHANGE_DATE)));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public boolean isArchived(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Immunization> from = cq.from(Immunization.class);

		cq.where(cb.and(cb.equal(from.get(Immunization.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	public List<String> getArchivedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(from.get(Immunization.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate archivedFilter = cb.equal(from.get(Immunization.ARCHIVED), true);
		if (filter != null) {
			filter = cb.and(filter, archivedFilter);
		} else {
			filter = archivedFilter;
		}

		cq.where(filter);
		cq.select(from.get(Immunization.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<String> getDeletedUuidsSince(Date since) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Immunization> from = cq.from(getElementClass());

		Predicate filter = createUserFilter(cb, cq, from);
		if (since != null) {
			Predicate dateFilter = cb.greaterThanOrEqualTo(from.get(Immunization.CHANGE_DATE), since);
			if (filter != null) {
				filter = cb.and(filter, dateFilter);
			} else {
				filter = dateFilter;
			}
		}

		Predicate deletedFilter = cb.equal(from.get(Immunization.DELETED), true);
		if (filter != null) {
			filter = cb.and(filter, deletedFilter);
		} else {
			filter = deletedFilter;
		}

		cq.where(filter);
		cq.select(from.get(Immunization.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Object[]> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<Immunization> from = cq.from(Immunization.class);

		ImmunizationQueryContext<Immunization> immunizationQueryContext = new ImmunizationQueryContext<>(cb, cq, from);
		ImmunizationJoins<Immunization> joins = (ImmunizationJoins<Immunization>) immunizationQueryContext.getJoins();

		cq.multiselect(
			from.get(Immunization.UUID),
			from.get(Immunization.MEANS_OF_IMMUNIZATION),
			from.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS),
			from.get(Immunization.IMMUNIZATION_STATUS),
			from.get(Immunization.START_DATE),
			from.get(Immunization.END_DATE),
			from.get(Immunization.RECOVERY_DATE),
			from.get(Immunization.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationQueryContext)));

		Predicate filter = createUserFilter(immunizationQueryContext);

		Predicate immunizationFilter =
			criteria.getImmunizationUuid() != null ? cb.notEqual(from.get(Immunization.UUID), criteria.getImmunizationUuid()) : null;

		Predicate diseaseFilter = criteria.getDisease() != null ? cb.equal(from.get(Immunization.DISEASE), criteria.getDisease()) : null;

		Predicate dateFilter = createDateFilter(cb, from, criteria);

		Predicate personSimilarityFilter =
			criteria.getPersonUuid() != null ? cb.equal(joins.getPerson().get(Person.UUID), criteria.getPersonUuid()) : null;

		Predicate notDeletedFilter = cb.isFalse(from.get(Immunization.DELETED));

		filter = CriteriaBuilderHelper.and(cb, filter, immunizationFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, diseaseFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, personSimilarityFilter);
		filter = CriteriaBuilderHelper.and(cb, filter, notDeletedFilter);

		cq.where(filter);

		cq.orderBy(cb.desc(from.get(Immunization.CHANGE_DATE)));

		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateImmunizationStatuses() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaUpdate<Immunization> cu = cb.createCriteriaUpdate(Immunization.class);
		Root<Immunization> root = cu.from(Immunization.class);

		cu.set(Immunization.CHANGE_DATE, Timestamp.from(Instant.now()));
		cu.set(root.get(Immunization.IMMUNIZATION_STATUS), ImmunizationStatus.EXPIRED);

		cu.where(
			cb.and(
				cb.equal(root.get(Immunization.IMMUNIZATION_STATUS), ImmunizationStatus.ACQUIRED),
				cb.lessThanOrEqualTo(root.get(Immunization.VALID_UNTIL), new Date())));

		em.createQuery(cu).executeUpdate();
	}

	private Predicate createUserFilter(ImmunizationQueryContext<Immunization> qc) {
		final User currentUser = userService.getCurrentUser();
		return ImmunizationJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}

	private Predicate createDateFilter(CriteriaBuilder cb, Root<Immunization> from, ImmunizationSimilarityCriteria criteria) {
		Date startDate = criteria.getStartDate();
		Date endDate = criteria.getEndDate();

		Predicate dateFilter = null;

		if (startDate != null && endDate != null) {
			dateFilter = cb.or(
				cb.and(cb.isNull(from.get(Immunization.END_DATE)), cb.between(from.get(Immunization.START_DATE), startDate, endDate)),
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.between(from.get(Immunization.END_DATE), startDate, endDate)),
				cb.and(
					cb.greaterThanOrEqualTo(from.get(Immunization.END_DATE), startDate),
					cb.lessThanOrEqualTo(from.get(Immunization.START_DATE), endDate)));
		} else if (startDate != null) {
			dateFilter = cb.or(
				cb.and(cb.isNull(from.get(Immunization.END_DATE)), cb.greaterThanOrEqualTo(from.get(Immunization.START_DATE), startDate)),
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.greaterThanOrEqualTo(from.get(Immunization.END_DATE), startDate)));
		} else if (endDate != null) {
			dateFilter = cb.or(
				cb.and(cb.isNull(from.get(Immunization.START_DATE)), cb.lessThanOrEqualTo(from.get(Immunization.END_DATE), endDate)),
				cb.and(cb.isNull(from.get(Immunization.END_DATE)), cb.lessThanOrEqualTo(from.get(Immunization.START_DATE), endDate)));
		}

		return dateFilter;
	}

	private <T> void buildWhereCondition(
		ImmunizationCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<T> cq,
		ImmunizationQueryContext<Immunization> immunizationQueryContext) {
		Predicate filter = createUserFilter(immunizationQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, immunizationQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
	}

	private Predicate buildCriteriaFilter(ImmunizationCriteria criteria, ImmunizationQueryContext<Immunization> immunizationQueryContext) {
		final ImmunizationJoins joins = (ImmunizationJoins) immunizationQueryContext.getJoins();
		final CriteriaBuilder cb = immunizationQueryContext.getCriteriaBuilder();
		final From<?, ?> from = immunizationQueryContext.getRoot();
		Join<Immunization, Person> person = joins.getPerson();

		final Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);

		Predicate filter = null;
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Immunization.DISEASE), criteria.getDisease()));
		}

		if (!DataHelper.isNullOrEmpty(criteria.getNameAddressPhoneEmailLike())) {
			final CriteriaQuery<PersonIndexDto> cq = cb.createQuery(PersonIndexDto.class);
			final PersonQueryContext personQueryContext = new PersonQueryContext(cb, cq, person);

			String[] textFilters = criteria.getNameAddressPhoneEmailLike().split("\\s+");

			for (String textFilter : textFilters) {
				if (DataHelper.isNullOrEmpty(textFilter)) {
					continue;
				}

				Predicate likeFilters = cb.or(
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.FIRST_NAME), textFilter),
					CriteriaBuilderHelper.unaccentedIlike(cb, person.get(Person.LAST_NAME), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.UUID), textFilter),
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
					CriteriaBuilderHelper.ilike(cb, person.get(Person.INTERNAL_TOKEN), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.EXTERNAL_ID), textFilter),
					CriteriaBuilderHelper.ilike(cb, person.get(Person.EXTERNAL_TOKEN), textFilter));
				filter = CriteriaBuilderHelper.and(cb, filter, likeFilters);
			}
		}
		filter = andEquals(cb, person, filter, criteria.getBirthdateYYYY(), Person.BIRTHDATE_YYYY);
		filter = andEquals(cb, person, filter, criteria.getBirthdateMM(), Person.BIRTHDATE_MM);
		filter = andEquals(cb, person, filter, criteria.getBirthdateDD(), Person.BIRTHDATE_DD);
		if (criteria.getMeansOfImmunization() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Immunization.MEANS_OF_IMMUNIZATION), criteria.getMeansOfImmunization()));
		}
		if (criteria.getManagementStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS), criteria.getManagementStatus()));
		}
		if (criteria.getImmunizationStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Immunization.IMMUNIZATION_STATUS), criteria.getImmunizationStatus()));
		}

		if (criteria.getPerson() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(person.get(Person.UUID), criteria.getPerson().getUuid()));
		}

		filter = andEqualsReferenceDto(cb, joins.getResponsibleRegion(), filter, criteria.getRegion());
		filter = andEqualsReferenceDto(cb, joins.getResponsibleDistrict(), filter, criteria.getDistrict());
		filter = andEqualsReferenceDto(cb, joins.getResponsibleCommunity(), filter, criteria.getCommunity());
		if (criteria.getFacilityType() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Immunization.FACILITY_TYPE), criteria.getFacilityType()));
		}
		filter = andEqualsReferenceDto(cb, joins.getHealthFacility(), filter, criteria.getHealthFacility());
		if (Boolean.TRUE.equals(criteria.getOnlyPersonsWithOverdueImmunization())) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.get(Immunization.IMMUNIZATION_MANAGEMENT_STATUS), ImmunizationManagementStatus.ONGOING));
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(from.get(Immunization.END_DATE), new Date()));
		}
		if (criteria.getImmunizationDateType() != null) {
			Path<Object> path = buildPathForDateFilter(criteria.getImmunizationDateType(), from);
			if (path != null) {
				filter = CriteriaBuilderHelper.applyDateFilter(cb, filter, path, criteria.getFromDate(), criteria.getToDate());
			}
		}
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Immunization.DELETED)));

		return filter;
	}

	private Path<Object> buildPathForDateFilter(ImmunizationDateType immunizationDateType, From<?, ?> defaultRoot) {
		Path<Object> path = null;
		String dateField = getDateFieldFromDateType(immunizationDateType);
		if (dateField != null) {
			if (LastVaccinationDate.VACCINATION_DATE.equals(dateField)) {
				final Join<Immunization, LastVaccinationDate> lastVaccinationDate =
					defaultRoot.join(Immunization.LAST_VACCINATION_DATE, JoinType.LEFT);
				path = lastVaccinationDate.get(LastVaccinationDate.VACCINATION_DATE);
			} else if (FirstVaccinationDate.VACCINATION_DATE.equals(dateField)) {
				final Join<Immunization, FirstVaccinationDate> firstVaccinationDate =
					defaultRoot.join(Immunization.FIRST_VACCINATION_DATE, JoinType.LEFT);
				path = firstVaccinationDate.get(FirstVaccinationDate.VACCINATION_DATE);
			} else {
				path = defaultRoot.get(dateField);
			}
		}
		return path;
	}

	private String getDateFieldFromDateType(ImmunizationDateType immunizationDateType) {
		switch (immunizationDateType) {
		case REPORT_DATE:
			return Immunization.REPORT_DATE;
		case IMMUNIZATION_END:
			return Immunization.END_DATE;
		case VALID_UNTIL:
			return Immunization.VALID_UNTIL;
		case RECOVERY_DATE:
			return Immunization.RECOVERY_DATE;
		case LAST_VACCINATION_DATE:
			return LastVaccinationDate.VACCINATION_DATE;
		case FIRST_VACCINATION_DATE:
			return FirstVaccinationDate.VACCINATION_DATE;
		}
		return null;
	}
}
