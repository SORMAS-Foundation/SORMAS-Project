package de.symeda.sormas.rest;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.person.PersonFollowUpEndDto;
import de.symeda.sormas.api.person.PersonQuarantineEndDto;
import de.symeda.sormas.api.person.PersonSymptomJournalStatusDto;
import de.symeda.sormas.api.visit.ExternalVisitDto;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/visits-external")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed("REST_EXTERNAL_VISITS_USER")
public class ExternalVisitsResource extends EntityDtoResource {

	public static final String EXTERNAL_VISITS_API_VERSION = "1.41.0";

	@GET
	@Path("/person/{personUuid}/isValid")
	public Boolean isValidPersonUuid(@PathParam("personUuid") String personUuid) {
		return FacadeProvider.getPersonFacade().isValidPersonUuid(personUuid);
	}

	@POST
	@Path("/person/{personUuid}/status")
	public boolean postSymptomJournalStatus(@PathParam("personUuid") String personUuid, PersonSymptomJournalStatusDto statusDto) {
		try {
			return FacadeProvider.getPersonFacade().setSymptomJournalStatus(personUuid, statusDto.getStatus());
		} catch (Exception e) {
			return false;
		}
	}

	@POST
	@Path("/")
	public List<PushResult> postExternalVisits(List<ExternalVisitDto> dtos) {
		List<PushResult> result = savePushedDto(dtos, FacadeProvider.getVisitFacade()::saveExternalVisit);
		return result;
	}

	@GET
	@Path("/version")
	public String getVersion() {
		return EXTERNAL_VISITS_API_VERSION;
	}

	@GET
	@Path("/quarantineEndDates/{since}")
	public List<PersonQuarantineEndDto> getLatestQuarantineEndDates(@PathParam("since") long since) {
		return FacadeProvider.getPersonFacade().getLatestQuarantineEndDates(new Date(since));
	}

	@GET
	@Path("/followUpEndDates/{since}")
	public List<PersonFollowUpEndDto> getLatestFollowUpEndDates(@PathParam("since") long since) {
		return FacadeProvider.getPersonFacade().getLatestFollowUpEndDates(new Date(since), true);
	}

	@Override
	protected <T> String createErrorMessage(T dto) {
		final ExternalVisitDto externalVisitDto = (ExternalVisitDto) dto;
		return dto.getClass().getSimpleName() + " #personUUID: " + externalVisitDto.getPersonUuid() + "\n";
	}

}
