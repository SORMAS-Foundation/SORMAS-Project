/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.disease;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.person.ApproximateAgeType;

public class DiseaseConfigurationAgeGroup implements Serializable {

	private static final long serialVersionUID = 4603371332829942453L;

	@Min(value = 1, message = Validations.numberTooSmall)
	private Integer startAge;
	@NotBlank(message = Validations.required)
	private ApproximateAgeType startAgeType;
	@Min(value = 1, message = Validations.numberTooSmall)
	private Integer endAge;
	@NotBlank(message = Validations.required)
	private ApproximateAgeType endAgeType;

	public DiseaseConfigurationAgeGroup() {
	}

	public DiseaseConfigurationAgeGroup(Integer startAge, ApproximateAgeType startAgeType, Integer endAge, ApproximateAgeType endAgeType) {
		this.startAge = startAge;
		this.startAgeType = startAgeType;
		this.endAge = endAge;
		this.endAgeType = endAgeType;
	}

	public Integer getStartAge() {
		return startAge;
	}

	public void setStartAge(Integer startAge) {
		this.startAge = startAge;
	}

	public ApproximateAgeType getStartAgeType() {
		return startAgeType;
	}

	public void setStartAgeType(ApproximateAgeType startAgeType) {
		this.startAgeType = startAgeType;
	}

	public Integer getEndAge() {
		return endAge;
	}

	public void setEndAge(Integer endAge) {
		this.endAge = endAge;
	}

	public ApproximateAgeType getEndAgeType() {
		return endAgeType;
	}

	public void setEndAgeType(ApproximateAgeType endAgeType) {
		this.endAgeType = endAgeType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass())
			return false;
		DiseaseConfigurationAgeGroup that = (DiseaseConfigurationAgeGroup) o;
		return startAge == that.startAge && endAge == that.endAge && startAgeType == that.startAgeType && endAgeType == that.endAgeType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(startAge, startAgeType, endAge, endAgeType);
	}
}
