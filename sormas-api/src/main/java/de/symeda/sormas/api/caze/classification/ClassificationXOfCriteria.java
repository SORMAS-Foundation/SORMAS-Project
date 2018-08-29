package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationXOfCriteria extends ClassificationCriteria implements ClassificationCollectiveCriteria {

	private static final long serialVersionUID = 1139711267145230378L;

	private final int requiredAmount;
	protected final List<ClassificationCriteria> classificationCriteria;

	public ClassificationXOfCriteria(int requiredAmount, ClassificationCriteria... criteria) {
		this.requiredAmount = requiredAmount;
		this.classificationCriteria = Arrays.asList(criteria);
	}

	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		int amount = 0;
		for (ClassificationCriteria classificationCriteria : classificationCriteria) {
			if (classificationCriteria.eval(caze, sampleTests)) {
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
		return "<b>" + parseAmountNumber() + " OF</b>";
	}

	@Override
	public List<ClassificationCriteria> getSubCriteria() {
		return classificationCriteria;
	}

	protected String parseAmountNumber() {
		switch (requiredAmount) {
		case 1:
			return "ONE";
		case 2:
			return "TWO";
		case 3:
			return "THREE";
		case 4:
			return "FOUR";
		case 5:
			return "FIVE";
		case 6:
			return "SIX";
		case 7:
			return "SEVEN";
		case 8:
			return "EIGHT";
		case 9:
			return "NINE";
		case 10:
			return "TEN";
		case 11:
			return "ELEVEN";
		case 12:
			return "TWELVE";
		default:
			return Integer.toString(requiredAmount);
		}
	}

	public static class ClassificationXOfSubCriteria extends ClassificationXOfCriteria {

		private static final long serialVersionUID = 8374870595895910414L;

		public ClassificationXOfSubCriteria(int requiredAmount, ClassificationCriteria... criteria) {
			super(requiredAmount, criteria);
		}

		@Override
		public String buildDescription() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<b> ONE OF </b>");
			for (int i = 0; i < classificationCriteria.size(); i++) {
				stringBuilder.append("<br/>- ");
				stringBuilder.append(classificationCriteria.get(i).buildDescription());	
			}
			
			return stringBuilder.toString();
		}
		
	}
	
	public static class ClassificationOneOfCompactCriteria extends ClassificationXOfCriteria implements ClassificationCompactCriteria {

		private static final long serialVersionUID = 8374870595895910414L;

		public ClassificationOneOfCompactCriteria(ClassificationCriteria... criteria) {
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
						stringBuilder.append(" <b>OR</b> ");
					}
				}
				
				stringBuilder.append(classificationCriteria.get(i).buildDescription());	
			}
			
			return stringBuilder.toString();
		}
		
	}

}
