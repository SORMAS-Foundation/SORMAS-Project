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

package de.symeda.sormas.backend.action;

import java.util.ArrayList;
import java.util.List;
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

import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionDto;
import de.symeda.sormas.api.action.ActionStatEntry;
import de.symeda.sormas.api.document.DocumentRelatedEntityType;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilterAndJurisdiction;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.document.DocumentService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventQueryContext;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless
@LocalBean
public class ActionService extends AdoServiceWithUserFilterAndJurisdiction<Action> {

	@EJB
	private EventService eventService;

	@EJB
	private DocumentService documentService;

	public ActionService() {
		super(Action.class);
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Predicate createRelevantDataFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Action> from) {

		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, from);

		Predicate filter = createActiveFilter(cb, queryContext.getJoins().getEvent());
		filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(queryContext));

		return filter;
	}

	public List<String> getAllActiveUuids() {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Action> from = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, from);

		Predicate filter = createActiveFilter(cb, queryContext.getJoins().getEvent());

		if (getCurrentUser() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, createUserFilter(cb, cq, from));
		}

		cq.where(filter);
		cq.select(from.get(Action.UUID));
		cq.distinct(true);

		return em.createQuery(cq).getResultList();
	}

	public List<Action> getAllByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(Action.EVENT), event);
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(Action.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Action> actionPath) {
		return createUserFilter(new ActionQueryContext(cb, cq, actionPath));
	}

	public Predicate createUserFilter(ActionQueryContext queryContext) {

		CriteriaQuery<?> cq = queryContext.getQuery();
		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		ActionJoins joins = queryContext.getJoins();

		User currentUser = getCurrentUser();
		if (currentUser == null) {
			return null;
		}

		final JurisdictionLevel jurisdictionLevel = currentUser.getJurisdictionLevel();
		if (jurisdictionLevel == JurisdictionLevel.NATION) {
			return null;
		}

		Predicate filter = cb.equal(joins.getCreator(), currentUser);
		Predicate eventFilter = eventService.createUserFilter(new EventQueryContext(cb, cq, joins.getEventJoins()));
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}

		return filter;
	}

	public Predicate createActiveFilter(CriteriaBuilder cb, From<?, Event> from) {

		return cb.and(cb.isFalse(from.get(Event.ARCHIVED)), cb.isFalse(from.get(Event.DELETED)));
	}

	/**
	 * Computes stats for action matching an actionCriteria.
	 */
	public List<ActionStatEntry> getActionStats(ActionCriteria actionCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ActionStatEntry> cq = cb.createQuery(ActionStatEntry.class);
		Root<Action> action = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);
		cq.multiselect(cb.countDistinct(action.get(Action.ID)).alias("count"), action.get(Action.ACTION_STATUS));

		Predicate filter = null;
		if (actionCriteria == null || !actionCriteria.hasContextCriteria()) {
			filter = createUserFilter(queryContext);
		}

		if (actionCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(actionCriteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.groupBy(action.get(Action.ACTION_STATUS));
		cq.orderBy(cb.desc(action.get(Action.ACTION_STATUS)));
		return em.createQuery(cq).getResultList();
	}

	public List<Action> getActionList(ActionCriteria actionCriteria, Integer first, Integer max) {
		return getActionList(actionCriteria, first, max, null);
	}

	public List<Action> getActionList(ActionCriteria actionCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> action = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);

		// Add filters
		Predicate filter = null;
		if (actionCriteria == null || !actionCriteria.hasContextCriteria()) {
			filter = createUserFilter(queryContext);
		}

		if (actionCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(actionCriteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
		if (sortProperties != null && !sortProperties.isEmpty()) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case ActionDto.UUID:
				case ActionDto.ACTION_STATUS:
				case ActionDto.ACTION_MEASURE:
				case ActionDto.ACTION_CONTEXT:
				case ActionDto.CHANGE_DATE:
				case ActionDto.CREATION_DATE:
				case ActionDto.DATE:
				case ActionDto.PRIORITY:
					expression = action.get(sortProperty.propertyName);
					break;
				case ActionDto.TITLE:
					expression = cb.lower(action.get(sortProperty.propertyName));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(action.get(Action.DATE)));
		}

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public Predicate buildCriteriaFilter(ActionCriteria actionCriteria, ActionQueryContext queryContext) {

		final CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		final ActionJoins joins = queryContext.getJoins();

		Predicate filter = null;
		if (actionCriteria.getActionStatus() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.equal(queryContext.getRoot().get(Action.ACTION_STATUS), actionCriteria.getActionStatus()));
		}
		if (actionCriteria.getEvent() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(joins.getEvent().get(Event.UUID), actionCriteria.getEvent().getUuid()));
		}

		return filter;
	}

	public Predicate buildEventCriteriaFilter(EventCriteria criteria, ActionQueryContext actionQueryContext) {

		CriteriaBuilder cb = actionQueryContext.getCriteriaBuilder();
		ActionJoins joins = actionQueryContext.getJoins();
		From<?, Action> action = joins.getRoot();

		Predicate filter =
			eventService.buildCriteriaFilter(criteria, new EventQueryContext(cb, actionQueryContext.getQuery(), joins.getEventJoins()));

		if (criteria.getActionChangeDateFrom() != null && criteria.getActionChangeDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(action.get(Action.CHANGE_DATE), criteria.getActionChangeDateFrom(), criteria.getActionChangeDateTo()));
		} else if (criteria.getActionChangeDateFrom() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(action.get(Action.CHANGE_DATE), criteria.getActionChangeDateFrom()));
		} else if (criteria.getActionChangeDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(action.get(Action.CHANGE_DATE), criteria.getActionChangeDateTo()));
		}

		if (criteria.getActionDateFrom() != null && criteria.getActionDateTo() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.between(action.get(Action.DATE), criteria.getActionDateFrom(), criteria.getActionDateTo()));
		} else if (criteria.getActionDateFrom() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(action.get(Action.DATE), criteria.getActionDateFrom()));
		} else if (criteria.getActionDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(action.get(Action.DATE), criteria.getActionDateTo()));
		}

		if (criteria.getActionStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(action.get(Action.ACTION_STATUS), criteria.getActionStatus()));
		}

		return filter;
	}

	public List<EventActionIndexDto> getEventActionIndexList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		List<Long> indexListIds = getEventActionIndexListIds(criteria, first, max, sortProperties);

		List<EventActionIndexDto> actions = new ArrayList<>();
		IterableHelper.executeBatched(indexListIds, ModelConstants.PARAMETER_LIMIT, batchedIds -> {
			final CriteriaBuilder cb = em.getCriteriaBuilder();
			final CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
			final Root<Action> action = cq.from(getElementClass());
			final ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);
			final ActionJoins actionJoins = queryContext.getJoins();

			Join<Action, User> lastModifiedBy = actionJoins.getLastModifiedBy();
			Join<Action, User> creatorUser = actionJoins.getCreator();
			Join<Action, Event> event = actionJoins.getEvent();
			Join<Event, User> eventReportingUser = actionJoins.getEventJoins().getReportingUser();
			Join<Event, User> eventResponsibleUser = actionJoins.getEventJoins().getResponsibleUser();

			cq.multiselect(
				action.get(Action.UUID),
				event.get(Event.UUID),
				event.get(Event.EVENT_TITLE),
				event.get(Event.DISEASE),
				event.get(Event.DISEASE_VARIANT),
				event.get(Event.DISEASE_DETAILS),
				event.get(Event.EVENT_IDENTIFICATION_SOURCE),
				event.get(Event.START_DATE),
				event.get(Event.END_DATE),
				event.get(Event.EVENT_STATUS),
				event.get(Event.RISK_LEVEL),
				event.get(Event.EVENT_INVESTIGATION_STATUS),
				event.get(Event.EVENT_MANAGEMENT_STATUS),
				eventReportingUser.get(User.UUID),
				eventReportingUser.get(User.FIRST_NAME),
				eventReportingUser.get(User.LAST_NAME),
				eventResponsibleUser.get(User.UUID),
				eventResponsibleUser.get(User.FIRST_NAME),
				eventResponsibleUser.get(User.LAST_NAME),
				action.get(Action.ACTION_MEASURE),
				event.get(Event.EVOLUTION_DATE),
				action.get(Action.TITLE),
				action.get(Action.CREATION_DATE),
				action.get(Action.CHANGE_DATE),
				action.get(Action.DATE),
				action.get(Action.ACTION_STATUS),
				action.get(Action.PRIORITY),
				lastModifiedBy.get(User.UUID),
				lastModifiedBy.get(User.FIRST_NAME),
				lastModifiedBy.get(User.LAST_NAME),
				creatorUser.get(User.UUID),
				creatorUser.get(User.FIRST_NAME),
				creatorUser.get(User.LAST_NAME),
				event.get(Event.CHANGE_DATE),
				// Selections needed for ordering
				cb.lower(event.get(Event.EVENT_TITLE)),
				cb.lower(action.get(Action.TITLE)),
				cb.lower(lastModifiedBy.get(User.LAST_NAME)),
				cb.lower(creatorUser.get(User.LAST_NAME)),
				cb.lower(eventReportingUser.get(User.LAST_NAME)),
				cb.lower(eventResponsibleUser.get(User.LAST_NAME)),
				cb.selectCase()
					.when(cb.isNotNull(action.get(Action.LAST_MODIFIED_BY)), cb.lower(lastModifiedBy.get(User.LAST_NAME)))
					.otherwise(cb.lower(creatorUser.get(User.LAST_NAME))),
				event.get(Event.CHANGE_DATE),
				event.get(Event.DELETION_REASON),
				event.get(Event.OTHER_DELETION_REASON));

			cq.where(action.get(Sample.ID).in(batchedIds));
			cq.orderBy(getOrderList(sortProperties, queryContext));
			cq.distinct(true);

			actions.addAll(QueryHelper.getResultList(em, cq, new EventActionIndexDtoReasultTransformer(), first, max));
		});

		return actions;
	}

	private List<Long> getEventActionIndexListIds(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		final Root<Action> action = cq.from(getElementClass());
		final ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);
		final ActionJoins actionJoins = queryContext.getJoins();

		List<Selection<?>> selections = new ArrayList<>();
		selections.add(action.get(Sample.ID));

		List<Order> orderList = getOrderList(sortProperties, queryContext);
		List<Expression<?>> sortColumns = orderList.stream().map(Order::getExpression).collect(Collectors.toList());
		selections.addAll(sortColumns);

		cq.multiselect(selections);

		// Add filters
		Predicate filter = eventService.createUserFilter(new EventQueryContext(cb, cq, actionJoins.getEventJoins()));

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.distinct(true);
		cq.orderBy(orderList);

		return QueryHelper.getResultList(em, cq, first, max).stream().map(t -> t.get(0, Long.class)).collect(Collectors.toList());
	}

	private List<Order> getOrderList(List<SortProperty> sortProperties, ActionQueryContext queryContext) {
		List<Order> orderList = new ArrayList<>();

		CriteriaBuilder cb = queryContext.getCriteriaBuilder();
		ActionJoins actionJoins = queryContext.getJoins();
		From<?, Action> actionRoot = queryContext.getRoot();
		Join<Action, Event> event = actionJoins.getEvent();
		Join<Event, User> eventReportingUser = actionJoins.getEventJoins().getReportingUser();
		Join<Event, User> eventResponsibleUser = actionJoins.getEventJoins().getResponsibleUser();
		Join<Action, User> lastModifiedBy = actionJoins.getLastModifiedBy();
		Join<Action, User> creatorUser = actionJoins.getCreator();

		if (sortProperties != null && !sortProperties.isEmpty()) {
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventActionIndexDto.EVENT_UUID:
					expression = event.get(Event.UUID);
					break;
				case EventActionIndexDto.EVENT_TITLE:
					expression = cb.lower(event.get(Event.EVENT_TITLE));
					break;
				case EventActionIndexDto.EVENT_DISEASE:
					expression = event.get(Event.DISEASE);
					break;
				case EventActionIndexDto.EVENT_DISEASE_VARIANT:
					expression = event.get(Event.DISEASE_VARIANT);
					break;
				case EventActionIndexDto.EVENT_IDENTIFICATION_SOURCE:
					expression = event.get(Event.EVENT_IDENTIFICATION_SOURCE);
					break;
				case EventActionIndexDto.EVENT_START_DATE:
					expression = event.get(Event.START_DATE);
					break;
				case EventActionIndexDto.EVENT_END_DATE:
					expression = event.get(Event.END_DATE);
					break;
				case EventActionIndexDto.EVENT_STATUS:
					expression = event.get(Event.EVENT_STATUS);
					break;
				case EventActionIndexDto.EVENT_INVESTIGATION_STATUS:
					expression = event.get(Event.EVENT_INVESTIGATION_STATUS);
					break;
				case EventActionIndexDto.EVENT_MANAGEMENT_STATUS:
					expression = event.get(Event.EVENT_MANAGEMENT_STATUS);
					break;
				case EventActionIndexDto.EVENT_EVOLUTION_DATE:
					expression = event.get(Event.EVOLUTION_DATE);
					break;
				case EventActionIndexDto.EVENT_RISK_LEVEL:
					expression = event.get(Event.RISK_LEVEL);
					break;
				case EventActionIndexDto.EVENT_REPORTING_USER:
					expression = cb.lower(eventReportingUser.get(User.LAST_NAME));
					break;
				case EventActionIndexDto.EVENT_RESPONSIBLE_USER:
					expression = cb.lower(eventResponsibleUser.get(User.LAST_NAME));
					break;
				case EventActionIndexDto.ACTION_CHANGE_DATE:
					expression = actionRoot.get(Action.CHANGE_DATE);
					break;
				case EventActionIndexDto.ACTION_CREATION_DATE:
					expression = actionRoot.get(Action.CREATION_DATE);
					break;
				case EventActionIndexDto.ACTION_DATE:
					expression = actionRoot.get(Action.DATE);
					break;
				case EventActionIndexDto.ACTION_PRIORITY:
					expression = actionRoot.get(Action.PRIORITY);
					break;
				case EventActionIndexDto.ACTION_STATUS:
					expression = actionRoot.get(Action.ACTION_STATUS);
					break;
				case EventActionIndexDto.ACTION_TITLE:
					expression = cb.lower(actionRoot.get(Action.TITLE));
					break;
				case EventActionIndexDto.ACTION_LAST_MODIFIED_BY:
					expression = cb.selectCase()
						.when(cb.isNotNull(actionRoot.get(Action.LAST_MODIFIED_BY)), cb.lower(lastModifiedBy.get(User.LAST_NAME)))
						.otherwise(cb.lower(creatorUser.get(User.LAST_NAME)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				orderList.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
		} else {
			orderList.add(cb.desc(event.get(Event.CHANGE_DATE)));
		}

		return orderList;
	}

	public List<EventActionExportDto> getEventActionExportList(EventCriteria criteria, Integer first, Integer max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventActionExportDto> cq = cb.createQuery(EventActionExportDto.class);
		Root<Action> action = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);
		ActionJoins actionJoins = queryContext.getJoins();
		Join<Action, User> lastModifiedBy = actionJoins.getLastModifiedBy();
		Join<Action, User> creator = actionJoins.getCreator();
		Join<Action, Event> event = actionJoins.getEvent();
		Join<Event, User> eventReportingUser = actionJoins.getEventJoins().getReportingUser();
		Join<Event, User> eventResponsibleUser = actionJoins.getEventJoins().getResponsibleUser();

		// Add filters
		Predicate filter = eventService.createUserFilter(new EventQueryContext(cb, cq, actionJoins.getEventJoins()));

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EVENT_TITLE),
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_VARIANT),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.EVENT_DESC),
			event.get(Event.EVENT_IDENTIFICATION_SOURCE),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVOLUTION_DATE),
			event.get(Event.EVOLUTION_COMMENT),
			event.get(Event.EVENT_STATUS),
			event.get(Event.RISK_LEVEL),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			eventReportingUser.get(User.UUID),
			eventReportingUser.get(User.FIRST_NAME),
			eventReportingUser.get(User.LAST_NAME),
			eventResponsibleUser.get(User.UUID),
			eventResponsibleUser.get(User.FIRST_NAME),
			eventResponsibleUser.get(User.LAST_NAME),
			action.get(Action.ACTION_MEASURE),
			action.get(Action.TITLE),
			action.get(Action.CREATION_DATE),
			action.get(Action.CHANGE_DATE),
			action.get(Action.DATE),
			action.get(Action.ACTION_STATUS),
			action.get(Action.PRIORITY),
			lastModifiedBy.get(User.UUID),
			lastModifiedBy.get(User.FIRST_NAME),
			lastModifiedBy.get(User.LAST_NAME),
			creator.get(User.UUID),
			creator.get(User.FIRST_NAME),
			creator.get(User.LAST_NAME));

		cq.orderBy(cb.desc(event.get(Event.CHANGE_DATE)));

		return QueryHelper.getResultList(em, cq, first, max);
	}

	public long countEventActions(EventCriteria criteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Action> action = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);
		ActionJoins actionJoins = queryContext.getJoins();

		// Add filters
		Predicate filter = eventService.createUserFilter(new EventQueryContext(cb, cq, actionJoins.getEventJoins()));

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(action.get(Action.UUID)));

		return em.createQuery(cq).getSingleResult();
	}

	public long countActions(ActionCriteria actionCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Action> action = cq.from(getElementClass());
		ActionQueryContext queryContext = new ActionQueryContext(cb, cq, action);

		// Add filters
		Predicate filter = null;
		if (actionCriteria == null || !actionCriteria.hasContextCriteria()) {
			filter = createUserFilter(queryContext);
		}

		if (actionCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(actionCriteria, queryContext);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(action.get(Action.UUID)));

		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public void deletePermanent(Action action) {

		documentService.getRelatedToEntity(DocumentRelatedEntityType.ACTION, action.getUuid()).forEach(d -> documentService.markAsDeleted(d));

		super.deletePermanent(action);
	}

	@Override
	public boolean inJurisdictionOrOwned(Action entity) {
		return eventService.inJurisdictionOrOwned(entity.getEvent());
	}
}
