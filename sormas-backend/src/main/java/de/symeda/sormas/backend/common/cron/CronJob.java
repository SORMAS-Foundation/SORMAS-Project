/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.common.cron;

import java.util.function.Consumer;

import de.symeda.sormas.backend.common.CronService;

public enum CronJob {

	SEND_NEW_AND_DUE_TASK_MESSAGES("0 */10 * * * *", CronService::sendNewAndDueTaskMessages),
	CALCULATE_CASE_COMPLETION("0 */2 * * * *", CronService::calculateCaseCompletion),
	DELETE_EXPIRED_FEATURE_CONFIGURATIONS("0 0 1 * * *", CronService::deleteAllExpiredFeatureConfigurations),
	GENERATE_AUTOMATIC_TASKS("0 5 1 * * *", CronService::generateAutomaticTasks),
	CLEAN_UP_TEMPORARY_FILES("0 10 1 * * *", CronService::cleanUpTemporaryFiles),
	ARCHIVE_CASES("0 15 1 * * *", CronService::archiveCases),
	ARCHIVE_EVENTS("0 20 1 * * *", CronService::archiveEvents),
	CLEANUP_DELETED_DOCUMENTS("0 25 1 * * *", CronService::cleanupDeletedDocuments),
	DELETE_SYSTEM_EVENTS("0 30 1 * * *", CronService::deleteSystemEvents),
	FETCH_EXTERNAL_MESSAGES("0 0 * * * *", CronService::fetchExternalMessages),
	FETCH_SURVEY_RESPONSES("0 0 * * * *", CronService::fetchSurveyResponses),
	UPDATE_IMMUNIZATION_STATUSES("0 40 1 * * *", CronService::updateImmunizationStatuses),
	SYNC_INFRA_WITH_CENTRAL("0 50 1 * * *", CronService::syncInfraWithCentral),
	DELETE_EXPIRED_ENTITIES("0 55 1 * * *", CronService::deleteExpiredEntities),
	ARCHIVE_CONTACTS("0 15 2 * * *", CronService::archiveContacts),
	ARCHIVE_EVENT_PARTICIPANTS("0 20 2 * * *", CronService::archiveEventParticipants),
	ARCHIVE_IMMUNIZATIONS("0 25 2 * * *", CronService::archiveImmunizations),
	ARCHIVE_TRAVEL_ENTRY("0 30 2 * * *", CronService::archiveTravelEntry),
	DELETE_EXPIRED_SPECIAL_CASE_ACCESSES("0 30 2 * * *", CronService::deleteExpiredSpecialCaseAccesses),
	SYNC_USERS_FROM_AUTH_PROVIDER("0 35 2 * * *", CronService::syncUsersFromAuthenticationProvider),
	SOFT_DELETE_OLD_NEGATIVE_SAMPLES("0 40 2 * * *", CronService::sofDeleteOldNegativeSamples);

	public static final String CONFIG_KEY_PREFIX = "CRON.";

	private final String defaultExpression;
	private final Consumer<CronService> invocation;

	CronJob(String defaultExpression, Consumer<CronService> invocation) {
		this.defaultExpression = defaultExpression;
		this.invocation = invocation;
	}

	public String getConfigKey() {
		return CONFIG_KEY_PREFIX + name();
	}

	public String getDefaultExpression() {
		return defaultExpression;
	}

	public void execute(CronService cronService) {
		invocation.accept(cronService);
	}
}
