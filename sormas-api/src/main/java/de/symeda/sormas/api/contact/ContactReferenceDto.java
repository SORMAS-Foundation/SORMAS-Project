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
package de.symeda.sormas.api.contact;

import de.symeda.sormas.api.caze.CaseJurisdictionDto;
import de.symeda.sormas.api.utils.PersonalData;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

import java.io.Serializable;

public class ContactReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;

	private PersonName contactPersonName;
	private PersonName casePersonName;
	private ContactJurisdictionDto contactJurisdiction;

	public ContactReferenceDto() {

	}

	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public ContactReferenceDto(String uuid, String contactFirstName, String contactLastName,
							   String caseFirstName, String caseLastName,
							   ContactJurisdictionDto contactJurisdiction) {
		setUuid(uuid);
		contactPersonName = new PersonName(contactFirstName, contactLastName);
		casePersonName = new PersonName(caseFirstName, caseLastName);

		this.contactJurisdiction = contactJurisdiction;
	}

	@Override
	public String getCaption() {
		return buildCaption(contactPersonName.firstName, contactPersonName.lastName, casePersonName.firstName, casePersonName.lastName);
	}

	public PersonName getContactPersonName() {
		return contactPersonName;
	}

	public PersonName getCasePersonName() {
		return casePersonName;
	}

	public ContactJurisdictionDto getContactJurisdiction() {
		return contactJurisdiction;
	}

	public static String buildCaption(String contactFirstName, String contactLastName, String caseFirstName, String caseLastName) {
		StringBuilder builder = new StringBuilder();
		builder.append(DataHelper.toStringNullable(contactFirstName))
			.append(" ").append(DataHelper.toStringNullable(contactLastName).toUpperCase());

		if (caseFirstName != null || caseLastName != null) {
			builder.append(StringUtils.wrap(I18nProperties.getString(Strings.toCase), ""))
			.append(DataHelper.toStringNullable(caseFirstName))
			.append(" ").append(DataHelper.toStringNullable(caseLastName));
		}

		return builder.toString();
	}

	public static class PersonName implements Serializable {
		private static final long serialVersionUID = -6077306945043270617L;

		@PersonalData
		private String firstName;
		@PersonalData
		private String lastName;

		public PersonName(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}
	}
}
