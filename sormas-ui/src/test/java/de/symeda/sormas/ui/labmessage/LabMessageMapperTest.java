package de.symeda.sormas.ui.labmessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.symeda.sormas.api.labmessage.LabMessageDto;
import de.symeda.sormas.api.labmessage.TestReportDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.AbstractBeanTest;

public class LabMessageMapperTest extends AbstractBeanTest {

	@Test
	public void testHomogenousTestResultTypesInWithNoTestReport() {
		LabMessageDto labMessageDto = LabMessageDto.build();
		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessageDto);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertNull(sample.getPathogenTestResult());
	}

	@Test
	public void testHomogenousTestResultTypesInWithHomogenousTestReports() {
		LabMessageDto labMessage = LabMessageDto.build();

		TestReportDto testReport1 = TestReportDto.build();
		testReport1.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport1);

		TestReportDto testReport2 = TestReportDto.build();
		testReport2.setTestResult(PathogenTestResultType.POSITIVE);
		labMessage.addTestReport(testReport2);

		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertEquals(sample.getPathogenTestResult(), PathogenTestResultType.POSITIVE);
	}

	@Test
	public void testHomogenousTestResultTypesInWithInhomogeneousTestReports() {
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

		LabMessageMapper mapper = LabMessageMapper.forLabMessage(labMessage);

		SampleDto sample = new SampleDto();
		mapper.mapToSample(sample);

		assertNull(sample.getPathogenTestResult());
	}
}
