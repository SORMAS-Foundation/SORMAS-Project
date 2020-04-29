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
package de.symeda.sormas.backend.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.task.TaskContext;
import de.symeda.sormas.api.task.TaskType;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.event.EventFacadeEjb.EventFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;

@Singleton
@RunAs(UserRole._SYSTEM)
public class CronService {

	public static final int TASK_UPDATE_INTERVAL = 10;

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

	@Schedule(hour = "*", minute = "*/" + TASK_UPDATE_INTERVAL, second = "0", persistent = false)
	public void sendNewAndDueTaskMessages() {
		taskFacade.sendNewAndDueTaskMessages();
	}

	@Schedule(hour = "1", minute = "0", second = "0", persistent = false)
	public void deleteAllExpiredFeatureConfigurations() {
		// Remove all feature configurations whose end dates have been reached
		featureConfigurationFacade.deleteAllExpiredFeatureConfigurations(new Date());

		logger.info("Deleted expired feature configurations");
	}

	@Schedule(hour = "1", minute = "5", second = "0", persistent = false)
	public void generateAutomaticTasks() {
		if (featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.CONTACT_FOLLOW_UP)) {
			contactFacade.generateContactFollowUpTasks();
		}
		if (featureConfigurationFacade.isTaskGenerationFeatureEnabled(TaskType.WEEKLY_REPORT_GENERATION)) {
			weeklyReportFacade.generateSubmitWeeklyReportTasks();
		}
	}

	@Schedule(hour = "1", minute = "10", second = "0", persistent = false)
	public void cleanUpTemporaryFiles() {
		Date now = new Date();
		File exportFolder = new File(configFacade.getTempFilesPath());
		int numberOfDeletedFiles = 0;
		for (final File fileEntry : exportFolder.listFiles()) {
			// Skip the file if it's a directory or not a temporary sormas file
			if (!fileEntry.isFile() || (!fileEntry.getName().startsWith(ImportExportUtils.TEMP_FILE_PREFIX))) {
				continue;
			}

			try {
				BasicFileAttributes fileAttributes = Files.readAttributes(fileEntry.toPath(), BasicFileAttributes.class);
				if (now.getTime() - fileAttributes.creationTime().toMillis() >= 1000 * 60 * 120) {
					fileEntry.delete();
					numberOfDeletedFiles++;
				}
			} catch (IOException e) {
				logger.info("Error deleting a file in CronService. The file in question was " + fileEntry.getAbsolutePath(), e);
			}
		}

		logger.info("Deleted " + numberOfDeletedFiles + " export files");
	}

	@Schedule(hour = "1", minute = "15", second = "0", persistent = false)
	public void archiveCases() {

		int daysAfterCaseGetsArchived = configFacade.getDaysAfterCaseGetsArchived();
		if (daysAfterCaseGetsArchived >= 1) {
			caseFacade.archiveAllArchivableCases(daysAfterCaseGetsArchived);
		}
	}

	@Schedule(hour = "1", minute = "20", second = "0", persistent = false)
	public void archiveEvents() {

		int daysAfterEventsGetsArchived = configFacade.getDaysAfterEventGetsArchived();
		if (daysAfterEventsGetsArchived >= 1) {
			eventFacade.archiveAllArchivableEvents(daysAfterEventsGetsArchived);
		}
	}
}

