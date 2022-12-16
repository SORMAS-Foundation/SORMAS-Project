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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.therapy.PrescriptionCriteria;
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionIndexDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/prescriptions")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Prescription Resource",
	description = "Information about a medication prescribed by a clinician for specific **case**. See also **Treatment** for documenting the execution of prescribed therapies.")
public class PrescriptionResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all prescription data from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of prescription data for the given interval.", useReturnTypeSchema = true)
	public List<PrescriptionDto> getAllPrescriptions(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since) {
		return FacadeProvider.getPrescriptionFacade().getAllActivePrescriptionsAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	@Operation(summary = "Get a batch of prescription data that fulfill certain criteria.",
		description = "**-** data is no older than a given date in the past until now [*since*]\n\n"
			+ "**-** TBD_RESTAPI_SWAGGER_DOC [*lastSynchronizedUuid*]\n\n" + "**-** number of results does not exceed a given numer [*size*]")
	@ApiResponse(responseCode = "200",
		description = "Returns a list of prescription data of the given batch size, that has been changed in the given interval.",
		useReturnTypeSchema = true)
	public List<PrescriptionDto> getAllPrescriptions(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT") @PathParam("since") long since,
		@Parameter(required = true, description = "batch size") @PathParam("size") int size,
		@Parameter(required = true, description = "TBD_RESTAPI_SWAGGER_DOC") @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getPrescriptionFacade().getAllActivePrescriptionsAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/push")
	@Operation(summary = "Submit a list of prescription data entries to the server.")
	@ApiResponse(responseCode = "200",
		description = "Returns a list containing the upload success status of each uploaded entry.",
		useReturnTypeSchema = true)
	public List<PushResult> postPrescriptions(
		@RequestBody(description = "List of PrescriptionDtos to be added to the existing prescription data entries.",
			required = true) @Valid List<PrescriptionDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getPrescriptionFacade()::savePrescription);
	}

	@GET
	@Path("/uuids")
	@Operation(summary = "Get the unique IDs (UUIDs) of all available prescription data entries.")
	@ApiResponse(responseCode = "200", description = "Returns a list of strings (UUIDs).", useReturnTypeSchema = true)
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getPrescriptionFacade().getAllActiveUuids();
	}

	@POST
	@Path("/query")
	@Operation(summary = "Get data of prescriptions based on their unique IDs (UUIDs).")
	@ApiResponse(responseCode = "200", description = "Returns a list of prescriptions by their UUIDs.", useReturnTypeSchema = true)
	public List<PrescriptionDto> getByUuids(
		@RequestBody(required = true, description = "List of UUIDs used to query prescription data entries.") List<String> uuids) {
		return FacadeProvider.getPrescriptionFacade().getByUuids(uuids);
	}

	@POST
	@Path("/indexList")
	@Operation(summary = "Get a list of Prescriptions based on PrescriptionCriteria filter params.")
	@ApiResponse(description = "Returns a list of prescriptions that met the filter criteria.", responseCode = "200", useReturnTypeSchema = true)
	public List<PrescriptionIndexDto> getIndexList(
		@RequestBody(description = "Prescription based query-filter criteria with sorting property.",
			required = true) CriteriaWithSorting<PrescriptionCriteria> criteriaWithSorting) {
		return FacadeProvider.getPrescriptionFacade().getIndexList(criteriaWithSorting.getCriteria());
	}
}
