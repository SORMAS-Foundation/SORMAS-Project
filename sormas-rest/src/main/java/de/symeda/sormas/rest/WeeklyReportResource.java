package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.report.WeeklyReportDto;
import de.symeda.sormas.api.report.WeeklyReportFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/weeklyreports")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class WeeklyReportResource {

	@GET
	@Path("/all/{since}")
	public List<WeeklyReportDto> getAllWeeklyReports(@Context SecurityContext sc, @PathParam("since") long since) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<WeeklyReportDto> weeklyReports = FacadeProvider.getWeeklyReportFacade().getAllWeeklyReportsAfter(new Date(since), userDto.getUuid());
		return weeklyReports;
	}
	
	@GET
	@Path("/query")
	public List<WeeklyReportDto> getByUuids(@Context SecurityContext sc, @QueryParam("uuids") List<String> uuids) {
		
		List<WeeklyReportDto> result = FacadeProvider.getWeeklyReportFacade().getByUuids(uuids);
		return result;
	}
	
	@POST
	@Path("/push")
	public Integer postWeeklyReports(List<WeeklyReportDto> dtos) {
		
		WeeklyReportFacade weeklyReportFacade = FacadeProvider.getWeeklyReportFacade();
		for (WeeklyReportDto dto : dtos) {
			weeklyReportFacade.saveWeeklyReport(dto);
		}
		
		return dtos.size();
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getWeeklyReportFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
	
}
