package de.symeda.sormas.ui.selfreport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportType;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.AbstractUiBeanTest;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.selfreport.importer.SelfReportImporter;

public class SelfReportImporterTest extends AbstractUiBeanTest {

	@Test
	public void testImportSelfReports() throws IOException, CsvValidationException, InvalidColumnException, InterruptedException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_self_report_import_test.csv").toURI());

		SelfReportImporterTest.SelfReportImporterExtension importer = new SelfReportImporterTest.SelfReportImporterExtension(csvFile, true, user);
		ImportResultStatus importResult = importer.runImport().getStatus();

		assertEquals(ImportResultStatus.COMPLETED, importResult, importer.errors.toString());
		assertEquals(2, getSelfReportFacade().count(null));

		List<SelfReportDto> selfReports = getSelfReportFacade().getAllAfter(null);

		SelfReportDto firstSelfReport = selfReports.stream().filter(e -> "John".equals(e.getFirstName())).findFirst().get();
		SelfReportDto secondSelfReport = selfReports.stream().filter(e -> "Joe".equals(e.getFirstName())).findFirst().get();

		assertThat(firstSelfReport.getType(), is(SelfReportType.CASE));
		assertEquals(firstSelfReport.getReportDate().getTime(), DateHelper.parseDate("3/5/2024", new SimpleDateFormat("M/dd/yyy")).getTime());
		assertThat(firstSelfReport.getCaseReference(), is("8765432109"));
		assertThat(firstSelfReport.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(firstSelfReport.getFirstName(), is("John"));
		assertThat(firstSelfReport.getLastName(), is("Doe"));
		assertThat(firstSelfReport.getSex(), is(Sex.MALE));
		assertThat(firstSelfReport.getBirthdateDD(), is(29));
		assertThat(firstSelfReport.getBirthdateMM(), is(3));
		assertThat(firstSelfReport.getBirthdateYYYY(), is(1992));
		assertThat(firstSelfReport.getNationalHealthId(), is("777777777"));
		assertEquals(firstSelfReport.getDateOfTest().getTime(), DateHelper.parseDate("3/5/2024", new SimpleDateFormat("M/dd/yyy")).getTime());
		assertThat(firstSelfReport.getComment(), is("Comment1"));

		assertThat(secondSelfReport.getType(), is(SelfReportType.CONTACT));
		assertEquals(secondSelfReport.getReportDate().getTime(), DateHelper.parseDate("2/5/2024", new SimpleDateFormat("M/dd/yyy")).getTime());
		assertThat(secondSelfReport.getCaseReference(), is("8765432108"));
		assertThat(secondSelfReport.getDisease(), is(Disease.CORONAVIRUS));
		assertThat(secondSelfReport.getFirstName(), is("Joe"));
		assertThat(secondSelfReport.getLastName(), is("Smith"));
		assertThat(secondSelfReport.getSex(), is(Sex.MALE));
		assertThat(secondSelfReport.getBirthdateDD(), is(21));
		assertThat(secondSelfReport.getBirthdateMM(), is(7));
		assertThat(secondSelfReport.getBirthdateYYYY(), is(1970));
		assertThat(secondSelfReport.getNationalHealthId(), is("666666666"));
		assertEquals(secondSelfReport.getDateOfTest().getTime(), DateHelper.parseDate("2/5/2024", new SimpleDateFormat("M/dd/yyy")).getTime());
		assertThat(secondSelfReport.getComment(), is("Comment2"));

	}

	private static class SelfReportImporterExtension extends SelfReportImporter {

		private StringBuilder errors = new StringBuilder("");
		private StringBuilderWriter writer = new StringBuilderWriter(errors);

		private SelfReportImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) throws IOException {
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
