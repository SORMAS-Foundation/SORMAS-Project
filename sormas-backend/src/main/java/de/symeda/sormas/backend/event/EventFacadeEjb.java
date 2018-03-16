package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.location.Location;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "EventFacade")
public class EventFacadeEjb implements EventFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private UserService userService;
	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private LocationService locationService;
	@EJB
	private TaskService taskService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventService.getAllUuids(user);
	}	
	
	@Override
	public List<EventDto> getAllEventsAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventService.getAllAfter(date, user).stream()
			.map(e -> toDto(e))
			.collect(Collectors.toList());
	}
		
	@Override
	public List<EventDto> getByUuids(List<String> uuids) {
		return eventService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<EventDto> getAllEventsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		District district = districtService.getByReferenceDto(districtRef);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventService.getAllBetween(fromDate, toDate, district, disease, user).stream()
				.map(e -> toDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<DashboardEventDto> getNewEventsForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		
		return eventService.getNewEventsForDashboard(region, district, disease, from, to, user);
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
		
		for (EventParticipant eventPerson : event.getEventPersons()) {
			eventParticipantService.udpateResultingCase(eventPerson);
		}
		
		return toDto(event);
	}
	
	@Override
	public List<EventReferenceDto> getSelectableEvents(UserReferenceDto userRef) {
		User user = userService.getByReferenceDto(userRef);
		return eventService.getAllAfter(null, user).stream()
				.map(c -> toReferenceDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public void deleteEvent(EventReferenceDto eventRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}
		
		Event event = eventService.getByReferenceDto(eventRef);
		List<EventParticipant> eventParticipants = eventParticipantService.getAllByEventAfter(null, event);
		for (EventParticipant eventParticipant : eventParticipants) {
			eventParticipantService.delete(eventParticipant);
		}
		List<Task> tasks = taskService.findBy(new TaskCriteria().eventEquals(eventRef));
		for (Task task : tasks) {
			taskService.delete(task);
		}
		eventService.delete(event);
	}
	
	@Override
	public List<EventIndexDto> getIndexList(String userUuid, EventCriteria eventCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventIndexDto> cq = cb.createQuery(EventIndexDto.class);
		Root<Event> event = cq.from(Event.class);
		Join<Event, Location> location = event.join(Event.EVENT_LOCATION, JoinType.LEFT);
		Join<Location, Region> region = location.join(Location.REGION, JoinType.LEFT);
		Join<Location, District> district = location.join(Location.DISTRICT, JoinType.LEFT);
		Join<Location, Community> community = location.join(Location.COMMUNITY, JoinType.LEFT);
		
		cq.multiselect(event.get(Event.UUID),
				event.get(Event.EVENT_TYPE),
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
				event.get(Event.REPORT_DATE_TIME)
		);
		
		Predicate filter = null;
		if (userUuid != null) {
			User user = userService.getByUuid(userUuid);
			filter = eventService.createUserFilter(cb, cq, event, user);
		}
		
		if (eventCriteria != null) {
			Predicate criteriaFilter = eventService.buildCriteriaFilter(eventCriteria, cb, event);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}
		
		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(event.get(Event.REPORT_DATE_TIME)));
		
		List<EventIndexDto> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	public Event fromDto(@NotNull EventDto source) {
		Event target = eventService.getByUuid(source.getUuid());
		if(target == null) {
			target = new Event();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
		
		target.setEventType(source.getEventType());
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
		if(entity == null) {
			return null;
		}
		
		EventReferenceDto dto = new EventReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	public static EventDto toDto(Event source) {
		if(source == null) {
			return null;
		}
		EventDto target = new EventDto();
		DtoHelper.fillDto(target, source);
		
		target.setEventType(source.getEventType());
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

	@LocalBean
	@Stateless
	public static class EventFacadeEjbLocal extends EventFacadeEjb {
	}	
}
