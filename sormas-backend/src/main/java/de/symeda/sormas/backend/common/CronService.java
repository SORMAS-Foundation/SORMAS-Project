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

package de.symeda.sormas.backend.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.common.DeletableEntityType;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.feature.FeatureTypeProperty;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.deletionconfiguration.CoreEntityDeletionService;
import de.symeda.sormas.backend.document.DocumentFacadeEjb.DocumentFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventParticipantFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationFacadeEjb;
import de.symeda.sormas.backend.infrastructure.central.CentralInfraSyncFacade;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.specialcaseaccess.SpecialCaseAccessFacadeEjb.SpecialCaseAccessFacadeEjbLocal;
import de.symeda.sormas.backend.systemevent.SystemEventFacadeEjb.SystemEventFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;
import de.symeda.sormas.backend.travelentry.TravelEntryFacadeEjb;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Singleton
@RunAs(UserRight._SYSTEM)
public class CronService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private WeeklyReportFacadeEjbLocal weeklyReportFacade;
	@EJB
	private TaskFacadeEjbLocal taskFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private EventFacadeEjbLocal eventFacade;
	@EJB
	private EventParticipantFacadeEjb.EventParticipantFacadeEjbLocal eventParticipantFacade;
	@EJB
	private DocumentFacadeEjbLocal documentFacade;
	@EJB
	private SystemEventFacadeEjbLocal systemEventFacade;
	@EJB
	private ExternalMessageFacadeEjbLocal externalMessageFacade;
	@EJB
	private ImmunizationFacadeEjb.ImmunizationFacadeEjbLocal immunizationFacade;
	@EJB
	private TravelEntryFacadeEjb.TravelEntryFacadeEjbLocal travelEntryFacade;
	@EJB
	private CentralInfraSyncFacade centralInfraSyncFacade;
	@EJB
	private CoreEntityDeletionService coreEntityDeletionService;
	@EJB
	private SpecialCaseAccessFacadeEjbLocal specialCaseAccessFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private SampleService sampleService;

	public void sendNewAndDueTaskMessages() {
		taskFacade.sendNewAndDueTaskMessages();
	}

	public void calculateCaseCompletion() {
		long timeStart = DateHelper.startTime();
		int casesUpdated = caseFacade.updateCompleteness();
		logger.debug("calculateCaseCompletion finished. {} cases, {} s", casesUpdated, DateHelper.durationSeconds(timeStart));
	}

	public void deleteAllExpiredFeatureConfigurations() {

		// Remove all feature configurations whose end dates have been reached
		featureConfigurationFacade.deleteAllExpiredFeatureConfigurations(new Date());
		logger.info("Deleted expired feature configurations");
	}

	public void generateAutomaticTasks() {

		if (featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CONTACT_FOLLOW_UP)) {
			contactFacade.generateContactFollowUpTasks();
		}
		if (featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.WEEKLY_REPORT_GENERATION)) {
			weeklyReportFacade.generateSubmitWeeklyReportTasks();
		}
	}

	public void cleanUpTemporaryFiles() {

		Date now = new Date();
		File exportFolder = new File(configFacade.getTempFilesPath());

		int numberOfDeletedFiles = 0;
		final File[] files = exportFolder.listFiles();
		if (files == null) {
			logger.warn("No files found in export folder {}", exportFolder.getAbsolutePath());
			return;
		}

		for (final File fileEntry : files) {
			// Skip the file if it's a directory or not a temporary sormas file
			if (!fileEntry.isFile() || (!fileEntry.getName().startsWith(ImportExportUtils.TEMP_FILE_PREFIX))) {
				continue;
			}

			try {
				BasicFileAttributes fileAttributes = Files.readAttributes(fileEntry.toPath(), BasicFileAttributes.class);
				if (now.getTime() - fileAttributes.creationTime().toMillis() >= 1000 * 60 * 120) {
					Files.delete(fileEntry.toPath());
					numberOfDeletedFiles++;
				}
			} catch (IOException e) {
				logger.info("Error deleting a file in CronService. The file in question was " + fileEntry.getAbsolutePath(), e);
			}
		}

		logger.info("Deleted {} export files", numberOfDeletedFiles);
	}

	public void archiveCases() {

		final int daysAfterCaseGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.CASE, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);
		final int daysAfterContactsGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.CONTACT, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);
		if (daysAfterCaseGetsArchived < daysAfterContactsGetsArchived) {
			logger.warn(
				"{} for {} [{}] should be <= the one for {} [{}]",
				FeatureTypeProperty.THRESHOLD_IN_DAYS,
				DeletableEntityType.CONTACT,
				DeletableEntityType.CASE,
				daysAfterContactsGetsArchived,
				daysAfterCaseGetsArchived);
		}
		if (daysAfterCaseGetsArchived >= 1) {
			caseFacade.archiveAllArchivableCases(daysAfterCaseGetsArchived);
		}
	}

	public void archiveEvents() {

		final int daysAfterEventsGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.EVENT, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);
		final int daysAfterEventParticipantsGetsArchived = featureConfigurationFacade.getProperty(
			FeatureType.AUTOMATIC_ARCHIVING,
			DeletableEntityType.EVENT_PARTICIPANT,
			FeatureTypeProperty.THRESHOLD_IN_DAYS,
			Integer.class);
		if (daysAfterEventsGetsArchived < daysAfterEventParticipantsGetsArchived) {
			logger.warn(
				"{} for {} [{}] should be <= the one for {} [{}]",
				FeatureTypeProperty.THRESHOLD_IN_DAYS,
				DeletableEntityType.EVENT_PARTICIPANT,
				DeletableEntityType.EVENT,
				daysAfterEventParticipantsGetsArchived,
				daysAfterEventsGetsArchived);
		}
		if (daysAfterEventsGetsArchived >= 1) {
			eventFacade.archiveAllArchivableEvents(daysAfterEventsGetsArchived);
		}
	}

	public void cleanupDeletedDocuments() {
		documentFacade.cleanupDeletedDocuments();
	}

	public void deleteSystemEvents() {
		int daysAfterSystemEventGetsDeleted = configFacade.getDaysAfterSystemEventGetsDeleted();
		if (daysAfterSystemEventGetsDeleted >= 1) {
			systemEventFacade.deleteAllDeletableSystemEvents(daysAfterSystemEventGetsDeleted);
		}
	}

	public void fetchExternalMessages() {
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.EXTERNAL_MESSAGES)) {
			externalMessageFacade.fetchAndSaveExternalMessages(null);
		}
	}

	public void fetchSurveyResponses() {
		if (!featureConfigurationFacade.isFeatureEnabled(FeatureType.EXTERNAL_MESSAGES)
			|| !featureConfigurationFacade.isPropertyValueTrue(FeatureType.EXTERNAL_MESSAGES, FeatureTypeProperty.SURVEY_FETCH_ENABLED)) {
			logger.info("External messages are disabled, survey responses will not be fetched");
			return;
		}

		List<ExternalMessageDto> surveyExternalMessages = externalMessageFacade.saveAndProcessSurveyResponses();

		if (logger.isInfoEnabled()) {
			List<String> reportIds = surveyExternalMessages.stream().map(ExternalMessageDto::getReportId).collect(Collectors.toList());
			if (!reportIds.isEmpty()) {
				logger.info("Survey responses with following reportIds were saved: [{}]", reportIds);
			}
		}
	}

	public void updateImmunizationStatuses() {
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.IMMUNIZATION_STATUS_AUTOMATION)) {
			immunizationFacade.updateImmunizationStatuses();
		}
	}

	public void syncInfraWithCentral() {
		centralInfraSyncFacade.syncAll();
	}

	public void deleteExpiredEntities() {
		coreEntityDeletionService.executeAutomaticDeletion();
	}

	public void archiveContacts() {
		final int daysAfterContactsGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.CONTACT, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);

		if (daysAfterContactsGetsArchived >= 1) {
			contactFacade.archiveAllArchivableContacts(daysAfterContactsGetsArchived);
		}
	}

	public void archiveEventParticipants() {
		final int daysAfterEventParticipantGetsArchived = featureConfigurationFacade.getProperty(
			FeatureType.AUTOMATIC_ARCHIVING,
			DeletableEntityType.EVENT_PARTICIPANT,
			FeatureTypeProperty.THRESHOLD_IN_DAYS,
			Integer.class);

		if (daysAfterEventParticipantGetsArchived >= 1) {
			eventParticipantFacade.archiveAllArchivableEventParticipants(daysAfterEventParticipantGetsArchived);
		}
	}

	public void archiveImmunizations() {
		final int daysAfterImmunizationsGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.IMMUNIZATION, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);

		if (daysAfterImmunizationsGetsArchived >= 1) {
			immunizationFacade.archiveAllArchivableImmunizations(daysAfterImmunizationsGetsArchived);
		}
	}

	public void archiveTravelEntry() {
		final int daysAfterTravelEntryGetsArchived = featureConfigurationFacade
			.getProperty(FeatureType.AUTOMATIC_ARCHIVING, DeletableEntityType.TRAVEL_ENTRY, FeatureTypeProperty.THRESHOLD_IN_DAYS, Integer.class);

		if (daysAfterTravelEntryGetsArchived >= 1) {
			travelEntryFacade.archiveAllArchivableTravelEntries(daysAfterTravelEntryGetsArchived);
		}
	}

	public void deleteExpiredSpecialCaseAccesses() {
		specialCaseAccessFacade.deleteExpiredSpecialCaseAccesses();
	}

	public void syncUsersFromAuthenticationProvider() {
		if (userFacade.isSyncEnabled() && featureConfigurationFacade.isFeatureEnabled(FeatureType.AUTH_PROVIDER_TO_SORMAS_USER_SYNC)) {
			userFacade.syncUsersFromAuthenticationProvider();
		}
	}

	public void sofDeleteOldNegativeSamples() {
		sampleService.cleanupOldCovidSamples();
	}
}
