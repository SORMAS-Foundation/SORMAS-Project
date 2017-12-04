package de.symeda.sormas.api.caze;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface CaseFacade {

	List<CaseDataDto> getAllCasesAfter(Date date, String userUuid);
	
	List<CaseDataDto> getAllCasesByDisease(Disease disease, String userUuid);
	
	List<CaseDataDto> getAllCasesBetween(Date fromDate, Date toDate, DistrictReferenceDto districtRef, Disease disease, String userUuid);
	
	List<CaseIndexDto> getIndexList(String userUuid);
	
	CaseDataDto getCaseDataByUuid(String uuid);
    
    CaseDataDto saveCase(CaseDataDto dto);

	List<CaseReferenceDto> getAllCasesAfterAsReference(Date date, String userUuid);

	List<CaseReferenceDto> getSelectableCases(UserReferenceDto user);

	CaseReferenceDto getReferenceByUuid(String uuid);
	
	CaseDataDto getByPersonAndDisease(String personUuid, Disease disease, String userUuid);

	List<String> getAllUuids(String userUuid);
	
	CaseDataDto moveCase(CaseReferenceDto caze, CommunityReferenceDto community, FacilityReferenceDto facility, String facilityDetails, UserReferenceDto surveillanceOfficer);

	List<CaseDataDto> getByUuids(List<String> uuids);
	
	List<DashboardCaseDto> getNewCasesForDashboard(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);

	List<MapCaseDto> getCasesForMap(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	List<StatisticsCaseDto> getCasesForStatistics(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
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
	Map<DistrictDto, Long> getCaseCountPerDistrict(Date onsetFromDate, Date onsetToDate, Disease disease);

	void deleteCase(CaseReferenceDto caseRef, String userUuid);
	
}
