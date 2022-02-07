/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.backend.person;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DaoException;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.common.PseudonymizableAdo;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.ServerCommunicationException;
import de.symeda.sormas.app.rest.ServerConnectionException;

public abstract class PersonDependentDtoHelper<ADO extends PseudonymizableAdo, DTO extends PseudonymizableDto> extends AdoDtoHelper<ADO, DTO> {

    private PersonDtoHelper personDtoHelper = new PersonDtoHelper();

    @Override
    protected void preparePulledResult(List<DTO> result) throws NoConnectionException, ServerCommunicationException, ServerConnectionException, DaoException {

        final List<String> missingPersonUuidsToBePulled = new ArrayList<>();
        result.forEach(dto -> {
            final PersonReferenceDto personReferenceDto = getPerson(dto);
            final Person person = DatabaseHelper.getPersonDao().getByReferenceDto(personReferenceDto);

            if (person == null || (person.isPseudonymized() && !dto.isPseudonymized())) {
                missingPersonUuidsToBePulled.add(personReferenceDto.getUuid());
            }
        });

        if (!missingPersonUuidsToBePulled.isEmpty()) {
            personDtoHelper.pullMissing(missingPersonUuidsToBePulled);
        }
    }

    protected abstract PersonReferenceDto getPerson(DTO dto);
}
