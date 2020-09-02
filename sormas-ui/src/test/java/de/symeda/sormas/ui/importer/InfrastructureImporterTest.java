package de.symeda.sormas.ui.importer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.infrastructure.PointOfEntryCriteria;
import de.symeda.sormas.api.region.CommunityCriteria;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionCriteria;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.TestDataCreator.RDCF;

@RunWith(MockitoJUnitRunner.class)
public class InfrastructureImporterTest extends AbstractBeanTest {

	@Test
	public void testUmlautsInInfrastructureImport() throws IOException, InvalidColumnException, InterruptedException {
		RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test.csv").getFile());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user.toReference(), InfrastructureType.REGION);
		importer.runImport();
		RegionReferenceDto region = getRegionFacade().getByName("Region with ä", false).get(0);

		// Import district
		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(districtCsvFile, user.toReference(), InfrastructureType.DISTRICT);
		importer.runImport();
		DistrictReferenceDto district = getDistrictFacade().getByName("District with ß", region, false).get(0);

		// Import community
		File communityCsvFile = new File(getClass().getClassLoader().getResource("sormas_community_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(communityCsvFile, user.toReference(), InfrastructureType.COMMUNITY);
		importer.runImport();
		CommunityReferenceDto community = getCommunityFacade().getByName("Community with ö", district, false).get(0);

		// Import facility
		File facilityCsvFile = new File(getClass().getClassLoader().getResource("sormas_facility_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(facilityCsvFile, user.toReference(), InfrastructureType.FACILITY);
		importer.runImport();
		getFacilityFacade().getByName("Facility with ü", district, community, false).get(0);
	}

	@Test
	public void testDontImportDuplicateInfrastructure() throws IOException, InvalidColumnException, InterruptedException {
		RDCF rdcf = new TestDataCreator().createRDCF("Default Region", "Default District", "Default Community", "Default Facility");
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Default", "User", UserRole.ADMIN);

		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_test.csv").getFile());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user.toReference(), InfrastructureType.REGION);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(2, getRegionFacade().count(new RegionCriteria()));

		// Import district
		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(districtCsvFile, user.toReference(), InfrastructureType.DISTRICT);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(2, getDistrictFacade().count(new DistrictCriteria()));

		// Import community
		File communityCsvFile = new File(getClass().getClassLoader().getResource("sormas_community_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(communityCsvFile, user.toReference(), InfrastructureType.COMMUNITY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(2, getCommunityFacade().count(new CommunityCriteria()));

		// Import facility
		File facilityCsvFile = new File(getClass().getClassLoader().getResource("sormas_facility_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(facilityCsvFile, user.toReference(), InfrastructureType.FACILITY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(2, getFacilityFacade().count(new FacilityCriteria()));

		// Import point of entry
		File poeCsvFile = new File(getClass().getClassLoader().getResource("sormas_poe_import_test.csv").getFile());
		importer = new InfrastructureImporterExtension(poeCsvFile, user.toReference(), InfrastructureType.POINT_OF_ENTRY);
		assertEquals(ImportResultStatus.COMPLETED_WITH_ERRORS, importer.runImport());
		assertEquals(1, getPointOfEntryFacade().count(new PointOfEntryCriteria()));
	}

	private static class InfrastructureImporterExtension extends InfrastructureImporter {

		private InfrastructureImporterExtension(File inputFile, UserReferenceDto currentUser, InfrastructureType infrastructureType) {
			super(inputFile, currentUser, infrastructureType);
		}

		protected Writer createErrorReportWriter() {
			return new OutputStreamWriter(new OutputStream() {

				@Override
				public void write(int b) throws IOException {
					// Do nothing
				}
			});
		}
	}
}
