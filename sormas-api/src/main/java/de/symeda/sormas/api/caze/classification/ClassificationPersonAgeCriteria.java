package de.symeda.sormas.api.caze.classification;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
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
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
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
		stringBuilder.append("person aged ");
		if (lowerThreshold != null && upperThreshold != null) {
			stringBuilder.append("between " + lowerThreshold + " and " + upperThreshold + " years");
		} else if (lowerThreshold != null) {
			stringBuilder.append(lowerThreshold + " years or more");
		} else if (upperThreshold != null) {
			stringBuilder.append(upperThreshold + " years or less");
		}

		return stringBuilder.toString();
	}
	
}
