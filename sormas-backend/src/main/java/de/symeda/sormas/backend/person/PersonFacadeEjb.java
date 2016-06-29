package de.symeda.sormas.backend.person;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.person.CasePersonDto;
import de.symeda.sormas.api.person.PersonFacade;

@Stateless(name = "PersonFacade")
public class PersonFacadeEjb implements PersonFacade {
	
	@EJB
	private PersonService ps;
	
	@Override
	public List<CasePersonDto> getAllPerson() {

		return ps.getAll().stream()
			.map(c -> toDto(c))
			.collect(Collectors.toList());
	}

	@Override
	public CasePersonDto getByUuid(String uuid) {
		return Optional.of(uuid)
				.map(u -> ps.getByUuid(u))
				.map(c -> toDto(c))
				.orElse(null);
	}
	
	@Override
	public CasePersonDto savePerson(CasePersonDto dto) {
		Person person = toPerson(dto);
		ps.ensurePersisted(person);
		
		return toDto(person);
		
	}
	
	public Person toPerson(@NotNull CasePersonDto dto) {
		Person bo = ps.getByUuid(dto.getUuid());
		if(bo==null) {
			bo = ps.createPerson();
		}
		bo.setUuid(dto.getUuid());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		return bo;
	}
	
	public static CasePersonDto toDto(Person person) {
		CasePersonDto dto = new CasePersonDto();
		dto.setChangeDate(person.getChangeDate());
		dto.setUuid(person.getUuid());
		dto.setFirstName(person.getFirstName());
		dto.setLastName(person.getLastName());
		return dto;
	}
	
	
}
