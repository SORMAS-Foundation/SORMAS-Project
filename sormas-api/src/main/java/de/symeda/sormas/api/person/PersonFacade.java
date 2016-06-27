package de.symeda.sormas.api.person;

import java.util.List;

import javax.ejb.Remote;

@Remote
public interface PersonFacade {

    public abstract List<PersonDto> getAllPerson();

    public abstract PersonDto getByUuid(String uuid);

    public abstract PersonDto savePerson(PersonDto dto);
}
