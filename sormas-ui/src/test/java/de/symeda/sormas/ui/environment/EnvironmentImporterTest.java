/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.environment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.caze.InvestigationStatus;
import de.symeda.sormas.api.environment.EnvironmentDto;
import de.symeda.sormas.api.environment.EnvironmentMedia;
import de.symeda.sormas.api.environment.WaterType;
import de.symeda.sormas.api.environment.WaterUse;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.environment.importer.EnvironmentImporter;
import de.symeda.sormas.ui.importer.ImportResultStatus;

public class EnvironmentImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportEnvironments()
		throws IOException, CsvValidationException, InvalidColumnException, InterruptedException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_environment_import_test.csv").toURI());

		EnvironmentImporterExtension importer = new EnvironmentImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = importer.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult, importer.errors.toString());
		assertEquals(3, getEnvironmentFacade().count(null));

		List<EnvironmentDto> environments = getEnvironmentFacade().getAllAfter(null);

		EnvironmentDto firstEnvironment = environments.stream().filter(e -> "Import environment 1".equals(e.getEnvironmentName())).findFirst().get();

		assertThat(firstEnvironment.getExternalId(), is("Env-ext-1"));
		assertThat(firstEnvironment.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		assertThat(firstEnvironment.getEnvironmentMedia(), is(EnvironmentMedia.WATER));
		assertThat(firstEnvironment.getEnvironmentMedia(), is(EnvironmentMedia.WATER));
		assertThat(firstEnvironment.getWaterType(), is(WaterType.GROUNDWATER));
		assertThat(firstEnvironment.getWaterUse().getOrDefault(WaterUse.INDUSTRY_COMMERCE, null), is(true));
		assertThat(firstEnvironment.getWaterUse().getOrDefault(WaterUse.OTHER, null), is(true));
		assertThat(firstEnvironment.getOtherWaterUse(), is("Other water use"));
		assertThat(firstEnvironment.getLocation().getRegion(), is(rdcf.region));
		assertThat(firstEnvironment.getLocation().getDistrict(), is(rdcf.district));
		assertThat(firstEnvironment.getLocation().getCity(), is("City"));
		assertThat(firstEnvironment.getLocation().getStreet(), is("Street"));
		assertThat(firstEnvironment.getLocation().getContactPersonFirstName(), is("Contact F"));
		assertThat(firstEnvironment.getLocation().getContactPersonLastName(), is("Contact L"));
	}

	private static class EnvironmentImporterExtension extends EnvironmentImporter {

		private StringBuilder errors = new StringBuilder("");
		private StringBuilderWriter writer = new StringBuilderWriter(errors);

		private EnvironmentImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) throws IOException {
			super(inputFile, hasEntityClassRow, currentUser, ValueSeparator.DEFAULT);
		}

		protected Writer createErrorReportWriter() {
			return writer;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
