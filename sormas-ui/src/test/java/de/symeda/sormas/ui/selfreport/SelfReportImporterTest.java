package de.symeda.sormas.ui.selfreport;

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

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
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
		assertEquals(3, getSelfReportFacade().count(null));

		List<SelfReportDto> selfReports = getSelfReportFacade().getAllAfter(null);

		SelfReportDto firstSelfReport = selfReports.stream().filter(e -> "Import self report 1".equals(e.getFirstName())).findFirst().get();

		//assertThat(firstSelfReport.getFirstName(), is("Env-ext-1"));
		//assertThat(firstSelfReport.getLastName(), is(EnvironmentMedia.WATER));
		//assertThat(firstSelfReport.getInvestigationStatus(), is(InvestigationStatus.PENDING));
		//assertThat(firstSelfReport.getEnvironmentMedia(), is(EnvironmentMedia.WATER));
		//assertThat(firstSelfReport.getWaterType(), is(WaterType.GROUNDWATER));
		//assertThat(firstSelfReport.getWaterUse().getOrDefault(WaterUse.INDUSTRY_COMMERCE, null), is(true));
		//assertThat(firstSelfReport.getWaterUse().getOrDefault(WaterUse.OTHER, null), is(true));
		//assertThat(firstSelfReport.getOtherWaterUse(), is("Other water use"));
		//assertThat(firstSelfReport.getLocation().getRegion(), is(rdcf.region));
		//assertThat(firstSelfReport.getLocation().getDistrict(), is(rdcf.district));
		//assertThat(firstSelfReport.getAddress().getCity(), is("City"));
		//assertThat(firstSelfReport.getAddress().getStreet(), is("Street"));
		//assertThat(firstSelfReport.getAddress().getHouseNumber(), is("3"));
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
