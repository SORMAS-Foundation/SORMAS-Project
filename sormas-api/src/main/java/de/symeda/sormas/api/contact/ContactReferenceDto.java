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

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

public class ContactReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -7764607075875188799L;
	
	public ContactReferenceDto() {
		
	}
	
	public ContactReferenceDto(String uuid) {
		setUuid(uuid);
	}
	
	public ContactReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}
	
	public ContactReferenceDto(String uuid, String contactFirstName, String contactLastName, String caseFirstName, String caseLastName) {
		setUuid(uuid);
		setCaption(buildCaption(contactFirstName, contactLastName, caseFirstName, caseLastName));
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
}
