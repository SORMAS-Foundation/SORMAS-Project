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

package de.symeda.sormas.api.climate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserRight;

class ClimateGridTest {

	@Test
	void testUsableGps() {

		assertTrue(ClimateGrid.isUsableGps(6.6, 3.35));
		assertTrue(ClimateGrid.isUsableGps(0.0, 3.35));
		assertFalse(ClimateGrid.isUsableGps(0.0, 0.0));
		assertFalse(ClimateGrid.isUsableGps(null, 3.35));
		assertFalse(ClimateGrid.isUsableGps(91.0, 3.35));
		assertFalse(ClimateGrid.isUsableGps(6.6, -181.0));
		assertFalse(ClimateGrid.isUsableGps(Double.NaN, 3.35));
	}

	@Test
	void testCellIndexAndCentre() {

		assertEquals(132, ClimateGrid.cellIndex(6.60, 0.05));
		assertEquals(67, ClimateGrid.cellIndex(3.35, 0.05));
		assertEquals(132, ClimateGrid.cellIndex(6.61, 0.05));
		assertEquals(133, ClimateGrid.cellIndex(6.63, 0.05));
		assertEquals(-132, ClimateGrid.cellIndex(-6.60, 0.05));

		assertEquals(6.6, ClimateGrid.cellCentre(132, 0.05));
		assertEquals(3.35, ClimateGrid.cellCentre(67, 0.05));
		assertEquals(1.375, ClimateGrid.cellCentre(55, 0.025));
	}

	@Test
	void testParseGridSize() {

		assertEquals(0.1, ClimateGrid.parseGridSize("0.1"));
		assertEquals(0.025, ClimateGrid.parseGridSize(" 0.025 "));
		assertEquals(ClimateGrid.DEFAULT_GRID_SIZE, ClimateGrid.parseGridSize("0.07"));
		assertEquals(ClimateGrid.DEFAULT_GRID_SIZE, ClimateGrid.parseGridSize("abc"));
		assertEquals(ClimateGrid.DEFAULT_GRID_SIZE, ClimateGrid.parseGridSize(null));
	}

	@Test
	void testHistoryCosts() {

		assertEquals(0, ClimateCosts.callsPerLocation(0));
		assertEquals(1, ClimateCosts.callsPerLocation(10));
		assertEquals(2, ClimateCosts.callsPerLocation(11));
		// 774 districts × 5 years: 1,826 days need 183 calls per location
		assertEquals(141_642, ClimateCosts.historyCalls(774, 5));
		assertEquals(37, ClimateCosts.historyCalls(1, 1));
	}

	@Test
	void testSourceOrder() {

		assertTrue(ClimateDataSource.MANUAL.outranks(ClimateDataSource.IMPORT));
		assertTrue(ClimateDataSource.IMPORT.outranks(ClimateDataSource.OPENWEATHER));
		assertTrue(ClimateDataSource.OPENWEATHER.outranks(null));
		assertFalse(ClimateDataSource.OPENWEATHER.outranks(ClimateDataSource.IMPORT));
		assertFalse(ClimateDataSource.IMPORT.outranks(ClimateDataSource.MANUAL));
		assertFalse(ClimateDataSource.IMPORT.outranks(ClimateDataSource.IMPORT));
	}

	/**
	 * Climate rights go only to ADMIN, ADMIN_SUPERVISOR and NATIONAL_USER by default, matching migration 684; deployments
	 * grant them to other roles themselves.
	 */
	@Test
	void testOnlyAdminAndNationalRolesGetClimateRights() {

		Set<UserRight> climateRights = Set.of(
			UserRight.CLIMATE_VIEW,
			UserRight.CLIMATE_EDIT,
			UserRight.CLIMATE_IMPORT,
			UserRight.CLIMATE_EXPORT,
			UserRight.CLIMATE_MANAGE_SOURCES,
			UserRight.DASHBOARD_CLIMATE_VIEW);
		Set<DefaultUserRole> rolesWithClimateRights = Set.of(DefaultUserRole.ADMIN, DefaultUserRole.ADMIN_SUPERVISOR, DefaultUserRole.NATIONAL_USER);

		for (DefaultUserRole role : DefaultUserRole.values()) {
			if (rolesWithClimateRights.contains(role)) {
				assertTrue(role.getDefaultUserRights().containsAll(climateRights), role.name());
			} else {
				assertTrue(role.getDefaultUserRights().stream().noneMatch(climateRights::contains), role.name());
			}
		}
	}

	@Test
	void testDropdownProviders() {

		ClimateGridSizeProvider gridSizes = new ClimateGridSizeProvider();
		assertEquals(5, gridSizes.getOptions().size());
		assertTrue(gridSizes.getOptions().containsValue("0.05"));

		ClimateHistorySourceProvider historySources = new ClimateHistorySourceProvider();
		assertEquals(3, historySources.getOptions().size());
		assertTrue(historySources.getOptions().containsValue(ClimateHistorySource.OPENWEATHER.name()));
	}
}
