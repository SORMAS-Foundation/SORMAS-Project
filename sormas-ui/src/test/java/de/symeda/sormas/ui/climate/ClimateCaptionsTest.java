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

package de.symeda.sormas.ui.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.climate.ClimateObservationIndexDto;
import de.symeda.sormas.api.climate.HumidityMeasure;

class ClimateCaptionsTest {

	@Test
	void testPlaceNamesTheSharingPlaces() {

		ClimateObservationIndexDto district = new ClimateObservationIndexDto();
		district.setDistrictNames(Collections.singletonList("Ikeja"));
		assertEquals("Ikeja", ClimateCaptions.place(district));

		ClimateObservationIndexDto environment = new ClimateObservationIndexDto();
		environment.setEnvironmentNames(Collections.singletonList("Well A"));
		assertEquals("Well A (environment)", ClimateCaptions.place(environment));

		ClimateObservationIndexDto shared = new ClimateObservationIndexDto();
		shared.setDistrictNames(Collections.singletonList("Ikeja"));
		shared.setEnvironmentNames(Arrays.asList("Well A", "Well B"));
		assertEquals("Ikeja + 2 more", ClimateCaptions.place(shared));
		assertEquals(Arrays.asList("Ikeja", "Well A (environment)", "Well B (environment)"), ClimateCaptions.placeNames(shared));

		ClimateObservationIndexDto unlinked = new ClimateObservationIndexDto();
		unlinked.setLatitude(6.6);
		unlinked.setLongitude(3.35);
		assertEquals("Not linked (6.6, 3.35)", ClimateCaptions.place(unlinked));
	}

	@Test
	void testValuesAreNeverShownAsZeroWhenMissing() {

		assertEquals("", ClimateCaptions.number(null));
		assertEquals("0", ClimateCaptions.number(0.0));
		assertEquals("26.4", ClimateCaptions.number(26.4));
		assertEquals("18.27", ClimateCaptions.number(18.2666));

		ClimateObservationIndexDto observation = new ClimateObservationIndexDto();
		assertEquals("", ClimateCaptions.humidity(observation));
		observation.setRelativeHumidity(84.0);
		observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
		assertEquals("84 (" + HumidityMeasure.NOON_READING + ")", ClimateCaptions.humidity(observation));
	}
}
