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
package de.symeda.sormas.api.caze;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ejb.Remote;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.messaging.ManualMessageLogDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CaseFacade extends CoreFacade<CaseDataDto, CaseIndexDto, CaseReferenceDto, CaseCriteria> {

	List<CaseDataDto> getAllActiveCasesAfter(Date date);

	/**
	 * Additional change dates filters for: sample, pathogenTests, patient and location.
	 */
	List<CaseDataDto> getAllActiveCasesAfter(Date date, boolean includeExtendedChangeDateFilters);

	long count(CaseCriteria caseCriteria, boolean ignoreUserFilter);

	List<CaseSelectionDto> getCaseSelectionList(CaseCriteria caseCriteria);

	List<CaseListEntryDto> getEntriesList(String personUuid, Integer first, Integer max);

	Page<CaseIndexDto> getIndexPage(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties);

	Page<CaseIndexDetailedDto> getIndexDetailedPage(
		@NotNull CaseCriteria caseCriteria,
		Integer offset,
		Integer max,
		List<SortProperty> sortProperties);

	List<CaseIndexDetailedDto> getIndexDetailedList(CaseCriteria caseCriteria, Integer offset, Integer max, List<SortProperty> sortProperties);

	CaseDataDto postUpdate(String uuid, JsonNode caseDataDtoJson);

	List<CaseExportDto> getExportList(
		CaseCriteria caseCriteria,
		Collection<String> selectedRows,
		CaseExportType exportType,
		int first,
		int max,
		ExportConfigurationDto exportConfiguration,
		Language userLanguage);

	CaseDataDto getCaseDataByUuid(String uuid);

	CaseDataDto save(@Valid @NotNull CaseDataDto dto, boolean systemSave) throws ValidationRuntimeException;

	CoreAndPersonDto<CaseDataDto> save(@Valid @NotNull CoreAndPersonDto<CaseDataDto> dto) throws ValidationRuntimeException;

	void setSampleAssociations(ContactReferenceDto sourceContact, CaseReferenceDto cazeRef);

	void setSampleAssociations(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef);

	void setSampleAssociationsUnrelatedDisease(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef);

	void validate(CaseDataDto dto) throws ValidationRuntimeException;

	List<String> getAllActiveUuids();

	List<CaseDataDto> getAllActiveCasesAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	String getUuidByUuidEpidNumberOrExternalId(String searchTerm, CaseCriteria caseCriteria);

	List<MapCaseDto> getCasesForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to,
		NewCaseDateType dateType);

	Long countCasesForMap(
		RegionReferenceDto regionRef,
		DistrictReferenceDto districtRef,
		Disease disease,
		Date from,
		Date to,
		NewCaseDateType dateType);

	List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date onsetFromDate, Date onsetToDate, Disease disease, CaseMeasure caseMeasure);

	List<CaseDataDto> getAllCasesOfPerson(String personUuid);

	List<String> deleteCases(List<String> caseUuids);

	void deleteWithContacts(String caseUuid);

	void deleteCaseAsDuplicate(String caseUuid, String duplicateOfCaseUuid) throws ExternalSurveillanceToolException;

	Date getOldestCaseOnsetDate();

	Date getOldestCaseReportDate();

	Date getOldestCaseOutcomeDate();

	boolean isDeleted(String caseUuid);

	boolean isArchived(String caseUuid);

	List<String> getArchivedUuidsSince(Date since);

	List<String> getDeletedUuidsSince(Date since);

	boolean doesEpidNumberExist(String epidNumber, String caseUuid, Disease disease);

	boolean doesExternalTokenExist(String externalToken, String caseUuid);

	String getGenerateEpidNumber(CaseDataDto caze);

	void mergeCase(String leadUuid, String otherUuid);

	List<CaseSelectionDto> getSimilarCases(CaseSimilarityCriteria criteria);

	List<CaseIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, boolean showDuplicatesWithDifferentRegion);

	void updateCompleteness(String caseUuid);

	CaseDataDto cloneCase(CaseDataDto existingCaseDto);

	void archiveAllArchivableCases(int daysAfterCaseGetsArchived);

	List<CaseReferenceDto> getRandomCaseReferences(CaseCriteria criteria, int count, Random randomGenerator);

	FollowUpPeriodDto calculateFollowUpUntilDate(CaseDataDto caseDto, boolean ignoreOverwrite);

	EditPermissionType isCaseEditAllowed(String caseUuid);

	List<CaseFollowUpDto> getCaseFollowUpList(
		CaseCriteria caseCriteria,
		Date referenceDate,
		int interval,
		Integer first,
		Integer max,
		List<SortProperty> sortProperties);

	Page<CaseFollowUpDto> getCaseFollowUpIndexPage(
		@NotNull CaseFollowUpCriteria criteria,
		Integer offset,
		Integer max,
		List<SortProperty> sortProperties);

	void sendMessage(List<String> caseUuids, String subject, String messageContent, MessageType... messageTypes);

	long countCasesWithMissingContactInformation(List<String> caseUuids, MessageType messageType);

	List<ManualMessageLogDto> getMessageLog(String caseUuid, MessageType messageType);

	List<String> getUuidsNotShareableWithExternalReportingTools(List<String> caseUuids);

	Integer saveBulkCase(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange);

	void saveBulkEditWithFacilities(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange,
		Boolean doTransfer);

	List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson, int reportDateThreshold);

	List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson);

	List<CaseDataDto> getByPersonUuids(List<String> personUuids);

	List<CaseDataDto> getByExternalId(String externalId);

	void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException;

	int updateCompleteness();

	PreviousCaseDto getMostRecentPreviousCase(PersonReferenceDto person, Disease disease, Date startDate);

	void archive(String entityUuid, Date endOfProcessingDate, boolean includeContacts);

	void archive(List<String> entityUuids, boolean includeContacts);

	void dearchive(List<String> entityUuids, String dearchiveReason, boolean includeContacts);
}
