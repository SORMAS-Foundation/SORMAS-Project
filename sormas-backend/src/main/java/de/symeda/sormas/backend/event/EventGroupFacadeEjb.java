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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupDto;
import de.symeda.sormas.api.event.EventGroupFacade;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.JurisdictionLevel;
import de.symeda.sormas.api.user.NotificationType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.common.NotificationService;
import de.symeda.sormas.backend.common.messaging.MessageContents;
import de.symeda.sormas.backend.common.messaging.MessageSubject;
import de.symeda.sormas.backend.common.messaging.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.infrastructure.region.Region;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.IterableHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.QueryHelper;

@Stateless(name = "EventGroupFacade")
public class EventGroupFacadeEjb implements EventGroupFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;
	@EJB
	private EventGroupService eventGroupService;
	@EJB
	private EventService eventService;
	@EJB
	private UserService userService;
	@EJB
	private NotificationService notificationService;

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
	public List<EventGroupReferenceDto> getCommonEventGroupsByEvents(List<EventReferenceDto> eventReferences) {
		Map<String, Set<String>> eventGroupsByEvent = new HashMap<>();
		IterableHelper.executeBatched(eventReferences, ModelConstants.PARAMETER_LIMIT, batchedEventReferences -> {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<EventGroup> from = cq.from(EventGroup.class);
			Join<EventGroup, Event> eventJoin = from.join(EventGroup.EVENTS, JoinType.INNER);
			Set<String> eventUuids = batchedEventReferences.stream().map(EventReferenceDto::getUuid).collect(Collectors.toSet());
			cq.where(eventJoin.get(Event.UUID).in(eventUuids));
			cq.multiselect(eventJoin.get(Event.UUID), from.get(EventGroup.UUID));
			eventGroupsByEvent.putAll(
				em.createQuery(cq)
					.getResultList()
					.stream()
					.collect(Collectors.groupingBy(row -> (String) row[0], Collectors.mapping(row -> (String) row[1], Collectors.toSet()))));
		});

		if (eventGroupsByEvent.isEmpty()) {
			return Collections.emptyList();
		}

		Set<String> commonEventGroupUuids = eventGroupsByEvent.values().stream().reduce(Sets::intersection).orElseGet(Collections::emptySet);

		return commonEventGroupUuids.stream().map(EventGroupReferenceDto::new).collect(Collectors.toList());
	}

	@Override
	public List<EventGroupIndexDto> getIndexList(
		EventGroupCriteria eventGroupCriteria,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties) {

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
				filter = eventGroupService.createUserFilter(cb, cq, eventGroup);
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

		return QueryHelper.getResultList(em, cq, first, max);
	}

	@Override
	public long count(EventGroupCriteria eventGroupCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<EventGroup> eventGroup = cq.from(EventGroup.class);

		Predicate filter = null;

		if (eventGroupCriteria != null) {
			if (eventGroupCriteria.getUserFilterIncluded()) {
				filter = eventGroupService.createUserFilter(cb, cq, eventGroup);
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

	public Page<EventGroupIndexDto> getIndexPage(
		@NotNull EventGroupCriteria eventGroupCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties) {
		List<EventGroupIndexDto> eventGroupIndexList = getIndexList(eventGroupCriteria, offset, size, sortProperties);
		long totalElementCount = count(eventGroupCriteria);
		return new Page<>(eventGroupIndexList, offset, size, totalElementCount);
	}

	@Override
	public EventGroupDto saveEventGroup(@Valid @NotNull EventGroupDto dto) {
		return saveEventGroup(dto, true);
	}

	public EventGroupDto saveEventGroup(@Valid @NotNull EventGroupDto dto, boolean checkChangeDate) {
		User currentUser = userService.getCurrentUser();
		if (!userService.hasRight(UserRight.EVENTGROUP_EDIT)) {
			throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to edit event groups.");
		}

		EventGroup eventGroup = fromDto(dto, checkChangeDate);

		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			List<RegionReferenceDto> regions = getEventGroupRelatedRegions(eventGroup.getUuid());
			for (RegionReferenceDto region : regions) {
				if (!userService.hasRegion(region)) {
					throw new UnsupportedOperationException(
						"User " + currentUser.getUuid() + " is not allowed to edit event groups related to another region.");
				}
			}
		}

		eventGroupService.ensurePersisted(eventGroup);

		return toDto(eventGroup);
	}

	@Override
	public void linkEventToGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference) {
		linkEventsToGroup(Collections.singletonList(eventReference), eventGroupReference);
	}

	@Override
	public void linkEventsToGroup(List<EventReferenceDto> eventReferences, EventGroupReferenceDto eventGroupReference) {
		linkEventsToGroups(eventReferences, Collections.singletonList(eventGroupReference));
	}

	@Override
	public void linkEventToGroups(EventReferenceDto eventReference, List<EventGroupReferenceDto> eventGroupReferences) {
		linkEventsToGroups(Collections.singletonList(eventReference), eventGroupReferences);
	}

	@Override
	public void linkEventsToGroups(List<EventReferenceDto> eventReferences, List<EventGroupReferenceDto> eventGroupReferences) {
		User currentUser = userService.getCurrentUser();
		if (!userService.hasRight(UserRight.EVENTGROUP_LINK)) {
			throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to link events.");
		}

		if (CollectionUtils.isEmpty(eventReferences)) {
			return;
		}

		List<String> eventUuids = eventReferences.stream().map(EventReferenceDto::getUuid).collect(Collectors.toList());
		List<Event> events = eventService.getByUuids(eventUuids);

		List<String> eventGroupUuids = eventGroupReferences.stream().map(EventGroupReferenceDto::getUuid).collect(Collectors.toList());
		List<EventGroup> eventGroups = eventGroupService.getByUuids(eventGroupUuids);

		for (Event event : events) {
			final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
			if (jurisdictionLevel != JurisdictionLevel.NATION) {
				Region region = event.getEventLocation().getRegion();
				if (!userService.hasRegion(new RegionReferenceDto(region.getUuid()))) {
					throw new UnsupportedOperationException(
						"User " + currentUser.getUuid() + " is not allowed to link events from another region to an event group.");
				}
			}

			// Check that the event group is not already related to this event
			List<EventGroup> filteredEventGroups = eventGroups;
			if (eventGroups == null) {
				filteredEventGroups = Collections.emptyList();
			}

			if (event.getEventGroups() != null) {
				Set<String> alreadyRelatedUuids = event.getEventGroups().stream().map(EventGroup::getUuid).collect(Collectors.toSet());
				filteredEventGroups = filteredEventGroups.stream()
					.filter(eventGroup -> !alreadyRelatedUuids.contains(eventGroup.getUuid()))
					.collect(Collectors.toList());
			}

			if (filteredEventGroups.isEmpty()) {
				continue;
			}

			List<EventGroup> groups = new ArrayList<>();
			if (event.getEventGroups() != null) {
				groups.addAll(event.getEventGroups());
			}
			groups.addAll(filteredEventGroups);
			event.setEventGroups(groups);

			eventService.ensurePersisted(event);
		}
	}

	@Override
	public void unlinkEventGroup(EventReferenceDto eventReference, EventGroupReferenceDto eventGroupReference) {
		User currentUser = userService.getCurrentUser();
		if (!userService.hasRight(UserRight.EVENTGROUP_LINK)) {
			throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to unlink events.");
		}

		Event event = eventService.getByUuid(eventReference.getUuid());

		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			Region region = event.getEventLocation().getRegion();
			if (!userService.hasRegion(new RegionReferenceDto(region.getUuid()))) {
				throw new UnsupportedOperationException(
					"User " + currentUser.getUuid() + " is not allowed to unlink events from another region to an event group.");
			}
		}

		// Check that the event group is not already unlinked to this event
		if (event.getEventGroups() == null
			|| event.getEventGroups().stream().noneMatch(group -> group.getUuid().equals(eventGroupReference.getUuid()))) {
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
		User currentUser = userService.getCurrentUser();
		if (!userService.hasRight(UserRight.EVENTGROUP_DELETE)) {
			throw new UnsupportedOperationException("User " + currentUser.getUuid() + " is not allowed to delete events.");
		}

		EventGroup eventGroup = eventGroupService.getByUuid(uuid);

		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			List<RegionReferenceDto> regions = getEventGroupRelatedRegions(eventGroup.getUuid());
			for (RegionReferenceDto region : regions) {
				if (!userService.hasRegion(region)) {
					throw new UnsupportedOperationException(
						"User " + currentUser.getUuid() + " is not allowed to delete event groups related to another region.");
				}
			}
		}

		for (Event event : eventGroup.getEvents()) {
			event.getEventGroups().remove(eventGroup);
			eventService.ensurePersisted(event);
		}

		eventGroupService.delete(eventGroup);
	}

	@Override
	public void archiveOrDearchiveEventGroup(String uuid, boolean archive) {
		User currentUser = userService.getCurrentUser();
		if (!userService.hasRight(UserRight.EVENTGROUP_ARCHIVE)) {
			throw new UnsupportedOperationException(
				"User " + currentUser.getUuid() + " is not allowed to " + (archive ? "" : "de") + "archive events.");
		}

		EventGroup eventGroup = eventGroupService.getByUuid(uuid);

		final JurisdictionLevel jurisdictionLevel = currentUser.getCalculatedJurisdictionLevel();
		if (jurisdictionLevel != JurisdictionLevel.NATION) {
			List<RegionReferenceDto> regions = getEventGroupRelatedRegions(eventGroup.getUuid());
			for (RegionReferenceDto region : regions) {
				if (!userService.hasRegion(region)) {
					throw new UnsupportedOperationException(
						"User " + currentUser.getUuid() + " is not allowed to " + (archive ? "" : "de")
							+ "archive event groups related to another region.");
				}
			}
		}

		eventGroup.setArchived(archive);
		eventGroupService.ensurePersisted(eventGroup);
	}

	@Override
	public List<RegionReferenceDto> getEventGroupRelatedRegions(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventGroup> eventGroupRoot = cq.from(EventGroup.class);
		Join<EventGroup, Event> eventJoin = eventGroupRoot.join(EventGroup.EVENTS, JoinType.INNER);
		Join<Event, Location> locationJoin = eventJoin.join(Event.EVENT_LOCATION, JoinType.INNER);
		cq.where(cb.equal(eventGroupRoot.get(EventGroup.UUID), uuid));
		cq.select(locationJoin.get(Location.REGION).get(Region.UUID));

		return em.createQuery(cq).getResultList().stream().map(RegionReferenceDto::new).collect(Collectors.toList());
	}

	@Override
	public void notifyEventEventGroupCreated(EventGroupReferenceDto eventGroupReference) {
		notifyModificationOfEventGroup(
			eventGroupReference,
			Collections.emptyList(),
			NotificationType.EVENT_GROUP_CREATED,
			MessageSubject.EVENT_GROUP_CREATED,
			MessageContents.CONTENT_EVENT_GROUP_CREATED);
	}

	@Override
	public void notifyEventAddedToEventGroup(EventGroupReferenceDto eventGroupReference, List<EventReferenceDto> eventReferences) {
		notifyModificationOfEventGroup(
			eventGroupReference,
			eventReferences,
			NotificationType.EVENT_ADDED_TO_EVENT_GROUP,
			MessageSubject.EVENT_ADDED_TO_EVENT_GROUP,
			MessageContents.CONTENT_EVENT_ADDED_TO_EVENT_GROUP);
	}

	@Override
	public void notifyEventRemovedFromEventGroup(EventGroupReferenceDto eventGroupReference, List<EventReferenceDto> eventReferences) {
		notifyModificationOfEventGroup(
			eventGroupReference,
			eventReferences,
			NotificationType.EVENT_REMOVED_FROM_EVENT_GROUP,
			MessageSubject.EVENT_REMOVED_FROM_EVENT_GROUP,
			MessageContents.CONTENT_EVENT_REMOVED_FROM_EVENT_GROUP);
	}

	private void notifyModificationOfEventGroup(
		EventGroupReferenceDto eventGroupReference,
		List<EventReferenceDto> impactedEventReferences,
		NotificationType notificationType,
		MessageSubject subject,
		String contentTemplate) {
		EventGroup eventGroup = eventGroupService.getByUuid(eventGroupReference.getUuid());
		if (eventGroup == null) {
			return;
		}

		User currentUser = userService.getCurrentUser();

		try {
			notificationService.sendNotifications(notificationType, subject, () -> {

				final Set<String> allRemainingEventUuids = getEventReferencesByEventGroupUuid(eventGroupReference.getUuid()).stream()
					.map(EventReferenceDto::getUuid)
					.collect(Collectors.toSet());
				final Set<String> impactedEventUuids = impactedEventReferences.stream().map(EventReferenceDto::getUuid).collect(Collectors.toSet());
				final Map<String, User> responsibleUserByEventUuid =
					userService.getResponsibleUsersByEventUuids(new ArrayList<>(Sets.union(allRemainingEventUuids, impactedEventUuids)));

				final Map<String, User> responsibleUserByRemainingEventUuid =
					Maps.filterKeys(responsibleUserByEventUuid, allRemainingEventUuids::contains);
				final Map<String, User> responsibleUserByImpactedEventUuid =
					Maps.filterKeys(responsibleUserByEventUuid, impactedEventUuids::contains);
				final String message;

				if (impactedEventReferences.isEmpty()) {
					message = String.format(
						I18nProperties.getString(contentTemplate),
						eventGroup.getName(),
						DataHelper.getShortUuid(eventGroup.getUuid()),
						buildCaptionForUserInNotification(currentUser),
						buildEventGroupSummaryForNotification(responsibleUserByRemainingEventUuid));
				} else {
					message = String.format(
						I18nProperties.getString(contentTemplate),
						stringifyEventsWithResponsibleUser(responsibleUserByImpactedEventUuid, ", ", ""),
						eventGroup.getName(),
						DataHelper.getShortUuid(eventGroup.getUuid()),
						buildCaptionForUserInNotification(currentUser),
						buildEventGroupSummaryForNotification(responsibleUserByRemainingEventUuid));
				}

				return responsibleUserByEventUuid.values().stream().collect(Collectors.toMap(Function.identity(), (u) -> message));
			});
		} catch (NotificationDeliveryFailedException e) {
			logger.error("NotificationDeliveryFailedException when trying to notify event responsible user about a modification on an EventGroup.");
		}
	}

	private List<EventReferenceDto> getEventReferencesByEventGroupUuid(String uuid) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<EventGroup> eventGroupRoot = cq.from(EventGroup.class);
		Join<EventGroup, Event> eventJoin = eventGroupRoot.join(EventGroup.EVENTS, JoinType.INNER);
		cq.where(cb.equal(eventGroupRoot.get(EventGroup.UUID), uuid));
		cq.select(eventJoin.get(Event.UUID));

		return em.createQuery(cq).getResultList().stream().map(EventReferenceDto::new).collect(Collectors.toList());
	}

	private String buildEventGroupSummaryForNotification(Map<String, User> responsibleUserByEventUuid) {
		if (responsibleUserByEventUuid.isEmpty()) {
			return I18nProperties.getString(Strings.notificationEventGroupSummaryEmpty);
		}

		return String.format(
			I18nProperties.getString(Strings.notificationEventGroupSummary),
			stringifyEventsWithResponsibleUser(responsibleUserByEventUuid, "\n* ", "* "));
	}

	private String stringifyEventsWithResponsibleUser(Map<String, User> responsibleUserByEventUuid, String delimiter, String prefix) {
		if (responsibleUserByEventUuid.isEmpty()) {
			return "";
		}

		return responsibleUserByEventUuid.entrySet()
			.stream()
			.map(entry -> stringifyEventWithResponsibleUser(entry.getKey(), entry.getValue()))
			.collect(Collectors.joining(delimiter, prefix, ""));
	}

	private String stringifyEventWithResponsibleUser(String eventUuid, User responsibleUser) {
		return String.format(
			I18nProperties.getString(Strings.notificationEventWithResponsibleUserLine),
			DataHelper.getShortUuid(eventUuid),
			buildCaptionForUserInNotification(responsibleUser));
	}

	private String buildCaptionForUserInNotification(User user) {
		if (user == null) {
			return "-";
		}

		String caption = user.getFirstName() + " " + user.getLastName();
		if (StringUtils.isNotEmpty(user.getUserEmail())) {
			caption += " (" + user.getUserEmail() + ")";
		}
		return caption;
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
