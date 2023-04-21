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

import java.util.List;
import java.util.function.UnaryOperator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.rest.resources.base.EntityDtoResource;

@Path("/surveillancereports")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class SurveillanceReportResource extends EntityDtoResource<SurveillanceReportDto> {

	@POST
	@Path("/query/cases")
	public List<SurveillanceReportDto> getByCaseUuids(List<String> uuids) {
		return FacadeProvider.getSurveillanceReportFacade().getByCaseUuids(uuids);
	}

	@Override
	public UnaryOperator<SurveillanceReportDto> getSave() {
		return FacadeProvider.getSurveillanceReportFacade()::save;
	}

	@Override
	public Response postEntityDtos(List<SurveillanceReportDto> surveillanceReportDtos) {
		return super.postEntityDtos(surveillanceReportDtos);
	}
}
