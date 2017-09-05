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
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;
import de.symeda.sormas.api.user.UserReferenceDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/tasks")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@Consumes({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class TaskResource {

	@GET
	@Path("/all/{since}")
	public List<TaskDto> getAll(@Context SecurityContext sc, @PathParam("since") long since) {

		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<TaskDto> result = FacadeProvider.getTaskFacade().getAllAfter(new Date(since), userDto.getUuid()); 
		return result;
	}
	
	@GET
	@Path("/query")
	public List<TaskDto> getByUuids(@Context SecurityContext sc, @QueryParam("uuids") List<String> uuids) {

		List<TaskDto> result = FacadeProvider.getTaskFacade().getByUuids(uuids); 
		return result;
	}
	
	@POST 
	@Path("/push")
	public Integer postTasks(List<TaskDto> dtos) {
		
		TaskFacade raskFacade = FacadeProvider.getTaskFacade();
		for (TaskDto dto : dtos) {
			raskFacade.saveTask(dto);
		}
		
		return dtos.size();
	}
	
	
	@GET
	@Path("/uuids")
	public List<String> getAllUuids(@Context SecurityContext sc) {
		
		UserReferenceDto userDto = FacadeProvider.getUserFacade().getByUserNameAsReference(sc.getUserPrincipal().getName());
		List<String> uuids = FacadeProvider.getTaskFacade().getAllUuids(userDto.getUuid());
		return uuids;
	}
}
