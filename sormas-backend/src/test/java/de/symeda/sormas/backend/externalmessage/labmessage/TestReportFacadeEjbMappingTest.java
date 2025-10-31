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
import de.symeda.sormas.api.sample.PathogenSpecie;
import de.symeda.sormas.api.therapy.DrugSusceptibilityType;
import de.symeda.sormas.api.utils.DataHelper;
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

		// Drug susceptibility test data
		source.setAmikacinMic(1.5f);
		source.setAmikacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setBedaquilineMic(2.0f);
		source.setBedaquilineSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setCapreomycinMic(0.8f);
		source.setCapreomycinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setCiprofloxacinMic(1.2f);
		source.setCiprofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setDelamanidMic(0.3f);
		source.setDelamanidSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setEthambutolMic(2.5f);
		source.setEthambutolSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setGatifloxacinMic(1.8f);
		source.setGatifloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setIsoniazidMic(0.5f);
		source.setIsoniazidSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setKanamycinMic(3.0f);
		source.setKanamycinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setLevofloxacinMic(1.1f);
		source.setLevofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setMoxifloxacinMic(0.9f);
		source.setMoxifloxacinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setOfloxacinMic(2.2f);
		source.setOfloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setRifampicinMic(1.0f);
		source.setRifampicinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setStreptomycinMic(4.0f);
		source.setStreptomycinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setCeftriaxoneMic(0.7f);
		source.setCeftriaxoneSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setPenicillinMic(1.6f);
		source.setPenicillinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setErythromycinMic(2.8f);
		source.setErythromycinSusceptibility(DrugSusceptibilityType.RESISTANT);

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

		// Drug susceptibility assertions
		assertEquals(source.getAmikacinMic(), result.getAmikacinMic());
		assertEquals(source.getAmikacinSusceptibility(), result.getAmikacinSusceptibility());
		assertEquals(source.getBedaquilineMic(), result.getBedaquilineMic());
		assertEquals(source.getBedaquilineSusceptibility(), result.getBedaquilineSusceptibility());
		assertEquals(source.getCapreomycinMic(), result.getCapreomycinMic());
		assertEquals(source.getCapreomycinSusceptibility(), result.getCapreomycinSusceptibility());
		assertEquals(source.getCiprofloxacinMic(), result.getCiprofloxacinMic());
		assertEquals(source.getCiprofloxacinSusceptibility(), result.getCiprofloxacinSusceptibility());
		assertEquals(source.getDelamanidMic(), result.getDelamanidMic());
		assertEquals(source.getDelamanidSusceptibility(), result.getDelamanidSusceptibility());
		assertEquals(source.getEthambutolMic(), result.getEthambutolMic());
		assertEquals(source.getEthambutolSusceptibility(), result.getEthambutolSusceptibility());
		assertEquals(source.getGatifloxacinMic(), result.getGatifloxacinMic());
		assertEquals(source.getGatifloxacinSusceptibility(), result.getGatifloxacinSusceptibility());
		assertEquals(source.getIsoniazidMic(), result.getIsoniazidMic());
		assertEquals(source.getIsoniazidSusceptibility(), result.getIsoniazidSusceptibility());
		assertEquals(source.getKanamycinMic(), result.getKanamycinMic());
		assertEquals(source.getKanamycinSusceptibility(), result.getKanamycinSusceptibility());
		assertEquals(source.getLevofloxacinMic(), result.getLevofloxacinMic());
		assertEquals(source.getLevofloxacinSusceptibility(), result.getLevofloxacinSusceptibility());
		assertEquals(source.getMoxifloxacinMic(), result.getMoxifloxacinMic());
		assertEquals(source.getMoxifloxacinSusceptibility(), result.getMoxifloxacinSusceptibility());
		assertEquals(source.getOfloxacinMic(), result.getOfloxacinMic());
		assertEquals(source.getOfloxacinSusceptibility(), result.getOfloxacinSusceptibility());
		assertEquals(source.getRifampicinMic(), result.getRifampicinMic());
		assertEquals(source.getRifampicinSusceptibility(), result.getRifampicinSusceptibility());
		assertEquals(source.getStreptomycinMic(), result.getStreptomycinMic());
		assertEquals(source.getStreptomycinSusceptibility(), result.getStreptomycinSusceptibility());
		assertEquals(source.getCeftriaxoneMic(), result.getCeftriaxoneMic());
		assertEquals(source.getCeftriaxoneSusceptibility(), result.getCeftriaxoneSusceptibility());
		assertEquals(source.getPenicillinMic(), result.getPenicillinMic());
		assertEquals(source.getPenicillinSusceptibility(), result.getPenicillinSusceptibility());
		assertEquals(source.getErythromycinMic(), result.getErythromycinMic());
		assertEquals(source.getErythromycinSusceptibility(), result.getErythromycinSusceptibility());

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

		// Drug susceptibility test data
		source.setAmikacinMic(1.5f);
		source.setAmikacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setBedaquilineMic(2.0f);
		source.setBedaquilineSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setCapreomycinMic(0.8f);
		source.setCapreomycinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setCiprofloxacinMic(1.2f);
		source.setCiprofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setDelamanidMic(0.3f);
		source.setDelamanidSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setEthambutolMic(2.5f);
		source.setEthambutolSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setGatifloxacinMic(1.8f);
		source.setGatifloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setIsoniazidMic(0.5f);
		source.setIsoniazidSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setKanamycinMic(3.0f);
		source.setKanamycinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setLevofloxacinMic(1.1f);
		source.setLevofloxacinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setMoxifloxacinMic(0.9f);
		source.setMoxifloxacinSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setOfloxacinMic(2.2f);
		source.setOfloxacinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setRifampicinMic(1.0f);
		source.setRifampicinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setStreptomycinMic(4.0f);
		source.setStreptomycinSusceptibility(DrugSusceptibilityType.RESISTANT);
		source.setCeftriaxoneMic(0.7f);
		source.setCeftriaxoneSusceptibility(DrugSusceptibilityType.INTERMEDIATE);
		source.setPenicillinMic(1.6f);
		source.setPenicillinSusceptibility(DrugSusceptibilityType.SUSCEPTIBLE);
		source.setErythromycinMic(2.8f);
		source.setErythromycinSusceptibility(DrugSusceptibilityType.RESISTANT);

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

		// Drug susceptibility assertions
		assertEquals(source.getAmikacinMic(), result.getAmikacinMic());
		assertEquals(source.getAmikacinSusceptibility(), result.getAmikacinSusceptibility());
		assertEquals(source.getBedaquilineMic(), result.getBedaquilineMic());
		assertEquals(source.getBedaquilineSusceptibility(), result.getBedaquilineSusceptibility());
		assertEquals(source.getCapreomycinMic(), result.getCapreomycinMic());
		assertEquals(source.getCapreomycinSusceptibility(), result.getCapreomycinSusceptibility());
		assertEquals(source.getCiprofloxacinMic(), result.getCiprofloxacinMic());
		assertEquals(source.getCiprofloxacinSusceptibility(), result.getCiprofloxacinSusceptibility());
		assertEquals(source.getDelamanidMic(), result.getDelamanidMic());
		assertEquals(source.getDelamanidSusceptibility(), result.getDelamanidSusceptibility());
		assertEquals(source.getEthambutolMic(), result.getEthambutolMic());
		assertEquals(source.getEthambutolSusceptibility(), result.getEthambutolSusceptibility());
		assertEquals(source.getGatifloxacinMic(), result.getGatifloxacinMic());
		assertEquals(source.getGatifloxacinSusceptibility(), result.getGatifloxacinSusceptibility());
		assertEquals(source.getIsoniazidMic(), result.getIsoniazidMic());
		assertEquals(source.getIsoniazidSusceptibility(), result.getIsoniazidSusceptibility());
		assertEquals(source.getKanamycinMic(), result.getKanamycinMic());
		assertEquals(source.getKanamycinSusceptibility(), result.getKanamycinSusceptibility());
		assertEquals(source.getLevofloxacinMic(), result.getLevofloxacinMic());
		assertEquals(source.getLevofloxacinSusceptibility(), result.getLevofloxacinSusceptibility());
		assertEquals(source.getMoxifloxacinMic(), result.getMoxifloxacinMic());
		assertEquals(source.getMoxifloxacinSusceptibility(), result.getMoxifloxacinSusceptibility());
		assertEquals(source.getOfloxacinMic(), result.getOfloxacinMic());
		assertEquals(source.getOfloxacinSusceptibility(), result.getOfloxacinSusceptibility());
		assertEquals(source.getRifampicinMic(), result.getRifampicinMic());
		assertEquals(source.getRifampicinSusceptibility(), result.getRifampicinSusceptibility());
		assertEquals(source.getStreptomycinMic(), result.getStreptomycinMic());
		assertEquals(source.getStreptomycinSusceptibility(), result.getStreptomycinSusceptibility());
		assertEquals(source.getCeftriaxoneMic(), result.getCeftriaxoneMic());
		assertEquals(source.getCeftriaxoneSusceptibility(), result.getCeftriaxoneSusceptibility());
		assertEquals(source.getPenicillinMic(), result.getPenicillinMic());
		assertEquals(source.getPenicillinSusceptibility(), result.getPenicillinSusceptibility());
		assertEquals(source.getErythromycinMic(), result.getErythromycinMic());
		assertEquals(source.getErythromycinSusceptibility(), result.getErythromycinSusceptibility());

	}
}
