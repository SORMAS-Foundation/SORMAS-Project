/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.caze;

import java.io.Serializable;

import de.symeda.sormas.api.audit.AuditInclude;
import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.person.ApproximateAgeType;

@AuditedClass
public class AgeAndBirthDateDto extends BirthDateDto implements Serializable {

	private static final long serialVersionUID = -3544971830146580773L;

	private Integer age;
	@AuditInclude
	private ApproximateAgeType ageType;

	public AgeAndBirthDateDto(Integer age, ApproximateAgeType ageType, Integer birthdateDD, Integer birthdateMM, Integer birthdateYYYY) {

		super(birthdateDD, birthdateMM, birthdateYYYY);

		this.age = age;
		this.ageType = ageType;
	}

	public Integer getAge() {
		return age;
	}

	public ApproximateAgeType getAgeType() {
		return ageType;
	}
}
