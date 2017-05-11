package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskHelper;
import de.symeda.sormas.api.task.TaskPriority;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb;
import de.symeda.sormas.backend.epidata.EpiDataFacadeEjb.EpiDataFacadeEjbLocal;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb;
import de.symeda.sormas.backend.hospitalization.HospitalizationFacadeEjb.HospitalizationFacadeEjbLocal;
import de.symeda.sormas.backend.hospitalization.HospitalizationService;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb.LocationFacadeEjbLocal;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityFacadeEjb;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictFacadeEjb;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.task.Task;
import de.symeda.sormas.backend.task.TaskCriteria;
import de.symeda.sormas.backend.task.TaskService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb;
import de.symeda.sormas.backend.user.UserService;
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
	private EpiDataFacadeEjbLocal epiDataFacade;
	
	@Override
	public List<CaseDataDto> getAllCasesAfter(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return caseService.getAllAfter(date, user).stream()
			.map(c -> toCaseDataDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<CaseDataDto> getAllCasesByDiseaseAfter(Date date, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return caseService.getAllByDiseaseAfter(date, disease, user).stream()
				.map(c -> toCaseDataDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getAllCasesBetween(Date fromDate, Date toDate, Disease disease, String userUuid) {
		User user = userService.getByUuid(userUuid);
		
		if (user == null) {
			return Collections.emptyList();
		}
		
		return caseService.getAllBetween(fromDate, toDate, disease, user).stream()
				.map(c -> toCaseDataDto(c))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<CaseReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		return caseService.getAllAfter(date, user).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}
	
	@Override
	public List<CaseReferenceDto> getSelectableCases(UserReferenceDto userRef) {
		
		User user = userService.getByReferenceDto(userRef);

		return caseService.getAllAfter(null, user).stream()
			.map(c -> toReferenceDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		return toCaseDataDto(caseService.getByUuid(uuid));
	}
	
	@Override
	public CaseReferenceDto getReferenceByUuid(String uuid) {
		return toReferenceDto(caseService.getByUuid(uuid));
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		
		Case caze = fromCaseDataDto(dto);
		caseService.ensurePersisted(caze);
		
		updateCaseInvestigationProcess(caze);
		
		return toCaseDataDto(caze);
	}
	
	@Override
	public CaseDataDto createCase(String personUuid, CaseDataDto caseDto) {
		Person person = personService.getByUuid(personUuid);
		
		Case caze = fromCaseDataDto(caseDto);
		caze.setPerson(person);
		caseService.ensurePersisted(caze);
		
		return toCaseDataDto(caze);
	}
	
	@Override
	public CaseDataDto getByPersonAndDisease(String personUuid, Disease disease, String userUuid) {
		Person person = personService.getByUuid(personUuid);
		User user = userService.getByUuid(userUuid);
		
		return toCaseDataDto(caseService.getByPersonAndDisease(disease, person, user));
	}
	
	public Case fromCaseDataDto(@NotNull CaseDataDto source) {
		
		Case target = caseService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Case();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
		DtoHelper.validateDto(source, target);
				
		target.setDisease(source.getDisease());
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

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(userService.getByReferenceDto(source.getCaseOfficer()));
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));

		target.setIllLocation(locationFacade.fromDto(source.getIllLocation()));
		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));
		
		target.setPregnant(source.getPregnant());
		target.setMeaslesVaccination(source.getMeaslesVaccination());
		target.setMeaslesDoses(source.getMeaslesDoses());
		target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
		
		target.setEpidNumber(source.getEpidNumber());

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

	public static CaseDataDto toCaseDataDto(Case source) {
		if (source == null) {
			return null;
		}
		CaseDataDto target = new CaseDataDto();
		DtoHelper.fillReferenceDto(target, source);

		target.setDisease(source.getDisease());
		target.setCaseClassification(source.getCaseClassification());
		target.setInvestigationStatus(source.getInvestigationStatus());
		target.setPerson(PersonFacadeEjb.toReferenceDto(source.getPerson()));
		target.setHospitalization(HospitalizationFacadeEjb.toDto(source.getHospitalization()));
		target.setEpiData(EpiDataFacadeEjb.toDto(source.getEpiData()));
		
		target.setRegion(RegionFacadeEjb.toReferenceDto(source.getRegion()));
		target.setDistrict(DistrictFacadeEjb.toReferenceDto(source.getDistrict()));
		target.setCommunity(CommunityFacadeEjb.toReferenceDto(source.getCommunity()));
		target.setHealthFacility(FacilityFacadeEjb.toReferenceDto(source.getHealthFacility()));
		
		target.setReportingUser(UserFacadeEjb.toReferenceDto(source.getReportingUser()));
		target.setReportDate(source.getReportDate());
		target.setInvestigatedDate(source.getInvestigatedDate());

		target.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(UserFacadeEjb.toReferenceDto(source.getCaseOfficer()));
		target.setContactOfficer(UserFacadeEjb.toReferenceDto(source.getContactOfficer()));

		target.setIllLocation(LocationFacadeEjb.toDto(source.getIllLocation()));
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		
		target.setPregnant(source.getPregnant());
		target.setMeaslesVaccination(source.getMeaslesVaccination());
		target.setMeaslesDoses(source.getMeaslesDoses());
		target.setMeaslesVaccinationInfoSource(source.getMeaslesVaccinationInfoSource());
		
		target.setEpidNumber(source.getEpidNumber());
		
		return target;
	}
	
	/**
	 * Update case investigation status and/or create case investigation task
	 * Call this whenever the Case or one of it's "case investigation" Tasks is modified 
	 */
	public void updateCaseInvestigationProcess(Case caze) {
		
		// any pending case investigation task?
		long pendingCount = taskService.getCount(new TaskCriteria()
				.taskTypeEquals(TaskType.CASE_INVESTIGATION)
				.cazeEquals(caze)
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
					.cazeEquals(caze));
			
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
		
		// assign officer or supervisor
		if (caze.getSurveillanceOfficer() != null) {
			task.setAssigneeUser(caze.getSurveillanceOfficer());
		} else {
			// assign the first supervisor
			List<User> supervisors = userService.getAllByRegionAndUserRoles(caze.getRegion(), UserRole.SURVEILLANCE_SUPERVISOR);
			if (!supervisors.isEmpty()) {
				task.setAssigneeUser(supervisors.get(0));
			} else {
				throw new UnsupportedOperationException("surveillance supervisor missing for: " + caze.getRegion()); 
			}
		}
		
		taskService.ensurePersisted(task);
	}
	
	@LocalBean
	@Stateless
	public static class CaseFacadeEjbLocal extends CaseFacadeEjb {
	}
	
}
