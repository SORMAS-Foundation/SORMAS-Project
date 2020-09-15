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
package de.symeda.sormas.api.sample;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

public class SampleReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -6975445672442728938L;

	public SampleReferenceDto() {

	}

	public SampleReferenceDto(String uuid) {
		setUuid(uuid);
	}

	public SampleReferenceDto(String uuid, String caption) {
		setUuid(uuid);
		setCaption(caption);
	}

	public SampleReferenceDto(String uuid, SampleMaterial sampleMaterial, String caseUuid, String contactUuid, String eventParticipantUuid) {
		setUuid(uuid);
		setCaption(buildCaption(sampleMaterial, caseUuid, contactUuid, eventParticipantUuid));
	}

	public static String buildCaption(SampleMaterial sampleMaterial, String caseUuid, String contactUuid, String eventParticipantUuid) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DataHelper.toStringNullable(sampleMaterial));
		if (stringBuilder.length() > 0) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(I18nProperties.getString(Strings.entitySample));
		if (caseUuid != null) {
			stringBuilder.append(StringUtils.wrap(I18nProperties.getString(Strings.forCase), " ")).append(DataHelper.getShortUuid(caseUuid));
		}
		if (contactUuid != null) {
			stringBuilder.append(StringUtils.wrap(I18nProperties.getString(Strings.forContact), " ")).append(DataHelper.getShortUuid(contactUuid));
		}
		if (eventParticipantUuid != null) {
			stringBuilder.append(StringUtils.wrap(I18nProperties.getString(Strings.forEventParticipant), " "))
				.append(DataHelper.getShortUuid(eventParticipantUuid));
		}
		return stringBuilder.toString();
	}
}
