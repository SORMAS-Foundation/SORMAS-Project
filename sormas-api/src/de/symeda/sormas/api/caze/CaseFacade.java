package de.symeda.sormas.api.caze;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CaseFacade {

    public abstract List<CaseDataDto> getAllCases();

    public abstract CaseDataDto getCaseDataByUuid(String uuid);
    
    public abstract CaseDataDto saveCase(CaseDataDto dto);

    public abstract CaseDataDto createCase(String personUuid, CaseDataDto caseDto);

}
