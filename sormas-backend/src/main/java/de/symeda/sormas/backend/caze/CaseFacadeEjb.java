package de.symeda.sormas.backend.caze;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.PlagueType;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.caze.MapCaseDto;
import de.symeda.sormas.api.caze.StatisticsCaseDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.symptoms.SymptomsHelper;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskCriteria;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.common.MessageType;
import de.symeda.sormas.backend.common.MessagingService;
import de.symeda.sormas.backend.common.NotificationDeliveryFailedException;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.facility.Facility;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.hospitalization.PreviousHospitalizationService;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.Community;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.District;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.ModelConstants;

@Stateless(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {

	@PersistenceContext(unitName = ModelConstants.PERSISTENCE_UNIT_NAME)
	protected EntityManager em;

	@EJB
	private CaseService caseService;
	@EJB
	private PersonService personService;
	@EJB
	private FacilityService facilityService;
	@EJB
	private UserService userService;
	@EJB
	private SymptomsFacadeEjbLocal symptomsFacade;
	@EJB
	private LocationFacadeEjbLocal locationFacade;
	@EJB
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;
	@EJB
	private TaskService taskService;
	@EJB
	private HospitalizationService hospitalizationService;
	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private HospitalizationFacadeEjbLocal hospitalizationFacade;
	@EJB
	private PreviousHospitalizationService previousHospitalizationService;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private MessagingService messagingService;
	@EJB
	private EventParticipantService eventParticipantService;
	

	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);

	@Override
	public List<CaseDataDto> getAllCasesAfter(Date date, String userUuid) {

		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllAfter(date, user).stream().map(c -> toDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getByUuids(List<String> uuids) {
		return caseService.getByUuids(uuids)
				.stream()
				.map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<CaseIndexDto> getIndexList(String userUuid, CaseCriteria caseCriteria) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CaseIndexDto> cq = cb.createQuery(CaseIndexDto.class);
		Root<Case> caze = cq.from(Case.class);
		Join<Case, Person> person = caze.join(Case.PERSON, JoinType.LEFT);
		Join<Case, Region> region = caze.join(Case.REGION, JoinType.LEFT);
		Join<Case, District> district = caze.join(Case.DISTRICT, JoinType.LEFT);
		Join<Case, Facility> facility = caze.join(Case.HEALTH_FACILITY, JoinType.LEFT);
		Join<Case, User> surveillanceOfficer = caze.join(Case.SURVEILLANCE_OFFICER, JoinType.LEFT);

		cq.multiselect(caze.get(Case.UUID), 
				caze.get(Case.EPID_NUMBER), person.get(Person.FIRST_NAME), person.get(Person.LAST_NAME),
				caze.get(Case.DISEASE), caze.get(Case.DISEASE_DETAILS), caze.get(Case.CASE_CLASSIFICATION),
				caze.get(Case.INVESTIGATION_STATUS), person.get(Person.PRESENT_CONDITION),
				caze.get(Case.REPORT_DATE), region.get(Region.UUID), district.get(District.UUID), district.get(District.NAME), 
				facility.get(Facility.UUID), surveillanceOfficer.get(User.UUID), caze.get(Case.OUTCOME));

		User user = userService.getByUuid(userUuid);		
		Predicate filter = caseService.createUserFilter(cb, cq, caze, user);

		if (caseCriteria != null) {
			Predicate criteriaFilter = caseService.buildCriteriaFilter(caseCriteria, cb, caze);
			filter = AbstractAdoService.and(cb, filter, criteriaFilter);
		}

		if (filter != null) {
			cq.where(filter);
		}

		List<CaseIndexDto> resultList = em.createQuery(cq).getResultList();
		return resultList;
	}

	@Override
	public List<String> getAllUuids(String userUuid) {

		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllUuids(user);
	}

	@Override
	public List<CaseReferenceDto> getSelectableCases(UserReferenceDto userRef) {

		User user = userService.getByReferenceDto(userRef);

		return caseService.getAllAfter(null, user).stream().map(c -> toReferenceDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<DashboardCaseDto> getNewCasesForDashboard(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getByUuid(userUuid);

		return caseService.getNewCasesForDashboard(district, disease, from, to, user);
	}

	@Override
	public List<MapCaseDto> getCasesForMap(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getByUuid(userUuid);

		return caseService.getCasesForMap(district, disease, from, to, user);
	}

	@Override
	public List<StatisticsCaseDto> getCasesForStatistics(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid) {
		Region region = regionService.getByReferenceDto(regionRef);
		District district = districtService.getByReferenceDto(districtRef);
		User user = userService.getByUuid(userUuid);

		return caseService.getCasesForStatistics(region, district, disease, from, to, user);
	}
	
	@Override
	public Map<CaseClassification, Long> getNewCaseCountPerClassification(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		return caseService.getNewCaseCountPerClassification(caseCriteria, user);
	}

	@Override
	public Map<PresentCondition, Long> getNewCaseCountPerPersonCondition(CaseCriteria caseCriteria, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		return caseService.getNewCaseCountPerPersonCondition(caseCriteria, user);
	}
	
	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		return toDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		CaseDataDto existingCase = toDto(caseService.getByUuid(dto.getUuid()));

		SymptomsHelper.updateIsSymptomatic(dto.getSymptoms());

		Case caze = fromDto(dto);
		caseService.ensurePersisted(caze);

		onCaseChanged(existingCase, caze);

		return toDto(caze);
	}

	/**
	 * Handles potential changes, processes and backend logic that needs to be done after a case has been created/saved
	 */
	private void onCaseChanged(CaseDataDto existingCase, Case newCase) {
		
		// If the case is new and the geo coordinates of the case's health facility are null, set its coordinates to the
		// case's report coordinates, if available
		Facility facility = newCase.getHealthFacility();
		if (existingCase == null && facility != null && facility.getUuid() != FacilityDto.OTHER_FACILITY_UUID && facility.getUuid() != FacilityDto.NONE_FACILITY_UUID
				&& (facility.getLatitude() == null || facility.getLongitude() == null)) {
			if (newCase.getReportLat() != null && newCase.getReportLon() != null) {
				facility.setLatitude(newCase.getReportLat());
				facility.setLongitude(newCase.getReportLon());
				facilityService.ensurePersisted(facility);
			}
		}
		
		if (newCase.getDisease() == Disease.PLAGUE) {
			PlagueType plagueType = DiseaseHelper.getPlagueTypeForSymptoms(SymptomsFacadeEjb.toDto(newCase.getSymptoms()));
			if (plagueType != newCase.getPlagueType() && plagueType != null) {
				newCase.setPlagueType(plagueType);
			}
		}		

		updateInvestigationByStatus(newCase);
	
		// Send an email to all responsible supervisors when the case classification has changed
		if (existingCase != null && existingCase.getCaseClassification() != newCase.getCaseClassification()) {
			List<User> messageRecipients = userService.getAllByRegionAndUserRoles(newCase.getRegion(), 
					UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
			for (User recipient : messageRecipients) {
				try {
					messagingService.sendMessage(recipient, I18nProperties.getMessage(MessagingService.SUBJECT_CASE_CLASSIFICATION_CHANGED), 
							String.format(I18nProperties.getMessage(MessagingService.CONTENT_CASE_CLASSIFICATION_CHANGED), DataHelper.getShortUuid(newCase.getUuid()), newCase.getCaseClassification().toString()), 
							MessageType.EMAIL, MessageType.SMS);
				} catch (NotificationDeliveryFailedException e) {
					logger.error(String.format("NotificationDeliveryFailedException when trying to notify supervisors about the change of a case classification. "
							+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
				}
			}
		}
	
		if (existingCase == null 
				|| newCase.getDisease() != existingCase.getDisease()
				|| newCase.getReportDate() != existingCase.getReportDate()
				|| newCase.getReceptionDate() != existingCase.getReceptionDate()
				|| newCase.getSymptoms().getOnsetDate() != existingCase.getSymptoms().getOnsetDate()
				) {

			// Update follow-up until and status of all contacts
			for (Contact contact : contactService.getAllByCase(newCase)) {
				contactService.updateFollowUpUntilAndStatus(contact);
				contactService.udpateContactStatusAndResultingCase(contact);
			}
			for (Contact contact : contactService.getAllByResultingCase(newCase)) {
				contactService.updateFollowUpUntilAndStatus(contact);
				contactService.udpateContactStatusAndResultingCase(contact);
			}

			// update result case of all related event participants
			for (EventParticipant eventParticipant : eventParticipantService.getAllByPerson(newCase.getPerson())) {
				eventParticipantService.udpateResultingCase(eventParticipant);
			}
		}
		
		// Create a task to search for other cases for new Plague cases
		if (existingCase == null && newCase.getDisease() == Disease.PLAGUE) {
			createActiveSearchForOtherCasesTask(newCase);
		}
	}

	@Override
	public CaseDataDto moveCase(CaseReferenceDto cazeRef, CommunityReferenceDto communityDto, FacilityReferenceDto facilityDto, String facilityDetails,
			UserReferenceDto officerDto) {
		Case caze = fromDto(getCaseDataByUuid(cazeRef.getUuid()));

		Community community = communityDto != null ? communityService.getByUuid(communityDto.getUuid()) : null;
		Facility facility = facilityService.getByUuid(facilityDto.getUuid());
		District district = facility.getDistrict();
		Region region = district.getRegion();
		User officer = null;
		if (officerDto != null) {
			officer = userService.getByUuid(officerDto.getUuid());
		}

		// Create a new previous hospitalization object if a new facility is set and reset the
		// current hospitalization
		if (!caze.getHealthFacility().getUuid().equals(facility.getUuid())) {
			caze.getHospitalization().getPreviousHospitalizations().add(previousHospitalizationService.buildPreviousHospitalizationFromHospitalization(caze));
			caze.getHospitalization().setHospitalizedPreviously(YesNoUnknown.YES);
			caze.getHospitalization().setAdmissionDate(new Date());
			caze.getHospitalization().setDischargeDate(null);
			caze.getHospitalization().setIsolated(null);
		}

		caze.setRegion(region);
		caze.setDistrict(district);
		caze.setCommunity(community);
		caze.setHealthFacility(facility);
		caze.setHealthFacilityDetails(facilityDetails);
		caze.setSurveillanceOfficer(officer);

		caseService.ensurePersisted(caze);

		// Assign all tasks associated with this case to the new officer or, if none has been selected,
		// to the region supervisor
		for (Task task : caze.getTasks()) {
			if (task.getTaskStatus() != TaskStatus.PENDING) {
				continue;
			}

			if (officer != null) {
				task.setAssigneeUser(officer);
			} else {
				List<User> supervisors = userService.getAllByRegionAndUserRoles(region, UserRole.SURVEILLANCE_SUPERVISOR);
				if (supervisors.size() >= 1) {
					task.setAssigneeUser(supervisors.get(0));
				} else {
					task.setAssigneeUser(null);
				}
			}

			taskService.ensurePersisted(task);
		}

		return toDto(caze);
	}

	@Override
	public void deleteCase(CaseReferenceDto caseRef, String userUuid) {
		User user = userService.getByUuid(userUuid);
		if (!user.getUserRoles().contains(UserRole.ADMIN)) {
			throw new UnsupportedOperationException("Only admins are allowed to delete entities.");
		}

		Case caze = caseService.getByReferenceDto(caseRef);
		List<Contact> contacts = contactService.getAllByCase(caze);
		for (Contact contact : contacts) {
			contactService.delete(contact);
		}
		contacts = contactService.getAllByResultingCase(caze);
		for (Contact contact : contacts) {
			contact.setResultingCase(null);
		}
		List<Sample> samples = sampleService.getAllByCase(caze);
		for (Sample sample : samples) {
			sampleService.delete(sample);
		}
		List<Task> tasks = taskService.findBy(new TaskCriteria().cazeEquals(caseRef));
		for (Task task : tasks) {
			taskService.delete(task);
		}
		caseService.delete(caze);
	}

	public Case fromDto(@NotNull CaseDataDto source) {

		Case target = caseService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Case();
			target.setUuid(source.getUuid());
			target.setReportDate(new Date());
			// TODO set target.setReportingUser(user); from sesssion context
		}
		DtoHelper.validateDto(source, target);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPlagueType(source.getPlagueType());
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setReceptionDate(source.getReceptionDate());
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setCaseClassification(source.getCaseClassification());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setHospitalization(hospitalizationFacade.fromDto(source.getHospitalization()));
		target.setEpiData(epiDataFacade.fromDto(source.getEpiData()));

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(userService.getByReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setVaccinationDate(source.getVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setOutcome(source.getOutcome());
		target.setOutcomeDate(source.getOutcomeDate());

		return target;
	}

	public static CaseReferenceDto toReferenceDto(Case entity) {
		if (entity == null) {
			return null;
		}
		CaseReferenceDto dto = new CaseReferenceDto(entity.getUuid(), entity.toString());
		return dto;
	}

	public static CaseDataDto toDto(Case source) {
		if (source == null) {
			return null;
		}
		CaseDataDto target = new CaseDataDto();
		DtoHelper.fillDto(target, source);

		target.setDisease(source.getDisease());
		target.setDiseaseDetails(source.getDiseaseDetails());
		target.setPlagueType(source.getPlagueType());
		target.setCaseClassification(source.getCaseClassification());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setHospitalization(HospitalizationFacadeEjb.toDto(source.getHospitalization()));
		target.setEpiData(EpiDataFacadeEjb.toDto(source.getEpiData()));

		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		target.setHealthFacilityDetails(source.getHealthFacilityDetails());

		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setInvestigatedDate(source.getInvestigatedDate());
		target.setReceptionDate(source.getReceptionDate());

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(UserFacadeEjb.toReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());
		target.setSmallpoxVaccinationReceived(source.getSmallpoxVaccinationReceived());
		target.setVaccinationDate(source.getVaccinationDate());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		target.setOutcome(source.getOutcome());
		target.setOutcomeDate(source.getOutcomeDate());

		return target;
	}

	public void updateInvestigationByStatus(Case caze) {
		CaseReferenceDto caseRef = caze.toReference();
		InvestigationStatus investigationStatus = caze.getInvestigationStatus();

		if (investigationStatus != InvestigationStatus.PENDING) {
			// Set the investigation date
			if (caze.getInvestigatedDate() == null) {
				caze.setInvestigatedDate(new Date());
			}

			// Set the task status of all investigation tasks to "Removed" because
			// the case status has been updated manually
			List<Task> pendingTasks = taskService.findBy(new TaskCriteria()
					.taskTypeEquals(TaskType.CASE_INVESTIGATION)
					.cazeEquals(caseRef)
					.taskStatusEquals(TaskStatus.PENDING));
			for (Task task : pendingTasks) {
				task.setTaskStatus(TaskStatus.REMOVED);
				task.setStatusChangeDate(new Date());
			}
			
			sendInvestigationDoneNotifications(caze);
		} else {
			// Remove the investigation date
			caze.setInvestigatedDate(null);

			// Create a new investigation task if none is present
			long pendingCount = taskService.getCount(new TaskCriteria()
					.taskTypeEquals(TaskType.CASE_INVESTIGATION)
					.cazeEquals(caseRef)
					.taskStatusEquals(TaskStatus.PENDING));

			if (pendingCount == 0) {
				createInvestigationTask(caze);
			}
		}	
	}

	public void updateInvestigationByTask(Case caze) {
		CaseReferenceDto caseRef = caze.toReference();

		// any pending case investigation task?
		long pendingCount = taskService.getCount(new TaskCriteria()
				.taskTypeEquals(TaskType.CASE_INVESTIGATION)
				.cazeEquals(caseRef)
				.taskStatusEquals(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {
			// get "case investigation" task created last
			List<Task> cazeTasks = taskService.findBy(new TaskCriteria()
					.taskTypeEquals(TaskType.CASE_INVESTIGATION)
					.cazeEquals(caseRef));

			Task youngestTask = cazeTasks.stream().max(new Comparator<Task>() {
				@Override
				public int compare(Task o1, Task o2) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());
				}
			}).get();

			switch (youngestTask.getTaskStatus()) {
			case PENDING:
				throw new UnsupportedOperationException("there should not be any pending tasks");
			case DONE:
				caze.setInvestigationStatus(InvestigationStatus.DONE);
				caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
				sendInvestigationDoneNotifications(caze);
				break;
			case REMOVED:
				caze.setInvestigationStatus(InvestigationStatus.DISCARDED);
				caze.setInvestigatedDate(youngestTask.getStatusChangeDate());
				break;
			case NOT_EXECUTABLE:
				caze.setInvestigationStatus(InvestigationStatus.PENDING);
				caze.setInvestigatedDate(null);
				break;
			default:
				break;
			}
		}
	}

	private void createInvestigationTask(Case caze) {
		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.CASE_INVESTIGATION);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void createActiveSearchForOtherCasesTask(Case caze) {
		Task task = new Task();
		task.setTaskStatus(TaskStatus.PENDING);
		task.setTaskContext(TaskContext.CASE);
		task.setCaze(caze);
		task.setTaskType(TaskType.ACTIVE_SEARCH_FOR_OTHER_CASES);
		task.setSuggestedStart(TaskHelper.getDefaultSuggestedStart());
		task.setDueDate(TaskHelper.getDefaultDueDate());
		task.setPriority(TaskPriority.NORMAL);

		assignOfficerOrSupervisorToTask(caze, task);

		taskService.ensurePersisted(task);
	}

	private void assignOfficerOrSupervisorToTask(Case caze, Task task) {
		if (caze.getSurveillanceOfficer() != null) {
			task.setAssigneeUser(caze.getSurveillanceOfficer());
		} else {
			// assign the first supervisor
			List<User> supervisors = userService.getAllByRegionAndUserRoles(caze.getRegion(),
					UserRole.SURVEILLANCE_SUPERVISOR);
			if (!supervisors.isEmpty()) {
				task.setAssigneeUser(supervisors.get(0));
			} else {
				List<User> nationalUsers = userService.getAllByRegionAndUserRoles(null, UserRole.NATIONAL_USER);
				if (!nationalUsers.isEmpty()) {
					task.setAssigneeUser(nationalUsers.get(0));
				} else {
					throw new UnsupportedOperationException("no national user and surveillance supervisor missing for: " + caze.getRegion());
				}
			}
		}
	}

	@Override
	public Map<RegionDto, Long> getCaseCountPerRegion(Date fromDate, Date toDate, Disease disease) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = null;		
		if (fromDate != null || toDate != null) {
			filter = caseService.createActiveCaseFilter(cb, from, fromDate, toDate);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			filter = filter != null ? cb.and(filter, diseaseFilter) : diseaseFilter;
		}	

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(from.get(Case.REGION));
		cq.multiselect(from.get(Case.REGION), cb.count(from));
		List<Object[]> results = em.createQuery(cq).getResultList();

		Map<RegionDto, Long> resultMap = results.stream().collect(
				Collectors.toMap(e -> RegionFacadeEjb.toDto((Region)e[0]), e -> (Long)e[1]));
		return resultMap;
	}



	@Override
	public List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date fromDate, Date toDate, Disease disease, CaseMeasure caseMeasure) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<Case> from = cq.from(Case.class);

		Predicate filter = null;		
		if (fromDate != null || toDate != null) {			
			filter = caseService.createActiveCaseFilter(cb, from, fromDate, toDate);
		}

		if (disease != null) {
			Predicate diseaseFilter = cb.equal(from.get(Case.DISEASE), disease);
			filter = filter != null ? cb.and(filter, diseaseFilter) : diseaseFilter;
		}		

		if (filter != null) {
			cq.where(filter);
		}

		cq.groupBy(from.get(Case.DISTRICT));
		cq.multiselect(from.get(Case.DISTRICT), cb.count(from));
		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			cq.orderBy(cb.asc(cb.count(from)));
		}
		List<Object[]> results = em.createQuery(cq).getResultList();

		if (caseMeasure == CaseMeasure.CASE_COUNT) {
			List<Pair<DistrictDto, BigDecimal>> resultList = results.stream()
					.map(e -> new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto((District)e[0]), new BigDecimal((Long)e[1])))
					.collect(Collectors.toList());
			return resultList;
		} else {
			List<Pair<DistrictDto, BigDecimal>> resultList = results.stream()
					.map(e -> {
						District district = (District) e[0];
						Integer population = district.getPopulation();
						Long caseCount = (Long) e[1];

						if (population == null || population <= 0) {
							// No or negative population - these entries will be cut off in the UI
							return new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto(district), new BigDecimal(0));
						} else {
							return new Pair<DistrictDto, BigDecimal>(DistrictFacadeEjb.toDto(district), 
									new BigDecimal(caseCount).divide(
											new BigDecimal((double) population / DistrictDto.CASE_INCIDENCE_DIVISOR), 1, RoundingMode.HALF_UP));
						}
					})
					.sorted(new Comparator<Pair<DistrictDto, BigDecimal>>() {
						@Override
						public int compare(Pair<DistrictDto, BigDecimal> o1, Pair<DistrictDto, BigDecimal> o2) {
							return o1.getElement1().compareTo(o2.getElement1());
						}
					})
					.collect(Collectors.toList());
			return resultList;
		}
	}
	
	private void sendInvestigationDoneNotifications(Case caze) {
		List<User> messageRecipients = userService.getAllByRegionAndUserRoles(caze.getRegion(), 
				UserRole.SURVEILLANCE_SUPERVISOR, UserRole.CASE_SUPERVISOR, UserRole.CONTACT_SUPERVISOR);
		for (User recipient : messageRecipients) {
			try {
				messagingService.sendMessage(recipient, I18nProperties.getMessage(MessagingService.SUBJECT_CASE_INVESTIGATION_DONE), 
						String.format(I18nProperties.getMessage(MessagingService.CONTENT_CASE_INVESTIGATION_DONE), DataHelper.getShortUuid(caze.getUuid())), 
						MessageType.EMAIL, MessageType.SMS);
			} catch (NotificationDeliveryFailedException e) {
				logger.error(String.format("NotificationDeliveryFailedException when trying to notify supervisors about the completion of a case investigation. "
						+ "Failed to send " + e.getMessageType() + " to user with UUID %s.", recipient.getUuid()));
			}
		}
	}
	
	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {
	}
}
