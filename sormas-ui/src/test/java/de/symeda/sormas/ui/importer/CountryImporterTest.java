package de.symeda.sormas.ui.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.region.CountryCriteria;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.caze.importer.CountryImporter;

// Using Silent Runner to ignore unnecessary stubbing exception
// which is a side effect of extending AbstractBeanTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CountryImporterTest extends AbstractBeanTest {

	@Test
	public void testUmlautsInCountryImport() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").getFile());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user.toReference());
		importer.runImport();
		getCountryFacade().getByDefaultName("Country with ä", false).get(0);
	}

	@Test
	public void testDontImportDuplicateCountry() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException {
		TestDataCreator.RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		File countryCsvFile = new File(getClass().getClassLoader().getResource("sormas_country_import_test.csv").getFile());
		InfrastructureImporter importer = new CountryImporterExtension(countryCsvFile, user.toReference());
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(1, getCountryFacade().count(new CountryCriteria()));
	}

	private static class CountryImporterExtension extends CountryImporter {

		private CountryImporterExtension(File inputFile, UserReferenceDto currentUser) {
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
