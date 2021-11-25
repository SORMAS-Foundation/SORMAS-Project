package de.symeda.sormas.backend.labmessage;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestReportFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void getAllByLabMessage() {

		// Create entities for reference
		LabMessageDto labMessage1 = creator.createLabMessage(null);

		TestReportDto testReport1 = creator.createTestReport(labMessage1.toReference());

		ArrayList expectedResult = new ArrayList();
		expectedResult.add(testReport1);

		// Create entity that shall not influence the expected result
		LabMessageDto labMessage2 = creator.createLabMessage(null);
		TestReportDto testReport2 = creator.createTestReport(labMessage2.toReference());

		// Get single result
		List<TestReportDto> result = getTestReportFacade().getAllByLabMessage(labMessage1.toReference());
		assertEquals(expectedResult, result);

		// Get two results
		TestReportDto testReport3 = creator.createTestReport(labMessage1.toReference());
		result = getTestReportFacade().getAllByLabMessage(labMessage1.toReference());
		assertEquals(2, result.size());
		assertTrue(result.contains(testReport1) && result.contains(testReport3));

	}
}
