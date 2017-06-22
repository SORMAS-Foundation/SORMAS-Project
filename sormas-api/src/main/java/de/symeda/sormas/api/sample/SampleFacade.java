package de.symeda.sormas.api.sample;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseReferenceDto;

@Remote
public interface SampleFacade {

	List<SampleDto> getAllSamplesAfter(Date date, String userUuid);
	
	List<SampleDto> getAllByCase(CaseReferenceDto caseRef);
	
	List<SampleIndexDto> getIndexList(String userUuid);
	
	List<SampleIndexDto> getIndexListByCase(CaseReferenceDto caseRef);
	
	SampleDto getSampleByUuid(String uuid);
	
	SampleDto saveSample(SampleDto dto);
	
	SampleReferenceDto getReferenceByUuid(String uuid);

	List<String> getAllUuids(String userUuid);

}
