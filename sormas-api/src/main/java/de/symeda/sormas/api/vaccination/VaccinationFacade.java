/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.vaccination;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;

@Remote
public interface VaccinationFacade {

	VaccinationDto save(@Valid VaccinationDto dto);

	VaccinationDto create(
		@Valid VaccinationDto dto,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease);

	List<VaccinationDto> getAllVaccinations(String personUuid, Disease disease);

	List<VaccinationListEntryDto> getEntriesList(String personUuid, Disease disease, Integer first, Integer max);

	void validate(VaccinationDto vaccinationDto, boolean allowEmptyImmunization);

	Map<String, String> getLastVaccinationType();

	void delete(String uuid);

	VaccinationDto getByUuid(String uuid);
}
