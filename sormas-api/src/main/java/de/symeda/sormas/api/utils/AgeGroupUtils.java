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

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;

public class AgeGroupUtils {

	private static final String AGE_GROUP_RANGE_REGEX = "\\d{1,3}[dmyDMY]_\\d{1,3}[dmyDMY]";
	private static final String AGE_GROUP_START_AT_REGEX = "\\d{1,3}[dmyDMY]";

	private static final Logger logger = LoggerFactory.getLogger(AgeGroupUtils.class);

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

		final List<String> ageGroupList = Stream.of(ageGroupsString.split(",")).map(String::trim).collect(Collectors.toList());
		ageGroupList.removeIf(s -> {
			try {
				validateAgeGroup(s);
				return false;
			} catch (IllegalArgumentException e) {
				logger.warn(String.format("Age group %s in ageGroupsString %s is not a valid age group.", s, ageGroupsString));
				return true;
			}
		});

		return ageGroupList;
	}

	public static String convertToString(List<String> ageGroupList) {

		if (CollectionUtils.isEmpty(ageGroupList)) {
			return null;
		}

		ageGroupList.removeIf(s -> {
			try {
				validateAgeGroup(s);
				return false;
			} catch (IllegalArgumentException e) {
				logger.warn(String.format("Age group %s in ageGroupsList %s is not a valid age group.", s, ageGroupList));
				return true;
			}
		});

		return String.join(",", ageGroupList);
	}

	public static Comparator<String> getComparator() {

		Comparator<String> comparator = Comparator
			.comparing(
				(String ageGroup) -> ageGroup != null
					? ageGroup.split("_")[0].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase()
					: StringUtils.EMPTY)
			.thenComparing(
				(String ageGroup) -> ageGroup != null ? Integer.parseInt(ageGroup.split("_")[0].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0)
			.thenComparing( // not relevant for correct age groups, but for corner cases (multiple age groups with same start)
				(String ageGroup) -> {
					if (ageGroup == null)
						return StringUtils.EMPTY;
					String[] split = ageGroup.split("_");
					return split.length > 1 ? split[1].replaceAll("[^a-zA-Z]", StringUtils.EMPTY).toUpperCase() : StringUtils.EMPTY;
				})
			.thenComparing((String ageGroup) -> {
				if (ageGroup == null)
					return 0;
				String[] split = ageGroup.split("_");
				return split.length > 1 ? Integer.parseInt(split[1].replaceAll("[^0-9]", StringUtils.EMPTY)) : 0;
			});
		return comparator;
	}
}
