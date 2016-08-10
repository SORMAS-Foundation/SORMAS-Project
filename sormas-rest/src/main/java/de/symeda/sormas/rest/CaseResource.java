package de.symeda.sormas.rest;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/cases")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class CaseResource {

	@GET
	@Path("/{uuid}")
	public CaseDataDto getCaseDataByUuid(@PathParam("uuid") String uuid) {
		
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
		return caze;
	}

	@GET
	@Path("/all/{user}/{since}")
	public List<CaseDataDto> getAllCases(@PathParam("user") String userUuid, @PathParam("since") long since) {
		
		List<CaseDataDto> cases = FacadeProvider.getCaseFacade().getAllCasesAfter(new Date(since), userUuid);
		return cases;
	}
}
