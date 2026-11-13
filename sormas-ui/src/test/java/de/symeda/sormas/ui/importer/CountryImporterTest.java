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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.country.CountryCriteria;
import de.symeda.sormas.api.infrastructure.country.CountryDto;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.caze.importer.CountryImporter;

public class CountryImporterTest extends AbstractUiBeanTest {

	/** Uuid of Greece in {@code sormas_import_all_countries.csv} and in {@code sormas_country_import_overwrite_test.csv}. */
	private static final String GREECE_UUID = "ee353123-b545-51a6-a87b-50a7ae15128c";

	@Test
	public void testUmlautsInCountryImport()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").toURI());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user);
		importer.runImport();
		CountryReferenceDto countryReference = getCountryFacade().getByDefaultName("Country with ä", false).get(0);
		assertEquals("XYZ", countryReference.getIsoCode());
	}

	@Test
	public void testDontImportDuplicateCountry()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").toURI());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
	}

	/**
	 * "Import default countries" with "Allow overwrite" fills the new codes of a country that already exists, and keeps its
	 * uuid; no second country is created.
	 *
	 * <p>
	 * Example: Greece ({@code uuid ee353123-…}) without codes, the line
	 * {@code Greece,8214,GRC,GR,300,300,EL,EL,FALSE,ee353123-…} imported with "Allow overwrite" → Greece has ISO2
	 * {@code GR}, ISO numeric {@code 300}, NUTS code {@code EL} and EpiPulse location code {@code EL}.
	 */
	@Test
	public void testOverwriteFillsTheCodesOfAnExistingCountry()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		UserDto user = createAdmin();
		createGreeceWithoutCodes();

		InfrastructureImporter importer = new CountryImporterExtension(overwriteCsvFile(), user, true);
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport().getStatus());

		CountryDto greece = getCountryFacade().getByUuid(GREECE_UUID);
		assertEquals("GR", greece.getIso2Code());
		assertEquals("300", greece.getIsoNumericCode());
		assertEquals("EL", greece.getNutsCode());
		assertEquals("EL", greece.getEpipulseLocationCode());
		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
	}

	/**
	 * With "Allow overwrite", a country created by hand with another uuid is found by its ISO code and updated; the line's
	 * uuid is not used and no second country is created.
	 *
	 * <p>
	 * Example: Greece created by hand with a new uuid and ISO3 {@code GRC}, the line of Greece ({@code uuid ee353123-…})
	 * imported with "Allow overwrite" → the hand-made Greece keeps its uuid and gets EpiPulse location code {@code EL}; there
	 * is still one country.
	 */
	@Test
	public void testOverwriteFindsAnExistingCountryByIsoCode()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		UserDto user = createAdmin();
		String handMadeUuid = DataHelper.createUuid();
		CountryDto handMadeGreece = CountryDto.build();
		handMadeGreece.setUuid(handMadeUuid);
		handMadeGreece.setDefaultName("Greece");
		handMadeGreece.setIsoCode("GRC");
		getCountryFacade().save(handMadeGreece);

		InfrastructureImporter importer = new CountryImporterExtension(overwriteCsvFile(), user, true);
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport().getStatus());

		CountryDto greece = getCountryFacade().getByUuid(handMadeUuid);
		assertEquals("GR", greece.getIso2Code());
		assertEquals("EL", greece.getEpipulseLocationCode());
		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
	}

	/**
	 * A line whose NUTS code another country already has is not imported; only this line fails, the import itself
	 * completes.
	 *
	 * <p>
	 * Example: "Elbonia" ({@code ELB}) with NUTS code {@code EL}, the line of Greece with NUTS code {@code EL} imported with
	 * "Allow overwrite" → completed with errors, Greece is not created, Elbonia keeps its NUTS code.
	 */
	@Test
	public void testLineWithNutsCodeOfAnotherCountryIsNotImported()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		UserDto user = createAdmin();
		CountryDto elbonia = CountryDto.build();
		elbonia.setDefaultName("Elbonia");
		elbonia.setIsoCode("ELB");
		elbonia.setNutsCode("EL");
		elbonia = getCountryFacade().save(elbonia);

		InfrastructureImporter importer = new CountryImporterExtension(overwriteCsvFile(), user, true);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());

		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
		assertEquals("EL", getCountryFacade().getByUuid(elbonia.getUuid()).getNutsCode());
	}

	/**
	 * Without "Allow overwrite", an existing country is reported as a duplicate and not changed.
	 *
	 * <p>
	 * Example: Greece without codes, the same line imported without "Allow overwrite" → completed with errors, Greece still
	 * has no ISO2, ISO numeric, NUTS or EpiPulse location code.
	 */
	@Test
	public void testWithoutOverwriteAnExistingCountryIsNotChanged()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		UserDto user = createAdmin();
		createGreeceWithoutCodes();

		InfrastructureImporter importer = new CountryImporterExtension(overwriteCsvFile(), user, false);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());

		CountryDto greece = getCountryFacade().getByUuid(GREECE_UUID);
		assertNull(greece.getIso2Code());
		assertNull(greece.getIsoNumericCode());
		assertNull(greece.getNutsCode());
		assertNull(greece.getEpipulseLocationCode());
	}

	/**
	 * "Allow overwrite" never restores a country an admin archived, though the line says {@code archived=FALSE}; its codes
	 * are still filled.
	 *
	 * <p>
	 * Example: Greece archived and without codes, the line imported with "Allow overwrite" → Greece stays archived and has
	 * EpiPulse location code {@code EL}.
	 */
	@Test
	public void testOverwriteKeepsAnArchivedCountryArchived()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		UserDto user = createAdmin();
		createGreeceWithoutCodes();
		getCountryFacade().archive(GREECE_UUID);

		InfrastructureImporter importer = new CountryImporterExtension(overwriteCsvFile(), user, true);
		importer.runImport();

		CountryDto greece = getCountryFacade().getByUuid(GREECE_UUID);
		assertTrue(greece.isArchived());
		assertEquals("EL", greece.getEpipulseLocationCode());
	}

	/** An admin who runs the import. */
	private UserDto createAdmin() {

		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		return creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));
	}

	/** Greece with the uuid of the default countries, ISO3 {@code GRC} and no other code, as created before #14406. */
	private void createGreeceWithoutCodes() {

		CountryDto greece = CountryDto.build();
		greece.setUuid(GREECE_UUID);
		greece.setDefaultName("Greece");
		greece.setIsoCode("GRC");
		getCountryFacade().save(greece);
	}

	/** {@code sormas_country_import_overwrite_test.csv}: one line for Greece with all codes. */
	private File overwriteCsvFile() throws URISyntaxException {
		return new File(getClass().getClassLoader().getResource("sormas_country_import_overwrite_test.csv").toURI());
	}

	private static class CountryImporterExtension extends CountryImporter {

		private CountryImporterExtension(File inputFile, UserDto currentUser) throws IOException {
			super(inputFile, currentUser, ValueSeparator.DEFAULT);
		}

		private CountryImporterExtension(File inputFile, UserDto currentUser, boolean allowOverwrite) throws IOException {
			super(inputFile, currentUser, allowOverwrite, ValueSeparator.DEFAULT);
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter((new OutputStream() {

				@Override
				public void write(int b) {
					// Do nothing
				}
			}));
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
