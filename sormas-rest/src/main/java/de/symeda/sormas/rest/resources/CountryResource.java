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

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryIndexDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/countries")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Country Resource",
	description = "Access to general geographic location following the hierarchy:\n\n"
		+ "Continent > Subcontinent > **Country** > Area > Region > District > Community > Facility\n\n"
		+ "Allows countries/districts/communities to set-up their own sub-divided infrastructure conforming to the centralized SORMAS base structure.")
public class CountryResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all avaliable countries from a date in the past until now.")
	@ApiResponse(description = "Returns a list of countries for the given time interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<CountryDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT.") @PathParam("since") long since) {
		return FacadeProvider.getCountryFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get a list of countries based on their unique IDs (UUIDs).")
	@ApiResponse(description = "Returns a list of countries by UUIDs. If a UUID does not match to any country, it is ignored.")
	public List<CountryDto> getByUuids(
		@RequestBody(description = "List of country UUIDs. These UUIDs are used to query countries.", required = true) List<String> uuids) {
		return FacadeProvider.getCountryFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available countries.")
	@ApiResponse(description = "Returns a list of available countries' UUIDs.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getCountryFacade().getAllUuids();
	}

	@POST
	@Path("/push")
	@Operation(summary = "Add a list of countries that should be created or updated.")
	@ApiResponse(description = "Returns a list with a push result for each country.", responseCode = "200", useReturnTypeSchema = true)
	public List<PushResult> postCountries(
		@RequestBody(description = "List of countries to create or update.", required = true) @Valid List<CountryDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getCountryFacade()::save);
		return result;
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of CountryIndices based on CountryCriteria filter params.")
	@ApiResponse(description = "Returns a page of countries that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<CountryIndexDto> getIndexList(
		@RequestBody(description = "Country-based query-filter criteria with sorting property.",
			required = true) CriteriaWithSorting<CountryCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(required = true, description = "page offset") int offset,
		@QueryParam("size") @Parameter(required = true, description = "page size") int size) {
		return FacadeProvider.getCountryFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/archive")
	@Operation(summary = "Mark countries as archived based on their unique IDs (UUIDs); i.e. deactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which archiving was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> archive(
		@RequestBody(description = "List of country UUIDs. These UUIDs denote the countries to be archived.", required = true) List<String> uuids) {
		return FacadeProvider.getCountryFacade().archive(uuids);
	}

	@POST
	@Path("/dearchive")
	@Operation(summary = "Remove countries from archive based on their unique IDs (UUIDs); i.e. reactivate.")
	@ApiResponse(description = "Returns a list of UUIDs for which reactivation was successful.", responseCode = "200", useReturnTypeSchema = true)
	public List<String> dearchive(
		@RequestBody(description = "List of country UUIDs. These UUIDs denote the countries to be reactivated from archive.",
			required = true) List<String> uuids) {
		return FacadeProvider.getCountryFacade().dearchive(uuids);
	}
}
