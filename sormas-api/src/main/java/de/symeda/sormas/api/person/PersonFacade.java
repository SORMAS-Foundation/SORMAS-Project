/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface PersonFacade {

	List<PersonNameDto> getNameDtos(UserReferenceDto user);

	List<PersonDto> getPersonsAfter(Date date, String uuid);

	List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid);
	
    PersonReferenceDto getReferenceByUuid(String uuid);
    
    PersonDto getPersonByUuid(String uuid);

    PersonDto savePerson(PersonDto dto) throws ValidationRuntimeException;
    
    PersonDto savePersonSimple(PersonDto source) throws ValidationRuntimeException;

    void validate(PersonDto dto) throws ValidationRuntimeException;
    
	List<String> getAllUuids(String userUuid);

	List<PersonDto> getByUuids(List<String> uuids);
	
	PersonIndexDto getIndexDto(String uuid);
	
	PersonDto buildPerson();
	
	Map<Disease, Long> getDeathCountByDisease(CaseCriteria caseCriteria, String userUuid);
}
