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

import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
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
import de.symeda.sormas.api.outbreak.OutbreakCriteria;
import de.symeda.sormas.api.outbreak.OutbreakDto;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey
 *      documentation</a>
 * @see <a href=
 *      "https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey
 *      documentation HTTP Methods</a>
 *
 */
@Path("/outbreaks")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class OutbreakResource extends EntityDtoResource {

	@GET
	@Path("/active/{since}")
	public List<OutbreakDto> getActiveSince(@PathParam("since") long since) {
		return FacadeProvider.getOutbreakFacade().getActiveAfter(new Date(since));
	}

	@GET
	@Path("/uuids")
	public List<String> getActiveUuids() {
		return FacadeProvider.getOutbreakFacade().getActiveUuidsAfter(null);
	}

	@GET
	@Path("/inactive/{since}")
	public List<String> getInactiveUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getOutbreakFacade().getInactiveUuidsAfter(new Date(since));
	}

	@POST
	@Path("/indexList")
	public Page<OutbreakDto> getIndexList(
		@RequestBody CriteriaWithSorting<OutbreakCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getOutbreakFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	public List<PushResult> postPersons(@Valid List<OutbreakDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getOutbreakFacade()::saveOutbreakByCurrentUser);
	}

}
