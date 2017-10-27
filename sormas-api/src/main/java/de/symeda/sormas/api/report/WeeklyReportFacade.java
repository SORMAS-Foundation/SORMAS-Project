package de.symeda.sormas.api.report;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.EpiWeek;

@Remote
public interface WeeklyReportFacade {

	List<WeeklyReportDto> getAllWeeklyReportsAfter(Date date, String userUuid);
	
	List<WeeklyReportDto> getByUuids(List<String> uuids);
	
	WeeklyReportDto saveWeeklyReport(WeeklyReportDto dto);
	
	List<String> getAllUuids(String userUuid);
	
	WeeklyReportSummaryDto getSummaryDtoByRegion(RegionReferenceDto regionRef, EpiWeek epiWeek);
	
	WeeklyReportSummaryDto getSummaryDtoByDistrict(DistrictReferenceDto districtRef, EpiWeek epiWeek);
	
	int getNumberOfWeeklyReportsByFacility(FacilityReferenceDto facilityRef, EpiWeek epiWeek);
	
	List<WeeklyReportReferenceDto> getWeeklyReportsByFacility(FacilityReferenceDto facilityRef, EpiWeek epiWeek);
	
	List<WeeklyReportSummaryDto> getSummariesPerRegion(EpiWeek epiWeek);
	
	List<WeeklyReportSummaryDto> getSummariesPerDistrict(RegionReferenceDto region, EpiWeek epiWeek);
	
	WeeklyReportReferenceDto getByEpiWeekAndUser(EpiWeek epiWeek, UserReferenceDto userRef);
	
	WeeklyReportDto getByUuid(String uuid);
	
}
