/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.sample;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;

public enum PathogenTestScale {

	@ApplicableToPathogenTests(value = {
		PathogenTestType.MICROSCOPY })
	ONE_PLUS,
	@ApplicableToPathogenTests(value = {
		PathogenTestType.MICROSCOPY })
	TWO_PLUS,
	@ApplicableToPathogenTests(value = {
		PathogenTestType.MICROSCOPY })
	THREE_PLUS;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static List<PathogenTestScale> forPathogenTest(PathogenTestType pathogenTest) {
		return Arrays.stream(values()).filter(status -> {
			try {
				Field f = PathogenTestScale.class.getField(status.name());
				ApplicableToPathogenTests ann = f.getAnnotation(ApplicableToPathogenTests.class);
				return ann != null && Arrays.asList(ann.value()).contains(pathogenTest);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}
}
