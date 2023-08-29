package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;

@Path("/environments")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class EnvironmentResource extends EntityDtoResource<EnvironmentDto> {

	@GET
	@Path("/all/{since}")
	public List<EnvironmentDto> getAllEnvironments(@PathParam("since") long since) {
		return FacadeProvider.getEnvironmentFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<EnvironmentDto> getAllEnvironments(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getEnvironmentFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<EnvironmentDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getEnvironmentFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getEnvironmentFacade().getAllUuids();
	}

	@GET
	@Path("/{uuid}")
	public EnvironmentDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getEnvironmentFacade().getByUuid(uuid);
	}

	@GET
	@Path("/obsolete/{since}")
	public List<String> getObsoleteUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getEnvironmentFacade().getObsoleteUuidsSince(new Date(since));
	}

	@Override
	public UnaryOperator<EnvironmentDto> getSave() {
		return FacadeProvider.getEnvironmentFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<EnvironmentDto> environmentDtos) {
		return super.postEntityDtos(environmentDtos);
	}
}
