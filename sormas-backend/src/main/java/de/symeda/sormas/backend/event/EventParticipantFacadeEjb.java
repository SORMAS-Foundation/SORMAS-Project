package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.event.EventReferenceDto;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "EventParticipantFacade")
public class EventParticipantFacadeEjb implements EventParticipantFacade {

	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	
	@Override
	public List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, EventReferenceDto eventRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Event event = eventService.getByUuid(eventRef.getUuid());
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		if(event == null) {
			return Collections.emptyList();
		}
		
		return eventParticipantService.getAllByEventAfter(date, event).stream()
				.map(e -> toEventParticipantDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public EventParticipantDto getEventParticipantByUuid(String uuid) {
		return toEventParticipantDto(eventParticipantService.getByUuid(uuid));
	}
	
	@Override
	public EventParticipantDto saveEventParticipant(EventParticipantDto dto) {
		EventParticipant entity = fromEventParticipantDto(dto);
		eventParticipantService.ensurePersisted(entity);
		return toEventParticipantDto(entity);
	}
	
	public EventParticipant fromEventParticipantDto(@NotNull EventParticipantDto source) {
		EventParticipant target = eventParticipantService.getByUuid(source.getUuid());
		if(target == null) {
			target = new EventParticipant();
			target.setUuid(source.getUuid());
			if(source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		target.setEvent(eventService.getByReferenceDto(source.getEvent()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setKindOfInvolvement(source.getKindOfInvolvement());
		
		return target;
	}
	
	public static EventParticipantReferenceDto toReferenceDto(EventParticipant entity) {
		if(entity == null) {
			return null;
		}
		
		EventParticipantDto dto = new EventParticipantDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}
	
	public static EventParticipantDto toEventParticipantDto(EventParticipant source) {
		if(source == null) {
			return null;
		}
		EventParticipantDto target = new EventParticipantDto();
		DtoHelper.fillReferenceDto(target, source);
		
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setKindOfInvolvement(source.getKindOfInvolvement());
		
		return target;
	}

}
