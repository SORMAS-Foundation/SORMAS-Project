package de.symeda.sormas.backend.immunization;

import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEquals;
import static de.symeda.sormas.backend.common.CriteriaBuilderHelper.andEqualsReferenceDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import de.symeda.sormas.api.immunization.ImmunizationManagementStatus;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.immunization.tramsformes.ImmunizationIndexDtoResultTransformer;
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

@Stateless
@LocalBean
public class ImmunizationDirectoryService extends AbstractCoreAdoService<ImmunizationDirectory> {

	@EJB
	private UserService userService;

	public ImmunizationDirectoryService() {
		super(ImmunizationDirectory.class);
	}

	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, ImmunizationDirectory> immunizationDirectoryPath) {
		return createUserFilter(new ImmunizationDirectoryQueryContext(cb, cq, immunizationDirectoryPath));
	}

	public List<ImmunizationIndexDto> getIndexList(ImmunizationCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		final Root<ImmunizationDirectory> immunization = cq.from(ImmunizationDirectory.class);

		ImmunizationDirectoryQueryContext<ImmunizationDirectory> immunizationDirectoryQueryContext =
			new ImmunizationDirectoryQueryContext<>(cb, cq, immunization);
		ImmunizationDirectoryJoins<ImmunizationDirectory> joins =
			(ImmunizationDirectoryJoins<ImmunizationDirectory>) immunizationDirectoryQueryContext.getJoins();

		final Join<ImmunizationDirectory, Person> person = joins.getPerson();

		final Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);
		final Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);

		final Join<ImmunizationDirectory, LastVaccineType> lastVaccineType = joins.getLastVaccineType();

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
			immunization.get(Immunization.CHANGE_DATE),
			JurisdictionHelper.booleanSelector(cb, createUserFilter(immunizationDirectoryQueryContext)));

		buildWhereCondition(criteria, cb, cq, immunizationDirectoryQueryContext);

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
		final Root<ImmunizationDirectory> immunization = cq.from(ImmunizationDirectory.class);

		ImmunizationDirectoryQueryContext<ImmunizationDirectory> immunizationQueryContext =
			new ImmunizationDirectoryQueryContext<>(cb, cq, immunization);

		buildWhereCondition(criteria, cb, cq, immunizationQueryContext);

		cq.select(cb.countDistinct(immunization));
		return em.createQuery(cq).getSingleResult();
	}

	private <T> void buildWhereCondition(
		ImmunizationCriteria criteria,
		CriteriaBuilder cb,
		CriteriaQuery<T> cq,
		ImmunizationDirectoryQueryContext<ImmunizationDirectory> immunizationDirectoryQueryContext) {
		Predicate filter = createUserFilter(immunizationDirectoryQueryContext);
		if (criteria != null) {
			final Predicate criteriaFilter = buildCriteriaFilter(criteria, immunizationDirectoryQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
	}

	private Predicate buildCriteriaFilter(
		ImmunizationCriteria criteria,
		ImmunizationDirectoryQueryContext<ImmunizationDirectory> immunizationDirectoryQueryContext) {
		final ImmunizationDirectoryJoins joins = (ImmunizationDirectoryJoins) immunizationDirectoryQueryContext.getJoins();
		final CriteriaBuilder cb = immunizationDirectoryQueryContext.getCriteriaBuilder();
		final From<?, ?> from = immunizationDirectoryQueryContext.getRoot();
		Join<ImmunizationDirectory, Person> person = joins.getPerson();

		final Join<Person, Location> location = person.join(Person.ADDRESS, JoinType.LEFT);

		Predicate filter = null;
		if (criteria.getDisease() != null) {
			filter = CriteriaBuilderHelper.and(cb, null, cb.equal(from.get(Immunization.DISEASE), criteria.getDisease()));
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
			Path<Object> path = buildPathForDateFilter(criteria.getImmunizationDateType(), immunizationDirectoryQueryContext);
			if (path != null) {
				filter = CriteriaBuilderHelper.applyDateFilter(cb, filter, path, criteria.getFromDate(), criteria.getToDate());
			}
		}
		filter = CriteriaBuilderHelper.and(cb, filter, cb.isFalse(from.get(Immunization.DELETED)));

		return filter;
	}

	private Path<Object> buildPathForDateFilter(
		ImmunizationDateType immunizationDateType,
		ImmunizationDirectoryQueryContext immunizationDirectoryQueryContext) {
		Path<Object> path = null;
		String dateField = getDateFieldFromDateType(immunizationDateType);
		if (dateField != null) {
			if (LastVaccinationDate.VACCINATION_DATE.equals(dateField)) {
				final Join<ImmunizationDirectory, LastVaccinationDate> lastVaccinationDate =
					((ImmunizationDirectoryJoins<ImmunizationDirectory>) immunizationDirectoryQueryContext.getJoins()).getLastVaccinationDate();
				path = lastVaccinationDate.get(LastVaccinationDate.VACCINATION_DATE);
			} else if (FirstVaccinationDate.VACCINATION_DATE.equals(dateField)) {
				final Join<ImmunizationDirectory, FirstVaccinationDate> firstVaccinationDate =
					((ImmunizationDirectoryJoins<ImmunizationDirectory>) immunizationDirectoryQueryContext.getJoins()).getFirstVaccinationDate();
				path = firstVaccinationDate.get(FirstVaccinationDate.VACCINATION_DATE);
			} else {
				path = immunizationDirectoryQueryContext.getRoot().get(dateField);
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

	private Predicate createUserFilter(ImmunizationDirectoryQueryContext<ImmunizationDirectory> qc) {
		final User currentUser = userService.getCurrentUser();
		return ImmunizationDirectoryJurisdictionPredicateValidator.of(qc, currentUser).inJurisdictionOrOwned();
	}
}
