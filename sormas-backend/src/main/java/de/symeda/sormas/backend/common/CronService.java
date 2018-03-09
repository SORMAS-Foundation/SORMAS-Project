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

import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;

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
	private TaskFacade taskFacade;

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
		// Remove all files with the sormas_export prefix from the export folder that are older than two hours
		Date now = new Date();
		File exportFolder = new File(configFacade.getExportPath());
		int numberOfDeletedFiles = 0;
		for (final File fileEntry : exportFolder.listFiles()) {
			// Skip the file if it's a directory or not a sormas export file
			if (!fileEntry.isFile() || !fileEntry.getName().startsWith("sormas_export")) {
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
