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

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/diseaseconfigurations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Disease Configuration Resource", description = "Configuration data for supported diseases")
public class DiseaseConfigurationResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all disease configuration data from a date in the past until now.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of disease configuration data for the given interval.",
		useReturnTypeSchema = true)
	public List<DiseaseConfigurationDto> getAllDiseaseConfigurations(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getDiseaseConfigurationFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get disease configuration data entries based on their unique IDs (UUIDs)")
	@ApiResponse(responseCode = "200", description = "Returns a list of disease configuration data based on the queried UUIDs")
	public List<DiseaseConfigurationDto> getByUuids(
		@RequestBody(description = "List of disease configuration UUIDs used for the query", required = true) List<String> uuids) {
		return FacadeProvider.getDiseaseConfigurationFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDS) of all disease configuraton data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getDiseaseConfigurationFacade().getAllUuids();
	}

	@GET
	@Path("/diseaseNames")
	@Operation(summary = "Get all diseases that fulfill the given filter criteria.")
	@ApiResponse(responseCode = "200", description = "Returns a list of diseases", useReturnTypeSchema = true)
	public List<Disease> getDiseases(
		@Parameter(description = "Whether the disease is set to active (whether the server is configured to tracking the disease)",
			required = true) @QueryParam("active") boolean active,
		@Parameter(
			description = "Whether the disease is set to primary (whether the the course of the diseasee will be prominently displayed in dashboard)",
			required = true) @QueryParam("primary") boolean primary,
		@Parameter(description = "TBD_RESTAPI_SWAGGER_DOC", required = true) @QueryParam("caseBased") boolean caseBased) {
		return FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(active, primary, caseBased);
	}

	@GET
	@Path("/diseaseVariants")
	@Operation(summary = "Get the different variants of a disease based on the name of the disease.")
	@ApiResponse(responseCode = "200", description = "Returns a List of disease variants.", useReturnTypeSchema = true)
	public List<CustomizableEnum> getDiseaseVariants(
		@Parameter(required = true, description = "Name of the disease that will be queried for variants") @QueryParam("disease") String disease) {
		return FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, Disease.valueOf(disease));
	}

}
