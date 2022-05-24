/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.backend.disease;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.AttributeConverter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.disease.AgeGroupUtils;

public class AgeGroupsConverter implements AttributeConverter<List<String>, String> {

	@Override
	public String convertToDatabaseColumn(List<String> ageGroupList) {
		if (CollectionUtils.isEmpty(ageGroupList)) {
			return null;
		}

		try {
			ageGroupList.forEach(s -> AgeGroupUtils.validateAgeGroup(s));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
				"Content of ageGroupsList is not a valid list of ageGroups, or one of the ageGroups in the String does not match a valid ageGroup: "
					+ ageGroupList);
		}

		return ageGroupList.stream().collect(Collectors.joining(","));
	}

	@Override
	public List<String> convertToEntityAttribute(String ageGroupsString) {
		if (StringUtils.isBlank(ageGroupsString)) {
			return null;
		}

		try {
			final List<String> agrGroupList = Stream.of(ageGroupsString.split(",")).collect(Collectors.toList());
			agrGroupList.forEach(s -> AgeGroupUtils.validateAgeGroup(s));
			return agrGroupList;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
				"Content of ageGroupsString is not a valid list of ageGroups, or one of the ageGroups in the String does not match a valid ageGroup: "
					+ ageGroupsString);
		}
	}
}

