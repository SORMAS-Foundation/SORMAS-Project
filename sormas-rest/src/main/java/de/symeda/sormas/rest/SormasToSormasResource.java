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

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasErrorResponse;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"REST_USER" })
public class SormasToSormasResource {

	@POST
	@Path(SormasToSormasApiConstants.SAVE_SHARED_CASE_ENDPOINT)
	public Response saveSharedCase(SormasToSormasCaseDto sharedCase) {
		try {
			FacadeProvider.getSormasToSormasFacade().saveSharedCase(sharedCase);
		} catch (ValidationRuntimeException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new SormasToSormasErrorResponse(e.getMessage())).build();
		}

		return Response.noContent().build();
	}

	@POST
	@Path(SormasToSormasApiConstants.SAVE_SHARED_CONTACT_ENDPOINT)
	public Response saveSharedContact(SormasToSormasContactDto sharedContact) {
		try {
			FacadeProvider.getSormasToSormasFacade().saveSharedContact(sharedContact);
		} catch (ValidationRuntimeException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity(new SormasToSormasErrorResponse(e.getMessage())).build();
		}

		return Response.noContent().build();
	}

}
