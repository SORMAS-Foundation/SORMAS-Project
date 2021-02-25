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

	List<AggregateReportDto> getList(AggregateReportCriteria criteria);

	void deleteReport(String reportUuid);

	long countWithCriteria(AggregateReportCriteria criteria);
}
