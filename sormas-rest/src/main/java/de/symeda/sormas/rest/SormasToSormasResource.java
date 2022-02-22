/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

import de.symeda.sormas.api.user.UserRight;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptionFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.rest.security.oidc.ClientCredentials;

@Path(SormasToSormasApiConstants.RESOURCE_PATH)
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@ClientCredentials
@RolesAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
public class SormasToSormasResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasResource.class);

	@POST
	@Path(SormasToSormasApiConstants.CASE_REQUEST_ENDPOINT)
	public Response saveSharedCaseRequest(@Valid SormasToSormasEncryptedDataDto sharedCases) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveShareRequest(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_REQUEST_GET_DATA_ENDPOINT)
	public Response getDataForCaseRequest(@Valid SormasToSormasEncryptedDataDto encryptedRequestUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasCaseFacade().getDataForShareRequest(encryptedRequestUuid));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_ENDPOINT)
	public Response saveSharedCase(@Valid SormasToSormasEncryptedDataDto sharedCases) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveSharedEntities(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_SYNC_ENDPOINT)
	public Response syncSharedCases(@Valid SormasToSormasEncryptedDataDto sharedCases) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasCaseFacade().saveSyncedEntity(sharedCases));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_REQUEST_ENDPOINT)
	public Response saveSharedContactRequest(@Valid SormasToSormasEncryptedDataDto sharedContacts) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveShareRequest(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_REQUEST_GET_DATA_ENDPOINT)
	public Response getDataForContactRequest(@Valid SormasToSormasEncryptedDataDto encryptedRequestUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasContactFacade().getDataForShareRequest(encryptedRequestUuid));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_ENDPOINT)
	public Response saveSharedContact(@Valid SormasToSormasEncryptedDataDto sharedContacts) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveSharedEntities(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_SYNC_ENDPOINT)
	public Response syncSharedContacts(@Valid SormasToSormasEncryptedDataDto sharedContacts) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasContactFacade().saveSyncedEntity(sharedContacts));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_REQUEST_ENDPOINT)
	public Response saveSharedEventRequest(@Valid SormasToSormasEncryptedDataDto sharedEvents) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveShareRequest(sharedEvents));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_REQUEST_GET_DATA_ENDPOINT)
	public Response getDataForEventRequest(@Valid SormasToSormasEncryptedDataDto encryptedRequestUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasEventFacade().getDataForShareRequest(encryptedRequestUuid));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_ENDPOINT)
	public Response saveSharedEvents(@Valid SormasToSormasEncryptedDataDto sharedEvents) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveSharedEntities(sharedEvents));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_SYNC_ENDPOINT)
	public Response syncSharedEvents(@Valid SormasToSormasEncryptedDataDto sharedEvent) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasEventFacade().saveSyncedEntity(sharedEvent));
	}

	@POST
	@Path(SormasToSormasApiConstants.LAB_MESSAGE_ENDPOINT)
	public Response syncSharedLAbMessages(@Valid SormasToSormasEncryptedDataDto labMessages) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasLabMessageFacade().saveLabMessages(labMessages));
	}

	@POST
	@Path(SormasToSormasApiConstants.REJECT_REQUESTS_ENDPOINT)
	public Response rejectShareRequests(@Valid SormasToSormasEncryptedDataDto rejectData) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasFacade().requestRejected(rejectData));
	}

	@POST
	@Path(SormasToSormasApiConstants.REVOKE_REQUESTS_ENDPOINT)
	public Response revokeShareRequests(@Valid SormasToSormasEncryptedDataDto requestUuids) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasFacade().requestsRevoked(requestUuids));
	}

	@POST
	@Path(SormasToSormasApiConstants.REQUEST_ACCEPTED_ENDPOINT)
	public Response acceptedShareRequests(@Valid SormasToSormasEncryptedDataDto requestUuid) {
		return handleVoidRequest(() -> FacadeProvider.getSormasToSormasFacade().requestAccepted(requestUuid));
	}

	@GET
	@Path(SormasToSormasApiConstants.CERT_ENDPOINT)
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getCertificate() {
		SormasToSormasEncryptionFacade encryptionFacade = FacadeProvider.getSormasToSormasEncryptionFacade();
		try {
			X509Certificate ownCert = encryptionFacade.loadOwnCertificate();
			return Response.ok(ownCert.getEncoded(), MediaType.APPLICATION_OCTET_STREAM).build();
		} catch (CertificateException | KeyStoreException | IOException | NoSuchAlgorithmException | SormasToSormasException e) {
			LOGGER.error("Could not load own S2S certificate: %s", e);
			return Response.serverError().build();
		}
	}

	@POST
	@Path(SormasToSormasApiConstants.CASE_SHARES_ENDPOINT)
	public Response getCaseReShares(@Valid SormasToSormasEncryptedDataDto caseUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasCaseFacade().getShareTrees(caseUuid));
	}

	@POST
	@Path(SormasToSormasApiConstants.CONTACT_SHARES_ENDPOINT)
	public Response getContactReShares(@Valid SormasToSormasEncryptedDataDto contactUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasContactFacade().getShareTrees(contactUuid));
	}

	@POST
	@Path(SormasToSormasApiConstants.EVENT_SHARES_ENDPOINT)
	public Response getEventReShares(@Valid SormasToSormasEncryptedDataDto eventUuid) {
		return handleRequestWithReturnData(() -> FacadeProvider.getSormasToSormasEventFacade().getShareTrees(eventUuid));
	}

	private Response handleVoidRequest(VoidFacadeCall facadeCall) {
		try {
			facadeCall.call();
		} catch (SormasToSormasValidationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new SormasToSormasErrorResponse(null, null, e.getErrors(), null)).build();
		} catch (SormasToSormasException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new SormasToSormasErrorResponse(e.getMessage(), e.getI18nTag(), e.getErrors(), e.getArgs()))
				.build();
		}

		return Response.noContent().build();
	}

	private Response handleRequestWithReturnData(FacadeCall facadeCall) {
		Object response;
		try {
			response = facadeCall.call();
		} catch (SormasToSormasValidationException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new SormasToSormasErrorResponse(null, null, e.getErrors(), null)).build();
		} catch (SormasToSormasException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
				.entity(new SormasToSormasErrorResponse(e.getMessage(), e.getI18nTag(), e.getErrors(), e.getArgs()))
				.build();
		}

		return Response.ok().entity(response).build();
	}

	private interface VoidFacadeCall {

		void call() throws SormasToSormasValidationException, SormasToSormasException;
	}

	private interface FacadeCall {

		Object call() throws SormasToSormasValidationException, SormasToSormasException;
	}
}
