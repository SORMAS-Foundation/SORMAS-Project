package de.symeda.sormas.api.caze;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface CaseFacade {

    public abstract List<CaseDto> getAllCases();

    public abstract CaseDto getByUuid(String uuid);

    public abstract CaseDto saveCase(CaseDto dto);

    public abstract void createDemo();

}
