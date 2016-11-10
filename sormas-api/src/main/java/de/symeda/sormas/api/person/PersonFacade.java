package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface PersonFacade {

    List<PersonReferenceDto> getAllPersons();

	List<PersonReferenceDto> getAllPersonsAfter(Date date);
    
    List<PersonReferenceDto> getAllNoCasePersons();

    PersonReferenceDto getByUuid(String uuid);
    
    CasePersonDto getCasePersonByUuid(String uuid);
    
	List<CasePersonDto> getAllCasePersonsAfter(Date date);

    PersonReferenceDto savePerson(PersonReferenceDto dto);

    CasePersonDto savePerson(CasePersonDto dto);

}
