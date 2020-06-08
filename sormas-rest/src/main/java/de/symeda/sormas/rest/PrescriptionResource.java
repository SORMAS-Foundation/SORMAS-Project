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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.therapy.PrescriptionDto;

@Path("/prescriptions")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@RolesAllowed({
	"USER",
	"REST_USER" })
public class PrescriptionResource extends EntityDtoResource {

	@GET
	@Path("/all/{since}")
	public List<PrescriptionDto> getAllPrescriptions(@PathParam("since") long since) {
		return FacadeProvider.getPrescriptionFacade().getAllActivePrescriptionsAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<PrescriptionDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getPrescriptionFacade().getByUuids(uuids);
	}

	@POST
	@Path("/push")
	public List<PushResult> postPrescriptions(List<PrescriptionDto> dtos) {
		return savePushedDto(dtos, FacadeProvider.getPrescriptionFacade()::savePrescription);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllActiveUuids() {
		return FacadeProvider.getPrescriptionFacade().getAllActiveUuids();
	}
}
