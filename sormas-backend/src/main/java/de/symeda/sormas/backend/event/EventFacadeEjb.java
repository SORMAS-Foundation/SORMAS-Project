package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventFacade;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationService;
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
	
	@Override
	public List<EventDto> getAllEventsAfter(Date date, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventService.getAllAfter(date, user).stream()
			.map(e -> toEventDto(e))
			.collect(Collectors.toList());
	}
	
	public Event fromEventDto(@NotNull EventDto source) {
		Event target = eventService.getByUuid(source.getUuid());
		if(target == null) {
			target = new Event();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		target.setEventType(source.getEventType());
		target.setEventStatus(source.getEventStatus());
		target.setEventDesc(source.getEventDesc());
		target.setEventDate(source.getEventDate());
		target.setReportDateTime(source.getReportDateTime());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setEventLocation(locationService.getByReferenceDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		
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
	
	public static EventDto toEventDto(Event source) {
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
		target.setEventLocation(LocationFacadeEjb.toReferenceDto(source.getEventLocation()));
		target.setTypeOfPlace(source.getTypeOfPlace());
		target.setSrcFirstName(source.getSrcFirstName());
		target.setSrcLastName(source.getSrcLastName());
		target.setSrcTelNo(source.getSrcTelNo());
		target.setSrcEmail(source.getSrcEmail());
		
		return target;
	}

}
