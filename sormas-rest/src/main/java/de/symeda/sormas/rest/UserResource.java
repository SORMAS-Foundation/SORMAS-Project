package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.user.UserDto;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/users")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class UserResource {

	@GET @Path("/all/{since}")
	public List<UserDto> getAll(@PathParam("since") long since) {
		return FacadeProvider.getUserFacade().getAllAfter(new Date(since));
	}
}
