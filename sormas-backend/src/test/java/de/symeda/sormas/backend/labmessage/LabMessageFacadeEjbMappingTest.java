package de.symeda.sormas.backend.labmessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.LabMessageStatus;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleMaterial;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class LabMessageFacadeEjbMappingTest extends TestCase {

	@Mock
	private TestReportFacadeEjb.TestReportFacadeEjbLocal testReportFacade;
	@Mock
	private SampleService sampleService;
	@Mock
	private UserService userservice;
	@InjectMocks
	private LabMessageFacadeEjb sut;

	@Test
	public void testFromDto() {

		LabMessageDto source = new LabMessageDto();

		TestReport testReport = new TestReport();
		TestReportDto testReportDto = TestReportFacadeEjb.toDto(testReport);

		Sample sample = new Sample();
		sample.setUuid("Uuid");
		SampleReferenceDto sampleRef = sample.toReference();

		User assignee = new User();
		assignee.setUuid("12345");

		when(sampleService.getByReferenceDto(sampleRef)).thenReturn(sample);
		when(testReportFacade.fromDto(eq(testReportDto), any(LabMessage.class), eq(false))).thenReturn(testReport);
		when(userservice.getByReferenceDto(assignee.toReference())).thenReturn(assignee);

		source.addTestReport(testReportDto);
		source.setCreationDate(new Date());
		source.setChangeDate(new Date());
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setSampleDateTime(new Date());
		source.setSampleReceivedDate(new Date());
		source.setLabSampleId("Lab Sample Id");
		source.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		source.setSampleMaterialText("Sample material text");
		source.setLabName("Test Lab Name");
		source.setLabExternalId("Test Lab External Id");
		source.setLabPostalCode("Test Lab Postal Code");
		source.setLabCity("Test Lab City");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setPersonPhone("0123456789");
		source.setPersonEmail("mail@domain.com");
		source.setLabMessageDetails("Lab Message Details");
		source.setSampleOverallTestResult(PathogenTestResultType.POSITIVE);
		source.setSample(sampleRef);
		source.setAssignee(assignee.toReference());

		LabMessage result = sut.fromDto(source, null, true);

		assertEquals(source.getTestReports(), result.getTestReports());
		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertNotSame(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getSampleMaterialText(), result.getSampleMaterialText());
		assertEquals(source.getLabName(), result.getLabName());
		assertEquals(source.getLabExternalId(), result.getLabExternalId());
		assertEquals(source.getLabPostalCode(), result.getLabPostalCode());
		assertEquals(source.getLabCity(), result.getLabCity());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getLabMessageDetails(), result.getLabMessageDetails());
		assertEquals(source.getSampleOverallTestResult(), result.getSampleOverallTestResult());
		assertEquals(sample, result.getSample());
		assertEquals(assignee.getUuid(), result.getAssignee().getUuid());
	}

	@Test
	public void testToDto() {
		LabMessage source = new LabMessage();

		TestReport testReport = new TestReport();
		ArrayList<TestReport> testReports = new ArrayList<>();
		testReports.add(testReport);

		TestReportDto testReportDto = TestReportFacadeEjb.toDto(testReport);
		ArrayList<TestReportDto> testReportDtos = new ArrayList<>();
		testReportDtos.add(testReportDto);

		Sample sample = new Sample();
		sample.setUuid("Uuid");

		User assignee = new User();
		assignee.setUuid("12345");

		source.setTestReports(testReports);
		source.setCreationDate(new Timestamp(new Date().getTime()));
		source.setChangeDate(new Timestamp(new Date().getTime()));
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setSampleDateTime(new Date());
		source.setSampleReceivedDate(new Date());
		source.setLabSampleId("Lab Sample Id");
		source.setSampleMaterial(SampleMaterial.NASAL_SWAB);
		source.setSampleMaterialText("Sample material text");
		source.setLabName("Test Lab Name");
		source.setLabExternalId("Test Lab External Id");
		source.setLabPostalCode("Test Lab Postal Code");
		source.setLabCity("Test Lab City");
		source.setSpecimenCondition(SpecimenCondition.ADEQUATE);
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setPersonPhone("0123456789");
		source.setPersonEmail("mail@domain.com");
		source.setLabMessageDetails("Lab Message Details");
		source.setStatus(LabMessageStatus.PROCESSED);
		source.setSampleOverallTestResult(PathogenTestResultType.NEGATIVE);
		source.setSample(sample);
		source.setAssignee(assignee);

		LabMessageDto result = sut.toDto(source);

		assertEquals(testReportDtos, result.getTestReports());
		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertEquals(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getSampleDateTime(), result.getSampleDateTime());
		assertEquals(source.getSampleReceivedDate(), result.getSampleReceivedDate());
		assertEquals(source.getLabSampleId(), result.getLabSampleId());
		assertEquals(source.getSampleMaterial(), result.getSampleMaterial());
		assertEquals(source.getSampleMaterialText(), result.getSampleMaterialText());
		assertEquals(source.getLabName(), result.getLabName());
		assertEquals(source.getLabExternalId(), result.getLabExternalId());
		assertEquals(source.getLabPostalCode(), result.getLabPostalCode());
		assertEquals(source.getLabCity(), result.getLabCity());
		assertEquals(source.getSpecimenCondition(), result.getSpecimenCondition());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getLabMessageDetails(), result.getLabMessageDetails());
		assertEquals(source.getSampleOverallTestResult(), result.getSampleOverallTestResult());
		assertEquals(source.getSample().toReference(), result.getSample());
		assertEquals(assignee.getUuid(), result.getAssignee().getUuid());
	}
}
