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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class EventResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<EventDto> getAllEvents(@PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<EventDto> getAllEvents(@PathParam("since") long since, @PathParam("size") int size, @PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getEventFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	/**
	 * This method returns the eventDto that correspond to the given uuid.
	 * The return eventDto has the superordinateEvent of type EventDetailedReferenceDto.
	 * 
	 * @param uuid
	 * @return
	 *         The return eventDto has the superordinateEvent of type EventDetailedReferenceDto
	 */
	@GET
	@Path("/{uuid}")
	public EventDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getEventFacade().getEventByUuid(uuid, true);
	}

	@POST
	@Path("/query")
	public List<EventDto> getByUuids(List<String> uuids) {
		List<EventDto> result = FacadeProvider.getEventFacade().getByUuids(uuids);
		return result;
	}

	@POST
	@Path("/push")
	public List<PushResult> postEvents(@Valid List<EventDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getEventFacade()::save);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getEventFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	public List<String> getArchivedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getDeletedUuidsSince(new Date(since));
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
	@Path("/indexList")
	public Page<EventIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<EventCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getEventFacade().getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/externalData")
	public Response updateExternalData(List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getEventFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/delete")
	public List<String> delete(List<String> uuids) {
		return FacadeProvider.getEventFacade().deleteEvents(uuids);
	}

	@POST
	@Path("/children")
	public List<String> getChildrenUuids(List<String> uuids) {
		return FacadeProvider.getEventFacade().getSubordinateEventUuids(uuids);
	}
}
