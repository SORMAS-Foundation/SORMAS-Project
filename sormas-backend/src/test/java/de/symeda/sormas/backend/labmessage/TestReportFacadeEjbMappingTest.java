package de.symeda.sormas.backend.labmessage;

import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.labmessage.LabMessageReferenceDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.sample.PathogenTestService;
import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class TestReportFacadeEjbMappingTest extends TestCase {

	@InjectMocks
	private TestReportFacadeEjb sut;

	@Mock
	private LabMessageService labMessageService;

	@Mock
	private PathogenTestService pathogenTestService;

	@Mock
	private TestReportService testReportService;

	@Test
	public void testFromDto() {

		LabMessage labMessage = new LabMessage();
		labMessage.setUuid(DataHelper.createUuid());
		LabMessageReferenceDto labMessageReference = LabMessageFacadeEjb.toReferenceDto(labMessage);
		when(labMessageService.getByReferenceDto(labMessageReference)).thenReturn(labMessage);

		when(testReportService.getByUuid("UUID")).thenReturn(null);

		TestReportDto source = new TestReportDto();

		source.setCreationDate(new Date(9999999L));
		source.setChangeDate(new Date(9999999L));
		source.setUuid("UUID");
		source.setLabMessage(labMessageReference);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalId("Test Lab External Id");
		source.setTestLabPostalCode("38100");
		source.setTestLabCity("Braunschweig");
		source.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		source.setTestDateTime(new Date(9999999L));
		source.setTestResult(PathogenTestResultType.POSITIVE);
		source.setTestResultVerified(true);
		source.setTestResultText("Test result text");
		source.setTestPcrTestSpecification(PCRTestSpecification.VARIANT_SPECIFIC);

		TestReport result = sut.fromDto(source, true);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertNotSame(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getLabMessage(), LabMessageFacadeEjb.toReferenceDto(result.getLabMessage()));
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalId(), result.getTestLabExternalId());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.isTestResultVerified(), result.isTestResultVerified());
		assertEquals(source.getTestResultText(), result.getTestResultText());
		assertEquals(source.getTestPcrTestSpecification(), result.getTestPcrTestSpecification());

	}

	@Test
	public void testToDto() {

		LabMessage labMessage = new LabMessage();
		labMessage.setUuid(DataHelper.createUuid());

		TestReport source = new TestReport();

		source.setCreationDate(new Timestamp(new Date(9999999L).getTime()));
		source.setChangeDate(new Timestamp(new Date(9999999L).getTime()));
		source.setUuid("UUID");
		source.setLabMessage(labMessage);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalId("Test Lab External Id");
		source.setTestLabPostalCode("38100");
		source.setTestLabCity("Braunschweig");
		source.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		source.setTestDateTime(new Date(9999999L));
		source.setTestResult(PathogenTestResultType.POSITIVE);
		source.setTestResultVerified(true);
		source.setTestResultText("Test result text");
		source.setTestPcrTestSpecification(PCRTestSpecification.VARIANT_SPECIFIC);

		TestReportDto result = sut.toDto(source);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertEquals(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(LabMessageFacadeEjb.toReferenceDto(source.getLabMessage()), result.getLabMessage());
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalId(), result.getTestLabExternalId());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.isTestResultVerified(), result.isTestResultVerified());
		assertEquals(source.getTestResultText(), result.getTestResultText());
		assertEquals(source.getTestPcrTestSpecification(), result.getTestPcrTestSpecification());

	}
}
