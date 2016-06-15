package de.symeda.sormas.backend.caze;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.backend.mock.MockDataGenerator;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.person.PersonService;

@Singleton(name = "CaseFacade")
public class CaseFacadeEjb implements CaseFacade {
	
	@EJB
	private CaseService cs;
	
	@EJB
	private PersonService ps;
	
	@EJB
	private PersonFacade pf;
	
	private List<CaseDto> cases;
	
	@Override
	public List<CaseDto> getAllCases() {
		List<Case> casesDB = cs.getAll();
		cases = new ArrayList<>();
		if(casesDB!=null && casesDB.size()>0) {
			for (Case caze : casesDB) {
				cases.add(toDto(caze));
			}
		}
		return cases;
	}

	@Override
	public CaseDto getByUuid(String uuid) {
		return cases.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
	}
	
	@Override
	public CaseDto saveCase(CaseDto dto) {
		
//		if(dto.getPerson() != null) {
//			PersonDto personDto = pf.savePerson(dto.getPerson());
//			dto.setPersonDto(personDto);
//		}
		
		Case caze = cs.toCase(dto);
		cs.ensurePersisted(caze);
		
		return toDto(caze);
		
	}
	
	@Override
	public void createDemo() {
		List<CaseDto> cases = MockDataGenerator.createCases();
		for (CaseDto dto : cases) {
			PersonDto personDto = MockDataGenerator.createPerson();
			//personDto = pf.savePerson(personDto);
			dto.setPersonDto(personDto);
			saveCase(dto);

		}
	}
	
	public static CaseDto toDto(Case caze) {
		CaseDto dto = new CaseDto();
		dto.setUuid(caze.getUuid());
		dto.setCaseStatus(caze.getCaseStatus());
		dto.setDescription(caze.getDescription());
		
		dto.setPersonDto(PersonFacadeEjb.toDto(caze.getPerson()));
		
		return dto;
	}
	
}
