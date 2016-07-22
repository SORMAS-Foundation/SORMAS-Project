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
	@Path("/all/{since}")
	public List<CaseDataDto> getAllCases(@PathParam("since") Long since) {
		if (since != null) {
			return FacadeProvider.getCaseFacade().getAllCasesAfter(new Date(since));
		}
		return FacadeProvider.getCaseFacade().getAllCases();
	}
}
