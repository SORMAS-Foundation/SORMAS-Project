package de.symeda.sormas.rest.externaljournal;

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
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.person.JournalPersonDto;
import de.symeda.sormas.api.person.PersonFollowUpEndDto;
import de.symeda.sormas.api.person.PersonSymptomJournalStatusDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.visit.ExternalVisitDto;
import de.symeda.sormas.rest.EntityDtoResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Path("/visits-external")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed(UserRight._EXTERNAL_VISITS)
public class ExternalVisitsResource extends EntityDtoResource {

	public static final String EXTERNAL_VISITS_API_VERSION = "1.41.1";

	@GET
	@Path("/person/{personUuid}")
	@Operation(summary = "Get person information", description = "Get some personal data for a specific person")
	@ApiResponse(
		description = "A selection of personal data, including first and last name, e-mail, phone number and birth date if available for that person. "
			+ "Regarding the e-mail and phone number: in SORMAS it is possible to store several e-mail addresses and phone numbers for a person. "
			+ "It is tried to provide one e-mail address and/or phone number via the external visits API in any feasible way.<br>"
			+ "if there is just one e-mail and/or phone number for a person, this is transmitted.<br>"
			+ "If there are several e-mail addresses and or phone numbers, SORMAS will transmit the ones marked as primary.<br>"
			+ "If there are several e-mail addresses or phone numbers without any marked as primary, SORMAS will request the user to mark one before registration.<br>"
			+ "Regarding the latestFollowUpEndDate: this is the latest follow up end date of any contact (or case, if the case follow up feature is enabled) "
			+ "related to the person. The contacts (and cases) considered are not filtered by disease.<br>"
			+ "Note that Null value fields may not be returned. If you get an unexpected result, it might help to verify "
			+ "if the personUuid is existing in your system via the isValid controller.<br>"
			+ "If you get \"pseudonymized\": true, the user the request was authorized with probably lacks the user role REST_EXTERNAL_VISITS_USER.")
	//@formatter:off
	public JournalPersonDto getPersonByUuid(@PathParam("personUuid") String personUuid) {
		return FacadeProvider.getPersonFacade().getPersonForJournal(personUuid);
	}

	@GET
	@Path("/person/{personUuid}/isValid")
	@Operation(summary = "Check person validity", description = "Check if a the Uuid given as parameter exists in SORMAS.",
		responses =
			@ApiResponse(description = "true a person with the given Uuid exists in SORMAS, false otherwise.",
					content = @Content(schema = @Schema(example = "true"))))
	public Boolean isValidPersonUuid(@PathParam("personUuid") String personUuid) {
		return FacadeProvider.getPersonFacade().isValidPersonUuid(personUuid);
	}

	//@formatter:off
	@POST
	@Path("/person/{personUuid}/status")
	@Operation(summary = "Save symptom journal status",
		responses =
			@ApiResponse(description = "true if the status was set successfully, false otherwise.",
					content = @Content(schema = @Schema(example = "true"))))
	@RequestBody(
		//@formatter:off
		description = "status may be one of the following:<br/>" +
				"UNREGISTERED: User has not yet sent any state<br/>" +
				"REGISTERED: After successful registration in SymptomJournal<br/>" +
				"ACCEPTED: User has accepted a confirmation<br/>" +
				"REJECTED: User has rejected (declined) a confirmation<br/>" +
				"DELETED: User was deleted",
		//@formatter:on
		content = @Content(schema = @Schema(example = "[\n  {\n    \"status\": \"REGISTERED\",\n"
			+ "    \"statusDateTime\": \"2020-04-15T12:55:00.000+02:00\" // datetime format yyyy-MM-dd'T'HH:mm:ss.SSSZ\n  }\n]")))
	//@formatter:on
	public boolean postSymptomJournalStatus(@PathParam("personUuid") String personUuid, PersonSymptomJournalStatusDto statusDto) {
		try {
			return FacadeProvider.getPersonFacade().setSymptomJournalStatus(personUuid, statusDto.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@POST
	@Path("/")
	@Operation(summary = "Save visits", description = "Upload visits with all symptom and disease related data to SORMAS.")
	public List<PushResult> postExternalVisits(List<ExternalVisitDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getVisitFacade()::saveExternalVisit);
	}

	@GET
	@Path("/version")
	@Operation(summary = "Get API version")
	@ApiResponse(description = "The minimal version needed for compatibility with the external ReST API of SORMAS.",
		content = @Content(schema = @Schema(type = "string", example = "1.37.0")))
	public String getVersion() {
		return EXTERNAL_VISITS_API_VERSION;
	}

	@GET
	@Path("/followUpEndDates/{since}")
	@Operation(summary = "Get follow up end dates",
		description = "Get latest follow up end date assigned to the specified person. "
			+ "Note: Only returns values for persons who have their symptom journal status set to ACCEPTED! "
			+ "Only returns values changed after {since}, which is interpreted as a UNIX timestamp.")
	//@formatter:off
	@ApiResponse(description = "List of personUuids and their latest follow up end dates as UNIX timestamps.",
			content = @Content(schema = @Schema(example = "[\n" +
			"  {\n" +
			"    \"personUuid\": \"Q56VFD-G3TXKT-R2DBIW-FTWIKAMI\",\n" +
			"    \"latestFollowUpEndDate\": 1599602400000\n" +
			"  },\n" +
			"  {\n" +
			"    \"personUuid\": \"TEYCIW-BHWHMH-MH2QIW-KBP72JMU\",\n" +
			"    \"latestFollowUpEndDate\": 1593727200000\n" +
			"  }\n" +
			"]")))
	//@formatter:on

	public List<PersonFollowUpEndDto> getLatestFollowUpEndDates(@PathParam("since") long since) {
		return FacadeProvider.getPersonFacade().getLatestFollowUpEndDates(new Date(since), true);
	}

	@Override
	protected <T> String createErrorMessage(T dto) {
		final ExternalVisitDto externalVisitDto = (ExternalVisitDto) dto;
		return dto.getClass().getSimpleName() + " #personUUID: " + externalVisitDto.getPersonUuid() + "\n";
	}

}
