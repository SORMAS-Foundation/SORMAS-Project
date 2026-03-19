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
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataDto;
import de.symeda.sormas.api.customizablefield.CustomizableFieldMetadataFacade;

/**
 * REST resource for managing customizable field metadata.
 */
@Path("/customizablefieldmetadata")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class CustomizableFieldMetadataResource {

	private CustomizableFieldMetadataFacade getFacade() {
		return FacadeProvider.getCustomizableFieldMetadataFacade();
	}

	@GET
	@Path("/all")
	public List<CustomizableFieldMetadataDto> getAll() {
		return getFacade().getAll();
	}

	@GET
	@Path("/{uuid}")
	public CustomizableFieldMetadataDto getByUuid(@PathParam("uuid") String uuid) {
		return getFacade().getByUuid(uuid);
	}

	@GET
	@Path("/context/{contextClass}")
	public List<CustomizableFieldMetadataDto> getActiveFieldsForContext(@PathParam("contextClass") CustomizableFieldContext contextClass) {
		return getFacade().getActiveFieldsForContext(contextClass);
	}

	@GET
	@Path("/uigroup/{uiGroup}")
	public List<CustomizableFieldMetadataDto> getFieldsForUIGroup(@PathParam("uiGroup") String uiGroup) {
		return getFacade().getFieldsOrderedByUIPosition(uiGroup);
	}

	@GET
	@Path("/byname")
	public CustomizableFieldMetadataDto getByNameAndContext(
		@QueryParam("name") String name,
		@QueryParam("contextClass") CustomizableFieldContext contextClass) {
		return getFacade().getByNameAndContext(name, contextClass);
	}

	@POST
	@Path("/save")
	public CustomizableFieldMetadataDto save(CustomizableFieldMetadataDto dto) {
		return getFacade().save(dto);
	}

	@PUT
	@Path("/{uuid}")
	public CustomizableFieldMetadataDto update(@PathParam("uuid") String uuid, CustomizableFieldMetadataDto dto) {
		dto.setUuid(uuid);
		return getFacade().save(dto);
	}

	@DELETE
	@Path("/{uuid}")
	public Response delete(@PathParam("uuid") String uuid) {
		getFacade().delete(uuid, new DeletionDetails());
		return Response.ok().build();
	}

	@POST
	@Path("/{uuid}/clone")
	public CustomizableFieldMetadataDto cloneField(@PathParam("uuid") String uuid, @QueryParam("newName") String newName) {
		return getFacade().cloneField(uuid, newName);
	}

	@POST
	@Path("/{uuid}/activate")
	public Response activate(@PathParam("uuid") String uuid) {
		getFacade().activateField(uuid);
		return Response.ok().build();
	}

	@POST
	@Path("/{uuid}/deactivate")
	public Response deactivate(@PathParam("uuid") String uuid) {
		getFacade().deactivateField(uuid);
		return Response.ok().build();
	}

	@GET
	@Path("/{uuid}/exists")
	public boolean exists(@PathParam("uuid") String uuid) {
		return getFacade().exists(uuid);
	}
}
