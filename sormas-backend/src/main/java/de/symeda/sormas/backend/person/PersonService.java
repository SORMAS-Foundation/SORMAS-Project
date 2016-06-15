package de.symeda.sormas.backend.person;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.common.AbstractAdoService;
import de.symeda.sormas.backend.person.Person;



@Stateless
@LocalBean
//@RolesAllowed({
//		Permission._ADMIN,
//		Permission._USER })
//@DeclareRoles({
//		Permission._ADMIN,
//		Permission._USER })
public class PersonService extends AbstractAdoService<Person> {

	public PersonService() {
		super(Person.class);
	}

	public Person createPerson() {

		Person person = new Person();
		return person;
	}
	
	public Person toPerson(@NotNull PersonDto dto) {
		Person bo = getByUuid(dto.getUuid());
		if(bo==null) {
			bo = createPerson();
		}
		bo.setUuid(dto.getUuid());
		bo.setFirstName(dto.getFirstName());
		bo.setLastName(dto.getLastName());
		return bo;
	}
}
