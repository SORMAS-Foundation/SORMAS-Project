/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.utils.Experimental;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/immunizations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Immunization Resource",
	description = "Management of the immunization data. Immunization can take place through previous illness or vaccination.\n\n"
		+ "Some servers are configured in such a way that no immunizations, but only vaccinations are possible.")
public class ImmunizationResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all immunization objects from a date in the past until now")
	@ApiResponse(description = "Returns a list of immunizations for the given interval.", responseCode = "200", useReturnTypeSchema = true)
	public List<ImmunizationDto> getAllAfter(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of immunizations that fulfill certain criteria.",
		description = "**-** immunizations that are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of immunizations for the given interval.", useReturnTypeSchema = true)

	public List<ImmunizationDto> getAllAfter(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getImmunizationFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get immunisations based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of Immunizations by their UUIDs.", useReturnTypeSchema = true)

	public List<ImmunizationDto> getByUuids(@Parameter(description = "List of unique IDs (UUIDs) of immunizations to query") List<String> uuids) {
		return FacadeProvider.getImmunizationFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/persons")
	@Operation(summary = "Get immunization data based on the unique IDs (UUIDS) of the persons that have a immunization.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of immunization data based on the UUIDs of the queried persons.",
		useReturnTypeSchema = true)
	public List<ImmunizationDto> getByPersonUuids(List<String> uuids) {
		return FacadeProvider.getImmunizationFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of immunization data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded immunization.",
		useReturnTypeSchema = true)
	public List<PushResult> post(
		@Valid @RequestBody(description = "List of ImmunizationDtos to be added to the existing immunization data entries.",
			required = true) List<ImmunizationDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getImmunizationFacade()::save);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available immunization data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of unique IDs (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getImmunizationFacade().getAllUuids();
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get the unique IDs (UUIDs) of all immunizations data that has been marked as archived for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get the unique IDs (UUIDs) of all immunization data that has been deleted during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs)", useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get the unique IDs of all immunization data that has been marked as obsolete for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getImmunizationFacade().getObsoleteUuidsSince(new Date(since));
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single immunization based on its unique ID (UUID).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns a immunization for the given UUID.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Immunization with the given UUID cannot be found.", useReturnTypeSchema = false) })
	public ImmunizationDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getImmunizationFacade().getByUuid(uuid);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of immunisation based on ImmunizationCriteria filter params.")
	@ApiResponse(responseCode = "200", description = "Returns a page of immunisations that met the filter criteria.", useReturnTypeSchema = true)
	public Page<ImmunizationIndexDto> getIndexList(
		@RequestBody(description = "Immnunization-based based query-filter criteria with sorting proprerty.",
			required = true) CriteriaWithSorting<ImmunizationCriteria> criteriaWithSorting,
		@Parameter(description = "page offset", required = true) @QueryParam("offset") int offset,
		@Parameter(description = "page size", required = true) @QueryParam("size") int size) {
		return FacadeProvider.getImmunizationFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete immunization entries based on their unique IDs (UUIDs)")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of unique IDs (UUIDs)UUIDs of the deleted immunization entries with the deletion reason \"Deleted via ReST call\".",
		useReturnTypeSchema = true)
	public List<String> delete(@Parameter(description = "List of unique IDs (UUIDs) for immunizations to delete") List<String> uuids) {
		return FacadeProvider.getImmunizationFacade()
			.deleteImmunizations(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
	}

	@POST
	@Path("/createVaccination")
	@Operation(
		summary = "Creates a vaccination that is automatically assigned to an immunization if it exists. Otherwise a new immunization is created.")
	@ApiResponse(responseCode = "200", description = "Returns newly created Vaccination object", useReturnTypeSchema = true)
	public VaccinationDto createVaccination(
		@Valid VaccinationDto vaccination,
		@QueryParam("regionUuid") @Parameter(required = true, description = "UUID of Region") String regionUuid,
		@QueryParam("districtUuid") @Parameter(required = true, description = "UUID of District") String districtUuid,
		@QueryParam("personUuid") @Parameter(required = true, description = "UUID of Person") String personUuid,
		@QueryParam("disease") @Parameter(required = true, description = "Type of disease") Disease disease) {
		return FacadeProvider.getVaccinationFacade()
			.createWithImmunization(
				vaccination,
				new RegionReferenceDto(regionUuid),
				new DistrictReferenceDto(districtUuid),
				new PersonReferenceDto(personUuid),
				disease);
	}

	@POST
	@Path("/vaccinations")
	@Operation(summary = "Get a list of  Vaccination by uuid")
	@ApiResponse(responseCode = "200", description = "Returns Vaccination object", useReturnTypeSchema = true)
	public List<VaccinationDto> getAllVaccinations(@QueryParam("personUuid") String personUuid, @QueryParam("disease") Disease disease) {
		return FacadeProvider.getVaccinationFacade().getAllVaccinations(personUuid, disease);
	}

	@GET
	@Path("/vaccination/{uuid}")
	@Operation(summary = "Get a single Vaccination by its uuid")
	@ApiResponse(responseCode = "200", description = "Returns Vaccination object", useReturnTypeSchema = true)
	public VaccinationDto getVaccinationByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getVaccinationFacade().getByUuid(uuid);
	}

	@POST
	@Path("/vaccination/push")
	@Operation(summary = "Create a new Vaccination")
	@ApiResponse(responseCode = "200", description = "Returns Vaccination object", useReturnTypeSchema = true)
	public VaccinationDto postVaccination(@Valid VaccinationDto vaccination) {
		return FacadeProvider.getVaccinationFacade().save(vaccination);
	}

	/**
	 * This endpoint is used to partially update the VaccinationData.
	 * For allowing only a subset of the fields of the immunizationDataDto to be updated
	 * THIS METHOD IS EXPERIMENTAL!!!
	 * 
	 * @param uuid
	 * @param vaccinationDataDtoJson
	 *            - a subset of immunizationDataDto fields, same structure as vaccinationDataDtoJson
	 * @return - the updated immunizationDataDto
	 * @throws Exception
	 */
	@POST
	@Path("/vaccination/postUpdate/{uuid}")
	@Experimental
	@Operation(summary = "Partially update the VaccinationData for allowing only a subset of the fields of the immunizationDataDto to be updated.",
		description = "**THIS METHOD IS EXPERIMENTAL!**")
	@ApiResponse(responseCode = "200", description = "Returns updated vaccination object", useReturnTypeSchema = true)
	public VaccinationDto postUpdate(@PathParam("uuid") String uuid, JsonNode vaccinationDataDtoJson) throws Exception {
		return FacadeProvider.getVaccinationFacade().postUpdate(uuid, vaccinationDataDtoJson);
	}

}
