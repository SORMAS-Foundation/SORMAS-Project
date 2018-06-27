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
import de.symeda.sormas.api.utils.VersionHelper;

@Path("/info")
@Produces({MediaType.APPLICATION_JSON + "; charset=UTF-8"})
@RolesAllowed("USER")
public class InfoResource {

	@GET
	@Path("/version")
	public String getVersion() {	
		return InfoProvider.get().getVersion();
	}
	
	@GET
	@Path("/appurl")
	public String getAppUrl(@QueryParam("appVersion") String appVersionString) {
		
		int[] appVersion = VersionHelper.extractVersion(appVersionString);
		if (VersionHelper.isVersion(appVersion)) {
			String appLegacyUrl = FacadeProvider.getConfigFacade().getAppLegacyUrl();
			int[] appLegacyVersion = VersionHelper.extractVersion(appLegacyUrl);
			if (VersionHelper.isVersion(appLegacyVersion)
				&& !VersionHelper.isAfter(appVersion, appLegacyVersion)) {
				// app legacy URL for versions before or equal to the legacy version
				return appLegacyUrl;
			}
		}
		
		return FacadeProvider.getConfigFacade().getAppUrl();
	}
	
	@GET
	@Path("/checkcompatibility")
	public CompatibilityCheckResponse isCompatibleToApi(@QueryParam("appVersion") String appVersion) {
		return InfoProvider.get().isCompatibleToApi(appVersion);
	}
}
