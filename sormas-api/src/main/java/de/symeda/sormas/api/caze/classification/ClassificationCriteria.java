package de.symeda.sormas.api.caze.classification;

import java.io.Serializable;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

/**
 * Classification criteria are used to automatically classify cases and to be able to display
 * the underlying rules in the UI. A specific criteria can be evaluated using the eval method
 * which returns whether this criteria is applicable or not for the given case. A set of
 * criteria can be evaluated in order to determine whether the case should be classified as
 * suspect, probable, confirmed or not classified at all.
 */
public abstract class ClassificationCriteria implements Serializable {
	
	private static final long serialVersionUID = -3686569295881034008L;
	
	public abstract boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests);
	public abstract String buildDescription();
	
}