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

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.infrastructure.InfrastructureType;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
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
		UserDto user = creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(),
				"Default", "User", UserRole.ADMIN);
		
		// Import region
		File regionCsvFile = new File(getClass().getClassLoader().getResource("sormas_region_import_umlauts_test.csv").getFile());
		InfrastructureImporter importer = new InfrastructureImporterExtension(regionCsvFile, user.toReference(), InfrastructureType.REGION);
		
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport());
		
		RegionReferenceDto region = getRegionFacade().getByName("Region with ä").get(0);
		
		// Import district
		File districtCsvFile = new File(getClass().getClassLoader().getResource("sormas_district_import_umlauts_test.csv").getFile());
		importer = new InfrastructureImporterExtension(districtCsvFile, user.toReference(), InfrastructureType.DISTRICT);
		
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport());
		
		DistrictReferenceDto district = getDistrictFacade().getByName("District with ß", region).get(0);
		
		// Import community
		File communityCsvFile = new File(getClass().getClassLoader().getResource("sormas_community_import_umlauts_test.csv").getFile());
		importer = new InfrastructureImporterExtension(communityCsvFile, user.toReference(), InfrastructureType.COMMUNITY);
		
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport());
		
		CommunityReferenceDto community = getCommunityFacade().getByName("Community with ö", district).get(0);
		
		// Import facility
		File facilityCsvFile = new File(getClass().getClassLoader().getResource("sormas_facility_import_umlauts_test.csv").getFile());
		importer = new InfrastructureImporterExtension(facilityCsvFile, user.toReference(), InfrastructureType.FACILITY);
		
		assertEquals(ImportResultStatus.COMPLETED, importer.runImport());
		
		getFacilityFacade().getByName("Facility with ü", district, community).get(0);
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
