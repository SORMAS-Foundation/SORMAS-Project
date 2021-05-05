/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserRole;

@Path(SormasToSormasApiConstants.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed(UserRole._SORMAS_TO_SORMAS_CLIENT)
public class SormasToSormasResource {

	@POST
	@Path(SormasToSormasApiConstants.CASE_REQUEST_ENDPOINT)
	public Response saveSharedCaseRequest(SormasToSormasEncryptedDataDto sharedCases) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveShareRequest(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_REQUEST_REJECT_ENDPOINT)
	public Response rejectSharedCaseRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().rejectShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_REQUEST_ACCEPT_ENDPOINT)
	public Response acceptSharedCaseRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().getDataForShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_ENDPOINT)
	public Response saveSharedCase(SormasToSormasEncryptedDataDto sharedCases) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveSharedEntities(sharedCases));
	}

	@PUT
	@Path(SormasToSormasApiConstants.CASE_ENDPOINT)
	public Response saveReturnedCase(SormasToSormasEncryptedDataDto sharedCases) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveReturnedEntity(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_SYNC_ENDPOINT)
	public Response syncSharedCases(SormasToSormasEncryptedDataDto sharedCases) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveSyncedEntity(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_REQUEST_ENDPOINT)
	public Response saveSharedContactRequest(SormasToSormasEncryptedDataDto sharedContacts) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveShareRequest(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_REQUEST_REJECT_ENDPOINT)
	public Response rejectSharedContactRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().rejectShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_REQUEST_ENDPOINT)
	public Response acceptSharedContactRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().getDataForShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_ENDPOINT)
	public Response saveSharedContact(SormasToSormasEncryptedDataDto sharedContacts) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveSharedEntities(sharedContacts));
	}

	@PUT
	@Path(SormasToSormasApiConstants.CONTACT_ENDPOINT)
	public Response saveReturnedContact(SormasToSormasEncryptedDataDto sharedContacts) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveReturnedEntity(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_SYNC_ENDPOINT)
	public Response syncSharedContacts(SormasToSormasEncryptedDataDto sharedContacts) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveSyncedEntity(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_REQUEST_ENDPOINT)
	public Response saveSharedEventRequest(SormasToSormasEncryptedDataDto sharedEvents) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveShareRequest(sharedEvents));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_REQUEST_REJECT_ENDPOINT)
	public Response rejectSharedEventRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().rejectShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_REQUEST_ACCEPT_ENDPOINT)
	public Response acceptSharedEventRequest(List<String> requestUuids) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().getDataForShareRequest(requestUuids.get(0)));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_ENDPOINT)
	public Response saveSharedEvents(SormasToSormasEncryptedDataDto sharedEvents) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveSharedEntities(sharedEvents));
	}

	@PUT
	@Path(SormasToSormasApiConstants.EVENT_ENDPOINT)
	public Response saveReturnedEvent(SormasToSormasEncryptedDataDto sharedEvent) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveReturnedEntity(sharedEvent));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_SYNC_ENDPOINT)
	public Response syncSharedEvents(SormasToSormasEncryptedDataDto sharedEvent) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveSyncedEntity(sharedEvent));
	}

	@POST
	@Path(SormasToSormasApiConstants.LAB_MESSAGE_ENDPOINT)
	public Response syncSharedLAbMessages(SormasToSormasEncryptedDataDto labMessages) {
		return handleRequest(() -> FacadeProvider.getSormasToSormasLabMessageFacade().saveLabMessages(labMessages));
	}

	private Response handleRequest(FacadeCall facadeCall) {
		try {
			facadeCall.call();
		} catch (SormasToSormasValidationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new SormasToSormasErrorResponse(e.getErrors())).build();
		} catch (SormasToSormasException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}

		return Response.noContent().build();
	}

	private interface FacadeCall {

		void call() throws SormasToSormasValidationException, SormasToSormasException;
	}
}
