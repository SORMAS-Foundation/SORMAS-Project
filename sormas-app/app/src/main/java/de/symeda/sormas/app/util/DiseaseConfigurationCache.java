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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.app.backend.user.User;

public final class DiseaseConfigurationCache {

	private static DiseaseConfigurationCache instance;

	private List<Disease> activeDiseases = new ArrayList<>();
	private List<Disease> inactiveDiseases = new ArrayList<>();
	private List<Disease> primaryDiseases = new ArrayList<>();
	private List<Disease> nonPrimaryDiseases = new ArrayList<>();
	private List<Disease> caseBasedDiseases = new ArrayList<>();
	private List<Disease> aggregateDiseases = new ArrayList<>();
	private List<Disease> followUpEnabledDiseases = new ArrayList<>();
	private Map<Disease, Integer> followUpDurations = new HashMap<>();

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
			if (Boolean.TRUE.equals(configuration.getCaseBased()) || (configuration.getCaseBased() == null && disease.isDefaultCaseBased())) {
				caseBasedDiseases.add(disease);
			} else {
				aggregateDiseases.add(disease);
			}
			if (Boolean.TRUE.equals(configuration.getFollowUpEnabled())
				|| (configuration.getFollowUpEnabled() == null && disease.isDefaultFollowUpEnabled())) {
				followUpEnabledDiseases.add(disease);
			}
			if (configuration.getFollowUpDuration() != null) {
				followUpDurations.put(disease, configuration.getFollowUpDuration());
			} else {
				followUpDurations.put(disease, disease.getDefaultFollowUpDuration());
			}
		}
	}

	public List<Disease> getAllDiseases(Boolean active, Boolean primary, Boolean caseBased) {
		User currentUser = ConfigProvider.getUser();
		Set<Disease> diseases = new HashSet<>();

		if (Boolean.TRUE.equals(active)) {
			if (currentUser.getLimitedDisease() != null && activeDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(activeDiseases);
			}
		} else if (Boolean.FALSE.equals(active)) {
			if (currentUser.getLimitedDisease() != null && inactiveDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(inactiveDiseases);
			}
		}

		if (Boolean.TRUE.equals(primary)) {
			if (currentUser.getLimitedDisease() != null && primaryDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(primaryDiseases);
			}
		} else if (Boolean.FALSE.equals(primary)) {
			if (currentUser.getLimitedDisease() != null && nonPrimaryDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(nonPrimaryDiseases);
			}
		}

		if (Boolean.TRUE.equals(caseBased)) {
			if (currentUser.getLimitedDisease() != null && caseBasedDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(caseBasedDiseases);
			}
		} else if (Boolean.FALSE.equals(caseBased)) {
			if (currentUser.getLimitedDisease() != null && aggregateDiseases.contains(currentUser.getLimitedDisease())) {
				diseases.add(currentUser.getLimitedDisease());
			} else {
				diseases.addAll(aggregateDiseases);
			}
		}

		Iterator<Disease> iterator = diseases.iterator();
		while (iterator.hasNext()) {
			Disease disease = iterator.next();
			if (Boolean.TRUE.equals(active)) {
				if (inactiveDiseases.contains(disease)) {
					iterator.remove();
					continue;
				}
			} else if (Boolean.FALSE.equals(active)) {
				if (activeDiseases.contains(disease)) {
					iterator.remove();
					continue;
				}
			}

			if (Boolean.TRUE.equals(primary)) {
				if (nonPrimaryDiseases.contains(disease)) {
					iterator.remove();
					continue;
				}
			} else if (Boolean.FALSE.equals(primary)) {
				if (primaryDiseases.contains(disease)) {
					iterator.remove();
					continue;
				}
			}

			if (Boolean.TRUE.equals(caseBased)) {
				if (aggregateDiseases.contains(disease)) {
					iterator.remove();
				}
			} else if (Boolean.FALSE.equals(caseBased)) {
				if (caseBasedDiseases.contains(disease)) {
					iterator.remove();
				}
			}
		}

		List<Disease> diseaseList = new ArrayList<>(diseases);
		Collections.sort(diseaseList, new Comparator<Disease>() {

			@Override
			public int compare(Disease o1, Disease o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		return diseaseList;
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
		User currentUser = ConfigProvider.getUser();
		if (currentUser.getLimitedDisease() != null) {
			ArrayList<Disease> list = new ArrayList<>();
			if (isActiveDisease(currentUser.getLimitedDisease())) {
				list.add(currentUser.getLimitedDisease());
			}
			return list;
		} else {
			return activeDiseases;
		}
	}

	public boolean isPrimaryDisease(Disease disease) {
		return primaryDiseases.contains(disease);
	}

	public List<Disease> getAllPrimaryDiseases() {
		User currentUser = ConfigProvider.getUser();
		if (currentUser.getLimitedDisease() != null) {
			ArrayList<Disease> list = new ArrayList<>();
			if (isPrimaryDisease(currentUser.getLimitedDisease())) {
				list.add(currentUser.getLimitedDisease());
			}
			return list;
		} else {
			return primaryDiseases;
		}
	}

	public boolean hasFollowUp(Disease disease) {
		return followUpEnabledDiseases.contains(disease);
	}

	public List<Disease> getAllDiseasesWithFollowUp() {
		User currentUser = ConfigProvider.getUser();
		if (currentUser.getLimitedDisease() != null) {
			ArrayList<Disease> list = new ArrayList<>();
			if (hasFollowUp(currentUser.getLimitedDisease())) {
				list.add(currentUser.getLimitedDisease());
			}
			return list;
		} else {
			return followUpEnabledDiseases;
		}
	}

	public int getFollowUpDuration(Disease disease) {
		return followUpDurations.get(disease);
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
