package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.labmessage.TestReportFacadeEjb.TestReportFacadeEjbLocal;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestReportServiceTest extends AbstractBeanTest {

	@Test
	public void getByPathogenTestUuids() {

		// Create entities for reference
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		Date sampleDate = new Date(1624952153848L);
		SampleDto sample = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		LabMessageDto labMessage = creator.createLabMessage(null);
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest3 = creator.createPathogenTest(sample.toReference(), caze);

		TestReportDto report1 = creator.createTestReport(pathogenTest1.toReference(), labMessage.toReference());
		TestReportDto report2 = creator.createTestReport(pathogenTest2.toReference(), labMessage.toReference());
		TestReportDto report3 = creator.createTestReport(pathogenTest2.toReference(), labMessage.toReference());

		// Get empty result from no pathogen test
		ArrayList uuidList = new ArrayList();
		ArrayList expectedResult = new ArrayList();

		List<TestReportDto> emptyResult = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(expectedResult, emptyResult);

		// Get empty result from one pathogen test
		uuidList.add(pathogenTest3.getUuid());

		emptyResult = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(expectedResult, emptyResult);

		// Get one result from two pathogen tests
		uuidList.add(pathogenTest1.getUuid());

		expectedResult.add(report1);

		List<TestReportDto> singleResult = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(expectedResult, singleResult);

		// Get one result from one pathogen test
		uuidList.remove(pathogenTest3.getUuid());

		singleResult = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(expectedResult, singleResult);

		// Get two results from one pathogen test
		expectedResult.remove(report1);
		expectedResult.add(report3);
		expectedResult.add(report2);

		uuidList.remove(pathogenTest1.getUuid());
		uuidList.add(pathogenTest2.getUuid());

		List<TestReportDto> multipleResults = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(2, multipleResults.size());
		assertTrue(multipleResults.contains(report2) && multipleResults.contains(report3));

		// Get two sorted results from one pathogen test
		multipleResults = getTestReportService().getByPathogenTestUuidsBatched(uuidList, true);
		assertEquals(expectedResult, multipleResults);

		// Get three results from two pathogen tests
		uuidList.add(pathogenTest1.getUuid());

		multipleResults = getTestReportService().getByPathogenTestUuidsBatched(uuidList, false);
		assertEquals(3, multipleResults.size());
		assertTrue(multipleResults.contains(report1) && multipleResults.contains(report2) && multipleResults.contains(report3));

		// Get three sorted results from two pathogen tests
		uuidList.add(pathogenTest1.getUuid());

		expectedResult.add((report1));
		multipleResults = getTestReportService().getByPathogenTestUuidsBatched(uuidList, true);
		assertEquals(expectedResult, multipleResults);
	}

	@Test
	public void getByPathogenTestUuid() {

		// Create entities for reference
		TestDataCreator.RDCF rdcf = creator.createRDCF("Region 1", "District 1", "Community 1", "Facility 1", "Point of entry 1");
		UserDto user =
			creator.createUser(rdcf.region.getUuid(), rdcf.district.getUuid(), rdcf.facility.getUuid(), "Nat", "User", UserRole.NATIONAL_USER);
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		Date sampleDate = new Date(1624952153848L);
		SampleDto sample = creator.createSample(caze.toReference(), sampleDate, sampleDate, user.toReference(), SampleMaterial.BLOOD, rdcf.facility);
		LabMessageDto labMessage = creator.createLabMessage(null);
		PathogenTestDto pathogenTest1 = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest2 = creator.createPathogenTest(sample.toReference(), caze);
		PathogenTestDto pathogenTest3 = creator.createPathogenTest(sample.toReference(), caze);

		TestReportDto report1Dto = creator.createTestReport(pathogenTest1.toReference(), labMessage.toReference());
		TestReportDto report2Dto = creator.createTestReport(pathogenTest2.toReference(), labMessage.toReference());
		TestReportDto report3Dto = creator.createTestReport(pathogenTest2.toReference(), labMessage.toReference());

		TestReportFacadeEjbLocal testReportFacade = (TestReportFacadeEjbLocal) getTestReportFacade();
		TestReport report1 = testReportFacade.fromDto(report1Dto, false);
		TestReport report2 = testReportFacade.fromDto(report2Dto, false);
		TestReport report3 = testReportFacade.fromDto(report3Dto, false);

		// Get empty result from no pathogen test
		ArrayList expectedResult = new ArrayList();

		List<TestReport> emptyResult = getTestReportService().getByPathogenTestUuid(null, false);
		assertEquals(expectedResult, emptyResult);

		// Get empty result from one pathogen test
		emptyResult = getTestReportService().getByPathogenTestUuid(pathogenTest3.getUuid(), false);
		assertEquals(expectedResult, emptyResult);

		// Get one result from one pathogen test
		expectedResult.add(report1);

		List<TestReport> singleResult = getTestReportService().getByPathogenTestUuid(pathogenTest1.getUuid(), false);
		assertEquals(expectedResult, singleResult);

		// Get two results from one pathogen test
		expectedResult.remove(report1);
		expectedResult.add(report3);
		expectedResult.add(report2);

		List<TestReport> multipleResults = getTestReportService().getByPathogenTestUuid(pathogenTest2.getUuid(), false);
		assertEquals(2, multipleResults.size());
		assertTrue(multipleResults.contains(report2) && multipleResults.contains(report3));

		// Get two sorted results from one pathogen test
		multipleResults = getTestReportService().getByPathogenTestUuid(pathogenTest2.getUuid(), true);
		assertEquals(expectedResult, multipleResults);

	}

}
