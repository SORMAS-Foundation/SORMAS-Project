package de.symeda.sormas.api.person;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface PersonFacade {

    public abstract List<PersonDto> getAllPersons();

    public abstract List<ReferenceDto> getAllNoCaseAsReference();

    public abstract PersonDto getByUuid(String uuid);
    
    public abstract CasePersonDto getCasePersonByUuid(String uuid);

    public abstract PersonDto savePerson(PersonDto dto);

    public abstract CasePersonDto savePerson(CasePersonDto dto);
    
}
