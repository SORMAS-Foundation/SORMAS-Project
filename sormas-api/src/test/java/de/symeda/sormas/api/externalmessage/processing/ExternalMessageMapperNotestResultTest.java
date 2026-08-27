package de.symeda.sormas.api.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExternalMessageMapperNotestResultTest {

	@Test
	public void notestResultTextSetsTestResultToNotApplicable() {
		TestReportDto testReport = TestReportDto.build();
		testReport.setTestResultText("NOTEST");

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		ExternalMessageMapper mapper =
			new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class));

		mapper.mapToPathogenTest(testReport, pathogenTest);

		assertEquals(PathogenTestResultType.NOT_APPLICABLE, pathogenTest.getTestResult());
		assertEquals("NOTEST", pathogenTest.getTestResultText());
	}

	@Test
	public void notestResultTextIsCaseInsensitive() {
		TestReportDto testReport = TestReportDto.build();
		testReport.setTestResultText("notest");

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		ExternalMessageMapper mapper =
			new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class));

		mapper.mapToPathogenTest(testReport, pathogenTest);

		assertEquals(PathogenTestResultType.NOT_APPLICABLE, pathogenTest.getTestResult());
		assertEquals("NOTEST", pathogenTest.getTestResultText());
	}

	@Test
	public void nonNotestResultTextLeavesTestResultUnmapped() {
		TestReportDto testReport = TestReportDto.build();
		testReport.setTestResultText("some lab comment");

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		ExternalMessageMapper mapper =
			new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class));

		mapper.mapToPathogenTest(testReport, pathogenTest);

		assertEquals(null, pathogenTest.getTestResult());
		assertEquals("some lab comment", pathogenTest.getTestResultText());
	}
}
