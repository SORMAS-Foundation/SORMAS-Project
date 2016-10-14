package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllCasesAfter(Date date, String userUuid);

	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto);

    CaseDataDto createCase(String personUuid, CaseDataDto caseDto);

   	CaseDataDto changeCaseStatus(String uuid, CaseStatus targetStatus);

	List<ReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid);
}
