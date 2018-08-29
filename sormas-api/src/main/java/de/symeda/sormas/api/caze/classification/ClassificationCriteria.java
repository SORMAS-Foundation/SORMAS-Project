package de.symeda.sormas.api.caze.classification;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public abstract class ClassificationCriteria implements Serializable {
	
	private static final long serialVersionUID = -3686569295881034008L;
	
	public abstract boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests);
	public abstract String buildDescription();
	
}