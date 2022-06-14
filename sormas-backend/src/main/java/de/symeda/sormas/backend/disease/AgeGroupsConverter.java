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

package de.symeda.sormas.backend.disease;

import static de.symeda.sormas.api.utils.AgeGroupUtils.convertToList;
import static de.symeda.sormas.api.utils.AgeGroupUtils.convertToString;

import java.util.List;

import javax.persistence.AttributeConverter;

public class AgeGroupsConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> ageGroupList) {
		return convertToString(ageGroupList);
	}

	@Override
	public List<String> convertToEntityAttribute(String ageGroupsString) {
		return convertToList(ageGroupsString);
	}
}
