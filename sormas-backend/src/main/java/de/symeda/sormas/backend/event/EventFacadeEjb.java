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
import java.util.List;
import java.util.Map;
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
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.event.EventStatus;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.AbstractDomainObject;
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

	@Override
	public List<String> getAllActiveUuids() {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getAllActiveUuids(user);
	}

	@Override
	public List<EventDto> getAllActiveEventsAfter(Date date) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getAllActiveEventsAfter(date, user).stream().map(e -> toDto(e)).collect(Collectors.toList());
	}

	@Override
	public List<EventDto> getByUuids(List<String> uuids) {
		return eventService.getByUuids(uuids).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<String> getDeletedUuidsSince(Date since) {

		User user = userService.getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}

		return eventService.getDeletedUuidsSince(user, since);
	}

	@Override
	public List<DashboardEventDto> getNewEventsForDashboard(EventCriteria eventCriteria) {

		User user = userService.getCurrentUser();
		return eventService.getNewEventsForDashboard(eventCriteria, user);
	}

	public Map<Disease, Long> getEventCountByDisease(EventCriteria eventCriteria) {

		User user = userService.getCurrentUser();
		return eventService.getEventCountByDisease(eventCriteria, user);
	}

	public Map<EventStatus, Long> getEventCountByStatus(EventCriteria eventCriteria) {

		User user = userService.getCurrentUser();
		return eventService.getEventCountByStatus(eventCriteria, user);
	}

	@Override
	public EventDto getEventByUuid(String uuid) {
		return toDto(eventService.getByUuid(uuid));
	}

	@Override
	public EventReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(eventService.getByUuid(uuid));
	}

	@Override
	public EventDto saveEvent(EventDto dto) {

		Event event = fromDto(dto);
		eventService.ensurePersisted(event);
		return toDto(event);
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

		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (eventCriteria != null) {
			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
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
			event.get(Event.DISEASE),
			event.get(Event.DISEASE_DETAILS),
			event.get(Event.EVENT_DATE),
			event.get(Event.EVENT_DESC),
			location.get(Location.UUID),
			region.get(Region.NAME),
			district.get(District.NAME),
			community.get(Community.NAME),
			location.get(Location.CITY),
			location.get(Location.ADDRESS),
			event.get(Event.SRC_FIRST_NAME),
			event.get(Event.SRC_LAST_NAME),
			event.get(Event.SRC_TEL_NO),
			event.get(Event.REPORT_DATE_TIME));

		Predicate filter = eventService.createUserFilter(cb, cq, event);

		if (eventCriteria != null) {
			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		cq.where(filter);

		if (sortProperties != null && sortProperties.size() > 0) {
			List<Order> order = new ArrayList<Order>(sortProperties.size());
			for (SortProperty sortProperty : sortProperties) {
				Expression<?> expression;
				switch (sortProperty.propertyName) {
				case EventIndexDto.UUID:
				case EventIndexDto.EVENT_STATUS:
				case EventIndexDto.DISEASE:
				case EventIndexDto.DISEASE_DETAILS:
				case EventIndexDto.EVENT_DATE:
				case EventIndexDto.EVENT_DESC:
				case EventIndexDto.SRC_FIRST_NAME:
				case EventIndexDto.SRC_LAST_NAME:
				case EventIndexDto.SRC_TEL_NO:
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
				default:
					throw new IllegalArgumentException(sortProperty.propertyName);
				}
				order.add(sortProperty.ascending ? cb.asc(expression) : cb.desc(expression));
			}
			cq.orderBy(order);
		} else {
			cq.orderBy(cb.desc(event.get(Contact.CHANGE_DATE)));
		}

		if (first != null && max != null) {
			return em.createQuery(cq).setFirstResult(first).setMaxResults(max).getResultList();
		} else {
			return em.createQuery(cq).getResultList();
		}
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

		return eventService.getArchivedUuidsSince(user, since);
	}

	public Event fromDto(@NotNull EventDto source) {

		Event target = eventService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Event();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setEventStatus(source.getEventStatus());
		target.setEventDesc(source.getEventDesc());
		target.setEventDate(source.getEventDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setEventLocation(locationFacade.fromDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static EventReferenceDto toReferenceDto(Event entity) {

		if (entity == null) {
			return null;
		}

		EventReferenceDto dto = new EventReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static EventDto toDto(Event source) {

		if (source == null) {
			return null;
		}
		EventDto target = new EventDto();
		DtoHelper.fillDto(target, source);

		target.setEventStatus(source.getEventStatus());
		target.setEventDesc(source.getEventDesc());
		target.setEventDate(source.getEventDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setEventLocation(LocationFacadeEjb.toDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setTypeOfPlaceText(source.getTypeOfPlaceText());

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

	void archiveAllArchivableEvents(int daysAfterEventGetsArchived, LocalDate referenceDate) {

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

	@LocalBean
	@Stateless
	public static class EventFacadeEjbLocal extends EventFacadeEjb {

	}
}
