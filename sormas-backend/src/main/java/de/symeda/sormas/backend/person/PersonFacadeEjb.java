package de.symeda.sormas.backend.person;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService ps;
	
	@Override
	public List<PersonDto> getAllPersons() {

		return ps.getAll().stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public List<ReferenceDto> getAllNoCaseAsReference() {

		return ps.getAllNoCase().stream()
				.map(c -> DtoHelper.toReferenceDto(c))
				.collect(Collectors.toList());
	}

	@Override
	public PersonDto getByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> ps.getByUuid(u))
				.map(c -> toDto(c))
				.orElse(null);
	}
	
	@Override
	public PersonDto savePerson(PersonDto dto) {
		Person person = toPerson(dto);
		ps.ensurePersisted(person);
		
		return toDto(person);
		
	}
	
	public Person toPerson(@NotNull PersonDto dto) {
		Person bo = ps.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = ps.createPerson();
		}
		bo.setUuid(dto.getUuid());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		return bo;
	}
	
	public static PersonDto toDto(Person person) {
		PersonDto dto = new PersonDto();
		dto.setChangeDate(person.getChangeDate());
		dto.setUuid(person.getUuid());
		dto.setFirstName(person.getFirstName());
		dto.setLastName(person.getLastName());
		return dto;
	}
	
	
}
