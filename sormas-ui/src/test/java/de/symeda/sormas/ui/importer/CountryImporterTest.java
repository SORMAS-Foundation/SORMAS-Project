package de.symeda.sormas.ui.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.caze.importer.CountryImporter;

public class CountryImporterTest extends AbstractUiBeanTest {

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

	private static class CountryImporterExtension extends CountryImporter {

		private CountryImporterExtension(File inputFile, UserDto currentUser) throws IOException {
			super(inputFile, currentUser, ValueSeparator.DEFAULT);
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
