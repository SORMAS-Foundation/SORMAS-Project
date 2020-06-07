package de.symeda.sormas.backend.caze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class CaseListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private CaseService caseService;

	public CriteriaQuery<CaseIndexDto> buildIndexCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {
		return buildIndexCriteria(CaseIndexDto.class, this::getCaseIndexSelections, caseCriteria, this::getIndexOrders, sortProperties);
	}

	public CriteriaQuery<CaseIndexDetailedDto> buildIndexDetailedCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {

		return buildIndexCriteria(
			CaseIndexDetailedDto.class,
			this::getCaseIndexDetailedSelections,
			caseCriteria,
			this::getIndexDetailOrders,
			sortProperties);
	}

	private <T> CriteriaQuery<T> buildIndexCriteria(
		Class<T> type,
		BiFunction<Root<Case>, CaseJoins<Case>, List<Selection<?>>> selectionProvider,
		CaseCriteria caseCriteria,
		OrderExpressionProvider orderExpressionProvider,
		List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(type);
		Root<Case> caze = cq.from(Case.class);
		CaseJoins<Case> joins = new CaseJoins<>(caze);

		cq.multiselect(selectionProvider.apply(caze, joins));
		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				order.addAll(
					orderExpressionProvider.forProperty(sortProperty, caze, joins)
						.stream()
						.map(e -> sortProperty.ascending ? cb.asc(e) : cb.desc(e))
						.collect(Collectors.toList()));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(caze.get(Case.CHANGE_DATE)));
		}

		Predicate filter = caseService.createUserFilter(cb, cq, caze);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caze);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		return cq;
	}

	public List<Selection<?>> getCaseIndexSelections(Root<Case> root, CaseJoins<Case> joins) {

		return Arrays.asList(
			root.get(AbstractDomainObject.ID),
			root.get(Case.UUID),
			root.get(Case.EPID_NUMBER),
			root.get(Case.EXTERNAL_ID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			root.get(Case.DISEASE),
			root.get(Case.DISEASE_DETAILS),
			root.get(Case.CASE_CLASSIFICATION),
			root.get(Case.INVESTIGATION_STATUS),
			joins.getPerson().get(Person.PRESENT_CONDITION),
			root.get(Case.REPORT_DATE),
			joins.getReportingUser().get(User.UUID),
			root.get(AbstractDomainObject.CREATION_DATE),
			joins.getRegion().get(Region.UUID),
			joins.getDistrict().get(District.UUID),
			joins.getDistrict().get(District.NAME),
			joins.getCommunity().get(Community.UUID),
			joins.getFacility().get(Facility.UUID),
			joins.getFacility().get(Facility.NAME),
			root.get(Case.HEALTH_FACILITY_DETAILS),
			joins.getPointOfEntry().get(PointOfEntry.UUID),
			joins.getPointOfEntry().get(PointOfEntry.NAME),
			root.get(Case.POINT_OF_ENTRY_DETAILS),
			joins.getSurveillanceOfficer().get(User.UUID),
			root.get(Case.OUTCOME),
			joins.getPerson().get(Person.APPROXIMATE_AGE),
			joins.getPerson().get(Person.APPROXIMATE_AGE_TYPE),
			joins.getPerson().get(Person.BIRTHDATE_DD),
			joins.getPerson().get(Person.BIRTHDATE_MM),
			joins.getPerson().get(Person.BIRTHDATE_YYYY),
			joins.getPerson().get(Person.SEX),
			root.get(Case.QUARANTINE_TO),
			root.get(Case.COMPLETENESS));
	}

	private List<Expression<?>> getIndexOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins) {

		switch (sortProperty.propertyName) {
		case CaseIndexDto.ID:
		case CaseIndexDto.UUID:
		case CaseIndexDto.EPID_NUMBER:
		case CaseIndexDto.EXTERNAL_ID:
		case CaseIndexDto.DISEASE:
		case CaseIndexDto.DISEASE_DETAILS:
		case CaseIndexDto.CASE_CLASSIFICATION:
		case CaseIndexDto.INVESTIGATION_STATUS:
		case CaseIndexDto.REPORT_DATE:
		case CaseIndexDto.CREATION_DATE:
		case CaseIndexDto.OUTCOME:
		case CaseIndexDto.QUARANTINE_TO:
		case CaseIndexDto.COMPLETENESS:
			return Collections.singletonList(caze.get(sortProperty.propertyName));
		case CaseIndexDto.PERSON_FIRST_NAME:
			return Collections.singletonList(joins.getPerson().get(Person.FIRST_NAME));
		case CaseIndexDto.PERSON_LAST_NAME:
			return Collections.singletonList(joins.getPerson().get(Person.LAST_NAME));
		case CaseIndexDto.PRESENT_CONDITION:
		case CaseIndexDto.SEX:
			return Collections.singletonList(joins.getPerson().get(sortProperty.propertyName));
		case CaseIndexDto.AGE_AND_BIRTH_DATE:
			return Collections.singletonList(joins.getPerson().get(Person.APPROXIMATE_AGE));
		case CaseIndexDto.REGION_UUID:
			return Collections.singletonList(joins.getRegion().get(Region.UUID));
		case CaseIndexDto.DISTRICT_UUID:
			return Collections.singletonList(joins.getDistrict().get(District.UUID));
		case CaseIndexDto.DISTRICT_NAME:
			return Collections.singletonList(joins.getDistrict().get(District.NAME));
		case CaseIndexDto.HEALTH_FACILITY_UUID:
			return Collections.singletonList(joins.getFacility().get(Facility.UUID));
		case CaseIndexDto.HEALTH_FACILITY_NAME:
			return Arrays.asList(joins.getFacility(), caze.get(Case.HEALTH_FACILITY_DETAILS));
		case CaseIndexDto.POINT_OF_ENTRY_NAME:
			return Collections.singletonList(joins.getPointOfEntry().get(PointOfEntry.NAME));
		case CaseIndexDto.SURVEILLANCE_OFFICER_UUID:
			return Collections.singletonList(joins.getSurveillanceOfficer().get(User.UUID));
		default:
			throw new IllegalArgumentException(sortProperty.propertyName);
		}
	}

	private List<Selection<?>> getCaseIndexDetailedSelections(Root<Case> caze, CaseJoins<Case> joins) {

		List<Selection<?>> selections = new ArrayList<>(getCaseIndexSelections(caze, joins));
		selections.addAll(
			Arrays.asList(
				joins.getAddress().get(Location.CITY),
				joins.getAddress().get(Location.ADDRESS),
				joins.getAddress().get(Location.POSTAL_CODE),
				joins.getPerson().get(Person.PHONE),
				joins.getReportingUser().get(User.FIRST_NAME),
				joins.getReportingUser().get(User.LAST_NAME)));

		return selections;
	}

	private List<Expression<?>> getIndexDetailOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins) {

		switch (sortProperty.propertyName) {
		case CaseIndexDetailedDto.CITY:
		case CaseIndexDetailedDto.ADDRESS:
		case CaseIndexDetailedDto.POSTAL_CODE:
			return Collections.singletonList(joins.getAddress().get(sortProperty.propertyName));
		case CaseIndexDetailedDto.PHONE:
			return Collections.singletonList(joins.getPerson().get(sortProperty.propertyName));
		case CaseIndexDetailedDto.REPORTING_USER:
			return Arrays.asList(joins.getReportingUser().get(User.FIRST_NAME), joins.getReportingUser().get(User.LAST_NAME));
		default:
			return getIndexOrders(sortProperty, caze, joins);
		}
	}

	private interface OrderExpressionProvider {

		List<Expression<?>> forProperty(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins);
	}
}
