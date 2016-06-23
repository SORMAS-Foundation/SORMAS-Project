package de.symeda.sormas.backend.caze;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;

@Singleton(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {
	
	@EJB
	private CaseService cs;
	
	@EJB
	private PersonService ps;
	
	
	@Override
	public List<CaseDataDto> getAllCases() {
		return cs.getAll().stream()
			.map(c -> toCaseDataDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public CaseDataDto getCaseDataByUuid(String uuid) {
		return Optional.of(uuid)
			.map(u -> cs.getByUuid(u))
			.map(c -> toCaseDataDto(c))
			.orElse(null);
	}
	
	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		Case caze = fromCaseDataDto(dto);
		cs.ensurePersisted(caze);
		
		return toCaseDataDto(caze);
		
	}
	
	@Override
	public CaseDataDto createCase(String personUuid, CaseDataDto caseDto) {
		Person person = ps.getByUuid(personUuid);
		
		Case caze = fromCaseDataDto(caseDto);
		caze.setPerson(person);
		cs.ensurePersisted(caze);
		
		return toCaseDataDto(caze);
		
	}
	
	
	public Case fromCaseDataDto(@NotNull CaseDataDto dto) {
		boolean createCase = dto.getChangeDate() == null;
		Case caze = createCase ? new Case() : cs.getByUuid(dto.getUuid());
		caze.setUuid(dto.getUuid());
		caze.setDisease(dto.getDisease());
		caze.setDescription(dto.getDescription());
		caze.setCaseStatus(dto.getCaseStatus());
		return caze;
	}
	
	public static CaseDataDto toCaseDataDto(Case caze) {
		CaseDataDto dto = new CaseDataDto();
		dto.setChangeDate(caze.getChangeDate());
		dto.setUuid(caze.getUuid());
		dto.setDisease(caze.getDisease());
		dto.setCaseStatus(caze.getCaseStatus());
		dto.setDescription(caze.getDescription());
		
		dto.setPersonDto(PersonFacadeEjb.toDto(caze.getPerson()));
		
		return dto;
	}
	
}
