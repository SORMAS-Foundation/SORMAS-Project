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
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;
import de.symeda.sormas.backend.task.TaskFacadeEjb.TaskFacadeEjbLocal;

@Singleton
@RunAs(UserRole._SYSTEM)
public class CronService {
	
	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private WeeklyReportFacadeEjbLocal weeklyReportFacade;
	@EJB
	private TaskFacadeEjbLocal taskFacade;

	public static final int REPEATEDLY_PER_HOUR_INTERVAL = 10;
	
	private static final Logger logger = LoggerFactory.getLogger(CaseFacadeEjb.class);
	
	@Schedule(hour = "4", minute = "0", second = "0", persistent=false)
    public void runEveryNight() {
		contactFacade.generateContactFollowUpTasks();		
		weeklyReportFacade.generateSubmitWeeklyReportTasks();
    }
	
	@Schedule(hour = "*", minute = "*/" + REPEATEDLY_PER_HOUR_INTERVAL, second = "0", persistent = false)
	public void runRepeatedlyPerHour() {
		taskFacade.sendNewAndDueTaskMessages();
	}
	
	@Schedule(hour = "0", minute ="0", second = "0", persistent = false)
	public void runAtMidnight() {
		// Remove all files with the sormas prefix from the export folder that are older than two hours
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
	
}
