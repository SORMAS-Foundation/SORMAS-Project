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

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseHelper;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;
import de.symeda.sormas.backend.region.CommunityService;
import de.symeda.sormas.backend.region.DistrictService;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb;
import de.symeda.sormas.backend.symptoms.SymptomsFacadeEjb.SymptomsFacadeEjbLocal;
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
	private RegionService regionService;
	@EJB
	private DistrictService districtService;
	@EJB
	private CommunityService communityService;

	
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
	public List<CaseReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid) {
		
		User user = userService.getByUuid(userUuid);
		
		return caseService.getAllAfter(date, user).stream()
			.map(c -> toReferenceDto(c))
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

		caze.setRegion(regionService.getByReferenceDto(dto.getRegion()));
		caze.setDistrict(districtService.getByReferenceDto(dto.getDistrict()));
		caze.setCommunity(communityService.getByReferenceDto(dto.getCommunity()));
		caze.setHealthFacility(facilityService.getByReferenceDto(dto.getHealthFacility()));

		caze.setSurveillanceOfficer(userService.getByReferenceDto(dto.getSurveillanceOfficer()));
		caze.setCaseOfficer(userService.getByReferenceDto(dto.getCaseOfficer()));
		caze.setContactOfficer(userService.getByReferenceDto(dto.getContactOfficer()));

		// TODO: split into multiple view dependant dtos?
		caze.setSymptoms(symptomsFacade.fromDto(dto.getSymptoms()));

		return caze;
	}
	
	public static CaseReferenceDto toReferenceDto(Case entity) {
		if (entity == null) {
			return null;
		}
		CaseReferenceDto dto = new CaseReferenceDto();
		DtoHelper.fillReferenceDto(dto, entity);
		return dto;
	}	

	public static CaseDataDto toCaseDataDto(Case entity) {
		if (entity == null) {
			return null;
		}
		CaseDataDto dto = new CaseDataDto();
		DtoHelper.fillReferenceDto(dto, entity);

		dto.setDisease(entity.getDisease());
		dto.setCaseStatus(entity.getCaseStatus());
		dto.setPerson(PersonFacadeEjb.toReferenceDto(entity.getPerson()));
		
		dto.setRegion(DtoHelper.toReferenceDto(entity.getRegion()));
		dto.setDistrict(DtoHelper.toReferenceDto(entity.getDistrict()));
		dto.setCommunity(DtoHelper.toReferenceDto(entity.getCommunity()));
		dto.setHealthFacility(DtoHelper.toReferenceDto(entity.getHealthFacility()));
		
		dto.setReportingUser(UserFacadeEjb.toReferenceDto(entity.getReportingUser()));
		dto.setReportDate(entity.getReportDate());
		dto.setInvestigatedDate(entity.getInvestigatedDate());

		dto.setSurveillanceOfficer(UserFacadeEjb.toReferenceDto(entity.getSurveillanceOfficer()));
		dto.setCaseOfficer(UserFacadeEjb.toReferenceDto(entity.getCaseOfficer()));
		dto.setContactOfficer(UserFacadeEjb.toReferenceDto(entity.getContactOfficer()));
		
		dto.setSymptoms(SymptomsFacadeEjb.toDto(entity.getSymptoms()));
		
		return dto;
	}
	
}
