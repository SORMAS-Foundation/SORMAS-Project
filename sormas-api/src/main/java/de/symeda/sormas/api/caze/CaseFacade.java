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
package de.symeda.sormas.api.caze;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.CaseMeasure;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllActiveCasesAfter(Date date);

	long count(CaseCriteria caseCriteria);
	
	List<CaseIndexDto> getIndexList(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties);
	
	List<CaseExportDto> getExportList(CaseCriteria caseCriteria, CaseExportType exportType, int first, int max, ExportConfigurationDto exportConfiguration);
	
	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto) throws ValidationRuntimeException;
    
    void validate(CaseDataDto dto) throws ValidationRuntimeException;

	CaseReferenceDto getReferenceByUuid(String uuid);
	
	List<String> getAllActiveUuids();

	List<CaseDataDto> getByUuids(List<String> uuids);
	
	String getUuidByUuidEpidNumberOrExternalId(String searchTerm);
	
	List<DashboardCaseDto> getCasesForDashboard(CaseCriteria caseCriteria);

	List<MapCaseDto> getCasesForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, CaseSurveillanceType surveillanceType, Date from, Date to);
	
	Map<CaseClassification, Long> getCaseCountPerClassification(CaseCriteria caseCriteria, boolean includeSharedCases);
	
	Map<PresentCondition, Long> getCaseCountPerPersonCondition(CaseCriteria caseCriteria, boolean includeSharedCases);
	
	Map<Disease, Long> getCaseCountByDisease(CaseCriteria caseCriteria, boolean includeSharedCases);
	
	String getLastReportedDistrictName(CaseCriteria caseCriteria, boolean includeSharedCases);
	
	List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date onsetFromDate, Date onsetToDate, Disease disease, CaseMeasure caseMeasure);

	List<CaseDataDto> getAllCasesOfPerson(String personUuid);
	
	void deleteCase(String caseUuid);
	
	void deleteCaseAsDuplicate(String caseUuid, String duplicateOfCaseUuid);
	
	Date getOldestCaseOnsetDate();
	
	Date getOldestCaseReportDate();
	
	boolean isArchived(String caseUuid);
	
	boolean isDeleted(String caseUuid);
	
	void archiveOrDearchiveCase(String caseUuid, boolean archive);
	
	List<String> getArchivedUuidsSince(Date since);
	
	List<String> getDeletedUuidsSince(Date since);
	
	boolean doesEpidNumberExist(String epidNumber, String caseUuid, Disease disease);
	
	String generateEpidNumber(CaseReferenceDto caze);

	void mergeCase(String leadUuid, String otherUuid);
	
	List<CaseIndexDto> getSimilarCases(CaseSimilarityCriteria criteria);
	
	List<CaseIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, boolean showDuplicatesWithDifferentRegion);
	
	void updateCompleteness(String caseUuid);

	CaseDataDto cloneCase(CaseDataDto existingCaseDto);

	void archiveAllArchivableCases(int daysAfterCaseGetsArchived);
	
	List<CaseReferenceDto> getRandomCaseReferences(CaseCriteria criteria, int count);
}
