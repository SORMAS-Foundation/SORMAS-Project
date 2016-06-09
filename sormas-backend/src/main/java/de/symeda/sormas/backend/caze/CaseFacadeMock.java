package de.symeda.sormas.backend.caze;

import java.util.List;

import javax.ejb.Singleton;

import de.symeda.sormas.api.caze.CaseDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.backend.caze.mock.MockDataGenerator;

@Singleton(name = "CaseFacade")
public class CaseFacadeMock implements CaseFacade {

	private List<CaseDto> cases = MockDataGenerator.createCases();
	
	@Override
	public List<CaseDto> getAllCases() {
		return cases;
	}

	@Override
	public CaseDto getByUuid(String uuid) {
		return cases.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
	}
}
