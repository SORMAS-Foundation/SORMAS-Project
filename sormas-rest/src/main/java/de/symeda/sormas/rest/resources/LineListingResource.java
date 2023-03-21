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
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CriteriaWithSorting;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.feature.FeatureConfigurationCriteria;
import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Path("/lineListing")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class LineListingResource {

	@POST
	@Path("/indexList")
	public Page<FeatureConfigurationIndexDto> getIndexList(
		@RequestBody @NotNull CriteriaWithSorting<FeatureConfigurationCriteria> criteriaWithSorting,
		@QueryParam("offset") int offset,
		@QueryParam("size") int size) {
		return FacadeProvider.getFeatureConfigurationFacade()
			.getIndexPage(criteriaWithSorting.getCriteria(), offset, size, criteriaWithSorting.getSortProperties());
	}

	@POST
	@Path("/push")
	public Response postFeatureConfigurations(@Valid List<FeatureConfigurationIndexDto> dtos) {
		FacadeProvider.getFeatureConfigurationFacade().saveFeatureConfigurations(dtos, FeatureType.LINE_LISTING);
		return Response.ok("OK").build();
	}

	@POST
	@Path("/delete")
	public Response deleteFeatureConfigurations(@Valid FeatureConfigurationCriteria criteria) {
		FacadeProvider.getFeatureConfigurationFacade().deleteAllFeatureConfigurations(criteria);
		return Response.ok("OK").build();
	}

	@POST
	@Path("/enabled")
	public Map<Disease, List<FeatureConfigurationIndexDto>> getEnabledFeatureConfigurations(@RequestBody FeatureConfigurationCriteria criteria) {
		return FacadeProvider.getFeatureConfigurationFacade().getEnabledFeatureConfigurations(criteria);
	}

}
