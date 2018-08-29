package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationNoneOfCriteria extends ClassificationCriteria implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = 2199852259112272090L;
	
	protected final List<ClassificationCriteria> classificationCriteria;

	public ClassificationNoneOfCriteria(ClassificationCriteria... criteria) {
		this.classificationCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (ClassificationCriteria classificationCriteria : classificationCriteria) {
			if (classificationCriteria.eval(caze, sampleTests)) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<b>NONE OF</b>");
		for (int i = 0; i < classificationCriteria.size(); i++) {
			stringBuilder.append("<br/>- ");
			stringBuilder.append(classificationCriteria.get(i).buildDescription());
		}
		return stringBuilder.toString();
	}
	
	@Override
	public String getCriteriaName() {
		return "<b>NONE OF</b>";
	}
	
	@Override
	public List<ClassificationCriteria> getSubCriteria() {
		return classificationCriteria;
	}

}