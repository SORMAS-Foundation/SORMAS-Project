package de.symeda.sormas.api.caze.classification;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

@Remote
public interface CaseClassificationFacade {
	
	CaseClassification getClassification(CaseDataDto caze, List<SampleTestDto> sampleTests);
	
	ClassificationCriteriaDto getSuspectCriteria(Disease disease);

	ClassificationCriteriaDto getProbableCriteria(Disease disease);
	
	ClassificationCriteriaDto getConfirmedCriteria(Disease disease);
	
	List<DiseaseClassificationCriteriaDto> getAllClassificationCriteria();
	
	DiseaseClassificationCriteriaDto getClassificationCriteriaForDisease(Disease disease);
	
}
