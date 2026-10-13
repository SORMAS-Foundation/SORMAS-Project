package de.symeda.sormas.api.externalmessage.processing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;

public class ExternalMessageMapperSeroConversionTest {

	private static PathogenTestDto mapWithSeroConversion(Boolean seroConversion) {
		TestReportDto testReport = TestReportDto.build();
		testReport.setSeroConversion(seroConversion);

		PathogenTestDto pathogenTest =
			PathogenTestDto.build(new SampleReferenceDto(DataHelper.createUuid()), new UserReferenceDto(DataHelper.createUuid()));

		new ExternalMessageMapper(ExternalMessageDto.build(), Mockito.mock(ExternalMessageProcessingFacade.class))
			.mapToPathogenTest(testReport, pathogenTest);

		return pathogenTest;
	}

	@Test
	public void seroConversionIsCopiedToThePathogenTest() {
		assertEquals(Boolean.TRUE, mapWithSeroConversion(Boolean.TRUE).getSeroConversion());
	}

	@Test
	public void missingSeroConversionLeavesThePathogenTestUnset() {
		assertNull(mapWithSeroConversion(null).getSeroConversion());
	}
}
