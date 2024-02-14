package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
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
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.person.PersonContactDetailType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.ExtendedPostgreSQL94Dialect;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.facility.Facility;
import de.symeda.sormas.backend.infrastructure.pointofentry.PointOfEntry;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonContactDetail;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.symptoms.Symptoms;
import de.symeda.sormas.backend.user.CurrentUserService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless
@LocalBean
public class CaseListCriteriaBuilder {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private CaseService caseService;

	@Inject
	private CurrentUserService currentUserService;

	public CriteriaQuery<Tuple> buildIndexCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties, List<Long> ids) {
		return buildIndexCriteria(this::getCaseIndexSelections, caseCriteria, this::getIndexOrders, sortProperties, false, ids);
	}

	public CriteriaQuery<Tuple> buildIndexDetailedCriteria(CaseCriteria caseCriteria, List<SortProperty> sortProperties, List<Long> ids) {

		return buildIndexCriteria(this::getCaseIndexDetailedSelections, caseCriteria, this::getIndexDetailOrders, sortProperties, true, ids);
	}

	public CriteriaQuery<Tuple> buildIndexCriteriaPrefetchIds(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {
		return buildIndexCriteria(this::getCaseIndexSelections, caseCriteria, this::getIndexOrders, sortProperties, false, null);
	}

	public CriteriaQuery<Tuple> buildIndexDetailedCriteriaPrefetchIds(CaseCriteria caseCriteria, List<SortProperty> sortProperties) {

		return buildIndexCriteria(this::getCaseIndexDetailedSelections, caseCriteria, this::getIndexDetailOrders, sortProperties, true, null);
	}

	private CriteriaQuery<Tuple> buildIndexCriteria(
		BiFunction<Root<Case>, CaseQueryContext, List<Selection<?>>> selectionProvider,
		CaseCriteria caseCriteria,
		OrderExpressionProvider orderExpressionProvider,
		List<SortProperty> sortProperties,
		boolean detailed,
		List<Long> ids) {

		boolean prefetchIds = ids == null;

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Case> caze = cq.from(Case.class);
		final CaseQueryContext caseQueryContext = new CaseQueryContext(cb, cq, caze);
		final CaseJoins joins = caseQueryContext.getJoins();

		Subquery<Integer> visitCountSq = cq.subquery(Integer.class);
		Root<Case> visitCountRoot = visitCountSq.from(Case.class);
		visitCountSq.where(cb.equal(visitCountRoot.get(AbstractDomainObject.ID), caze.get(AbstractDomainObject.ID)));
		visitCountSq.select(cb.size(visitCountRoot.get(Case.VISITS)));

		List<Selection<?>> selectionList = new ArrayList<>();

		Expression<Date> latestChangedDateFunction =
			cb.function(ExtendedPostgreSQL94Dialect.GREATEST, Date.class, caze.get(Contact.CHANGE_DATE), joins.getPerson().get(Person.CHANGE_DATE));

		List<Selection<?>> orderBySelections = new ArrayList<>();
		if (!CollectionUtils.isEmpty(sortProperties)) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				List<Expression<?>> expressions = orderExpressionProvider.forProperty(sortProperty, caze, joins, cb);
				orderBySelections.addAll(expressions);
				order.addAll(expressions.stream().map(e -> sortProperty.ascending ? cb.asc(e) : cb.desc(e)).collect(Collectors.toList()));
			}
			cq.orderBy(order);
		} else {
			orderBySelections.add(latestChangedDateFunction);
			cq.orderBy(cb.desc(latestChangedDateFunction));
		}

		if (prefetchIds) {
			selectionList.add(caze.get(AbstractDomainObject.ID));
		} else {
			selectionList.addAll(selectionProvider.apply(caze, caseQueryContext));
			selectionList.add(visitCountSq);

			if (detailed) {
				// Events count subquery
				if (currentUserService.hasUserRight(UserRight.EVENT_VIEW)) {
					Subquery<Long> eventCountSq = cq.subquery(Long.class);
					Root<EventParticipant> eventCountRoot = eventCountSq.from(EventParticipant.class);
					Join<EventParticipant, Event> event = eventCountRoot.join(EventParticipant.EVENT, JoinType.INNER);
					Join<EventParticipant, Case> resultingCase = eventCountRoot.join(EventParticipant.RESULTING_CASE, JoinType.INNER);
					eventCountSq.where(
						cb.and(
							cb.equal(resultingCase.get(Case.ID), caze.get(Case.ID)),
							cb.isFalse(event.get(Event.DELETED)),
							cb.isFalse(eventCountRoot.get(EventParticipant.DELETED))));
					eventCountSq.select(cb.countDistinct(event.get(Event.ID)));
					selectionList.add(eventCountSq);
				}

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
					cb.equal(
						sampleCountRoot.join(Sample.ASSOCIATED_CASE, JoinType.LEFT).get(AbstractDomainObject.ID),
						caze.get(AbstractDomainObject.ID)),
					cb.isFalse(sampleCountRoot.get(Sample.DELETED)));
				sampleCountSq.select(cb.countDistinct(sampleCountRoot.get(AbstractDomainObject.ID)));
				selectionList.add(sampleCountSq);
			}
		}

		// include order by in the select
		selectionList.addAll(orderBySelections);

		cq.multiselect(selectionList);
		cq.distinct(true);

		CaseUserFilterCriteria caseUserFilterCriteria = new CaseUserFilterCriteria();
		if (caseCriteria != null) {
			caseUserFilterCriteria.setIncludeCasesFromOtherJurisdictions(caseCriteria.getIncludeCasesFromOtherJurisdictions());
		}

		Predicate filter = caseService.createUserFilter(caseQueryContext, caseUserFilterCriteria);

		if (!prefetchIds) {
			filter = CriteriaBuilderHelper.and(cb, filter, caze.get(AbstractDomainObject.ID).in(ids));
		}

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.createCriteriaFilter(caseCriteria, caseQueryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		return cq;
	}

	public List<Selection<?>> getCaseIndexSelections(From<?, Case> root, CaseQueryContext caseQueryContext) {

		final CaseJoins joins = caseQueryContext.getJoins();
		final CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		return Arrays.asList(
			root.get(AbstractDomainObject.ID),
			root.get(Case.UUID),
			root.get(Case.EPID_NUMBER),
			root.get(Case.EXTERNAL_ID),
			root.get(Case.EXTERNAL_TOKEN),
			root.get(Case.INTERNAL_TOKEN),
			joins.getPerson().get(Person.UUID),
			joins.getPerson().get(Person.FIRST_NAME),
			joins.getPerson().get(Person.LAST_NAME),
			root.get(Case.DISEASE),
			root.get(Case.DISEASE_VARIANT),
			root.get(Case.DISEASE_DETAILS),
			root.get(Case.CASE_CLASSIFICATION),
			root.get(Case.INVESTIGATION_STATUS),
			joins.getPerson().get(Person.PRESENT_CONDITION),
			root.get(Case.REPORT_DATE),
			root.get(AbstractDomainObject.CREATION_DATE),
			joins.getRegion().get(Region.UUID),
			joins.getDistrict().get(District.UUID),
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
			root.get(Case.VACCINATION_STATUS),
			root.get(Case.CHANGE_DATE),
			joins.getFacility().get(Facility.ID),
			joins.getResponsibleRegion().get(Region.UUID),
			joins.getResponsibleDistrict().get(District.UUID),
			joins.getResponsibleDistrict().get(District.NAME),
			root.get(Case.DELETION_REASON),
			root.get(Case.OTHER_DELETION_REASON),
			JurisdictionHelper.booleanSelector(cb, caseService.inJurisdictionOrOwned(caseQueryContext)));
	}

	private List<Expression<?>> getIndexOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins joins, CriteriaBuilder cb) {

		switch (sortProperty.propertyName) {
		case CaseIndexDto.PERSON_UUID:
			return Collections.singletonList(joins.getPerson().get(Person.UUID));
		case CaseIndexDto.ID:
		case CaseIndexDto.UUID:
		case CaseIndexDto.EPID_NUMBER:
		case CaseIndexDto.DISEASE:
		case CaseIndexDto.CASE_CLASSIFICATION:
		case CaseIndexDto.INVESTIGATION_STATUS:
		case CaseIndexDto.REPORT_DATE:
		case CaseIndexDto.CREATION_DATE:
		case CaseIndexDto.OUTCOME:
		case CaseIndexDetailedDto.RE_INFECTION:
		case CaseIndexDto.QUARANTINE_TO:
		case CaseIndexDto.COMPLETENESS:
		case CaseIndexDto.FOLLOW_UP_STATUS:
		case CaseIndexDto.FOLLOW_UP_UNTIL:
		case CaseIndexDto.VACCINATION_STATUS:
		case CaseIndexDto.DISEASE_VARIANT:
			return Collections.singletonList(caze.get(sortProperty.propertyName));
		case CaseIndexDto.EXTERNAL_ID:
		case CaseIndexDto.EXTERNAL_TOKEN:
		case CaseIndexDto.INTERNAL_TOKEN:
		case CaseIndexDto.DISEASE_DETAILS:
			return Collections.singletonList(cb.lower(caze.get(sortProperty.propertyName)));
		case CaseIndexDto.PERSON_FIRST_NAME:
			return Collections.singletonList(cb.lower(joins.getPerson().get(Person.FIRST_NAME)));
		case CaseIndexDto.PERSON_LAST_NAME:
			return Collections.singletonList(cb.lower(joins.getPerson().get(Person.LAST_NAME)));
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
		case CaseIndexDto.RESPONSIBLE_DISTRICT_NAME:
			return Collections.singletonList(cb.lower(joins.getResponsibleDistrict().get(District.NAME)));
		case CaseIndexDto.HEALTH_FACILITY_UUID:
			return Collections.singletonList(joins.getFacility().get(Facility.UUID));
		case CaseIndexDto.HEALTH_FACILITY_NAME:
			return Arrays.asList(joins.getFacility(), cb.lower(caze.get(Case.HEALTH_FACILITY_DETAILS)));
		case CaseIndexDto.POINT_OF_ENTRY_NAME:
			return Collections.singletonList(cb.lower(joins.getPointOfEntry().get(PointOfEntry.NAME)));
		case CaseIndexDto.SURVEILLANCE_OFFICER_UUID:
			return Collections.singletonList(joins.getSurveillanceOfficer().get(User.UUID));
		default:
			throw new IllegalArgumentException(sortProperty.propertyName);
		}
	}

	private List<Selection<?>> getCaseIndexDetailedSelections(Root<Case> caze, CaseQueryContext caseQueryContext) {

		CaseJoins joins = caseQueryContext.getJoins();

		Join<Person, PersonContactDetail> phone = joins.getPersonJoins().getPhone();
		CriteriaBuilder cb = caseQueryContext.getCriteriaBuilder();
		phone.on(
			cb.and(
				cb.isTrue(phone.get(PersonContactDetail.PRIMARY_CONTACT)),
				cb.equal(phone.get(PersonContactDetail.PERSON_CONTACT_DETAIL_TYPE), PersonContactDetailType.PHONE)));

		List<Selection<?>> selections = new ArrayList<>(getCaseIndexSelections(caze, caseQueryContext));
		selections.addAll(
			Arrays.asList(
				caze.get(Case.RE_INFECTION),
				joins.getPersonAddress().get(Location.CITY),
				joins.getPersonAddress().get(Location.STREET),
				joins.getPersonAddress().get(Location.HOUSE_NUMBER),
				joins.getPersonAddress().get(Location.ADDITIONAL_INFORMATION),
				joins.getPersonAddress().get(Location.POSTAL_CODE),
				phone.get(PersonContactDetail.CONTACT_INFORMATION),
				joins.getReportingUser().get(User.UUID),
				joins.getReportingUser().get(User.FIRST_NAME),
				joins.getReportingUser().get(User.LAST_NAME),
				joins.getSymptoms().get(Symptoms.ONSET_DATE),
				joins.getResponsibleRegion().get(Region.NAME),
				joins.getResponsibleCommunity().get(Community.NAME)));

		return selections;
	}

	private List<Expression<?>> getIndexDetailOrders(SortProperty sortProperty, Root<Case> caze, CaseJoins joins, CriteriaBuilder cb) {

		switch (sortProperty.propertyName) {
		case CaseIndexDetailedDto.CITY:
		case CaseIndexDetailedDto.STREET:
		case CaseIndexDetailedDto.HOUSE_NUMBER:
		case CaseIndexDetailedDto.ADDITIONAL_INFORMATION:
		case CaseIndexDetailedDto.POSTAL_CODE:
			return Collections.singletonList(cb.lower(joins.getPersonAddress().get(sortProperty.propertyName)));
		case CaseIndexDetailedDto.PHONE:
			return Collections.singletonList(joins.getPersonJoins().getPhone().get(PersonContactDetail.CONTACT_INFORMATION));
		case CaseIndexDetailedDto.REPORTING_USER:
			return Arrays.asList(cb.lower(joins.getReportingUser().get(User.FIRST_NAME)), cb.lower(joins.getReportingUser().get(User.LAST_NAME)));
		case CaseIndexDetailedDto.SYMPTOM_ONSET_DATE:
			return Collections.singletonList(joins.getSymptoms().get(Symptoms.ONSET_DATE));
		case CaseIndexDetailedDto.RESPONSIBLE_REGION:
			return Collections.singletonList(cb.lower(joins.getResponsibleRegion().get(Region.NAME)));
		case CaseIndexDetailedDto.RESPONSIBLE_COMMUNITY:
			return Collections.singletonList(cb.lower(joins.getResponsibleCommunity().get(Community.NAME)));
		default:
			return getIndexOrders(sortProperty, caze, joins, cb);
		}
	}

	private interface OrderExpressionProvider {

		List<Expression<?>> forProperty(SortProperty sortProperty, Root<Case> caze, CaseJoins joins, CriteriaBuilder cb);
	}
}
