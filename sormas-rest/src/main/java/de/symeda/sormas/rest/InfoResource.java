package de.symeda.sormas.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.InfoProvider;

@Path("/info")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class InfoResource {

	@GET
	@Path("/version")
	public String getVersion() {	
//		return InfoProvider.getVersion();
		return "0.21.0";
	}
	
	@GET
	@Path("/appurl")
	public String getAppUrl(@QueryParam("appVersion") String appVersion) {
		return FacadeProvider.getConfigFacade().getAppUrl();
	}
	
	@GET
	@Path("/checkcompatibility")
	public CompatibilityCheckResponse isCompatibleToApi(@QueryParam("appVersion") String appVersion) {
		return InfoProvider.isCompatibleToApi(appVersion);
	}
}
