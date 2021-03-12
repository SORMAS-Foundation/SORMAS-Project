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
package de.symeda.sormas.backend.event;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupFacade;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "EventGroupFacade")
public class EventGroupFacadeEjb implements EventGroupFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private EventGroupService eventGroupService;
	@EJB
	private EventService eventService;
	@EJB
	private UserService userService;

	@Override
	public EventGroupReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(eventGroupService.getByUuid(uuid));
	}

	@Override
	public boolean exists(String uuid) {
		return eventGroupService.exists(uuid);
	}

	@Override
	public boolean isArchived(String uuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventGroup> from = cq.from(EventGroup.class);

		// Workaround for probable bug in Eclipse Link/Postgre that throws a NoResultException when trying to
		// query for a true Boolean result
		cq.where(cb.and(cb.equal(from.get(EventGroup.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), uuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public EventGroupDto getEventGroupByUuid(String uuid) {
		return toDto(eventGroupService.getByUuid(uuid));
	}

	@Override
	public List<EventGroupIndexDto> getIndexList(EventGroupCriteria eventGroupCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventGroupIndexDto> cq = cb.createQuery(EventGroupIndexDto.class);
		Root<EventGroup> eventGroup = cq.from(EventGroup.class);

		Subquery<Long> eventCountSubquery = cq.subquery(Long.class);
		Root<EventGroup> eventGroupSubQuery = eventCountSubquery.from(EventGroup.class);
		Join<EventGroup, Event> eventSubQueryJoin = eventGroupSubQuery.join(EventGroup.EVENTS, JoinType.LEFT);
		eventCountSubquery.select(cb.countDistinct(eventSubQueryJoin.get(Event.ID)));
		eventCountSubquery.where(cb.equal(eventGroupSubQuery.get(EventGroup.ID), eventGroup.get(EventGroup.ID)));
		eventCountSubquery.groupBy(eventGroupSubQuery.get(EventGroup.ID));

		cq.multiselect(
			eventGroup.get(EventGroup.UUID),
			eventGroup.get(EventGroup.NAME),
			eventGroup.get(EventGroup.CHANGE_DATE),
			eventCountSubquery.getSelection());

		Predicate filter = null;

		if (eventGroupCriteria != null) {
			if (eventGroupCriteria.getUserFilterIncluded()) {
				eventGroupService.createUserFilter(cb, cq, eventGroup);
			}

			Predicate criteriaFilter = eventGroupService.buildCriteriaFilter(eventGroupCriteria, cb, eventGroup);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
					case EventGroupIndexDto.UUID:
					case EventGroupIndexDto.NAME:
						expression = eventGroup.get(sortProperty.propertyName);
						break;
					default:
						throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(eventGroup.get(EventGroup.CHANGE_DATE)));
		}

		cq.distinct(true);

		List<EventGroupIndexDto> indexList;
		if (first != null && max != null) {
			indexList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			indexList = em.createQuery(cq).getResultList();
		}

		return indexList;

	}

	@Override
	public long count(EventGroupCriteria eventGroupCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventGroup> eventGroup = cq.from(EventGroup.class);

		Predicate filter = null;

		if (eventGroupCriteria != null) {
			if (eventGroupCriteria.getUserFilterIncluded()) {
				eventGroupService.createUserFilter(cb, cq, eventGroup);
			}

			Predicate criteriaFilter = eventGroupService.buildCriteriaFilter(eventGroupCriteria, cb, eventGroup);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		cq.select(cb.countDistinct(eventGroup));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public EventGroupDto saveEventGroup(@Valid @NotNull EventGroupDto dto) {
		return saveEventGroup(dto, true);
	}

	public EventGroupDto saveEventGroup(@Valid @NotNull EventGroupDto dto, boolean checkChangeDate) {

		EventGroup eventGroup = fromDto(dto, checkChangeDate);

		eventGroupService.ensurePersisted(eventGroup);

		return toDto(eventGroup);
	}

	@Override
	public void linkEventToGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference) {
		Event event = eventService.getByUuid(eventReference.getUuid());

		// Check that the event group is not already related to this event
		if (event.getEventGroups() != null && event.getEventGroups().stream().anyMatch(group -> group.getUuid().equals(eventGroupReference.getUuid()))) {
			return;
		}

		EventGroup eventGroupToAdd = eventGroupService.getByUuid(eventGroupReference.getUuid());
		List<EventGroup> groups = new ArrayList<>();
		if (event.getEventGroups() != null) {
			groups.addAll(event.getEventGroups());
		}
		groups.add(eventGroupToAdd);
		event.setEventGroups(groups);

		eventService.ensurePersisted(event);
	}

	@Override
	public void unlinkEventGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference) {
		Event event = eventService.getByUuid(eventReference.getUuid());

		// Check that the event group is not already unlinked to this event
		if (event.getEventGroups() == null || event.getEventGroups().stream().noneMatch(group -> group.getUuid().equals(eventGroupReference.getUuid()))) {
			return;
		}

		List<EventGroup> groups = new ArrayList<>();
		for (EventGroup eventGroup : event.getEventGroups()) {
			if (eventGroup.getUuid().equals(eventGroupReference.getUuid())) {
				continue;
			}

			groups.add(eventGroup);
		}
		event.setEventGroups(groups);

		eventService.ensurePersisted(event);
	}

	@Override
	public void deleteEventGroup(String uuid) {

		if (!userService.hasRight(UserRight.EVENT_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete events.");
		}

		eventGroupService.delete(eventGroupService.getByUuid(uuid));
	}

	@Override
	public void archiveOrDearchiveEventGroup(String uuid, boolean archive) {

		EventGroup eventGroup = eventGroupService.getByUuid(uuid);
		eventGroup.setArchived(archive);
		eventGroupService.ensurePersisted(eventGroup);
	}

	public static EventGroupDto toDto(EventGroup source) {

		if (source == null) {
			return null;
		}

		EventGroupDto target = new EventGroupDto();
		DtoHelper.fillDto(target, source);

		target.setName(source.getName());

		return target;
	}

	public EventGroup fromDto(@NotNull EventGroupDto source, boolean checkChangeDate) {
		EventGroup target = DtoHelper.fillOrBuildEntity(source, eventGroupService.getByUuid(source.getUuid()), EventGroup::new, checkChangeDate);

		target.setName(source.getName());

		return target;
	}

	public static EventGroupReferenceDto toReferenceDto(EventGroup entity) {

		if (entity == null) {
			return null;
		}

		return new EventGroupReferenceDto(entity.getUuid(), entity.getName());
	}

	@LocalBean
	@Stateless
	public static class EventGroupFacadeEjbLocal extends EventGroupFacadeEjb {

	}
}
