package de.symeda.sormas.backend.visit;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitFacade;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "VisitFacade")
public class VisitFacadeEjb implements VisitFacade {
	
	@EJB
	private VisitService visitService;	
	@EJB
	private ContactService contactService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;

	
	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return visitService.getAllUuids(user);
	}
	
	@Override
	public List<VisitDto> getAllVisitsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return visitService.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<VisitDto> getByUuids(List<String> uuids) {
		return visitService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<VisitDto> getAllByContact(ContactReferenceDto contactRef) {
		
		Contact contact = contactService.getByReferenceDto(contactRef);
		
		return visitService.getAllByContact(contact).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<VisitDto> getAllByPerson(PersonReferenceDto personRef) {
		
		Person person = personService.getByReferenceDto(personRef);
		
		return visitService.getAllByPerson(person).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public VisitDto getVisitByUuid(String uuid) {
		return toDto(visitService.getByUuid(uuid));
	}
	
	@Override
	public VisitReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(visitService.getByUuid(uuid));
	}
	
	@Override
	public VisitDto saveVisit(VisitDto dto) {
		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());
		Visit entity = fromDto(dto);
		visitService.ensurePersisted(entity);
		contactService.updateFollowUpUntilAndStatusByVisit(entity);
		return toDto(entity);
	}
	
	@Override
	public void deleteVisit(VisitReferenceDto visitRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}
		
		Visit visit = visitService.getByReferenceDto(visitRef);
		visitService.delete(visit);
	}

	public Visit fromDto(@NotNull VisitDto source) {
		
		Visit target = visitService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Visit();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
		
		target.setDisease(source.getDisease());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(userService.getByReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		
		return target;
	}
	
	public static VisitReferenceDto toReferenceDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitReferenceDto target = new VisitReferenceDto();
		DtoHelper.fillReferenceDto(target, source);
		return target;
	}	
	
	public static VisitDto toDto(Visit source) {
		if (source == null) {
			return null;
		}
		VisitDto target = new VisitDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setDisease(source.getDisease());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		target.setVisitDateTime(source.getVisitDateTime());
		target.setVisitRemarks(source.getVisitRemarks());
		target.setVisitStatus(source.getVisitStatus());
		target.setVisitUser(UserFacadeEjb.toReferenceDto(source.getVisitUser()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}
}
