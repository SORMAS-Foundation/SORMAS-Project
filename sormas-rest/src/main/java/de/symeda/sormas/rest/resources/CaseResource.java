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
import java.util.Map;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFollowUpCriteria;
import de.symeda.sormas.api.caze.CaseFollowUpDto;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CasePersonDto;
import de.symeda.sormas.api.caze.CoreAndPersonDto;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.api.utils.Experimental;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/cases")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Case Resource",
	description = "Management of case tracking data. Cases are created for persons that have at least a suspected case of a SORMAS supported disease. "
		+ "If a person with a case of a disease meets other people, a traceable contact for each person happens.\n\n"
		+ "See also: **Contacts**, **Travel Entry**.")
public class CaseResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all case data from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of all case data for the given interval.", useReturnTypeSchema = true)
	public List<CaseDataDto> getAllCases(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of cases that fulfill certain criteria.",
		description = "**-** cases that are no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given number [*size*]")
	@ApiResponse(responseCode = "200", description = "Returns a list of cases for the given interval.", useReturnTypeSchema = true)
	public List<CaseDataDto> getAllCases(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getCaseFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get cases based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of cases by their UUIDs.", useReturnTypeSchema = true)
	public List<CaseDataDto> getByUuids(
		@RequestBody(description = "List of UUIDs used to query case data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getCaseFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/persons")
	@Operation(summary = "Get case data based on the unique IDs (UUIDS) of the persons that have a case of a disease.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of case data based on the UUIDs of the queried persons.",
		useReturnTypeSchema = true)
	public List<CaseDataDto> getByPersonUuids(
		@RequestBody(description = "List of person UUIDs used to query case data entries.", required = true) List<String> uuids) {
		return FacadeProvider.getCaseFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of case data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postCases(
		@RequestBody(description = "List of CaseDtos to be added to the existing case data entries.",
			required = true) @Valid List<CaseDataDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getCaseFacade()::save);
	}

	@POST
	@Path("/pushWithPerson")
	@Operation(summary = "Submit a single CoreAndPersonDTO to create a new case and a new person related to it.")
	@ApiResponse(responseCode = "200", description = "Returns the uploaded entry.", useReturnTypeSchema = true)
	public CoreAndPersonDto<CaseDataDto> postCase(
		@RequestBody(
			description = "CoreAndPersonDto containig the PersonDto and the CaseDto that are to be added to the existing contact data entries.",
			required = true) @Valid CoreAndPersonDto<CaseDataDto> dto) {
		return FacadeProvider.getCaseFacade().save(dto);
	}

	@POST
	@Path("/push-detailed")
	@Operation(summary = "Submit a list of case data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the UUIDs of the submitted data entries and the upload success status of each entry."
			+ "If there is a known reason why upload failed, the upload success status also contains a reason why the upload failed.")
	public Map<String, Map<PushResult, String>> postCasesDetailed(
		@RequestBody(description = "List of CaseDtos to be added to the existing case data entries.",
			required = true) @Valid List<CaseDataDto> dtos) {
		return savePushedDetailedDto(dtos, FacadeProvider.getCaseFacade()::save);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available case data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllUuids() {
		return FacadeProvider.getCaseFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	@Operation(summary = "Get the unique IDs of all case data that has been marked as archived for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getArchivedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	@Operation(summary = "Get the unique IDs of all case data that has been deleted during the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getDeletedUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	@Operation(summary = "Get the unique IDs of all case data that has been marked as obsolete for the given interval.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getObsoleteUuidsSince(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getObsoleteUuidsSince(new Date(since));
	}

	@POST
	@Path("/getduplicates")
	@Operation(summary = "Get a case person that resembles the submitted case person closely and the birthdate of the person exactly.")
	@ApiResponse(responseCode = "200", description = "Returns a case person object that matches the submitted case person.")
	public List<CasePersonDto> getDuplicates(
		@RequestBody(description = "CasePerson object containing the person information and case information",
			required = true) @Valid CasePersonDto casePerson) {
		return FacadeProvider.getCaseFacade().getDuplicates(casePerson);
	}

	@POST
	@Path("/getduplicates/{reportDateThreshold}")
	@Operation(
		summary = "Get a case person that resembles the submitted case person closely and the birthdate of the person within the given threshold.")
	@ApiResponse(responseCode = "200", description = "Returns a case person object that matches the submitted case person.")
	public List<CasePersonDto> getDuplicates(
		@RequestBody(description = "CasePerson object containing the person information and case information",
			required = true) @Valid CasePersonDto casePerson,
		@Parameter(
			description = "Value describing how closely the birth dates of duplicates have to match the birth date of the submitted case person") @PathParam("reportDateThreshold") int reportDateThreshold) {
		return FacadeProvider.getCaseFacade().getDuplicates(casePerson, reportDateThreshold);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a page of Cases based on CaseCriteria filter params.")
	@ApiResponse(description = "Returns a page of cases that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public Page<CaseIndexDto> getIndexList(
		@RequestBody(description = "Case based query-filter criteria with sorting proprerty.",
			required = true) CriteriaWithSorting<CaseCriteria> criteriaWithSorting,
		@Parameter(description = "page offset", required = true) @QueryParam("offset") int offset,
		@Parameter(description = "page size", required = true) @QueryParam("size") int size) {
		return FacadeProvider.getCaseFacade().getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	/**
	 * 
	 * @param criteriaWithSorting
	 *            - The criteria object inside criteriaWithSorting cannot be null. Use an empty criteria instead.
	 * @param offset
	 * @param size
	 * @return
	 */
	@POST
	@Path("/detailedIndexList")
	@Operation(summary = "Get a page of Cases with additional personal information based on CaseCriteria filter params.")
	@ApiResponse(description = "Returns a page of cases with additional personal information that met the filter criteria.",
		responseCode = "200",
		useReturnTypeSchema = true)
	public Page<CaseIndexDetailedDto> getIndexDetailedList(
		@RequestBody(description = "Case based query-filter criteria with sorting proprerty.",
			required = true) CriteriaWithSorting<CaseCriteria> criteriaWithSorting,
		@QueryParam("offset") @Parameter(description = "page offset", required = true) int offset,
		@QueryParam("size") @Parameter(description = "page size", required = true) int size) {
		return FacadeProvider.getCaseFacade()
			.getIndexDetailedPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/caseFollowUpIndexList")
	@Operation(summary = "Get a page of follow-up information based on CaseFollowUpCriteria")
	@ApiResponse(responseCode = "200",
		description = "Returns a page of follow-up information, that met the filter criteria.",
		useReturnTypeSchema = true)
	public Page<CaseFollowUpDto> getCaseFollowUpIndexList(
		@RequestBody(description = "Case based query-filter criteria with sorting proprerty, that also considers follow-up timeframe.",
			required = true) CriteriaWithSorting<CaseFollowUpCriteria> criteriaWithSorting,
		@Parameter(description = "page offset", required = true) @QueryParam("offset") int offset,
		@Parameter(description = "page size", required = true) @QueryParam("size") int size) {
		return FacadeProvider.getCaseFacade()
			.getCaseFollowUpIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@GET
	@Path("/{uuid}")
	@Operation(summary = "Get a single case based on its universally unique ID (UUID).")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns a case for the given UUID.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Case with the given UUID cannot be found.", useReturnTypeSchema = false) })
	public CaseDataDto getByUuid(@Parameter(required = true, description = "UUID of case.") @PathParam("uuid") String uuid) {
		return FacadeProvider.getCaseFacade().getByUuid(uuid);
	}

	@POST
	@Path("/externalData")
	@Operation(summary = "Submit a list of ExternalData objects to update the external data entries in the corresponding cases")
	@ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
	public Response updateExternalData(@Valid List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getCaseFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	/**
	 * This endpoint is used to partially update the CaseData.
	 * For allowing only a subset of the fields of the caseDataDto to be updated
	 * THIS METHOD IS EXPERIMENTAL!!!
	 * 
	 * @param uuid
	 * @param caseDataDtoJson
	 *            - a subset of caseDataDto fields, same structure as caseDataDto
	 * @return - the updated caseDataDto
	 * @throws Exception
	 */
	@POST
	@Path("/postUpdate/{uuid}")
	@Experimental
	@Operation(summary = "EXPERIMENTAL!",
		description = " Submit the UUID of a case and a subset of filled caseDataDto"
			+ " fields structured identically to CaseDataDto to update the fields of the case with the specified UUID")
	@ApiResponse(responseCode = "200", description = "Returns the updated caseDataDto", useReturnTypeSchema = true)
	public CaseDataDto postUpdate(
		@Parameter(required = true, description = "UUID of case.") @PathParam("uuid") String uuid,
		@RequestBody(required = true,
			description = "Subset of caseDataDto fields to be changed, structured exactly like caseDataDto") JsonNode caseDataDtoJson)
		throws Exception {
		return FacadeProvider.getCaseFacade().postUpdate(uuid, caseDataDtoJson);
	}

	@POST
	@Path("/delete")
	@Operation(summary = "Delete case data entries from storage based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of UUIDs of the deleted case entries with the deletion reason \"Deleted via ReST call\".",
		useReturnTypeSchema = true)
	public List<String> delete(List<String> uuids) {
		return FacadeProvider.getCaseFacade().deleteCases(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"));
	}

	@POST
	@Path("/specificCase/{searchTerm}")
	@Operation(summary = "Search for a specific case UUID based on the cases full UUID, epid Number or external ID")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Returns the UUID of a case that mathes the search term.", useReturnTypeSchema = true),
		@ApiResponse(responseCode = "204", description = "Case with the given search term cannot be found.", useReturnTypeSchema = false) })
	public String getSpecificCase(
		@RequestBody(description = "Leave this empty for now, filtering by CaseCriteria is not yet supported.") CaseCriteria caseCriteria,
		@Parameter(required = true,
			description = "UUID, epid number or external ID of the case in question.") @PathParam("searchTerm") String searchTerm) {
		return FacadeProvider.getCaseFacade().getUuidByUuidEpidNumberOrExternalId(searchTerm, caseCriteria);
	}

}
