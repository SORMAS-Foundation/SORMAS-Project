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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.MergeFacade;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.event.EventParticipantReferenceDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.followup.FollowUpPeriodDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.manualmessagelog.ManualMessageLogDto;
import de.symeda.sormas.api.messaging.MessageType;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.vaccination.VaccinationDto;

@Remote
public interface CaseFacade extends CoreFacade<CaseDataDto, CaseIndexDto, CaseReferenceDto, CaseCriteria>, MergeFacade {

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

	CaseDataDto updateFollowUpComment(@Valid @NotNull CaseDataDto dto) throws ValidationRuntimeException;

	void updateVaccinationStatus(CaseReferenceDto caseRef, VaccinationStatus status);

	CoreAndPersonDto<CaseDataDto> save(@Valid @NotNull CoreAndPersonDto<CaseDataDto> dto) throws ValidationRuntimeException;

	void setSampleAssociations(ContactReferenceDto sourceContact, CaseReferenceDto cazeRef);

	void setSampleAssociations(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef);

	void setSampleAssociationsUnrelatedDisease(EventParticipantReferenceDto sourceEventParticipant, CaseReferenceDto cazeRef);

	List<String> getAllActiveUuids();

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

	void deleteWithContacts(String caseUuid, DeletionDetails deletionDetails);

	Date getOldestCaseOnsetDate();

	Date getOldestCaseReportDate();

	Date getOldestCaseOutcomeDate();

	List<String> getDeletedUuidsSince(Date since);

	boolean doesEpidNumberExist(String epidNumber, String caseUuid, Disease disease);

	boolean doesExternalTokenExist(String externalToken, String caseUuid);

	String getGenerateEpidNumber(CaseDataDto caze);

	List<CaseSelectionDto> getSimilarCases(CaseSimilarityCriteria criteria);

	List<CaseMergeIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, @Min(1) Integer limit, boolean showDuplicatesWithDifferentRegion);

	void updateCompleteness(String caseUuid);

	CaseDataDto cloneCase(CaseDataDto existingCaseDto);

	void archiveAllArchivableCases(int daysAfterCaseGetsArchived);

	List<CaseReferenceDto> getRandomCaseReferences(CaseCriteria criteria, int count, Random randomGenerator);

	FollowUpPeriodDto calculateFollowUpUntilDate(CaseDataDto caseDto, boolean ignoreOverwrite);

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

	List<ProcessedEntity> saveBulkCase(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean diseaseVariantChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange);

	List<ProcessedEntity> saveBulkEditWithFacilities(
		List<String> caseUuidList,
		@Valid CaseBulkEditData updatedCaseBulkEditData,
		boolean diseaseChange,
		boolean diseaseVariantChange,
		boolean classificationChange,
		boolean investigationStatusChange,
		boolean outcomeChange,
		boolean surveillanceOfficerChange,
		Boolean doTransfer);

	List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson, int reportDateThreshold);

	List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson);

	List<CaseDataDto> getDuplicatesWithPathogenTest(@Valid PersonReferenceDto personReferenceDto, PathogenTestDto pathogenTestDto);

	List<CaseDataDto> getByPersonUuids(List<String> personUuids);

	List<CaseDataDto> getByExternalId(String externalId);

	List<CaseDataDto> getRelevantCasesForVaccination(VaccinationDto vaccination);

	void updateExternalData(@Valid List<ExternalDataDto> externalData) throws ExternalDataUpdateException;

	int updateCompleteness();

	PreviousCaseDto getMostRecentPreviousCase(PersonReferenceDto person, Disease disease, Date startDate);

	ProcessedEntity archive(String entityUuid, Date endOfProcessingDate, boolean includeContacts);

	List<ProcessedEntity> archive(List<String> entityUuids, boolean includeContacts);

	ProcessedEntity dearchive(String entityUuid, String dearchiveReason, boolean includeContacts);

	List<ProcessedEntity> dearchive(List<String> entityUuids, String dearchiveReason, boolean includeContacts);

	void setResultingCase(EventParticipantReferenceDto eventParticipantReferenceDto, CaseReferenceDto caseReferenceDto);

	EditPermissionType isEditContactAllowed(String uuid);

	boolean hasOtherValidVaccination(CaseDataDto caze, String vaccinationUuid);

	Pair<RegionReferenceDto, DistrictReferenceDto> getRegionAndDistrictRefsOf(CaseReferenceDto caze);

	boolean hasCurrentUserSpecialAccess(CaseReferenceDto caze);
}
