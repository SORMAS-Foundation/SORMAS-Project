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
package de.symeda.sormas.api.symptoms;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;

public final class SymptomsHelper {

	private SymptomsHelper() {
		// Hide Utility Class Constructor
	}

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

	public static List<Integer> getBloodPressureValues() {
		return DataHelper.buildIntegerList(0, 300);
	}

	public static List<Integer> getHeartRateValues() {
		return DataHelper.buildIntegerList(0, 300);
	}

	public static List<Integer> getRespiratoryRateValues() {
		return DataHelper.buildIntegerList(0, 80);
	}

	public static List<Integer> getGlasgowComaScaleValues() {
		return DataHelper.buildIntegerList(3, 15);
	}

	public static List<Integer> getWeightValues() {
		return DataHelper.buildIntegerList(0, 50000, 10);
	}

	public static List<Integer> getHeightValues() {
		return DataHelper.buildIntegerList(0, 250);
	}

	public static List<Integer> getMidUpperArmCircumferenceValues() {
		return DataHelper.buildIntegerList(100, 10000, 10);
	}

	public static String getTemperatureString(float value) {
		return String.format("%.1f °C", value);
	}

	public static String getDecimalString(int value) {

		BigDecimal d = new BigDecimal(value).divide(new BigDecimal(100));
		return d.toString();
	}

	public static String getBloodPressureString(Integer systolic, Integer diastolic) {

		if (systolic == null && diastolic == null) {
			return "";
		}

		StringBuilder bpStringBuilder = new StringBuilder();
		bpStringBuilder.append(systolic != null ? systolic : "?")
			.append("/")
			.append(diastolic != null ? diastolic : "?")
			.append(" ")
			.append(I18nProperties.getString(Strings.mmhg));

		return bpStringBuilder.toString();
	}

	public static String getHeartRateString(int heartRate) {
		return heartRate + " " + I18nProperties.getString(Strings.bpm);
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

	/**
	 * Updates the targetSymptoms according to the sourceSymptoms values. All sourceSymptoms that
	 * are set to YES will also be set to YES in the targetSymptoms, while sourceSymptoms set to NO
	 * will not result in an update of the corresponding targetSymptom. Additionally, the
	 * targetSymptoms temperature will be updated if it is lower than the sourceSymptoms temperature,
	 * and Strings will be added to existing Strings when those do not contain them already.
	 */
	public static void updateSymptoms(SymptomsDto sourceSymptoms, SymptomsDto targetSymptoms) {

		if (sourceSymptoms == null || targetSymptoms == null) {
			throw new NullPointerException("sourceSymptoms and targetSymptoms can not be null.");
		}

		boolean newTemperatureSet = false;
		try {
			PropertyDescriptor[] pds = Introspector.getBeanInfo(SymptomsDto.class, EntityDto.class).getPropertyDescriptors();
			for (PropertyDescriptor pd : pds) {
				// Skip properties without a read or write method
				if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
					continue;
				}

				if (pd.getReadMethod().getReturnType() == SymptomState.class) {
					// SymptomStates are carried over when they are set to YES
					SymptomState result = (SymptomState) pd.getReadMethod().invoke(sourceSymptoms);
					if (result == SymptomState.YES) {
						pd.getWriteMethod().invoke(targetSymptoms, result);
					}
				} else if (pd.getReadMethod().getReturnType() == Boolean.class) {
					// Booleans are carried over when they are TRUE
					if (pd.getName().equals(SymptomsDto.SYMPTOMATIC)) {
						continue;
					} else {
						Boolean result = (Boolean) pd.getReadMethod().invoke(sourceSymptoms);
						if (Boolean.TRUE.equals(result)) {
							pd.getWriteMethod().invoke(targetSymptoms, result);
						}
					}
				} else if (pd.getName().equals(SymptomsDto.TEMPERATURE)) {
					// Temperature is carried over when it's higher than the targetSymptoms temperature
					Float sourceResult = (Float) pd.getReadMethod().invoke(sourceSymptoms);
					Float targetResult = (Float) pd.getReadMethod().invoke(targetSymptoms);
					if (sourceResult != null && (targetResult == null || sourceResult > targetResult)) {
						pd.getWriteMethod().invoke(targetSymptoms, sourceResult);
						newTemperatureSet = true;
					}
				} else if (pd.getReadMethod().getReturnType() == String.class) {
					// Strings are added to the targetSymptoms when they are not contained within the
					// respective targetSymptoms String
					if (pd.getName().equals(SymptomsDto.ONSET_DATE)) {
						continue;
					} else {
						String sourceResult = (String) pd.getReadMethod().invoke(sourceSymptoms);
						String targetResult = (String) pd.getReadMethod().invoke(targetSymptoms);
						if (targetResult == null) {
							targetResult = "";
						}
						if (!StringUtils.isEmpty(sourceResult) && !targetResult.contains(sourceResult)) {
							if (targetResult.isEmpty()) {
								pd.getWriteMethod().invoke(targetSymptoms, sourceResult);
							} else {
								pd.getWriteMethod().invoke(targetSymptoms, targetResult + ", " + sourceResult);
							}
						}
					}
				}
			}

			if (newTemperatureSet) {
				targetSymptoms.setTemperatureSource(sourceSymptoms.getTemperatureSource());
			}
		} catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException("Exception when trying to update symptoms: " + e.getMessage(), e.getCause());
		}
	}
}
