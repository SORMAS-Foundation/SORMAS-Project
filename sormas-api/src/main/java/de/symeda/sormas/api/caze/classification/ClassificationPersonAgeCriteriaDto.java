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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.caze.classification;

import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.ApproximateAgeType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.SampleTestDto;

public class ClassificationPersonAgeCriteriaDto extends ClassificationCriteriaDto {
	
	private static final long serialVersionUID = 7306888279187764644L;
	
	protected Integer lowerThreshold;
	protected Integer upperThreshold;
	protected ApproximateAgeType ageType;
	
	public ClassificationPersonAgeCriteriaDto() {
		
	}
	
	public ClassificationPersonAgeCriteriaDto(Integer lowerThreshold, Integer upperThreshold, ApproximateAgeType ageType) {
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
		stringBuilder.append(I18nProperties.getString(Strings.personAged)).append(" ");
		if (lowerThreshold != null && upperThreshold != null) {
			stringBuilder.append(I18nProperties.getString(Strings.between))
					.append(" ").append(lowerThreshold).append(" ")
					.append(I18nProperties.getString(Strings.and)).append(" ")
					.append(upperThreshold).append(" ").append(I18nProperties.getString(Strings.years));
		} else if (lowerThreshold != null) {
			stringBuilder.append(lowerThreshold).append(" ").append(I18nProperties.getString(Strings.yearsOrMore));
		} else if (upperThreshold != null) {
			stringBuilder.append(upperThreshold).append(" ").append(I18nProperties.getString(Strings.yearsOrLess));
		}

		return stringBuilder.toString();
	}

	public Integer getLowerThreshold() {
		return lowerThreshold;
	}

	public void setLowerThreshold(Integer lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
	}

	public Integer getUpperThreshold() {
		return upperThreshold;
	}

	public void setUpperThreshold(Integer upperThreshold) {
		this.upperThreshold = upperThreshold;
	}

	public ApproximateAgeType getAgeType() {
		return ageType;
	}

	public void setAgeType(ApproximateAgeType ageType) {
		this.ageType = ageType;
	}
	
}
