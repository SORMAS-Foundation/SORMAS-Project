package de.symeda.sormas.api.report;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface WeeklyReportEntryFacade {

	List<WeeklyReportEntryDto> getAllWeeklyReportEntriesAfter(Date date, String userUuid);
	
	List<WeeklyReportEntryDto> getByUuids(List<String> uuids);
	
	WeeklyReportEntryDto saveWeeklyReportEntry(WeeklyReportEntryDto dto);
	
	List<String> getAllUuids(String userUuid);
	
}
