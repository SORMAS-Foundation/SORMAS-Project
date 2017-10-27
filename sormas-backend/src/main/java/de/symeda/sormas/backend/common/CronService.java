package de.symeda.sormas.backend.common;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.report.WeeklyReportFacadeEjb.WeeklyReportFacadeEjbLocal;

@Singleton
@RunAs(UserRole._SYSTEM)
public class CronService {

	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private WeeklyReportFacadeEjbLocal weeklyReportFacade;
	
	@Schedule(hour = "4", minute = "0", second = "0", persistent=false)
    public void runEveryNight() {
		
		contactFacade.generateContactFollowUpTasks();		
		weeklyReportFacade.generateSubmitWeeklyReportTasks();
    }   
}
