/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.contact;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.EmbeddedPersonalData;
import de.symeda.sormas.api.utils.HasCaption;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;

@DependingOnFeatureType(featureType = FeatureType.CONTACT_TRACING)
public class ContactReferenceDto extends ReferenceDto implements IsContact {

	private static final long serialVersionUID = -7764607075875188799L;

	@EmbeddedPersonalData
	private PersonName contactName;
	private CaseReferenceDto caze;

	public ContactReferenceDto() {

	}

	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public ContactReferenceDto(String uuid, String contactFirstName, String contactLastName, CaseReferenceDto caze) {

		setUuid(uuid);
		this.contactName = new PersonName(contactFirstName, contactLastName);
		this.caze = caze;
	}

	@Override
	public String getCaption() {
		return buildCaption(
			contactName.firstName,
			contactName.lastName,
			caze != null ? caze.getFirstName() : null,
			caze != null ? caze.getLastName() : null,
			getUuid(),
			true);
	}

	@JsonIgnore
	public String getCaptionAlwaysWithUuid() {
		return buildCaption(
			contactName.firstName,
			contactName.lastName,
			caze != null ? caze.getFirstName() : null,
			caze != null ? caze.getLastName() : null,
			getUuid(),
			true);
	}

	public PersonName getContactName() {
		return contactName;
	}

	public CaseReferenceDto getCaze() {
		return caze;
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

	public static class PersonName implements Serializable, HasCaption {

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

		public String buildCaption() {
			return firstName + " " + lastName;
		}
	}

	public static CaseReferenceDto getCazeNullable(ContactReferenceDto contactReference) {
		return contactReference != null ? contactReference.getCaze() : null;
	}
}
