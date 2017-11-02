package de.symeda.sormas.backend.caze;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
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
import de.symeda.sormas.backend.report.WeeklyReport;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskCriteria;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.DateHelper8;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {

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
	private HospitalizationFacadeEjbLocal hospitalizationFacade;
	@EJB
	private PreviousHospitalizationService previousHospitalizationService;
	@EJB
	private EpiDataFacadeEjbLocal epiDataFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;

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
	public List<String> getAllUuids(String userUuid) {

		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllUuids(user);
	}

	@Override
	public List<CaseDataDto> getAllCasesByDisease(Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllByDisease( disease, user).stream().map(c -> toDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getAllCasesBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		District district = districtService.getByReferenceDto(districtRef);

		if (user == null) {
			return Collections.emptyList();
		}

		return caseService.getAllBetween(fromDate, toDate, district, disease, user).stream().map(c -> toDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CaseReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid) {

		User user = userService.getByUuid(userUuid);

		return caseService.getAllAfter(date, user).stream().map(c -> toReferenceDto(c)).collect(Collectors.toList());
	}

	@Override
	public List<CaseReferenceDto> getSelectableCases(UserReferenceDto userRef) {

		User user = userService.getByReferenceDto(userRef);

		return caseService.getAllAfter(null, user).stream().map(c -> toReferenceDto(c)).collect(Collectors.toList());
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
		Case currentCaze = caseService.getByUuid(dto.getUuid());
		Disease currentDisease = null;
		if (currentCaze != null) {
			currentDisease = currentCaze.getDisease();
		}
		
		// If the case is new and the geo coordinates of the case's health facility are null, set its coordinates to the
		// case's report coordinates, if available
		FacilityReferenceDto facilityRef = dto.getHealthFacility();
		Facility facility = facilityService.getByReferenceDto(facilityRef);
		if (currentCaze == null && facility != null && facility.getUuid() != FacilityDto.OTHER_FACILITY_UUID && facility.getUuid() != FacilityDto.NONE_FACILITY_UUID
				&& (facility.getLatitude() == null || facility.getLongitude() == null)) {
			if (dto.getReportLat() != null && dto.getReportLon() != null) {
				facility.setLatitude(dto.getReportLat());
				facility.setLongitude(dto.getReportLon());
				facilityService.ensurePersisted(facility);
			}
		}
				
		Case caze = fromDto(dto);

		caseService.ensurePersisted(caze);
		updateCaseInvestigationProcess(caze);

		// Update follow-up until and status of all contacts of this case if the
		// disease has changed
		if (currentDisease != null && caze.getDisease() != currentDisease) {
			for (ContactDto contact : contactFacade.getAllByCase(getReferenceByUuid(caze.getUuid()))) {
				contactFacade.updateFollowUpUntilAndStatus(contact);
			}
		}
		
		// Create a task to search for other cases for new Plague cases
		if (currentCaze == null && dto.getDisease() == Disease.PLAGUE) {
			createActiveSearchForOtherCasesTask(caze);
		}

		return toDto(caze);
	}

	@Override
	public CaseDataDto getByPersonAndDisease(String personUuid, Disease disease, String userUuid) {
		Person person = personService.getByUuid(personUuid);
		User user = userService.getByUuid(userUuid);

		return toDto(caseService.getByPersonAndDisease(disease, person, user));
	}

	@Override
	public CaseDataDto moveCase(CaseReferenceDto cazeRef, CommunityReferenceDto communityDto, FacilityReferenceDto facilityDto, String facilityDetails,
			UserReferenceDto officerDto) {
		Case caze = fromDto(getCaseDataByUuid(cazeRef.getUuid()));

		Community community = communityService.getByUuid(communityDto.getUuid());
		District district = community.getDistrict();
		Region region = district.getRegion();
		Facility facility = facilityService.getByUuid(facilityDto.getUuid());
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
		target.setMeaslesVaccination(source.getMeaslesVaccination());
		target.setMeaslesDoses(source.getMeaslesDoses());
		target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
		target.setYellowFeverVaccination(source.getYellowFeverVaccination());
		target.setYellowFeverVaccinationInfoSource(source.getYellowFeverVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	public static CaseReferenceDto toReferenceDto(Case entity) {
		if (entity == null) {
			return null;
		}
		CaseReferenceDto dto = new CaseReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}

	public static CaseDataDto toDto(Case source) {
		if (source == null) {
			return null;
		}
		CaseDataDto target = new CaseDataDto();
		DtoHelper.fillReferenceDto(target, source);

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

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(UserFacadeEjb.toReferenceDto(source.getCaseOfficer()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));

		target.setPregnant(source.getPregnant());
		target.setMeaslesVaccination(source.getMeaslesVaccination());
		target.setMeaslesDoses(source.getMeaslesDoses());
		target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
		target.setYellowFeverVaccination(source.getYellowFeverVaccination());
		target.setYellowFeverVaccinationInfoSource(source.getYellowFeverVaccinationInfoSource());
		target.setSmallpoxVaccinationScar(source.getSmallpoxVaccinationScar());

		target.setEpidNumber(source.getEpidNumber());

		target.setReportLat(source.getReportLat());
		target.setReportLon(source.getReportLon());
		target.setReportLatLonAccuracy(source.getReportLatLonAccuracy());

		return target;
	}

	/**
	 * Update case investigation status and/or create case investigation task
	 * Call this whenever the Case or one of it's "case investigation" Tasks is
	 * modified
	 */
	public void updateCaseInvestigationProcess(Case caze) {

		// any pending case investigation task?
		long pendingCount = taskService.getCount(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION)
				.cazeEquals(caze).taskStatusEquals(TaskStatus.PENDING));

		if (pendingCount > 0) {
			// set status to investigation pending
			caze.setInvestigationStatus(InvestigationStatus.PENDING);
			// .. and clear date
			caze.setInvestigatedDate(null);
		} else {

			// get "case investigation" task created last
			List<Task> cazeTasks = taskService
					.findBy(new TaskCriteria().taskTypeEquals(TaskType.CASE_INVESTIGATION).cazeEquals(caze));

			if (cazeTasks.isEmpty()) {
				// no tasks at all -> create
				createInvestigationTask(caze);
			} else {

				// otherwise only the last created task is relevant
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
				throw new UnsupportedOperationException("surveillance supervisor missing for: " + caze.getRegion());
			}
		}
	}
	
	@Override
	public Map<RegionReferenceDto, Long> getCaseCountPerRegion(Date onsetFromDate, Date onsetToDate, Disease disease) {
		return caseService.getCaseCountPerRegion(onsetFromDate, onsetToDate, disease);
	}
	
	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {
	}

}
