package de.symeda.sormas.backend.contact;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
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
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactExportDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.contact.MapContactDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.visit.VisitReferenceDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;
import de.symeda.sormas.backend.visit.Visit;
import de.symeda.sormas.backend.visit.VisitService;

@Stateless(name = "ContactFacade")
public class ContactFacadeEjb implements ContactFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;
	
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
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	
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
		
		if (!DiseaseHelper.hasContactFollowUp(entity.getCaze().getDisease(), entity.getCaze().getPlagueType())) {
			throw new UnsupportedOperationException("Contact creation is not allowed for diseases that don't have contact follow-up.");
		}
		
		contactService.ensurePersisted(entity);

		contactService.updateFollowUpUntilAndStatus(entity);
		
		contactService.udpateContactStatusAndResultingCase(entity);
		
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
	public List<MapContactDto> getContactsForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date fromDate, Date toDate, String userUuid, List<MapCaseDto> mapCaseDtos) {
		User user = userService.getByUuid(userUuid);
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		List<String> caseUuids = new ArrayList<>();
		for (MapCaseDto mapCaseDto : mapCaseDtos) {
			caseUuids.add(mapCaseDto.getUuid());
		}

		if (user == null) {
			return Collections.emptyList();
		}
		
		return contactService.getContactsForMap(region, district, disease, fromDate, toDate, user, caseUuids);
	}
	
	@Override
	public void deleteContact(ContactReferenceDto contactRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}
		
		Contact contact = contactService.getByReferenceDto(contactRef);
		List<Visit> visits = visitService.getAllByContact(contact);
		for (Visit visit : visits) {
			visitService.delete(visit);
		}
		List<Task> tasks = taskService.findBy(new TaskCriteria().contactEquals(contactRef));
		for (Task task : tasks) {
			taskService.delete(task);
		}
		contactService.delete(contact);
	}
	
	@Override
	public List<ContactExportDto> getExportList(String userUuid, ContactCriteria contactCriteria) {
		
		User user = userService.getByUuid(userUuid);

		return contactService.findBy(contactCriteria, user)
				.stream()
				.map(c -> toExportDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<ContactIndexDto> getIndexList(String userUuid, ContactCriteria contactCriteria) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ContactIndexDto> cq = cb.createQuery(ContactIndexDto.class);
		Root<Contact> contact = cq.from(Contact.class);
		Join<Contact, Person> contactPerson = contact.join(Contact.PERSON, JoinType.LEFT);
		Join<Contact, Case> contactCase = contact.join(Contact.CAZE, JoinType.LEFT);
		Join<Case, Person> contactCasePerson = contactCase.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> contactCaseRegion = contactCase.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> contactCaseDistrict = contactCase.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Facility> contactCaseFacility = contactCase.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Contact, User> contactOfficer = contact.join(Contact.CONTACT_OFFICER, JoinType.LEFT);
		
		cq.multiselect(contact.get(Contact.UUID), contactPerson.get(Person.UUID), contactPerson.get(Person.FIRST_NAME), contactPerson.get(Person.LAST_NAME),
				contactCase.get(Case.UUID), contactCase.get(Case.DISEASE), contactCase.get(Case.DISEASE_DETAILS), contactCasePerson.get(Person.UUID), contactCasePerson.get(Person.FIRST_NAME),
				contactCasePerson.get(Person.LAST_NAME), contactCaseRegion.get(Region.UUID), contactCaseDistrict.get(District.UUID),
				contactCaseFacility.get(Facility.UUID), contact.get(Contact.LAST_CONTACT_DATE), contact.get(Contact.CONTACT_PROXIMITY),
				contact.get(Contact.CONTACT_CLASSIFICATION), contact.get(Contact.CONTACT_STATUS), contact.get(Contact.FOLLOW_UP_STATUS), contact.get(Contact.FOLLOW_UP_UNTIL),
				contactOfficer.get(User.UUID));
		
		Predicate filter = null;
		if (userUuid != null 
				&& (contactCriteria == null || contactCriteria.getCaze() == null)) {
			User user = userService.getByUuid(userUuid);
			filter = contactService.createUserFilter(cb, cq, contact, user);
		}
		
		if (contactCriteria != null) {
			Predicate criteriaFilter = contactService.buildCriteriaFilter(contactCriteria, cb, contact);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}
		cq.orderBy(cb.desc(contact.get(Contact.REPORT_DATE_TIME)));
		
		List<ContactIndexDto> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}
	
	@Override
	public List<ContactReferenceDto> getAllByVisit(VisitReferenceDto visitRef) {
		Visit visit = visitService.getByReferenceDto(visitRef);
		return contactService.getAllByVisit(visit).stream()
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
		target.setLastContactDate(source.getLastContactDate() != null ? DateHelper8.toDate(DateHelper8.toLocalDate(source.getLastContactDate())) : null);
		if (target.getLastContactDate() != null && target.getLastContactDate().after(target.getReportDateTime())) {
			throw new ValidationException(Contact.LAST_CONTACT_DATE + " has to be before " + Contact.REPORT_DATE_TIME);
		}

		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setContactStatus(source.getContactStatus());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());
		
		// resulting case is not set from DTO @see ContactService#udpateContactStatusAndResultingCase
		//target.setResultingCase(caseService.getByReferenceDto(source.getResultingCase()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static ContactReferenceDto toReferenceDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactReferenceDto target = new ContactReferenceDto(source.getUuid(), source.toString());
		return target;
	}	

	public static ContactDto toDto(Contact source) {
		if (source == null) {
			return null;
		}
		ContactDto target = new ContactDto();
		DtoHelper.fillDto(target, source);

		target.setCaze(CaseFacadeEjb.toReferenceDto(source.getCaze()));
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDateTime(source.getReportDateTime());

		target.setLastContactDate(source.getLastContactDate());
		target.setContactProximity(source.getContactProximity());
		target.setContactClassification(source.getContactClassification());
		target.setContactStatus(source.getContactStatus());
		target.setFollowUpStatus(source.getFollowUpStatus());
		target.setFollowUpComment(source.getFollowUpComment());
		target.setFollowUpUntil(source.getFollowUpUntil());
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));
		target.setDescription(source.getDescription());
		target.setRelationToCase(source.getRelationToCase());
		target.setResultingCase(CaseFacadeEjb.toReferenceDto(source.getResultingCase()));

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());
		
		return target;
	}

	public ContactExportDto toExportDto(Contact source) {
		
		Person sourcePerson = source.getPerson();
		Case sourceCase = source.getCaze();
		
		ContactExportDto target = new ContactExportDto();

		target.setUuid(source.getUuid());
		target.setSourceCaseUuid(sourceCase.getUuid());
		target.setCaseClassification(sourceCase.getCaseClassification());
		target.setDisease(DiseaseHelper.toString(sourceCase.getDisease(), sourceCase.getDiseaseDetails()));
		target.setContactClassification(source.getContactClassification());
		target.setLastContactDate(source.getLastContactDate());
		target.setPerson(PersonDto.buildCaption(sourcePerson.getFirstName(), sourcePerson.getLastName()));
		target.setSex(sourcePerson.getSex());
		target.setApproximateAge(PersonHelper.buildAgeString(sourcePerson.getApproximateAge(), sourcePerson.getApproximateAgeType()));
		target.setReportDate(source.getReportDateTime());
		target.setContactProximity(source.getContactProximity());
		target.setContactStatus(source.getContactStatus());
		target.setPresentCondition(sourcePerson.getPresentCondition());
		target.setDeathDate(sourcePerson.getDeathDate());
		target.setAddress(sourcePerson.getAddress().toString());
		target.setPhone(PersonHelper.buildPhoneString(sourcePerson.getPhone(), sourcePerson.getPhoneOwner()));
		target.setOccupationType(PersonHelper.buildOccupationString(sourcePerson.getOccupationType(), sourcePerson.getOccupationDetails(), 
				sourcePerson.getOccupationFacility() != null ? sourcePerson.getOccupationFacility().getName() : null));
		
		target.setNumberOfVisits(visitService.getVisitCount(source, null));
		Visit lastSourceVisit = visitService.getLastVisitByContact(source, VisitStatus.COOPERATIVE);
		if (lastSourceVisit != null) {
			target.setSymptomatic(YesNoUnknown.valueOf(lastSourceVisit.getSymptoms().getSymptomatic()));
			target.setLastVisitDate(lastSourceVisit.getVisitDateTime());
			target.setLastSymptoms(lastSourceVisit.getSymptoms().toHumanString(true));
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
					.contactEquals(contact.toReference())
					.taskTypeEquals(TaskType.CONTACT_FOLLOW_UP)
					.assigneeUserEquals(assignee.toReference())
					.taskStatusEquals(TaskStatus.PENDING);
			List<Task> pendingUserTasks = taskService.findBy(pendingUserTaskCriteria);

			if (!pendingUserTasks.isEmpty()) {
				// the user still has a pending task for this contact
				continue;
			}

			TaskCriteria dayTaskCriteria = new TaskCriteria()
					.contactEquals(contact.toReference())
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
