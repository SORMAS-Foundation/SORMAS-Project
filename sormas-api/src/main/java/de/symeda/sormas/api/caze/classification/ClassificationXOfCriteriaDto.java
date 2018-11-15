package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

/**
 * A criteria determining that a specific number of sub criteria need to be true in order for the whole criteria
 * to be applicable. The exact number is specified in the constructor.
 */
public class ClassificationXOfCriteriaDto extends ClassificationCriteriaDto implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = 1139711267145230378L;

	private int requiredAmount;
	protected List<ClassificationCriteriaDto> classificationCriteria;

	public ClassificationXOfCriteriaDto() {
		
	}
	
	public ClassificationXOfCriteriaDto(int requiredAmount, ClassificationCriteriaDto... criteria) {
		this.requiredAmount = requiredAmount;
		this.classificationCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
		int amount = 0;
		for (ClassificationCriteriaDto classificationCriteria : classificationCriteria) {
			if (classificationCriteria.eval(caze, person, sampleTests)) {
				amount++;
				if (amount >= requiredAmount) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String buildDescription() {
		return getCriteriaName();
	}

	@Override
	public String getCriteriaName() {
		return "<b>" + parseAmountNumber() + " " + I18nProperties.getText("of").toUpperCase() + "</b>";
	}

	@Override
	public List<ClassificationCriteriaDto> getSubCriteria() {
		return classificationCriteria;
	}

	protected String parseAmountNumber() {
		switch (requiredAmount) {
		case 1:
			return I18nProperties.getText("one").toUpperCase();
		case 2:
			return I18nProperties.getText("two").toUpperCase();
		case 3:
			return I18nProperties.getText("three").toUpperCase();
		case 4:
			return I18nProperties.getText("four").toUpperCase();
		case 5:
			return I18nProperties.getText("five").toUpperCase();
		case 6:
			return I18nProperties.getText("six").toUpperCase();
		case 7:
			return I18nProperties.getText("seven").toUpperCase();
		case 8:
			return I18nProperties.getText("eight").toUpperCase();
		case 9:
			return I18nProperties.getText("nine").toUpperCase();
		case 10:
			return I18nProperties.getText("ten").toUpperCase();
		case 11:
			return I18nProperties.getText("eleven").toUpperCase();
		case 12:
			return I18nProperties.getText("twelve").toUpperCase();
		default:
			return Integer.toString(requiredAmount);
		}
	}

	public int getRequiredAmount() {
		return requiredAmount;
	}

	public void setRequiredAmount(int requiredAmount) {
		this.requiredAmount = requiredAmount;
	}

	public List<ClassificationCriteriaDto> getClassificationCriteria() {
		return classificationCriteria;
	}

	public void setClassificationCriteria(List<ClassificationCriteriaDto> classificationCriteria) {
		this.classificationCriteria = classificationCriteria;
	}

	/**
	 * Has a different buildDescription method to display all sub criteria with bullet points.
	 * Functionality is identical to ClassificationXOfCriteria.
	 */
	public static class ClassificationXOfSubCriteriaDto extends ClassificationXOfCriteriaDto {

		private static final long serialVersionUID = 8374870595895910414L;

		public ClassificationXOfSubCriteriaDto() {
			super();
		}
		
		public ClassificationXOfSubCriteriaDto(int requiredAmount, ClassificationCriteriaDto... criteria) {
			super(requiredAmount, criteria);
		}

		@Override
		public String buildDescription() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<b> ").append(I18nProperties.getText("oneOf").toUpperCase()).append("</b>");
			for (int i = 0; i < classificationCriteria.size(); i++) {
				stringBuilder.append("<br/>- ");
				stringBuilder.append(classificationCriteria.get(i).buildDescription());	
			}
			
			return stringBuilder.toString();
		}
		
	}
	
	/**
	 * Has a different buildDescription method to display all sub criteria in one line, separated by commas and
	 * an "OR" for the last criteria. Functionality is identical to ClassificationXOfCriteria.
	 */
	public static class ClassificationOneOfCompactCriteriaDto extends ClassificationXOfCriteriaDto implements ClassificationCompactCriteria {

		private static final long serialVersionUID = 8374870595895910414L;

		public ClassificationOneOfCompactCriteriaDto() {
			super();
		}
		
		public ClassificationOneOfCompactCriteriaDto(ClassificationCriteriaDto... criteria) {
			super(1, criteria);
		}

		@Override
		public String buildDescription() {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < classificationCriteria.size(); i++) {
				if (i > 0) {
					if (i + 1 < classificationCriteria.size()) {
						stringBuilder.append(", ");
					} else {
						stringBuilder.append(" <b>").append( I18nProperties.getText("or").toUpperCase()).append("</b> ");
					}
				}
				
				stringBuilder.append(classificationCriteria.get(i).buildDescription());	
			}
			
			return stringBuilder.toString();
		}
		
	}

}
