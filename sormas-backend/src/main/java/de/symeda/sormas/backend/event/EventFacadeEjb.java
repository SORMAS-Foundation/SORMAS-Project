package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.location.LocationService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "EventFacade")
public class EventFacadeEjb implements EventFacade {

	@EJB
	private UserService userService;
	@EJB
	private EventService eventService;
	@EJB
	private LocationService locationService;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
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
	public List<EventReferenceDto> getSelectableEvents(UserReferenceDto userRef) {
		User user = userService.getByReferenceDto(userRef);
		return eventService.getAllAfter(null, user).stream()
				.map(c -> toReferenceDto(c))
				.collect(Collectors.toList());
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
		
		EventReferenceDto dto = new EventReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	public static EventDto toDto(Event source) {
		if(source == null) {
			return null;
		}
		EventDto target = new EventDto();
		DtoHelper.fillReferenceDto(target, source);
		
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

}
