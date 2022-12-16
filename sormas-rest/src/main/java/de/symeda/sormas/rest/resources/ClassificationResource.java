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
package de.symeda.sormas.rest.resources;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.classification.DiseaseClassificationCriteriaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("/classification")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Tag(name = "Classification Resource",
 description = "Returns a list defining the criteria used to determine the classification of a case on a disease-by-disease basis.\n\n"+
 " **ConfirmedNoSymptoms** is reserved for confirmed cases (e.g. by lab-analysis) without any other symptoms.\n\n"+
 " **ConfirmedUnknownSymptoms** is used for confirmed cases with symptoms in an early tracking stage of the disease, listed symptoms may"+
 " be identified as caused by the tracked disease and later used as criteria for other case classifications.\n\n"+
 " **SuspectCriteria** is used to identify criteria that signify a suspected case of the disease.\n\n"+
 " **ProbableCriteria** is used to identify criteria that signify a probable case of the disease.\n\n"+
 " **ConfirmedCriteria** is used to identify criteria that signify a confirmed case of the disease.")
public class ClassificationResource {

	@GET
	@Path("/all/{since}")
	@Operation(summary = "Get all classification criteria from a date in the past until now.")
	@ApiResponse(responseCode = "200", description = "Returns a list of classification criteria for the given time interval.", useReturnTypeSchema = true)
	public List<DiseaseClassificationCriteriaDto> getAll(
		@Parameter(required = true, description = "Milliseconds since January 1, 1970, 00:00:00 GMT")
		@PathParam("since") long since) {
		return FacadeProvider.getCaseClassificationFacade().getAllSince(new Date(since));
	}
}
