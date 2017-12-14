package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.region.DistrictReferenceDto;

@Remote
public interface SampleTestFacade {

	List<SampleTestDto> getAllAfter(Date date, String userUuid);
	
	List<SampleTestDto> getAllBySample(SampleReferenceDto sampleRef);
	
	SampleTestDto getLatestBySample(SampleReferenceDto sampleRef);
	
	SampleTestDto getByUuid(String uuid);
	
	SampleTestDto saveSampleTest(SampleTestDto dto);

	List<String> getAllUuids(String userUuid);

	List<SampleTestDto> getByUuids(List<String> uuids);
	
	List<DashboardTestResultDto> getNewTestResultsForDashboard(DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	void deleteSampleTest(SampleTestReferenceDto sampleTestRef, String userUuid);
}
