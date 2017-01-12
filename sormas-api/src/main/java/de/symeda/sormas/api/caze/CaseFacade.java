package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllCasesAfter(Date date, String userUuid);

	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto);

    CaseDataDto createCase(String personUuid, CaseDataDto caseDto);

	List<CaseReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid);

	List<CaseReferenceDto> getSelectableCases(UserReferenceDto user);

	CaseReferenceDto getReferenceByUuid(String uuid);
	
	CaseDataDto getByPersonAndDisease(String personUuid, Disease disease, String userUuid);
}
