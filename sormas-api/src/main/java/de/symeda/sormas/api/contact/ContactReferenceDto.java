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
package de.symeda.sormas.api.contact;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.EmbeddedSensitiveData;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

public class ContactReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;

	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private PersonName contactName;
	@EmbeddedPersonalData
	@EmbeddedSensitiveData
	private PersonName caseName;

	public ContactReferenceDto() {

	}

	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public ContactReferenceDto(String uuid, String contactFirstName, String contactLastName, String caseFirstName, String caseLastName) {

		setUuid(uuid);
		this.contactName = new PersonName(contactFirstName, contactLastName);

		if (caseFirstName != null && caseLastName != null) {
			this.caseName = new PersonName(caseFirstName, caseLastName);
		}
	}

	@Override
	public String getCaption() {
		return buildCaption(
			contactName.firstName,
			contactName.lastName,
			caseName != null ? caseName.firstName : null,
			caseName != null ? caseName.lastName : null,
			getUuid());
	}

	public String getCaptionAlwaysWithUuid() {
		return buildCaption(
			contactName.firstName,
			contactName.lastName,
			caseName != null ? caseName.firstName : null,
			caseName != null ? caseName.lastName : null,
			getUuid(),
			true);
	}

	public PersonName getContactName() {
		return contactName;
	}

	public PersonName getCaseName() {
		return caseName;
	}

	public static String buildCaption(
		String contactFirstName,
		String contactLastName,
		String caseFirstName,
		String caseLastName,
		String contactUuid) {
		return buildCaption(contactFirstName, contactLastName, caseFirstName, caseLastName, contactUuid, false);
	}

	public static String buildCaption(
		String contactFirstName,
		String contactLastName,
		String caseFirstName,
		String caseLastName,
		String contactUuid,
		boolean alwaysShowUuid) {

		StringBuilder builder = new StringBuilder();
		if (!DataHelper.isNullOrEmpty(contactFirstName) || !DataHelper.isNullOrEmpty(contactLastName)) {
			builder.append(DataHelper.toStringNullable(contactFirstName))
				.append(" ")
				.append(DataHelper.toStringNullable(contactLastName).toUpperCase());
		}

		if (!DataHelper.isNullOrEmpty(caseFirstName) || !DataHelper.isNullOrEmpty(caseLastName)) {
			builder.append(StringUtils.wrap(I18nProperties.getString(Strings.toCase), " "))
				.append(DataHelper.toStringNullable(caseFirstName))
				.append(" ")
				.append(DataHelper.toStringNullable(caseLastName));
		}

		if (alwaysShowUuid || builder.length() == 0) {
			builder.append(builder.length() > 0 ? " (" + DataHelper.getShortUuid(contactUuid) + ")" : DataHelper.getShortUuid(contactUuid));
		}

		return builder.toString();
	}

	public static class PersonName implements Serializable {

		private static final long serialVersionUID = 3655299579771996044L;

		@PersonalData
		@SensitiveData
		private String firstName;
		@PersonalData
		@SensitiveData
		private String lastName;

		public PersonName() {
		}

		public PersonName(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}

		public String getFirstName() {
			return firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public String toString() {
			return firstName + " " + lastName;
		}
	}
}
