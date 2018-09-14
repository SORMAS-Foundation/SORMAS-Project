package de.symeda.sormas.api.caze.classification;

import java.util.Arrays;
import java.util.List;

import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.person.PersonDto;
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
	public boolean eval(CaseDataDto caze, PersonDto person, List<SampleTestDto> sampleTests) {
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
		stringBuilder.append(I18nProperties.getText("onePositiveTestResult")).append(" ");
		for (int i = 0; i < sampleTestTypes.size(); i++) {
			if (i > 0) {
				if (i < sampleTestTypes.size() - 1) {
					stringBuilder.append(", ");
				} else {
					stringBuilder.append(" <b>").append(I18nProperties.getText("or").toUpperCase()).append("</b> ");
				}
			}

			stringBuilder.append(sampleTestTypes.get(i).toString());	
		}

		return stringBuilder.toString();
	}

}