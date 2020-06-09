/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze.classification;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.ApproximateAgeType.ApproximateAgeHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;

public class ClassificationPersonAgeBetweenYearsCriteriaDto extends ClassificationCriteriaDto {

	private static final long serialVersionUID = 7306888279187764644L;

	protected Integer lowerYearsThreshold;
	protected Integer upperYearsThreshold;

	public ClassificationPersonAgeBetweenYearsCriteriaDto() {

	}

	public ClassificationPersonAgeBetweenYearsCriteriaDto(Integer lowerYearsThreshold, Integer upperYearsThreshold) {
		this.lowerYearsThreshold = lowerYearsThreshold;
		this.upperYearsThreshold = upperYearsThreshold;
	}

	@Override
	public boolean eval(CaseDataDto caze, PersonDto person, List<PathogenTestDto> sampleTests) {
		Integer approximateAge = ApproximateAgeHelper.getAgeYears(person.getApproximateAge(), person.getApproximateAgeType());
		if (approximateAge == null) {
			return false;
		}

		if (lowerYearsThreshold != null && approximateAge < lowerYearsThreshold) {
			return false;
		}
		if (upperYearsThreshold != null && approximateAge > upperYearsThreshold) {
			return false;
		}

		return true;
	}

	@Override
	public String buildDescription() {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(I18nProperties.getString(Strings.classificationPersonAged)).append(" ");
		if (lowerYearsThreshold != null && upperYearsThreshold != null) {
			stringBuilder.append(I18nProperties.getString(Strings.between))
				.append(" ")
				.append(lowerYearsThreshold)
				.append(" ")
				.append(I18nProperties.getString(Strings.and))
				.append(" ")
				.append(upperYearsThreshold)
				.append(" ")
				.append(I18nProperties.getString(Strings.years));
		} else if (lowerYearsThreshold != null) {
			stringBuilder.append(lowerYearsThreshold).append(" ").append(I18nProperties.getString(Strings.classificationYearsOrMore));
		} else if (upperYearsThreshold != null) {
			stringBuilder.append(upperYearsThreshold).append(" ").append(I18nProperties.getString(Strings.classificationYearsOrLess));
		}

		return stringBuilder.toString();
	}

	public Integer getLowerThreshold() {
		return lowerYearsThreshold;
	}

	public void setLowerThreshold(Integer lowerThreshold) {
		this.lowerYearsThreshold = lowerThreshold;
	}

	public Integer getUpperThreshold() {
		return upperYearsThreshold;
	}

	public void setUpperThreshold(Integer upperThreshold) {
		this.upperYearsThreshold = upperThreshold;
	}
}
