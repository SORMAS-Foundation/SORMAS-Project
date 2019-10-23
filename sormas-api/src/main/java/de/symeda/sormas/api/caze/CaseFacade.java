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
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.importexport.ExportConfigurationDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllActiveCasesAfter(Date date, String userUuid);

	long count(CaseCriteria caseCriteria, String userUuid);
	
	List<CaseIndexDto> getIndexList(CaseCriteria caseCriteria, Integer first, Integer max, String userUuid, List<SortProperty> sortProperties);
	
	List<CaseExportDto> getExportList(CaseCriteria caseCriteria, CaseExportType exportType, int first, int max, String userUuid, ExportConfigurationDto exportConfiguration);
	
	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto) throws ValidationRuntimeException;
    
    void validate(CaseDataDto dto) throws ValidationRuntimeException;

	CaseReferenceDto getReferenceByUuid(String uuid);
	
	List<String> getAllActiveUuids(String userUuid);

	List<CaseDataDto> getByUuids(List<String> uuids);
	
	List<DashboardCaseDto> getCasesForDashboard(CaseCriteria caseCriteria, String userUuid);

	List<MapCaseDto> getCasesForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	Map<CaseClassification, Long> getCaseCountPerClassification(CaseCriteria caseCriteria, String userUuid);
	
	Map<PresentCondition, Long> getCaseCountPerPersonCondition(CaseCriteria caseCriteria, String userUuid);
	
	Map<Disease, Long> getCaseCountByDisease(CaseCriteria caseCriteria, String userUuid);
	
	String getLastReportedDistrictName(CaseCriteria caseCriteria, String userUuid);
	
	List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date onsetFromDate, Date onsetToDate, Disease disease, CaseMeasure caseMeasure);

	List<CaseDataDto> getAllCasesOfPerson(String personUuid, String userUuid);
	
	void deleteCase(String caseUuid, String userUuid);
	
	void deleteCaseAsDuplicate(String caseUuid, String duplicateOfCaseUuid, String userUuid);
	
	List<Object[]> queryCaseCount(StatisticsCaseCriteria caseCriteria, StatisticsCaseAttribute groupingA, StatisticsCaseSubAttribute subGroupingA,
			StatisticsCaseAttribute groupingB, StatisticsCaseSubAttribute subGroupingB, boolean includePopulation, Integer populationReferenceYear);
	
	Date getOldestCaseOnsetDate();
	
	Date getOldestCaseReportDate();
	
	boolean isArchived(String caseUuid);
	
	boolean isDeleted(String caseUuid);
	
	void archiveOrDearchiveCase(String caseUuid, boolean archive);
	
	List<String> getArchivedUuidsSince(String userUuid, Date since);
	
	List<String> getDeletedUuidsSince(String userUuid, Date since);
	
	boolean doesEpidNumberExist(String epidNumber, String caseUuid);
	
	String generateEpidNumber(CaseReferenceDto caze);

	void mergeCase(String leadUuid, String otherUuid);
	
	List<CaseIndexDto> getSimilarCases(CaseSimilarityCriteria criteria, String userUuid);
	
	List<CaseIndexDto[]> getCasesForDuplicateMerging(CaseCriteria criteria, String userUuid, boolean showDuplicatesWithDifferentRegion);
	
	void updateCompleteness(String caseUuid);

	CaseDataDto cloneCase(CaseDataDto existingCaseDto);

}
