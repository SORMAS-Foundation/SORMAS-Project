package de.symeda.sormas.backend.contact;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskCriteria;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless(name = "ContactFacade")
public class ContactFacadeEjb implements ContactFacade {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ContactService contactService;	
	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private UserService userService;
	@EJB
	private VisitService visitService;
	@EJB
	private TaskService taskService;

	@Override
	public List<String> getAllUuids(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return contactService.getAllUuids(user);
	}	
	
	@Override
	public List<ContactDto> getAllContactsAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return contactService.getAllAfter(date, user).stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactDto> getFollowUpBetween(Date fromDate, Date toDate, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return contactService.getFollowUpBetween(fromDate, toDate, disease, user).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactDto> getAllByCase(CaseReferenceDto caseRef) {
		Case caze = caseService.getByReferenceDto(caseRef);
		
		return contactService.getAllByCase(caze).stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactIndexDto> getIndexList(String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return contactService.getAllAfter(null, user).stream()
			.map(c -> toIndexDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactIndexDto> getIndexListByCase(CaseReferenceDto caseRef) {
		
		Case caze = caseService.getByReferenceDto(caseRef);
		
		return contactService.getAllByCase(caze).stream()
			.map(c -> toIndexDto(c))
			.collect(Collectors.toList());
	}
	

	@Override
	public ContactDto getContactByUuid(String uuid) {
		return toDto(contactService.getByUuid(uuid));
	}
	
	@Override
	public ContactReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(contactService.getByUuid(uuid));
	}
	
	@Override
	public ContactDto saveContact(ContactDto dto) {
		Contact entity = fromDto(dto);
		contactService.ensurePersisted(entity);
		contactService.updateFollowUpUntilAndStatus(entity);
		return toDto(entity);
	}
	
	@Override
	public ContactDto updateFollowUpUntilAndStatus(ContactDto dto) {
		Contact entity = fromDto(dto);
		contactService.updateFollowUpUntilAndStatus(entity);
		return toDto(entity);
	}
	
	@Override
	public List<ContactReferenceDto> getSelectableContacts(UserReferenceDto userRef) {
		User user = userService.getByReferenceDto(userRef);
		return contactService.getAllAfter(null, user).stream()
				.map(c -> toReferenceDto(c))
				.collect(Collectors.toList());
	}

	public Contact fromDto(@NotNull ContactDto source) {
		
		Contact target = contactService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Contact();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);

		target.setCaze(caseService.getByReferenceDto(source.getCaze()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());
		
		// use only date, not time
		target.setLastContactDate(DateHelper8.toDate(DateHelper8.toLocalDate(source.getLastContactDate())));
		if (target.getLastContactDate() != null && target.getLastContactDate().after(target.getReportDateTime())) {
			throw new ValidationException(Contact.LAST_CONTACT_DATE + " has to be before " + Contact.REPORT_DATE_TIME);
		}
		
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());

		return target;
	}
	
	public static ContactReferenceDto toReferenceDto(Contact source) {
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
		target.setRelationToCase(source.getRelationToCase());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		
		return target;
	}
	
	public ContactIndexDto toIndexDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactIndexDto target = new ContactIndexDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setCazePerson(PersonFacadeEjb.toReferenceDto(source.getCaze().getPerson()));
		target.setCazeDisease(source.getCaze().getDisease());
		target.setCazeDistrict(DistrictFacadeEjb.toReferenceDto(source.getCaze().getDistrict()));
		
		target.setLastContactDate(source.getLastContactDate());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		
		// TODO optimize performance by using count query
		List<Visit> visits = visitService.getAllByContact(source);
		int numberOfCooperativeVisits = 0;
		int numberOfMissedVisits = 0;
		for (Visit visit : visits) {
			if (visit.getVisitStatus() == VisitStatus.COOPERATIVE) {
				numberOfCooperativeVisits++;
			} else {
				numberOfMissedVisits++;
			}
		}
		target.setNumberOfCooperativeVisits(numberOfCooperativeVisits);
		target.setNumberOfMissedVisits(numberOfMissedVisits);
		
		return target;
	}
	
	@RolesAllowed(UserRole._SYSTEM)
	public void generateContactFollowUpTasks() {
		
		// get all contacts that are followed up
		LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
		LocalDateTime toDateTime = fromDateTime.plusDays(1);
		List<Contact> contacts = contactService.getFollowUpBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime), null, null);

		for (Contact contact : contacts) {
			// find already existing tasks
			TaskCriteria taskCriteria = new TaskCriteria()
					.contactEquals(contact)
					.taskTypeEquals(TaskType.CONTACT_FOLLOW_UP)
					.dueDateBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime));
			List<Task> tasks = taskService.findBy(taskCriteria);
			
			if (tasks.isEmpty()) {
				// none found -> create the task
				Task task = taskService.buildTask(null);
				task.setTaskContext(TaskContext.CONTACT);
				task.setContact(contact);
				task.setTaskType(TaskType.CONTACT_FOLLOW_UP);
				task.setSuggestedStart(DateHelper8.toDate(fromDateTime));
				task.setDueDate(DateHelper8.toDate(toDateTime.minusMinutes(1)));
				// assign responsible user
				if (contact.getContactOfficer() != null) {
					// A. contact officer
					task.setAssigneeUser(contact.getContactOfficer());
				} else {
					// use region where contact person lifes
					Region region = contact.getPerson().getAddress().getRegion();
					if (region == null) {
						// fallback: use region of related caze
						region = contact.getCaze().getRegion();
					}
					// B. contact supervisor
					List<User> users = userService.getAllByRegionAndUserRoles(region, UserRole.CONTACT_SUPERVISOR);
					if (!users.isEmpty()) {
						task.setAssigneeUser(users.get(0));
					} else {
						logger.warn("Could not assign responsible user to contact follow-up task: " + task.getUuid());
					}
				}
				taskService.ensurePersisted(task);
			}
		}
	}

	
	@LocalBean
	@Stateless
	public static class ContactFacadeEjbLocal extends ContactFacadeEjb {
	}
}
