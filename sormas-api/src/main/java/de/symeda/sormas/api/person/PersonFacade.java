package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface PersonFacade {

    List<PersonReferenceDto> getAllPersons(UserReferenceDto user);

	List<PersonIndexDto> getIndexList(UserReferenceDto user);

	List<PersonDto> getPersonsAfter(Date date, String uuid);

	List<PersonReferenceDto> getPersonReferencesAfter(Date date, UserReferenceDto userRef);
    
	List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, Disease disease, String userUuid);
	
    PersonReferenceDto getReferenceByUuid(String uuid);
    
    PersonDto getPersonByUuid(String uuid);
    
    PersonReferenceDto savePerson(PersonReferenceDto dto);

    PersonDto savePerson(PersonDto dto);

	List<String> getAllUuids(String userUuid);

	List<PersonDto> getByUuids(List<String> uuids);
}
