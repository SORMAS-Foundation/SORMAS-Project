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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.utils.ValidationRuntimeException;

/**
 * Daily climate values sent by a met agency system, a weather-station platform or a script. Rows are identified by place
 * and day, not by a uuid, so this resource does not extend {@code EntityDtoResource}, but it answers the same way: one
 * {@link PostResponse} per row. A sent row replaces an earlier import or OpenWeather value for the same place and day; a
 * manual value is never replaced. The caller needs the SORMAS_REST and CLIMATE_IMPORT rights.
 */
@Path("/climateobservations")
@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
@Consumes(MediaType.APPLICATION_JSON + "; charset=UTF-8")
public class ClimateObservationResource {

	/**
	 * @return 200 when every row was saved, otherwise 207 with each row's status: 200 saved, 422 rejected (invalid or
	 *         unknown place), 409 not changed because a manual value is kept. The whole request is refused with 413 when
	 *         it has too many rows, and with 403 while climate data is switched off.
	 */
	@POST
	@Path("/push")
	public Response postClimateObservations(List<ClimateObservationEntryDto> entries) {

		Date receivedAt = new Date();
		if (entries == null || entries.isEmpty()) {
			return Response.ok(Collections.emptyList()).build();
		}
		if (entries.size() > ClimateObservationFacade.MAX_IMPORT_ENTRIES) {
			String message = I18nProperties.getValidationError(Validations.climateEntryTooMany, ClimateObservationFacade.MAX_IMPORT_ENTRIES);
			return Response.status(HttpStatus.SC_REQUEST_TOO_LONG).entity(new PostResponse(HttpStatus.SC_REQUEST_TOO_LONG, message)).build();
		}

		List<ClimateImportRowResult> results;
		try {
			results = FacadeProvider.getClimateObservationFacade().importEntries(entries, true, receivedAt);
		} catch (ValidationRuntimeException e) {
			// the size is checked above, so this is the feature being switched off
			return Response.status(HttpStatus.SC_FORBIDDEN).entity(new PostResponse(HttpStatus.SC_FORBIDDEN, e.getMessage())).build();
		}
		return toResponse(results);
	}

	static Response toResponse(List<ClimateImportRowResult> results) {

		List<PostResponse> responses = new ArrayList<>(results.size());
		boolean allSaved = true;
		for (ClimateImportRowResult result : results) {
			switch (result.getStatus()) {
			case SAVED:
				responses.add(new PostResponse(HttpStatus.SC_OK, null));
				break;
			case SKIPPED:
				responses.add(new PostResponse(HttpStatus.SC_CONFLICT, result.getMessage()));
				allSaved = false;
				break;
			default:
				responses.add(new PostResponse(HttpStatus.SC_UNPROCESSABLE_ENTITY, result.getMessage()));
				allSaved = false;
			}
		}
		return Response.status(allSaved ? HttpStatus.SC_OK : HttpStatus.SC_MULTI_STATUS).entity(responses).build();
	}
}
