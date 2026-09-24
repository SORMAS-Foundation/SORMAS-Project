/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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
package de.symeda.sormas.backend.event.sevenoneseven;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.sevenoneseven.Event717EarlyResponseAction;
import de.symeda.sormas.api.event.sevenoneseven.Event717ExportDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717IndexDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.event.sevenoneseven.Event717OutcomeCountsDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717SummaryDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessStatus;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventJoins;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.event.EventUserFilterCriteria;
import de.symeda.sormas.backend.infrastructure.community.Community;
import de.symeda.sormas.backend.infrastructure.district.District;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.JurisdictionHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class Event717AssessmentService extends BaseAdoService<Event717Assessment> {

	/**
	 * The status columns in the order in which {@link #getSummary(EventCriteria)} reads them: the intervals in enum order, then the
	 * overall status.
	 */
	private static final String[] SUMMARY_STATUS_PROPERTIES = {
		Event717Assessment.DETECTION_STATUS,
		Event717Assessment.NOTIFICATION_STATUS,
		Event717Assessment.RESPONSE_STATUS,
		Event717Assessment.TIMELINESS_STATUS };

	@EJB
	private EventService eventService;

	public Event717AssessmentService() {
		super(Event717Assessment.class);
	}

	public Event717Assessment getByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event717Assessment> cq = cb.createQuery(Event717Assessment.class);
		Root<Event717Assessment> from = cq.from(Event717Assessment.class);
		cq.where(cb.equal(from.get(Event717Assessment.EVENT), event));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public Event717Assessment getByEventUuid(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event717Assessment> cq = cb.createQuery(Event717Assessment.class);
		Root<Event717Assessment> from = cq.from(Event717Assessment.class);
		Join<Event717Assessment, Event> eventJoin = from.join(Event717Assessment.EVENT);
		cq.where(cb.equal(eventJoin.get(AbstractDomainObject.UUID), eventUuid));

		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}

	public void deletePermanentByEvent(Event event) {

		Event717Assessment assessment = getByEvent(event);
		if (assessment != null) {
			deletePermanent(assessment);
		}
	}

	@Override
	public boolean deletePermanent(Event717Assessment assessment) {

		// Corrective actions may reference bottlenecks of the same assessment; unlink them first so the cascaded delete
		// does not depend on the order in which the child rows are removed
		assessment.getCorrectiveActions().forEach(action -> action.setBottleneck(null));
		em.flush();

		return super.deletePermanent(assessment);
	}

	/**
	 * Filters the assessments by the event criteria. The 7-1-7 criteria are only evaluated here, not by the event list.
	 */
	public Predicate buildEventCriteriaFilter(EventCriteria criteria, Event717AssessmentQueryContext queryContext) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		Event717AssessmentJoins joins = queryContext.getJoins();
		From<?, Event717Assessment> assessment = joins.getRoot();

		Predicate filter = eventService.buildCriteriaFilter(criteria, new EventQueryContext(cb, queryContext.getQuery(), joins.getEventJoins()));

		if (criteria.getEvent717Status() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(assessment.get(Event717Assessment.TIMELINESS_STATUS), criteria.getEvent717Status()));
		}
		if (criteria.getEvent717DetectionStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(assessment.get(Event717Assessment.DETECTION_STATUS), criteria.getEvent717DetectionStatus()));
		}
		if (criteria.getEvent717NotificationStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(assessment.get(Event717Assessment.NOTIFICATION_STATUS), criteria.getEvent717NotificationStatus()));
		}
		if (criteria.getEvent717ResponseStatus() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(assessment.get(Event717Assessment.RESPONSE_STATUS), criteria.getEvent717ResponseStatus()));
		}

		return filter;
	}

	/**
	 * Restricts the assessments to the events the user can see in the event directory and to the criteria. Without criteria, the
	 * default event criteria apply, e.g. deleted events are excluded.
	 */
	private Predicate buildFilter(EventCriteria criteria, Event717AssessmentQueryContext queryContext) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		EventCriteria eventCriteria = criteria != null ? criteria : new EventCriteria();

		Predicate filter = null;
		if (!Boolean.FALSE.equals(eventCriteria.getUserFilterIncluded())) {
			// like the event directory, also include events the user can see through their cases and event participants
			EventUserFilterCriteria userFilterCriteria = new EventUserFilterCriteria();
			userFilterCriteria.includeUserCaseAndEventParticipantFilter(true);
			filter = eventService.createUserFilter(
				new EventQueryContext(cb, queryContext.getQuery(), queryContext.getJoins().getEventJoins()),
				userFilterCriteria);
		}
		return CriteriaBuilderHelper.and(cb, filter, buildEventCriteriaFilter(eventCriteria, queryContext));
	}

	public List<Event717IndexDto> getIndexList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getIndexListIds(criteria, first, max, sortProperties);

		List<Event717IndexDto> assessments = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Event717IndexDto> cq = cb.createQuery(Event717IndexDto.class);
			Root<Event717Assessment> assessment = cq.from(getElementClass());
			Event717AssessmentQueryContext queryContext = new Event717AssessmentQueryContext(cb, cq, assessment);
			Join<Event717Assessment, Event> event = queryContext.getJoins().getEvent();
			EventJoins eventJoins = queryContext.getJoins().getEventJoins();

			cq.multiselect(
				event.get(Event.UUID),
				event.get(Event.EVENT_TITLE),
				event.get(Event.DISEASE),
				event.get(Event.DISEASE_DETAILS),
				assessment.get(Event717Assessment.DATE_OF_EMERGENCE),
				eventJoins.getRegion().get(Region.NAME),
				eventJoins.getDistrict().get(District.NAME),
				eventJoins.getCommunity().get(Community.NAME),
				assessment.get(Event717Assessment.DETECTION_DAYS),
				assessment.get(Event717Assessment.DETECTION_STATUS),
				assessment.get(Event717Assessment.NOTIFICATION_DAYS),
				assessment.get(Event717Assessment.NOTIFICATION_STATUS),
				assessment.get(Event717Assessment.INVESTIGATION_DAYS),
				assessment.get(Event717Assessment.INVESTIGATION_NOT_APPLICABLE),
				assessment.get(Event717Assessment.EPI_ANALYSIS_DAYS),
				assessment.get(Event717Assessment.EPI_ANALYSIS_NOT_APPLICABLE),
				assessment.get(Event717Assessment.LAB_CONFIRMATION_DAYS),
				assessment.get(Event717Assessment.LAB_CONFIRMATION_NOT_APPLICABLE),
				assessment.get(Event717Assessment.CASE_MANAGEMENT_DAYS),
				assessment.get(Event717Assessment.CASE_MANAGEMENT_NOT_APPLICABLE),
				assessment.get(Event717Assessment.COUNTERMEASURES_DAYS),
				assessment.get(Event717Assessment.COUNTERMEASURES_NOT_APPLICABLE),
				assessment.get(Event717Assessment.RISK_COMMUNICATION_DAYS),
				assessment.get(Event717Assessment.RISK_COMMUNICATION_NOT_APPLICABLE),
				assessment.get(Event717Assessment.COORDINATION_DAYS),
				assessment.get(Event717Assessment.COORDINATION_NOT_APPLICABLE),
				assessment.get(Event717Assessment.RESPONSE_DAYS),
				assessment.get(Event717Assessment.RESPONSE_STATUS),
				assessment.get(Event717Assessment.TIMELINESS_STATUS));

			cq.where(assessment.get(AbstractDomainObject.ID).in(batchedIds));
			cq.orderBy(getOrderList(sortProperties, queryContext));

			// paging was already applied when fetching the ids
			assessments.addAll(QueryHelper.getResultList(em, cq, null, null));
		});

		return assessments;
	}

	private List<Long> getIndexListIds(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Event717Assessment> assessment = cq.from(getElementClass());
		Event717AssessmentQueryContext queryContext = new Event717AssessmentQueryContext(cb, cq, assessment);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(assessment.get(AbstractDomainObject.ID));

		List<Order> orderList = getOrderList(sortProperties, queryContext);
		selections.addAll(orderList.stream().map(Order::getExpression).collect(Collectors.toList()));
		cq.multiselect(selections);

		Predicate filter = buildFilter(criteria, queryContext);
		if (filter != null) {
			cq.where(filter);
		}

		// the event criteria may join to-many relations of the event
		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, Event717AssessmentQueryContext queryContext) {

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		From<?, Event717Assessment> assessment = queryContext.getRoot();
		Join<Event717Assessment, Event> event = queryContext.getJoins().getEvent();
		EventJoins eventJoins = queryContext.getJoins().getEventJoins();

		List<Order> orderList = new ArrayList<>();
		if (sortProperties == null || sortProperties.isEmpty()) {
			orderList.add(cb.desc(assessment.get(AbstractDomainObject.CHANGE_DATE)));
			// a unique order keeps the pages of the grid and the export stable
			orderList.add(cb.desc(assessment.get(AbstractDomainObject.ID)));
			return orderList;
		}

		for (SortProperty sortProperty : sortProperties) {
			Expression<?> expression;
			switch (sortProperty.propertyName) {
			case Event717IndexDto.EVENT_UUID:
				expression = event.get(Event.UUID);
				break;
			case Event717IndexDto.EVENT_TITLE:
				expression = cb.lower(event.get(Event.EVENT_TITLE));
				break;
			case Event717IndexDto.EVENT_DISEASE:
				expression = event.get(Event.DISEASE);
				break;
			case Event717IndexDto.REGION:
				expression = cb.lower(eventJoins.getRegion().get(Region.NAME));
				break;
			case Event717IndexDto.DISTRICT:
				expression = cb.lower(eventJoins.getDistrict().get(District.NAME));
				break;
			case Event717IndexDto.COMMUNITY:
				expression = cb.lower(eventJoins.getCommunity().get(Community.NAME));
				break;
			case Event717IndexDto.DATE_OF_EMERGENCE:
			case Event717IndexDto.DETECTION_DAYS:
			case Event717IndexDto.DETECTION_STATUS:
			case Event717IndexDto.NOTIFICATION_DAYS:
			case Event717IndexDto.NOTIFICATION_STATUS:
			case Event717IndexDto.INVESTIGATION_DAYS:
			case Event717IndexDto.EPI_ANALYSIS_DAYS:
			case Event717IndexDto.LAB_CONFIRMATION_DAYS:
			case Event717IndexDto.CASE_MANAGEMENT_DAYS:
			case Event717IndexDto.COUNTERMEASURES_DAYS:
			case Event717IndexDto.RISK_COMMUNICATION_DAYS:
			case Event717IndexDto.COORDINATION_DAYS:
			case Event717IndexDto.RESPONSE_DAYS:
			case Event717IndexDto.RESPONSE_STATUS:
			case Event717IndexDto.TIMELINESS_STATUS:
				// these index properties are named like the stored properties of the assessment
				expression = assessment.get(sortProperty.propertyName);
				break;
			default:
				throw new IllegalArgumentException(sortProperty.propertyName);
			}
			orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
		}
		orderList.add(cb.desc(assessment.get(AbstractDomainObject.ID)));

		return orderList;
	}

	public long count(EventCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event717Assessment> assessment = cq.from(getElementClass());
		Event717AssessmentQueryContext queryContext = new Event717AssessmentQueryContext(cb, cq, assessment);

		Predicate filter = buildFilter(criteria, queryContext);
		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(assessment.get(AbstractDomainObject.ID)));

		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * Counts the stored timeliness results of the assessments matching the criteria in a single aggregate query. Every result is
	 * counted as distinct assessments, because the event criteria may join to-many relations of the event.
	 */
	public Event717SummaryDto getSummary(EventCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Event717Assessment> assessment = cq.from(getElementClass());
		Event717AssessmentQueryContext queryContext = new Event717AssessmentQueryContext(cb, cq, assessment);
		Expression<Long> id = assessment.get(AbstractDomainObject.ID);

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(cb.countDistinct(id));
		for (String statusProperty : SUMMARY_STATUS_PROPERTIES) {
			Expression<Event717TimelinessStatus> status = assessment.get(statusProperty);
			selections.add(countIf(cb, id, cb.equal(status, Event717TimelinessStatus.WITHIN_TARGET)));
			selections.add(countIf(cb, id, cb.equal(status, Event717TimelinessStatus.OVER_TARGET)));
			selections.add(countIf(cb, id, cb.or(cb.equal(status, Event717TimelinessStatus.MISSING), cb.isNull(status))));
			selections.add(countIf(cb, id, cb.equal(status, Event717TimelinessStatus.INCOMPLETE)));
			selections.add(countIf(cb, id, cb.equal(status, Event717TimelinessStatus.DATA_ERROR)));
		}
		// early response actions are compared to the target of the response interval, see
		// Event717TimelinessCalculator.calculateEarlyResponseActionStatus
		int target = Event717Interval.RESPONSE.getTargetDays();
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			Expression<Integer> days = assessment.get(Event717IndexDto.getEarlyResponseActionDaysProperty(action));
			Expression<Boolean> notApplicable = assessment.get(getEarlyResponseActionNotApplicableProperty(action));
			Predicate applicable = cb.isFalse(notApplicable);
			selections.add(countIf(cb, id, cb.and(applicable, cb.between(days, 0, target))));
			selections.add(countIf(cb, id, cb.and(applicable, cb.greaterThan(days, target))));
			selections.add(countIf(cb, id, cb.and(applicable, cb.isNull(days))));
			selections.add(countIf(cb, id, cb.and(applicable, cb.lessThan(days, 0))));
			selections.add(countIf(cb, id, cb.isTrue(notApplicable)));
		}
		cq.multiselect(selections);

		Predicate filter = buildFilter(criteria, queryContext);
		if (filter != null) {
			cq.where(filter);
		}

		// the counts are read in the order in which they were selected
		Iterator<Object> counts = Arrays.asList(em.createQuery(cq).getSingleResult().toArray()).iterator();
		IntSupplier next = () -> toInt(counts.next());

		Event717SummaryDto summary = new Event717SummaryDto(next.getAsInt());
		for (Event717Interval interval : Event717Interval.values()) {
			summary.setInterval(interval, new Event717OutcomeCountsDto(next.getAsInt(), next.getAsInt(), next.getAsInt(), next.getAsInt(), next.getAsInt(), 0));
		}
		summary.setAllTargets(new Event717OutcomeCountsDto(next.getAsInt(), next.getAsInt(), next.getAsInt(), next.getAsInt(), next.getAsInt(), 0));
		for (Event717EarlyResponseAction action : Event717EarlyResponseAction.values()) {
			int withinTarget = next.getAsInt();
			int overTarget = next.getAsInt();
			int missing = next.getAsInt();
			int dataError = next.getAsInt();
			int notApplicable = next.getAsInt();
			summary.setEarlyResponseAction(action, new Event717OutcomeCountsDto(withinTarget, overTarget, missing, 0, dataError, notApplicable));
		}
		return summary;
	}

	private static Expression<Long> countIf(CriteriaBuilder cb, Expression<Long> id, Predicate condition) {
		return cb.countDistinct(cb.<Long> selectCase().when(condition, id));
	}

	private static int toInt(Object count) {
		return count != null ? ((Number) count).intValue() : 0;
	}

	private static String getEarlyResponseActionNotApplicableProperty(Event717EarlyResponseAction action) {

		switch (action) {
		case INVESTIGATION:
			return Event717Assessment.INVESTIGATION_NOT_APPLICABLE;
		case EPI_ANALYSIS:
			return Event717Assessment.EPI_ANALYSIS_NOT_APPLICABLE;
		case LAB_CONFIRMATION:
			return Event717Assessment.LAB_CONFIRMATION_NOT_APPLICABLE;
		case CASE_MANAGEMENT:
			return Event717Assessment.CASE_MANAGEMENT_NOT_APPLICABLE;
		case COUNTERMEASURES:
			return Event717Assessment.COUNTERMEASURES_NOT_APPLICABLE;
		case RISK_COMMUNICATION:
			return Event717Assessment.RISK_COMMUNICATION_NOT_APPLICABLE;
		case COORDINATION:
			return Event717Assessment.COORDINATION_NOT_APPLICABLE;
		default:
			throw new IllegalArgumentException(action.name());
		}
	}

	public List<Event717ExportDto> getExportList(EventCriteria criteria, Integer first, Integer max) {

		// the ids query applies the filters, removes duplicates caused by joined event participants and does the paging
		List<Long> exportListIds = getIndexListIds(criteria, first, max, null);

		List<Event717ExportDto> assessments = new ArrayList<>();
		IterableHelper.executeBatched(exportListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			assessments.addAll(getExportListByIds(batchedIds));
		});
		return assessments;
	}

	private List<Event717ExportDto> getExportListByIds(List<Long> ids) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Event717ExportDto> cq = cb.createQuery(Event717ExportDto.class);
		Root<Event717Assessment> assessment = cq.from(getElementClass());
		Event717AssessmentQueryContext queryContext = new Event717AssessmentQueryContext(cb, cq, assessment);
		Join<Event717Assessment, Event> event = queryContext.getJoins().getEvent();
		EventJoins eventJoins = queryContext.getJoins().getEventJoins();

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EVENT_TITLE),
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.EVENT_STATUS),
			eventJoins.getRegion().get(Region.NAME),
			eventJoins.getDistrict().get(District.NAME),
			eventJoins.getCommunity().get(Community.NAME),
			assessment.get(Event717Assessment.DATE_OF_EMERGENCE),
			assessment.get(Event717Assessment.DATE_OF_DETECTION),
			assessment.get(Event717Assessment.DATE_OF_NOTIFICATION),
			assessment.get(Event717Assessment.DETECTION_DAYS),
			assessment.get(Event717Assessment.DETECTION_STATUS),
			assessment.get(Event717Assessment.NOTIFICATION_DAYS),
			assessment.get(Event717Assessment.NOTIFICATION_STATUS),
			assessment.get(Event717Assessment.INVESTIGATION_DATE),
			assessment.get(Event717Assessment.INVESTIGATION_NOT_APPLICABLE),
			assessment.get(Event717Assessment.INVESTIGATION_DAYS),
			assessment.get(Event717Assessment.EPI_ANALYSIS_DATE),
			assessment.get(Event717Assessment.EPI_ANALYSIS_NOT_APPLICABLE),
			assessment.get(Event717Assessment.EPI_ANALYSIS_DAYS),
			assessment.get(Event717Assessment.LAB_CONFIRMATION_DATE),
			assessment.get(Event717Assessment.LAB_CONFIRMATION_NOT_APPLICABLE),
			assessment.get(Event717Assessment.LAB_CONFIRMATION_DAYS),
			assessment.get(Event717Assessment.CASE_MANAGEMENT_DATE),
			assessment.get(Event717Assessment.CASE_MANAGEMENT_NOT_APPLICABLE),
			assessment.get(Event717Assessment.CASE_MANAGEMENT_DAYS),
			assessment.get(Event717Assessment.COUNTERMEASURES_DATE),
			assessment.get(Event717Assessment.COUNTERMEASURES_NOT_APPLICABLE),
			assessment.get(Event717Assessment.COUNTERMEASURES_DAYS),
			assessment.get(Event717Assessment.RISK_COMMUNICATION_DATE),
			assessment.get(Event717Assessment.RISK_COMMUNICATION_NOT_APPLICABLE),
			assessment.get(Event717Assessment.RISK_COMMUNICATION_DAYS),
			assessment.get(Event717Assessment.COORDINATION_DATE),
			assessment.get(Event717Assessment.COORDINATION_NOT_APPLICABLE),
			assessment.get(Event717Assessment.COORDINATION_DAYS),
			assessment.get(Event717Assessment.EARLY_RESPONSE_COMPLETION_DATE),
			assessment.get(Event717Assessment.RESPONSE_DAYS),
			assessment.get(Event717Assessment.RESPONSE_STATUS),
			assessment.get(Event717Assessment.TIMELINESS_STATUS),
			assessment.get(Event717Assessment.GENERAL_NOTES),
			JurisdictionHelper.booleanSelector(cb, eventService.inJurisdictionOrOwned(new EventQueryContext(cb, cq, eventJoins))));

		cq.where(assessment.get(AbstractDomainObject.ID).in(ids));
		// same order as the ids query
		cq.orderBy(getOrderList(null, queryContext));

		return em.createQuery(cq).getResultList();
	}
}
