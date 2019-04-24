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
package de.symeda.sormas.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DataHelper;

public class DiseaseHelper {

	private static Set<Disease> defaultFollowUpDiseases = new HashSet<>(Arrays.asList(
			Disease.CHOLERA,
			Disease.EVD, 
			Disease.LASSA, 
			Disease.NEW_INFLUENCA, 
			Disease.MONKEYPOX,
			Disease.PLAGUE, 
			Disease.OTHER));

	@SuppressWarnings("serial")
	private static Map<Disease, Integer> defaultFollowUpDurations = new HashMap<Disease, Integer>() {{
		put(Disease.NEW_INFLUENCA, 17);
		put(Disease.DENGUE, 14);
		put(Disease.CSM, 10);
		put(Disease.PLAGUE, 7);
		put(Disease.YELLOW_FEVER, 6);
		put(Disease.CHOLERA, 5);
	}};
	
	private static Set<Disease> defaultActiveDiseases = new HashSet<>(Arrays.asList(
			Disease.CHOLERA,
			Disease.CSM,
			Disease.DENGUE,
			Disease.EVD,
			Disease.LASSA,
			Disease.MEASLES,
			Disease.MONKEYPOX,
			Disease.NEW_INFLUENCA,
			Disease.PLAGUE,
			Disease.YELLOW_FEVER,
			Disease.OTHER));
	
	private static Set<Disease> defaultPrimaryDiseases = new HashSet<>(Arrays.asList(
			Disease.CHOLERA,
			Disease.CSM,
			Disease.DENGUE,
			Disease.EVD,
			Disease.LASSA,
			Disease.MEASLES,
			Disease.MONKEYPOX,
			Disease.NEW_INFLUENCA,
			Disease.PLAGUE,
			Disease.YELLOW_FEVER,
			Disease.OTHER));
	
	/**
	 * Checks whether the given symptoms match the clinical criteria of one of the three Plague types.
	 */
	public static PlagueType getPlagueTypeForSymptoms(SymptomsDto symptoms) {
		if (symptoms.getFever() == SymptomState.YES) {
			if (symptoms.getPainfulLymphadenitis() == SymptomState.YES) {
				return PlagueType.BUBONIC;
			} else if (symptoms.getCough() == SymptomState.YES || symptoms.getChestPain() == SymptomState.YES ||
					symptoms.getCoughingBlood() == SymptomState.YES) {
				return PlagueType.PNEUMONIC;
			} else if (symptoms.getChillsSweats() == SymptomState.YES) {
				return PlagueType.SEPTICAEMIC;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Checks whether the given disease is set to have contact follow-up on the current system. If the
	 * DiseaseConfiguration in the database does not specify this value, a default value is returned instead.
	 */
	public static boolean hasContactFollowUp(Disease disease) {
		DiseaseConfigurationDto configuration = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		if (configuration.getFollowUpEnabled() != null) {
			return configuration.getFollowUpEnabled();
		} else {
			return defaultFollowUpDiseases.contains(disease);
		}
	}

	/**
	 * Returns all diseases that are set to have contact follow-up on the current system by using hasContactFollowUp(Disease).
	 */
	public static List<Disease> getAllDiseasesWithFollowUp() {
		EnumSet<Disease> diseases = EnumSet.allOf(Disease.class);
		List<Disease> diseasesWithFollowUp = new ArrayList<>();

		for (Disease disease : diseases) {
			if (hasContactFollowUp(disease)) {
				diseasesWithFollowUp.add(disease);
			}
		}

		return diseasesWithFollowUp;
	}

	/**
	 * Returns the duration of contact follow-up for the given disease. If the DiseaseConfiguration in the database 
	 * does not specify this value, a default value is returned instead.
	 */
	public static int getFollowUpDuration(Disease disease) {
		DiseaseConfigurationDto configuration = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		if (configuration.getFollowUpDuration() != null) {
			return configuration.getFollowUpDuration();
		} else if (defaultFollowUpDurations.containsKey(disease)) {
			return defaultFollowUpDurations.get(disease);
		} else {
			return 21;
		}
	}

	/**
	 * Checks whether the given disease is set to be active on the current system. If the DiseaseConfiguration 
	 * in the database does not specify this value, a default value is returned instead.
	 */
	public static boolean isDiseaseActive(Disease disease) {
		DiseaseConfigurationDto configuration = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		if (configuration.getActive() != null) {
			return configuration.getActive();
		} else {
			return defaultActiveDiseases.contains(disease);
		}
	}
	
	/**
	 * Returns all diseases that are set to be active on the current system by using isDiseaseActive(Disease).
	 */
	public static List<Disease> getAllActiveDiseases() {
		EnumSet<Disease> diseases = EnumSet.allOf(Disease.class);
		List<Disease> activeDiseases = new ArrayList<>();

		for (Disease disease : diseases) {
			if (isDiseaseActive(disease)) {
				activeDiseases.add(disease);
			}
		}

		return activeDiseases;
	}

	/**
	 * Checks whether the given disease is set to be used as a primary disease on the current system, meaning that
	 * it is enabled for case surveillance. If the DiseaseConfiguration in the database does not specify this value,
	 *  a default value is returned instead.
	 */
	public static boolean isDiseasePrimary(Disease disease) {
		DiseaseConfigurationDto configuration = FacadeProvider.getDiseaseConfigurationFacade().getDiseaseConfiguration(disease);
		if (configuration.getPrimaryDisease() != null) {
			return configuration.getPrimaryDisease();
		} else {
			return defaultPrimaryDiseases.contains(disease);
		}
	}

	/**
	 * Returns all diseases that are set to be primary on the current system by using isDiseasePrimary(Disease).
	 */
	public static List<Disease> getAllPrimaryDiseases() {
		EnumSet<Disease> diseases = EnumSet.allOf(Disease.class);
		List<Disease> primaryDiseases = new ArrayList<>();

		for (Disease disease : diseases) {
			if (isDiseasePrimary(disease)) {
				primaryDiseases.add(disease);
			}
		}

		return primaryDiseases;
	}

	/**
	 * Returns all diseases that are set to be active and primary on the current system by using isDiseaseActive(Disease)
	 * and isDiseasePrimary(Disease).
	 */
	public static List<Disease> getAllActivePrimaryDiseases() {
		EnumSet<Disease> diseases = EnumSet.allOf(Disease.class);
		List<Disease> activePrimaryDiseases = new ArrayList<>();

		for (Disease disease : diseases) {
			if (isDiseaseActive(disease) && isDiseasePrimary(disease)) {
				activePrimaryDiseases.add(disease);
			}
		}

		return activePrimaryDiseases;
	}

	public static String toString(Disease disease, String diseaseDetails) {
		if (disease == null) {
			return "";
		}
		
		return disease != Disease.OTHER ? disease.toShortString() : DataHelper.toStringNullable(diseaseDetails);
	}
}
