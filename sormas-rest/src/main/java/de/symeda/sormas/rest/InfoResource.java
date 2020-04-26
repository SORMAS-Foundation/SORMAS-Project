/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
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
@RolesAllowed({"USER", "REST_USER"})
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

		String appLegacyUrl = FacadeProvider.getConfigFacade().getAppLegacyUrl();
		int[] appLegacyVersion = VersionHelper.extractVersion(appLegacyUrl);
		if (VersionHelper.isVersion(appLegacyVersion))
		{
			if (!VersionHelper.isVersion(appVersion)) {
				return appLegacyUrl; // no version -> likely old app 0.22.0 or older
			} else if (VersionHelper.isEqual(appVersion, appLegacyVersion)) {
				return null; // keep legacy version
			} else if (VersionHelper.isBefore(appVersion, appLegacyVersion)) {
				return appLegacyUrl;
			}
		}
		
		return FacadeProvider.getConfigFacade().getAppUrl();
	}
	
	@GET
	@Path("/locale")
	public String getLocale() {
		return FacadeProvider.getConfigFacade().getCountryLocale();
	}
	
	@GET
	@Path("/checkcompatibility")
	public CompatibilityCheckResponse isCompatibleToApi(@QueryParam("appVersion") String appVersion) {
		return InfoProvider.get().isCompatibleToApi(appVersion);
	}
}
