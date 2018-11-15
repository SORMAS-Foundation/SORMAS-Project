package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationAllOfCriteriaDto extends ClassificationCriteriaDto implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = -6427002056924376593L;
	
	protected List<ClassificationCriteriaDto> subCriteria;

	public ClassificationAllOfCriteriaDto() {
		
	}
	
	public ClassificationAllOfCriteriaDto(ClassificationCriteriaDto... criteria) {
		this.subCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
		for (ClassificationCriteriaDto classificationCriteriaDto : subCriteria) {
			if (!classificationCriteriaDto.eval(caze, person, sampleTests))
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
	public List<ClassificationCriteriaDto> getSubCriteria() {
		return subCriteria;
	}
	
	/**
	 * Has a different buildDescription method to display all sub criteria in one line, with the sub criteria separated
	 * by an "AND". Functionality is identical to ClassificationAllOfCriteria.
	 */
	public static class ClassificationAllOfCompactCriteriaDto extends ClassificationAllOfCriteriaDto implements ClassificationCompactCriteria {
		
		private static final long serialVersionUID = 3761118522728690578L;

		public ClassificationAllOfCompactCriteriaDto(ClassificationCriteriaDto... criteria) {
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

	public void setSubCriteria(List<ClassificationCriteriaDto> subCriteria) {
		this.subCriteria = subCriteria;
	}
	
}