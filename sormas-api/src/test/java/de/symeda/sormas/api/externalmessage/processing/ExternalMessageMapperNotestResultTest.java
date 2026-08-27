package de.symeda.sormas.api.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.epipulse.EpipulseLaboratoryMapper;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExternalMessageMapperNotestResultTest {

	private static PathogenTestDto mapNotest(String rawResultText) {
		TestReportDto testReport = TestReportDto.build();
		testReport.setTestResultText(rawResultText);

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		ExternalMessageMapper mapper =
			new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class));

		mapper.mapToPathogenTest(testReport, pathogenTest);

		return pathogenTest;
	}

	@Test
	public void notestResultTextSetsTestResultToNotDone() {
		PathogenTestDto pathogenTest = mapNotest("NOTEST");

		assertEquals(PathogenTestResultType.NOT_DONE, pathogenTest.getTestResult());
		assertEquals("NOTEST", pathogenTest.getTestResultText());
	}

	@Test
	public void notestResultTextIsCaseInsensitive() {
		PathogenTestDto pathogenTest = mapNotest("notest");

		assertEquals(PathogenTestResultType.NOT_DONE, pathogenTest.getTestResult());
		assertEquals("NOTEST", pathogenTest.getTestResultText());
	}

	@Test
	public void notestResultTextIsTrimmed() {
		PathogenTestDto pathogenTest = mapNotest(" NOTEST ");

		assertEquals(PathogenTestResultType.NOT_DONE, pathogenTest.getTestResult());
		assertEquals("NOTEST", pathogenTest.getTestResultText());
	}

	@Test
	public void nonNotestResultTextLeavesTestResultUnmapped() {
		PathogenTestDto pathogenTest = mapNotest("some lab comment");

		assertEquals(null, pathogenTest.getTestResult());
		assertEquals("some lab comment", pathogenTest.getTestResultText());
	}

	@Test
	public void notestRoundTripsThroughEpipulseExport() {
		PathogenTestDto pathogenTest = mapNotest("NOTEST");

		String reExported = EpipulseLaboratoryMapper.mapTestResultToEpipulseCode(pathogenTest.getTestResult());

		assertEquals("NOTEST", reExported);
	}
}
