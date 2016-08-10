package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CaseFacade {

    List<CaseDataDto> getAllCases();

	List<CaseDataDto> getAllCasesAfter(Date date, String userUuid);

	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto);

    CaseDataDto createCase(String personUuid, CaseDataDto caseDto);

   	CaseDataDto changeCaseStatus(String uuid, CaseStatus targetStatus);
}
