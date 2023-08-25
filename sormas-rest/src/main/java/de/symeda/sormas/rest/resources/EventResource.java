/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.event.EventCriteria;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventIndexDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/events")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class EventResource extends EntityDtoResource<EventDto> {

	@GET
	@Path("/all/{since}")
	public List<EventDto> getAllEvents(@PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<EventDto> getAllEvents(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
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
		return FacadeProvider.getEventFacade().getByUuids(uuids);
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

	@GET
	@Path("/obsolete/{since}")
	public List<String> getObsoleteUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getEventFacade().getObsoleteUuidsSince(new Date(since));
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
		return FacadeProvider.getEventFacade()
			.delete(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"))
			.stream()
			.map(ProcessedEntity::getEntityUuid)
			.collect(Collectors.toList());
	}

	@POST
	@Path("/children")
	public List<String> getChildrenUuids(List<String> uuids) {
		return FacadeProvider.getEventFacade().getSubordinateEventUuids(uuids);
	}

	@GET
	@Path("/specificEvent/{searchTerm}")
	public String getSpecificCase(@PathParam("searchTerm") String searchTerm) {
		return FacadeProvider.getEventFacade().getUuidByCaseUuidOrPersonUuid(searchTerm);
	}

	@Override
	public UnaryOperator<EventDto> getSave() {
		return FacadeProvider.getEventFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<EventDto> eventDtos) {
		return super.postEntityDtos(eventDtos);
	}
}
