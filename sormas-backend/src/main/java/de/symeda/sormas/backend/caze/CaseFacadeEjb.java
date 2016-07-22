package de.symeda.sormas.backend.caze;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
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
	
	
	@Override
	public List<CaseDataDto> getAllCases() {
		return caseService.getAll().stream()
			.map(c -> toCaseDataDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public List<CaseDataDto> getAllCasesAfter(Date date) {
		return caseService.getAllAfter(date).stream()
			.map(c -> toCaseDataDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		return toCaseDataDto(caseService.getByUuid(uuid));
	}
	
	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		Case caze = fromCaseDataDto(dto);
		caseService.ensurePersisted(caze);
		
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
	public CaseDataDto changeCaseStatus(String uuid, CaseStatus targetStatus) {
		
		Case caze = caseService.getByUuid(uuid);
		if (caze == null) {
			throw new EJBException("Case not found: " + uuid);
		}
		CaseStatus currentStatus = caze.getCaseStatus();
		
		if (targetStatus.equals(currentStatus)) {
			throw new EJBException("Case status equals existing: " + targetStatus);
		}
		
		boolean targetStatusFound = false;
		for (CaseStatus status : CaseHelper.getPossibleStatusChanges(currentStatus, UserRole.SURVEILLANCE_SUPERVISOR)) {
			if (status.equals(targetStatus)) {
				targetStatusFound = true;
				break;
			}
		}
		if (!targetStatusFound) {
			throw new EJBException("Case status change not allowed from '" + currentStatus + "' to '" + targetStatus + "'");
		}
		
		caze.setCaseStatus(targetStatus);
		
		switch (targetStatus) {
		case INVESTIGATED:
			caze.setInvestigatedDate(new Date());
			break;
		case CONFIRMED:
			caze.setConfirmedDate(new Date());
			break;
		case NO_CASE:
			caze.setNoCaseDate(new Date());
			break;
		case RECOVERED:
			caze.setRecoveredDate(new Date());
			break;
		case SUSPECT:
			caze.setSuspectDate(new Date());
			break;
			// TODO others...
			// TODO what about going back and forth?
		default:
			break;
		}
		
		return toCaseDataDto(caze);
	}
	
	public Case fromCaseDataDto(@NotNull CaseDataDto dto) {
		
		Case caze;
		if (dto.getChangeDate() == null) {
			caze = new Case();
		} else {
			caze = caseService.getByUuid(dto.getUuid());
		}

		caze.setUuid(dto.getUuid());
		caze.setDisease(dto.getDisease());
		caze.setReportDate(dto.getReportDate());
		caze.setReportingUser(DtoHelper.fromReferenceDto(dto.getReportingUser(), userService));
		caze.setPerson(DtoHelper.fromReferenceDto(dto.getPerson(), personService));
		caze.setCaseStatus(dto.getCaseStatus());
		caze.setHealthFacility(DtoHelper.fromReferenceDto(dto.getHealthFacility(), facilityService));

		caze.setSurveillanceOfficer(DtoHelper.fromReferenceDto(dto.getSurveillanceOfficer(), userService));
		caze.setSurveillanceSupervisor(DtoHelper.fromReferenceDto(dto.getSurveillanceSupervisor(), userService));
		caze.setCaseOfficer(DtoHelper.fromReferenceDto(dto.getCaseOfficer(), userService));
		caze.setCaseSupervisor(DtoHelper.fromReferenceDto(dto.getCaseSupervisor(), userService));
		caze.setContactOfficer(DtoHelper.fromReferenceDto(dto.getContactOfficer(), userService));
		caze.setContactSupervisor(DtoHelper.fromReferenceDto(dto.getContactSupervisor(), userService));

		return caze;
	}
	
	public static CaseDataDto toCaseDataDto(Case caze) {
		if (caze == null) {
			return null;
		}
		CaseDataDto dto = new CaseDataDto();
		dto.setCreationDate(caze.getChangeDate());
		dto.setChangeDate(caze.getChangeDate());
		dto.setUuid(caze.getUuid());
		dto.setDisease(caze.getDisease());
		dto.setCaseStatus(caze.getCaseStatus());
		dto.setPerson(DtoHelper.toReferenceDto(caze.getPerson()));
		dto.setHealthFacility(DtoHelper.toReferenceDto(caze.getHealthFacility()));
		
		dto.setReportingUser(DtoHelper.toReferenceDto(caze.getReportingUser()));
		dto.setReportDate(caze.getReportDate());
		dto.setInvestigatedDate(caze.getInvestigatedDate());

		dto.setSurveillanceOfficer(DtoHelper.toReferenceDto(caze.getSurveillanceOfficer()));
		dto.setSurveillanceSupervisor(DtoHelper.toReferenceDto(caze.getSurveillanceSupervisor()));
		dto.setCaseOfficer(DtoHelper.toReferenceDto(caze.getCaseOfficer()));
		dto.setCaseSupervisor(DtoHelper.toReferenceDto(caze.getCaseSupervisor()));
		dto.setContactOfficer(DtoHelper.toReferenceDto(caze.getContactOfficer()));
		dto.setContactSupervisor(DtoHelper.toReferenceDto(caze.getContactSupervisor()));
		
		return dto;
	}
	
}
