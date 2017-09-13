package de.symeda.sormas.api.report;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface WeeklyReportFacade {

	List<WeeklyReportDto> getAllWeeklyReportsAfter(Date date, String userUuid);
	
	List<WeeklyReportDto> getByUuids(List<String> uuids);
	
	WeeklyReportDto saveWeeklyReport(WeeklyReportDto dto);
	
	List<String> getAllUuids(String userUuid);
	
}
