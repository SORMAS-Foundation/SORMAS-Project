package de.symeda.sormas.api.caze.classification;

import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;

public class ClassificationPathogenTestNegativeResultCriteriaDto extends ClassificationCriteriaDto {

	private static final long serialVersionUID = -1455979115306583281L;

	protected Disease testedDisease;

	public ClassificationPathogenTestNegativeResultCriteriaDto() {

	}

	public ClassificationPathogenTestNegativeResultCriteriaDto(Disease testedDisease) {
		this.testedDisease = testedDisease;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> pathogenTests) {

		for (PathogenTestDto pathogenTest : pathogenTests) {
			if (pathogenTest.getTestResult() == PathogenTestResultType.NEGATIVE) {
				if (pathogenTest.getTestedDisease() == testedDisease) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String buildDescription() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getString(Strings.classificationOneNegativeTestResult)).append(" ");

		if (testedDisease != null) {
			stringBuilder.append(" ").append(I18nProperties.getString(Strings.classificationForDisease)).append(" ").append(testedDisease.toString());
		}

		return stringBuilder.toString();
	}
}
