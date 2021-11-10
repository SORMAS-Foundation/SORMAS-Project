package de.symeda.sormas.ui.labmessage;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LabMessageControllerTest {

	@Test
	public void testHomogenousTestResultTypesInWithNoTestReport() {
		LabMessageController sut = new LabMessageController();
		LabMessageDto labMessageDto = LabMessageDto.build();

		assertFalse(sut.homogenousTestResultTypesIn(labMessageDto));
	}

	@Test
	public void testHomogenousTestResultTypesInWithHomogenousTestReports() {
		LabMessageController sut = new LabMessageController();
		LabMessageDto labMessage = LabMessageDto.build();

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport2);

		assertTrue(sut.homogenousTestResultTypesIn(labMessage));
	}

	@Test
	public void testHomogenousTestResultTypesInWithInhomogeneousTestReports() {
		LabMessageController sut = new LabMessageController();
		LabMessageDto labMessage = LabMessageDto.build();

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport2);

		TestReportDto testReport3 = TestReportDto.build();
		testReport3.setTestResult(PathogenTestResultType.NEGATIVE);
		labMessage.addTestReport(testReport3);

		assertFalse(sut.homogenousTestResultTypesIn(labMessage));
	}
}
