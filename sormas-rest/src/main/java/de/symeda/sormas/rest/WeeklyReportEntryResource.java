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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.report.WeeklyReportEntryDto;
import de.symeda.sormas.api.report.WeeklyReportEntryFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

@Path("/weeklyreportentries")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class WeeklyReportEntryResource {

	@GET
	@Path("/all/{since}")
	public List<WeeklyReportEntryDto> getAllWeeklyReportEntries(@Context SecurityContext sc, @PathParam("since") long since) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<WeeklyReportEntryDto> weeklyReportEntries = FacadeProvider.getWeeklyReportEntryFacade().getAllWeeklyReportEntriesAfter(new Date(since), userDto.getUuid());
		return weeklyReportEntries;
	}
	
	@POST
	@Path("/query")
	public List<WeeklyReportEntryDto> getByUuids(@Context SecurityContext sc, List<String> uuids) {
		
		List<WeeklyReportEntryDto> result = FacadeProvider.getWeeklyReportEntryFacade().getByUuids(uuids);
		return result;
	}
	
	@POST
	@Path("/push")
	public Integer postWeeklyReportEntries(List<WeeklyReportEntryDto> dtos) {
		
		WeeklyReportEntryFacade weeklyReportEntryFacade = FacadeProvider.getWeeklyReportEntryFacade();
		for (WeeklyReportEntryDto dto : dtos) {
			weeklyReportEntryFacade.saveWeeklyReportEntry(dto);
		}
		
		return dtos.size();
	}
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getWeeklyReportEntryFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
	
}
