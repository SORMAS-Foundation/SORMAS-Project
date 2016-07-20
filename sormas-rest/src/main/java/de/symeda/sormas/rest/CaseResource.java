package de.symeda.sormas.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseStatus;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey documentation</a>
 * @see <a href="https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey documentation HTTP Methods</a>
 *
 */
@Path("/cases")
@Produces({
	MediaType.APPLICATION_JSON + "; charset=UTF-8"
	})
public class CaseResource implements CaseFacade {

	@GET
	@Path("/{uuid}")
	@Override
	public CaseDataDto getCaseDataByUuid(@PathParam("uuid") String uuid) {
		
		CaseDataDto caze = FacadeProvider.getCaseFacade().getCaseDataByUuid(uuid);
		return caze;
	}

	@GET
	@Path("/all")
	@Override
	public List<CaseDataDto> getAllCases() {
		return FacadeProvider.getCaseFacade().getAllCases();
	}

	@Override
	public CaseDataDto saveCase(CaseDataDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDataDto createCase(String personUuid, CaseDataDto caseDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CaseDataDto changeCaseStatus(String uuid, CaseStatus targetStatus) {
		// TODO Auto-generated method stub
		return null;
	}

}
