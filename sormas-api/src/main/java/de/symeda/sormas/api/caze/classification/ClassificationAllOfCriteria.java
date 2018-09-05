package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationAllOfCriteria extends ClassificationCriteria implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = -6427002056924376593L;
	
	protected final List<ClassificationCriteria> subCriteria;

	public ClassificationAllOfCriteria(ClassificationCriteria... criteria) {
		this.subCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (ClassificationCriteria classificationCriteria : subCriteria) {
			if (!classificationCriteria.eval(caze, sampleTests))
				return false;
		}
		
		return true;
	}

	@Override
	public String buildDescription() {
		return getCriteriaName();
	}
	
	@Override
	public String getCriteriaName() {
		return "<b>" + I18nProperties.getText("allOf").toUpperCase() + "</b>";
	}
	
	@Override
	public List<ClassificationCriteria> getSubCriteria() {
		return subCriteria;
	}
	
	public static class ClassificationAllOfCompactCriteria extends ClassificationAllOfCriteria implements ClassificationCompactCriteria {
		
		private static final long serialVersionUID = 3761118522728690578L;

		public ClassificationAllOfCompactCriteria(ClassificationCriteria... criteria) {
			super(criteria);
		}

		@Override
		public String buildDescription() {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < subCriteria.size(); i++) {
				if (i > 0) {
					if (i + 1 < subCriteria.size()) {
						stringBuilder.append(", ");
					} else {
						stringBuilder.append(" <b>").append(I18nProperties.getText("and").toUpperCase()).append("</b> ");
					}
				}
				
				stringBuilder.append(subCriteria.get(i).buildDescription());	
			}
			
			return stringBuilder.toString();
		}
	
	}
	
}