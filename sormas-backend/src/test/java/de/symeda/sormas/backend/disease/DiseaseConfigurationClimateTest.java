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

package de.symeda.sormas.backend.disease;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.disease.DiseaseConfigurationDto;
import de.symeda.sormas.backend.AbstractBeanTest;

class DiseaseConfigurationClimateTest extends AbstractBeanTest {

	@Test
	void testClimateSensitiveDefaultsComeFromTheShippedList() {

		getDiseaseConfigurationFacade().loadData();

		assertTrue(getDiseaseConfigurationFacade().isClimateSensitive(Disease.MALARIA));
		assertTrue(getDiseaseConfigurationFacade().isClimateSensitive(Disease.CHOLERA));
		assertFalse(getDiseaseConfigurationFacade().isClimateSensitive(Disease.MEASLES));

		DiseaseConfigurationDto malaria = getDiseaseConfigurationFacade().getDiseaseConfiguration(Disease.MALARIA);
		DiseaseConfigurationDto byUuid = getDiseaseConfigurationFacade().getByUuid(malaria.getUuid());
		assertEquals(Boolean.TRUE, byUuid.getClimateSensitive(), "an empty flag is shown with its default");
	}

	@Test
	void testClimateSettingsAreSavedAndCached() {

		DiseaseConfigurationDto measles = getDiseaseConfigurationFacade().getDiseaseConfiguration(Disease.MEASLES);
		measles.setClimateSensitive(true);
		measles.setUsualRangeExcludedYears(new TreeSet<>(Set.of(2024, 2019)));
		getDiseaseConfigurationFacade().saveDiseaseConfiguration(measles);
		getDiseaseConfigurationFacade().loadData();

		assertTrue(getDiseaseConfigurationFacade().isClimateSensitive(Disease.MEASLES));
		assertEquals(Set.of(2019, 2024), getDiseaseConfigurationFacade().getUsualRangeExcludedYears(Disease.MEASLES));
		assertTrue(getDiseaseConfigurationFacade().getUsualRangeExcludedYears(Disease.CHOLERA).isEmpty());
	}

	@Test
	void testYearSetConverter() {

		YearSetConverter converter = new YearSetConverter();

		assertEquals("2019,2024", converter.convertToDatabaseColumn(new TreeSet<>(Set.of(2024, 2019))));
		assertNull(converter.convertToDatabaseColumn(Set.of()));
		assertEquals(Set.of(2019, 2024), converter.convertToEntityAttribute(" 2024, 2019 ,x, 20l9"));
		assertTrue(converter.convertToEntityAttribute(null).isEmpty());
	}
}
