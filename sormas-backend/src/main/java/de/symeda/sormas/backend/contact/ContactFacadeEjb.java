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
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactMapDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
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
import de.symeda.sormas.backend.visit.VisitFacadeEjb;
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
	@EJB
	private DistrictService districtService;
	
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
	public List<ContactDto> getByUuids(List<String> uuids) {
		return contactService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<ContactDto> getFollowUpBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		District district = districtService.getByReferenceDto(districtRef);

		if (user == null) {
			return Collections.emptyList();
		}

		return contactService.getFollowUpBetween(fromDate, toDate, district, disease, user).stream()
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
		
		if (!DiseaseHelper.hasContactFollowUp(FacadeProvider.getCaseFacade().getCaseDataByUuid(entity.getCaze().getUuid()))) {
			throw new UnsupportedOperationException("Contact creation is not allowed for diseases that don't have contact follow-up.");
		}
		
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

	@Override
	public List<ContactMapDto> getMapContacts(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		District district = districtService.getByReferenceDto(districtRef);

		if (user == null) {
			return Collections.emptyList();
		}

		return contactService.getMapContacts(fromDate, toDate, district, disease, user)
				.stream()
				.map(c -> toMapDto(c, visitService.getLastVisitByContact(c, VisitStatus.COOPERATIVE)))
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
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

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
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		
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

	public ContactMapDto toMapDto(Contact source, Visit lastVisit) {
		if (source == null) {
			return null;
		}

		ContactMapDto target = new ContactMapDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setContactClassification(source.getContactClassification());
		
		if (lastVisit != null) {
			target.setLastVisit(VisitFacadeEjb.toReferenceDto(lastVisit));
		}

		return target;
	}

	@RolesAllowed(UserRole._SYSTEM)
	public void generateContactFollowUpTasks() {

		// get all contacts that are followed up
		LocalDateTime fromDateTime = LocalDate.now().atStartOfDay();
		LocalDateTime toDateTime = fromDateTime.plusDays(1);
		List<Contact> contacts = contactService.getFollowUpBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime), null, null, null);

		for (Contact contact : contacts) {

			User assignee;
			// assign responsible user
			if (contact.getContactOfficer() != null) {
				// A. contact officer
				assignee = contact.getContactOfficer();
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
					assignee = users.get(0);
				} else {
					logger.warn("Contact has not contact officer and no region - can't create follow-up task: " + contact.getUuid());
					continue;
				}
			}

			// find already existing tasks
			TaskCriteria pendingUserTaskCriteria = new TaskCriteria()
					.contactEquals(contact)
					.taskTypeEquals(TaskType.CONTACT_FOLLOW_UP)
					.assigneeUserEquals(assignee)
					.taskStatusEquals(TaskStatus.PENDING);
			List<Task> pendingUserTasks = taskService.findBy(pendingUserTaskCriteria);

			if (!pendingUserTasks.isEmpty()) {
				// the user still has a pending task for this contact
				continue;
			}

			TaskCriteria dayTaskCriteria = new TaskCriteria()
					.contactEquals(contact)
					.taskTypeEquals(TaskType.CONTACT_FOLLOW_UP)
					.dueDateBetween(DateHelper8.toDate(fromDateTime), DateHelper8.toDate(toDateTime));
			List<Task> dayTasks = taskService.findBy(dayTaskCriteria);

			if (!dayTasks.isEmpty()) {
				// there is already a task for the exact day
				continue;
			}

			// none found -> create the task
			Task task = taskService.buildTask(null);
			task.setTaskContext(TaskContext.CONTACT);
			task.setContact(contact);
			task.setTaskType(TaskType.CONTACT_FOLLOW_UP);
			task.setSuggestedStart(DateHelper8.toDate(fromDateTime));
			task.setDueDate(DateHelper8.toDate(toDateTime.minusMinutes(1)));
			task.setAssigneeUser(assignee);
			taskService.ensurePersisted(task);
		}
	}


	@LocalBean
	@Stateless
	public static class ContactFacadeEjbLocal extends ContactFacadeEjb {
	}
}
