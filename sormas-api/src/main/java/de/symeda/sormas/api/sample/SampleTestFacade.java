package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface SampleTestFacade {

	List<SampleTestDto> getAllSampleTestsAfter(Date date, String userUuid);
	
	List<SampleTestDto> getAllBySample(SampleReferenceDto sampleRef);
	
	SampleTestDto getByUuid(String uuid);
	
	SampleTestDto saveSampleTest(SampleTestDto dto);

	List<String> getAllUuids(String userUuid);
}
