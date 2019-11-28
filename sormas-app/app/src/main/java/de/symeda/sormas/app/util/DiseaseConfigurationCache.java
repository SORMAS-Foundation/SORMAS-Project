/*
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
 */

package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.config.ConfigProvider;
import de.symeda.sormas.app.backend.disease.DiseaseConfiguration;
import de.symeda.sormas.app.backend.user.User;

public class DiseaseConfigurationCache {

    private static DiseaseConfigurationCache instance;

    private List<Disease> activeDiseases = new ArrayList<>();
    private List<Disease> primaryDiseases = new ArrayList<>();
    private List<Disease> activePrimaryDiseases = new ArrayList<>();
    private List<Disease> followUpEnabledDiseases = new ArrayList<>();
    private Map<Disease, Integer> followUpDurations = new HashMap<>();

    private DiseaseConfigurationCache() {
        for (Disease disease : Disease.values()) {
            DiseaseConfiguration configuration = DatabaseHelper.getDiseaseConfigurationDao().getDiseaseConfiguration(disease);
            if (configuration == null) {
                //Create empty DiseaseConfiguration to use the default values.
                configuration = new DiseaseConfiguration();
            }

            boolean diseaseActive = false;
            boolean diseasePrimary = false;
            if (Boolean.TRUE.equals(configuration.getActive())
                    || (configuration.getActive() == null && disease.isDefaultActive())) {
                activeDiseases.add(disease);
                diseaseActive = true;
            }
            if (Boolean.TRUE.equals(configuration.getPrimaryDisease())
                    || (configuration.getPrimaryDisease() == null && disease.isDefaultPrimary())) {
                primaryDiseases.add(disease);
                diseasePrimary = true;
            }
            if (diseaseActive && diseasePrimary) {
                activePrimaryDiseases.add(disease);
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

    public List<Disease> getAllActivePrimaryDiseases() {
        User currentUser = ConfigProvider.getUser();
        if (currentUser.getLimitedDisease() != null) {
            ArrayList<Disease> list = new ArrayList<>();
            if (isPrimaryDisease(currentUser.getLimitedDisease()) && isActiveDisease(currentUser.getLimitedDisease())) {
                list.add(currentUser.getLimitedDisease());
            }
            return list;
        } else {
            return activePrimaryDiseases;
        }
    }

    public boolean hasFollowUp(Disease disease) {
        return followUpEnabledDiseases.contains(disease);
    }

    public List<Disease> getAllDiseasesWithFollowUp() {User currentUser = ConfigProvider.getUser();
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

    public static void reset(){
        instance = new DiseaseConfigurationCache();
    }
}
