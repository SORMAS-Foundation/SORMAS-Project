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
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseCriteria;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllActiveCasesAfter(Date date, String userUuid);

	List<CaseIndexDto> getIndexList(String userUuid, CaseCriteria caseCriteria);
	
	List<CaseExportDto> getExportList(String userUuid, CaseCriteria caseCriteria, int first, int max);
	
	CaseDataDto getCaseDataByUuid(String uuid);
    
	/**
	 * Saves the case. Throws a ValidationRuntimeException when a required field that does not have a not null constraint in the database
	 * is saved or there is an infrastructure error (e.g. the district of the case does not belong in the region of the case).
	 */
    CaseDataDto saveCase(CaseDataDto dto) throws ValidationRuntimeException;
    
    void validate(CaseDataDto dto) throws ValidationRuntimeException;

	List<CaseReferenceDto> getSelectableCases(UserReferenceDto user);

	CaseReferenceDto getReferenceByUuid(String uuid);
	
	List<String> getAllActiveUuids(String userUuid);
	
	CaseDataDto saveAndTransferCase(CaseDataDto caze);

	List<CaseDataDto> getByUuids(List<String> uuids);
	
	List<DashboardCaseDto> getNewCasesForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);

	List<MapCaseDto> getCasesForMap(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	Map<CaseClassification, Long> getNewCaseCountPerClassification(CaseCriteria caseCriteria, String userUuid);
	
	Map<PresentCondition, Long> getNewCaseCountPerPersonCondition(CaseCriteria caseCriteria, String userUuid);
	
	/**
	 * @param fromDate optional
	 * @param toDate optional
	 * @param disease optional
	 */
	Map<RegionDto, Long> getCaseCountPerRegion(Date fromDate, Date toDate, Disease disease);

	/**
	 * @param fromDate optional
	 * @param toDate optional
	 * @param disease optional
	 */
	List<Pair<DistrictDto, BigDecimal>> getCaseMeasurePerDistrict(Date onsetFromDate, Date onsetToDate, Disease disease, CaseMeasure caseMeasure);

	CaseDataDto getLatestCaseByPerson(String personUuid, String userUuid);
	
	CaseDataDto getMatchingCaseForImport(CaseDataDto importCaze, PersonReferenceDto existingPerson, String userUuid);
	
	List<CaseDataDto> getAllCasesOfPerson(String personUuid, String userUuid);
	
	void deleteCase(CaseReferenceDto caseRef, String userUuid);

	List<Object[]> queryCaseCount(StatisticsCaseCriteria caseCriteria, StatisticsCaseAttribute groupingA, StatisticsCaseSubAttribute subGroupingA,
			StatisticsCaseAttribute groupingB, StatisticsCaseSubAttribute subGroupingB);
	
	Date getOldestCaseOnsetDate();
	
	Date getOldestCaseReceptionDate();
	
	Date getOldestCaseReportDate();
	
	boolean isArchived(String caseUuid);
	
	void archiveOrDearchiveCase(String caseUuid, boolean archive);
	
	List<String> getArchivedUuidsSince(String userUuid, Date since);
	
}
