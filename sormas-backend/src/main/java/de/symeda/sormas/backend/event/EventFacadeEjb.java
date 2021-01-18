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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseOutcome;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventExportDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.CriteriaBuilderHelper;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserRoleConfigFacadeEjb.UserRoleConfigFacadeEjbLocal;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless(name = "EventFacade")
public class EventFacadeEjb implements EventFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private EventService eventService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private UserRoleConfigFacadeEjbLocal userRoleConfigFacade;
	@EJB
	private EventJurisdictionChecker eventJurisdictionChecker;

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getAllActiveUuids();
	}

	@Override
	public List<EventDto> getAllActiveEventsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return eventService.getAllActiveEventsAfter(date).stream().map(e -> convertToDto(e, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<EventDto> getByUuids(List<String> uuids) {
		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		return eventService.getByUuids(uuids).stream().map(e -> convertToDto(e, pseudonymizer)).collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getDeletedUuidsSince(since);
	}

	@Override
	public List<DashboardEventDto> getNewEventsForDashboard(EventCriteria eventCriteria) {

		return eventService.getNewEventsForDashboard(eventCriteria);
	}

	public Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria) {

		return eventService.getEventCountByDisease(eventCriteria);
	}

	public Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria) {

		return eventService.getEventCountByStatus(eventCriteria);
	}

	@Override
	public EventDto getEventByUuid(String uuid) {
		return convertToDto(eventService.getByUuid(uuid), Pseudonymizer.getDefault(userService::hasRight));
	}

	@Override
	public EventReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(eventService.getByUuid(uuid));
	}

	@Override
	public EventReferenceDto getReferenceByEventParticipant(String uuid) {
		return toReferenceDto(eventService.getEventReferenceByEventParticipant(uuid));
	}

	@Override
	public EventDto saveEvent(@NotNull EventDto dto) {

		Pseudonymizer pseudonymizer = Pseudonymizer.getDefault(userService::hasRight);
		Event existingEvent = dto.getUuid() != null ? eventService.getByUuid(dto.getUuid()) : null;
		EventDto existingDto = toDto(existingEvent);

		restorePseudonymizedDto(dto, existingEvent, existingDto, pseudonymizer);

		Event event = fromDto(dto, true);
		eventService.ensurePersisted(event);

		return convertToDto(event, pseudonymizer);
	}

	@Override
	public void deleteEvent(String eventUuid) {

		if (!userService.hasRight(UserRight.EVENT_DELETE)) {
			throw new UnsupportedOperationException("User " + userService.getCurrentUser().getUuid() + " is not allowed to delete events.");
		}

		eventService.delete(eventService.getByUuid(eventUuid));
	}

	@Override
	public long count(EventCriteria eventCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event> event = cq.from(Event.class);

		Predicate filter = null;

		if (eventCriteria != null) {
			if (eventCriteria.getUserFilterIncluded()) {
				eventService.createUserFilter(cb, cq, event);
			}

			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);
		cq.select(cb.count(event));
		return em.createQuery(cq).getSingleResult();
	}

	@Override
	public List<EventIndexDto> getIndexList(EventCriteria eventCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventIndexDto> cq = cb.createQuery(EventIndexDto.class);
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> location = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
		Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
		Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EVENT_STATUS),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVENT_TITLE),
			region.get(Region.UUID),
			region.get(Region.NAME),
			district.get(District.UUID),
			district.get(District.NAME),
			community.get(Community.UUID),
			community.get(Community.NAME),
			location.get(Location.CITY),
			location.get(Location.STREET),
			location.get(Location.HOUSE_NUMBER),
			location.get(Location.ADDITIONAL_INFORMATION),
			event.get(Event.SRC_TYPE),
			event.get(Event.SRC_FIRST_NAME),
			event.get(Event.SRC_LAST_NAME),
			event.get(Event.SRC_TEL_NO),
			event.get(Event.SRC_MEDIA_WEBSITE),
			event.get(Event.SRC_MEDIA_NAME),
			event.get(Event.REPORT_DATE_TIME),
			event.join(Event.REPORTING_USER, JoinType.LEFT).get(User.UUID),
			event.join(Event.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID));

		Predicate filter = null;

		if (eventCriteria != null) {
			if (eventCriteria.getUserFilterIncluded()) {
				eventService.createUserFilter(cb, cq, event);
			}

			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventIndexDto.UUID:
				case EventIndexDto.EVENT_STATUS:
				case EventIndexDto.EVENT_INVESTIGATION_STATUS:
				case EventIndexDto.DISEASE:
				case EventIndexDto.DISEASE_DETAILS:
				case EventIndexDto.START_DATE:
				case EventIndexDto.EVENT_TITLE:
				case EventIndexDto.SRC_FIRST_NAME:
				case EventIndexDto.SRC_LAST_NAME:
				case EventIndexDto.SRC_TEL_NO:
				case EventIndexDto.SRC_TYPE:
				case EventIndexDto.REPORT_DATE_TIME:
					expression = event.get(sortProperty.propertyName);
					break;
				case EventIndexDto.EVENT_LOCATION:
					expression = region.get(Region.NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = district.get(District.NAME);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = community.get(Community.NAME);
					break;
				case EventIndexDto.REGION:
					expression = region.get(Region.NAME);
					break;
				case EventIndexDto.DISTRICT:
					expression = district.get(District.NAME);
					break;
				case EventIndexDto.COMMUNITY:
					expression = community.get(Community.NAME);
					break;
				case EventIndexDto.ADDRESS:
					expression = location.get(Location.CITY);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.STREET);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.HOUSE_NUMBER);
					order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
					expression = location.get(Location.ADDITIONAL_INFORMATION);
					break;
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(event.get(Contact.CHANGE_DATE)));
		}

		List<EventIndexDto> indexList;
		if (first != null && max != null) {
			indexList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			indexList = em.createQuery(cq).getResultList();
		}

		Map<String, Long> participantCounts = new HashMap<>();
		Map<String, Long> caseCounts = new HashMap<>();;
		Map<String, Long> deathCounts = new HashMap<>();
		if (indexList != null) {
			List<Object[]> objectQueryList = null;

			CriteriaQuery<Object[]> objectCQ = cb.createQuery(Object[].class);
			Root<Event> eventRoot = objectCQ.from(Event.class);

			// number of Participants
			Subquery<Long> participantCount = objectCQ.subquery(Long.class);
			Root<EventParticipant> eventParticipantRoot = participantCount.from(EventParticipant.class);
			Predicate assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			Predicate notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			participantCount.select(cb.count(eventParticipantRoot));
			participantCount.where(assignedToEvent, notDeleted);

			// number of cases among event participants
			Subquery<Long> caseCount = objectCQ.subquery(Long.class);
			eventParticipantRoot = caseCount.from(EventParticipant.class);
			assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			Predicate isCase = cb.isNotNull(eventParticipantRoot.get(EventParticipant.RESULTING_CASE));
			caseCount.select(cb.count(eventParticipantRoot));
			caseCount.where(assignedToEvent, notDeleted, isCase);

			// number of fatalities among event participant cases
			Subquery<Long> deathsCount = objectCQ.subquery(Long.class);
			eventParticipantRoot = deathsCount.from(EventParticipant.class);
			Join<EventParticipant, Case> caseJoin = eventParticipantRoot.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
			assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			isCase = cb.isNotNull(eventParticipantRoot.get(EventParticipant.RESULTING_CASE));
			Predicate isDead = cb.equal(caseJoin.get(Case.OUTCOME), CaseOutcome.DECEASED);
			deathsCount.select(cb.count(eventParticipantRoot));
			deathsCount.where(assignedToEvent, notDeleted, isCase, isDead);

			objectCQ.multiselect(eventRoot.get(Event.UUID), participantCount, caseCount, deathsCount);
			objectQueryList = em.createQuery(objectCQ).getResultList();
			objectQueryList.forEach(r -> {
				participantCounts.put((String) r[0], (Long) r[1]);
				caseCounts.put((String) r[0], (Long) r[2]);
				deathCounts.put((String) r[0], (Long) r[3]);
			});

		}

		if (indexList != null) {
			for (EventIndexDto eventDto : indexList) {
				Optional.ofNullable(participantCounts.get(eventDto.getUuid())).ifPresent(eventDto::setParticipantCount);
				Optional.ofNullable(caseCounts.get(eventDto.getUuid())).ifPresent(eventDto::setCaseCount);
				Optional.ofNullable(deathCounts.get(eventDto.getUuid())).ifPresent(eventDto::setDeathCount);
			}
		}

		return indexList;

	}

	@Override
	public List<EventExportDto> getExportList(EventCriteria eventCriteria, Integer first, Integer max) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventExportDto> cq = cb.createQuery(EventExportDto.class);
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> location = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
		Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
		Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);

		cq.multiselect(
			event.get(Event.UUID),
			event.get(Event.EXTERNAL_ID),
			event.get(Event.EXTERNAL_TOKEN),
			event.get(Event.EVENT_STATUS),
			event.get(Event.RISK_LEVEL),
			event.get(Event.EVENT_INVESTIGATION_STATUS),
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.START_DATE),
			event.get(Event.END_DATE),
			event.get(Event.EVENT_TITLE),
			event.get(Event.EVENT_DESC),
			event.get(Event.DISEASE_TRANSMISSION_MODE),
			event.get(Event.NOSOCOMIAL),
			event.get(Event.TRANSREGIONAL_OUTBREAK),
			event.get(Event.MEANS_OF_TRANSPORT),
			event.get(Event.MEANS_OF_TRANSPORT_DETAILS),
			region.get(Region.UUID),
			region.get(Region.NAME),
			district.get(District.UUID),
			district.get(District.NAME),
			community.get(Community.UUID),
			community.get(Community.NAME),
			location.get(Location.CITY),
			location.get(Location.STREET),
			location.get(Location.HOUSE_NUMBER),
			location.get(Location.ADDITIONAL_INFORMATION),
			event.get(Event.SRC_TYPE),
			event.get(Event.SRC_INSTITUTIONAL_PARTNER_TYPE),
			event.get(Event.SRC_INSTITUTIONAL_PARTNER_TYPE_DETAILS),
			event.get(Event.SRC_FIRST_NAME),
			event.get(Event.SRC_LAST_NAME),
			event.get(Event.SRC_TEL_NO),
			event.get(Event.SRC_EMAIL),
			event.get(Event.SRC_MEDIA_WEBSITE),
			event.get(Event.SRC_MEDIA_NAME),
			event.get(Event.SRC_MEDIA_DETAILS),
			event.get(Event.REPORT_DATE_TIME),
			event.join(Event.REPORTING_USER, JoinType.LEFT).get(User.UUID),
			event.join(Event.SURVEILLANCE_OFFICER, JoinType.LEFT).get(User.UUID));

		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (eventCriteria != null) {
			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = CriteriaBuilderHelper.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);
		cq.orderBy(cb.desc(event.get(Event.REPORT_DATE_TIME)));

		List<EventExportDto> exportList;
		if (first != null && max != null) {
			exportList = em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();

		} else {
			exportList = em.createQuery(cq).getResultList();
		}

		Map<String, Long> participantCounts = new HashMap<>();
		Map<String, Long> caseCounts = new HashMap<>();;
		Map<String, Long> deathCounts = new HashMap<>();
		if (exportList != null) {
			List<Object[]> objectQueryList = null;

			CriteriaQuery<Object[]> objectCQ = cb.createQuery(Object[].class);
			Root<Event> eventRoot = objectCQ.from(Event.class);

			// number of Participants
			Subquery<Long> participantCount = objectCQ.subquery(Long.class);
			Root<EventParticipant> eventParticipantRoot = participantCount.from(EventParticipant.class);
			Predicate assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			Predicate notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			participantCount.select(cb.count(eventParticipantRoot));
			participantCount.where(assignedToEvent, notDeleted);

			// number of cases among event participants
			Subquery<Long> caseCount = objectCQ.subquery(Long.class);
			eventParticipantRoot = caseCount.from(EventParticipant.class);
			assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			Predicate isCase = cb.isNotNull(eventParticipantRoot.get(EventParticipant.RESULTING_CASE));
			caseCount.select(cb.count(eventParticipantRoot));
			caseCount.where(assignedToEvent, notDeleted, isCase);

			// number of fatalities among event participant cases
			Subquery<Long> deathsCount = objectCQ.subquery(Long.class);
			eventParticipantRoot = deathsCount.from(EventParticipant.class);
			Join<EventParticipant, Case> caseJoin = eventParticipantRoot.join(EventParticipant.RESULTING_CASE, JoinType.LEFT);
			assignedToEvent = cb.equal(eventParticipantRoot.get(EventParticipant.EVENT), eventRoot.get(AbstractDomainObject.ID));
			notDeleted = cb.isFalse(eventParticipantRoot.get(EventParticipant.DELETED));
			isCase = cb.isNotNull(eventParticipantRoot.get(EventParticipant.RESULTING_CASE));
			Predicate isDead = cb.equal(caseJoin.get(Case.OUTCOME), CaseOutcome.DECEASED);
			deathsCount.select(cb.count(eventParticipantRoot));
			deathsCount.where(assignedToEvent, notDeleted, isCase, isDead);

			objectCQ.multiselect(eventRoot.get(Event.UUID), participantCount, caseCount, deathsCount);
			objectQueryList = em.createQuery(objectCQ).getResultList();
			objectQueryList.forEach(r -> {
				participantCounts.put((String) r[0], (Long) r[1]);
				caseCounts.put((String) r[0], (Long) r[2]);
				deathCounts.put((String) r[0], (Long) r[3]);
			});

		}

		if (exportList != null) {
			for (EventExportDto exportDto : exportList) {
				Optional.ofNullable(participantCounts.get(exportDto.getUuid())).ifPresent(exportDto::setParticipantCount);
				Optional.ofNullable(caseCounts.get(exportDto.getUuid())).ifPresent(exportDto::setCaseCount);
				Optional.ofNullable(deathCounts.get(exportDto.getUuid())).ifPresent(exportDto::setDeathCount);
			}
		}

		return exportList;
	}

	@Override
	public boolean isArchived(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event> from = cq.from(Event.class);

		// Workaround for probable bug in Eclipse Link/Postgre that throws a NoResultException when trying to
		// query for a true Boolean result
		cq.where(cb.and(cb.equal(from.get(Event.ARCHIVED), true), cb.equal(from.get(AbstractDomainObject.UUID), eventUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public boolean isDeleted(String eventUuid) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Event> from = cq.from(Event.class);

		cq.where(cb.and(cb.isTrue(from.get(Event.DELETED)), cb.equal(from.get(AbstractDomainObject.UUID), eventUuid)));
		cq.select(cb.count(from));
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}

	@Override
	public void archiveOrDearchiveEvent(String eventUuid, boolean archive) {

		Event event = eventService.getByUuid(eventUuid);
		event.setArchived(archive);
		eventService.ensurePersisted(event);
	}

	@Override
	public List<String> getArchivedUuidsSince(Date since) {

		User user = userService.getCurrentUser();

		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getArchivedUuidsSince(since);
	}

	@Override
	public Set<String> getAllSubordinateEventUuids(String eventUuid) {

		Set<String> uuids = new HashSet<>();
		Event superordinateEvent = eventService.getByUuid(eventUuid);
		addAllSubordinateEventsToSet(superordinateEvent, uuids);

		return uuids;
	}

	private void addAllSubordinateEventsToSet(Event event, Set<String> uuids) {

		uuids.addAll(event.getSubordinateEvents().stream().map(AbstractDomainObject::getUuid).collect(Collectors.toSet()));
		event.getSubordinateEvents().forEach(e -> addAllSubordinateEventsToSet(e, uuids));
	}

	@Override
	public Set<String> getAllSuperordinateEventUuids(String eventUuid) {

		Set<String> uuids = new HashSet<>();
		Event event = eventService.getByUuid(eventUuid);
		addSuperordinateEventToSet(event, uuids);

		return uuids;
	}

	private void addSuperordinateEventToSet(Event event, Set<String> uuids) {

		if (event.getSuperordinateEvent() != null) {
			uuids.add(event.getSuperordinateEvent().getUuid());
			addSuperordinateEventToSet(event.getSuperordinateEvent(), uuids);
		}
	}

	public Event fromDto(@NotNull EventDto source, boolean checkChangeDate) {
		Event target = eventService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Event();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target, checkChangeDate);

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setEventLocation(locationFacade.fromDto(source.getEventLocation(), checkChangeDate));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setTravelDate(source.getTravelDate());
		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());
		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());
		target.setSuperordinateEvent(eventService.getByReferenceDto(source.getSuperordinateEvent()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public EventDto convertToDto(Event source, Pseudonymizer pseudonymizer) {
		EventDto eventDto = toDto(source);

		pseudonymizeDto(source, eventDto, pseudonymizer);

		return eventDto;
	}

	private void pseudonymizeDto(Event event, EventDto dto, Pseudonymizer pseudonymizer) {
		if (dto != null) {
			boolean inJurisdiction = eventJurisdictionChecker.isInJurisdictionOrOwned(event);

			pseudonymizer.pseudonymizeDto(EventDto.class, dto, inJurisdiction, (e) -> {
				pseudonymizer.pseudonymizeUser(event.getReportingUser(), userService.getCurrentUser(), dto::setReportingUser);
			});
		}
	}

	private void restorePseudonymizedDto(EventDto dto, Event existingEvent, EventDto existingDto, Pseudonymizer pseudonymizer) {
		if (existingDto != null) {
			boolean inJurisdiction = eventJurisdictionChecker.isInJurisdictionOrOwned(existingEvent);
			pseudonymizer.restorePseudonymizedValues(EventDto.class, dto, existingDto, inJurisdiction);
			pseudonymizer.restoreUser(existingEvent.getReportingUser(), userService.getCurrentUser(), dto, dto::setReportingUser);
		}
	}

	public static EventReferenceDto toReferenceDto(Event entity) {

		if (entity == null) {
			return null;
		}

		return new EventReferenceDto(entity.getUuid(), entity.toString());
	}

	public static EventDto toDto(Event source) {

		if (source == null) {
			return null;
		}
		EventDto target = new EventDto();
		DtoHelper.fillDto(target, source);

		target.setEventStatus(source.getEventStatus());
		target.setRiskLevel(source.getRiskLevel());
		target.setEventInvestigationStatus(source.getEventInvestigationStatus());
		target.setEventInvestigationStartDate(source.getEventInvestigationStartDate());
		target.setEventInvestigationEndDate(source.getEventInvestigationEndDate());
		target.setExternalId(source.getExternalId());
		target.setExternalToken(source.getExternalToken());
		target.setEventTitle(source.getEventTitle());
		target.setEventDesc(source.getEventDesc());
		target.setNosocomial(source.getNosocomial());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setEventLocation(LocationFacadeEjb.toDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setMeansOfTransport(source.getMeansOfTransport());
		target.setMeansOfTransportDetails(source.getMeansOfTransportDetails());
		target.setConnectionNumber(source.getConnectionNumber());
		target.setSeatNumber(source.getSeatNumber());
		target.setTravelDate(source.getTravelDate());
		target.setSrcType(source.getSrcType());
		target.setSrcInstitutionalPartnerType(source.getSrcInstitutionalPartnerType());
		target.setSrcInstitutionalPartnerTypeDetails(source.getSrcInstitutionalPartnerTypeDetails());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setSrcMediaWebsite(source.getSrcMediaWebsite());
		target.setSrcMediaName(source.getSrcMediaName());
		target.setSrcMediaDetails(source.getSrcMediaDetails());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());
		target.setTransregionalOutbreak(source.getTransregionalOutbreak());
		target.setDiseaseTransmissionMode(source.getDiseaseTransmissionMode());
		target.setSuperordinateEvent(EventFacadeEjb.toReferenceDto(source.getSuperordinateEvent()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	/**
	 * Archives all events that have not been changed for a defined amount of days
	 * 
	 * @param daysAfterEventsGetsArchived
	 *            defines the amount of days
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void archiveAllArchivableEvents(int daysAfterEventGetsArchived) {

		archiveAllArchivableEvents(daysAfterEventGetsArchived, LocalDate.now());
	}

	void archiveAllArchivableEvents(int daysAfterEventGetsArchived, @NotNull LocalDate referenceDate) {

		LocalDate notChangedSince = referenceDate.minusDays(daysAfterEventGetsArchived);

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Event> from = cq.from(Event.class);

		Timestamp notChangedTimestamp = Timestamp.valueOf(notChangedSince.atStartOfDay());
		cq.where(cb.equal(from.get(Event.ARCHIVED), false), cb.not(eventService.createChangeDateFilter(cb, from, notChangedTimestamp)));
		cq.select(from.get(Event.UUID));
		List<String> uuids = em.createQuery(cq).getResultList();

		if (!uuids.isEmpty()) {

			CriteriaUpdate<Event> cu = cb.createCriteriaUpdate(Event.class);
			Root<Event> root = cu.from(Event.class);

			cu.set(root.get(Event.ARCHIVED), true);

			cu.where(root.get(Event.UUID).in(uuids));

			em.createQuery(cu).executeUpdate();
		}
	}

	@Override
	public boolean exists(String uuid) {
		return eventService.exists(uuid);
	}

	@Override
	public String getUuidByCaseUuidOrPersonUuid(String searchTerm) {
		return eventService.getUuidByCaseUuidOrPersonUuid(searchTerm);
	}

	@LocalBean
	@Stateless
	public static class EventFacadeEjbLocal extends EventFacadeEjb {

	}

	public Boolean isEventEditAllowed(String eventUuid) {

		Event event = eventService.getByUuid(eventUuid);
		return eventJurisdictionChecker.isInJurisdictionOrOwned(event);
	}
}
