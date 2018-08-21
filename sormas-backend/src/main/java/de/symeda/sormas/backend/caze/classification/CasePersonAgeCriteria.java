package de.symeda.sormas.backend.caze.classification;

import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class CasePersonAgeCriteria extends Criteria {

	protected final Integer lowerThreshold;
	protected final Integer upperThreshold;
	protected final ApproximateAgeType ageType;
	
	public CasePersonAgeCriteria(Integer lowerThreshold, Integer upperThreshold, ApproximateAgeType ageType) {
		this.lowerThreshold = lowerThreshold;
		this.upperThreshold = upperThreshold;
		this.ageType = ageType;
	}
	
	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		PersonDto person = FacadeProvider.getPersonFacade().getPersonByUuid(caze.getPerson().getUuid());
		if (person.getApproximateAge() == null) {
			return false;
		}
		
		if (person.getApproximateAgeType() == ageType) {
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
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append("person aged ");
		if (lowerThreshold != null && upperThreshold != null) {
			stringBuilder.append("between " + lowerThreshold + " and " + upperThreshold + " years");
		} else if (lowerThreshold != null) {
			stringBuilder.append(lowerThreshold + " years or more");
		} else if (upperThreshold != null) {
			stringBuilder.append(upperThreshold + " years or less");
		}

		return stringBuilder;
	}
	
}
