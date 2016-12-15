package de.symeda.sormas.backend.caze;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.backend.facility.FacilityFacadeEjb;
import de.symeda.sormas.backend.facility.FacilityService;
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
	
	public Case fromCaseDataDto(@NotNull CaseDataDto source) {
		
		Case target = caseService.getByUuid(source.getUuid());
		if (target == null) {
			target = new Case();
			target.setUuid(source.getUuid());
			if (source.getCreationDate() != null) {
				target.setCreationDate(new Timestamp(source.getCreationDate().getTime()));
			}
		}
				
		target.setDisease(source.getDisease());
		target.setReportDate(source.getReportDate());
		target.setReportingUser(userService.getByReferenceDto(source.getReportingUser()));
		target.setPerson(personService.getByReferenceDto(source.getPerson()));
		target.setCaseClassification(source.getCaseClassification());
		target.setInvestigationStatus(source.getInvestigationStatus());

		target.setRegion(regionService.getByReferenceDto(source.getRegion()));
		target.setDistrict(districtService.getByReferenceDto(source.getDistrict()));
		target.setCommunity(communityService.getByReferenceDto(source.getCommunity()));
		target.setHealthFacility(facilityService.getByReferenceDto(source.getHealthFacility()));

		target.setSurveillanceOfficer(userService.getByReferenceDto(source.getSurveillanceOfficer()));
		target.setCaseOfficer(userService.getByReferenceDto(source.getCaseOfficer()));
		target.setContactOfficer(userService.getByReferenceDto(source.getContactOfficer()));

		target.setSymptoms(symptomsFacade.fromDto(source.getSymptoms()));

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
		
		target.setSymptoms(SymptomsFacadeEjb.toDto(source.getSymptoms()));
		
		return target;
	}
	
}
