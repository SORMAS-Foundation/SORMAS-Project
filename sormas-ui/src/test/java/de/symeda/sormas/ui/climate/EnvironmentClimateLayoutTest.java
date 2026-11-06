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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.climate.ClimateDataSource;
import de.symeda.sormas.api.climate.HumidityMeasure;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.climate.ClimateLocation;
import de.symeda.sormas.backend.climate.ClimateObservation;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.climate.EnvironmentClimateLayout.ClimatePeriod;

class EnvironmentClimateLayoutTest extends AbstractUiBeanTest {

	private final LocalDate yesterday = LocalDate.now().minusDays(1);
	private TestDataCreator.RDCF rdcf;
	private UserDto user;

	@BeforeEach
	void setUpClimate() {
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF("Guanacaste", "Bagaces", "Community", "Facility");
		user = useNationalAdminLogin();
	}

	@Test
	void testOwnPointPeriodsAndStrip() {

		EnvironmentDto environment = createEnvironment(10.52657, -84.90845);
		fill(environment.getUuid(), yesterday.minusDays(4), yesterday);

		EnvironmentClimateLayout layout = new EnvironmentClimateLayout(environment.toReference(), true);

		assertEquals(EnvironmentClimateLayout.DEFAULT_PERIOD, layout.getPeriod());
		assertEquals(90, layout.getSeries().getDays().size());
		assertEquals(yesterday, layout.getSeries().getDays().get(89).getDate());
		assertEquals(EnvironmentClimateLayout.DEFAULT_PERIOD, layout.getPeriodGroup().getValue(), "90 days is selected");
		String card = text(layout.getContextCard());
		assertTrue(card.contains("10.52657, -84.90845"), card);
		assertTrue(card.contains("Source: OpenWeather 5 days"), card);
		assertFalse(card.contains("No usable GPS"), card);
		assertFalse(card.contains(" · "), "one fact per line: " + card);
		assertTrue(card.contains("Building climate history for normal ranges: less than a month of "), "the chart's status is in the card: " + card);
		assertFalse(text(layout.getChart()).contains("Building climate history"), "and no longer above the chart");
		assertTrue(text(layout.getChart()).contains("Daily climate, samples and events"), "the chart has its own heading");
		assertNull(layout.getActions(), "no actions card without any of its rights");

		layout.getPeriodGroup().setValue(ClimatePeriod.DAYS_30);
		assertEquals(ClimatePeriod.DAYS_30, layout.getPeriod());
		assertEquals(30, layout.getSeries().getDays().size());

		layout.selectPeriod(ClimatePeriod.YEAR);
		assertEquals(ClimatePeriod.YEAR, layout.getPeriodGroup().getValue());
		assertEquals(365, layout.getSeries().getDays().size());
	}

	@Test
	void testWithoutUsableGpsTheStripNamesTheDistrict() {

		EnvironmentDto environment = createEnvironment(10.52657, -84.90845);
		executeInTransaction(em -> {
			getBean(EnvironmentService.class).getByUuid(environment.getUuid()).setClimateLocation(null);
		});

		EnvironmentClimateLayout layout = new EnvironmentClimateLayout(environment.toReference(), true);

		String card = text(layout.getContextCard());
		assertTrue(card.contains("No usable GPS for this environment – showing Bagaces district climate."), card);
		assertTrue(card.contains("No climate data held"), card);
	}

	private EnvironmentDto createEnvironment(double latitude, double longitude) {
		return creator.createEnvironment("Lake Arenal", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(latitude);
			e.getLocation().setLongitude(longitude);
		});
	}

	private void fill(String environmentUuid, LocalDate from, LocalDate to) {
		executeInTransaction(em -> {
			ClimateLocation location = getBean(EnvironmentService.class).getByUuid(environmentUuid).getClimateLocation();
			for (LocalDate day = from; !day.isAfter(to); day = day.plusDays(1)) {
				ClimateObservation observation = new ClimateObservation();
				observation.setClimateLocation(location);
				observation.setObservationDate(ClimateCaptions.toDate(day));
				observation.setSource(ClimateDataSource.OPENWEATHER);
				observation.setRainfall(4.0);
				observation.setTemperatureMin(22.0);
				observation.setTemperatureMean(26.0);
				observation.setTemperatureMax(31.0);
				observation.setRelativeHumidity(80.0);
				observation.setHumidityMeasure(HumidityMeasure.NOON_READING);
				em.persist(observation);
			}
		});
	}

	/**
	 * @return the text of every label in the component, one per line
	 */
	static String text(Component component) {
		List<String> lines = new ArrayList<>();
		collect(component, lines);
		return String.join("\n", lines);
	}

	private static void collect(Component component, List<String> lines) {
		if (component instanceof Label) {
			lines.add(((Label) component).getValue());
		} else if (component instanceof HasComponents) {
			((HasComponents) component).forEach(child -> collect(child, lines));
		}
	}
}
