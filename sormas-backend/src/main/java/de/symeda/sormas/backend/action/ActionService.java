/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.backend.action;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import de.symeda.sormas.api.action.ActionCriteria;
import de.symeda.sormas.api.action.ActionStatEntry;
import de.symeda.sormas.api.event.EventActionExportDto;
import de.symeda.sormas.api.event.EventActionIndexDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;

@Stateless
@LocalBean
public class ActionService extends AdoServiceWithUserFilter<Action> {

	@EJB
	private EventService eventService;

	public ActionService() {
		super(Action.class);
	}

	public List<Action> getAllActionsAfter(Date date, User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> from = cq.from(getElementClass());
		Predicate filter = null;
		if (user != null) {
			Predicate userFilter = createUserFilter(cb, cq, from);
			filter = CriteriaBuilderHelper.and(cb, filter, userFilter);
		}
		if (date != null) {
			Predicate dateFilter = createChangeDateFilter(cb, from, date);
			filter = CriteriaBuilderHelper.and(cb, filter, dateFilter);
		}
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(from.get(Action.CHANGE_DATE)));
		cq.distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public List<String> getAllUuids(User user) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Action> from = cq.from(getElementClass());

		if (user != null) {
			cq.where(createUserFilter(cb, cq, from));
		}

		cq.select(from.get(Action.UUID));

		return em.createQuery(cq).getResultList();
	}

	public List<Action> getAllByEvent(Event event) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> from = cq.from(getElementClass());

		Predicate filter = cb.equal(from.get(EventParticipant.EVENT), event);
		cq.where(filter);
		cq.orderBy(cb.desc(from.get(EventParticipant.CREATION_DATE)));

		return em.createQuery(cq).getResultList();
	}

	/**
	 * @see /sormas-backend/doc/UserDataAccess.md
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Predicate createUserFilter(CriteriaBuilder cb, CriteriaQuery cq, From<?, Action> actionPath) {

		// National users can access all actions in the system
		User currentUser = getCurrentUser();
		if (currentUser.hasAnyUserRole(UserRole.NATIONAL_USER, UserRole.NATIONAL_CLINICIAN, UserRole.NATIONAL_OBSERVER, UserRole.REST_USER)) {
			return null;
		}

		// whoever created the action is allowed to access it
		Predicate filter = cb.equal(actionPath.join(Action.CREATOR_USER, JoinType.LEFT), currentUser);

		Predicate eventFilter = eventService.createUserFilter(cb, cq, actionPath.join(Action.EVENT, JoinType.LEFT));
		if (eventFilter != null) {
			filter = cb.or(filter, eventFilter);
		}

		return filter;
	}

	/**
	 * Computes stats for action matching an actionCriteria.
	 */
	public List<ActionStatEntry> getActionStats(ActionCriteria actionCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ActionStatEntry> cq = cb.createQuery(ActionStatEntry.class);
		Root<Action> action = cq.from(getElementClass());
		cq.multiselect(cb.countDistinct(action.get(Action.ID)).alias("count"), action.get(Action.ACTION_STATUS));

		Predicate filter = null;
		if (actionCriteria == null || !actionCriteria.hasContextCriteria()) {
			filter = createUserFilter(cb, cq, action);
		}

		if (actionCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(actionCriteria, cb, action);
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

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Action> cq = cb.createQuery(getElementClass());
		Root<Action> action = cq.from(getElementClass());

		// Add filters
		Predicate filter = null;
		if (actionCriteria == null || !actionCriteria.hasContextCriteria()) {
			filter = createUserFilter(cb, cq, action);
		}

		if (actionCriteria != null) {
			Predicate criteriaFilter = buildCriteriaFilter(actionCriteria, cb, action);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.orderBy(cb.desc(action.get(Action.DATE)));
		List<Action> actions;
		if (first != null && max != null) {
			actions = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			actions = em.createQuery(cq).getResultList();
		}

		return actions;
	}

	/**
	 * Build a predicate corresponding to an actionCriteria
	 *
	 * @param actionCriteria
	 * @param cb
	 * @param from
	 * @return predicate corresponding to the actionCriteria
	 */
	public Predicate buildCriteriaFilter(ActionCriteria actionCriteria, CriteriaBuilder cb, Root<Action> from) {

		Predicate filter = null;

		if (actionCriteria.getActionStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(from.get(Action.ACTION_STATUS), actionCriteria.getActionStatus()));
		}
		if (actionCriteria.getEvent() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.equal(from.join(Action.EVENT, JoinType.LEFT).get(Event.UUID), actionCriteria.getEvent().getUuid()));
		}
		return filter;
	}

	public Predicate buildEventCriteriaFilter(EventCriteria criteria, CriteriaBuilder cb, ActionJoins joins) {

		From<Action, Action> action = joins.getRoot();
		From<Action, Event> event = joins.getEvent(JoinType.INNER);

		Predicate filter = eventService.buildCriteriaFilter(criteria, cb, event);

		if (criteria.getActionChangeDateFrom() != null && criteria.getActionChangeDateTo() != null) {
			filter = CriteriaBuilderHelper
				.and(cb, filter, cb.between(action.get(Action.CHANGE_DATE), criteria.getActionChangeDateFrom(), criteria.getActionChangeDateTo()));
		} else if (criteria.getActionChangeDateFrom() != null) {
			filter =
				CriteriaBuilderHelper.and(cb, filter, cb.greaterThanOrEqualTo(action.get(Action.CHANGE_DATE), criteria.getActionChangeDateFrom()));
		} else if (criteria.getActionChangeDateTo() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.lessThanOrEqualTo(event.get(Event.START_DATE), criteria.getActionChangeDateTo()));
		}

		if (criteria.getActionStatus() != null) {
			filter = CriteriaBuilderHelper.and(cb, filter, cb.equal(action.get(Action.ACTION_STATUS), criteria.getActionStatus()));
		}

		return filter;
	}

	public List<EventActionIndexDto> getEventActionIndexList(EventCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventActionIndexDto> cq = cb.createQuery(EventActionIndexDto.class);
		Root<Action> action = cq.from(getElementClass());
		ActionJoins actionJoins = new ActionJoins(action);
		Join<Action, User> lastModifiedBy = actionJoins.getLastModifiedBy();
		Join<Action, User> creatorUser = actionJoins.getCreator();
		Join<Action, Event> event = actionJoins.getEvent(JoinType.INNER);

		// Add filters
		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, cb, actionJoins);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EVENT_TITLE),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVENT_STATUS),
			event.get(Event.RISK_LEVEL),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			action.get(Action.ACTION_MEASURE),
			event.get(Event.EVOLUTION_DATE),
			action.get(Action.TITLE),
			action.get(Action.CREATION_DATE),
			action.get(Action.CHANGE_DATE),
			action.get(Action.ACTION_STATUS),
			action.get(Action.PRIORITY),
			lastModifiedBy.get(User.UUID),
			lastModifiedBy.get(User.FIRST_NAME),
			lastModifiedBy.get(User.LAST_NAME),
			creatorUser.get(User.UUID),
			creatorUser.get(User.FIRST_NAME),
			creatorUser.get(User.LAST_NAME));

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventActionIndexDto.EVENT_UUID:
					expression = event.get(Event.UUID);
					break;
				case EventActionIndexDto.EVENT_TITLE:
					expression = cb.lower(event.get(Event.EVENT_TITLE));
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
				case EventActionIndexDto.EVENT_EVOLUTION_DATE:
					expression = event.get(Event.EVOLUTION_DATE);
					break;
				case EventActionIndexDto.ACTION_CHANGE_DATE:
					expression = action.get(Action.CHANGE_DATE);
					break;
				case EventActionIndexDto.ACTION_CREATION_DATE:
					expression = action.get(Action.CREATION_DATE);
					break;
				case EventActionIndexDto.ACTION_PRIORITY:
					expression = action.get(Action.PRIORITY);
					break;
				case EventActionIndexDto.ACTION_STATUS:
					expression = action.get(Action.ACTION_STATUS);
					break;
				case EventActionIndexDto.ACTION_TITLE:
					expression = cb.lower(action.get(Action.TITLE));
					break;
				case EventActionIndexDto.ACTION_LAST_MODIFIED_BY:
					expression = cb.selectCase()
						.when(cb.isNotNull(action.get(Action.LAST_MODIFIED_BY)), cb.lower(action.get(Action.LAST_MODIFIED_BY).get(User.LAST_NAME)))
						.otherwise(cb.lower(action.get(Action.CREATOR_USER).get(User.LAST_NAME)));
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(event.get(Event.CHANGE_DATE)));
		}

		List<EventActionIndexDto> actions;
		if (first != null && max != null) {
			actions = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			actions = em.createQuery(cq).getResultList();
		}

		return actions;
	}

	public List<EventActionExportDto> getEventActionExportList(EventCriteria criteria, Integer first, Integer max) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventActionExportDto> cq = cb.createQuery(EventActionExportDto.class);
		Root<Action> action = cq.from(getElementClass());
		ActionJoins actionJoins = new ActionJoins(action);
		Join<Action, User> lastModifiedBy = actionJoins.getLastModifiedBy();
		Join<Action, User> creator = actionJoins.getCreator();
		Join<Action, Event> event = actionJoins.getEvent(JoinType.INNER);

		// Add filters
		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, cb, actionJoins);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EVENT_TITLE),
			event.get(Event.EVENT_DESC),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVOLUTION_DATE),
			event.get(Event.EVOLUTION_COMMENT),
			event.get(Event.EVENT_STATUS),
			event.get(Event.RISK_LEVEL),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			action.get(Action.ACTION_MEASURE),
			action.get(Action.TITLE),
			action.get(Action.CREATION_DATE),
			action.get(Action.CHANGE_DATE),
			action.get(Action.ACTION_STATUS),
			action.get(Action.PRIORITY),
			lastModifiedBy.get(User.UUID),
			lastModifiedBy.get(User.FIRST_NAME),
			lastModifiedBy.get(User.LAST_NAME),
			creator.get(User.UUID),
			creator.get(User.FIRST_NAME),
			creator.get(User.LAST_NAME));

		cq.orderBy(cb.desc(event.get(Event.CHANGE_DATE)));

		List<EventActionExportDto> actions;
		if (first != null && max != null) {
			actions = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			actions = em.createQuery(cq).getResultList();
		}

		return actions;
	}

	public long countEventActions(EventCriteria criteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Action> action = cq.from(getElementClass());
		ActionJoins actionJoins = new ActionJoins(action);
		Join<Action, Event> event = actionJoins.getEvent(JoinType.INNER);
		Join<Event, Location> location = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
		Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
		Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);

		// Add filters
		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (criteria != null) {
			Predicate criteriaFilter = buildEventCriteriaFilter(criteria, cb, actionJoins);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.count(action.get(Action.UUID)));

		return em.createQuery(cq).getSingleResult();
	}
}
