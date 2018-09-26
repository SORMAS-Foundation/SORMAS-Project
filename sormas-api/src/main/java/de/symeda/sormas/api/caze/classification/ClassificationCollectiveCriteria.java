package de.symeda.sormas.api.caze.classification;

import java.util.List;

/**
 * Used for classification criteria that contain a set of other criteria, either
 * because all or a specific number of them need to be applicable.
 */
public interface ClassificationCollectiveCriteria {

	public String getCriteriaName();
	public List<ClassificationCriteria> getSubCriteria();
	
}
