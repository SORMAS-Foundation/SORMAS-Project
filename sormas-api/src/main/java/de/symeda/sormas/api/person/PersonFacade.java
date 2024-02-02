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
package de.symeda.sormas.api.person;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.BaseFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionFacade;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface PersonFacade extends BaseFacade<PersonDto, PersonIndexDto, PersonReferenceDto, PersonCriteria>, EditPermissionFacade {

	Set<PersonAssociation> getPermittedAssociations();

	List<PersonDto> getDeathsBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease);

	JournalPersonDto getPersonForJournal(String uuid);

	PersonDto save(@Valid @NotNull PersonDto source, boolean skipValidation);

	DataHelper.Pair<CaseClassification, PersonDto> savePersonWithoutNotifyingExternalJournal(@Valid PersonDto source);

	/**
	 * Returns a list with the names of all persons that the user has access to and that match the criteria.
	 * This only includes persons that are associated with an active case, contact or event participant.
	 * 
	 * @return
	 */
	List<SimilarPersonDto> getSimilarPersonDtos(PersonSimilarityCriteria criteria);

	boolean checkMatchingNameInDatabase(UserReferenceDto userRef, PersonSimilarityCriteria criteria);

	Boolean isValidPersonUuid(String personUuid);

	List<PersonFollowUpEndDto> getLatestFollowUpEndDates(Date since, boolean forSymptomsJournal);

	Date getLatestFollowUpEndDateByUuid(String uuid);

	boolean setSymptomJournalStatus(String personUuid, SymptomJournalStatus status);

	List<PersonExportDto> getExportList(PersonCriteria criteria, int first, int max);

	Page<PersonIndexDto> getIndexPage(PersonCriteria personCriteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	boolean exists(String uuid);

	boolean doesExternalTokenExist(String externalToken, String personUuid);

	long setMissingGeoCoordinates(boolean overwriteExistingCoordinates);

	boolean isSharedOrReceived(String uuid);

	List<PersonDto> getByExternalIds(List<String> externalIds);

	void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException;

	void mergePerson(PersonDto leadPerson, PersonDto otherPerson);

	void mergePerson(
		String leadPersonUuid,
		String otherPersonUuid,
		boolean mergeProperties,
		List<String> selectedEventParticipantUuids,
		boolean mergeEventparticipantProperties);

	boolean isPersonSimilar(PersonSimilarityCriteria criteria, String personUuid);

	PersonDto getByContext(PersonContext context, String contextUuid);

	boolean isEnrolledInExternalJournal(String uuid);

	void copyHomeAddress(PersonReferenceDto source, PersonReferenceDto target);

    Map<Disease, Long> getDeathCountByDisease(CaseCriteria caseCriteria, boolean excludeSharedCases, boolean excludeCasesFromContacts);
}
