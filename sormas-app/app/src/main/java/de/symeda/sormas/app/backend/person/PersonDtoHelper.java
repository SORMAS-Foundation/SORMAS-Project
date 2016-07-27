package de.symeda.sormas.app.backend.person;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.app.backend.caze.Case;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;

/**
 * Created by Martin Wahnschaffe on 27.07.2016.
 */
public class PersonDtoHelper extends AdoDtoHelper<Person, PersonDto> {

    @Override
    public Person create() {
        return new Person();
    }

    @Override
    public void fillInnerFromDto(Person ado, PersonDto dto) {

        ado.setFirstName(dto.getFirstName());
        ado.setLastName(dto.getLastName());
    }
}
