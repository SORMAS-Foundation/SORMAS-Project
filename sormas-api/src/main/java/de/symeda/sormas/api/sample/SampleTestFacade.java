package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

@Remote
public interface SampleTestFacade {

	List<SampleTestDto> getAllActiveSampleTestsAfter(Date date, String userUuid);
	
	List<SampleTestDto> getAllBySample(SampleReferenceDto sampleRef);
	
	SampleTestDto getByUuid(String uuid);
	
	SampleTestDto saveSampleTest(SampleTestDto dto);

	List<String> getAllActiveUuids(String userUuid);

	List<SampleTestDto> getByUuids(List<String> uuids);
	
	List<DashboardTestResultDto> getNewTestResultsForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	void deleteSampleTest(SampleTestReferenceDto sampleTestRef, String userUuid);
}
