package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

/**
 * Classification criteria that specifies that none of the sub criteria may be true in order for the whole
 * criteria to be applicable. This is used e.g. to make sure that certain sample test types have returned
 * a negative result in order to rule out specific diseases.
 */
public class ClassificationNoneOfCriteria extends ClassificationCriteria implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = 2199852259112272090L;
	
	protected final List<ClassificationCriteria> classificationCriteria;

	public ClassificationNoneOfCriteria(ClassificationCriteria... criteria) {
		this.classificationCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
		for (ClassificationCriteria classificationCriteria : classificationCriteria) {
			if (classificationCriteria.eval(caze, person, sampleTests)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<b> ").append(I18nProperties.getText("noneOf").toUpperCase()).append("</b>");
		for (int i = 0; i < classificationCriteria.size(); i++) {
			stringBuilder.append("<br/>- ");
			stringBuilder.append(classificationCriteria.get(i).buildDescription());
		}
		return stringBuilder.toString();
	}
	
	@Override
	public String getCriteriaName() {
		return "<b>" + I18nProperties.getText("noneOf").toUpperCase() + "</b>";
	}
	
	@Override
	public List<ClassificationCriteria> getSubCriteria() {
		return classificationCriteria;
	}

}