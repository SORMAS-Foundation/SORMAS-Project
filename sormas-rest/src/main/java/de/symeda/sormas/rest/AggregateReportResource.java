package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.report.AggregateReportCriteria;
import de.symeda.sormas.api.report.AggregateReportDto;
import de.symeda.sormas.api.report.AggregatedCaseCountDto;

@Path("/aggregatereports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class AggregateReportResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<AggregateReportDto> getAllAggregateReports(@PathParam("since") long since) {
		return FacadeProvider.getAggregateReportFacade().getAllAggregateReportsAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<AggregateReportDto> getByUuids(List<String> uuids) {
		List<AggregateReportDto> result = FacadeProvider.getAggregateReportFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postAggregateReports(@Valid List<AggregateReportDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getAggregateReportFacade()::saveAggregateReport);
		return result;
	}

	@POST
	@Path("/indexList")
	public List<AggregatedCaseCountDto> getIndexList(AggregateReportCriteria criteria) {
		return FacadeProvider.getAggregateReportFacade().getIndexList(criteria);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getAggregateReportFacade().getAllUuids();
	}
}
