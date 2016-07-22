package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;

@Remote
public interface PersonFacade {

    List<PersonDto> getAllPersons();

	List<PersonDto> getAllPersonsAfter(Date date);
    
    List<ReferenceDto> getAllNoCaseAsReference();

    PersonDto getByUuid(String uuid);
    
    CasePersonDto getCasePersonByUuid(String uuid);

    PersonDto savePerson(PersonDto dto);

    CasePersonDto savePerson(CasePersonDto dto);

}
