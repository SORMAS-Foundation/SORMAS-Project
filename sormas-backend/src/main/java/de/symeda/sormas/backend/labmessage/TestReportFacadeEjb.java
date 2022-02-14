package de.symeda.sormas.backend.labmessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.labmessage.LabMessageReferenceDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.labmessage.TestReportFacade;
import de.symeda.sormas.backend.util.DtoHelper;

@Stateless(name = "TestReportFacade")
public class TestReportFacadeEjb implements TestReportFacade {

	@EJB
	private LabMessageService labMessageService;

	@EJB
	private TestReportService testReportService;

	@Override
	public TestReportDto getByUuid(String uuid) {
		return toDto(testReportService.getByUuid(uuid));
	}

	@Override
	public TestReportDto saveTestReport(@Valid TestReportDto dto) {

		return saveTestReport(dto, true);
	}

	public TestReportDto saveTestReport(@Valid TestReportDto dto, boolean checkChangeDate) {

		TestReport testReport = fromDto(dto, checkChangeDate);

		testReportService.ensurePersisted(testReport);

		return toDto(testReport);
	}

	@Override
	public List<TestReportDto> getAllByLabMessage(LabMessageReferenceDto labMessageRef) {

		if (labMessageRef == null) {
			return Collections.emptyList();
		}

		return labMessageService.getByUuid(labMessageRef.getUuid()).getTestReports().stream().map(t -> toDto(t)).collect(Collectors.toList());
	}

	public static TestReportDto toDto(TestReport source) {
		if (source == null) {
			return null;
		}

		TestReportDto target = new TestReportDto();
		DtoHelper.fillDto(target, source);

		target.setLabMessage(LabMessageFacadeEjb.toReferenceDto(source.getLabMessage()));
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestType(source.getTestType());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setTypingId(source.getTypingId());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setPreliminary(source.getPreliminary());

		return target;
	}

	public TestReport fromDto(@NotNull TestReportDto source, @NotNull LabMessage labMessage, boolean checkChangeDate) {
		TestReport target = DtoHelper.fillOrBuildEntity(source, testReportService.getByUuid(source.getUuid()), TestReport::new, checkChangeDate);

		target.setLabMessage(labMessage);
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalId(source.getTestLabExternalId());
		target.setTestLabPostalCode(source.getTestLabPostalCode());
		target.setTestLabCity(source.getTestLabCity());
		target.setTestType(source.getTestType());
		target.setTestDateTime(source.getTestDateTime());
		target.setTestResult(source.getTestResult());
		target.setTestResultVerified(source.isTestResultVerified());
		target.setTestResultText(source.getTestResultText());
		target.setTypingId(source.getTypingId());
		target.setExternalId(source.getExternalId());
		target.setExternalOrderId(source.getExternalOrderId());
		target.setTestedDiseaseVariant(source.getTestedDiseaseVariant());
		target.setTestedDiseaseVariantDetails(source.getTestedDiseaseVariantDetails());
		target.setPreliminary(source.getPreliminary());

		return target;
	}

	public TestReport fromDto(@NotNull TestReportDto source, boolean checkChangeDate) {
		LabMessage labMessage = labMessageService.getByReferenceDto(source.getLabMessage());

		return fromDto(source, labMessage, checkChangeDate);
	}

	@LocalBean
	@Stateless
	public static class TestReportFacadeEjbLocal extends TestReportFacadeEjb {

	}
}
