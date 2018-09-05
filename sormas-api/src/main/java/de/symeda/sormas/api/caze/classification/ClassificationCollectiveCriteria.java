package de.symeda.sormas.api.caze.classification;

import java.util.List;

public interface ClassificationCollectiveCriteria {

	public String getCriteriaName();
	public List<ClassificationCriteria> getSubCriteria();
	
}
