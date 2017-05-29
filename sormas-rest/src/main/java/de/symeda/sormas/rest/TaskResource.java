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
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.task.TaskDto;
import de.symeda.sormas.api.task.TaskFacade;

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
	@Path("/all/{user}/{since}")
	public List<TaskDto> getAll(@PathParam("user") String userUuid, @PathParam("since") long since) {
		List<TaskDto> result = FacadeProvider.getTaskFacade().getAllAfter(new Date(since), userUuid); 
		return result;
	}
	
	@POST 
	@Path("/push")
	public Long postTasks(List<TaskDto> dtos) {
		
		TaskFacade raskFacade = FacadeProvider.getTaskFacade();
		for (TaskDto dto : dtos) {
			raskFacade.saveTask(dto);
		}
		
		return new Date().getTime();
	}
}
