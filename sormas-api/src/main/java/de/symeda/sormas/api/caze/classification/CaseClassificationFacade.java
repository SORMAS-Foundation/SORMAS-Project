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
	
	ClassificationCriteria getSuspectCriteria(Disease disease);

	ClassificationCriteria getProbableCriteria(Disease disease);
	
	ClassificationCriteria getConfirmedCriteria(Disease disease);
	
	List<DiseaseClassificationCriteria> getAllClassificationCriteria();
	
	DiseaseClassificationCriteria getClassificationCriteriaForDisease(Disease disease);
	
}
