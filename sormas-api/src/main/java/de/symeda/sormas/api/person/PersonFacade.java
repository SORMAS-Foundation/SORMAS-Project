package de.symeda.sormas.api.person;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface PersonFacade {

    public abstract List<CasePersonDto> getAllPerson();

    public abstract CasePersonDto getByUuid(String uuid);

    public abstract CasePersonDto savePerson(CasePersonDto dto);
}
