package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;

@Remote
public interface SampleFacade {

	List<SampleDto> getAllActiveSamplesAfter(Date date, String userUuid);
	
	List<SampleDto> getAllByCase(CaseReferenceDto caseRef);
	
	int getReceivedSampleCountByCase(CaseReferenceDto caseRef);
	
	List<SampleIndexDto> getIndexList(String userUuid, SampleCriteria sampleCriteria);
	
	SampleDto getSampleByUuid(String uuid);
	
	SampleDto saveSample(SampleDto dto);
	
	SampleReferenceDto getReferenceByUuid(String uuid);
	
	SampleReferenceDto getReferredFrom(String sampleUuid);

	List<String> getAllActiveUuids(String userUuid);

	List<SampleDto> getByUuids(List<String> uuids);
	
	List<DashboardSampleDto> getNewSamplesForDashboard(RegionReferenceDto regionRef, DistrictReferenceDto districtRef, Disease disease, Date from, Date to, String userUuid);
	
	void deleteSample(SampleReferenceDto sampleRef, String userUuid);

}
