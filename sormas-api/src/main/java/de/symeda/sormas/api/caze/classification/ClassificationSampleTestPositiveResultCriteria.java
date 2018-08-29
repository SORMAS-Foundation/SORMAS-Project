package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.sample.SampleTestDto;
import de.symeda.sormas.api.sample.SampleTestResultType;
import de.symeda.sormas.api.sample.SampleTestType;

public class ClassificationSampleTestPositiveResultCriteria extends ClassificationCriteria {

	private static final long serialVersionUID = 3811127784970509183L;
	
	protected final List<SampleTestType> sampleTestTypes;

	public ClassificationSampleTestPositiveResultCriteria(SampleTestType... sampleTestTypes) {
		this.sampleTestTypes = Arrays.asList(sampleTestTypes);
	}

	@Override
	public boolean eval(CaseDataDto caze, List<SampleTestDto> sampleTests) {
		for (SampleTestDto sampleTest : sampleTests) {
			if (sampleTest.getTestResult() == SampleTestResultType.POSITIVE
					&& sampleTestTypes.contains(sampleTest.getTestType())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String buildDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("One positive lab result of ");
		for (int i = 0; i < sampleTestTypes.size(); i++) {
			if (i > 0) {
				if (i < sampleTestTypes.size() - 1) {
					stringBuilder.append(", ");
				} else {
					stringBuilder.append(" <b>OR</b> ");
				}
			}

			stringBuilder.append(sampleTestTypes.get(i).toString());	
		}

		return stringBuilder.toString();
	}

}