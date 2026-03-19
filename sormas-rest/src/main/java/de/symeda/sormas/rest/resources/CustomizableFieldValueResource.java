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

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.customizablefield.CustomizableFieldContext;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldValueFacade;

/**
 * REST resource for managing customizable field values.
 */
@Path("/customizablefieldvalue")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class CustomizableFieldValueResource {

	private CustomizableFieldValueFacade getFacade() {
		return FacadeProvider.getCustomizableFieldValueFacade();
	}

	@GET
	@Path("/all")
	public List<CustomizableFieldValueDto> getAll() {
		return getFacade().getAll();
	}

	@GET
	@Path("/{uuid}")
	public CustomizableFieldValueDto getByUuid(@PathParam("uuid") String uuid) {
		return getFacade().getByUuid(uuid);
	}

	@GET
	@Path("/entity/{entityUuid}")
	public Map<String, CustomizableFieldValueDto> getValuesForEntity(
		@PathParam("entityUuid") String entityUuid,
		@QueryParam("contextClass") CustomizableFieldContext contextClass) {
		return getFacade().getValuesForEntity(entityUuid, contextClass);
	}

	@POST
	@Path("/entity/{entityUuid}/save")
	public Response saveEntityCustomFields(
		@PathParam("entityUuid") String entityUuid,
		@QueryParam("contextClass") CustomizableFieldContext contextClass,
		Map<String, CustomizableFieldValueDto> fieldValues) {
		getFacade().saveEntityCustomFields(entityUuid, contextClass, fieldValues);
		return Response.ok().build();
	}

	@DELETE
	@Path("/entity/{entityUuid}")
	public Response deleteValuesForEntity(
		@PathParam("entityUuid") String entityUuid,
		@QueryParam("contextClass") CustomizableFieldContext contextClass) {
		getFacade().deleteValuesForEntity(entityUuid, contextClass);
		return Response.ok().build();
	}

	@POST
	@Path("/save")
	public CustomizableFieldValueDto save(CustomizableFieldValueDto dto) {
		return getFacade().save(dto);
	}

	@PUT
	@Path("/{uuid}")
	public CustomizableFieldValueDto update(@PathParam("uuid") String uuid, CustomizableFieldValueDto dto) {
		dto.setUuid(uuid);
		return getFacade().save(dto);
	}

	@DELETE
	@Path("/{uuid}")
	public Response delete(@PathParam("uuid") String uuid) {
		getFacade().delete(uuid, new DeletionDetails());
		return Response.ok().build();
	}
}
