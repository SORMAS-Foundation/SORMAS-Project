package de.symeda.sormas.api.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExternalMessageMapperTestTypeTextTest {

	@Test
	public void testTypeDetailsIsCarriedOntoTestTypeText() {
		TestReportDto testReport = TestReportDto.build();
		testReport.setTestType(PathogenTestType.OTHER);
		testReport.setTestTypeDetails("cgMLST scheme v2");

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		ExternalMessageMapper mapper =
			new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class));

		mapper.mapToPathogenTest(testReport, pathogenTest);

		assertEquals("cgMLST scheme v2", pathogenTest.getTestTypeText());
	}
}
