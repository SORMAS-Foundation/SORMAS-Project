package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface AdditionalTestFacade {

	AdditionalTestDto getByUuid(String uuid);

	List<AdditionalTestDto> getAllBySample(String sampleUuid);

	AdditionalTestDto saveAdditionalTest(@Valid AdditionalTestDto additionalTest);

	void deleteAdditionalTest(String additionalTestUuid);

	List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date);

	List<AdditionalTestDto> getAllActiveAdditionalTestsAfter(Date date, Integer batchSize, String lastSynchronizedUuid);

	List<AdditionalTestDto> getByUuids(List<String> uuids);

	List<String> getAllActiveUuids();

	Page<AdditionalTestDto> getIndexPage(
		AdditionalTestCriteria additionalTestCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);
}
