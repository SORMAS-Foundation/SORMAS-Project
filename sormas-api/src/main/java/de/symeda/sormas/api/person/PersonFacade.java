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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface PersonFacade {

	List<PersonDto> getPersonsAfter(Date date);

	List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease);

	PersonReferenceDto getReferenceByUuid(String uuid);

	JournalPersonDto getPersonForJournal(String uuid);

	PersonDto savePerson(@Valid PersonDto dto);

	DataHelper.Pair<CaseClassification, PersonDto> savePersonWithoutNotifyingExternalJournal(@Valid PersonDto source);

	void validate(PersonDto dto);

	List<String> getAllUuids();

	PersonDto getPersonByUuid(String uuid);

	List<PersonDto> getByUuids(List<String> uuids);

	/**
	 * Returns a list with the names of all persons that the user has access to and that match the criteria.
	 * This only includes persons that are associated with an active case, contact or event participant.
	 */
	List<PersonNameDto> getMatchingNameDtos(UserReferenceDto user, PersonSimilarityCriteria criteria);

	boolean checkMatchingNameInDatabase(UserReferenceDto userRef, PersonSimilarityCriteria criteria);

	List<SimilarPersonDto> getSimilarPersonsByUuids(List<String> personUuids);

	Boolean isValidPersonUuid(String personUuid);

	List<PersonFollowUpEndDto> getLatestFollowUpEndDates(Date since, boolean forSymptomsJournal);

	Date getLatestFollowUpEndDateByUuid(String uuid);

	FollowUpStatus getMostRelevantFollowUpStatusByUuid(String uuid);

	boolean setSymptomJournalStatus(String personUuid, SymptomJournalStatus status);

	List<PersonIndexDto> getIndexList(PersonCriteria criteria, Integer offset, Integer limit, List<SortProperty> sortProperties);

	Page<PersonIndexDto> getIndexPage(PersonCriteria personCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	long count(PersonCriteria criteria);

	boolean exists(String uuid);

	boolean doesExternalTokenExist(String externalToken, String personUuid);

	long setMissingGeoCoordinates(boolean overwriteExistingCoordinates);

	boolean isSharedWithoutOwnership(String uuid);

	List<PersonDto> getByExternalIds(List<String> externalIds);

	void updateExternalData(List<ExternalDataDto> externalData) throws ExternalDataUpdateException;
}
