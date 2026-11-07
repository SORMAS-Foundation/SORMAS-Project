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
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.vaadin.data.provider.Query;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.climate.ClimateDayDto;
import de.symeda.sormas.api.climate.SiteClimateReadingDto;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.environment.EnvironmentService;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.UiUtil;

class EnvironmentClimateEditTest extends AbstractUiBeanTest {

	private final LocalDate yesterday = LocalDate.now().minusDays(1);
	private TestDataCreator.RDCF rdcf;
	private UserDto user;
	private MockedStatic<UiUtil> uiUtil;

	@BeforeEach
	void setUpClimate() {
		getFeatureConfigurationFacade().setServerFeatureEnabled(FeatureType.CLIMATE_MANAGEMENT, true);
		rdcf = creator.createRDCF("Guanacaste", "Bagaces", "Community", "Facility");
		user = useNationalAdminLogin();
		// the user holds the climate and environment edit rights; samples, events and cases are left out
		uiUtil = Mockito.mockStatic(UiUtil.class, Answers.CALLS_REAL_METHODS);
		Set<UserRight> rights = EnumSet.of(UserRight.CLIMATE_EDIT, UserRight.CLIMATE_EXPORT, UserRight.ENVIRONMENT_EDIT);
		uiUtil.when(() -> UiUtil.permitted(any(UserRight.class))).thenAnswer(call -> rights.contains(call.getArgument(0)));
	}

	@AfterEach
	void closeMock() {
		uiUtil.close();
	}

	@Test
	void testOwnRecordCanBeEditedAndReadingsListed() {

		EnvironmentDto environment = createEnvironment();
		SiteClimateReadingDto reading = SiteClimateReadingDto.build(environment.toReference());
		reading.setReadingDateTime(Date.from(yesterday.atTime(LocalTime.of(9, 30)).atZone(ZoneId.systemDefault()).toInstant()));
		reading.setRainfall(12.0);
		reading.setRainfallPeriodHours(24);
		getBean(de.symeda.sormas.backend.climate.SiteClimateReadingFacadeEjb.SiteClimateReadingFacadeEjbLocal.class).saveReading(reading);

		EnvironmentClimateLayout layout = new EnvironmentClimateLayout(environment.toReference(), true);

		assertTrue(layout.isManualEntryAllowed());
		assertTrue(layout.isSiteReadingEditAllowed());
		assertEquals(Arrays.asList("Add site reading", "Add daily values", "Export daily values"), buttonCaptions(layout.getActions()), "stacked in the actions card");
		assertEquals(8, layout.getDays().getGrid().getColumns().size(), "values, source and the edit action");
		assertEquals(7, layout.getSiteReadings().getGrid().getColumns().size(), "values and the edit and delete actions");

		List<SiteClimateReadingDto> readings = items(layout.getSiteReadings().getGrid());
		assertEquals(1, readings.size());
		assertEquals("12 mm in 24 h", SiteClimateReadingsPanel.rainfall(readings.get(0)));

		List<ClimateDayDto> days = items(layout.getDays().getGrid());
		assertEquals(90, days.size());
		assertEquals(yesterday, days.get(0).getDate(), "newest first");
		assertEquals("No data", EnvironmentClimateDaysPanel.source(days.get(0)), "a missing day is shown as such, never as 0");
	}

	@Test
	void testDistrictFallbackHasNoManualEntryAndReadingsFollowTheEnvironment() {

		EnvironmentDto environment = createEnvironment();
		executeInTransaction(em -> {
			getBean(EnvironmentService.class).getByUuid(environment.getUuid()).setClimateLocation(null);
		});

		EnvironmentClimateLayout layout = new EnvironmentClimateLayout(environment.toReference(), false);

		assertFalse(layout.isManualEntryAllowed(), "the record belongs to the district");
		assertFalse(layout.isSiteReadingEditAllowed(), "the environment may not be edited");
		assertEquals(Collections.singletonList("Export daily values"), buttonCaptions(layout.getActions()));
		assertEquals(7, layout.getDays().getGrid().getColumns().size());
		assertEquals(6, layout.getSiteReadings().getGrid().getColumns().size());
	}

	/**
	 * @return the captions of the buttons in the component, in order; every one is a link with an icon (decision 117)
	 */
	private static List<String> buttonCaptions(Component component) {
		List<String> captions = new ArrayList<>();
		if (component instanceof Button) {
			assertTrue(component.getStyleName().contains(ValoTheme.BUTTON_LINK), component.getCaption() + " is a link");
			assertTrue(component.getIcon() != null, component.getCaption() + " has an icon");
			captions.add(component.getCaption());
		} else if (component instanceof HasComponents) {
			((HasComponents) component).forEach(child -> captions.addAll(buttonCaptions(child)));
		}
		return captions;
	}

	private EnvironmentDto createEnvironment() {
		return creator.createEnvironment("Lake Arenal", EnvironmentMedia.WATER, user.toReference(), rdcf, e -> {
			e.getLocation().setLatitude(10.52657);
			e.getLocation().setLongitude(-84.90845);
		});
	}

	private static <T> List<T> items(com.vaadin.ui.Grid<T> grid) {
		return grid.getDataProvider().fetch(new Query<>()).collect(Collectors.toList());
	}
}
