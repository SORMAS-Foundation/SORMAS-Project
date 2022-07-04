package de.symeda.sormas.api.report;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.validation.Valid;

@Remote
public interface AggregateReportFacade {

	List<AggregateReportDto> getAllAggregateReportsAfter(Date date);

	List<AggregateReportDto> getByUuids(List<String> uuids);

	AggregateReportDto saveAggregateReport(@Valid AggregateReportDto report);

	List<String> getAllUuids();

	List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria);

	List<AggregateReportDto> getAggregateReports(AggregateReportCriteria criteria);

	List<AggregateReportDto> getList(AggregateReportCriteria criteria);

	List<AggregateReportDto> getAllAggregatedReportsFromSameEpiWeekUserAndJurisdiction(AggregateReportDto aggregateReportDto);

	void deleteReport(String reportUuid);

	void deleteAggregatedReports(List<String> uuids);

	long countWithCriteria(AggregateReportCriteria criteria);
}
