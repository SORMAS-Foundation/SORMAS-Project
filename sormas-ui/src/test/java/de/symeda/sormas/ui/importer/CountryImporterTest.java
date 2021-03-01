package de.symeda.sormas.ui.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.charset.MalformedInputException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.caze.importer.CountryImporter;

// Using Silent Runner to ignore unnecessary stubbing exception
// which is a side effect of extending AbstractBeanTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CountryImporterTest extends AbstractBeanTest {

	@Test
	public void testUmlautsInCountryImport() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").toURI());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user);
		importer.runImport();
		getCountryFacade().getByDefaultName("Country with ä", false).get(0);
	}


	@Test(expected = MalformedInputException.class)
	public void testUmlautsInCountryImportNonUTF8() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_non_utf_test.csv").toURI());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user);
		importer.runImport();
	}

	@Test
	public void testDontImportDuplicateCountry() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").toURI());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
	}

	private static class CountryImporterExtension extends CountryImporter {

		private CountryImporterExtension(File inputFile, UserDto currentUser) {
			super(inputFile, currentUser);
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter((new OutputStream() {

				@Override
				public void write(int b) {
					// Do nothing
				}
			}));
		}
	}
}
