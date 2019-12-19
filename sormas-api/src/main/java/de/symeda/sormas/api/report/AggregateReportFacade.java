package de.symeda.sormas.api.report;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

@Remote
public interface AggregateReportFacade {

	List<AggregateReportDto> getAllAggregateReportsAfter(Date date, String userUuid);
	
	List<AggregateReportDto> getByUuids(List<String> uuids);
	
	AggregateReportDto saveAggregateReport(AggregateReportDto report);
	
	List<String> getAllUuids(String userUuid);
	
	List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria, String userUuid);
	
}
