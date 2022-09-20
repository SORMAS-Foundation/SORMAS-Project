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

import de.symeda.sormas.api.audit.AuditedClass;
import de.symeda.sormas.api.utils.PersonalData;

@AuditedClass
public class BirthDateDto implements Serializable {

	private static final long serialVersionUID = -905128183629450296L;

	public static final String DATE_OF_BIRTH_DD = "dateOfBirthDD";
	public static final String DATE_OF_BIRTH_MM = "dateOfBirthMM";
	public static final String DATE_OF_BIRTH_YYYY = "dateOfBirthYYYY";

	@PersonalData
	private Integer dateOfBirthDD;
	private Integer dateOfBirthMM;
	private Integer dateOfBirthYYYY;

	public BirthDateDto() {
	}

	public BirthDateDto(Integer dateOfBirthDD, Integer dateOfBirthMM, Integer dateOfBirthYYYY) {

		this.dateOfBirthDD = dateOfBirthDD;
		this.dateOfBirthMM = dateOfBirthMM;
		this.dateOfBirthYYYY = dateOfBirthYYYY;
	}

	public Integer getDateOfBirthDD() {
		return dateOfBirthDD;
	}

	public void setDateOfBirthDD(Integer dateOfBirthDD) {
		this.dateOfBirthDD = dateOfBirthDD;
	}

	public Integer getDateOfBirthMM() {
		return dateOfBirthMM;
	}

	public void setDateOfBirthMM(Integer dateOfBirthMM) {
		this.dateOfBirthMM = dateOfBirthMM;
	}

	public Integer getDateOfBirthYYYY() {
		return dateOfBirthYYYY;
	}

	public void setDateOfBirthYYYY(Integer dateOfBirthYYYY) {
		this.dateOfBirthYYYY = dateOfBirthYYYY;
	}
}
