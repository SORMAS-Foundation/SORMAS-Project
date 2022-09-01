package de.symeda.sormas.backend.externalmessage.labmessage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.externalmessage.ExternalMessageReferenceDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportFacade;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.util.DtoHelper;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "TestReportFacade")
@RightsAllowed(UserRight._EXTERNAL_MESSAGE_PROCESS)
public class TestReportFacadeEjb implements TestReportFacade {

	@EJB
	private ExternalMessageService externalMessageService;

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
	public List<TestReportDto> getAllByLabMessage(ExternalMessageReferenceDto labMessageRef) {

		if (labMessageRef == null) {
			return Collections.emptyList();
		}

		return externalMessageService.getByUuid(labMessageRef.getUuid())
			.getTestReports()
			.stream()
			.map(TestReportFacadeEjb::toDto)
			.collect(Collectors.toList());
	}

	public static TestReportDto toDto(TestReport source) {
		if (source == null) {
			return null;
		}

		TestReportDto target = new TestReportDto();
		DtoHelper.fillDto(target, source);

		target.setLabMessage(ExternalMessageFacadeEjb.toReferenceDto(source.getLabMessage()));
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalIds(source.getTestLabExternalIds());
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
		target.setTestPcrTestSpecification(source.getTestPcrTestSpecification());

		return target;
	}

	public TestReport fromDto(@NotNull TestReportDto source, @NotNull ExternalMessage externalMessage, boolean checkChangeDate) {
		TestReport target = DtoHelper.fillOrBuildEntity(source, testReportService.getByUuid(source.getUuid()), TestReport::new, checkChangeDate);

		target.setLabMessage(externalMessage);
		target.setTestLabName(source.getTestLabName());
		target.setTestLabExternalIds(source.getTestLabExternalIds());
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
		target.setTestPcrTestSpecification(source.getTestPcrTestSpecification());

		return target;
	}

	public TestReport fromDto(@NotNull TestReportDto source, boolean checkChangeDate) {
		ExternalMessage externalMessage = externalMessageService.getByReferenceDto(source.getLabMessage());

		return fromDto(source, externalMessage, checkChangeDate);
	}

	@LocalBean
	@Stateless
	public static class TestReportFacadeEjbLocal extends TestReportFacadeEjb {

	}
}
