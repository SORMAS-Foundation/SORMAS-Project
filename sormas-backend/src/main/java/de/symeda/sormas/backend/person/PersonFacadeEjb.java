package de.symeda.sormas.backend.person;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Singleton;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;

@Singleton(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService ps;
	
	private List<PersonDto> persons;
	
	@Override
	public List<PersonDto> getAllPerson() {
		List<Person> personsDB = ps.getAll();
		persons = new ArrayList<>();
		if(personsDB!=null && personsDB.size()>0) {
			for (Person person : personsDB) {
				persons.add(toDto(person));
			}
		}
		return persons;
	}

	@Override
	public PersonDto getByUuid(String uuid) {
		return persons.stream().filter(c -> c.getUuid().equals(uuid)).findFirst().orElse(null);
	}
	
	@Override
	public PersonDto savePerson(PersonDto dto) {
		Person person = ps.toPerson(dto);
		ps.ensurePersisted(person);
		
		return toDto(person);
		
	}
	
	public static PersonDto toDto(Person person) {
		PersonDto dto = new PersonDto();
		dto.setUuid(person.getUuid());
		dto.setFirstName(person.getFirstName());
		dto.setLastName(person.getLastName());
		return dto;
	}
	
	
}
