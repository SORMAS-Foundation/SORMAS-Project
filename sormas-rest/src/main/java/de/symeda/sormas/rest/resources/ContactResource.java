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
 */

package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
import de.symeda.sormas.api.caze.CoreAndPersonDto;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.common.progress.ProcessedEntity;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactIndexDetailedDto;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.externaldata.ExternalDataDto;
import de.symeda.sormas.api.externaldata.ExternalDataUpdateException;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * @see <a href="https://jersey.java.net/documentation/latest/">Jersey
 *      documentation</a>
 * @see <a href=
 *      "https://jersey.java.net/documentation/latest/jaxrs-resources.html#d0e2051">Jersey
 *      documentation HTTP Methods</a>
 *
 */
@Path("/contacts")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ContactResource extends EntityDtoResource<ContactDto> {

	@GET
	@Path("/all/{since}")
	public List<ContactDto> getAllContacts(@PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getAllAfter(new Date(since));
	}

	@GET
	@Path("/all/{since}/{size}/{lastSynchronizedUuid}")
	public List<ContactDto> getAllContacts(
		@PathParam("since") long since,
		@PathParam("size") int size,
		@PathParam("lastSynchronizedUuid") String lastSynchronizedUuid) {
		return FacadeProvider.getContactFacade().getAllAfter(new Date(since), size, lastSynchronizedUuid);
	}

	@POST
	@Path("/query")
	public List<ContactDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getContactFacade().getByUuids(uuids);
	}

	@POST
	@Path("/query/persons")
	public List<ContactDto> getByPersonUuids(List<String> uuids) {
		return FacadeProvider.getContactFacade().getByPersonUuids(uuids);
	}

	@POST
	@Path("/pushWithPerson")
	public CoreAndPersonDto<ContactDto> postContact(@Valid CoreAndPersonDto<ContactDto> dto) {
		return FacadeProvider.getContactFacade().save(dto);

	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getContactFacade().getAllActiveUuids();
	}

	@GET
	@Path("/archived/{since}")
	public List<String> getArchivedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getArchivedUuidsSince(new Date(since));
	}

	@GET
	@Path("/deleted/{since}")
	public List<String> getDeletedUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getDeletedUuidsSince(new Date(since));
	}

	@GET
	@Path("/obsolete/{since}")
	public List<String> getObsoleteUuidsSince(@PathParam("since") long since) {
		return FacadeProvider.getContactFacade().getObsoleteUuidsSince(new Date(since));
	}

	@POST
	@Path("/indexList")
	public Page<ContactIndexDto> getIndexList(
		@RequestBody CriteriaWithSorting<ContactCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getContactFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/detailedIndexList")
	public Page<ContactIndexDetailedDto> getIndexDetailedList(
		@RequestBody CriteriaWithSorting<ContactCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getContactFacade()
			.getIndexDetailedPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/externalData")
	public Response updateExternalData(@Valid List<ExternalDataDto> externalData) {
		try {
			FacadeProvider.getContactFacade().updateExternalData(externalData);
			return Response.status(Response.Status.OK).build();
		} catch (ExternalDataUpdateException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@POST
	@Path("/delete")
	public List<String> delete(List<String> uuids) {
		return FacadeProvider.getContactFacade()
			.delete(uuids, new DeletionDetails(DeletionReason.OTHER_REASON, "Deleted via ReST call"))
			.stream()
			.map(ProcessedEntity::getEntityUuid)
			.collect(Collectors.toList());
	}

	@GET
	@Path("/{uuid}")
	public ContactDto getByUuid(@PathParam("uuid") String uuid) {
		return FacadeProvider.getContactFacade().getByUuid(uuid);
	}

	@Override
	public UnaryOperator<ContactDto> getSave() {
		return FacadeProvider.getContactFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<ContactDto> contactDtos) {
		return super.postEntityDtos(contactDtos);
	}
}
