/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.app.backend.user.User;

/**
 * Replicates logic of de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb backend class
 */
public final class DiseaseConfigurationCache {

	private static DiseaseConfigurationCache instance;

	private List<Disease> activeDiseases = new ArrayList<>();
	private List<Disease> inactiveDiseases = new ArrayList<>();
	private List<Disease> primaryDiseases = new ArrayList<>();
	private List<Disease> nonPrimaryDiseases = new ArrayList<>();
	private List<Disease> caseBasedDiseases = new ArrayList<>();
	private List<Disease> aggregateDiseases = new ArrayList<>();
	private List<Disease> followUpEnabledDiseases = new ArrayList<>();

	private Map<Disease, Boolean> extendedClassificationDiseases = new HashMap<>();
	private Map<Disease, Boolean> extendedClassificationMultiDiseases = new HashMap<>();

	private Map<Disease, Integer> followUpDurations = new HashMap<>();
	private Map<Disease, Integer> caseFollowUpDurations = new HashMap<>();
	private Map<Disease, Integer> eventParticipantFollowUpDurations = new HashMap<>();

	private DiseaseConfigurationCache() {
		for (Disease disease : Disease.values()) {
			DiseaseConfiguration configuration = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(disease);
			if (configuration == null) {
				//Create empty DiseaseConfiguration to use the default values.
				configuration = new DiseaseConfiguration();
			}

			if (Boolean.TRUE.equals(configuration.getActive()) || (configuration.getActive() == null && disease.isDefaultActive())) {
				activeDiseases.add(disease);
			} else {
				inactiveDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getPrimaryDisease()) || (configuration.getPrimaryDisease() == null && disease.isDefaultPrimary())) {
				primaryDiseases.add(disease);
			} else {
				nonPrimaryDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getCaseSurveillanceEnabled())
				|| (configuration.getCaseSurveillanceEnabled() == null && disease.isDefaultCaseSurveillanceEnabled())) {
				caseBasedDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getAggregateReportingEnabled())
				|| (configuration.getAggregateReportingEnabled() == null && disease.isDefaultAggregateReportingEnabled())) {
				aggregateDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getFollowUpEnabled())
				|| (configuration.getFollowUpEnabled() == null && disease.isDefaultFollowUpEnabled())) {
				followUpEnabledDiseases.add(disease);
			}

			if (configuration.getExtendedClassification() == null) {
				extendedClassificationDiseases.put(disease, disease.isDefaultExtendedClassification());
			} else {
				extendedClassificationDiseases.put(disease, configuration.getExtendedClassification());
			}

			if (configuration.getExtendedClassificationMulti() == null) {
				extendedClassificationMultiDiseases.put(disease, disease.isDefaultExtendedClassificationMulti());
			} else {
				extendedClassificationMultiDiseases.put(disease, configuration.getExtendedClassificationMulti());
			}

			if (configuration.getFollowUpDuration() != null) {
				followUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				followUpDurations.put(disease, disease.getDefaultFollowUpDuration());
			}
			if (configuration.getCaseFollowUpDuration() != null) {
				caseFollowUpDurations.put(disease, configuration.getCaseFollowUpDuration());
			} else {
				caseFollowUpDurations.put(disease, followUpDurations.get(disease));
			}
			if (configuration.getFollowUpDuration() != null) {
				eventParticipantFollowUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				eventParticipantFollowUpDurations.put(disease, followUpDurations.get(disease));
			}
		}
	}

	public List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance) {
		return getAllDiseases(active, primary, caseSurveillance, false);
	}

	public List<Disease> getAllDiseases(Boolean active, Boolean primary, boolean caseSurveillance, boolean aggregateReporting) {

		User currentUser = ConfigProvider.getUser();

		Set<Disease> diseases = EnumSet.noneOf(Disease.class);

		if (caseSurveillance) {
			if (CollectionUtils.isNotEmpty(currentUser.getLimitedDiseases())) {
				diseases.addAll(currentUser.getLimitedDiseases());
			} else {
				diseases.addAll(caseBasedDiseases);
			}

			if (isTrue(primary)) {
				diseases.retainAll(primaryDiseases);
			} else if (isFalse(primary)) {
				diseases.retainAll(nonPrimaryDiseases);
			}
		}

		if (aggregateReporting) {
			diseases.addAll(aggregateDiseases);
		}

		if (isTrue(active)) {
			diseases.retainAll(activeDiseases);
		} else if (isFalse(active)) {
			diseases.retainAll(inactiveDiseases);
		}

		return diseases.stream().sorted(Comparator.comparing(Disease::toString)).collect(Collectors.toList());
	}

	private static boolean isFalse(Boolean value) {
		return Boolean.FALSE.equals(value);
	}

	private static boolean isTrue(Boolean value) {
		return Boolean.TRUE.equals(value);
	}

	public Disease getDefaultDisease() {
		List<Disease> diseases =
			getAllDiseases(true, true, true).stream().filter(d -> d != Disease.OTHER && d != Disease.UNDEFINED).collect(Collectors.toList());

		if (diseases.size() == 1) {
			return diseases.get(0);
		}

		return null;
	}

	public boolean isActiveDisease(Disease disease) {
		return activeDiseases.contains(disease);
	}

	public List<Disease> getAllActiveDiseases() {
		return activeDiseases;
	}

	public boolean isPrimaryDisease(Disease disease) {
		return primaryDiseases.contains(disease);
	}

	public boolean usesExtendedClassification(Disease disease) {
		return extendedClassificationDiseases.get(disease);
	}

	public boolean usesExtendedClassificationMulti(Disease disease) {
		return extendedClassificationMultiDiseases.get(disease);
	}

	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	public int getFollowUpDuration(Disease disease) {
		return followUpDurations.get(disease);
	}

	public int getCaseFollowUpDuration(Disease disease) {
		return caseFollowUpDurations.get(disease);
	}

	public int getEventParticipantFollowUpDuration(Disease disease) {
		return eventParticipantFollowUpDurations.get(disease);
	}

	public static DiseaseConfigurationCache getInstance() {
		if (instance == null) {
			instance = new DiseaseConfigurationCache();
		}

		return instance;
	}

	public static void reset() {
		instance = new DiseaseConfigurationCache();
	}
}
