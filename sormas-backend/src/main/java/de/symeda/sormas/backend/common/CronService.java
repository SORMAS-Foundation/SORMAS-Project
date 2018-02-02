package de.symeda.sormas.backend.common;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;

@Singleton
@RunAs(UserRole._SYSTEM)
public class CronService {

	// Adjust this when changing the minute value of the runRepeatedlyPerHour method
	public static final int REPEATEDLY_PER_HOUR_INTERVAL = 10;
	
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private WeeklyReportFacadeEjbLocal weeklyReportFacade;
	@EJB
	private TaskFacade taskFacade;
	
	@Schedule(hour = "4", minute = "0", second = "0", persistent=false)
    public void runEveryNight() {
		contactFacade.generateContactFollowUpTasks();		
		weeklyReportFacade.generateSubmitWeeklyReportTasks();
    }
	
	@Schedule(hour = "*/1", minute = "*/10", second = "0", persistent = false)
	public void runRepeatedlyPerHour() {
		taskFacade.sendNewAndDueTaskMessages();
	}
	
}
