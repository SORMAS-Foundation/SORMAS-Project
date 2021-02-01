package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.utils.CaseJoins;

@Stateless
@LocalBean
public class CaseListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private CaseService caseService;

	public CriteriaQuery<CaseIndexDto> buildIndexCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {
		return buildIndexCriteria(CaseIndexDto.class, this::getCaseIndexSelections, caseCriteria, this::getIndexOrders, sortProperties, false);
	}

	public CriteriaQuery<CaseIndexDetailedDto> buildIndexDetailedCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {

		return buildIndexCriteria(
			CaseIndexDetailedDto.class,
			this::getCaseIndexDetailedSelections,
			caseCriteria,
			this::getIndexDetailOrders,
			sortProperties,
			true);
	}

	private <T> CriteriaQuery<T> buildIndexCriteria(
		Class<T> type,
		BiFunction<Root<Case>, CaseJoins<Case>, List<Selection<?>>> selectionProvider,
		CaseCriteria caseCriteria,
		OrderExpressionProvider orderExpressionProvider,
		List<SortProperty> sortProperties,
		boolean detailed) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(type);
		Root<Case> caze = cq.from(Case.class);
		CaseJoins<Case> joins = new CaseJoins<>(caze);

		List<Selection<?>> selectionList = new ArrayList<>(selectionProvider.apply(caze, joins));

		Subquery<Integer> visitCountSq = cq.subquery(Integer.class);
		Root<Case> visitCountRoot = visitCountSq.from(Case.class);
		visitCountSq.where(cb.equal(visitCountRoot.get(AbstractDomainObject.ID), caze.get(AbstractDomainObject.ID)));
		visitCountSq.select(cb.size(visitCountRoot.get(Case.VISITS)));
		selectionList.add(visitCountSq);

		if (detailed) {
			// Events count subquery
			Subquery<Long> eventCountSq = cq.subquery(Long.class);
			Root<EventParticipant> eventCountRoot = eventCountSq.from(EventParticipant.class);
			Join<EventParticipant, Event> event = eventCountRoot.join(EventParticipant.EVENT, JoinType.INNER);
			Join<EventParticipant, Case> resultingCase = eventCountRoot.join(EventParticipant.RESULTING_CASE, JoinType.INNER);
			eventCountSq.where(
				cb.and(
					cb.equal(resultingCase.get(Case.ID), caze.get(Case.ID)),
					cb.isFalse(event.get(Event.DELETED)),
					cb.isFalse(event.get(Event.ARCHIVED)),
					cb.isFalse(eventCountRoot.get(EventParticipant.DELETED))));
			eventCountSq.select(cb.countDistinct(event.get(Event.ID)));
			selectionList.add(eventCountSq);

			// Latest sampleDateTime subquery
			Subquery<Timestamp> latestSampleDateTimeSq = cq.subquery(Timestamp.class);
			Root<Sample> sample = latestSampleDateTimeSq.from(Sample.class);
			Path<Timestamp> sampleDateTime = sample.get(Sample.SAMPLE_DATE_TIME);
			latestSampleDateTimeSq.where(
				cb.equal(sample.join(Sample.ASSOCIATED_CASE, JoinType.LEFT).get(AbstractDomainObject.ID), caze.get(AbstractDomainObject.ID)),
				cb.isFalse(sample.get(Sample.DELETED)));
			latestSampleDateTimeSq.select(cb.greatest(sampleDateTime));
			selectionList.add(latestSampleDateTimeSq);

			// Samples count subquery
			Subquery<Long> sampleCountSq = cq.subquery(Long.class);
			Root<Sample> sampleCountRoot = sampleCountSq.from(Sample.class);
			sampleCountSq.where(
				cb.equal(sampleCountRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT).get(AbstractDomainObject.ID), caze.get(AbstractDomainObject.ID)),
				cb.isFalse(sampleCountRoot.get(Sample.DELETED)));
			sampleCountSq.select(cb.countDistinct(sampleCountRoot.get(AbstractDomainObject.ID)));
			selectionList.add(sampleCountSq);
		}

		cq.multiselect(selectionList);
		cq.distinct(true);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
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

		CaseUserFilterCriteria caseUserFilterCriteria = new CaseUserFilterCriteria();
		if (caseCriteria != null) {
			caseUserFilterCriteria.setIncludeCasesFromOtherJurisdictions(caseCriteria.getIncludeCasesFromOtherJurisdictions());
		}
		Predicate filter = caseService.createUserFilter(cb, cq, caze, caseUserFilterCriteria);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, cb, cq, caze, joins);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
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
			root.get(Case.EXTERNAL_TOKEN),
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
			root.get(Case.COMPLETENESS),
			root.get(Case.FOLLOW_UP_STATUS),
			root.get(Case.FOLLOW_UP_UNTIL),
			joins.getPerson().get(Person.SYMPTOM_JOURNAL_STATUS),
			root.get(Case.CHANGE_DATE),
			joins.getFacility().get(Facility.ID));
	}

	private List<Expression<?>> getIndexOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins) {

		switch (sortProperty.propertyName) {
		case CaseIndexDto.ID:
		case CaseIndexDto.UUID:
		case CaseIndexDto.EPID_NUMBER:
		case CaseIndexDto.EXTERNAL_ID:
		case CaseIndexDto.EXTERNAL_TOKEN:
		case CaseIndexDto.DISEASE:
		case CaseIndexDto.DISEASE_DETAILS:
		case CaseIndexDto.CASE_CLASSIFICATION:
		case CaseIndexDto.INVESTIGATION_STATUS:
		case CaseIndexDto.REPORT_DATE:
		case CaseIndexDto.CREATION_DATE:
		case CaseIndexDto.OUTCOME:
		case CaseIndexDto.QUARANTINE_TO:
		case CaseIndexDto.COMPLETENESS:
		case CaseIndexDto.FOLLOW_UP_STATUS:
		case CaseIndexDto.FOLLOW_UP_UNTIL:
			return Collections.singletonList(caze.get(sortProperty.propertyName));
		case CaseIndexDto.PERSON_FIRST_NAME:
			return Collections.singletonList(joins.getPerson().get(Person.FIRST_NAME));
		case CaseIndexDto.PERSON_LAST_NAME:
			return Collections.singletonList(joins.getPerson().get(Person.LAST_NAME));
		case CaseIndexDto.PRESENT_CONDITION:
		case CaseIndexDto.SEX:
		case ContactIndexDto.SYMPTOM_JOURNAL_STATUS:
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
				joins.getAddress().get(Location.STREET),
				joins.getAddress().get(Location.HOUSE_NUMBER),
				joins.getAddress().get(Location.ADDITIONAL_INFORMATION),
				joins.getAddress().get(Location.POSTAL_CODE),
				joins.getPerson().get(Person.PHONE),
				joins.getReportingUser().get(User.FIRST_NAME),
				joins.getReportingUser().get(User.LAST_NAME),
				joins.getSymptoms().get(Symptoms.ONSET_DATE)));

		return selections;
	}

	private List<Expression<?>> getIndexDetailOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins) {

		switch (sortProperty.propertyName) {
		case CaseIndexDetailedDto.CITY:
		case CaseIndexDetailedDto.STREET:
		case CaseIndexDetailedDto.HOUSE_NUMBER:
		case CaseIndexDetailedDto.ADDITIONAL_INFORMATION:
		case CaseIndexDetailedDto.POSTAL_CODE:
			return Collections.singletonList(joins.getAddress().get(sortProperty.propertyName));
		case CaseIndexDetailedDto.PHONE:
			return Collections.singletonList(joins.getPerson().get(sortProperty.propertyName));
		case CaseIndexDetailedDto.REPORTING_USER:
			return Arrays.asList(joins.getReportingUser().get(User.FIRST_NAME), joins.getReportingUser().get(User.LAST_NAME));
		case CaseIndexDetailedDto.SYMPTOM_ONSET_DATE:
			return Collections.singletonList(joins.getSymptoms().get(Symptoms.ONSET_DATE));
		default:
			return getIndexOrders(sortProperty, caze, joins);
		}
	}

	public Stream<Selection<?>> getJurisdictionSelections(CaseJoins<Case> joins) {

		return Stream.of(
			joins.getReportingUser().get(User.UUID),
			joins.getRegion().get(Region.UUID),
			joins.getDistrict().get(District.UUID),
			joins.getCommunity().get(Community.UUID),
			joins.getFacility().get(Facility.UUID),
			joins.getPointOfEntry().get(PointOfEntry.UUID));
	}

	private interface OrderExpressionProvider {

		List<Expression<?>> forProperty(SortProperty sortProperty, Root<Case> caze, CaseJoins<Case> joins);
	}
}
