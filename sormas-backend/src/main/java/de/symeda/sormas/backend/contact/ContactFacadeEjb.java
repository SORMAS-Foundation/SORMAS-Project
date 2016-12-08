package de.symeda.sormas.backend.contact;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "ContactFacade")
public class ContactFacadeEjb implements ContactFacade {
	
	@EJB
	private ContactService service;	
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;

	
	@Override
	public List<ContactDto> getAllContactsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactIndexDto> getIndexList(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return service.getAllAfter(null, user).stream()
			.map(c -> toIndexDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactIndexDto> getIndexListByCase(String userUuid, CaseReferenceDto caseRef) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		Case caze = caseService.getByReferenceDto(caseRef);
		
		return service.getAllByCase(caze, user).stream()
			.map(c -> toIndexDto(c))
			.collect(Collectors.toList());
	}
	

	@Override
	public ContactDto getContactByUuid(String uuid) {
		return toDto(service.getByUuid(uuid));
	}
	
	@Override
	public ContactReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(service.getByUuid(uuid));
	}
	
	@Override
	public ContactDto saveContact(ContactDto dto) {
		Contact entity = fromDto(dto);
		updateFollowUpUntil(entity);
		service.ensurePersisted(entity);
		return toDto(entity);
	}

	public Contact fromDto(@NotNull ContactDto source) {
		
		Contact target = service.getByUuid(source.getUuid());
		if (target == null) {
			target = new Contact();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		
		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		
		target.setLastContactDate(source.getLastContactDate());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());

		return target;
	}
	
	public ContactReferenceDto toReferenceDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactReferenceDto target = new ContactReferenceDto();
		DtoHelper.fillReferenceDto(target, source);
		return target;
	}	
	
	public static ContactDto toDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactDto target = new ContactDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		
		target.setLastContactDate(source.getLastContactDate());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		
		return target;
	}
	
	public static ContactIndexDto toIndexDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactIndexDto target = new ContactIndexDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setCazePerson(PersonFacadeEjb.toReferenceDto(source.getCaze().getPerson()));
		target.setCazeDisease(source.getCaze().getDisease());
		target.setCazeDistrict(DtoHelper.toReferenceDto(source.getCaze().getDistrict()));
		
		target.setLastContactDate(source.getLastContactDate());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		
		return target;
	}
	
	private int getFollowUpDuration(Disease disease) {
		switch (disease) {
		case EVD:
		case CHOLERA:
			return 21;
		case LASSA:
			return 6;
		default:
			return 0;
		}
	}
	
	private void updateFollowUpUntil(Contact contact) {
		Disease disease = contact.getCaze().getDisease();
		int followUpDuration = getFollowUpDuration(disease);

		if (followUpDuration == 0) {
			contact.setFollowUpUntil(null);
			contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
		} else {
			LocalDate beginDate = contact.getReportDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			contact.setFollowUpUntil(Date.from(beginDate.plusDays(followUpDuration).atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}
	}
}
