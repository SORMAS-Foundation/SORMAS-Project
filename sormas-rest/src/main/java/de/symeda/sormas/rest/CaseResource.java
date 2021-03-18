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
package de.symeda.sormas.rest;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CasePersonDto;
import de.symeda.sormas.api.common.Page;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path("/cases")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class CaseResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<CaseDataDto> getAllCases(@PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getAllActiveCasesAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<CaseDataDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getCaseFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/persons")
	public List<CaseDataDto> getByPersonUuids(List<String> uuids) {
		return FacadeProvider.getCaseFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/push")
	public List<PushResult> postCases(@Valid List<CaseDataDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getCaseFacade()::saveCase);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getCaseFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	public List<String> getArchivedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getCaseFacade().getDeletedUuidsSince(new Date(since));
	}

	@POST
	@Path("/getduplicates")
	public List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson) {
		return FacadeProvider.getCaseFacade().getDuplicates(casePerson);
	}

	@POST
	@Path("/getduplicates/{reportDateThreshold}")
	public List<CasePersonDto> getDuplicates(@Valid CasePersonDto casePerson, @PathParam("reportDateThreshold") int reportDateThreshold) {
		return FacadeProvider.getCaseFacade().getDuplicates(casePerson, reportDateThreshold);
	}

	@POST
	@Path("/caseIndex")
	public Page<CaseIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<CaseCriteria> criteriaWithSorting,
		@QueryParam("page") int page,
		@QueryParam("size") int size) {
		return FacadeProvider.getCaseFacade().getIndexPage(criteriaWithSorting.getCriteria(), page, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/caseIndexDetailed")
	public Page<CaseIndexDetailedDto> getIndexDetailedList(
		@RequestBody CriteriaWithSorting<CaseCriteria> criteriaWithSorting,
		@QueryParam("page") int page,
		@QueryParam("size") int size) {
		return FacadeProvider.getCaseFacade()
			.getIndexDetailedPage(criteriaWithSorting.getCriteria(), page, size, criteriaWithSorting.getSortProperties());
	}

}
