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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;

@Path("/campaignFormMeta")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class CampaignFormMetaResource {

	@GET
	@Path("/all/{since}")
	public List<CampaignFormMetaDto> getAllCampaignFormMeta(@PathParam("since") long since) {
		return FacadeProvider.getCampaignFormMetaFacade().getAllAfter(new Date(since));
	}

	@POST
	@Path("/query")
	public List<CampaignFormMetaDto> getByUuids(List<String> uuids) {
		return FacadeProvider.getCampaignFormMetaFacade().getByUuids(uuids);
	}

	@GET
	@Path("/uuids")
	public List<String> getAllUuids() {
		return FacadeProvider.getCampaignFormMetaFacade().getAllUuids();
	}
}
