package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface AdditionalTestFacade {
	
	AdditionalTestDto getByUuid(String uuid);
	
	List<AdditionalTestDto> getAllBySample(String sampleUuid);
	
	AdditionalTestDto saveAdditionalTest(AdditionalTestDto additionalTest);
	
	void deleteAdditionalTest(String additionalTestUuid, String userUuid);

	List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date, String userUuid);

	List<AdditionalTestDto> getByUuids(List<String> uuids);

	List<String> getAllActiveUuids(String userUuid);
	
}
