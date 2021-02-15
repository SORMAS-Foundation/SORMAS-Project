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
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;

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

	public static Boolean atLeastOnSymptomTrue(SymptomState... symptomsStates) {
		for (SymptomState symptomsState : symptomsStates) {
			if (symptomsState == SymptomState.YES) {
				return true;
			}
		}
		return false;
	}

	public static Boolean allSymptomsUnknownOrNull(SymptomsDto dto) {
		if (dto == null) {
			return true;
		}

		try {
			for (Method method : SymptomsDto.class.getDeclaredMethods()) {
				if (method.getReturnType() == SymptomState.class) {
					Object symptomState = method.invoke(dto);
					if (symptomState == SymptomState.YES || symptomState == SymptomState.NO) {
						return false;
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	public static Boolean allSymptomsFalse(SymptomsDto dto) {

		if (dto == null) {
			return true;
		}

		try {
			for (Method method : SymptomsDto.class.getDeclaredMethods()) {
				if (method.getReturnType() == SymptomState.class) {
					Object symptomState = method.invoke(dto);
					if (symptomState == SymptomState.YES || symptomState == SymptomState.UNKNOWN || symptomState == null) {
						return false;
					}
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	public static String buildSymptomsHumanString(SymptomsDto symptomsDto, boolean includeOnset, Language language) {
		StringBuilder string = new StringBuilder();

		// would be much nicer to have some automatism for this
		if (includeOnset) {
			appendNotNullDateValue(string, symptomsDto.getOnsetDate(), SymptomsDto.ONSET_DATE, language);
		}
		// onsetSymptom;
		// symptomatic;
		// patientIllLocation;
		appendNotNullValue(string, symptomsDto.getTemperature(), SymptomsDto.TEMPERATURE);
		appendNotNullValue(string, symptomsDto.getTemperatureSource(), SymptomsDto.TEMPERATURE_SOURCE);
		appendNotNullValue(string, symptomsDto.getBloodPressureSystolic(), SymptomsDto.BLOOD_PRESSURE_SYSTOLIC);
		appendNotNullValue(string, symptomsDto.getBloodPressureDiastolic(), SymptomsDto.BLOOD_PRESSURE_DIASTOLIC);
		appendNotNullValue(string, symptomsDto.getHeartRate(), SymptomsDto.HEART_RATE);
		appendNotNullValue(string, symptomsDto.getMidUpperArmCircumference(), SymptomsDto.MID_UPPER_ARM_CIRCUMFERENCE);
		appendNotNullValue(string, symptomsDto.getRespiratoryRate(), SymptomsDto.RESPIRATORY_RATE);
		appendNotNullValue(string, symptomsDto.getWeight(), SymptomsDto.WEIGHT);
		appendNotNullValue(string, symptomsDto.getHeight(), SymptomsDto.HEIGHT);
		appendNotNullValue(string, symptomsDto.getGlasgowComaScale(), SymptomsDto.GLASGOW_COMA_SCALE);

		appendYesSymptom(string, symptomsDto.getAlteredConsciousness(), SymptomsDto.ALTERED_CONSCIOUSNESS);
		appendYesSymptom(string, symptomsDto.getConfusedDisoriented(), SymptomsDto.CONFUSED_DISORIENTED);
		appendYesSymptom(string, symptomsDto.getHemorrhagicSyndrome(), SymptomsDto.HEMORRHAGIC_SYNDROME);
		appendYesSymptom(string, symptomsDto.getHyperglycemia(), SymptomsDto.HYPERGLYCEMIA);
		appendYesSymptom(string, symptomsDto.getHypoglycemia(), SymptomsDto.HYPOGLYCEMIA);
		appendYesSymptom(string, symptomsDto.getMeningealSigns(), SymptomsDto.MENINGEAL_SIGNS);
		appendYesSymptom(string, symptomsDto.getSeizures(), SymptomsDto.SEIZURES);
		appendYesSymptom(string, symptomsDto.getSepsis(), SymptomsDto.SEPSIS);
		appendYesSymptom(string, symptomsDto.getShock(), SymptomsDto.SHOCK);

		appendYesSymptom(string, symptomsDto.getFever(), SymptomsDto.FEVER);
		appendYesSymptom(string, symptomsDto.getVomiting(), SymptomsDto.VOMITING);
		appendYesSymptom(string, symptomsDto.getDiarrhea(), SymptomsDto.DIARRHEA);
		appendYesSymptom(string, symptomsDto.getBloodInStool(), SymptomsDto.BLOOD_IN_STOOL);
		appendYesSymptom(string, symptomsDto.getNausea(), SymptomsDto.NAUSEA);
		appendYesSymptom(string, symptomsDto.getAbdominalPain(), SymptomsDto.ABDOMINAL_PAIN);
		appendYesSymptom(string, symptomsDto.getHeadache(), SymptomsDto.HEADACHE);
		appendYesSymptom(string, symptomsDto.getMusclePain(), SymptomsDto.MUSCLE_PAIN);
		appendYesSymptom(string, symptomsDto.getFatigueWeakness(), SymptomsDto.FATIGUE_WEAKNESS);
		appendYesSymptom(string, symptomsDto.getUnexplainedBleeding(), SymptomsDto.UNEXPLAINED_BLEEDING);
		appendYesSymptom(string, symptomsDto.getGumsBleeding(), SymptomsDto.GUMS_BLEEDING);
		appendYesSymptom(string, symptomsDto.getInjectionSiteBleeding(), SymptomsDto.INJECTION_SITE_BLEEDING);
		appendYesSymptom(string, symptomsDto.getNoseBleeding(), SymptomsDto.NOSE_BLEEDING);
		appendYesSymptom(string, symptomsDto.getBloodyBlackStool(), SymptomsDto.BLOODY_BLACK_STOOL);
		appendYesSymptom(string, symptomsDto.getRedBloodVomit(), SymptomsDto.RED_BLOOD_VOMIT);
		appendYesSymptom(string, symptomsDto.getDigestedBloodVomit(), SymptomsDto.DIGESTED_BLOOD_VOMIT);
		appendYesSymptom(string, symptomsDto.getCoughingBlood(), SymptomsDto.COUGHING_BLOOD);
		appendYesSymptom(string, symptomsDto.getBleedingVagina(), SymptomsDto.BLEEDING_VAGINA);
		appendYesSymptom(string, symptomsDto.getSkinBruising(), SymptomsDto.SKIN_BRUISING);
		appendYesSymptom(string, symptomsDto.getBloodUrine(), SymptomsDto.BLOOD_URINE);
		//otherHemorrhagicSymptoms
		appendNotNullValue(string, symptomsDto.getOtherHemorrhagicSymptomsText(), SymptomsDto.OTHER_HEMORRHAGIC_SYMPTOMS_TEXT);
		appendYesSymptom(string, symptomsDto.getSkinRash(), SymptomsDto.SKIN_RASH);
		appendYesSymptom(string, symptomsDto.getNeckStiffness(), SymptomsDto.NECK_STIFFNESS);
		appendYesSymptom(string, symptomsDto.getSoreThroat(), SymptomsDto.SORE_THROAT);
		appendYesSymptom(string, symptomsDto.getCough(), SymptomsDto.COUGH);
		appendYesSymptom(string, symptomsDto.getCoughWithSputum(), SymptomsDto.COUGH_WITH_SPUTUM);
		appendYesSymptom(string, symptomsDto.getCoughWithHeamoptysis(), SymptomsDto.COUGH_WITH_HEAMOPTYSIS);
		appendYesSymptom(string, symptomsDto.getRunnyNose(), SymptomsDto.RUNNY_NOSE);
		appendYesSymptom(string, symptomsDto.getDifficultyBreathing(), SymptomsDto.DIFFICULTY_BREATHING);
		appendYesSymptom(string, symptomsDto.getChestPain(), SymptomsDto.CHEST_PAIN);
		appendYesSymptom(string, symptomsDto.getConjunctivitis(), SymptomsDto.CONJUNCTIVITIS);
		appendYesSymptom(string, symptomsDto.getEyePainLightSensitive(), SymptomsDto.EYE_PAIN_LIGHT_SENSITIVE);
		appendYesSymptom(string, symptomsDto.getKopliksSpots(), SymptomsDto.KOPLIKS_SPOTS);
		appendYesSymptom(string, symptomsDto.getThrobocytopenia(), SymptomsDto.THROBOCYTOPENIA);
		appendYesSymptom(string, symptomsDto.getOtitisMedia(), SymptomsDto.OTITIS_MEDIA);
		appendYesSymptom(string, symptomsDto.getHearingloss(), SymptomsDto.HEARINGLOSS);
		appendYesSymptom(string, symptomsDto.getDehydration(), SymptomsDto.DEHYDRATION);
		appendYesSymptom(string, symptomsDto.getAnorexiaAppetiteLoss(), SymptomsDto.ANOREXIA_APPETITE_LOSS);
		appendYesSymptom(string, symptomsDto.getRefusalFeedorDrink(), SymptomsDto.REFUSAL_FEEDOR_DRINK);
		appendYesSymptom(string, symptomsDto.getJointPain(), SymptomsDto.JOINT_PAIN);
		appendYesSymptom(string, symptomsDto.getHiccups(), SymptomsDto.HICCUPS);
		// otherNonHemorrhagicSymptoms
		appendNotNullValue(string, symptomsDto.getOtherNonHemorrhagicSymptomsText(), SymptomsDto.OTHER_NON_HEMORRHAGIC_SYMPTOMS_TEXT);
		appendYesSymptom(string, symptomsDto.getBackache(), SymptomsDto.BACKACHE);
		appendYesSymptom(string, symptomsDto.getEyesBleeding(), SymptomsDto.EYES_BLEEDING);
		appendYesSymptom(string, symptomsDto.getJaundice(), SymptomsDto.JAUNDICE);
		appendNotNullValue(string, symptomsDto.getJaundiceWithin24HoursOfBirth(), SymptomsDto.JAUNDICE_WITHIN_24_HOURS_OF_BIRTH);
		appendYesSymptom(string, symptomsDto.getDarkUrine(), SymptomsDto.DARK_URINE);
		appendYesSymptom(string, symptomsDto.getStomachBleeding(), SymptomsDto.STOMACH_BLEEDING);
		appendYesSymptom(string, symptomsDto.getRapidBreathing(), SymptomsDto.RAPID_BREATHING);
		appendYesSymptom(string, symptomsDto.getSwollenGlands(), SymptomsDto.SWOLLEN_GLANDS);
		appendYesSymptom(string, symptomsDto.getLesions(), SymptomsDto.LESIONS);
		appendYesSymptom(string, symptomsDto.getLesionsSameState(), SymptomsDto.LESIONS_SAME_STATE);
		appendYesSymptom(string, symptomsDto.getLesionsSameSize(), SymptomsDto.LESIONS_SAME_SIZE);
		appendYesSymptom(string, symptomsDto.getLesionsDeepProfound(), SymptomsDto.LESIONS_DEEP_PROFOUND);
		appendYesSymptom(string, symptomsDto.getLesionsThatItch(), SymptomsDto.LESIONS_THAT_ITCH);
		appendTrue(string, symptomsDto.getLesionsFace(), SymptomsDto.LESIONS_FACE);
		appendTrue(string, symptomsDto.getLesionsLegs(), SymptomsDto.LESIONS_LEGS);
		appendTrue(string, symptomsDto.getLesionsSolesFeet(), SymptomsDto.LESIONS_SOLES_FEET);
		appendTrue(string, symptomsDto.getLesionsPalmsHands(), SymptomsDto.LESIONS_PALMS_HANDS);
		appendTrue(string, symptomsDto.getLesionsThorax(), SymptomsDto.LESIONS_THORAX);
		appendTrue(string, symptomsDto.getLesionsArms(), SymptomsDto.LESIONS_ARMS);
		appendTrue(string, symptomsDto.getLesionsGenitals(), SymptomsDto.LESIONS_GENITALS);
		appendTrue(string, symptomsDto.getLesionsAllOverBody(), SymptomsDto.LESIONS_ALL_OVER_BODY);
		// TODO images should have more specific caption to be included here
//		appendYesSymptom(string, lesionsResembleImg1, SymptomsDto.LESIONS_RESEMBLE_IMG1);
//		appendYesSymptom(string, lesionsResembleImg2, SymptomsDto.LESIONS_RESEMBLE_IMG2);
//		appendYesSymptom(string, lesionsResembleImg3, SymptomsDto.LESIONS_RESEMBLE_IMG3);
//		appendYesSymptom(string, lesionsResembleImg4, SymptomsDto.LESIONS_RESEMBLE_IMG4);
		appendNotNullDateValue(string, symptomsDto.getLesionsOnsetDate(), SymptomsDto.LESIONS_ONSET_DATE, language);
		appendYesSymptom(string, symptomsDto.getLymphadenopathy(), SymptomsDto.LYMPHADENOPATHY);
		appendYesSymptom(string, symptomsDto.getLymphadenopathyInguinal(), SymptomsDto.LYMPHADENOPATHY_INGUINAL);
		appendYesSymptom(string, symptomsDto.getLymphadenopathyAxillary(), SymptomsDto.LYMPHADENOPATHY_AXILLARY);
		appendYesSymptom(string, symptomsDto.getLymphadenopathyCervical(), SymptomsDto.LYMPHADENOPATHY_CERVICAL);
		appendYesSymptom(string, symptomsDto.getMeningealSigns(), SymptomsDto.MENINGEAL_SIGNS);
		appendYesSymptom(string, symptomsDto.getChillsSweats(), SymptomsDto.CHILLS_SWEATS);
		appendYesSymptom(string, symptomsDto.getBedridden(), SymptomsDto.BEDRIDDEN);
		appendYesSymptom(string, symptomsDto.getOralUlcers(), SymptomsDto.ORAL_ULCERS);
		appendYesSymptom(string, symptomsDto.getPainfulLymphadenitis(), SymptomsDto.PAINFUL_LYMPHADENITIS);
		appendYesSymptom(string, symptomsDto.getBlackeningDeathOfTissue(), SymptomsDto.BLACKENING_DEATH_OF_TISSUE);
		appendYesSymptom(string, symptomsDto.getBuboesGroinArmpitNeck(), SymptomsDto.BUBOES_GROIN_ARMPIT_NECK);
		appendYesSymptom(string, symptomsDto.getBulgingFontanelle(), SymptomsDto.BULGING_FONTANELLE);
		appendYesSymptom(string, symptomsDto.getPharyngealErythema(), SymptomsDto.PHARYNGEAL_ERYTHEMA);
		appendYesSymptom(string, symptomsDto.getPharyngealExudate(), SymptomsDto.PHARYNGEAL_EXUDATE);
		appendYesSymptom(string, symptomsDto.getOedemaFaceNeck(), SymptomsDto.OEDEMA_FACE_NECK);
		appendYesSymptom(string, symptomsDto.getOedemaLowerExtremity(), SymptomsDto.OEDEMA_LOWER_EXTREMITY);
		appendYesSymptom(string, symptomsDto.getLossSkinTurgor(), SymptomsDto.LOSS_SKIN_TURGOR);
		appendYesSymptom(string, symptomsDto.getPalpableLiver(), SymptomsDto.PALPABLE_LIVER);
		appendYesSymptom(string, symptomsDto.getPalpableSpleen(), SymptomsDto.PALPABLE_SPLEEN);
		appendYesSymptom(string, symptomsDto.getMalaise(), SymptomsDto.MALAISE);
		appendYesSymptom(string, symptomsDto.getSunkenEyesFontanelle(), SymptomsDto.SUNKEN_EYES_FONTANELLE);
		appendYesSymptom(string, symptomsDto.getSidePain(), SymptomsDto.SIDE_PAIN);
		appendYesSymptom(string, symptomsDto.getFluidInLungCavity(), SymptomsDto.FLUID_IN_LUNG_CAVITY);
		appendYesSymptom(string, symptomsDto.getTremor(), SymptomsDto.TREMOR);
		appendYesSymptom(string, symptomsDto.getBilateralCataracts(), SymptomsDto.BILATERAL_CATARACTS);
		appendYesSymptom(string, symptomsDto.getUnilateralCataracts(), SymptomsDto.UNILATERAL_CATARACTS);
		appendYesSymptom(string, symptomsDto.getCongenitalGlaucoma(), SymptomsDto.CONGENITAL_GLAUCOMA);
		appendYesSymptom(string, symptomsDto.getPigmentaryRetinopathy(), SymptomsDto.PIGMENTARY_RETINOPATHY);
		appendYesSymptom(string, symptomsDto.getPurpuricRash(), SymptomsDto.PURPURIC_RASH);
		appendYesSymptom(string, symptomsDto.getMicrocephaly(), SymptomsDto.MICROCEPHALY);
		appendYesSymptom(string, symptomsDto.getDevelopmentalDelay(), SymptomsDto.DEVELOPMENTAL_DELAY);
		appendYesSymptom(string, symptomsDto.getSplenomegaly(), SymptomsDto.SPLENOMEGALY);
		appendYesSymptom(string, symptomsDto.getMeningoencephalitis(), SymptomsDto.MENINGOENCEPHALITIS);
		appendYesSymptom(string, symptomsDto.getRadiolucentBoneDisease(), SymptomsDto.RADIOLUCENT_BONE_DISEASE);
		appendYesSymptom(string, symptomsDto.getCongenitalHeartDisease(), SymptomsDto.CONGENITAL_HEART_DISEASE);
		appendYesSymptom(string, symptomsDto.getFluidInLungCavityAuscultation(), SymptomsDto.FLUID_IN_LUNG_CAVITY_AUSCULTATION);
		appendYesSymptom(string, symptomsDto.getFluidInLungCavityXray(), SymptomsDto.FLUID_IN_LUNG_CAVITY_XRAY);
		appendYesSymptom(string, symptomsDto.getAbnormalLungXrayFindings(), SymptomsDto.ABNORMAL_LUNG_XRAY_FINDINGS);
		appendYesSymptom(string, symptomsDto.getConjunctivalInjection(), SymptomsDto.CONJUNCTIVAL_INJECTION);
		appendYesSymptom(string, symptomsDto.getAcuteRespiratoryDistressSyndrome(), SymptomsDto.ACUTE_RESPIRATORY_DISTRESS_SYNDROME);
		appendYesSymptom(string, symptomsDto.getPneumoniaClinicalOrRadiologic(), SymptomsDto.PNEUMONIA_CLINICAL_OR_RADIOLOGIC);
		appendNotNullValue(string, symptomsDto.getCongenitalHeartDiseaseType(), SymptomsDto.CONGENITAL_HEART_DISEASE_TYPE);
		appendNotNullValue(string, symptomsDto.getCongenitalHeartDiseaseDetails(), SymptomsDto.CONGENITAL_HEART_DISEASE_DETAILS);
		appendYesSymptom(string, symptomsDto.getLossOfTaste(), SymptomsDto.LOSS_OF_TASTE);
		appendYesSymptom(string, symptomsDto.getLossOfSmell(), SymptomsDto.LOSS_OF_SMELL);
		appendYesSymptom(string, symptomsDto.getWheezing(), SymptomsDto.WHEEZING);
		appendYesSymptom(string, symptomsDto.getSkinUlcers(), SymptomsDto.SKIN_ULCERS);
		appendYesSymptom(string, symptomsDto.getInabilityToWalk(), SymptomsDto.INABILITY_TO_WALK);
		appendYesSymptom(string, symptomsDto.getInDrawingOfChestWall(), SymptomsDto.IN_DRAWING_OF_CHEST_WALL);
		appendYesSymptom(string, symptomsDto.getRespiratoryDiseaseVentilation(), SymptomsDto.RESPIRATORY_DISEASE_VENTILATION);
		appendYesSymptom(string, symptomsDto.getFeelingIll(), SymptomsDto.FEELING_ILL);
		appendYesSymptom(string, symptomsDto.getShivering(), SymptomsDto.SHIVERING);
		appendYesSymptom(string, symptomsDto.getFastHeartRate(), SymptomsDto.FAST_HEART_RATE);
		appendYesSymptom(string, symptomsDto.getOxygenSaturationLower94(), SymptomsDto.OXYGEN_SATURATION_LOWER_94);

		appendYesSymptom(string, symptomsDto.getFeverishFeeling(), SymptomsDto.FEVERISHFEELING);
		appendYesSymptom(string, symptomsDto.getFatigueWeakness(), SymptomsDto.WEAKNESS);
		appendYesSymptom(string, symptomsDto.getFatigue(), SymptomsDto.FATIGUE);
		appendYesSymptom(string, symptomsDto.getCoughWithoutSputum(), SymptomsDto.COUGH_WITHOUT_SPUTUM);
		appendYesSymptom(string, symptomsDto.getBreathlessness(), SymptomsDto.BREATHLESSNESS);
		appendYesSymptom(string, symptomsDto.getChestPressure(), SymptomsDto.CHEST_PRESSURE);
		appendYesSymptom(string, symptomsDto.getBlueLips(), SymptomsDto.BLUE_LIPS);
		appendYesSymptom(string, symptomsDto.getBloodCirculationProblems(), SymptomsDto.BLOOD_CIRCULATION_PROBLEMS);
		appendYesSymptom(string, symptomsDto.getPalpitations(), SymptomsDto.PALPITATIONS);
		appendYesSymptom(string, symptomsDto.getDizzinessStandingUp(), SymptomsDto.DIZZINESS_STANDING_UP);
		appendYesSymptom(string, symptomsDto.getHighOrLowBloodPressure(), SymptomsDto.HIGH_OR_LOW_BLOOD_PRESSURE);
		appendYesSymptom(string, symptomsDto.getUrinaryRetention(), SymptomsDto.URINARY_RETENTION);

		appendNotNullValue(string, symptomsDto.getOtherComplicationsText(), SymptomsDto.OTHER_COMPLICATIONS_TEXT);

		// symptomsComments;

		return string.toString();
	}

	private static void appendNotNullValue(StringBuilder stringBuilder, Object value, String dtoPropertyId) {
		if (value != null) {
			if (value instanceof String && ((String) value).isEmpty()) {
				return; // ignore empty strings
			}
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null)).append(": ");

			stringBuilder.append(value);
		}
	}

	private static void appendNotNullDateValue(StringBuilder stringBuilder, Date value, String dtoPropertyId, Language language) {
		appendNotNullValue(stringBuilder, DateHelper.formatLocalDate(value, language), dtoPropertyId);
	}

	private static void appendYesSymptom(StringBuilder stringBuilder, SymptomState symptom, String dtoPropertyId) {
		if (symptom == SymptomState.YES) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null));
		}
	}

	private static void appendTrue(StringBuilder stringBuilder, Boolean value, String dtoPropertyId) {
		if (value != null && Boolean.TRUE.equals(value)) {
			if (stringBuilder.length() > 0) {
				stringBuilder.append(", ");
			}
			stringBuilder.append(I18nProperties.getPrefixCaption(SymptomsDto.I18N_PREFIX, dtoPropertyId, null));
		}
	}
}
