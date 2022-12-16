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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.utils.CompatibilityCheckResponse;
import de.symeda.sormas.api.utils.InfoProvider;
import de.symeda.sormas.api.utils.VersionHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Info Resource",
	description = "Read-only access to information about the configuration of a SORMAS server w.r.t. server-client compatibility.")
public class InfoResource {

	@GET
	@Path("/version")
	@Operation(summary = "Get information on the SORMAS server version.")
	@ApiResponse(description = "Returns the SORMAS server's version string.", responseCode = "200", useReturnTypeSchema = true)
	public String getVersion() {
		return InfoProvider.get().getVersion();
	}

	@GET
	@Path("/appurl")
	@Operation(summary = "TBD_RESTAPI_SWAGGER_DOC")
	@ApiResponse(description = "TBD_RESTAPI_SWAGGER_DOC", responseCode = "200", useReturnTypeSchema = true)
	public String getAppUrl(@Parameter(required = true, description = "Client version string") @QueryParam("appVersion") String appVersionString) {

		int[] appVersion = VersionHelper.extractVersion(appVersionString);

		String appLegacyUrl = FacadeProvider.getConfigFacade().getAppLegacyUrl();
		int[] appLegacyVersion = VersionHelper.extractVersion(appLegacyUrl);
		if (VersionHelper.isVersion(appLegacyVersion)) {
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
	@Operation(summary = "Get the langauge/localization the SORMAS server is configured for.")
	@ApiResponse(description = "Returns the configured localization language of the SORMAS server (e.g. \"de-DE\").",
		responseCode = "200",
		useReturnTypeSchema = true)
	public String getLocale() {
		return FacadeProvider.getConfigFacade().getCountryLocale();
	}

	@GET
	@Path("/checkcompatibility")
	@Operation(summary = "Check whether the client is compatible with the addressed SORMAS server.")
	@ApiResponse(description = "Returns a CompatibilityCheckResponse denotng the compatibility status.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public CompatibilityCheckResponse isCompatibleToApi(
		@Parameter(required = true, description = "Client version string") @QueryParam("appVersion") String appVersion) {
		return InfoProvider.get().isCompatibleToApi(appVersion);
	}

	@GET
	@Path("/countryname")
	@Operation(summary = "Get the name of the country the SORMAS server is configured for.")
	@ApiResponse(description = "Returns the configured country name of the SORMAS server.", responseCode = "200", useReturnTypeSchema = true)
	public String getCountryName() {
		return FacadeProvider.getConfigFacade().getCountryName();
	}
}
