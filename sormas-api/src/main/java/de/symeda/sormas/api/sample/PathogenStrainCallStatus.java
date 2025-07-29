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

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.ApplicableToPathogenTests;
import de.symeda.sormas.api.utils.Diseases;

public enum PathogenStrainCallStatus {

	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.BEIJINGGENOTYPING })
	BEIJING,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.BEIJINGGENOTYPING })
	NOBEIJING,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.BEIJINGGENOTYPING })
	POSSBEIJING,
	@Diseases(value = {
		Disease.TUBERCULOSIS })
	@ApplicableToPathogenTests(value = {
		PathogenTestType.BEIJINGGENOTYPING })
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static List<PathogenStrainCallStatus> forDisease(Disease disease) {
		return Arrays.stream(values()).filter(status -> {
			try {
				Field f = PathogenStrainCallStatus.class.getField(status.name());
				Diseases ann = f.getAnnotation(Diseases.class);
				return ann != null && Arrays.asList(ann.value()).contains(disease);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}

	public static List<PathogenStrainCallStatus> forPathogenTest(PathogenTestType pathogenTest) {
		return Arrays.stream(values()).filter(status -> {
			try {
				Field f = PathogenStrainCallStatus.class.getField(status.name());
				ApplicableToPathogenTests ann = f.getAnnotation(ApplicableToPathogenTests.class);
				return ann != null && Arrays.asList(ann.value()).contains(pathogenTest);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}

	public static List<PathogenStrainCallStatus> forDiseaseAndTest(Disease disease, PathogenTestType pathogenTest) {
		return Arrays.stream(values()).filter(status -> {
			try {
				Field f = PathogenStrainCallStatus.class.getField(status.name());
				Diseases diseaseAnn = f.getAnnotation(Diseases.class);
				ApplicableToPathogenTests testAnn = f.getAnnotation(ApplicableToPathogenTests.class);
				return diseaseAnn != null
					&& testAnn != null
					&& Arrays.asList(diseaseAnn.value()).contains(disease)
					&& Arrays.asList(testAnn.value()).contains(pathogenTest);
			} catch (NoSuchFieldException e) {
				return false;
			}
		}).collect(Collectors.toList());
	}
}
