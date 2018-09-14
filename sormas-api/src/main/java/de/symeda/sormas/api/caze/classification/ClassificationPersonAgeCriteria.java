package de.symeda.sormas.api.caze.classification;

import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationPersonAgeCriteria extends ClassificationCriteria {

	private static final long serialVersionUID = -3069692968632918398L;
	
	protected final Integer lowerThreshold;
	protected final Integer upperThreshold;
	protected final ApproximateAgeType ageType;
	
	public ClassificationPersonAgeCriteria(Integer lowerThreshold, Integer upperThreshold, ApproximateAgeType ageType) {
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.ageType = ageType;
	}
	
	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
		if (person.getApproximateAge() == null) {
			return false;
		}
		
		if (person.getApproximateAgeType() == ageType || person.getApproximateAgeType() == null) {
			if (lowerThreshold != null && person.getApproximateAge() < lowerThreshold) {
				return false;
			}
			if (upperThreshold != null && person.getApproximateAge() > upperThreshold) {
				return false;
			}
			return true;
		} else {
			if (ageType == ApproximateAgeType.MONTHS && person.getApproximateAge() == 0) {
				return true;
			}
			return false;
		}
	}
	
	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getText("personAged")).append(" ");
		if (lowerThreshold != null && upperThreshold != null) {
			stringBuilder.append(I18nProperties.getText("between"))
					.append(" ").append(lowerThreshold).append(" ")
					.append(I18nProperties.getText("and")).append(" ")
					.append(upperThreshold).append(" ").append(I18nProperties.getText("years"));
		} else if (lowerThreshold != null) {
			stringBuilder.append(lowerThreshold).append(" ").append(I18nProperties.getText("yearsOrMore"));
		} else if (upperThreshold != null) {
			stringBuilder.append(upperThreshold).append(" ").append(I18nProperties.getText("yearsOrLess"));
		}

		return stringBuilder.toString();
	}
	
}
