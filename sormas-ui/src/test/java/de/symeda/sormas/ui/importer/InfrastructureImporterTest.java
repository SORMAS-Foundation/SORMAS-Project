package de.symeda.sormas.ui.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.community.CommunityCriteria;
import de.symeda.sormas.api.infrastructure.community.CommunityReferenceDto;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryCriteria;
import de.symeda.sormas.api.infrastructure.region.RegionCriteria;
import de.symeda.sormas.api.infrastructure.region.RegionReferenceDto;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractUiBeanTest;

public class InfrastructureImporterTest extends AbstractUiBeanTest {

	@Test
	public void testUmlautsInInfrastructureImport()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();
		List<RegionReferenceDto> regions = getRegionFacade().getReferencesByName("Region with ä", false);
		assertThat(regions, hasSize(1));
		RegionReferenceDto region = regions.get(0);

		// Import district
		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(districtCsvFile, user, InfrastructureType.DISTRICT);
		importer.runImport();
		List<DistrictReferenceDto> districts = getDistrictFacade().getByName("District with ß", region, false);
		assertThat(districts, hasSize(1));
		DistrictReferenceDto district = districts.get(0);

		// Import community
		File communityCsvFile = new File(getClass().getClassLoader().getResource("sormas_community_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(communityCsvFile, user, InfrastructureType.COMMUNITY);
		importer.runImport();
		List<CommunityReferenceDto> communities = getCommunityFacade().getByName("Community with ö", district, false);
		assertThat(communities, hasSize(1));
		CommunityReferenceDto community = communities.get(0);

		// Import facility
		File facilityCsvFile = new File(getClass().getClassLoader().getResource("sormas_facility_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(facilityCsvFile, user, InfrastructureType.FACILITY);
		importer.runImport();
		assertThat(getFacilityFacade().getByNameAndType("Facility with ü", district, community, null, false), hasSize(2));

		// Import point of entry from commented CSV file
		File commentedPoeCsvFile = new File(getClass().getClassLoader().getResource("sormas_poe_import_test_comment.csv").toURI());
		importer = new InfrastructureImporterExtension(commentedPoeCsvFile, user, InfrastructureType.POINT_OF_ENTRY);
		importer.runImport();
		assertThat(getPointOfEntryFacade().getByName("Airport A", district, false), hasSize(1));
	}

	@Test
	public void testUmlautsInInfrastructureImportIso8859()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test_iso_8859_1.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();
		RegionReferenceDto region1 = getRegionFacade().getReferencesByName("Region with ä", false).get(0);
		assertEquals("Region with ä", region1.getCaption());
		RegionReferenceDto region2 = getRegionFacade().getReferencesByName("Region with ß", false).get(0);
		assertEquals("Region with ß", region2.getCaption());
	}

	@Test
	public void testUmlautsInInfrastructureWindows1252()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test_windows_1252.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();
		RegionReferenceDto region1 = getRegionFacade().getReferencesByName("Region with ä", false).get(0);
		assertEquals("Region with ä", region1.getCaption());
		RegionReferenceDto region2 = getRegionFacade().getReferencesByName("Region with ß", false).get(0);
		assertEquals("Region with ß", region2.getCaption());
	}

	@Test
	public void testLargeFileUtf8() throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test_large_file_utf8.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();
		List<RegionReferenceDto> allActiveRegions = getRegionFacade().getAllActiveAsReference();
		RegionReferenceDto region2 = getRegionFacade().getReferencesByName("Sömé rêâlly ßtràngë cøüntry", false).get(0);
		assertEquals("Sömé rêâlly ßtràngë cøüntry", region2.getCaption());
	}

	@Test
	public void testLargeFileUtf8WithBOM()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test_large_file_utf8_with_bom.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();
		List<RegionReferenceDto> allActiveRegions = getRegionFacade().getAllActiveAsReference();
		RegionReferenceDto region2 = getRegionFacade().getReferencesByName("Sömé rêâlly ßtràngë cøüntry", false).get(0);
		assertEquals("Sömé rêâlly ßtràngë cøüntry", region2.getCaption());
	}

	@Test
	public void testLargeFileISO8859_1()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test_large_file_iso8859_1.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		importer.runImport();

		List<RegionReferenceDto> allActiveRegions = getRegionFacade().getAllActiveAsReference();
		RegionReferenceDto region2 = getRegionFacade().getReferencesByName("Sömé rêâlly ßtràngë cøüntry", false).get(0);
		assertEquals("Sömé rêâlly ßtràngë cøüntry", region2.getCaption());
	}

	@Test
	public void testDontImportDuplicateInfrastructure()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user, InfrastructureType.REGION);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(2, getRegionFacade().count(new RegionCriteria()));

		// Import district
		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(districtCsvFile, user, InfrastructureType.DISTRICT);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(2, getDistrictFacade().count(new DistrictCriteria()));

		// Import community
		File communityCsvFile = new File(getClass().getClassLoader().getResource("sormas_community_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(communityCsvFile, user, InfrastructureType.COMMUNITY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(2, getCommunityFacade().count(new CommunityCriteria()));

		// Import facility
		File facilityCsvFile = new File(getClass().getClassLoader().getResource("sormas_facility_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(facilityCsvFile, user, InfrastructureType.FACILITY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(3, getFacilityFacade().count(new FacilityCriteria()));

		// Import point of entry
		File poeCsvFile = new File(getClass().getClassLoader().getResource("sormas_poe_import_test.csv").toURI());
		importer = new InfrastructureImporterExtension(poeCsvFile, user, InfrastructureType.POINT_OF_ENTRY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport().getStatus());
		assertEquals(1, getPointOfEntryFacade().count(new PointOfEntryCriteria()));
	}

	@Test
	public void testImportFromFileWithBom()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		var rdcf = creator.createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Default",
			"User",
			creator.getUserRoleReference(DefaultUserRole.ADMIN));

		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_bom_test.csv").toURI());
		InfrastructureImporter importer = new InfrastructureImporterExtension(districtCsvFile, user, InfrastructureType.DISTRICT);
		importer.runImport();

		// expected Default District + 2 imported districts
		assertEquals(3, getDistrictFacade().count(new DistrictCriteria()));
	}

	private static class InfrastructureImporterExtension extends InfrastructureImporter {

		private InfrastructureImporterExtension(File inputFile, UserDto currentUser, InfrastructureType infrastructureType) throws IOException {
			super(inputFile, currentUser, infrastructureType, ValueSeparator.COMMA);
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter(new OutputStream() {

				@Override
				public void write(int b) {
					// Do nothing
				}
			});
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}
}
