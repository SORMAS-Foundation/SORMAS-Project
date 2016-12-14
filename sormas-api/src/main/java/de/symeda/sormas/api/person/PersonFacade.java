package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface PersonFacade {

    List<PersonReferenceDto> getAllPersons();

	List<PersonReferenceDto> getPersonReferencesAfter(Date date);
    
	List<PersonReferenceDto> getPersonsByRegion(RegionReferenceDto regionDto);
	
    PersonReferenceDto getReferenceByUuid(String uuid);
    
    PersonDto getPersonByUuid(String uuid);
    
	List<PersonDto> getPersonsAfter(Date date);

    PersonReferenceDto savePerson(PersonReferenceDto dto);

    PersonDto savePerson(PersonDto dto);

	List<PersonIndexDto> getIndexList(UserReferenceDto user);

}
