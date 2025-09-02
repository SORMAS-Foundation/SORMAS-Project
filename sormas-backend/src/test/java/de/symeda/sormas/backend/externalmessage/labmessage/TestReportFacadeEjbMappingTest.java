package de.symeda.sormas.backend.externalmessage.labmessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.symeda.sormas.api.externalmessage.labmessage.SampleReportReferenceDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PCRTestSpecification;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.infrastructure.country.CountryService;

@ExtendWith(MockitoExtension.class)
public class TestReportFacadeEjbMappingTest {

	@InjectMocks
	private TestReportFacadeEjb sut;

	@Mock
	private ExternalMessageService externalMessageService;

	@Mock
	private SampleReportService sampleReportService;

	@Mock
	private TestReportService testReportService;

	@Mock
	private CountryService countryService;

	@Test
	public void testFromDto() {

		SampleReport sampleReport = new SampleReport();
		sampleReport.setUuid(DataHelper.createUuid());
		SampleReportReferenceDto sampleReportReference = SampleReportFacadeEjb.toReferenceDto(sampleReport);
		when(sampleReportService.getByReferenceDto(sampleReportReference)).thenReturn(sampleReport);

		when(testReportService.getByUuid("UUID")).thenReturn(null);

		TestReportDto source = new TestReportDto();

		source.setCreationDate(new Date(9999999L));
		source.setChangeDate(new Date(9999999L));
		source.setUuid("UUID");
		source.setSampleReport(sampleReportReference);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalIds(Arrays.asList("Test Lab External Id 1", "Test Lab External Id 2"));
		source.setTestLabPostalCode("38100");
		source.setTestLabCity("Braunschweig");
		source.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		source.setTestDateTime(new Date(9999999L));
		source.setTestResult(PathogenTestResultType.POSITIVE);
		source.setTestResultVerified(true);
		source.setTestResultText("Test result text");
		source.setTestPcrTestSpecification(PCRTestSpecification.VARIANT_SPECIFIC);

		source.setSpecie(PathogenSpecie.UNKNOWN);
		source.setTubeNil(1.23f);
		source.setTubeNilGT10(true);
		source.setTubeAgTb1(2.34f);
		source.setTubeAgTb1GT10(false);
		source.setTubeAgTb2(3.45f);
		source.setTubeAgTb2GT10(true);
		source.setTubeMitogene(4.56f);
		source.setTubeMitogeneGT10(false);

		TestReport result = sut.fromDto(source, true);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertNotSame(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(source.getSampleReport(), SampleReportFacadeEjb.toReferenceDto(result.getSampleReport()));
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalIds(), result.getTestLabExternalIds());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.isTestResultVerified(), result.isTestResultVerified());
		assertEquals(source.getTestResultText(), result.getTestResultText());
		assertEquals(source.getTestPcrTestSpecification(), result.getTestPcrTestSpecification());

		assertEquals(source.getSpecie(), result.getSpecie());
		assertEquals(source.getTubeNil(), result.getTubeNil());
		assertEquals(source.getTubeNilGT10(), result.getTubeNilGT10());
		assertEquals(source.getTubeAgTb1(), result.getTubeAgTb1());
		assertEquals(source.getTubeAgTb1GT10(), result.getTubeAgTb1GT10());
		assertEquals(source.getTubeAgTb2(), result.getTubeAgTb2());
		assertEquals(source.getTubeAgTb2GT10(), result.getTubeAgTb2GT10());
		assertEquals(source.getTubeMitogene(), result.getTubeMitogene());
		assertEquals(source.getTubeMitogeneGT10(), result.getTubeMitogeneGT10());

	}

	@Test
	public void testToDto() {

		SampleReport sampleReport = new SampleReport();
		sampleReport.setUuid(DataHelper.createUuid());

		TestReport source = new TestReport();

		source.setCreationDate(new Timestamp(new Date(9999999L).getTime()));
		source.setChangeDate(new Timestamp(new Date(9999999L).getTime()));
		source.setUuid("UUID");
		source.setSampleReport(sampleReport);
		source.setTestLabName("Test Lab Name");
		source.setTestLabExternalIds(Arrays.asList("Test Lab External Id 1", "Test Lab External Id 2"));
		source.setTestLabPostalCode("38100");
		source.setTestLabCity("Braunschweig");
		source.setTestType(PathogenTestType.ANTIBODY_DETECTION);
		source.setTestTypeDetails("Test Type Details");
		source.setTestDateTime(new Date(9999999L));
		source.setTestResult(PathogenTestResultType.POSITIVE);
		source.setTestResultVerified(true);
		source.setTestResultText("Test result text");
		source.setTestPcrTestSpecification(PCRTestSpecification.VARIANT_SPECIFIC);

		source.setSpecie(PathogenSpecie.UNKNOWN);
		source.setTubeNil(1.23f);
		source.setTubeNilGT10(true);
		source.setTubeAgTb1(2.34f);
		source.setTubeAgTb1GT10(false);
		source.setTubeAgTb2(3.45f);
		source.setTubeAgTb2GT10(true);
		source.setTubeMitogene(4.56f);
		source.setTubeMitogeneGT10(false);

	TestReportDto result = TestReportFacadeEjb.toDto(source);

		assertNotSame(source.getCreationDate().getTime(), result.getCreationDate().getTime());
		assertEquals(source.getChangeDate(), result.getChangeDate());
		assertEquals(source.getUuid(), result.getUuid());
		assertEquals(SampleReportFacadeEjb.toReferenceDto(source.getSampleReport()), result.getSampleReport());
		assertEquals(source.getTestLabName(), result.getTestLabName());
		assertEquals(source.getTestLabExternalIds(), result.getTestLabExternalIds());
		assertEquals(source.getTestLabPostalCode(), result.getTestLabPostalCode());
		assertEquals(source.getTestLabCity(), result.getTestLabCity());
		assertEquals(source.getTestType(), result.getTestType());
		assertEquals(source.getTestTypeDetails(), result.getTestTypeDetails());
		assertEquals(source.getTestDateTime(), result.getTestDateTime());
		assertEquals(source.getTestResult(), result.getTestResult());
		assertEquals(source.isTestResultVerified(), result.isTestResultVerified());
		assertEquals(source.getTestResultText(), result.getTestResultText());
		assertEquals(source.getTestPcrTestSpecification(), result.getTestPcrTestSpecification());

		assertEquals(source.getSpecie(), result.getSpecie());
		assertEquals(source.getTubeNil(), result.getTubeNil());
		assertEquals(source.getTubeNilGT10(), result.getTubeNilGT10());
		assertEquals(source.getTubeAgTb1(), result.getTubeAgTb1());
		assertEquals(source.getTubeAgTb1GT10(), result.getTubeAgTb1GT10());
		assertEquals(source.getTubeAgTb2(), result.getTubeAgTb2());
		assertEquals(source.getTubeAgTb2GT10(), result.getTubeAgTb2GT10());
		assertEquals(source.getTubeMitogene(), result.getTubeMitogene());
		assertEquals(source.getTubeMitogeneGT10(), result.getTubeMitogeneGT10());

	}
}
