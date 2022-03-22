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

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface VaccinationFacade {

	VaccinationDto save(@Valid VaccinationDto dto);

	/**
	 * Creates the passed DTO as an entity and automatically assigns an existing immunization or, if none is found,
	 * creates a new immunization with the passed region, district, person, and disease, and assigns it to the vaccination.
	 * Throws an exception when called with a DTO that already has an immunization assigned or has a UUID that already
	 * exists in the database.
	 * 
	 * If the vaccination date is empty, assigns the vaccination to the latest immunization (defined by
	 * ImmunizationEntityHelper.getDateForComparison).
	 * If the vaccination date is not empty, assigns an existing immunization according to the following priorities:
	 * 1. Immunization with start date <= vaccination date <= end date
	 * 2. Immunization with the nearest start or end date to the vaccination date
	 * 3. Immunization with the nearest report date to the vaccination date
	 */
	VaccinationDto createWithImmunization(
		@Valid VaccinationDto dto,
		RegionReferenceDto region,
		DistrictReferenceDto district,
		PersonReferenceDto person,
		Disease disease);

	List<VaccinationDto> getAllVaccinations(String personUuid, Disease disease);

	List<VaccinationListEntryDto> getEntriesList(VaccinationListCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	List<VaccinationListEntryDto> getEntriesListWithRelevance(
		CaseReferenceDto caseReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max);

	List<VaccinationListEntryDto> getEntriesListWithRelevance(
		ContactReferenceDto contactReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max);

	List<VaccinationListEntryDto> getEntriesListWithRelevance(
		EventParticipantReferenceDto eventParticipantReferenceDto,
		VaccinationListCriteria criteria,
		Integer first,
		Integer max);

	void validate(VaccinationDto vaccinationDto, boolean allowEmptyImmunization);

	Map<String, String> getLastVaccinationType();

	/**
	 * Deletes the vaccination with the specified UUID, and also deletes the associated immunization if it
	 * is not associated with any other vaccination in the database.
	 */
	void deleteWithImmunization(String uuid);

	VaccinationDto getByUuid(String uuid);

	VaccinationDto postUpdate(String uuid, JsonNode vaccinationDtoJson);
}
