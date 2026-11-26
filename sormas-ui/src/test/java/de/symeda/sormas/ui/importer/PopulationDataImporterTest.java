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

package de.symeda.sormas.ui.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.PopulationDataCriteria;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class PopulationDataImporterTest extends AbstractUiBeanTest {

	/**
	 * The file holds the whole import template with most cells left blank: a region with only its total, and a district
	 * with a growth rate, its total, both sexes and one age group.
	 */
	@Test
	public void testBlankCellsAreSkipped() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		var rdcf = creator.createRDCF("Region", "District", "Community", "Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// a figure the file leaves blank is kept as it is
		PopulationDataDto regionMales = PopulationDataDto.build(new Date());
		regionMales.setRegion(rdcf.region);
		regionMales.setSex(Sex.MALE);
		regionMales.setPopulation(480);
		getPopulationDataFacade().savePopulationData(Collections.singletonList(regionMales));

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_population_import_test_blank_cells.csv").toURI());
		PopulationDataImporterExtension importer = new PopulationDataImporterExtension(csvFile, user);
		ImportResultStatus status = importer.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, status, importer.errors.toString());

		List<PopulationDataDto> regionData =
			getPopulationDataFacade().getPopulationData(new PopulationDataCriteria().region(rdcf.region).districtIsNull(true).communityIsNull(true));
		assertEquals(2, regionData.size());
		assertEquals(1000, population(regionData, null, null));
		assertEquals(480, population(regionData, Sex.MALE, null));

		List<PopulationDataDto> districtData = getPopulationDataFacade()
			.getPopulationData(new PopulationDataCriteria().region(rdcf.region).district(rdcf.district).communityIsNull(true));
		assertEquals(4, districtData.size());
		assertEquals(400, population(districtData, null, null));
		assertEquals(190, population(districtData, Sex.MALE, null));
		assertEquals(210, population(districtData, Sex.FEMALE, null));
		assertEquals(50, population(districtData, null, AgeGroup.AGE_0_4));
		assertEquals(2.5f, getDistrictFacade().getByUuid(rdcf.district.getUuid()).getGrowthRate());
	}

	private static int population(List<PopulationDataDto> data, Sex sex, AgeGroup ageGroup) {
		return data.stream()
			.filter(populationData -> populationData.getSex() == sex && populationData.getAgeGroup() == ageGroup)
			.findFirst()
			.orElseThrow(() -> new AssertionError("No population data for " + sex + ", " + ageGroup))
			.getPopulation();
	}

	private static class PopulationDataImporterExtension extends PopulationDataImporter {

		private final StringWriter errors = new StringWriter();

		private PopulationDataImporterExtension(File inputFile, UserDto currentUser) throws IOException {
			super(inputFile, currentUser, new Date(), ValueSeparator.COMMA);
		}

		@Override
		protected Writer createErrorReportWriter() {
			return errors;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
