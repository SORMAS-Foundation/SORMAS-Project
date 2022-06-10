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

package de.symeda.sormas.api.utils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class AgeGroupUtils {

	private final static String AGE_GROUP_RANGE_REGEX = "\\d{1,3}[dmyDMY]_\\d{1,3}[dmyDMY]";
	private final static String AGE_GROUP_START_AT_REGEX = "\\d{1,3}[dmyDMY]";

	public static void validateAgeGroup(String ageGroup) {
		if (!(Pattern.matches(AGE_GROUP_RANGE_REGEX, ageGroup) || Pattern.matches(AGE_GROUP_START_AT_REGEX, ageGroup))) {
			throw new IllegalArgumentException("Invalid ageGroup definition: " + ageGroup);
		}
	}

	public static String createCaption(String ageGroup) {
		if (ageGroup == null || ageGroup.isEmpty()) {
			return StringUtils.EMPTY;
		}
		validateAgeGroup(ageGroup);
		if (ageGroup.contains("_")) {
			final String[] ageGroupSplit = ageGroup.split("_");
			final String firstGroup = ageGroupSplit[0];
			final String secondGroup = ageGroupSplit[1];
			final String firstGroupTimeUnit = firstGroup.substring(firstGroup.length() - 1);
			final String secondGroupTimeUnit = secondGroup.substring(secondGroup.length() - 1);
			if (firstGroupTimeUnit.equalsIgnoreCase(secondGroupTimeUnit)) {
				return firstGroup.substring(0, firstGroup.length() - 1) + "-" + secondGroup.substring(0, secondGroup.length() - 1) + StringUtils.SPACE
					+ getAgeGroupTimeUnitCaption(firstGroupTimeUnit);
			} else {
				return firstGroup.substring(0, firstGroup.length() - 1) + StringUtils.SPACE + getAgeGroupTimeUnitCaption(firstGroupTimeUnit) + " - "
					+ secondGroup.substring(0, secondGroup.length() - 1) + StringUtils.SPACE + getAgeGroupTimeUnitCaption(secondGroupTimeUnit);
			}
		} else {
			return ageGroup.substring(0, ageGroup.length() - 1) + "+ " + getAgeGroupTimeUnitCaption(ageGroup.substring(ageGroup.length() - 1));
		}
	}

	private static String getAgeGroupTimeUnitCaption(String ageGroupTimeUnit) {
		if (ageGroupTimeUnit.equalsIgnoreCase("d")) {
			return I18nProperties.getCaption(Captions.days);
		}
		if (ageGroupTimeUnit.equalsIgnoreCase("m")) {
			return I18nProperties.getCaption(Captions.months);
		}
		if (ageGroupTimeUnit.equalsIgnoreCase("y")) {
			return I18nProperties.getCaption(Captions.years);
		}
		throw new IllegalArgumentException("Invalid ageGroupTimeUnit definition: " + ageGroupTimeUnit);
	}

	public static List<String> convertToList(String ageGroupsString) {
		if (StringUtils.isBlank(ageGroupsString)) {
			return null;
		}

		try {
			final List<String> ageGroupList = Stream.of(ageGroupsString.split(",")).collect(Collectors.toList());
			ageGroupList.forEach(s -> validateAgeGroup(s));
			return ageGroupList;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
				"Content of ageGroupsString is not a valid list of ageGroups, or one of the ageGroups in the String does not match a valid ageGroup: "
					+ ageGroupsString);
		}
	}

	public static String convertToString(List<String> ageGroupList) {
		if (CollectionUtils.isEmpty(ageGroupList)) {
			return null;
		}

		try {
			ageGroupList.forEach(s -> validateAgeGroup(s));
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(
				"Content of ageGroupsList is not a valid list of ageGroups, or one of the ageGroups in the String does not match a valid ageGroup: "
					+ ageGroupList);
		}

		return ageGroupList.stream().collect(Collectors.joining(","));
	}
}
