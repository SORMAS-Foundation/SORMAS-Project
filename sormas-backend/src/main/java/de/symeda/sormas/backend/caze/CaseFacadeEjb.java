package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;
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
	private UserFacadeEjbLocal userFacade;

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
	public List<ReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		return caseService.getAllAfter(date, user).stream()
			.map(c -> DtoHelper.toReferenceDto(c))
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
		
		Case caze = caseService.getByUuid(dto.getUuid());
		if (caze == null) {
			caze = new Case();
			caze.setUuid(dto.getUuid());
			if (dto.getCreationDate() != null) {
				caze.setCreationDate(new Timestamp(dto.getCreationDate().getTime()));
			}
		}
				
		caze.setDisease(dto.getDisease());
		caze.setReportDate(dto.getReportDate());
		caze.setReportingUser(userService.getByReferenceDto(dto.getReportingUser()));
		caze.setPerson(personService.getByReferenceDto(dto.getPerson()));
		caze.setCaseStatus(dto.getCaseStatus());
		caze.setHealthFacility(facilityService.getByReferenceDto(dto.getHealthFacility()));

		caze.setSurveillanceOfficer(userService.getByReferenceDto(dto.getSurveillanceOfficer()));
		caze.setSurveillanceSupervisor(userService.getByReferenceDto(dto.getSurveillanceSupervisor()));
		caze.setCaseOfficer(userService.getByReferenceDto(dto.getCaseOfficer()));
		caze.setCaseSupervisor(userService.getByReferenceDto(dto.getCaseSupervisor()));
		caze.setContactOfficer(userService.getByReferenceDto(dto.getContactOfficer()));
		caze.setContactSupervisor(userService.getByReferenceDto(dto.getContactSupervisor()));

		// TODO: split into multiple view dependant dtos?
		caze.setSymptoms(symptomsFacade.fromDto(dto.getSymptoms()));

		return caze;
	}
	
	public CaseDataDto toCaseDataDto(Case caze) {
		if (caze == null) {
			return null;
		}
		CaseDataDto dto = new CaseDataDto();
		dto.setCreationDate(caze.getCreationDate());
		dto.setChangeDate(caze.getChangeDate());
		dto.setUuid(caze.getUuid());
		dto.setDisease(caze.getDisease());
		dto.setCaseStatus(caze.getCaseStatus());
		dto.setPerson(DtoHelper.toReferenceDto(caze.getPerson()));
		dto.setHealthFacility(DtoHelper.toReferenceDto(caze.getHealthFacility()));
		
		dto.setReportingUser(userFacade.toReferenceDto(caze.getReportingUser()));
		dto.setReportDate(caze.getReportDate());
		dto.setInvestigatedDate(caze.getInvestigatedDate());

		dto.setSurveillanceOfficer(userFacade.toReferenceDto(caze.getSurveillanceOfficer()));
		dto.setSurveillanceSupervisor(userFacade.toReferenceDto(caze.getSurveillanceSupervisor()));
		dto.setCaseOfficer(userFacade.toReferenceDto(caze.getCaseOfficer()));
		dto.setCaseSupervisor(userFacade.toReferenceDto(caze.getCaseSupervisor()));
		dto.setContactOfficer(userFacade.toReferenceDto(caze.getContactOfficer()));
		dto.setContactSupervisor(userFacade.toReferenceDto(caze.getContactSupervisor()));
		
		dto.setSymptoms(SymptomsFacadeEjb.toDto(caze.getSymptoms()));
		
		return dto;
	}
	
}
