package de.symeda.sormas.backend.caze;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.backend.facility.FacilityService;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
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
	
	
	public Case fromCaseDataDto(@NotNull CaseDataDto dto) {
		
		Case caze;
		if (dto.getChangeDate() == null) {
			caze = new Case();
		} else {
			caze = caseService.getByUuid(dto.getUuid());
		}

		caze.setUuid(dto.getUuid());
		caze.setDisease(dto.getDisease());
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
		dto.setChangeDate(caze.getChangeDate());
		dto.setUuid(caze.getUuid());
		dto.setDisease(caze.getDisease());
		dto.setCaseStatus(caze.getCaseStatus());
		dto.setPerson(PersonFacadeEjb.toDto(caze.getPerson()));
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
