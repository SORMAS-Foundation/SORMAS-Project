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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.climate.ClimateCaseClassificationFilter;
import de.symeda.sormas.api.climate.ClimateNearbyCasesDto;
import de.symeda.sormas.api.climate.ClimateNearbyCasesWeekDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class EnvironmentNearbyCasesPanelTest extends AbstractUiBeanTest {

	private static final double SITE_LATITUDE = 10.52657;
	private static final double SITE_LONGITUDE = -84.90845;

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
	void testWeeklyCountsWordingAndCaseList() throws java.io.IOException {

		EnvironmentDto environment = creator.createEnvironment("Lake Arenal", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(SITE_LATITUDE);
			e.getLocation().setLongitude(SITE_LONGITUDE);
		});
		// about 1.1 km north of the site
		createCase(SITE_LATITUDE + 0.01, SITE_LONGITUDE);
		// about 22 km away
		createCase(SITE_LATITUDE + 0.2, SITE_LONGITUDE);

		EnvironmentNearbyCasesPanel panel = new EnvironmentNearbyCasesPanel(environment.toReference(), false);
		panel.update(yesterday.minusDays(29), yesterday);

		ClimateNearbyCasesDto nearby = panel.getNearby();
		assertEquals(2.0, nearby.getRadiusKm());
		assertEquals(1, nearby.getWeeks().stream().mapToInt(ClimateNearbyCasesWeekDto::getCount).sum());
		ClimateNearbyCasesWeekDto week = nearby.getWeeks().stream().filter(w -> w.getCount() > 0).findFirst().get();

		String details = EnvironmentNearbyCasesPanel.weekDetails(nearby, week);
		assertTrue(details.contains("1 within 2 km (0 by probable infection environment, 1 by home address)"), details);
		assertTrue(details.contains("0 without GPS in the same community"), details);
		assertTrue(panel.getScript().contains("color:'#5b7083'"), "a neutral colour, no alarm colours");
		assertFalse(panel.getScript().toLowerCase().contains("risk"), "no wording that suggests cause");

		// written for a render check in a browser with the bundled Highcharts files
		java.nio.file.Files.write(java.nio.file.Paths.get("target", "climate-nearby-test.js"), panel.getScript().getBytes(java.nio.charset.StandardCharsets.UTF_8));

		List<EnvironmentNearbyCasesWindow.Row> rows = EnvironmentNearbyCasesWindow.rows(week);
		assertEquals(1, rows.size());

		panel.getRadius().setValue(5.0);
		assertEquals(5.0, panel.getNearby().getRadiusKm());
	}

	@Test
	void testWithoutUsableGpsTheRadiusIsHidden() {

		EnvironmentDto environment = creator.createEnvironment("Lake Arenal", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(SITE_LATITUDE);
			e.getLocation().setLongitude(SITE_LONGITUDE);
		});

		EnvironmentNearbyCasesPanel panel = new EnvironmentNearbyCasesPanel(environment.toReference(), true);
		panel.update(yesterday.minusDays(29), yesterday);

		assertFalse(panel.getRadius().isVisible());
	}

	@Test
	void testClassificationFilterStartsOnConfirmedBetweenDiseaseAndRadius() {

		EnvironmentDto environment = creator.createEnvironment("Lake Arenal", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(SITE_LATITUDE);
			e.getLocation().setLongitude(SITE_LONGITUDE);
		});
		// all about 1.1 km north of the site
		createCase(SITE_LATITUDE + 0.01, SITE_LONGITUDE, CaseClassification.CONFIRMED);
		createCase(SITE_LATITUDE + 0.01, SITE_LONGITUDE, CaseClassification.PROBABLE);
		createCase(SITE_LATITUDE + 0.01, SITE_LONGITUDE, CaseClassification.SUSPECT);
		createCase(SITE_LATITUDE + 0.01, SITE_LONGITUDE, CaseClassification.NO_CASE);

		EnvironmentNearbyCasesPanel panel = new EnvironmentNearbyCasesPanel(environment.toReference(), false);
		panel.update(yesterday.minusDays(29), yesterday);

		ComboBox<ClimateCaseClassificationFilter> classification = panel.getClassification();
		assertEquals("Case classification", classification.getCaption());
		assertFalse(classification.isEmptySelectionAllowed());
		assertEquals(
			Arrays.asList(ClimateCaseClassificationFilter.values()),
			classification.getDataProvider().fetch(new Query<>()).collect(Collectors.toList()),
			"the options of the Climate dashboard's filter");
		HorizontalLayout filters = (HorizontalLayout) classification.getParent();
		assertEquals(0, filters.getComponentIndex(panel.getDisease()));
		assertEquals(1, filters.getComponentIndex(classification), "after the disease and before the radius");
		assertEquals(2, filters.getComponentIndex(panel.getRadius()));

		// confirmed cases at first, as on the Climate dashboard
		assertEquals(ClimateCaseClassificationFilter.CONFIRMED, classification.getValue());
		assertEquals(ClimateCaseClassificationFilter.CONFIRMED, panel.getNearby().getClassification());
		assertEquals(1, shownCases(panel));

		classification.setValue(ClimateCaseClassificationFilter.CONFIRMED_AND_PROBABLE);
		assertEquals(ClimateCaseClassificationFilter.CONFIRMED_AND_PROBABLE, panel.getNearby().getClassification());
		assertEquals(2, shownCases(panel));

		classification.setValue(ClimateCaseClassificationFilter.ALL_BUT_NOT_A_CASE);
		assertEquals(3, shownCases(panel));
	}

	private static int shownCases(EnvironmentNearbyCasesPanel panel) {
		return panel.getNearby().getWeeks().stream().mapToInt(ClimateNearbyCasesWeekDto::getCount).sum();
	}

	private void createCase(double homeLatitude, double homeLongitude) {
		createCase(homeLatitude, homeLongitude, CaseClassification.CONFIRMED);
	}

	private void createCase(double homeLatitude, double homeLongitude, CaseClassification classification) {
		PersonDto person = creator.createPerson("Case", "Person", p -> {
			p.getAddress().setLatitude(homeLatitude);
			p.getAddress().setLongitude(homeLongitude);
			p.getAddress().setRegion(rdcf.region);
			p.getAddress().setDistrict(rdcf.district);
		});
		Date date = ClimateCaptions.toDate(yesterday.minusDays(3));
		creator.createCase(user.toReference(), person.toReference(), rdcf, c -> {
			c.setDisease(Disease.CHOLERA);
			c.setCaseClassification(classification);
			c.setReportDate(date);
			c.getSymptoms().setOnsetDate(date);
		});
	}
}
