package de.symeda.sormas.backend.event;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.event.EventParticipantFacade;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
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
	private CaseService caseService;
	@EJB
	private UserService userService;
	
	@Override
	public List<EventParticipantDto> getAllEventParticipantsByEventAfter(Date date, String eventUuid, String userUuid) {
		User user = userService.getByUuid(userUuid);
		Event event = eventService.getByUuid(eventUuid);
		
		if(user == null) {
			return Collections.emptyList();
		}
		
		if(event == null) {
			return Collections.emptyList();
		}
		
		return eventParticipantService.getAllByEventAfter(date, event).stream()
				.map(e -> toDto(e))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventParticipantService.getAllUuids(user);
	}	
	
	@Override
	public List<EventParticipantDto> getAllEventParticipantsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return eventParticipantService.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
		
	@Override
	public List<EventParticipantDto> getByUuids(List<String> uuids) {
		return eventParticipantService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public EventParticipantDto getEventParticipantByUuid(String uuid) {
		return toDto(eventParticipantService.getByUuid(uuid));
	}
	
	@Override
	public EventParticipantDto saveEventParticipant(EventParticipantDto dto) {
		EventParticipant entity = fromDto(dto);
		eventParticipantService.ensurePersisted(entity);
		
		return toDto(entity);
	}
	
	@Override
	public void deleteEventParticipant(EventParticipantReferenceDto eventParticipantRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}
		
		EventParticipant eventParticipant = eventParticipantService.getByReferenceDto(eventParticipantRef);
		eventParticipantService.delete(eventParticipant);
	}
	
	public EventParticipant fromDto(@NotNull EventParticipantDto source) {
		
		EventParticipant target = eventParticipantService.getByUuid(source.getUuid());
		if (target == null) {
			target = new EventParticipant();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
		
		target.setEvent(eventService.getByReferenceDto(source.getEvent()));
		target.setPerson(personService.getByUuid(source.getPerson().getUuid()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));

		return target;
	}
	
	public static EventParticipantReferenceDto toReferenceDto(EventParticipant entity) {
		if(entity == null) {
			return null;
		}
		
		EventParticipantReferenceDto dto = new EventParticipantReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}
	
	public static EventParticipantDto toDto(EventParticipant source) {
		if(source == null) {
			return null;
		}
		EventParticipantDto target = new EventParticipantDto();
		DtoHelper.fillDto(target, source);
		
		target.setEvent(EventFacadeEjb.toReferenceDto(source.getEvent()));
		target.setPerson(PersonFacadeEjb.toDto(source.getPerson()));
		target.setInvolvementDescription(source.getInvolvementDescription());
		target.setResultingCase(CaseFacadeEjb.toReferenceDto(source.getResultingCase()));

		return target;
	}

	@LocalBean
	@Stateless
	public static class EventParticipantFacadeEjbLocal extends EventParticipantFacadeEjb {
	}	
}
