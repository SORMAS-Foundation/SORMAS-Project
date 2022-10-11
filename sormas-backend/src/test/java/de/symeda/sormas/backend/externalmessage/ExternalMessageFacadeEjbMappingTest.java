package de.symeda.sormas.backend.externalmessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.externalmessage.ExternalMessageType;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReport;
import de.symeda.sormas.backend.externalmessage.labmessage.SampleReportFacadeEjb;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import junit.framework.TestCase;

@RunWith(MockitoJUnitRunner.class)
public class ExternalMessageFacadeEjbMappingTest extends TestCase {

	@Mock
	private SampleReportFacadeEjb.SampleReportFacadeEjbLocal sampleReportFacade;
	@Mock
	private UserService userservice;
	@InjectMocks
	private ExternalMessageFacadeEjb sut;

	@Test
	public void testFromDto() {

		ExternalMessageDto source = new ExternalMessageDto();

		SampleReport sampleReport = new SampleReport();
		SampleReportDto sampleReportDto = new SampleReportFacadeEjb.SampleReportFacadeEjbLocal().toDto(sampleReport);

		User assignee = new User();
		assignee.setUuid("12345");

		when(sampleReportFacade.fromDto(eq(sampleReportDto), any(ExternalMessage.class), eq(false))).thenReturn(sampleReport);
		when(userservice.getByReferenceDto(assignee.toReference())).thenReturn(assignee);

		source.addSampleReport(sampleReportDto);
		source.setCreationDate(new Date());
		source.setChangeDate(new Date());
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setReporterName("Test Lab Name");
		source.setReporterExternalIds(Arrays.asList("Test Lab External Id 1", "Test Lab External Id 2"));
		source.setReporterPostalCode("Test Lab Postal Code");
		source.setReporterCity("Test Lab City");
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonPresentCondition(PresentCondition.ALIVE);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setPersonPhone("0123456789");
		source.setPersonEmail("mail@domain.com");
		source.setExternalMessageDetails("Lab Message Details");
		source.setAssignee(assignee.toReference());
		source.setType(ExternalMessageType.LAB_MESSAGE);

		ExternalMessage result = sut.fillOrBuildEntity(source, null, true);

		assertEquals(source.getSampleReports(), result.getSampleReports());
		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertNotSame(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getReporterName(), result.getReporterName());
		assertEquals(source.getReporterExternalIds(), result.getReporterExternalIds());
		assertEquals(source.getReporterPostalCode(), result.getReporterPostalCode());
		assertEquals(source.getReporterCity(), result.getReporterCity());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonPresentCondition(), result.getPersonPresentCondition());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getExternalMessageDetails(), result.getExternalMessageDetails());
		assertEquals(assignee.getUuid(), result.getAssignee().getUuid());
		assertEquals(source.getType(), result.getType());
	}

	@Test
	public void testToDto() {
		ExternalMessage source = new ExternalMessage();

		SampleReport sampleReport = new SampleReport();
		List<SampleReport> sampleReports = Collections.singletonList(sampleReport);

		SampleReportDto sampleReportDto = sampleReportFacade.toDto(sampleReport);
		List<SampleReportDto> sampleReportDtos = Collections.singletonList(sampleReportDto);

		Sample sample = new Sample();
		sample.setUuid("Uuid");

		User assignee = new User();
		assignee.setUuid("12345");

		source.setSampleReports(sampleReports);
		source.setCreationDate(new Timestamp(new Date().getTime()));
		source.setChangeDate(new Timestamp(new Date().getTime()));
		source.setUuid("UUID");
		source.setMessageDateTime(new Date());
		source.setReporterName("Test Lab Name");
		source.setReporterExternalIds(Arrays.asList("Test Lab External Id 1", "Test Lab External Id 2"));
		source.setReporterPostalCode("Test Lab Postal Code");
		source.setReporterCity("Test Lab City");
		source.setTestedDisease(Disease.CORONAVIRUS);
		source.setPersonFirstName("Person First Name");
		source.setPersonLastName("Person Last Name");
		source.setPersonSex(Sex.OTHER);
		source.setPersonPresentCondition(PresentCondition.DEAD);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateDD(1);
		source.setPersonBirthDateYYYY(1970);
		source.setPersonPostalCode("Person Postal Code");
		source.setPersonCity("Person City");
		source.setPersonStreet("Person Street");
		source.setPersonHouseNumber("Person House Number");
		source.setPersonPhone("0123456789");
		source.setPersonEmail("mail@domain.com");
		source.setExternalMessageDetails("Lab Message Details");
		source.setStatus(ExternalMessageStatus.PROCESSED);
		source.setAssignee(assignee);
		source.setType(ExternalMessageType.LAB_MESSAGE);

		ExternalMessageDto result = sut.toDto(source);

		assertEquals(sampleReportDtos, result.getSampleReports());
		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertEquals(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getMessageDateTime(), result.getMessageDateTime());
		assertEquals(source.getReporterName(), result.getReporterName());
		assertEquals(source.getReporterExternalIds(), result.getReporterExternalIds());
		assertEquals(source.getReporterPostalCode(), result.getReporterPostalCode());
		assertEquals(source.getReporterCity(), result.getReporterCity());
		assertEquals(source.getTestedDisease(), result.getTestedDisease());
		assertEquals(source.getPersonFirstName(), result.getPersonFirstName());
		assertEquals(source.getPersonLastName(), result.getPersonLastName());
		assertEquals(source.getPersonSex(), result.getPersonSex());
		assertEquals(source.getPersonPresentCondition(), result.getPersonPresentCondition());
		assertEquals(source.getPersonBirthDateDD(), result.getPersonBirthDateDD());
		assertEquals(source.getPersonBirthDateMM(), result.getPersonBirthDateMM());
		assertEquals(source.getPersonBirthDateYYYY(), result.getPersonBirthDateYYYY());
		assertEquals(source.getPersonPostalCode(), result.getPersonPostalCode());
		assertEquals(source.getPersonCity(), result.getPersonCity());
		assertEquals(source.getPersonStreet(), result.getPersonStreet());
		assertEquals(source.getPersonHouseNumber(), result.getPersonHouseNumber());
		assertEquals(source.getExternalMessageDetails(), result.getExternalMessageDetails());
		assertEquals(assignee.getUuid(), result.getAssignee().getUuid());
		assertEquals(source.getType(), result.getType());
	}
}
