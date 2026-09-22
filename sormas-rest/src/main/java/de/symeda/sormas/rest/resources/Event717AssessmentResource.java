/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717TimelinessDto;

@Path("/event717assessments")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class Event717AssessmentResource {

	@GET
	@Path("/event/{eventUuid}")
	public Event717AssessmentDto getByEventUuid(@PathParam("eventUuid") String eventUuid) {
		return FacadeProvider.getEvent717AssessmentFacade().getByEventUuid(eventUuid);
	}

	@GET
	@Path("/event/{eventUuid}/timeliness")
	public Event717TimelinessDto getTimelinessByEventUuid(@PathParam("eventUuid") String eventUuid) {
		return FacadeProvider.getEvent717AssessmentFacade().getTimelinessByEventUuid(eventUuid);
	}

	@POST
	@Path("/save")
	public Event717AssessmentDto save(@Valid Event717AssessmentDto dto) {
		return FacadeProvider.getEvent717AssessmentFacade().save(dto);
	}

	@DELETE
	@Path("/event/{eventUuid}")
	public Response deleteByEventUuid(@PathParam("eventUuid") String eventUuid) {
		FacadeProvider.getEvent717AssessmentFacade().deleteByEventUuid(eventUuid);
		return Response.ok().build();
	}
}
