/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.environment.environmentsample;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

public class EnvironmentSampleReferenceDto extends ReferenceDto {

	private static final long serialVersionUID = -2590392329041969693L;

	public EnvironmentSampleReferenceDto(String uuid, EnvironmentSampleMaterial sampleMaterial, String environmentUuid) {
		super(uuid, buildCaption(sampleMaterial, environmentUuid));
	}

	private static String buildCaption(EnvironmentSampleMaterial sampleMaterial, String environmentUuid) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DataHelper.toStringNullable(sampleMaterial));
		if (stringBuilder.length() > 0) {
			stringBuilder.append(" ");
		}
		stringBuilder.append(I18nProperties.getString(Strings.entityEnvironmentSample).toLowerCase())
			.append(StringUtils.wrap(I18nProperties.getString(Strings.forEnvironment), " "))
			.append("(")
			.append(DataHelper.getShortUuid(environmentUuid))
			.append(")");

		return stringBuilder.toString();
	}
}
