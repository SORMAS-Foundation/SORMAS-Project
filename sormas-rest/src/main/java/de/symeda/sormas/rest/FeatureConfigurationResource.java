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
import de.symeda.sormas.api.feature.FeatureConfigurationDto;

@Path("/featureconfigurations")
@Produces({ MediaType.APPLICATION_JSON + "; charset=UTF-8" })
@Consumes({ MediaType.APPLICATION_JSON + "; charset=UTF-8" })
@RolesAllowed("USER")
public class FeatureConfigurationResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<FeatureConfigurationDto> getAllFeatureConfigurations(@PathParam("since") long since) {
		return FacadeProvider.getFeatureConfigurationFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<FeatureConfigurationDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getFeatureConfigurationFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getFeatureConfigurationFacade().getAllUuids();
	}
	
	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuids(@PathParam("since") long since) {
		return FacadeProvider.getFeatureConfigurationFacade().getDeletedUuids(new Date(since));
	}
	
}
