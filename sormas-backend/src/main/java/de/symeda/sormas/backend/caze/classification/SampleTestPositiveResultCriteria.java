package de.symeda.sormas.backend.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;

public class SampleTestPositiveResultCriteria extends Criteria {

	protected final List<SampleTestType> sampleTestTypes;

	public SampleTestPositiveResultCriteria(SampleTestType... sampleTestTypes) {
		this.sampleTestTypes = Arrays.asList(sampleTestTypes);
	}

	@Override
	boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (SampleTestDto sampleTest : sampleTests) {
			if (sampleTest.getTestResult() == SampleTestResultType.POSITIVE
					&& sampleTestTypes.contains(sampleTest.getTestType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	StringBuilder appendDesc(StringBuilder stringBuilder) {
		stringBuilder.append("one positive lab result of: ");
		for (int i=0; i<sampleTestTypes.size(); i++) {
			if (i > 0) stringBuilder.append(", ");
			stringBuilder.append(sampleTestTypes.get(i).toString());	
		}

		return stringBuilder;
	}
	
}