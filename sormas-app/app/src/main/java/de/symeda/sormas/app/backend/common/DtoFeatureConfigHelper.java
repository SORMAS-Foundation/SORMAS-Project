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

package de.symeda.sormas.app.backend.common;

import de.symeda.sormas.api.feature.FeatureType;

public class DtoFeatureConfigHelper {

	public static boolean isFeatureConfigForCaseEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CASE_SURVEILANCE);
	}

	public static boolean isFeatureConfigForImmunizationEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.IMMUNIZATION_MANAGEMENT);
	}

	public static boolean isFeatureConfigForEventsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE);
	}

	public static boolean isFeatureConfigForEventParticipantsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.EVENT_SURVEILLANCE);
	}

	public static boolean isFeatureConfigForSampleEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.SAMPLES_LAB);
	}

	public static boolean isFeatureConfigForPathogenTestsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.SAMPLES_LAB)
			|| DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.ENVIRONMENT_MANAGEMENT);
	}

	public static boolean isFeatureConfigForAdditionalTestsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.ADDITIONAL_TESTS);
	}

	public static boolean isFeatureConfigForContactsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CONTACT_TRACING);
	}

	public static boolean isFeatureConfigForVisitsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CASE_FOLLOWUP)
			|| DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CONTACT_TRACING);
	}

	public static boolean isFeatureConfigForTasksEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.TASK_MANAGEMENT);
	}

	public static boolean isFeatureConfigForWeeklyReportsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.WEEKLY_REPORTING);
	}

	public static boolean isFeatureConfigForAggregateReportsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.AGGREGATE_REPORTING);
	}

	public static boolean isFeatureConfigForPrescriptionsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CLINICAL_MANAGEMENT);
	}

	public static boolean isFeatureConfigForTreatmentsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CLINICAL_MANAGEMENT);
	}

	public static boolean isFeatureConfigForClinicalVisitsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CLINICAL_MANAGEMENT);
	}

	public static boolean isFeatureConfigForEnvironmentEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.ENVIRONMENT_MANAGEMENT);
	}

	public static boolean isFeatureConfigForEnvironmentSamplesEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.ENVIRONMENT_MANAGEMENT);
	}

	public static boolean isFeatureConfigForCampaignsEnabled() {
		return DatabaseHelper.getFeatureConfigurationDao().isFeatureEnabled(FeatureType.CAMPAIGNS);
	}
}
