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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.PostResponse;
import de.symeda.sormas.api.climate.ClimateImportRowResult;
import de.symeda.sormas.api.climate.ClimateImportRowResult.Status;
import de.symeda.sormas.api.climate.ClimateObservationEntryDto;
import de.symeda.sormas.api.climate.ClimateObservationFacade;

class ClimateObservationResourceTest {

	@Test
	void testAllSavedIs200() {

		Response response = ClimateObservationResource.toResponse(Arrays.asList(ClimateImportRowResult.saved(), ClimateImportRowResult.saved()));

		assertEquals(200, response.getStatus());
		List<PostResponse> rows = rows(response);
		assertEquals(200, rows.get(1).getStatusCode());
		assertNull(rows.get(1).getBody());
	}

	@Test
	void testRowStatusesAndMultiStatus() {

		Response response = ClimateObservationResource.toResponse(
			Arrays.asList(
				ClimateImportRowResult.saved(),
				new ClimateImportRowResult(Status.REJECTED, "Rainfall must be between 0 and 1000 mm a day."),
				new ClimateImportRowResult(Status.SKIPPED, "A manual value is kept for this day; imports never replace it.")));

		assertEquals(207, response.getStatus());
		List<PostResponse> rows = rows(response);
		assertEquals(200, rows.get(0).getStatusCode());
		assertEquals(422, rows.get(1).getStatusCode());
		assertEquals("Rainfall must be between 0 and 1000 mm a day.", rows.get(1).getBody());
		assertEquals(409, rows.get(2).getStatusCode());
	}

	@Test
	void testTooManyRowsAreRefusedBeforeAnythingIsSaved() {

		List<ClimateObservationEntryDto> entries = new ArrayList<>();
		for (int i = 0; i <= ClimateObservationFacade.MAX_IMPORT_ENTRIES; i++) {
			entries.add(new ClimateObservationEntryDto());
		}

		assertEquals(413, new ClimateObservationResource().postClimateObservations(entries).getStatus());
		assertEquals(200, new ClimateObservationResource().postClimateObservations(Collections.emptyList()).getStatus());
	}

	@SuppressWarnings("unchecked")
	private static List<PostResponse> rows(Response response) {
		return (List<PostResponse>) response.getEntity();
	}
}
