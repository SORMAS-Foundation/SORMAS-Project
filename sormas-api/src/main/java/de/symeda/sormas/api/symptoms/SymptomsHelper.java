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
package de.symeda.sormas.api.symptoms;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class SymptomsHelper {

	// TODO thread safety, etc.?!
	private static List<String> symptomPropertyIds;
	private static List<String> specialSymptomPropertyIds;
	private static List<String> lesionsLocationsPropertyIds;

	public static List<Float> getTemperatureValues() {
		List<Float> x = new ArrayList<Float>();
		for (int i = 350; i <= 440; i++) {
			x.add(i / 10.0f);
		}
		return x;
	}

	public static String getTemperatureString(float value) {
		return String.format("%.1f °C", value);
	}

	public static List<String> getSymptomPropertyIds() {
		if (symptomPropertyIds == null) {
			buildSymptomPropertyIds();
		}
		return symptomPropertyIds;
	}

	private static void buildSymptomPropertyIds() {
		symptomPropertyIds = new ArrayList<String>();

		for (Field field : SymptomsDto.class.getDeclaredFields()) {
			if (SymptomState.class.equals(field.getType())) {
				symptomPropertyIds.add(field.getName());
			}
		}
	}

	public static boolean isSpecialSymptom(String symptomPropertyId) {
		if (specialSymptomPropertyIds == null) {
			buildSpecialSymptomPropertyIds();
		}
		return specialSymptomPropertyIds.contains(symptomPropertyId);
	}

	private static void buildSpecialSymptomPropertyIds() {
		specialSymptomPropertyIds = new ArrayList<String>();
		specialSymptomPropertyIds.add(SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS);
		specialSymptomPropertyIds.add(SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_THAT_ITCH);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_SAME_SIZE);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_SAME_STATE);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_DEEP_PROFOUND);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_RESEMBLE_IMG1);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_RESEMBLE_IMG2);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_RESEMBLE_IMG3);
		specialSymptomPropertyIds.add(SymptomsDto.LESIONS_RESEMBLE_IMG4);
	}

	public static List<String> getLesionsLocationsPropertyIds() {
		if (lesionsLocationsPropertyIds == null) {
			buildLesionsLocationsPropertyIds();
		}
		return lesionsLocationsPropertyIds;
	}

	private static void buildLesionsLocationsPropertyIds() {
		lesionsLocationsPropertyIds = new ArrayList<String>();
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_FACE);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_LEGS);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_SOLES_FEET);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_PALMS_HANDS);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_THORAX);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_ARMS);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_GENITALS);
		lesionsLocationsPropertyIds.add(SymptomsDto.LESIONS_ALL_OVER_BODY);
	}

	public static void updateIsSymptomatic(SymptomsDto dto) {
		if (dto == null) {
			return;
		}

		try {
			for (Method method : SymptomsDto.class.getDeclaredMethods()) {
				if (method.getReturnType() == SymptomState.class) {
					if (method.invoke(dto) == SymptomState.YES) {
						dto.setSymptomatic(true);
						return;
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		dto.setSymptomatic(false);
	}
}
