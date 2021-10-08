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
package de.symeda.sormas.api.user;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

public class UserReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -8558187171374254398L;

	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	private String firstName;
	@PersonalData(mandatoryField = true)
	@SensitiveData(mandatoryField = true)
	private String lastName;

	public UserReferenceDto() {
	}

	public UserReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public UserReferenceDto(String uuid, String firstName, String lastName, Set<UserRole> userRoles) {
		setUuid(uuid);
		setCaption(buildCaption(firstName, lastName, userRoles));
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public static String buildCaption(String firstName, String lastName, Set<UserRole> userRoles) {

		StringBuilder result = new StringBuilder();
		result.append(DataHelper.toStringNullable(firstName)).append(" ").append(DataHelper.toStringNullable(lastName).toUpperCase());
		boolean first = true;
		if (userRoles != null) {
			for (UserRole userRole : userRoles) {
				if (first) {
					result.append(" - ");
					first = false;
				} else {
					result.append(", ");
				}
				result.append(userRole.toString());
			}
		}
		return result.toString();
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	@JsonIgnore
	public String getShortCaption() {
		return buildCaption(firstName, lastName, null);
	}
}
